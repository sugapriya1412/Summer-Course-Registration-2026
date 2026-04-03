package org.vtop.CourseRegistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.ProgrammeSpecializationCurriculumCategoryCredit;
import org.vtop.CourseRegistration.model.ProgrammeSpecializationCurriculumCategoryCreditPk;

@Repository
public interface ProgramSpecializationCurriculumCategoryCreditRepository extends 
                       JpaRepository<ProgrammeSpecializationCurriculumCategoryCredit, ProgrammeSpecializationCurriculumCategoryCreditPk>{
	
	@Query("select a from ProgrammeSpecializationCurriculumCategoryCredit a"
			+ " where a.id.specializationId=?1 and a.id.admissionYear=?2  and"
			+ " (a.id.specializationId, a.id.admissionYear, a.id.curriculumVersion) in"
			+ " (select b.pscccPkId.specializationId, b.pscccPkId.admissionYear, max(b.pscccPkId.curriculumVersion) from"
			+ " ProgrammeSpecializationCurriculumCreditModel b where b.pscccPkId.specializationId=?1 and b.pscccPkId.admissionYear=?2 and b.status=0"
			+ " group by b.pscccPkId.specializationId, b.pscccPkId.admissionYear)"
			+ " order by a.id.specializationId, a.id.admissionYear, a.curriculumCategoryMaster.orderNo")
	List<ProgrammeSpecializationCurriculumCategoryCredit> findBySpecIdAndAdmissionYear(int specializationId, int admissionYear);
	
	@Query("select a from ProgrammeSpecializationCurriculumCategoryCredit a"
			+ " where a.id.specializationId=?1 and a.id.admissionYear=?2  and a.id.courseCategory=?3 and"
			+ " (a.id.specializationId, a.id.admissionYear, a.id.curriculumVersion) in"
			+ " (select b.pscccPkId.specializationId, b.pscccPkId.admissionYear, max(b.pscccPkId.curriculumVersion) from"
			+ " ProgrammeSpecializationCurriculumCreditModel b where b.pscccPkId.specializationId=?1 and b.pscccPkId.admissionYear=?2"
			+ " and  b.status=0 group by b.pscccPkId.specializationId, b.pscccPkId.admissionYear) order by a.id.specializationId,"
			+ " a.id.admissionYear")
	ProgrammeSpecializationCurriculumCategoryCredit findBySpecIdAdmissionYearAndCategory(int specializationId, int admissionYear, 
			String courseCategory);
}
