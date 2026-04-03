package org.vtop.CourseRegistration.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name="VTOP_MOBILE_APP_ACCESS_HISTORY" ,schema="mobileapp")
public class VtopMobileAppAccessHistory 
{
	@Id
	@Column(name="FCM_TOKEN")
	private String fcmToken;
	
	@Column(name="STDNTSLGNDTLS_REGISTER_NUMBER")
	private String registerNo;
	
	@Column(name="IMEI_NUMBER")
	private String imeiNumber;
	
	@Column(name="PERSONAL_EMAIL")
	private String personalEmail;
	
	@Column(name="OLD_PASSWORD")
	private String oldPassword;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOG_TIMESTAMP")
	private Date logTimestamp;
	
	@Column(name="TOKEN_EXPIRY_TIME")
	private String tokenExpiryTime;
	
	@Column(name="KEY")
	private String keyEnc;
	
	@Column(name="IV")
	private String ivEnc;

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public String getRegisterNo() {
		return registerNo;
	}

	public void setRegisterNo(String registerNo) {
		this.registerNo = registerNo;
	}

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public String getPersonalEmail() {
		return personalEmail;
	}

	public void setPersonalEmail(String personalEmail) {
		this.personalEmail = personalEmail;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public Date getLogTimestamp() {
		return logTimestamp;
	}

	public void setLogTimestamp(Date logTimestamp) {
		this.logTimestamp = logTimestamp;
	}

	public String getTokenExpiryTime() {
		return tokenExpiryTime;
	}

	public void setTokenExpiryTime(String tokenExpiryTime) {
		this.tokenExpiryTime = tokenExpiryTime;
	}
		

	public String getKeyEnc() {
		return keyEnc;
	}

	public void setKeyEnc(String keyEnc) {
		this.keyEnc = keyEnc;
	}

	public String getIvEnc() {
		return ivEnc;
	}

	public void setIvEnc(String ivEnc) {
		this.ivEnc = ivEnc;
	}

	@Override
	public String toString() {
		return "VtopMobileAppAccessHistory [fcmToken=" + fcmToken + ", registerNo=" + registerNo + ", imeiNumber="
				+ imeiNumber + ", personalEmail=" + personalEmail + ", oldPassword=" + oldPassword + ", logTimestamp="
				+ logTimestamp + ", tokenExpiryTime=" + tokenExpiryTime + ", keyEnc=" + keyEnc + ", ivEnc=" + ivEnc
				+ "]";
	}	
	
	
}
