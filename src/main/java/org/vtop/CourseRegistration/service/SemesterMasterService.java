package org.vtop.CourseRegistration.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.model.CourseEligibleModel;
import org.vtop.CourseRegistration.model.CourseEquivalancesModel;
import org.vtop.CourseRegistration.model.CourseOptionModel;
import org.vtop.CourseRegistration.model.CourseTypeComponentModel;
import org.vtop.CourseRegistration.model.EmployeeProfile;
import org.vtop.CourseRegistration.model.SemesterDetailsModel;
import org.vtop.CourseRegistration.model.SemesterMasterModel;
import org.vtop.CourseRegistration.model.SlotTimeMasterModel;
import org.vtop.CourseRegistration.model.StudentsLoginDetailsModel;
import org.vtop.CourseRegistration.repository.SemesterMasterRepository;


@Service
@Transactional(readOnly=true)
public class SemesterMasterService
{
	@Autowired private SemesterMasterRepository semesterMasterRepository;

	@Autowired private CourseRegistrationService regService;
	
	private static final String STRINGINTIATE = "";

	public SemesterMasterModel getOne(Integer semesterId)
	{
		return semesterMasterRepository.findById(semesterId).orElse(null);
	}


	//Semester Detail
	public SemesterDetailsModel getSemesterDetailBySemesterSubId(String semesterSubId)
	{
		return semesterMasterRepository.findSemesterDetailBySemesterSubId(semesterSubId);
	}

	public List<Object[]> getSemesterDetailBySemesterSubId2(String semesterSubId)
	{
		return semesterMasterRepository.findSemesterDetailBySemesterSubId2(semesterSubId);
	}


	//Course Eligible
	public CourseEligibleModel getCourseEligibleByProgramGroupId(Integer groupId)
	{
		return semesterMasterRepository.findCourseEligibleByProgramGroupId(groupId);
	}


	//Course Equivalances
	public List<CourseEquivalancesModel> getCourseEquivalancesByCourseId(String courseId)
	{
		return semesterMasterRepository.findCourseEquivalancesByCourseId(courseId);
	}

	public List<Object[]> getCourseEquivalanceListByCourseCode(String courseCode)
	{
		return semesterMasterRepository.findCourseEquivalanceListByCourseCode(courseCode);
	}


	//Course Type Master
	public CourseTypeComponentModel getCourseTypeMasterByCourseType(String courseType)
	{
		return semesterMasterRepository.findCourseTypeMasterByCourseType(courseType);
	}


	//Course Type Component
	public List<String> getCourseTypeComponentByGenericType(String courseGenericType)
	{
		return semesterMasterRepository.findCourseTypeComponentByGenericType(courseGenericType);
	}


	//Bridge Course Condition Detail
	public String getEPTResultByRegisterNumber(String registerNumber)
	{
		return semesterMasterRepository.findEPTResultByRegisterNumber(registerNumber);
	}

	public String getPCMBStatusByRegisterNumber(String registerNumber)
	{
		return semesterMasterRepository.findPCMBStatusByRegisterNumber(registerNumber);
	}

	public String getPCMBStatusFromAdmissionsByRegisterNumber(String registerNumber)
	{
		return semesterMasterRepository.findPCMBStatusFromAdmissionsByRegisterNumber(registerNumber);
	}

	public List<Object[]> getSPTResultByRegisterNumberAndSemester(String registerNumber, Integer studentSemester)
	{
		return semesterMasterRepository.findSPTResultByRegisterNumberAndSemester(registerNumber, studentSemester);
	}

	List<Object[]> getEPTDetailByRegisterNumber(String registerNumber)
	{
		return semesterMasterRepository.findEPTDetailByRegisterNumber(registerNumber);
	}


	//CapStone Project Condition Detail
	public List<Object[]> getProjectEligibilityByProgramGroupIdAndStudYear(Integer programGroupId, Integer studYear)
	{
		return semesterMasterRepository.findProjectEligibilityByProgramGroupIdAndStudYear(programGroupId, studYear);
	}

	public List<Object[]> getStudentMaxYearProjectEligibilityByProgramGroupId(Integer programGroupId)
	{
		return semesterMasterRepository.findStudentMaxYearProjectEligibilityByProgramGroupId(programGroupId);
	}


	//Additional Learning Course Detail
	public List<Object[]> getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(Integer allowFlag, 
			String learningType, Integer groupId, Integer specId, String courseCode, String studStudySystem)
	{
		List<Object[]> tempObjectList = new ArrayList<Object[]>();

		if (allowFlag == 1)
		{
			if ((learningType.equals("HON")) && (studStudySystem.equals("CAL")))
			{
				tempObjectList = semesterMasterRepository.findAdditionalLearningTitleByLearnTypeGroupIdAndSpecId(
						learningType, groupId, specId.toString());
			}
			else
			{
				tempObjectList = semesterMasterRepository.findAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(
						learningType, groupId, specId.toString(), courseCode);
			}
		}

		return tempObjectList;
	}


	//Slot Time Master Detail
	public List<SlotTimeMasterModel> getSlotTimeMasterCommonTimeSlotBySemesterSubId(List<String> semesterSubId)
	{
		return semesterMasterRepository.findSlotTimeMasterCommonTimeSlotBySemesterSubId(semesterSubId);
	}

	public Map<String, List<SlotTimeMasterModel>> getSlotTimeMasterCommonTimeSlotBySemesterSubIdAsMap(List<String> semesterSubId)
	{
		Map<String, List<SlotTimeMasterModel>> returnMapList = new HashMap<String, List<SlotTimeMasterModel>>();

		String key = "";
		List<SlotTimeMasterModel> modelList = new ArrayList<SlotTimeMasterModel>();
		List<SlotTimeMasterModel> tempModelList = new ArrayList<SlotTimeMasterModel>();

		modelList = getSlotTimeMasterCommonTimeSlotBySemesterSubId(semesterSubId);
		if (!modelList.isEmpty())
		{
			for (SlotTimeMasterModel e : modelList)
			{
				key = e.getStmPkId().getPatternId() +"_"+ e.getStmPkId().getSlot();
				if (returnMapList.containsKey(key))
				{
					tempModelList = returnMapList.get(key);
					tempModelList.add(e);
					returnMapList.replace(key, tempModelList);
				}
				else
				{
					tempModelList = new ArrayList<SlotTimeMasterModel>();
					tempModelList.add(e);
					returnMapList.put(key, tempModelList);
				}
			}
		}

		return returnMapList;
	}

	public List<Object[]> getSlotTimeMasterWeekDayList(Integer patternId)
	{			
		return semesterMasterRepository.findSlotTimeMasterWeekDayList(patternId);	
	}

	public List<Object[]> getSlotTimeMasterWeekDaySessionList(Integer patternId)
	{			
		return semesterMasterRepository.findSlotTimeMasterWeekDaySessionList(patternId);	
	}

	public List<Object[]> getSlotTimeMasterByPatternId(Integer patternId)
	{
		return semesterMasterRepository.findSlotTimeMasterByPatternId(patternId);
	}


	//Basket Course Catalog Detail
	public List<String> getBasketCourseCatalogCourseCodeByBasketId(String basketId)
	{
		return semesterMasterRepository.findBasketCourseCatalogCourseCodeByBasketId(basketId);
	}


	//Employee Profile
	public List<Object[]> getEmployeeProfileByCampusCode(String campusCode)
	{
		return semesterMasterRepository.findEmployeeProfileByCampusCode(campusCode);
	}

	public List<EmployeeProfile> getEmployeeProfileByCentreId(Integer costCentreId)
	{
		return semesterMasterRepository.findEmployeeProfileByCentreId(costCentreId);
	}


	//Student Login Details
	public StudentsLoginDetailsModel getStudentLoginDetailByRegisterNumber(String registerNumber)
	{
		return semesterMasterRepository.findStudentDetailByRegisterNumber(registerNumber);
	}

	public List<Object[]> getStudentLoginDetailByRegisterNumber2(String registerNumber)
	{
		return semesterMasterRepository.findStudentDetailByRegisterNumber2(registerNumber);
	}

	public List<Object[]> getStudentLoginDetailByUserName(String userName)
	{
		return semesterMasterRepository.findStudentDetailByUserName(userName);
	}

	public List<Object[]> getStudentFeeCategoryByFeeIdAndSpecId(Integer feeId, Integer specId)
	{
		return semesterMasterRepository.findStudentFeeCategoryByFeeIdAndSpecId(feeId, specId);
	}


	//Student Credit Transfer Detail
	public String getStudentCreditTransferOldRegisterNumberByRegisterNumber(String registerNumber)
	{
		return semesterMasterRepository.findStudentCreditTransferOldRegisterNumberByRegisterNumber(registerNumber);
	}


	//Student Registration Method
	public String getRegistrationMethodByStudentSpecIdAndStartYear(Integer specId, Integer studyStartYear)
	{
		String tempRegistrationMethod = "";		
		Integer tempStudySystemYear = 0;

		tempStudySystemYear = semesterMasterRepository.findStudentCurriculumProgramStartYearBySpecializationId(specId);
		if ((tempStudySystemYear != null) && (tempStudySystemYear > 0) && (studyStartYear >= tempStudySystemYear))
		{
			tempRegistrationMethod = "CAL";
		}
		else
		{
			tempRegistrationMethod = "GEN";
		}

		return tempRegistrationMethod;
	}


	//User Detail Validation
	public void getUserLoginValidation(String userId, String userPassword, Integer testStatus,HttpSession session,String ipAddress)
	{


		if(testStatus == 2 || AppGlobalValues.ADMIN_IP_ADDRESS.contains(ipAddress))
		{
			List<Object[]> userDetails = regService.doGetUserDetailsInfoByUserId(userId.trim());

			if(userDetails!=null && !userDetails.isEmpty())
			{
				session.setAttribute("userId", userDetails.get(0)[0].toString());
							
			}
			else
			{
				throw new BadCredentialsException("Not Eligible for Registration");
			}
		}
		else
		{

			List<Object[]> userDetails = regService.doGetUserDetailsInfo(userId.trim());


			if(userDetails!=null && !userDetails.isEmpty())
			{
				CommonFunctions scf = new CommonFunctions();
				
				scf.verifyCredentials(userPassword, userDetails.get(0)[2].toString());

				//scf.String_Validate(userPassword, userDetails.get(0)[1].toString()); 

				session.setAttribute("userId", userDetails.get(0)[0].toString());
				
			}
			else
			{
				throw new BadCredentialsException("Not Eligible for Registration");
			}
		}



	}





	//Registration Schedule
	public List<Object[]> getCourseEligibleProgramByProgGroupId(Integer progGroupId)
	{
		return semesterMasterRepository.findCourseEligibleProgramByProgGroupId(progGroupId);
	}


	//Pattern Time Master
	public List<Object[]> getPatternTimeMasterSlotTypeByPatternId(List<Integer> patternId)
	{
		return semesterMasterRepository.findPatternTimeMasterSlotTypeByPatternId(patternId);
	}

	public List<Object[]> getPatternTimeMasterSlotDetailByPatternId(Integer patternId)
	{
		return semesterMasterRepository.findPatternTimeMasterSlotDetailByPatternId(patternId);
	}


	//Time Table Pattern Detail
	public List<Object[]> getTTPatternDetailSessionSlotByPatternId(Integer patternId)
	{	
		List<Object[]> returnObjectList = new ArrayList<Object[]>();

		int orderNo = 0;
		for (Object[] e : semesterMasterRepository.findTTPatternDetailSessionSlotByPatternId(patternId))
		{
			switch(e[0].toString())
			{
			case "FN": orderNo = 1; break;
			case "LN": orderNo = 2; break;
			case "AN": orderNo = 3; break;
			case "EN": orderNo = 4; break;
			default: orderNo = 5; break;
			}

			returnObjectList.add(new Object[] {orderNo, e[0].toString(), Integer.parseInt(e[1].toString()), 
					Integer.parseInt(e[2].toString()), Integer.parseInt(e[3].toString())});
		}
		returnObjectList.add(new Object[] {2, "LN", 0, 0, 0});

		if (!returnObjectList.isEmpty())
		{
			Collections.sort(returnObjectList, new Comparator<Object[]>() 
			{
				@Override
				public int compare(Object[] o1, Object[] o2) 
				{
					try
					{
						return new CompareToBuilder()
								.append(o1[0], o2[0])
								.toComparison();
					}
					catch (Exception e)
					{
						return 0;
					}
				}
			});
		}

		return returnObjectList;	
	}


	public List<Object[]> getTTPatternDetailMaxSlots(Integer patternId)
	{
		return semesterMasterRepository.findTTPatternDetailMaxSlots(patternId);
	}


	//Course Option
	public List<Object[]> getRegistrationCourseOption(String courseOption, String genericCourseType, int regularStatus, 
			int auditStatus, int honourStatus, int minorStatus, int additionalStatus, 
			int substitutionStatus, int RPEUEStatus, int RUCUEStatus, int RDEOEStatus, int RSEOEStatus)
	{
		List<Object[]> returnObjectList = new ArrayList<Object[]>();

		List<String> optionList = new ArrayList<String>();
		List<String> optionList2 = new ArrayList<String>();
		List<String> optionList3 = new ArrayList<String>();
		List<Object> temp = new ArrayList<>();

		
		if ((courseOption != null) && (!courseOption.equals("")))
		{
			if (courseOption.equals("RGR") && (regularStatus == 1))
			{
				optionList.add(courseOption);
			}
			else if (!courseOption.equals("RGR"))
			{
				optionList.add(courseOption);
			}
		}
		if (genericCourseType.equals("TH") || genericCourseType.equals("LO") 
				|| genericCourseType.equals("ETL") || genericCourseType.equals("ETP") 
				|| genericCourseType.equals("ELP") || genericCourseType.equals("ETLP"))
		{
			if (auditStatus == 1)
			{
				optionList.add("AUD"); 
			}

			if (honourStatus == 1)
			{
				optionList.add("HON"); 
			}

			if (minorStatus == 1)
			{
				optionList.add("MIN"); 
			}

			if (additionalStatus == 1)
			{
				optionList.add("RGA"); 
			}

			if (substitutionStatus == 1)
			{
				optionList.add("CS"); 
			}

			if (RPEUEStatus == 1)
			{
				optionList.add("RPEUE"); 
			}

			if(RDEOEStatus == 1)
			{
				optionList.add("RDEOE");
			}

			if(RSEOEStatus == 1)
			{
				optionList.add("RSEOE");
			}

			if (RUCUEStatus == 1)
			{
				optionList.add("RUCUE"); 
			}
		}
		if (!optionList.isEmpty())
		{
			optionList2 = Arrays.asList("GI", "GICE", "GIVC", "RR", "RRCE", "RRN", "RRVC");
			optionList3 = Arrays.asList("RGR", "AUD", "DM", "RGCE", "RGVC", "RPEUE", "RUCUE","RDEOE","RSEOE");

			for (CourseOptionModel e : semesterMasterRepository.findByOptionCode(optionList))
			{
				temp.clear();

				temp.add(e.getCode());
				temp.add(e.getDescription());

				if (optionList2.contains(e.getCode()))
				{
					temp.add(2);
				}
				else
				{
					temp.add(1);
				}

				if (optionList3.contains(e.getCode()))
				{
					temp.add(1);
				}
				else
				{
					temp.add(2);
				}

				returnObjectList.add(temp.toArray());
			}
		}

		return returnObjectList;
	}

	public float getGradePoint(String grade, Float credits)
	{
		float gradePoint = 0;

		switch (grade)
		{
		case "S":
			gradePoint= 10*credits;
			break;
		case "A":
			gradePoint= 9*credits;
			break;
		case "B":
			gradePoint= 8*credits;
			break;
		case "C":
			gradePoint= 7*credits;
			break;
		case "D":
			gradePoint= 6*credits;
			break;
		case "E":
			gradePoint= 5*credits;
			break;
		default:
			gradePoint= 0;
			break;
		}

		return gradePoint;
	}

	public List<Object[]> getStudentDetailOthersByRegisterNumberAndSemesterSubId(String registerNumber, String semesterSubId)
	{
		return semesterMasterRepository.findStudentDetailOthersByRegisterNumberAndSemesterSubId(registerNumber, semesterSubId);
	}

	//Generate Admin Password
	public String generateAdminPassword()
	{
		String returnPassword = "", password = "NONE", hashPassword = "";
		CommonFunctions scf = new CommonFunctions();

		password = scf.getrandom4(10);
		hashPassword = scf.Encrypt_String3(password, AppGlobalValues.KEY_LENGTH);

		returnPassword = "Password: "+ password +" | Hash Password: "+ hashPassword;

		return returnPassword;
	}

	//Validate Admin Password
	public int validateAdminPassword(String password, String adminPassword)
	{
		int regValidFlag = 2;

		if ((password != null) && (!password.equals("")) && (!password.equals("NONE")) 
				&& (adminPassword != null) && (!adminPassword.equals("")) && (!adminPassword.equals("NONE")))
		{	
			CommonFunctions scf3 = new CommonFunctions();

			regValidFlag = scf3.String_Validate3(password, adminPassword, AppGlobalValues.KEY_LENGTH);
		}

		return regValidFlag;
	}


}
