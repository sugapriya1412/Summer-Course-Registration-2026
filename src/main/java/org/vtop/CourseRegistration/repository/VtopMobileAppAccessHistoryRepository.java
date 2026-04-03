package org.vtop.CourseRegistration.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.VtopMobileAppAccessHistory;

@Repository
public interface VtopMobileAppAccessHistoryRepository extends JpaRepository<VtopMobileAppAccessHistory, String>
{
	@Query(value="select fcm_token from mobileapp.vtop_mobile_app_access_history where "
			+ "stdntslgndtls_register_number = ?1",nativeQuery = true)
	String findFcmByRegisterNumber(String registerNo);
	
	Optional<VtopMobileAppAccessHistory> findByRegisterNo(String registerNumber);
	
	@Query(value="select a from VtopMobileAppAccessHistory a where a.fcmToken=:fcmToken and a.registerNo=:regNo")
	VtopMobileAppAccessHistory getKeyIVByFcmRegNo(@Param("fcmToken") String fcmToken,@Param("regNo") String regNo);
	
	
}
