package org.vtop.CourseRegistration.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.vtop.CourseRegistration.service.CourseRegCommonService;
import org.vtop.CourseRegistration.service.CourseRegistrationCommonFunction;
import org.vtop.CourseRegistration.service.CustomUserDetailService;
import org.vtop.CourseRegistration.util.RegistrationConstants;

@Controller
public class CourseRegistrationLogoutController {
	@Autowired private CourseRegistrationCommonFunction courseRegCommonFn;

	private static final Logger LOGGER = LogManager.getLogger(CourseRegistrationLogoutController.class);

	@Autowired
	private CourseRegCommonService commonService;


	@RequestMapping(value = "SessionTimedOut", method = { RequestMethod.POST, RequestMethod.GET })
	public String sessionError(@CookieValue(value = "RegisterNumber") String registerNumber, Model model, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session) 
					throws ServletException, IOException 
	{
		String page = "";		
		Cookie[] cookies = request.getCookies();

		if (cookies!=null)
		{
			for (Cookie cookie : cookies) 
			{
				if(cookie.getName().equals(registerNumber))
				{
					cookie = new Cookie("RegisterNumber", null);
					cookie.setMaxAge(0);
					cookie.setSecure(true);
					cookie.setHttpOnly(true);
					response.addCookie(cookie);
					request.getSession().invalidate();				
				}				

				model.addAttribute("message", "Session Expired");
				model.addAttribute("error", "Try Logout and Log-in");
				model.addAttribute("errno", 3);
				page = "CustomErrorPage";
			}			
		}
		else
		{
			courseRegCommonFn.callCaptcha(request, response, session, model);			
			model.addAttribute("flag", 2);			
			page = "redirectpage";							
		}	

		//CustomUserDetailService.invalidateAuthentication(session, request, response);

		return page;
	}


	@RequestMapping("/login/error")
	public String loginError(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "error", required = false) String error)
	{
		String file = "StudentLogin";
		HttpSession session = request.getSession(false);
		String errMsg = "";

		try
		{
			AuthenticationException exp = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
			if (exp != null)
			{
				errMsg = exp.getMessage();
			}

			courseRegCommonFn.callCaptcha(request, response, session, model);

			//CustomUserDetailService.invalidateAuthentication(session, request, response);
		}
		catch (Exception exception)
		{
			LOGGER.trace(exception);
			errMsg = "Technical error....!";
		}
		model.addAttribute("CurrentDateTime", RegistrationConstants.doGetCurrentDateDispaly(new Date()));
		model.addAttribute("info", errMsg);

		return file;
	}

	@GetMapping("signOut")
	public String signOut(Model model,HttpServletRequest request,HttpSession session,
			HttpServletResponse response) throws ServletException, IOException 
	{
		model.addAttribute("message", " V TOP ");
		model.addAttribute("error", " Thank you For Using V TOP Course Registration Portal .");
		return "CustomErrorPage";
	}

	@RequestMapping(value = "processLogout", method = { RequestMethod.POST, RequestMethod.GET })
	public String doLogout(HttpSession session, HttpServletRequest request, HttpServletResponse response, 
			Model model) throws ServletException, IOException 
	{
		try 
		{	
			model.addAttribute("flag", 4);
			commonService.writeToLog(String.valueOf(session.getAttribute("RegisterNumber")),String.valueOf(session.getAttribute("IpAddress")),"LOGOUT");

			CustomUserDetailService.invalidateAuthentication(session, request, response);
		}
		catch (Exception exception) 
		{
			LOGGER.trace(exception);

			model.addAttribute("info", "Login with your Username and Password");
			courseRegCommonFn.callCaptcha(request, response, session, model);
			session.setAttribute("CAPTCHA",session.getAttribute("CAPTCHA"));

		}
		return "LogoutScreen";
	}

}
