package org.vtop.CourseRegistration.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.GlobalMaster;
import org.vtop.CourseRegistration.model.CourseEligibleModel;
import org.vtop.CourseRegistration.model.StudentInformation;
import org.vtop.CourseRegistration.model.ProgrammeSpecializationCurriculumCreditModel;
import org.vtop.CourseRegistration.service.CourseEligibleRegService;
import org.vtop.CourseRegistration.service.CourseRegCommonService;
import org.vtop.CourseRegistration.service.CourseRegistrationCommonFunction;
import org.vtop.CourseRegistration.service.CourseRegistrationReadWriteService;
import org.vtop.CourseRegistration.service.CourseRegistrationService;
import org.vtop.CourseRegistration.service.CurriculumCategoryMasterService;
import org.vtop.CourseRegistration.util.RegistrationConstants;


@Controller
public class CourseRegistrationLoginController 
{	
	@Autowired private CourseRegistrationCommonFunction courseRegCommonFn;
	@Autowired private CourseRegistrationReadWriteService courseRegistrationReadWriteService;
	@Autowired private CourseRegCommonService courseRegistrationCommonMongoService;
	@Autowired private CourseEligibleRegService courseEligibleRegService;
	@Autowired private CurriculumCategoryMasterService curriculumCategoryMasterService;

	@Autowired
	private CourseRegistrationService regService;

	@Autowired
	private StudentInformation details;


	private static String WISHLIST_CHECK= "Wishlist Completed ?";
	private static String ACAEMIC_YEAR_CHECK = "Your Academic Start Year";
	private static String PGM_SPEC_CHECK = "Programme Specializations ";
	private static String PGM_MODE_CHECK = "Programme Mode";
	private static String REGISTRATION_SCHEDULE_CHECK = "Registration Schedule";

	private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationLoginController.class);
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");	




	@PostMapping("registrationStart")
	public String 	registrationStart(Model model, HttpServletRequest request, HttpSession session, 
			HttpServletResponse response)
	{
		String studentDetails = (String) session.getAttribute("studentDetails");
		Date currentDateTime = new Date();
		String currentDateTimeStr="";
		currentDateTimeStr = RegistrationConstants.doGetCurrentDateDispaly(currentDateTime);
		model.addAttribute("CurrentDateTime", currentDateTimeStr);
		model.addAttribute("studentDetails", studentDetails);
		try
		{
			session.setAttribute("excessCreditAllowedCategories", curriculumCategoryMasterService.getCreditExceedAllowedCategories());



			String specCode = (String) session.getAttribute("ProgramSpecCode");
			String specDesc = (String) session.getAttribute("ProgramSpecDesc");
			int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
			DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");  


			Map<String,String> eligibilityResult = new LinkedHashMap<>();
			eligibilityResult.put(REGISTRATION_SCHEDULE_CHECK, details.getStartTimestamp().format(format)+" till "+details.getEndTimestamp().format(format));
			eligibilityResult.put(WISHLIST_CHECK, "NOT APPLICABLE");
			eligibilityResult.put(ACAEMIC_YEAR_CHECK, studyStartYear+"");
			eligibilityResult.put(PGM_SPEC_CHECK, specCode+" - "+ specDesc);
			eligibilityResult.put(PGM_MODE_CHECK, details.getProgGroupMode());

			model.addAttribute("eligibilityResult", eligibilityResult);

			courseRegCommonFn.callCaptcha(request, response, session, model);
			session.setAttribute("CAPTCHA", session.getAttribute("CAPTCHA"));
					
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

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "ConditionProgressPage";
	}
	
	public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


	@RequestMapping("/login/success")
	public String loginSuccess(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) 
			throws ServletException, IOException, ParseException
	{	

		String registerNo = (String) session.getAttribute("RegisterNumber");
		String ipAddress = (String) session.getAttribute("IpAddress");		
		String urlPage = "", msg = "", currentDateTimeStr = "";
		Date currentDateTime = new Date();


		try
		{

			String studentName = (String) session.getAttribute("studentName");
			int specId = (Integer) session.getAttribute("ProgramSpecId");
			String specCode = (String) session.getAttribute("ProgramSpecCode");
			String specDesc = (String) session.getAttribute("ProgramSpecDesc");
			int groupId = (Integer) session.getAttribute("ProgramGroupId");
			String programGroupCode = (String) session.getAttribute("ProgramGroupCode");				
			int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
			int studentGraduateYear = (Integer) session.getAttribute("StudentGraduateYear");			

			int regularFlag = 2, reRegFlag = 2,   wlCount = 0, 
					semesterId = 0, regCount = 0, regCredit = 0, wlCredit= 0,minorFlag=2;
			float cclVersion = 0;

			String semesterSubId = "", semesterShortDesc = "", semesterDesc = "", 
					registrationMethod = "GEN", classGroupId = "";
			String programEligible = "", CGPAEligible = "",   checkCourseSystem = "";

			semesterSubId = details.getSemesterSubId();	
			semesterId = details.getSemesterId();
			semesterDesc = details.getSemesterDesc();
			semesterShortDesc = details.getSemesterDesc();
			classGroupId = details.getClassGroupId();

			int activityId = 86;

			if(AppGlobalValues.ACTIVITY_EVENT.equals("REGISTRATION"))
			{
				activityId = 1;
			}
			else if(AppGlobalValues.ACTIVITY_EVENT.equals("MOCK_COURSE_REGISTRATION"))
			{
				activityId = 93;
			}

			GlobalMaster globalValues = regService.doGetAppGlobalValues(semesterSubId, classGroupId, activityId);

			session.setAttribute("globalValues", globalValues);

			int  academicGraduateYear = 0;
			float maxCredit = globalValues.getMaxCredits(), minCredit = 16,	cclTotalCredit = 0	;

			academicGraduateYear = AppGlobalValues.REGISTRATION_SEMESTER_GRADUATE_YEAR;

			String[] egbProgram = {};
			String[] courseSystem = new String[2];
			List<Integer> egbProgramInt = new ArrayList<>();
			List<String> compulsoryCourseList = new ArrayList<>();

			CourseEligibleModel courseEligible = null;
			ProgrammeSpecializationCurriculumCreditModel programSpecializationCurriculumCredit = null;

			String[] courseOptionStatusArray = courseRegCommonFn.getCourseOptionStatus(programGroupCode, specCode, studentGraduateYear, 
					academicGraduateYear, semesterId, studyStartYear).split("\\|");
			
			if ((courseOptionStatusArray != null) && (courseOptionStatusArray.length > 0) && (studyStartYear==2025) && programGroupCode.equals("BTECH"))
			{
				//minorFlag = Integer.parseInt(courseOptionStatusArray[4]);
				regularFlag = Integer.parseInt(courseOptionStatusArray[0]);
				
			}
			else if ((courseOptionStatusArray != null) && (courseOptionStatusArray.length > 0))
			{
				regularFlag = Integer.parseInt(courseOptionStatusArray[0]);
				reRegFlag = Integer.parseInt(courseOptionStatusArray[1]);
			}


			String studentDetails = registerNo +" - "+ studentName +" - "+ specCode +" - "+ specDesc +" - "+ programGroupCode;

			//Eligibility Criteria
			courseEligible = courseEligibleRegService.getCourseEligibleByProgGroupId(groupId);
			if (courseEligible != null)
			{
				programEligible = courseEligible.getProgramEligible();
				CGPAEligible = (courseEligible.getProgramCgpa() != null) ? courseEligible.getProgramCgpa() : "";

				egbProgram =  programEligible.split("/");
				for (String e: egbProgram)
				{
					egbProgramInt.add(Integer.parseInt(e));									
				}
			}

			//Getting Curriculum Detail				
			programSpecializationCurriculumCredit = courseRegistrationCommonMongoService.getPrgSpecCurriculumCreditBySpecIdAndAdmissionYear
					(specId, studyStartYear);
			if (programSpecializationCurriculumCredit != null)
			{
				cclVersion = programSpecializationCurriculumCredit.getPscccPkId().getCurriculumVersion();
				cclTotalCredit = programSpecializationCurriculumCredit.getTotalCredits();
				checkCourseSystem = programSpecializationCurriculumCredit.getCourseSystem();
			}

			//Check & Assign the course system
			if (programGroupCode.equals("RP") || programGroupCode.equals("IEP"))
			{
				courseSystem[0] = "FFCS";
				courseSystem[1] = "CAL";

				registrationMethod = "FFCS";
			}
			else
			{
				if ((checkCourseSystem != null) && (!checkCourseSystem.equals("")) 
						&& (!checkCourseSystem.equals("FFCS")) && (!checkCourseSystem.equals("NONFFCS")))
				{
					courseSystem[0] = checkCourseSystem;
					courseSystem[1] = "NONE";

					registrationMethod = "CAL";
				}
				else
				{
					courseSystem[0] = "FFCS";
					courseSystem[1] = "NONE";

					registrationMethod = "FFCS";
				}
			}


			currentDateTimeStr = RegistrationConstants.doGetCurrentDateDispaly(currentDateTime);

			//Cookie assignment
			Cookie cookie = new Cookie("RegisterNumber", registerNo);
			cookie.setSecure(true);
			cookie.setHttpOnly(true);
			cookie.setMaxAge(-1);
			response.addCookie(cookie);

			//Session assignment 10mins
			session.setMaxInactiveInterval(AppGlobalValues.MAX_INACTIVE_SESSION_TIME_OUT);

			if (egbProgramInt!=null && egbProgramInt.size()>0)
			{
				session.setAttribute("EligibleProgramLs", egbProgramInt);									
			}

			session.setAttribute("SemesterSubId", semesterSubId);
			session.setAttribute("SemesterId", semesterId);
			session.setAttribute("SemesterDesc", semesterDesc);
			session.setAttribute("SemesterShortDesc", semesterShortDesc);
			session.setAttribute("registrationMethod", registrationMethod);
			session.setAttribute("minCredit", minCredit);
			session.setAttribute("maxCredit", maxCredit);

			session.setAttribute("classGroupId", classGroupId);
			session.setAttribute("StudySystem", courseSystem);				
			session.setAttribute("EligibleProgram", courseEligible);
			session.setAttribute("CGPAProgram", CGPAEligible);				
			session.setAttribute("curriculumVersion", cclVersion);
			session.setAttribute("cclTotalCredit", cclTotalCredit);
			session.setAttribute("studentDetails", studentDetails);
			session.setAttribute("acadGraduateYear", academicGraduateYear);
			session.setAttribute("regularFlag", regularFlag);
			
			session.setAttribute("minorFlag", minorFlag);
			session.setAttribute("programGroupCode", programGroupCode);

			session.setAttribute("reRegFlag", reRegFlag);
			session.setAttribute("PEUEAllowStatus", AppGlobalValues.CORE_CATEGORY_COURSE_STATUS);
			session.setAttribute("approvalStatus", globalValues.getRegApprovalStatus());
			session.setAttribute("OptionNAStatus", AppGlobalValues.OPTION_NA_STATUS);
			session.setAttribute("compulsoryCourseStatus", globalValues.getCompCourseStatus());
			session.setAttribute("otpStatus", AppGlobalValues.OTP_STATUS);

			session.setAttribute("compulsoryCourseList", compulsoryCourseList);

			session.setAttribute("corAuthStatus", "NONE");
			session.setAttribute("authStatus", "NONE");
			session.setAttribute("CAPTCHA", "");
			session.setAttribute("ENCDATA", "");

			model.addAttribute("studySystem", courseSystem);				
			model.addAttribute("regCredit", regCredit);
			model.addAttribute("regCount", regCount);
			model.addAttribute("wlCount", wlCount);
			model.addAttribute("maxCredit", maxCredit);
			model.addAttribute("wlCredit", wlCredit);
			model.addAttribute("regularFlag", regularFlag);
			model.addAttribute("CurrentDateTime", currentDateTimeStr);	
			model.addAttribute("studentDetails", studentDetails);

			model.addAttribute("startDate", details.getStartTimestamp().toLocalDate().toString());
			model.addAttribute("startTime", details.getStartTimestamp().toLocalTime().toString());
			model.addAttribute("endTime",  details.getEndTimestamp().toLocalTime().toString());

			urlPage = "RegistrationStart";

		}
		catch(Exception exception)
		{
			exception.printStackTrace();


			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationLoginController", 
					"processStudentLogin", registerNo, ipAddress);

			courseRegCommonFn.callCaptcha(request, response, session, model);
			msg = "Invalid Details.";
			urlPage = "StudentLogin";

			return urlPage;
		}

		currentDateTimeStr = RegistrationConstants.doGetCurrentDateDispaly(currentDateTime);
		model.addAttribute("CurrentDateTime", currentDateTimeStr);
		model.addAttribute("info", msg);

		return urlPage;		
	}	
	
	@PostMapping("viewProgressInfo")
	public String viewProgressInfo(Model model, HttpServletRequest request, HttpSession session, 
			HttpServletResponse response) throws ServletException, IOException 
	{
	
		courseRegCommonFn.callCaptcha(request, response, session, model);
		session.setAttribute("CAPTCHA", session.getAttribute("CAPTCHA"));

		return "ConditionProgressPage::test";
	}
	
}