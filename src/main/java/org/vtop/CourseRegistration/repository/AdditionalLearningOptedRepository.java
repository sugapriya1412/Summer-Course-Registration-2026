package org.vtop.CourseRegistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.model.AdditionalLearningOpted;
import org.vtop.CourseRegistration.model.AdditionalLearningOptedPK;

@Repository
public interface AdditionalLearningOptedRepository extends JpaRepository<AdditionalLearningOpted
, AdditionalLearningOptedPK> 
{
	@Query("select a from AdditionalLearningOpted a where a.id.stdntslgndtlsRegisterNumber=?1 ")
	List<AdditionalLearningOpted> findAddlLearningByRegNo(String regNo);
	
	@Query("select a from AdditionalLearningOpted a where a.id.stdntslgndtlsRegisterNumber=?1 and a.learningType=?2")
	List<AdditionalLearningOpted> findAddlLearningByRegNoAndLearnType(String regNo,String learnType);
	
	@Modifying
	@Transactional
	@Query("delete from AdditionalLearningOpted a where a.id.stdntslgndtlsRegisterNumber=?1 "+
			"and a.id.additionalLearningCode=?2")
	void deleteByRegisterNumberAndAddLearnCode(String regNo, String addLearnCode);
	
	@Modifying
	@Query(value="insert into academics.additional_learning_opted_backup (stdntslgndtls_register_number,additional_learning_code, "
			+ "semstr_details_semester_sub_id,learning_type,log_userid,log_timestamp,log_ipaddress,moved_timestamp, "
			+ "moved_userid, moved_ipaddress, command_type)  "
			+ "(select stdntslgndtls_register_number,additional_learning_code,semstr_details_semester_sub_id, "
			+ "learning_type,log_userid,log_timestamp,log_ipaddress,current_timestamp,?3, ?4, ?5 from "
			+ "academics.additional_learning_opted where stdntslgndtls_register_number=?1 and additional_learning_code = ?2)", nativeQuery=true)
	int insertByRegisterNumberAndAddLearnCode(String regNo, String addLearnCode, 
				String userId, String ipaddress, String commandType);
	
}
