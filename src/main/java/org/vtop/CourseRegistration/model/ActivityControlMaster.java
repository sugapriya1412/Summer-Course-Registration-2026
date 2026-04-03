package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the activity_control_master database table.
 * 
 */
@Entity
@Table(name="activity_control_master", schema="ACADEMICS")
@NamedQuery(name="ActivityControlMaster.findAll", query="SELECT a FROM ActivityControlMaster a")
public class ActivityControlMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="activity_control_master_id")
	private Integer activityControlMasterId;

	@Column(name="activity_master_activity_id")
	private Integer activityMasterActivityId;

	@Column(name="create_ipaddress")
	private String createIpaddress;

	@Column(name="create_timestamp")
	private Timestamp createTimestamp;

	@Column(name="create_userid")
	private String createUserid;

	@Column(name="end_timestamp")
	private Timestamp endTimestamp;

	@Column(name="extension_timestamp")
	private Timestamp extensionTimestamp;

	@Column(name="lock_status")
	private Integer lockStatus;

	@Column(name="start_timestamp")
	private Timestamp startTimestamp;

	@Column(name="update_ipaddress")
	private String updateIpaddress;

	@Column(name="update_timestamp")
	private Timestamp updateTimestamp;

	@Column(name="update_userid")
	private String updateUserid;

	public ActivityControlMaster() {
	}

	public Integer getActivityControlMasterId() {
		return this.activityControlMasterId;
	}

	public void setActivityControlMasterId(Integer activityControlMasterId) {
		this.activityControlMasterId = activityControlMasterId;
	}

	public Integer getActivityMasterActivityId() {
		return this.activityMasterActivityId;
	}

	public void setActivityMasterActivityId(Integer activityMasterActivityId) {
		this.activityMasterActivityId = activityMasterActivityId;
	}

	public String getCreateIpaddress() {
		return this.createIpaddress;
	}

	public void setCreateIpaddress(String createIpaddress) {
		this.createIpaddress = createIpaddress;
	}

	public Timestamp getCreateTimestamp() {
		return this.createTimestamp;
	}

	public void setCreateTimestamp(Timestamp createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public String getCreateUserid() {
		return this.createUserid;
	}

	public void setCreateUserid(String createUserid) {
		this.createUserid = createUserid;
	}

	public Timestamp getEndTimestamp() {
		return this.endTimestamp;
	}

	public void setEndTimestamp(Timestamp endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public Timestamp getExtensionTimestamp() {
		return this.extensionTimestamp;
	}

	public void setExtensionTimestamp(Timestamp extensionTimestamp) {
		this.extensionTimestamp = extensionTimestamp;
	}

	public Integer getLockStatus() {
		return this.lockStatus;
	}

	public void setLockStatus(Integer lockStatus) {
		this.lockStatus = lockStatus;
	}

	public Timestamp getStartTimestamp() {
		return this.startTimestamp;
	}

	public void setStartTimestamp(Timestamp startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public String getUpdateIpaddress() {
		return this.updateIpaddress;
	}

	public void setUpdateIpaddress(String updateIpaddress) {
		this.updateIpaddress = updateIpaddress;
	}

	public Timestamp getUpdateTimestamp() {
		return this.updateTimestamp;
	}

	public void setUpdateTimestamp(Timestamp updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public String getUpdateUserid() {
		return this.updateUserid;
	}

	public void setUpdateUserid(String updateUserid) {
		this.updateUserid = updateUserid;
	}

}