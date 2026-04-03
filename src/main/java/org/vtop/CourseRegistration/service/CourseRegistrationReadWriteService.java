package org.vtop.CourseRegistration.service;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.Common.service.MailUtility;
import org.vtop.CourseRegistration.model.ProjectRegistrationModel;
import org.vtop.CourseRegistration.model.ProjectRegistrationPKModel;
import org.vtop.CourseRegistration.repository.CourseEquivalanceRegRepository;
import org.vtop.CourseRegistration.repository.CourseRegistrationRepository;
import org.vtop.CourseRegistration.repository.CourseRegistrationWithdrawRepository;
import org.vtop.CourseRegistration.repository.ProjectRegistrationRepository;
import org.vtop.CourseRegistration.repository.StudentHistoryRepository;


@Service
@Transactional
public class CourseRegistrationReadWriteService
{
	@Autowired private CourseEquivalanceRegRepository courseEquivalanceRegRepository;
	@Autowired private CourseRegistrationRepository courseRegistrationRepository;
	@Autowired private CourseRegistrationWithdrawRepository courseRegistrationWithdrawRepository;
	@Autowired private ProjectRegistrationRepository projectRegistrationRepository;
	@Autowired private StudentHistoryRepository studentHistoryRepository;
	//@Autowired
	//private StudentMobilePushService studentMobilePushService;

	private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationReadWriteService.class);		

	//Course Equivalence Registration
	public void courseEquRegDeleteByRegisterNumberAndCourseId(String semesterSubId, String registerNumber, String courseId)
	{		
		courseEquivalanceRegRepository.deleteByRegisterNumberCourseId(semesterSubId, registerNumber, courseId);
	}


	//Course Registration
	//Procedure
	public String courseRegistrationAdd2(String psemsubid, String pclassid, String pregno, String pcourseid, 
			String pcomponent_type, String pcourse_option, Integer pregstatus, Integer pregcomponent_type, 
			String ploguserid, String plogipaddress, String pregtype, String pold_course_code, String pcalltype, 
			String pold_course_type, String pold_exam_month, String gradecategory,String curriculumCategory)
	{
		return courseRegistrationRepository.registration_insert_prc(psemsubid, pclassid, pregno, pcourseid, 
				pcomponent_type, pcourse_option, pregstatus, pregcomponent_type, ploguserid, plogipaddress, 
				pregtype, pold_course_code, pcalltype, pold_course_type, pold_exam_month, gradecategory,curriculumCategory, "NONE");
	}

	public String courseRegistrationUpdate2(String psemsubid, String pregno, String pcourseid, String pcomponent_type,
			String pcourse_option, String poldclassid, String pnewclassid, String ploguserid, String plogipaddress,
			Integer pregstatus, Integer pregcomponent_type, String pregtype, String pold_course_code, 
			String pold_course_type, String pold_exam_month, String gradecategory,String curriculumCategory)
	{
		return courseRegistrationRepository.registration_update_prc(psemsubid, pregno, pcourseid, pcomponent_type,
				pcourse_option, poldclassid, pnewclassid, ploguserid, plogipaddress, pregstatus, pregcomponent_type, 
				pregtype, pold_course_code, pold_course_type, pold_exam_month, gradecategory,curriculumCategory, "NONE");
	}

	public String courseRegistrationDelete(String psemsubid, String pregno, String pcourseid, String pcalltype, 
			String ploguserid, String plogipaddress, String pregtype, String poldcoursecode)
	{
		return courseRegistrationRepository.registration_delete_prc(psemsubid, pregno, pcourseid, pcalltype, 
				ploguserid, plogipaddress, pregtype, poldcoursecode, "NONE");
	}

	//Course Registration Withdraw
	public List<Object[]> getCourseWithdrawOTP(String semesterSubId, String registerNumber, String courseId, 
			Integer otpReasonType)
	{
		return courseRegistrationWithdrawRepository.findCourseWithdrawOTP(semesterSubId, registerNumber, courseId, 
				otpReasonType);
	}

	public void addCourseWithdrawOTP(String semesterSubId, String registerNumber, String courseId, 
			Integer otpReasonType, String mailOTP, String mobileOTP, String userId, String ipAddress)
	{
		courseRegistrationWithdrawRepository.insertCourseWithdrawOTP(semesterSubId, registerNumber, courseId, 
				otpReasonType, mailOTP, mobileOTP, userId, ipAddress);
	}

	public void modifyCourseWithdrawOTP(String semesterSubId, String registerNumber, String courseId, 
			Integer otpReasonType, String mailOTP, String mobileOTP, String userId, String ipAddress)
	{
		courseRegistrationWithdrawRepository.insertCRWOTPBackup(semesterSubId, registerNumber, courseId, 
				otpReasonType, userId, ipAddress);

		courseRegistrationWithdrawRepository.updateCourseWithdrawOTP(semesterSubId, registerNumber, courseId, 
				otpReasonType, mailOTP, mobileOTP, userId, ipAddress);
	}

	public void modifyCourseWithdrawOTPResponse(String semesterSubId, String registerNumber, String courseId, 
			Integer otpReasonType, String mobileOTPResponse)
	{
		courseRegistrationWithdrawRepository.updateCourseWithdrawOTPResponse(semesterSubId, registerNumber, 
				courseId, otpReasonType, mobileOTPResponse);
	}	

	public void modifyWithdrawOTPConfirmationStatus(String semesterSubId, String registerNumber, String courseId, 
			Integer otpReasonType, int mailOTPStatus, int mobileOTPStatus, String userId, String ipAddress)
	{
		courseRegistrationWithdrawRepository.updateWithdrawOTPConfirmationStatus(semesterSubId, registerNumber, 
				courseId, otpReasonType, mailOTPStatus, mobileOTPStatus, userId, ipAddress);
	}


	//Project Registration - Add
	public void saveProjectRegistration(String semesterSubId, String registerNumber, String courseId, String courseType, 
			String classId, String projectTitle, String guideErpId, int projectDuration, String resultSemesterSubId, 
			String projectOption)
	{		 
		ProjectRegistrationPKModel projectRegistrationPKModel = new ProjectRegistrationPKModel();
		ProjectRegistrationModel projectRegistrationModel= new ProjectRegistrationModel();

		projectRegistrationPKModel.setClassId(classId);
		projectRegistrationPKModel.setRegisterNumber(registerNumber);
		projectRegistrationModel.setProjectRegistrationPKId(projectRegistrationPKModel);								
		projectRegistrationModel.setSemesterSubId(semesterSubId);
		projectRegistrationModel.setCourseId(courseId);
		projectRegistrationModel.setSlotId(0);
		projectRegistrationModel.setCourseType(courseType);
		projectRegistrationModel.setProjectTitle(projectTitle);			
		projectRegistrationModel.setGuideErpid(guideErpId);
		projectRegistrationModel.setProjectDuration(projectDuration);
		projectRegistrationModel.setResultSemesterSubId(resultSemesterSubId);
		projectRegistrationModel.setInternalFoilcardNumber(0L);
		projectRegistrationModel.setExternalFoilcardNumber(0L);
		projectRegistrationModel.setProjectOption(projectOption);

		projectRegistrationRepository.save(projectRegistrationModel);
	}

	//Project Registration - Delete
	public void projectRegDeleteByRegisterNumberAndCourseId(String semesterSubId, String registerNumber, String courseId)
	{		
		projectRegistrationRepository.deleteByRegisterNumberCourseId(semesterSubId, registerNumber, courseId);
	}

	//Student History
	//Procedure - To insert the fresh Data in Student History from Examination Schema
	public String studentHistoryInsertProcess(String pRegisterNumber, String pCourseSystem)
	{
		String returnStatus = "SUCCESS";
		float days = 0, hours = 0;
		boolean executeStatus = false;

		List<Object[]> objectList = new ArrayList<>();

		objectList = studentHistoryRepository.findLastUpdatedPeriodByRegisterNumber(pRegisterNumber);
		if (objectList.isEmpty())
		{
			executeStatus = true;
		}
		else
		{
			if (objectList.get(0)[0] == null)
			{
				executeStatus = true;
			}
			else
			{
				days = Float.parseFloat(objectList.get(0)[1].toString());
				hours = Float.parseFloat(objectList.get(0)[2].toString());
				//LOGGER.trace("\n days : "+ days +" | hours: "+ hours);

				if ((days >= 1) || (hours >= 2))
				{
					executeStatus = true;
				}
			}
		}
		LOGGER.trace("\n Student History executeStatus : "+ executeStatus);

		if (executeStatus)
		{
			returnStatus = studentHistoryRepository.acad_student_history_insert_process2(pRegisterNumber, pCourseSystem, "NONE");
		}		

		return returnStatus;
	}


	//Checking the Data/Time & Update the Active Time Stamp
	/*public String scheduleCheck(Date startDate, Date endDate, String startTime, String endTime,
			String registerNo, int updateStatus, String ipAddress)
	{	
		int timeCheckFlag = 2;
		String timeCheckMessage = "NONE", presentDateTime = "", presentTime = "";
		long startTimeVal = 0, endTimeVal = 0, presentTimeVal = 0;

		Date presentDate = null;
		DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

		try
		{
			if ((startDate != null) && (endDate != null) && (startTime != null) && (!startTime.equals("")) 
					&& (endTime != null) && (!endTime.equals("")) && (registerNo != null) && (!registerNo.equals("")) 
					&& (ipAddress != null) && (!ipAddress.equals("")))
			{
				startTimeVal = Long.parseLong(startTime.replace(":", ""));
				endTimeVal = Long.parseLong(endTime.replace(":", ""));

				presentDateTime = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date());					
				String[] presentDateTimeArr = presentDateTime.split(" ");
				presentDate = format.parse(presentDateTimeArr[0]);
				presentTime = presentDateTimeArr[1];
				presentTimeVal = Long.parseLong(presentTime.replace(":", ""));

				//LOGGER.trace("\n StartDate: "+ startDate +" | StartTime: "+ startTime 
				//	+" | Start Time Value: " + startTimeVal);
				//LOGGER.trace("\n EndDate: "+ endDate +" | EndTime: "+ endTime +" | EndTimeValue: " + endTimeVal);
				//LOGGER.trace("\n PresentDate: "+ presentDate +" | PresentTime: "+ presentTime
				//		 				+" | PresentTimeValue: " + presentTimeVal);

				//Based on fixed Date & Time
				if ((presentDate.compareTo(startDate) >= 0) && (presentDate.compareTo(endDate) <= 0))
				{				
					if ((startDate.compareTo(endDate) == 0) && (presentDate.compareTo(startDate) == 0) 
							&& (presentTimeVal < startTimeVal))
					{
						timeCheckMessage = "Registration starts at "+ startTime +" Hrs.";
					}
					else if ((startDate.compareTo(endDate) == 0) && (presentDate.compareTo(startDate) == 0) 
							&& (presentTimeVal >= startTimeVal) && (presentTimeVal <= endTimeVal))
					{
						timeCheckMessage = "Success.";
						timeCheckFlag = 1;
					}
					else if ((startDate.compareTo(endDate) == 0) && (presentDate.compareTo(startDate) == 0) 
							&& (presentTimeVal > endTimeVal))
					{
						timeCheckMessage = "Registration closed.";
					}
					else if ((startDate.compareTo(endDate) != 0) && (presentDate.compareTo(startDate) == 0) 
							&& (presentTimeVal < startTimeVal))
					{
						timeCheckMessage = "Registration starts at "+ startTime +" Hrs.";
					}
					else if ((startDate.compareTo(endDate) != 0) && (presentDate.compareTo(startDate) == 0) 
							&& (presentTimeVal >= startTimeVal))
					{
						timeCheckMessage = "Success.";
						timeCheckFlag = 1;
					}
					else if ((startDate.compareTo(endDate) != 0) && (presentDate.compareTo(startDate) > 0) 
							&& (presentDate.compareTo(endDate) < 0))
					{
						timeCheckMessage = "Success.";
						timeCheckFlag = 1;
					}
					else if ((startDate.compareTo(endDate) != 0) && (presentDate.compareTo(endDate) == 0) 
							&& (presentTimeVal <= endTimeVal))
					{
						timeCheckMessage = "Success.";
						timeCheckFlag = 1;
					}
					else
					{
						timeCheckMessage = "Registration closed.";
					}
				}
				else
				{
					if (presentDate.compareTo(endDate) > 0)
					{
						timeCheckMessage = "Registration closed.";
					}
					else
					{
						timeCheckMessage = "Registration will start on "+ new SimpleDateFormat("dd-MMM-yyyy").format(startDate) 
								+" at "+ startTime +" Hrs.";
					}
				}

			}
			else
			{
				timeCheckMessage = "Session timed out.  Kindly logout and login again.";
			}
		}
		catch (Exception ex)
		{
			LOGGER.trace(ex);
		}

		return timeCheckFlag +"/"+ timeCheckMessage;
	}*/

	//Validate Course & Sending the OTP
	public String validateCourseAndSendOTP(String semesterSubId, String semesterDesc, String semesterShortDesc, 
			String registerNumber, String courseId, String courseCode, String studentEMailId, 
			String IpAddress, String processType, HttpSession session)
	{	
		int validateStatus = 2, otpReasonType = 0, flag = 2, flag2 = 2, flag3 = 2;	
		String msg = "NONE", authKeyVal = "NONE", mobileOTP = "NONE", emailOTP = "NONE", mailSubject = "", 
				mailBody = "", mailStatus = "";
		List<Object[]> objectList = new ArrayList<Object[]>();

		LOGGER.trace("\n semesterSubId: "+ semesterSubId +" | registerNumber: "+ registerNumber 
				+" | courseId: "+ courseId +" | studentEMailId: "+ studentEMailId +" | IpAddress: "+ IpAddress 
				+" | processType: "+ processType);

		try
		{
			//Validating the Mobile No.
			if((studentEMailId != null) && (!studentEMailId.equals("")) && (!studentEMailId.equals("NONE")))
			{
				flag = 1;
			}
			else
			{
				msg = "Kindly update your E-Mail id in your profile."; 
			}

			//Validating the Process Type.
			if (flag == 1)
			{
				if (processType.equals("DELETE") || processType.equals("MODIFY"))
				{
					flag2 = 1;
				}
				else
				{
					msg = "Process type is invalid. Please try again."; 
				}
			}

			//Sent & Check the Mail Status			
			if (flag2 == 1)
			{					
				mobileOTP = getRandomOtp(5);
				emailOTP = getRandomOtp(5);
				String prefixChar = getRandomChar(3);

				session.setAttribute("prefixOTP", prefixChar);

				session.setAttribute("prefixChar", prefixChar+"-" +emailOTP);


				if (emailOTP.equals(mobileOTP))
				{
					mobileOTP = getRandomOtp(5);
				}

				if (processType.equals("DELETE"))
				{					
					mailSubject = semesterShortDesc +" - Course Delete OTP";					
					mailBody = "<b>Dear Student ("+ registerNumber +"),</b><br><br>";
					mailBody = mailBody +"OTP to delete the course "+ courseCode +" is <b>"+prefixChar+" - "+ emailOTP +"</b> for "
							+ semesterDesc +".";
					mailBody = mailBody +"<br><br>This is an auto generated mail.  Kindly don't reply or send message "
							+"to this mail id.<br><br>Thanks !!";
					otpReasonType = 2;
				}
				else if (processType.equals("MODIFY"))
				{
					mailSubject = semesterShortDesc +" - Course Modify OTP";
					mailBody = "<b>Dear Student ("+ registerNumber +"),</b><br><br>";
					mailBody = mailBody +"OTP to modify the course "+ courseCode +" is <b>"+prefixChar+" - "+ emailOTP  +"</b> for "
							+ semesterDesc +".";
					mailBody = mailBody +"<br><br>This is an auto generated mail.  Kindly don't reply or send message "
							+"to this mail id.<br><br>Thanks !!";
					otpReasonType = 3;
				}

				//Add or Update the OTP Data
				objectList = getCourseWithdrawOTP(semesterSubId, registerNumber, courseId, otpReasonType);
				if (objectList.isEmpty())
				{
					addCourseWithdrawOTP(semesterSubId, registerNumber, courseId, otpReasonType, emailOTP, mobileOTP, 
							registerNumber, IpAddress);
				}
				else
				{
					modifyCourseWithdrawOTP(semesterSubId, registerNumber, courseId, otpReasonType, emailOTP, mobileOTP, 
							registerNumber, IpAddress);
				}


		/*

				int responseCode = 0;
				PushNotificationResult result = null;
				
			       // process end time
				 Date processStartTime = new Date();
                

                // writing to transaction and master logs

                String notificationId = UUID.randomUUID().toString();
                int successCount = (UtilityService.isSuccess(responseCode)) ? 1 : 0;
                int failCount = (UtilityService.isSuccess(responseCode)) ? 0 : 1;
                String appStoreData="<b>"+mailSubject+"</b><br/><br/><em>"+"Your OTP is "+prefixChar+" - "+ emailOTP+"</em>";
				
				try
				{
					responseCode = studentMobilePushService.pushMessage(registerNumber,mailSubject,"Your OTP is "+prefixChar+" - "+ emailOTP);
					 result = new PushNotificationResult(responseCode, UtilityService.processResult(responseCode),
                             registerNumber);

	                Date processEndTime = new Date();

					studentMobilePushService.writeToTransactionLog(notificationId, "mobileNotification", registerNumber,
							result.getMessage(),String.valueOf(responseCode), registerNumber, IpAddress);
					
					studentMobilePushService.writeToMasterLog(notificationId, "mobileNotification", "Student", processStartTime,
							processEndTime, 1, 0, 0, "", successCount, failCount, result.getMessage(), appStoreData,
							"", registerNumber, IpAddress);
					
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
*/

				//Sending E-Mail
				mailStatus = MailUtility.triggerMail(mailSubject, mailBody, "", studentEMailId);
				if (mailStatus.length() > 250)
				{
					mailStatus = mailStatus.substring(0, 250);
				}

				//Update the Mail Response Status
				modifyCourseWithdrawOTPResponse(semesterSubId, registerNumber, courseId, otpReasonType, mailStatus);

				if (mailStatus.equals("SUCCESS"))
				{
					flag3 = 1;
				}
				else
				{
					msg = "Invalid E-Mail ID. Kindly update your profile."; 
				}
			}
			LOGGER.trace("\n Flag: "+ flag +" | Flag2: "+ flag2 +" | Flag3: "+ flag3);

			if ((flag == 1) && (flag2 == 1) && (flag3 == 1))
			{	
				validateStatus = 1;
				msg = "SUCCESS";
			}
		}
		catch (Exception ex)
		{
			LOGGER.trace(ex);
		}

		//Generating the Authentication Key Value
		authKeyVal = generateCourseAuthKey(registerNumber, courseId, validateStatus, 2);

		LOGGER.trace("\n validateStatus: "+ validateStatus +" | authKeyVal: "+ authKeyVal 
				+" | emailOTP: "+ emailOTP +" | msg: "+ msg);

		return validateStatus +"|"+ authKeyVal +"|"+ emailOTP +"|"+ msg;
	}

	//Validate Course & OTP
	public String validateCourseAndOTP(String semesterSubId, String semesterDesc, String semesterShortDesc, 
			String registerNumber, String courseId, String courseCode, String emailOTP, 
			String IpAddress, String processType, HttpSession session)
	{	
		int validateStatus = 2, otpReasonType = 0, otpAllowFlag = 2, flag = 2, flag2 = 2;							
		String msg = "NONE", emailOTPDB = "NONE";
		List<Object[]> objectList = new ArrayList<Object[]>();

		LOGGER.trace("\n semesterSubId: "+ semesterSubId +" | registerNumber: "+ registerNumber 
				+" | courseId: "+ courseId +" | emailOTP: "+ emailOTP +" | IpAddress: "+ IpAddress 
				+" | processType: "+ processType);

		try
		{			
			//Validating the Process Type.
			if (processType.equals("DELETE"))
			{
				otpReasonType = 2;
				flag = 1;
			}
			else if (processType.equals("MODIFY"))
			{
				otpReasonType = 3;
				flag = 1;
			}
			else
			{
				msg = "Process type is invalid. Please try again."; 
			}

			//Checking the OTP
			if (flag == 1)
			{
				objectList = getCourseWithdrawOTP(semesterSubId, registerNumber, courseId, otpReasonType);
				if (!objectList.isEmpty())
				{
					for (Object[] e: objectList)
					{
						emailOTPDB = e[1].toString();
						break;
					}

					String prefixChar[] = session.getAttribute("prefixChar").toString().split("-");

					if (emailOTPDB.equals(emailOTP) && prefixChar[1].equals(emailOTP))
					{
						flag2 = 1;
					}
					else
					{
						otpAllowFlag = 1;
						msg = "Invalid OTP entered. Check your mail and try again."; 
					}
				}
				else
				{
					msg = "Unable to send OTP now. Try again later."; 
				}
			}
			LOGGER.trace("\n Flag: "+ flag +" | Flag2: "+ flag2);

			if ((flag == 1) && (flag2 == 1))
			{	
				//Confirm the OTP Status
				modifyWithdrawOTPConfirmationStatus(semesterSubId, registerNumber, courseId, otpReasonType, 2, 2, 
						registerNumber, IpAddress);

				validateStatus = 1;
				msg = "SUCCESS";
			}
		}
		catch (Exception ex)
		{
			LOGGER.trace(ex);
		}
		LOGGER.trace("\n validateStatus: "+ validateStatus +" | otpAllowFlag: "+ otpAllowFlag +" | msg: "+ msg);

		return validateStatus +"|"+ otpAllowFlag +"|"+ msg;
	}	

	public String getRandomOtp(Integer keyLength)
	{
		String sDefaultChars = "123456789abcdefghjkmnpqrxyzABCDEFGHJKMNPQRXYZ";
		Integer iKeyLength =keyLength;
		Integer iDefaultCharactersLength = sDefaultChars.length();
		String sMyKey="";
		for(int iCounter=1;iCounter<=iKeyLength;iCounter++)
		{
			Integer iPickedChar=(int) ((iDefaultCharactersLength*Math.random())+1);
			if(iPickedChar>=sDefaultChars.length())
				sMyKey = sMyKey+(sDefaultChars.substring(sDefaultChars.length()-1));
			else
				sMyKey = sMyKey+(sDefaultChars.substring(iPickedChar,iPickedChar+1));
		}

		return sMyKey;
	}

	public String getRandomChar(Integer keyLength)
	{
		String sDefaultChars = "ABCDEFGHJKMNPQRXYZ";
		Integer iKeyLength =keyLength;
		Integer iDefaultCharactersLength = sDefaultChars.length();
		String sMyKey="";
		for(int iCounter=1;iCounter<=iKeyLength;iCounter++)
		{
			Integer iPickedChar=(int) ((iDefaultCharactersLength*Math.random())+1);
			if(iPickedChar>=sDefaultChars.length())
				sMyKey = sMyKey+(sDefaultChars.substring(sDefaultChars.length()-1));
			else
				sMyKey = sMyKey+(sDefaultChars.substring(iPickedChar,iPickedChar+1));
		}

		return sMyKey;
	}

	//Generate Course Authorization Key
	public String generateCourseAuthKey(String registerNumber, String courseId, int validateStatus, int levelType)
	{
		String authKeyVal = "NONE";
		CommonFunctions scf = new CommonFunctions();

		if ((registerNumber == null) || registerNumber.equals(""))
		{
			registerNumber = "NONE";
		}

		if ((courseId == null) || courseId.equals(""))
		{
			courseId = "NONE";
		}

		if (levelType == 1)
		{
			authKeyVal = scf.Encrypt_String3(registerNumber +"_"+ courseId +"_"+ validateStatus +"_1", AppGlobalValues.KEY_LENGTH);
		}
		else if (levelType == 2)
		{
			authKeyVal = scf.Encrypt_String3(registerNumber +"_"+ courseId +"_"+ validateStatus +"_2", AppGlobalValues.KEY_LENGTH);
		}

		return authKeyVal;
	}

	public void addErrorLog(Exception x,String packageName, String programName, 
			String userId, String ipAddress)
	{

		if (packageName.length() >= 100)
		{
			packageName = packageName.substring(1, 99);
		}

		if (programName.length() >= 250)
		{
			programName = programName.substring(1, 249);
		}


		StackTraceElement[] lines=x.getStackTrace();
		String className="";
		String message=((x.getMessage()!=null)?x.getMessage():"")+" .. " +
				((x.getClass()!=null)?x.getClass().getName():"");

		int lineNo=0;
		String methodName="";
		String fileName="";
		String source="";

		for(StackTraceElement line : lines) {
			className=line.getClassName();

			if(className.contains("org.vtop") || className.contains("ORA-") 
					|| className.contains("oracle") || className.contains("SQL Error") || className.contains("SQLState")) {
				lineNo=line.getLineNumber();
				fileName=line.getFileName();
				methodName=line.getMethodName();
				source+="Line No="+String.valueOf(lineNo)+" .. Method Name="+methodName+" .. ClassName="+className+" .. FileName="+fileName;
			}
		}




		courseEquivalanceRegRepository.InsertErrorLog(message, "ACADEMICS", packageName, 
				programName, userId, ipAddress,source);
	}

}
