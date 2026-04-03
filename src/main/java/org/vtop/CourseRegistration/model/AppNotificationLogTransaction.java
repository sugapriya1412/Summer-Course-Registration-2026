package org.vtop.CourseRegistration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "app_notification_log_transaction", schema = "mobileapp")
public class AppNotificationLogTransaction {
	
	@EmbeddedId
	private AppNotificationLogTransactionPK appNotificationLogTransactionPK;

	@ManyToOne(optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "notification_id", referencedColumnName = "notification_id", insertable = false, updatable = false)
	private AppNotificationLogMaster appNotificationLogMaster;
	

	@Column(name = "response_message")
	private String responseMessage;

	@Column(name = "delivery_status")
	private String deliveryStatus;

	@Column(name = "log_userid")
	private String logUserid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "log_timestamp")
	private Date logTimestamp;

	@Column(name = "log_ipaddress")
	private String logIpaddress;

	public AppNotificationLogTransactionPK getAppNotificationLogTransactionPK() {
		return appNotificationLogTransactionPK;
	}

	public void setAppNotificationLogTransactionPK(AppNotificationLogTransactionPK appNotificationLogTransactionPK) {
		this.appNotificationLogTransactionPK = appNotificationLogTransactionPK;
	}

	public AppNotificationLogMaster getAppNotificationLogMaster() {
		return appNotificationLogMaster;
	}

	public void setAppNotificationLogMaster(AppNotificationLogMaster appNotificationLogMaster) {
		this.appNotificationLogMaster = appNotificationLogMaster;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}
	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public String getLogUserid() {
		return logUserid;
	}

	public void setLogUserid(String logUserid) {
		this.logUserid = logUserid;
	}

	public Date getLogTimestamp() {
		return logTimestamp;
	}

	public void setLogTimestamp(Date logTimestamp) {
		this.logTimestamp = logTimestamp;
	}

	public String getLogIpaddress() {
		return logIpaddress;
	}

	public void setLogIpaddress(String logIpaddress) {
		this.logIpaddress = logIpaddress;
	}

}
