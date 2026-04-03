package org.vtop.CourseRegistration.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.Dto.ProgramSpecializationCurriculumDetailDto;
import org.vtop.CourseRegistration.model.*;
import org.vtop.CourseRegistration.repository.ProgramSpecializationCurriculumCategoryCreditRepository;
import org.vtop.CourseRegistration.repository.ProgrammeSpecializationCurriculumCreditRepository;
import org.vtop.CourseRegistration.repository.ProgrammeSpecializationCurriculumDetailRepository;
import org.vtop.CourseRegistration.repository.SemesterMasterRepository;


@Service
public class CourseRegCommonService
{
	@Autowired private ProgrammeSpecializationCurriculumCreditRepository programmeSpecializationCurriculumCreditRepository;
	@Autowired private ProgrammeSpecializationCurriculumDetailRepository programmeSpecializationCurriculumDetailRepository;
	@Autowired private ProgramSpecializationCurriculumCategoryCreditRepository programSpecializationCurriculumCategoryCreditRepository;
	@Autowired private SemesterMasterRepository semesterMasterRepository;
	@Autowired private AdditionalLearningOptedService optedService;
	@Autowired private CourseRegUserActivityLogService logService;
	
	private static final Logger logger = LogManager.getLogger(CourseRegCommonService.class);
	
	
	//Program Specialization Curriculum Credit
	public ProgrammeSpecializationCurriculumCreditModel getPrgSpecCurriculumCreditBySpecIdAndAdmissionYear(int specializationId, 
														int	admissionYear)
	{
		return programmeSpecializationCurriculumCreditRepository.findBySpecIdAndAdmissionYear(specializationId, admissionYear); 
	}
	
	
	//Program Specialization Curriculum Detail
	public ProgramSpecializationCurriculumDetailDto getBySpecIdAdmissionYearAndCourseCode(int specializationId, int admissionYear, 
													String courseCode)
	{
		return programmeSpecializationCurriculumDetailRepository.findBySpecIdAdmissionYearAndCourseCode(specializationId, 
					admissionYear, courseCode); 
	}
	
	public List<ProgramSpecializationCurriculumDetailDto> getBySpecIdAndAdmissionYear(int specializationId, int admissionYear)
	{
		return programmeSpecializationCurriculumDetailRepository.findBySpecIdAndAdmissionYear(specializationId, admissionYear); 
	}
	
	public List<ProgramSpecializationCurriculumDetailDto> getBySpecIdAdmissionYearAndCourseCategory(int specializationId, 
																int admissionYear, String courseCategory)
	{
		return programmeSpecializationCurriculumDetailRepository.findBySpecIdAdmissionYearAndCourseCategory(specializationId, 
					admissionYear, courseCategory); 
	}
	
	
	
	//Registration Course Option
	public List<Object[]> getRegistrationOption(String programGroupCode, String courseSystem, int rgrCourseAllowStatus, 
								int reRegCourseAllowStatus, int peueAllowStatus, Integer specializationId, Integer admissionYear, 
								Float curriculumVersion, int compulsoryCourseStatus, HttpSession session)
	{
		List<Object[]> returnObjectList = new ArrayList<Object[]>();
		List<ProgrammeSpecializationCurriculumCategoryCredit> pscccList = new ArrayList<>();
		logger.trace("\n programGroupCode: "+ programGroupCode +" | courseSystem: "+ courseSystem
				 +" | rgrCourseAllowStatus: "+ rgrCourseAllowStatus +" | reRegCourseAllowStatus: "+ reRegCourseAllowStatus 
				 +" | peueAllowStatus: "+ peueAllowStatus +" | specializationId: "+ specializationId 
				 +" | admissionYear: "+ admissionYear +" | curriculumVersion: "+ curriculumVersion);
		
		
		String studentCgpaData = (String) session.getAttribute("studentCgpaData"); 
		
		Float cgpa = 0F;
		if ((studentCgpaData != null) && (!studentCgpaData.equals("")))
		{
			String[] studentCgpaArr = studentCgpaData.split("\\|");

			cgpa = Float.parseFloat(studentCgpaArr[2]);
		}
		
		String studySystem = (String) session.getAttribute("studentStudySystem");
		if ((programGroupCode == null) || programGroupCode.equals(""))
		{
			programGroupCode = "NONE";
		}
		
		if ((courseSystem == null) || courseSystem.equals(""))
		{
			courseSystem = "NONE";
		}
		
		if (specializationId == null)
		{
			specializationId = 0;
		}
		
		if (admissionYear == null)
		{
			admissionYear = 0;
		}
		
		if (curriculumVersion == null)
		{
			curriculumVersion = 0F;
		}
		
		
		if ((!programGroupCode.equals("NONE")) && (programGroupCode.equals("RP") || programGroupCode.equals("IEP")))
		{
			returnObjectList.add(new Object[] {"RGR", "Regular"});
			if (reRegCourseAllowStatus == 1)
			{
				returnObjectList.add(new Object[] {"RR", "Re - Registration"});
			}
		}
		else if ((!programGroupCode.equals("NONE")) && (!courseSystem.equals("NONE")) 
						&& (courseSystem.equals("NONFFCS") || courseSystem.equals("FFCS")))
		{
			if (rgrCourseAllowStatus == 1)
			{
				returnObjectList.add(new Object[] {"RGR", "Regular"});
				returnObjectList.add(new Object[] {"FFCSCAL", "FFCS to CAL Course Equivalence"});
				returnObjectList.add(new Object[] {"FFCSCBCS", "FFCS to CBCS Course Equivalence"});
			}
			
			if (reRegCourseAllowStatus == 1)
			{
				returnObjectList.add(new Object[] {"RR", "Re - Registration"});
			}
		}
		else if ((!programGroupCode.equals("NONE")) && (!courseSystem.equals("NONE")) 
						&& (!courseSystem.equals("NONFFCS")) && (!courseSystem.equals("FFCS")))
		{
			//if (compulsoryCourseStatus == 1)
			{
				returnObjectList.add(new Object[] {"COMP", "Compulsory Course"});
			}
			
			//if (rgrCourseAllowStatus == 1 && admissionYear != null && admissionYear != 2025)
			if (rgrCourseAllowStatus == 1  && admissionYear != null && (admissionYear != 2025 || Integer.valueOf(156).equals(specializationId)))				
			{
				pscccList = programSpecializationCurriculumCategoryCreditRepository.findBySpecIdAndAdmissionYear(specializationId, 
								admissionYear);
				if (!pscccList.isEmpty())
				{
					for (ProgrammeSpecializationCurriculumCategoryCredit e : pscccList)
					{
						if (peueAllowStatus == 1)
						{
							returnObjectList.add(new Object[] {e.getId().getCourseCategory(), e.getCurriculumCategoryMaster().getDescription()});
						}
						else if ((peueAllowStatus == 2) && (e.getId().getCourseCategory().equals("PC") || e.getId().getCourseCategory().equals("UC")))
						{
							returnObjectList.add(new Object[] {e.getId().getCourseCategory(), e.getCurriculumCategoryMaster().getDescription()});
						}
					}
				}
				
				if(studySystem!=null &&  studySystem.equals("CAL"))
				{
					returnObjectList.add(new Object[] {"CALCBCS", "CAL to CBCS Course Equivalence"});
				}
			}
			
			if (reRegCourseAllowStatus == 1)
			{
				returnObjectList.add(new Object[] {"RR", "Re - Registration"});
			}
			
			if(AppGlobalValues.IS_ALLOWED_CBCS_MIN)
			{
				
				if(studySystem!=null && studySystem.equals("CBCS"))
				{
					Map<String,String> minorBasketDetails =getMinorApplicableForStudentNew(session,"MIN");

					if(minorBasketDetails!=null && !minorBasketDetails.isEmpty())
					{
						returnObjectList.add(new Object[] {"CBCSMIN", "Minor"});
						session.setAttribute("cbcsMinorBasketDetails", minorBasketDetails);

					}
					
				}
			}
			
			
		  if(AppGlobalValues.IS_ALLOWED_CBCS_HON && cgpa>=AppGlobalValues.CBCS_HON_CGPA)
			{
				
				if(studySystem!=null && studySystem.equals("CBCS"))
				{
					Map<String,String> honourBasketDetails =getMinorApplicableForStudentNew(session,"HON");
					
					if(honourBasketDetails!=null && !honourBasketDetails.isEmpty())
					{
						returnObjectList.add(new Object[] {"CBCSHON", "Honour"});
						session.setAttribute("cbcsHonourBasketDetails", honourBasketDetails);

					}
					
				}
			}
		  
		  if(AppGlobalValues.IS_ALLOWED_ACE_MIN)
			{
				
				if(studySystem!=null && studySystem.equals("ACE"))
				{
					Map<String,String> minorBasketDetailsAce =getMinorApplicableForStudentNew(session,"MIN");

					if(minorBasketDetailsAce!=null && !minorBasketDetailsAce.isEmpty())
					{
						returnObjectList.add(new Object[] {"ACEMIN", "Minor"});
						session.setAttribute("minorBasketDetailsAce", minorBasketDetailsAce);

					}
					
				}
			}
		}
		logger.trace("\n returnObjectList size: "+ returnObjectList.size());
		
		return returnObjectList;
	}
	
	//Registration Option Description
	public String getCourseOptionDescription(int specializationId, int admissionYear, String registrationOption)
	{
		String returnDescription = "";
		
		switch (registrationOption)
		{
			case "COMP":  returnDescription = "Compulsory Course(s)"; break;
			case "RGR":  returnDescription = "Regular Course(s)"; break;
			case "RR":  returnDescription = "Re-Registration Course(s)"; break;
			case "FFCSCAL":  returnDescription = "FFCS to CAL Course Equivalence"; break;
			case "ExtraCur":  returnDescription = "Co-Extra Curricular Activity Courses(s)"; break;
			
			default:
				ProgrammeSpecializationCurriculumCategoryCredit programSpecializationCurriculumCategoryCredit 
						= programSpecializationCurriculumCategoryCreditRepository.findBySpecIdAdmissionYearAndCategory
								(specializationId, admissionYear, registrationOption);
				if (programSpecializationCurriculumCategoryCredit != null)
				{
					returnDescription = programSpecializationCurriculumCategoryCredit.getCurriculumCategoryMaster().getDescription();
				}
				break;
		}
				
		return returnDescription;
	}
	
	
	public Map<String,String> getMinorApplicableForStudentNew(HttpSession httpSession, String registrationOption )
	{
		Integer pgmGroupId = (Integer)httpSession.getAttribute("ProgramGroupId");
		Integer specId = (Integer)httpSession.getAttribute("ProgramSpecId");
		String studySystem = (String)httpSession.getAttribute("studentStudySystem");
		Integer studyStartYear =(Integer)httpSession.getAttribute("StudyStartYear");
		Map<String,String> minorBasketDetails = new LinkedHashMap<>();
		String optedCBCSMinor = "";
		//String optedCBCSHonour = "";

		String regOptDesc="";
		
		
		if(registrationOption.equals("MIN"))
		{
			regOptDesc="Minor in ";
			optedCBCSMinor=(String)httpSession.getAttribute("optedCBCSMinor");
		}
		else
		{
			regOptDesc="Honours in ";
			optedCBCSMinor=(String)httpSession.getAttribute("optedCBCSHonour");
		}
		if(studySystem.equals("CBCS") && studyStartYear>=2021)
		{
			Map<String,List<String>> minorCourseDetails = new LinkedHashMap<>();

			/*List<String> cbcsMinorList = Arrays.asList("MIN040","MIN041","MIN042","MIN043","MIN044","MIN045",
					"MIN046","MIN047","MIN048","MIN049","MIN050","MIN051","MIN052","MIN053","MIN054");*/
			
			List<String> cbcsMinorList = semesterMasterRepository.doGetAdditionalLearningCode("CBCS",registrationOption,0);

			
			if(optedCBCSMinor!=null && !optedCBCSMinor.isEmpty())
			{
				cbcsMinorList = Arrays.asList(optedCBCSMinor);
			}
			List<Object[]> minorCourseInfo = semesterMasterRepository.
					getAdditionalLearningDetailsForCBCSMinor(registrationOption, pgmGroupId, specId.toString(), cbcsMinorList);

			if(minorCourseInfo!=null && !minorCourseInfo.isEmpty())
			{
				for (Object[] minorRow : minorCourseInfo) {
					String basketCode = minorRow[0].toString();
					String basketTitle = minorRow[1].toString();
					String courseId = minorRow[3].toString();
					if(minorBasketDetails.containsKey(basketCode))
					{
						List<String> temp = minorCourseDetails.get(basketCode);
						temp.add(courseId);
						minorCourseDetails.put(basketCode, temp);
					}
					else
					{
						minorBasketDetails.put(basketCode,regOptDesc + basketTitle);
						List<String> temp = new ArrayList<>();
						temp.add(courseId);
						minorCourseDetails.put(basketCode, temp);
					}
				}
				
				if(registrationOption.equals("MIN"))
				{
					httpSession.setAttribute("minorCourseDetails",minorCourseDetails);
				}
				else
				{
					httpSession.setAttribute("honourCourseDetails",minorCourseDetails);
				}
				
				
			}
		}
		else if(studySystem.equals("ACE") && studyStartYear==2025)
		{
			Map<String,List<String>> minorCourseDetails = new LinkedHashMap<>();

			/*List<String> cbcsMinorList = Arrays.asList("MIN040","MIN041","MIN042","MIN043","MIN044","MIN045",
					"MIN046","MIN047","MIN048","MIN049","MIN050","MIN051","MIN052","MIN053","MIN054");*/
			
			List<String> cbcsMinorList = semesterMasterRepository.doGetAdditionalLearningCode("ACE",registrationOption,0);

			
			if(optedCBCSMinor!=null && !optedCBCSMinor.isEmpty())
			{
				cbcsMinorList = Arrays.asList(optedCBCSMinor);
			}
			List<Object[]> minorCourseInfo = semesterMasterRepository.
					getAdditionalLearningDetailsForCBCSMinor(registrationOption, pgmGroupId, specId.toString(), cbcsMinorList);

			if(minorCourseInfo!=null && !minorCourseInfo.isEmpty())
			{
				for (Object[] minorRow : minorCourseInfo) {
					String basketCode = minorRow[0].toString();
					String basketTitle = minorRow[1].toString();
					String courseId = minorRow[3].toString();
					if(minorBasketDetails.containsKey(basketCode))
					{
						List<String> temp = minorCourseDetails.get(basketCode);
						temp.add(courseId);
						minorCourseDetails.put(basketCode, temp);
					}
					else
					{
						minorBasketDetails.put(basketCode,regOptDesc + basketTitle);
						List<String> temp = new ArrayList<>();
						temp.add(courseId);
						minorCourseDetails.put(basketCode, temp);
					}
				}
				
				if(registrationOption.equals("MIN"))
				{
					httpSession.setAttribute("minorCourseDetails",minorCourseDetails);
				}
				else
				{
					httpSession.setAttribute("honourCourseDetails",minorCourseDetails);
				}
				
				
			}
		}
		return minorBasketDetails;
	}
	
	public void saveAddlOpted(String addlLerCode,String regNo,String learningType,String semesterSubId,HttpSession session)
	{
		String IpAddress = (String) session.getAttribute("IpAddress");
		AdditionalLearningOptedPK pk = new AdditionalLearningOptedPK();
		pk.setAdditionalLearningCode(addlLerCode);
		pk.setStdntslgndtlsRegisterNumber(regNo);
		AdditionalLearningOpted lerningOpted = new AdditionalLearningOpted();
		
		lerningOpted.setId(pk);
		lerningOpted.setLearningType(learningType);
		lerningOpted.setLogIpaddress(IpAddress);
		lerningOpted.setLogUserid(regNo);
		lerningOpted.setLogTimestamp(LocalDateTime.now());
		lerningOpted.setSemstrDetailsSemesterSubId(semesterSubId);
		
		optedService.saveAddlLearning(lerningOpted);
	}
	
	public void deleteAddlOpted(String regNo, String addlLerCode,HttpSession session)
	{
		String IpAddress = (String) session.getAttribute("IpAddress");
		String userId = (String) session.getAttribute("userId");

		optedService.deleteAddlLearning(regNo,addlLerCode, userId, IpAddress, "D");
	}
	
	
	/*public void updateOptedMinorBasketCode(String registerNumber, String subRegistrationOption) {

		StudentDetail updateStudentDetail = studentDetailMongoRepository.findByRegisterNumber(registerNumber);
		if (updateStudentDetail != null)
		{
			updateStudentDetail.setOptedCBCSMinor(subRegistrationOption);
			studentDetailMongoRepository.save(updateStudentDetail);
		}
		
	}*/



	public void writeToLog(String userId,String ipAddress,String activity)  {

		CourseRegUserActivityLogPK logPk = new CourseRegUserActivityLogPK();
		logPk.setActivity(activity);
		logPk.setRequestUrl("");
		logPk.setUserId(userId);
		logPk.setRequestTimestamp(LocalDateTime.now());


		CourseRegUserActivityLog log = new CourseRegUserActivityLog();
		log.setId(logPk);
		log.setClientIpAddress(ipAddress);
		log.setResponseCode(String.valueOf(200));

		logService.doSaveCourseRegActivityLog(log);

	}
}
