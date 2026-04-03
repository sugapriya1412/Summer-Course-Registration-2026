package org.vtop.CourseRegistration.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vtop.CourseRegistration.repository.VtopMobileAppAccessHistoryRepository;

@Service
public class VtopMobileAppAccessHistoryService {
       
        @Autowired
        private VtopMobileAppAccessHistoryRepository vtopMobileAppAccessHistoryRepository;


        public String findByRegisterNo(String registerNumber)
        {
                return vtopMobileAppAccessHistoryRepository.findFcmByRegisterNumber(registerNumber);
        }

    
}
