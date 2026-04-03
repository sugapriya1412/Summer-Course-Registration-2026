package org.vtop.CourseRegistration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.vtop.CourseRegistration.model.CourseEquivalancesModel;
import org.vtop.CourseRegistration.model.CourseEquivalancesPKModel;

@Repository
public interface CourseEquivalncesRepository extends JpaRepository<CourseEquivalancesModel, CourseEquivalancesPKModel>
{

	@Query("select a from CourseEquivalancesModel a where  courseCode in (?1) ")
	List<CourseEquivalancesModel> findByCourseCodeAndEquivalentCourseCode(List<String> courseCode);
}
