package org.vtop.CourseRegistration.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.AppNotificationLogTransaction;
import org.vtop.CourseRegistration.model.AppNotificationLogTransactionPK;


@Repository
public interface AppNotificationLogTransactionRepository
		extends JpaRepository<AppNotificationLogTransaction, AppNotificationLogTransactionPK> {

	@Query("select a from AppNotificationLogTransaction a where a.appNotificationLogTransactionPK.receiverId=:userId"
			+ " and cast(a.logTimestamp as date) between :fromDate AND :toDate")
	List<AppNotificationLogTransaction> getNotificationDataByUser(@Param("userId") String userId,
			@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

	@Query("select a from AppNotificationLogTransaction a where a.appNotificationLogTransactionPK.receiverId=:userId "
			+ "and a.logUserid=:logUserId and cast(a.logTimestamp as date) between :fromDate AND :toDate")
	List<AppNotificationLogTransaction> getNotificationDataByLogUser(@Param("userId") String userId,
			@Param("logUserId") String logUserId, @Param("fromDate") Date fromDate,
			@Param("toDate") Date toDate);

	@Query("select a.appNotificationLogTransactionPK.receiverId, a.appNotificationLogTransactionPK.notificationType, a.deliveryStatus, a.responseMessage , to_char(a.logTimestamp,'dd-MON-yyyy hh24:mi:ss') from "
			+ "AppNotificationLogTransaction a where a.appNotificationLogTransactionPK.notificationId=:notificationId")
	List<Object[]> getNotificationDataById(@Param("notificationId") String notificationId);

}
