package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;


/**
 * The persistent class for the "course_reg_user_activity_logs" database table.
 * 
 */
@Entity
@Table(name="\"course_reg_user_activity_logs\"", schema="ACADEMICS")
@NamedQuery(name="CourseRegUserActivityLog.findAll", query="SELECT c FROM CourseRegUserActivityLog c")
public class CourseRegUserActivityLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CourseRegUserActivityLogPK id;

	@Column(name="\"client_ip_address\"")
	private String clientIpAddress;


	@Column(name="\"response_code\"")
	private String responseCode;

	@Column(name="\"response_timestamp\"")
	private LocalDateTime responseTimestamp;

	public CourseRegUserActivityLog() {
	}

	public CourseRegUserActivityLogPK getId() {
		return this.id;
	}

	public void setId(CourseRegUserActivityLogPK id) {
		this.id = id;
	}

	public String getClientIpAddress() {
		return this.clientIpAddress;
	}

	public void setClientIpAddress(String clientIpAddress) {
		this.clientIpAddress = clientIpAddress;
	}

	public String getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public LocalDateTime getResponseTimestamp() {
		return this.responseTimestamp;
	}

	public void setResponseTimestamp(LocalDateTime responseTimestamp) {
		this.responseTimestamp = responseTimestamp;
	}

}