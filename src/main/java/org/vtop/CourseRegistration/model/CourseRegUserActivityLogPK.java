package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

/**
 * The primary key class for the "course_reg_user_activity_logs" database table.
 * 
 */
@Embeddable
public class CourseRegUserActivityLogPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="\"user_id\"")
	private String userId;

	@Column(name="\"activity\"")
	private String activity;

	@Column(name="\"request_url\"")
	private String requestUrl;
	

	@Column(name="\"request_timestamp\"")
	private LocalDateTime requestTimestamp;

	public CourseRegUserActivityLogPK() {
	}
	
	public LocalDateTime getRequestTimestamp() {
		return this.requestTimestamp;
	}

	public void setRequestTimestamp(LocalDateTime requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}
	
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getActivity() {
		return this.activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getRequestUrl() {
		return this.requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CourseRegUserActivityLogPK)) {
			return false;
		}
		CourseRegUserActivityLogPK castOther = (CourseRegUserActivityLogPK)other;
		return 
			this.userId.equals(castOther.userId)
			&& this.activity.equals(castOther.activity)
			&& this.requestUrl.equals(castOther.requestUrl);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.activity.hashCode();
		hash = hash * prime + this.requestUrl.hashCode();
		
		return hash;
	}
}