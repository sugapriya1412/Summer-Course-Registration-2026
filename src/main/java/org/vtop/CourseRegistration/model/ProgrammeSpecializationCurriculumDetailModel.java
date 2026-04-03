package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;

@SqlResultSetMapping(name = "ProgrammeSpecializationCurriculumInfo", classes = {
		@ConstructorResult(targetClass = org.vtop.CourseRegistration.Dto.ProgramSpecializationCurriculumDetailDto.class, columns = {
				@ColumnResult(name = "progSpecializationId", type=Integer.class),
				@ColumnResult(name = "admissionYear", type=Integer.class),
				@ColumnResult(name = "curriculumVersion", type=Float.class),
				@ColumnResult(name = "courseCategory", type=String.class),
				@ColumnResult(name = "catalogType",type=String.class),
				@ColumnResult(name = "basketCategory",type=String.class),
				@ColumnResult(name = "basketCredit",type=Integer.class),
				@ColumnResult(name = "courseId", type=String.class),
				@ColumnResult(name = "courseCode", type=String.class),
				@ColumnResult(name = "courseTitle", type=String.class),
		})
})

 @NamedNativeQuery(name = "ProgrammeSpecializationCurriculumDetailModel.findBySpecIdAdmissionYearAndCourseCode",
		query = "select a.prgsplzn_prg_specialization_id as progSpecializationId, a.admission_year as admissionYear, "
				+ "a.curriculum_version as curriculumVersion, a.course_category as courseCategory, a.catalog_type as catalogType, "
				+ "a.basket_category as basketCategory, a.basket_credit as basketCredit, a.course_id as courseId, b.code as courseCode, "
				+ "b.title as courseTitle from ("
				+ "select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, a.basket_category, a.basket_credit, a.course_id from ("
				+ "(select prgsplzn_prg_specialization_id, admission_year, curriculum_version, course_category, "
				+ "catalog_type, course_basket_id, 'NONE' as basket_category, 0 as basket_credit, course_basket_id "
				+ "as course_id from academics.prg_splztn_curriculum_details where prgsplzn_prg_specialization_id =?1 and admission_year=?2 "
				+ "and catalog_type='CC' and lock_status=0 ) "
				+ "union all "
				+ "(select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, b.basket_category, b.credits as basket_credit, c.course_catalog_course_id "
				+ "as course_id from "
				+ "(select * from academics.prg_splztn_curriculum_details where "
				+ "prgsplzn_prg_specialization_id =?1 and admission_year=?2 and catalog_type='BC' and lock_status=0 ) a, academics.basket_details b, "
				+ "academics.basket_course_catalog c where a.course_basket_id=b.basket_id "
				+ "and a.course_basket_id=c.basket_details_basket_id and b.basket_id=c.basket_details_basket_id)) a "
				+ "where (a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version) in "
				+ "(select prgsplzn_prg_specialization_id, admission_year, max(curriculum_version) from "
				+ "academics.prg_splztn_curriculum_credits where lock_status=0 group by prgsplzn_prg_specialization_id, "
				+ "admission_year)) a, academics.course_catalog b where b.code=?3 and "
				+ "a.course_id=b.course_id "
				+ "order by a.course_id DESC LIMIT 1 ", resultSetMapping = "ProgrammeSpecializationCurriculumInfo")

@NamedNativeQuery(name = "ProgrammeSpecializationCurriculumDetailModel.findBySpecIdAndAdmissionYear",
		query = "select a.prgsplzn_prg_specialization_id as progSpecializationId, a.admission_year as admissionYear, "
				+ "a.curriculum_version as curriculumVersion, a.course_category as courseCategory, a.catalog_type as catalogType, "
				+ "a.basket_category as basketCategory, a.basket_credit as basketCredit, a.course_id as courseId, b.code as courseCode, "
				+ "b.title as courseTitle from ("
				+ "select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, a.basket_category, a.basket_credit, a.course_id from ("
				+ "(select prgsplzn_prg_specialization_id, admission_year, curriculum_version, course_category, "
				+ "catalog_type, course_basket_id, 'NONE' as basket_category, 0 as basket_credit, course_basket_id "
				+ "as course_id from academics.prg_splztn_curriculum_details where prgsplzn_prg_specialization_id =?1 and admission_year=?2 "
				+ "and catalog_type='CC' and lock_status=0) "
				+ "union all "
				+ "(select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, b.basket_category, b.credits as basket_credit, c.course_catalog_course_id "
				+ "as course_id from "
				+ "(select * from academics.prg_splztn_curriculum_details where "
				+ "prgsplzn_prg_specialization_id =?1 and admission_year=?2 and catalog_type='BC' and lock_status=0) a, academics.basket_details b, "
				+ "academics.basket_course_catalog c where a.course_basket_id=b.basket_id "
				+ "and a.course_basket_id=c.basket_details_basket_id and b.basket_id=c.basket_details_basket_id)) a "
				+ "where (a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version) in "
				+ "(select prgsplzn_prg_specialization_id, admission_year, max(curriculum_version) from "
				+ "academics.prg_splztn_curriculum_credits where lock_status=0 group by prgsplzn_prg_specialization_id, "
				+ "admission_year)) a, academics.course_catalog b where  "
				+ "a.course_id=b.course_id "
				+ "order by a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, a.course_id ", resultSetMapping = "ProgrammeSpecializationCurriculumInfo")

@NamedNativeQuery(name = "ProgrammeSpecializationCurriculumDetailModel.findBySpecIdAdmissionYearAndCourseCategory",
		query = "select a.prgsplzn_prg_specialization_id as progSpecializationId, a.admission_year as admissionYear, "
				+ "a.curriculum_version as curriculumVersion, a.course_category as courseCategory, a.catalog_type as catalogType, "
				+ "a.basket_category as basketCategory, a.basket_credit as basketCredit, a.course_id as courseId, b.code as courseCode, "
				+ "b.title as courseTitle from ("
				+ "select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, a.basket_category, a.basket_credit, a.course_id from ("
				+ "(select prgsplzn_prg_specialization_id, admission_year, curriculum_version, course_category, "
				+ "catalog_type, course_basket_id, 'NONE' as basket_category, 0 as basket_credit, course_basket_id "
				+ "as course_id from academics.prg_splztn_curriculum_details where prgsplzn_prg_specialization_id =?1 and admission_year=?2 "
				+ "and catalog_type='CC' and lock_status=0 and course_category=?3) "
				+ "union all "
				+ "(select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, b.basket_category, b.credits as basket_credit, c.course_catalog_course_id "
				+ "as course_id from "
				+ "(select * from academics.prg_splztn_curriculum_details where "
				+ "prgsplzn_prg_specialization_id =?1 and admission_year=?2 and catalog_type='BC' and lock_status=0 and course_category=?3) a, academics.basket_details b, "
				+ "academics.basket_course_catalog c where a.course_basket_id=b.basket_id "
				+ "and a.course_basket_id=c.basket_details_basket_id and b.basket_id=c.basket_details_basket_id)) a "
				+ "where (a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version) in "
				+ "(select prgsplzn_prg_specialization_id, admission_year, max(curriculum_version) from "
				+ "academics.prg_splztn_curriculum_credits where lock_status=0 group by prgsplzn_prg_specialization_id, "
				+ "admission_year)) a, academics.course_catalog b where  "
				+ "a.course_id=b.course_id "
				+ "order by a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, a.course_id ", resultSetMapping = "ProgrammeSpecializationCurriculumInfo")

@NamedNativeQuery(name = "ProgrammeSpecializationCurriculumDetailModel.findCourseCodeBySpecIdAndAdmissionYear",
		query = "select a.prgsplzn_prg_specialization_id as progSpecializationId, a.admission_year as admissionYear, "
				+ "a.curriculum_version as curriculumVersion, a.course_category as courseCategory, a.catalog_type as catalogType, "
				+ "a.basket_category as basketCategory, a.basket_credit as basketCredit, a.course_id as courseId, b.code as courseCode, "
				+ "b.title as courseTitle from ("
				+ "select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, a.basket_category, a.basket_credit, a.course_id from ("
				+ "(select prgsplzn_prg_specialization_id, admission_year, curriculum_version, course_category, "
				+ "catalog_type, course_basket_id, 'NONE' as basket_category, 0 as basket_credit, course_basket_id "
				+ "as course_id from academics.prg_splztn_curriculum_details where prgsplzn_prg_specialization_id =?1 and admission_year=?2 "
				+ "and catalog_type='CC' and lock_status=0 ) "
				+ "union all "
				+ "(select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, b.basket_category, b.credits as basket_credit, c.course_catalog_course_id "
				+ "as course_id from "
				+ "(select * from academics.prg_splztn_curriculum_details where "
				+ "prgsplzn_prg_specialization_id =?1 and admission_year=?2 and catalog_type='BC' and lock_status=0 ) a, academics.basket_details b, "
				+ "academics.basket_course_catalog c where a.course_basket_id=b.basket_id "
				+ "and a.course_basket_id=c.basket_details_basket_id and b.basket_id=c.basket_details_basket_id)) a "
				+ "where (a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version) in "
				+ "(select prgsplzn_prg_specialization_id, admission_year, max(curriculum_version) from "
				+ "academics.prg_splztn_curriculum_credits where lock_status=0 group by prgsplzn_prg_specialization_id, "
				+ "admission_year)) a, academics.course_catalog b where  "
				+ "a.course_id=b.course_id "
				+ "order by a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, b.code", resultSetMapping = "ProgrammeSpecializationCurriculumInfo")

@NamedNativeQuery(name = "ProgrammeSpecializationCurriculumDetailModel.findCourseCodeBySpecIdAdmissionYearAndCourseCategory",
		query = "select a.prgsplzn_prg_specialization_id as progSpecializationId, a.admission_year as admissionYear, "
				+ "a.curriculum_version as curriculumVersion, a.course_category as courseCategory, a.catalog_type as catalogType, "
				+ "a.basket_category as basketCategory, a.basket_credit as basketCredit, a.course_id as courseId, b.code as courseCode, "
				+ "b.title as courseTitle from ("
				+ "select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, a.basket_category, a.basket_credit, a.course_id from ("
				+ "(select prgsplzn_prg_specialization_id, admission_year, curriculum_version, course_category, "
				+ "catalog_type, course_basket_id, 'NONE' as basket_category, 0 as basket_credit, course_basket_id "
				+ "as course_id from academics.prg_splztn_curriculum_details where prgsplzn_prg_specialization_id =?1 and admission_year=?2 "
				+ "and catalog_type='CC' and lock_status=0 and course_category=?3) "
				+ "union all "
				+ "(select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, b.basket_category, b.credits as basket_credit, c.course_catalog_course_id "
				+ "as course_id from "
				+ "(select * from academics.prg_splztn_curriculum_details where "
				+ "prgsplzn_prg_specialization_id =?1 and admission_year=?2 and catalog_type='BC' and lock_status=0 and course_category=?3) a, academics.basket_details b, "
				+ "academics.basket_course_catalog c where a.course_basket_id=b.basket_id "
				+ "and a.course_basket_id=c.basket_details_basket_id and b.basket_id=c.basket_details_basket_id)) a "
				+ "where (a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version) in "
				+ "(select prgsplzn_prg_specialization_id, admission_year, max(curriculum_version) from "
				+ "academics.prg_splztn_curriculum_credits where lock_status=0 group by prgsplzn_prg_specialization_id, "
				+ "admission_year)) a, academics.course_catalog b where  "
				+ "a.course_id=b.course_id "
				+ "order by a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category, "
				+ "a.catalog_type, a.course_basket_id, b.code", resultSetMapping = "ProgrammeSpecializationCurriculumInfo")

@Entity
@Table(name="PRG_SPLZTN_CURRICULUM_DETAILS", schema="ACADEMICS")
public class ProgrammeSpecializationCurriculumDetailModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private ProgrammeSpecializationCurriculumDetailPKModel psccdPkId;
	
	@ManyToOne
	@JoinColumn(name="PRGSPLZN_PRG_SPECIALIZATION_ID", insertable = false, updatable = false)
	private ProgrammeSpecializationModel programmeSpecializationModel;
	
	@Column(name="CATALOG_TYPE")
	private String catalogType;
	
	@Column(name="COURSE_CATEGORY")
	private String courseCategory;
	
	@Column(name="LOCK_STATUS")
	private int status;
	
	@Column(name="LOG_USERID")
	private String logUserId;
	
	@Column(name="LOG_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date logTimestamp;
	
	@Column(name="LOG_IPADDRESS")
	private String logIpaddress;

	public ProgrammeSpecializationCurriculumDetailPKModel getPsccdPkId() {
		return psccdPkId;
	}

	public void setPsccdPkId(ProgrammeSpecializationCurriculumDetailPKModel psccdPkId) {
		this.psccdPkId = psccdPkId;
	}

	public ProgrammeSpecializationModel getProgrammeSpecializationModel() {
		return programmeSpecializationModel;
	}

	public void setProgrammeSpecializationModel(ProgrammeSpecializationModel programmeSpecializationModel) {
		this.programmeSpecializationModel = programmeSpecializationModel;
	}

	public String getCatalogType() {
		return catalogType;
	}

	public void setCatalogType(String catalogType) {
		this.catalogType = catalogType;
	}

	public String getCourseCategory() {
		return courseCategory;
	}

	public void setCourseCategory(String courseCategory) {
		this.courseCategory = courseCategory;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLogUserId() {
		return logUserId;
	}

	public void setLogUserId(String logUserId) {
		this.logUserId = logUserId;
	}
	
	public String getLogTimestamp() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	    if(this.logTimestamp==null) {
	    	return "";
	    } else {
	      return dateFormat.format(logTimestamp); 
	    }
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((psccdPkId == null) ? 0 : psccdPkId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProgrammeSpecializationCurriculumDetailModel other = (ProgrammeSpecializationCurriculumDetailModel) obj;
		if (psccdPkId == null) {
			if (other.psccdPkId != null)
				return false;
		} else if (!psccdPkId.equals(other.psccdPkId))
			return false;
		return true;
	}
	
}
