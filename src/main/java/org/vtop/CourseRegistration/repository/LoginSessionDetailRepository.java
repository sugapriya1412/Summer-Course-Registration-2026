package org.vtop.CourseRegistration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.LoginSessionDetail;

@Repository
public interface LoginSessionDetailRepository extends JpaRepository<LoginSessionDetail, String> {
        
	@Query(value = "select current_user ",nativeQuery=true)
	String getUser();
	
}
