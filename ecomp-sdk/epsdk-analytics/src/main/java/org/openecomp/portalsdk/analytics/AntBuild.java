/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.analytics;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author sundar 
 * This class is used to get version and Build information when
 *         user run "java -jar raptor_classes.jar" command.
 */
public class AntBuild {
	
	public static String buildNum = "";

	public static void main(String[] args) {
		System.out.println("Jar (raptor_classes.jar) Information: ");
		readManifest();
	}

	public static void readManifest() {
		try {
			Class clazz = AntBuild.class;
			String classContainer = clazz.getProtectionDomain().getCodeSource().getLocation().toString();
			URL manifestUrl = new URL("jar:" + classContainer + "!/META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(manifestUrl.openStream());
			
			//JarFile jar = new JarFile("../lib/raptor_classes.jar");
			//Manifest manifest = jar.getManifest();

			Attributes attribs = manifest.getMainAttributes();
			Iterator it = attribs.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Attributes.Name attributeName = (Attributes.Name) entry.getKey();
				String attributeValue = (String) entry.getValue();
				if (attributeName.toString().equals("Created-By"))
					System.out.println("Java HotSpot(TM) Client VM " + " : " + attributeValue);
				else if (attributeName.toString().equals("Java-Version"))
					System.out.println("Java Version " + " : " + attributeValue);
				else if (attributeName.toString().equals("Java-Runtime-Version"))
					System.out.println("Java Runtime Version " + " : " + attributeValue);
				else if (attributeName.toString().equals("Ant-Version"))
					System.out.println(attributeName.toString() + " : " + attributeValue);
				else {
					if(attributeName.toString().startsWith("Raptor")) {
						if (attributeName.toString().startsWith("Raptor-Build-Version"))
							buildNum = attributeValue;
						System.out.println(attributeName.toString() + " : " + attributeValue);
					}
				}
			}

		} catch (IOException e) {
			System.err.println("Cannot read jar-file manifest: "
					+ e.getMessage());
		}
	}
	
	public static String getBuildNum() {
		if (buildNum.length()>0)
			return buildNum;
		else {
			readManifest();
			return buildNum;
		}
	}
}
