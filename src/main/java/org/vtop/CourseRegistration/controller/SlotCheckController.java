package org.vtop.CourseRegistration.controller;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SlotCheckController 
{

	@RequestMapping("/doValidate/sessionSlotTime")
	public String slotValidate(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) 
						throws ServletException, IOException, ParseException
	{	
		
		
		return "SlotTimeOver";
		
		
	}
	
	@RequestMapping("/duplicate/sessionError")
	public String doDuplicateSession(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) 
						throws ServletException, IOException, ParseException
	{	
		
		
		return "DuplicateSessionError";
		
		
	}
}
