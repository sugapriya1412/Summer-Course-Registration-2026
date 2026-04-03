package org.vtop.CourseRegistration.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.model.CourseCatalogModel;
import org.vtop.CourseRegistration.model.CourseRegistrationModel;
import org.vtop.CourseRegistration.service.CourseCatalogService;
import org.vtop.CourseRegistration.service.CourseEquivalanceRegService;
import org.vtop.CourseRegistration.service.CourseRegCommonService;
import org.vtop.CourseRegistration.service.CourseRegistrationCommonFunction;
import org.vtop.CourseRegistration.service.CourseRegistrationReadWriteService;
import org.vtop.CourseRegistration.service.CourseRegistrationService;
import org.vtop.CourseRegistration.service.ProgrammeSpecializationCurriculumDetailService;


@Controller
public class CourseRegistrationDeleteController 
{
	@Autowired private CourseRegistrationService courseRegistrationService;
	@Autowired private CourseEquivalanceRegService courseEquivalanceRegService;
	@Autowired private CourseRegistrationCommonFunction courseRegCommonFn;
	@Autowired private ProgrammeSpecializationCurriculumDetailService programmeSpecializationCurriculumDetailService;
	@Autowired private CourseCatalogService courseCatalogService;
	@Autowired private CourseRegistrationReadWriteService courseRegistrationReadWriteService;
	@Autowired private CourseRegCommonService courseRegCommonService;
	//@Autowired private CourseRegistrationCommonMongoService courseRegistrationCommonMongoService;

	private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationDeleteController.class);


	@PostMapping("processDeleteCourseRegistration")
	public String processDeleteCourseRegistration(String courseId, Model model, HttpSession session, 
			HttpServletRequest request) 
	{
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try
		{						
			if (registerNumber != null)
			{	
				int delStatusFlag = 2, deleteAllowStatus = 0;
				String  courseAuthStatus = "",deleteMessage="";
				List<CourseRegistrationModel> courseRegistrationModel = new ArrayList<>();
				List<CourseRegistrationModel> courseRegistrationModel3 = new ArrayList<>();

				String[] regStatusArr = new String[5];
				int crCourseStatus = 2;
				String ccCourseSystem = "", crCourseId = "", crCourseCode = "";
				CourseCatalogModel courseCatalog = null;

				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				int programGroupId = (Integer) session.getAttribute("ProgramGroupId");
				String ProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				String programGroupCode = (String) session.getAttribute("ProgramGroupCode");
				String programGroupMode = (String) session.getAttribute("programGroupMode");
				Float CurriculumVersion = (Float) session.getAttribute("curriculumVersion");
				int otpStatus = (Integer) session.getAttribute("otpStatus");
				session.removeAttribute("crCourseIdDelete");
				float minCredit = (float) session.getAttribute("minCredit");
				float maxCredit = (float) session.getAttribute("maxCredit");
				
				@SuppressWarnings("unchecked")
				List<String> compCourseList = (List<String>) session.getAttribute("compulsoryCourseList");			


				courseCatalog = courseCatalogService.getOne(courseId);
				if (courseCatalog != null) 
				{
					ccCourseSystem = courseCatalog.getCourseSystem();

					if ((courseCatalog.getCorequisite() != null) && (!courseCatalog.getCorequisite().equals("")) 
							&& (!courseCatalog.getCorequisite().equals("NONE")) && (!courseCatalog.getCorequisite().equals("NIL")))
					{
						crCourseCode = courseCatalog.getCorequisite().trim();
					}
				}

				if ((!ccCourseSystem.equals("NONFFCS")) && (!ccCourseSystem.equals("FFCS")) 
						&& (!ccCourseSystem.equals("CAL")) && (!crCourseCode.equals("")))
				{						
					courseRegistrationModel3 = courseRegistrationService.getByRegisterNumberCourseCode(semesterSubId, 
							registerNumber, crCourseCode);
					if (!courseRegistrationModel3.isEmpty())
					{
						for (CourseRegistrationModel e : courseRegistrationModel3)
						{
							if (AppGlobalValues.CR_COURSE_OPTION.contains(e.getCourseOptionCode()))
							{
								crCourseId = e.getCourseCatalogModel().getCourseId();
								crCourseStatus = 1;
							}

							break;
						}
					}
				}

				regStatusArr = courseRegCommonFn.checkRegistrationDeleteCondition(semesterSubId, registerNumber, 
						courseId, programGroupId, programGroupCode, programGroupMode, ProgramSpecCode, 
						programSpecId, studyStartYear, CurriculumVersion, compCourseList,crCourseId,session).split("\\|");
				delStatusFlag = Integer.parseInt(regStatusArr[0]);
				deleteMessage = regStatusArr[1];
				courseAuthStatus = regStatusArr[2];

				if ((delStatusFlag == 1) && (crCourseStatus == 1))
				{
					regStatusArr = courseRegCommonFn.checkRegistrationDeleteCondition(semesterSubId, registerNumber, 
							crCourseId, programGroupId, programGroupCode, programGroupMode, ProgramSpecCode, 
							programSpecId, studyStartYear, CurriculumVersion, compCourseList,courseId,session).split("\\|");
					delStatusFlag = Integer.parseInt(regStatusArr[0]);
					deleteMessage = regStatusArr[1];
				}

				if (otpStatus == 1)
				{
					deleteAllowStatus = 1;
				}
				else if (otpStatus == 2)
				{
					courseAuthStatus = courseRegCommonFn.generateCourseAuthKey(registerNumber, courseId, delStatusFlag, 2);
				}

				session.setAttribute("authStatus", courseAuthStatus);

				if (delStatusFlag == 1)
				{							
					courseRegistrationModel.addAll(courseRegistrationService.getByRegisterNumberCourseIdByClassGroupId(
							semesterSubId, registerNumber, courseId, classGroupId));
					if (crCourseStatus == 1)
					{
						courseRegistrationModel.addAll(courseRegistrationService.getByRegisterNumberCourseIdByClassGroupId(
								semesterSubId, registerNumber, crCourseId, classGroupId));
						session.setAttribute("crCourseIdDelete", crCourseId);
					}

					model.addAttribute("courseId", courseId);
					model.addAttribute("courseRegistrationModel", courseRegistrationModel);
					model.addAttribute("msg", deleteMessage);
					model.addAttribute("tlDeleteAllowStatus", deleteAllowStatus);

					urlPage = "mainpages/DeleteConfirmation :: section";
				}
				else
				{
					urlPage = loadProcessDeletion(semesterSubId, registerNumber, classGroupId, model, studyStartYear, 
							programGroupId, programSpecId, CurriculumVersion, deleteMessage, minCredit, maxCredit,session);
				}

			}
			else
			{
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
				return urlPage;
			}
		}
		catch(Exception exception)
		{
			LOGGER.trace(exception);

			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationDeleteController", 
					"processDeleteCourseRegistration", registerNumber, IpAddress);
			urlPage = "redirectpage";
			return urlPage;
		}

		return urlPage;
	}		


	
	public void doGetCreditInformation(HttpSession session, Model model )
	{
		int regCount = 0, ncCount = 0;
		float regCredit = 0, ncCredit = 0;
		String checkCourseId = "";

		List<Object[]> courseRegistrationModel = new ArrayList<>();
		List<Integer> patternIdList = new ArrayList<>();
		List<Object[]> regCreditList = new ArrayList<>();
		List<String> ncCourseList = new ArrayList<>();

		String registerNumber = (String) session.getAttribute("RegisterNumber");
		float minCredit = (float) session.getAttribute("minCredit");
		float maxCredit = (float) session.getAttribute("maxCredit");
		String semesterSubId = (String) session.getAttribute("SemesterSubId");
		int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
		int studyStartYear = (int) session.getAttribute("StudyStartYear");
		float curriculumVersion = (Float) session.getAttribute("curriculumVersion");												


		regCreditList.add(new Object[] {"Minimum", minCredit, "-"});
		regCreditList.add(new Object[] {"Maximum", maxCredit, "-"});

		if ((semesterSubId !=null) &&(registerNumber !=null))
		{
			ncCourseList = programmeSpecializationCurriculumDetailService.getNCCourseByYearAndCCVersion(programSpecId, 
					studyStartYear, curriculumVersion);

			courseRegistrationModel = courseRegistrationService.getByRegisterNumber3(semesterSubId, registerNumber);
			if (!courseRegistrationModel.isEmpty())
			{
				for (Object[] obj : courseRegistrationModel) 
				{

					if ((Integer.parseInt(obj[28].toString()) > 0) && (!patternIdList.contains(Integer.parseInt(obj[27].toString()))))
					{
						patternIdList.add(Integer.parseInt(obj[27].toString()));
					}

					regCredit = regCredit + Float.parseFloat(obj[14].toString());

					if (ncCourseList.contains(obj[3].toString()))
					{
						ncCredit = ncCredit + Float.parseFloat(obj[14].toString());
					}

					if (!obj[2].toString().equals(checkCourseId))
					{
						regCount++;
						checkCourseId = obj[2].toString();

						if (ncCourseList.contains(obj[3].toString()))
						{
							ncCount++;
						}
					}
				}
			}
		}
		regCreditList.add(new Object[] {"Registered (Including Non-Credit Category)", regCredit, regCount});

		if (!ncCourseList.isEmpty())
		{
			regCreditList.add(new Object[] {"Non-Credit Category", ncCredit, ncCount});
		}
		model.addAttribute("regCreditList", regCreditList);
	}
	

	public String loadProcessDeletion(String semesterSubId, String registerNumber,
			String[] classGroupId, Model model,int studyStartYear,int programGroupId, 
			int programSpecId ,float CurriculumVersion,  String message , float minCredit, float maxCredit,HttpSession session)
	{

		List<Object[]> courseRegistrationModel2 =  courseRegistrationService.getByRegisterNumberAndClassGroup(
				semesterSubId, registerNumber, classGroupId);
		
		model.addAttribute("ModifyCourse", courseRegistrationModel2);
		model.addAttribute("blockedCourse", courseRegistrationService.getBlockedCourseIdByRegisterNumberForUpdate(
				semesterSubId, registerNumber));
		
		String studySystem = (String) session.getAttribute("studentStudySystem");

		
		model.addAttribute("curriculumMapList", programmeSpecializationCurriculumDetailService.
				getCurriculumBySpecIdYearAndCCVersionAsMap(programSpecId, studyStartYear, 
						CurriculumVersion,studySystem,registerNumber));
		model.addAttribute("showFlag", 0);
		model.addAttribute("infoMessage", message);
		
	  List<Object[]> regCreditList = 	programmeSpecializationCurriculumDetailService.doGetCreditInformation(model, semesterSubId, registerNumber, minCredit,
				maxCredit, programSpecId, studyStartYear, CurriculumVersion,courseRegistrationModel2);
	  model.addAttribute("regCreditList", regCreditList);
	  
	  
		model.addAttribute("statusMap", CourseRegistrationCommonFunction.doGetRegistrationStatus());

	  
		return "mainpages/ModifySlots::section";
	}
	
	
	@PostMapping("processDeleteCourseRegistrationOTP")
	public String processDeleteCourseRegistrationOTP(String courseId, Model model, HttpSession session, 
			HttpServletRequest request) 
	{
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try
		{	
			if (registerNumber != null)
			{	
				int deleteAllowStatus = 0, redirectFlag = 2, statusFlag = 2;
				String  courseAuthStatus = "", deleteMessage = "", courseCode = "";
				String[] validateStatusArr = new String[]{};
				List<CourseRegistrationModel> courseRegistrationModel = new ArrayList<>();

				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				String semesterDesc = (String) session.getAttribute("SemesterDesc");
				String semesterShortDesc = (String) session.getAttribute("SemesterShortDesc");
				String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				float CurriculumVersion = (Float) session.getAttribute("curriculumVersion");
				String studentEMailId = (String) session.getAttribute("studentEMailId");
				int otpStatus = (Integer) session.getAttribute("otpStatus");
				String crCourseId = (String) session.getAttribute("crCourseIdDelete");

				float minCredit = (float) session.getAttribute("minCredit");
				float maxCredit = (float) session.getAttribute("maxCredit");
				
				String authStatus = (String) session.getAttribute("authStatus");
				int authCheckStatus = courseRegCommonFn.validateCourseAuthKey(authStatus, registerNumber, 
						courseId, 1);

				if ((authCheckStatus == 1) && (otpStatus == 1))
				{	
					CourseCatalogModel courseCatalogModel = courseCatalogService.getOne(courseId);
					if (courseCatalogModel != null)
					{
						courseCode = courseCatalogModel.getCode();
					}

					validateStatusArr = courseRegistrationReadWriteService.validateCourseAndSendOTP(semesterSubId, semesterDesc, 
							semesterShortDesc, registerNumber, courseId, courseCode, studentEMailId, IpAddress, 
							"DELETE",session).split("\\|");
					statusFlag = Integer.parseInt(validateStatusArr[0]);
					courseAuthStatus = validateStatusArr[1];
					deleteMessage = validateStatusArr[3];
					LOGGER.trace("\n statusFlag: "+ statusFlag +" | courseAuthStatus: "+ courseAuthStatus 
							+" | deleteMessage: "+ deleteMessage); 

					if (statusFlag == 1)
					{
						deleteAllowStatus = 2;
						redirectFlag = 1;
					}
				}
				else
				{
					deleteMessage = "Invalid course...!";
				}						

				if (redirectFlag == 1)
				{
					courseRegistrationModel.addAll(courseRegistrationService.getByRegisterNumberCourseIdByClassGroupId(
							semesterSubId, registerNumber, courseId, classGroupId));
					if ((crCourseId != null) && (!crCourseId.equals("")))
					{
						courseRegistrationModel.addAll(courseRegistrationService.getByRegisterNumberCourseIdByClassGroupId(
								semesterSubId, registerNumber, crCourseId, classGroupId));
					}

					session.setAttribute("authStatus", courseAuthStatus);

					model.addAttribute("courseId", courseId);
					model.addAttribute("courseRegistrationModel", courseRegistrationModel);
					model.addAttribute("msg", deleteMessage);
					model.addAttribute("tlDeleteAllowStatus", deleteAllowStatus);

					urlPage = "mainpages/DeleteConfirmation :: section";
				}
				else
				{
					urlPage = loadProcessDeletion(semesterSubId, registerNumber, classGroupId, model, studyStartYear,
							authCheckStatus, programSpecId, CurriculumVersion, deleteMessage, minCredit,maxCredit,session);
				}

			}
			else
			{
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
				return urlPage;
			}
		}
		catch(Exception exception)
		{
			LOGGER.trace(exception);

			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationDeleteController", 
					"processDeleteCourseRegistrationOTP", registerNumber, IpAddress);
			urlPage = "redirectpage";
			return urlPage;
		}		
		return urlPage;
	}

	@PostMapping("processDeleteConfirmationCourseRegistration")
	public String processDeleteConfirmationCourseRegistration(String courseId, Model model, HttpSession session, 
			HttpServletRequest request) 
	{
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try
		{	
			if (registerNumber != null)
			{
				String  message = null;
				int redirectFlag = 2, statusFlag = 2;
				String oldCourseId = "", pDelStatus = "", mailOTP = "", courseCode = "";
				String[] validateStatusArr = new String[]{};

				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				String semesterDesc = (String) session.getAttribute("SemesterDesc");
				String semesterShortDesc = (String) session.getAttribute("SemesterShortDesc");
				String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				float curriculumVersion = (Float) session.getAttribute("curriculumVersion");								
				int otpStatus = (Integer) session.getAttribute("otpStatus");
				String crCourseId = (String) session.getAttribute("crCourseIdDelete");
				float minCredit = (float) session.getAttribute("minCredit");
				float maxCredit = (float) session.getAttribute("maxCredit");
				
				mailOTP = request.getParameter("mailOTP");
				if ((mailOTP != null) && (!mailOTP.equals("")))
				{
					mailOTP = mailOTP.trim();
				}
				else
				{
					mailOTP = "NONE";
				}

				List<String> courseIdList = new ArrayList<>();
				List<CourseRegistrationModel> courseRegistrationModel2 = new ArrayList<>();

				String authStatus = (String) session.getAttribute("authStatus");
				int authCheckStatus = courseRegCommonFn.validateCourseAuthKey(authStatus, registerNumber, courseId, 2);

				if(authCheckStatus == 1)
				{
					if (otpStatus == 1)
					{
						CourseCatalogModel courseCatalogModel = courseCatalogService.getOne(courseId);
						if (courseCatalogModel != null)
						{
							courseCode = courseCatalogModel.getCode();
						}

						validateStatusArr = courseRegistrationReadWriteService.validateCourseAndOTP(semesterSubId, 
								semesterDesc, semesterShortDesc, registerNumber, courseId, 
								courseCode, mailOTP, IpAddress, "DELETE",session).split("\\|");
						statusFlag = Integer.parseInt(validateStatusArr[0]);
						redirectFlag = Integer.parseInt(validateStatusArr[1]);

						if(validateStatusArr[2].toString().equals("SUCCESS"))
						{
							message = "Registered Course(s) Successfully Deleted.";
						}
						else
						{
							message = validateStatusArr[2].toString();
						}
					}
					else if (otpStatus == 2)
					{
						statusFlag = 1;
						redirectFlag = 2;
					}
					LOGGER.trace("\n statusFlag: "+ statusFlag +" | redirectFlag: "+ redirectFlag 
							+" | message: "+ message); 

					oldCourseId = courseEquivalanceRegService.getEquivCourseByRegisterNumberAndCourseId(
							semesterSubId, registerNumber, courseId);
					if ((oldCourseId == null) || (oldCourseId.equals(null)))
					{
						oldCourseId = "";
					}

					if (statusFlag == 1)
					{
						courseIdList.add(courseId);
						if ((crCourseId != null) && (!crCourseId.equals("")))
						{
							courseIdList.add(crCourseId);
						}

						synchronized (this)
						{
							for (String crsId : courseIdList)
							{
								pDelStatus = courseRegistrationReadWriteService.courseRegistrationDelete(semesterSubId, 
										registerNumber, crsId, "DELETE", registerNumber, IpAddress, "GEN", oldCourseId);
								if (pDelStatus.equals("SUCCESS"))
								{
									String optedCBCSMinor = (String) session.getAttribute("optedCBCSMinor");
									String optedCBCSHonour = (String) session.getAttribute("optedCBCSHonour");
																		
									List<String> courseCodeList = new ArrayList<>();
									
									if(optedCBCSMinor!=null && !optedCBCSMinor.isEmpty())
									{
										courseCodeList = courseRegistrationService.getCourseCodeByAddiLearnCode(optedCBCSMinor);
										int minCount = courseRegistrationService.getCourseCountByRegisterNumberAndCourseOptionAndCourseCode(
												registerNumber, "MIN", courseCodeList);
										if(minCount==0)
										{
											courseRegCommonService.deleteAddlOpted(registerNumber, optedCBCSMinor,session);
											session.removeAttribute("optedCBCSMinor");
										}
									}
									
									if(optedCBCSHonour!=null && !optedCBCSHonour.isEmpty())
									{
										courseCodeList = courseRegistrationService.getCourseCodeByAddiLearnCode(optedCBCSHonour);

										int minCount = courseRegistrationService.getCourseCountByRegisterNumberAndCourseOptionAndCourseCode(
												registerNumber, "HON", courseCodeList);
										if(minCount==0)
										{
											courseRegCommonService.deleteAddlOpted(registerNumber, optedCBCSHonour,session);
											session.removeAttribute("optedCBCSHonour");
										}
									}
									courseRegistrationReadWriteService.projectRegDeleteByRegisterNumberAndCourseId(semesterSubId, 
											registerNumber, crsId);
									message = "Selected course successfully deleted.";
								}
								else if ((pDelStatus.equals("FAIL")) || (pDelStatus.substring(0, 5).equals("error")))
								{
									message = "Technical error.";
								}
								else
								{
									message = pDelStatus;
								}
							}
						}
					}
				}
				else
				{
					message = "Not a valid course to delete.";
				}	

				if (redirectFlag == 1)
				{	
					courseRegistrationModel2.addAll(courseRegistrationService.getByRegisterNumberCourseIdByClassGroupId(
							semesterSubId, registerNumber, courseId, classGroupId));
					if ((crCourseId != null) && (!crCourseId.equals("")))
					{
						courseRegistrationModel2.addAll(courseRegistrationService.getByRegisterNumberCourseIdByClassGroupId(
								semesterSubId, registerNumber, crCourseId, classGroupId));
					}

					model.addAttribute("courseId", courseId);
					model.addAttribute("courseRegistrationModel", courseRegistrationModel2);
					model.addAttribute("infoMessage", message);
					model.addAttribute("tlDeleteAllowStatus", 2);

					urlPage = "mainpages/DeleteConfirmation :: section";
				}
				else
				{

					urlPage = loadProcessDeletion(semesterSubId, registerNumber, classGroupId, model, studyStartYear,
							authCheckStatus, programSpecId, curriculumVersion, message, minCredit, maxCredit,session);
				}

			}
			else
			{
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
				return urlPage;
			}
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
			LOGGER.trace(exception);

			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationDeleteController", 
					"processDeleteConfirmationCourseRegistration", registerNumber, IpAddress);
			urlPage = "redirectpage";
			return urlPage;
		}

		return urlPage;
	}


	@PostMapping("processDeleteConfirmationCourseRegistrationRirect")
	public String processDeleteConfirmationCourseRegistrationRirect(Model model, HttpSession session, 
			HttpServletRequest request) 
	{		
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try
		{	
			if (registerNumber != null)
			{	
				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
				Integer programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				float curriculumVersion = (Float) session.getAttribute("curriculumVersion");				
				int programGroupId = (Integer) session.getAttribute("ProgramGroupId");
				float minCredit = (float) session.getAttribute("minCredit");
				float maxCredit = (float) session.getAttribute("maxCredit");
				
				urlPage =loadProcessDeletion(semesterSubId,  registerNumber,
						 classGroupId,  model, studyStartYear, programGroupId, 
						 programSpecId , curriculumVersion,  null, minCredit, maxCredit,session  );
			}
			else
			{
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
				return urlPage;
			}
		}
		catch(Exception exception)
		{
			LOGGER.trace(exception);

			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationDeleteController", 
					"processDeleteConfirmationCourseRegistrationRirect", registerNumber, IpAddress);
			urlPage = "redirectpage";
			return urlPage;
		}
		return urlPage;			
	}


	@PostMapping("deleteRegisteredCourse")
	public String deleteRegisteredCourse(Model model, HttpSession session, HttpServletRequest request) 
	{	
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try
		{	
			if (registerNumber != null)
			{	
				String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				float curriculumVersion = (Float) session.getAttribute("curriculumVersion");

				List<Object[]> courseRegistrationModel = new ArrayList<>();

				courseRegistrationModel = courseRegistrationService.getByRegisterNumberAndClassGroup(
						semesterSubId, registerNumber, classGroupId);

				model.addAttribute("courseRegistrationModel", courseRegistrationModel);
				model.addAttribute("blockedCourse", courseRegistrationService.getBlockedCourseIdByRegisterNumberForDelete(
						semesterSubId, registerNumber));
				String studySystem = (String) session.getAttribute("studentStudySystem");

				model.addAttribute("curriculumMapList", programmeSpecializationCurriculumDetailService.
						getCurriculumBySpecIdYearAndCCVersionAsMap(programSpecId, studyStartYear, curriculumVersion,studySystem,registerNumber));
				model.addAttribute("showFlag", 0);
				urlPage = "mainpages/DeleteCourse::section";

			}
			else
			{
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
				return urlPage;
			}
		}
		catch(Exception exception)
		{
			LOGGER.trace(exception);

			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationDeleteController", 
					"deleteRegisteredCourse", registerNumber, IpAddress);
			urlPage = "redirectpage";
			return urlPage;
		}

		return urlPage;
	}

}
