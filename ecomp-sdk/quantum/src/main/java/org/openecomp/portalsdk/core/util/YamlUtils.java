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
package org.openecomp.portalsdk.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

public class YamlUtils {
	
	static Yaml yaml;
	
	static {

		Representer representer = new Representer();
		yaml = new Yaml(representer);
		
	}
	
	public static void writeYamlFile(String filePath, String fileName,
			Map<String, Object> model) throws IOException {
		FileWriter writer = new FileWriter(filePath + File.separator + fileName);
		yaml.dump(model, writer); 
		writer.close();
	}
	
	public static String returnYaml(
			Map<String, Object> model) throws IOException {
		
		return yaml.dump(model); 
	
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> readYamlFile(
			String filePath, String fileName) throws FileNotFoundException,
			IOException {
		FileReader reader = new FileReader(filePath + File.separator + fileName);
		
		Map<String,Object> callFlowBs = (Map<String,Object>)yaml.load(reader);
		reader.close();
		return callFlowBs;
	}


}
