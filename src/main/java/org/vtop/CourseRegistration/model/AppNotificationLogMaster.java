package org.vtop.CourseRegistration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "app_notification_log_master", schema = "mobileapp")
public class AppNotificationLogMaster {

	@Id
	@Column(name = "notification_id")
	private String notificationId;

	@Column(name = "notification_category")
	private String notificationCategory;

	@Column(name = "notification_type")
	private String notificationType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "process_start_time")
	private Date processStartTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "process_end_time")
	private Date processEndTime;

	@Column(name = "total_count")
	private Integer totalCount;

	@Column(name = "mail_success_count")
	private Integer mailSuccessCount;

	@Column(name = "mail_failure_count")
	private Integer mailFailureCount;

	@Column(name = "mail_failure_log")
	private String mailFailureLog;

	@Column(name = "app_success_count")
	private Integer appSuccessCount;

	@Column(name = "app_failure_count")
	private Integer appFailureCount;

	@Column(name = "app_failure_log")
	private String appFailureLog;

	@Column(name = "app_message")
	private String appMessage;

	@Column(name = "mail_message")
	private String mailMessage;

	@Column(name = "log_userid")
	private String logUserId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_timestamp")
	private Date logTimeStamp;

	@Column(name = "log_ipaddress")
	private String logIpAddress;

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getNotificationCategory() {
		return notificationCategory;
	}

	public void setNotificationCategory(String notificationCategory) {
		this.notificationCategory = notificationCategory;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public Date getProcessStartTime() {
		return processStartTime;
	}

	public void setProcessStartTime(Date processStartTime) {
		this.processStartTime = processStartTime;
	}

	public Date getProcessEndTime() {
		return processEndTime;
	}

	public void setProcessEndTime(Date processEndTime) {
		this.processEndTime = processEndTime;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getMailSuccessCount() {
		return mailSuccessCount;
	}

	public void setMailSuccessCount(Integer mailSuccessCount) {
		this.mailSuccessCount = mailSuccessCount;
	}

	public Integer getMailFailureCount() {
		return mailFailureCount;
	}

	public void setMailFailureCount(Integer mailFailureCount) {
		this.mailFailureCount = mailFailureCount;
	}

	public String getMailFailureLog() {
		return mailFailureLog;
	}

	public void setMailFailureLog(String mailFailureLog) {
		this.mailFailureLog = mailFailureLog;
	}

	public Integer getAppSuccessCount() {
		return appSuccessCount;
	}

	public void setAppSuccessCount(Integer appSuccessCount) {
		this.appSuccessCount = appSuccessCount;
	}

	public Integer getAppFailureCount() {
		return appFailureCount;
	}

	public void setAppFailureCount(Integer appFailureCount) {
		this.appFailureCount = appFailureCount;
	}

	public String getAppFailureLog() {
		return appFailureLog;
	}

	public void setAppFailureLog(String appFailureLog) {
		this.appFailureLog = appFailureLog;
	}

	public String getAppMessage() {
		return appMessage;
	}

	public void setAppMessage(String appMessage) {
		this.appMessage = appMessage;
	}

	public String getMailMessage() {
		return mailMessage;
	}

	public void setMailMessage(String mailMessage) {
		this.mailMessage = mailMessage;
	}

	public String getLogUserId() {
		return logUserId;
	}

	public void setLogUserId(String logUserId) {
		this.logUserId = logUserId;
	}

	public Date getLogTimeStamp() {
		return logTimeStamp;
	}

	public void setLogTimeStamp(Date logTimeStamp) {
		this.logTimeStamp = logTimeStamp;
	}

	public String getLogIpAddress() {
		return logIpAddress;
	}

	public void setLogIpAddress(String logIpAddress) {
		this.logIpAddress = logIpAddress;
	}

}
