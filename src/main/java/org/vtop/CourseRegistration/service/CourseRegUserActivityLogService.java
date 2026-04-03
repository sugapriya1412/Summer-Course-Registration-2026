package org.vtop.CourseRegistration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.model.CourseRegUserActivityLog;
import org.vtop.CourseRegistration.repository.CourseRegUserActivityLogRepository;

@Service
public class CourseRegUserActivityLogService 
{
	
	@Autowired
	private CourseRegUserActivityLogRepository activityLogRepo;
	
	@Transactional(readOnly=false)
	public void doSaveCourseRegActivityLog(CourseRegUserActivityLog log)
	{
		activityLogRepo.save(log);
	}
	

}
