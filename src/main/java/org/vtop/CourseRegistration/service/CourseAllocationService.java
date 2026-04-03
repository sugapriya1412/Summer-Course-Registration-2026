package org.vtop.CourseRegistration.service;

import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.model.CourseAllocationModel;
import org.vtop.CourseRegistration.model.SlotTimeMasterModel;
import org.vtop.CourseRegistration.repository.CourseAllocationRepository;


@Service
@Transactional(readOnly=true)
public class CourseAllocationService
{
	@Autowired private CourseAllocationRepository courseAllocationRepository;
	
	private static final Logger LOGGER = LogManager.getLogger(CourseAllocationService.class);
	
	
	public CourseAllocationModel getOne(String classId)
	{
		return courseAllocationRepository.findById(classId).orElse(null);
	}
	
	public List<CourseAllocationModel> getCourseAllocationCourseIdList2(String semesterSubId, String[] classGroupId, 
											String[] classType, String courseId, List<String> courseType, 
											String progGroupCode, String progSpecCode, String costCentreCode)
	{
		List<CourseAllocationModel> tempModelList = new ArrayList<CourseAllocationModel>();
		
		if (progGroupCode.equals("RP"))
		{
			tempModelList = courseAllocationRepository.findByCourseIdAndCourseType(semesterSubId, classGroupId, 
								classType, courseId, courseType);
		}
		else
		{
			tempModelList = courseAllocationRepository.findByCourseIdCourseTypeAndClassOption(semesterSubId, 
								classGroupId, classType, courseId, courseType, progGroupCode, progSpecCode, 
								costCentreCode);
		}
		
		Collections.sort(tempModelList);
		
		return tempModelList;
	}
	
	public List<CourseAllocationModel> getCourseAllocationCourseIdTypeList(String semesterSubId, String[] classGroupId, 
											String[] classType, String courseId, String courseType, String progGroupCode, 
											String progSpecCode, String costCentreCode, String registrationOption,String regNo) throws JsonProcessingException {
		List<CourseAllocationModel> tempModelList = new ArrayList<CourseAllocationModel>();
		
		if (progGroupCode.equals("RP"))
		{
			tempModelList = courseAllocationRepository.findByCourseIdAndCourseType2(semesterSubId, classGroupId, 
								classType, courseId, courseType);
		}
		else if(registrationOption!=null && registrationOption.equals("CBCSMIN"))
		{
			tempModelList = courseAllocationRepository.findByCourseIdAndCourseTypeClassOptionGeneral(semesterSubId, classGroupId, 
					classType, courseId, courseType);
			
		}
		else
		{
			tempModelList = courseAllocationRepository.findByCourseIdCourseTypeAndClassOption2(semesterSubId, 
								classGroupId, classType, courseId, courseType, progGroupCode, progSpecCode, 
								costCentreCode);
		}


		String psychometryClassIdList = courseAllocationRepository.doGetPsychometricTestClassIds(semesterSubId, Arrays.asList(classGroupId),courseId,regNo);

		LOGGER.info("psychometryClassIdList===>"+psychometryClassIdList);
		//System.out.println("psychometryClassIdList===>"+psychometryClassIdList+" ; "+semesterSubId+" ; "+ Arrays.asList(classGroupId)+" ; "+courseId+" ; "+regNo);



		if (psychometryClassIdList != null && !psychometryClassIdList.isEmpty()) {

			ObjectMapper mapper = new ObjectMapper();

			// Parse JSON safely into a List<String>
			List<String> classNbrList = mapper.readValue(
					psychometryClassIdList, new TypeReference<List<String>>() {}
			);

			// Use iterator if you want to remove items safely from tempModelList
			Iterator<CourseAllocationModel> iterator = tempModelList.iterator();
			while (iterator.hasNext()) {
				CourseAllocationModel allocation = iterator.next();

				if (!classNbrList.contains(allocation.getClassId())) {

					LOGGER.info("psychometryClassId===>Remove===>"+allocation.getClassId());
					//System.out.println("psychometryClassId===>Remove===>"+allocation.getClassId());

					iterator.remove(); // ✅ safely removes from list during iteration
				}
			}
		}



		
		Collections.sort(tempModelList);
		
		return tempModelList;
	}
	
	public List<CourseAllocationModel> getCourseAllocationCourseIdTypeEmpidList(String semesterSubId, String[] classGroupId, 
											String[] classType, String courseId, String courseType, String erpId, 
											String progGroupCode, String progSpecCode, String costCentreCode,String regNo) throws JsonProcessingException {
		List<CourseAllocationModel> tempModelList = new ArrayList<CourseAllocationModel>();
		
		if (progGroupCode.equals("RP"))
		{
			tempModelList = courseAllocationRepository.findByCourseIdCourseTypeAndEmpId(semesterSubId, classGroupId, 
								classType, courseId, courseType, erpId);
		}
		else
		{
			tempModelList = courseAllocationRepository.findByCourseIdCourseTypeEmpIdAndClassOption(semesterSubId, 
								classGroupId, classType, courseId, courseType, erpId, progGroupCode, progSpecCode, 
								costCentreCode);
		}


		String psychometryClassIdList = courseAllocationRepository.doGetPsychometricTestClassIds(semesterSubId, Arrays.asList(classGroupId),courseId,regNo);

		if (psychometryClassIdList != null && !psychometryClassIdList.isEmpty()) {

			ObjectMapper mapper = new ObjectMapper();

			// Parse JSON safely into a List<String>
			List<String> classNbrList = mapper.readValue(
					psychometryClassIdList, new TypeReference<List<String>>() {}
			);

			// Use iterator if you want to remove items safely from tempModelList
			Iterator<CourseAllocationModel> iterator = tempModelList.iterator();
			while (iterator.hasNext()) {
				CourseAllocationModel allocation = iterator.next();

				if (!classNbrList.contains(allocation.getClassId())) {
					iterator.remove(); // ✅ safely removes from list during iteration
				}
			}
		}
		
		Collections.sort(tempModelList);
		
		return tempModelList;
	}
	
	public CourseAllocationModel getCourseAllocationCourseIdTypeEmpidSlotAssoList(String semesterSubId, 
										String[] classGroupId, String[] classType, String courseId, String courseType, 
										String erpId, Long slotId, String assoClassId, String progGroupCode, 
										String progSpecCode, String costCentreCode)
	{
		CourseAllocationModel tempModel = new CourseAllocationModel();

		if (progGroupCode.equals("RP"))
		{
			tempModel = courseAllocationRepository.findByCourseIdCourseTypeEmpIdSlotIdAndAssoClassId(semesterSubId, 
							classGroupId, classType, courseId, courseType, erpId, slotId, assoClassId);
		}
		else
		{
			tempModel = courseAllocationRepository.findByCourseIdCourseTypeEmpIdSlotIdAssoClassIdAndClassOption(
							semesterSubId, classGroupId, classType, courseId, courseType, erpId, slotId, assoClassId, 
							progGroupCode, progSpecCode, costCentreCode);
		}


		
		
		return tempModel;
	}
	
	public List<CourseAllocationModel> getCourseAllocationCourseCodeAvbList(String semesterSubId, String[] classGroupId, 
											String[] classType, String courseCode, String[] courseSystem, String progGroupCode, 
											String progSpecCode, String costCentreCode,String regNo,String courseId) throws JsonProcessingException {
		List<CourseAllocationModel> tempModelList = new ArrayList<CourseAllocationModel>();
		
		if (progGroupCode.equals("RP"))
		{
			tempModelList = courseAllocationRepository.findAvailableClassByCourseCode(semesterSubId, classGroupId, 
								classType, courseCode, courseSystem);
		}
		else
		{
			tempModelList = courseAllocationRepository.findAvailableClassByCourseCodeAndClassOption(semesterSubId, 
								classGroupId, classType, courseCode, courseSystem, progGroupCode, progSpecCode, costCentreCode);
		}

		String psychometryClassIdList = courseAllocationRepository.doGetPsychometricTestClassIds(semesterSubId, Arrays.asList(classGroupId),courseId, regNo);

		if (psychometryClassIdList != null && !psychometryClassIdList.isEmpty()) {

			ObjectMapper mapper = new ObjectMapper();

			// Parse JSON safely into a List<String>
			List<String> classNbrList = mapper.readValue(
					psychometryClassIdList, new TypeReference<List<String>>() {}
			);

			// Use iterator if you want to remove items safely from tempModelList
			Iterator<CourseAllocationModel> iterator = tempModelList.iterator();
			while (iterator.hasNext()) {
				CourseAllocationModel allocation = iterator.next();

				if (!classNbrList.contains(allocation.getClassId())) {
					iterator.remove(); // ✅ safely removes from list during iteration
				}
			}
		}
		
		Collections.sort(tempModelList);
		
		return tempModelList;
	}
	
	
	//Filter Course Allocation by Employee Id
	public List<CourseAllocationModel> getAllocationByEmployeeId(List<CourseAllocationModel> camList, String employeeId, 
											String ccCourseSystem, String combineClassId)
	{
		int filterType = 2; 
		List<CourseAllocationModel> tempModelList = new ArrayList<CourseAllocationModel>();
		List<String> classIdList = new ArrayList<String>();
		//LOGGER.trace("\n ccCourseSystem: "+ ccCourseSystem +" | combineClassId: "+ combineClassId);
		
		if ((!camList.isEmpty()) && (employeeId != null) && (!employeeId.equals("")))
		{
			if (ccCourseSystem.equals("NONFFCS") || ccCourseSystem.equals("FFCS") || ccCourseSystem.equals("CAL"))
			{
				filterType = 2;
			}
			else
			{
				filterType = 1;
			}
			
			if (filterType == 1)
			{
				for (CourseAllocationModel e : camList)
				{
					if (e.getCombineClassId().equals(combineClassId))
					{
						tempModelList.add(e);
					}
				}
			}
			else
			{
				for (CourseAllocationModel e : camList)
				{
					if (e.getErpId().equals(employeeId))
					{
						classIdList.add(e.getClassId());
					}
				}
				
				if (!classIdList.isEmpty())
				{
					for (CourseAllocationModel e : camList)
					{
						if (classIdList.contains(e.getAssoClassId()))
						{
							tempModelList.add(e);
						}
					}
				}
			}
		}



		
		Collections.sort(tempModelList);
		
		return tempModelList;
	}	
	
	public Integer getAvailableRegisteredSeats(String classId)
	{
		Integer returnCount = 0;
		
		returnCount = courseAllocationRepository.findAvailableRegisteredSeats(classId);
		if ((returnCount == null) || (returnCount < 0))
		{
			returnCount = 0;
		}
		
		return returnCount;
	}
	
	public Integer getAvailableWaitingSeats(String classId)
	{
		Integer returnCount = 0;
		
		returnCount = courseAllocationRepository.findAvailableWaitingSeats(classId);
		if ((returnCount == null) || (returnCount < 0))
		{
			returnCount = 0;
		}
		
		return returnCount;
	}
	
	public String getClashStatus(Integer patternId, String clashSlot, List<Object[]> allottedSlot, 
							Map<String, List<SlotTimeMasterModel>> slotTimeMapList)
	{
		int clashStatus = 2, allotPatternId = 0;
		long clashStartTime = 0, clashEndTime = 0, allotStartTime = 0, allotEndTime = 0;
		String checkSlot = "NONE", allotSlot = "", clashWeekDay = "", allotWeekDay = "", clashSlotType = "", 
					allotSlotType = "", key = "", key2 = "";
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		List<SlotTimeMasterModel> stmModelList = new ArrayList<SlotTimeMasterModel>();
		List<SlotTimeMasterModel> stmModelList2 = new ArrayList<SlotTimeMasterModel>();
				
		try
		{
			//LOGGER.trace("\n patternId: "+ patternId +" | clashSlot: "+ clashSlot);
			//LOGGER.trace("\n allottedSlot size: "+ allottedSlot.size() +" | slotTimeMapList size: "+ slotTimeMapList.size());
			
			if ((patternId != null) && (patternId > 0) && (clashSlot != null) && (!clashSlot.equals("")) 
					&& (allottedSlot.isEmpty()))
			{
				clashStatus = 1;
			}
			else if ((patternId != null) && (patternId > 0) && (clashSlot != null) && (!clashSlot.equals("")) 
						&& (!allottedSlot.isEmpty()) && (!slotTimeMapList.isEmpty()))
			{
				for (String clhSt : clashSlot.split("/"))
				{
					stmModelList.clear();
										
					key = patternId +"_"+ clhSt;
					if (slotTimeMapList.containsKey(key))
					{
						stmModelList.addAll(slotTimeMapList.get(key));
					}
					//LOGGER.trace("\n Clash=> key: "+ key +" | stmModelList size: "+ stmModelList.size());
										
					if (!stmModelList.isEmpty())
					{
						for (SlotTimeMasterModel stm : stmModelList)
						{
							clashWeekDay = stm.getStmPkId().getWeekdays();
							clashStartTime = Long.parseLong(sdf.format(stm.getStmPkId().getSlotStartingTime()));
							clashEndTime = Long.parseLong(sdf.format(stm.getStmPkId().getSlotEndingTime()));
							clashSlotType = stm.getSlotType();
							//LOGGER.trace("\n clashWeekDay: "+ clashWeekDay +" | clashStartTime: "+ clashStartTime 
							//		+" | clashEndTime: "+ clashEndTime +" | clashSlotType: "+ clashSlotType);
							
							for (Object[] obj : allottedSlot)
							{
								allotPatternId = Integer.parseInt(obj[0].toString());
								allotSlot = obj[1].toString();
								//LOGGER.trace("\n allotPatternId: "+ allotPatternId +" | allotSlot: "+ allotSlot);
								
								for (String altSt : allotSlot.split("\\+"))
								{
									stmModelList2.clear();
										
									key2 = allotPatternId +"_"+ altSt;
									if (slotTimeMapList.containsKey(key2))
									{
										stmModelList2.addAll(slotTimeMapList.get(key2));
									}
									//LOGGER.trace("\n Allot=> key2: "+ key2 +" | stmModelList2 size: "+ stmModelList2.size());
									
									if (!stmModelList2.isEmpty())
									{
										for (SlotTimeMasterModel stm2 : stmModelList2)
										{
											clashStatus = 2;
											checkSlot = "NONE";
											
											allotWeekDay = stm2.getStmPkId().getWeekdays();
											allotStartTime = Long.parseLong(sdf.format(stm2.getStmPkId().getSlotStartingTime()));
											allotEndTime = Long.parseLong(sdf.format(stm2.getStmPkId().getSlotEndingTime()));
											allotSlotType = stm2.getSlotType();
											//LOGGER.trace("\n allotWeekDay: "+ allotWeekDay +" | allotStartTime: "+ allotStartTime 
											//		+" | allotEndTime: "+ allotEndTime +" | allotSlotType: "+ allotSlotType);
											
											if (altSt.equals(clhSt))
											{
												checkSlot = altSt;
												//LOGGER.trace("\n Clash Check Level 1: Failed");
											}
											else if (allotWeekDay.equals(clashWeekDay) && allotSlotType.equals(clashSlotType) 
															&& (((allotStartTime >= clashStartTime) && (allotStartTime <= clashEndTime))
																	|| ((allotEndTime >= clashStartTime) && (allotEndTime <= clashEndTime))))
											{
												checkSlot = altSt;
												//LOGGER.trace("\n Clash Check Level 2: Failed");
											}
											else if (allotWeekDay.equals(clashWeekDay) && (!allotSlotType.equals(clashSlotType))  
															&& (clashSlotType.equals("GENERAL") || allotSlotType.equals("GENERAL")) 
															&& (((allotStartTime >= clashStartTime) && (allotStartTime <= clashEndTime))
																	|| ((allotEndTime >= clashStartTime) && (allotEndTime <= clashEndTime))))
											{
												checkSlot = altSt;
												//LOGGER.trace("\n Clash Check Level 3: Failed");
											}
											else
											{
												clashStatus = 1;
											}
											//LOGGER.trace("\n clashStatus: "+ clashStatus);
											
											if (clashStatus == 2) break;
										}
									}
									
									if (clashStatus == 2) break;
								}
								
								if (clashStatus == 2) break;
							}
							
							if (clashStatus == 2) break;
						}
					}
					
					if (clashStatus == 2) break;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.trace(e);
		}
		
		return clashStatus +"|"+ checkSlot;
	}
	
	public String getSlotInfoStatus(Integer patternId, String slot, Long buildingId, String buildingCode, List<Object[]> allottedSlot, 
							Map<String, List<SlotTimeMasterModel>> slotTimeMapList, Map<String, Integer> slotFixedInfoList)
	{
		int clashStatus = 2, allotPatternId = 0;
		long allotBuildingId = 0, diffValue = 0;
		String message = "NONE", allotSlot = "", clashWeekDay = "", allotWeekDay = "", key = "", key2 = "", color = "";
				
		List<SlotTimeMasterModel> stmModelList = new ArrayList<SlotTimeMasterModel>();
		List<SlotTimeMasterModel> stmModelList2 = new ArrayList<SlotTimeMasterModel>();
				
		try
		{
			//LOGGER.trace("\n patternId: "+ patternId +" | slot: "+ slot 
			//		+" | buildingId: "+ buildingId +" | buildingCode: "+ buildingCode);
			//LOGGER.trace("\n allottedSlot size: "+ allottedSlot.size() 
			//		+" | slotTimeMapList size: "+ slotTimeMapList.size() 
			//		+" | slotFixedInfoList size: "+ slotFixedInfoList.size());
			
			if ((patternId != null) && (patternId > 0) && (slot != null) && (!slot.equals("")) 
					&& (allottedSlot.isEmpty()))
			{
				clashStatus = 1;
			}
			else if ((patternId != null) && (patternId > 0) && (slot != null) && (!slot.equals("")) 
						&& (!allottedSlot.isEmpty()) && (!slotTimeMapList.isEmpty()))
			{
				for (String slt : slot.split("\\+"))
				{
					stmModelList.clear();
										
					key = patternId +"_"+ slt;
					if (slotTimeMapList.containsKey(key))
					{
						stmModelList.addAll(slotTimeMapList.get(key));
					}
					//LOGGER.trace("\n Clash=> key: "+ key +" | stmModelList size: "+ stmModelList.size());
										
					if (!stmModelList.isEmpty())
					{
						for (SlotTimeMasterModel stm : stmModelList)
						{
							clashWeekDay = stm.getStmPkId().getWeekdays();
							//LOGGER.trace("\n clashWeekDay: "+ clashWeekDay);
							
							for (Object[] obj : allottedSlot)
							{
								allotPatternId = Integer.parseInt(obj[0].toString());
								allotSlot = obj[1].toString();
								allotBuildingId = Long.parseLong(obj[4].toString());
								//LOGGER.trace("\n allotPatternId: "+ allotPatternId +" | allotSlot: "+ allotSlot 
								//		+" | allotBuildingId: "+ allotBuildingId);
								
								for (String altSt : allotSlot.split("\\+"))
								{
									stmModelList2.clear();
										
									key2 = allotPatternId +"_"+ altSt;
									if (slotTimeMapList.containsKey(key2))
									{
										stmModelList2.addAll(slotTimeMapList.get(key2));
									}
									//LOGGER.trace("\n Allot=> key2: "+ key2 +" | stmModelList2 size: "+ stmModelList2.size());
									
									if (!stmModelList2.isEmpty())
									{	
										message = "NONE";
										color = "green";
										
										//Checking the clash
										for (SlotTimeMasterModel stm2 : stmModelList2)
										{
											clashStatus = 2;
											diffValue = 0;
																						
											allotWeekDay = stm2.getStmPkId().getWeekdays();
											//LOGGER.trace("\n allotWeekDay: "+ allotWeekDay);
											
											if (allotWeekDay.equals(clashWeekDay))
											{													
												if (slotFixedInfoList.containsKey(slt +"_"+ altSt))
												{
													diffValue = slotFixedInfoList.get(slt +"_"+ altSt);
												}
												else if (slotFixedInfoList.containsKey(altSt +"_"+ slt))
												{
													diffValue = slotFixedInfoList.get(altSt +"_"+ slt);
												}
												//LOGGER.trace("\n diffValue: "+ diffValue);
												
												if (allotBuildingId != buildingId) 
												{
													if (diffValue > 0)
													{
														message = "Different building block in same week day and having only "+ diffValue 
																		+" minute(s) difference with registered slot "+ allotSlot +".";
													}
													else
													{
														message = "Different building block in same week day with registered slot "+ allotSlot +".";
														clashStatus = 1;
													}
													color = "#1E6B16";
												}
												else if (allotBuildingId == buildingId)
												{
													if (diffValue > 0)
													{
														message = "Same building block in same week day and having only "+ diffValue 
																		+" minute(s) difference with registered slot "+ allotSlot +".";
													}
													else
													{
														message = "Same building block in same week day with registered slot "+ allotSlot +".";
														clashStatus = 1;
													}
													color = "#1E6B16";
												}
												else if (diffValue > 0)
												{
													message = "Having only "+ diffValue +" minute(s) difference with registered slot "+ allotSlot +".";
													color = "#1E6B16";
												}
												else
												{
													clashStatus = 1;
												}													
											}
											else
											{
												clashStatus = 1;
											}
												
											if (clashStatus == 2) break;
											//LOGGER.trace("\n Block & Time Check Status: "+ clashStatus);
										}
									}
									
									if (clashStatus == 2) break;
								}
								
								if (clashStatus == 2) break;
							}
							
							if (clashStatus == 2) break;
						}
					}
					
					if (clashStatus == 2) break;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.trace(e);
		}
				
		return clashStatus +"|"+ message +"|"+ color;
	}
	
	public List<String> getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(String semesterSubId, List<String> classGroupId, 
			List<String> classType, String progGroupCode, String progSpecCode, String costCentreCode)
	{
		return courseAllocationRepository.getCourseIdBySemesterSubIdClassGroupClassTypeAndClassOption(semesterSubId, classGroupId, classType, progGroupCode, progSpecCode, costCentreCode);
	}
	
	public List<String> getCourseIdBySemesterSubIdClassOptionGeneral(String semesterSubId, List<String> classGroupId, 
			List<String> classType,List<String> courseIdList)
	{
		return courseAllocationRepository.getCourseIdBySemesterSubIdClassOptionGeneral(semesterSubId, classGroupId, classType, courseIdList);
	}

	public 	List<Object[]> doGetListCourseIdOfOECBySemesterSubIdAndClassGrpIdAndProgId(int admissionsYear,String semesterSubId,List<String> classGrpId,int programmeSplzationId,int progGroupId)
	{
		return courseAllocationRepository.doGetListCourseIdOfOECBySemesterSubIdAndClassGrpIdAndProgId(admissionsYear,semesterSubId,classGrpId,programmeSplzationId,progGroupId);
	}


}
