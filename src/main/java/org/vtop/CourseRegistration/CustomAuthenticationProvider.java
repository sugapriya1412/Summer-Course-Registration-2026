package org.vtop.CourseRegistration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.vtop.CourseRegistration.model.StudentInformation;
import org.vtop.CourseRegistration.service.*;


public class CustomAuthenticationProvider extends DaoAuthenticationProvider
{
	@Autowired private HttpSession session;
	@Autowired HttpServletRequest request;
	@Autowired private SemesterMasterService semesterMasterService;

	@Autowired private StudentHistoryService histService;

	@Autowired 
	private CourseRegistrationReadWriteService courseRegistrationReadWriteService;	

	@Autowired
	private CourseRegistrationCommonFunction commonFunction;
	
	@Autowired
	private ProgrammeSpecializationCurriculumCreditService programmeSpecializationCurriculumCreditService;

	@Autowired 
	private CourseRegistrationService regService;

	@Autowired
	private CourseRegCommonService commonService;
	

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");	


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException
	{		
		String userId = authentication.getName().toUpperCase().trim();
		String passwordInput = authentication.getCredentials().toString().trim();
		String captchaInput = request.getParameter("captchaString")!=null ? request.getParameter("captchaString").trim() : "" ;

		//For getting captcha from session attribute					
		String sessioncaptchaString = (String) session.getAttribute("CAPTCHA");

		//Assigning IP address
		String ipAddress = request.getRemoteAddr();
		try
		{


			if (request != null) {
				ipAddress = request.getHeader("X-FORWARDED-FOR");
				if (ipAddress == null || "".equals(ipAddress)) {
					ipAddress = request.getRemoteAddr();
				}
			}
			

			validateCaptch(captchaInput, sessioncaptchaString,ipAddress);

			//validateCaptchLoadTest(captchaInput );
			
			validateCredential(userId, passwordInput, ipAddress,session);
			
			String userIdAuth = (String) session.getAttribute("userId");

			StudentInformation details =null;
			
			if(AppGlobalValues.ACTIVITY_EVENT.equals("REGISTRATION"))
			{
				details = commonFunction.getCourseRegistrationDetailByRegisterNumber(userIdAuth);
				session.setAttribute("endDateTime", details.getEndTimestamp());
			}
			else if(AppGlobalValues.ACTIVITY_EVENT.equals("ADDDROP"))
			{
				details = commonFunction.getAddDropCourseRegistrationStudentDetailByRegisterNumber(userIdAuth);
				session.setAttribute("endDateTime", details.getEndTimestamp());

			}
			else if(AppGlobalValues.ACTIVITY_EVENT.equals("MOCK_COURSE_REGISTRATION"))
			{
				details = commonFunction.getMockCourseRegistrationDetailByRegisterNumber(userIdAuth);
				session.setAttribute("endDateTime", details.getEndTimestamp());
			}
			else
			{
				throw new BadCredentialsException("No Activity found");
			}
					
			
			List<String> allowedEduStatus = Arrays.asList("AT","RT","TO","AM","BS","HFD","FD","SP");
					

			if(details!=null)
			{
				if(!details.getMessage().equals("SUCCESS"))
				{
					throw new BadCredentialsException(details.getMessage());
				}
				else
				{

					if(!allowedEduStatus.contains(details.getEducationStatus()))
					{
						String errorMessage = null;
						String office = Optional
								.ofNullable(regService.getLockStatusDescription(details.getRegisterNumber()))
								.orElse("");
						if (!office.isEmpty())
						{
							errorMessage = "Your Account is Locked.".concat("Contact ")
									.concat(office).concat(" Office.");

						}

						if(errorMessage!=null && !errorMessage.isEmpty())
						{
							throw new BadCredentialsException(errorMessage);
						}
					}
					else
					{
						session.setAttribute("sInfo", details);
						setStudentInformation(details, ipAddress);

						commonService.writeToLog(userId,ipAddress,"LOGIN");


					}					
				}
			}


			session.setAttribute("scheduleLockStatus", 0);




			return new UsernamePasswordAuthenticationToken(userId, passwordInput, 
					AuthorityUtils.NO_AUTHORITIES);

		}
		catch (BadCredentialsException e) {
			throw new BadCredentialsException(e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			courseRegistrationReadWriteService.addErrorLog(e, 
					AppGlobalValues.REG_ERROR_METHOD +"CustomAuthenticationProvider", 
					"authenticate", userId, ipAddress);
			throw new BadCredentialsException("Unable to login. Kindly try again later.");
		}

	}

	private void setStudentInformation(StudentInformation studentDetail, String ipAddress) throws ParseException
	{

		int  programDuration = 0,  studyStartYear = 0, studentGraduateYear = 0, 
				studentWishListStatus = 2;
		String   studEMailId =AppGlobalValues.TESTING_MAIL_ID,studentCgpaData="";

		programDuration = studentDetail.getProgGroupDuration();
		studyStartYear = studentDetail.getAdmissionYear();
		studentGraduateYear = studyStartYear + programDuration;

		List<Object[]> studentHistory = histService.getStaticStudentCGPAFromTable(studentDetail.getRegisterNumber());


		if(studentHistory!=null && !studentHistory.isEmpty())
		{
			studentCgpaData = Float.parseFloat(studentHistory.get(0)[0].toString()) +"|"+ Float.parseFloat(studentHistory.get(0)[1].toString()) 
			+"|"+ Float.parseFloat(studentHistory.get(0)[2].toString());
		}
		else
		{
			studentCgpaData = Float.parseFloat("0") +"|"+ Float.parseFloat("0") 
			+"|"+ Float.parseFloat("0");
		}


		studentWishListStatus = (studentDetail.getWishlistStatus() == null) ? 2 : studentDetail.getWishlistStatus();

		if (AppGlobalValues.TEST_STATUS != 2)
		{
			studEMailId = (studentDetail.getEmail() == null) ? AppGlobalValues.TESTING_MAIL_ID : studentDetail.getEmail();
		}

		//Session Assignment
		session.setAttribute("RegisterNumber", studentDetail.getRegisterNumber());
		session.setAttribute("studentName", studentDetail.getStudentName());
		session.setAttribute("ProgramSpecId", studentDetail.getProgSpecializationId());
		session.setAttribute("ProgramSpecCode", studentDetail.getProgSpecializationCode());
		session.setAttribute("ProgramSpecDesc", studentDetail.getProgSpecializationDescription());
		session.setAttribute("ProgramGroupId", studentDetail.getProgGroupId());
		session.setAttribute("ProgramGroupCode", studentDetail.getProgGroupCode());				
		session.setAttribute("StudyStartYear", studyStartYear);
		session.setAttribute("StudentGraduateYear", studentGraduateYear);
		session.setAttribute("studentStudySystem", studentDetail.getStudySystem());				
		session.setAttribute("programGroupMode", studentDetail.getProgGroupMode());
		session.setAttribute("studentEMailId", studEMailId);
		session.setAttribute("costCentreCode", studentDetail.getCentreCode());
		session.setAttribute("costCenterId", studentDetail.getCentreId());
		session.setAttribute("studentCategory", studentDetail.getFeeCategoryDescription());

		session.setAttribute("exemptionStatus", 0);
		session.setAttribute("graduationStatus", studentDetail.getExamGraduationStatus());
		session.setAttribute("studentCgpaData", studentCgpaData);
		session.setAttribute("studentWishListStatus", studentWishListStatus);
		session.setAttribute("slotEndTime", studentDetail.getEndTimestamp().toLocalTime().toString());

		session.setAttribute("testStatus", AppGlobalValues.TEST_STATUS);

		session.setAttribute("IpAddress", ipAddress);
		session.setAttribute("CAPTCHA", "");
		session.setAttribute("ENCDATA", "");

		session.setAttribute("optedCBCSMinor", studentDetail.getOptedCBCSMinor());
		session.setAttribute("optedCBCSHonour", studentDetail.getOptedCBCSHonour());
		
		
		List<Object[]> doGetCurriculumCredits =  programmeSpecializationCurriculumCreditService.getMaxVerDetailBySpecIdAndAdmYear
		(studentDetail.getProgSpecializationId(), studyStartYear);
		
		if(doGetCurriculumCredits!=null && !doGetCurriculumCredits.isEmpty())
		{
			session.setAttribute("displaySdyCurSystem","(Study System : "+studentDetail.getStudySystem().concat(" -  Course System : ")
					.concat(doGetCurriculumCredits.get(0)[8].toString()).concat(")"));
			
			session.setAttribute("currCourseSystem",doGetCurriculumCredits.get(0)[8].toString());
		}
		else
		{
			session.setAttribute("displaySdyCurSystem","(Study System : "+studentDetail.getStudySystem().concat(" - Course System : NA").concat(")"));
			session.setAttribute("currCourseSystem","NA");

		}
		
		
		
	}


	private void validateCaptchLoadTest(String captchaInput)
	{
		if ((captchaInput == null) || (captchaInput.equals("")) || (captchaInput.length() != 6))
		{	
			throw new BadCredentialsException("Captcha is mandatory");
		}
		
	}


	private void validateCaptch(String captchaInput, String sessioncaptchaString,String ipAddress)
	{
		if ((captchaInput == null) || (captchaInput.equals("")) || (captchaInput.length() != 6))
		{	
			throw new BadCredentialsException("Captcha is mandatory");
		}
		else
		{
			if (AppGlobalValues.TEST_STATUS != 2 && !captchaInput.equals(sessioncaptchaString) 
					&& !AppGlobalValues.ADMIN_IP_ADDRESS.contains(ipAddress))
			{
				throw new BadCredentialsException("Invalid Captcha");
			}
		}
	}


	private void validateCredential(String userId, String passwordInput, String ipAddress,HttpSession session)
	{
		//if(ipAddress.startsWith("****") || ipAddress.startsWith("********"))
		//{
		
			if(passwordInput==null || passwordInput.isEmpty())
			{
				throw new BadCredentialsException("Password is mandatory");
			}
			else
			{
				semesterMasterService.getUserLoginValidation(userId, passwordInput, AppGlobalValues.TEST_STATUS,session,ipAddress);
			}
		/*}
		else
		{
			throw new BadCredentialsException("Not Permitted IP  : "+ipAddress);
		}*/

	}

	
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}





}



