package org.vtop.CourseRegistration;

import javax.servlet.http.HttpSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.filter.GenericFilterBean;
import org.vtop.CourseRegistration.model.StudentInformation;

@SpringBootApplication
@EnableRedisHttpSession
public class RegistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistrationApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<GenericFilterBean> loggingFilter() {
		FilterRegistrationBean<GenericFilterBean> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(appFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(Integer.MAX_VALUE);

		return registrationBean;
	}
	
	@Bean
	@SessionScope
	public StudentInformation studInfo(HttpSession session) {
		StudentInformation sInfo = (StudentInformation) session.getAttribute("sInfo");
		if(sInfo==null){
			sInfo=new StudentInformation();
			session.setAttribute("sInfo", sInfo);
		}
		return sInfo;
	}
	
	@Bean
	@SessionScope
	public GlobalMaster user(HttpSession session) {
		GlobalMaster globalValues = (GlobalMaster) session.getAttribute("globalValues");
		if(globalValues==null){
			globalValues=new GlobalMaster();
			session.setAttribute("globalValues", globalValues);
		}
		return globalValues;
	}


	public GenericFilterBean appFilter() {
		return new AppFilter();
	}
}

