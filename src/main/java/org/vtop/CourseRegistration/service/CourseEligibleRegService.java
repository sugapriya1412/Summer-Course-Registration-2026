package org.vtop.CourseRegistration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vtop.CourseRegistration.model.CourseEligibleModel;
import org.vtop.CourseRegistration.repository.CourseEligibleRepository;

@Service
public class CourseEligibleRegService {
	
	@Autowired private CourseEligibleRepository courseEligibleRepository;

	
	//Course Eligible
		public CourseEligibleModel getCourseEligibleByProgGroupId(int progGroupId)
		{
			return courseEligibleRepository.findByProgGroupId(progGroupId); 
		}
		
}
