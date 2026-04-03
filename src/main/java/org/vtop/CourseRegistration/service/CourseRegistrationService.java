package org.vtop.CourseRegistration.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.GlobalMaster;
import org.vtop.CourseRegistration.model.CourseRegistrationModel;
import org.vtop.CourseRegistration.model.CourseRegistrationPKModel;
import org.vtop.CourseRegistration.repository.CourseRegistrationRepository;


@Service
@Transactional(readOnly=true)
public class CourseRegistrationService
{
	@Autowired private CourseRegistrationRepository courseRegistrationRepository;
	
	@Autowired GlobalMaster master;
		
	public CourseRegistrationModel getOne(CourseRegistrationPKModel courseRegistrationPKModel)
	{
		return courseRegistrationRepository.findById(courseRegistrationPKModel).orElse(null);
	}
	
	public List<Object[]> getByRegisterNumber3(String semesterSubId, String registerNumber)
	{
		return courseRegistrationRepository.findByRegisterNumber3(semesterSubId, registerNumber);
	}
	
	public List<Object[]> getByRegisterNumberAndClassGroup(String semesterSubId, String registerNumber, String[] classGroupId)
	{
		return courseRegistrationRepository.findByRegisterNumberAndClassGroup(semesterSubId, registerNumber, classGroupId);
	}
	
	public List<CourseRegistrationModel> getByRegisterNumberCourseIdByClassGroupId(String semesterSubId, 
											String registerNumber, String courseId, String[] classGroupId)
	{
		return courseRegistrationRepository.findByRegisterNumberCourseIdByClassGroupId(semesterSubId, 
					registerNumber, courseId, classGroupId);
	}
	
	public CourseRegistrationModel getByRegisterNumberCourseIdAndType(String semesterSubId, String registerNumber, 
										String courseId, String courseType)
	{
		return courseRegistrationRepository.findByRegisterNumberCourseIdAndType(semesterSubId, registerNumber, 
					courseId, courseType);
	}
	
	public List<CourseRegistrationModel> getByRegisterNumberCourseCode(String semesterSubId, String registerNumber, 
											String courseCode)
	{
		return courseRegistrationRepository.findByRegisterNumberCourseCode(semesterSubId, registerNumber, courseCode);
	}
	
	public Integer getRegisterNumberTCCountByClassGroupId(String semesterSubId, String registerNumber, 
						String[] classGroupId)
	{
		Integer tempCount = 0;
		
		tempCount = courseRegistrationRepository.findRegisterNumberTCCountByClassGroupId(semesterSubId, 
						registerNumber, classGroupId);
		if (tempCount == null)
		{
			tempCount = 0;
		}
		
		return tempCount;
	}
		
	public Integer getCourseCountByRegisterNumberAndCourseOption(String semesterSubId, String registerNumber, 
						List<String> courseOption)
	{
		Integer tempCount = 0;
		
		tempCount = courseRegistrationRepository.findCourseCountByRegisterNumberAndCourseOption(
						semesterSubId, registerNumber, courseOption);
		if (tempCount == null)
		{
			tempCount = 0;
		}
		
		return tempCount;
	}
	
	public Integer getCourseCountByRegisterNumberAndCourseOptionAndCourseCode(String registerNumber, 
			String courseOption, List<String> courseCode)
	{
		Integer tempCount = 0;
		
		tempCount = courseRegistrationRepository.findCourseCountByRegisterNumberAndCourseOptionAndCourseCode(
				registerNumber, courseOption, courseCode);
		if (tempCount == null)
		{
			tempCount = 0;
		}
	
		return tempCount;
	}
	
	public List<String> getCourseCodeByAddiLearnCode(String addiLearnCode)
	{
		List<String> courseCodeList = new ArrayList<>();
		
		courseCodeList = courseRegistrationRepository.findCourseCodeByAddiLearnCode(
				addiLearnCode);
		
		return courseCodeList;
	}
		
	public Integer getGICourseCountByRegisterNumberCourseOptionAndClassGroup(String semesterSubId, String registerNumber, 
						String[] classGroupId)
	{
		Integer tempCount = 0;
		
		tempCount = courseRegistrationRepository.findCourseCountByRegisterNumberCourseOptionAndClassGroup(semesterSubId, 
						registerNumber, Arrays.asList("GI","GICE"), Arrays.asList(classGroupId));
		if (tempCount == null)
		{
			tempCount = 0;
		}
		
		return tempCount;
	}
		
	public Float getRegCreditByRegisterNumber(String semesterSubId, String registerNumber )
	{
		Float tempCount = 0F;
		
		tempCount = courseRegistrationRepository.findRegCreditByRegisterNumber(semesterSubId, 
						registerNumber);
		if (tempCount == null)
		{
			tempCount = 0F;
		}
		
		return tempCount;
	}	
	
	public Integer getProjectCourseCountByRegisterNumber(String semesterSubId, String registerNumber, List<String> evaluationType)
	{
		Integer tempCount = 0;
		
		tempCount = courseRegistrationRepository.findProjectCourseCountByRegisterNumber(semesterSubId, registerNumber, evaluationType);
		if (tempCount == null)
		{
			tempCount = 0;
		}
		
		return tempCount;
	}
	
	
	public List<Object[]> getRegisteredSlots2(String semesterSubId, String registerNumber)
	{
		return courseRegistrationRepository.findRegisteredSlots2(semesterSubId, registerNumber);
	}
	
	public List<Object[]> getRegisteredSlotsByNotClassGroup(String semesterSubId, String registerNumber, List<String> classGroupId)
	{
		return courseRegistrationRepository.findRegisteredSlotsByNotClassGroup(semesterSubId, registerNumber, classGroupId);
	}
	
	public List<Object[]> getRegisteredSlotsBySemesterAndNotClassGroup(List<String> semesterSubId, String registerNumber, List<String> classGroupId)
	{
		return courseRegistrationRepository.findRegisteredSlotsBySemesterAndNotClassGroup(semesterSubId, registerNumber, classGroupId);
	}
	
	
	public List<Object[]> getRegisteredSlotsforUpdate2(String semesterSubId, String registerNumber, String oldClassId)
	{
		return courseRegistrationRepository.findRegisteredSlotsforUpdate2(semesterSubId, registerNumber, oldClassId);
	}
	
	public List<Object[]> getRegisteredSlotsByNotClassGroupforUpdate(String semesterSubId, String registerNumber, String oldClassId, 
								List<String> classGroupId)
	{
		return courseRegistrationRepository.findRegisteredSlotsByNotClassGroupforUpdate(semesterSubId, registerNumber, oldClassId, 
					classGroupId);
	}
	
	public List<Object[]> getRegisteredSlotsBySemesterAndNotClassGroupforUpdate(List<String> semesterSubId, String registerNumber, String oldClassId, 
								List<String> classGroupId)
	{
		return courseRegistrationRepository.findRegisteredSlotsBySemesterAndNotClassGroupforUpdate(semesterSubId, registerNumber, oldClassId, 
					classGroupId);
	}	
	
	public List<Object[]> getRegistrationSlotDetail(String semesterSubId, String registerNumber)
	{
		return courseRegistrationRepository.findRegistrationSlotDetail(semesterSubId, registerNumber);
	}
	
	
	public List<String> getRegisteredCourseByClassGroup(String semesterSubId, String registerNumber, 
							String[] classGroupId)
	{
		return courseRegistrationRepository.findRegisteredCourseByClassGroup(semesterSubId, registerNumber, classGroupId);
	}
	
	public List<String> getPrevSemCourseRegistrationPARequisiteByRegisterNumber( String semesterSubId, String registerNumber, List<String> courseCode)
	{
		return courseRegistrationRepository.findPrevSemCourseRegistrationPARequisiteByRegisterNumber( semesterSubId,registerNumber, courseCode);
	}
	
	public List<String> getRegistrationAndWLCourseByRegisterNumber(String semesterSubId, List<String> registerNumber)
	{
		return courseRegistrationRepository.findRegistrationAndWLCourseByRegisterNumber(semesterSubId, registerNumber);
	}
	
	public List<Object[]> getCourseRegWlSlotByStudent(String semesterSubId, String registerNumber, Integer patternId)
	{
		return courseRegistrationRepository.findCourseRegWlSlotByStudent(semesterSubId, registerNumber);
	}
	
	public List<Object[]> getCourseRegWlSlotByStudent2(String semesterSubId, String registerNumber, Integer patternId)
	{
		return courseRegistrationRepository.findCourseRegWlSlotByStudent2(semesterSubId, registerNumber);
	}
	
	public List<Object[]> getCancelCourseByRegisterNumberAndCourseCode(String registerNumber, String courseCode)
	{
		return courseRegistrationRepository.findCancelCourseByRegisterNumberAndCourseCode(registerNumber, courseCode);
	}
	
	public List<Object[]> getPrevSemCourseDetailWithCEByRegisterNumber(String registerNumber, String courseCode)
	{
		return courseRegistrationRepository.findPrevSemCourseDetailWithCEByRegisterNumber(registerNumber, courseCode);
	}
		
	//For registration purpose
	public List<Object[]> getRegistrationAndWLByRegisterNumberAndCourseCode(String semesterSubId, String registerNumber, 
							String courseCode)
	{
		return courseRegistrationRepository.findRegistrationAndWLByRegisterNumberAndCourseCode(semesterSubId, registerNumber, 
					courseCode);
	}
	
	public List<Object[]> getCERegistrationAndWLByRegisterNumberAndCourseCode(String semesterSubId, String registerNumber, 
							String courseCode)
	{
		return courseRegistrationRepository.findCERegistrationAndWLByRegisterNumberAndCourseCode(semesterSubId, registerNumber, 
			courseCode);
	}

	
	public List<Object[]> getCourseOptionByRegisterNumberAndCourseCode(String semesterSubId, String registerNumber, 
								String courseCode)
	{
		return courseRegistrationRepository.findCourseOptionByRegisterNumberAndCourseCode(semesterSubId, registerNumber, 
				courseCode);
	}

	//Blocked Course Id List for Update
	public List<String> getBlockedCourseIdByRegisterNumberForUpdate(String semesterSubId, String registerNumber)
	{
		List<String> tempCourseIdList = new ArrayList<String>();
		
		tempCourseIdList = courseRegistrationRepository.findBlockedCourseIdByRegisterNumberForUpdate2(
								semesterSubId, registerNumber);
		if (tempCourseIdList.isEmpty())
		{
			tempCourseIdList.add("NONE");
		}
						
		return tempCourseIdList;
	}
	
	//Blocked Course Id List for Delete
	public List<String> getBlockedCourseIdByRegisterNumberForDelete(String semesterSubId, String registerNumber)
	{
		List<String> tempCourseIdList = new ArrayList<String>();
		
		tempCourseIdList = courseRegistrationRepository.findBlockedCourseIdByRegisterNumberForDelete(semesterSubId, 
								registerNumber);
		if (tempCourseIdList.isEmpty())
		{
			tempCourseIdList.add("NONE");
		}
				
		return tempCourseIdList;
	}
	
	//UE Registered Course(s)
	public List<String> getUECourseByRegisterNumber(String registerNumber)
	{
		return courseRegistrationRepository.findUECourseByRegisterNumber(registerNumber);
	}
	
	public List<String> getConvertedElectiveCourseByRegisterNumberAndCourseOption(String semesterSubId, String registerNumber, 
							String courseCategory)
	{
		List<String> tempCourseList = new ArrayList<String>();
		
		if (courseCategory.equals("UC"))
		{
			tempCourseList = courseRegistrationRepository.findElectiveCourseByRegisterNumberAndCourseOption(semesterSubId, 
									registerNumber, Arrays.asList("RUCUE"));
		}
		else if (courseCategory.equals("PE"))
		{
			tempCourseList = courseRegistrationRepository.findElectiveCourseByRegisterNumberAndCourseOption(semesterSubId, 
									registerNumber, Arrays.asList("RPEUE","RGA","HON"));
		}
		else if (courseCategory.equals("UE"))
		{
			tempCourseList = courseRegistrationRepository.findElectiveCourseByRegisterNumberAndCourseOption(semesterSubId, 
									registerNumber, Arrays.asList("RUCUE","RPEUE","RGA","MIN"));
		}
		else if(courseCategory.equals("DE"))
		{
			tempCourseList = courseRegistrationRepository.findElectiveCourseByRegisterNumberAndCourseOption(semesterSubId, 
					registerNumber, Arrays.asList("RDEOE","RGA"));
	
		}
		else if(courseCategory.equals("SPE"))
		{
			tempCourseList = courseRegistrationRepository.findElectiveCourseByRegisterNumberAndCourseOption(semesterSubId, 
					registerNumber, Arrays.asList("RSEOE","RGA","MIN"));
	
		}
		return tempCourseList;
	}
	
	
	//For Compulsory Course Checking
	public List<Object[]> getByRegisterNumberCourseCode3(String semesterSubId, String registerNumber, 
								String courseCode)
	{
		return courseRegistrationRepository.findByRegisterNumberCourseCode3(semesterSubId, registerNumber, courseCode);
	}
	
	public List<Object[]> getByRegisterNumberCourseCodeAndExcludeSemesterSubId(List<String> registerNumber, 
								String courseCode, String semesterSubId)
	{
		return courseRegistrationRepository.findByRegisterNumberCourseCodeAndExcludeSemesterSubId(registerNumber, 
						courseCode, semesterSubId);
	}	

	public List<Object[]> getResultUnpublishedSemesterCreditDetail(String registerNumber, List<String> courseOptionCode)
	{				
		return courseRegistrationRepository.findResultUnpublishedSemesterCreditDetail(registerNumber, courseOptionCode);
	}

	public List<Object[]> getRegisteredSemesterCreditDetail(String registerNumber, List<String> courseOptionCode)
	{				
		return courseRegistrationRepository.findRegisteredSemesterCreditDetail(registerNumber, courseOptionCode);
	}

	public String getGradeCategory(int admissionYear, String courseCategory, String genericCourseType, String programmeGroupCode)
	{
		String returnGradeCategory = "CG";
		
		if ((admissionYear == 2018) && programmeGroupCode.equals("BSC4"))
		{
			if (courseCategory.equals("NC"))
			{
				returnGradeCategory = "NCPF";
			}
			else if (courseCategory.equals("BC"))
			{
				returnGradeCategory = "NCG";
			}	
		}
		else if ((admissionYear >= 2019) && (courseCategory.equals("NC") || courseCategory.equals("BC")))
		{
			if (genericCourseType.equals("ECA"))
			{
				returnGradeCategory = "NCPF";
			}
			else
			{
				returnGradeCategory = "NCG";
			}	
		}
		else if ((admissionYear >= 2021) && (courseCategory.equals("NGCR") || courseCategory.equals("FCNG")))
		{
			if (genericCourseType.equals("PJT"))
			{
				returnGradeCategory = "NCPF";
			}
			else
			{
				returnGradeCategory = "NCG";
			}	
		}
		
		return returnGradeCategory;
	}
	
	
	//New Service
	public List<Object[]> getRegistrationAndWLWithCEByRegisterNumberAndCourseCode(String semesterSubId, String registerNumber, 
								String courseCode)
	{
		return courseRegistrationRepository.findRegistrationAndWLWithCEByRegisterNumberAndCourseCode(semesterSubId, 
					registerNumber, courseCode);
	}
	
	public List<Object[]> getCourseDetailFromRegistrationAndStudentHistoryByRegisterNoAndCourseCode(String registerNumber, 
								String courseCode)
	{
		return courseRegistrationRepository.findCourseDetailFromRegistrationAndStudentHistoryByRegisterNoAndCourseCode(
					registerNumber, courseCode);
	}
	
	public List<Object[]> getCourseDetailFromRegistrationAndStudentHistoryByRegisterNoCourseCodeAndExcludeSemester(String registerNumber, 
								String courseCode, String semesterSubId)
	{
		return courseRegistrationRepository.findCourseDetailFromRegistrationAndStudentHistoryByRegisterNoCourseCodeAndExcludeSemester(
					registerNumber, courseCode, semesterSubId);
	}
	
	public List<String> getPreviousSemesterCourseByRegisterNumber(String semesterSubId, String registerNumber)
	{
		return courseRegistrationRepository.findPreviousSemesterCourseByRegisterNumber( semesterSubId, registerNumber);
	}
	
	public Float getPreviousSemesterCreditByRegisterNumber(String registerNumber)
	{
		Float returnCredit = 0f;
		
		returnCredit = courseRegistrationRepository.findPreviousSemesterCreditByRegisterNumber(registerNumber);
		if (returnCredit == null)
		{
			returnCredit = 0f;
		}
		
		return returnCredit;
	}
	
	
	//Compulsory Course Registration & Allocation Status
	public List<Object[]> getCompulsoryCourseRegistrationAndAllocationStatus(String semesterSubId, String registerNumber, List<String> compCourseCode, 
								String[] classGroupId, String[] classType, String progGroupCode, String progSpecCode, String costCentreCode, 
								String[] courseSystem)
	{
		List<Object[]> returnObjectList = new ArrayList<>();
		
		if (progGroupCode.equals("RP"))
		{
			returnObjectList = courseRegistrationRepository.findCompulsoryCourseRegistrationAndAllocationForRP(semesterSubId, registerNumber, 
									compCourseCode, classGroupId, classType, courseSystem);
		}
		else
		{
			returnObjectList = courseRegistrationRepository.findCompulsoryCourseRegistrationAndAllocation(semesterSubId, registerNumber, 
									compCourseCode, classGroupId, classType, progGroupCode, progSpecCode, costCentreCode, courseSystem);
		}
		
		return returnObjectList;
	}
	
	public List<String> getByRegisterNumberAndCourseCodeForPARequisite( String semesterSubId, String registerNumber, List<String> courseCode)
	{
		return courseRegistrationRepository.findByRegisterNumberAndCourseCodeForPARequisite( semesterSubId, registerNumber, courseCode);
	}
	
	
	public GlobalMaster doGetAppGlobalValues(String semesterSubId,String classGroupId, int activityId)
	{
		
		List<Object[]>  globalData = courseRegistrationRepository.doGetAppGlobalValues(semesterSubId, classGroupId, activityId);
		
		//GlobalMaster master = new GlobalMaster(); 
		
		master.setActivityId(Integer.parseInt(globalData.get(0)[2].toString()));
		master.setMaxCredits((int)Float.parseFloat(globalData.get(0)[3].toString()));
		master.setMinCredits((int)Float.parseFloat(globalData.get(0)[4].toString()));
		master.setFinalYrMaxCredits((int)Float.parseFloat(globalData.get(0)[5].toString()));
		master.setLowCgpa((int)Float.parseFloat(globalData.get(0)[6].toString()));
		master.setLowCgpaMaxCredits((int)Float.parseFloat(globalData.get(0)[7].toString()));
		master.setRegApprovalStatus(Integer.parseInt(globalData.get(0)[8].toString()));
		master.setRegularCourseStatus(Integer.parseInt(globalData.get(0)[9].toString()));
		master.setnGradeCourseStatus(Integer.parseInt(globalData.get(0)[10].toString()));
		master.setGradeImpCourseStatus(Integer.parseInt(globalData.get(0)[11].toString()));
		master.setAuditCourseStatus(Integer.parseInt(globalData.get(0)[12].toString()));
		master.setMinorHonorCourseStatus(Integer.parseInt(globalData.get(0)[13].toString()));
		master.setAddlCourseStatus(Integer.parseInt(globalData.get(0)[14].toString()));
		master.setPeAddlCourseStatus(Integer.parseInt(globalData.get(0)[15].toString()));
		master.setUeAddlCourseStatus(Integer.parseInt(globalData.get(0)[16].toString()));
		master.setDeOeAllowStatus(Integer.parseInt(globalData.get(0)[17].toString()));
		master.setSpeOeAllowStatus(Integer.parseInt(globalData.get(0)[18].toString()));
		master.setN2N4AllowStatus(Integer.parseInt(globalData.get(0)[19].toString()));
		master.setCompCourseStatus(Integer.parseInt(globalData.get(0)[20].toString()));
		
		
		return master;
	}
	
	public Map<String,String> doGetRegClassIdsELAETH(String semesterId,String courseId,String regNo)
	{
		List<Object[]> classIdAndClrsType =  courseRegistrationRepository.doGetRegisteredElaEthClassIds(semesterId, courseId, regNo);
		Map<String,String> returnMap = new HashMap<>();
		for (Object[] objects : classIdAndClrsType) {
			returnMap.put(objects[1].toString(), objects[0].toString());
		}
		return returnMap;

	}
	
	public List<Object[]> findRegisteredSlotsNotInCourseIdBySemesterAndRegNo(String semesterSubId, String registerNumber,String courseId)
	{
		return courseRegistrationRepository.findRegisteredSlotsNotInCourseIdBySemesterAndRegNo(semesterSubId, registerNumber, courseId);
	}
	
	public List<Object[]> doGetUserDetailsInfo (String nickName)
	{
		return courseRegistrationRepository.doGetUserDetailsInfo(nickName);
	}
	
	public List<Object[]> doGetUserDetailsInfoByUserId (String userId)
	{
		return courseRegistrationRepository.doGetUserDetailsInfoByUserId(userId);
	}
	
	public String getLockStatusDescription(String regNo) {
		return courseRegistrationRepository.getStudentLockDescription(regNo);
	}
	
	
	public List<Object[]> doGetRegisteredCourseByCourseCateg(String semesterSubId,String regNo,String coursecatg)
	{
		return courseRegistrationRepository.doGetRegisteredCourseByCourseCateg(semesterSubId, regNo, coursecatg);
	}
	
	
	
	
}
