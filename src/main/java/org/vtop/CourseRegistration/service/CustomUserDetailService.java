package org.vtop.CourseRegistration.service;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailService implements UserDetailsService
{	
	private static final Logger logger = LogManager.getLogger(CustomUserDetailService.class);
			
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{	
		String[] userDetail = new String[] {};
		UserBuilder builder = null;
		
		if ((username != null) && (!username.equals("")))
		{
			userDetail = username.split("\\|");
		}
		logger.trace("\n userDetail: "+ userDetail[0]+" ;"+userDetail[1]+";"+userDetail[2]);
		
	    if (userDetail.length > 0)
	    {
	    	 builder = org.springframework.security.core.userdetails.User.withUsername(userDetail[0]);
	         builder.password(userDetail[1]);
	         builder.accountExpired(((Integer.parseInt(userDetail[2]) == 0) ? false : true));
	         builder.authorities(AuthorityUtils.NO_AUTHORITIES);
	         builder.roles("Student");
	    }
	    else
	    {
	    	throw new UsernameNotFoundException(" Invalid credentials. ");
	    }
	    
	    return builder.build();
	}
	
	
	public static void invalidateAuthentication(HttpSession session, HttpServletRequest request,
		      HttpServletResponse response)  throws IOException
	{

			    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			    if (auth != null) {
			      new SecurityContextLogoutHandler().logout(request, response, auth);
			      auth.setAuthenticated(false);
			      SecurityContextHolder.clearContext();
			      for (Cookie cookie : request.getCookies()) {
			        String cookieName = cookie.getName();
			        Cookie cookieToDelete = new Cookie(cookieName, null);
			        cookieToDelete.setPath(request.getContextPath() + "/");
			        cookieToDelete.setMaxAge(0);
			        response.addCookie(cookieToDelete);
			      }
			      SecurityContextHolder.getContext().setAuthentication(null);
			    }
			    
	}
}
