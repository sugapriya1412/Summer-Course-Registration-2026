package org.vtop.CourseRegistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.SemesterClassgroupApplicableSplztnCumActivity;
import org.vtop.CourseRegistration.model.SemesterClassgroupApplicableSplztnCumActivityPK;


@Repository
public interface SemesterClassgroupApplicableSplztnCumActivityRepository extends 
JpaRepository<SemesterClassgroupApplicableSplztnCumActivity,SemesterClassgroupApplicableSplztnCumActivityPK>{
	
	
	@Query(value="SELECT x.programme_code progGroupCode, x.specialization progSpecializationDescription,x.costcentre, x.reg_no registerNumber, x.admission_year admissionYear,  "
			+ "x.programme_specialization_id progSpecializationId, x.study_system studySystem, x.edu_expn educationStatusDescription, x.lock_status lockStatus,  "
			+ "x.degree_prog_specialization_id, x.semstr_details_semester_sub_id, x.semester_desc, x.clssgrp_master_class_group_id,  "
			+ "x.class_group_desc, x.programme_duration, x.programme_group_id progGroupId, x.centreId, x.start_timestamp, x.end_timestamp,  "
			+ "x.student_Name studentName, x.gender, x.application_Number applicationNumber, x.progSpecializationCode, x.progGroupDescription,  "
			+ "x.programme_mode progGroupMode, x.programme_level progGroupLevel, x.centreDescription, x.educationStatus,x.email, x.mobile, x.semester_master_semester_id semesterId, "
			+" clock_timestamp() between start_timestamp - INTERVAL '5 Minutes' and end_timestamp "
			+ "FROM (SELECT pg.code AS programme_code,  "
			+ "ps.code AS specialization_code, ps.description AS specialization, cc.code AS costcentre, pg.programme_duration, pg.programme_group_id,   "
			+ "cc.centre_id, sld.reg_no, sld.edu_status, es.edu_expn, sld.lock_status, date_part('year', sld.study_start_date) AS admission_year,  "
			+ "ps.programme_specialization_id, sld.study_system, sld.degree_prog_specialization_id, scasca.semstr_details_semester_sub_id, sd.description semester_desc,  "
			+ "scasca.clssgrp_master_class_group_id, cgm.description class_group_desc, sld.cost_centre centreId, acm.start_timestamp,  "
			+ "case when not acm.extension_timestamp IS NULL and acm.extension_timestamp>acm.end_timestamp THEN acm.extension_timestamp  "
			+ "else acm.end_timestamp END AS end_timestamp,sb.student_name ,sb.gender, sb.application_number, ps.code progSpecializationCode, pg.description progGroupDescription, "
			+ "pg.programme_mode, pg.programme_level, cc.description centreDescription,sld.edu_status educationStatus, ud.email, ud.mobile, sd.semester_master_semester_id "
			+ "FROM vtopmaster.programme_group pg  "
			+ "INNER JOIN vtopmaster.programme_specialization ps ON pg.programme_group_id = ps.prgrm_group_programme_group_id  "
			+ "INNER JOIN admissions.students_login_details sld ON sld.prgsplprgrm_specialization_id = ps.programme_specialization_id  "
			+ "INNER JOIN academics.semester_classgroup_applicable_splztn_cum_activity scasca ON sld.prgsplprgrm_specialization_id = scasca.programme_specialization_id and "
			+ "date_part('year', sld.study_start_date) = scasca.admission_year "
			+ "INNER JOIN academics.activity_control_master acm ON scasca.registration_activity_control_master_id = acm.activity_control_master_id "
			+ " AND acm.activity_master_activity_id = 1 and acm.lock_status=0 "
			+ "INNER JOIN admissions.student_base sb ON sb.application_number = sld.application_no  "
			+ "INNER JOIN vtopmaster.cost_centre cc ON sld.cost_centre = cc.centre_id  "
			+ "INNER JOIN vtopmaster.education_status es ON es.edu_status = sld.edu_status "
			+ "INNER JOIN academics.semester_details sd on scasca.semstr_details_semester_sub_id=sd.semester_sub_id  "
			+ "INNER JOIN academics.class_group_master cgm on scasca.clssgrp_master_class_group_id=cgm.class_group_id  "
			+ "INNER JOIN vtopmaster.user_details ud on sld.reg_no =ud.userid) x  "
			+ "where reg_no=?1  "
			+ "order by start_timestamp asc ", nativeQuery=true)
	List<Object[]> findScheduleForCourseRegDetailByRegisterNumber(String registerNumber);	


		@Query(value="SELECT x.programme_code progGroupCode, x.specialization progSpecializationDescription,x.costcentre, x.reg_no registerNumber, x.admission_year admissionYear,  "
			+ "x.programme_specialization_id progSpecializationId, x.study_system studySystem, x.edu_expn educationStatusDescription, x.lock_status lockStatus,  "
			+ "x.degree_prog_specialization_id, x.semstr_details_semester_sub_id, x.semester_desc, x.clssgrp_master_class_group_id,  "
			+ "x.class_group_desc, x.programme_duration, x.programme_group_id progGroupId, x.centreId, x.start_timestamp, x.end_timestamp,  "
			+ "x.student_Name studentName, x.gender, x.application_Number applicationNumber, x.progSpecializationCode, x.progGroupDescription,  "
			+ "x.programme_mode progGroupMode, x.programme_level progGroupLevel, x.centreDescription, x.educationStatus,x.email, x.mobile, x.semester_master_semester_id semesterId "
			+ ", clock_timestamp() between start_timestamp and end_timestamp FROM (SELECT pg.code AS programme_code,  "
			+ "ps.code AS specialization_code, ps.description AS specialization, cc.code AS costcentre, pg.programme_duration, pg.programme_group_id,   "
			+ "cc.centre_id, sld.reg_no, sld.edu_status, es.edu_expn, sld.lock_status, date_part('year', sld.study_start_date) AS admission_year,  "
			+ "ps.programme_specialization_id, sld.study_system, sld.degree_prog_specialization_id, scasca.semstr_details_semester_sub_id, sd.description semester_desc,  "
			+ "scasca.clssgrp_master_class_group_id, cgm.description class_group_desc, sld.cost_centre centreId, acm.start_timestamp,  "
			+ "case when not acm.extension_timestamp IS NULL and acm.extension_timestamp>acm.end_timestamp THEN acm.extension_timestamp  "
			+ "else acm.end_timestamp END AS end_timestamp,sb.student_name ,sb.gender, sb.application_number, ps.code progSpecializationCode, pg.description progGroupDescription, "
			+ "pg.programme_mode, pg.programme_level, cc.description centreDescription,sld.edu_status educationStatus, ud.email, ud.mobile, sd.semester_master_semester_id "
			+ "FROM vtopmaster.programme_group pg  "
			+ "INNER JOIN vtopmaster.programme_specialization ps ON pg.programme_group_id = ps.prgrm_group_programme_group_id  "
			+ "INNER JOIN admissions.students_login_details sld ON sld.prgsplprgrm_specialization_id = ps.programme_specialization_id  "
			+ "INNER JOIN academics.semester_classgroup_applicable_splztn_cum_activity scasca ON sld.prgsplprgrm_specialization_id = scasca.programme_specialization_id and "
			+ "date_part('year', sld.study_start_date) = scasca.admission_year "
			+ "INNER JOIN academics.activity_control_master acm ON scasca.mock_activity_control_master_id = acm.activity_control_master_id "
			+ " AND acm.activity_master_activity_id = 93 and acm.lock_status=0 "
			+ "INNER JOIN admissions.student_base sb ON sb.application_number = sld.application_no  "
			+ "INNER JOIN vtopmaster.cost_centre cc ON sld.cost_centre = cc.centre_id  "
			+ "INNER JOIN vtopmaster.education_status es ON es.edu_status = sld.edu_status "
			+ "INNER JOIN academics.semester_details sd on scasca.semstr_details_semester_sub_id=sd.semester_sub_id  "
			+ "INNER JOIN academics.class_group_master cgm on scasca.clssgrp_master_class_group_id=cgm.class_group_id  "
			+ "INNER JOIN vtopmaster.user_details ud on sld.reg_no =ud.userid ) x  "
			+ "where reg_no=?1  "
			+ "order by start_timestamp asc ", nativeQuery=true)
	List<Object[]> findScheduleForMockCourseRegDetailByRegisterNumber(String registerNumber);	

	
	@Query(value="select sld.reg_no as registernumber, sed.mark as eptMark, sbcd.hsc_group as hscGroup, fee.feeCategoryDescription, "
			+ "cgpa.total_credits_registered as totalCreditsRegistered, cgpa.total_credits_earned as totalCreditsEarned,  "
			+ "cgpa.cumulative_grade_point_average as cumulativeGradePointAverage "
			+ "from (select sld.reg_no,(case when sb.college_fees_fee_id is null then 0 else  "
			+ "sb.college_fees_fee_id end) as feeId from admissions.students_login_details sld inner join  "
			+ "admissions.student_base sb on sld.application_no=sb.application_number  "
			+ "where reg_no=?1) sld  "
			+ "left join academics.student_ept_details sed on sld.reg_no =sed.stdntslgndtls_register_number  "
			+ "left join academics.student_bridge_course_detail sbcd on sld.reg_no=sbcd.regno "
			+ "left join (select cf.fee_id, cs.description  feeCategoryDescription from finance.college_fees cf  "
			+ "inner join finance.fees_category fc on cf.fee_category_id =fc.fee_category_id  "
			+ "inner join finance.category_student cs on fc.category_student_category_id =cs.category_id) fee "
			+ "on sld.feeid=fee.fee_id left join (select sch.stdntslgndtls_register_number,total_credits_registered, total_credits_earned,  "
			+ "cumulative_grade_point_average, number_of_s_grade, number_of_a_grade,number_of_b_grade, number_of_c_grade, number_of_d_grade, "
			+ "number_of_e_grade,  number_of_f_grade, number_of_n_grade from  "
			+ "examinations.student_cgpa_history sch inner join admissions.students_login_details sld2 on sch.stdntslgndtls_register_number =sld2.reg_no  "
			+ "and sch.programme_specialization_id =sld2.prgsplprgrm_specialization_id where sld2.reg_no=?1 and  "
			+ "sch.lock_status=0 and modified_timestamp=(select max(modified_timestamp) from examinations.student_cgpa_history sch2 "
			+ "inner join admissions.students_login_details sld3 on sch2.stdntslgndtls_register_number =sld3.reg_no  "
			+ "and sch2.programme_specialization_id =sld3.prgsplprgrm_specialization_id where sld3.reg_no=?1 and sch2.lock_status=0)) cgpa "
			+ "on sld.reg_no=cgpa.stdntslgndtls_register_number ",nativeQuery=true)
	List<Object[]> findEPTMarkHscGroupFeeDescCGPAForCourseRegByRegisterNumber(String registerNumber);
	
	

	
	@Query(value="SELECT x.programme_code progGroupCode, x.specialization progSpecializationDescription,x.costcentre, x.reg_no registerNumber, x.admission_year admissionYear,  "
			+ "x.programme_specialization_id progSpecializationId, x.study_system studySystem, x.edu_expn educationStatusDescription, x.lock_status lockStatus,  "
			+ "x.degree_prog_specialization_id, x.semstr_details_semester_sub_id, x.semester_desc, x.clssgrp_master_class_group_id,  "
			+ "x.class_group_desc, x.programme_duration, x.programme_group_id progGroupId, x.centreId, x.start_timestamp, x.end_timestamp,  "
			+ "x.student_Name studentName, x.gender, x.application_Number applicationNumber, x.progSpecializationCode, x.progGroupDescription,  "
			+ "x.programme_mode progGroupMode, x.programme_level progGroupLevel, x.centreDescription, x.educationStatus,x.email, x.mobile, x.semester_master_semester_id semesterId "
			+ ", clock_timestamp() between start_timestamp and end_timestamp  FROM (SELECT pg.code AS programme_code,  "
			+ "ps.code AS specialization_code, ps.description AS specialization, cc.code AS costcentre, pg.programme_duration, pg.programme_group_id,   "
			+ "cc.centre_id, sld.reg_no, sld.edu_status, es.edu_expn, sld.lock_status, date_part('year', sld.study_start_date) AS admission_year,  "
			+ "ps.programme_specialization_id, sld.study_system, sld.degree_prog_specialization_id, scasca.semstr_details_semester_sub_id, sd.description semester_desc,  "
			+ "scasca.clssgrp_master_class_group_id, cgm.description class_group_desc, sld.cost_centre centreId, acm.start_timestamp,  "
			+ "case when not acm.extension_timestamp IS NULL and acm.extension_timestamp>acm.end_timestamp THEN acm.extension_timestamp  "
			+ "else acm.end_timestamp END AS end_timestamp,sb.student_name ,sb.gender, sb.application_number, ps.code progSpecializationCode, pg.description progGroupDescription, "
			+ "pg.programme_mode, pg.programme_level, cc.description centreDescription,sld.edu_status educationStatus, ud.email, ud.mobile, sd.semester_master_semester_id "
			+ "FROM vtopmaster.programme_group pg  "
			+ "INNER JOIN vtopmaster.programme_specialization ps ON pg.programme_group_id = ps.prgrm_group_programme_group_id  "
			+ "INNER JOIN admissions.students_login_details sld ON sld.prgsplprgrm_specialization_id = ps.programme_specialization_id  "
			+ "INNER JOIN academics.semester_classgroup_applicable_splztn_cum_activity scasca ON sld.prgsplprgrm_specialization_id = scasca.programme_specialization_id and "
			+ "date_part('year', sld.study_start_date) = scasca.admission_year "
			+ "INNER JOIN academics.activity_control_master acm ON scasca.adddrop_activity_control_master_id = acm.activity_control_master_id AND acm.activity_master_activity_id = 86 and acm.lock_status=0 "
			+ "INNER JOIN admissions.student_base sb ON sb.application_number = sld.application_no  "
			+ "INNER JOIN vtopmaster.cost_centre cc ON sld.cost_centre = cc.centre_id  "
			+ "INNER JOIN vtopmaster.education_status es ON es.edu_status = sld.edu_status "
			+ "INNER JOIN academics.semester_details sd on scasca.semstr_details_semester_sub_id=sd.semester_sub_id  "
			+ "INNER JOIN academics.class_group_master cgm on scasca.clssgrp_master_class_group_id=cgm.class_group_id  "
			+ "INNER JOIN vtopmaster.user_details ud on sld.reg_no =ud.userid ) x  "
			+ "where reg_no=?1  "
			+ "order by start_timestamp asc ", nativeQuery=true)
	List<Object[]> findScheduleForAddDropCourseRegDetailByRegisterNumber(String registerNumber);
	
	
	@Query(value = "select (case when count(*)>0 then 1 else 0 end) status from academics.wishlist_registration wr where "
			+ "semstr_details_semester_sub_id  =?1 and clssgrp_master_class_group_id=?2 and stdntslgndtls_register_number =?3", nativeQuery = true)
	Integer findWishListRegistrationStatusBySemesterSubIdAndClassGroupIdAndRegisterNumber(String semesterSubId, String classGroupId, String registerNumber);

	
}
