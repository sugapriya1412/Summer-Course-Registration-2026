package org.vtop.CourseRegistration.service;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.vtop.CourseRegistration.AppGlobalValues;
import org.vtop.CourseRegistration.GlobalMaster;
import org.vtop.CourseRegistration.Common.service.CaptchaManager;
import org.vtop.CourseRegistration.model.AdditionalLearningOpted;
import org.vtop.CourseRegistration.model.CourseAllocationModel;
import org.vtop.CourseRegistration.model.CourseCatalogModel;
import org.vtop.CourseRegistration.model.CourseEquivalancesModel;
import org.vtop.CourseRegistration.model.SlotTimeMasterModel;
import org.vtop.CourseRegistration.model.StudentHistoryModel;
import org.vtop.CourseRegistration.model.StudentInformation;
import org.vtop.CourseRegistration.repository.SemesterClassgroupApplicableSplztnCumActivityRepository;

@Service
@Transactional(readOnly = true)
public class CourseRegistrationCommonFunction {
	@Autowired
	private CourseCatalogService courseCatalogService;
	@Autowired
	private CourseAllocationService courseAllocationService;
	@Autowired
	private CourseRegistrationService courseRegistrationService;
	@Autowired
	private StudentHistoryService studentHistoryService;
	@Autowired
	private ProgrammeSpecializationCurriculumCreditService programmeSpecializationCurriculumCreditService;
	@Autowired
	private ProgrammeSpecializationCurriculumDetailService programmeSpecializationCurriculumDetailService;
	@Autowired
	private CourseRegistrationWithdrawService courseRegistrationWithdrawService;
	@Autowired
	private CourseEquivalanceRegService courseEquivalanceRegService;
	@Autowired
	private CompulsoryCourseConditionDetailService compulsoryCourseConditionDetailService;
	@Autowired
	private SemesterMasterService semesterMasterService;
	@Autowired
	private CaptchaManager captchaManager;
	@Autowired
	private SemesterClassgroupApplicableSplztnCumActivityRepository activityRepo;
	@Autowired
	private AdditionalLearningOptedService optedService;
	@Autowired
	private StudentInformation details;

	public static String WISHLIST_CHECK = "Wishlist Completed ?";
	public static String ACAEMIC_YEAR_CHECK = "Your Academic Start Year";
	public static String PGM_SPEC_CHECK = "Programme Specializations ";
	public static String PGM_MODE_CHECK = "Programme Mode";
	public static String REGISTRATION_SCHEDULE_CHECK = "Registration Schedule";

	private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationCommonFunction.class);

	@Autowired
	GlobalMaster globalValues;

	@Autowired
	StudentInformation stuCrsRegActy;

	// Check Registration of course Eligibility
	public String CheckRegistrationCondition(String[] pCourseSystem, Integer pProgramGroupId, String pProgramGroupCode,
			String pProgramSpecCode, String pSemesterSubId, String pRegisterNumber, String pOldRegisterNumber,
			float maxCredit, String pCourseId, Integer pStudentStartYear, Integer pStudentGraduateYear,
			String studStudySystem, Integer pProgramSpecId, Float pCurriculumVersion, Integer PEUEStatus,
			String programGroupMode, String[] classGroupId, String pStudentCgpaData, Integer optionNAStatus,
			List<String> studCompulsoryCourse, Integer pSemesterId, String[] classType, String costCentreCode,
			int academicGraduateYear, float cclTotalCredit, String cgpaProgGroupId, String registrationOption,
			List<String> excessCreditAllowedCategories, float coRecCredits) {
		int historyflag = 2, regflag = 2, compCourseFlag = 1;
		int regAllowFlag = 2, wlAllowFlag = 2, audAllowFlag = 2, rgrAllowFlag = 2, minAllowFlag = 2, honAllowFlag = 2,
				adlAllowFlag = 2, RPEUEAllowFlag = 2, RDEOEAllowFlag = 2, RSEOEAllowFlag = 2, csAllowFlag = 2,
				RUCUEAllowFlag = 2, rgrOptionFlag = 2, allCompAllowFlag = 1, rrAllowFlag = 1;
		int regularAllowStatus = 2, NGradeAllowStatus = 2, giAllowStatus = 2, auditAllowStatus = 2,
				minHonAllowStatus = 2, adlAllowStatus = 2, peAdlAllowStatus = 2, ueAdlAllowStatus = 2,
				regComponentFlag = 0;
		int courseMehtodType = 1, crTpCount = 0, subCrCount = 0;
		int flag = 2, flag2 = 2, flag3 = 2, flag4 = 2, flag5 = 2;
		int flag6 = 2, flag7 = 2, flag8 = 2, flag9 = 2, flag10 = 2;
		Integer courseProgId = 0, audCount = 0, adlCount = 0, giCount = 0;

		float courseCredit = 0, obtCredit = 0, rmgCredit = 0, ueRmgCredit = 0, oeRmgCredit = 0, totCdtReg = 0,
				totCdtEarn = 0, ccCredit = 0, ctgCredit = 0, bskObtCredit = 0, regCredit = 0, wlCredit = 0,
				totalRegCredit = 0, lectureCredit = 0, practicalCredit = 0, projectCredit = 0;
		Float cgpa = 0F;

		String courseOption = "RGR", courseType = "NONE", subCourseOption = "NONE", subCourseType = "NONE",
				subCourseDate = "NONE";
		String courseCode = "", ccCourseSystem = "", ceCourseId = "NIL", compCourseStatus = "NONE";
		String genericCoursetype = "", evaluationType = "", courseAltProgId = "", prerequisite = "NONE";
		String grade = "", msg = "", subCrType = "";
		String historyCourseId = "", historyGenericCourseType = "", historyExamMonth = "", historyCourseSystem = "",
				authKeyVal = "NONE";
		String courseCategory = "UE", ccCourseId = "NONE", catalogType = "NONE", basketCategory = "NONE";

		String[] antirequisite = { "NONE" };
		CourseCatalogModel ccm = new CourseCatalogModel();
		CourseCatalogModel historyCCM = new CourseCatalogModel();

		List<CourseEquivalancesModel> cemList = new ArrayList<CourseEquivalancesModel>();
		List<StudentHistoryModel> shmList = new ArrayList<StudentHistoryModel>();

		List<String> historyCourseTypeList = new ArrayList<String>();
		List<String> subCrTypeList = new ArrayList<String>();
		List<String> courseTypeList = new ArrayList<String>();
		List<String> courseTypeList2 = new ArrayList<String>();
		List<String> ceCourseList = new ArrayList<String>();
		List<String> ceList = new ArrayList<String>();
		List<String> spsRegList = new ArrayList<String>();
		List<String> ncCourseList = new ArrayList<String>();

		List<Object[]> shmList2 = new ArrayList<Object[]>();
		List<Object[]> psRegList = new ArrayList<Object[]>();
		List<Object[]> ccCreditList = new ArrayList<Object[]>();

		try {

			// Student CGPA Detail - Method 2
			if ((pStudentCgpaData != null) && (!pStudentCgpaData.equals(""))) {
				String[] studentCgpaArr = pStudentCgpaData.split("\\|");

				totCdtReg = Float.parseFloat(studentCgpaArr[0]);
				totCdtEarn = Float.parseFloat(studentCgpaArr[1]);
				cgpa = Float.parseFloat(studentCgpaArr[2]);
			}

			// Get the Allowed Course Option Status
			String[] courseOptionStatusArray = getCourseOptionStatus(pProgramGroupCode, pProgramSpecCode,
					pStudentGraduateYear, academicGraduateYear, pSemesterId, pStudentStartYear).split("\\|");
			if ((courseOptionStatusArray != null) && (courseOptionStatusArray.length > 0)) {
				regularAllowStatus = Integer.parseInt(courseOptionStatusArray[0]);
				NGradeAllowStatus = Integer.parseInt(courseOptionStatusArray[1]);
				giAllowStatus = Integer.parseInt(courseOptionStatusArray[2]);
				auditAllowStatus = Integer.parseInt(courseOptionStatusArray[3]);
				minHonAllowStatus = Integer.parseInt(courseOptionStatusArray[4]);
				adlAllowStatus = Integer.parseInt(courseOptionStatusArray[5]);
				peAdlAllowStatus = Integer.parseInt(courseOptionStatusArray[6]);
				ueAdlAllowStatus = Integer.parseInt(courseOptionStatusArray[7]);
			}

			// Checking the select course is valid or not
			if ((pCourseId != null) && (!pCourseId.equals(""))) {
				ccm = courseCatalogService.getOne(pCourseId);
				if (ccm != null) {
					courseCode = ccm.getCode();
					courseProgId = ccm.getGroupId();
					courseAltProgId = ccm.getGroupCode();
					genericCoursetype = ccm.getGenericCourseType();
					ccCourseSystem = ccm.getCourseSystem();
					evaluationType = ccm.getEvaluationType();
					courseCredit = ccm.getCredits() + coRecCredits;
					lectureCredit = ccm.getLectureCredits();
					practicalCredit = ccm.getPracticalCredits();
					projectCredit = ccm.getProjectCredits();

					if ((ccm.getPrerequisite() != null) && (!ccm.getPrerequisite().equals(""))) {
						prerequisite = ccm.getPrerequisite().replace(" ", "");
					}

					if ((ccm.getAntirequisite() != null) && (!ccm.getAntirequisite().equals(""))) {
						antirequisite = ccm.getAntirequisite().replace(" ", "").split("/");
					}

					// To get the course equivalence
					cemList = semesterMasterService.getCourseEquivalancesByCourseId(pCourseId);
					List<String> eqCourseCodeList = new ArrayList<>();
					for (CourseEquivalancesModel e : cemList) {
						ceCourseList.add(e.getCourseEquivalancesPkId().getEquivalentCourseId());
						eqCourseCodeList.add(e.getCourseCode());
					}

					// To get course category from curriculum
					if (pCurriculumVersion > 0) {
						psRegList.clear();
						if (registrationOption.equals("CALCBCS")) {
							psRegList = programmeSpecializationCurriculumDetailService
									.getCurriculumByAdmsnYearCCVersionAndCourseCode(pProgramSpecId, pStudentStartYear,
											pCurriculumVersion, eqCourseCodeList);
						} else if (registrationOption.equals("OEC")) {
							List<Object[]> courseList = new ArrayList<>();
							courseList = courseAllocationService
									.doGetListCourseIdOfOECBySemesterSubIdAndClassGrpIdAndProgId(pStudentStartYear,
											pSemesterSubId, Arrays.asList(classGroupId), pProgramSpecId,
											pProgramGroupId);

							courseList.addAll(programmeSpecializationCurriculumDetailService
									.doGetAllOECoursesACE(pStudentStartYear, pProgramGroupId, "OEC"));

							for (Object[] obj : courseList) {
								if (obj[4].toString().equals(courseCode)) {
									psRegList.add(obj);
									break;
								}
							}
						} else {
							psRegList = programmeSpecializationCurriculumDetailService
									.getCurriculumByAdmsnYearCCVersionAndCourseCode(pProgramSpecId, pStudentStartYear,
											pCurriculumVersion, courseCode);
						}

						if (!psRegList.isEmpty()) {
							for (Object[] e : psRegList) {
								courseCategory = e[0].toString();
								catalogType = e[1].toString();
								ccCourseId = e[2].toString();
								basketCategory = e[5].toString();
								ccCredit = Float.parseFloat(e[6].toString());
								break;
							}
						}
						if (registrationOption.equals("CBCSMIN")) {
							courseCategory = "CBCSMIN";
						}
						if (registrationOption.equals("CBCSHON")) {
							courseCategory = "CBCSHON";
						}
						if (registrationOption.equals("ACEMIN")) {
							courseCategory = "ACEMIN";
						}
						if (registrationOption.equals("ME")) {
							courseCategory = "ME";
						}

						if (registrationOption.equals("OEC")) {
							courseCategory = registrationOption;
						}
					}

					// To assign the All Component Allow Flag
					if (evaluationType.equals("TARP") || evaluationType.equals("LSM") || evaluationType.equals("IIP")) {
						allCompAllowFlag = 2;
					} else if (ccCourseSystem.equals("FFCS")) {
						allCompAllowFlag = 2;
					}

					flag = 1;
				} else {
					// Co-Requisite not offered
					msg = "Invalid course code-1";
				}

				if (flag == 1) {
					if (pProgramGroupCode.equals("RP") || pProgramGroupCode.equals("IEP")) {
						flag = 1;
					} else if ((!studStudySystem.equals("NONFFCS")) && (!studStudySystem.equals("FFCS"))) {
						flag = 1;
					} else if (courseCategory.equals("PC") || courseCategory.equals("UC") || courseCategory.equals("DC")
							|| courseCategory.equals("DLES") || courseCategory.equals("FC")) {
						flag = 1;
					} else {
						if (PEUEStatus == 1) {
							flag = 1;
						} else if (studCompulsoryCourse.contains(courseCode)) {
							flag = 1;
						} else {
							flag = 2;
							msg = "Only core courses are allowed in this registration.";
						}
					}
				}
			} else {
				msg = "Invalid course code-2";
			}

			// Checking whether student is already registered or not
			if (flag == 1) {
				psRegList.clear();
				psRegList = courseRegistrationService.getRegistrationAndWLWithCEByRegisterNumberAndCourseCode(
						pSemesterSubId, pRegisterNumber, courseCode);
				if (psRegList.isEmpty()) {
					flag2 = 1;
				} else {
					switch (psRegList.get(0)[1].toString()) {
					case "REG":
						msg = "You had already registered this course.";
						break;
					case "CEREG":
						msg = "You have already registered this course under course equivalence ("
								+ psRegList.get(0)[2].toString() + " - " + psRegList.get(0)[3].toString() + ").";
						break;
					}
					flag2 = 2;
				}

				if (flag2 == 1) {
					psRegList.clear();
					psRegList = courseRegistrationService.getPrevSemCourseDetailWithCEByRegisterNumber(pRegisterNumber,
							courseCode);
					if (psRegList.isEmpty()) {
						flag2 = 1;
					} else {
						switch (psRegList.get(0)[1].toString()) {
						case "REG":
							msg = "This course was registered in " + psRegList.get(0)[4].toString()
									+ " and awaiting results to be declared. You are not permitted to register now.";
							break;
						case "CEREG":
							msg = "This course was registered in " + psRegList.get(0)[4].toString()
									+ " under course equivalence and awaiting results to be declared. You are not permitted to register now.";
							break;
						}
						flag2 = 2;
					}
				}

				// Regular Arrear Check - CR Rule : 7
				if (flag2 == 1) {
					psRegList.clear();
					psRegList = studentHistoryService.getArrearRegistrationWithCEByRegisterNumberAndCourseCode(
							pSemesterSubId, pRegisterNumber, courseCode);
					if (psRegList.isEmpty()) {
						flag2 = 1;
					} else {
						msg = "This course was registered in " + psRegList.get(0)[1].toString() + " "
								+ psRegList.get(0)[3].toString() + ". You are not permitted to register now.";
						flag2 = 2;
					}
				}

				if (flag2 == 1) {
					psRegList.clear();
					psRegList = studentHistoryService
							.getCourseChangeHistoryByRegisterNumberAndCourseCode2(pRegisterNumber, courseCode);
					if (psRegList.isEmpty()) {
						flag2 = 1;
					} else {
						msg = "This course had been already substituted with another course. You are not permitted to register. Check your grade history.";
						flag2 = 2;
					}
				}

				if (flag2 == 1) {
					psRegList.clear();
					psRegList = courseEquivalanceRegService.getByRegisterNumberAndCourseCode(pSemesterSubId,
							pRegisterNumber, Arrays.asList("CS", "CSUPE", "CSPUE", "CSUEC"), courseCode);
					if (psRegList.isEmpty()) {
						flag2 = 1;
					} else {
						msg = "You had already registered this course in a previous semester. You can’t register again";
						flag2 = 2;
					}
				}
			}

			// Checking whether the student is already studied or not
			if (flag2 == 1) {
				historyflag = 2;
				courseMehtodType = 1;
				shmList2.clear();

				shmList2 = studentHistoryService.getStudentHistoryGrade2(pRegisterNumber, courseCode);
				if (!shmList2.isEmpty()) {
					for (Object[] e : shmList2) {
						grade = e[0].toString();
						historyCourseId = e[1].toString();
						historyGenericCourseType = e[3].toString();
						historyExamMonth = e[4].toString();
						break;
					}

					if (allCompAllowFlag == 1) {
						historyCCM = courseCatalogService.getOne(historyCourseId);
						if (historyCCM != null) {
							historyCourseSystem = historyCCM.getCourseSystem();
						}
						if (historyCourseSystem.equals("FFCS")) {
							allCompAllowFlag = 2;
						}
					}
					historyflag = 1;
				}

				if (historyflag == 2) {
					shmList2.clear();
					shmList2 = studentHistoryService.getStudentHistoryCEGrade3(pRegisterNumber, courseCode);
					if (!shmList2.isEmpty()) {
						for (Object[] e : shmList2) {
							grade = e[0].toString();
							historyCourseId = e[1].toString();
							historyGenericCourseType = e[3].toString();
							historyExamMonth = e[4].toString();
							break;
						}

						if (allCompAllowFlag == 1) {
							historyCCM = courseCatalogService.getOne(historyCourseId);
							if (historyCCM != null) {
								historyCourseSystem = historyCCM.getCourseSystem();
							}
							if (historyCourseSystem.equals("FFCS")) {
								allCompAllowFlag = 2;
							}
						}
						historyflag = 1;
						courseMehtodType = 2;
					}
				}

				if (historyflag == 2) {
					shmList2.clear();
					shmList2 = courseRegistrationWithdrawService.getByRegisterNumberAndCourseCode2(pRegisterNumber,
							courseCode);
					if (!shmList2.isEmpty()) {
						for (Object[] e : shmList2) {
							grade = "W";
							courseMehtodType = Integer.parseInt(e[0].toString());
							historyCourseId = e[1].toString();
							historyGenericCourseType = e[3].toString();
							historyExamMonth = e[4].toString();
							break;
						}
						historyflag = 1;
					}
				}

				if (historyflag == 2) {
					shmList2.clear();
					shmList2 = courseRegistrationService.getCancelCourseByRegisterNumberAndCourseCode(pRegisterNumber,
							courseCode);
					if (!shmList2.isEmpty()) {
						for (Object[] e : shmList2) {
							grade = "CL";
							courseMehtodType = Integer.parseInt(e[0].toString());
							historyCourseId = e[1].toString();
							historyGenericCourseType = e[3].toString();
							historyExamMonth = e[4].toString();
							break;
						}
						historyflag = 1;
					}
				}

				if (historyflag == 1) {
					if ((grade.equals("S")) || (grade.equals("U")) || (grade.equals("P")) || (grade.equals("Pass"))) {
						msg = "You had completed this course with maximum grade already.";
					} else if ((grade.equals("A")) || (grade.equals("B")) || (grade.equals("C")) || (grade.equals("D"))
							|| (grade.equals("E"))) {
						if (giAllowStatus == 1) {
							if (pStudentGraduateYear <= academicGraduateYear) {
								courseOption = (courseMehtodType == 2) ? "GICE" : "GI";
								flag3 = 1;
							} else {
								giCount = courseRegistrationService
										.getGICourseCountByRegisterNumberCourseOptionAndClassGroup(pSemesterSubId,
												pRegisterNumber, classGroupId);
								if (giCount == 0) {
									courseOption = (courseMehtodType == 2) ? "GICE" : "GI";
									flag3 = 1;
								} else {
									flag3 = 2;
									msg = "Not permitted to register for more than one grade improvement course.";
								}
							}
						} else {
							msg = "Registration under grade improvement is not permitted now.";
						}
					} else if ((grade.equals("F")) || (grade.equals("Fail"))) {
						if (NGradeAllowStatus == 1) {
							courseOption = (courseMehtodType == 2) ? "RRCE" : "RR";
							flag3 = 1;
						} else {
							flag3 = 2;
							msg = "Re-registration of courses is not permitted now.";
						}
					} else if (grade.equals("N") || grade.equals("N1") || grade.equals("N2") || grade.equals("N3")
							|| grade.equals("N4")) {
						if (NGradeAllowStatus == 1) {
							if (regularAllowStatus == 1) {
								courseOption = (courseMehtodType == 2) ? "RRCE" : "RR";
								flag3 = 1;
							} else {
								if (globalValues.getN2N4AllowStatus() == 1
										|| (studentHistoryService.getStudentHistoryNotAllowedGrade(pRegisterNumber,
												historyCourseId, historyExamMonth).isEmpty())) {
									courseOption = (courseMehtodType == 2) ? "RRCE" : "RR";
									flag3 = 1;
								} else {
									flag3 = 2;
									msg = "Courses with N2 and N4 grade are not permitted for re-registration.";
								}
							}
						} else {
							flag3 = 2;
							msg = "Re-registration of courses is not permitted now.";
						}
					} else if (grade.equals("W")) {
						courseOption = (courseMehtodType == 2) ? "RWCE" : "RGW";
						flag3 = 1;
					} else if ((grade.equals("WWW")) || (grade.equals("AAA"))) {
						if (NGradeAllowStatus == 1) {
							courseOption = (courseMehtodType == 2) ? "RRCE" : "RR";
							flag3 = 1;
						} else {
							flag3 = 2;
							msg = "Re-registration of courses is not permitted now.";
						}
					} else if (grade.equals("---")) {
						msg = "Not permitted to register because of your ReFAT application process.";
					} else if ((grade == null) || (grade.equals("")) || (grade.equals("-"))) {
						msg = "Registration is not allowed for credit transfer courses.";
					} else if (grade.equals("CL")) {
						courseOption = (courseMehtodType == 2) ? "RPCE" : "RGP";
						flag3 = 1;
					}
				} else {
					if ((courseOption.equals("RGR")) && (pProgramGroupCode.equals("RP"))) {
						courseOption = "RGP";
					} else if ((courseOption.equals("RGR")) && (!pProgramGroupCode.equals("IEP"))
							&& ((studStudySystem.equals("FFCS") && ccCourseSystem.equals("CAL"))
									|| (studStudySystem.equals("FFCS") && ccCourseSystem.equals("CBCS"))
									|| (studStudySystem.equals("CAL") && ccCourseSystem.equals("CBCS")))) {
						courseOption = "RGCE";
					}
					flag3 = 1;
				}

				if (flag3 == 1) {
					if ((!pProgramGroupCode.equals("IEP"))
							&& ((studStudySystem.equals("FFCS") && ccCourseSystem.equals("CAL"))
									|| (studStudySystem.equals("FFCS") && ccCourseSystem.equals("CBCS"))
									|| (studStudySystem.equals("CAL") && ccCourseSystem.equals("CBCS")))
							&& (courseOption.equals("RGCE") || courseOption.equals("RWCE")
									|| courseOption.equals("RPCE") || courseOption.equals("GI")
									|| courseOption.equals("GICE"))) {
						if (courseOption.equals("RGCE") || courseOption.equals("RWCE") || courseOption.equals("RPCE")) {
							shmList2.clear();
							shmList2 = semesterMasterService.getCourseEquivalanceListByCourseCode(courseCode);
							if (!shmList2.isEmpty()) {
								for (Object[] e : shmList2) {
									if (e[6].toString().equals("FFCS")) {
										historyCourseId = e[2].toString();
										historyGenericCourseType = e[4].toString();
										historyExamMonth = e[6].toString();
									} else if (e[2] != null) {
										historyCourseId = e[2].toString();
										historyGenericCourseType = e[4].toString();
										historyExamMonth = e[6].toString();
									} else {
										historyCourseId = e[0].toString();
										historyGenericCourseType = genericCoursetype;
										historyExamMonth = e[6].toString();
									}

									break;
								}
							}
						}

						if (!historyGenericCourseType.equals(genericCoursetype)) {
							// Generic type Validation if course type is not equal
							if (ccm.getCorequisite() == null || ccm.getCorequisite().isEmpty()) {
								flag3 = 2;
							} else {
								List<String> historyCourseTypeComponents = semesterMasterService
										.getCourseTypeComponentByGenericType(historyGenericCourseType);
								List<String> registerCourseTypeComponents = semesterMasterService
										.getCourseTypeComponentByGenericType(genericCoursetype);

								String[] coeqCourseCodes = ccm.getCorequisite().split(",");
								for (String coReqTemp : coeqCourseCodes) {
									String coReqGenType = courseCatalogService.findGenericTypeByCourseCode(coReqTemp);
									registerCourseTypeComponents.add(coReqGenType);
									regComponentFlag = 2;
								}

								List<String> histCEComponents = studentHistoryService
										.doGetFaildCourseByRegNoAndCourseId(pRegisterNumber, historyCourseId);

								if (histCEComponents != null && !pRegisterNumber.isEmpty()) {
									historyCourseTypeComponents = histCEComponents;
								}

								for (String tempCourseType : historyCourseTypeComponents) {
									String allowedCourseType = tempCourseType;
									switch (tempCourseType) {
									case "TH":
										allowedCourseType = "ETH";
										break;
									case "ETH":
										allowedCourseType = "TH";
										break;
									case "LO":
										allowedCourseType = "ELA";
										break;
									case "ELA":
										allowedCourseType = "LO";
										break;
									default:
										break;
									}

									if (!registerCourseTypeComponents.contains(allowedCourseType)) {
										flag3 = 2;
										msg = "Fresh-Registration or Grade Improvement is permitted only if all the component is similar or all components are matching.";
										break;
									}
								}
							}
						} else {
							// Generic type Validation if course type is equal

							List<String> histCEComponents = studentHistoryService
									.doGetPassedCourseByRegNoAndCourseId(pRegisterNumber, historyCourseId);
							if (histCEComponents != null && !histCEComponents.isEmpty()) {
								flag3 = 2;
								msg = "You have completed an equivalence course.";
							}

							if (ccm.getCorequisite() != null && !ccm.getCorequisite().isEmpty()) {
								regComponentFlag = 2;
							}
						}

					}

					if ((allCompAllowFlag == 1) && ((courseOption.equals("RR")) || (courseOption.equals("RRCE")))) {
						historyCourseTypeList.clear();

						if ((historyGenericCourseType.equals("ETLP")) || (historyGenericCourseType.equals("ETL"))
								|| (historyGenericCourseType.equals("ETP"))
								|| (historyGenericCourseType.equals("ELP"))) {
							historyCourseTypeList = studentHistoryService.getStudentHistoryFailComponentCourseType(
									pRegisterNumber, historyCourseId, historyExamMonth);
							if (historyCourseTypeList.isEmpty()) {
								historyCourseTypeList = semesterMasterService
										.getCourseTypeComponentByGenericType(historyGenericCourseType);
							}
						} else {
							historyCourseTypeList = semesterMasterService
									.getCourseTypeComponentByGenericType(historyGenericCourseType);
						}

						if (historyGenericCourseType.equals(genericCoursetype)) {
							courseTypeList = historyCourseTypeList;
						} else {

							List<String> crCourseType = new ArrayList<>();

							if (ccm.getCorequisite() != null) {
								String[] coeqCourseCodes = ccm.getCorequisite().split(",");
								if (coeqCourseCodes != null && coeqCourseCodes.length > 0) {
									for (String coReqTemp : coeqCourseCodes) {
										String coReqGenType = courseCatalogService
												.findGenericTypeByCourseCode(coReqTemp);
										crCourseType.add(coReqGenType);
										regComponentFlag = 2;
									}

								}
							}

							crCourseType.add(genericCoursetype);
							courseTypeList2 = historyCourseTypeList;

							if (courseTypeList2.size() > 0) {
								for (String rrCourseType : courseTypeList2) {
									if ((crCourseType.contains("ETLP")) && (historyGenericCourseType.equals("ETL"))
											&& ((rrCourseType.equals("ETH")) || (rrCourseType.equals("ELA")))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETLP"))
											&& (historyGenericCourseType.equals("ETP"))
											&& ((rrCourseType.equals("ETH")) || (rrCourseType.equals("EPJ")))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETLP"))
											&& (historyGenericCourseType.equals("ELP"))
											&& ((rrCourseType.equals("ELA")) || (rrCourseType.equals("EPJ")))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETLP"))
											&& (historyGenericCourseType.equals("TH")) && (rrCourseType.equals("TH"))) {
										courseTypeList.add("ETH");
									} else if ((crCourseType.contains("ETLP"))
											&& (historyGenericCourseType.equals("LO")) && (rrCourseType.equals("LO"))) {
										courseTypeList.add("ELA");
									} else if ((crCourseType.contains("ETL"))
											&& (historyGenericCourseType.equals("ETLP"))
											&& ((rrCourseType.equals("ETH")) || (rrCourseType.equals("ELA")))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETL"))
											&& (historyGenericCourseType.equals("ETP"))
											&& (rrCourseType.equals("ETH"))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETL"))
											&& (historyGenericCourseType.equals("ELP"))
											&& (rrCourseType.equals("ELA"))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETL")) && (historyGenericCourseType.equals("TH"))
											&& (rrCourseType.equals("TH"))) {
										courseTypeList.add("ETH");
									} else if ((crCourseType.contains("ETL")) && (historyGenericCourseType.equals("LO"))
											&& (rrCourseType.equals("LO"))) {
										courseTypeList.add("ELA");
									} else if ((crCourseType.contains("ETP"))
											&& (historyGenericCourseType.equals("ETLP"))
											&& ((rrCourseType.equals("ETH")) || (rrCourseType.equals("EPJ")))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETP"))
											&& (historyGenericCourseType.equals("ETL"))
											&& (rrCourseType.equals("ETH"))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETP"))
											&& (historyGenericCourseType.equals("ELP"))
											&& (rrCourseType.equals("EPJ"))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ETP")) && (historyGenericCourseType.equals("TH"))
											&& (rrCourseType.equals("TH"))) {
										courseTypeList.add("ETH");
									} else if ((crCourseType.contains("ELP"))
											&& (historyGenericCourseType.equals("ETLP"))
											&& ((rrCourseType.equals("ELA")) || (rrCourseType.equals("EPJ")))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ELP"))
											&& (historyGenericCourseType.equals("ETL"))
											&& (rrCourseType.equals("ELA"))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ELP"))
											&& (historyGenericCourseType.equals("ETP"))
											&& (rrCourseType.equals("EPJ"))) {
										courseTypeList.add(rrCourseType);
									} else if ((crCourseType.contains("ELP")) && (historyGenericCourseType.equals("LO"))
											&& (rrCourseType.equals("LO"))) {
										courseTypeList.add("ELA");
									} else if ((crCourseType.contains("TH"))
											&& (historyGenericCourseType.equals("ETLP"))
											&& (rrCourseType.equals("ETH"))) {
										courseTypeList.add("TH");
									} else if ((crCourseType.contains("TH")) && (historyGenericCourseType.equals("ETL"))
											&& (rrCourseType.equals("ETH"))) {
										courseTypeList.add("TH");
									} else if ((crCourseType.contains("TH")) && (historyGenericCourseType.equals("ETP"))
											&& (rrCourseType.equals("ETH"))) {
										courseTypeList.add("TH");
									} else if ((crCourseType.contains("LO"))
											&& (historyGenericCourseType.equals("ETLP"))
											&& (rrCourseType.equals("ELA"))) {
										courseTypeList.add("LO");
									} else if ((crCourseType.contains("LO")) && (historyGenericCourseType.equals("ETL"))
											&& (rrCourseType.equals("ELA"))) {
										courseTypeList.add("LO");
									} else if ((crCourseType.contains("LO")) && (historyGenericCourseType.equals("ELP"))
											&& (rrCourseType.equals("ELA"))) {
										courseTypeList.add("LO");
									} else {
										courseTypeList.clear();
										rrAllowFlag = 2;
										break;
									}
								}
							}
						}

						LOGGER.trace("\n courseTypeList: " + courseTypeList);

						if (rrAllowFlag == 1) {
							flag3 = 1;
						} else {
							flag3 = 2;
							msg = "Component mismatch. Your previous course components do not match the current course components.";
						}
					}

				}

			}

			// Checking the selected course is related to Student programme
			if (flag3 == 1) {
				if (historyflag == 1) {
					flag4 = 1;
				} else if (pProgramGroupId == courseProgId) {
					flag4 = 1;
				} else if ((cgpaProgGroupId == null) || (cgpaProgGroupId.equals(""))) {
					flag4 = 1;
				} else {
					msg = "You are not permitted to register for higher level courses.";
					int pgid;
					int cgpaCourseFlag = 2;

					if (courseAltProgId != null) {
						String[] pg = courseAltProgId.split("/");
						for (int i = 0; i < pg.length; i++) {
							pgid = Integer.parseInt(pg[i]);
							if (pgid == pProgramGroupId) {
								flag4 = 1;
								msg = "";
								break;
							}
						}
					}

					if (flag4 == 2) {
						String[] pg = cgpaProgGroupId.split("/");

						for (int i = 0; i < pg.length; i++) {
							pgid = Integer.parseInt(pg[i]);
							LOGGER.trace("\n Check Level 2==> pgid: " + pgid);
							if (pgid == courseProgId) {
								cgpaCourseFlag = 1;
								break;
							}
						}

						if (cgpaCourseFlag == 1) {
							if (cgpa >= 8) {
								flag4 = 1;
								msg = "";
							} else {
								flag4 = 2;
								msg = "You are not permitted to register for higher level courses due to CGPA requirements.";
							}
						} else {
							flag4 = 1;
							msg = "";
						}
					}
				}
			}

			// To check the credit limit
			if (flag4 == 1) {
				ncCourseList = programmeSpecializationCurriculumDetailService
						.getNCCourseByYearAndCCVersion(pProgramSpecId, pStudentStartYear, pCurriculumVersion);
				regCredit = courseRegistrationService.getRegCreditByRegisterNumber(pSemesterSubId, pRegisterNumber);
				totalRegCredit = regCredit + wlCredit;

				if (courseTypeList.size() > 0) {
					for (String courseType3 : courseTypeList) {
						if (courseType.equals("NONE")) {
							courseType = courseType3;
						} else {
							courseType = courseType + "," + courseType3;
						}

						if (courseType3.equals("ETH")) {
							totalRegCredit = totalRegCredit + lectureCredit;
						} else if (courseType3.equals("ELA")) {
							totalRegCredit = totalRegCredit + practicalCredit;
						} else if (courseType3.equals("EPJ")) {
							totalRegCredit = totalRegCredit + projectCredit;
						} else {
							totalRegCredit = totalRegCredit + courseCredit;
						}

						crTpCount++;
					}
				} else {
					totalRegCredit = totalRegCredit + courseCredit;
				}

				if ((pStudentGraduateYear <= academicGraduateYear) && (maxCredit == 30) && (totalRegCredit <= 32)) {
					regAllowFlag = 1;
				} else if (totalRegCredit <= maxCredit) {
					regAllowFlag = 1;
				}

				if (AppGlobalValues.MAX_CREDIT_CHECK_REQUIRED) {
					if ((!pProgramGroupCode.equals("RP")) && (!pProgramGroupCode.equals("MBA"))
							&& (!pProgramGroupCode.equals("MBA5")) && (pStudentGraduateYear <= academicGraduateYear)
							&& (maxCredit == 30) && ((regCredit + wlCredit) < 30) && (totalRegCredit <= 32)) {
						flag5 = 1;
					} else if (totalRegCredit <= maxCredit) {
						flag5 = 1;
					} else {
						flag5 = 2;
						msg = "You can’t register beyond the permitted credit limits " + maxCredit;
					}
				} else {
					regAllowFlag = 1;
					flag5 = 1;
				}

				if (flag5 == 1) {
					flag5 = 2;

					if (courseOption.equals("RGR") || courseOption.equals("RGP") || courseOption.equals("RGCE")
							|| courseOption.equals("RGA") || courseOption.equals("RPCE") || courseOption.equals("HON")
							|| courseOption.equals("MIN") || courseOption.equals("AUD") || courseOption.equals("RGW")
							|| courseOption.equals("RWCE") || courseOption.equals("RPEUE")
							|| courseOption.equals("RDEOE") || courseOption.equals("RSEOE")
							|| courseOption.equals("RUCUE") || courseOption.equals("DM") || courseOption.equals("RWVC")
							|| courseOption.equals("RUEPE") || courseOption.equals("RGVC")) {
						rgrOptionFlag = 1;

						if (regularAllowStatus == 1) {
							flag5 = 1;
						} else {
							msg = "Regular courses are not allowed this semester.";
						}
					} else {
						flag5 = 1;
					}
				}

				if (flag5 == 1) {
					flag5 = 2;

					if ((rgrOptionFlag == 1) && (studCompulsoryCourse.contains(courseCode))) {
						compCourseStatus = compulsoryCoursePriorityCheck(pProgramGroupId, pStudentStartYear,
								pStudentGraduateYear, pSemesterId, pSemesterSubId, pRegisterNumber, classGroupId,
								classType, pProgramSpecCode, pProgramSpecId, pProgramGroupCode, pOldRegisterNumber,
								studCompulsoryCourse, costCentreCode, courseCode, pCourseSystem, ccm.getCorequisite());
						if (compCourseStatus.equals("NONE") || compCourseStatus.equals("SUCCESS")) {
							flag5 = 1;
						} else {
							msg = compCourseStatus;
						}
					} else {
						flag5 = 1;
					}
				}
			}

			// To check the Anti-requisite
			if (flag5 == 1) {
				if ((pProgramGroupCode.equals("RP")) || (pProgramGroupCode.equals("IEP"))) {
					flag6 = 1;
				} else if (historyflag == 1) {
					flag6 = 1;
				} else if (antirequisite.length <= 0) {
					flag6 = 1;
				} else {
					spsRegList.clear();
					shmList.clear();

					shmList = studentHistoryService.getStudentHistoryPARequisite(pRegisterNumber, antirequisite);
					if (shmList.isEmpty()) {
						spsRegList = courseRegistrationService.getPrevSemCourseRegistrationPARequisiteByRegisterNumber(
								pSemesterSubId, pRegisterNumber, Arrays.asList(antirequisite));
						if (spsRegList.isEmpty()) {
							flag6 = 1;
						} else {
							msg = "You have already registered or completed the Anti-Requisite course(s) of this course.";
						}
					} else {
						msg = "You have already registered or completed the Anti-Requisite course(s) of this course.";
					}
				}
			}

			// To check the Pre-requisite -->PREREQ
			if (flag6 == 1) {
				if ((pProgramGroupCode.equals("RP")) || (pProgramGroupCode.equals("IEP"))) {
					flag7 = 1;
				} else if (historyflag == 1) {
					flag7 = 1;
				} else if ((prerequisite == null) || prerequisite.equals("") || prerequisite.equals("NONE")) {
					flag7 = 1;
				} else {
					int prereqflag = 2, eptPrFlag = 2, pcmbPrFlag = 2;
					String prCourseCode = "", eptResult = "NONE", pcmbStatus = "NONE";
					String[] prerequisite2 = {};
					String[] prerequisite3 = {};
					ceList = new ArrayList<String>();

					prerequisite2 = prerequisite.split("/");
					for (int i = 0; i < prerequisite2.length; i++) {
						prCourseCode = "";
						ceList.clear();
						shmList.clear();
						spsRegList.clear();

						prerequisite3 = prerequisite2[i].split(",");

						for (int j = 0; j < prerequisite3.length; j++) {
							prCourseCode = prerequisite3[j].trim();

							if (prCourseCode.equals("EPT")) {
								eptPrFlag = 1;
							} else if ((prCourseCode.equals("PCMB")) || (prCourseCode.equals("PCMC"))
									|| (prCourseCode.equals("PCM")) || (prCourseCode.equals("PCB"))
									|| (prCourseCode.equals("PCBE")) || (prCourseCode.equals("PCME"))) {
								pcmbPrFlag = 1;
							}

							ceList.add(prCourseCode);
						}

						if (eptPrFlag == 1) {
							eptResult = semesterMasterService.getEPTResultByRegisterNumber(pRegisterNumber);
							if ((eptResult == null) || (eptResult.equals(""))) {
								eptResult = "NONE";
							}
						}

						if (pcmbPrFlag == 1) {
							pcmbStatus = semesterMasterService.getPCMBStatusByRegisterNumber(pRegisterNumber);
							if ((pcmbStatus == null) || (pcmbStatus.equals("")) || (pcmbStatus.equals("NONE"))) {
								pcmbStatus = semesterMasterService
										.getPCMBStatusFromAdmissionsByRegisterNumber(pRegisterNumber);
							}
							if ((pcmbStatus == null) || (pcmbStatus.equals(""))) {
								pcmbStatus = "NONE";
							}
						}

						Set<String> studiedList = new HashSet<>();

						shmList = studentHistoryService.getStudentHistoryPARequisite2(pRegisterNumber, ceList);

						if (shmList.isEmpty()) {
							if ((eptPrFlag == 1) && (eptResult.equals("P"))) {
								prereqflag = 1;
							} else if ((pcmbPrFlag == 1) && (!pcmbStatus.equals("NONE"))) {
								for (String pcmbs : ceList) {
									if (pcmbs.equals(pcmbStatus)) {
										prereqflag = 1;
										break;
									}
								}
							} else {

								spsRegList = courseRegistrationService
										.getPrevSemCourseRegistrationPARequisiteByRegisterNumber(pSemesterSubId,
												pRegisterNumber, ceList);

								if (ceList.size() == spsRegList.size()) {
									prereqflag = 1;
									break;
								}
							}
						} else {

							for (StudentHistoryModel histCourse : shmList) {
								studiedList.add(histCourse.getCourseCode());
							}

							spsRegList = courseRegistrationService
									.getPrevSemCourseRegistrationPARequisiteByRegisterNumber(pSemesterSubId,
											pRegisterNumber, ceList);

							studiedList.addAll(spsRegList);

							if (ceList.size() == studiedList.size()) {
								prereqflag = 1;
								break;
							}

						}

					}

					if (prereqflag == 1) {
						flag7 = 1;
					} else {
						msg = "You have not completed the required Pre-Requisite course.";
					}
				}
			}

			// TARP Course Eligibility Checking
			if (flag7 == 1) {
				int tarpCeilCdtper = 0, tarpPercentage = 65;
				float studTarpCredits = 0, psRegCredit = 0, tarpCdtRequired = 0, tarpCdtPer = 0;

				if (evaluationType.equals("TARP")) {
					if (historyflag == 1) {
						flag8 = 1;
					} else if ((pProgramGroupCode.equals("BTECH") || pProgramGroupCode.equals("MTECH5"))
							&& (cclTotalCredit > 0)) {
						psRegCredit = courseRegistrationService
								.getPreviousSemesterCreditByRegisterNumber(pRegisterNumber);
						LOGGER.trace("\n totCdtEarn: " + totCdtEarn + " | psRegCredit: " + psRegCredit);

						studTarpCredits = totCdtEarn + psRegCredit;
						tarpCdtRequired = ((float) cclTotalCredit * (float) tarpPercentage) / 100;
						tarpCdtPer = ((float) studTarpCredits / (float) cclTotalCredit) * 100;
						tarpCeilCdtper = (int) Math.ceil(tarpCdtPer);
						LOGGER.trace("\n studTarpCredits: " + studTarpCredits + " | tarpCdtRequired: " + tarpCdtRequired
								+ " | tarpCdtPer: " + tarpCdtPer + " | tarpCeilCdtper: " + tarpCeilCdtper);

						if (tarpCeilCdtper >= tarpPercentage) {
							flag8 = 1;
						} else {
							msg = "You have not earned the minimum credits required to register for this course. Required Credits: "
									+ tarpCdtRequired + " (" + tarpPercentage + "%) to "
									+ "take this TARP course.  Whereas, you earned " + studTarpCredits + " ("
									+ tarpCeilCdtper + "%) only." + "\nDescription of your total credits:  Earned = "
									+ totCdtEarn + " | Result Not Published = " + psRegCredit + ".";
						}
					} else {
						msg = "You are not eligible to register for TARP course.";
					}
				} else {
					flag8 = 1;
				}
			}

			// To check the eligibility of final project course
			if (flag8 == 1) {
				if ((genericCoursetype.equals("PJT")) && (evaluationType.equals("CAPSTONE"))) {
					int cspeFlag = 2, cspeFlag2 = 2;
					Integer ceilCdtper = 0, maxPjtYear = 0;
					Float cdtRequired = 0F, cdtPer = 0F, totCredit = 0F, pjtPer = 0F, totPjtCdt = 0F, psRegCredit = 0F;
					

					if (historyflag == 1) {
						cspeFlag = 1;
					} else if (pStudentGraduateYear < academicGraduateYear) {
						cspeFlag = 1;
					} else if (pStudentGraduateYear == academicGraduateYear) {
						if (((pSemesterId == 1) || (pSemesterId == 2) || (pSemesterId == 3))
								&& (pProgramGroupCode.equals("MTECH") || pProgramGroupCode.equals("MCA")|| pProgramGroupCode.equals("BTECH")
										|| (pProgramGroupCode.equals("MTECH5") && (pProgramSpecCode.equals("MIS")
												|| pProgramSpecCode.equals("MID") || pProgramSpecCode.equals("MTI")|| pProgramSpecCode.equals("MIC")|| pProgramSpecCode.equals("MIA")))
										|| (pProgramGroupCode.equals("MSC5")
												&& (pProgramSpecCode.equals("MSI") || pProgramSpecCode.equals("MIY")))
										|| (pProgramGroupCode.equals("MSC")))) {
							cspeFlag = 1;
						} else if ((pSemesterId == 5) || (pSemesterId == 6)) {
							cspeFlag = 1;
						} else {
							msg = "You are not eligible to register for the capstone project course now.";
						}
					} else {
						msg = "Only Timed out and final year students are permitted to register the Capstone project course.";
					}

					if (cspeFlag == 1) {
						cspeFlag = 2;

						if (cclTotalCredit > 0) {
							totCredit = (float) cclTotalCredit;
							pjtPer = 65F;
							cspeFlag = 1;
						} else {
							psRegList.clear();
							psRegList = semesterMasterService.getProjectEligibilityByProgramGroupIdAndStudYear(
									pProgramGroupId, pStudentStartYear);
							if (!psRegList.isEmpty()) {
								for (Object[] e : psRegList) {
									totCredit = Float.parseFloat(e[0].toString());
									pjtPer = Float.parseFloat(e[1].toString());
									cspeFlag = 1;
									break;
								}
							} else {
								psRegList.clear();
								psRegList = semesterMasterService
										.getStudentMaxYearProjectEligibilityByProgramGroupId(pProgramGroupId);
								if (!psRegList.isEmpty()) {
									for (Object[] e : psRegList) {
										maxPjtYear = Integer.parseInt(e[0].toString());
										totCredit = Float.parseFloat(e[1].toString());
										pjtPer = Float.parseFloat(e[2].toString());
										break;
									}
								}
								LOGGER.trace(
										"\n maxPjtYear: " + maxPjtYear + " | pStudentStartYear: " + pStudentStartYear);

								if (pStudentStartYear > maxPjtYear) {
									cspeFlag = 1;
								} else {
									msg = "You do not satisfy the eligible criteria to register this Capstone project.";
								}
							}
						}
					}
					LOGGER.trace("\n totCredit: " + totCredit + " | pjtPer: " + pjtPer);

					if (cspeFlag == 1) {

						if (pProgramGroupCode.equals("MTECH") || pProgramGroupCode.equals("MCA")
								|| pProgramGroupCode.equals("MSC")) {
							cspeFlag2 = 1;
						} else if (totCdtReg > 0) {
							// failCdt =
							// Float.parseFloat(studentHistoryService.getStudentHistoryFailCourseCredits2(pRegisterNumber).toString());
							psRegCredit = courseRegistrationService
									.getPreviousSemesterCreditByRegisterNumber(pRegisterNumber);

							totPjtCdt = totCdtReg + psRegCredit;
							cdtRequired = ((float) totCredit * (float) pjtPer) / 100;
							cdtPer = ((float) totPjtCdt / (float) totCredit) * 100;
							ceilCdtper = (int) Math.ceil(cdtPer);
							LOGGER.trace("\n totPjtCdt: " + totPjtCdt + " | cdtRequired: " + cdtRequired + " | cdtPer: "
									+ cdtPer + " | pjtPer: " + pjtPer + " | ceilCdtper: " + ceilCdtper);

							if ((float) ceilCdtper >= (float) pjtPer) {
								cspeFlag2 = 1;
							} else {
								msg = "You did not meet the required credits " + cdtRequired + " (" + pjtPer + "%) to "
										+ "take this capstone project.  Whereas, your total credits are " + totPjtCdt
										+ " (" + ceilCdtper + "%) only.";
							}
						} else {
							msg = "You have not eligible to take capstone project course.";
						}
					}

					if ((cspeFlag == 1) && (cspeFlag2 == 1)) {
						flag9 = 1;
					}
				} else {
					flag9 = 1;
				}
			}

			// To check the Curriculum Credit
			if (flag9 == 1) {
				LOGGER.trace("\n rgrOptionFlag: " + rgrOptionFlag + " | pProgramGroupCode: " + pProgramGroupCode
						+ " | studStudySystem: " + studStudySystem + " | courseOption: " + courseOption
						+ " | courseCategory: " + courseCategory);

				if (rgrOptionFlag == 1) {
					if ((auditAllowStatus == 1) && (cgpa >= 8)) {
						audCount = courseRegistrationService.getCourseCountByRegisterNumberAndCourseOption(
								pSemesterSubId, pRegisterNumber, Arrays.asList("AUD"));
					}

					if ((adlAllowStatus == 1) || (peAdlAllowStatus == 1) || (ueAdlAllowStatus == 1)) {
						adlCount = courseRegistrationService.getCourseCountByRegisterNumberAndCourseOption(
								pSemesterSubId, pRegisterNumber, Arrays.asList("RGA"));
					}

					if ((pProgramGroupCode.equals("RP")) || (pProgramGroupCode.equals("IEP"))) {
						rgrAllowFlag = 1;
						flag10 = 1;
					} else if (programGroupMode.equals("Twinning")) {
						rgrAllowFlag = 1;
						flag10 = 1;
					} else if (studStudySystem.equals("NONFFCS") || studStudySystem.equals("FFCS")) {
						rgrAllowFlag = 1;

						if (optionNAStatus == 1) {
							if (courseOption.equals("RGR")) {
								if ((auditAllowStatus == 1) && (cgpa >= 8) && (audCount < 1)) {
									audAllowFlag = 1;
								}

								if ((adlAllowStatus == 1) && (adlCount < 1)) {
									adlAllowFlag = 1;
								}

								if ((minHonAllowStatus == 1) && (cgpa >= 8)) {
									psRegList.clear();
									psRegList = semesterMasterService
											.getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(1, "HON",
													pProgramGroupId, pProgramSpecId, courseCode, studStudySystem);
									if (!psRegList.isEmpty()) {
										honAllowFlag = 1;
									}

									psRegList.clear();
									psRegList = semesterMasterService
											.getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(1, "MIN",
													pProgramGroupId, pProgramSpecId, courseCode, studStudySystem);
									if (!psRegList.isEmpty()) {
										minAllowFlag = 1;
									}
								}
							}

							psRegList.clear();
							psRegList = studentHistoryService.getStudentHistoryCS2(pRegisterNumber, courseCode,
									studStudySystem, pProgramSpecId, pStudentStartYear, pCurriculumVersion,
									pSemesterSubId, courseCategory, courseOption, ccCourseId, 1);
							if (!psRegList.isEmpty()) {
								csAllowFlag = 1;
							}
						}

						if ((rgrAllowFlag == 1) || (csAllowFlag == 1) || (honAllowFlag == 1) || (minAllowFlag == 1)
								|| (adlAllowFlag == 1) || (audAllowFlag == 1)) {
							flag10 = 1;
						} else {
							msg = "Minimum credit requirements satisfied. You won’t be permitted to register. Check your curriculum and grade history.";
						}
					} else {

						Map<String, Map<String, Float>> ccCategoryMap = new HashMap<>();

						// ME Eligible Query--------------------------------------------------->

						Integer combineCreditMax = 0;
						Float dePlusOeObtcAndME = 0.0F;
						Float deObtc = 0.0F; // DE OBTAINED CREDITS FOR ME
						Float deMinCredit = 0.0F;
						Map<String, Map<String, Integer>> splztnCredits = programmeSpecializationCurriculumCreditService
								.doGetCurriculumCredits(pProgramSpecId, pStudentStartYear);

						ccCreditList = programmeSpecializationCurriculumCreditService
								.getCurrentSemRegCurCtgCreditByRegisterNo(pProgramSpecId, pStudentStartYear,
										pCurriculumVersion, pSemesterSubId, pRegisterNumber, courseCategory,
										studStudySystem);
						if (!ccCreditList.isEmpty()) {

							for (Object[] e : ccCreditList) {
								if (e[0].toString().equals(courseCategory)) {
									ctgCredit = Float.parseFloat(e[1].toString());
									obtCredit = Float.parseFloat(e[6].toString());
									rmgCredit = Float.parseFloat(e[7].toString());

									break;
								}
							}

							for (Object[] e : ccCreditList) {
								Map<String, Float> insideMap = new HashMap<>();
								insideMap.put("OBTC", Float.parseFloat(e[6].toString())); // Obtained Credit
								insideMap.put("REMC", Float.parseFloat(e[7].toString())); // Remaining Credit
								insideMap.put("CTOTALC", Float.parseFloat(e[1].toString())); // Category Total Credit

								ccCategoryMap.put(e[0].toString(), insideMap);

							}

							ueRmgCredit = ccCategoryMap.containsKey("UE") ? ccCategoryMap.get("UE").get("REMC") : 0;
							oeRmgCredit = ccCategoryMap.containsKey("OE") ? ccCategoryMap.get("OE").get("REMC") : 0;

							if (splztnCredits != null && !splztnCredits.isEmpty()) {

								combineCreditMax = splztnCredits.get("COMCREDIT").get("MAX") + 2;

								dePlusOeObtcAndME = ccCategoryMap.get("DE").get("OBTC")
										+ ccCategoryMap.get("OE").get("OBTC") + ccCategoryMap.get("ME").get("OBTC");

								deObtc = ccCategoryMap.get("DE").get("OBTC");
								deMinCredit = splztnCredits.get("DE").get("MIN").floatValue();
							}

							// ---------------------------------------------------------------------------------------------------------->

							LOGGER.trace("\n courseCategory: " + courseCategory + " | catalogType: " + catalogType
									+ " | ccCourseId: " + ccCourseId + " | basketCategory: " + basketCategory
									+ " | ccCredit: " + ccCredit);
							LOGGER.trace("\n ctgCredit: " + ctgCredit + " | obtCredit: " + obtCredit + " | rmgCredit: "
									+ rmgCredit + " | ueRmgCredit: " + ueRmgCredit + " | oeRmgCredit: " + oeRmgCredit);
						}

						if (!ccCreditList.isEmpty()) {
							psRegList.clear();
							psRegList = compulsoryCourseConditionDetailService.getByCourseId(pSemesterSubId,
									pProgramGroupId, pStudentStartYear, courseCode);
							if (!psRegList.isEmpty()) {
								if (studCompulsoryCourse.contains(courseCode)) {
									compCourseFlag = 2;
								} else {
									msg = "This course " + courseCode
											+ " is Compulsory and you are not eligible to register here.";
								}
							} else {
								compCourseFlag = 3;
							}
						}

						if ((!ccCreditList.isEmpty()) && ((compCourseFlag == 2) || (compCourseFlag == 3))) {
							if (courseCategory.equals("PC") || courseCategory.equals("UC")) {
								if ((genericCoursetype.equals("PJT")) && (evaluationType.equals("CAPSTONE"))) {
									rgrAllowFlag = 1;
								} else if (courseCategory.equals("UC") && catalogType.equals("BC")) {
									if (basketCategory.equals("LANGUAGE")) {
										bskObtCredit = programmeSpecializationCurriculumCreditService
												.getBasketCtgCreditByRegisterNo(pSemesterSubId, pRegisterNumber,
														ccCourseId);
										LOGGER.trace("\n bskObtCredit: " + bskObtCredit);

										if ((bskObtCredit + courseCredit) <= ccCredit) {
											rgrAllowFlag = 1;
										} else if ((compCourseFlag == 3) && (optionNAStatus == 1)
												&& ((bskObtCredit + courseCredit) > ccCredit) && (ueRmgCredit > 0)) {
											RUCUEAllowFlag = 1;
										} else {
											if (optionNAStatus == 1) {
												msg = "Minimum credit requirements satisfied in UE. You won’t be permitted to register. Check your curriculum and grade history.";
											} else {
												msg = "Minimum credit requirements satisfied. You won’t be permitted to register. Check your curriculum and grade history.";
											}
										}
									} else if (((obtCredit + courseCredit) <= ctgCredit) && (rmgCredit > 0)) {
										rgrAllowFlag = 1;
									}

									if ((compCourseFlag == 3) && (optionNAStatus == 1)) {
										psRegList.clear();
										psRegList = studentHistoryService.getStudentHistoryCS2(pRegisterNumber,
												courseCode, studStudySystem, pProgramSpecId, pStudentStartYear,
												pCurriculumVersion, pSemesterSubId, courseCategory, courseOption,
												ccCourseId, 1);
										if (!psRegList.isEmpty()) {
											csAllowFlag = 1;
										}
									}

									if ((rgrAllowFlag == 2) && (csAllowFlag == 2) && (RUCUEAllowFlag == 2)
											&& (!msg.equals(""))) {
										msg = "Your credit limit exceeds if you choose this course under this "
												+ courseCategory + " category.  Check your registration details and "
												+ " grade history for more details. ";

									}
								} else if (((obtCredit + courseCredit) <= ctgCredit) && (rmgCredit > 0)) {
									rgrAllowFlag = 1;
								} else {
									if (rmgCredit <= 0) {
										msg = "You are not permitted to register in this category since you have "
												+ " already registered or completed the required credits.";
									} else {
										msg = "Selected course " + courseCode + " credit is exceeding over " + ctgCredit
												+ " credit under " + courseCategory + " category "
												+ "(i.e. Result Published: " + obtCredit + " | Course Credit: "
												+ courseCredit + ").  So, not allowed to register.";
									}
								}
							} else if (courseCategory.equals("PE")) {
								if ((rmgCredit > 0) || studCompulsoryCourse.contains(courseCode)) {
									rgrAllowFlag = 1;
								} else {
									// Honors Eligibility Check
									if ((compCourseFlag == 3) && (optionNAStatus == 1) && (cgpa >= 8)) {
										psRegList.clear();
										psRegList = semesterMasterService
												.getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(1,
														"HON", pProgramGroupId, pProgramSpecId, courseCode,
														studStudySystem);
										if (!psRegList.isEmpty()) {
											honAllowFlag = 1;
										}
									}

									// Audit Eligibility Check
									if ((compCourseFlag == 3) && (auditAllowStatus == 1) && (cgpa >= 8)
											&& (audCount < 1)) {
										audAllowFlag = 1;
									}

									// Additional Eligibility Check
									if ((compCourseFlag == 3) && (peAdlAllowStatus == 1) && (adlCount < 1)) {
										adlAllowFlag = 1;
									}

									// RPEUE Eligibility Check
									if ((compCourseFlag == 3) && (rmgCredit <= 0) && (ueRmgCredit > 0)) {
										RPEUEAllowFlag = 1;
									}
								}

								// Course Substitution Eligibility Check
								if ((compCourseFlag == 3) && (optionNAStatus == 1)) {
									psRegList.clear();
									psRegList = studentHistoryService.getStudentHistoryCS2(pRegisterNumber, courseCode,
											studStudySystem, pProgramSpecId, pStudentStartYear, pCurriculumVersion,
											pSemesterSubId, courseCategory, courseOption, ccCourseId, 1);
									if (!psRegList.isEmpty()) {
										csAllowFlag = 1;
									}
								}
							} else if (courseCategory.equals("UE")) {
								if ((rmgCredit > 0) || studCompulsoryCourse.contains(courseCode)) {
									rgrAllowFlag = 1;
									flag10 = 1;
								} else {
									// Minors Eligibility Check
									if ((compCourseFlag == 3) && (minHonAllowStatus == 1) && (cgpa >= 8)) {
										psRegList.clear();
										psRegList = semesterMasterService
												.getAdditionalLearningTitleByLearnTypeGroupIdSpecIdAndCourseCode(1,
														"MIN", pProgramGroupId, pProgramSpecId, courseCode,
														studStudySystem);
										if (!psRegList.isEmpty()) {
											minAllowFlag = 1;
										}
									}

									// Audit or Additional Eligibility Check
									if (compCourseFlag == 3) {
										if ((auditAllowStatus == 1) && (cgpa >= 8) && (audCount < 1)) {
											audAllowFlag = 1;
										}

										if ((ueAdlAllowStatus == 1) && (adlCount < 1)) {
											adlAllowFlag = 1;
										}
									}
								}

								// Course Substitution Eligibility Check
								if ((compCourseFlag == 3) && (optionNAStatus == 1)) {
									psRegList.clear();
									psRegList = studentHistoryService.getStudentHistoryCS2(pRegisterNumber, courseCode,
											studStudySystem, pProgramSpecId, pStudentStartYear, pCurriculumVersion,
											pSemesterSubId, courseCategory, courseOption, ccCourseId, 1);
									if (!psRegList.isEmpty()) {
										csAllowFlag = 1;
									}
								}
							} else if (courseCategory.equals("BC")) {
								msg = "Bridge course registration is not permitted.";
							} else if (minHonAllowStatus == 1
									&& (registrationOption.equals("CBCSMIN") || registrationOption.equals("ACEMIN"))) {
								if (antirequisite != null && antirequisite.length > 0) {
									List<Object[]> antiReqCur = programmeSpecializationCurriculumDetailService
											.findCurriculumByAdmsnYearCCVersionAndCourseCode(pProgramSpecId,
													pStudentStartYear, pCurriculumVersion, antirequisite[0].split("/"));
									if (antiReqCur != null && antiReqCur.size() > 0) {
										msg = "Similar course available as Core in your curriculum, hence not permitted to register under Minor.";
									} else {
										minAllowFlag = 1;
									}
								} else {
									minAllowFlag = 1;
								}
							} else if (minHonAllowStatus == 1 && registrationOption.equals("CBCSHON")) {
								if (antirequisite != null && antirequisite.length > 0) {
									List<Object[]> antiReqCur = programmeSpecializationCurriculumDetailService
											.findCurriculumByAdmsnYearCCVersionAndCourseCode(pProgramSpecId,
													pStudentStartYear, pCurriculumVersion, antirequisite[0].split("/"));
									if (antiReqCur != null && antiReqCur.size() > 0) {
										msg = "Similar course available as Core in your curriculum, hence not permitted to register under Honour.";
									} else {
										honAllowFlag = 1;
									}
								} else {
									honAllowFlag = 1;
								}
							} else if (ctgCredit > 0) {
								if (studCompulsoryCourse.contains(courseCode)) {
									rgrAllowFlag = 1;
								} else if (catalogType.equals("BC")) // Basket Course Eligiblity
								{
									bskObtCredit = programmeSpecializationCurriculumCreditService
											.getBasketRegisterCreditByRegisterNumber(pSemesterSubId, pRegisterNumber,
													ccCourseId);
									LOGGER.trace("\n bskObtCredit: " + bskObtCredit);
									if (((bskObtCredit + courseCredit) <= ccCredit) && (rmgCredit > 0)) {
										rgrAllowFlag = 1;
									} else {

										if ((courseCategory.equals("FC") || courseCategory.equals("FCHSSM"))
												&& AppGlobalValues.CBCS_FC_BASKET_IDS_CS.contains(ccCourseId)) {
											// Course Substitution Eligibility Check
											if ((compCourseFlag == 3) && (optionNAStatus == 1)) {
												psRegList.clear();
												psRegList = studentHistoryService.getStudentHistoryCS2(pRegisterNumber,
														courseCode, studStudySystem, pProgramSpecId, pStudentStartYear,
														pCurriculumVersion, pSemesterSubId, courseCategory,
														courseOption, ccCourseId, 1);
												if (!psRegList.isEmpty()) {
													csAllowFlag = 1;
												}
											}
										} else {

											msg = "You already completed the " + basketCategory
													+ " basket credit limit " + ccCredit + " under " + courseCategory
													+ " category.";
										}

									}
								} else if (studStudySystem.equals("CBCS") && courseCategory.equals("ME")) {
									if (/*deMinCredit*/ 9 > deObtc) {
										msg = "Minimum credits requirement is not satisfied in  "
												+ splztnCredits.get("DE").get("MIN")
												+ " DE category.  You are not permitted to register.";
									} else if (dePlusOeObtcAndME >= combineCreditMax) {
										msg = "You are not permitted to register since you have already "
												+ " earned or registered the required number of credits in DE, OE or ME category.";
									} else {
										rgrAllowFlag = 1;
									}

								} else if (excessCreditAllowedCategories != null
										&& excessCreditAllowedCategories.contains(courseCategory)) {

									if (splztnCredits.containsKey(courseCategory) && rmgCredit > 0) {
										if (dePlusOeObtcAndME >= combineCreditMax) {
											msg = "You are not permitted to register since you have already earned or registered the required number of credits in DE, OE or ME category.";
											rgrAllowFlag = 2;
										} else {
											
											
											
											//new code chennai campus winsem2025-26
//											if (courseCategory.equals("DE") && (obtCredit >= 9) && oeRmgCredit >0) {
//												RDEOEAllowFlag = 1;
//												rgrAllowFlag = 1;
//											}else if (courseCategory.equals("DE") && (obtCredit >= ctgCredit)&& oeRmgCredit >0){
//												RDEOEAllowFlag = 1;
//											}else {
//												rgrAllowFlag = 1;
//											}
											
											//System.out.println("obtCredit==>"+obtCredit);
											//System.out.println("ctgCredit==>"+ctgCredit);
											if (courseCategory.equals("DE") && (rmgCredit <= 0) && oeRmgCredit > 0) {
												RDEOEAllowFlag = 1;
											} else {
												rgrAllowFlag = 1;
											}

										}
									} else {
										if ((studStudySystem.equals("CBCS") && (courseCategory.equals("DE")
												|| (studStudySystem.equals("CBCS") && courseCategory.equals("OE"))))) {
											// Course Substitution Eligibility Check
											if ((compCourseFlag == 3) && (optionNAStatus == 1)) {
												psRegList.clear();
												psRegList = studentHistoryService.getStudentHistoryCS2(pRegisterNumber,
														courseCode, studStudySystem, pProgramSpecId, pStudentStartYear,
														pCurriculumVersion, pSemesterSubId, courseCategory,
														courseOption, ccCourseId, 1);
												/*
												 * if (!psRegList.isEmpty()) { csAllowFlag = 1; } else if(rmgCredit>0) {
												 * rgrAllowFlag = 1; }
												 */

												// chennai code

												if (!psRegList.isEmpty() && rmgCredit > 0) {
													csAllowFlag = 1;
													rgrAllowFlag = 1;
												} else if (!psRegList.isEmpty()) {
													csAllowFlag = 1;
												} else if (rmgCredit > 0) {
													rgrAllowFlag = 1;
												}
											}
										} else if (rmgCredit > 0) {
											rgrAllowFlag = 1;
										}

										if (rmgCredit <= 0 && rgrAllowFlag != 1 && csAllowFlag != 1) // Over flow
																										// credits
										{
											// RDEOE Eligibility Check
											if (courseCategory.equals("DE") && (compCourseFlag == 3)
													&& (oeRmgCredit > 0)) {

												if (splztnCredits.containsKey(courseCategory)) {
													if (dePlusOeObtcAndME >= combineCreditMax) {
														msg = "You are not permitted to register since you have already earned or registered the required number of credits in DE, OE or ME category.";
													} else {
														RDEOEAllowFlag = 1;
													}
												} else {
													RDEOEAllowFlag = 1;
												}
											} else if (studStudySystem.equals("CBCS") && courseCategory.equals("SPE")
													&& (compCourseFlag == 3) && (oeRmgCredit > 0)) {
												RSEOEAllowFlag = 1;
											} else {
												msg = "You are not permitted to register since you have already earned the required number of credits";

											}
										} else {
											msg = "Selected course " + courseCode + " credit is exceeding over "
													+ ctgCredit + " credit under " + courseCategory + " category "
													+ "(i.e. Result Published: " + obtCredit + " | Course Credit: "
													+ courseCredit + ").  So, not allowed to register.";
										}

									}

								}
								// else if ((!catalogType.equals("BC")) && (rmgCredit > 0) && ((obtCredit +
								// courseCredit) <= ctgCredit))
								else if ((!catalogType.equals("BC")) && (rmgCredit > 0)) {

//									if((obtCredit + courseCredit) <= ctgCredit)
//									{
//										if(splztnCredits.containsKey(courseCategory))
//										{
//											if(dePlusOeObtcAndME>=combineCreditMax)
//											{
//												msg = "You are not permitted to register since you have already earned or registered the required number of credits in DE, OE or ME category.";
//											}
//											else
//											{
//												rgrAllowFlag = 1;
//											}
//										}
//										else
//										{
//											rgrAllowFlag = 1;
//										}
//									}
//									else 
//									{
//										rgrAllowFlag = 1;
//									}'CHECKING SP
									// System.out.println("CHECKING SPE");
									if ((obtCredit + courseCredit) <= (ctgCredit + 2)) {
										if (courseCategory.equals("SPE")) {

											if (splztnCredits.containsKey(courseCategory)) {
												if ((dePlusOeObtcAndME + courseCredit) > (combineCreditMax)) {
													msg = "You are not permitted to register since you have already earned or registered the required number of credits in DE, OE or ME category.";
												} else {

													if ((obtCredit >= ctgCredit) && oeRmgCredit > 0) {
														RSEOEAllowFlag = 1;
													} else {
														rgrAllowFlag = 1;
													}
												}
											} else {
												if ((obtCredit >= ctgCredit) && oeRmgCredit > 0) {
													RSEOEAllowFlag = 1;
												} else {
													rgrAllowFlag = 1;
												}
											}

											psRegList.clear();
											psRegList = studentHistoryService.getStudentHistoryCS2(pRegisterNumber,
													courseCode, studStudySystem, pProgramSpecId, pStudentStartYear,
													pCurriculumVersion, pSemesterSubId, courseCategory, courseOption,
													ccCourseId, 1);
											if (!psRegList.isEmpty()) {
												csAllowFlag = 1;
											}
										} else {
											rgrAllowFlag = 1;
										}
									}
								}
								// chennai code
								else if (courseCategory.equals("SPE") && rmgCredit <= 0) {
									psRegList = studentHistoryService.getStudentHistoryCS2(pRegisterNumber, courseCode,
											studStudySystem, pProgramSpecId, pStudentStartYear, pCurriculumVersion,
											pSemesterSubId, courseCategory, courseOption, ccCourseId, 1);
									if (!psRegList.isEmpty()) {
										csAllowFlag = 1;
									}
								}

							} else {
								rgrAllowFlag = 1;
							}

						} else if ((ccCreditList.isEmpty()) && ((compCourseFlag == 2) || (compCourseFlag == 3))) {
							msg = "Credit distribution in curriculum is not available for your specialization. You are not allowed to register.";
						}

						if ((rgrAllowFlag == 1) || (csAllowFlag == 1) || (honAllowFlag == 1) || (minAllowFlag == 1)
								|| (RUCUEAllowFlag == 1) || (RPEUEAllowFlag == 1) || (adlAllowFlag == 1)
								|| (audAllowFlag == 1) || (RDEOEAllowFlag == 1) || RSEOEAllowFlag == 1) {
							flag10 = 1;
						} else if ((msg == null) || msg.equals("")) {
							msg = "You are not permitted to register since you have already earned the required number of credits.";
						}
					}
				} else {
					flag10 = 1;
				}
			}
			// suga chennai code only one OC code allowed
			if (genericCoursetype.equals("OC")) {

				Integer nccount = courseRegistrationService.getncCourseCountByRegisterNumberCourseOptionAndClassGroup(
						pSemesterSubId, pRegisterNumber, classGroupId);

				if (nccount >= 1) {
					flag10 = 2;
					msg = "Only One Online Course Allowed.";
				}

			}

			if ((flag == 1) && (flag2 == 1) && (flag3 == 1) && (flag4 == 1) && (flag5 == 1) && (flag6 == 1)
					&& (flag7 == 1) && (flag8 == 1) && (flag9 == 1) && (flag10 == 1)) {
				// To assign the course equivalence registration detail value
				if ((courseOption.equals("RR")) || (courseOption.equals("RRCE")) || (courseOption.equals("GI"))
						|| (courseOption.equals("GICE")) || (courseOption.equals("RGCE"))
						|| (courseOption.equals("RWCE")) || (courseOption.equals("RPCE"))) {
					subCourseOption = historyCourseId;
					subCourseDate = historyExamMonth;

					if (courseType.equals("NONE")) {
						subCrTypeList = semesterMasterService
								.getCourseTypeComponentByGenericType(historyGenericCourseType);
						crTpCount = semesterMasterService.getCourseTypeComponentByGenericType(genericCoursetype).size();
					} else {
						subCrTypeList = historyCourseTypeList;
					}

					for (String e : subCrTypeList) {
						subCrType = "";
						if ((genericCoursetype.equals("ETLP") || genericCoursetype.equals("ETL")
								|| genericCoursetype.equals("ETP")) && (e.equals("ETH"))) {
							subCrType = "ETH";
						} else if ((genericCoursetype.equals("ETLP") || genericCoursetype.equals("ETL")
								|| genericCoursetype.equals("ETP")) && (e.equals("TH"))) {
							subCrType = "TH";
						} else if ((genericCoursetype.equals("ETLP") || genericCoursetype.equals("ETL")
								|| genericCoursetype.equals("ELP")) && (e.equals("ELA"))) {
							subCrType = "ELA";
						} else if ((genericCoursetype.equals("ETLP") || genericCoursetype.equals("ETL")
								|| genericCoursetype.equals("ELP")) && (e.equals("LO"))) {
							subCrType = "LO";
						} else if ((genericCoursetype.equals("ETLP") || genericCoursetype.equals("ETP")
								|| genericCoursetype.equals("ELP")) && (e.equals("EPJ"))) {
							subCrType = "EPJ";
						} else if ((genericCoursetype.equals("TH")) && (e.equals("ETH"))) {
							subCrType = "ETH";
						} else if ((genericCoursetype.equals("LO")) && (e.equals("ELA"))) {
							subCrType = "ELA";
						} else if ((genericCoursetype.equals("PJT")) && (e.equals("EPJ"))) {
							subCrType = "EPJ";
						} else if (genericCoursetype.equals(e)) {
							subCrType = e;
						} else {
							subCrType = historyGenericCourseType;
						}

						if (subCourseType.equals("NONE")) {
							subCourseType = subCrType;
						} else {
							subCourseType = subCourseType + "," + subCrType;
						}

						subCrCount++;
						if (subCrCount >= crTpCount) {
							break;
						}
					}

					// If history course component type is less than registered component type
					LOGGER.trace("\n rrAllowFlag: " + rrAllowFlag + " | subCrCount: " + subCrCount + " | crTpCount: "
							+ crTpCount);
					if ((rrAllowFlag == 1) && (subCrCount != crTpCount) && (subCrCount == (crTpCount - 1))) {
						subCourseType = subCourseType + ",NONE";
					} else if ((rrAllowFlag == 1) && (subCrCount != crTpCount) && (subCrCount == (crTpCount - 2))) {
						subCourseType = subCourseType + ",NONE,NONE";
					}
				}

				regflag = 1;
				msg = "Success";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.trace(ex);
		}

		// Generating the Authentication Key Value
		authKeyVal = generateCourseAuthKey(pRegisterNumber, pCourseId, regflag, 1);

		LOGGER.trace("\n Flag: " + flag + " | Flag2: " + flag2 + " | Flag3: " + flag3 + " | Flag4: " + flag4
				+ " | Flag5: " + flag5 + " | Flag6: " + flag6 + " | Flag7: " + flag7 + " | Flag8: " + flag8
				+ " | Flag9: " + flag9 + " | Flag10: " + flag10);

		LOGGER.trace("\n regflag: " + regflag + " / msg: " + msg + " / courseOption: " + courseOption
				+ " / regAllowFlag: " + regAllowFlag + " / wlAllowFlag: " + wlAllowFlag + " / ceCourseId: " + ceCourseId
				+ " / courseType: " + courseType + " / subCourseOption: " + subCourseOption + " / audAllowFlag: "
				+ audAllowFlag + " / subCourseType: " + subCourseType + " / subCourseDate: " + subCourseDate
				+ " / rgrAllowFlag: " + rgrAllowFlag + " / honAllowFlag: " + honAllowFlag + " / adlAllowFlag: "
				+ adlAllowFlag + " / authKeyVal: " + authKeyVal + " / RPEUEAllowFlag: " + RPEUEAllowFlag
				+ " / csAllowFlag: " + csAllowFlag + " / RUCUEAllowFlag: " + RUCUEAllowFlag + " / RDEOEAllowFlag: "
				+ RDEOEAllowFlag + " / RSEOEAllowFlag: " + RSEOEAllowFlag);

		return regflag + "/" + msg + "/" + courseOption + "/" + regAllowFlag + "/" + wlAllowFlag + "/" + ceCourseId
				+ "/" + courseType + "/" + subCourseOption + "/" + audAllowFlag + "/" + subCourseType + "/"
				+ subCourseDate + "/" + rgrAllowFlag + "/" + honAllowFlag + "/" + minAllowFlag + "/" + courseCategory
				+ "/" + adlAllowFlag + "/" + authKeyVal + "/" + RPEUEAllowFlag + "/" + csAllowFlag + "/"
				+ RUCUEAllowFlag + "/" + ccCourseId + "/" + RDEOEAllowFlag + "/" + RSEOEAllowFlag + "/"
				+ regComponentFlag;
	}

	// Checking slot clash
	public String checkClash(Integer patternId, List<String> clashSlotList, String pSemesterSubId,
			String pRegisterNumber, String pRegType, String pOldClassId, String prvSemesterSubId,
			List<String> prvNonClassGroupList) {
		int clashStatus = 2;
		String message = "NONE", slot = "";
		String[] clashStatusArray = new String[] {};

		List<String> tempStringList = new ArrayList<String>();
		List<Object[]> objectList = new ArrayList<Object[]>();
		Map<String, List<SlotTimeMasterModel>> slotTimeMapList = new HashMap<String, List<SlotTimeMasterModel>>();

		LOGGER.trace("\n patternId: " + patternId + " | clashSlotList: " + clashSlotList + " | pSemesterSubId: "
				+ pSemesterSubId + " | pRegisterNumber: " + pRegisterNumber + " | pRegType: " + pRegType
				+ " | pOldClassId: " + pOldClassId);

		if (clashSlotList.isEmpty()) {
			clashStatus = 1;
		} else if ((!clashSlotList.isEmpty()) && (pSemesterSubId != null) && (pRegisterNumber != null)) {
			// Get the Slot Time Master By Semester
			LOGGER.trace(LocalDateTime.now());
			slotTimeMapList = semesterMasterService
					.getSlotTimeMasterCommonTimeSlotBySemesterSubIdAsMap(Arrays.asList(pSemesterSubId));
			LOGGER.trace(LocalDateTime.now());
			// slotTimeMapList =
			// semesterMasterService.getSlotTimeMasterCommonTimeSlotBySemesterSubIdAsMap(Arrays.asList(pSemesterSubId,
			// prvSemesterSubId));

			// Get list of Registration Slots based on Adding or Modifying the Course
			if ((pRegType.equals("MODIFY")) && (!pOldClassId.equals("")) && (!pOldClassId.equals(null))) {
				// General
				objectList = courseRegistrationService.getRegisteredSlotsforUpdate2(pSemesterSubId, pRegisterNumber,
						pOldClassId);

			} else if ((pRegType.equals("EDIT")) && (!pOldClassId.isEmpty()) && (pOldClassId != null)) {

				CourseAllocationModel allocation = courseAllocationService.getOne(pOldClassId);

				// General
				objectList = courseRegistrationService.findRegisteredSlotsNotInCourseIdBySemesterAndRegNo(
						pSemesterSubId, pRegisterNumber, allocation.getCourseId());
			} else {
				// General
				objectList = courseRegistrationService.getRegisteredSlots2(pSemesterSubId, pRegisterNumber);

			}

			// Checking the clash with Registered Slot
			if (!objectList.isEmpty()) {
				for (String clhslt : clashSlotList) {
					clashStatus = 2;
					clashStatusArray = new String[] {};

					clashStatusArray = courseAllocationService
							.getClashStatus(patternId, clhslt, objectList, slotTimeMapList).split("\\|");
					clashStatus = Integer.parseInt(clashStatusArray[0].toString());
					slot = clashStatusArray[1].toString();

					if (clashStatus == 2) {
						message = "Selected slot clashed with " + slot + " slot or combination of " + slot
								+ " slots in Registered Course(s).";
						break;
					} else {
						clashStatus = 1;
					}
				}
			} else {
				clashStatus = 1;
			}

			// Getting Waiting List Slots & Checking Clash
			if (clashStatus == 1) {
				clashStatus = 2;
				objectList.clear();
				for (String clhslt : clashSlotList) {
					clashStatus = 2;
					clashStatusArray = new String[] {};

					clashStatusArray = courseAllocationService
							.getClashStatus(patternId, clhslt, objectList, slotTimeMapList).split("\\|");
					clashStatus = Integer.parseInt(clashStatusArray[0].toString());
					slot = clashStatusArray[1].toString();

					if (clashStatus == 2) {
						message = "Selected slot clashed with " + slot + " slot or combination of " + slot
								+ " slots in Waitlist Course(s).";
						break;
					} else {
						clashStatus = 1;
					}
				}
			}

			// Checking the selected slot for Embedded Course (i.e. ETH/ELA) & whether it is
			// clashed with each other
			if (clashStatus == 1) {
				clashStatus = 2;

				if (clashSlotList.size() >= 2) {
					tempStringList.clear();
					tempStringList.addAll(Arrays.asList(clashSlotList.get(1).split("/")));
					if (!tempStringList.isEmpty()) {
						for (String clhst : clashSlotList.get(0).split("/")) {
							clashStatus = 2;

							if (tempStringList.contains(clhst)) {
								message = "Chosen theory slot clashed with selected lab slot. Try again with new slot(s).";
								break;
							} else {
								clashStatus = 1;
							}
						}
					}
				} else {
					clashStatus = 1;
				}
			}
		}
		LOGGER.trace("\n Return=> clashStatus: " + clashStatus + " | message: " + message);

		return clashStatus + "/" + message;
	}

	// Checking the Soft Skill course
	public List<String> SoftSkillCourseCheck(Integer progGroupId, Integer studentBatch, Integer studentGradYear,
			String registerNumber, String progSpecCode, String progGroupCode, String semesterSubId) {
		List<String> ssCourseList = new ArrayList<String>();

		ssCourseList = compulsoryCourseConditionDetailService.getSoftSkillCourseList(semesterSubId, progGroupId,
				studentBatch);
		if (ssCourseList.isEmpty()) {
			ssCourseList.add("NIL");
		}

		return ssCourseList;
	}

	// Checking the compulsory course is registered or not & then return the status
	public int compulsoryCourseCheck(Integer progGroupId, Integer studentBatch, Integer studentGradYear,
			Integer semesterId, String semesterSubId, String registerNumber, String[] classGroupId, String[] classType,
			String progSpecCode, Integer progSpecId, String progGroupCode, String oldRegisterNumber,
			List<String> compulsoryCourse, String costCentreCode, String[] courseSystem) {
		int returnStatus = 2;
		String compCourseStatus = "NONE";

		compCourseStatus = compulsoryCoursePriorityCheck(progGroupId, studentBatch, studentGradYear, semesterId,
				semesterSubId, registerNumber, classGroupId, classType, progSpecCode, progSpecId, progGroupCode,
				oldRegisterNumber, compulsoryCourse, costCentreCode, "", courseSystem, "");
		if (!compCourseStatus.equals("NONE")) {
			returnStatus = 1;
		}
		LOGGER.trace("\n compCourseStatus: " + compCourseStatus + " | returnStatus: " + returnStatus);

		return returnStatus;
	}

	// Checking the compulsory course is registered as per priority - New Method
	public String compulsoryCoursePriorityCheck(Integer progGroupId, Integer studentBatch, Integer studentGradYear,
			Integer semesterId, String semesterSubId, String registerNumber, String[] classGroupId, String[] classType,
			String progSpecCode, Integer progSpecId, String progGroupCode, String oldRegisterNumber,
			List<String> compulsoryCourse, String costCentreCode, String regCourseCode, String[] courseSystem,
			String coReqCourseCode) {
		String compCourseStatus = "NONE";
		int checkCompFlag = 2, checkCompFlag2 = 2;

		List<Object[]> objectList = new ArrayList<>();
		List<String> registrationCourseList = new ArrayList<>();
		List<String> allocationCourseList = new ArrayList<>();

		if (!compulsoryCourse.isEmpty()) {
			objectList = courseRegistrationService.getCompulsoryCourseRegistrationAndAllocationStatus(semesterSubId,
					registerNumber, compulsoryCourse, classGroupId, classType, progGroupCode, progSpecCode,
					costCentreCode, courseSystem);
			if (!objectList.isEmpty()) {
				registrationCourseList = objectList.stream().filter(p -> p[0].toString().equals("REG"))
						.map(e -> e[1].toString()).distinct().collect(Collectors.toList());
				allocationCourseList = objectList.stream().filter(p -> p[0].toString().equals("ALLOT"))
						.map(e -> e[1].toString()).distinct().collect(Collectors.toList());
			}

			for (String crCode : compulsoryCourse) {
				checkCompFlag = 2;
				checkCompFlag2 = 2;

				// Checking in the Registration
				if (!registrationCourseList.contains(crCode)) {
					checkCompFlag = 1;
				}

				// Checking in Course Allocation whether Seat is available or not
				if (checkCompFlag == 1) {
					if (allocationCourseList.contains(crCode)) {
						checkCompFlag2 = 1;
					}
				}

				if ((checkCompFlag == 1) && (checkCompFlag2 == 1)) {
					if (crCode.equals(regCourseCode)) {
						compCourseStatus = "SUCCESS";
					}

					else {
						boolean coreqFound = false;
						List<String> coReqList = new ArrayList<>();
						if (coReqCourseCode != null) {
							coReqList = Arrays.asList(coReqCourseCode.replace(" ", "").split(","));
						}
						for (String coReqCode : coReqList) {
							if (crCode.equals(coReqCode)) {
								coreqFound = true;
							}
						}
						if (coreqFound) {
							compCourseStatus = "SUCCESS";
						} else {
							compCourseStatus = "Kindly register the compulsory course " + crCode + " as per priority.";
						}
					}
					break;
				}
			}
		}
		LOGGER.trace("\n compCourseStatus: " + compCourseStatus);

		return compCourseStatus;
	}

	public Integer findStudentSemester(String progGroupCode, Integer studentBatch) {
		Integer tempStudentSemester = 0;

		if (progGroupCode.equals("MBA") || progGroupCode.equals("MIB")) {
			if (studentBatch == 2020) {
				tempStudentSemester = 3;
			} else if (studentBatch == 2019) {
				tempStudentSemester = 6;
			} else if (studentBatch <= 2018) {
				tempStudentSemester = 7;
			}
		} else {
			if (studentBatch == 2020) {
				tempStudentSemester = 2;
			} else if (studentBatch == 2019) {
				tempStudentSemester = 4;
			} else if (studentBatch == 2018) {
				tempStudentSemester = 6;
			} else if (studentBatch == 2017) {
				tempStudentSemester = 8;
			} else if (studentBatch == 2016) {
				tempStudentSemester = 10;
			} else if (studentBatch <= 2015) {
				tempStudentSemester = 11;
			}
		}

		return tempStudentSemester;
	}

	public void callCaptcha(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)
			throws ServletException, IOException {
		int num = 0;
		String randomChars = "", res = "";
		CommonFunctions sdf = new CommonFunctions();

		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Max-Age", 0);
		response.setContentType("image/jpeg");

		randomChars = sdf.getWord();

		captchaManager.setHeight(40);
		captchaManager.setWidth(200);
		captchaManager.setForegroundColor(Color.BLUE);
		captchaManager.setBackgroundColor(new Color(249, 231, 159));

		num = new Random().nextInt(10);
		if (num <= 5) {
			captchaManager.setGridLines(true);
			captchaManager.setDrawLines(false);
		} else {
			captchaManager.setGridLines(false);
			captchaManager.setDrawLines(true);
		}
		res = captchaManager.createCaptcha(randomChars);

		session.setAttribute("CAPTCHA", randomChars);
		session.setAttribute("ENCDATA", res);
		model.addAttribute("res1", res);
	}

	public String callClassGroup(Integer pSemesterId, String pProgramGroupCode, String costCentreCode, int academicYear,
			int academicGraduateYear, int studentAdmissionYear, int studentGraduateYear) {
		String returnClassGroup = "";

		if ((pSemesterId == 4) || (pSemesterId == 8) || (pSemesterId == 9)) {
			if (pProgramGroupCode.equals("MBA") || pProgramGroupCode.equals("MBA5")) {
				returnClassGroup = "MBA";
			} else if (pProgramGroupCode.equals("RP") && costCentreCode.equals("VITBS")) {
				returnClassGroup = "MBA";
			} else {
				returnClassGroup = "ALL";
			}
		} else if ((pSemesterId == 7)) {
			returnClassGroup = "ST004";
		} else {
			returnClassGroup = "ALL";
		}

		return returnClassGroup;
	}

	// Generate Course Authorization Key
	public String generateCourseAuthKey(String registerNumber, String courseId, int validateStatus, int levelType) {
		String authKeyVal = "NONE";
		CommonFunctions scf = new CommonFunctions();

		if ((registerNumber == null) || registerNumber.equals("")) {
			registerNumber = "NONE";
		}

		if ((courseId == null) || courseId.equals("")) {
			courseId = "NONE";
		}

		if (levelType == 1) {
			authKeyVal = scf.Encrypt_String3(registerNumber + "_" + courseId + "_" + validateStatus + "_1",
					AppGlobalValues.KEY_LENGTH);
		} else if (levelType == 2) {
			authKeyVal = scf.Encrypt_String3(registerNumber + "_" + courseId + "_" + validateStatus + "_2",
					AppGlobalValues.KEY_LENGTH);
		}

		return authKeyVal;
	}

	// Validate the Course Authorization Key
	public int validateCourseAuthKey(String authorizationKey, String registerNumber, String courseId, int levelType) {
		int regValidFlag = 2;

		if ((authorizationKey != null) && (!authorizationKey.equals("")) && (!authorizationKey.equals("NONE"))
				&& (registerNumber != null) && (!registerNumber.equals("")) && (!registerNumber.equals("NONE"))
				&& (courseId != null) && (!courseId.equals("")) && (!courseId.equals("NONE"))) {
			CommonFunctions scf3 = new CommonFunctions();

			if (levelType == 1) {
				regValidFlag = scf3.String_Validate3(registerNumber + "_" + courseId + "_1_1", authorizationKey,
						AppGlobalValues.KEY_LENGTH);
			} else if (levelType == 2) {
				regValidFlag = scf3.String_Validate3(registerNumber + "_" + courseId + "_1_2", authorizationKey,
						AppGlobalValues.KEY_LENGTH);
			}
		}

		return regValidFlag;
	}

	// Generate Page Authorization Key
	public String generatePageAuthKey(String registerNumber, int levelType) {
		String authKeyValue = "NONE";
		CommonFunctions scf = new CommonFunctions();

		if ((registerNumber == null) || registerNumber.equals("")) {
			registerNumber = "NONE";
		}

		if (levelType == 1) {
			authKeyValue = scf.Encrypt_String3(registerNumber + "_1", AppGlobalValues.KEY_LENGTH);
		} else if (levelType == 2) {
			authKeyValue = scf.Encrypt_String3(registerNumber + "_1_1", AppGlobalValues.KEY_LENGTH);
		}

		return authKeyValue;
	}

	// Validate Page Authorization Key
	public int validatePageAuthKey(String authorizationKey, String registerNumber, int levelType) {
		int regValidFlag = 2;

		if ((authorizationKey != null) && (!authorizationKey.equals("")) && (!authorizationKey.equals("NONE"))
				&& (registerNumber != null) && (!registerNumber.equals("")) && (!registerNumber.equals("NONE"))) {
			CommonFunctions scf3 = new CommonFunctions();

			if (levelType == 1) {
				regValidFlag = scf3.String_Validate3(registerNumber + "_1", authorizationKey,
						AppGlobalValues.KEY_LENGTH);
			} else if (levelType == 2) {
				regValidFlag = scf3.String_Validate3(registerNumber + "_1_1", authorizationKey,
						AppGlobalValues.KEY_LENGTH);
			}
		}

		return regValidFlag;
	}

	// Registration Status
	public Integer getRegistrationStatus(Integer approvalStatus, String courseOption, String genericCourseType,
			String evaluationType, String studentCategory) {
		Integer tempRegistrationStatus = 0;
		LOGGER.trace("\n approvalStatus: " + approvalStatus + " | courseOption: " + courseOption
				+ " | genericCourseType: " + genericCourseType + " | evaluationType: " + evaluationType
				+ " | studentCategory: " + studentCategory);

		if (approvalStatus == 1) {
			if (courseOption.equals("RGR") || courseOption.equals("AUD") || courseOption.equals("RGCE")
					|| courseOption.equals("RPEUE") || courseOption.equals("RDEOE") || courseOption.equals("RSEOE")
					|| courseOption.equals("RUCUE") || courseOption.equals("RGVC")) {
				if ((genericCourseType.equals("PJT"))
						&& (evaluationType.equals("CAPSTONE") || evaluationType.equals("GUIDE"))) {
					tempRegistrationStatus = 7;
				} else {
					tempRegistrationStatus = 10;
				}
			} else {
				if (genericCourseType.equals("PJT") && evaluationType.equals("CAPSTONE")) {
					tempRegistrationStatus = 0;
				} else {
					if (studentCategory.equals("STARS")) {
						tempRegistrationStatus = 14;
					} else {
						tempRegistrationStatus = 1;
					}
				}
			}
		}

		return tempRegistrationStatus;
	}

	public String checkRegistrationDeleteCondition(String pSemesterSubId, String pRegisterNumber, String pCourseId,
			Integer pProgramGroupId, String pProgramGroupCode, String programGroupMode, String pProgramSpecCode,
			Integer pProgramSpecId, Integer pStudentStartYear, Float pCurriculumVersion, List<String> compulsoryCourse,
			String coReqCourseId, HttpSession session) {
		int deleteAllowFlag = 2;
		int checkFlag = 2, checkFlag2 = 2, checkFlag3 = 2, checkFlag4 = 2, checkFlag5 = 2;
		
		session.setAttribute("pProgramSpecCode", pProgramSpecCode);

		String message = "", authKeyValue = "NONE";
		String courseCode = "", courseTitle = "", courseCategory = "NONE", courseOption = "NONE";

		CourseCatalogModel ccm = new CourseCatalogModel();
		List<Object[]> psRegList = new ArrayList<Object[]>();
		List<String> courseList = new ArrayList<String>();

		try {
			// Checking the select course is valid or not
			if ((pCourseId != null) && (!pCourseId.equals(""))) {
				ccm = courseCatalogService.getOne(pCourseId);
				if (ccm != null) {
					courseCode = ccm.getCode();
					courseTitle = ccm.getTitle();

					// To get course category from curriculum
					if (pCurriculumVersion > 0) {
						psRegList.clear();
						psRegList = programmeSpecializationCurriculumDetailService
								.getCurriculumByAdmsnYearCCVersionAndCourseCode(pProgramSpecId, pStudentStartYear,
										pCurriculumVersion, courseCode);
						if (!psRegList.isEmpty()) {
							for (Object[] e : psRegList) {
								courseCategory = e[0].toString();
								break;
							}
						} else {
							courseCategory = "UE";
						}
					}

					checkFlag = 1;
				} else {
					message = "Invalid course code.";
				}
			} else {
				message = "Invalid course code";
			}

			float courseCredit = 0, obtCredit = 0, rmgCredit = 0, ctgCredit = 0;

			// Checking whether student is already registered or not
			if (checkFlag == 1) {
				psRegList.clear();
				psRegList = courseRegistrationService.getCourseOptionByRegisterNumberAndCourseCode(pSemesterSubId,
						pRegisterNumber, courseCode);
				if (!psRegList.isEmpty()) {
					for (Object[] e : psRegList) {
						courseOption = e[3].toString();
						courseCredit = Float.parseFloat(e[5].toString());
						break;
					}
					checkFlag2 = 1;
				}

			}

			// Checking whether student is deleting the blocked course
			if (checkFlag2 == 1) {
				if (!compulsoryCourse.contains(courseCode)) {
					checkFlag3 = 1;
				} else if (coReqCourseId != null && !coReqCourseId.trim().equals("")) {
					checkFlag3 = 1;
				} else {
					checkFlag3 = 2;
					message = "Compulsory course(s) are not permitted to delete";
				}

				if (checkFlag3 == 1) {
					courseList.clear();
					courseList = courseRegistrationService.getBlockedCourseIdByRegisterNumberForDelete(pSemesterSubId,
							pRegisterNumber);
					if (!courseList.contains(pCourseId)) {
						checkFlag3 = 1;
					} else {
						checkFlag3 = 2;
						message = "Invoice generated course(s) are not permitted to delete.";
					}
				}
			}

			// Checking whether student is deleting the Regular Course with other Options
			// (i.e. RPEUE/ RUCPE/ MINOR/ HONOUR) in same category
			if (checkFlag3 == 1) {
				if (courseCategory.equals("UC") || courseCategory.equals("PE") || courseCategory.equals("UE")
						|| courseCategory.equals("BC") || courseCategory.equals("NC") || courseCategory.equals("DE")
						|| courseCategory.equals("SPE")) {
					if (courseOption.equals("RGR") || courseOption.equals("RGCE") || courseOption.equals("RGP")
							|| courseOption.equals("RGW") || courseOption.equals("RPCE")
							|| courseOption.equals("RWCE")) {
						if (courseCategory.equals("BC") || courseCategory.equals("NC")) {
							checkFlag4 = 2;
						} else {
							courseList.clear();
							courseList = courseRegistrationService
									.getConvertedElectiveCourseByRegisterNumberAndCourseOption(pSemesterSubId,
											pRegisterNumber, courseCategory);
							if (courseList.isEmpty()) {
								checkFlag4 = 1;
							} else {
								checkFlag4 = 2;
							}

						}

						if (checkFlag4 == 2) {
							message = "Selected course " + courseCode + " - " + courseTitle + " not allowed to delete.";
							if (courseCategory.equals("UC")) {
								message = message
										+ "  First, remove the registered course(s) under the course option Regular"
										+ " (UC as UE) and then remove this course.";
							} else if (courseCategory.equals("PE")) {
								message = message + "  First, remove the registered course(s) under the course option "
										+ "Regular (PE as UE)/ Regular (Additional)/ Honour and then remove this course.";
							} else if (courseCategory.equals("UE")) {
								message = message + "  First, remove the registered course(s) under the course option "
										+ "Regular (UC as UE)/ Regular (PE as UE)/ Regular (Additional)/ Minor and then remove this course.";
							} else if (courseCategory.equals("BC") || courseCategory.equals("NC")) {
								message = message + " Bridge courses and Non-Credit category courses can’t be deleted.";
							} else if (courseCategory.equals("DE")) {
								message = message + " First, remove the registered course(s) under the course option "
										+ " Regular (DE as OE)/ Regular (Additional)/ Honor and then remove this course.";
							} else if (courseCategory.equals("SPE")) {
								message = message
										+ "First, remove the registered course(s) under course option Regular "
										+ " (SPE as OE)/ Regular (Additional)/ Honour and then remove this course.";
							}
						}
					} else {
						checkFlag4 = 1;
					}
				} else {
					checkFlag4 = 1;
				}
			}

			psRegList = courseRegistrationService.doGetRegisteredCourseByCourseCateg(pSemesterSubId, pRegisterNumber,
					"ME");

			if (psRegList != null && !psRegList.isEmpty() && courseCategory.equals("DE")) {

				String studStudySystem = (String) session.getAttribute("studentStudySystem");

				List<Object[]> ccCreditList = programmeSpecializationCurriculumCreditService
						.getCurrentSemRegCurCtgCreditByRegisterNo(pProgramSpecId, pStudentStartYear, pCurriculumVersion,
								pSemesterSubId, pRegisterNumber, courseCategory, studStudySystem);
				if (!ccCreditList.isEmpty()) {

					for (Object[] e : ccCreditList) {
						if (e[0].toString().equals(courseCategory)) {
							ctgCredit = Float.parseFloat(e[1].toString());
							obtCredit = Float.parseFloat(e[6].toString());
							rmgCredit = Float.parseFloat(e[7].toString());

							break;
						}
					}

				}

				if ((obtCredit - courseCredit) < 9) {
					message = "First, delete the course(s) registered in ME category and then proceed.";
				} else {
					checkFlag5 = 1;
				}

			} else {
				checkFlag5 = 1;
			}

			if ((checkFlag == 1) && (checkFlag2 == 1) && (checkFlag3 == 1) && (checkFlag4 == 1) && (checkFlag5 == 1)) {
				deleteAllowFlag = 1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.trace(ex);
		}

		// Generating the Authorization key
		authKeyValue = generateCourseAuthKey(pRegisterNumber, pCourseId, deleteAllowFlag, 1);

		LOGGER.trace("\n checkFlag: " + checkFlag + " | checkFlag2: " + checkFlag2 + " | checkFlag3: " + checkFlag3
				+ " | checkFlag4: " + checkFlag4);
		LOGGER.trace("\n deleteAllowFlag: " + deleteAllowFlag + " / message: " + message + " / authKeyValue: "
				+ authKeyValue);

		return deleteAllowFlag + "|" + message + "|" + authKeyValue;
	}

	/// Get the maximum credit
	public String getMinimumAndMaximumCreditLimit(String semesterSubId, String registerNumber, String programGroupCode,
			String costCentreCode, int studentStartYear, int studentGraduateYear, int checkGraduateYear, int semesterId,
			String programSpecializationCode, String studentCgpaData, HttpSession session) {
		
		String pProgramSpecCode = (String) session.getAttribute("pProgramSpecCode");
		float minCredit = 16f, maxCredit = 27.5f;
		int lowCgpa = 0;
		String studySystem = (String)session.getAttribute("studentStudySystem");
		// Checking with CGPA for particular Semester & Programme
		if (((semesterId == 1) || (semesterId == 5) || (semesterId == 7))
				&& (programGroupCode.equals("BTECH") || programGroupCode.equals("BCOM")|| programGroupCode.equals("BBH")
						|| programGroupCode.equals("BBA") || programGroupCode.equals("BCA")|| programGroupCode.equals("MCA")|| programGroupCode.equals("MTECH")
						|| programGroupCode.equals("MSC5") || programGroupCode.equals("MTECH5")|| programGroupCode.equals("LAW")|| programGroupCode.equals("MBA")
						|| (programGroupCode.equals("BSC")))
				&& (studentCgpaData != null) && (!studentCgpaData.equals(""))) {
			String[] studentCGPAArray = studentCgpaData.split("\\|");

			if (AppGlobalValues.MAX_CREDIT_CGPA_CHECK && (studentCGPAArray.length > 0)
					&& (Float.parseFloat(studentCGPAArray[2]) < (float) globalValues.getLowCgpa())) {
				minCredit = 0f;
				maxCredit = globalValues.getLowCgpaMaxCredits();
				lowCgpa = 1;

			}

		}

		if ((programGroupCode.equals("MBA") || programGroupCode.equals("MBA5")
				|| (programGroupCode.equals("RP") && costCentreCode.equals("VITBS"))) && (maxCredit > 16)) {

			maxCredit = globalValues.getMaxCredits();
		} else if ((semesterId == 1) && (studentGraduateYear <= checkGraduateYear)
				&& (maxCredit > 16)) {
			if (courseRegistrationService.getProjectCourseCountByRegisterNumber(semesterSubId, registerNumber,
					Arrays.asList("CAPSTONE", "GUIDE")) > 0) {
				maxCredit = 30;
			} else {
				maxCredit = 27.5f;
			}
		} else if ((semesterId == 1) && (studySystem.equals("ACE"))
				&& (maxCredit > 16)) {
				maxCredit = 27f;
		}else if ((semesterId == 1) && (programGroupCode.equals("LAW"))
				&& (maxCredit > 16)) {
				maxCredit = 28f;
		}
		else if (semesterId == 1 && programGroupCode.equals("MTECH5")  && maxCredit > 16 && registerNumber.startsWith("23MIA")) {
			maxCredit = 29f;
		}
		else if ((semesterId == 5) && (studentGraduateYear <= checkGraduateYear) && (maxCredit > 16)) {
			if (courseRegistrationService.getProjectCourseCountByRegisterNumber(semesterSubId, registerNumber,
					Arrays.asList("CAPSTONE")) > 0) {
				maxCredit = 30f;
			} else {
				maxCredit = globalValues.getMaxCredits();
			}
		}

		// CREDITS_NEW MAHE
		if (lowCgpa == 0) {
			if (semesterId == 5 && programGroupCode.equals("BTECH") && studentStartYear == 2021) {
				maxCredit = 30f;
			}
		}

		return minCredit + "|" + maxCredit;
	}

	public String getRandomOtp(Integer keyLength) {
		String sDefaultChars = "123456789abcdefghjkmnpqrxyzABCDEFGHJKMNPQRXYZ";
		Integer iKeyLength = keyLength;
		Integer iDefaultCharactersLength = sDefaultChars.length();
		String sMyKey = "";
		for (int iCounter = 1; iCounter <= iKeyLength; iCounter++) {
			Integer iPickedChar = (int) ((iDefaultCharactersLength * Math.random()) + 1);
			if (iPickedChar >= sDefaultChars.length())
				sMyKey = sMyKey + (sDefaultChars.substring(sDefaultChars.length() - 1));
			else
				sMyKey = sMyKey + (sDefaultChars.substring(iPickedChar, iPickedChar + 1));
		}

		return sMyKey;
	}

	// Course Option Allow Status - MAHE
	public String getCourseOptionStatus(String programGroupCode, String programSpecCode, int studentGraduateYear,
			int acadGraduateYear, int semesterId, int studentAdmissionYear) {
		int regularCourseStatus = 2, NGradeCourseStatus = 2, giCourseStatus = 2, auditCourseStatus = 2,
				minHonCourseStatus = 2, adlCourseStatus = 2, peAdlCourseStatus = 2, ueAdlCourseStatus = 2;

		if (semesterId == 8) {
			if (((programGroupCode.equals("MTECH5") && (programSpecCode.equals("MIS") || programSpecCode.equals("MID")
					|| programSpecCode.equals("MIC")))
					|| (programGroupCode.equals("MSC5") && programSpecCode.equals("MIY")))
					&& studentAdmissionYear == 2021) {
				regularCourseStatus = 1;
				NGradeCourseStatus = 1;
			} else if (studentGraduateYear <= acadGraduateYear) {
				regularCourseStatus = 1;
				NGradeCourseStatus = 1;
			} else {
				NGradeCourseStatus = 1;
			}

			/*
			 * regularCourseStatus = 1; NGradeCourseStatus = 1; giCourseStatus = 1;
			 * auditCourseStatus = 1; minHonCourseStatus = 1; adlCourseStatus = 1;
			 * peAdlCourseStatus = 1; ueAdlCourseStatus = 1;
			 */

		} else if ((semesterId == 7) || (semesterId == 9)) {
			if (programGroupCode.equals("MCA")) {
				regularCourseStatus = 1;
				NGradeCourseStatus = 1;
			} else if (programGroupCode.equals("MTECH5") && (studentGraduateYear <= (acadGraduateYear + 1))) {
				regularCourseStatus = 1;
				NGradeCourseStatus = 1;

			} else if (studentGraduateYear <= acadGraduateYear) {
				regularCourseStatus = 1;
				NGradeCourseStatus = 1;
			} else if (studentGraduateYear == (acadGraduateYear + 3) && (programGroupCode.equals("BTECH"))) {
				// minHonCourseStatus = 1;
				regularCourseStatus = 1;
			} else if (studentGraduateYear == (acadGraduateYear + 1) && (programGroupCode.equals("MTECH")
					|| programGroupCode.equals("MBA") || programGroupCode.equals("MCA")
					|| (programGroupCode.equals("MSC") && !programSpecCode.equals("MDT")))) {

				NGradeCourseStatus = 1;
			} else if (studentGraduateYear == (acadGraduateYear + 1)
					&& (programGroupCode.equals("MSC") && programSpecCode.equals("MDT"))) {
				regularCourseStatus = 1;
				NGradeCourseStatus = 1;
			}

			else {
				// regularCourseStatus = 1;
				NGradeCourseStatus = 1;
			}
		} else {

			/*
			 * For WEI Intra Only Re-Registration Allowed & No Max credit from Winter
			 * 2022-23
			 */

			regularCourseStatus = 1;
			NGradeCourseStatus = 1;

			/* Enable at the time of Add/Drop */
			
			  giCourseStatus = 1; auditCourseStatus = 1; minHonCourseStatus = 1;
			  adlCourseStatus = 1; peAdlCourseStatus = 1; ueAdlCourseStatus = 1;
			 
		}

		return regularCourseStatus + "|" + NGradeCourseStatus + "|" + giCourseStatus + "|" + auditCourseStatus + "|"
				+ minHonCourseStatus + "|" + adlCourseStatus + "|" + peAdlCourseStatus + "|" + ueAdlCourseStatus;
	}

	// Get the Clash Status (New Method)
	public Map<Long, Object[]> getClashSlotStatus2(List<Object[]> registeredList,
			List<CourseAllocationModel> courseAllocationList, Map<String, List<SlotTimeMasterModel>> slotTimeMapList) {
		Map<Long, Object[]> returnMapList = new HashMap<Long, Object[]>();

		int checkStatus = 2, patternId = 0;
		String clashSlot = "", checkMessage = "", color = "";
		String[] clashStatusArray = new String[] {};
		List<String> classIdList = new ArrayList<String>();
		List<Long> slotIdList = new ArrayList<Long>();
		LOGGER.trace("\n Checking clash status with registered Slots==>");

		if ((!registeredList.isEmpty()) && (!courseAllocationList.isEmpty())) {

			classIdList = registeredList.stream().map(e -> e[3].toString()).collect(Collectors.toList());
			slotIdList = registeredList.stream().map(e -> Long.parseLong(e[2].toString())).collect(Collectors.toList());
			LOGGER.trace("\n classIdLists: " + classIdList + " | slotIdList: " + slotIdList);

			// Checking the clash with Registered Slot
			for (CourseAllocationModel e : courseAllocationList) {
				patternId = e.getTimeTableModel().getPatternId();
				clashSlot = e.getTimeTableModel().getClashSlot();

				if (!returnMapList.containsKey(e.getSlotId())) {
					checkStatus = 2;
					checkMessage = "NONE";
					color = "green";

					if ((e.getSlotId() <= 0) || (e.getCourseType().equals("EPJ"))) {
						checkStatus = 1;
					} else if (classIdList.contains(e.getClassId())) {
						checkMessage = "Registered Slot";
						color = "red";
					} else if ((!classIdList.contains(e.getClassId())) && (slotIdList.contains(e.getSlotId()))) {
						checkMessage = "Similar to Registered Slot";
						color = "brown";
					} else {
						clashStatusArray = courseAllocationService
								.getClashStatus(patternId, clashSlot, registeredList, slotTimeMapList).split("\\|");
						checkStatus = Integer.parseInt(clashStatusArray[0].toString());

						if (checkStatus == 2) {
							checkMessage = "Clashed with " + clashStatusArray[1].toString();
							color = "red";
						}
					}

					returnMapList.put(e.getSlotId(), new Object[] { checkMessage, color });
				}
			}
		}

		return returnMapList;
	}

	// Get the Slot Information
	public Map<String, Object[]> getSlotInfo(List<Object[]> registeredList,
			List<CourseAllocationModel> courseAllocationList, Map<String, List<SlotTimeMasterModel>> slotTimeMapList) {
		Map<String, Object[]> returnMapList = new HashMap<String, Object[]>();

		int patternId = 0, checkStatus = 2;
		long buildingId = 0;
		String slot = "", clashSlot = "", buildingCode = "", checkMessage = "", color = "";

		String[] clashStatusArray = new String[] {}, clashStatusArray2 = new String[] {};
		List<String> classIdList = new ArrayList<String>();
		List<Long> slotIdList = new ArrayList<Long>();
		Map<String, Integer> sfiMapList = new HashMap<String, Integer>();
		LOGGER.trace("\n Checking Info with registered Slots==>");

		if ((!registeredList.isEmpty()) && (!courseAllocationList.isEmpty())) {
			classIdList = registeredList.stream().map(e -> e[3].toString()).collect(Collectors.toList());
			slotIdList = registeredList.stream().map(e -> Long.parseLong(e[2].toString())).collect(Collectors.toList());
			// sfiMapList = courseAllocationService.getSlotFixedInfoList();
			LOGGER.trace("\n classIdLists: " + classIdList + " | slotIdList: " + slotIdList + " | sfiMapList size: "
					+ sfiMapList.size());

			for (CourseAllocationModel e : courseAllocationList) {
				patternId = e.getTimeTableModel().getPatternId();
				slot = e.getTimeTableModel().getSlotName();
				clashSlot = e.getTimeTableModel().getClashSlot();
				buildingId = e.getBuildingMasterBuildingId();
				buildingCode = e.getRoomMaster().getBuildingMaster().getCode();

				if (!returnMapList.containsKey(e.getClassId())) {
					checkStatus = 2;
					checkMessage = "NONE";
					color = "green";

					if (classIdList.contains(e.getClassId())) {
						checkMessage = "Registered Slot";
						color = "red";
					} else if ((!classIdList.contains(e.getClassId())) && (slotIdList.contains(e.getSlotId()))) {
						checkMessage = "Similar to Registered Slot";
						color = "brown";
					} else if ((e.getSlotId() > 0) && (!e.getCourseType().equals("EPJ"))) {
						clashStatusArray = courseAllocationService
								.getClashStatus(patternId, clashSlot, registeredList, slotTimeMapList).split("\\|");
						// LOGGER.trace("\n clashStatusArray: "+ Arrays.toString(clashStatusArray));
						checkStatus = Integer.parseInt(clashStatusArray[0].toString());

						if (checkStatus == 1) {
							clashStatusArray2 = courseAllocationService.getSlotInfoStatus(patternId, slot, buildingId,
									buildingCode, registeredList, slotTimeMapList, sfiMapList).split("\\|");
							checkMessage = clashStatusArray2[1].toString();
							color = clashStatusArray2[2].toString();
						} else {
							checkMessage = "Clashed with " + clashStatusArray[1].toString();
							color = "red";
						}
					}

					returnMapList.put(e.getClassId(), new Object[] { checkMessage, color });
				}
			}
		}

		return returnMapList;
	}

	public static Map<String, List<String>> doGetRegistrationStatus() {
		Map<String, List<String>> statusMap = new HashMap<>();
		statusMap.put("0", Arrays.asList("Registered", "green"));
		statusMap.put("1", Arrays.asList("Registered and Invoice Not Generated", "red"));
		statusMap.put("2", Arrays.asList("Registered, Invoice Generated and Fees Not Paid", "red"));
		statusMap.put("3", Arrays.asList("Registered, Invoice Generated, Fees Paid and Approved", "green"));
		statusMap.put("4", Arrays.asList("Registered, Invoice Generated, Fees Paid and Not Approved by Guide", "red"));
		statusMap.put("5", Arrays.asList("Registered, Invoice Generated, Fees Paid and Rejected by Guide", "red"));
		statusMap.put("6", Arrays.asList("Registered, Invoice Generated, Fees Paid and Approved By Faculty", "green"));
		statusMap.put("7", Arrays.asList("Registered and Not Approved by Guide", "red"));
		statusMap.put("8", Arrays.asList("Registered and Rejected by Guide", "red"));
		statusMap.put("9", Arrays.asList("Registered and Approved by Guide", "green"));
		statusMap.put("10", Arrays.asList("Registered and Approved", "green"));
		statusMap.put("11", Arrays.asList("Registered and Rejected", "red"));
		statusMap.put("12", Arrays.asList("Registered and Course in this slot not offered", "red"));
		statusMap.put("13", Arrays.asList("Registered, Invoice Generated and Cancelled", "red"));
		statusMap.put("14", Arrays.asList("Registered and Approved (STARS Student)", "green"));

		return statusMap;
	}

	public StudentInformation getCourseRegistrationDetailByRegisterNumber(String registerNumber) {
		List<Object[]> courseRegActivity;
		List<Object[]> EPTHscFeeDescCGPA;

		StringBuilder message = new StringBuilder();

		try {

			courseRegActivity = activityRepo.findScheduleForCourseRegDetailByRegisterNumber(registerNumber);
			// chennai code for registration permit
			Integer registrationPermitCount = activityRepo.getRegistrationPermitDetails(
					courseRegActivity.isEmpty() ? "" : courseRegActivity.get(0)[10].toString(), registerNumber);

			// if (courseRegActivity != null && !courseRegActivity.isEmpty() &&
			// registrationPermitCount > 0) {
			if (courseRegActivity != null && !courseRegActivity.isEmpty()) {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

				DateTimeFormatter DDMMYYYYHHMM = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

				if (courseRegActivity.size() > 1) {

					message.append("More than one activity is enabled now. You can’t proceed. Try again later.");

					for (Object[] objects : courseRegActivity) {

						message.append(" Semester : " + objects[10].toString() + " - "
								+ courseRegActivity.get(0)[11].toString());
						message.append(" ClassGroup : " + objects[13].toString());
					}

					stuCrsRegActy.setMessage(message.toString());

				} else {

					if (courseRegActivity.get(0)[9] == null) {
						if (Boolean.parseBoolean(courseRegActivity.get(0)[31].toString())) {
							stuCrsRegActy.setProgGroupCode(
									courseRegActivity.get(0)[0] != null ? courseRegActivity.get(0)[0].toString()
											: null);
							stuCrsRegActy.setProgSpecializationDescription(
									courseRegActivity.get(0)[1] != null ? courseRegActivity.get(0)[1].toString()
											: null);
							stuCrsRegActy.setCentreCode(
									courseRegActivity.get(0)[2] != null ? courseRegActivity.get(0)[2].toString()
											: null);
							stuCrsRegActy.setRegisterNumber(
									courseRegActivity.get(0)[3] != null ? courseRegActivity.get(0)[3].toString()
											: null);
							stuCrsRegActy.setAdmissionYear(courseRegActivity.get(0)[4] != null
									? (int) Double.parseDouble(courseRegActivity.get(0)[4].toString())
									: 0);
							stuCrsRegActy.setProgSpecializationId(courseRegActivity.get(0)[5] != null
									? Integer.parseInt(courseRegActivity.get(0)[5].toString())
									: 0);
							stuCrsRegActy.setStudySystem(
									courseRegActivity.get(0)[6] != null ? courseRegActivity.get(0)[6].toString()
											: null);
							stuCrsRegActy.setEducationStatusDescription(
									courseRegActivity.get(0)[7] != null ? courseRegActivity.get(0)[7].toString()
											: null);
							stuCrsRegActy.setLockStatus(courseRegActivity.get(0)[8] != null
									? Integer.parseInt(courseRegActivity.get(0)[8].toString())
									: 0);
							stuCrsRegActy.setExamGraduationStatus(courseRegActivity.get(0)[9] != null
									? Integer.valueOf(courseRegActivity.get(0)[9].toString())
									: 0);
							stuCrsRegActy.setSemesterSubId(
									courseRegActivity.get(0)[10] != null ? courseRegActivity.get(0)[10].toString()
											: null);
							stuCrsRegActy.setSemesterDesc(
									courseRegActivity.get(0)[11] != null ? courseRegActivity.get(0)[11].toString()
											: null);
							stuCrsRegActy.setClassGroupId(
									courseRegActivity.get(0)[12] != null ? courseRegActivity.get(0)[12].toString()
											: null);
							stuCrsRegActy.setClassGroupDesc(
									courseRegActivity.get(0)[13] != null ? courseRegActivity.get(0)[13].toString()
											: null);
							stuCrsRegActy.setProgGroupDuration(courseRegActivity.get(0)[14] != null
									? Integer.parseInt(courseRegActivity.get(0)[14].toString())
									: 0);
							stuCrsRegActy.setProgGroupId(courseRegActivity.get(0)[15] != null
									? Integer.parseInt(courseRegActivity.get(0)[15].toString())
									: 0);
							stuCrsRegActy.setCentreId(courseRegActivity.get(0)[16] != null
									? Integer.parseInt(courseRegActivity.get(0)[16].toString())
									: 0);
							stuCrsRegActy.setStartTimestamp(courseRegActivity.get(0)[17] != null
									? LocalDateTime.parse(courseRegActivity.get(0)[17].toString(), formatter)
									: null);
							stuCrsRegActy.setEndTimestamp(courseRegActivity.get(0)[18] != null
									? LocalDateTime.parse(courseRegActivity.get(0)[18].toString(), formatter)
									: null);
							stuCrsRegActy.setStudentName(
									courseRegActivity.get(0)[19] != null ? courseRegActivity.get(0)[19].toString()
											: null);
							stuCrsRegActy.setGender(
									courseRegActivity.get(0)[20] != null ? courseRegActivity.get(0)[20].toString()
											: null);
							stuCrsRegActy.setApplicationNumber(courseRegActivity.get(0)[21] != null
									? Long.parseLong(courseRegActivity.get(0)[21].toString())
									: 0L);
							stuCrsRegActy.setProgSpecializationCode(
									courseRegActivity.get(0)[22] != null ? courseRegActivity.get(0)[22].toString()
											: null);
							stuCrsRegActy.setProgGroupDescription(
									courseRegActivity.get(0)[23] != null ? courseRegActivity.get(0)[23].toString()
											: null);
							stuCrsRegActy.setProgGroupMode(
									courseRegActivity.get(0)[24] != null ? courseRegActivity.get(0)[24].toString()
											: null);
							stuCrsRegActy.setProgGroupLevel(
									courseRegActivity.get(0)[25] != null ? courseRegActivity.get(0)[25].toString()
											: null);
							stuCrsRegActy.setCentreDescription(
									courseRegActivity.get(0)[26] != null ? courseRegActivity.get(0)[26].toString()
											: null);
							stuCrsRegActy.setEducationStatus(
									courseRegActivity.get(0)[27] != null ? courseRegActivity.get(0)[27].toString()
											: null);
							stuCrsRegActy.setExamGraduationStatus(courseRegActivity.get(0)[9] != null
									? Integer.parseInt(courseRegActivity.get(0)[9].toString())
									: 0);
							stuCrsRegActy.setEmail(
									courseRegActivity.get(0)[28] != null ? courseRegActivity.get(0)[28].toString()
											: null);
							stuCrsRegActy.setMobile(
									courseRegActivity.get(0)[29] != null ? courseRegActivity.get(0)[29].toString()
											: null);
							stuCrsRegActy.setSemesterId(courseRegActivity.get(0)[30] != null
									? Integer.parseInt(courseRegActivity.get(0)[30].toString())
									: 0);
							stuCrsRegActy.setCurrentSchedule(courseRegActivity.get(0)[31] != null
									? Boolean.parseBoolean(courseRegActivity.get(0)[31].toString())
									: false);

							EPTHscFeeDescCGPA = activityRepo
									.findEPTMarkHscGroupFeeDescCGPAForCourseRegByRegisterNumber(registerNumber);

							if (EPTHscFeeDescCGPA != null && !EPTHscFeeDescCGPA.isEmpty()) {
								stuCrsRegActy.setEptMark(EPTHscFeeDescCGPA.get(0)[1] != null
										? Integer.parseInt(EPTHscFeeDescCGPA.get(0)[1].toString())
										: 0);
								stuCrsRegActy.setHscGroup(
										EPTHscFeeDescCGPA.get(0)[2] != null ? EPTHscFeeDescCGPA.get(0)[2].toString()
												: null);
								stuCrsRegActy.setFeeCategoryDescription(
										EPTHscFeeDescCGPA.get(0)[3] != null ? EPTHscFeeDescCGPA.get(0)[3].toString()
												: null);
								stuCrsRegActy.setTotalCreditsRegistered(EPTHscFeeDescCGPA.get(0)[4] != null
										? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[4].toString())
										: 0.0f);
								stuCrsRegActy.setTotalCreditsEarned(EPTHscFeeDescCGPA.get(0)[5] != null
										? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[5].toString())
										: 0.0f);
								stuCrsRegActy.setCumulativeGradePointAverage(EPTHscFeeDescCGPA.get(0)[6] != null
										? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[6].toString())
										: 0.0f);
							}

							if (stuCrsRegActy.isCurrentSchedule()) {
								stuCrsRegActy.setMessage("SUCCESS");
								stuCrsRegActy.setWishlistStatus(activityRepo
										.findWishListRegistrationStatusBySemesterSubIdAndClassGroupIdAndRegisterNumber(
												stuCrsRegActy.getSemesterSubId(), stuCrsRegActy.getClassGroupId(),
												registerNumber));
							}

						} else {
							stuCrsRegActy.setMessage("Your Schedule is " + courseRegActivity.get(0)[3].toString()
									+ " - "
									+ DDMMYYYYHHMM.format(
											LocalDateTime.parse(courseRegActivity.get(0)[17].toString(), formatter))
									+ " to " + DDMMYYYYHHMM.format(
											LocalDateTime.parse(courseRegActivity.get(0)[18].toString(), formatter)));
						}

						// Minor Opted Check
						List<AdditionalLearningOpted> addlLearningList = optedService
								.findAddlLearningByRegNoAndLearnType(registerNumber, "MINOR");

						if (addlLearningList != null && !addlLearningList.isEmpty()) {
							stuCrsRegActy
									.setOptedCBCSMinor(addlLearningList.get(0).getId().getAdditionalLearningCode());
						}

						// Honour Opted Check
						List<AdditionalLearningOpted> addlLearningHonList = optedService
								.findAddlLearningByRegNoAndLearnType(registerNumber, "HONOUR");

						if (addlLearningHonList != null && !addlLearningHonList.isEmpty()) {
							stuCrsRegActy
									.setOptedCBCSHonour(addlLearningHonList.get(0).getId().getAdditionalLearningCode());
						}
					} else {
						stuCrsRegActy.setMessage("Degree Eligibility has been processed");
					}

				}
			} else {
				stuCrsRegActy.setMessage("Not Eligible for Current Registration");
			}
		} catch (Exception e) {
			e.printStackTrace();
			stuCrsRegActy.setMessage("FAIL");
		}

		return stuCrsRegActy;
	}

	public StudentInformation setCourseRegistrationStudentDetailActivityDTO(StudentInformation stuCrsRegActy,
			List<Object[]> courseRegActivity) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

		stuCrsRegActy
				.setProgGroupCode(courseRegActivity.get(0)[0] != null ? courseRegActivity.get(0)[0].toString() : null);
		stuCrsRegActy.setProgSpecializationDescription(
				courseRegActivity.get(0)[1] != null ? courseRegActivity.get(0)[1].toString() : null);
		stuCrsRegActy
				.setCentreCode(courseRegActivity.get(0)[2] != null ? courseRegActivity.get(0)[2].toString() : null);
		stuCrsRegActy
				.setRegisterNumber(courseRegActivity.get(0)[3] != null ? courseRegActivity.get(0)[3].toString() : null);
		stuCrsRegActy.setAdmissionYear(
				courseRegActivity.get(0)[4] != null ? (int) Double.parseDouble(courseRegActivity.get(0)[4].toString())
						: 0);
		stuCrsRegActy.setProgSpecializationId(
				courseRegActivity.get(0)[5] != null ? Integer.parseInt(courseRegActivity.get(0)[5].toString()) : 0);
		stuCrsRegActy
				.setStudySystem(courseRegActivity.get(0)[6] != null ? courseRegActivity.get(0)[6].toString() : null);
		stuCrsRegActy.setEducationStatusDescription(
				courseRegActivity.get(0)[7] != null ? courseRegActivity.get(0)[7].toString() : null);
		stuCrsRegActy.setLockStatus(
				courseRegActivity.get(0)[8] != null ? Integer.parseInt(courseRegActivity.get(0)[8].toString()) : 0);
		stuCrsRegActy.setExamGraduationStatus(
				courseRegActivity.get(0)[9] != null ? Integer.valueOf(courseRegActivity.get(0)[9].toString()) : 0);
		stuCrsRegActy.setSemesterSubId(
				courseRegActivity.get(0)[10] != null ? courseRegActivity.get(0)[10].toString() : null);
		stuCrsRegActy
				.setSemesterDesc(courseRegActivity.get(0)[11] != null ? courseRegActivity.get(0)[11].toString() : null);
		stuCrsRegActy
				.setClassGroupId(courseRegActivity.get(0)[12] != null ? courseRegActivity.get(0)[12].toString() : null);
		stuCrsRegActy.setClassGroupDesc(
				courseRegActivity.get(0)[13] != null ? courseRegActivity.get(0)[13].toString() : null);
		stuCrsRegActy.setProgGroupDuration(
				courseRegActivity.get(0)[14] != null ? Integer.parseInt(courseRegActivity.get(0)[14].toString()) : 0);
		stuCrsRegActy.setProgGroupId(
				courseRegActivity.get(0)[15] != null ? Integer.parseInt(courseRegActivity.get(0)[15].toString()) : 0);
		stuCrsRegActy.setCentreId(
				courseRegActivity.get(0)[16] != null ? Integer.parseInt(courseRegActivity.get(0)[16].toString()) : 0);
		stuCrsRegActy.setStartTimestamp(courseRegActivity.get(0)[17] != null
				? LocalDateTime.parse(courseRegActivity.get(0)[17].toString(), formatter)
				: null);
		stuCrsRegActy.setEndTimestamp(courseRegActivity.get(0)[18] != null
				? LocalDateTime.parse(courseRegActivity.get(0)[18].toString(), formatter)
				: null);
		stuCrsRegActy
				.setStudentName(courseRegActivity.get(0)[19] != null ? courseRegActivity.get(0)[19].toString() : null);
		stuCrsRegActy.setGender(courseRegActivity.get(0)[20] != null ? courseRegActivity.get(0)[20].toString() : null);
		stuCrsRegActy.setApplicationNumber(
				courseRegActivity.get(0)[21] != null ? Long.parseLong(courseRegActivity.get(0)[21].toString()) : 0L);
		stuCrsRegActy.setProgSpecializationCode(
				courseRegActivity.get(0)[22] != null ? courseRegActivity.get(0)[22].toString() : null);
		stuCrsRegActy.setProgGroupDescription(
				courseRegActivity.get(0)[23] != null ? courseRegActivity.get(0)[23].toString() : null);
		stuCrsRegActy.setProgGroupMode(
				courseRegActivity.get(0)[24] != null ? courseRegActivity.get(0)[24].toString() : null);
		stuCrsRegActy.setProgGroupLevel(
				courseRegActivity.get(0)[25] != null ? courseRegActivity.get(0)[25].toString() : null);
		stuCrsRegActy.setCentreDescription(
				courseRegActivity.get(0)[26] != null ? courseRegActivity.get(0)[26].toString() : null);
		stuCrsRegActy.setEducationStatus(
				courseRegActivity.get(0)[27] != null ? courseRegActivity.get(0)[27].toString() : null);
		stuCrsRegActy.setExamGraduationStatus(
				courseRegActivity.get(0)[9] != null ? Integer.parseInt(courseRegActivity.get(0)[9].toString()) : 0);
		stuCrsRegActy.setEmail(courseRegActivity.get(0)[28] != null ? courseRegActivity.get(0)[28].toString() : null);
		stuCrsRegActy.setMobile(courseRegActivity.get(0)[29] != null ? courseRegActivity.get(0)[29].toString() : null);
		stuCrsRegActy.setSemesterId(
				courseRegActivity.get(0)[30] != null ? Integer.parseInt(courseRegActivity.get(0)[30].toString()) : 0);

		return stuCrsRegActy;
	}

	public StudentInformation getMockCourseRegistrationDetailByRegisterNumber(String registerNumber) {
		List<Object[]> courseRegActivity;
		List<Object[]> EPTHscFeeDescCGPA;

		StringBuilder message = new StringBuilder();

		try {

			courseRegActivity = activityRepo.findScheduleForMockCourseRegDetailByRegisterNumber(registerNumber);

			if (courseRegActivity != null && !courseRegActivity.isEmpty()) {

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

				if (courseRegActivity.size() > 1) {

					message.append("More than one activity is enabled now. You can’t proceed. Try again later.");

					for (Object[] objects : courseRegActivity) {

						message.append(" Semester : " + objects[10].toString() + " - "
								+ courseRegActivity.get(0)[11].toString());
						message.append(" ClassGroup : " + objects[13].toString());
					}

					stuCrsRegActy.setMessage(message.toString());

				} else {

					if (courseRegActivity.get(0)[9] == null) {

						stuCrsRegActy.setProgGroupCode(
								courseRegActivity.get(0)[0] != null ? courseRegActivity.get(0)[0].toString() : null);
						stuCrsRegActy.setProgSpecializationDescription(
								courseRegActivity.get(0)[1] != null ? courseRegActivity.get(0)[1].toString() : null);
						stuCrsRegActy.setCentreCode(
								courseRegActivity.get(0)[2] != null ? courseRegActivity.get(0)[2].toString() : null);
						stuCrsRegActy.setRegisterNumber(
								courseRegActivity.get(0)[3] != null ? courseRegActivity.get(0)[3].toString() : null);
						stuCrsRegActy.setAdmissionYear(courseRegActivity.get(0)[4] != null
								? (int) Double.parseDouble(courseRegActivity.get(0)[4].toString())
								: 0);
						stuCrsRegActy.setProgSpecializationId(courseRegActivity.get(0)[5] != null
								? Integer.parseInt(courseRegActivity.get(0)[5].toString())
								: 0);
						stuCrsRegActy.setStudySystem(
								courseRegActivity.get(0)[6] != null ? courseRegActivity.get(0)[6].toString() : null);
						stuCrsRegActy.setEducationStatusDescription(
								courseRegActivity.get(0)[7] != null ? courseRegActivity.get(0)[7].toString() : null);
						stuCrsRegActy.setLockStatus(courseRegActivity.get(0)[8] != null
								? Integer.parseInt(courseRegActivity.get(0)[8].toString())
								: 0);
						stuCrsRegActy.setExamGraduationStatus(courseRegActivity.get(0)[9] != null
								? Integer.valueOf(courseRegActivity.get(0)[9].toString())
								: 0);
						stuCrsRegActy.setSemesterSubId(
								courseRegActivity.get(0)[10] != null ? courseRegActivity.get(0)[10].toString() : null);
						stuCrsRegActy.setSemesterDesc(
								courseRegActivity.get(0)[11] != null ? courseRegActivity.get(0)[11].toString() : null);
						stuCrsRegActy.setClassGroupId(
								courseRegActivity.get(0)[12] != null ? courseRegActivity.get(0)[12].toString() : null);
						stuCrsRegActy.setClassGroupDesc(
								courseRegActivity.get(0)[13] != null ? courseRegActivity.get(0)[13].toString() : null);
						stuCrsRegActy.setProgGroupDuration(courseRegActivity.get(0)[14] != null
								? Integer.parseInt(courseRegActivity.get(0)[14].toString())
								: 0);
						stuCrsRegActy.setProgGroupId(courseRegActivity.get(0)[15] != null
								? Integer.parseInt(courseRegActivity.get(0)[15].toString())
								: 0);
						stuCrsRegActy.setCentreId(courseRegActivity.get(0)[16] != null
								? Integer.parseInt(courseRegActivity.get(0)[16].toString())
								: 0);
						stuCrsRegActy.setStartTimestamp(courseRegActivity.get(0)[17] != null
								? LocalDateTime.parse(courseRegActivity.get(0)[17].toString(), formatter)
								: null);
						stuCrsRegActy.setEndTimestamp(courseRegActivity.get(0)[18] != null
								? LocalDateTime.parse(courseRegActivity.get(0)[18].toString(), formatter)
								: null);
						stuCrsRegActy.setStudentName(
								courseRegActivity.get(0)[19] != null ? courseRegActivity.get(0)[19].toString() : null);
						stuCrsRegActy.setGender(
								courseRegActivity.get(0)[20] != null ? courseRegActivity.get(0)[20].toString() : null);
						stuCrsRegActy.setApplicationNumber(courseRegActivity.get(0)[21] != null
								? Long.parseLong(courseRegActivity.get(0)[21].toString())
								: 0L);
						stuCrsRegActy.setProgSpecializationCode(
								courseRegActivity.get(0)[22] != null ? courseRegActivity.get(0)[22].toString() : null);
						stuCrsRegActy.setProgGroupDescription(
								courseRegActivity.get(0)[23] != null ? courseRegActivity.get(0)[23].toString() : null);
						stuCrsRegActy.setProgGroupMode(
								courseRegActivity.get(0)[24] != null ? courseRegActivity.get(0)[24].toString() : null);
						stuCrsRegActy.setProgGroupLevel(
								courseRegActivity.get(0)[25] != null ? courseRegActivity.get(0)[25].toString() : null);
						stuCrsRegActy.setCentreDescription(
								courseRegActivity.get(0)[26] != null ? courseRegActivity.get(0)[26].toString() : null);
						stuCrsRegActy.setEducationStatus(
								courseRegActivity.get(0)[27] != null ? courseRegActivity.get(0)[27].toString() : null);
						stuCrsRegActy.setExamGraduationStatus(courseRegActivity.get(0)[9] != null
								? Integer.parseInt(courseRegActivity.get(0)[9].toString())
								: 0);
						stuCrsRegActy.setEmail(
								courseRegActivity.get(0)[28] != null ? courseRegActivity.get(0)[28].toString() : null);
						stuCrsRegActy.setMobile(
								courseRegActivity.get(0)[29] != null ? courseRegActivity.get(0)[29].toString() : null);
						stuCrsRegActy.setSemesterId(courseRegActivity.get(0)[30] != null
								? Integer.parseInt(courseRegActivity.get(0)[30].toString())
								: 0);

						EPTHscFeeDescCGPA = activityRepo
								.findEPTMarkHscGroupFeeDescCGPAForCourseRegByRegisterNumber(registerNumber);

						if (EPTHscFeeDescCGPA != null && !EPTHscFeeDescCGPA.isEmpty()) {
							stuCrsRegActy.setEptMark(EPTHscFeeDescCGPA.get(0)[1] != null
									? Integer.parseInt(EPTHscFeeDescCGPA.get(0)[1].toString())
									: 0);
							stuCrsRegActy.setHscGroup(
									EPTHscFeeDescCGPA.get(0)[2] != null ? EPTHscFeeDescCGPA.get(0)[2].toString()
											: null);
							stuCrsRegActy.setFeeCategoryDescription(
									EPTHscFeeDescCGPA.get(0)[3] != null ? EPTHscFeeDescCGPA.get(0)[3].toString()
											: null);
							stuCrsRegActy.setTotalCreditsRegistered(EPTHscFeeDescCGPA.get(0)[4] != null
									? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[4].toString())
									: 0.0f);
							stuCrsRegActy.setTotalCreditsEarned(EPTHscFeeDescCGPA.get(0)[5] != null
									? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[5].toString())
									: 0.0f);
							stuCrsRegActy.setCumulativeGradePointAverage(EPTHscFeeDescCGPA.get(0)[6] != null
									? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[6].toString())
									: 0.0f);
						}
						if (LocalDateTime.now().isBefore(stuCrsRegActy.getStartTimestamp())) {
							stuCrsRegActy.setMessage("HAS NOT STARTED.");
						} else if (LocalDateTime.now().isBefore(stuCrsRegActy.getEndTimestamp())
								&& LocalDateTime.now().isAfter(stuCrsRegActy.getStartTimestamp())) {
							stuCrsRegActy.setMessage("SUCCESS");
							stuCrsRegActy.setWishlistStatus(activityRepo
									.findWishListRegistrationStatusBySemesterSubIdAndClassGroupIdAndRegisterNumber(
											stuCrsRegActy.getSemesterSubId(), stuCrsRegActy.getClassGroupId(),
											registerNumber));
						} else {
							stuCrsRegActy.setMessage("Not Eligible for Current Registration-3");
						}

						// Minor Opted Check
						List<AdditionalLearningOpted> addlLearningList = optedService
								.findAddlLearningByRegNoAndLearnType(registerNumber, "MINOR");

						if (addlLearningList != null && !addlLearningList.isEmpty()) {
							stuCrsRegActy
									.setOptedCBCSMinor(addlLearningList.get(0).getId().getAdditionalLearningCode());
						}

						// Honour Opted Check
						List<AdditionalLearningOpted> addlLearningHonList = optedService
								.findAddlLearningByRegNoAndLearnType(registerNumber, "HONOUR");

						if (addlLearningHonList != null && !addlLearningHonList.isEmpty()) {
							stuCrsRegActy
									.setOptedCBCSHonour(addlLearningHonList.get(0).getId().getAdditionalLearningCode());
						}

					} else {
						stuCrsRegActy.setMessage("Degree Eligibility has been processed");
					}

				}
			} else {
				stuCrsRegActy.setMessage("Not Eligible for Current Registration-4");
			}
		} catch (Exception e) {
			e.printStackTrace();
			stuCrsRegActy.setMessage("FAIL");
		}

		return stuCrsRegActy;
	}

	public StudentInformation getAddDropCourseRegistrationStudentDetailByRegisterNumber(String registerNumber) {

		List<Object[]> courseRegActivity;
		List<Object[]> EPTHscFeeDescCGPA;

		StringBuilder message = new StringBuilder();

		try {

			courseRegActivity = activityRepo.findScheduleForAddDropCourseRegDetailByRegisterNumber(registerNumber);

			if (courseRegActivity != null && !courseRegActivity.isEmpty()) {
				if (courseRegActivity.size() > 1) {

					message.append("More than one activity is enabled now. You can’t proceed. Try again later.");

					for (Object[] objects : courseRegActivity) {

						message.append(" Semester : ").append(objects[10].toString()).append(" - ")
								.append(courseRegActivity.get(0)[11].toString()).append(" ClassGroup : ")
								.append(objects[13].toString());
					}

				} else {

					setCourseRegistrationStudentDetailActivityDTO(stuCrsRegActy, courseRegActivity);

					EPTHscFeeDescCGPA = activityRepo
							.findEPTMarkHscGroupFeeDescCGPAForCourseRegByRegisterNumber(registerNumber);

					if (EPTHscFeeDescCGPA != null && !EPTHscFeeDescCGPA.isEmpty()) {
						stuCrsRegActy.setEptMark(EPTHscFeeDescCGPA.get(0)[1] != null
								? Integer.parseInt(EPTHscFeeDescCGPA.get(0)[1].toString())
								: 0);
						stuCrsRegActy.setHscGroup(
								EPTHscFeeDescCGPA.get(0)[2] != null ? EPTHscFeeDescCGPA.get(0)[2].toString() : null);
						stuCrsRegActy.setFeeCategoryDescription(
								EPTHscFeeDescCGPA.get(0)[3] != null ? EPTHscFeeDescCGPA.get(0)[3].toString() : null);
						stuCrsRegActy.setTotalCreditsRegistered(EPTHscFeeDescCGPA.get(0)[4] != null
								? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[4].toString())
								: 0.0f);
						stuCrsRegActy.setTotalCreditsEarned(EPTHscFeeDescCGPA.get(0)[5] != null
								? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[5].toString())
								: 0.0f);
						stuCrsRegActy.setCumulativeGradePointAverage(EPTHscFeeDescCGPA.get(0)[6] != null
								? Float.parseFloat(EPTHscFeeDescCGPA.get(0)[6].toString())
								: 0.0f);
					}

					if (LocalDateTime.now().isBefore(stuCrsRegActy.getStartTimestamp())) {
						stuCrsRegActy.setMessage("HAS NOT STARTED.");
					} else if (LocalDateTime.now().isBefore(stuCrsRegActy.getEndTimestamp())
							&& LocalDateTime.now().isAfter(stuCrsRegActy.getStartTimestamp())) {
						stuCrsRegActy.setMessage("SUCCESS");
						stuCrsRegActy.setWishlistStatus(activityRepo
								.findWishListRegistrationStatusBySemesterSubIdAndClassGroupIdAndRegisterNumber(
										stuCrsRegActy.getSemesterSubId(), stuCrsRegActy.getClassGroupId(),
										registerNumber));
					} else {
						stuCrsRegActy.setMessage("Not Eligible for Current Registration-5");
					}

					// Minor Opted Check
					List<AdditionalLearningOpted> addlLearningList = optedService
							.findAddlLearningByRegNoAndLearnType(registerNumber, "MINOR");

					if (addlLearningList != null && !addlLearningList.isEmpty()) {
						stuCrsRegActy.setOptedCBCSMinor(addlLearningList.get(0).getId().getAdditionalLearningCode());
					}

					// Honour Opted Check
					List<AdditionalLearningOpted> addlLearningHonList = optedService
							.findAddlLearningByRegNoAndLearnType(registerNumber, "HONOUR");

					if (addlLearningHonList != null && !addlLearningHonList.isEmpty()) {
						stuCrsRegActy
								.setOptedCBCSHonour(addlLearningHonList.get(0).getId().getAdditionalLearningCode());
					}

				}
			} else {
				stuCrsRegActy.setMessage("Not Eligible for Current Registration-6");
			}
		} catch (Exception e) {
			e.printStackTrace();
			stuCrsRegActy.setMessage("FAIL");
		}

		return stuCrsRegActy;
	}

	public void commonFunctionStudProgressInfo(HttpSession session, Model model) {
		String specCode = (String) session.getAttribute("ProgramSpecCode");
		String specDesc = (String) session.getAttribute("ProgramSpecDesc");
		int studyStartYear = (Integer) session.getAttribute("StudyStartYear");
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

		Map<String, String> eligibilityResult = new LinkedHashMap<>();
		eligibilityResult.put(REGISTRATION_SCHEDULE_CHECK,
				details.getStartTimestamp().format(format) + " till " + details.getEndTimestamp().format(format));
		eligibilityResult.put(WISHLIST_CHECK, "NOT APPLICABLE");
		eligibilityResult.put(ACAEMIC_YEAR_CHECK, studyStartYear + "");
		eligibilityResult.put(PGM_SPEC_CHECK, specCode + " - " + specDesc);
		eligibilityResult.put(PGM_MODE_CHECK, details.getProgGroupMode());

		model.addAttribute("eligibilityResult", eligibilityResult);
	}

}
