package org.vtop.CourseRegistration;

import java.io.Serializable;

public class GlobalMaster implements Serializable {
    private static final long serialVersionUID = 1234567L;

	String semesterSubId;
	String classGroupId;
	int activityId;
	float maxCredits;
	float minCredits;
	float finalYrMaxCredits;
	int lowCgpa;
	float lowCgpaMaxCredits;
	int regApprovalStatus;
	int regularCourseStatus;
	int nGradeCourseStatus;
	int gradeImpCourseStatus;
	int auditCourseStatus;
	int minorHonorCourseStatus;
	int addlCourseStatus;
	int peAddlCourseStatus;
	int ueAddlCourseStatus;
	int deOeAllowStatus;
	int speOeAllowStatus;
	int n2N4AllowStatus;
	int compCourseStatus;
	
	
	public String getSemesterSubId() {
		return semesterSubId;
	}
	public void setSemesterSubId(String semesterSubId) {
		this.semesterSubId = semesterSubId;
	}
	public String getClassGroupId() {
		return classGroupId;
	}
	public void setClassGroupId(String classGroupId) {
		this.classGroupId = classGroupId;
	}
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public float getMaxCredits() {
		return maxCredits;
	}
	public void setMaxCredits(int maxCredits) {
		this.maxCredits = maxCredits;
	}
	public float getMinCredits() {
		return minCredits;
	}
	public void setMinCredits(int minCredits) {
		this.minCredits = minCredits;
	}
	public float getFinalYrMaxCredits() {
		return finalYrMaxCredits;
	}
	public void setFinalYrMaxCredits(int finalYrMaxCredits) {
		this.finalYrMaxCredits = finalYrMaxCredits;
	}
	public int getLowCgpa() {
		return lowCgpa;
	}
	public void setLowCgpa(int lowCgpa) {
		this.lowCgpa = lowCgpa;
	}
	public float getLowCgpaMaxCredits() {
		return lowCgpaMaxCredits;
	}
	public void setLowCgpaMaxCredits(int lowCgpaMaxCredits) {
		this.lowCgpaMaxCredits = lowCgpaMaxCredits;
	}
	public int getRegApprovalStatus() {
		return regApprovalStatus;
	}
	public void setRegApprovalStatus(int regApprovalStatus) {
		this.regApprovalStatus = regApprovalStatus;
	}
	public int getRegularCourseStatus() {
		return regularCourseStatus;
	}
	public void setRegularCourseStatus(int regularCourseStatus) {
		this.regularCourseStatus = regularCourseStatus;
	}
	public int getnGradeCourseStatus() {
		return nGradeCourseStatus;
	}
	public void setnGradeCourseStatus(int nGradeCourseStatus) {
		this.nGradeCourseStatus = nGradeCourseStatus;
	}
	public int getGradeImpCourseStatus() {
		return gradeImpCourseStatus;
	}
	public void setGradeImpCourseStatus(int gradeImpCourseStatus) {
		this.gradeImpCourseStatus = gradeImpCourseStatus;
	}
	public int getAuditCourseStatus() {
		return auditCourseStatus;
	}
	public void setAuditCourseStatus(int auditCourseStatus) {
		this.auditCourseStatus = auditCourseStatus;
	}
	public int getMinorHonorCourseStatus() {
		return minorHonorCourseStatus;
	}
	public void setMinorHonorCourseStatus(int minorHonorCourseStatus) {
		this.minorHonorCourseStatus = minorHonorCourseStatus;
	}
	public int getAddlCourseStatus() {
		return addlCourseStatus;
	}
	public void setAddlCourseStatus(int addlCourseStatus) {
		this.addlCourseStatus = addlCourseStatus;
	}
	public int getPeAddlCourseStatus() {
		return peAddlCourseStatus;
	}
	public void setPeAddlCourseStatus(int peAddlCourseStatus) {
		this.peAddlCourseStatus = peAddlCourseStatus;
	}
	public int getUeAddlCourseStatus() {
		return ueAddlCourseStatus;
	}
	public void setUeAddlCourseStatus(int ueAddlCourseStatus) {
		this.ueAddlCourseStatus = ueAddlCourseStatus;
	}
	public int getDeOeAllowStatus() {
		return deOeAllowStatus;
	}
	public void setDeOeAllowStatus(int deOeAllowStatus) {
		this.deOeAllowStatus = deOeAllowStatus;
	}
	public int getSpeOeAllowStatus() {
		return speOeAllowStatus;
	}
	public void setSpeOeAllowStatus(int speOeAllowStatus) {
		this.speOeAllowStatus = speOeAllowStatus;
	}
	public int getN2N4AllowStatus() {
		return n2N4AllowStatus;
	}
	public void setN2N4AllowStatus(int n2n4AllowStatus) {
		n2N4AllowStatus = n2n4AllowStatus;
	}
	public int getCompCourseStatus() {
		return compCourseStatus;
	}
	public void setCompCourseStatus(int compCourseStatus) {
		this.compCourseStatus = compCourseStatus;
	}

	

}
