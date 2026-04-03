package org.vtop.CourseRegistration.Dto;

import java.io.Serializable;

public class ProgramSpecializationCurriculumDetailDto implements Serializable {
	private static final long serialVersionUID = 1L;

	
    private String id;
    
	private int	progSpecializationId;
	private int	admissionYear;
	private float curriculumVersion;
	private String courseCategory;
	private String catalogType;
	private String basketCategory;
	private int	basketCredit;
	private String courseId;
	private String courseCode;
	private String courseTitle;
	
	public ProgramSpecializationCurriculumDetailDto() {
    }    

	public ProgramSpecializationCurriculumDetailDto(int progSpecializationId, int admissionYear,
			float curriculumVersion, String courseCategory, String catalogType, String basketCategory, int basketCredit,
			String courseId, String courseCode, String courseTitle) {
		super();
		this.progSpecializationId = progSpecializationId;
		this.admissionYear = admissionYear;
		this.curriculumVersion = curriculumVersion;
		this.courseCategory = courseCategory;
		this.catalogType = catalogType;
		this.basketCategory = basketCategory;
		this.basketCredit = basketCredit;
		this.courseId = courseId;
		this.courseCode = courseCode;
		this.courseTitle = courseTitle;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getProgSpecializationId() {
		return progSpecializationId;
	}

	public void setProgSpecializationId(int progSpecializationId) {
		this.progSpecializationId = progSpecializationId;
	}

	public int getAdmissionYear() {
		return admissionYear;
	}

	public void setAdmissionYear(int admissionYear) {
		this.admissionYear = admissionYear;
	}

	public float getCurriculumVersion() {
		return curriculumVersion;
	}

	public void setCurriculumVersion(float curriculumVersion) {
		this.curriculumVersion = curriculumVersion;
	}

	public String getCourseCategory() {
		return courseCategory;
	}

	public void setCourseCategory(String courseCategory) {
		this.courseCategory = courseCategory;
	}

	public String getCatalogType() {
		return catalogType;
	}

	public void setCatalogType(String catalogType) {
		this.catalogType = catalogType;
	}

	public String getBasketCategory() {
		return basketCategory;
	}

	public void setBasketCategory(String basketCategory) {
		this.basketCategory = basketCategory;
	}

	public int getBasketCredit() {
		return basketCredit;
	}

	public void setBasketCredit(int basketCredit) {
		this.basketCredit = basketCredit;
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
	
}
