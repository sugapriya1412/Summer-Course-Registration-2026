package org.vtop.CourseRegistration.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.NetAssist;
import org.vtop.CourseRegistration.model.EmployeeProfile;
import org.vtop.CourseRegistration.model.PatternTimeMasterModel;
import org.vtop.CourseRegistration.model.SemesterDetailsModel;
import org.vtop.CourseRegistration.model.SlotTimeMasterModel;
import org.vtop.CourseRegistration.model.StudentsLoginDetailsModel;
import org.vtop.CourseRegistration.service.CourseRegistrationCommonFunction;
import org.vtop.CourseRegistration.service.CourseRegistrationReadWriteService;
import org.vtop.CourseRegistration.service.CourseRegistrationService;
import org.vtop.CourseRegistration.service.ProgrammeSpecializationCurriculumCreditService;
import org.vtop.CourseRegistration.service.ProgrammeSpecializationCurriculumDetailService;
import org.vtop.CourseRegistration.service.SemesterMasterService;
import org.vtop.CourseRegistration.util.RegistrationConstants;

@Controller
public class CourseRegistrationPageController 
{
	@Autowired private CourseRegistrationService courseRegistrationService;
	@Autowired private CourseRegistrationCommonFunction courseRegCommonFn;
	@Autowired private ProgrammeSpecializationCurriculumCreditService programmeSpecializationCurriculumCreditService;
	@Autowired private ProgrammeSpecializationCurriculumDetailService programmeSpecializationCurriculumDetailService;
	@Autowired private SemesterMasterService semesterMasterService;
	@Autowired private CourseRegistrationReadWriteService courseRegistrationReadWriteService;

	private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationPageController.class);



	@RequestMapping(value="/", method = {RequestMethod.GET, RequestMethod.POST})
	public String home(HttpServletRequest httpServletRequest, Model model, HttpSession session, 
			HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String userSessionId = null;

		String currentDateTimeStr;	

		Date currentDateTime = new Date();

		currentDateTimeStr = RegistrationConstants.doGetCurrentDateDispaly(currentDateTime);

		model.addAttribute("CurrentDateTime", currentDateTimeStr);

		session.setAttribute("baseURL", NetAssist.getBaseURL(httpServletRequest));		

		userSessionId = (String) session.getAttribute("userSessionId");

		if (userSessionId == null) 
		{
			session.setAttribute("userSessionId", session.getId());
		}

		courseRegCommonFn.callCaptcha(request, response, session, model);
		session.setAttribute("CAPTCHA", session.getAttribute("CAPTCHA"));

		return "StudentLogin";
	}


	@PostMapping("viewStudentLogin1")
	public String viewStudentLogin1(Model model, HttpServletRequest request, HttpSession session, 
			HttpServletResponse response) throws ServletException, IOException 
	{
		session.setAttribute("baseURL", NetAssist.getBaseURL(request));
		String userSessionId = (String) session.getAttribute("userSessionId");

		if (userSessionId == null) 
		{
			session.setAttribute("userSessionId", session.getId());
		}

		courseRegCommonFn.callCaptcha(request, response, session, model);
		session.setAttribute("CAPTCHA", session.getAttribute("CAPTCHA"));

		return "StudentLogin::test";
	}	

	@RequestMapping(value = "ServerLimit", method = { RequestMethod.POST, RequestMethod.GET })
	public String serverLimit(Model model, HttpSession session, HttpServletRequest request) throws ServletException 
	{
		String page = "CustomErrorPage";
		String baseURL = NetAssist.getBaseURL(request);
		LOGGER.trace("BaseUrl - " + baseURL);

		request.getSession().invalidate();
		model.addAttribute("message", "");
		model.addAttribute("error", " Please Note: Try one of the following Servers <br/><br/>");
		model.addAttribute("errno", 99);

		return page;
	}

	@RequestMapping(value = "AlreadyLogin", method = { RequestMethod.POST, RequestMethod.GET })
	public String AlreadyLogin(Model model,HttpSession session, HttpServletRequest request, 
			HttpServletResponse response) throws ServletException 
	{
		String page = "CustomErrorPage";

		Cookie cookie = new Cookie("RegisterNumber", null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		request.getSession().invalidate();
		model.addAttribute("message", "Multi-Tab Access");
		model.addAttribute("error", "Multiple Tabs Access prevented !!!");
		model.addAttribute("errno", 6);

		return page;
	}



	@GetMapping("noscript")
	public String noscript(Model model) 
	{
		model.addAttribute("message", "JavaScript Error");
		model.addAttribute("error", "Kindly Enable JavaScript in Your Browser to Access V-TOP.");

		return "ErrorPage";
	}

	@PostMapping("viewCurriculumCredits")
	public String viewCurriculumCredits(HttpSession session,Model model)
	{
		String registerNo = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try 
		{
			if (registerNo != null)
			{
				float cgpa = 0;
				List<Object[]> cclCtgCreditList = new ArrayList<>();

				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
				String studentStudySystem = (String) session.getAttribute("studentStudySystem");
				float curriculumVersion = (Float) session.getAttribute("curriculumVersion");
				String semesterSubId = (String) session.getAttribute("SemesterSubId");				
				String studentCgpaData = (String) session.getAttribute("studentCgpaData");


				cclCtgCreditList = programmeSpecializationCurriculumCreditService.getCurrentSemRegCurCtgCreditByRegisterNo(
						programSpecId, studyStartYear, curriculumVersion, semesterSubId, registerNo,"ME",studentStudySystem);

				//Student CGPA Detail
				if ((studentCgpaData != null) && (!studentCgpaData.equals("")))
				{
					String[] studentCgpaArr = studentCgpaData.split("\\|");

					cgpa = Float.parseFloat(studentCgpaArr[2]);
				}

				model.addAttribute("cclCtgCreditList", cclCtgCreditList);
				model.addAttribute("studentStudySystem", studentStudySystem);
				model.addAttribute("tlCgpa", cgpa);

				urlPage = "mainpages/ViewCurriculumCredits::section";
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
			LOGGER.trace(exception);

			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationPageController", 
					"viewCurriculumCredits", registerNo, IpAddress);
			model.addAttribute("flag", 1);
			urlPage = "redirectpage";
			return urlPage;			
		}

		return urlPage;
	}

	@PostMapping("viewRegistered")
	public String viewRegistered(Model model, HttpSession session, HttpServletRequest request) 
	{
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress = (String) session.getAttribute("IpAddress");
		String urlPage = "";

		try
		{
			if (registerNumber!=null)
			{
				int regCount = 0,  ncCount = 0;
				float regCredit = 0, ncCredit = 0;
				String checkCourseId = "";

				List<Object[]> courseRegistrationModel = new ArrayList<>();
				List<Integer> patternIdList = new ArrayList<>();
				List<Object[]> regCreditList = new ArrayList<>();
				List<String> ncCourseList = new ArrayList<>();

				float minCredit = (float) session.getAttribute("minCredit");
				float maxCredit = (float) session.getAttribute("maxCredit");
				String semesterSubId = (String) session.getAttribute("SemesterSubId");
				int programSpecId = (Integer) session.getAttribute("ProgramSpecId");
				int studyStartYear = (int) session.getAttribute("StudyStartYear");
				float curriculumVersion = (Float) session.getAttribute("curriculumVersion");												

				StudentsLoginDetailsModel studentsLoginDetailsModel = new StudentsLoginDetailsModel();
				studentsLoginDetailsModel = semesterMasterService.getStudentLoginDetailByRegisterNumber(registerNumber);


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

							if ((Integer.parseInt(obj[30].toString()) > 0) && (!patternIdList.contains(Integer.parseInt(obj[29].toString()))))
							{
								patternIdList.add(Integer.parseInt(obj[29].toString()));
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

				List<Object[]> ttObjList = new ArrayList<Object[]>();
				Map<Integer, List<String>> slotTypeMapList = new HashMap<Integer, List<String>>();
				Map<Integer, List<Object[]>> ttSessionList = new HashMap<Integer, List<Object[]>>();
				Map<Integer, List<Object[]>> weekDayList = new HashMap<Integer, List<Object[]>>();
				Map<String, List<Object[]>> tmMapList = new HashMap<String, List<Object[]>>();
				Map<String, List<Object[]>> wdsMapList = new HashMap<String, List<Object[]>>();
				Map<String, List<Object[]>> stmMapList = new HashMap<String, List<Object[]>>();
				Map<String, String> regMapList = new HashMap<String, String>();

				SemesterDetailsModel sdm = new SemesterDetailsModel();	
				String hashKey = "", hashValue = "";;						

				sdm = semesterMasterService.getSemesterDetailBySemesterSubId(semesterSubId);
				if (!patternIdList.isEmpty())
				{
					for (Integer patternId : patternIdList)
					{								
						//Slot Type Map List
						ttObjList.clear();
						ttObjList = semesterMasterService.getPatternTimeMasterSlotTypeByPatternId(Arrays.asList(patternId));
						if (!ttObjList.isEmpty())
						{
							for (Object[] parameters : ttObjList)
							{
								if(slotTypeMapList.containsKey(patternId))
								{
									List<String> mapTempList = slotTypeMapList.get(patternId);
									mapTempList.add(parameters[1].toString());
									slotTypeMapList.replace(patternId, mapTempList);
								}
								else
								{
									List<String> mapTempList = new ArrayList<String>();
									mapTempList.add(parameters[1].toString());
									slotTypeMapList.put(patternId, mapTempList);
								}
							}
						}

						//Session Map List
						ttObjList.clear();
						ttObjList = semesterMasterService.getTTPatternDetailSessionSlotByPatternId(patternId);
						if (!ttObjList.isEmpty())
						{
							for (Object[] parameters : ttObjList)
							{
								if(ttSessionList.containsKey(patternId))
								{
									List<Object[]> mapTempList = ttSessionList.get(patternId);
									mapTempList.add(parameters);
									ttSessionList.replace(patternId, mapTempList);
								}
								else
								{
									List<Object[]> mapTempList = new ArrayList<Object[]>();
									mapTempList.add(parameters);
									ttSessionList.put(patternId, mapTempList);
								}
							}
						}

						//Week Day Map List
						ttObjList.clear();
						ttObjList = semesterMasterService.getSlotTimeMasterWeekDayList(patternId);	
						if (!ttObjList.isEmpty())
						{
							for (Object[] parameters : ttObjList)
							{
								if(weekDayList.containsKey(patternId))
								{
									List<Object[]> mapTempList = weekDayList.get(patternId);
									mapTempList.add(parameters);
									weekDayList.replace(patternId, mapTempList);
								}
								else
								{
									List<Object[]> mapTempList = new ArrayList<Object[]>();
									mapTempList.add(parameters);
									weekDayList.put(patternId, mapTempList);
								}
							}
						}

						//Slot Detail Map List
						ttObjList.clear();
						ttObjList = semesterMasterService.getPatternTimeMasterSlotDetailByPatternId(patternId);
						if (!ttObjList.isEmpty())
						{
							for (Object[] parameters : ttObjList)
							{
								hashKey = patternId +"_"+ parameters[0].toString() +"_"+ parameters[2].toString();
								if(tmMapList.containsKey(hashKey))
								{
									List<Object[]> mapTempList = tmMapList.get(hashKey);
									mapTempList.add(parameters);
									tmMapList.put(hashKey, mapTempList);
								}
								else
								{
									List<Object[]> mapTempList = new ArrayList<Object[]>();
									mapTempList.add(parameters);
									tmMapList.put(hashKey, mapTempList);
								}
							}
						}								

						//Week Day Session Map List
						ttObjList.clear();
						ttObjList = semesterMasterService.getSlotTimeMasterWeekDaySessionList(patternId);
						if (!ttObjList.isEmpty())
						{
							for (Object[] parameters : ttObjList)
							{
								hashKey = patternId +"_"+ parameters[0].toString() +"_"+ parameters[1].toString();
								if(wdsMapList.containsKey(hashKey))
								{
									List<Object[]> mapTempList = wdsMapList.get(hashKey);
									mapTempList.add(parameters);
									wdsMapList.put(hashKey, mapTempList);
								}
								else
								{
									List<Object[]> mapTempList = new ArrayList<Object[]>();
									mapTempList.add(parameters);
									wdsMapList.put(hashKey, mapTempList);
								}
							}
						}

						//Slot Time Map List
						ttObjList.clear();
						ttObjList = semesterMasterService.getSlotTimeMasterByPatternId(patternId);
						if (!ttObjList.isEmpty())
						{
							for (Object[] parameters : ttObjList)
							{
								hashKey = patternId +"_"+ parameters[0].toString() +"_"+ parameters[1].toString() 
										+"_"+ parameters[2].toString();
								if(stmMapList.containsKey(hashKey))
								{
									List<Object[]> mapTempList = stmMapList.get(hashKey);
									mapTempList.add(parameters);
									stmMapList.put(hashKey, mapTempList);
								}
								else
								{
									List<Object[]> mapTempList = new ArrayList<Object[]>();
									mapTempList.add(parameters);
									stmMapList.put(hashKey, mapTempList);
								}
							}
						}

						//Registered Map List
						ttObjList.clear();
						ttObjList = courseRegistrationService.getCourseRegWlSlotByStudent2(semesterSubId,registerNumber, patternId);
						if (!ttObjList.isEmpty())
						{					
							for (Object[] parameters : ttObjList)
							{
								hashKey = patternId +"_"+ parameters[0].toString() +"_"+ parameters[1].toString() 
										+"_"+ parameters[2].toString();
								hashValue = parameters[2].toString() +"-"+ parameters[3].toString() 
										+"-"+ parameters[4].toString() +"-"+ parameters[5].toString() 
										+"-"+ parameters[8].toString();
								LOGGER.trace("\n hashKey: "+ hashKey +" | hashValue: "+ hashValue);
								if(regMapList.containsKey(hashKey))
								{
									hashValue = regMapList.get(hashKey) +"/ "+ hashValue;
									regMapList.replace(hashKey, hashValue);
								}
								else
								{
									regMapList.put(hashKey, hashValue);
								}
							}
						}
					}

					model.addAttribute("patternIdList", patternIdList);
					model.addAttribute("slotTypeMapList", slotTypeMapList);
					model.addAttribute("ttSessionList", ttSessionList);
					model.addAttribute("weekDayList", weekDayList);
					model.addAttribute("tmMapList", tmMapList);
					model.addAttribute("wdsMapList", wdsMapList);
					model.addAttribute("stmMapList", stmMapList);
					model.addAttribute("regMapList", regMapList);
				}

				model.addAttribute("sdm", sdm);
				model.addAttribute("cDate", new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(new Date()));
				model.addAttribute("courseRegistrationModel", courseRegistrationModel);
				model.addAttribute("studentsLoginDetailsModel", studentsLoginDetailsModel);
				model.addAttribute("showFlag", 0);
				
				String studySystem = (String) session.getAttribute("studentStudySystem");

				
				model.addAttribute("curriculumMapList", programmeSpecializationCurriculumDetailService.
						getCurriculumBySpecIdYearAndCCVersionAsMap(programSpecId, studyStartYear, curriculumVersion,studySystem,registerNumber));
				model.addAttribute("regCreditList", regCreditList);

				session.removeAttribute("registrationOption");
				urlPage = "mainpages/ViewRegistered::section";
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

			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationPageController", 
					"viewRegistered", registerNumber, IpAddress);
			model.addAttribute("flag", 1);
			urlPage = "redirectpage";
			return urlPage;
		}

		return urlPage;
	}

	@PostMapping(value = "getSchoolWiseGuideList")
	public String getSchoolWiseGuideList(String guideSchoolOpt , Model model, HttpSession session, HttpServletRequest request) 
	{			
		String registerNumber = (String) session.getAttribute("RegisterNumber");
		String IpAddress=(String) session.getAttribute("IpAddress");

		int costCentreId = 0;
		String urlPage = "";

		try
		{
			if ((guideSchoolOpt != null) && (!guideSchoolOpt.equals("")))
			{
				costCentreId =Integer.parseInt(guideSchoolOpt);
			}

			List<EmployeeProfile> employeeList = semesterMasterService.getEmployeeProfileByCentreId(costCentreId);
			model.addAttribute("employeeList", employeeList);
			model.addAttribute("costCentreId", costCentreId);
			urlPage = "mainpages/ProjectRegistration::ProjectGuideFragment";			
		}
		catch(Exception exception)
		{
			LOGGER.trace(exception);

			model.addAttribute("flag", 1);
			courseRegistrationReadWriteService.addErrorLog(exception, 
					AppGlobalValues.REG_ERROR_METHOD +"_CourseRegistrationPageController", 
					"getSchoolWiseGuideList", registerNumber, IpAddress);
			urlPage = "redirectpage";
			return urlPage;
		}

		return urlPage;	
	}

	List<String> getStartingTimeTableSlots(Integer patternId, List<PatternTimeMasterModel> list1)
	{		
		BigDecimal bg;
		List<Object[]> listMax = semesterMasterService.getTTPatternDetailMaxSlots(patternId);
		List<String> listTimeTableSlots = new ArrayList<String>();

		int fnMax = 0, anMax = 0, enMax=0;
		String sesMax = "";

		try
		{
			for (int m= 0; m< listMax.size(); m++)
			{
				sesMax =listMax.get(m)[1].toString(); 
				if (sesMax.equals("FN"))
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					fnMax = bg.intValue();
				}
				if (sesMax.equals("AN"))
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					anMax =bg.intValue();
				}
				if (sesMax.equals("EN"))
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					enMax = bg.intValue();
				}			
			}
		}
		catch(Exception ex)
		{
			LOGGER.trace(ex);
		}

		//THEORY STARTING TIMINGS 
		int i = 1;
		for(PatternTimeMasterModel ls: list1)
		{
			String slName = ls.getPtmPkId().getSlotName().substring(0, 2);
			if (slName.equals("FN"))
			{
				listTimeTableSlots.add(ls.getStartingTime().toString().substring(0, 5));
				i++;
			}				
		}
		i = i-1;
		if (i < fnMax)
		{
			for (int j=1; j <= fnMax - i; j++)
			{
				listTimeTableSlots.add("-");
			}
		}

		listTimeTableSlots.add("Lunch");
		i=1;
		for(PatternTimeMasterModel ls: list1)
		{
			String slName = ls.getPtmPkId().getSlotName().substring(0, 2);
			if (slName.equals("AN"))
			{
				listTimeTableSlots.add(ls.getStartingTime().toString().substring(0, 5));
				i++;
			}
		}
		i = i-1;
		if (i < anMax)
		{
			for (int j=1; j <= anMax - i; j++)
			{
				listTimeTableSlots.add("-");
			}
		}


		i=1;
		for(PatternTimeMasterModel ls: list1)
		{
			String slName = ls.getPtmPkId().getSlotName().substring(0, 2);
			if (slName.equals("EN"))
			{
				listTimeTableSlots.add(ls.getStartingTime().toString().substring(0, 5));
				i++;
			}				
		}	
		i = i-1;
		if (i < enMax)
		{
			for (int j=1; j <= enMax - i; j++)
			{
				listTimeTableSlots.add("-");
			}
		}


		for (int k = 0; k < listTimeTableSlots.size(); k++)
		{

		}

		return listTimeTableSlots;
	}	

	List<String> getEndingTimeTableSlots(Integer patternId, List<PatternTimeMasterModel> list1)
	{		
		BigDecimal bg;
		List<Object[]> listMax = semesterMasterService.getTTPatternDetailMaxSlots(patternId);
		List<String> listTimeTableSlots = new ArrayList<String>();

		int fnMax = 0, anMax = 0, enMax=0;
		String sesMax;
		try
		{
			for (int m= 0; m< listMax.size(); m++)
			{
				sesMax =listMax.get(m)[1].toString(); 
				if (sesMax.equals("FN"))
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					fnMax = bg.intValue();
				}
				if (sesMax.equals("AN"))
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					anMax =bg.intValue();
				}
				if (sesMax.equals("EN"))
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					enMax = bg.intValue();
				}			
			}
		}
		catch(Exception ex)
		{
			LOGGER.trace(ex);
		}


		//THEORY STARTING TIMINGS 
		int i = 1;
		for(PatternTimeMasterModel ls: list1)
		{
			String slName = ls.getPtmPkId().getSlotName().substring(0, 2);
			if (slName.equals("FN"))
			{
				listTimeTableSlots.add(ls.getEndingTime().toString().substring(0, 5));
				i++;
			}				
		}
		i = i-1;
		if (i < fnMax)
		{
			for (int j=1; j <= fnMax - i; j++)
			{
				listTimeTableSlots.add("-");
			}
		}

		listTimeTableSlots.add("Lunch");
		i=1;
		for(PatternTimeMasterModel ls: list1)
		{
			String slName = ls.getPtmPkId().getSlotName().substring(0, 2);
			if (slName.equals("AN"))
			{
				listTimeTableSlots.add(ls.getEndingTime().toString().substring(0, 5));
				i++;
			}
		}
		i = i-1;
		if (i < anMax)
		{
			for (int j=1; j <= anMax - i; j++)
			{
				listTimeTableSlots.add("-");
			}
		}


		i=1;
		for(PatternTimeMasterModel ls: list1)
		{
			String slName = ls.getPtmPkId().getSlotName().substring(0, 2);
			if (slName.equals("EN"))
			{
				listTimeTableSlots.add(ls.getEndingTime().toString().substring(0, 5));
				i++;
			}				
		}	
		i = i-1;
		if (i < enMax)
		{
			for (int j=1; j <= enMax - i; j++)
			{
				listTimeTableSlots.add("-");
			}
		}

		return listTimeTableSlots;
	}

	List<Object[]> getTimeTableSlots(String semesterSubId, String registerNumber, Integer patternId,
			List<SlotTimeMasterModel> slotTimeMasterList) 
	{
		BigDecimal bg;
		List<Object[]> listMax = semesterMasterService.getTTPatternDetailMaxSlots(patternId);
		List<String> listTimeTableSlots = new ArrayList<String>();
		List<Object[]> listTimeTableSlots1 = new ArrayList<Object[]>();

		int fnMax = 0, anMax = 0, enMax = 0;
		String sesMax;
		try 
		{
			for (int m = 0; m < listMax.size(); m++) 
			{
				sesMax = listMax.get(m)[1].toString();
				if (sesMax.equals("FN")) 
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					fnMax = bg.intValue();
				}
				if (sesMax.equals("AN")) 
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					anMax = bg.intValue();
				}
				if (sesMax.equals("EN")) 
				{
					bg = new BigDecimal(listMax.get(m)[0].toString());
					enMax = bg.intValue();
				}
			}
		} 
		catch (Exception ex) 
		{
			LOGGER.trace(ex);
		}

		int i = 1;

		List<Object[]> regSlots = courseRegistrationService.getCourseRegWlSlotByStudent(semesterSubId,
				registerNumber, patternId);
		Map<String, List<Object[]>> tempMap = new HashMap<>();

		for (Object[] parameters : regSlots) 
		{
			if (tempMap.containsKey(parameters[1])) 
			{
				List<Object[]> temp = tempMap.get(parameters[1]);
				temp.add(parameters);
				tempMap.put(parameters[1].toString(), temp);
			} 
			else 
			{
				List<Object[]> temp = new ArrayList<>();
				temp.add(parameters);
				tempMap.put(parameters[1].toString(), temp);
			}
		}

		for (SlotTimeMasterModel ls : slotTimeMasterList) 
		{
			String slName = ls.getSession();
			if (slName.equals("FN")) 
			{
				String[] tempArr = new String[2];
				tempArr[0] = ls.getStmPkId().getSlot();
				if (tempMap.containsKey(ls.getStmPkId().getWeekdays())) 
				{
					for (Object[] obj : tempMap.get(ls.getStmPkId().getWeekdays())) 
					{
						if (obj[0].equals(ls.getStmPkId().getSlot())) 
						{
							tempArr[0] = obj[2] + "-" + obj[3] + "-" + obj[0] + "-" + obj[4];
							tempArr[1] = "#CCFF33";
							break;
						} 
						else 
						{
							tempArr[1] = "";
						}
					}
				} 
				else 
				{
					tempArr[1] = "";
				}

				listTimeTableSlots1.add(tempArr);
				i++;
			}
		}

		i = i - 1;
		if (i < fnMax) 
		{
			for (int j = 1; j <= fnMax - i; j++) 
			{
				listTimeTableSlots.add("-");
				String[] tempArr = new String[2];
				tempArr[0] = "-";
				tempArr[1] = "";
				listTimeTableSlots1.add(tempArr);
			}
		}

		String[] tempArrLunch = new String[2];
		tempArrLunch[0] = "Lunch";
		tempArrLunch[1] = "#e2e2e2";
		listTimeTableSlots1.add(tempArrLunch);
		i = 1;
		for (SlotTimeMasterModel ls : slotTimeMasterList) 
		{
			String slName = ls.getSession();
			if (slName.equals("AN")) {
				String[] tempArr = new String[2];
				tempArr[0] = ls.getStmPkId().getSlot();
				if (tempMap.containsKey(ls.getStmPkId().getWeekdays())) 
				{
					for (Object[] obj : tempMap.get(ls.getStmPkId().getWeekdays())) 
					{
						if (obj[0].equals(ls.getStmPkId().getSlot())) 
						{
							tempArr[0] = obj[2] + "-" + obj[3] + "-" + obj[0] + "-" + obj[4];
							tempArr[1] = "#CCFF33";
							break;
						} 
						else 
						{
							tempArr[1] = "";
						}
					}

				} 
				else 
				{
					tempArr[1] = "";
				}

				listTimeTableSlots1.add(tempArr);
				i++;
			}
		}
		i = i - 1;
		if (i < anMax) 
		{
			for (int j = 1; j <= anMax - i; j++) 
			{
				listTimeTableSlots.add("-");
				String[] tempArr = new String[2];
				tempArr[0] = "-";
				tempArr[1] = "";
				listTimeTableSlots1.add(tempArr);
			}
		}

		i = 1;
		for (SlotTimeMasterModel ls : slotTimeMasterList) 
		{
			String slName = ls.getSession();
			if (slName.equals("EN")) 
			{
				String[] tempArr = new String[2];
				tempArr[0] = ls.getStmPkId().getSlot();
				if (tempMap.containsKey(ls.getStmPkId().getWeekdays())) 
				{
					for (Object[] obj : tempMap.get(ls.getStmPkId().getWeekdays())) 
					{
						if (obj[0].equals(ls.getStmPkId().getSlot())) 
						{
							tempArr[0] = obj[2] + "-" + obj[3] + "-" + obj[0] + "-" + obj[4];
							tempArr[1] = "#CCFF33";
							break;
						} 
						else 
						{
							tempArr[1] = "";
						}
					}
				} 
				else 
				{
					tempArr[1] = "";
				}

				listTimeTableSlots1.add(tempArr);
				i++;
			}
		}
		i = i - 1;
		if (i < enMax) 
		{
			for (int j = 1; j <= enMax - i; j++) 
			{
				listTimeTableSlots.add("-");
				String[] tempArr = new String[2];
				tempArr[0] = "-";
				tempArr[1] = "";
				listTimeTableSlots1.add(tempArr);
			}
		}

		return listTimeTableSlots1;
	}
}
