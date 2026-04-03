package org.vtop.CourseRegistration.Dto;

public class ReseachStudentCourseDetailDto {

	
	private String registerNumber;
	private String courseId;
	private String courseCode;
	private String courseTitle;
	private float courseVersion;
	private String genericCourseType;
	private int meetingStatus;
	
	public String getRegisterNumber() {
		return registerNumber;
	}
	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}
	public String getCourseId() {
		return courseId;
	}
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	public String getCourseCode() {
		return courseCode;
	}
	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
	public String getCourseTitle() {
		return courseTitle;
	}
	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}
	public float getCourseVersion() {
		return courseVersion;
	}
	public void setCourseVersion(float courseVersion) {
		this.courseVersion = courseVersion;
	}
	public String getGenericCourseType() {
		return genericCourseType;
	}
	public void setGenericCourseType(String genericCourseType) {
		this.genericCourseType = genericCourseType;
	}
	public int getMeetingStatus() {
		return meetingStatus;
	}
	public void setMeetingStatus(int meetingStatus) {
		this.meetingStatus = meetingStatus;
	}
}
