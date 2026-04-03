package org.vtop.CourseRegistration.model;


import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the additional_learning_opted database table.
 * 
 */
@Embeddable
public class AdditionalLearningOptedPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="stdntslgndtls_register_number")
	private String stdntslgndtlsRegisterNumber;

	@Column(name="additional_learning_code")
	private String additionalLearningCode;

    public AdditionalLearningOptedPK() {
    }
	public String getStdntslgndtlsRegisterNumber() {
		return this.stdntslgndtlsRegisterNumber;
	}
	public void setStdntslgndtlsRegisterNumber(String stdntslgndtlsRegisterNumber) {
		this.stdntslgndtlsRegisterNumber = stdntslgndtlsRegisterNumber;
	}
	public String getAdditionalLearningCode() {
		return this.additionalLearningCode;
	}
	public void setAdditionalLearningCode(String additionalLearningCode) {
		this.additionalLearningCode = additionalLearningCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AdditionalLearningOptedPK)) {
			return false;
		}
		AdditionalLearningOptedPK castOther = (AdditionalLearningOptedPK)other;
		return 
			this.stdntslgndtlsRegisterNumber.equals(castOther.stdntslgndtlsRegisterNumber)
			&& this.additionalLearningCode.equals(castOther.additionalLearningCode);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.stdntslgndtlsRegisterNumber.hashCode();
		hash = hash * prime + this.additionalLearningCode.hashCode();
		
		return hash;
    }
}