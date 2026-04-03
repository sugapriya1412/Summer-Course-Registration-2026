package org.vtop.CourseRegistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.vtop.CourseRegistration.model.CourseEligibleModel;


public interface CourseEligibleRepository extends JpaRepository<CourseEligibleModel, String>
{
	@Query("select a from CourseEligibleModel a where a.groupId=?1 ")
	CourseEligibleModel findByProgGroupId(int progGroupId); 
}


