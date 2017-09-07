/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.core.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.util.YamlUtils;

public class ElementLinkService {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ElementLinkService.class);

	/**
	 * Builds renderable model of element links in the network map. Parses YAML
	 * files with metadata and builds input for JoinJS to render in the browser as
	 * SVG.
	 *
	 * @param args
	 *            arg 0 - realPath; arg 1 - callFlowName; arg 2 - callFlowStep.
	 * @return Renderable model of element links
	 * @throws IOException
	 */
	public String buildElementLinkYaml(String[] args) throws IOException {
		String relFilePath;
		if (args[1].startsWith("custom"))
			relFilePath = SystemProperties.getProperty("customCallFlow_path");
		else
			relFilePath = SystemProperties.getProperty("element_map_file_path");
		final String yamlDirPath = new File(args[0], relFilePath).getPath();

		String callFlowBusinessYml = "";
		String callFlowStep = "";

		if (args != null && args.length > 0) {
			if (args[1] != null)
				callFlowBusinessYml = args[1] + ".yml";
			if (args[2] != null)
				callFlowStep = args[2];
		}

		ElementLinkService mapper = new ElementLinkService();
		String linkYaml = mapper.createLinkFile(yamlDirPath, callFlowBusinessYml, callFlowStep);
		return linkYaml;
	}

	@SuppressWarnings("unchecked")
	protected String createLinkFile(String resourceFilePath, String callFLowBsFileName, String callFlowStep)
			throws IOException {
		Map<String, Object> callFlowBs = YamlUtils.readYamlFile(resourceFilePath, callFLowBsFileName);
		List<Map<String, Object>> callSteps = (List<Map<String, Object>>) callFlowBs.get("callSequenceSteps");
		String callFlowName = (String) callFlowBs.get("shortName");
		return addLinks(resourceFilePath, callFlowName, callSteps, callFlowStep);
	}

	@SuppressWarnings("unchecked")
	protected String createLinkFileAdditional(String resourceFilePath, String callFLowBsFileName, String callFlowStep)
			throws Exception {
		Map<String, Object> callFlowBs;
		try {
			callFlowBs = YamlUtils.readYamlFile(resourceFilePath, callFLowBsFileName);
			List<Map<String, Object>> callSteps = (List<Map<String, Object>>) callFlowBs.get("callSequenceSteps");
			String callFlowName = (String) callFlowBs.get("shortName");
			return addLinksAdditional(resourceFilePath, callFlowName, callSteps, callFlowStep);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "createLinkFileAdditional failed", e);
			return "";
		}

	}

	@SuppressWarnings("unchecked")
	protected String addLinks(String filePath, String callFlowName, List<Map<String, Object>> callSteps,
			String callFlowStep) throws IOException {

		Map<String, List<String>> checkDuplicateMap = new HashMap<>();

		for (Map<String, Object> callStep : callSteps) {

			if (((String) callStep.get("name")).split(":")[0].trim().replace(" ", "_").equals(callFlowStep)) {

				List<Map<String, Object>> links = new ArrayList<>();

				List<Map<String, Object>> subSteps = (List<Map<String, Object>>) callStep.get("subSteps");

				for (Map<String, Object> subStep : subSteps) {
					Map<String, Object> link = new HashMap<>();

					String source = (String) subStep.get("source_tosca_id");
					String destination = (String) subStep.get("destination_tosca_id");

					if ((checkDuplicateMap.get(source) == null || checkDuplicateMap.get(source).isEmpty()
							|| !checkDuplicateMap.get(source).contains(destination)) && !source.equals(destination)) {
						if (checkDuplicateMap.get(destination) == null) {
							List<String> toscaList = new ArrayList<>();
							checkDuplicateMap.put(destination, toscaList);
						}

						if (checkDuplicateMap.get(source) == null) {
							List<String> toscaList = new ArrayList<>();
							checkDuplicateMap.put(source, toscaList);
						}

						List<String> toscaSourceList = checkDuplicateMap.get(destination);
						toscaSourceList.add(source);

						List<String> toscaDestinationList = checkDuplicateMap.get(source);
						toscaDestinationList.add(destination);

						link.put("s", source);
						link.put("d", destination);
						links.add(link);
					}

				}

				Map<String, Object> callFlowUI = new HashMap<>();
				callFlowUI.put("linkList", links);

				return YamlUtils.returnYaml(callFlowUI);
			}

		}
		return "";
	}

	protected String addLinksAdditional(String filePath, String callFlowName, List<Map<String, Object>> callSteps,
			String callFlowStep) throws IOException {

		for (Map<String, Object> callStep : callSteps) {

			if (((String) callStep.get("name")).split(":")[0].trim().replace(" ", "_").equals(callFlowStep)) {

				Map<String, Object> callFlowUI = new HashMap<>();
				try {
					List<Map<String, Object>> links = addLinkVertices(callStep);
					callFlowUI.put("linkList", links);
				} catch (Exception e) {
					logger.error(EELFLoggerDelegate.errorLogger, "addLinksAdditional failed", e);
				}
				try {
					List<String> activeIds = addActiveNodes(callStep);
					callFlowUI.put("activeIds", activeIds);
				} catch (Exception e) {
					logger.error(EELFLoggerDelegate.errorLogger, "addLinksAdditional failed", e);
				}
				try {
					List<Map<String, Object>> disconnectLinks = addDisconnectLinks(callStep);
					callFlowUI.put("disconnectLinks", disconnectLinks);
				} catch (Exception e) {
					logger.error(EELFLoggerDelegate.errorLogger, "addLinksAdditional failed", e);
				}

				return YamlUtils.returnYaml(callFlowUI);
			}
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	List<String> addActiveNodes(Map<String, Object> callStep) {
		List<String> activeIds = (List<String>) callStep.get("activeIds");
		return activeIds;
	}

	@SuppressWarnings("unchecked")
	List<Map<String, Object>> addDisconnectLinks(Map<String, Object> callStep) {
		List<Map<String, Object>> disconnectLinks = (List<Map<String, Object>>) callStep.get("disconnectLinks");
		return disconnectLinks;
	}

	@SuppressWarnings("unchecked")
	List<Map<String, Object>> addLinkVertices(Map<String, Object> callStep) {
		List<Map<String, Object>> links = new ArrayList<>();

		List<Map<String, Object>> vertices = (List<Map<String, Object>>) callStep.get("vertices");

		for (int i = 0; i < vertices.size() - 1; i++) {
			Map<String, Object> vertex = vertices.get(i);
			Map<String, Object> vertexNext = vertices.get(i + 1);

			Integer sourceX = (Integer) vertex.get("x");
			Integer sourceY = (Integer) vertex.get("y");
			String sourceD = (String) vertex.get("D");
			String sourceL = (vertex.get("L") != null) ? (String) vertex.get("L") : "-";

			if (sourceX == -999) // there is a break in the linkage
				continue;

			Integer destinationX = (Integer) vertexNext.get("x");
			Integer destinationY = (Integer) vertexNext.get("y");
			String destinationD = (String) vertexNext.get("D");

			if (destinationX == -999) // there is a break in the linkage
				continue;

			Map<String, Object> link = new HashMap<>();

			link.put("s", sourceX + "," + sourceY + "," + sourceD + "," + sourceL);
			link.put("d", destinationX + "," + destinationY + "," + destinationD);
			links.add(link);
		}
		return links;
	}

	public static void main(String[] args) throws Exception {
		String filePath = "\\D2Platform\\war\\WEB-INF\\resources\\trisim_files";
		String callFlowBusinessYml = "call_flow_hc-origination-termination-to-volteue-3.3.16-Override.yml";
		ElementLinkService mapper = new ElementLinkService();
		System.out.print(mapper.createLinkFileAdditional(filePath, callFlowBusinessYml, "Step_2"));
	}

}
