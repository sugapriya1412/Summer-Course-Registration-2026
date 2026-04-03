package org.vtop.CourseRegistration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vtop.CourseRegistration.repository.CurriculumCategoryMasterRepository;

@Service
public class CurriculumCategoryMasterService {

	@Autowired private CurriculumCategoryMasterRepository curriculumCategoryMasterRepository;
	
	public List<String> getCreditExceedAllowedCategories()
	{
		return curriculumCategoryMasterRepository.getCreditExceedAllowedCategories();
	}
}
