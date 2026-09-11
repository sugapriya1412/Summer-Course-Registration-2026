package org.vtop.CourseRegistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.Dto.ProgramSpecializationCurriculumDetailDto;
import org.vtop.CourseRegistration.model.ProgrammeSpecializationCurriculumDetailModel;
import org.vtop.CourseRegistration.model.ProgrammeSpecializationCurriculumDetailPKModel;

@Repository
public interface ProgrammeSpecializationCurriculumDetailRepository extends 
					JpaRepository<ProgrammeSpecializationCurriculumDetailModel, ProgrammeSpecializationCurriculumDetailPKModel>
{		
	@Query(value="select a.COURSE_CATEGORY, b.DESCRIPTION as COURSE_CATEGORY_DESC, a.CATALOG_TYPE, a.COURSE_BASKET_ID,  " + 
			"a.COURSE_ID, a.CODE, b.ORDER_NO, a.BASKET_CATEGORY,a.BASKETNAME from ( " +
			"select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, a.COURSE_ID, a.CODE,  " + 
			"a.BASKET_CATEGORY,a.BASKETNAME from ( " +
			"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_ID, b.CODE,  " + 
			"'NONE' as BASKET_CATEGORY,'NONE' as BASKETNAME from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, ACADEMICS.COURSE_CATALOG b  " +
			"where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and a.CURRICULUM_VERSION=?3  " + 
			"and a.CATALOG_TYPE='CC' and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.COURSE_ID)  " + 
			"union all  " + 
			"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_CATALOG_COURSE_ID as  " + 
			"COURSE_ID, c.CODE, d.BASKET_CATEGORY,D.DESCRIPTION as BASKETNAME from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a,  " +
			"ACADEMICS.BASKET_COURSE_CATALOG b, ACADEMICS.COURSE_CATALOG c, ACADEMICS.BASKET_DETAILS d where  " + 
			"a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and a.CURRICULUM_VERSION=?3 and  " + 
			"a.CATALOG_TYPE='BC' and a.LOCK_STATUS=0 and b.LOCK_STATUS=0 and d.LOCK_STATUS=0 and  " + 
			"a.COURSE_BASKET_ID=b.BASKET_DETAILS_BASKET_ID and a.COURSE_BASKET_ID=d.BASKET_ID and  " + 
			"b.COURSE_CATALOG_COURSE_ID=c.COURSE_ID) " + 
			"union all  " + 
			"select c.course_category ,c.course_category as catalog_type, " + 
			"b.course_catalog_course_id as course_basket_id  , " + 
			"b.course_catalog_course_id ,d.code ,c.description as basket_category,c.description as BASKETNAME " +
			"from academics.additional_learning_details a    " + 
			"inner join  academics.additional_learning_crs_ctlg b " + 
			"on  a.code  = b.addtnl_learning_details_code " + 
			"inner join academics.course_catalog d " + 
			"on b.course_catalog_course_id  = d.course_id  " + 
			"inner join academics.curriculum_category_master c  " + 
			"on a.learning_type  = c.course_category  " + 
			"and a.learning_type='ME'   " + 
			"and A.learning_system ='CBCS' " + 
			"and text(?1) = ANY(string_to_array(program_specialization, '/')) " + 
			") a) a, ACADEMICS.CURRICULUM_CATEGORY_MASTER b where  " + 
			"a.COURSE_CATEGORY=b.COURSE_CATEGORY order by b.ORDER_NO, a.CATALOG_TYPE, a.COURSE_ID", nativeQuery=true)
	List<Object[]> findCurriculumByAdmsnYearAndCCVersion2(Integer specId, Integer admissionYear, Float ccVersion,String studySystem);
	
	//Program Specialization, Year & Course based Curriculum Category detail	
	@Query(value="select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, a.COURSE_ID, a.CODE, a.BASKET_CATEGORY, "+
					"a.CREDITS from ("+
					"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_ID, b.CODE, "+
					"'NONE' as BASKET_CATEGORY, b.CREDITS from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+
					"ACADEMICS.COURSE_CATALOG b where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 "+
					"and a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='CC' and a.LOCK_STATUS=0 and "+
					"a.COURSE_BASKET_ID=b.COURSE_ID and b.CODE=?4) "+
					"union all "+
					"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, c.COURSE_CATALOG_COURSE_ID "+
					"as COURSE_ID, d.CODE, b.BASKET_CATEGORY, b.CREDITS from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+
					"ACADEMICS.BASKET_DETAILS b, ACADEMICS.BASKET_COURSE_CATALOG c, ACADEMICS.COURSE_CATALOG d "+
					"where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and a.CURRICULUM_VERSION=?3 "+
					"and a.CATALOG_TYPE='BC' and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.BASKET_ID and "+
					"a.COURSE_BASKET_ID=c.BASKET_DETAILS_BASKET_ID and b.BASKET_ID=c.BASKET_DETAILS_BASKET_ID and "+
					"c.COURSE_CATALOG_COURSE_ID=d.COURSE_ID and d.CODE=?4)) a "+
					"order by a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_ID", nativeQuery=true)
	List<Object[]> findCurriculumByAdmsnYearCCVersionAndCourseCode(Integer specId, Integer admissionYear, Float ccVersion, 
						String courseCode);
	
	@Query(value="select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, a.COURSE_ID, a.CODE, a.BASKET_CATEGORY, "+
			"a.CREDITS from ("+
			"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_ID, b.CODE, "+
			"'NONE' as BASKET_CATEGORY, b.CREDITS from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+
			"ACADEMICS.COURSE_CATALOG b where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 "+
			"and a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='CC' and a.LOCK_STATUS=0 and "+
			"a.COURSE_BASKET_ID=b.COURSE_ID and b.CODE in (?4)) "+
			"union all "+
			"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, c.COURSE_CATALOG_COURSE_ID "+
			"as COURSE_ID, d.CODE, b.BASKET_CATEGORY, b.CREDITS from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+
			"ACADEMICS.BASKET_DETAILS b, ACADEMICS.BASKET_COURSE_CATALOG c, ACADEMICS.COURSE_CATALOG d "+
			"where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and a.CURRICULUM_VERSION=?3 "+
			"and a.CATALOG_TYPE='BC' and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.BASKET_ID and "+
			"a.COURSE_BASKET_ID=c.BASKET_DETAILS_BASKET_ID and b.BASKET_ID=c.BASKET_DETAILS_BASKET_ID and "+
			"c.COURSE_CATALOG_COURSE_ID=d.COURSE_ID and d.CODE in (?4))) a "+
			"order by a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_ID", nativeQuery=true)
List<Object[]> findCurriculumByAdmsnYearCCVersionAndCourseCode(Integer specId, Integer admissionYear, Float ccVersion, 
				String[] courseCode);
	
	@Query(value="select a.COURSE_CODE from ("+
					"(select b.CODE as COURSE_CODE from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+
					"ACADEMICS.COURSE_CATALOG b where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and "+
					"a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='CC' and (a.COURSE_CATEGORY in (?4) or a.COURSE_CATEGORY in "+ 
					"(select course_category from academics.prgspl_curr_category_credit where PRGSPLZN_PRG_SPECIALIZATION_ID=?1 "+ 
					"and ADMISSION_YEAR=?2 and CURRICULUM_VERSION=?3 and total_credit_calc_status=2)) and a.LOCK_STATUS=0 and "+
					"a.COURSE_BASKET_ID=b.COURSE_ID) "+
					"union all "+
					"(select d.CODE as COURSE_CODE from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, ACADEMICS.BASKET_DETAILS b, "+
					"ACADEMICS.BASKET_COURSE_CATALOG c, ACADEMICS.COURSE_CATALOG d where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 "+
					"and a.ADMISSION_YEAR=?2 and a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='BC' and (a.COURSE_CATEGORY in (?4) or "+
					"a.COURSE_CATEGORY in (select course_category from academics.prgspl_curr_category_credit where "+
					"PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and ADMISSION_YEAR=?2 and CURRICULUM_VERSION=?3 and total_credit_calc_status=2)) "+
					"and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.BASKET_ID and a.COURSE_BASKET_ID=c.BASKET_DETAILS_BASKET_ID and "+
					"b.BASKET_ID=c.BASKET_DETAILS_BASKET_ID and c.COURSE_CATALOG_COURSE_ID=d.COURSE_ID)) a order by a.COURSE_CODE", 
					nativeQuery=true)
	List<String> findNCCourseByYearAndCCVersion(Integer specId, Integer admissionYear, Float ccVersion, List<String> courseCategory);
	
	
	
	@Query(value="select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, a.COURSE_ID, a.course_code, a.basket_code, "+
					"a.basket_credit, a.basket_category, b.DESCRIPTION as course_category_desc from ( "+ 
					"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_ID, b.CODE as course_code, "+
					"'NONE' as basket_code, 0 as basket_credit, 'NONE' as basket_category from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+
					"ACADEMICS.COURSE_CATALOG b where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and "+
					"a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='CC' and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.COURSE_ID and "+
					"b.CODE not in (?5)) "+ 
					"union all "+ 
					"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_CATALOG_COURSE_ID as course_id, "+ 
					"c.CODE as course_code, d.CODE as basket_code, d.CREDITS as basket_credit, d.BASKET_CATEGORY from "+
					"ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, ACADEMICS.BASKET_COURSE_CATALOG b, ACADEMICS.COURSE_CATALOG c, "+
					"ACADEMICS.BASKET_DETAILS d where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and "+
					"a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='BC' and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.BASKET_DETAILS_BASKET_ID "+ 
					"and a.COURSE_BASKET_ID=d.BASKET_ID and b.COURSE_CATALOG_COURSE_ID=c.COURSE_ID and b.BASKET_DETAILS_BASKET_ID=d.BASKET_ID "+
					"and c.CODE not in (?5)) "+ 
					"union all  "+ 
					"(select distinct 'PE' as course_category, 'CC' as catalog_type, a.COURSE_CATALOG_COURSE_ID as course_basket_id, "+ 
					"b.COURSE_ID, b.CODE as course_code, 'NONE' as basket_code, 0 as basket_credit, 'NONE' as basket_category from "+
					"ACADEMICS.COURSE_REGISTRATION a, ACADEMICS.COURSE_CATALOG b where a.STDNTSLGNDTLS_REGISTER_NUMBER in (?4) and "+
					"a.COURSE_OPTION_MASTER_CODE in (?6) and a.COURSE_CATALOG_COURSE_ID=b.COURSE_ID) "+
					") a, ACADEMICS.CURRICULUM_CATEGORY_MASTER b where a.COURSE_CATEGORY=b.COURSE_CATEGORY "+
					"order by a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, a.COURSE_ID", nativeQuery=true)
	List<Object[]> findStudentCurriculumByRegisterNumberUECourseAndPECourseOption(Integer specializationId, Integer admissionYear, 
						Float curriculumVersion, List<String> registerNumber, List<String> ueCourseCode, List<String> peCourseOptionCode);
	
	
	@Query(value="select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, a.COURSE_ID, a.course_code, a.basket_code, "+ 
					"a.basket_credit, a.basket_category, b.DESCRIPTION as course_category_desc from (  "+ 
					"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_ID, b.CODE as course_code, "+ 
					"'NONE' as basket_code, 0 as basket_credit, 'NONE' as basket_category from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+ 
					"ACADEMICS.COURSE_CATALOG b where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and "+ 
					"a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='CC' and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.COURSE_ID and "+ 
					"b.CODE not in (select b.CODE from ACADEMICS.COURSE_REGISTRATION a, ACADEMICS.COURSE_CATALOG b, "+ 
					"ACADEMICS.COURSE_OPTION_MASTER c where a.STDNTSLGNDTLS_REGISTER_NUMBER =?4 and a.COURSE_CATALOG_COURSE_ID=b.COURSE_ID "+ 
					"and a.COURSE_OPTION_MASTER_CODE=c.CODE and c.CURRICULUM_COURSE_CATEGORY not in ('NONE')))  "+ 
					"union all  "+ 
					"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_CATALOG_COURSE_ID as course_id,  "+ 
					"c.CODE as course_code, d.CODE as basket_code, d.CREDITS as basket_credit, d.BASKET_CATEGORY from "+ 
					"ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, ACADEMICS.BASKET_COURSE_CATALOG b, ACADEMICS.COURSE_CATALOG c, "+ 
					"ACADEMICS.BASKET_DETAILS d where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and "+ 
					"a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='BC' and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.BASKET_DETAILS_BASKET_ID  "+ 
					"and a.COURSE_BASKET_ID=d.BASKET_ID and b.COURSE_CATALOG_COURSE_ID=c.COURSE_ID and b.BASKET_DETAILS_BASKET_ID=d.BASKET_ID "+ 
					"and c.CODE not in (select b.CODE from ACADEMICS.COURSE_REGISTRATION a, ACADEMICS.COURSE_CATALOG b, "+ 
					"ACADEMICS.COURSE_OPTION_MASTER c where a.STDNTSLGNDTLS_REGISTER_NUMBER =?4 and a.COURSE_CATALOG_COURSE_ID=b.COURSE_ID "+ 
					"and a.COURSE_OPTION_MASTER_CODE=c.CODE and c.CURRICULUM_COURSE_CATEGORY not in ('NONE')))  "+ 
					"union all   "+ 
					"(select c.CURRICULUM_COURSE_CATEGORY as course_category, 'CC' as catalog_type, a.COURSE_CATALOG_COURSE_ID "+ 
					"as course_basket_id, b.COURSE_ID, b.CODE as course_code, 'NONE' as basket_code, 0 as basket_credit, "+ 
					"'NONE' as basket_category from ACADEMICS.COURSE_REGISTRATION a, ACADEMICS.COURSE_CATALOG b, "+ 
					"ACADEMICS.COURSE_OPTION_MASTER c where a.STDNTSLGNDTLS_REGISTER_NUMBER =?4 and a.COURSE_CATALOG_COURSE_ID=b.COURSE_ID "+ 
					"and a.COURSE_OPTION_MASTER_CODE=c.CODE and c.CURRICULUM_COURSE_CATEGORY not in ('NONE')) "
					+ "union all " + 
					"( " + 
					"select c.course_category ,c.course_category as catalog_type,  " + 
					"b.course_catalog_course_id as course_basket_id  ,  " + 
					"b.course_catalog_course_id ,d.code, 'NONE' as basket_code, 0 as basket_credit,  " + 
					"'NONE' as basket_category   " + 
					"from academics.additional_learning_details a     " + 
					"inner join  academics.additional_learning_crs_ctlg b  " + 
					"on  a.code  = b.addtnl_learning_details_code  " + 
					"inner join academics.course_catalog d  " + 
					"on b.course_catalog_course_id  = d.course_id   " + 
					"inner join academics.curriculum_category_master c   " + 
					"on a.learning_type  = c.course_category   " + 
					"and a.learning_type='ME'    " + 
					"and A.learning_system ='CBCS'  " + 
					"and text(?1) = ANY(string_to_array(program_specialization, '/')) " + 
					")"+ 
					") a, ACADEMICS.CURRICULUM_CATEGORY_MASTER b where a.COURSE_CATEGORY=b.COURSE_CATEGORY "+ 
					"order by a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, a.COURSE_ID", nativeQuery=true)
	List<Object[]> findCurriculumBySpecIdYearCCVersionAndRegisterNumber(Integer specializationId, Integer admissionYear, Float curriculumVersion, 
						String registerNumber,String courseCategory,String studeySystem);
	
	@Query(nativeQuery=true)
    ProgramSpecializationCurriculumDetailDto findBySpecIdAdmissionYearAndCourseCode(int specializationId, int admissionYear, 
			String courseCode);
	
	@Query(nativeQuery=true)
    List<ProgramSpecializationCurriculumDetailDto> findBySpecIdAndAdmissionYear(int specializationId, int admissionYear);
	
	@Query(nativeQuery=true)
	List<ProgramSpecializationCurriculumDetailDto> findBySpecIdAdmissionYearAndCourseCategory(int specializationId, int admissionYear, String courseCategory);
	
	@Query(nativeQuery=true)
	List<ProgramSpecializationCurriculumDetailDto> findCourseCodeBySpecIdAndAdmissionYear(int specializationId, int admissionYear);
	
	@Query(nativeQuery=true)
	List<ProgramSpecializationCurriculumDetailDto> findCourseCodeBySpecIdAdmissionYearAndCourseCategory(int specializationId, 
													int admissionYear, String courseCategory);

	//Program Specialization, Year & Course based Curriculum Category detail	
		@Query(value="select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, a.COURSE_ID, a.CODE, a.BASKET_CATEGORY, "+
						"a.CREDITS from ("+
						"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_ID, b.CODE, "+
						"'NONE' as BASKET_CATEGORY, b.CREDITS from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+
						"ACADEMICS.COURSE_CATALOG b where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 "+
						"and a.CURRICULUM_VERSION=?3 and a.CATALOG_TYPE='CC' and a.LOCK_STATUS=0 and "+
						"a.COURSE_BASKET_ID=b.COURSE_ID and b.CODE in (?4)) "+
						"union all "+
						"(select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, c.COURSE_CATALOG_COURSE_ID "+
						"as COURSE_ID, d.CODE, b.BASKET_CATEGORY, b.CREDITS from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a, "+
						"ACADEMICS.BASKET_DETAILS b, ACADEMICS.BASKET_COURSE_CATALOG c, ACADEMICS.COURSE_CATALOG d "+
						"where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 and a.ADMISSION_YEAR=?2 and a.CURRICULUM_VERSION=?3 "+
						"and a.CATALOG_TYPE='BC' and a.LOCK_STATUS=0 and a.COURSE_BASKET_ID=b.BASKET_ID and "+
						"a.COURSE_BASKET_ID=c.BASKET_DETAILS_BASKET_ID and b.BASKET_ID=c.BASKET_DETAILS_BASKET_ID and "+
						"c.COURSE_CATALOG_COURSE_ID=d.COURSE_ID and d.CODE in (?4))) a "+
						"order by a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_ID", nativeQuery=true)
	List<Object[]> findCurriculumByAdmsnYearCCVersionAndCourseCode(Integer pProgramSpecId, Integer pStudentStartYear,
			Float pCurriculumVersion, List<String> eqCourseCodeList);
		
	@Query(value="select b.course_catalog_course_id from  " +
			"academics.prg_splztn_curriculum_details a " +
			"inner join academics.basket_course_catalog b " +
			"on a.prgsplzn_prg_specialization_id=?7  " +
			"and a.admission_year =?1 " +
			"and a.course_category='CON' " +
			"and a.course_basket_id  = b.basket_details_basket_id " +
			"inner join academics.basket_details c " +
			"on a.course_basket_id  = c.basket_id " +
			"inner join academics.course_allocation d " +
			"on d.semstr_details_semester_sub_id =?2 " +
			"and d.clssgrp_master_class_group_id in (?3) " +
			"and b.course_catalog_course_id  = d.course_catalog_course_id " +
			"and  d.class_type in ('EFS','BFS') and (d.class_option=1 or (d.class_option=2 and  " +
			"d.specialization_batch=?4) or (d.class_option=3 and d.specialization_batch like '%' || ?5 || '%')  " +
			"or (d.class_option=4 and d.specialization_batch=?6)  " +
			"and d.lock_status=0) " +
			"inner join academics.course_catalog cc " +
			"on cc.course_id  = d.course_catalog_course_id ",nativeQuery = true)
	List<String> doGetConcentrationBasketCourses(int admissionYear,String semesterSubId,List<String> classGroupId,String progGroupShortDesc,
												 String specShortDesc,String schoolShortDesc,int progSpecId);



	@Query(value="select c.description,b.course_catalog_course_id from  " +
			"academics.prg_splztn_curriculum_details a " +
			"inner join academics.basket_course_catalog b " +
			"on a.prgsplzn_prg_specialization_id=2  " +
			"and a.admission_year =?1 " +
			"and a.course_category='CON' " +
			"and a.course_basket_id  = b.basket_details_basket_id " +
			"inner join academics.basket_details c " +
			"on a.course_basket_id  = c.basket_id " +
			"inner join academics.course_allocation d " +
			"on d.semstr_details_semester_sub_id =?2 " +
			"and d.clssgrp_master_class_group_id in (?3) " +
			"and b.course_catalog_course_id  = d.course_catalog_course_id " +
			"and  d.class_type in ('EFS','BFS') and (d.class_option=1 or (d.class_option=2 and  " +
			"d.specialization_batch=?4) or (d.class_option=3 and d.specialization_batch like '%' || ?5 || '%')  " +
			"or (d.class_option=4 and d.specialization_batch=?6)  " +
			"and d.lock_status=0) " +
			"inner join academics.course_catalog cc " +
			"on cc.course_id  = d.course_catalog_course_id ",nativeQuery = true)
	List<Object[]> doGetConBasketDetails(int admissionYear,String semesterSubId,List<String> classGroupId,String progGroupShortDesc,String specShortDesc,String schoolShortDesc);


	@Query(value="select q1.COURSE_CATEGORY,  " +
			"       q1.COURSE_CATEGORY_DESC,  " +
			"       q1.CATALOG_TYPE,  " +
			"       q1.COURSE_BASKET_ID,  " +
			"       q1.COURSE_ID,  " +
			"       q1.CODE,  " +
			"       q1.ORDER_NO,  " +
			"       q1.BASKET_CATEGORY,  " +
			"       q1.basketname,  " +
			"       case " +
			"         when q2.course_category is not null " +
			"              and q2.course_category <> q1.COURSE_CATEGORY  " +
			"         then q2.course_category  " +
			"         else null  " +
			"       end as registered_category,  " +
			"       case " +
			"         when q2.course_category is not null " +
			"              and q2.course_category <> q1.COURSE_CATEGORY  " +
			"         then cm.description  " +
			"         else null  " +
			"       end as registered_category_desc  " +
			"from (  " +
			"    select a.COURSE_CATEGORY, b.DESCRIPTION as COURSE_CATEGORY_DESC, a.CATALOG_TYPE, a.COURSE_BASKET_ID,    " +
			"           a.COURSE_ID, a.CODE, b.ORDER_NO, a.BASKET_CATEGORY,a.basketname " +
			"    from ( " +
			"        (select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_ID, b.CODE,    " +
			"                'NONE' as BASKET_CATEGORY ,'NONE' as basketname " +
			"         from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a  " +
			"         join ACADEMICS.COURSE_CATALOG b    " +
			"           on a.COURSE_BASKET_ID=b.COURSE_ID  " +
			"         where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 " +
			"           and a.ADMISSION_YEAR=?2 " +
			"           and a.CURRICULUM_VERSION=?3    " +
			"           and a.CATALOG_TYPE='CC' " +
			"           and a.LOCK_STATUS=0)    " +
			"        union all    " +
			"        (select a.COURSE_CATEGORY, a.CATALOG_TYPE, a.COURSE_BASKET_ID, b.COURSE_CATALOG_COURSE_ID as COURSE_ID, " +
			"                c.CODE, d.BASKET_CATEGORY,D.DESCRIPTION as basketname " +
			"         from ACADEMICS.PRG_SPLZTN_CURRICULUM_DETAILS a  " +
			"         join ACADEMICS.BASKET_COURSE_CATALOG b " +
			"           on a.COURSE_BASKET_ID=b.BASKET_DETAILS_BASKET_ID  " +
			"         join ACADEMICS.COURSE_CATALOG c " +
			"           on b.COURSE_CATALOG_COURSE_ID=c.COURSE_ID  " +
			"         join ACADEMICS.BASKET_DETAILS d " +
			"           on a.COURSE_BASKET_ID=d.BASKET_ID  " +
			"         where a.PRGSPLZN_PRG_SPECIALIZATION_ID=?1 " +
			"           and a.ADMISSION_YEAR=?2 " +
			"           and a.CURRICULUM_VERSION=?3 " +
			"           and a.CATALOG_TYPE='BC' " +
			"           and a.LOCK_STATUS=0 " +
			"           and b.LOCK_STATUS=0 " +
			"           and d.LOCK_STATUS=0) " +
			"        union all    " +
			"        (select c.course_category ,c.course_category as catalog_type, " +
			"                b.course_catalog_course_id as course_basket_id, " +
			"                b.course_catalog_course_id ,d.code ,c.description as basket_category ,  " +
			"                c.description as basketname  " +
			"         from academics.additional_learning_details a      " +
			"         join academics.additional_learning_crs_ctlg b " +
			"           on a.code  = b.addtnl_learning_details_code " +
			"         join academics.course_catalog d " +
			"           on b.course_catalog_course_id  = d.course_id    " +
			"         join academics.curriculum_category_master c    " +
			"           on a.learning_type  = c.course_category    " +
			"         where a.learning_type='ME'     " +
			"           and a.learning_system =?4 " +
			"           and text(?1) = ANY(string_to_array(program_specialization, '/'))) " +
			"    ) a  " +
			"    join ACADEMICS.CURRICULUM_CATEGORY_MASTER b " +
			"      on a.COURSE_CATEGORY=b.COURSE_CATEGORY  " +
			") q1  " +
			"left join (  " +
			"    select course_catalog_course_id, " +
			"           substring(course_catalog_course_id FROM '_(.+?)_') AS result,  " +
			"           course_category  " +
			"    from academics.COURSE_REGISTRATION " +
			"    where stdntslgndtls_register_number =?5  " +
			") q2  " +
			"  on q1.course_id = q2.course_catalog_course_id  " +
			"left join academics.curriculum_category_master cm " +
			"  on q2.course_category = cm.course_category  " +
			"order by q1.ORDER_NO, q1.CATALOG_TYPE, q1.COURSE_ID", nativeQuery=true)
	List<Object[]> findCurriculumByAdmsnYearAndCCVersion2ByRegNo(Integer specId, Integer admissionYear, Float ccVersion,String studySystem,String regNo);



	@Query(value="select distinct c.description, cat.code from   " +
			"academics.prg_splztn_curriculum_details a  " +
			"inner join academics.basket_course_catalog b  " +
			"on a.prgsplzn_prg_specialization_id <> 2   " +
			"and a.admission_year =?1  " +
			"and a.course_category='CON'  " +
			"and a.course_basket_id  = " +
			"b.basket_details_basket_id  " +
			"inner join academics.course_catalog basket_cat  " +
			"on basket_cat.course_id  = b.course_catalog_course_id " +
			"inner join academics.basket_details c  " +
			"on a.course_basket_id  = c.basket_id  " +
			"inner join academics.course_allocation d  " +
			"on d.semstr_details_semester_sub_id =?2  " +
			"and d.clssgrp_master_class_group_id in (?3)  " +
			"inner join academics.course_catalog cat  " +
			"on cat.course_id  = d.course_catalog_course_id " +
			"and basket_cat.code=cat.code and cat.prgrm_group_programme_group_id in (15) ",nativeQuery = true)
	List<Object[]> doGetConBasketDetailsForOEC(int admissionYear,String semesterSubId,List<String> classGroupId);
	
	
	
	@Query(value="select a.prgsplzn_prg_specialization_id as progSpecializationId, a.admission_year as admissionYear,  " +
			"a.curriculum_version as curriculumVersion, a.course_category as courseCategory, a.catalog_type as catalogType,  " +
			"a.basket_category as basketCategory, a.basket_credit as basketCredit, a.course_id as courseId, b.code as courseCode,  " +
			"b.title as courseTitle from ( " +
			"select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category,  " +
			"a.catalog_type, a.course_basket_id, a.basket_category, a.basket_credit, a.course_id from ( " +
			"(select prgsplzn_prg_specialization_id, admission_year, curriculum_version, course_category,  " +
			"catalog_type, course_basket_id, 'NONE' as basket_category, 0 as basket_credit, course_basket_id  " +
			"as course_id from academics.prg_splztn_curriculum_details pscd inner join vtopmaster.programme_specialization ps on   " +
			"pscd.prgsplzn_prg_specialization_id=ps.programme_specialization_id  " +
			"where pscd.admission_year=?1 and ps.prgrm_group_programme_group_id in (?2) and pscd.course_category=?3 and pscd.catalog_type='CC' and pscd.lock_status=0 )  " +
			"union all  " +
			"(select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category,  " +
			"a.catalog_type, a.course_basket_id, b.basket_category, b.credits as basket_credit, c.course_catalog_course_id  " +
			"as course_id from  " +
			"(select * from academics.prg_splztn_curriculum_details pscd inner join vtopmaster.programme_specialization ps on   " +
			"pscd.prgsplzn_prg_specialization_id=ps.programme_specialization_id  " +
			"where admission_year=?1  and ps.prgrm_group_programme_group_id in (?2) and pscd.course_category=?3 and " +
			"pscd.admission_year=?1 and catalog_type='BC' and pscd.lock_status=0 ) a, academics.basket_details b,  " +
			"academics.basket_course_catalog c where a.course_basket_id=b.basket_id  " +
			"and a.course_basket_id=c.basket_details_basket_id and b.basket_id=c.basket_details_basket_id)) a  " +
			"where (a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version) in  " +
			"(select prgsplzn_prg_specialization_id, admission_year, max(curriculum_version) from  " +
			"academics.prg_splztn_curriculum_credits where lock_status=0 group by prgsplzn_prg_specialization_id,  " +
			"admission_year)) a, academics.course_catalog b where   " +
			"a.course_id=b.course_id  " +
			"order by a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category,  " +
			"a.catalog_type, a.course_basket_id, b.code ",nativeQuery = true)
	List<Object[]>  doGetAllOECoursesACE(int admissionYear,int progGroupId,String courseOption);
	
	
	@Query(value="select a.prgsplzn_prg_specialization_id as progSpecializationId, a.admission_year as admissionYear,  " +
			"a.curriculum_version as curriculumVersion, a.course_category as courseCategory, a.catalog_type as catalogType,  " +
			"a.basket_category as basketCategory, a.basket_credit as basketCredit, a.course_id as courseId, b.code as courseCode,  " +
			"b.title as courseTitle from ( " +
			"select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category,  " +
			"a.catalog_type, a.course_basket_id, a.basket_category, a.basket_credit, a.course_id from ( " +
			"(select prgsplzn_prg_specialization_id, admission_year, curriculum_version, course_category,  " +
			"catalog_type, course_basket_id, 'NONE' as basket_category, 0 as basket_credit, course_basket_id  " +
			"as course_id from academics.prg_splztn_curriculum_details pscd inner join vtopmaster.programme_specialization ps on   " +
			"pscd.prgsplzn_prg_specialization_id=ps.programme_specialization_id  " +
			"where pscd.admission_year=?1 and ps.prgrm_group_programme_group_id in (?2) and pscd.course_category=?3 and  pscd.prgsplzn_prg_specialization_id=?4 and pscd.catalog_type='CC' and pscd.lock_status=0 )  " +
			"union all  " +
			"(select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category,  " +
			"a.catalog_type, a.course_basket_id, b.basket_category, b.credits as basket_credit, c.course_catalog_course_id  " +
			"as course_id from  " +
			"(select * from academics.prg_splztn_curriculum_details pscd inner join vtopmaster.programme_specialization ps on   " +
			"pscd.prgsplzn_prg_specialization_id=ps.programme_specialization_id  " +
			"where admission_year=?1  and ps.prgrm_group_programme_group_id in (?2) and pscd.course_category=?3 and  pscd.prgsplzn_prg_specialization_id=?4 and " +
			"pscd.admission_year=?1 and catalog_type='BC' and pscd.lock_status=0 ) a, academics.basket_details b,  " +
			"academics.basket_course_catalog c where a.course_basket_id=b.basket_id  " +
			"and a.course_basket_id=c.basket_details_basket_id and b.basket_id=c.basket_details_basket_id)) a  " +
			"where (a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version) in  " +
			"(select prgsplzn_prg_specialization_id, admission_year, max(curriculum_version) from  " +
			"academics.prg_splztn_curriculum_credits where lock_status=0 group by prgsplzn_prg_specialization_id,  " +
			"admission_year)) a, academics.course_catalog b where   " +
			"a.course_id=b.course_id  " +
			"order by a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category,  " +
			"a.catalog_type, a.course_basket_id, b.code ",nativeQuery = true)
	List<Object[]>  doGetAllOECoursesACEByProgramSpecId(int admissionYear,int progGroupId,String courseOption,int programmeSplzationId);


}
