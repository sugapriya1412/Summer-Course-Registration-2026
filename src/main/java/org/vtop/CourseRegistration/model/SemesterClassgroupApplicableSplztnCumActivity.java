package org.vtop.CourseRegistration.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the semester_classgroup_applicable_splztn_cum_activity database table.
 * 
 */
@Entity
@Table(name="semester_classgroup_applicable_splztn_cum_activity",  schema="ACADEMICS")
@NamedQuery(name="SemesterClassgroupApplicableSplztnCumActivity.findAll", query="SELECT s FROM SemesterClassgroupApplicableSplztnCumActivity s")
public class SemesterClassgroupApplicableSplztnCumActivity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SemesterClassgroupApplicableSplztnCumActivityPK id;

	@Column(name="adddrop_activity_control_master_id")
	private Integer adddropActivityControlMasterId;

	@Column(name="lock_status")
	private Integer lockStatus;

	@Column(name="log_ipaddress")
	private String logIpaddress;

	@Column(name="log_timestamp")
	private Timestamp logTimestamp;

	@Column(name="log_userid")
	private String logUserid;

	@Column(name="mock_activity_control_master_id")
	private Integer mockActivityControlMasterId;

	@Column(name="registration_activity_control_master_id")
	private Integer registrationActivityControlMasterId;

	@Column(name="wishlist_activity_control_master_id")
	private Integer wishlistActivityControlMasterId;

	@Column(name="withdraw_activity_control_master_id")
	private Integer withdrawActivityControlMasterId;

	public SemesterClassgroupApplicableSplztnCumActivity() {
	}

	public SemesterClassgroupApplicableSplztnCumActivityPK getId() {
		return this.id;
	}

	public void setId(SemesterClassgroupApplicableSplztnCumActivityPK id) {
		this.id = id;
	}

	public Integer getAdddropActivityControlMasterId() {
		return this.adddropActivityControlMasterId;
	}

	public void setAdddropActivityControlMasterId(Integer adddropActivityControlMasterId) {
		this.adddropActivityControlMasterId = adddropActivityControlMasterId;
	}

	public Integer getLockStatus() {
		return this.lockStatus;
	}

	public void setLockStatus(Integer lockStatus) {
		this.lockStatus = lockStatus;
	}

	public String getLogIpaddress() {
		return this.logIpaddress;
	}

	public void setLogIpaddress(String logIpaddress) {
		this.logIpaddress = logIpaddress;
	}

	public Timestamp getLogTimestamp() {
		return this.logTimestamp;
	}

	public void setLogTimestamp(Timestamp logTimestamp) {
		this.logTimestamp = logTimestamp;
	}

	public String getLogUserid() {
		return this.logUserid;
	}

	public void setLogUserid(String logUserid) {
		this.logUserid = logUserid;
	}

	public Integer getMockActivityControlMasterId() {
		return this.mockActivityControlMasterId;
	}

	public void setMockActivityControlMasterId(Integer mockActivityControlMasterId) {
		this.mockActivityControlMasterId = mockActivityControlMasterId;
	}

	public Integer getRegistrationActivityControlMasterId() {
		return this.registrationActivityControlMasterId;
	}

	public void setRegistrationActivityControlMasterId(Integer registrationActivityControlMasterId) {
		this.registrationActivityControlMasterId = registrationActivityControlMasterId;
	}

	public Integer getWishlistActivityControlMasterId() {
		return this.wishlistActivityControlMasterId;
	}

	public void setWishlistActivityControlMasterId(Integer wishlistActivityControlMasterId) {
		this.wishlistActivityControlMasterId = wishlistActivityControlMasterId;
	}

	public Integer getWithdrawActivityControlMasterId() {
		return this.withdrawActivityControlMasterId;
	}

	public void setWithdrawActivityControlMasterId(Integer withdrawActivityControlMasterId) {
		this.withdrawActivityControlMasterId = withdrawActivityControlMasterId;
	}

}