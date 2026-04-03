	package org.vtop.CourseRegistration.controller;

	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	import javax.servlet.http.HttpSession;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Controller;
	import org.springframework.ui.Model;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.vtop.CourseRegistration.AppGlobalValues;
	import org.vtop.CourseRegistration.GlobalMaster;
	import org.vtop.CourseRegistration.Dto.ProgramSpecializationCurriculumDetailDto;
	import org.vtop.CourseRegistration.model.CourseAllocationModel;
	import org.vtop.CourseRegistration.model.CourseCatalogModel;
	import org.vtop.CourseRegistration.model.SlotTimeMasterModel;
	import org.vtop.CourseRegistration.service.*;
	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;


	@Controller
	public class CourseRegistrationFormController 
	{	
		@Autowired private CourseCatalogService courseCatalogService;
		@Autowired private CourseAllocationService courseAllocationService;
		@Autowired private CourseRegistrationService courseRegistrationService;
		@Autowired private StudentHistoryService studentHistoryService;
		@Autowired private CourseRegistrationCommonFunction courseRegCommonFn;
		@Autowired private SemesterMasterService semesterMasterService;
		@Autowired private CourseRegistrationReadWriteService courseRegistrationReadWriteService;	
		@Autowired private CourseRegCommonService courseRegistrationCommonMongoService;
		@Autowired private CourseRegCommonService crMongoService;
		@Autowired private ProgrammeSpecializationCurriculumDetailService spcService;


		@Autowired GlobalMaster globalValues;


		private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationFormController.class);	


		@PostMapping("viewRegistrationOption")
		public String viewRegistrationOption(Model model, HttpSession session, HttpServletRequest request, 
				HttpServletResponse response) 
		{
			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");
			String msg = null,  urlPage = "";

			try
			{
				if (registerNumber != null)
				{

					int PEUEAllowStatus = (Integer) session.getAttribute("PEUEAllowStatus");
					String programGroupCode = (String) session.getAttribute("ProgramGroupCode");
					int regularFlag = (Integer) session.getAttribute("regularFlag");
					int reRegFlag =  (Integer) session.getAttribute("reRegFlag");
					float maxCredit = (float) session.getAttribute("maxCredit");
					int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
					int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
					int compulsoryCourseStatus = (Integer) session.getAttribute("compulsoryCourseStatus");
					String registrationMethod = (String) session.getAttribute("registrationMethod");
					float curriculumVersion = (Float) session.getAttribute("curriculumVersion");

					model.addAttribute("regularFlag", regularFlag);
					model.addAttribute("PEUEAllowStatus", PEUEAllowStatus);

					session.removeAttribute("subRegistrationOption");
					model.addAttribute("regOptionList", courseRegistrationCommonMongoService.getRegistrationOption(
							programGroupCode, registrationMethod, regularFlag, reRegFlag, PEUEAllowStatus, programSpecId, 
							studyStartYear, curriculumVersion, compulsoryCourseStatus, session));
					model.addAttribute("studySystem", session.getAttribute("StudySystem"));
					model.addAttribute("maxCredit", maxCredit);
					model.addAttribute("showFlag", 0);

					urlPage = "mainpages/RegistrationOptionList :: section";

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
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"viewRegistrationOption", registerNumber, IpAddress);
				urlPage = "redirectpage";
				return urlPage;			
			}

			model.addAttribute("info", msg);
			return urlPage;
		}

		@PostMapping("processRegistrationOption")
		public String processRegistrationOption(@RequestParam(value="registrationOption", required=false) String registrationOption, 
				Model model, HttpSession session, @RequestParam(value="pageSize", required=false) Integer pageSize,
				@RequestParam(value="page", required=false) Integer page,
				@RequestParam(value="searchType", required=false) Integer searchType,
				@RequestParam(value="searchVal", required=false) String searchVal,
				@RequestParam(value="subCourseOption", required=false) String subCourseOption, 
				HttpServletRequest request,
				@RequestParam(value="subRegistrationOption", required=false) String subRegistrationOption)
		{
			LOGGER.trace("\n registrationOption: "+ registrationOption +" | pageSize: "+ pageSize 
					+" | page: "+ page +" | searchType: "+ searchType +" | searchVal: "+ searchVal 
					+" | subCourseOption: "+ subCourseOption);

			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");
			String  urlPage = "";

			try
			{
				if (registerNumber != null)
				{
					int compulsoryStatus = 2;
					String flagValue = request.getParameter("flag");
					if ((flagValue == null) || (flagValue.equals(null)))
					{
						flagValue = "0";
					}			

					if ((registrationOption != null) && (!registrationOption.equals(null))) 
					{
						session.setAttribute("registrationOption", registrationOption);
					} 
					else 
					{
						registrationOption = (String) session.getAttribute("registrationOption");
					}

					if ((subRegistrationOption != null)) 
					{
						session.setAttribute("subRegistrationOption", subRegistrationOption);
					} 

					if(registrationOption!=null && registrationOption.equals("CBCSMIN") 
							&& (subRegistrationOption==null || subRegistrationOption.isEmpty()))
					{
						urlPage = "mainpages/CBCSMinorView";
					}
					else if(registrationOption!=null && registrationOption.equals("ACEMIN") 
							&& (subRegistrationOption==null || subRegistrationOption.isEmpty()))
					{
						urlPage = "mainpages/ACEMinorView";
					}
					else if(registrationOption!=null && registrationOption.equals("CBCSHON")
							&& (subRegistrationOption==null || subRegistrationOption.isEmpty()))
					{
						urlPage = "mainpages/CBCSHonourView";
					}
					
					else
					{

						String semesterSubId = (String) session.getAttribute("SemesterSubId");
						int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
						int StudentGraduateYear = (Integer) session.getAttribute("StudentGraduateYear");
						int semesterId  = (Integer) session.getAttribute("SemesterId");
						String ProgramGroupCode = (String) session.getAttribute("ProgramGroupCode");
						int programGroupId = (Integer) session.getAttribute("ProgramGroupId");
						String ProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
						int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
						String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
						String pOldRegisterNumber = (String) session.getAttribute("OldRegNo");
						String[] pCourseSystem = (String[]) session.getAttribute("StudySystem");

						@SuppressWarnings("unchecked")
						List<String> compCourseList = (List<String>) session.getAttribute("compulsoryCourseList");
						String costCentreCode = (String) session.getAttribute("costCentreCode");
						int compulsoryCourseStatus = (Integer) session.getAttribute("compulsoryCourseStatus");

						if (compulsoryCourseStatus == 1)
						{
							compulsoryStatus = courseRegCommonFn.compulsoryCourseCheck(programGroupId, studyStartYear, 
									StudentGraduateYear, semesterId, semesterSubId, registerNumber, 
									classGroupId, AppGlobalValues.CLASS_TYPE, ProgramSpecCode, programSpecId, 
									ProgramGroupCode, pOldRegisterNumber, compCourseList, costCentreCode, 
									pCourseSystem);
							session.setAttribute("compulsoryCourseStatus", compulsoryStatus);
						}

						if (compulsoryStatus == 1)
						{	
							registrationOption = "COMP";
							session.setAttribute("registrationOption", registrationOption);
						}
						

						callCourseRegistrationTypes(registrationOption, pageSize, page, searchType, searchVal, session, model);

						model.addAttribute("studySystem", session.getAttribute("StudySystem"));		
						model.addAttribute("showFlag", 1);

						if (flagValue.equals("1"))
						{
							urlPage = "mainpages/CourseList :: cclistfrag";
						}
						else
						{
							urlPage = "mainpages/CourseList :: section";
						}

					}
				}			
				else
				{
					model.addAttribute("flag", 1);
					urlPage = "redirectpage";
					return urlPage;
				}
			} 
			catch (Exception exception) 
			{
				exception.printStackTrace();
				LOGGER.trace(exception);

				model.addAttribute("flag", 1);
				courseRegistrationReadWriteService.addErrorLog(exception, 
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"processRegistrationOption", registerNumber, IpAddress);
				urlPage = "redirectpage";
				return urlPage;
			}

			return urlPage;
		}

		public int callCourseRegistrationTypes(String registrationOption, Integer pageSize, Integer page, 
				Integer searchType, String searchVal, HttpSession session, Model model)
		{
			String semesterSubId = (String) session.getAttribute("SemesterSubId");		
			String registerNo = (String) session.getAttribute("RegisterNumber");

			try
			{
				if (semesterSubId != null)
				{
					int programGroupId = (Integer) session.getAttribute("ProgramGroupId");
					String ProgramGroupCode = (String) session.getAttribute("ProgramGroupCode");
					int ProgramSpecId = (Integer) session.getAttribute("ProgramSpecId");
					String ProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
					int studYear = (Integer) session.getAttribute("StudyStartYear");
					float curriculumVersion = (Float) session.getAttribute("curriculumVersion");

					@SuppressWarnings("unchecked")
					List<Integer> egbGroupId = (List<Integer>) session.getAttribute("EligibleProgramLs");
					@SuppressWarnings("unchecked")
					List<String> compCourseList = (List<String>) session.getAttribute("compulsoryCourseList");
					@SuppressWarnings("unchecked")
					List<String> reRegisterCourseCodeList = (List<String>) session.getAttribute("reRegisterCourseCodeList");

					String[] courseSystem = (String[]) session.getAttribute("StudySystem");				
					String registrationMethod = (String) session.getAttribute("registrationMethod");
					int StudentGraduateYear = (Integer) session.getAttribute("StudentGraduateYear");
					int PEUEAllowStatus = (Integer) session.getAttribute("PEUEAllowStatus");
					String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
					String costCentreCode = (String) session.getAttribute("costCentreCode");

					Pager pager = null;		
					int evalPageSize = AppGlobalValues.INITIAL_PAGE_SIZE;
					int evalPage = AppGlobalValues.INITIAL_PAGE;
					evalPageSize = pageSize == null ? AppGlobalValues.INITIAL_PAGE_SIZE : pageSize;
					evalPage = (page == null || page < 1) ? AppGlobalValues.INITIAL_PAGE : page - 1;
					int pageSerialNo = evalPageSize * evalPage;
					int srhType = (searchType == null) ? 0 : searchType;
					String srhVal = (searchVal == null) ? "NONE" : searchVal;

					LOGGER.trace("\n pageSize: "+ pageSize +" | page: "+ page +" | pageSerialNo: "+ pageSerialNo 
							+" | evalPageSize: "+ evalPageSize +" | evalPage: "+ evalPage);

					if (registrationOption != null) 
					{
						session.setAttribute("registrationOption", registrationOption);
					} 
					else 
					{
						registrationOption = (String) session.getAttribute("registrationOption");
					}

					int totalPage = 0, pageNumber = evalPage; 
					String[] pagerArray = new String[]{};

					List<CourseCatalogModel> courseCatalogModelPageList = new ArrayList<>();
					courseCatalogModelPageList = courseCatalogService.getCourseListForRegistration(registrationOption, 
							AppGlobalValues.CAMPUS_CODE, courseSystem, egbGroupId, programGroupId, semesterSubId, 
							ProgramSpecId, classGroupId, AppGlobalValues.CLASS_TYPE, studYear, curriculumVersion, 
							registerNo, srhType, srhVal, StudentGraduateYear, ProgramGroupCode, 
							ProgramSpecCode, registrationMethod,  PEUEAllowStatus, 
							evalPage, evalPageSize, costCentreCode, compCourseList, reRegisterCourseCodeList,session);


					if(registrationOption.equals("CON"))
					{
						Map<String, String> conMap = spcService.doGetConBasketDetails(studYear,semesterSubId,Arrays.asList(classGroupId)
								,ProgramGroupCode, ProgramSpecCode, costCentreCode);

						Map<String,List<CourseCatalogModel>> conCourseList = new HashMap<>();


						for (CourseCatalogModel courseList : courseCatalogModelPageList)
						{
							if(conMap.containsKey(courseList.getCourseId()))
							{
								if(conCourseList.containsKey(conMap.get(courseList.getCourseId())))
								{
									List<CourseCatalogModel> inrData = conCourseList.get(conMap.get(courseList.getCourseId()));
									inrData.add(courseList);
									conCourseList.put(conMap.get(courseList.getCourseId()),inrData);
								}
								else {
									List<CourseCatalogModel> inrData =new ArrayList<>();
									inrData.add(courseList);
									conCourseList.put(conMap.get(courseList.getCourseId()),inrData);
								}

							}

						}



						model.addAttribute("conCourseList",conCourseList);

						model.addAttribute("conBasket",spcService.doGetConBasketDetails(studYear,semesterSubId,Arrays.asList(classGroupId)
								,ProgramGroupCode, ProgramSpecCode, costCentreCode));
					}
					else if(registrationOption.equals("OEC"))
					{
						Map<String, String> conMap = spcService.doGetConBasketDetailsOEC(studYear,semesterSubId,Arrays.asList(classGroupId));

						Map<String,List<CourseCatalogModel>> oecCourseList = new HashMap<>();


						for (CourseCatalogModel courseList : courseCatalogModelPageList)
						{
							if(conMap.containsKey(courseList.getCourseId()))
							{
								if(oecCourseList.containsKey(conMap.get(courseList.getCourseId())))
								{
									List<CourseCatalogModel> inrData = oecCourseList.get(conMap.get(courseList.getCourseId()));
									inrData.add(courseList);
									oecCourseList.put(conMap.get(courseList.getCourseId()),inrData);
								}
								else {
									List<CourseCatalogModel> inrData =new ArrayList<>();
									inrData.add(courseList);
									oecCourseList.put(conMap.get(courseList.getCourseId()),inrData);
								}

							}

						}



						model.addAttribute("oecCourseList",oecCourseList);

						model.addAttribute("oecBasket",spcService.doGetConBasketDetailsOEC(studYear,semesterSubId,Arrays.asList(classGroupId)));
					}



					LOGGER.trace("\n CourseListSize: "+ courseCatalogModelPageList.size() 
					+" | evalPageSize: "+ evalPageSize +" | pageNumber: "+ pageNumber);

					pagerArray = courseCatalogService.getTotalPageAndIndex(courseCatalogModelPageList.size(), 
							evalPageSize, pageNumber).split("\\|");
					totalPage = Integer.parseInt(pagerArray[0]);
					pager = new Pager(totalPage, pageNumber, AppGlobalValues.BUTTONS_TO_SHOW);
					LOGGER.trace("\n totalPage: "+ totalPage);

					model.addAttribute("tlTotalPage", totalPage);
					model.addAttribute("tlPageNumber", pageNumber);
					model.addAttribute("tlCourseCatalogModelList", courseCatalogModelPageList);
					model.addAttribute("courseRegModelList", courseRegistrationService.getRegisteredCourseByClassGroup(semesterSubId, 
							registerNo, classGroupId));
					model.addAttribute("registrationOption", registrationOption);
					model.addAttribute("registrationOptionDesc", courseRegistrationCommonMongoService.getCourseOptionDescription(ProgramSpecId, studYear, registrationOption));
					model.addAttribute("pageSlno", pageSerialNo);
					model.addAttribute("selectedPageSize", evalPageSize);
					model.addAttribute("pageSizes", AppGlobalValues.PAGE_SIZES);
					model.addAttribute("srhType", srhType);
					model.addAttribute("srhVal", srhVal);
					model.addAttribute("pager", pager);
					model.addAttribute("page", page);
				}			
			}
			catch(Exception exception)
			{
				//exception.printStackTrace();
				LOGGER.trace(exception);
			}

			return 1;
		}


		@PostMapping(value="processCourseRegistration")
		public String processCourseRegistration(String courseId, 
				@RequestParam(value = "page", required = false) Integer page,
				@RequestParam(value = "searchType", required = false) Integer searchType,
				@RequestParam(value = "searchVal", required = false) String searchVal, 
				Model model, HttpSession session, HttpServletRequest request) 
		{

			return loadProcessCourseRegistration(courseId, page, searchType, searchVal, model, session, request);
		}


		public String loadProcessCourseRegistration(String courseId, 
				Integer page,
				Integer searchType,
				String searchVal, 
				Model model, HttpSession session, HttpServletRequest request)
		{

			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");
			String urlPage = "";

			try
			{
				if (registerNumber != null)
				{
					String courseTypeDisplay = "",  message = null, courseOption = "",	
							genericCourseType = "";
					String courseCategory = "NONE", subCourseType = "", subCourseDate = "", courseCode = "", 
							genericCourseTypeDisplay = "", authKeyVal = "", corAuthStatus = "", ccCourseId = "", 
							ccCourseSystem = "", crCourseCode = "NONE", crCourseId = "NONE", crGenericCourseType = "NONE", 
							crSubCourseOption = "", crSubCourseType = "", crSubCourseDate = "";
					String[] regStatusArr = new String[50], regStatusArr2 = new String[50], tempRegStatusArr = new String[50];
					int  regStatusFlag = 2, projectStatus = 2, regAllowFlag = 1, wlAllowFlag = 1, 
							audAllowFlag = 1, rgrAllowFlag=2, minAllowFlag = 2, honAllowFlag = 2, adlAllowFlag = 2, 
							RPEUEAllowFlag=2, csAllowFlag=2, RUCUEAllowFlag=2,RDEOEAllowFlag = 2,RSEOEAllowFlag = 2;
					int ethExistFlag = 2, epjExistFlag = 2, epjSlotFlag = 2, regularFlag=2, crCourseStatus = 2;

					String semesterSubId = (String) session.getAttribute("SemesterSubId");
					String[] pCourseSystem = (String[]) session.getAttribute("StudySystem");
					int pProgramGroupId = (Integer) session.getAttribute("ProgramGroupId"); 
					String pProgramGroupCode = (String) session.getAttribute("ProgramGroupCode");
					int pProgramSpecId = (Integer) session.getAttribute("ProgramSpecId");
					String pProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
					int pSemesterId = (Integer) session.getAttribute("SemesterId");
					float CurriculumVersion = (Float) session.getAttribute("curriculumVersion");
					String pOldRegisterNumber = (String) session.getAttribute("OldRegNo");
					float maxCredit = (float) session.getAttribute("maxCredit");
					float cclTotalCredit = (float) session.getAttribute("cclTotalCredit");

					String registrationOption = (String) session.getAttribute("registrationOption");
					String subRegistrationOption = (String) session.getAttribute("subRegistrationOption");

					String subCourseOption = (String) session.getAttribute("subCourseOption");
					int StudyStartYear = (Integer) session.getAttribute("StudyStartYear");
					float curriculumVersion = (Float) session.getAttribute("curriculumVersion");
					int StudentGraduateYear = (Integer) session.getAttribute("StudentGraduateYear");
					int OptionNAStatus = (Integer) session.getAttribute("OptionNAStatus");

					int PEUEAllowStatus = (Integer) session.getAttribute("PEUEAllowStatus");
					String studentStudySystem = (String) session.getAttribute("studentStudySystem");
					String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
					String programGroupMode = (String) session.getAttribute("programGroupMode");
					String studentCgpaData = (String) session.getAttribute("studentCgpaData");
					String costCentreCode = (String) session.getAttribute("costCentreCode");
					int acadGraduateYear = (Integer) session.getAttribute("acadGraduateYear");
					String cgpaProgGroup = (String) session.getAttribute("CGPAProgram");

					regularFlag = (Integer) session.getAttribute("regularFlag");
					session.setAttribute("corAuthStatus", "NONE");
					session.setAttribute("authStatus", "NONE");
					session.setAttribute("camList", null);
					session.setAttribute("camList2", null);
					session.setAttribute("camList3", null);

					session.setAttribute("courseIdVar", courseId);

					List<String> courseTypeArr = new ArrayList<String>();					

					@SuppressWarnings("unchecked")
					List<String> compCourseList = (List<String>) session.getAttribute("compulsoryCourseList");

					session.setAttribute("courseCategory", "");

					CourseCatalogModel courseCatalog = new CourseCatalogModel();
					CourseCatalogModel courseCatalog2 = null;

					List<CourseAllocationModel> list1 = new ArrayList<>();
					List<CourseAllocationModel> ela = new ArrayList<>();
					List<CourseAllocationModel> epj = new ArrayList<>();
					List<CourseAllocationModel> courseAllocationList = new ArrayList<>();

					courseCatalog = courseCatalogService.getOne(courseId);
					if (courseCatalog != null)
					{
						courseCode = courseCatalog.getCode();
						genericCourseType = courseCatalog.getGenericCourseType();
						genericCourseTypeDisplay = courseCatalog.getCourseTypeComponentModel().getDescription();
						ccCourseSystem = courseCatalog.getCourseSystem();
						if ((courseCatalog.getCorequisite() != null) && (!courseCatalog.getCorequisite().equals("")) 
								&& (!courseCatalog.getCorequisite().equals("NONE")) && (!courseCatalog.getCorequisite().equals("NIL")))
						{
							crCourseCode = courseCatalog.getCorequisite().trim();
						}
					}

					if ((!ccCourseSystem.equals("NONFFCS")) && (!ccCourseSystem.equals("FFCS")) 
							&& (!ccCourseSystem.equals("CAL")) && (!crCourseCode.equals("")) 
							&& (!crCourseCode.equals("NONE")))
					{	
						crCourseStatus = 1;

						courseCatalog2 = courseCatalogService.getOfferedCourseDetailByCourseCode(semesterSubId, classGroupId, 
								AppGlobalValues.CLASS_TYPE, crCourseCode);
						if (courseCatalog2 != null)
						{
							crCourseId = courseCatalog2.getCourseId();
							crGenericCourseType = courseCatalog2.getGenericCourseType();
						}
					}
					LOGGER.trace("\n "+ pCourseSystem +" | "+ pProgramGroupId +" | "+ pProgramGroupCode +" | "+
							pProgramSpecCode +" | "+ semesterSubId +" | "+ registerNumber +" | "+ 
							pOldRegisterNumber +" | "+ maxCredit +" | "+ courseId +" | "+ StudyStartYear+" | "+
							StudentGraduateYear +" | "+ studentStudySystem);


					List<String> excessCreditAllowedCategories = (List<String>) session.getAttribute("excessCreditAllowedCategories");
					tempRegStatusArr = courseRegCommonFn.CheckRegistrationCondition(pCourseSystem, pProgramGroupId, 
							pProgramGroupCode, pProgramSpecCode, semesterSubId, registerNumber, 
							pOldRegisterNumber, maxCredit, courseId, StudyStartYear, StudentGraduateYear, 
							studentStudySystem, pProgramSpecId, CurriculumVersion, PEUEAllowStatus, 
							programGroupMode, classGroupId, studentCgpaData, 
							OptionNAStatus, compCourseList, pSemesterId, AppGlobalValues.CLASS_TYPE, costCentreCode, 
							acadGraduateYear, cclTotalCredit, cgpaProgGroup,registrationOption,excessCreditAllowedCategories,0.0f).split("/");
					
					session.setAttribute("regComponentEquivWithCoReq", tempRegStatusArr[23]);

					//System.out.println("tempRegStatusArr[0]==>"+tempRegStatusArr[0].toString());

					
					if ((Integer.parseInt(tempRegStatusArr[0]) == 1) && (crCourseStatus == 1))
					{
						if (AppGlobalValues.CR_COURSE_OPTION.contains(tempRegStatusArr[2]))
						{
							regStatusArr2 = courseRegCommonFn.CheckRegistrationCondition(pCourseSystem, pProgramGroupId, 
									pProgramGroupCode, pProgramSpecCode, semesterSubId, registerNumber, 
									pOldRegisterNumber, maxCredit, crCourseId, StudyStartYear, StudentGraduateYear, 
									studentStudySystem, pProgramSpecId, CurriculumVersion, PEUEAllowStatus, 
									programGroupMode, classGroupId, studentCgpaData, 
									OptionNAStatus, compCourseList, pSemesterId, AppGlobalValues.CLASS_TYPE, costCentreCode, 
									acadGraduateYear, cclTotalCredit, cgpaProgGroup,registrationOption,excessCreditAllowedCategories,courseCatalog.getCredits()).split("/");
							
							if ((Integer.parseInt(regStatusArr2[0]) == 1) && (AppGlobalValues.CR_COURSE_OPTION.contains(regStatusArr2[2])))
							{
								crCourseStatus = 1;

								if (genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
								{
									regStatusArr = regStatusArr2;
								}
								else
								{
									regStatusArr = tempRegStatusArr;
								}
							}
							else
							{
								if(tempRegStatusArr[2].startsWith("RR"))
								{
									regStatusArr = tempRegStatusArr;
								}
								else
								{
									regStatusArr = regStatusArr2;
								}
								crCourseStatus = 2;
							}
						}
						else
						{
							regStatusArr = tempRegStatusArr;
							crCourseStatus = 2;
						}
					}
					else
					{
						regStatusArr = tempRegStatusArr;
						crCourseStatus = 2;
					}

					regStatusFlag = Integer.parseInt(regStatusArr[0]);
					message = regStatusArr[1];							
					courseOption = regStatusArr[2];
					regAllowFlag = Integer.parseInt(regStatusArr[3]);
					wlAllowFlag = Integer.parseInt(regStatusArr[4]);
					audAllowFlag = Integer.parseInt(regStatusArr[8]);
					rgrAllowFlag= Integer.parseInt(regStatusArr[11]);
					minAllowFlag = Integer.parseInt(regStatusArr[13]);
					honAllowFlag = Integer.parseInt(regStatusArr[12]);
					courseCategory = regStatusArr[14];
					adlAllowFlag = Integer.parseInt(regStatusArr[15]);
					authKeyVal = regStatusArr[16];
					RPEUEAllowFlag = Integer.parseInt(regStatusArr[17]);
					csAllowFlag = Integer.parseInt(regStatusArr[18]);
					RUCUEAllowFlag = Integer.parseInt(regStatusArr[19]);
					ccCourseId = regStatusArr[20];
					RDEOEAllowFlag = Integer.parseInt(regStatusArr[21]);
					RSEOEAllowFlag = Integer.parseInt(regStatusArr[22]);

					if ((crCourseStatus == 1) && genericCourseType.equals("TH") && crGenericCourseType.equals("LO"))
					{
						corAuthStatus = regStatusArr[2] +"/"+ regStatusArr[3] +"/"+ regStatusArr[4] +"/"+ regStatusArr[8] 
								+"/"+ regStatusArr[11] +"/"+ regStatusArr[13] +"/"+ regStatusArr[12] +"/"+ regStatusArr[14] 
										+"/"+ regStatusArr[15] +"/"+ regStatusArr[17] +"/"+ regStatusArr[6] +"/"+ regStatusArr[7] 
												+"/"+ regStatusArr[9] +"/"+ regStatusArr[10] +"/"+ regStatusArr[18] +"/"+ regStatusArr[19] 
														+"/"+ regStatusArr[20] +"/"+ crCourseStatus +"/"+ crCourseId +"/"+ crCourseCode 
														+"/"+ crGenericCourseType +"/"+ regStatusArr2[7] +"/"+ regStatusArr2[9] +"/"+ regStatusArr2[10]+"/"+regStatusArr[21]+"/"+regStatusArr[22];
					}
					else if ((crCourseStatus == 1) && genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
					{
						corAuthStatus = regStatusArr[2] +"/"+ regStatusArr[3] +"/"+ regStatusArr[4] +"/"+ regStatusArr[8] 
								+"/"+ regStatusArr[11] +"/"+ regStatusArr[13] +"/"+ regStatusArr[12] +"/"+ regStatusArr[14] 
										+"/"+ regStatusArr[15] +"/"+ regStatusArr[17] +"/"+ regStatusArr[6]	+"/"+ regStatusArr[7] 
												+"/"+ regStatusArr[9] +"/"+ regStatusArr[10] +"/"+ regStatusArr[18] +"/"+ regStatusArr[19] 
														+"/"+ regStatusArr[20] +"/"+ crCourseStatus +"/"+ courseId +"/"+ courseCode 
														+"/"+ genericCourseType +"/"+ tempRegStatusArr[7] +"/"+ tempRegStatusArr[9] +"/"+ tempRegStatusArr[10]+"/"+regStatusArr[21]+"/"+regStatusArr[22];
					}
					else
					{
						corAuthStatus = regStatusArr[2] +"/"+ regStatusArr[3] +"/"+ regStatusArr[4] +"/"+ regStatusArr[8] 
								+"/"+ regStatusArr[11] +"/"+ regStatusArr[13] +"/"+ regStatusArr[12] +"/"+ regStatusArr[14] 
										+"/"+ regStatusArr[15] +"/"+ regStatusArr[17] +"/"+ regStatusArr[6] +"/"+ regStatusArr[7] 
												+"/"+ regStatusArr[9] +"/"+ regStatusArr[10] +"/"+ regStatusArr[18] +"/"+ regStatusArr[19] 
														+"/"+ regStatusArr[20] +"/"+ crCourseStatus	+"/"+ crCourseId +"/"+ crCourseCode 
														+"/"+ crGenericCourseType +"///"+"/"+regStatusArr[21]+"/"+regStatusArr[22];
					}

					session.setAttribute("authStatus", authKeyVal);
					session.setAttribute("corAuthStatus", corAuthStatus);
					LOGGER.trace("\n corAuthStatus: "+ corAuthStatus);
					LOGGER.trace("\n AuthKeyVal: "+ authKeyVal);

					if(courseOption.equals("RR") || courseOption.equals("RRCE"))
					{
						if (!regStatusArr[6].equals("NONE"))
						{
							//courseTypeArr = Arrays.asList(regStatusArr[6].split(","));

							if ((crCourseStatus == 1) && genericCourseType.equals("TH") && crGenericCourseType.equals("LO"))
							{
								courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
								courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(crGenericCourseType));
							}
							else if ((crCourseStatus == 1) && genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
							{
								courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(crGenericCourseType));
								courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
							}
							else
							{
								courseTypeArr = Arrays.asList(regStatusArr[6].split(","));
							}
						}																	

						if (courseTypeArr.size() <= 0)
						{
							courseTypeArr = semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType);
						}

					}
					else
					{
						if ((crCourseStatus == 1) && genericCourseType.equals("TH") && crGenericCourseType.equals("LO"))
						{
							courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
							courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(crGenericCourseType));
						}
						else if ((crCourseStatus == 1) && genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
						{
							courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(crGenericCourseType));
							courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
						}
						else
						{
							courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
						}
					}

					switch(courseOption)
					{
					case "RR":
					case "RRCE":
					case "GI":
					case "GICE":
					case "RGCE":
					case "RPCE":
					case "RWCE":

						if ((crCourseStatus == 1) && genericCourseType.equals("TH") && crGenericCourseType.equals("LO"))
						{
							subCourseOption = regStatusArr[7];
							subCourseType = regStatusArr[9];
							subCourseDate = regStatusArr[10];
							crSubCourseOption = regStatusArr2[7]; 
							crSubCourseType = regStatusArr2[9];
							crSubCourseDate = regStatusArr2[10];	
						}
						else if ((crCourseStatus == 1) && genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
						{
							subCourseOption = regStatusArr[7];
							subCourseType = regStatusArr[9];
							subCourseDate = regStatusArr[10];
							crSubCourseOption = tempRegStatusArr[7]; 
							crSubCourseType = tempRegStatusArr[9];
							crSubCourseDate = tempRegStatusArr[10];
						}
						else
						{
							subCourseOption = regStatusArr[7];
							subCourseType = regStatusArr[9];
							subCourseDate = regStatusArr[10];
						}
						LOGGER.trace("\n subCourseOption: "+ subCourseOption +" | subCourseType: "+ subCourseType 
								+" | subCourseDate: "+ subCourseDate);

						break;

					default:
						if (regStatusArr[7].equals("NONE"))
						{
							subCourseOption = "";
						}
						break;
					}

					for (String crstp : courseTypeArr) 
					{
						if (courseTypeDisplay.equals(""))
						{
							courseTypeDisplay = semesterMasterService.getCourseTypeMasterByCourseType(crstp).getDescription();
						}
						else
						{
							courseTypeDisplay = courseTypeDisplay +" / "+ semesterMasterService.getCourseTypeMasterByCourseType(crstp).getDescription();
						}

						if (crstp.equals("ETH"))
						{
							ethExistFlag = 1;
						}
						else if (crstp.equals("EPJ"))
						{
							epjExistFlag = 1;
						}								 
					}

					if ((courseTypeArr.size() == 2) && (genericCourseType.equals("ETLP")) 
							&& (ethExistFlag == 1) && (epjExistFlag == 1))
					{
						epjSlotFlag = 1;
					}
					else if ((courseTypeArr.size() == 1) && (epjExistFlag == 1))
					{
						epjSlotFlag = 1;
					}
					LOGGER.trace("\n regStatusFlag: "+ regStatusFlag);

					switch(regStatusFlag)
					{    
					case 1:								
						if (courseTypeArr.size() > 0) 
						{
							for (String crtp : courseTypeArr) 
							{	
								LOGGER.trace("\n Course Type: "+ crtp);
								switch(crtp)
								{
								case "EPJ":
									epj = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
											classGroupId, AppGlobalValues.CLASS_TYPE, courseId, "EPJ", pProgramGroupCode, 
											pProgramSpecCode, costCentreCode,registrationOption,registerNumber);
									model.addAttribute("cam3", epj);
									session.setAttribute("camList3", epj);
									break;

								case "ELA":
									ela = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
											classGroupId, AppGlobalValues.CLASS_TYPE, courseId, "ELA", pProgramGroupCode, 
											pProgramSpecCode, costCentreCode,registrationOption,registerNumber);
									model.addAttribute("cam2", ela);
									session.setAttribute("camList2", ela);
									break;

								default:
									if ((crCourseStatus == 1) && crtp.equals("LO"))
									{
										if (genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
										{
											ela = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
													classGroupId, AppGlobalValues.CLASS_TYPE, courseId, crtp, pProgramGroupCode, 
													pProgramSpecCode, costCentreCode,registrationOption,registerNumber);
										}
										else
										{
											ela = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
													classGroupId, AppGlobalValues.CLASS_TYPE, crCourseId, crtp, pProgramGroupCode, 
													pProgramSpecCode, costCentreCode,registrationOption,registerNumber);
										}
										model.addAttribute("cam2", ela);
										session.setAttribute("camList2", ela);
									}
									else
									{
										if ((crCourseStatus == 1) && genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
										{
											list1 = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
													classGroupId, AppGlobalValues.CLASS_TYPE, crCourseId, crtp, pProgramGroupCode, 
													pProgramSpecCode, costCentreCode,registrationOption,registerNumber);
										}
										else
										{
											list1 = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
													classGroupId, AppGlobalValues.CLASS_TYPE, courseId, crtp, pProgramGroupCode, 
													pProgramSpecCode, costCentreCode,registrationOption,registerNumber);
										}
										model.addAttribute("cam", list1);
										session.setAttribute("camList", list1);
									}
									break;
								}

								if(crtp.equals("PJT"))
								{
									projectStatus = 1;
								}											
							}
						}

						//Assigning the all course type of allocation list to one course allocation list
						LOGGER.trace("\n list1 size: "+ list1.size());
						if (!list1.isEmpty())
						{
							courseAllocationList.addAll(list1);
						}

						LOGGER.trace("\n ela size: "+ ela.size());
						if (!ela.isEmpty())
						{
							courseAllocationList.addAll(ela);
						}

						LOGGER.trace("\n projectStatus: "+ projectStatus);
						if (projectStatus == 1)
						{
							List<Object[]> courseCostCentre = semesterMasterService.getEmployeeProfileByCampusCode(AppGlobalValues.CAMPUS_CODE);

							model.addAttribute("courseCostCentre", courseCostCentre);
							model.addAttribute("ProgramCode", session.getAttribute("ProgramGroupCode"));
							model.addAttribute("courseOption", courseOption);
                      System.out.println("courseOption==>"+courseOption);
							session.setAttribute("courseOption", courseOption);


							urlPage = "mainpages/ProjectRegistration :: section";
						}
						else
						{											
							urlPage = "mainpages/CourseRegistration :: section";
						}

						model.addAttribute("shcssList", studentHistoryService.getStudentHistoryCS2(registerNumber, 
								courseCode, studentStudySystem, pProgramSpecId, StudyStartYear, curriculumVersion, 
								semesterSubId, courseCategory, courseOption, ccCourseId, csAllowFlag));
						model.addAttribute("minorList", semesterMasterService.getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(
								minAllowFlag, "MIN", pProgramGroupId, pProgramSpecId, courseCode, studentStudySystem));
						model.addAttribute("honorList", semesterMasterService.getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(
								honAllowFlag, "HON", pProgramGroupId, pProgramSpecId, courseCode, studentStudySystem));
						
						model.addAttribute("courseOptionList",semesterMasterService.getRegistrationCourseOption(
								courseOption, genericCourseType, rgrAllowFlag, audAllowFlag, honAllowFlag, 
								minAllowFlag, adlAllowFlag, csAllowFlag, RPEUEAllowFlag, RUCUEAllowFlag,
								RDEOEAllowFlag, RSEOEAllowFlag));

						//callSlotInformation(model, semesterSubId, registerNumber, courseAllocationList);
						session.setAttribute("courseCategory", courseCategory);

						model.addAttribute("crCourseStatus", crCourseStatus);
						model.addAttribute("tlcourseType", courseTypeArr);
						model.addAttribute("courseTypeDisplay", courseTypeDisplay);
						model.addAttribute("genericCourseTypeDisplay", genericCourseTypeDisplay);
						model.addAttribute("registrationOptionDesc", courseRegistrationCommonMongoService.getCourseOptionDescription(pProgramSpecId, StudyStartYear, registrationOption));


						if ((crCourseStatus == 1) && genericCourseType.equals("TH") && crGenericCourseType.equals("LO"))
						{
							model.addAttribute("courseCatalogModel", courseCatalog);
							model.addAttribute("courseCatalogModel2", courseCatalog2);
						}
						else if ((crCourseStatus == 1) && genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
						{
							model.addAttribute("courseCatalogModel", courseCatalog2);
							model.addAttribute("courseCatalogModel2", courseCatalog);
						}
						else
						{
							model.addAttribute("courseCatalogModel", courseCatalog);
							model.addAttribute("courseCatalogModel2", courseCatalog2);
						}

						model.addAttribute("regAllowFlag", regAllowFlag);
						model.addAttribute("wlAllowFlag", wlAllowFlag);
						model.addAttribute("epjSlotFlag", epjSlotFlag);
						model.addAttribute("rgrAllowFlag", rgrAllowFlag);
						model.addAttribute("minAllowFlag", minAllowFlag);
						model.addAttribute("honAllowFlag", honAllowFlag);
						model.addAttribute("RPEUEAllowFlag", RPEUEAllowFlag);
						model.addAttribute("RDEOEAllowFlag", RDEOEAllowFlag);
						model.addAttribute("RSEOEAllowFlag", RSEOEAllowFlag);
						model.addAttribute("csAllowFlag", csAllowFlag);
						model.addAttribute("RUCUEAllowFlag", RUCUEAllowFlag);
						model.addAttribute("page", page);
						model.addAttribute("srhType", searchType);
						model.addAttribute("srhVal", searchVal);
						model.addAttribute("courseOption", courseOption);
						model.addAttribute("registrationOption", registrationOption);						
						model.addAttribute("audAllowFlag", audAllowFlag);
						model.addAttribute("adlAllowFlag", adlAllowFlag);
						model.addAttribute("ProgramGroupCode", pProgramGroupCode);
						model.addAttribute("subCourseOption", subCourseOption);
						model.addAttribute("subCourseType", subCourseType);
						model.addAttribute("subCourseDate", subCourseDate);
						model.addAttribute("regularFlag", regularFlag);	
						model.addAttribute("tlCourseCategory", courseCategory);
						model.addAttribute("tlCompCourseList", compCourseList);
						model.addAttribute("crSubCourseOption", crSubCourseOption);
						model.addAttribute("crSubCourseType", crSubCourseType);
						model.addAttribute("crSubCourseDate", crSubCourseDate);

						session.setAttribute("courseOption", courseOption);


						break;  

					case 2:
						model.addAttribute("infoMessage", message);									
						urlPage = processRegistrationOption(registrationOption, model, session, 10, page, searchType, 
								searchVal, subCourseOption, request,subRegistrationOption);									
						break;  
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
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"processCourseRegistration", registerNumber, IpAddress);
				urlPage = "redirectpage";
				return urlPage;
			}		

			return urlPage;

		}


		@PostMapping(value="processRegisterProjectCourse")	
		public String processRegisterProjectCourse(	String costCentreId, String guideErpId, String projectTitle, 
				String courseOption, String courseCode, String courseType, String courseId,
				String clashSlot, String classId, String projectDuration,String projectOption, 
				@RequestParam(value = "pageSize", required = false) Integer pageSize,
				@RequestParam(value = "page", required = false) Integer page,
				@RequestParam(value = "searchType", required = false) Integer searchType,
				@RequestParam(value = "searchVal", required = false) String searchVal,
				@RequestParam(value = "subCourseOption", required = false)  String subCourseOption, 
				Model model, HttpSession session, HttpServletRequest request) 
		{			
			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");
			String authStatus = (String) session.getAttribute("authStatus");
			String urlPage = "", msg = null, message = null;
			
			int programSpecId = (Integer) session.getAttribute("ProgramSpecId");

			try
			{
				int authCheckStatus = courseRegCommonFn.validateCourseAuthKey(authStatus, registerNumber, courseId, 1);

				if ((registerNumber != null) && (authCheckStatus == 1))
				{

					String courseIdVar = (String) session.getAttribute("courseIdVar");

					if(!courseIdVar.equals(courseId))
					{
						model.addAttribute("flag", 1);
						urlPage = "redirectpage";
						return urlPage;
					}

					projectDuration =AppGlobalValues.CAPSTONE_PROJECT_DURATION;
					
					String pRegStatus = "",  csPjtMsg = "",RsemesterSubId="";
					String genericCourseType = "NONE", evaluationType = "NONE", gradeCategory = "";
					Integer projectStatus = 2, regStatus = 0;
					int csPjtFlag = 2,  semSubIdCharCount = 0;
					int checkGraduateYear = 2020;
					float maxCredit = globalValues.getMaxCredits(), minCredit = 16f;

					CourseCatalogModel ccm = null;
					List<CourseAllocationModel> projAllocationList = new ArrayList<>();

					String semesterSubId = (String) session.getAttribute("SemesterSubId");
					String studentCategory = (String) session.getAttribute("studentCategory");
					int approvalStatus = (Integer) session.getAttribute("approvalStatus");
					String registrationOption = (String) session.getAttribute("registrationOption");
					int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
					int StudentGraduateYear = (Integer) session.getAttribute("StudentGraduateYear");
					int semesterId  = (Integer) session.getAttribute("SemesterId");
					String ProgramGroupCode = (String) session.getAttribute("ProgramGroupCode");
					String ProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
					String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
					String costCentreCode = (String) session.getAttribute("costCentreCode");
					String courseCategory = (String) session.getAttribute("courseCategory");
					String studentCgpaData = (String) session.getAttribute("studentCgpaData");

					ccm = courseCatalogService.getOne(courseId);
					if (ccm != null)
					{
						genericCourseType = ccm.getGenericCourseType();
						evaluationType = ccm.getEvaluationType();
					}

					if (projectOption.equals("PAT"))
					{
						projAllocationList = courseAllocationService.getCourseAllocationCourseIdTypeEmpidList(
								semesterSubId, classGroupId, AppGlobalValues.CLASS_TYPE, courseId, courseType, 
								"PAT", ProgramGroupCode, ProgramSpecCode, costCentreCode,registerNumber);
						if (projAllocationList.isEmpty())
						{
							csPjtFlag = 2;
							csPjtMsg="PAT Project Not Available/Allocated.";
						}
						else
						{
							csPjtFlag = 1;
						}

						if (csPjtFlag == 1)
						{
							for (CourseAllocationModel pjtCam : projAllocationList)
							{
								classId = pjtCam.getClassId();
							}
						}

						projectTitle = "NONE";
						guideErpId = "PAT";
					}
					else
					{
						csPjtFlag = 1;
					}

					String courseOptionVal = (String) session.getAttribute("courseOption");


					if(!courseOptionVal.equals(courseOption))
					{
						model.addAttribute("flag", 1);
						urlPage = "redirectpage";
						return urlPage;
					}



					LOGGER.trace("\n "+ csPjtFlag +" | "+ courseOption);

					if (csPjtFlag == 1)
					{
						//Get Registration Status
						regStatus = courseRegCommonFn.getRegistrationStatus(approvalStatus, courseOption, 
								genericCourseType, evaluationType, studentCategory);

						//Get Grade Category
						gradeCategory = courseRegistrationService.getGradeCategory(studyStartYear, courseCategory, genericCourseType, ProgramGroupCode);

						//Project_Duration Assign
						if(evaluationType.equals("CAPSTONE") || evaluationType.equals("GUIDE"))
						{
							projectStatus = 1;
							RsemesterSubId = semesterSubId;

							if((projectDuration != null) && (!projectDuration.equals("")) 
									&& projectDuration.equals("12"))
							{
								if (semesterId == 1)
								{
									semSubIdCharCount = semesterSubId.length();

									if (semSubIdCharCount >= 10)
									{
										RsemesterSubId = semesterSubId.substring(0, (semesterSubId.length()-2)) +"05";
									}
									else
									{
										RsemesterSubId = semesterSubId.substring(0, (semesterSubId.length()-1)) +"5";
									}
								}
							}

						}
						
						ProgramSpecializationCurriculumDetailDto curriculumDetails = crMongoService.getBySpecIdAdmissionYearAndCourseCode(programSpecId, studyStartYear, courseCode);

						String curriculumCategory = curriculumDetails!=null ? curriculumDetails.getCourseCategory() : "UE";
						LOGGER.trace("*****PROJECT********curriculumCategory : "+curriculumCategory);

						pRegStatus = courseRegistrationReadWriteService.courseRegistrationAdd2(semesterSubId, classId, registerNumber, 
								courseId, courseType, courseOption, regStatus, 0, registerNumber, IpAddress, 
								"GEN", subCourseOption, "INSERT", "", "", gradeCategory,curriculumCategory);
						if (pRegStatus.equals("SUCCESS"))
						{
							msg = "Selected Project Course Successfully Registered";
						}
						else if ((pRegStatus.equals("FAIL")) || (pRegStatus.substring(0, 5).equals("error")))
						{
							message = "Technical error.";
						}
						else
						{
							message = pRegStatus;
						}

						if ((projectStatus == 1) && (pRegStatus.equals("SUCCESS")))
						{	
							//Add Project Registration
							courseRegistrationReadWriteService.saveProjectRegistration(semesterSubId, registerNumber, courseId, courseType, 
									classId, projectTitle, guideErpId, Integer.parseInt(projectDuration), RsemesterSubId, projectOption);


							//Fixing the Minimum & Maximum credit
							String[] creditLimitArr = courseRegCommonFn.getMinimumAndMaximumCreditLimit(semesterSubId, 
									registerNumber, ProgramGroupCode, costCentreCode, 
									studyStartYear, StudentGraduateYear, checkGraduateYear, 
									semesterId, ProgramSpecCode, studentCgpaData).split("\\|");
							minCredit = Float.parseFloat(creditLimitArr[0]);
							maxCredit = Float.parseFloat(creditLimitArr[1]);
							session.setAttribute("minCredit", minCredit);
							session.setAttribute("maxCredit", maxCredit);
						}							
					}
					else
					{
						message = csPjtMsg;
					}

					model.addAttribute("info", msg);								
					model.addAttribute("infoMessage", message);
					callCourseRegistrationTypes(registrationOption, pageSize, page, searchType, searchVal, session, model);

					urlPage = "mainpages/CourseList :: section";
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
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"processRegisterProjectCourse", registerNumber, IpAddress);
				urlPage = "redirectpage";
				return urlPage;
			}

			model.addAttribute("info", msg);
			model.addAttribute("infoMessage", message);

			return urlPage;		
		}


		@PostMapping(value = "processRegisterCourse")
		public String processRegisterCourse(String ClassID, String courseId, String courseType, String courseCode, 
				String courseOption, String clashSlot, String epjSlotFlag, 
				@RequestParam(value = "pageSize", required = false) Integer pageSize, 
				@RequestParam(value = "page", required = false) Integer page,
				@RequestParam(value = "searchType", required = false) Integer searchType, 
				@RequestParam(value = "searchVal", required = false) String searchVal,
				@RequestParam(value = "subCourseOption", required = false) String subCourseOption, 
				@RequestParam(value = "subCourseType", required = false) String subCourseType,
				@RequestParam(value = "subCourseDate", required = false) String subCourseDate,
				@RequestParam(value = "courseCat", required = false) String courseCat,
				String[] clArr, Integer crCourseStatus, String crCourseId, String crCourseCode, 
				String crCourseType, String crSubCourseOption, String crSubCourseType, 
				String crSubCourseDate, Model model, HttpSession session, HttpServletRequest request) 
		{

			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");
			String urlPage = "", message = null;

			try
			{
				String authStatus = (String) session.getAttribute("authStatus");
				int authCheckStatus = courseRegCommonFn.validateCourseAuthKey(authStatus, registerNumber, courseId, 1);			
				LOGGER.trace("\n authCheckStatus: "+ authCheckStatus +" | registerNumber: "+ registerNumber);

				if ((authCheckStatus == 1) && (registerNumber!=null))
				{


					String courseIdVar = (String) session.getAttribute("courseIdVar");

					if(!courseIdVar.equals(courseId))
					{
						model.addAttribute("flag", 1);
						urlPage = "redirectpage";
						return urlPage;
					}

					String courseOptionVal = (String) session.getAttribute("courseOption");


					if(!courseOptionVal.equals(courseOption))
					{
						model.addAttribute("flag", 1);
						urlPage = "redirectpage";
						return urlPage;
					}



					String msg = null, classId1 = "", classId2 = "", classId3 = "", classId = "";
					String labErpId = "", labAssoId = "", genericCourseType = "", evaluationType = "";
					String[] courseTypels = {}, classNbr = {}, regStatusArr = {};		
					String thyErpId = "", thyAssoId="", seatRegClassNbr = "", tempCourseId = "", gradeCategory = "";
					String pCourseIdArr = "", pClassIdArr = "", pCompTypeArr = "", pRegStatus = "", eqvCourseId = "", 
							eqvCourseType = "", eqvExamDate = "";

					int patternId = 0, emdPjtFlag = 1, seatRegFlg = 1, regTypeCount = 0, regCompType = 0, regStatusFlag = 2, regStatus = 0;
					long labSlotId = 0, thySlotId = 0;
					List<String> courseTypeArr = new ArrayList<>();

					String registrationOption = (String) session.getAttribute("registrationOption");
					String studentCategory = (String) session.getAttribute("studentCategory");
					int approvalStatus = (Integer) session.getAttribute("approvalStatus");
					int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
					String ProgramGroupCode = (String) session.getAttribute("ProgramGroupCode");
					String ProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
					String semesterSubId = (String) session.getAttribute("SemesterSubId");
					String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");				
					String costCentreCode = (String) session.getAttribute("costCentreCode");
					String courseCategory = (String) session.getAttribute("courseCategory");	
					int programSpecId = (Integer) session.getAttribute("ProgramSpecId");

					List<String> clashslot = new ArrayList<>();
					CourseCatalogModel ccm = null;				
					CourseAllocationModel courseAllocationModel = null;
					CourseAllocationModel courseAllocationModel2 = null;

					ccm = courseCatalogService.getOne(courseId);
					if (ccm != null)
					{
						genericCourseType = ccm.getGenericCourseType();
						evaluationType = ccm.getEvaluationType();
					}

					courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
					if (crCourseStatus == 1)
					{
						courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(crCourseType));
					}

					for (String courseList : clArr) 
					{	
						switch(courseList)
						{
						case "ELA":
							courseTypels = ClassID.split(",");									
							classNbr = courseTypels[1].split("/");									
							classId2 = classNbr[1];
							break;

						case "EPJ":
							courseTypels = ClassID.split(",");									
							classNbr = courseTypels[2].split("/");									
							classId3 = classNbr[1];
							break;

						default:
							if ((crCourseStatus == 1) && courseList.equals("LO"))
							{
								courseTypels = ClassID.split(",");									
								classNbr = courseTypels[1].split("/");									
								classId2 = classNbr[1];
							}
							else
							{
								courseTypels = ClassID.split(",");
								classNbr = courseTypels[0].split("/");									
								classId1 = classNbr[1];
							}
							break;
						}							
					}						

					for (String courseList : clArr) 
					{							
						courseAllocationModel = new CourseAllocationModel();
						courseAllocationModel2 = new CourseAllocationModel();							

						if (!courseList.equals("EPJ"))
						{
							if(courseList.equals("ELA"))
							{
								courseAllocationModel = courseAllocationService.getOne(classId2);
								if (courseAllocationModel != null)
								{
									labErpId = courseAllocationModel.getErpId();
									labSlotId = courseAllocationModel.getSlotId();
									labAssoId = courseAllocationModel.getAssoClassId();
									patternId = courseAllocationModel.getTimeTableModel().getPatternId();
								}

								switch(genericCourseType)
								{
								case "ETLP":
								case "ELP":
									courseAllocationModel2 = courseAllocationService.getCourseAllocationCourseIdTypeEmpidSlotAssoList(semesterSubId,
											classGroupId, AppGlobalValues.CLASS_TYPE, courseId, "EPJ", labErpId, labSlotId, labAssoId, 
											ProgramGroupCode, ProgramSpecCode, costCentreCode);
									if (courseAllocationModel2!=null)
									{
										classId3 = courseAllocationModel2.getClassId();
									}
									else
									{
										emdPjtFlag = 2;
									}
									break;
								}

							}
							else
							{
								if ((crCourseStatus == 1) && courseList.equals("LO"))
								{
									courseAllocationModel = courseAllocationService.getOne(classId2);
									if (courseAllocationModel != null)
									{
										labErpId = courseAllocationModel.getErpId();
										labSlotId = courseAllocationModel.getSlotId();
										labAssoId = courseAllocationModel.getAssoClassId();
										patternId = courseAllocationModel.getTimeTableModel().getPatternId();
									}
								}
								else
								{
									courseAllocationModel = courseAllocationService.getOne(classId1);
									if (courseAllocationModel != null)
									{
										thyErpId = courseAllocationModel.getErpId();
										thySlotId = courseAllocationModel.getSlotId();
										thyAssoId = courseAllocationModel.getAssoClassId();	
										patternId = courseAllocationModel.getTimeTableModel().getPatternId();
									}

									if(genericCourseType.equals("ETP"))
									{
										if (courseList.equals("ETH"))
										{
											courseAllocationModel2 = courseAllocationService.getCourseAllocationCourseIdTypeEmpidSlotAssoList(semesterSubId,
													classGroupId, AppGlobalValues.CLASS_TYPE, courseId, "EPJ", thyErpId, thySlotId, thyAssoId, 
													ProgramGroupCode, ProgramSpecCode, costCentreCode);

											if (courseAllocationModel2!=null)
											{
												classId3 = courseAllocationModel2.getClassId();
											}
											else
											{
												emdPjtFlag = 2;
											}
										}												
									}
								}
							}
						}

						if ((!courseList.equals("EPJ")) && (courseAllocationModel.getSlotId() > 0))
						{
							clashslot.add(courseAllocationModel.getTimeTableModel().getClashSlot());
						}								
					}

					//Get the Registration Status
					regStatus = courseRegCommonFn.getRegistrationStatus(approvalStatus, courseOption, 
							genericCourseType, evaluationType, studentCategory);

					//Get Grade Category
					gradeCategory = courseRegistrationService.getGradeCategory(studyStartYear, courseCategory, genericCourseType, ProgramGroupCode);

					regStatusArr = courseRegCommonFn.checkClash(patternId, clashslot, semesterSubId, registerNumber, "ADD", "", 
							"", Arrays.asList("BVOC", "INT", "MBA", "ST002", "ST004")).split("/");
					regStatusFlag = Integer.parseInt(regStatusArr[0]);
					LOGGER.trace("\n regStatusFlag: "+ regStatusFlag +" | Message: "+ regStatusArr[1]);

					if (regStatusFlag == 2)
					{
						message = regStatusArr[1];
						model.addAttribute("infoMessage", message);
						return loadProcessCourseRegistration(courseId, page, searchType, searchVal, model, session, request);
					}
					else
					{								
						for (String courseList : clArr) 
						{							
							switch(courseList)
							{	
							case "ELA":
								seatRegClassNbr = classId2;										
								message =  "Lab Component Seats not available";
								break;
							case "EPJ":
								seatRegClassNbr = classId3;											
								message = "Project Component Seats not available";
								break;
							default:
								if ((crCourseStatus == 1) && courseList.equals("LO"))
								{
									seatRegClassNbr = classId2;										
									message =  "Lab Component Seats not available";
								}
								else
								{
									seatRegClassNbr = classId1;											
									switch(courseList)
									{
									case "ETH":
									case "TH":
										message = "Theory Component Seats not available";
										break;
									case "SS":
										message = "Softskills Component Seats not available";
										break;
									case "LO":
										message = "Lab Component Seats not available";
										break;
									default:
										message = "Seats not available";
										break;
									}
								}
								break;
							}

							if (courseAllocationService.getAvailableRegisteredSeats(seatRegClassNbr) <= 0) 
							{
								seatRegFlg = 2;
								break;
							}									
						}								

						if ((emdPjtFlag == 1) && (seatRegFlg == 2))
						{
							model.addAttribute("infoMessage", message);
						}
						else
						{
							message = null;
						}						

						if ((regStatusFlag == 1) && (seatRegFlg == 1) && (emdPjtFlag == 1))
						{
							regTypeCount = 0;

							for (String courseList : clArr) 
							{
								switch(courseList)
								{
								case "ELA":
									classId = classId2;
									tempCourseId = courseId;
									break;
								case "EPJ":
									classId = classId3;
									tempCourseId = courseId;
									break;
								default:
									if ((crCourseStatus == 1) && courseList.equals("LO"))
									{
										classId = classId2;
										tempCourseId = crCourseId;
									}
									else
									{
										classId = classId1;
										tempCourseId = courseId;
									}
									break;
								}

								if (pCompTypeArr.equals(""))
								{
									pClassIdArr = classId;
									pCompTypeArr = courseList;
									pCourseIdArr = tempCourseId;
								}
								else
								{
									pClassIdArr = pClassIdArr +"|"+ classId;
									pCompTypeArr = pCompTypeArr +"|"+ courseList;
									pCourseIdArr = pCourseIdArr +"|"+ tempCourseId;
								}

								regTypeCount = regTypeCount + 1;										
							}

							if ((!subCourseOption.equals("")) && (!subCourseOption.equals(null)))
							{
								switch(courseOption)
								{
								case "RR":
								case "RRCE":
								case "GI":
								case "GICE":
								case "RGCE":
								case "RPCE":
								case "RWCE":											
									for (String e: subCourseType.split(","))
									{
										if (eqvCourseType.equals(""))
										{
											eqvCourseType = e;
											eqvCourseId = subCourseOption;
											eqvExamDate = subCourseDate;
										}
										else
										{
											eqvCourseType = eqvCourseType +"|"+ e;
											eqvCourseId = eqvCourseId +"|"+ subCourseOption;
											eqvExamDate = eqvExamDate +"|"+ subCourseDate;
										}
									}

									if (crCourseStatus == 1)
									{
										for (String e: crSubCourseType.split(","))
										{
											if (eqvCourseType.equals(""))
											{
												eqvCourseType = e;
												eqvCourseId = crSubCourseOption;
												eqvExamDate = crSubCourseDate;
											}
											else
											{
												eqvCourseType = eqvCourseType +"|"+ e;
												eqvCourseId = eqvCourseId +"|"+ crSubCourseOption;
												eqvExamDate = eqvExamDate +"|"+ crSubCourseDate;
											}
										}
									}
									break;

								case "CS":
									String[] subCrsOptArr = subCourseOption.split("/");
									subCourseOption = subCrsOptArr[0];
									subCourseType = subCrsOptArr[1];
									subCourseDate = subCrsOptArr[2];

									for (@SuppressWarnings("unused") String courseList : clArr) 
									{
										if (eqvCourseType.equals(""))
										{
											eqvCourseType = subCourseType;
											eqvCourseId = subCourseOption;
											eqvExamDate = subCourseDate;
										}
										else
										{
											eqvCourseType = eqvCourseType +"|"+ subCourseType;
											eqvCourseId = eqvCourseId +"|"+ subCourseOption;
											eqvExamDate = eqvExamDate +"|"+ subCourseDate;
										}
									}

									break;

								case "MIN":
								case "HON":											
									for (@SuppressWarnings("unused") String courseList : clArr) 
									{
										if (eqvCourseId.equals(""))
										{
											eqvCourseId = subCourseOption;
										}
										else
										{
											eqvCourseId = eqvCourseId +"|"+ subCourseOption;
										}
									}
									String studySystem = (String)session.getAttribute("studentStudySystem");

									if(AppGlobalValues.ACTIVITY_EVENT.equals("ADDDROP") 
									&& studySystem!=null && studySystem.equals("CBCS"))
									{
										regStatus = 10;
									}


									break;
								}
							}


						int regComponentEquivWithCoReq = Integer.parseInt(session.getAttribute("regComponentEquivWithCoReq").toString());

						if(regComponentEquivWithCoReq==2)
						{
							regCompType = 2;
						}
						else
						{
							if(courseOption.startsWith("RR"))
							{
								List<String> equivalenceCourseTypes = courseCatalogService.getCourseTypesByCourseId(subCourseOption);
								if(regTypeCount != courseTypeArr.size() || 
										(clArr!=null && !Arrays.asList(clArr).containsAll(equivalenceCourseTypes))
										)
								{
									regCompType = 1;
								}
							}
							else if (regTypeCount != courseTypeArr.size())
							{
								regCompType = 1;
							}
						}
							

							LOGGER.trace("\n semesterSubId: "+ semesterSubId +" | pClassIdArr: "+ pClassIdArr 
									+" | registerNumber: "+ registerNumber +" | courseId: "+ courseId 
									+" | pCompTypeArr: "+ pCompTypeArr +" | courseOption: "+ courseOption 
									+" | regStatus: "+ regStatus +" | regCompType: "+ regCompType 
									+" | registerNumber: "+ registerNumber +" | IpAddress: "+ IpAddress 
									+" | eqvCourseId: "+ eqvCourseId +" | eqvCourseType: "+ eqvCourseType 
									+" | eqvExamDate: "+ eqvExamDate+" | registrationOption : "+registrationOption);

							ProgramSpecializationCurriculumDetailDto curriculumDetails = crMongoService.getBySpecIdAdmissionYearAndCourseCode(programSpecId, studyStartYear, courseCode);

							String curriculumCategory = curriculumDetails!=null ? curriculumDetails.getCourseCategory() :
							 (registrationOption.equals("ME") ? "ME" : "UE") ;
							LOGGER.trace("*************curriculumCategory : "+curriculumCategory);
							
							String regOptDesc="";
							
							if(registrationOption.equals("CBCSMIN"))
							{
								curriculumCategory="MN";
								courseOption="MIN";
								regOptDesc="MINOR";
							}
							
							if(registrationOption.equals("ACEMIN"))
							{
								curriculumCategory="MN";
								courseOption="MIN";
								regOptDesc="MINOR";
							}
							if(registrationOption.equals("CBCSHON"))
							{
								curriculumCategory="HN";
								courseOption="HON";
								regOptDesc="HONOUR";

							}

							if(registrationOption.equals("CON") && courseCat!=null && !courseCat.isEmpty())
							{
								curriculumCategory=courseCat;
							}
							

							pRegStatus = courseRegistrationReadWriteService.courseRegistrationAdd2(semesterSubId, pClassIdArr, 
									registerNumber, courseId, pCompTypeArr, courseOption, regStatus, regCompType, 
									registerNumber, IpAddress, "GEN", eqvCourseId, "INSERT", eqvCourseType, 
									eqvExamDate, gradeCategory,curriculumCategory);

							if (pRegStatus.equals("SUCCESS"))
							{
								msg = "Selected Course Successfully Registered";

								String optedCBCSMinor = "";
								String subRegistrationOption = (String) session.getAttribute("subRegistrationOption");
								
								if(registrationOption.equals("CBCSMIN"))
								{
									optedCBCSMinor = (String) session.getAttribute("optedCBCSMinor");
								}
								else if(registrationOption.equals("ACEMIN"))
								{
									optedCBCSMinor = (String) session.getAttribute("optedCBCSMinor");
								}
								else
								{
									optedCBCSMinor = (String) session.getAttribute("optedCBCSHonour");
								}

								if(registrationOption!=null && (registrationOption.equals("CBCSMIN") ||registrationOption.equals("ACEMIN"))
										&& (subRegistrationOption!=null && !subRegistrationOption.isEmpty())
										&& (optedCBCSMinor==null || optedCBCSMinor.isEmpty()))
								{
									courseRegistrationCommonMongoService.saveAddlOpted(subRegistrationOption, registerNumber,
											regOptDesc, semesterSubId, session);
									session.setAttribute("optedCBCSMinor", subRegistrationOption);
								}
								else if(registrationOption!=null && (registrationOption.equals("CBCSHON"))
										&& (subRegistrationOption!=null && !subRegistrationOption.isEmpty())
										&& (optedCBCSMinor==null || optedCBCSMinor.isEmpty()))
								{
									courseRegistrationCommonMongoService.saveAddlOpted(subRegistrationOption, registerNumber,
											regOptDesc, semesterSubId, session);
									session.setAttribute("optedCBCSHonour", subRegistrationOption);
								}

							}
							else if ((pRegStatus.equals("FAIL")) || (pRegStatus.substring(0, 5).equalsIgnoreCase("error")))
							{
								message = "Technical error.";
							}
							else
							{
								message = pRegStatus;
							}
						}							
					}						

					session.setAttribute("authStatus", "NONE");						
					callCourseRegistrationTypes(registrationOption, pageSize, page, searchType, searchVal, session, model);
					model.addAttribute("info", msg);

					urlPage = "mainpages/CourseList :: section";

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
				exception.printStackTrace();

				//exception.printStackTrace();

				session.setAttribute("authStatus", "NONE");
				model.addAttribute("flag", 1);

				courseRegistrationReadWriteService.addErrorLog(exception, 
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"processRegisterCourse", registerNumber, IpAddress);

				urlPage = "redirectpage";
				return urlPage;
			}

			model.addAttribute("infoMessage", message);
			return urlPage;		
		}

		@PostMapping("processSearch")
		public String processSearch(Model model, HttpSession session, 
				@RequestParam(value = "pageSize", required = false) Integer pageSize,
				@RequestParam(value = "page", required = false) Integer page, 
				@RequestParam(value = "searchType", required = false) Integer searchType,
				@RequestParam(value = "searchVal", required = false) String searchVal, 
				@RequestParam(value = "subCourseOption", required = false) String subCourseOption, 
				HttpServletRequest request) 
		{	
			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");
			String  urlPage = "";

			try 
			{
				if (registerNumber != null)
				{	

					String registrationOption = (String) session.getAttribute("registrationOption");				


					callCourseRegistrationTypes(registrationOption, pageSize, page, searchType, searchVal, 
							session, model);
					model.addAttribute("registrationOption", registrationOption);
					model.addAttribute("searchFlag", 1);
					urlPage = "mainpages/CourseList::section";						

				}
				else
				{
					model.addAttribute("flag", 1);
					urlPage = "redirectpage";
					return urlPage;
				}
			} 
			catch (Exception ex) 
			{
				model.addAttribute("flag", 1);
				courseRegistrationReadWriteService.addErrorLog(ex, 
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"processSearch", registerNumber, IpAddress);
				urlPage = "redirectpage";			
				return urlPage;
			}

			return urlPage; 
		}

		@PostMapping(value="viewCorrespondingCourse")
		public String viewCorrespondingCourse(String courseId, String erpId, String genericCourseType, String classId, 
				@RequestParam(value = "page", required = false) Integer page,
				@RequestParam(value = "searchType", required = false) Integer searchType,
				@RequestParam(value = "searchVal", required = false) String searchVal, 
				Model model, HttpSession session, HttpServletRequest request)
		{
			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");
			String urlPage = "", message = null;
			
			try
			{
				if (registerNumber != null)
				{
					int ethExistFlag = 2, epjExistFlag = 2, epjSlotFlag = 2,  crCourseStatus = 2;
					int regStatusFlag = 2, regAllowFlag = 1, wlAllowFlag = 1, audAllowFlag = 1, rgrAllowFlag = 2, minAllowFlag = 2, 
							honAllowFlag = 2, adlAllowFlag = 2, RPEUEAllowFlag = 2, csAllowFlag = 2, 
							RUCUEAllowFlag = 2,RDEOEAllowFlag=2, RSEOEAllowFlag=2;

					String courseTypeDisplay = "", courseOption = "";
					String courseCategory = "NONE", courseCode = "", genericCourseTypeDisplay = "", ccCourseId = "", 
							ccCourseSystem = "", crCourseId = "", crGenericCourseType = "", crSubCourseOption = "", 
							crSubCourseType = "", crSubCourseDate = "";		
					String[] regStatusArr = {};

					List<String> courseTypeArr = new ArrayList<>();
					CourseCatalogModel courseCatalog = null;
					CourseCatalogModel courseCatalog2 = null;

					String semesterSubId = (String) session.getAttribute("SemesterSubId");
					String authStatus = (String) session.getAttribute("authStatus");
					String corAuthStatus = (String) session.getAttribute("corAuthStatus");

					int authCheckStatus = courseRegCommonFn.validateCourseAuthKey(authStatus, registerNumber, courseId, 1);
					if (authCheckStatus == 1)
					{						
						int pProgramGroupId = (Integer) session.getAttribute("ProgramGroupId");
						String pProgramGroupCode = (String) session.getAttribute("ProgramGroupCode"); 
						int pProgramSpecId = (Integer) session.getAttribute("ProgramSpecId");
						int regularFlag = (Integer) session.getAttribute("regularFlag");
						String studentStudySystem = (String) session.getAttribute("studentStudySystem");
						float curriculumVersion = (Float) session.getAttribute("curriculumVersion");					
						String registrationOption = (String) session.getAttribute("registrationOption");
						String subCourseOption = (String) session.getAttribute("subCourseOption");
						int StudyStartYear = (Integer) session.getAttribute("StudyStartYear");
						String subRegistrationOption = (String) session.getAttribute("subRegistrationOption");

						@SuppressWarnings("unchecked")
						List<String> compCourseList = (List<String>) session.getAttribute("compulsoryCourseList");

						String subCourseType = "", subCourseDate = "";
						List<CourseAllocationModel> courseAllocationList = new ArrayList<>();

						@SuppressWarnings("unchecked")
						List<CourseAllocationModel> list1 = ((session.getAttribute("camList") != null) && (!session.getAttribute("camList").equals(""))) 
						? (List<CourseAllocationModel>) session.getAttribute("camList") 
								: new ArrayList<CourseAllocationModel>();

						@SuppressWarnings("unchecked")
						List<CourseAllocationModel> ela = ((session.getAttribute("camList2") != null) && (!session.getAttribute("camList2").equals(""))) 
						? (List<CourseAllocationModel>) session.getAttribute("camList2") 
								: new ArrayList<CourseAllocationModel>();

						@SuppressWarnings("unchecked")
						List<CourseAllocationModel> epj = ((session.getAttribute("camList3") != null) && (!session.getAttribute("camList3").equals("")))
						? (List<CourseAllocationModel>) session.getAttribute("camList3") 
								: new ArrayList<CourseAllocationModel>();


						courseCatalog = courseCatalogService.getOne(courseId);
						if (courseCatalog != null)
						{
							courseCode = courseCatalog.getCode();
							ccCourseSystem = courseCatalog.getCourseSystem();	
							genericCourseTypeDisplay = courseCatalog.getCourseTypeComponentModel().getDescription();
						}

						LOGGER.trace("\n corAuthStatus: "+ corAuthStatus);

						regStatusArr = corAuthStatus.split("/");
						regStatusFlag = authCheckStatus;
						courseOption = regStatusArr[0];
						regAllowFlag = Integer.parseInt(regStatusArr[1]);
						wlAllowFlag = Integer.parseInt(regStatusArr[2]);
						audAllowFlag = Integer.parseInt(regStatusArr[3]);
						rgrAllowFlag= Integer.parseInt(regStatusArr[4]);
						minAllowFlag = Integer.parseInt(regStatusArr[5]);
						honAllowFlag = Integer.parseInt(regStatusArr[6]);
						courseCategory = regStatusArr[7];
						adlAllowFlag = Integer.parseInt(regStatusArr[8]);
						RPEUEAllowFlag = Integer.parseInt(regStatusArr[9]);
						csAllowFlag = Integer.parseInt(regStatusArr[14]);
						RUCUEAllowFlag = Integer.parseInt(regStatusArr[15]);
						ccCourseId = regStatusArr[16];
						crCourseStatus = Integer.parseInt(regStatusArr[17]);
						crCourseId = regStatusArr[18];
						crGenericCourseType = regStatusArr[20];
						RDEOEAllowFlag = Integer.parseInt(regStatusArr[24]);
						RSEOEAllowFlag = Integer.parseInt(regStatusArr[25]);
						if (crCourseStatus == 1)
						{
							courseCatalog2 = courseCatalogService.getOne(crCourseId);
						}

						switch(courseOption)
						{
						case "RR":
						case "RRCE":
							if (!regStatusArr[10].equals("NONE"))
							{																				
								if ((crCourseStatus == 1) && genericCourseType.equals("TH") && crGenericCourseType.equals("LO"))
								{
									courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
									courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(crGenericCourseType));
								}
								else if ((crCourseStatus == 1) && genericCourseType.equals("LO") && crGenericCourseType.equals("TH"))
								{
									courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(crGenericCourseType));
									courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
								}
								else
								{
									courseTypeArr = Arrays.asList(regStatusArr[10].split(","));
								}
							}

							if (courseTypeArr.size() <= 0)
							{
								courseTypeArr = semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType);
							}									
							break;

						default:
							courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
							if ((crCourseStatus == 1) && (crGenericCourseType.equals("LO")))
							{
								courseTypeArr.addAll(semesterMasterService.getCourseTypeComponentByGenericType(crGenericCourseType));
							}
							break;
						}

						switch(courseOption)
						{
						case "RR":
						case "RRCE":
						case "GI":
						case "GICE":
						case "RGCE":
						case "RPCE":
						case "RWCE":
							if (crCourseStatus == 1)
							{
								crSubCourseOption = regStatusArr[21]; 
								crSubCourseType = regStatusArr[22];
								crSubCourseDate = regStatusArr[23];
							}
							subCourseOption = regStatusArr[11];
							subCourseType = regStatusArr[12];
							subCourseDate = regStatusArr[13];
							break;

						default:
							if (regStatusArr[11].equals("NONE"))
							{
								subCourseOption = "";
							}
							break;
						}

						for (String crstp: courseTypeArr) 
						{
							if (courseTypeDisplay.equals(""))
							{
								courseTypeDisplay = semesterMasterService.getCourseTypeMasterByCourseType(crstp).getDescription();
							}
							else
							{
								courseTypeDisplay = courseTypeDisplay +" / "+ semesterMasterService.getCourseTypeMasterByCourseType(crstp).getDescription();
							}

							if (crstp.equals("ETH"))
							{
								ethExistFlag = 1;
							}
							else if (crstp.equals("EPJ"))
							{
								epjExistFlag = 1;
							}						   
						}

						if ((courseTypeArr.size() == 2) && (genericCourseType.equals("ETLP")) 
								&& (ethExistFlag == 1) && (epjExistFlag == 1))
						{
							epjSlotFlag = 1;
						}
						else if ((courseTypeArr.size() == 1) && (epjExistFlag == 1))
						{
							epjSlotFlag = 1;
						}

						switch(regStatusFlag)
						{    
						case 1:								
							if (courseTypeArr.size() > 0) 
							{
								for (String crtp : courseTypeArr) 
								{											
									switch(crtp)
									{
									case "EPJ":
										model.addAttribute("cam3", epj);
										break;

									case "ELA":
										switch(genericCourseType)
										{
										case "ETLP":
										case "ETL":
											model.addAttribute("cam2", courseAllocationService.getAllocationByEmployeeId(ela, erpId, ccCourseSystem, classId));
											break;

										default:
											model.addAttribute("cam2", ela);
											break;
										}													
										break;

									default:
										if ((crCourseStatus == 1) && (crtp.equals("LO")))
										{
											model.addAttribute("cam2", courseAllocationService.getAllocationByEmployeeId(ela, erpId, ccCourseSystem, classId));
										}
										else
										{
											model.addAttribute("cam", list1);
										}
										break;
									}																		
								}
							}

							//Assigning the all course type of allocation list to one course allocation list									
							if (!list1.isEmpty())
							{
								courseAllocationList.addAll(list1);
							}

							if (!ela.isEmpty())
							{
								courseAllocationList.addAll(ela);
							}

							model.addAttribute("shcssList", studentHistoryService.getStudentHistoryCS2(registerNumber, 
									courseCode, studentStudySystem, pProgramSpecId, StudyStartYear, curriculumVersion, 
									semesterSubId, courseCategory, courseOption, ccCourseId, csAllowFlag));
							model.addAttribute("minorList", semesterMasterService.getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(
									minAllowFlag, "MIN", pProgramGroupId, pProgramSpecId, courseCode, studentStudySystem));
							model.addAttribute("honorList", semesterMasterService.getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(
									honAllowFlag, "HON", pProgramGroupId, pProgramSpecId, courseCode, studentStudySystem));
							model.addAttribute("courseOptionList",semesterMasterService.getRegistrationCourseOption(
									courseOption, genericCourseType, rgrAllowFlag, audAllowFlag, honAllowFlag, 
									minAllowFlag, adlAllowFlag, csAllowFlag, RPEUEAllowFlag, RUCUEAllowFlag,
									RDEOEAllowFlag,RSEOEAllowFlag));

							//	callSlotInformation(model, semesterSubId, registerNumber, courseAllocationList);

							model.addAttribute("regAllowFlag", regAllowFlag);
							model.addAttribute("regularFlag", regularFlag);
							model.addAttribute("wlAllowFlag", wlAllowFlag);
							model.addAttribute("rgrAllowFlag", rgrAllowFlag);
							model.addAttribute("minAllowFlag", minAllowFlag);
							model.addAttribute("honAllowFlag", honAllowFlag);
							model.addAttribute("RPEUEAllowFlag", RPEUEAllowFlag);
							model.addAttribute("RDEOEAllowFlag",RDEOEAllowFlag);
							model.addAttribute("RSEOEAllowFlag", RSEOEAllowFlag);
							model.addAttribute("csAllowFlag", csAllowFlag);
							model.addAttribute("RUCUEAllowFlag", RUCUEAllowFlag);
							model.addAttribute("courseCatalogModel", courseCatalog);
							model.addAttribute("epjSlotFlag", epjSlotFlag);
							model.addAttribute("page", page);
							model.addAttribute("srhType", searchType);
							model.addAttribute("srhVal", searchVal);
							model.addAttribute("courseOption", courseOption);
							model.addAttribute("registrationOption", registrationOption);
							model.addAttribute("audAllowFlag", audAllowFlag);
							model.addAttribute("adlAllowFlag", adlAllowFlag);
							model.addAttribute("tlcourseType", courseTypeArr);					
							model.addAttribute("courseTypeDisplay", courseTypeDisplay);
							model.addAttribute("genericCourseTypeDisplay", genericCourseTypeDisplay);
							model.addAttribute("ProgramGroupCode", pProgramGroupCode);
							model.addAttribute("tlClassId", classId);
							model.addAttribute("subCourseOption", subCourseOption);
							model.addAttribute("subCourseType", subCourseType);
							model.addAttribute("subCourseDate", subCourseDate);
							model.addAttribute("tlCourseCategory", courseCategory);
							model.addAttribute("tlCompCourseList", compCourseList);
							model.addAttribute("crCourseStatus", crCourseStatus);
							model.addAttribute("courseCatalogModel2", courseCatalog2);
							model.addAttribute("crSubCourseOption", crSubCourseOption);
							model.addAttribute("crSubCourseType", crSubCourseType);
							model.addAttribute("crSubCourseDate", crSubCourseDate);

							urlPage = "mainpages/CourseRegistration :: section";

							break;

						case 2:
							model.addAttribute("infoMessage", message);
							urlPage = processRegistrationOption(registrationOption, model, session, 10, page, searchType, 
									searchVal, subCourseOption, request,subRegistrationOption);							 
							break;  
						}							
					}
					else
					{
						model.addAttribute("flag", 1);
						urlPage = "redirectpage";
						return urlPage;
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
				//exception.printStackTrace();
				LOGGER.trace(exception);

				model.addAttribute("flag", 1);
				courseRegistrationReadWriteService.addErrorLog(exception, 
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"viewCorrespondingCourse", registerNumber, IpAddress);
				urlPage = "redirectpage";
				return urlPage;
			}

			return urlPage;
		}



		@PostMapping("processViewSlots")
		public String ProcessViewSlots(String courseId, @RequestParam(value = "page", required = false) Integer page,
				@RequestParam(value = "searchType", required = false) Integer searchType,
				@RequestParam(value = "searchVal", required = false) String searchVal, 
				Model model, HttpSession session, HttpServletRequest request)
		{
			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");	
			String urlPage = "";

			try
			{
				if (registerNumber != null)
				{	
					int checkEligibleStatus = 2;
					String courseCode = "", genericCourseType = "", courseSystem = "", crCourseCode = "", crCourseId = "", 
							crGenericCourseType = "";
					CourseCatalogModel ccm = new CourseCatalogModel();
					CourseCatalogModel ccm2 = null;

					List<CourseAllocationModel> courseAllocationList = new ArrayList<>();
					List<CourseAllocationModel> courseAllocationList2 = null;
					List<String> courseTypeList = new ArrayList<>();
					Map<String, List<CourseAllocationModel>> camMapList = new HashMap<>();

					String semesterSubId = (String) session.getAttribute("SemesterSubId");
					String[] classGroupId = session.getAttribute("classGroupId").toString().split("/");
					String ProgramGroupCode = (String) session.getAttribute("ProgramGroupCode");
					String ProgramSpecCode = (String) session.getAttribute("ProgramSpecCode");
					String costCentreCode = (String) session.getAttribute("costCentreCode");
					String registrationOption = (String) session.getAttribute("registrationOption");
					@SuppressWarnings("unchecked")
					List<String> compCourseList = (List<String>) session.getAttribute("compulsoryCourseList");


					ccm = courseCatalogService.getOne(courseId);
					if (ccm != null)
					{
						courseCode = ccm.getCode();
						genericCourseType = ccm.getGenericCourseType();
						courseSystem = ccm.getCourseSystem();
						crCourseCode = ccm.getCorequisite();

						if ((crCourseCode == null) || crCourseCode.trim().equals("NIL") || crCourseCode.trim().equals("NONE"))
						{
							crCourseCode = "";
						}
					}

					courseTypeList.addAll(semesterMasterService.getCourseTypeComponentByGenericType(genericCourseType));
					if (!courseTypeList.isEmpty())
					{
						for (String crtp : courseTypeList)
						{
							courseAllocationList2 = new ArrayList<CourseAllocationModel>();

							courseAllocationList2 = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
									classGroupId, AppGlobalValues.CLASS_TYPE, courseId, crtp, ProgramGroupCode, 
									ProgramSpecCode, costCentreCode,registrationOption,registerNumber);
							if (!courseAllocationList2.isEmpty())
							{
								camMapList.put(crtp, courseAllocationList2);
								courseAllocationList.addAll(courseAllocationList2);
							}
						}
					}

					if (courseSystem.equals("CBCS") && (genericCourseType.equals("TH") || genericCourseType.equals("LO")) 
							&& (!crCourseCode.trim().equals("")))
					{
						ccm2 = courseCatalogService.getOfferedCourseDetailByCourseCode(semesterSubId, classGroupId, AppGlobalValues.CLASS_TYPE, crCourseCode);
						if (ccm2 != null)
						{
							crCourseId = ccm2.getCourseId();
							crGenericCourseType = ccm2.getGenericCourseType();

							courseTypeList.add(crGenericCourseType);
						}

						courseAllocationList2 = new ArrayList<CourseAllocationModel>();

						courseAllocationList2 = courseAllocationService.getCourseAllocationCourseIdTypeList(semesterSubId, 
								classGroupId, AppGlobalValues.CLASS_TYPE, crCourseId, crGenericCourseType, ProgramGroupCode, 
								ProgramSpecCode, costCentreCode,registrationOption,registerNumber);
						if (!courseAllocationList2.isEmpty())
						{
							camMapList.put(crGenericCourseType, courseAllocationList2);
							courseAllocationList.addAll(courseAllocationList2);
						}
					}

					if (courseRegistrationService.getByRegisterNumberCourseCode(semesterSubId, registerNumber, courseCode).isEmpty())
					{
						checkEligibleStatus = 1;
					}

					model.addAttribute("CourseDetails", ccm);
					model.addAttribute("CourseDetails2", ccm2);
					model.addAttribute("courseTypeList", courseTypeList);
					model.addAttribute("CourseSlotDetails", camMapList);
					model.addAttribute("checkEligibleStatus", checkEligibleStatus);

					model.addAttribute("page", page);
					model.addAttribute("srhType", searchType);
					model.addAttribute("srhVal", searchVal);
					model.addAttribute("genericCourseType", genericCourseType);
					model.addAttribute("tlCompCourseList", compCourseList);

					//callSlotInformation(model, semesterSubId, registerNumber, courseAllocationList);

					urlPage = "mainpages/ViewSlots::section";
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
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"processViewSlots", registerNumber, IpAddress);
				urlPage = "redirectpage";
				return urlPage;
			}

			return urlPage;
		}

		@PostMapping(value="processPageNumbers")
		public String processPageNumbers(Model model, HttpSession session, HttpServletRequest request, 
				@RequestParam(value="pageSize", required=false) Integer pageSize,
				@RequestParam(value="page", required=false) Integer page, 
				@RequestParam(value="searchType", required=false) Integer searchType, 
				@RequestParam(value="searchVal", required=false) String searchVal, 
				@RequestParam(value="totalPage", required=false) Integer totalPage, 
				@RequestParam(value="processType", required=false) Integer processType)
		{
			String registerNumber = (String) session.getAttribute("RegisterNumber");
			String IpAddress = (String) session.getAttribute("IpAddress");
			String urlPage = "";

			LOGGER.trace("\n registerNumber: "+ registerNumber +" | IpAddress: "+ IpAddress);
			LOGGER.trace("\n pageSize: "+ pageSize +" | page: "+ page +" | searchType: "+ searchType 
					+" | searchVal: "+ searchVal +" | totalPage: "+ totalPage +" | processType: "+ processType);

			try
			{
				if (registerNumber != null)
				{				
					Pager pager = null;		
					int evalPageSize = AppGlobalValues.INITIAL_PAGE_SIZE;
					int evalPage = AppGlobalValues.INITIAL_PAGE;
					evalPageSize = pageSize == null ? AppGlobalValues.INITIAL_PAGE_SIZE : pageSize;
					evalPage = (page == null || page < 1) ? AppGlobalValues.INITIAL_PAGE : page - 1;
					int pageSerialNo = evalPageSize * evalPage;
					int srhType = (searchType == null) ? 0 : searchType;
					String srhVal = (searchVal == null) ? "NONE" : searchVal;

					int pageNumber = evalPage;

					if (pageNumber <= 0)
					{
						pageNumber = 0;
					}
					else if ((int)pageNumber >= (int)totalPage)
					{
						pageNumber = totalPage - 1;
					}

					pager = new Pager(totalPage, pageNumber, AppGlobalValues.BUTTONS_TO_SHOW);

					model.addAttribute("tlTotalPage", totalPage);
					model.addAttribute("tlPageNumber", pageNumber);
					model.addAttribute("pageSlno", pageSerialNo);
					model.addAttribute("selectedPageSize", evalPageSize);
					model.addAttribute("pageSizes", AppGlobalValues.PAGE_SIZES);
					model.addAttribute("srhType", srhType);
					model.addAttribute("srhVal", srhVal);
					model.addAttribute("pager", pager);
					model.addAttribute("page", page);

					LOGGER.trace("\n totalPage: "+ totalPage +" | pageNumber: "+ pageNumber);
					LOGGER.trace("\n pageSerialNo: "+ pageSerialNo +" | evalPageSize: "+ evalPageSize 
							+" | srhType: "+ srhType +" | srhVal: "+ srhVal +" | pager: "+ pager 
							+" | page: "+ page);

					if (processType == 1)
					{
						urlPage = "mainpages/CourseList :: pageNoFrag";
					}
					else if (processType == 2)
					{
						urlPage = "mainpages/CourseList :: pageNoFrag2";
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
				//exception.printStackTrace();
				LOGGER.trace(exception);

				courseRegistrationReadWriteService.addErrorLog(exception, 
						AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationFormController", 
						"processPageNumbers", registerNumber, IpAddress);
				model.addAttribute("flag", 1);
				urlPage = "redirectpage";
				return urlPage;			
			}		

			return urlPage;
		}

		//Calling Slot Information When Required.
		public void callSlotInformation(Model model, String semesterSubId, String registerNumber, List<CourseAllocationModel> courseAllocationList)
		{
			List<Object[]> registeredObjectList = new ArrayList<Object[]>();
			Map<String, List<SlotTimeMasterModel>> slotTimeMapList = new HashMap<String, List<SlotTimeMasterModel>>();

			//General
			registeredObjectList = courseRegistrationService.getRegistrationSlotDetail(semesterSubId, registerNumber);

			slotTimeMapList = semesterMasterService.getSlotTimeMasterCommonTimeSlotBySemesterSubIdAsMap(Arrays.asList(semesterSubId));
			Map<String, Object[]> tlInfoMapList =courseRegCommonFn.getSlotInfo(registeredObjectList, courseAllocationList, slotTimeMapList);
			model.addAttribute("tlInfoMapList", tlInfoMapList);
		}



	}
