package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the semester_classgroup_applicable_splztn_cum_activity database table.
 * 
 */
@Embeddable
public class SemesterClassgroupApplicableSplztnCumActivityPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="semstr_details_semester_sub_id", insertable=false, updatable=false)
	private String semstrDetailsSemesterSubId;

	@Column(name="clssgrp_master_class_group_id", insertable=false, updatable=false)
	private String clssgrpMasterClassGroupId;

	@Column(name="programme_specialization_id", insertable=false, updatable=false)
	private Integer programmeSpecializationId;

	@Column(name="admission_year")
	private Integer admissionYear;

	public SemesterClassgroupApplicableSplztnCumActivityPK() {
	}
	public String getSemstrDetailsSemesterSubId() {
		return this.semstrDetailsSemesterSubId;
	}
	public void setSemstrDetailsSemesterSubId(String semstrDetailsSemesterSubId) {
		this.semstrDetailsSemesterSubId = semstrDetailsSemesterSubId;
	}
	public String getClssgrpMasterClassGroupId() {
		return this.clssgrpMasterClassGroupId;
	}
	public void setClssgrpMasterClassGroupId(String clssgrpMasterClassGroupId) {
		this.clssgrpMasterClassGroupId = clssgrpMasterClassGroupId;
	}
	public Integer getProgrammeSpecializationId() {
		return this.programmeSpecializationId;
	}
	public void setProgrammeSpecializationId(Integer programmeSpecializationId) {
		this.programmeSpecializationId = programmeSpecializationId;
	}
	public Integer getAdmissionYear() {
		return this.admissionYear;
	}
	public void setAdmissionYear(Integer admissionYear) {
		this.admissionYear = admissionYear;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SemesterClassgroupApplicableSplztnCumActivityPK)) {
			return false;
		}
		SemesterClassgroupApplicableSplztnCumActivityPK castOther = (SemesterClassgroupApplicableSplztnCumActivityPK)other;
		return 
			this.semstrDetailsSemesterSubId.equals(castOther.semstrDetailsSemesterSubId)
			&& this.clssgrpMasterClassGroupId.equals(castOther.clssgrpMasterClassGroupId)
			&& this.programmeSpecializationId.equals(castOther.programmeSpecializationId)
			&& this.admissionYear.equals(castOther.admissionYear);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.semstrDetailsSemesterSubId.hashCode();
		hash = hash * prime + this.clssgrpMasterClassGroupId.hashCode();
		hash = hash * prime + this.programmeSpecializationId.hashCode();
		hash = hash * prime + this.admissionYear.hashCode();
		
		return hash;
	}
}