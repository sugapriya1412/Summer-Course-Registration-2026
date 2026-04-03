package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StudentInformation implements Serializable
{
    private static final long serialVersionUID = 123456799L;


	private String semesterSubId;
	private String semesterDesc;
	private String classGroupId;
	private String classGroupDesc;
	private String registerNumber;
    private long applicationNumber;
    private String studentName;
    private String gender;
    private int progSpecializationId;
    private String progSpecializationCode;
    private String progSpecializationDescription;
    private int progGroupId;
    private String progGroupCode;
    private String progGroupDescription;
    private String progGroupMode;
    private int progGroupDuration;
    private String progGroupLevel;
    private int centreId;
    private String centreCode;
    private String centreDescription;
    private int admissionYear;
    private String studySystem;   
    private String educationStatus;
    private String educationStatusDescription;
    private int lockStatus;
    private String email;
    private String mobile;    
    private int examGraduationStatus;    
    private int eptMark;  
    private String hscGroup;
    private Integer wishlistStatus;    
    private String optedCBCSMinor;
    private String optedCBCSHonour;
    private float totalCreditsRegistered;
    private float totalCreditsEarned;
    private float cumulativeGradePointAverage;
	private LocalDateTime startTimestamp;
	private LocalDateTime endTimestamp ;
	private String feeCategoryDescription;
	private int semesterId;
	private String message;
	private boolean isCurrentSchedule;
	
	
	
	
	public boolean isCurrentSchedule() {
		return isCurrentSchedule;
	}
	public void setCurrentSchedule(boolean isCurrentSchedule) {
		this.isCurrentSchedule = isCurrentSchedule;
	}
	public int getSemesterId() {
		return semesterId;
	}
	public void setSemesterId(int semesterId) {
		this.semesterId = semesterId;
	}
	public String getFeeCategoryDescription() {
		return feeCategoryDescription;
	}
	public void setFeeCategoryDescription(String feeCategoryDescription) {
		this.feeCategoryDescription = feeCategoryDescription;
	}
	public String getSemesterSubId() {
		return semesterSubId;
	}
	public void setSemesterSubId(String semesterSubId) {
		this.semesterSubId = semesterSubId;
	}
	public String getSemesterDesc() {
		return semesterDesc;
	}
	public void setSemesterDesc(String semesterDesc) {
		this.semesterDesc = semesterDesc;
	}
	public String getClassGroupId() {
		return classGroupId;
	}
	public void setClassGroupId(String classGroupId) {
		this.classGroupId = classGroupId;
	}
	public String getClassGroupDesc() {
		return classGroupDesc;
	}
	public void setClassGroupDesc(String classGroupDesc) {
		this.classGroupDesc = classGroupDesc;
	}
	public String getRegisterNumber() {
		return registerNumber;
	}
	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}
	public long getApplicationNumber() {
		return applicationNumber;
	}
	public void setApplicationNumber(long applicationNumber) {
		this.applicationNumber = applicationNumber;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getProgSpecializationId() {
		return progSpecializationId;
	}
	public void setProgSpecializationId(int progSpecializationId) {
		this.progSpecializationId = progSpecializationId;
	}
	public String getProgSpecializationCode() {
		return progSpecializationCode;
	}
	public void setProgSpecializationCode(String progSpecializationCode) {
		this.progSpecializationCode = progSpecializationCode;
	}
	public String getProgSpecializationDescription() {
		return progSpecializationDescription;
	}
	public void setProgSpecializationDescription(String progSpecializationDescription) {
		this.progSpecializationDescription = progSpecializationDescription;
	}
	public int getProgGroupId() {
		return progGroupId;
	}
	public void setProgGroupId(int progGroupId) {
		this.progGroupId = progGroupId;
	}
	public String getProgGroupCode() {
		return progGroupCode;
	}
	public void setProgGroupCode(String progGroupCode) {
		this.progGroupCode = progGroupCode;
	}
	public String getProgGroupDescription() {
		return progGroupDescription;
	}
	public void setProgGroupDescription(String progGroupDescription) {
		this.progGroupDescription = progGroupDescription;
	}
	public String getProgGroupMode() {
		return progGroupMode;
	}
	public void setProgGroupMode(String progGroupMode) {
		this.progGroupMode = progGroupMode;
	}
	public int getProgGroupDuration() {
		return progGroupDuration;
	}
	public void setProgGroupDuration(int progGroupDuration) {
		this.progGroupDuration = progGroupDuration;
	}
	public String getProgGroupLevel() {
		return progGroupLevel;
	}
	public void setProgGroupLevel(String progGroupLevel) {
		this.progGroupLevel = progGroupLevel;
	}
	public int getCentreId() {
		return centreId;
	}
	public void setCentreId(int centreId) {
		this.centreId = centreId;
	}
	public String getCentreCode() {
		return centreCode;
	}
	public void setCentreCode(String centreCode) {
		this.centreCode = centreCode;
	}
	public String getCentreDescription() {
		return centreDescription;
	}
	public void setCentreDescription(String centreDescription) {
		this.centreDescription = centreDescription;
	}
	public int getAdmissionYear() {
		return admissionYear;
	}
	public void setAdmissionYear(int admissionYear) {
		this.admissionYear = admissionYear;
	}
	public String getStudySystem() {
		return studySystem;
	}
	public void setStudySystem(String studySystem) {
		this.studySystem = studySystem;
	}
	public String getEducationStatus() {
		return educationStatus;
	}
	public void setEducationStatus(String educationStatus) {
		this.educationStatus = educationStatus;
	}
	public String getEducationStatusDescription() {
		return educationStatusDescription;
	}
	public void setEducationStatusDescription(String educationStatusDescription) {
		this.educationStatusDescription = educationStatusDescription;
	}
	public int getLockStatus() {
		return lockStatus;
	}
	public void setLockStatus(int lockStatus) {
		this.lockStatus = lockStatus;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getExamGraduationStatus() {
		return examGraduationStatus;
	}
	public void setExamGraduationStatus(int examGraduationStatus) {
		this.examGraduationStatus = examGraduationStatus;
	}
	public int getEptMark() {
		return eptMark;
	}
	public void setEptMark(int eptMark) {
		this.eptMark = eptMark;
	}
	public String getHscGroup() {
		return hscGroup;
	}
	public void setHscGroup(String hscGroup) {
		this.hscGroup = hscGroup;
	}
	public Integer getWishlistStatus() {
		return wishlistStatus;
	}
	public void setWishlistStatus(Integer wishlistStatus) {
		this.wishlistStatus = wishlistStatus;
	}	
	public String getOptedCBCSMinor() {
		return optedCBCSMinor;
	}
	public void setOptedCBCSMinor(String optedCBCSMinor) {
		this.optedCBCSMinor = optedCBCSMinor;
	}
	
	public float getTotalCreditsRegistered() {
		return totalCreditsRegistered;
	}
	public void setTotalCreditsRegistered(float totalCreditsRegistered) {
		this.totalCreditsRegistered = totalCreditsRegistered;
	}
	public float getTotalCreditsEarned() {
		return totalCreditsEarned;
	}
	public void setTotalCreditsEarned(float totalCreditsEarned) {
		this.totalCreditsEarned = totalCreditsEarned;
	}
	public String getOptedCBCSHonour() {
		return optedCBCSHonour;
	}
	public void setOptedCBCSHonour(String optedCBCSHonour) {
		this.optedCBCSHonour = optedCBCSHonour;
	}
	public float getCumulativeGradePointAverage() {
		return cumulativeGradePointAverage;
	}
	public void setCumulativeGradePointAverage(float cumulativeGradePointAverage) {
		this.cumulativeGradePointAverage = cumulativeGradePointAverage;
	}
	public LocalDateTime getStartTimestamp() {
		return startTimestamp;
	}
	public void setStartTimestamp(LocalDateTime startTimestamp) {
		this.startTimestamp = startTimestamp;
	}
	public LocalDateTime getEndTimestamp() {
		return endTimestamp;
	}
	public void setEndTimestamp(LocalDateTime endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}	

     
}
