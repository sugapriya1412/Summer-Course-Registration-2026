package org.vtop.CourseRegistration.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.AppNotificationLogMaster;



@Repository
public interface AppNotificationLogMasterRepository  extends JpaRepository<AppNotificationLogMaster, String> {

	@Query("select a from AppNotificationLogMaster a where "
			+ "cast(a.logTimeStamp as date) between :fromDate AND :toDate ")
	List<AppNotificationLogMaster> getNotificationOverAllData(@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);
	 
	
	@Query("select a from AppNotificationLogMaster a where "
			+ "a.logUserId=:logUserId and cast(a.logTimeStamp as date) between :fromDate AND :toDate ")
	List<AppNotificationLogMaster> getNotificationOverAllDataByLogUser(@Param("logUserId") String logUserId
			,@Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);
			

}
