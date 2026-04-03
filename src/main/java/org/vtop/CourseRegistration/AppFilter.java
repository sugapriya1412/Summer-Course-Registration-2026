package org.vtop.CourseRegistration;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.vtop.CourseRegistration.model.CourseRegUserActivityLog;
import org.vtop.CourseRegistration.model.CourseRegUserActivityLogPK;
import org.vtop.CourseRegistration.service.CommonFunctions;
import org.vtop.CourseRegistration.service.CourseRegUserActivityLogService;


public class AppFilter extends GenericFilterBean {

	private static final Logger LOG = LoggerFactory.getLogger(AppFilter.class);
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session=request.getSession(); 
		String uri = request.getRequestURI();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.isAuthenticated()) {

			ServletContext servletContext = request.getServletContext();
			WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			CustomSessionManager sessionManager= webApplicationContext.getBean(CustomSessionManager.class);
			if(sessionManager!=null){
				sessionManager.validateLogin(session, request, response, authentication);
			}

		}

		try
		{
			LocalDateTime endTime = (LocalDateTime) session.getAttribute("endDateTime");

			/*
			Commented Based on Discussion Winter 2023-24 Add Drop
			String userId = authentication.getName().toUpperCase().trim();

			if (!uri.contains("/assets/") && !uri.contains("/webjars/") && !userId.equals("ANONYMOUSUSER")) {

				writeToLog(uri, request, response, session,
						session.getServletContext().getRealPath("/"),userId);
			}*/


			if(session!=null && !uri.startsWith("/logout") && endTime!=null) {

				boolean validateSlotTime = CommonFunctions.doValidateSlotToTime(session, req, res,endTime);
				
				if(validateSlotTime)
				{
					RequestDispatcher dispatcher= request.getRequestDispatcher("/doValidate/sessionSlotTime");
					dispatcher.forward(request, res);
					return;
				}

			}
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}

		chain.doFilter(request, res);                
	}

	
	

}
