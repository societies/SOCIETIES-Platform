/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @phase process-sources
 * @goal convert
 */
public class Jaxb2Simple extends AbstractMojo
{ 
	private final String FOLDER_PATH = "src/main/java"; //"target/generated-sources/xjc/"
	private final String TO_STRING_CODE = "    }\n\n    @Override\n    public String toString() {\n    	return this.value();\n    }\n}\n";

	/**
	 * Output directory for schemas
	 *
	 * @parameter default-value="src/main/resources/"
	 */
	private String folderOutputDirectory;

	/**
	 * Input directory for schemas
	 *
	 * @parameter default-value="${project.build.directory}/generated-sources/"
	 */
	private String folderInputDirectory = FOLDER_PATH;

	/**
	 * To wrap all Societies schemas with the Android Parcelable Interface
	 *
	 * @parameter default-value=false
	 */
	private boolean wrapIntoParcelable;


	private Map<String, Set<String>> newClassesOnPackage = new HashMap<String, Set<String>>();
	private Map<String, String> namespaceForPackage = new HashMap<String, String>();

	public void execute() throws MojoExecutionException
	{
		// Init
		initParameters();
		File startingDirectory= new File(folderInputDirectory);
		getLog().info("Source Directory: " + folderInputDirectory);
		List<File> files = null;

		try {
			files = FileListing.getFileListing(startingDirectory);

			// First process ObjectFactories and package-infos: will potentially create new files
			for (File javaFile : files) {
				if (javaFile.isFile() && javaFile.getName().equals("ObjectFactory.java")) {
					getLog().debug("Processing: " + javaFile.getAbsolutePath());
					processObjectFactory(javaFile);
				}
				if (javaFile.isFile() && javaFile.getName().equals("package-info.java")) {
					getLog().debug("Processing: " + javaFile.getAbsolutePath());
					processPackageInfo(javaFile);
				}
			}

			// Reload files
			files = FileListing.getFileListing(startingDirectory);

			// Process all java files now and delete unwanted ones
			for (File javaFile : files) {
				if (javaFile.isFile()) { //IGNORE DIRECTORIES
					if (javaFile.getName().equals("ObjectFactory.java") || javaFile.getName().equals("package-info.java") || javaFile.getName().equals("Adapter2.java")) {
						getLog().debug("Deleting: " + javaFile.getAbsolutePath());
						javaFile.delete();

					}
					else {	
						if (javaFile.getName().endsWith(".java")) {
							getLog().debug("Processing: " + javaFile.getAbsolutePath());
							String newSchemaContent = processCodeFile(javaFile);
							FileWriter newFile = new FileWriter(javaFile.getAbsolutePath());
							newFile.write(newSchemaContent);
							newFile.close();
						}
					}
				}

			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String processCodeFile(File javaFile) throws FileNotFoundException {

		Scanner scanner = null;
		String newSchemaContent = new String();
		// READ THE ORIGINAL .java FILE
		scanner = new Scanner(javaFile);
		StringBuffer schemaContent = new StringBuffer();
		while (scanner.hasNextLine()) {
			schemaContent.append(scanner.nextLine()+"\n");
		}

		// Main Annotations Replacement
		newSchemaContent = findReplacePatterns(schemaContent);

		// Empty Elements Processing
		Matcher m = packagePattern.matcher(newSchemaContent);
		m.find();
		String pkgName = m.group(1);
		Map<String,String> fieldClasses = detectFields(newSchemaContent, newClassesOnPackage.get(pkgName));
		for (String fieldName : fieldClasses.keySet()) {
			String className = fieldClasses.get(fieldName);
			getLog().debug("Changing class of field '"+fieldName+"' to class '"+className+"'");
			newSchemaContent = replaceFieldAndAccessors(newSchemaContent, fieldName, className);
		}

		//ENUM NEEDS TO BE SERIALIZED WITH "VALUE", NOT "NAME"
		if (newSchemaContent.indexOf("public enum ") > 0) {
			String textToFind = ".*}\n\n}\n";
			String textToReplace = TO_STRING_CODE;
			newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace); 
		}

		//ADD IMPORT ElementList
		if (newSchemaContent.indexOf("@ElementList") > 0) {
			String textToFind = "(import org.simpleframework.xml.Element;)";
			String textToReplace = "$1\nimport org.simpleframework.xml.ElementList;";
			newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace); 
		}

		// Parcelable Stuff
		if (wrapIntoParcelable) {
			newSchemaContent = replaceParcelableStuff(javaFile, newSchemaContent);
		}

		newSchemaContent = createEqualsMethod(javaFile, newSchemaContent);

		return newSchemaContent;
	}

	private static String replaceFieldAndAccessors(String newSchemaContent, String fieldName, String className) {
		String textToFind = "protected String "+fieldName+";\n";
		String textToReplace = "protected "+className+" "+fieldName+";\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		String capitalizedfieldName = Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);;

		textToFind = "public String get"+capitalizedfieldName+"\\(\\) \\{\n";
		textToReplace = "public "+className+" get"+capitalizedfieldName+"() {\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		textToFind = "public void set"+capitalizedfieldName+"\\(String value\\) \\{\n";
		textToReplace = "public void set"+capitalizedfieldName+"("+className+" value) {\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		return newSchemaContent;
	}

	private static final Pattern packagePattern = Pattern.compile("package ([\\d\\w.-]*);");
	private static final Pattern fieldPattern = Pattern.compile("\\s*protected String (\\w*);\\s*");

	private Map<String,String> detectFields(String newSchemaContent, Set<String> newClasses) {
		Map<String,String> fieldClasses = new HashMap<String, String>();

		Matcher matcher = fieldPattern.matcher(newSchemaContent);
		while (matcher.find()) {
			String fieldName = matcher.group(1);
			for (String cl : newClasses) {
				if (cl.equalsIgnoreCase(fieldName)) {
					fieldClasses.put(fieldName,cl);
					//System.out.println("replace 'protected String "+fieldName+";' with 'protected "+cl+" "+fieldName+";'");
					break;
				}
			}
		}

		return fieldClasses;
	}

	private String findReplacePatterns(StringBuffer schemaContent) {
		String newSchemaContent; String textToFind; String textToReplace;
		newSchemaContent = schemaContent.toString();

		//import javax.xml.bind.annotation.* -> import org.simpleframework.xml.*
		//textToFind = "import javax.xml.bind.annotation.*import javax.xml.bind.annotation.\\w*;";
		//textToReplace = "import org.simpleframework.xml.*;";
		//newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace, Pattern.DOTALL|Pattern.CASE_INSENSITIVE);

		//import javax\.xml\.bind\.annotation\.XmlAccessType/import org.simpleframework.xml.DefaultType 
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlAccessType;";
		textToReplace = "import org.simpleframework.xml.DefaultType;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlAccessorType/import org.simpleframework.xml.Default/ 
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlAccessorType;\n";
		//textToReplace = "import org.simpleframework.xml.Default;";
		textToReplace = "";  //PubSub has a class called Default also! Need to refer to xml.default by FQ name
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlAttribute;/import org.simpleframework.xml.Attribute;
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlAttribute;";
		textToReplace = "import org.simpleframework.xml.Attribute;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlElement;/import org.simpleframework.xml.Element;/
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlElement;";
		textToReplace = "import org.simpleframework.xml.Element;\nimport org.simpleframework.xml.Namespace;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlType;/import org.simpleframework.xml.Element;\nimport org.simpleframework.xml.Order;/ 
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlType;";
		textToReplace = "import org.simpleframework.xml.Order;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlRootElement/import org.simpleframework.xml.Root/ 
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlRootElement;";
		textToReplace = "import org.simpleframework.xml.Root;\nimport org.simpleframework.xml.Namespace;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlValue/import org.simpleframework.xml.Text
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlValue;";
		textToReplace = "import org.simpleframework.xml.Text;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlElements/import org.simpleframework.xml.ElementList 
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlElements;";
		textToReplace = "import org.simpleframework.xml.ElementList;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter -> import org.simpleframework.xml.convert.Convert;
		textToFind = "import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;";
		textToReplace = "import org.simpleframework.xml.convert.Convert;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import org.w3._2001.xmlschema.Adapter1; -> import org.societies.simple.converters.URIConverter;
		textToFind = "import org.w3._2001.xmlschema.Adapter1;";
		textToReplace = "import org.societies.simple.basic.URIConverter;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import org.w3._2001.xmlschema.Adapter1; -> import org.societies.simple.converters.URIConverter;
		textToFind = "import org.w3._2001.xmlschema.Adapter2;";
		textToReplace = "import org.societies.simple.basic.DateConverter;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax.xml.bind.annotation.adapters.CollapsedStringAdapter; -> import org.societies.simple.converters.CollapsedStringAdapter;
		textToFind = "import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;";
		textToReplace = "import org.societies.simple.basic.CollapsedStringAdapter;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlJavaTypeAdapter(Adapter1 .class) -> @Convert(URIConverter.class)
		textToFind = "@XmlJavaTypeAdapter\\(Adapter1.*\\.class?\\)";
		textToReplace = "@Convert(URIConverter.class)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlJavaTypeAdapter(Adapter1 .class) -> @Convert(URIConverter.class)
		textToFind = "@XmlJavaTypeAdapter\\(Adapter2.*\\.class?\\)";
		textToReplace = "@Convert(DateConverter.class)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		textToFind = "import java.util.Date;";
		textToReplace = "import java.text.DateFormat;\nimport java.text.SimpleDateFormat;\nimport java.util.Date;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlJavaTypeAdapter(CollapsedStringAdapter.class) -> @Convert(CollapsedStringAdapter.class)
		textToFind = "@XmlJavaTypeAdapter\\(CollapsedStringAdapter.class?\\)";
		textToReplace = "@Convert(CollapsedStringAdapter.class)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//REMOVE namespace FROM XmlElement ANNOTATION  @XmlElement(namespace = "jabber:x:data")
		//s/\(@XmlElement(name = ".*"\), namespace\( = ".*"\))/\1, required = false)\n    @Namespace(reference\2)/
		textToFind = "(@XmlElement\\(.*)namespace( = \".*\")\\)";
		textToReplace = "$1required = false)\n    @Namespace(reference$2)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlElement(name = "Activity", namespace = "http://societies.org/api/schema/activity", required = true)
		textToFind = "(@XmlElement\\(.*)namespace( = \".*\"),(.*)\\)";
		textToReplace = "$1$3)\n    @Namespace(reference$2)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		// @XmlElement with List -> @XmlElementList     @XmlElement(nillable = true) -> @ElementList(inline=true, entry="Service")
		// @XmlElement(name[      ]*=\(.*\)).*\(\n.*List\<.*\>.*;\)/@ElementList(inline=true, entry=\1)\2/
		textToFind   = "@XmlElement\\(.*?\\)(\n.*?List\\<(.*?)\\>.*?;)";
		//textToReplace = "@ElementList(inline=true, entry=\"$2\")$1";
		textToReplace = "$1";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace, Pattern.DOTALL|Pattern.CASE_INSENSITIVE);

		//@XmlElement -> @Element
		//1) if "required = true|false" is missing then add "required=false" else do nothing
		//2) remove nillable=true|false

		//@XmlElement(required = true, type = Integer.class, nillable = true)
		//textToFind = "(@XmlElement\\(.*)nillable = true|false\\)";
		textToFind = ", nillable = (true|false)";
		textToReplace = "";	//"$1)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlElement(required = true, type = Integer.class, nillable = true)
		textToFind = "@XmlElement\\(nillable = true\\)";
		textToReplace = "@Element(required=false)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlElement(\(.*required.*\))/@Element(\1)/   @XmlElement(required = true)
		//textToFind = "@XmlElement(\\(.*required.*\\))";
		textToFind = "@XmlElement\\((.*required.*?)\\)";
		textToReplace = "@Element($1)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlElement(\(.*\))/@Element(required=false,\1)/ 
		textToFind = "@XmlElement\\((.*?)\\)";
		textToReplace = "@Element($1, required=false)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//NAMESPACE
		Pattern patternNS = Pattern.compile("package (.*);", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
		Matcher matcherNS = patternNS.matcher(newSchemaContent);
		String ns = "";
		if (matcherNS.find()) { 
			String pkgTmp = matcherNS.group();
			String pkgFinal = pkgTmp.substring(8, pkgTmp.indexOf(";"));
			//			String[] nsArr = pkgFinal.split("\\.");
			//			ns = "@Namespace(reference=\"http://" + nsArr[1] + "." + nsArr[0];
			//			for(int i=2; i<nsArr.length; i++)
			//				ns+="/" + nsArr[i];
			//			ns += "\")\n";
			ns = "@Namespace(reference=\""+namespaceForPackage.get(pkgFinal)+"\")\n"; // fix for non-default namespaces (eg pubsub#event) issue
		}

		// @XmlRootElement -> @Root + @Namespace(reference="http://...)
		// s/@XmlRootElement(\(.*\))/@Root(\1, strict=false)/
		//textToFind = "@XmlRootElement(\\(.*)\\)\n";
		textToFind = "@XmlRootElement(\\(.*?)\\)\n";
		textToReplace = "@Root$1, strict=false)\n" + ns;
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlType(.*propOrder[    ]*\(=.*\)/@Order(elements\1/  
		//		textToFind = "@XmlType\\(.*propOrder";
		//		textToReplace = "@Order(elements";
		//		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		// TODO discarding element order information instead of placing @Order annotations
		textToFind = "@XmlType\\([^\\)]*\\)";
		textToReplace = "";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		// @XmlAccessorType([ ]*XmlAccessType\.\(.*\))\n@XmlType(\(.*\)[ ]*,[ ]*propOrder[ ]*\(=.*\)/@Default(DefaultType.\1)\n@Order(elements\3/
		textToFind = "@XmlAccessorType\\(XmlAccessType.FIELD\\)";
		textToReplace = "@org.simpleframework.xml.Default(value=DefaultType.FIELD, required=false)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		// @XmlAccessorType([ ]*XmlAccessType\.\(.*\))\n@XmlType(\(.*\))/@Default(DefaultType.\1)\n@Root(\2, strict=false
		//Pattern patternAccessor1 = Pattern.compile("@XmlAccessorType([ ]*XmlAccessType\\.\\(.*\\))\\n@XmlType(\\(.*\\))", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
		//Matcher matcherAccessor1 = patternAccessor1.matcher(newSchemaContent);
		//newSchemaContent = matcherAccessor1.replaceAll("@Default(DefaultType.$1)\n@Root($2, strict=false");

		// @XmlAccessorType([ ]*XmlAccessType\.\(.*\))\n@XmlType(\(.*\)[ ]*,[ ]*propOrder[ ]*\(=.*\)/@Default(DefaultType.\1)\n@Order(elements\3/
		//Pattern patternAccessor2 = Pattern.compile("@XmlAccessorType([ ]*XmlAccessType\\.\\(.*\\))\\n@XmlType(\\(.*\\)[ ]*,[ ]*propOrder[ ]*\\(=.*\\)", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
		//Matcher matcherAccessor2 = patternAccessor2.matcher(newSchemaContent);
		//newSchemaContent = matcherAccessor.replaceAll("@Default(DefaultType.$1)\n@Order(elements$3");

		// @XmlAttribute -> @Attribute
		//if "required = true|false" is missing then add "required=false" else do nothing
		textToFind = "@XmlAttribute\\((.*, required = true|false)\\)";
		textToReplace = "@Attribute($1)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//ADD required=false IF MISSING 
		textToFind = "@XmlAttribute\\((.*?(?!required = true|false))\\)";
		textToReplace = "@Attribute($1, required=false)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlAttribute/@Attribute(required=false)/ 
		textToFind = "@XmlAttribute\n";
		textToReplace = "@Attribute(required=false)\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlValue -> @Text
		textToFind = "@XmlValue";
		textToReplace = "@Text(required=false)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//REMOVE @XmlSchemaType(name = "anyURI")
		textToFind = "@XmlSchemaType\\(name = \".*\"\\)\n    ";
		textToReplace = "";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//ENUMERATOR FILE - REMOVE ALL ANNOTATIONS
		textToFind = "@XmlEnum.*?\n";
		textToReplace = "";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlType(name = "methodType")
		textToFind = "@XmlType\\(name = \".*?\"\\)?\n";
		textToReplace = "";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//@XmlSeeAlso/,/})
		textToFind = "@XmlSeeAlso.*?}\\)\n";
		textToReplace = "";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace, Pattern.DOTALL|Pattern.CASE_INSENSITIVE);

		// @XmlAnyElement.*
		textToFind = "@XmlAnyElement.*?\n";
		textToReplace = "";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlAnyElement;/d
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlAnyElement;";
		textToReplace = "\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlAnyAttribute;/d
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlAnyAttribute;";
		textToReplace = "\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlSeeAlso;/d
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlSeeAlso;";
		textToReplace = "\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlSchemaType;/d 
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlSchemaType;";
		textToReplace = "\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlEnum;/d 
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlEnum;";
		textToReplace = "\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//import javax\.xml\.bind\.annotation\.XmlEnumValue;/d
		textToFind = "import javax\\.xml\\.bind\\.annotation\\.XmlEnumValue;";
		textToReplace = "\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		//COMPLETELY REMOVE @Element(defaultValue = "", required=false)
		textToFind = "@Element\\(defaultValue = \".*\", required=false\\)";
		textToReplace = "";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		// /@XmlSchemaType.*/d 
		// /@XmlAnyAttribute.*/d 

		return newSchemaContent;
	}


	private String createEqualsMethod(File javaFile, String schemaContent) {
		String textToFind; String textToReplace;

		// -- Check if this file has to be Parcelable
		if (isPatternMatching("Adapter1", javaFile.getAbsolutePath())) {
			return schemaContent;
		}
		if (isPatternMatching("Adapter2", javaFile.getAbsolutePath())) {
			return schemaContent;
		}
		if (!isPatternMatching("xjc", javaFile.getAbsolutePath())) {
			return schemaContent;
		}

		// -- Collect information about the file
		// Retrieve ClassName (even if it is an enum)
		Pattern patternClassName = Pattern.compile("public (?:abstract )?(class|enum) (.+)( |\\s)");
		Matcher matcherClassName = patternClassName.matcher(schemaContent);
		// Not a class or an enum: stop everything
		if (!matcherClassName.find()) {
			return schemaContent;
		}
		String className = matcherClassName.group(2).trim();
		className = className.replace(" {", ""); // For enum and empty class it is useful
		getLog().debug("###ClassName:"+className);
		// Is it an enum?
		boolean isEnum = "enum".equals(matcherClassName.group(1).trim());
		// Is abstract?
		boolean isAbstract = isPatternMatching(" abstract ", schemaContent);
		// Is it extending something?
		boolean isExtension = isPatternMatching("extends ", schemaContent);
		// Is it requiring a default constructor?
		boolean requiredDefaultConstructor = !isPatternMatching("public "+className+"\\(", schemaContent);

		// Retrieve all Fields information
		LinkedHashMap <String, String> fields = new LinkedHashMap <String, String>();
		Pattern patternFields = Pattern.compile("(?:protected|private) (?:final )?(?:static )?([^ ]+) ([^ ]+);\n", Pattern.CASE_INSENSITIVE);
		Matcher matcherFields = patternFields.matcher(schemaContent);
		while (matcherFields.find()) {
			// In case the name is overrided
			Pattern patternOverrideField = Pattern.compile("@Element\\(name = \"("+matcherFields.group(2)+")\",? ?.*? ?\\)", Pattern.CASE_INSENSITIVE);
			Matcher matcherOverrideField = patternOverrideField.matcher(schemaContent);
			if (matcherOverrideField.find()) {
				fields.put(matcherOverrideField.group(1), matcherFields.group(1));
				getLog().debug("#Attr:"+matcherFields.group(1)+" "+matcherOverrideField.group(1));
			}
			// Not overrided, use the found one
			else {
				fields.put(matcherFields.group(2), matcherFields.group(1));
				getLog().debug("#Attr:"+matcherFields.group(1)+" "+matcherFields.group(2));
			}

		}

		String newSchemaContent = new String(schemaContent);
		if (!isEnum && !isAbstract) {
			// Equals
			StringBuilder equalsStuff = new StringBuilder("\t/**\n\t * This method is not tested, and using contains on this object may failed\n\t */\n\tpublic boolean equals(Object o) {\n");
			equalsStuff.append("\t\tif (o == null) { return false; }\n");
			equalsStuff.append("\t\tif (o == this) { return true; }\n");
			equalsStuff.append("\t\tif (o.getClass() != getClass()) {\n");
			equalsStuff.append("\t\t\treturn false;\n");
			equalsStuff.append("\t\t}\n");
			// No fields
			if (fields.size() <= 0) {
				equalsStuff.append("\t\treturn true;\n");
			}
			else {
				equalsStuff.append("\t\t"+className+" rhs = ("+className+") o;\n");
				equalsStuff.append("\t\treturn (\n");
				// Super equals
				if (isExtension) {
					equalsStuff.append("\t\t\tsuper.equals(rhs)\n");
				}
				// Equals for all fields
				int i = 0;
				for (String fieldName : fields.keySet()) {
					String type = fields.get(fieldName);
					String fieldNameUcfirst = fieldName.replaceFirst("[a-zA-Z]{1}", fieldName.substring(0,1).toUpperCase());
					equalsStuff.append("\t\t\t");
					// Not first
					if (isExtension || 0 != i) {
						equalsStuff.append("&& ");
					}
					String accessor = "get"+fieldNameUcfirst+"()";
					if (isBooleanType(type)) {
						accessor = "is"+fieldNameUcfirst+"()";
					}
					// Simple type
					if (isSimpleType(type)) {
						equalsStuff.append("(this."+accessor+" == rhs."+accessor+")");
					}
					// Object type
					else {
						equalsStuff.append("(");
						equalsStuff.append("this."+accessor+" == rhs."+accessor); // same reference
						equalsStuff.append("|| (null != this."+accessor+" && this."+accessor+".equals(rhs."+accessor+"))"); // or same content
						equalsStuff.append(")");
					}
					//				else if (!isListType(type)) {
					//				}
					//				// List type
					//				else {
					//				}
					equalsStuff.append("\n");
					i++;
				}
				equalsStuff.append("\t\t);\n");
			}
			equalsStuff.append("\t}\n\n");
			
			// - HashCode
			StringBuilder hashCodeStuff = new StringBuilder("\t/**\n\t * This method is not tested, and using contains on this object may failed\n\t */\n\tpublic int hashCode() {\n");
			hashCodeStuff.append("\t\tint result = 7;\n");
			hashCodeStuff.append("\t\tfinal int multiplier = 31;\n");
			// No fields
			if (!isExtension && fields.size() <= 0) {
				hashCodeStuff.append("\t\treturn result*multiplier;\n");
			}
			else {
				// Super equals
				if (isExtension) {
					hashCodeStuff.append("\t\tresult = multiplier*result + super.hashCode();\n");
				}
				// Equals for all fields
				int i = 0;
				for (String fieldName : fields.keySet()) {
					String type = fields.get(fieldName);
					String fieldNameUcfirst = fieldName.replaceFirst("[a-zA-Z]{1}", fieldName.substring(0,1).toUpperCase());
					hashCodeStuff.append("\t\t");
					String accessor = "get"+fieldNameUcfirst+"()";
					if (isBooleanType(type)) {
						accessor = "is"+fieldNameUcfirst+"()";
					}
					// Simple type
					if (isSimpleType(type)) {
						if (isBooleanType(type)) {
							hashCodeStuff.append("result = multiplier*result + (this."+accessor+" ? 1231 : 1237);\n");
						}
						else if ("long".equals(type)) {
							hashCodeStuff.append("result = multiplier*result + (int)(this."+accessor+" ^(this."+accessor+" >>> 32));\n");
						}
						else {
							hashCodeStuff.append("result = multiplier*result + (int)this."+accessor+";\n");
						}
					}
					// Object type
					else {
						hashCodeStuff.append("result = multiplier*result + (null == this."+accessor+" ? 0 : this."+accessor+".hashCode());\n");
					}
					i++;
				}
				hashCodeStuff.append("\t\treturn result;\n");
			}
			hashCodeStuff.append("\t}\n\n");
			

			// - ToString
			StringBuilder toStringStuff = new StringBuilder("\tpublic String toString() {\n");
			toStringStuff.append("\t\tfinal String separator = System.getProperty(\"line.separator\");\n");
			toStringStuff.append("\t\tStringBuilder sb = new StringBuilder(\""+className+"(\"+separator);\n");
			if (isExtension || fields.size() > 0) {
				// Super
				if (isExtension) {
					toStringStuff.append("\t\tsb.append(super.toString());\n");
				}
				// Actual fields
				int i = 0;
				for (String fieldName : fields.keySet()) {
					String type = fields.get(fieldName);
					String fieldNameUcfirst = fieldName.replaceFirst("[a-zA-Z]{1}", fieldName.substring(0,1).toUpperCase());
					toStringStuff.append("\t\t");
					String accessor = "get"+fieldNameUcfirst+"()";
					if (isBooleanType(type)) {
						accessor = "is"+fieldNameUcfirst+"()";
					}
					// Simple type
					if (isSimpleType(type)) {
						if (isBooleanType(type)) {
							toStringStuff.append("sb.append(\""+fieldNameUcfirst+": \"+(this."+accessor+" ? \"yes\" : \"no\"));\n");
						}
						else {
							toStringStuff.append("sb.append(\""+fieldNameUcfirst+": \"+this."+accessor+");\n");
						}
					}
					// Object type
					else {
						toStringStuff.append("sb.append(\""+fieldNameUcfirst+": \"+this."+accessor+");\n");
					}
					// Not the end
					if ((i+1) != fields.size()) {
						toStringStuff.append("\t\tsb.append(\",\"+separator);\n");
					}
					i++;
				}
			}
			toStringStuff.append("\t\tsb.append(\")\"+separator);\n");
			toStringStuff.append("\t\treturn sb.toString();\n");
			toStringStuff.append("\t}\n\n");
			
			textToFind = "}\n$";
			textToReplace = "\n"+equalsStuff.toString()+hashCodeStuff.toString()+toStringStuff.toString()+"\n}\n";
			newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		}

		return newSchemaContent;
	}

	private boolean isListType(String type) {
		return (!isSimpleType(type) && isListOrArray(type));
	}

	private boolean isSimpleType(String type) {
		return ("int".equals(type)
				|| "double".equals(type)
				|| "boolean".equals(type)
				|| "byte".equals(type)
				|| "float".equals(type)
				|| "long".equals(type)
				);
	}

	private boolean isBooleanType(String type) {
		return ("boolean".equals(type) || "Boolean".equals(type));
	}

	private String replaceParcelableStuff(File javaFile, String schemaContent) {
		String textToFind; String textToReplace;

		// -- Check if this file has to be Parcelable
		if (isPatternMatching("Adapter1", javaFile.getAbsolutePath())) {
			return schemaContent;
		}
		if (isPatternMatching("Adapter2", javaFile.getAbsolutePath())) {
			return schemaContent;
		}
		if (!isPatternMatching("xjc", javaFile.getAbsolutePath())) {
			return schemaContent;
		}

		// -- Collect information about the file
		// Retrieve ClassName (even if it is an enum)
		Pattern patternClassName = Pattern.compile("public (?:abstract )?(class|enum) (.+)( |\\s)");
		Matcher matcherClassName = patternClassName.matcher(schemaContent);
		// Not a class or an enum: stop everything
		if (!matcherClassName.find()) {
			return schemaContent;
		}
		String className = matcherClassName.group(2).trim();
		className = className.replace(" {", ""); // For enum and empty class it is useful
		getLog().debug("###ClassName:"+className);
		// Is it an enum?
		boolean isEnum = "enum".equals(matcherClassName.group(1).trim());
		// Is abstract?
		boolean isAbstract = isPatternMatching(" abstract ", schemaContent);
		// Is it extending something?
		boolean isExtension = isPatternMatching("extends ", schemaContent);
		// Is it requiring a default constructor?
		boolean requiredDefaultConstructor = !isPatternMatching("public "+className+"\\(", schemaContent);

		// Retrieve all Fields information
		LinkedHashMap <String, String> fields = new LinkedHashMap <String, String>();
		Pattern patternFields = Pattern.compile("(?:protected|private) (?:final )?(?:static )?([^ ]+) ([^ ]+);\n", Pattern.CASE_INSENSITIVE);
		Matcher matcherFields = patternFields.matcher(schemaContent);
		while (matcherFields.find()) {
			fields.put(matcherFields.group(2), matcherFields.group(1));
			getLog().debug("#Attr:"+matcherFields.group(1)+" "+matcherFields.group(2));
		}

		// -- Add Parcelable information to the file
		// - Implement parcelable
		Pattern patternIsSerializable = Pattern.compile("implements Serializable");
		Matcher matcherIsSerializable = patternIsSerializable.matcher(schemaContent);
		boolean isSerializable = matcherIsSerializable.find();
		// enum
		if (isEnum) {
			textToFind = "public enum (.+) ";
			textToReplace = "public enum $1 implements Parcelable ";
		}
		// Class
		else if (!isSerializable) {
			textToFind = "public class (.+) ";
			textToReplace = "public class $1 implements Parcelable ";
		}
		else {
			textToFind = "implements Serializable";
			textToReplace = "implements Serializable, Parcelable";
		}
		String newSchemaContent = findReplacePattern(schemaContent, textToFind, textToReplace);

		// - Add imports
		// Parcelable imports
		textToFind = "package (.+);";
		textToReplace = "package $1;\n\n\nimport android\\.os\\.Parcel;\nimport android\\.os\\.Parcelable;\nimport java\\.util\\.Arrays;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		// Specific imports (if some specific classes are found in the class)
		if (isPatternMatching("URI", newSchemaContent)) {
			textToFind = "package (.+);";
			textToReplace = "package $1;\n\n\nimport java\\.net\\.URI;\nimport java\\.net\\.URISyntaxException;\n";
			newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		}
		if (isPatternMatching("XMLGregorianCalendar", newSchemaContent)) {
			textToFind = "package (.+);";
			textToReplace = "package $1;\n\n\nimport javax\\.xml\\.datatype\\.DatatypeConfigurationException;\nimport javax\\.xml\\.datatype\\.DatatypeFactory;\nimport javax\\.xml\\.datatype\\.XMLGregorianCalendar;\n";
			newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		}
		if (isPatternMatching(" Object ", newSchemaContent)) {
			textToFind = "([\\(| ])Object ";
			textToReplace = "$1Parcelable ";
			newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		}

		// -- Generate the parcelable inherited methods
		String parcelableStuff = generateParcelableStuff(isEnum, isExtension, isAbstract, requiredDefaultConstructor, className, fields);
		textToFind = "}\n$";
		textToReplace = "\n"+parcelableStuff+"\n}\n";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);

		return newSchemaContent;
	}

	/**
	 * Generate parcelable inherited methods
	 * @param schemaContent
	 * @param className
	 * @param fieldClasses
	 * @return
	 */
	private String generateParcelableStuff(boolean isEnum, boolean isExtension, boolean isAbstract, boolean requiredDefaultConstructor, String className, LinkedHashMap <String, String> fieldClasses) {
		StringBuilder str = new StringBuilder();
		// Constructors
		if (requiredDefaultConstructor && !isEnum) {
			str.append("\tpublic PARCELABLECLASSNAME() { }\n\n");
		}
		if (!isEnum || (isEnum && fieldClasses.size() > 0)) {
			str.append("\t"+(isEnum ? "" : "public ")+"PARCELABLECLASSNAME(Parcel in) {\n");
			// Call to super constructor: already done in read	
			//			if (isExtension) {
			//				str.append("\t\tsuper(in);\n");
			//			}
			if (isEnum) {
				str.append("READPARCELABLE");
			}
			else {
				str.append("\t\treadFromParcel(in);\n");
			}
			str.append("\t}\n\n");
		}

		str.append("\tpublic int describeContents() {\n");
		str.append("\t\treturn 0;\n");
		str.append("\t}\n\n");

		if (!isEnum) {
			str.append("\tprotected void readFromParcel(Parcel in) {\n");
			if (isExtension) {
				str.append("\t\tsuper.readFromParcel(in);\n");
			}
			str.append("READPARCELABLE");
			str.append("\t}\n\n");
		}

		str.append("\tpublic void writeToParcel(Parcel dest, int flags) {\n");
		if (isExtension) {
			str.append("\t\tsuper.writeToParcel(dest, flags);\n");
		}
		str.append("WRITEPARCELABLE");
		str.append("\t}\n\n");

		if (!isAbstract) {
			str.append("\tpublic static final Parcelable.Creator<PARCELABLECLASSNAME> CREATOR = new Parcelable.Creator<PARCELABLECLASSNAME>() {\n");
			if (isEnum) {
				str.append("\t\tpublic PARCELABLECLASSNAME createFromParcel(final Parcel in) {\n");
				str.append("\t\t\treturn PARCELABLECLASSNAME.fromValue(in.readString());\n");
				str.append("\t\t}\n");
			}
			else {
				str.append("\t\tpublic PARCELABLECLASSNAME createFromParcel(Parcel in) {\n");
				str.append("\t\t\treturn new PARCELABLECLASSNAME(in);\n");
				str.append("\t\t}\n");
			}
			str.append("\t\tpublic PARCELABLECLASSNAME[] newArray(int size) {\n");
			str.append("\t\t\treturn new PARCELABLECLASSNAME[size];\n");
			str.append("\t\t}\n");
			str.append("\t};\n\n");
		}
		else {
			str.append("\t\tpublic static Parcelable.Creator CREATOR;\n");
		}
		String parcelableStuff = str.toString();

		// ClassName
		Pattern patternClassName = Pattern.compile("PARCELABLECLASSNAME");
		Matcher matcherClassName = patternClassName.matcher(parcelableStuff);
		parcelableStuff = matcherClassName.replaceAll(className);

		// Write/Read Parcel
		StringBuilder strWrite = new StringBuilder();
		StringBuilder strRead = new StringBuilder();
		for (String fieldName : fieldClasses.keySet()) {
			String type = fieldClasses.get(fieldName);
			String writedMethod = "dest.write"+type2ParcelableAction(type);
			strWrite.append("\t\t"+type2ParcelableWriteData(type, fieldName, writedMethod)+";\n");
			String readMethod = "in.read"+type2ParcelableAction(type);
			strRead.append("\t\t"+type2ParcelableReadData(type, fieldName, readMethod, className)+";\n");
		}
		Pattern patternWrite = Pattern.compile("WRITEPARCELABLE");
		Matcher matcherWrite = patternWrite.matcher(parcelableStuff);
		parcelableStuff = matcherWrite.replaceAll(strWrite.toString());
		Pattern patternRead = Pattern.compile("READPARCELABLE");
		Matcher matcherRead = patternRead.matcher(parcelableStuff);
		parcelableStuff = matcherRead.replaceAll(strRead.toString());
		return  parcelableStuff;
	}


	private String type2ParcelableWriteData(String type, String fieldName, String writedMethod) {
		if ("boolean".equals(type) || "Boolean".equals(type)) {
			return writedMethod+"("+fieldName+" ? 1 : 0)";
		}
		String globalType = type2ParcelableAction(type);
		if ("Parcelable".equals(globalType)) {
			return writedMethod+"("+fieldName+", flags)";
		}
		if (isListOrArray(type)) {
			if (globalType.startsWith("Parcelable") && globalType.endsWith("List")) {
				//				writedMethod = writedMethod.replace("List", "Array");
				//				return writedMethod+"(("+typeToParcelableRawType(type, false)+"[]) "+fieldName+".toArray(), flags)";
				return "dest.writeTypedList("+fieldName+")";
			}
			if ("Int".equals(typeToParcelableRawType(type, false)) && globalType.endsWith("List")) {
				return "dest.writeList("+fieldName+")";
			}
			if ("URI".equals(typeToParcelableRawType(type, false)) && globalType.endsWith("List")) {
				StringBuilder strList = new StringBuilder("List<String> "+fieldName+"StringList = new ArrayList<String>();\n");
				strList.append("\t\tfor(int i=0; i<"+fieldName+".size(); i++) {\n");
				strList.append("\t\t\t"+fieldName+"StringList.add("+fieldName+".get(i).toASCIIString());\n");
				strList.append("\t\t}\n");
				strList.append("\t\tdest.writeStringList("+fieldName+"StringList)");
				return strList.toString();
			}
			if ("Byte".equals(typeToParcelableRawType(type, false)) ) {
				String sByteCode = "dest.writeInt(" + fieldName + ".length);\n\t\t" + 
						"dest.writeByteArray(" + fieldName + ")";
				return sByteCode;
			}
			return writedMethod+"("+fieldName+")";
		}
		if ("URI".equals(typeToParcelableRawType(type, false))) {
			return "dest.writeString("+fieldName+".toASCIIString())";
		}
		if ("XMLGregorianCalendar".equals(typeToParcelableRawType(type, false))) {
			return "dest.writeString("+fieldName+".toString())";
		}
		if ("Date".equals(typeToParcelableRawType(type, false))) {
			return "DateFormat df = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss Z\");\n\t\tString val = df.format(" +fieldName+ ");\n\t\tdest.writeString(val); ";
		}
		return writedMethod+"("+fieldName+")";
	}

	private String type2ParcelableReadData(String type, String fieldName, String readMethod, String className) {
		if ("boolean".equals(type) || "Boolean".equals(type)) {
			return fieldName+" = (1=="+readMethod+"() ? true : false)";
		}
		String globalType = type2ParcelableAction(type);
		if ("Parcelable".equals(globalType)) {
			return fieldName+" = "+readMethod+"("+type+".class.getClassLoader())";
		}
		if (isListOrArray(type)) {
			if (globalType.startsWith("Parcelable") && globalType.endsWith("List")) {
				StringBuilder strList = new StringBuilder(fieldName+" = new ArrayList<"+typeToParcelableRawType(type, false)+">();\n");
				strList.append("\t\tin.readTypedList("+fieldName+", "+typeToParcelableRawType(type, false)+".CREATOR)");
				return strList.toString();
			}
			if ("Int".equals(typeToParcelableRawType(type, false)) && globalType.endsWith("List")) {
				return "in.readList("+fieldName+", Integer.class.getClassLoader())";
			}
			if ("URI".equals(typeToParcelableRawType(type, false)) && globalType.endsWith("List")) {
				StringBuilder strList = new StringBuilder("List<String> "+fieldName+"StringList = new ArrayList<String>();\n");
				strList.append("\t\t"+fieldName+" = new ArrayList<URI>();\n");
				strList.append("\t\tin.readStringList("+fieldName+"StringList);\n");
				strList.append("\t\ttry {\n");
				strList.append("\t\t\tfor(int i=0; i<"+fieldName+"StringList.size(); i++) {\n");
				strList.append("\t\t\t\t"+fieldName+".add(new URI("+fieldName+"StringList.get(i)));\n");
				strList.append("\t\t\t}\n");
				strList.append("\t\t} catch(URISyntaxException e) { System.out.println(\"Arg, can't create URI for field fileUris\"); }\n");
				strList.append("\t\tint i=0");
				return strList.toString();
			}
			if ("Byte".equals(typeToParcelableRawType(type, false)) ) {
				String sByteCode = fieldName + " = new byte[in.readInt()];\n\t\t" + 
						"in.readByteArray(" + fieldName + ")";
				return sByteCode;
			}
			if (globalType.endsWith("List")) {
				return "if (null == "+fieldName+") { "+fieldName+" = new ArrayList(); }\n\t\t"+
						readMethod+"("+fieldName+")";
			}
			return readMethod+"("+fieldName+")";
		}
		if ("URI".equals(typeToParcelableRawType(type, false))) {
			return "try { "+fieldName+" = new URI(in.readString()); } catch(URISyntaxException e) { System.out.println(\"Arg, can't create URI for field "+fieldName+"\"); }";
		}
		if ("XMLGregorianCalendar".equals(typeToParcelableRawType(type, false))) {
			return "try { "+fieldName+" = DatatypeFactory.newInstance().newXMLGregorianCalendar(in.readString()); } catch(DatatypeConfigurationException e) { System.out.println(\"Arg, can't create XMLGregorianCalendar for field "+fieldName+"\"); }";
		}
		if ("Date".equals(typeToParcelableRawType(type, false))) {
			return "DateFormat df = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss Z\");\n\t\ttry { " + fieldName + " = df.parse(in.readString()); } catch(java.text.ParseException pEx) { System.out.println(\"Arg, can't parse DateTime for field "+fieldName+"\"); }";
		}
		return fieldName+" = "+readMethod+"()";
	}

	private boolean isListOrArray(String type) {
		return type.endsWith("[]") || type.startsWith("List<");
	}

	private String type2ParcelableAction(String type) {
		// -- Simple type
		String suffixe = "";
		if (type.startsWith("List<")) {
			suffixe = "List";
			type = type.replace("List<", "");
			type = type.replace(">", "");
		}
		if (type.endsWith("[]")) {
			suffixe = "Array";
			type = type.replace("[]", "");
		}
		return typeToParcelableGlobalType(type)+suffixe;
	}


	private String typeToParcelableGlobalType(String type, boolean rawTypeAreParcelable) {
		// Very simple
		Pattern patternSimpleType = Pattern.compile("(String|Float|Double|Long|Byte)");
		Matcher matcherSimpleType = patternSimpleType.matcher(type);
		if (matcherSimpleType.find()) {
			return type;
		}
		if ("int".equals(type) || "Integer".equals(type) || "boolean".equals(type) || "Boolean".equals(type)) {
			return "Int";
		}
		if ("double".equals(type)) {
			return "Double";
		}
		if ("byte".equals(type)) {
			return "Byte";
		}
		if ("float".equals(type)) {
			return "Float";
		}
		if ("long".equals(type) ) {
			return "Long";
		}
		// Manual steps
		if ("XMLGregorianCalendar".equals(type) || "URI".equals(type) || "Date".equals(type)) {
			return "String";
		}
		// Parcelable
		if (rawTypeAreParcelable) {
			return "Parcelable";
		}
		return type;
	}
	private String typeToParcelableRawType(String type, boolean rawTypeAreParcelable) {
		if (type.startsWith("List<")) {
			type = type.replace("List<", "");
			type = type.replace(">", "");
		}
		if (type.endsWith("[]")) {
			type = type.replace("[]", "");
		}
		// Very simple
		Pattern patternSimpleType = Pattern.compile("(String|Float|Double|Long|Byte)");
		Matcher matcherSimpleType = patternSimpleType.matcher(type);
		if (matcherSimpleType.find()) {
			return type;
		}
		if ("int".equals(type) || "Integer".equals(type) || "boolean".equals(type) || "Boolean".equals(type)) {
			return "Int";
		}
		if ("double".equals(type)) {
			return "Double";
		}
		if ("byte".equals(type)) {
			return "Byte";
		}
		if ("float".equals(type)) {
			return "Float";
		}
		if ("long".equals(type) ) {
			return "Long";
		}
		// Manual steps
		if ("XMLGregorianCalendar".equals(type) || "URI".equals(type) || "Date".equals(type)) {
			return type;
		}
		// Parcelable
		if (rawTypeAreParcelable) {
			return "Parcelable";
		}
		return type;
	}

	public String typeToParcelableGlobalType(String type) {
		return typeToParcelableGlobalType(type, true);
	}

	/**
	 * Check if the pattern is found in a string
	 * @param textToFind Text to find 
	 * @param textWhereToSearch Text where to find the pattern
	 * @param flags Regex flags
	 * @return True if the pattern is founded
	 */
	private static boolean isPatternMatching(String textToFind, String textWhereToSearch, int flags) {
		Pattern pattern = Pattern.compile(textToFind, flags);
		Matcher matcher = pattern.matcher(textWhereToSearch);
		return  matcher.find();
	}

	/**
	 * Check if the pattern is found in a string
	 * @param textToFind Text to find 
	 * @param textWhereToSearch Text where to find the pattern
	 * @return True if the pattern is founded
	 */
	private static boolean isPatternMatching(String textToFind, String textWhereToSearch) {
		return isPatternMatching(textToFind, textWhereToSearch, Pattern.CASE_INSENSITIVE);
	}

	/**Replaces a regular expression with a specified string
	 * @param schemaContent The string to check
	 * @param textToFind Text to find 
	 * @param textToReplace Text to replace
	 * @return Updated String with changes
	 */
	private static String findReplacePattern(String schemaContent, String textToFind, String textToReplace, int flags) {
		Pattern patternImport = Pattern.compile(textToFind, flags);
		Matcher matcherImport = patternImport.matcher(schemaContent);
		int i = matcherImport.groupCount();

		return  matcherImport.replaceAll(textToReplace);
	}

	private static String findReplacePattern(String schemaContent, String textToFind, String textToReplace) {
		return findReplacePattern(schemaContent, textToFind, textToReplace, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Init parameters
	 */
	private void initParameters() {
		if (!folderOutputDirectory.equals(".") && !folderOutputDirectory.endsWith("/")) {
			folderOutputDirectory += "/";
		}
		if (!folderInputDirectory.equals(".") && !folderInputDirectory.endsWith("/")) {
			folderInputDirectory += "/";
		}
	}

	// Object factory processing code

	private static final Pattern elementDeclPattern = Pattern.compile("\\s*@XmlElementDecl\\(([^\\)]*)\\)");
	private static final Pattern namespacePattern = Pattern.compile(".*namespace\\s*=\\s*\"([^\"]*)\".*");
	private static final Pattern elementNamePattern = Pattern.compile(".*name\\s*=\\s*\"([^\"]*)\".*");
	private static final String pkgDecl = "package ";
	private static final String COMMENT = "// This is a file generated by some gorgeous code in Jaxb2Simple.processObjectFactory(File objectFactoryFile) :(";
	private static final String IMPORT_NS = "import org.simpleframework.xml.Namespace;";
	private static final String IMPORT_ROOT = "import org.simpleframework.xml.Root;";
	private static final String ROOT_ANNOT = "@Root(name=\"";
	private static final String NS_ANNOT = "@Namespace(reference=\"";
	private static final String ANNOT_END = "\")";

	private void processObjectFactory(File objectFactoryFile) throws IOException {
		HashSet<String> newClasses = new HashSet<String>();

		File parentDirectory = objectFactoryFile.getParentFile();
		String pkgName = null; 
		BufferedReader br = new BufferedReader(new FileReader(objectFactoryFile));
		while (true) {
			String line = br.readLine();
			if (line==null)
				break;
			//getLog().debug("Going to match: '"+line+"'");
			if (pkgName==null && line.startsWith(pkgDecl)) {
				pkgName = line.substring(pkgDecl.length(),line.length()-1);
				getLog().debug("Got package: '"+pkgName+"'");
			}
			else {
				Matcher edm = elementDeclPattern.matcher(line);				
				if (edm.matches()) {
					//getLog().debug("Found XmlElementDecl - parsing namespace and elementName of '"+edm.group(1)+"'");
					Matcher nm = namespacePattern.matcher(edm.group(1));
					if (nm.matches()) {
						Matcher enm = elementNamePattern.matcher(edm.group(1));
						if (enm.matches()) {
							//getLog().debug("Bulding empty bean for {"+nm.group(1)+"}"+enm.group(1));
							String newC = buildEmptySimpleXmlBean(parentDirectory, pkgName, nm.group(1), enm.group(1));
							newClasses.add(newC);
						}
					}
				}
			}
		}

		newClassesOnPackage.put(pkgName,newClasses);

		br.close();
	}

	private String buildEmptySimpleXmlBean(File directory, String pkgName,
			String namespace, String elementName) throws IOException {
		String className = classifyName(elementName);
		getLog().debug("Creating empty SimpleXML bean '"+pkgName+"."+className+" for element {'"+namespace+"}"+elementName+" in directory "+directory.getAbsolutePath()+"...");
		File newBeanFile = new File(directory, className+".java");
		newBeanFile.createNewFile(); // TODO check if className already exists
		PrintWriter filePw = new PrintWriter(new FileOutputStream(newBeanFile));
		filePw.println(COMMENT);
		filePw.println();
		filePw.println(pkgDecl+pkgName+";");
		filePw.println();
		filePw.println(IMPORT_NS);
		filePw.println(IMPORT_ROOT);
		filePw.println();
		filePw.println(NS_ANNOT+namespace+ANNOT_END);
		filePw.println(ROOT_ANNOT+elementName+ANNOT_END);
		filePw.println("public class "+className+" {");
		filePw.println("\tpublic "+className+"() {}");
		filePw.println("}");
		filePw.flush();
		filePw.close();

		return className;
	}

	private static String classifyName(String elementName) {
		// TODO properly, using com.sun.codemodel.JCodeModel and so on
		String[] parts = elementName.split("-|_");
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<parts.length; i++) {
			sb.append(Character.toUpperCase(parts[i].charAt(0)));
			sb.append(parts[i].substring(1));
		}
		return sb.toString();
	}

	private void processPackageInfo(File javaFile) throws IOException {
		String pkgName = null;
		String ns = null;
		BufferedReader br = new BufferedReader(new FileReader(javaFile));
		while (true) {
			String line = br.readLine();
			if (line==null)
				break;

			if (pkgName==null && line.startsWith(pkgDecl)) {
				pkgName = line.substring(pkgDecl.length(),line.length()-1);
				getLog().debug("Got package: '"+pkgName+"'");
			}

			if (ns==null) {
				Matcher nm = namespacePattern.matcher(line);
				if (nm.matches())
					ns = nm.group(1);
			}

			if (pkgName!=null && ns!=null) {
				namespaceForPackage.put(pkgName, ns);
				return;
			}
		}
	}
}
