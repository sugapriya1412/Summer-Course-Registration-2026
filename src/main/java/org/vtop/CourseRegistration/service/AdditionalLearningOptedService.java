package org.vtop.CourseRegistration.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.model.AdditionalLearningOpted;
import org.vtop.CourseRegistration.repository.AdditionalLearningOptedRepository;

@Transactional
@Service
public class AdditionalLearningOptedService 
{
	@Autowired
	private AdditionalLearningOptedRepository addlLearningOptedRepo;

	public List<AdditionalLearningOpted> findAddlLearningByRegNo(String regNo)
	{
		return addlLearningOptedRepo.findAddlLearningByRegNo(regNo);
	}

	public void saveAddlLearning(AdditionalLearningOpted opted)
	{
		addlLearningOptedRepo.save(opted);
	}
	public List<AdditionalLearningOpted> findAddlLearningByRegNoAndLearnType(String regNo,String learnType)
	{
		return addlLearningOptedRepo.findAddlLearningByRegNoAndLearnType(regNo,learnType);
	}
	public void deleteAddlLearning(String regNo, String addLearnCode, String userId, 
			String ipaddress, String commandType)
	{
		int insertStatus=0;
		insertStatus = addlLearningOptedRepo.insertByRegisterNumberAndAddLearnCode(regNo,addLearnCode, 
				 userId, ipaddress, commandType);
		if (insertStatus >0) {
			addlLearningOptedRepo.deleteByRegisterNumberAndAddLearnCode(regNo,addLearnCode);		
		}		
	}
}
