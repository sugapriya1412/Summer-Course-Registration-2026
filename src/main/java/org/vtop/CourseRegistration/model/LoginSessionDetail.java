package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;


/**
 * The persistent class for the login_session_details database table.
 * 
 */
@Entity
@Table(name="login_session_details",schema = "vtopmaster")
@NamedQuery(name="LoginSessionDetail.findAll", query="SELECT l FROM LoginSessionDetail l")
public class LoginSessionDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="session_id")
	private String sessionId;

	@Column(name="application_name")
	private String applicationName;

	@Column(name="log_ipaddress")
	private String logIpaddress;

	@Column(name="principal_name")
	private String principalName;

	private String remarks;

	@Column(name="session_closed_time")
	private LocalDateTime sessionClosedTime;

	@Column(name="session_created_time")
	private LocalDateTime sessionCreatedTime;

	public LoginSessionDetail() {
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getApplicationName() {
		return this.applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getLogIpaddress() {
		return this.logIpaddress;
	}

	public void setLogIpaddress(String logIpaddress) {
		this.logIpaddress = logIpaddress;
	}

	public String getPrincipalName() {
		return this.principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getSessionClosedTime() {
		return this.sessionClosedTime;
	}

	public void setSessionClosedTime(LocalDateTime sessionClosedTime) {
		this.sessionClosedTime = sessionClosedTime;
	}

	public LocalDateTime getSessionCreatedTime() {
		return this.sessionCreatedTime;
	}

	public void setSessionCreatedTime(LocalDateTime sessionCreatedTime) {
		this.sessionCreatedTime = sessionCreatedTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((applicationName == null) ? 0 : applicationName.hashCode());
		result = prime * result + ((logIpaddress == null) ? 0 : logIpaddress.hashCode());
		result = prime * result + ((principalName == null) ? 0 : principalName.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((sessionClosedTime == null) ? 0 : sessionClosedTime.hashCode());
		result = prime * result + ((sessionCreatedTime == null) ? 0 : sessionCreatedTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginSessionDetail other = (LoginSessionDetail) obj;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (applicationName == null) {
			if (other.applicationName != null)
				return false;
		} else if (!applicationName.equals(other.applicationName))
			return false;
		if (logIpaddress == null) {
			if (other.logIpaddress != null)
				return false;
		} else if (!logIpaddress.equals(other.logIpaddress))
			return false;
		if (principalName == null) {
			if (other.principalName != null)
				return false;
		} else if (!principalName.equals(other.principalName))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (sessionClosedTime == null) {
			if (other.sessionClosedTime != null)
				return false;
		} else if (!sessionClosedTime.equals(other.sessionClosedTime))
			return false;
		if (sessionCreatedTime == null) {
			if (other.sessionCreatedTime != null)
				return false;
		} else if (!sessionCreatedTime.equals(other.sessionCreatedTime))
			return false;
		return true;
	}

}