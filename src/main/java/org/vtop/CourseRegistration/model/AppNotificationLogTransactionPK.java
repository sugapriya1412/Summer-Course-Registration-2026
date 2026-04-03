package org.vtop.CourseRegistration.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AppNotificationLogTransactionPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "notification_id")
	private String notificationId;

	@Column(name = "notification_type")
	private String notificationType;

	@Column(name = "receiver_id")
	private String receiverId;

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

}
