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
package org.openecomp.portalsdk.analytics.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.scheduler.SchedulerUtil.Executor;
import org.openecomp.portalsdk.analytics.system.Globals;



public class SendEmail {
	
SchedulerUtil schedulerUtil;
	
	public SendEmail() {
		
	}

	
	public  void sendEmail( String p_mail_server, String p_sender, String p_subject, String p_mail_text, String p_url, int p_file_type, int p_schedule_id, int p_time_interval, boolean p_send_attachment, int connectionTimeout) throws SQLException, ReportSQLException{
		
		String allEmailAddr = "";
		final List<String> emailArr = new ArrayList<String>();
		//int count1 = 0;
		String schedular_email;


		schedular_email = (String) schedulerUtil.getSingleResult("select email from fn_user au, cr_report_schedule crs where CRS.SCHED_USER_ID = AU.USER_ID and CRS.SCHEDULE_ID = "+ p_schedule_id, "email");


		String sql=Globals.getSchedulerUserEmails().replace("[p_schedule_id]", p_schedule_id+"");
		schedulerUtil.getAndExecute(sql, new Executor() {

			@Override
			public void execute(ResultSet rs) throws SQLException {

				emailArr.add(rs.getString("email"));
				// count1 = count1 + 1
			}

		});

		if (!p_send_attachment) {
			http_to_blob(p_url, p_file_type, p_schedule_id, connectionTimeout);
		}

		int i = 0;
		for (String email : emailArr) {
			/* If the email address is invalid ignore that email address */
			if (email.contains("@")) {
				
				if (i == 0)
					allEmailAddr = email;
				else
					allEmailAddr += ',' + email;

				i++;
			}
		}

		/*List<MailAttachment> mailAttachments = null;

		if (p_file_type > 1 && p_send_attachment) {
			mailAttachments = add_attachment(p_url, p_file_type, p_schedule_id, connectionTimeout);
		}
		AppUtils.notifyWithAttachments(p_mail_text, emailArr.toArray(new String[emailArr.size()]), p_sender, p_subject, new String[] { schedular_email }, null, mailAttachments, true);
        */  
	}

	class HistRec {

		String file_blob;
		BigDecimal rep_id;
		BigDecimal hist_id;
		String file_name;
		int sched_user_id;
		String recurrence;
		int file_size = 0;
		String raptor_url;
		int schedule_id;
		int file_type_id;
		int user_id;
		String deleted_yn;
	}


	private  HistRec http_to_blob(String p_url, int v_file_type, int p_schedule_id, int connectionTimeout) throws SQLException , ReportSQLException{

		
		final HistRec v_hist_rec  = initializeVHistoryRecord(p_url, v_file_type, p_schedule_id);
		HttpURLConnection con = null;
		try {
			URL url = new URL(p_url);
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(connectionTimeout*1000);
			con.setRequestMethod("GET");
			schedulerUtil.insertOrUpdate("INSERT INTO cr_filehist_log (SCHEDULE_ID, url, notes, run_time) VALUES ("+ p_schedule_id +",'" + p_url+ "','http_to_blob: Initiated HTTP request', " + Globals.getCurrentDateString() + " )");
			int responseCode = con.getResponseCode();
			String outputFolder = Globals.getProjectFolder() + java.io.File.separator + Globals.getOutputFolder();
			String fileName = v_hist_rec.file_name;
			createFile(con, outputFolder, fileName);
			
			File readFile = new File(outputFolder + java.io.File.separator + fileName);
			// need to revist this conversion; may not be safe for large file sizes
			v_hist_rec.file_size = (int)readFile.length();
			
			schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + p_schedule_id + ",'" + p_url +"', 'http_to_blob: http response recieved. Code " + responseCode  + "', " + Globals.getCurrentDateString() + " )");
			
			//v_hist_rec.file_blob = response.toString();
			//v_hist_rec.file_size = v_hist_rec.file_blob.length();
			
			List<Object> params = new ArrayList<Object>();
			List<Integer> types = new ArrayList<Integer>();
			prepareHisRecUpdate(v_hist_rec, params, types);
			
			schedulerUtil
					.insertOrUpdateWithPrepared("INSERT INTO cr_report_file_history(HIST_ID, SCHED_USER_ID, SCHEDULE_ID, USER_ID, REP_ID, RUN_DATE, RECURRENCE, FILE_TYPE_ID, FILE_NAME, FILE_SIZE, RAPTOR_URL, ERROR_YN, ERROR_CODE, DELETED_YN, DELETED_BY)"
							+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							params, types
							);
	        
			FileInputStream fileStream = new FileInputStream(readFile);
			schedulerUtil.updateBinaryStream("update cr_report_file_history set file_blob = ? where hist_id = ?", v_hist_rec.hist_id, fileStream, v_hist_rec.file_size);
			fileStream.close();
			
	        String userAddRecSql = 
	       	Globals.getSchedulerUserEmails().replace("[p_schedule_id]", p_schedule_id+"");
	        		
			schedulerUtil.getAndExecute(userAddRecSql, new Executor() {

				@Override
				public void execute(ResultSet rs) throws SQLException {

					try {
						schedulerUtil.insertOrUpdate("INSERT INTO CR_HIST_USER_MAP (HIST_ID, USER_ID) values ( " + v_hist_rec.hist_id + "," + rs.getInt("user_id") + ")");
					} catch (ReportSQLException e) {
						throw new SQLException(e.getMessage());
					}
				}

			});
	
			schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + p_schedule_id + ",'" + p_url + "','Success: http_to_blob', " + Globals.getCurrentDateString() + " )");
			
			
		} catch (Exception e) {
			schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + p_schedule_id + ",'" + p_url + "', 'Failure: http_to_blob : Exception" + e.getMessage() +"', " + Globals.getCurrentDateString() + " )");
			e.printStackTrace();
		} finally {
			if(con != null)
				con.disconnect();
		}
		return v_hist_rec;
		
	}


	protected void prepareHisRecUpdate(final HistRec v_hist_rec, List<Object> params,
			List<Integer> types) {
		params.add( v_hist_rec.hist_id);
		types.add(Types.BIGINT);
		params.add( v_hist_rec.sched_user_id);
		types.add(Types.INTEGER);
		params.add( v_hist_rec.schedule_id);
		types.add(Types.INTEGER);
		params.add( v_hist_rec.user_id);
		types.add(Types.INTEGER);
		params.add( v_hist_rec.rep_id);
		types.add(Types.BIGINT);
		params.add( new java.sql.Date(Calendar.getInstance().getTime().getTime()));
		types.add(Types.DATE);
		params.add( v_hist_rec.recurrence);
		types.add(Types.VARCHAR);
		params.add( v_hist_rec.file_type_id);
		types.add(Types.INTEGER);
		params.add( v_hist_rec.file_name);
		types.add(Types.VARCHAR);
		params.add( v_hist_rec.file_size);
		types.add(Types.INTEGER);
		params.add( v_hist_rec.raptor_url);
		types.add(Types.VARCHAR);
		params.add( "N");
		types.add(Types.VARCHAR);
		params.add( "NULL");
		types.add(Types.INTEGER);
		params.add( v_hist_rec.deleted_yn);
		types.add(Types.VARCHAR);
		params.add(v_hist_rec.sched_user_id );
		types.add(Types.INTEGER);
	}


	protected HistRec initializeVHistoryRecord(String p_url, int v_file_type,
			int p_schedule_id) throws SQLException,
			ReportSQLException {
		
		final HistRec v_hist_rec = new HistRec();
		
		v_hist_rec.rep_id = (BigDecimal) schedulerUtil.getSingleResult("SELECT rep_id FROM cr_report_schedule WHERE schedule_id =" + p_schedule_id, "rep_id");

		Object sequenceId =   schedulerUtil.getSingleResult(Globals.getSequenceNextVal().replace("[sequenceName]", "seq_cr_report_file_history"),"ID");
		
		if(sequenceId instanceof Long)
			v_hist_rec.hist_id = new BigDecimal((Long)sequenceId);
		else if(sequenceId instanceof BigDecimal)
			v_hist_rec.hist_id = (BigDecimal)sequenceId;
		
		v_hist_rec.file_name = (String) schedulerUtil.getSingleResult("select translate(title||to_char( "+ Globals.getCurrentDateString() + ",'MM-dd-yyyyHH24:mm:ss'), "
			+ "'0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'||'():;.-`~^\\|'||chr(34)||chr(39)||chr(9)||' ', "
			+ "'0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ')|| "+ v_hist_rec.hist_id +" as title FROM cr_report WHERE rep_id = "+v_hist_rec.rep_id, "title");
	
	
		class File {

			String file_name;
			String file_ext;
		}
		final File file = new File();
		schedulerUtil.getAndExecute("select template_file from cr_report_template_map where report_id = " + v_hist_rec.rep_id, new Executor() {

			@Override
			public void execute(ResultSet rs) throws SQLException {

				file.file_name = rs.getString("template_file");
				file.file_ext = file.file_name.substring(file.file_name.indexOf('.'));

			}
		});
	
		if (v_file_type == 2) {
			v_hist_rec.file_name = v_hist_rec.file_name + ".pdf";

		} else if (v_file_type == 4) {
			v_hist_rec.file_name = v_hist_rec.file_name + ".xls";

		} else if (v_file_type == 5) {
			if (file.file_name != null && file.file_ext.length() > 0) {
				v_hist_rec.file_name = v_hist_rec.file_name + file.file_ext;
			} else {
				v_hist_rec.file_name = v_hist_rec.file_name + ".xlsx";
			}
		} else if (v_file_type == 3) {
			v_hist_rec.file_name = v_hist_rec.file_name + ".csv";

		}
	

		schedulerUtil.getAndExecute("select sched_user_id, rep_id, recurrence from cr_report_schedule where schedule_id="+p_schedule_id, new Executor() {

			@Override
			public void execute(ResultSet rs) throws SQLException {
				v_hist_rec.sched_user_id = rs.getInt("sched_user_id");
				v_hist_rec.rep_id = rs.getBigDecimal("rep_id");
				v_hist_rec.recurrence = rs.getString("recurrence");
			}
		});
		
		
		v_hist_rec.file_size = 0;
		v_hist_rec.raptor_url = p_url;
		v_hist_rec.schedule_id = p_schedule_id;
		v_hist_rec.file_type_id = v_file_type;
		v_hist_rec.user_id = v_hist_rec.sched_user_id;
		v_hist_rec.deleted_yn = "N";
		
		return v_hist_rec;
	}
	
	
	
	/*private  List<MailAttachment> add_attachment(String p_url, int v_file_type, int p_schedule_id, int connectionTimeout) throws SQLException,ReportSQLException{
		
		
		List<MailAttachment> mailAttachmentList = new ArrayList<MailAttachment>();
		final HistRec vHistRec  = initializeVHistoryRecord(p_url, v_file_type, p_schedule_id);
		
		/*
		refer to http_to_blob for more details
		
		String v_content_type;
		String v_content_disposition;
		int transfer_timeout_limit   = 1800;
		String v_title;
		
		if (v_file_type == 2) {
			v_content_type = "application/pdf";
			v_content_disposition ="inline; filename=\""+v_title+".pdf\"";

		}else if(v_file_type == 4){
			v_content_type = "application/excel";
			v_content_disposition ="inline; filename=\""+v_title+".xls\"";

		}else if(v_file_type == 5){
			v_content_type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			
			if (file.file_ext != null && file.file_ext.length() > 0) {
			    v_content_disposition ="inline; filename=\""+v_title+ file.file_ext+"\"";
			    if (".xlsm".equals(file.file_ext)) {
			        v_content_type = "application/vnd.ms-excel.sheet.macroEnabled.12";
			    }
			} else {
			    v_content_disposition ="inline; filename=\""+v_title+".xlsx\""; 
			}; 
		}else if(v_file_type == 3){
			v_content_type = "application/csv";
			v_content_disposition ="inline; filename=\""+v_title+".csv\"";

		}
		* ... /
		HttpURLConnection con = null;
		try {
			URL url = new URL(p_url);
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(connectionTimeout*1000);
			con.setRequestMethod("GET");
			schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + p_schedule_id + ",'"+ p_url +"', 'Success: http request began.', " + Globals.getCurrentDateString() + " )");
			int responseCode = con.getResponseCode();
			
			String outputFolder = Globals.getProjectFolder() + java.io.File.separator + Globals.getOutputFolder();
			String fileName = vHistRec.file_name;
			createFile(con, outputFolder, fileName);
			
			MailAttachment mailAttachment = new MailAttachment();
			mailAttachment.setAttachmentType(MailAttachment.FILE_ATTACHMENT);
			mailAttachment.setFilePathName(outputFolder);
			mailAttachment.setFileName(fileName);
			mailAttachmentList.add(mailAttachment);
			
			schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + p_schedule_id + ",'" + p_url +"', 'Success: http response recieved. Code " + responseCode  + "', " + Globals.getCurrentDateString() + " )");
	
			schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + p_schedule_id + ",'" + p_url + "','Success: added attachment', " + Globals.getCurrentDateString() + " )");
		
		} catch (Exception e) {
			schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + p_schedule_id + ",'" + p_url + "', 'Failure: adding attachment : Exception" + e.getMessage() +"', " + Globals.getCurrentDateString() + " )");
			e.printStackTrace();
		} finally {
			if(con != null)
				con.disconnect();
		}
		
		
		return mailAttachmentList;
	}*/


	void createFile(HttpURLConnection con, String outputFolder, String fileName)
			throws IOException, FileNotFoundException {
		//BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		InputStream in  = con.getInputStream();
		
		try {
			
			FileOutputStream out = new FileOutputStream(outputFolder + java.io.File.separator + fileName );
			try {
				int inputLine;
				
				while ((inputLine = in.read()) != -1) {
					out.write(inputLine);
				}
				out.flush();
			}
			finally {
				out.close();
			}
			
		}
		finally {
			in.close();
		}
	}


	public SchedulerUtil getSchedulerUtil() {
		return schedulerUtil;
	}


	public void setSchedulerUtil(SchedulerUtil schedulerUtil) {
		this.schedulerUtil = schedulerUtil;
	}

}
