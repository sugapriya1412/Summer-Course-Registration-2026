package org.vtop.CourseRegistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.CourseRegUserActivityLog;
import org.vtop.CourseRegistration.model.CourseRegUserActivityLogPK;

@Repository
public interface CourseRegUserActivityLogRepository 
extends JpaRepository<CourseRegUserActivityLog, CourseRegUserActivityLogPK>
{

}
