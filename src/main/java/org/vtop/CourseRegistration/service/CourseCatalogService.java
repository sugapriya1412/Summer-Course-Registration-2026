package org.vtop.CourseRegistration.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.Dto.ProgramSpecializationCurriculumDetailDto;
import org.vtop.CourseRegistration.Dto.ReseachStudentCourseDetailDto;
import org.vtop.CourseRegistration.model.CourseCatalogModel;
import org.vtop.CourseRegistration.model.CourseEquivalancesModel;
import org.vtop.CourseRegistration.repository.CourseAllocationRepository;
import org.vtop.CourseRegistration.repository.CourseCatalogRepository;
import org.vtop.CourseRegistration.repository.CourseEquivalncesRepository;
import org.vtop.CourseRegistration.repository.ProgrammeSpecializationCurriculumDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@Transactional(readOnly=true)
public class CourseCatalogService
{
	@Autowired private CourseCatalogRepository courseCatalogRepository;
	@Autowired private ProgrammeSpecializationCurriculumDetailRepository programmeSpecializationCurriculumDetailRepository;
	@Autowired private CourseAllocationService courseAllocationService;
	@Autowired private StudentHistoryService studentHistoryService;
	@Autowired private CourseAllocationRepository courseAllocationRepository;
	@Autowired private CourseEquivalncesRepository courseEquivalncesRepository;
	@Autowired private ProgrammeSpecializationCurriculumDetailService spcService;
	
	private static final Logger LOGGER = LogManager.getLogger(CourseCatalogService.class);
		
	public CourseCatalogModel getOne(String courseId)
	{
		return courseCatalogRepository.findById(courseId).orElse(null);
	}
	
	public CourseCatalogModel getOfferedCourseDetailByCourseCode(String semesterSubId, String[] classGroupId, 
									String[] classType, String courseCode)
	{
		return courseCatalogRepository.findOfferedCourseDetailByCourseCode(semesterSubId, classGroupId, 
					classType, courseCode);
	}	
			
	//To get Course Owner's List
	public List<Object[]> getCourseCostCentre (String campus)
	{
		return courseCatalogRepository.findCourseCostCentre(campus);
	}
	
	
	//Compulsory Course Pagination
	public Page<CourseCatalogModel> getCompulsoryCoursePagination(String campusCode, String[] courseSystem, 
										List<Integer> egbGroupId, String groupCode, String semesterSubId, 
										String[] classGroupId, String[] classType, List<String> courseCode, 
										String progGroupCode, String progSpecCode, String costCentreCode, 
										Pageable pageable)
	{
		if (progGroupCode.equals("RP"))
		{
			return courseCatalogRepository.findCompulsoryCourseAsPage(campusCode, courseSystem, egbGroupId, 
						groupCode, semesterSubId, classGroupId, classType, courseCode, pageable);
		}
		else
		{
			return courseCatalogRepository.findCompulsoryCourseByClassOptionAsPage(campusCode, courseSystem, 
						egbGroupId, groupCode, semesterSubId, classGroupId, classType, courseCode, progGroupCode, 
						progSpecCode, costCentreCode, pageable);
		}
	}
	
		
	public String getTotalPageAndIndex(int dataSize, int pageSize, int pageNumber)
	{
		int totalPage = 0, fromIndex = 0, toIndex = 0;
		double calcTotalPage = 0;
		
		if (pageSize > 0)
		{
			calcTotalPage = (double)dataSize / (double)pageSize;
			totalPage = (int) Math.ceil(calcTotalPage);
		}
		
		if (pageNumber <= 0)
		{
			pageNumber = 0;
		}
		else if (pageNumber >= totalPage)
		{
			pageNumber = totalPage - 1;
		}
				
		if (totalPage > 0)
		{
			fromIndex = pageNumber * pageSize;
			toIndex = fromIndex + pageSize;
			if (toIndex > dataSize)
			{
				toIndex = dataSize;
			}
		}
						
		return totalPage +"|"+ fromIndex +"|"+ toIndex;
	}

	public List<String> getCourseTypesByCourseId(String courseId) {
		return courseCatalogRepository.getCourseTypesByCourseId(courseId);
	}
	
	
	public List<CourseCatalogModel> getCourseListForRegistration(String registrationOption, String campusCode, 
										String[] courseSystem, List<Integer> egbGroupId, Integer programGroupId, 
										String semesterSubId, Integer programSpecId, String[] classGroupId, 
										String[] classType, Integer admissionYear, Float curriculumVersion, 
										String registerNumber, int searchType, String searchValue, 
										Integer studentGraduateYear, String programGroupCode, 
										String programSpecCode, String registrationMethod,  
										int PEUEAllowStatus, int evalPage, int evalPageSize, String costCentreCode, 
										List<String> compulsoryCourseCode, List<String> reRegisterCourseCode, HttpSession session)
	{
		List<CourseCatalogModel> returnModelList = new ArrayList<>();
		
		try
		{		
			int dataListFlag = 2;
			String programGroup = "", programGroupStart = "", programGroupMid = "", programGroupEnd = "";
			
			List<String> courseCodeList = new ArrayList<>();
			List<String> notCourseCodeList = new ArrayList<>();
			List<String> courseIdList = new ArrayList<>();
			
			List<ProgramSpecializationCurriculumDetailDto> prgSplCurriculumDetailList = new ArrayList<>();
			List<ReseachStudentCourseDetailDto> researchStudentCourseDetailList = new ArrayList<>();
			List<CourseEquivalancesModel> courseEquivalanceList = new ArrayList<>();

			Map<String,List<Object[]>> concentrationCourse = null;

			if ((registrationOption != null) && (!registrationOption.equals("")))
			{
				if ((registrationOption.equals("PE") || registrationOption.equals("UE") 
						|| registrationOption.equals("BC") || registrationOption.equals("NC")) 
						&& (PEUEAllowStatus == 1))
				{
					dataListFlag = 1;
				}
				else
				{
					dataListFlag = 1;
				}
			}
			
			if ((searchType == 2) || (searchType == 3))
			{
				if ((searchValue == null) || (searchValue.equals("")))
				{
					searchValue = "NONE";
				}
				else
				{
					searchValue = searchValue.toUpperCase();
				}
			}
			
			if (programGroupId == null)
			{
				programGroup = "NONE";
			}
			else
			{
				programGroup = programGroupId.toString();
			}		
			
					
			
			if (dataListFlag == 1)
			{
				programGroupStart = ".*^"+ programGroup +"/.*";
				programGroupMid = ".*/"+ programGroup +"/.*";
				programGroupEnd = ".*/"+ programGroup +"$.*";
						
				switch(registrationOption)
				{	
					case "COMP":
						List<CourseCatalogModel> tempReturnModelList = new ArrayList<>();
						
						if (!compulsoryCourseCode.isEmpty())
						{
							if (programGroupCode.equals("RP"))
							{
								tempReturnModelList = courseAllocationRepository.findCourseForResearchProg
										(semesterSubId, Arrays.asList(classGroupId), egbGroupId, 
												Arrays.asList(classType),"%"+programGroup+"%",compulsoryCourseCode);
							}
							else
							{
								
								tempReturnModelList = courseAllocationRepository.findCourseForNonResearchProg
										(semesterSubId, Arrays.asList(classGroupId), egbGroupId, 
												Arrays.asList(classType),"%"+programGroup+"%",compulsoryCourseCode,programGroupCode,programSpecCode,costCentreCode);
								
							}
							
														
							
						    for (String compCourseCode : compulsoryCourseCode) {
								for (CourseCatalogModel courseCatalog : tempReturnModelList) {
									if(compCourseCode.equals(courseCatalog.getCode()))
									{
										returnModelList.add(courseCatalog);
										break;
									}
								}
							}
						}
						break;
						
					case "UE":
						prgSplCurriculumDetailList = programmeSpecializationCurriculumDetailRepository.findCourseCodeBySpecIdAndAdmissionYear(
														programSpecId, admissionYear);
						if (!prgSplCurriculumDetailList.isEmpty())
						{
							courseCodeList = prgSplCurriculumDetailList.stream().map(e-> e.getCourseCode()).distinct().collect(Collectors.toList());
						}
						else
						{
							courseCodeList.add("NONE");
						}
						LOGGER.trace("\n UE - courseCodeList: "+ courseCodeList.toString());		
						
						returnModelList = courseAllocationRepository.findCourseUECourseList(semesterSubId, Arrays.asList(classGroupId), 
								 Arrays.asList(classType),  programGroupCode, programSpecCode, costCentreCode, Arrays.asList(courseSystem), 
								 egbGroupId, "%"+programGroup+"%", Arrays.asList("ECA","PJT","OC"), Arrays.asList("IIP","LSM","TARP"),courseCodeList);
						
						break;
						
					case "RGR":						
						if (programGroupCode.equals("RP"))
						{
							
							List<String> researchCourseIdList = studentHistoryService.getRPCourseWorkCourseIdByRegisterNumber(registerNumber);
							
							courseIdList = courseAllocationRepository.findCourseIdBySemesterSubIdClassGroupAndClassType(semesterSubId, 
														Arrays.asList(classGroupId), Arrays.asList(classType));
							
							
							if (!courseIdList.isEmpty())
							{
								courseIdList = courseIdList.stream().filter(e->researchCourseIdList.contains(e)).map(e-> e).distinct().collect(Collectors.toList());
							}
							else
							{
								courseIdList.add("NONE");
							}
						}
						else
						{
						
							courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(semesterSubId, 
									Arrays.asList(classGroupId), Arrays.asList(classType), programGroupCode, programSpecCode, costCentreCode);
							
							if(courseIdList.isEmpty())
							{
								courseIdList.add("NONE");
							}
						}
						
						LOGGER.trace("\n RGR courseIdList: "+ courseIdList);
						
						if (programGroupCode.equals("RP"))
						{
							notCourseCodeList = (notCourseCodeList == null || notCourseCodeList.isEmpty())
									? new ArrayList<>(Collections.singletonList("NONE"))
									: notCourseCodeList;

							researchStudentCourseDetailList = findResearchCourseDetails(registerNumber);
							if (!researchStudentCourseDetailList.isEmpty())
							{
								courseCodeList = researchStudentCourseDetailList.stream().map(e-> e.getCourseCode()).distinct().collect(Collectors.toList());
							}
							else
							{
								courseCodeList.add("NONE");
							}
							LOGGER.trace("\n RGR - Research courseCodeList: "+ courseCodeList);
							
							returnModelList = courseAllocationRepository.findCourseRGRWithRPProg(egbGroupId, "%"+programGroup+"%", 
									courseCodeList, notCourseCodeList,
									Arrays.asList("ECA","OC"), courseIdList);
							
							
						}
						else
						{
							
							returnModelList = courseAllocationRepository.findCourseRGRWithRPProg(egbGroupId, "%"+programGroup+"%",
									notCourseCodeList,Arrays.asList(courseSystem),Arrays.asList("ECA","OC"), courseIdList);
							
						}
						
						if (!returnModelList.isEmpty())
						{
							returnModelList = returnModelList.parallelStream().filter(e-> (!e.getEvaluationType().equals("SETCONFERENCE"))).collect(Collectors.toList());
						}
						break;
						
					case "RR":					
						if (!reRegisterCourseCode.isEmpty())
						{
							courseCodeList.addAll(reRegisterCourseCode);
						}
						LOGGER.trace("\n RR - courseCodeList (Level - 1): "+ courseCodeList.toString());
						
						courseEquivalanceList = courseEquivalncesRepository.findByCourseCodeAndEquivalentCourseCode(courseCodeList);
						if (!courseEquivalanceList.isEmpty())
						{
							courseCodeList.addAll(courseEquivalanceList.stream().map(e-> ((e.getCourseCode() == null) 
									|| (e.getCourseCode().isEmpty())) ? "NONE" : e.getCourseCode()).distinct().collect(Collectors.toList()));
							courseCodeList.addAll(courseEquivalanceList.stream().map(e-> ((e.getEquivalentCourseCode() == null) 
									|| (e.getEquivalentCourseCode().isEmpty())) ? "NONE" : e.getEquivalentCourseCode())
									.distinct().collect(Collectors.toList()));
						}
						LOGGER.trace("\n RR - courseCodeList (Level - 2): "+ courseCodeList.toString());
						
						if (!courseCodeList.isEmpty())
						{
							courseCodeList = courseCodeList.stream().distinct().collect(Collectors.toList());
						}
						LOGGER.trace("\n RR - courseCodeList (Level - 3): "+ courseCodeList.toString());
						
						if (programGroupCode.equals("RP"))
						{
							courseIdList = courseAllocationRepository.findCourseIdBySemesterSubIdClassGroupAndClassType(semesterSubId, 
														Arrays.asList(classGroupId), Arrays.asList(classType));
							if (!courseIdList.isEmpty())
							{
								courseIdList = courseIdList.stream().map(e-> e).distinct().collect(Collectors.toList());
							}
							else
							{
								courseIdList.add("NONE");
							}
						}
						else
						{
							courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(semesterSubId, 
									Arrays.asList(classGroupId), Arrays.asList(classType), programGroupCode, programSpecCode, costCentreCode);
							
							if(courseIdList.isEmpty())
							{
								courseIdList.add("NONE");
							}
						}
						
						
						LOGGER.trace("\n RR - courseIdList: "+ courseIdList);
						LOGGER.trace("grp{}",egbGroupId,"pgrp{}", programGroup, "crscode{}",courseCodeList);
						
						returnModelList = courseAllocationRepository.findCourseRRCourseList(egbGroupId, "%"+programGroup+"%", courseCodeList, 
								 Arrays.asList("ECA","OC"), courseIdList);
						
						
						if (!returnModelList.isEmpty())
						{
							returnModelList = returnModelList.parallelStream().filter(e-> (!e.getEvaluationType().equals("SETCONFERENCE")))
														.collect(Collectors.toList());
						}
						break;
										
					case "FFCSCAL":						
						courseEquivalanceList = courseEquivalncesRepository.findAll();
						if (!courseEquivalanceList.isEmpty())
						{
							courseCodeList.addAll(courseEquivalanceList.stream().map(e-> ((e.getCourseCode() == null) 
									|| (e.getCourseCode().isEmpty())) ? "NONE" : e.getCourseCode())
									.distinct().collect(Collectors.toList()));
							courseCodeList.addAll(courseEquivalanceList.stream().map(e-> ((e.getEquivalentCourseCode() == null) 
									|| (e.getEquivalentCourseCode().isEmpty())) ? "NONE" : e.getEquivalentCourseCode())
									.distinct().collect(Collectors.toList()));
						}
						LOGGER.trace("\n RR - courseCodeList (Leve - 1): "+ courseCodeList.toString());
						
						if (!courseCodeList.isEmpty())
						{
							courseCodeList = courseCodeList.stream().distinct().collect(Collectors.toList());
						}
						LOGGER.trace("\n RR - courseCodeList (Leve - 2): "+ courseCodeList.toString());
						
						courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(semesterSubId, 
								Arrays.asList(classGroupId), Arrays.asList(classType), programGroupCode, programSpecCode, costCentreCode);
						
						if(courseIdList.isEmpty())
						{
							courseIdList.add("NONE");
						}
						
						LOGGER.trace("\n RR - courseIdList: "+ courseIdList);
						
						
						returnModelList = courseAllocationRepository.findCourseFFCSCALCourseList(egbGroupId,  "%"+programGroup+"%",
								courseCodeList, Arrays.asList("CAL"), Arrays.asList("ECA","PJT","OC"), courseIdList);
						
						break;
					case "FFCSCBCS":						
						courseEquivalanceList = courseEquivalncesRepository.findAll();
						if (!courseEquivalanceList.isEmpty())
						{
							courseCodeList.addAll(courseEquivalanceList.stream().map(e-> ((e.getCourseCode() == null) 
									|| (e.getCourseCode().isEmpty())) ? "NONE" : e.getCourseCode())
									.distinct().collect(Collectors.toList()));
							courseCodeList.addAll(courseEquivalanceList.stream().map(e-> ((e.getEquivalentCourseCode() == null) 
									|| (e.getEquivalentCourseCode().isEmpty())) ? "NONE" : e.getEquivalentCourseCode())
									.distinct().collect(Collectors.toList()));
						}
						LOGGER.trace("\n RR - courseCodeList (Leve - 1): "+ courseCodeList.toString());
						
						if (!courseCodeList.isEmpty())
						{
							courseCodeList = courseCodeList.stream().distinct().collect(Collectors.toList());
						}
						LOGGER.trace("\n RR - courseCodeList (Leve - 2): "+ courseCodeList.toString());
						
						courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(semesterSubId, 
								Arrays.asList(classGroupId), Arrays.asList(classType), programGroupCode, programSpecCode, costCentreCode);
						
						if(courseIdList.isEmpty())
						{
							courseIdList.add("NONE");
						}
						
						LOGGER.trace("\n RR - courseIdList: "+ courseIdList);
						
						
						returnModelList = courseAllocationRepository.findCourseFFCSCALCourseList(egbGroupId,  "%"+programGroup+"%",
								courseCodeList, Arrays.asList("CBCS"), Arrays.asList("ECA","PJT","OC"), courseIdList);
						
						break;
					case "CALCBCS":						
						courseEquivalanceList = courseEquivalncesRepository.findAll();
						if (!courseEquivalanceList.isEmpty())
						{
							courseCodeList.addAll(courseEquivalanceList.stream().map(e-> ((e.getCourseCode() == null) 
									|| (e.getCourseCode().isEmpty())) ? "NONE" : e.getCourseCode())
									.distinct().collect(Collectors.toList()));
							courseCodeList.addAll(courseEquivalanceList.stream().map(e-> ((e.getEquivalentCourseCode() == null) 
									|| (e.getEquivalentCourseCode().isEmpty())) ? "NONE" : e.getEquivalentCourseCode())
									.distinct().collect(Collectors.toList()));
						}
						LOGGER.trace("\n RR - courseCodeList (Leve - 1): "+ courseCodeList.toString());
						
						if (!courseCodeList.isEmpty())
						{
							courseCodeList = courseCodeList.stream().distinct().collect(Collectors.toList());
						}
						LOGGER.trace("\n RR - courseCodeList (Leve - 2): "+ courseCodeList.toString());
						
						courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(semesterSubId, 
								Arrays.asList(classGroupId), Arrays.asList(classType), programGroupCode, programSpecCode, costCentreCode);
						
						if(courseIdList.isEmpty())
						{
							courseIdList.add("NONE");
						}
						
						LOGGER.trace("\n RR - courseIdList: "+ courseIdList);
						
						
						returnModelList = courseAllocationRepository.findCourseFFCSCALCourseList(egbGroupId,  "%"+programGroup+"%",
								courseCodeList, Arrays.asList("CBCS"), Arrays.asList("ECA","PJT","OC"), courseIdList);
						
						break;
						
						
						
					case "ME":		
						
 						String studySystem = (String) session.getAttribute("studentStudySystem");
						List<String> courseList = courseAllocationRepository.findCourseMultiDespCourse
						(semesterSubId,classGroupId,programSpecId.toString(),studySystem);
						
						returnModelList = courseAllocationRepository.findCourseMultiDespCourseList(courseList);

						break;

					case "CON":

					/*	concentrationCourse = spcService.getConcentrationList(admissionYear,semesterSubId,Arrays.asList(classGroupId)
								,programGroupCode, programSpecCode, costCentreCode);*/

						courseIdList = programmeSpecializationCurriculumDetailRepository.doGetConcentrationBasketCourses(admissionYear,semesterSubId,Arrays.asList(classGroupId)
								,programGroupCode, programSpecCode, costCentreCode,programSpecId);

						returnModelList = courseAllocationRepository.findCourseListByCourseIds(courseIdList);


						break;

					case "OEC":

						courseCodeList = courseAllocationRepository.doGetListCourseIdOfOECBySemesterSubIdAndClassGrpId(admissionYear,semesterSubId,Arrays.asList(classGroupId),programSpecId,programGroupId);

						List<Object[]> curriculuimDetails =  programmeSpecializationCurriculumDetailRepository.doGetAllOECoursesACE(admissionYear,programGroupId,"OEC");

						for(Object[] obj : curriculuimDetails)
						{
							courseCodeList.add(obj[8].toString());
						}

						courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(semesterSubId,
								Arrays.asList(classGroupId), Arrays.asList(classType), programGroupCode, programSpecCode, costCentreCode);


						returnModelList = courseAllocationRepository.findCourseFFCSCALCourseList(egbGroupId,  "%"+programGroup+"%",
								courseCodeList, Arrays.asList(courseSystem), Arrays.asList("ECA","OC"), courseIdList);


						break;
						
						
					default:
						if(registrationOption.equals("CBCSMIN"))
						{
							Map<String, List<String>> cbcsMinorCourseDetails =  (Map<String, List<String>>) session.getAttribute("minorCourseDetails");
							String subRegistrationOption = (String) session.getAttribute("subRegistrationOption");
							if(cbcsMinorCourseDetails!=null && cbcsMinorCourseDetails.containsKey(subRegistrationOption))
							{
								
								courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassOptionGeneral(semesterSubId, 
										Arrays.asList(classGroupId), Arrays.asList(classType), cbcsMinorCourseDetails.get(subRegistrationOption));
								returnModelList = courseAllocationRepository.findCourseListByCourseIds(courseIdList);
								
								
							}
						}
						else if(registrationOption.equals("CBCSHON"))
						{
							Map<String, List<String>> cbcsMinorCourseDetails =  (Map<String, List<String>>) session.getAttribute("honourCourseDetails");
							String subRegistrationOption = (String) session.getAttribute("subRegistrationOption");
							if(cbcsMinorCourseDetails!=null && cbcsMinorCourseDetails.containsKey(subRegistrationOption))
							{
								
								courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassOptionGeneral(semesterSubId, 
										Arrays.asList(classGroupId), Arrays.asList(classType), cbcsMinorCourseDetails.get(subRegistrationOption));
								
								returnModelList = courseAllocationRepository.findCourseListByCourseIds(courseIdList);
								
							}
						}
						else if(registrationOption.equals("ACEMIN"))
						{
							Map<String, List<String>> aceMinorCourseDetails =  (Map<String, List<String>>) session.getAttribute("minorCourseDetails");
							String subRegistrationOption = (String) session.getAttribute("subRegistrationOption");
							if(aceMinorCourseDetails!=null && aceMinorCourseDetails.containsKey(subRegistrationOption))
							{
								
								courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassOptionGeneral(semesterSubId, 
										Arrays.asList(classGroupId), Arrays.asList(classType), aceMinorCourseDetails.get(subRegistrationOption));
								returnModelList = courseAllocationRepository.findCourseListByCourseIds(courseIdList);
								
								
							}
						}
						else
							
						{
							prgSplCurriculumDetailList = programmeSpecializationCurriculumDetailRepository.findCourseCodeBySpecIdAdmissionYearAndCourseCategory(
									programSpecId, admissionYear, registrationOption);
							if (!prgSplCurriculumDetailList.isEmpty())
							{
								courseCodeList = prgSplCurriculumDetailList.stream().map(e-> e.getCourseCode()).distinct().collect(Collectors.toList());
							}
							else
							{
								courseCodeList.add("NONE");
							}
							
							
							LOGGER.trace("\n default - courseIdList: "+ courseIdList);

							courseIdList=courseAllocationService.getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(semesterSubId, 
									Arrays.asList(classGroupId), Arrays.asList(classType), programGroupCode, programSpecCode, costCentreCode);
							
							if(courseIdList.isEmpty())
							{
								courseIdList.add("NONE");
							}
							LOGGER.trace("\n default - courseIdList:--> "+ courseIdList);
							
							returnModelList = courseAllocationRepository.findCourseFFCSCALCourseList(egbGroupId,  "%"+programGroup+"%",
									courseCodeList, Arrays.asList(courseSystem), Arrays.asList("ECA","OC"), courseIdList);

							
							if (!returnModelList.isEmpty())
							{
								returnModelList = returnModelList.parallelStream().filter(e-> (!e.getEvaluationType().equals("SETCONFERENCE")))
										.collect(Collectors.toList());
							}
						}
						break;
				}
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			LOGGER.trace("\n Exception: "+ exception);
		}
		
		return returnModelList;
	}	
	
	
	List<ReseachStudentCourseDetailDto> findResearchCourseDetails(String regNo)
	{
		List<Object[]> getResearchCourseDetails =  courseCatalogRepository.findResearchStudentByRegNo(regNo);
		
		List<ReseachStudentCourseDetailDto> dtoListDetail = new ArrayList<>();
		
		for (Object[] objects : getResearchCourseDetails) 
		{
			ReseachStudentCourseDetailDto dto = new ReseachStudentCourseDetailDto();
			
			dto.setRegisterNumber(objects[0].toString());
			dto.setCourseId(objects[1].toString());
			dto.setCourseCode(objects[2].toString());
			dto.setCourseTitle(objects[3].toString());
			dto.setCourseVersion(Float.parseFloat(objects[4].toString()));
			dto.setGenericCourseType(objects[5].toString());
			dto.setMeetingStatus(Integer.parseInt(objects[6].toString()));
			
			dtoListDetail.add(dto);
			
		}
		
		return dtoListDetail;
	}

	public String findGenericTypeByCourseCode(String courseCode) {
		return courseCatalogRepository.findGenericTypeByCourseCode(courseCode);
	}
	
	
	
}
