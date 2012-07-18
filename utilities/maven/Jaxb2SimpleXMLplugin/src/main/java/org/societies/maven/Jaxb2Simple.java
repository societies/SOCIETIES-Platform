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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
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

			// First process ObjectFactories and package-infos
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
			
			// Process all java files now and delete unwanted ones
			for (File javaFile : files) {
				if (javaFile.isFile()) { //IGNORE DIRECTORIES
					if (javaFile.getName().equals("ObjectFactory.java") || javaFile.getName().equals("package-info.java")) {
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
		
		//import org.w3._2001.xmlschema.Adapter1; -> import org.societies.maven.converters.URIConverter;
		textToFind = "import org.w3._2001.xmlschema.Adapter1;";
		textToReplace = "import org.societies.maven.converters.URIConverter;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		
		//import javax.xml.bind.annotation.adapters.CollapsedStringAdapter; -> import org.societies.maven.converters.CollapsedStringAdapter;
		textToFind = "import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;";
		textToReplace = "import org.societies.maven.converters.CollapsedStringAdapter;";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		
		//@XmlJavaTypeAdapter(Adapter1 .class) -> @Convert(URIConverter.class)
		textToFind = "@XmlJavaTypeAdapter\\(Adapter1.*\\.class?\\)";
		textToReplace = "@Convert(URIConverter.class)";
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
		
		// /@XmlSchemaType.*/d 
		// /@XmlAnyAttribute.*/d 
		
		return newSchemaContent;
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
