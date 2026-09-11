package org.vtop.CourseRegistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.CourseAllocationModel;
import org.vtop.CourseRegistration.model.CourseCatalogModel;


@Repository
public interface CourseAllocationRepository extends JpaRepository<CourseAllocationModel, String>
{	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType in (?5) and a.lockStatus=0 "+
			"order by a.timeTableModel.slotName, a.assoClassId, a.classId")
	List<CourseAllocationModel> findByCourseIdAndCourseType(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseId, List<String> courseType);
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType in (?5) and (a.classOption=1 or "+
			"(a.classOption=2 and a.specializationBatch=?6) or (a.classOption=3 and a.specializationBatch like '%' || ?7 || '%') or "+
			"(a.classOption=4 and a.specializationBatch=?8)) and a.lockStatus=0 order by a.timeTableModel.slotName, "+
			"a.assoClassId, a.classId")
	List<CourseAllocationModel> findByCourseIdCourseTypeAndClassOption(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseId, List<String> courseType, String progGroupCode, 
									String progSpecCode, String costCentreCode);
	
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType=?5 and a.lockStatus=0 "+
			"order by a.timeTableModel.slotName, a.assoClassId, a.classId")
	List<CourseAllocationModel> findByCourseIdAndCourseType2(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseId, String courseType);
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType=?5 and (a.classOption=1 or "+
			"(a.classOption=2 and a.specializationBatch=?6) or (a.classOption=3 and a.specializationBatch like '%' || ?7 || '%') or "+ 
			"(a.classOption=4 and a.specializationBatch=?8)) and a.lockStatus=0 order by a.timeTableModel.slotName, "+
			"a.assoClassId, a.classId")
	List<CourseAllocationModel> findByCourseIdCourseTypeAndClassOption2(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseId, String courseType, String progGroupCode, 
									String progSpecCode, String costCentreCode);
	
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType=?5 and a.erpId=?6 and a.lockStatus=0 "+
			"order by a.timeTableModel.slotName, a.assoClassId, a.classId")
	List<CourseAllocationModel> findByCourseIdCourseTypeAndEmpId(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseId, String courseType, String erpId);

	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType=?5 and a.erpId=?6 and (a.classOption=1 or "+ 
			"(a.classOption=2 and a.specializationBatch=?7) or (a.classOption=3 and a.specializationBatch like '%' || ?8 || '%') or "+ 
			"(a.classOption=4 and a.specializationBatch=?9)) and a.lockStatus=0 order by a.timeTableModel.slotName, "+
			"a.assoClassId, a.classId")
	List<CourseAllocationModel> findByCourseIdCourseTypeEmpIdAndClassOption(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseId, String courseType, String erpId, 
									String progGroupCode, String progSpecCode, String costCentreCode);
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType=?5 and a.erpId=?6 and a.slotId=?7 and "+
			"a.assoClassId=?8 and a.lockStatus=0 order by a.classId")
	CourseAllocationModel findByCourseIdCourseTypeEmpIdSlotIdAndAssoClassId(String semesterSubId, String[] classGroupId, 
								String[] classType, String courseId, String courseType, String erpId, Long slotId, 
								String assoClassId);
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType=?5 and a.erpId=?6 and a.slotId=?7 and "+
			"a.assoClassId=?8 and (a.classOption=1 or (a.classOption=2 and a.specializationBatch=?9) or "+
			"(a.classOption=3 and a.specializationBatch like '%' || ?10 || '%') or (a.classOption=4 and a.specializationBatch=?11)) "+
			"and a.lockStatus=0 order by a.classId")
	CourseAllocationModel findByCourseIdCourseTypeEmpIdSlotIdAssoClassIdAndClassOption(String semesterSubId, 
								String[] classGroupId, String[] classType, String courseId, String courseType, 
								String erpId, Long slotId, String assoClassId, String progGroupCode, 
								String progSpecCode, String costCentreCode);
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseCatalogModel.code=?4 and a.courseCatalogModel.courseSystem in (?5) "+
			"and a.registeredSeats<a.totalSeats and a.lockStatus=0 order by a.timeTableModel.slotName, "+
			"a.assoClassId, a.classId")
	List<CourseAllocationModel> findAvailableClassByCourseCode(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseCode, String[] courseSystem);
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseCatalogModel.code=?4 and a.courseCatalogModel.courseSystem in (?5) "+
			"and (a.classOption=1 or (a.classOption=2 and a.specializationBatch=?6) or (a.classOption=3 and "+
			"a.specializationBatch like '%' || ?7 || '%') or (a.classOption=4 and a.specializationBatch=?8)) and a.registeredSeats<a.totalSeats "+
			"and a.lockStatus=0 order by a.timeTableModel.slotName, a.assoClassId, a.classId")
	List<CourseAllocationModel> findAvailableClassByCourseCodeAndClassOption(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseCode, String[] courseSystem, String progGroupCode, 
									String progSpecCode, String costCentreCode);
	
	
	@Query("select (a.totalSeats-a.registeredSeats) as avbseats from CourseAllocationModel a where a.classId=?1")
	Integer findAvailableRegisteredSeats(String classId);
	
	@Query("select (10-a.waitingSeats) as avbseats from CourseAllocationModel a where a.classId=?1")
	Integer findAvailableWaitingSeats(String classId);
	
	//chennai code bai,bps
	@Query(value="select distinct course_catalog_course_id  from academics.course_allocation where semstr_details_semester_sub_id=?1 "
			+ "and clssgrp_master_class_group_id in (?2)  and  class_type in (?3) and (class_option=1 or (class_option=2 and "
			+ "specialization_batch=?4) or (class_option=3 and specialization_batch like '%' || ?5 || '%')  or (class_option=4 and specialization_batch=?6) "
			+ "and lock_status=0)",nativeQuery=true)	
	List<String> getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(String semesterSubId, List<String> classGroupId, 
			List<String> classType, String progGroupCode, String progSpecCode, String costCentreCode);
	
	
	@Query(value="select distinct course_catalog_course_id  from academics.course_allocation where semstr_details_semester_sub_id=?1 "
			+ "and clssgrp_master_class_group_id in (?2)  and  class_type in (?3) and class_option=1 and lock_status=0 "
			+ " and course_catalog_course_id in (?4) ",nativeQuery=true)	
	List<String> getCourseIdBySemesterSubIdClassOptionGeneral(String semesterSubId, List<String> classGroupId, 
			List<String> classType,List<String> courseIdList);
	
	@Query("select a from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "+
			"and a.classType in (?3) and a.courseId=?4 and a.courseType=?5 and a.lockStatus=0 and  classOption =1 "+
			"order by a.timeTableModel.slotName, a.assoClassId, a.classId")
	List<CourseAllocationModel> findByCourseIdAndCourseTypeClassOptionGeneral(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseId, String courseType);

	@Query(value=" select distinct course_catalog_course_id  from academics.course_allocation ca, "
			+ "academics.course_catalog cc  "
			+ "where semstr_details_semester_sub_id =?1 "
			+ "and clssgrp_master_class_group_id in (?2) and class_type in (?3) "
			+ "and cc.code in (?4)  and ca.lock_status =0 "
			+ "and ca.course_catalog_course_id = cc.course_id  ", nativeQuery=true )
	List<String> findCourseIdBySemesterSubIdClassGroupClassTypeAndCourseCode(String semesterSubId,
			List<String> classGroupLIst, List<String> classTypeList, List<String> compulsoryCourseCode);

	@Query(value="select distinct course_catalog_course_id  from academics.course_allocation ca,"
			+ "academics.course_catalog cc "
			+ "where semstr_details_semester_sub_id =?1 "
			+ "and clssgrp_master_class_group_id in (?2) and class_type in (?3)"
			+ "and cc.code in (?7) and ca.lock_status =0 "
			+ "and (ca.class_option =1 or (ca.class_option=2 and ca.specialization_batch =?4) or (ca.class_option=3 and "
			+ "ca.specialization_batch like '%' || ?5 || '%') or (ca.class_option=4 and ca.specialization_batch=?6))"
			+ "and ca.course_catalog_course_id = cc.course_id ", nativeQuery=true)
	List<String> findCourseIdBySemesterSubIdClassGroupClassTypeClassOptionAndCourseCode(String semesterSubId,
			List<String> classGroupLIst, List<String> classTypeList, String programGroupCode, String programSpecCode,
			String costCentreCode, List<String> compulsoryCourseCode);

	@Query(value= "select distinct course_catalog_course_id from academics.course_allocation where "
			+ "semstr_details_semester_sub_id =?1 and "
			+ "clssgrp_master_class_group_id in (?2) and class_type in (?3) "
			+ "and lock_status =0 ", nativeQuery = true)
	List<String> findCourseIdBySemesterSubIdClassGroupAndClassType(String semesterSubId, List<String> classGroupList,
			List<String> classTYpeList);
	
	
	@Query(" select distinct a.courseCatalogModel from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "
			+ " and a.courseCatalogModel.groupId in (?3) and a.classType in (?4) "
			+ " and  a.courseCatalogModel.groupCode like ?5 and a.courseCatalogModel.code in (?6) and a.lockStatus=0  ")
	List<CourseCatalogModel> findCourseForResearchProg(String semesterSubId,List<String> classGrpId,List<Integer> groupId,List<String> classType,
			String groupCode,List<String> courseCode);
	
	@Query(" select distinct a.courseCatalogModel from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2) "
			+ " and (a.courseCatalogModel.groupId in (?3) or  a.courseCatalogModel.groupCode like ?5) and a.classType in (?4) "
			+ "  and a.courseCatalogModel.code in (?6) and a.lockStatus=0  "
			+ "and (a.classOption=1 or (a.classOption=2 and a.specializationBatch=?7) or (a.classOption=3 and a.specializationBatch like '%'|| ?8 ||'%')"
			+ " or (a.classOption=4 and a.specializationBatch=?9))    ")
	List<CourseCatalogModel> findCourseForNonResearchProg(String semesterSubId,List<String> classGrpId,List<Integer> groupId,List<String> classType,
			String groupCode,List<String> courseCode,String specBatch1,String specBatch2,String specBatch3);
	

	@Query(" select distinct a.courseCatalogModel from CourseAllocationModel a where a.semesterSubId=?1 and a.clsGrpMasterGroupId in (?2)"
			+ "and  a.courseCatalogModel.courseSystem in (?7) "
			+ "	and (a.courseCatalogModel.groupId in (?8) or a.courseCatalogModel.groupCode like ?9)  "
			+ " and a.courseCatalogModel.genericCourseType not in (?10) and "
			+ "	a.courseCatalogModel.evaluationType not in (?11) "
			+ " and a.courseCatalogModel.courseId in (select  distinct b.courseId from CourseAllocationModel b where b.semesterSubId=?1 "
			+ " and b.clsGrpMasterGroupId in (?2) "
			+ " and b.classType in (?3) "
			+ " and (b.classOption=1 or (b.classOption=2 and b.specializationBatch=?4) or (b.classOption=3 and b.specializationBatch like '%' || ?5 || '%') "
			+ "	or (b.classOption=4 and b.specializationBatch=?6)) and b.lockStatus=0 ) and a.courseCatalogModel.code not in (?12) ")
	List<CourseCatalogModel>  findCourseUECourseList(String semesterSubId,List<String> classGrpId,List<String> classType,String specBatch1,
			String specBatch2,String specBatch3, List<String> courseSystem,List<Integer> groupId,String groupCode, List<String> gCourseType,
			 List<String> evelType,List<String> notEligibleCourseCode);
	
	
	@Query(" select distinct a.courseCatalogModel from CourseAllocationModel a where  "
			+ "	 (a.courseCatalogModel.groupId in (?1)"
			+ " or  a.courseCatalogModel.groupCode like ?2) and a.courseCatalogModel.code in (?3)   "
			+ "	and a.courseCatalogModel.code not in (?4) and a.courseCatalogModel.genericCourseType not in (?5)  "
			+ " and a.courseId in (?6) ")
	List<CourseCatalogModel>  findCourseRGRWithRPProg(List<Integer> eligibleGroupId, String alternateProgramGroup, List<String> courseCode, 
			List<String> notCourseCode,  List<String> notGenericCourseType, 
			List<String> courseId);
	
	
	
	@Query(" select distinct a.courseCatalogModel from CourseAllocationModel a where  "
			+ "	 (a.courseCatalogModel.groupId in (?1)"
			+ " or  a.courseCatalogModel.groupCode like ?2) and a.courseCatalogModel.code not in (?3) and "
			+ " a.courseCatalogModel.courseSystem in (?4)   "
			+ "	and a.courseCatalogModel.genericCourseType not in (?5)  "
			+ " and a.courseId in (?6) ")
	List<CourseCatalogModel>  findCourseRGRWithNonRPProg(List<Integer> eligibleGroupId, String alternateProgramGroup, List<String> courseCode, 
			List<String> courseSystem,  List<String> notGenericCourseType, 
			List<String> courseId);
	
	
	@Query(" select a from CourseCatalogModel a "
			+ " where (a.groupId in (?1) or  a.groupCode like ?2) "
			+ " and a.code in (?3) "
			+ "	and a.genericCourseType not in (?4)  "
			+ " and a.courseId in (?5) ")
	List<CourseCatalogModel>  findCourseRRCourseList(List<Integer> eligibleGroupId, String alternateProgramGroup, List<String> courseCode, 
			 List<String> notGenericCourseType, List<String> courseId);
	
	
	@Query(" select distinct a.courseCatalogModel from CourseAllocationModel a where  "
			+ "	 (a.courseCatalogModel.groupId in (?1)"
			+ " or  a.courseCatalogModel.groupCode like ?2) "
			+ "	and a.courseCatalogModel.code in (?3) and "
			+ " a.courseCatalogModel.courseSystem in (?4)   "
			+ "	and a.courseCatalogModel.genericCourseType not in (?5)  "
			+ " and a.courseId in (?6) ")
	List<CourseCatalogModel>  findCourseFFCSCALCourseList(List<Integer> eligibleGroupId, String alternateProgramGroup, List<String> courseCode, 
			List<String> courseSystem, List<String> notGenericCourseType, List<String> courseId);
	
	@Query(" select a from CourseCatalogModel a where a.courseId in (?1) ")
	List<CourseCatalogModel> findCourseListByCourseIds(List<String> courseId);
	
	
	@Query( value="select distinct c.course_catalog_course_id as course_ids  from academics.course_allocation  c " + 
			"inner join academics.additional_learning_details a    " + 
			"on c.semstr_details_semester_sub_id=?1   " + 
			"and c.clssgrp_master_class_group_id in (?2)    " + 
			"and c.lock_status=0  " + 
			"inner join  academics.additional_learning_crs_ctlg b " + 
			"on c.course_catalog_course_id  = b.course_catalog_course_id  " + 
			"and a.code  = b.addtnl_learning_details_code   " + 
			"and a.learning_type='ME'   " + 
			"and A.learning_system =?4 " + 
			"and ?3 = ANY(string_to_array(program_specialization, '/'))",nativeQuery=true)
	List<String>  findCourseMultiDespCourse(String semesterSubId,String[] classGroupId,String progSplId,String courseSystem);
	
	
	@Query(" select a from CourseCatalogModel a "
			+ " where a.courseId in (?1) ")
	List<CourseCatalogModel>  findCourseMultiDespCourseList(List<String> courseId);
	
	
	
	@Query(value="select DISTINCT cat.CODE as course_code from    " +
			"academics.prg_splztn_curriculum_details a   " +
			"inner join academics.basket_course_catalog b   " +
			"on a.prgsplzn_prg_specialization_id <> ?4    " +
			"and a.admission_year = ?1   " +
			"and a.course_category='CON'   " +
			"and a.course_basket_id  = b.basket_details_basket_id   " +
			"inner join academics.course_catalog basket_cat   " +
			"on basket_cat.course_id  = b.course_catalog_course_id  " +
			"inner join academics.basket_details c   " +
			"on a.course_basket_id  = c.basket_id   " +
			"inner join academics.course_allocation d   " +
			"on d.semstr_details_semester_sub_id = ?2   " +
			"and d.clssgrp_master_class_group_id in ( ?3)   " +
			"inner join academics.course_catalog cat   " +
			"on cat.course_id  = d.course_catalog_course_id  " +
			"and basket_cat.code=cat.code and cat.prgrm_group_programme_group_id in (?5)  " +
			"where cat.code not in ( " +
			"select DISTINCT b.code as courseCode from ( " +
			"select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category,  " +
			"a.catalog_type, a.course_basket_id, a.basket_category, a.basket_credit, a.course_id from ( " +
			"(select prgsplzn_prg_specialization_id, admission_year, curriculum_version, course_category,  " +
			"catalog_type, course_basket_id, 'NONE' as basket_category, 0 as basket_credit, course_basket_id  " +
			"as course_id from academics.prg_splztn_curriculum_details where prgsplzn_prg_specialization_id =?4 and admission_year= ?1  " +
			"and catalog_type='CC' and lock_status=0 and course_category in ('PFCC','PCC','UCC'))  " +
			"union all  " +
			"(select a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version, a.course_category,  " +
			"a.catalog_type, a.course_basket_id, b.basket_category, b.credits as basket_credit, c.course_catalog_course_id  " +
			"as course_id from  " +
			"(select * from academics.prg_splztn_curriculum_details where  " +
			"prgsplzn_prg_specialization_id =?4 and admission_year= ?1 and catalog_type='BC' and lock_status=0 and course_category in ('PFCC','PCC','UCC')) a, academics.basket_details b,  " +
			"academics.basket_course_catalog c where a.course_basket_id=b.basket_id  " +
			"and a.course_basket_id=c.basket_details_basket_id and b.basket_id=c.basket_details_basket_id)) a  " +
			"where (a.prgsplzn_prg_specialization_id, a.admission_year, a.curriculum_version) in  " +
			"(select prgsplzn_prg_specialization_id, admission_year, max(curriculum_version) from  " +
			"academics.prg_splztn_curriculum_credits where lock_status=0 group by prgsplzn_prg_specialization_id,  " +
			"admission_year)) a, academics.course_catalog b where   " +
			"a.course_id=b.course_id) ",nativeQuery = true)
	List<String> doGetListCourseIdOfOECBySemesterSubIdAndClassGrpId(int admissionsYear,String semesterSubId,List<String> classGrpId,
																	int programmeSplzationId,int progGroupId);
	@Query(" select distinct a.courseCatalogModel from CourseAllocationModel a where  "
			+ "	 (a.courseCatalogModel.groupId in (?1)"
			+ " or  a.courseCatalogModel.groupCode like ?2) "
			+ "	and a.courseCatalogModel.code in (?3) and "
			+ " a.courseCatalogModel.courseSystem in (?4)   "
			+ "	and a.courseCatalogModel.genericCourseType not in (?5)  ")
	List<CourseCatalogModel>  findCourseFFCSCALCourseListByCourseCode(List<Integer> eligibleGroupId, String alternateProgramGroup, List<String> courseCode,
														  List<String> courseSystem, List<String> notGenericCourseType);


	@Query(value="select DISTINCT 'OEC' as course_cat, a.CATALOG_TYPE as cat_type, a.COURSE_BASKET_ID as basket_Id, cat.COURSE_ID as course_id, cat.CODE as course_code, 'GENERAL' as catgry, cat.CREDITS as course_credits from   " +
			"academics.prg_splztn_curriculum_details a  " +
			"inner join academics.basket_course_catalog b  " +
			"on a.prgsplzn_prg_specialization_id <> ?4   " +
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
			"and basket_cat.code=cat.code and cat.prgrm_group_programme_group_id in (?5) ",nativeQuery = true)
	List<Object[]> doGetListCourseIdOfOECBySemesterSubIdAndClassGrpIdAndProgId(int admissionsYear,String semesterSubId,List<String> classGrpId,int programmeSplzationId,int progGroupId);
	
	
	@Query(value="select text(class_ids) from academics.psychometric_test_mark p  " +
			"inner join academics.psychometric_class_group p2  " +
			"on P.group_id  = P2.group_id " +
			"where p.semester_sub_id =?1 " +
			"and p.class_group_id in (?2) " +
			"and p.course_id =?3 " +
			"and p.regno =?4 ",nativeQuery=true)
	String doGetPsychometricTestClassIds(String semesterSubId,List<String> classGroupId,String courseId,String regNo);
}
