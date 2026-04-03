package org.vtop.CourseRegistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.CurriculumCategoryMaster;

@Repository
public interface CurriculumCategoryMasterRepository extends JpaRepository
<CurriculumCategoryMaster, String> {

	@Query(value="select course_category  from academics.curriculum_category_master ccm  where "
			+ " excess_credit_allowed = 1 " , nativeQuery = true)
	public List<String> getCreditExceedAllowedCategories();
}
