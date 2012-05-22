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
// * @extendsPlugin jaxb2
 * @phase generate-sources
 * @goal xsd
 */
public class XMPPMojo extends AbstractMojo
{
	/**
	 * Output directory for schemas
	 *
	 * @parameter default-value="src/main/resources/"
	 */
	private String folderOutputDirectory;
	/**
	 * Input directory for schemas
	 *
	 * @parameter default-value="${project.build.directory}/generated-resources/schemagen/"
	 */
	private String folderInputDirectory;
	/**
	 * Input file name of the schema
	 *
	 * @parameter default-value="schema1.xsd"
	 */
	private String file;
	/**
	 * httpNamespace of the schema
	 *
	 * @parameter
	 * @required
	 */
	private String httpNamespace;
	/**
	 * packageName of the schema
	 *
	 * @parameter
	 * @required
	 */
	private String packageName;
	/**
	 * Path to schema API of the schema
	 *
	 * @parameter default-value="../api/java/schema/"
	 */
	private String pathToParentSchema;
	/**
	 * List of beans
	 * If this list is null, complexType
	 * finishing with Bean or BeanResult
	 * will be used
	 *
	 * @parameter
	 */
	private List beans;


	public void execute() throws MojoExecutionException
	{
		// Init
		initParameters();

		// ---- Execute SchemaGen
		// To do. For now, do mvn jaxb2:schemagen
		//		getLog().info("Don't forget to begin with a \"mvn jaxb2:schemagen\"");



		// ---- Refactor the Schema
		File schemaInputDirectory = new File(folderInputDirectory+file);

		if (!schemaInputDirectory.canRead()) {
			getLog().info("Schema XSD not readable. Refactoring aborted.");
			return;
		}


		Scanner scanner = null;
		try {
			// Read the schema XSD content
			scanner = new Scanner(schemaInputDirectory);
			StringBuffer schemaContent = new StringBuffer();
			while (scanner.hasNextLine()) {
				schemaContent.append(scanner.nextLine()+"\n");
			}
			getLog().info("#################### Before refactoring");
			getLog().info(schemaContent);

			String newSchemaContent = new String();

			// - Step 1: change "complexType" of Beans to "element"
			Pattern patternComplexeType = null;
			// Bean list not null : use it
			if (null != beans) {
				StringBuffer beansString = new StringBuffer();
				int i = beans.size();
				for(String bean : (List<String>) beans) {
					i--;
					if (i == 0) {
						beansString.append(bean);
					}
					else {
						beansString.append(bean+"|");
					}
				}
				patternComplexeType = Pattern.compile("<xs:complexType name=\"("+beansString+")\"(.*?)</xs:complexType>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
			}
			// Else use complexeType finishing by Bean or BeanResult
			else {
				patternComplexeType = Pattern.compile("<xs:complexType name=\"([^\"]*Bean(?:Result)?)\"(.*?)</xs:complexType>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
			}
			Matcher matcherComplexeType = patternComplexeType.matcher(schemaContent);
			newSchemaContent = matcherComplexeType.replaceAll("<xs:element name=\"$1\">\n\t<xs:complexType$2\t</xs:complexType>\n\t</xs:element>");
			//		    getLog().info("#################### After ComplexeType");
			//		    getLog().info(newSchemaContent);

			// -- Step 2: Add relevant namespace to complex type
			//		    Pattern patternTypeNamespace = Pattern.compile("type=\"(xs:(?!char|byte|short|int|long|float|double|boolean|string))\"", Pattern.CASE_INSENSITIVE);
			Pattern patternTypeNamespace = Pattern.compile("(type|base)=\"((?!xs|ns[0-9]{1,3})[^\"]+)\"", Pattern.CASE_INSENSITIVE);
			Matcher matcherTypeNamespace = patternTypeNamespace.matcher(newSchemaContent);
			newSchemaContent = matcherTypeNamespace.replaceAll("$1=\"tns:$2\"");
			//		    getLog().info("#################### After TypeNamespace");
			//		    getLog().info(newSchemaContent);

			// - Step 3: If they are not already added, add namespace in the root markup
			Pattern patternNamespaceCheck = Pattern.compile("xmlns:tns", Pattern.CASE_INSENSITIVE);
			Matcher matcherNamespaceCheck = patternNamespaceCheck.matcher(newSchemaContent);
			if (!matcherNamespaceCheck.find()) {
				Pattern patternNamespace = Pattern.compile("<xs:schema version=\"1.0\" (.*)xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
				Matcher matcherNamespace = patternNamespace.matcher(newSchemaContent);
				newSchemaContent = matcherNamespace.replaceAll("<xs:schema version=\"1.0\" elementFormDefault=\"qualified\"\n\ttargetNamespace=\""+httpNamespace+"\"\n\txmlns:tns=\""+httpNamespace+"\"\n\t$1\n\txmlns:xs=\"http://www.w3.org/2001/XMLSchema\">");
				//			    getLog().info("#################### After Namespace");
				//			    getLog().info(newSchemaContent);
			}

			// - Step 4: If this schema import other existing schema, add their relevant URL
			Pattern patternImportCheck = Pattern.compile("schemaLocation", Pattern.CASE_INSENSITIVE);
			Matcher matcherImportCheck = patternImportCheck.matcher(newSchemaContent);
			if (matcherImportCheck.find()) {
				Pattern patternIsInternal = Pattern.compile("internal/", Pattern.CASE_INSENSITIVE);
				Matcher matcherIsInternal = patternIsInternal.matcher(httpNamespace);
				boolean isInternal = matcherIsInternal.find();
				Pattern patternImport = Pattern.compile("<xs:import namespace=\"(http://societies.org/api/(internal/)?schema/([^\"]+))\" schemaLocation=\"([^\"]*)\" ?/>", Pattern.CASE_INSENSITIVE);
				Matcher matcherImport = patternImport.matcher(newSchemaContent);
				StringBuffer importsContent = new StringBuffer();
				while (matcherImport.find()) {
					Pattern patternInternalCheck = Pattern.compile("internal/", Pattern.CASE_INSENSITIVE);
					Matcher matcherInternalCheck = patternInternalCheck.matcher(matcherImport.group());
					String httpNs = matcherImport.group(1);
					String typeHttpNs = matcherImport.group(2);
					String endHttpNs = matcherImport.group(3);
					if (matcherInternalCheck.find()) {
//						getLog().info("internal:"+httpNs);
//						getLog().info("internal:"+typeHttpNs);
//						getLog().info("internal:"+endHttpNs);
						matcherImport.appendReplacement(importsContent, "<xs:import namespace=\"http://societies.org/api/$2schema/$3\" schemaLocation=\"org.societies.api."+typeHttpNs.replace("/", ".")+"schema."+endHttpNs.replace("/", ".")+".xsd\" />");
					}
					else {
//						getLog().info("external:"+httpNs);
//						getLog().info("external:"+typeHttpNs);
//						getLog().info("external:"+endHttpNs);
						matcherImport.appendReplacement(importsContent, "<xs:import namespace=\"http://societies.org/api/$2schema/$3\" schemaLocation=\""+(isInternal ? "../../../../external/src/main/resources/" : "")+"org.societies.api.schema."+endHttpNs.replace("/", ".")+".xsd\" />");
					}
				}
				matcherImport.appendTail(importsContent);
				newSchemaContent = importsContent.toString();
				getLog().info("#################### After Import");
				getLog().info(newSchemaContent);
			}

			getLog().info("#################### After refactoring");
			getLog().info(newSchemaContent);

			// - Step 5: Save in the folderOutputDirectory
			FileWriter newFile = new FileWriter(folderOutputDirectory+packageName+".xsd");
			newFile.write(newSchemaContent);
			newFile.close();

			// - Step 6: Save in the Societies Schema API
			//		    FileWriter finalSchema = new FileWriter(pathToParentSchema+folderOutputDirectory+packageName+".xsd");
			//		    finalSchema.write(newSchemaContent);
			//		    finalSchema.close();

		} catch (FileNotFoundException e) {
			getLog().error("File not found");
			getLog().error(e);
		} catch (IOException e) {
			getLog().error("Error in file usage");
			getLog().error(e);
		}
		finally {
			scanner.close();	
		}
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
		if (!pathToParentSchema.equals(".") && !pathToParentSchema.endsWith("/")) {
			pathToParentSchema += "/";
		}
	}

	private String httpNs2packageNs(String httpNs) {
//		getLog().info("httpNs2packageNs: "+httpNs);
//		Pattern pattern = Pattern.compile("http://societies.org/api/(internal/)?schema/([^\"]+)", Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(httpNs);
//		String type = matcher.group(1);
//		String endHttpNs = matcher.group(2);
//		getLog().info("httpNs2packageNs: "+type+", endHttpNs:"+endHttpNs);
//		return new String("org.societies.api.schema."+type.replace("/", ".")+endHttpNs.replace("/", "."));
		return new String("org.societies.api.schema.");
	}
}
