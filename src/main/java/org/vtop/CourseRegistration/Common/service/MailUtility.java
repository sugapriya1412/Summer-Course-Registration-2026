package org.vtop.CourseRegistration.Common.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class MailUtility
{


	public static String triggerMail(String subject, String body, String attachementFilePath, String toEmail)
	{
		try {


			String FromEmailID = "";

			String url = "";

			String payload = "{\"from\":{\"email\":\""+FromEmailID+"\"},\"subject\":\""+subject+"\","
					+ "\"content\":[{\"type\":\"html\",\"value\":"
					+ "\""+body+"\"}],"
					+ "\"personalizations\":[{\"to\":[";

			List<String> toEmailList = Arrays.asList(toEmail.split(","));

			String emailStr = "";

			for (String string : toEmailList)
			{
				emailStr+= "{\"email\":\""+string+"\"},";
			}


			String stringWithoutLastChar = emailStr.substring(0, emailStr.length() - 1);


			payload+=stringWithoutLastChar+"]}]}";

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// Set request method
			con.setRequestMethod("POST");

			// Set request headers
			con.setRequestProperty("api_key", "");
			con.setRequestProperty("Content-Type", "application/json");

			// Enable input/output streams
			con.setDoOutput(true);

			// Write payload to the request
			try (OutputStream os = con.getOutputStream()) {
				byte[] input = payload.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			// Get response code
			int responseCode = con.getResponseCode();

			// Read response
			if (responseCode == 202) {
				return "SUCCESS";
			} else {
				return "FAIL";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "FAIL";
	}

}
