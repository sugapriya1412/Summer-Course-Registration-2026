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
			+ " AND acm.activity_master_activity_id = 1 and acm.lock_status=0 and acm.activity_control_master_id = '14'"
			+ "INNER JOIN admissions.student_base sb ON sb.application_number = sld.application_no  "
			+ "INNER JOIN vtopmaster.cost_centre cc ON sld.cost_centre = cc.centre_id  "
			+ "INNER JOIN vtopmaster.education_status es ON es.edu_status = sld.edu_status "
			+ "INNER JOIN academics.semester_details sd on scasca.semstr_details_semester_sub_id=sd.semester_sub_id  "
			+ "INNER JOIN academics.class_group_master cgm on scasca.clssgrp_master_class_group_id=cgm.class_group_id  "
			+ "INNER JOIN vtopmaster.user_details ud on sld.reg_no =ud.userid) x  "
			+ "where reg_no=?1  "
			+ "order by start_timestamp asc ", nativeQuery=true)
	List<Object[]> findScheduleForCourseRegDetailByRegisterNumber(String registerNumber);
	
	
	
	// chennai code for 1 hour based

/*@Query(value = "SELECT x.programme_code AS progGroupCode,x.specialization AS progSpecializationDescription, x.costcentre, "
			+ "    x.reg_no AS registerNumber, "
			+ "    x.admission_year AS admissionYear,x.programme_specialization_id AS progSpecializationId, "
			+ "   x.study_system AS studySystem, x.edu_expn AS educationStatusDescription,x.lock_status AS lockStatus, "
			+ "    x.degree_prog_specialization_id, x.semstr_details_semester_sub_id,x.semester_desc,  x.clssgrp_master_class_group_id, "
			+ "    x.class_group_desc,x.programme_duration,x.programme_group_id AS progGroupId, "
			+ "    x.centreId,x.start_timestamp, " + "    x.end_timestamp,x.student_Name AS studentName,x.gender, "
			+ "    x.application_Number AS applicationNumber,\r\n" + "    x.progSpecializationCode,\r\n"
			+ "    x.progGroupDescription,\r\n" + "    x.programme_mode AS progGroupMode,\r\n"
			+ "    x.programme_level AS progGroupLevel,\r\n" + "    x.centreDescription,\r\n"
			+ "    x.educationStatus,\r\n" + "    x.email,\r\n" + "    x.mobile,\r\n"
			+ "    x.semester_master_semester_id AS semesterId,\r\n"
			+ "    clock_timestamp() between start_timestamp and end_timestamp,\r\n" + "\r\n" + "    x.reg_lock\r\n"
			+ "FROM\r\n" + "    (\r\n" + "    SELECT\r\n" + "        pg.code AS programme_code,\r\n"
			+ "        ps.code AS specialization_code,\r\n" + "        ps.description AS specialization,\r\n"
			+ "        cc.code AS costcentre,\r\n" + "        pg.programme_duration,\r\n"
			+ "        pg.programme_group_id,\r\n" + "        cc.centre_id,\r\n" + "        sld.reg_no,\r\n"
			+ "        sld.edu_status,\r\n" + "        es.edu_expn,\r\n" + "        sld.lock_status,\r\n"
			+ "        DATE_PART('year', sld.study_start_date) AS admission_year,\r\n"
			+ "        ps.programme_specialization_id,\r\n" + "        sld.study_system,\r\n"
			+ "        sld.degree_prog_specialization_id,\r\n" + "        scasca.semstr_details_semester_sub_id,\r\n"
			+ "        sd.description AS semester_desc,\r\n" + "        scasca.clssgrp_master_class_group_id,\r\n"
			+ "        cgm.description AS class_group_desc,\r\n" + "        sld.cost_centre AS centreId,\r\n"
			+ "        rs.from_time start_timestamp,\r\n" + "        rs.to_time end_timestamp,\r\n"
			+ "        sb.student_name,\r\n" + "        sb.gender,\r\n" + "        sb.application_number,\r\n"
			+ "        ps.code AS progSpecializationCode,\r\n" + "        pg.description AS progGroupDescription,\r\n"
			+ "        pg.programme_mode,\r\n" + "        pg.programme_level,\r\n"
			+ "        cc.description AS centreDescription,\r\n" + "        sld.edu_status AS educationStatus,\r\n"
			+ "        ud.email,\r\n" + "        ud.mobile,\r\n" + "        sd.semester_master_semester_id,\r\n"
			+ "        acm.lock_status AS reg_lock\r\n" + "    FROM\r\n" + "        vtopmaster.programme_group pg\r\n"
			+ "    INNER JOIN vtopmaster.programme_specialization ps ON\r\n"
			+ "        pg.programme_group_id = ps.prgrm_group_programme_group_id\r\n"
			+ "    INNER JOIN admissions.students_login_details sld ON\r\n"
			+ "        sld.prgsplprgrm_specialization_id = ps.programme_specialization_id\r\n"
			+ "    INNER JOIN academics.semester_classgroup_applicable_splztn_cum_activity scasca ON\r\n"
			+ "        sld.prgsplprgrm_specialization_id = scasca.programme_specialization_id\r\n"
			+ "        AND DATE_PART('year', sld.study_start_date) = scasca.admission_year\r\n"
			+ "    INNER JOIN academics.activity_control_master acm ON\r\n"
			+ "        scasca.registration_activity_control_master_id = acm.activity_control_master_id\r\n"
			+ "        AND acm.activity_master_activity_id = 1\r\n" + "        AND acm.lock_status = 0\r\n"
			+ "    INNER JOIN admissions.student_base sb ON\r\n"
			+ "        sb.application_number = sld.application_no\r\n"
			+ "    INNER JOIN vtopmaster.cost_centre cc ON\r\n" + "        sld.cost_centre = cc.centre_id\r\n"
			+ "    INNER JOIN vtopmaster.education_status es ON\r\n" + "        es.edu_status = sld.edu_status\r\n"
			+ "    INNER JOIN academics.semester_details sd ON\r\n"
			+ "        scasca.semstr_details_semester_sub_id = sd.semester_sub_id\r\n"
			+ "    INNER JOIN academics.class_group_master cgm ON\r\n"
			+ "        scasca.clssgrp_master_class_group_id = cgm.class_group_id\r\n"
			+ "    INNER JOIN vtopmaster.user_details ud ON\r\n" + "        sld.reg_no = ud.userid\r\n"
			+ "    INNER JOIN academics.registration_schedule_new  rs ON\r\n" + "        rs.regno = ud.userid\r\n"
			+ "        AND rs.status = '0'\r\n" + "    ) x\r\n" + "WHERE\r\n" + "    reg_no = ?1 \r\n" + "ORDER BY\r\n"
			+ "    start_timestamp ASC", nativeQuery = true)
	List<Object[]> findScheduleForCourseRegDetailByRegisterNumber(String registerNumber);*/


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

	//chennai code for registration permit
	@Query(value = "SELECT count(*) FROM academics.registration_permit a where semstr_details_semester_sub_id = ?1 and a.stdntslgndtls_register_number = ?2 and cast(permit_date as date) = CURRENT_DATE", nativeQuery = true)
	Integer getRegistrationPermitDetails(String semesterId, String registerNo);
}
