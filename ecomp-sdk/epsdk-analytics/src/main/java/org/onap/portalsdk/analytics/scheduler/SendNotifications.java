/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.analytics.scheduler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.scheduler.SchedulerUtil.Executor;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.Log;

public class SendNotifications {
	
	SchedulerUtil schedulerUtil;
	public SendEmail sendEmail;
	
	public SendNotifications() throws Exception {
		schedulerUtil = new SchedulerUtil();
		sendEmail = new SendEmail();
		sendEmail.setSchedulerUtil(schedulerUtil);
		init();
	}

		
	public  void deInit() throws SQLException {
		schedulerUtil.closeConnection();
	}
	
	public  void init() throws SQLException, ReportSQLException {
		schedulerUtil.init();
	}
	
	public void send_notification(String p_mail_server, String p_sender, String p_system_name, String p_system_url, int connectionTimeout) throws RaptorException, Exception {

		System.out.println(p_mail_server + " " + p_sender + " " + p_system_name + " " + p_system_url);
		int p_time_interval = Globals.getSchedulerInterval();
		int v_num_recs = 0;
		String v_gen_key;
		BigDecimal v_id = null;
		String v_url;
		String v_r_action = "report.download.pdf";
		String v_email_msg;
		String v_formfields;
		// String error_m;
		// int transfer_timeout_limit = 1800;
		// boolean v_attach_email_yn = true;
		int v_schedule_id;
		Date v_touch_date;
		// Exception for_rec;

		Connection conn = schedulerUtil.getConnection();
		Statement stat = conn.createStatement();
		
		String CNotificationsql = 
				/*
				"SELECT x.rep_id, x.schedule_id, x.conditional_yn, x.condition_large_sql, x.notify_type, x.max_row, x.initial_formfields, x.processed_formfields, r.title, x.user_id "
				+ "FROM ("
					+ "SELECT rs.rep_id, rs.schedule_id, rs.sched_user_id user_id, rs.conditional_yn, rs.condition_large_sql, "
					+ "rs.notify_type, rs.max_row, rs.initial_formfields, rs.processed_formfields "
					+ "FROM cr_report_schedule rs "
					+ "WHERE rs.enabled_yn='Y' "
					+ "AND rs.start_"
					+ "date <= sysdate "
					+ "AND  (rs.end_date >= sysdate or rs.end_date is null ) "
					+ "AND rs.run_date IS NOT NULL "
					+ ") x, cr_report r "
				+ "WHERE x.rep_id = r.rep_id ";
				 */
		
		Globals.getAvailableSchedules().replace("[currentDate]", Globals.getCurrentDateString());
				
		
		ResultSet rs = stat.executeQuery(CNotificationsql);

		while (rs.next()) {
			
			v_schedule_id = rs.getInt("schedule_id");
			int offset = get_report_sched_offset(rs.getInt("rep_id"), v_schedule_id);
			
			if(offset >= p_time_interval) continue;

			
			v_touch_date = (Date) schedulerUtil.getSingleResult("select touch_date from cr_report_email_sent_log where schedule_id = " + v_schedule_id + " and log_id = (select max(log_id) from cr_report_email_sent_log where schedule_id = " + v_schedule_id + ")", "touch_date");
			if (v_touch_date != null) {
				if (Math.abs(System.currentTimeMillis() - v_touch_date.getTime()) /1000 < (p_time_interval - 1)) {
					return;
				}
			}

			if ("Y".equals(rs.getString("conditional_yn"))) {

				v_num_recs = (Integer) schedulerUtil.getSingleResult("select count(*) count from (" + rs.getString("condition_large_sql") + " )", "count");
			}

			if (v_num_recs > 0 || "N".equals(rs.getString("conditional_yn"))) {

				v_gen_key = ("Z" + UUID.randomUUID()).toString().substring(0,24); // 25 character string
				Object sequenceId = schedulerUtil.getSingleResult(Globals.getSequenceNextVal().replace("[sequenceName]", "seq_email_sent_log_id"), "id");
				
				if(sequenceId instanceof Long)
					v_id = new BigDecimal((Long)sequenceId);
				else if(sequenceId instanceof BigDecimal)
					v_id = (BigDecimal)sequenceId;
				
				schedulerUtil.insertOrUpdate("insert into cr_report_email_sent_log (log_id, gen_key, schedule_id, rep_id, user_id, touch_date) values (" + v_id + ",'" + v_gen_key + "'," + rs.getInt("schedule_id") + "," + rs.getInt("rep_id") + "," + rs.getInt("user_id") + ", " + Globals.getCurrentDateString() + " )");

				int notify_type = rs.getInt("notify_type");
				if (notify_type == 4)
					v_r_action = "report.download";
				else if (notify_type == 2)
					v_r_action = "report.download.pdf";
				else if (notify_type == 3)
					v_r_action = "report.csv.download";
				else if (notify_type == 5)
					v_r_action = "report.download.excel2007";
				else if (notify_type == 6)
					v_r_action = "download.all";

				if (rs.getObject("processed_formfields") != null)
					v_formfields = modify_formfields(v_schedule_id, rs.getString("processed_formFields"));
				else
					v_formfields = strip_formfields(v_schedule_id, rs.getString("initial_formfields"));

				v_url = p_system_url + "&r_action=" + v_r_action + "&log_id=" + v_id + "&user_id=" + rs.getString("user_id") + "&pdfAttachmentKey=" + v_gen_key + "&download_limit=" + rs.getInt("max_row") + v_formfields;

				boolean v_attach_email_yn = shouldSendAttachmentInEmail(v_schedule_id);

				v_email_msg = "<html><body><p><b><u><i>" + p_system_name + " System Notification</i></u></b></p>" + "<p>Report <b>" + rs.getString("title") + "</b> is available for viewing.</p><p>You can view the report if it is attached. </br>"
						+ "If it is not attached, or you have problem to open it, you can log into Business Direct and run the report.</p>" + "</body></html>";

				if (rs.getInt("notify_type") != 6) {

					sendEmail.sendEmail(p_mail_server, p_sender, p_system_name + " System Notification: Report " + rs.getString("title") + " generated", v_email_msg, v_url, rs.getInt("notify_type"), v_schedule_id, p_time_interval, v_attach_email_yn,connectionTimeout);

				} else {

					// may not necessary
					schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + v_schedule_id + ",'" + v_url + "'," + "'Success: http request began.', " + Globals.getCurrentDateString() + " )");
					schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values  (" + v_schedule_id + ",'" + v_url + "'," + "'Success: http response recieved. Code  resp.status_code '' desc '' resp.reason_phrase', " + Globals.getCurrentDateString() + " )");

				}
				
				schedulerUtil.insertOrUpdate("update cr_report_schedule set run_date = " + Globals.getCurrentDateString() +" where schedule_id=" + v_schedule_id);
				
				schedulerUtil.insertOrUpdate("update cr_report_email_sent_log set sent_date= " + Globals.getCurrentDateString() +" , access_flag='N' where log_id=" + v_id);
				schedulerUtil.insertOrUpdate("insert into cr_schedule_activity_log (SCHEDULE_ID, url, notes, run_time) values (" + v_schedule_id + ",'" + v_url + "','Success: Email Sent', " + Globals.getCurrentDateString() + " )");

			}

		}

		   if(rs!=null) 
				rs.close();
		   if(stat!=null)
			stat.close();
		//conn.close();

	}

	private boolean shouldSendAttachmentInEmail(int v_schedule_id) throws SQLException, ReportSQLException {

		String l_boolean = (String) schedulerUtil.getSingleResult("SELECT ATTACHMENT_YN from cr_report_schedule where schedule_id  = " + v_schedule_id, "ATTACHMENT_YN");
		if ("Y".equals(l_boolean))
			return true;
		return false;
	}

	private String strip_formfields(int v_schedule_id, String p_formfields) throws SQLException, ReportSQLException {

		String v_formfields_insert = "";
		String v_formfields_generate = "";
		String v_name = "";
		String v_value = "";
		
		
		String[] column_values = schedulerUtil.cr_dissecturl(p_formfields, "&");
		
		for(String column_value : column_values){
			if(column_value == null || column_value.isEmpty())
				continue;
			
			v_name = column_value.substring(0, column_value.indexOf('='));
			v_formfields_insert += column_value + "&";
			v_value = column_value.substring(column_value.indexOf('=') + 1);
			if (column_value.indexOf("_auto") > 0) {
				v_formfields_generate = v_formfields_generate + v_name.substring(0, v_name.indexOf("_auto")) + "=" + v_value + "&";
			} else {
				v_formfields_generate = v_formfields_generate + column_value + "&";
			}
		}
		
		schedulerUtil.insertOrUpdate("update CR_REPORT_SCHEDULE set processed_formfields ='" + v_formfields_insert + "' where schedule_id = " + v_schedule_id);

		return v_formfields_generate.substring(0, v_formfields_generate.length());

	}

	private String modify_formfields(int v_schedule_id, String p_formfields) throws SQLException, ReportSQLException {

		class Result {

			String v_formfields_insert = "";
			String v_formfields_generate = "";
			String v_name = "";
			String v_value = "";
			Date v_date;
			String v_hour = "";
			String v_hour_value = "";
		}

		final Result result = new Result();

		final String v_recurrence = (String) schedulerUtil.getSingleResult("select recurrence from cr_report_schedule where schedule_id = " + v_schedule_id, "recurrence");

		String[] column_values = schedulerUtil.cr_dissecturl(p_formfields, "&");
		
		for(String column_value : column_values){
			if (column_value == null)
				column_value = "";

			if ("MONTHLY".equals(v_recurrence)) {
				if (column_value.indexOf("_auto") > 0) {
					result.v_name = column_value.substring(0, column_value.indexOf('='));
					result.v_date = schedulerUtil.to_date(column_value.substring(column_value.indexOf('=') + 1), "mm/dd/yyyy");
					result.v_value = schedulerUtil.add_months(result.v_date, 1).toString();

					if (result.v_name.length() > 0) {
						result.v_formfields_insert = result.v_formfields_insert + result.v_name + "=" + result.v_value + "&";
						result.v_formfields_generate = result.v_formfields_generate + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "=" + result.v_value + "&";
					}
				} else {
					result.v_formfields_insert = result.v_formfields_insert + column_value + "&";
					result.v_formfields_generate = result.v_formfields_generate + column_value + "&";
				}

			} else if ("DAILY".equals(v_recurrence)) {
				if (column_value.indexOf("_auto") > 0) {
					result.v_name = column_value.substring(0, column_value.indexOf('='));
					result.v_date = schedulerUtil.to_date(column_value.substring(column_value.indexOf('=') + 1), "mm/dd/yyyy");
					result.v_value = schedulerUtil.add_months(result.v_date, 1).toString();

					if (result.v_name.length() > 0) {
						result.v_formfields_insert = result.v_formfields_insert + result.v_name + "=" + result.v_value + "&";
						result.v_formfields_generate = result.v_formfields_generate + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "=" + result.v_value + "&";
					}
				} else {
					result.v_formfields_insert = result.v_formfields_insert + column_value + "&";
					result.v_formfields_generate = result.v_formfields_generate + column_value + "&";
				}

			} else if ("DAILY_MO_FR".equals(v_recurrence)) {
				if (column_value.indexOf("_auto") > 0) {
					result.v_name = column_value.substring(0, column_value.indexOf('='));
					result.v_date = schedulerUtil.to_date(column_value.substring(column_value.indexOf('=') + 1), "mm/dd/yyyy");
					SimpleDateFormat sdf = new SimpleDateFormat("EEE");
					sdf.format(result.v_date);
					if ("FRI".equals(result.v_date.toString())) {
						result.v_date = schedulerUtil.add_days(result.v_date, 3);
					} else if ("SAT".equals(result.v_date.toString())) {
						result.v_date = schedulerUtil.add_days(result.v_date, 2);
					} else {
						result.v_date = schedulerUtil.add_days(result.v_date, 1);
					}
					result.v_value = result.v_date.toString();
					if (result.v_name.length() > 0) {
						result.v_formfields_insert = result.v_formfields_insert + result.v_name + "=" + result.v_value + "&";
						result.v_formfields_generate = result.v_formfields_generate + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "=" + result.v_value + "&";
					}

				} else {
					result.v_formfields_insert = result.v_formfields_insert + column_value + "&";
					result.v_formfields_generate = result.v_formfields_generate + column_value + "&";
				}

			} else if ("HOURLY".equals(v_recurrence)) {
				
				result.v_name = column_value.indexOf('=')>0?column_value.substring(0, column_value.indexOf('=')) : "";
				if (column_value.indexOf("_auto") > 0) {
					
					
					String[] column_values2 = schedulerUtil.cr_dissecturl(p_formfields, "&");
					
					for(String column_value2 : column_values2){
						
						String key = column_value2.substring(0, column_value2.indexOf("="));
						if(key.equals(result.v_name.substring(0, result.v_name.indexOf("_auto")))  || key.equals(result.v_name.substring(0, result.v_name.indexOf("_Hr")))){
							result.v_hour = column_value2;
						}
					}
					
//					schedulerUtil.getAndExecute("select c.column_value from table(CR_DISSECTURL(p_formfields)) c where substr(c.column_value, 1, instr(c.column_value, '=')-1) = substr(" + result.v_name + ",1,instr(" + result.v_name + ",'_auto')-1)||'_Hr'", new Executor() {
//
//						@Override
//						public void execute(ResultSet rs) throws SQLException {
//
//							result.v_hour = rs.getString("column_value");
//						}
//
//					});

					if (result.v_hour.length() > 0) {
						result.v_hour_value = result.v_hour.substring(result.v_hour.indexOf('=') + 1);
					}

					result.v_date = schedulerUtil.to_date(column_value.substring(column_value.indexOf('=') + 1) + " " + result.v_hour_value, "mm/dd/yyyy HH24:MI:SS");

					result.v_value = schedulerUtil.to_date_str(schedulerUtil.add_hours(result.v_date, 1), "mm/dd/yyyy HH24");

					if (result.v_name.length() > 0) {
						if (result.v_hour.length() > 0) {

							result.v_formfields_insert = result.v_formfields_insert + result.v_name + "=" + result.v_value.substring(0, 10) + "&" + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "_Hr=" + result.v_value.substring(11, 13);
							result.v_formfields_generate = result.v_formfields_generate + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "=" + result.v_value.substring(0, 10) + "&" + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "_Hr="
									+ result.v_value.substring(11, 13) + "&";

						} else {
							result.v_formfields_insert = result.v_formfields_insert + result.v_name + "=" + result.v_value + "&";
							result.v_formfields_generate = result.v_formfields_generate + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "=" + result.v_value + "&";
						}
					}
				}
				if (column_value.indexOf("_Hr") <= 0) {
					result.v_formfields_insert = result.v_formfields_insert + column_value + "&";
					result.v_formfields_generate = result.v_formfields_generate + column_value + "&";
				}
			} else if ("WEEKLY".equals(v_recurrence)) {

				if (column_value.indexOf("_auto") > 0) {
					result.v_name = column_value.substring(0, column_value.indexOf('='));
					result.v_date = schedulerUtil.to_date(column_value.substring(column_value.indexOf('=') + 1), "mm/dd/yyyy");
					result.v_value = schedulerUtil.add_days(result.v_date, 7).toString();

					if (result.v_name.length() > 0) {
						result.v_formfields_insert = result.v_formfields_insert + result.v_name + "=" + result.v_value + "&";
						result.v_formfields_generate = result.v_formfields_generate + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "=" + result.v_value + "&";
					}

				} else {
					result.v_formfields_insert = result.v_formfields_insert + column_value + "&";
					result.v_formfields_generate = result.v_formfields_generate + column_value + "&";
				}

			} else {
				if (column_value.indexOf("_auto") > 0) {
					result.v_name = column_value.substring(0, column_value.indexOf('='));
					result.v_date = schedulerUtil.to_date(column_value.substring(column_value.indexOf('=') + 1), "mm/dd/yyyy");
					result.v_value = schedulerUtil.add_days(result.v_date, 7).toString();
					if (result.v_name.length() > 0) {
						result.v_formfields_insert = result.v_formfields_insert + result.v_name + "=" + result.v_value + "&";
						result.v_formfields_generate = result.v_formfields_generate + result.v_name.substring(0, result.v_name.indexOf("_auto")) + "=" + result.v_value + "&";
					}

				} else {
					result.v_formfields_insert = result.v_formfields_insert + column_value + "&";
					result.v_formfields_generate = result.v_formfields_generate + column_value + "&";
				}
			}
		}

		schedulerUtil.insertOrUpdate("update CR_REPORT_SCHEDULE set processed_formfields ='" + result.v_formfields_insert + "' where schedule_id =" + v_schedule_id);
		return "&" + result.v_formfields_generate.substring(0, result.v_formfields_generate.length());
	}

	private int get_report_sched_offset(int p_rep_id, int p_schedule_id) throws SQLException, ReportSQLException {

		class CrReportSchedule {

			Date run_date;
			String recurrence;
		}
		Date v_last_date = null;
		Date v_sysdate = new Date();

		final CrReportSchedule v_report_schedule_rec = new CrReportSchedule();

		schedulerUtil.getAndExecute("SELECT * FROM cr_report_schedule WHERE rep_id = " + p_rep_id + " and schedule_id = " + p_schedule_id, new Executor() {

			@Override
			public void execute(ResultSet rs) throws SQLException {

				v_report_schedule_rec.recurrence = rs.getString("recurrence");
				java.sql.Timestamp runDate = rs.getTimestamp("run_date");
				v_report_schedule_rec.run_date = new Date(runDate.getTime()) ;
			}

		});

		if (v_report_schedule_rec.run_date == null || v_report_schedule_rec.run_date.compareTo(v_sysdate) > 0) {
			return Integer.MAX_VALUE;
		}

		Date v_next_date = v_report_schedule_rec.run_date;

		while (v_next_date.compareTo(v_sysdate) < 0) {
			
			if ("HOURLY".equals(v_report_schedule_rec.recurrence)) {
				v_next_date = SchedulerUtil.add_hours(v_next_date, 1);
			} else if ("DAILY".equals(v_report_schedule_rec.recurrence)) {
				v_next_date = SchedulerUtil.add_days(v_next_date, 1);
			} else if ("DAILY_MO_FR".equals(v_report_schedule_rec.recurrence)) {
				SimpleDateFormat sdf = new SimpleDateFormat("EEE");
				sdf.format(v_next_date);
				if ("FRI".equals(v_next_date.toString())) {
					v_next_date = SchedulerUtil.add_days(v_next_date, 3);
				} else if ("SAT".equals(v_next_date.toString())) {
					v_next_date = SchedulerUtil.add_days(v_next_date, 2);
				} else {
					v_next_date = SchedulerUtil.add_days(v_next_date, 1);
				}
			} else if ("WEEKLY".equals(v_report_schedule_rec.recurrence)) {
				v_next_date = SchedulerUtil.add_days(v_next_date, 7);
			} else if ("MONTHLY".equals(v_report_schedule_rec.recurrence)) {
				v_next_date = SchedulerUtil.add_months(v_next_date, 1);
			} else {
				break;
			}
			v_last_date = v_next_date;
		}

		if (SchedulerUtil.trunc_hour(v_last_date).compareTo(SchedulerUtil.trunc_hour(v_sysdate)) == 0) {
			return (int)(Math.abs (v_sysdate.getTime() - v_last_date.getTime()) / 1000);
		} else {
			// More than an hour
			return 3601;
		}
	}

	public SchedulerUtil getSchedulerUtil() {
		return schedulerUtil;
	}

	public void setSchedulerUtil(SchedulerUtil schedulerUtil) {
		this.schedulerUtil = schedulerUtil;
	}

}
