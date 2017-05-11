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
package org.openecomp.portalsdk.analytics.system.fusion.controller;
/**
 * Raptor Blob Extract Servlet
 * 
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.springframework.web.servlet.ModelAndView;;


public class FileServletController  {

	private DataAccessService dataAccessService;

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FileServletController.class);


	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug(EELFLoggerDelegate.debugLogger, ("FileServletController:: f=" + request.getParameter("f")));

		String fname = request.getParameter("f");

		try {
			Map params = new HashMap();
			params.put("fname", fname);

			logger.debug(EELFLoggerDelegate.debugLogger, ("executing query: select file_blob from cr_report_file_history where file_name = :"
					+ fname));

			List<Object> fileFromDB = (List<Object>) getDataAccessService().executeNamedQuery("getFileWithName", params, null);

			byte[] allBytesInBlob = null;

			if (fileFromDB != null && fileFromDB.size() > 0) {

				logger.debug(EELFLoggerDelegate.debugLogger, ("reading file blob from DB..."));
				try {
					
		            /*for weblogic setup
		             * if(Globals.isWeblogicServer()) {
		            	weblogic.jdbc.vendor.oracle.OracleThinBlob aBlob = (weblogic.jdbc.vendor.oracle.OracleThinBlob) ((org.hibernate.lob.SerializableBlob) fileFromDB
								.get(0)).getWrappedBlob();
		            	InputStream inBlob = ((java.sql.Blob) aBlob).getBinaryStream();
		            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            	byte[] buf = new byte[1024];
		            	int n = 0;
		            	while ((n=inBlob.read(buf))>=0) {
		            		baos.write(buf, 0, n);
		            	}
		            	inBlob.close();
		            	allBytesInBlob = baos.toByteArray();
		             } else { */
					/* works in Hinernate3	[	oracle.sql.BLOB aBlob = (oracle.sql.BLOB) ((org.hibernate.lob.SerializableBlob) fileFromDB
									.get(0)).getWrappedBlob();
							allBytesInBlob = aBlob.getBytes(1, (int) aBlob.length()); ] */
		            // }
					
					Object fileFromDBType = fileFromDB.get(0);
					if(fileFromDBType instanceof byte[] ) // postgres
						allBytesInBlob = (byte[]) fileFromDB.get(0);
					else if (fileFromDBType instanceof Blob ) // oracle
						allBytesInBlob = ((Blob) fileFromDB.get(0)).getBytes(1, (int) ((Blob) fileFromDB.get(0)).length());
					
					
					
				} catch (Exception e) {
					logger.error(EELFLoggerDelegate.debugLogger, ("An exception has occurred: " + e.getMessage()));
					throw (e);
				}

			} else {
				logger.error(EELFLoggerDelegate.debugLogger, ("ERROR: No BLOB returned from DB..."));
				throw (new Exception("ERROR: No BLOB returned from DB..."));
			}

			serveFile(response, allBytesInBlob, fname);
			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.debugLogger, ("Exception occurred..." + e.getMessage()));
			Map<String, Object> errView = new HashMap<String, Object>();
			errView.put("error", "The requested resource was not found.");
			//return new ModelAndView(getExceptionView(), "model", errView);
			return null;
		}

	}

	private void serveFile(HttpServletResponse response, File inFile)
			throws Exception {
		OutputStream os = null;
		InputStream is = null;
		try {
			response.reset();
			is = new BufferedInputStream(new FileInputStream(inFile));
			os = new BufferedOutputStream(response.getOutputStream());
			response.setContentLength((int) inFile.length());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ inFile.getName() + "\"");
			copyStream(is, os);
			os.flush();
		} catch (Exception ex) {
			if (os == null)
				throw new Exception("Could not open output stream for file ");
			if (is == null)
				throw new Exception("Could not open input stream for file ");
		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null)
				is.close();
		}
	}

	private void serveFile(HttpServletResponse response, byte[] outStream,
			String name) throws Exception {
		OutputStream os = null;
		InputStream is = null;
		try {
			response.reset();
			response.setContentLength((int) outStream.length);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ name + "\"");
			copyStream(response, outStream);
		} catch (Exception ex) {
			if (os == null)
				throw new Exception("Could not open output stream for file ");
			if (is == null)
				throw new Exception("Could not open input stream for file ");
		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null)
				is.close();
		}
	}

	private int copyStream(InputStream in, OutputStream out) throws IOException {
		int bytes, totalBytes = 0;

		byte[] b = new byte[4096];

		while ((bytes = in.read(b, 0, b.length)) != -1) {
			totalBytes += bytes;
			out.write(b, 0, bytes);
		}
		return totalBytes;
	}

	private int copyStream(HttpServletResponse response, byte[] outStream)
			throws IOException {
		
		OutputStream os = new BufferedOutputStream(response.getOutputStream());
		os.write(outStream);
		os.flush();
		return outStream.length;
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

}
