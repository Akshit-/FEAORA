package pcgui;

import java.util.ArrayList;
import java.util.List;

import model.Distribution;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import randomization.StepDistribution;



public class Symbol<T> {
	
	public String typeString;
	public int size;
	public int dimension; //only for case of array
	public String name;
	public String mode;
	public String externalFile;
	public String excelFileRange;
	public String distFunction;
	public boolean trackingEnabled;
	
	/**
	 * @return the trackingEnabled
	 */
	public boolean isTrackingEnabled() {
		return trackingEnabled;
	}

	/**
	 * @param trackingEnabled the trackingEnabled to set
	 */
	public void setTrackingEnabled(boolean trackingEnabled) {
		this.trackingEnabled = trackingEnabled;
	}

	T symbol;
	public int indexInList;
	public boolean isStepDist=false;
	public IntegerDistribution iDist;
	public RealDistribution rDist;
	public StepDistribution stepDist;
	public boolean isDependent;
	public Distribution distribution;
	public Object apacheDist;
	public List<String> arrayParams;
	public boolean isArray;//is this symbol is an array
	public boolean isDependentDimension;//if the array are variable symbols
	/**
	 * @return the apacheDist
	 */
	public Object getApacheDist() {
		return apacheDist;
	}

	/**
	 * @return the isArray
	 */
	public boolean isArray() {
		return isArray;
	}

	/**
	 * @param isArray the isArray to set
	 */
	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	/**
	 * @param apacheDist the apacheDist to set
	 */
	public void setApacheDist(Object apacheDist) {
		this.apacheDist = apacheDist;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	public boolean isDependent() {
		return isDependent;
	}

	public void setDependent(boolean isDependent) {
		this.isDependent = isDependent;
	}

	public StepDistribution getStepDist() {
		return stepDist;
	}

	public void setStepDist(StepDistribution stepDist) {
		this.stepDist = stepDist;
	}

	public IntegerDistribution getIntegerDistribution() {
		return iDist;
	}

	public void setIntegerDistribution(IntegerDistribution iDist) {
		this.iDist = iDist;
	}

	public RealDistribution getRealDistribution() {
		return rDist;
	}

	public void setRealDistribution(RealDistribution rDist) {
		this.rDist = rDist;
	}

	public T get(){
        return this.symbol;
    }
     
    public void set(T t1){
        this.symbol=t1;
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Symbol other = (Symbol) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	

	
}
