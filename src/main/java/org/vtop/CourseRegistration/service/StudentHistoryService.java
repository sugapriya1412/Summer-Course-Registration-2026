package org.vtop.CourseRegistration.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.model.StudentHistoryModel;
import org.vtop.CourseRegistration.repository.CourseRegistrationRepository;
import org.vtop.CourseRegistration.repository.StudentHistoryRepository;


@Service
@Transactional(readOnly=true)
public class StudentHistoryService
{	
	@Autowired private StudentHistoryRepository studentHistoryRepository;
	@Autowired private CourseRegistrationRepository courseRegistrationRepository;
	
	private static final Logger LOGGER = LogManager.getLogger(StudentHistoryService.class);
	
		
	
	public List<StudentHistoryModel> getStudentHistoryPARequisite(String registerNumber, String[] courseId)
	{
		return studentHistoryRepository.findStudentHistoryPARequisite(registerNumber, courseId);
	}
	
	public List<StudentHistoryModel> getStudentHistoryPARequisite2(String registerNumber, List<String> courseId)
	{
		return studentHistoryRepository.findStudentHistoryPARequisite2(registerNumber, courseId);
	}
	
	public List<Object[]> getStudentHistoryGrade2(String registerNumber, String courseCode)
	{
		return studentHistoryRepository.findStudentHistoryGrade3(registerNumber, courseCode);
	}
	
	public List<Object[]> getStudentHistoryCS2(String registerNumber, String courseCode, String studySystem, 
								Integer specializationId, Integer studentYear, Float curriculumVersion, 
								String semesterSubId, String courseCategory, String courseOption, String basketId, 
								int allowFlag)
	{
		List<Object[]> courseSubList = new ArrayList<Object[]>();
		List<String> regCourseList = new ArrayList<String>();
		List<String> tempCourseList = new ArrayList<String>();
		
		
		if ((allowFlag == 1) && (courseOption.equals("RGR") || courseOption.equals("RGCE") 
				|| courseOption.equals("RGP") || courseOption.equals("RGW") 
				|| courseOption.equals("RPCE") || courseOption.equals("RWCE") 
				|| courseOption.equals("RGVC")))
		{
			regCourseList.add(courseCode);
			
			tempCourseList = courseRegistrationRepository.findCourseFromRegistrationAndStudentHistoryBySemesterAndRegisterNoforCS(
								semesterSubId, registerNumber);
			if (!tempCourseList.isEmpty())
			{
				regCourseList.addAll(tempCourseList.stream().distinct().collect(Collectors.toList()));
			}			
			
			if (studySystem.equals("CAL"))
			{	
				if (courseCategory.equals("UC"))
				{
					courseSubList = studentHistoryRepository.findCSCourseByCourseCategoryAndBasketId(registerNumber, 
										regCourseList, specializationId, studentYear, curriculumVersion, courseCategory, 
										basketId);
				}
				else if (courseCategory.equals("PE"))
				{
					courseSubList = studentHistoryRepository.findStudentHistoryCS3(registerNumber, regCourseList, 
										specializationId, studentYear, curriculumVersion);
				}
				else if (courseCategory.equals("UE"))
				{
					courseSubList = studentHistoryRepository.findStudentHistoryCS4(registerNumber, regCourseList, 
										specializationId, studentYear, curriculumVersion);
				}
			}
			else if (studySystem.equals("FFCS") || studySystem.equals("NONFFCS"))
			{
				courseSubList = studentHistoryRepository.findStudentHistoryCS2(registerNumber, regCourseList);
			}
			else if(studySystem.equals("CBCS") && (courseCategory.equals("FC") || courseCategory.equals("FCHSSM")))
			{
				courseSubList = studentHistoryRepository.doGetBasketCourseForCSByRegNoAndSpecIdAndAdmYrAndBsktIdAndCrsCodeAndCrsCatgAndCurVersion
						(registerNumber, specializationId, studentYear, basketId, courseCode, courseCategory, curriculumVersion);
			}
			else if(studySystem.equals("CBCS") && (courseCategory.equals("DE") || courseCategory.equals("OE") || courseCategory.equals("SPE")))
			{
				courseSubList = studentHistoryRepository.findStudentHistoryCSDEAndOE(registerNumber, regCourseList, 
						specializationId, studentYear, curriculumVersion,courseCategory);
			}
			
			
			//Equvalance Check pointed to Academic  academics.course_equivalance_reg
			List<String> csFiletrCourseList = studentHistoryRepository.doGetCSCourseFilter(registerNumber);
			
			courseSubList = courseSubList.stream().filter(s -> !csFiletrCourseList.contains(s[1].toString())).collect(Collectors.toList());
		}
		LOGGER.trace("\n courseSubList: "+ courseSubList);
		
		return courseSubList;
	}
	
		
	public Integer getStudentHistoryFailCourseCredits2(String registerNumber)
	{
		Integer tempFailCredit = 0;
		
		tempFailCredit = studentHistoryRepository.findStudentHistoryFailCourseCredits2(registerNumber);
		if (tempFailCredit == null)
		{
			tempFailCredit = 0;
		}
		
		return tempFailCredit;
	}
		
	public List<Object[]> getStudentHistoryCEGrade3(String registerNumber, String courseCode)
	{
		return studentHistoryRepository.findStudentHistoryCEGrade4(registerNumber, courseCode);
	}
	
	public List<Object[]> getStudentHistoryGIAndFailCourse(String registerNumber)
	{
		return studentHistoryRepository.findStudentHistoryGIAndFailCourse(registerNumber);
	}
	
	public List<String> getStudentHistoryFailComponentCourseType(String registerNumber, String courseId, 
							String examMonth)
	{
		return studentHistoryRepository.findStudentHistoryFailComponentCourseType(registerNumber, courseId, examMonth);
	}
		
	public List<Object[]> getStudentHistoryNotAllowedGrade(String registerNumber, String courseId, String examMonth)
	{
		return studentHistoryRepository.findStudentHistoryNotAllowedGrade(registerNumber, courseId, examMonth);
	}
	
	public List<Object[]> getArrearRegistrationWithCEByRegisterNumberAndCourseCode(String semesterSubId, String registerNumber, String courseCode)
	{
		return studentHistoryRepository.findArrearRegistrationWithCEByRegisterNumberAndCourseCode(semesterSubId, registerNumber, courseCode);
	}
	
		
	public List<Object[]> getCourseChangeHistoryByRegisterNumberAndCourseCode2(String registerNumber, String courseCode)
	{
		return studentHistoryRepository.findCourseChangeHistoryByRegisterNumberAndCourseCode3(registerNumber, courseCode);
	}
	
	
	//Research Program
	public Integer getRPApprovalStatusByRegisterNumber(String registerNumber)
	{
		Integer tempStatus = 2;
		
		tempStatus = studentHistoryRepository.findRPApprovalStatusByRegisterNumber(registerNumber);
		if (tempStatus == null)
		{
			tempStatus = 2;
		}
		
		return tempStatus;
	}
	
	public List<String> getRPCourseWorkByRegisterNumber(String registerNumber)
	{
		List<String> tempCourseList = new ArrayList<String>();
		 
		if (getRPApprovalStatusByRegisterNumber(registerNumber) == 1)
		{
			tempCourseList = studentHistoryRepository.findRPCourseWorkByRegisterNumber(registerNumber);
		}
		
		if (tempCourseList.isEmpty())
		{
			tempCourseList.add("NONE");
		}
		
		return tempCourseList;
	}
	
	public List<String> getRPCourseWorkCourseIdByRegisterNumber(String registerNumber)
	{
		List<String> tempCourseList = new ArrayList<String>();
		 
		if (getRPApprovalStatusByRegisterNumber(registerNumber) == 1)
		{
			tempCourseList = studentHistoryRepository.findRPCourseWorkCourseIdByRegisterNumber(registerNumber);
		}
		
		if (tempCourseList.isEmpty())
		{
			tempCourseList.add("NONE");
		}
		
		return tempCourseList;
	}
	
	public List<String> getCSCourseCodeByRegisterNoAndCourseId(String semesterSubId, List<String> registerNumber, 
								List<String> courseId)
	{
		return studentHistoryRepository.findCSCourseCodeByRegisterNoAndCourseId(semesterSubId, registerNumber, 
					courseId);
	}
	
	public List<Object[]> getByRegisterNumberCourseOptionAndGrade(String registerNumber, List<String> courseOptionCode, 
								List<String> grade)
	{
		return studentHistoryRepository.findByRegisterNumberCourseOptionAndGrade(registerNumber, courseOptionCode, grade);
	}
	
	
	
	//***************************************
	//Examinations Result & Graduation Check
	//***************************************
	
	public List<Object[]> getResultPublishedCourseDataBySemAndRegNo(String semesterSubId, List<String> regNoList)
	{
		return studentHistoryRepository.findResultPublishedCourseDataBySemAndRegNo(semesterSubId, regNoList);
	}
	
	public List<Object[]> getResultPublishedCourseDataBySemRegNoAndCourseCode(String semesterSubId, List<String> regNoList, 
								String courseCode)
	{
		return studentHistoryRepository.findResultPublishedCourseDataBySemRegNoAndCourseCode(semesterSubId, regNoList, 
					courseCode);
	}
	
	/*public List<Object[]> getResultPublishedCourseDataForRARBySemRegNoAndCourseCode(String semesterSubId, List<String> regNoList, 
									String courseCode)
	{
		return studentHistoryRepository.findResultPublishedCourseDataForRARBySemRegNoAndCourseCode(semesterSubId, regNoList, 
					courseCode);
	}*/
	
	public List<Object[]> getStaticStudentCGPAFromTable(String registerNumber, Integer specializationId)
	{
		return studentHistoryRepository.findStaticStudentCGPAFromTable(registerNumber, specializationId);
	}
	
	public Integer getGraduationValue(List<String> registerNumber)
	{
		return studentHistoryRepository.findGraduationValue(registerNumber);
	}
	
	public List<Object[]> getStudentHistoryForCgpaCalc(String regNo, Short pgmSpecId)
	{
		return studentHistoryRepository.findStudentHistoryForCgpaCalc(regNo, pgmSpecId);
	}
	
	public List<Object[]> getStudentHistoryForGpaCalc(String regNo, Short pgmSpecId, Date examMonth)
	{
		return studentHistoryRepository.findStudentHistoryForGpaCalc(regNo, pgmSpecId, examMonth);
	}
	
	public List<Object[]> getStudentHistoryForCgpaCalc(String regNo, Short pgmSpecId, Date examMonth)
	{
		return studentHistoryRepository.findStudentHistoryForCgpaCalc(regNo, pgmSpecId, examMonth);
	}
	
	public List<Object[]> getStudentHistoryForCgpaNonCalCalc(String regNo, Short pgmSpecId)
	{
		return studentHistoryRepository.findStudentHistoryForCgpaNonCalCalc(regNo, pgmSpecId);
	}
	
	public List<Object[]> getStudentHistoryForGpaNonCalCalc(String regNo, Short pgmSpecId, Date examMonth)
	{
		return studentHistoryRepository.findStudentHistoryForGpaNonCalCalc(regNo, pgmSpecId, examMonth);
	}
	
	public List<Object[]> getStudentHistoryForCgpaNonCalCalc(String regNo, Short pgmSpecId, Date examMonth)
	{
		return studentHistoryRepository.findStudentHistoryForCgpaNonCalCalc(regNo, pgmSpecId, examMonth);
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
	
	public List<Object[]> getStaticStudentCGPAFromTable(String regNoList)
	{
		return studentHistoryRepository.getStaticStudentCGPAFromTable(regNoList);
	}
	
	public 	List<String> doGetFaildCourseByRegNoAndCourseId(String regNo,String courseId)
	{
		return studentHistoryRepository.doGetFaildCourseByRegNoAndCourseId(regNo, courseId);
	}
	
	public 	List<String> doGetPassedCourseByRegNoAndCourseId(String regNo,String courseId)
	{
		return studentHistoryRepository.doGetPassedCourseByRegNoAndCourseId(regNo, courseId);
	}

}
