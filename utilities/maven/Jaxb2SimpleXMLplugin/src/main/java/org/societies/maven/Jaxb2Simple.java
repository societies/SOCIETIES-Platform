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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
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

	public void execute() throws MojoExecutionException
	{
		// Init
		initParameters();
		File startingDirectory= new File(folderInputDirectory);
		getLog().info("Source Directory: " + folderInputDirectory);
	    List<File> files = null;
	    
	    try {
			files = FileListing.getFileListing(startingDirectory);

			for (File javaFile : files) {
				if (javaFile.isFile()) { //IGNORE DIRECTORIES
					if ((javaFile.getName().equals("ObjectFactory.java") || javaFile.getName().equals("package-info.java"))) {	
						javaFile.delete(); //JAXB GENERATED FILES - NOT REQUIRED
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
		newSchemaContent = findReplacePatterns(schemaContent);

		//ENUM NEEDS TO BE SERIALIZED WITH "VALUE", NOT "NAME"
		if (newSchemaContent.indexOf("public enum ") > 0) {
			String textToFind = ".*}\n\n}\n";
			String textToReplace = TO_STRING_CODE;
			newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace); 
		}
		return newSchemaContent;
	}

	private String findReplacePatterns(StringBuffer schemaContent) {
		String newSchemaContent; String textToFind; String textToReplace;

		//import javax.xml.bind.annotation.* -> import org.simpleframework.xml.*
		//s/^\(import javax.xml.bind.annotation.\w*;\n\)*import javax.xml.bind.annotation.\w*;/import org.simpleframework.xml.*;/
		textToFind = "import javax.xml.bind.annotation.*import javax.xml.bind.annotation.\\w*;";
		textToReplace = "import org.simpleframework.xml.*;";
		newSchemaContent = findReplacePattern(schemaContent.toString(), textToFind, textToReplace, Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
				
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
		
		// @XmlElement with List -> @XmlElementList     @XmlElement(nillable = true) -> @ElementList(inline=true, entry="Service")
		// @XmlElement(name[      ]*=\(.*\)).*\(\n.*List\<.*\>.*;\)/@ElementList(inline=true, entry=\1)\2/
		textToFind   = "@XmlElement\\(.*?\\)(\n.*?List\\<(.*?)\\>.*?;)";
		textToReplace = "@ElementList(inline=true, entry=\"$2\")$1";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace, Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
		
		//@XmlElement -> @Element
		//1) if "required = true|false" is missing then add "required=false" else do nothing
		//2) remove nillable=true|false
		
		//@XmlElement(required = true, type = Integer.class, nillable = true)
		//textToFind = "(@XmlElement\\(.*)nillable = true|false?\\)";
		textToFind = ", nillable = (true|false)";
		textToReplace = "";		//"$1)";
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
		textToReplace = "@Element($1)";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		
		//NAMESPACE
		Pattern patternNS = Pattern.compile("package (.*);", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
		Matcher matcherNS = patternNS.matcher(newSchemaContent);
		String ns = "";
		if (matcherNS.find()) { 
			String pkgTmp = matcherNS.group();
			String pkgFinal = pkgTmp.substring(8, pkgTmp.indexOf(";"));
			String[] nsArr = pkgFinal.split("\\.");
			ns = "@Namespace(reference=\"http://" + nsArr[1] + "." + nsArr[0];
			for(int i=2; i<nsArr.length; i++)
				ns+="/" + nsArr[i];
			ns += "\")\n"; 
		}
		
		// @XmlRootElement -> @Root + @Namespace(reference="http://...)
		// s/@XmlRootElement(\(.*\))/@Root(\1, strict=false)/
		//textToFind = "@XmlRootElement(\\(.*)\\)\n";
		textToFind = "@XmlRootElement(\\(.*?)\\)\n";
		textToReplace = "@Root$1, strict=false)\n" + ns;
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		
		//@XmlType(.*propOrder[    ]*\(=.*\)/@Order(elements\1/  
		textToFind = "@XmlType\\(.*propOrder";
		textToReplace = "@Order(elements";
		newSchemaContent = findReplacePattern(newSchemaContent, textToFind, textToReplace);
		
		// @XmlAccessorType([ ]*XmlAccessType\.\(.*\))\n@XmlType(\(.*\)[ ]*,[ ]*propOrder[ ]*\(=.*\)/@Default(DefaultType.\1)\n@Order(elements\3/
		textToFind = "@XmlAccessorType\\(XmlAccessType.FIELD\\)";
		textToReplace = "@org.simpleframework.xml.Default";
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
		textToReplace = "@Text";
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
}
