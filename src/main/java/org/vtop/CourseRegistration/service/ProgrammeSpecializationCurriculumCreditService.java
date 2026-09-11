package org.vtop.CourseRegistration.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.model.CurriculumCreditCalculationDto;
import org.vtop.CourseRegistration.model.ProgrammeSpecializationCurriculumCategoryCredit;
import org.vtop.CourseRegistration.repository.ProgrammeSpecializationCurriculumCreditRepository;


@Service
@Transactional(readOnly=true)
public class ProgrammeSpecializationCurriculumCreditService
{		
	@Autowired private ProgrammeSpecializationCurriculumCreditRepository programmeSpecializationCurriculumCreditRepository;
	@Autowired private CourseRegistrationService courseRegistrationService;
	@Autowired private ProgrammeSpecializationCurriculumDetailService programmeSpecializationCurriculumDetailService;
	@Autowired private StudentHistoryService studentHistoryService;
	@Autowired private SemesterMasterService semesterMasterService;
	
	private static final Logger LOGGER = LogManager.getLogger(ProgrammeSpecializationCurriculumCreditService.class);

	
	public List<Object[]> getMaxVerDetailBySpecIdAndAdmYear(Integer specId, Integer admissionYear)
	{
		return programmeSpecializationCurriculumCreditRepository.findMaxVerDetailBySpecIdAndAdmYear(specId, admissionYear);
	}
	
	public List<Object[]> getCurrentSemRegCurCtgCreditByRegisterNo(Integer specializationId, Integer admissionYear, 
								Float curriculumVersion, String semSubId, String registerNumber,String courseCategory,String studySystem)
	{	
		List<Object[]> returnObjectList = new ArrayList<Object[]>();
		
		
		float courseCredit = 0, basketCredit = 0, totalCredit = 0;
		String categoryCode = "", catalogType = "", courseCode = "", basketCode = "";
		
		Object[] objectArray = null;
		CurriculumCreditCalculationDto curriculumCreditCalculationDto = null;
		List<Object[]> objectList = new ArrayList<>();
		
		LinkedHashMap<String, CurriculumCreditCalculationDto> ccCalculationMapList = new LinkedHashMap<>();
		Map<String, Object[]> ccMapList = new HashMap<>();
		Map<String, Map<String, Float>> totalMapList = new HashMap<>();
		Map<String, Float> totalMapList2 = new HashMap<>();
				
		ccCalculationMapList = getCreditDetail(specializationId, admissionYear, curriculumVersion);
		ccMapList = programmeSpecializationCurriculumDetailService.getStudentCurriculumByRegisterNumber(specializationId, 
						admissionYear, curriculumVersion, registerNumber,courseCategory,studySystem);
				
		if ((!ccCalculationMapList.isEmpty()) && (!ccMapList.isEmpty()))
		{			
			//Result Published (i.e. Academics History) - Credit Count
			objectList = studentHistoryService.getByRegisterNumberCourseOptionAndGrade(registerNumber, Arrays.asList("HON","MIN"), 
								Arrays.asList("U","W"));
			LOGGER.trace("\n Result Published Data: "+ objectList.size());
			if (!objectList.isEmpty())
			{
				for (Object[] e : objectList)
				{
					//LOGGER.trace("\n "+ Arrays.deepToString(e));
					categoryCode = "UE";
					catalogType = "CC";
					basketCode = "NONE";
					basketCredit = 0;
					totalCredit = 0;
					objectArray = new Object[] {};
					
					courseCode = e[1].toString();
					courseCredit = Float.parseFloat(e[4].toString());
														
					if (ccMapList.containsKey(courseCode))
					{
						objectArray = ccMapList.get(courseCode);
						if (objectArray.length > 0)
						{
							categoryCode = objectArray[0].toString();
							catalogType = objectArray[1].toString();
							basketCode = objectArray[5].toString();
							basketCredit = Float.parseFloat(objectArray[6].toString());
						}
					}
					
					if (totalMapList.containsKey(categoryCode))
					{
						totalMapList2 = totalMapList.get(categoryCode);
						if (totalMapList2.containsKey(categoryCode +"_"+ basketCode))
						{
							totalCredit = totalMapList2.get(categoryCode +"_"+ basketCode);
							
							if (catalogType.equals("BC"))
							{
								totalCredit = totalCredit + courseCredit;
								if (totalCredit > basketCredit)
								{
									totalCredit = basketCredit;
								}
							}
							else
							{
								totalCredit = totalCredit + courseCredit;
							}
							
							totalMapList2.replace(categoryCode +"_"+ basketCode, totalCredit);
						}
						else
						{	
							totalMapList2.put(categoryCode +"_"+ basketCode, courseCredit);
						}
						
						totalMapList.replace(categoryCode, totalMapList2);
					}
					else
					{	
						totalMapList2 = new HashMap<String, Float>();
						totalMapList2.put(categoryCode +"_"+ basketCode, courseCredit);
						totalMapList.put(categoryCode, totalMapList2);
					}
				}
				
				if (!totalMapList.isEmpty())
				{
					for (Map.Entry<String, Map<String, Float>> entry : totalMapList.entrySet())
					{
						categoryCode = entry.getKey();
						totalCredit = 0;
						curriculumCreditCalculationDto = new CurriculumCreditCalculationDto();
						LOGGER.trace("\n entry key: "+ entry.getKey());
						
						if (ccCalculationMapList.containsKey(categoryCode))
						{
							curriculumCreditCalculationDto = ccCalculationMapList.get(categoryCode);
							if (curriculumCreditCalculationDto != null)
							{
								totalCredit = curriculumCreditCalculationDto.getResultPublishedCredit();
								for (Map.Entry<String, Float> entry2 : entry.getValue().entrySet())
								{
									LOGGER.trace("\n entry2 key: "+ entry2.getKey() +" | entry2 value: "+ entry2.getValue());
									totalCredit = totalCredit + entry2.getValue();
								}
								
								curriculumCreditCalculationDto.setResultPublishedCredit(totalCredit);
								ccCalculationMapList.replace(categoryCode, curriculumCreditCalculationDto);
							}
						}
					}
				}
			}
			
			//Result UnPublished (i.e. Previous Semester Registration) - Credit Count
			objectList.clear();
			totalMapList.clear();
			totalMapList2.clear();
			
			objectList = courseRegistrationService.getResultUnpublishedSemesterCreditDetail(registerNumber, AppGlobalValues.REG_COURSE_OPTION);
			LOGGER.trace("\n Result UnPublished Data: "+ objectList.size());
			if (!objectList.isEmpty())
			{
				for (Object[] e : objectList)
				{
					//LOGGER.trace("\n "+ Arrays.deepToString(e));
					categoryCode = "UE";
					catalogType = "CC";
					basketCode = "NONE";
					basketCredit = 0;
					totalCredit = 0;
					objectArray = new Object[] {};
					
					courseCode = e[2].toString();
					courseCredit = Float.parseFloat(e[7].toString());
														
					if (ccMapList.containsKey(courseCode))
					{
						objectArray = ccMapList.get(courseCode);
						if (objectArray.length > 0)
						{
							categoryCode = objectArray[0].toString();
							catalogType = objectArray[1].toString();
							basketCode = objectArray[5].toString();
							basketCredit = Float.parseFloat(objectArray[6].toString());
						}
					}
					
					if (totalMapList.containsKey(categoryCode))
					{
						totalMapList2 = totalMapList.get(categoryCode);
						if (totalMapList2.containsKey(categoryCode +"_"+ basketCode))
						{
							totalCredit = totalMapList2.get(categoryCode +"_"+ basketCode);
							
							if (catalogType.equals("BC"))
							{
								totalCredit = totalCredit + courseCredit;
								if (totalCredit > basketCredit)
								{
									totalCredit = basketCredit;
								}
							}
							else
							{
								totalCredit = totalCredit + courseCredit;
							}
							
							totalMapList2.replace(categoryCode +"_"+ basketCode, totalCredit);
						}
						else
						{	
							totalMapList2.put(categoryCode +"_"+ basketCode, courseCredit);
						}
						
						totalMapList.replace(categoryCode, totalMapList2);
					}
					else
					{	
						totalMapList2 = new HashMap<String, Float>();
						totalMapList2.put(categoryCode +"_"+ basketCode, courseCredit);
						totalMapList.put(categoryCode, totalMapList2);
					}
				}
				
				if (!totalMapList.isEmpty())
				{
					for (Map.Entry<String, Map<String, Float>> entry : totalMapList.entrySet())
					{
						categoryCode = entry.getKey();
						totalCredit = 0;
						curriculumCreditCalculationDto = new CurriculumCreditCalculationDto();
						//LOGGER.trace("\n entry key: "+ entry.getKey());
						
						if (ccCalculationMapList.containsKey(categoryCode))
						{
							curriculumCreditCalculationDto = ccCalculationMapList.get(categoryCode);
							if (curriculumCreditCalculationDto != null)
							{
								totalCredit = curriculumCreditCalculationDto.getResultUnpublishedCredit();
								for (Map.Entry<String, Float> entry2 : entry.getValue().entrySet())
								{
									//LOGGER.trace("\n entry2 key: "+ entry2.getKey() +" | entry2 value: "+ entry2.getValue());
									totalCredit = totalCredit + entry2.getValue();
								}
								
								curriculumCreditCalculationDto.setResultUnpublishedCredit(totalCredit);
								ccCalculationMapList.replace(categoryCode, curriculumCreditCalculationDto);
							}
						}
					}
				}
			}
			
						
			//Course Registration Waiting List (i.e. Ongoing Semester) - Credit Count
			objectList.clear();
			totalMapList.clear();
			totalMapList2.clear();
			
			//Over All Calculation
			for (CurriculumCreditCalculationDto e : ccCalculationMapList.values())
			{
			//	System.out.println("Testing UE"+e.getCategoryCode()+" - "+e.getResultPublishedCredit());
				e.setObtainedCredit(e.getResultPublishedCredit() + e.getResultUnpublishedCredit() 
						+ e.getRegisteredCredit() + e.getWaitingListCredit());
				e.setRemainingCredit((float)e.getCategoryCredit() - (e.getResultPublishedCredit() + e.getResultUnpublishedCredit() 
						+ e.getRegisteredCredit() + e.getWaitingListCredit()));
				
				returnObjectList.add(new Object[] {e.getCategoryCode(), e.getCategoryCredit(), e.getResultPublishedCredit(), 
						e.getResultUnpublishedCredit(), e.getRegisteredCredit(), e.getWaitingListCredit(), 
						e.getObtainedCredit(), e.getRemainingCredit(), e.getCategoryNumber(), e.getCategoryDescription()});
			}
		}
						
		return returnObjectList;
	}	
		

	public Integer getBasketCtgCreditByRegisterNo(String semSubId, String registerNumber, String basketId)
	{	
		Integer tempCredit = 0;
		
		List<String> tempHistGrade = new ArrayList<String>();
		List<String> tempCourseOption = new ArrayList<String>();
		List<String> tempUECourseCode = new ArrayList<String>();
		List<String> tempBasketCourseCode = new ArrayList<String>();
		List<String> tempPSCourseCode = new ArrayList<String>();
				
		tempHistGrade.addAll(Arrays.asList("U","W"));
		tempCourseOption.addAll(Arrays.asList("RGCE","RGP","RGR","RGW","RPCE","RWCE","RPEUE","RUCUE","RUEPE","RDEOE","RSEOE"));
				
		tempUECourseCode = courseRegistrationService.getUECourseByRegisterNumber(registerNumber);
		if (tempUECourseCode.isEmpty())
		{
			tempUECourseCode.add("NONE");
		}
		
		tempBasketCourseCode = semesterMasterService.getBasketCourseCatalogCourseCodeByBasketId(basketId);
		if (tempBasketCourseCode.isEmpty())
		{
			tempBasketCourseCode.add("NONE");
		}
		
		tempPSCourseCode = courseRegistrationService.getPreviousSemesterCourseByRegisterNumber( semSubId, registerNumber);
		if (tempPSCourseCode.isEmpty())
		{
			tempPSCourseCode.add("NONE");
		}
				
		LOGGER.trace("\n semSubId: "+ semSubId +" | registerNumber: "+ registerNumber 
				+" | basketId: "+ basketId);				
		LOGGER.trace("\n Basket Course: "+ tempBasketCourseCode);
		LOGGER.trace("\n Result Published Course: "+ tempPSCourseCode);
		
		tempCredit = programmeSpecializationCurriculumCreditRepository.findStudentBasketCreditDetailByRegisterNo(
							semSubId, registerNumber, tempHistGrade, tempCourseOption, tempUECourseCode, 
							tempBasketCourseCode, tempPSCourseCode);

		if (tempCredit == null)
		{
			tempCredit = 0;
		}
		
		return tempCredit;
	}
	
	private LinkedHashMap<String, CurriculumCreditCalculationDto> getCreditDetail(Integer specializationId, Integer admissionYear, Float curriculumVersion)
	{	
		LinkedHashMap<String, CurriculumCreditCalculationDto> returnMapList = new LinkedHashMap<String, CurriculumCreditCalculationDto>();
		
		CurriculumCreditCalculationDto curriculumCreditCalculationDto = new CurriculumCreditCalculationDto();
		List<ProgrammeSpecializationCurriculumCategoryCredit> modelList = new ArrayList<ProgrammeSpecializationCurriculumCategoryCredit>();
		
		modelList = getBySpecializationIdAdmissionYearAndVersion(specializationId, admissionYear, curriculumVersion);
		if (!modelList.isEmpty())
		{
			for (ProgrammeSpecializationCurriculumCategoryCredit e : modelList)
			{
				curriculumCreditCalculationDto = new CurriculumCreditCalculationDto();
			
				curriculumCreditCalculationDto.setCategoryCode(e.getId().getCourseCategory());
				curriculumCreditCalculationDto.setCategoryDescription(e.getCurriculumCategoryMaster().getDescription());
				curriculumCreditCalculationDto.setCategoryNumber(e.getCurriculumCategoryMaster().getOrderNo());
				curriculumCreditCalculationDto.setCategoryCredit(e.getCreditMaximum());
				curriculumCreditCalculationDto.setResultPublishedCredit(0);
				curriculumCreditCalculationDto.setResultUnpublishedCredit(0);
				curriculumCreditCalculationDto.setRegisteredCredit(0);
				curriculumCreditCalculationDto.setWaitingListCredit(0);
				curriculumCreditCalculationDto.setObtainedCredit(0);
				curriculumCreditCalculationDto.setRemainingCredit(e.getCreditMaximum());
					
				returnMapList.put(e.getId().getCourseCategory(), curriculumCreditCalculationDto);
			}
		}
						
		return returnMapList;
	}
	
	
	//Curriculum Category Credit
	public List<ProgrammeSpecializationCurriculumCategoryCredit> getBySpecializationIdAdmissionYearAndVersion(Integer specializationId, 
																		Integer admissionYear, Float curriculumVersion)
	{
		return programmeSpecializationCurriculumCreditRepository.findBySpecializationIdAdmissionYearAndVersion(
						specializationId, admissionYear, curriculumVersion);
	}

	public Float getBasketRegisterCreditByRegisterNumber(String semesterSubId, String registerNumber, String basketId)
	{	
		Float returnCredit = 0F;
		List<String> basketCourseCodeList = new ArrayList<>();
					
		basketCourseCodeList = semesterMasterService.getBasketCourseCatalogCourseCodeByBasketId(basketId);
		if (basketCourseCodeList.isEmpty())
		{
			basketCourseCodeList.add("NONE");
		}
				
		returnCredit = programmeSpecializationCurriculumCreditRepository.findBasketRegisterCreditByRegisterNumber(
						 	 registerNumber, Arrays.asList("U","W"), AppGlobalValues.REG_COURSE_OPTION,
						 	basketCourseCodeList);
		if (returnCredit == null)
		{
			returnCredit = 0F;
		}
		
		return returnCredit;
	}
	
	
	
	/*public Map<String,Map<String,Integer>> doGetCurriculumCredits(Integer specId, Integer admissionYear,
			String[] courseCategoryList,Integer combinedCreditMin,Integer combinedCreditMax)
	{
		Map<String,Map<String,Integer>> creditsMap = new HashMap<>();
		
		List<Object[]> spltnCrdt = programmeSpecializationCurriculumCreditRepository.doGetCurriculumCredits(specId, admissionYear, courseCategoryList);
		
		if(spltnCrdt!=null && !spltnCrdt.isEmpty())
		{
			for (Object[] objects : spltnCrdt) 
			{
				Map<String,Integer> minMaxCredit = new HashMap<>();
				
				minMaxCredit.put("MIN", Integer.parseInt(objects[1].toString()));
				minMaxCredit.put("MAX", Integer.parseInt(objects[2].toString()));
				
				creditsMap.put(objects[0].toString(), minMaxCredit);
			}
			
			Map<String,Integer> minMaxCredit = new HashMap<>();
			
			minMaxCredit.put("MIN", combinedCreditMin);
			minMaxCredit.put("MAX", combinedCreditMax);
			
			creditsMap.put("COMCREDIT", minMaxCredit);
		}
		
		
		return creditsMap;
	}*/
	public Map<String,Map<String,Integer>> doGetCurriculumCredits(Integer specId, Integer admissionYear)
	{
		Map<String,Map<String,Integer>> creditsMap = new HashMap<>();
		
		List<Object[]> currAndCombinedCredits = programmeSpecializationCurriculumCreditRepository.doGetCurriculumCombinedCategoryCredits(specId, admissionYear);
		
		

		if(currAndCombinedCredits!=null && !currAndCombinedCredits.isEmpty())
		{
		
		int cMinCredit = Integer.parseInt(currAndCombinedCredits.get(0)[3].toString()),
				cMaxCredit=Integer.parseInt(currAndCombinedCredits.get(0)[4].toString());
		
		for (Object[] objects : currAndCombinedCredits) 
		{
			
			Map<String,Integer> minMaxCredit = new HashMap<>();
			
			minMaxCredit.put("MIN", Integer.parseInt(objects[1].toString()));
			minMaxCredit.put("MAX", Integer.parseInt(objects[2].toString()));
			
			creditsMap.put(objects[0].toString(), minMaxCredit);
		}
		
		Map<String,Integer> minMaxCredit = new HashMap<>();
		
		minMaxCredit.put("MIN", cMinCredit);
		minMaxCredit.put("MAX", cMaxCredit);
		
		creditsMap.put("COMCREDIT", minMaxCredit);
		}
		
		return creditsMap;
	}
	
}
