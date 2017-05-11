/*-
 * ================================================================================
 * ECOMP Portal SDK
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
package org.openecomp.portalsdk.analytics.system.fusion;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * @author sundar 
 * This class is used to get version and Build information when
 *         user run "java -jar raptor_fusion.jar" command.
 */
public class AntBuild {

	public static void main(String[] args) {
		System.out.println("Jar (raptor_fusion.jar) Information: ");
		readManifest();
	}

	public static void readManifest() {
		try {
			JarFile jar = new JarFile("./raptor_fusion.jar");
			Manifest manifest = jar.getManifest();

			Attributes attribs = manifest.getMainAttributes();
			Iterator it = attribs.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Attributes.Name attributeName = (Attributes.Name) entry.getKey();
				String attributeValue = (String) entry.getValue();
				if (attributeName.toString().equals("Created-By"))
					System.out.println("JDK Version " + " : " + attributeValue);
				else if (attributeName.toString().equals("Ant-Version"))
					System.out.println(attributeName.toString() + " : " + attributeValue);
				else {
					if(attributeName.toString().startsWith("Raptor"))
						System.out.println(attributeName.toString() + " : " + attributeValue);
				}
			}

		} catch (IOException e) {
			System.err.println("Cannot read jar-file manifest: "
					+ e.getMessage());
		}
	}
}