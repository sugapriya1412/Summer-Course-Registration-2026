package org.vtop.CourseRegistration.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.vtop.CourseRegistration.repository.ProgrammeSpecializationCurriculumDetailRepository;


@Service
@Transactional(readOnly=true)
public class ProgrammeSpecializationCurriculumDetailService
{
	@Autowired private ProgrammeSpecializationCurriculumDetailRepository programmeSpecializationCurriculumDetailRepository;
	private static final Logger LOGGER = LogManager.getLogger(ProgrammeSpecializationCurriculumDetailService.class);
	
	
	public List<Object[]> getCurriculumByAdmsnYearCCVersionAndCourseCode(Integer specId, Integer admissionYear, 
								Float ccVersion, String courseCode)
	{
		return programmeSpecializationCurriculumDetailRepository.findCurriculumByAdmsnYearCCVersionAndCourseCode(
					specId, admissionYear, ccVersion, courseCode);
	}
	
	public List<String> getNCCourseByYearAndCCVersion(Integer specId, Integer admissionYear, Float ccVersion)
	{
		List<String> tempStringList = new ArrayList<String>();
		
		tempStringList = programmeSpecializationCurriculumDetailRepository.findNCCourseByYearAndCCVersion(specId, 
							admissionYear, ccVersion, Arrays.asList("BC","NC"));
		if (tempStringList.isEmpty())
		{
			tempStringList.add("NONE");
		}
		
		return tempStringList;
	}
	
	
	public Map<String, List<Object[]>> getCurriculumBySpecIdYearAndCCVersionAsMap(Integer specId, Integer admissionYear, 
											Float ccVersion,String studySystem,String regNo)
	{
		Map<String, List<Object[]>> tempMapList = new HashMap<String, List<Object[]>>();
		
		String tempHashKey = "";
		List<Object[]> tempObjectList = new ArrayList<Object[]>();
		List<Object[]> tempObjectMapList = new ArrayList<Object[]>();
		
		/*tempObjectList = programmeSpecializationCurriculumDetailRepository.findCurriculumByAdmsnYearAndCCVersion2(specId,
							admissionYear, ccVersion,studySystem);*/


		tempObjectList = programmeSpecializationCurriculumDetailRepository.findCurriculumByAdmsnYearAndCCVersion2ByRegNo(specId,
							admissionYear, ccVersion,studySystem,regNo);


		if (!tempObjectList.isEmpty())
		{
			for (Object[] parameters : tempObjectList)
			{
				tempHashKey = parameters[5].toString();
				if(tempMapList.containsKey(tempHashKey))
				{
					tempObjectMapList = tempMapList.get(tempHashKey);
					tempObjectMapList.add(parameters);
					tempMapList.put(tempHashKey, tempObjectMapList);
				}
				else
				{
					tempObjectMapList = new ArrayList<Object[]>();
					tempObjectMapList.add(parameters);
					tempMapList.put(tempHashKey, tempObjectMapList);
				}
			}
		}
		
		return tempMapList;
	}
	
	public Map<String, Object[]> getStudentCurriculumByRegisterNumber(Integer specializationId, Integer admissionYear, Float curriculumVersion, 
										String registerNumber,String courseCategory,String studySystem)
	{
		Map<String, Object[]> returnMapList = new HashMap<String, Object[]>();
		
		String courseCode = "";
		List<Object[]> objectList = new ArrayList<Object[]>();
		
		if ((specializationId != null) && (admissionYear != null) && (curriculumVersion != null) && (!registerNumber.isEmpty()))
		{				
			objectList = programmeSpecializationCurriculumDetailRepository.findCurriculumBySpecIdYearCCVersionAndRegisterNumber(specializationId, 
								admissionYear, curriculumVersion, registerNumber,courseCategory,studySystem);
			LOGGER.trace("\n Curriculum Detail Data: "+ objectList.size());
			if (!objectList.isEmpty())
			{
				for (Object[] e : objectList)
				{
					//LOGGER.trace("\n "+ Arrays.deepToString(e));
					courseCode = e[4].toString();
					if(!returnMapList.containsKey(courseCode))
					{
						returnMapList.put(courseCode, e);
					}
				}
			}
		}
		
		return returnMapList;
	}
	
	public List<Object[]> doGetCreditInformation(Model model, String semesterSubId , String registerNumber, float minCredit,
												 float maxCredit, int programSpecId, int studyStartYear, float curriculumVersion,List<Object[]> courseRegistrationModel)
	{
		int regCount = 0, ncCount = 0;
		float regCredit = 0, ncCredit = 0;
		String checkCourseId = "";

		List<Integer> patternIdList = new ArrayList<>();
		List<Object[]> regCreditList = new ArrayList<>();
		List<String> ncCourseList = new ArrayList<>();


		regCreditList.add(new Object[] {"Minimum", minCredit, "-"});
		regCreditList.add(new Object[] {"Maximum", maxCredit, "-"});

		if ((semesterSubId !=null) &&(registerNumber !=null))
		{
			ncCourseList = getNCCourseByYearAndCCVersion(programSpecId, 
					studyStartYear, curriculumVersion);

			if (!courseRegistrationModel.isEmpty())
			{
				for (Object[] obj : courseRegistrationModel) 
				{

					if ((Integer.parseInt(obj[23].toString()) > 0) && (!patternIdList.contains(Integer.parseInt(obj[22].toString()))))
					{
						patternIdList.add(Integer.parseInt(obj[22].toString()));
					}

					regCredit = regCredit + Float.parseFloat(obj[10].toString());

					if (ncCourseList.contains(obj[1].toString()))
					{
						ncCredit = ncCredit + Float.parseFloat(obj[10].toString());
					}

					if (!obj[0].toString().equals(checkCourseId))
					{
						regCount++;
						checkCourseId = obj[0].toString();

						if (ncCourseList.contains(obj[1].toString()))
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
		return regCreditList;
	}

	public List<Object[]> findCurriculumByAdmsnYearCCVersionAndCourseCode(Integer pProgramSpecId, Integer pStudentStartYear,
			Float pCurriculumVersion, String[] antirequisite) {
		return programmeSpecializationCurriculumDetailRepository.findCurriculumByAdmsnYearCCVersionAndCourseCode(pProgramSpecId, pStudentStartYear,
				pCurriculumVersion, antirequisite);
	}

	public List<Object[]> getCurriculumByAdmsnYearCCVersionAndCourseCode(Integer pProgramSpecId,
			Integer pStudentStartYear, Float pCurriculumVersion, List<String> eqCourseCodeList) {
		return programmeSpecializationCurriculumDetailRepository.findCurriculumByAdmsnYearCCVersionAndCourseCode(
				pProgramSpecId, pStudentStartYear, pCurriculumVersion, eqCourseCodeList);
	}

	/*public Map<String,List<Object[]>> getConcentrationList(int admissionYear,String semesterSubId,List<String> classGroupId,String progGroupShortDesc,String specShortDesc,String schoolShortDesc)
	{
		List<Object[]> getConList = programmeSpecializationCurriculumDetailRepository.doGetConcentrationBasketCourses(admissionYear,semesterSubId,classGroupId,progGroupShortDesc,specShortDesc,schoolShortDesc);

		Map<String,List<Object[]>> mapData = new HashMap<>();


		for (Object[] obj : getConList) {

			if (mapData.containsKey(obj[0].toString())) {


				List<Object[]> listData = mapData.get(obj[0].toString());

				listData.add(obj);
				mapData.put(obj[0].toString(), listData);
			}
			else {


				List<Object[]> listData = new ArrayList<>();

				listData.add(obj);
				mapData.put(obj[0].toString(), listData);
			}
		}

		return mapData;
	}*/

	public Map<String,String> doGetConBasketDetails(int admissionYear,String semesterSubId,List<String> classGroupId,String progGroupShortDesc,String specShortDesc,String schoolShortDesc)
	{
		List<Object[]> getConList = programmeSpecializationCurriculumDetailRepository.doGetConBasketDetails(admissionYear,semesterSubId,classGroupId,progGroupShortDesc,specShortDesc,schoolShortDesc);
		Map<String,String> mapData = new HashMap<>();

		for (Object[] obj : getConList) {
			mapData.put(obj[1].toString(),obj[0].toString());
		}

		return mapData;
	}

	public Map<String,String> doGetConBasketDetailsOEC(int admissionYear,String semesterSubId,List<String> classGroupId)
	{
		List<Object[]> getConList = programmeSpecializationCurriculumDetailRepository.doGetConBasketDetailsForOEC(admissionYear,semesterSubId,classGroupId);
		Map<String,String> mapData = new HashMap<>();

		for (Object[] obj : getConList) {
			mapData.put(obj[1].toString(),obj[0].toString());
		}

		return mapData;
	}

	public List<Object[]>  doGetAllOECoursesACE(int admissionYear,int progGroupId,String courseOption)
	{
		return programmeSpecializationCurriculumDetailRepository.doGetAllOECoursesACE(admissionYear,progGroupId,courseOption);
	}
	
	
}
