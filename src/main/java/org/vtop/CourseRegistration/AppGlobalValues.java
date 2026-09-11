package org.vtop.CourseRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppGlobalValues 
{
	
	public static int  REGISTRATION_SEMESTER_ACADEMIC_YEAR=2026;
	public static int  REGISTRATION_SEMESTER_GRADUATE_YEAR=2027;
	
	public static String ACTIVITY_EVENT = "REGISTRATION"; //REGISTRATION;ADDDROP;MOCK_COURSE_REGISTRATION

	public static int TEST_STATUS =2; //Login with Password & Captcha-> 1: Enable/ 2: Disable
	public static int OTP_STATUS =2; //OTP Send Status-> 1: Enable/ 2: Disable
	
	public static boolean IS_ALLOWED_CBCS_MIN = true;
	
	public static boolean IS_ALLOWED_CBCS_HON = true;
	
	public static boolean IS_ALLOWED_ACE_MIN = true;
    
	
	
	public static float CBCS_HON_CGPA = 7.5f;
	
	//4 cgpa check
	public static boolean MAX_CREDIT_CGPA_CHECK = true;


	
	//For WEI Intra No Max credit from Winter 2022-23
		//For WEI Intra No Max credit from Winter 2022-23 MAX_CREDIT_CHECK_REQUIRED = false;
	
	public static boolean MAX_CREDIT_CHECK_REQUIRED = true;

	public static int CORE_CATEGORY_COURSE_STATUS =1; //Core Category Course Allow Status-> 1: Enable/ 2: Disable
	public static int OPTION_NA_STATUS = 1; //Option Not Allowed Status-> 1: Enable/ 2: Disable
	
	public static String CAPSTONE_PROJECT_DURATION="6"; //For FALL -12 MONTHS ; WINTER -6  MONTHS
	
	
	public static String TESTING_MAIL_ID = "sugapriya.s@vit.ac.in";

	public static String REG_ERROR_METHOD = "FALLSEM2026-27_REG";
	public static String[] CLASS_TYPE = new String[] {"BFS","EFS"};
	public static String CAMPUS_CODE = "CHN";

	public static int BUTTONS_TO_SHOW = 5;
	public static int INITIAL_PAGE = 0;
	public static int INITIAL_PAGE_SIZE = 10;
	public static int[] PAGE_SIZES = new int[] {10, 20, 30, 40};

	public static final int KEY_LENGTH = 21; //Key length to generate hash value

	public static List<String> CR_COURSE_OPTION = new ArrayList<String>(Arrays.asList("RGR", "RGCE", "RGP", "RGW", "RPCE", "RWCE", "RR","MIN","RDEOE", "RSEOE","RRCE","HON","CS"));
	public static List<String> REG_COURSE_OPTION = new ArrayList<String>(Arrays.asList("RGR", "RGCE", "RGP", "RGW", "RPCE", "RWCE", "RPEUE", "RUCUE", "RGVC", "RUEPE", "RWVC","RDEOE","RSEOE","RRCE"));

	public static List<String> ADMIN_IP_ADDRESS = new ArrayList<String>(Arrays.asList(""));
	
	
	public static List<String> CBCS_FC_BASKET_IDS_CS = new ArrayList<String>(Arrays.asList("CH_BFLE200L_00100","CH_BHSM200L_00100"));

	//Seeion timed out 10mins
	public static int MAX_INACTIVE_SESSION_TIME_OUT=10*60;
	
	
	
}
