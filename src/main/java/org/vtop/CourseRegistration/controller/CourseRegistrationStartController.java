package org.vtop.CourseRegistration.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.model.StudentInformation;
import org.vtop.CourseRegistration.service.CompulsoryCourseConditionDetailService;
import org.vtop.CourseRegistration.service.CourseRegistrationCommonFunction;
import org.vtop.CourseRegistration.service.CourseRegistrationReadWriteService;
import org.vtop.CourseRegistration.service.StudentHistoryService;
import org.vtop.CourseRegistration.util.RegistrationConstants;


@Controller
public class CourseRegistrationStartController
{
	@Autowired private StudentHistoryService studentHistoryService;
	@Autowired private CompulsoryCourseConditionDetailService compulsoryCourseConditionDetailService;
	@Autowired private CourseRegistrationReadWriteService courseRegistrationReadWriteService;
	@Autowired private CourseRegistrationCommonFunction courseRegCommonFn;
	@Autowired private StudentInformation details;


	private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationStartController.class);


	@PostMapping("checkRegistration")
	public String checkRegistration(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response)
	{		
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");

		String studentDetails = (String) session.getAttribute("studentDetails");
		int regularFlag = (Integer) session.getAttribute("regularFlag");
		
		String programcode=(String) session.getAttribute("programGroupCode");
		//System.out.println("programcode==>"+programcode);
		int minStatus=(Integer) session.getAttribute("minorFlag");
		
		String captchaInput = request.getParameter("captchaStringProgInfo")!=null ? request.getParameter("captchaStringProgInfo").trim() : "" ;

		//For getting captcha from session attribute					
		String sessioncaptchaString = (String) session.getAttribute("CAPTCHA");
		
		Integer validateCaptcha = validateCaptcha(captchaInput, sessioncaptchaString,IpAddress);

		

		int  validateRRStatus = 2;
		String msg = "", urlPage = "", currentDateTimeStr="";
		Date currentDateTime = new Date();
		List<Object[]> fGradeList = new ArrayList<>();	

		try
		{			
			if (registerNumber != null)
			{	
				
				
				if(registerNumber.startsWith("25") && programcode.equalsIgnoreCase("BTECH")&& (regularFlag==1))
				{
					validateRRStatus = 1;
				}

				else if (regularFlag == 1)
				{
					validateRRStatus = 1;
				}
				else
				{
					fGradeList = studentHistoryService.getStudentHistoryGIAndFailCourse(registerNumber);
					if (fGradeList.size() > 0) 
					{
						validateRRStatus = 1;
					}
					else
					{
						msg = "You dont have F or N grade courses, so you are not eligible for Course Registration.";
					}
				}
				LOGGER.trace("\n  validateRRStatus: "+ validateRRStatus);

				if (validateRRStatus == 1)
				{
					if(validateCaptcha==0)
					{
						urlPage = "mainpages/MainPage";
					}
					else
					{
						currentDateTimeStr = RegistrationConstants.doGetCurrentDateDispaly(currentDateTime);
						model.addAttribute("CurrentDateTime", currentDateTimeStr);
						model.addAttribute("studentDetails", studentDetails);
						try
						{

							courseRegCommonFn.commonFunctionStudProgressInfo (session,model);

							courseRegCommonFn.callCaptcha(request, response, session, model);
							session.setAttribute("CAPTCHA", session.getAttribute("CAPTCHA"));
							String error = "";
							if(validateCaptcha==2)
							{
								error = "Invalid Captcha";
							}
							else
							{
								error = "Captcha is mandatory";
							}
							
							LocalDateTime currentDateTimeConvert = convertToLocalDateTimeViaInstant(currentDateTime);
					        
					        LocalDateTime startDateTimeConvert = details.getStartTimestamp();

					       
					       Boolean captchaAllowFlag = true ;  
					       
					   
					       if (startDateTimeConvert.isAfter(currentDateTimeConvert) || startDateTimeConvert.isEqual(currentDateTimeConvert)) {
					            captchaAllowFlag = false;
					        } 
					        
					        Duration duration = Duration.between(currentDateTimeConvert, startDateTimeConvert);
					        long differenceInSeconds = duration.getSeconds();
					        
					        model.addAttribute("regStartCountDiff", differenceInSeconds);
					        model.addAttribute("captchaAllowFlag", captchaAllowFlag);
					        model.addAttribute("errorCaptcha", error);
														
							
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						urlPage = "ConditionProgressPage";				
					}
					
				}
				else
				{
					courseRegCommonFn.callCaptcha(request, response, session, model);				
					urlPage = "StudentLogin";
				}
			}
			else
			{
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
			}

			currentDateTimeStr = RegistrationConstants.doGetCurrentDateDispaly(currentDateTime);
			model.addAttribute("CurrentDateTime", currentDateTimeStr);
			model.addAttribute("studentDetails", studentDetails);

		}
		catch(Exception exception)
		{
			LOGGER.trace(exception);
			exception.printStackTrace();
			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationStartController", 
					"checkRegistration", registerNumber, IpAddress);
			urlPage = "redirectpage";			
		}
		model.addAttribute("info", msg);

		return urlPage;
	}

	@PostMapping("doBackGroundProcess")
	public String doBackGroundProcess(Model model, HttpServletRequest request, HttpSession session, 
			HttpServletResponse response) throws ServletException, IOException 
	{

		List<String> compulsoryCourseList2 = new ArrayList<>();

		String registerNumber = (String) session.getAttribute("RegisterNumber");

		try
		{
			String studentCgpaData = (String) session.getAttribute("studentCgpaData");
			int compulsoryCourseStatus = (Integer) session.getAttribute("compulsoryCourseStatus");
			String semesterSubId = (String) session.getAttribute("SemesterSubId");
			int semesterId = (Integer) session.getAttribute("SemesterId");
			int academicGraduateYear = (Integer) session.getAttribute("acadGraduateYear");
			int programGroupId = (Integer) session.getAttribute("ProgramGroupId");
			String programGroupCode = (String) session.getAttribute("ProgramGroupCode");
			int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
			String programSpecCode = (String) session.getAttribute("ProgramSpecCode");
			int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
			int studentGraduateYear = (Integer) session.getAttribute("StudentGraduateYear");
			int costCenterId = (Integer) session.getAttribute("costCenterId");
			String costCentreCode = (String) session.getAttribute("costCentreCode");
			String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");


			//@SuppressWarnings("unchecked")
			//List<String> registerNumberList = (List<String>) session.getAttribute("registerNumberList");
			@SuppressWarnings("unchecked")
			List<String> compulsoryCourseList = (List<String>) session.getAttribute("compulsoryCourseList");

			List<Object[]> failGradeList = new ArrayList<>();
			List<String> reRegisterCourseCodeList = new ArrayList<>();


			failGradeList = studentHistoryService.getStudentHistoryGIAndFailCourse(registerNumber);
			if (!failGradeList.isEmpty()) 
			{
				reRegisterCourseCodeList = failGradeList.stream().map(e-> e[2].toString()).distinct().collect(Collectors.toList());
			}
			//LOGGER.trace("\n reRegisterCourseCodeList: "+ reRegisterCourseCodeList);

			//Fixing the Minimum & Maximum credit based on CGPA
			String[] creditLimitArr = courseRegCommonFn.getMinimumAndMaximumCreditLimit(semesterSubId, registerNumber, 
					programGroupCode, costCentreCode, studyStartYear, studentGraduateYear, 
					academicGraduateYear, semesterId, programSpecCode, studentCgpaData, session).split("\\|");
			float minCredit = Float.parseFloat(creditLimitArr[0]);
			float maxCredit = Float.parseFloat(creditLimitArr[1]);
			LOGGER.trace("\n minCredit: "+ minCredit +" | maxCredit: "+ maxCredit);

			//Reset the Minimum & Maximum credit in session
			session.setAttribute("minCredit", minCredit);
			session.setAttribute("maxCredit", maxCredit);

			//Processing the Compulsory Courses
			if ((compulsoryCourseStatus == 1) && (compulsoryCourseList.isEmpty()))
			{
				//Get the Compulsory Course List
				compulsoryCourseList2 = compulsoryCourseConditionDetailService.getEligibleCompulsoryCourseList(
						semesterSubId, programGroupId, studyStartYear, programSpecId, 
						registerNumber, programSpecCode, costCenterId, 0, classGroupId, 
						AppGlobalValues.CLASS_TYPE);
			}
			LOGGER.trace("\n compulsoryCourseList2: "+ compulsoryCourseList2);

			//Setting Session for Re-registered Course Code List
			session.setAttribute("reRegisterCourseCodeList", reRegisterCourseCodeList);
			//Setting Compulsory Course session
			session.setAttribute("compulsoryCourseList", compulsoryCourseList2);
		}
		catch (Exception exception)
		{
			LOGGER.trace("\n Exception: "+ exception);
		}

		//return "registrationStart :: ProcessJob";

		return "ConditionProgressPage";
	}


	private Integer validateCaptcha(String captchaInput, String sessioncaptchaString,String ipAddress)
	{
		Integer captchaNumber = 0;
		if ((captchaInput == null) || (captchaInput.equals("")) || (captchaInput.length() != 6))
		{	
			captchaNumber = 1;
		}
		else
		{
			if (AppGlobalValues.TEST_STATUS != 2 && !captchaInput.equals(sessioncaptchaString) 
					&& !AppGlobalValues.ADMIN_IP_ADDRESS.contains(ipAddress))
			{
				captchaNumber = 2;
			}
		}
		
		return captchaNumber;
	}
	
	public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


}