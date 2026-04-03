package org.vtop.CourseRegistration.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vtop.CourseRegistration.model.LoginSessionDetail;
import org.vtop.CourseRegistration.repository.LoginSessionDetailRepository;

@Service
public class LoginSessionDetailsService {

    @Autowired
    private LoginSessionDetailRepository sessionRepository;
    

    @Transactional(readOnly=false)
    public void saveLogin(String sessionId,String name,String ipAddress) {
    	
            LoginSessionDetail data = new LoginSessionDetail();

            data.setSessionId(sessionId);
            data.setApplicationName("Course registration");
            data.setPrincipalName(name);
            data.setLogIpaddress(ipAddress);
            data.setSessionCreatedTime(LocalDateTime.now());
            data.setRemarks("-");

            sessionRepository.save(data);
    }

    @Transactional(readOnly=false)
    public void closeLogin(String sessionId) {
    	
            Optional<LoginSessionDetail> data = sessionRepository.findById(sessionId);

            if (data.isPresent()) {
                    data.get().setSessionClosedTime(LocalDateTime.now());
                    sessionRepository.save(data.get());
            }
    }
}
