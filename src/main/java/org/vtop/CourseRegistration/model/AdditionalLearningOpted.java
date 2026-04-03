package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;


/**
 * The persistent class for the additional_learning_opted database table.
 * 
 */
@Entity
@Table(name="additional_learning_opted",  schema="ACADEMICS")
public class AdditionalLearningOpted implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AdditionalLearningOptedPK id;

	@Column(name="learning_type")
	private String learningType;

	@Column(name="log_ipaddress")
	private String logIpaddress;

	@Column(name="log_timestamp")
	private LocalDateTime logTimestamp;

	@Column(name="log_userid")
	private String logUserid;

	@Column(name="semstr_details_semester_sub_id")
	private String semstrDetailsSemesterSubId;

    public AdditionalLearningOpted() {
    }

	public AdditionalLearningOptedPK getId() {
		return this.id;
	}

	public void setId(AdditionalLearningOptedPK id) {
		this.id = id;
	}
	
	public String getLearningType() {
		return this.learningType;
	}

	public void setLearningType(String learningType) {
		this.learningType = learningType;
	}

	public String getLogIpaddress() {
		return this.logIpaddress;
	}

	public void setLogIpaddress(String logIpaddress) {
		this.logIpaddress = logIpaddress;
	}

	public LocalDateTime getLogTimestamp() {
		return this.logTimestamp;
	}

	public void setLogTimestamp(LocalDateTime logTimestamp) {
		this.logTimestamp = logTimestamp;
	}

	public String getLogUserid() {
		return this.logUserid;
	}

	public void setLogUserid(String logUserid) {
		this.logUserid = logUserid;
	}

	public String getSemstrDetailsSemesterSubId() {
		return this.semstrDetailsSemesterSubId;
	}

	public void setSemstrDetailsSemesterSubId(String semstrDetailsSemesterSubId) {
		this.semstrDetailsSemesterSubId = semstrDetailsSemesterSubId;
	}

}