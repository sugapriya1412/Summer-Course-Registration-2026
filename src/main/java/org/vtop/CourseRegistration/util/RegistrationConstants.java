package org.vtop.CourseRegistration.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrationConstants 
{
	
	public static String doGetCurrentDateDispaly(Date date)
	{
		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(date);
	}

}
