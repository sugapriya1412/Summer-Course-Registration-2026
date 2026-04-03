package org.vtop.CourseRegistration.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.Dto.ProgramSpecializationCurriculumDetailDto;
import org.vtop.CourseRegistration.model.CourseAllocationModel;
import org.vtop.CourseRegistration.model.CourseCatalogModel;
import org.vtop.CourseRegistration.model.CourseRegistrationModel;
import org.vtop.CourseRegistration.model.CourseRegistrationPKModel;
import org.vtop.CourseRegistration.service.CourseAllocationService;
import org.vtop.CourseRegistration.service.CourseCatalogService;
import org.vtop.CourseRegistration.service.CourseRegCommonService;
import org.vtop.CourseRegistration.service.CourseRegistrationCommonFunction;
import org.vtop.CourseRegistration.service.CourseRegistrationReadWriteService;
import org.vtop.CourseRegistration.service.CourseRegistrationService;
import org.vtop.CourseRegistration.service.ProgrammeSpecializationCurriculumDetailService;


@Controller
public class CourseRegistrationEditController
{	
	@Autowired private CourseCatalogService courseCatalogService;
	@Autowired private CourseAllocationService courseAllocationService;
	@Autowired private CourseRegistrationService courseRegistrationService;
	@Autowired private CourseRegistrationCommonFunction courseRegCommonFn;
	@Autowired private ProgrammeSpecializationCurriculumDetailService programmeSpecializationCurriculumDetailService;
	@Autowired private CourseRegistrationReadWriteService courseRegistrationReadWriteService;
	@Autowired private CourseRegCommonService crMongoService;

	private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationEditController.class);


	@PostMapping("modifySlots")
	public String modifySlots(Model model, HttpSession session, HttpServletRequest request) 
	{	
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";
		

		try
		{
			if (registerNumber != null)
			{	
				String studySystem = (String) session.getAttribute("studentStudySystem");


				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				float curriculumVersion = (Float) session.getAttribute("curriculumVersion");

				float minCredit = (float) session.getAttribute("minCredit");
				float maxCredit = (float) session.getAttribute("maxCredit");

				//2
				List<Object[]> courseRegistrationModel = courseRegistrationService.getByRegisterNumberAndClassGroup(
						semesterSubId, registerNumber, classGroupId);

				model.addAttribute("ModifyCourse", courseRegistrationModel);
				model.addAttribute("blockedCourse", courseRegistrationService.getBlockedCourseIdByRegisterNumberForUpdate(
						semesterSubId, registerNumber));
				model.addAttribute("curriculumMapList", programmeSpecializationCurriculumDetailService.
						getCurriculumBySpecIdYearAndCCVersionAsMap(programSpecId, studyStartYear, curriculumVersion,studySystem,registerNumber));
				//1
				List<Object[]> regCreditList =  programmeSpecializationCurriculumDetailService.doGetCreditInformation(model, semesterSubId, registerNumber,
						minCredit, maxCredit, programSpecId, studyStartYear, curriculumVersion,courseRegistrationModel);
				model.addAttribute("regCreditList", regCreditList);


				model.addAttribute("statusMap", CourseRegistrationCommonFunction.doGetRegistrationStatus());


				urlPage = "mainpages/ModifySlots::section";
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
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationEditController", 
					"modifySlots", registerNumber, IpAddress);
			urlPage = "redirectpage";

			return urlPage;
		}

		return urlPage;
	}



	@PostMapping("getCorrespondingLabSlots")
	public String getCorrespondingLabSlots(String courseId, String erpId, String genericCourseType, String classId, Model model, HttpSession session, HttpServletRequest request) 
	{

		String crsId =  (String) session.getAttribute("orCourseId");

		editRegisteredSlots(crsId, model, session, request);

		CourseCatalogModel courseCatalog = courseCatalogService.getOne(courseId);
		List<CourseAllocationModel> ela = ((session.getAttribute("camList2") != null) && (!session.getAttribute("camList2").equals(""))) 
				? (List<CourseAllocationModel>) session.getAttribute("camList2") 
						: new ArrayList<CourseAllocationModel>();
		model.addAttribute("cam2", courseAllocationService.getAllocationByEmployeeId(ela, erpId, courseCatalog.getCourseSystem(), classId));

		model.addAttribute("tlClassId", classId);

		return "mainpages/ModifyEdit :: section";
	}



	@PostMapping("editRegisteredSlots")
	public String editRegisteredSlots(String courseId, Model model, HttpSession session, HttpServletRequest request) 
	{
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try
		{
			if (registerNumber != null)
			{
				int statusFlag = 2;
				String msg = null, classId = "", courseId2 = "", courseType = "", 
						courseAuthStatus = "", oldErpId = "", oldSlot = "", courseCode = "", courseTitle = "";
				String[] cId = new String[]{};
				String[] validateStatusArr = new String[]{};

				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				String semesterDesc = (String) session.getAttribute("SemesterDesc");
				String semesterShortDesc = (String) session.getAttribute("SemesterShortDesc");
				String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
				String programGroupCode = (String) session.getAttribute("ProgramGroupCode");
				String ProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
				String costCentreCode = (String) session.getAttribute("costCentreCode");
				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				float curriculumVersion = (Float) session.getAttribute("curriculumVersion");
				String studentEMailId = (String) session.getAttribute("studentEMailId");
				int otpStatus = (Integer) session.getAttribute("otpStatus");

				String registrationOption = (String) session.getAttribute("registrationOption");

				CourseCatalogModel courseCatalogModel = null;
				List<CourseAllocationModel> courseAllocationList = new ArrayList<>();
				List<Object[]> objectList = new ArrayList<>();
				List<String> classIdList = new ArrayList<>();

				String genericCourseType = "", courseOption = "", ccCourseSystem = "", crCourseCode = "";
				List<CourseRegistrationModel> courseRegistrationModel3 = new ArrayList<>();

				session.setAttribute("orCourseId", courseId);
				

				cId = courseId.split("/");
				classId = cId[0];
				courseId2 = cId[1];
				courseType = cId[2];

				courseCatalogModel = courseCatalogService.getOne(courseId2);
				if (courseCatalogModel != null)
				{
					courseCode = courseCatalogModel.getCode();
					courseTitle = courseCatalogModel.getTitle();
					genericCourseType = courseCatalogModel.getGenericCourseType();
					ccCourseSystem = courseCatalogModel.getCourseSystem();

					if ((courseCatalogModel.getCorequisite() != null) && (!courseCatalogModel.getCorequisite().equals("")) 
							&& (!courseCatalogModel.getCorequisite().equals("NONE")) && (!courseCatalogModel.getCorequisite().equals("NIL")))
					{
						crCourseCode = courseCatalogModel.getCorequisite().trim();
					}
				}

				CourseRegistrationPKModel courseRegistrationPKModel = new CourseRegistrationPKModel();
				CourseRegistrationModel courseRegistrationModel = new CourseRegistrationModel();

				courseRegistrationPKModel.setSemesterSubId(semesterSubId);
				courseRegistrationPKModel.setRegisterNumber(registerNumber);
				courseRegistrationPKModel.setCourseId(courseId2);
				courseRegistrationPKModel.setCourseType(courseType);						
				courseRegistrationModel = courseRegistrationService.getOne(courseRegistrationPKModel);
				if (courseRegistrationModel != null)
				{
					oldErpId = courseRegistrationModel.getCourseAllocationModel().getErpId();
					oldSlot = courseRegistrationModel.getCourseAllocationModel().getTimeTableModel().getSlotName();
					courseOption = courseRegistrationModel.getCourseOptionCode();
					statusFlag = 1;
				}
				else
				{
					msg = "You are not authorized to modify "+ courseCode +" - "+ courseTitle;
				}

				List<CourseAllocationModel> ela= new ArrayList<>();
				List<CourseAllocationModel> list1= new ArrayList<>();

				Map<String,String> returnMap  = null;
				List<String> compCourseIdList = (List<String>) session.getAttribute("compulsoryCourseList");


				if (statusFlag == 1)
				{
					statusFlag = 2;

					if (courseOption.equals("RGR") && (!ccCourseSystem.equals("NONFFCS")) 
							&& (!ccCourseSystem.equals("FFCS")) && (!ccCourseSystem.equals("CAL")))
					{	
						 if ((genericCourseType.equals("TH") || genericCourseType.equals("LO")) 
								&& (!crCourseCode.equals("")))
						{
							courseRegistrationModel3 = courseRegistrationService.getByRegisterNumberCourseCode(semesterSubId, 
									registerNumber, crCourseCode);
							if (!courseRegistrationModel3.isEmpty())
							{
								for (CourseRegistrationModel e : courseRegistrationModel3)
								{
									if (e.getCourseOptionCode().equals("RGR"))
									{
										msg = "Selected "+ courseCode +" - "+ courseTitle +" co-requisite course is not allowed to modify. Please delete and enroll again";
									}
									else
									{
										statusFlag = 1;
									}

									break;
								}
							}
							else
							{
								statusFlag = 1;
							}
						}
						else
						{
							statusFlag = 1;
						}
					}
					else
					{
						statusFlag = 1;
					}
				}

				if (statusFlag == 1 && msg==null)
				{
					statusFlag = 2;

					if (courseType.equals("ETH") || courseType.equals("ELA")) 
					{
						CourseCatalogModel courseCatalog =  courseCatalogService.getOne(courseId2);
						if(compCourseIdList.contains(courseCatalog.getCode()))
						{
							
							
							returnMap  = courseRegistrationService.doGetRegClassIdsELAETH(semesterSubId, courseId2, registerNumber);


							ela = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
									classGroupId, AppGlobalValues.CLASS_TYPE, courseId2, "ELA", programGroupCode, 
									ProgramSpecCode, costCentreCode,registrationOption,registerNumber);
							model.addAttribute("cam2", courseAllocationService.getAllocationByEmployeeId(ela, oldErpId, ccCourseSystem, returnMap.get("ETH")));

							
							
							session.setAttribute("camList2", ela);

							list1 = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
									classGroupId, AppGlobalValues.CLASS_TYPE, courseId2, "ETH", programGroupCode, 
									ProgramSpecCode, costCentreCode,registrationOption,registerNumber);
							model.addAttribute("cam", list1);

							model.addAttribute("tlClassId", returnMap.get("ETH"));
							model.addAttribute("llClassId", returnMap.get("ELA"));

							session.setAttribute("tlClassId", returnMap.get("ETH"));
							session.setAttribute("llClassId", returnMap.get("ELA"));
						}
						else
						{
							msg = "Selected "+ courseCode +" - "+ courseTitle +" course is not allowed to modify. Please delete and enroll again";

						}
					} 
					else 
					{
						courseAllocationList = courseAllocationService.getCourseAllocationCourseIdTypeList(
								semesterSubId, classGroupId, AppGlobalValues.CLASS_TYPE, 
								courseId2, courseType, programGroupCode, ProgramSpecCode, 
								costCentreCode,registrationOption,registerNumber);
					}


					if((returnMap==null || returnMap.get("ELA")==null)   && msg==null)
					{

						courseAllocationList = courseAllocationService.getCourseAllocationCourseIdTypeList(
								semesterSubId, classGroupId, AppGlobalValues.CLASS_TYPE, 
								courseId2, courseType, programGroupCode, ProgramSpecCode, 
								costCentreCode,registrationOption,registerNumber);

						if (!courseAllocationList.isEmpty())
						{
							if (courseAllocationList.size() > 1)
							{
								classIdList = courseAllocationList.stream()
										.filter(e -> e.getAvailableSeats() > 0)
										.map(e -> e.getClassId()).collect(Collectors.toList());
								LOGGER.trace("\n classIdList size: "+ classIdList.size());
								if (!classIdList.isEmpty())
								{
									statusFlag = 1;
								}
								else
								{
									if (courseType.equals("ETH") || courseType.equals("ELA")) 
									{
										msg = "Based on registered embedded theory/lab faculty class(s), seats are full for the selected course "+ 
												courseCode +" - "+ courseTitle +".  So not allowed to modify.";
									}
									else
									{
										msg = "Seats are full for the selected course "+ courseCode +" - "+ courseTitle +", so not allowed to modify.";
									}
								}
							}
							else
							{
								if (courseType.equals("ETH") || courseType.equals("ELA")) 
								{
									msg = "No more eligible slots are available. Not allowed to modify.";
								}
								else
								{
									msg = "Only registered class is available for the selected course "+ courseCode +" - "+ courseTitle 
											+", so not allowed to modify.";
								}
							}
						}
						else
						{
							msg = "Course allocation did not exist for the selected course "+ courseCode +" - "+ courseTitle +" to modify.";
						}
					}
					else
					{
						statusFlag = 1;
					}
				}

				if (statusFlag == 1  && msg==null)
				{
					statusFlag = 2;

					if (otpStatus == 1)
					{								
						validateStatusArr = courseRegistrationReadWriteService.validateCourseAndSendOTP(semesterSubId, semesterDesc, 
								semesterShortDesc, registerNumber, courseId2, courseCode, studentEMailId, IpAddress, 
								"MODIFY",session).split("\\|");
						statusFlag = Integer.parseInt(validateStatusArr[0]);
						courseAuthStatus = validateStatusArr[1];
						//msg = validateStatusArr[3];
					}
					else if (otpStatus == 2)
					{
						statusFlag = 1;
						courseAuthStatus = courseRegCommonFn.generateCourseAuthKey(registerNumber, courseId2, statusFlag, 2);
						//msg = "SUCCESS";
					}
				}

				if (statusFlag == 1  && msg==null)
				{							
					session.setAttribute("authStatus", courseAuthStatus);

					model.addAttribute("courseCatalogModel", courseCatalogModel);
					model.addAttribute("oldClassId", classId);
					model.addAttribute("oldCourseId", courseId2);
					model.addAttribute("oldCourseType", courseType);
					model.addAttribute("courseAllocationList", courseAllocationList);

					//	courseRegistrationFormController.callSlotInformation(model, semesterSubId, registerNumber, courseAllocationList);

					model.addAttribute("oldSlot", oldSlot);
					model.addAttribute("otpAllowStatus", otpStatus);

					urlPage = "mainpages/ModifyEdit :: section";
				}
				else
				{
					objectList = courseRegistrationService.getByRegisterNumberAndClassGroup(semesterSubId, registerNumber, classGroupId);

					model.addAttribute("ModifyCourse", objectList);
					model.addAttribute("blockedCourse", courseRegistrationService.getBlockedCourseIdByRegisterNumberForUpdate(
							semesterSubId, registerNumber));
					
					String studySystem = (String) session.getAttribute("studentStudySystem");

					model.addAttribute("curriculumMapList", programmeSpecializationCurriculumDetailService.
							getCurriculumBySpecIdYearAndCCVersionAsMap(programSpecId, studyStartYear, curriculumVersion,studySystem,registerNumber));
					model.addAttribute("infoMessage", msg);

					float minCredit = (float) session.getAttribute("minCredit");
					float maxCredit = (float) session.getAttribute("maxCredit");

					List<Object[]> regCreditList =  programmeSpecializationCurriculumDetailService.doGetCreditInformation(model, semesterSubId, registerNumber,
							minCredit, maxCredit, programSpecId, studyStartYear, curriculumVersion,objectList);
					model.addAttribute("regCreditList", regCreditList);


					urlPage = "mainpages/ModifySlots :: section";
				}

			}
			else
			{
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
				return urlPage;
			}

			model.addAttribute("statusMap", CourseRegistrationCommonFunction.doGetRegistrationStatus());

		}
		catch(Exception exception)
		{
			LOGGER.trace(exception);

			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationEditController", 
					"editRegisteredSlots", registerNumber, IpAddress);
			urlPage = "redirectpage";

			return urlPage;
		}

		return urlPage;
	}		

	@PostMapping("UpdateRegisteredSlots")
	public String UpdateRegisteredSlots(String newCourseDetail, String oldClassId, Model model, HttpSession session, 
			HttpServletRequest request) 
	{
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try
		{
			if (registerNumber != null)
			{	
				int patternId = 0, checkflag = 2, regStatusFlag = 0, oldRegStatus = 0, oldCompType = 0;
				long newSlotId = 0;

				String msg = null, message = null;
				String[] courseDetailArr = {}, regStatusArr = {};		
				String oldGenericType = "", oldCourseCode = "", oldCourseType = "", newEpjClassId = "", oldCourseOption = "";
				String newCourseId = "", newClassId = "", newCourseType = "", newSlotClash = "", 
						newGenericType = "", newErpId = "", newAssoClassId = "";
				String pClassIdArr = "", pCompTypeArr = "", pOldClassIdArr = "", mailOTP = "";
				String pRegStatus = "", oldErpId = "", oldGradeCategory = "";
				String oldCurriculumCategory="";

				CourseRegistrationModel courseRegistrationModel2 = null;
				CourseAllocationModel courseAllocationModel = null;
				CourseAllocationModel courseAllocationModel2 = null;
				List<CourseAllocationModel> camList = new ArrayList<>();
				CourseCatalogModel courseCatalogModel =  null;

				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				String semesterDesc = (String) session.getAttribute("SemesterDesc");
				String semesterShortDesc = (String) session.getAttribute("SemesterShortDesc");
				String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				float curriculumVersion = (Float) session.getAttribute("curriculumVersion");
				String programGroupCode = (String) session.getAttribute("ProgramGroupCode");
				String ProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
				String costCentreCode = (String) session.getAttribute("costCentreCode");
				int otpStatus = (Integer) session.getAttribute("otpStatus");

				String registrationOption = (String) session.getAttribute("registrationOption");

				int statusFlag = 2, redirectFlag = 2;
				List<String> clashslot = new ArrayList<>();
				String[] validateStatusArr = new String[]{};
				courseAllocationModel = new CourseAllocationModel();

				mailOTP = request.getParameter("mailOTP");
				if ((mailOTP != null) && (!mailOTP.equals("")))
				{
					mailOTP = mailOTP.trim();
				}
				else
				{
					mailOTP = "NONE";
				}

				String gCourseType = request.getParameter("gCourseType");

				String newCourseDetail1 = "";
				String[] courseDetailArr1 = {};						
				String newClassId1 ="";
				String newSlotClash1 = "";

				if(gCourseType.equals("ETL"))
				{
					
					if(request.getParameter("courseOption1") != null || request.getParameter("courseOption1") != null) 
					{
					
					newCourseDetail1 = request.getParameter("courseOption1");
					
					
					if(newCourseDetail1!=null && !newCourseDetail.isEmpty())
					courseDetailArr1 = newCourseDetail1.split(",");	
					
					if(courseDetailArr1!=null && courseDetailArr1.length>0)
					{
						if(courseDetailArr1[0]!=null && !courseDetailArr1[0].isEmpty())
                        newClassId1 = courseDetailArr1[0];
						
						if(courseDetailArr1[3]!=null && !courseDetailArr1[3].isEmpty())
					    newSlotClash1 = courseDetailArr1[3];
					}
					}
				}

				

				courseDetailArr = newCourseDetail.split(",");						
				newClassId = courseDetailArr[0];
				newCourseId = courseDetailArr[1];
				newCourseType = courseDetailArr[2];
				newSlotClash = courseDetailArr[3];
				LOGGER.trace("\n newClassId: "+ newClassId +" | newCourseId: "+ newCourseId 
						+" | newCourseType: "+ newCourseType +" | newSlotClash: "+ newSlotClash);

				if ((!newSlotClash.equals("")) && (!newSlotClash.equals("NONE")))
				{
					clashslot.add(newSlotClash);

					if(gCourseType.equals("ETL"))
					{
						clashslot.add(newSlotClash1);
					}
				}

				pOldClassIdArr = oldClassId;

				String authStatus = (String) session.getAttribute("authStatus");
				int authCheckStatus = courseRegCommonFn.validateCourseAuthKey(authStatus, registerNumber, 
						newCourseId, 2);
				LOGGER.trace("\n authStatus: "+ authStatus +" | authCheckStatus: "+ authCheckStatus);

				courseCatalogModel = courseCatalogService.getOne(newCourseId);

				courseRegistrationModel2 = courseRegistrationService.getByRegisterNumberCourseIdAndType(semesterSubId,
						registerNumber, newCourseId, newCourseType);
				if (courseRegistrationModel2 != null)
				{
					oldCourseCode = courseRegistrationModel2.getCourseCatalogModel().getCode();
					oldGenericType = courseRegistrationModel2.getCourseCatalogModel().getGenericCourseType();
					oldCourseType = courseRegistrationModel2.getCourseRegistrationPKId().getCourseType();
					oldCourseOption = courseRegistrationModel2.getCourseOptionCode();
					oldRegStatus = courseRegistrationModel2.getStatusNumber();
					oldCompType = courseRegistrationModel2.getComponentType();
					oldErpId = courseRegistrationModel2.getCourseAllocationModel().getErpId();

					ProgramSpecializationCurriculumDetailDto curriculumDetails = crMongoService.getBySpecIdAdmissionYearAndCourseCode(programSpecId, studyStartYear, oldCourseCode);

					oldGradeCategory = courseRegistrationModel2.getGradeCategory()!=null 
							? courseRegistrationModel2.getGradeCategory() : "";

					oldCurriculumCategory=courseRegistrationModel2.getCourseCategory()!=null 
							? courseRegistrationModel2.getCourseCategory() 
									: (curriculumDetails!=null ? curriculumDetails.getCourseCategory() : "");

				}						

				courseAllocationModel = courseAllocationService.getOne(newClassId);
				if (courseAllocationModel != null)
				{
					newGenericType = courseAllocationModel.getCourseCatalogModel().getGenericCourseType();
					newErpId = courseAllocationModel.getErpId();
					newSlotId = courseAllocationModel.getSlotId();
					newAssoClassId = courseAllocationModel.getAssoClassId();
					patternId = courseAllocationModel.getTimeTableModel().getPatternId();
				}

				if (authCheckStatus == 1)
				{
					if (otpStatus == 1)
					{																
						validateStatusArr = courseRegistrationReadWriteService.validateCourseAndOTP(semesterSubId, 
								semesterDesc, semesterShortDesc, registerNumber, newCourseId, 
								oldCourseCode, mailOTP, IpAddress, "MODIFY",session).split("\\|");
						statusFlag = Integer.parseInt(validateStatusArr[0]);
						redirectFlag = Integer.parseInt(validateStatusArr[1]);
						message = validateStatusArr[2];
					}
					else if (otpStatus == 2)
					{
						statusFlag = 1;
						redirectFlag = 2;
						message = "SUCCESS";
					}
				}
				else
				{
					message = "Not a valid course to modify.";
				}

				if (statusFlag == 1)
				{
					if ((oldGenericType.equals(newGenericType)) && (oldCourseType.equals(newCourseType)))
					{
						checkflag = 1;
					}
					else
					{
						message = "Selected course generic type is not valid.";
					}

					/*if (checkflag == 1) 
					{
						if (!oldClassId.equals(newClassId))
						{
							checkflag = 1;
						}
						else
						{
							checkflag = 2;
							message = "Old class and selected new class are same, not allowed to update.";
						}
					}*/
				}

				if (checkflag == 1) 
				{

					if(gCourseType.equals("ETL"))
					{
						regStatusArr = courseRegCommonFn.checkClash(patternId, clashslot, semesterSubId, registerNumber, "EDIT", oldClassId, 
								"", Arrays.asList("BVOC", "INT", "MBA", "ST002", "ST004")).split("/");
					}
					else
					{
						regStatusArr = courseRegCommonFn.checkClash(patternId, clashslot, semesterSubId, registerNumber, "MODIFY", oldClassId, 
								"", Arrays.asList("BVOC", "INT", "MBA", "ST002", "ST004")).split("/");
					}

					regStatusFlag = Integer.parseInt(regStatusArr[0]);
					message = regStatusArr[1];
					if (regStatusFlag == 2)
					{
						redirectFlag = 1;
					}
				}

				if ((authCheckStatus == 1) && (statusFlag == 1) && (checkflag == 1) && (regStatusFlag == 1)) 
				{
					pClassIdArr = newClassId;
					pCompTypeArr = newCourseType;
					courseRegistrationModel2 =  new CourseRegistrationModel();

					if (((newGenericType.equals("ETLP")) || (newGenericType.equals("ELP"))) && (newCourseType.equals("ELA")))
					{
						courseRegistrationModel2 = courseRegistrationService.getByRegisterNumberCourseIdAndType(
								semesterSubId, registerNumber, newCourseId, "EPJ");
						if (courseRegistrationModel2 != null)
						{
							pOldClassIdArr = pOldClassIdArr +"|"+ courseRegistrationModel2.getClassId();

							courseAllocationModel2 = courseAllocationService.getCourseAllocationCourseIdTypeEmpidSlotAssoList(
									semesterSubId, classGroupId, AppGlobalValues.CLASS_TYPE, newCourseId, 
									"EPJ", newErpId, newSlotId, newAssoClassId, programGroupCode, 
									ProgramSpecCode, costCentreCode);

							if (courseAllocationModel2 != null)
							{
								newEpjClassId = courseAllocationModel2.getClassId();
								pClassIdArr = pClassIdArr +"|"+ newEpjClassId;
								pCompTypeArr = pCompTypeArr +"|EPJ";
							}
						}
					}
					else if ((newGenericType.equals("ETP")) && (newCourseType.equals("ETH")))
					{
						courseRegistrationModel2 = courseRegistrationService.getByRegisterNumberCourseIdAndType(semesterSubId, 
								registerNumber, newCourseId, "EPJ");
						if (courseRegistrationModel2 != null)
						{
							pOldClassIdArr = pOldClassIdArr +"|"+ courseRegistrationModel2.getClassId();

							courseAllocationModel2 = courseAllocationService.getCourseAllocationCourseIdTypeEmpidSlotAssoList(
									semesterSubId, classGroupId, AppGlobalValues.CLASS_TYPE, newCourseId, 
									"EPJ", newErpId, newSlotId, newAssoClassId, programGroupCode, 
									ProgramSpecCode, costCentreCode);

							if (courseAllocationModel2 != null)
							{
								newEpjClassId = courseAllocationModel2.getClassId();
								pClassIdArr = pClassIdArr +"|"+ newEpjClassId;
								pCompTypeArr = pCompTypeArr +"|EPJ";


							}
						}
					}
					else if (gCourseType.equals("ETL"))
					{

						pClassIdArr = newClassId +"|"+ newClassId1;
						pCompTypeArr = "ETH" +"|ELA";
						pOldClassIdArr =  session.getAttribute("tlClassId")+"|"+session.getAttribute("llClassId");


					}


					pRegStatus = courseRegistrationReadWriteService.courseRegistrationUpdate2(semesterSubId, registerNumber, newCourseId, 
							pCompTypeArr, oldCourseOption, pOldClassIdArr, pClassIdArr, registerNumber, IpAddress, 
							oldRegStatus, oldCompType, "GEN", "", "", "", oldGradeCategory,oldCurriculumCategory);
					
					LOGGER.info("========>>>>>"+pRegStatus);
					
					LOGGER.info("====>"+(semesterSubId+"-"+ registerNumber+"-"+ newCourseId+"-"+ 
							pCompTypeArr+"-"+ oldCourseOption+"-"+ pOldClassIdArr+"-"+ pClassIdArr+"-"+ registerNumber+"-"+ IpAddress+"-"+ 
							oldRegStatus+"-"+ oldCompType+"-"+oldGradeCategory+"-"+oldCurriculumCategory));
					
					if (pRegStatus.equals("SUCCESS"))
					{
						msg = "Slot Modified Successfully";
						message = null;
					}
					else if ((pRegStatus.equals("FAIL")) || (pRegStatus.substring(0, 5).toLowerCase().equals("error")))
					{
						message = "Technical error.";
					}
					else
					{
						message = pRegStatus;
					}							
				}						

				if (redirectFlag == 1)
				{
					if (oldCourseType.equals("ETH") || oldCourseType.equals("ELA")) 
					{
						camList = courseAllocationService.getCourseAllocationCourseIdTypeEmpidList(semesterSubId, 
								classGroupId, AppGlobalValues.CLASS_TYPE, newCourseId, oldCourseType, 
								oldErpId, programGroupCode, ProgramSpecCode, costCentreCode,registerNumber);
					} 
					else 
					{
						camList = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
								classGroupId, AppGlobalValues.CLASS_TYPE, newCourseId, oldCourseType, 
								programGroupCode, ProgramSpecCode, costCentreCode,registrationOption,registerNumber);
					}

					model.addAttribute("courseCatalogModel", courseCatalogModel);
					model.addAttribute("oldClassId", oldClassId);
					model.addAttribute("oldCourseId", newCourseId);
					model.addAttribute("oldCourseType", oldCourseType);
					model.addAttribute("courseAllocationList", camList);
					model.addAttribute("otpAllowStatus", otpStatus);

					//courseRegistrationFormController.callSlotInformation(model, semesterSubId, registerNumber, camList);

					model.addAttribute("info", msg);
					model.addAttribute("infoMessage", message);
					
					//AD1
					//editRegisteredSlots(newCourseId, model, session, request);
					modifySlots(model, session, request);
					urlPage = "mainpages/ModifySlots::section";
				}
				else
				{
					List<Object[]> courseRegistrationModel = courseRegistrationService.getByRegisterNumberAndClassGroup(
							semesterSubId, registerNumber, classGroupId);

					model.addAttribute("ModifyCourse", courseRegistrationModel);
					model.addAttribute("blockedCourse", courseRegistrationService.getBlockedCourseIdByRegisterNumberForUpdate(
							semesterSubId, registerNumber));
					
					
					
					String studySystem = (String) session.getAttribute("studentStudySystem");
					
					model.addAttribute("curriculumMapList", programmeSpecializationCurriculumDetailService.
							getCurriculumBySpecIdYearAndCCVersionAsMap(programSpecId, studyStartYear, curriculumVersion,studySystem,registerNumber));
					model.addAttribute("info", msg);
					model.addAttribute("infoMessage", message);



					float minCredit = (float) session.getAttribute("minCredit");
					float maxCredit = (float) session.getAttribute("maxCredit");

					List<Object[]> regCreditList =  programmeSpecializationCurriculumDetailService.doGetCreditInformation(model, semesterSubId, registerNumber,
							minCredit, maxCredit, programSpecId, studyStartYear, curriculumVersion,courseRegistrationModel);
					model.addAttribute("regCreditList", regCreditList);

					urlPage = "mainpages/ModifySlots::section";
				}

			}
			else
			{
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
				return urlPage;
			}

			model.addAttribute("statusMap", CourseRegistrationCommonFunction.doGetRegistrationStatus());

		}
		catch(Exception exception)
		{
			LOGGER.trace(exception);

			//exception.printStackTrace();

			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationEditController", 
					"UpdateRegisteredSlots", registerNumber, IpAddress);
			urlPage = "redirectpage";

			return urlPage;
		}

		return urlPage;
	}	
}
