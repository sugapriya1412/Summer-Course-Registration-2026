package org.vtop.CourseRegistration.service;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.model.AppNotificationLogMaster;
import org.vtop.CourseRegistration.model.AppNotificationLogTransaction;
import org.vtop.CourseRegistration.model.AppNotificationLogTransactionPK;
import org.vtop.CourseRegistration.repository.AppNotificationLogMasterRepository;
import org.vtop.CourseRegistration.repository.AppNotificationLogTransactionRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



@Service
public class StudentMobilePushService {


	@Autowired
	private VtopMobileAppAccessHistoryService mobileAppAccessHistoryService;


	@Autowired
	private AppNotificationLogTransactionRepository appNotificationLogTransactionRepository;


	@Autowired
	private AppNotificationLogMasterRepository appNotificationLogMasterRepository;
	
	@Autowired private CourseRegistrationReadWriteService courseRegistrationReadWriteService;



	public void writeToTransactionLog(String notificationId,String notificationType,String receiverId,String responseMessage,String deliveryStatus,String logUserId,String ipAddress) {

		AppNotificationLogTransaction appNotificationLogTransaction = new AppNotificationLogTransaction();
		AppNotificationLogTransactionPK appNotificationLogTransactionPK = new AppNotificationLogTransactionPK();

		appNotificationLogTransactionPK.setNotificationId(notificationId);
		appNotificationLogTransactionPK.setNotificationType(notificationType);
		appNotificationLogTransactionPK.setReceiverId(receiverId);
		appNotificationLogTransaction.setAppNotificationLogTransactionPK(appNotificationLogTransactionPK);
		appNotificationLogTransaction.setResponseMessage(responseMessage);
		appNotificationLogTransaction.setDeliveryStatus(deliveryStatus);
		appNotificationLogTransaction.setLogUserid(logUserId);
		appNotificationLogTransaction.setLogIpaddress(ipAddress);
		appNotificationLogTransaction.setLogTimestamp(new Date());
		appNotificationLogTransactionRepository.save(appNotificationLogTransaction);

	}


	public int writeToMasterLog(String  notificationId, String notificationType, String categoryType,
			Date processStartTime, Date processEndTime, int totalCount, int mailSucessCount, int mailFailureCount,
			String mailFailureLog, int appSucessCount, int appFailureCount, String appFailureLog, String appMsg,
			String mailMsg, String logUserId, String ipAddress) {
		int result = 1;

		Optional<AppNotificationLogMaster> anlm = appNotificationLogMasterRepository.findById(notificationId);

		AppNotificationLogMaster appNotificationLogMaster=null;

		if (anlm.isPresent()) {

			appNotificationLogMaster = anlm.get();

			appNotificationLogMaster.setProcessEndTime(processEndTime);

			appNotificationLogMaster.setTotalCount(totalCount);
			appNotificationLogMaster.setAppSuccessCount(appSucessCount);
			appNotificationLogMaster.setAppFailureCount(appFailureCount);
			appNotificationLogMaster.setAppFailureLog(appFailureLog);
			appNotificationLogMaster.setAppMessage(appMsg);

			appNotificationLogMaster.setMailSuccessCount(mailSucessCount);
			appNotificationLogMaster.setMailFailureCount(mailFailureCount);
			appNotificationLogMaster.setMailFailureLog(mailFailureLog);
			appNotificationLogMaster.setMailMessage(mailMsg);

			appNotificationLogMaster.setLogUserId(logUserId);
			appNotificationLogMaster.setLogIpAddress(ipAddress);
			appNotificationLogMaster.setLogTimeStamp(new Date());

		} else {

			appNotificationLogMaster = new AppNotificationLogMaster();
			appNotificationLogMaster.setNotificationId(notificationId);
			appNotificationLogMaster.setNotificationType(notificationType);
			appNotificationLogMaster.setNotificationCategory(categoryType);
			appNotificationLogMaster.setProcessStartTime(processStartTime);
			appNotificationLogMaster.setProcessEndTime(processEndTime);
			appNotificationLogMaster.setTotalCount(totalCount);

			appNotificationLogMaster.setAppSuccessCount(appSucessCount);
			appNotificationLogMaster.setAppFailureCount(appFailureCount);
			appNotificationLogMaster.setAppFailureLog(appFailureLog);
			appNotificationLogMaster.setAppMessage(appMsg);

			appNotificationLogMaster.setMailSuccessCount(mailSucessCount);
			appNotificationLogMaster.setMailFailureCount(mailFailureCount);
			appNotificationLogMaster.setMailFailureLog(mailFailureLog);
			appNotificationLogMaster.setMailMessage(mailMsg);

			appNotificationLogMaster.setLogUserId(logUserId);
			appNotificationLogMaster.setLogIpAddress(ipAddress);
			appNotificationLogMaster.setLogTimeStamp(new Date());

		}

		appNotificationLogMasterRepository.save(appNotificationLogMaster);

		return result;
	}

	public int pushMessage(String registerNumber, String appTitle, String messageBody,HttpSession session)
			throws Exception {

		String authKey = "AIzaSyA_fWyoFALphctuguY20GpTiAfjvzc1OAw";
		String FMCurl = "https://fcm.googleapis.com/fcm/send";

		int responseCode =0;

		try
		{



			String vtopMobileAppData = mobileAppAccessHistoryService.findByRegisterNo(registerNumber);

			if (vtopMobileAppData!=null && !vtopMobileAppData.isEmpty()) {
				String userDeviceIdKey = vtopMobileAppData;

				URL url = new URL(FMCurl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);

				conn.setRequestMethod("POST");
				conn.setRequestProperty("Authorization", "key=" + authKey);
				conn.setRequestProperty("Content-Type", "application/json");

				ObjectMapper mapper = new ObjectMapper();
				ObjectNode json = mapper.createObjectNode();

				json.put("to", userDeviceIdKey.trim());
				ObjectNode info = mapper.createObjectNode();
				info.put("body", messageBody);
				info.put("title", appTitle);
				info.put("priority", "high");
				info.put("android_channel_id", "edu.vit.vtop.VITian");
				info.put("content_available", true);
				info.put("alert", true);
				json.putPOJO("data", info);
				json.putPOJO("notification", info);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(json.toString());
				wr.flush();
				wr.close();

				responseCode = conn.getResponseCode(); // ex. 200 = OK Success //500 internal error

			}
		}
		catch (Exception e) {
			// TODO: handle exception
			String IpAddress = (String) session.getAttribute("IpAddress");

			courseRegistrationReadWriteService.addErrorLog(e, 
					AppGlobalValues.REG_ERROR_METHOD +"_StudentMobilePushService", 
					"modifySlots", registerNumber, IpAddress);
		}


		return responseCode;
	}
}
