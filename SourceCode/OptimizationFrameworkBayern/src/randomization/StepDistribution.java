package randomization;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;

public class StepDistribution implements IntegerDistribution {
	
	int min=1;
	int max;
	int step=1;
	int sampledCount=0;
	
	public StepDistribution(int max){
		this.max=max;
	}
	
	public StepDistribution(int min,int max){
		this.max=max;
		this.min=min;
	}
	
	public StepDistribution(int min,int max, int step){
		this.max=max;
		this.min=min;
		this.step = step;
	}
	

	@Override
	public double cumulativeProbability(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double cumulativeProbability(int x0, int x1)
			throws NumberIsTooLargeException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNumericalMean() {
		// TODO Auto-generated method stub
		return (min+max)/2;
	}

	@Override
	public double getNumericalVariance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSupportLowerBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSupportUpperBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int inverseCumulativeProbability(double p)
			throws OutOfRangeException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSupportConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double probability(int x) {
		if(x>=min && x<=max){
			return 1/((max-min)/step);
		}
		return 0;
	}

	@Override
	public void reseedRandomGenerator(long seed) {
		// TODO Auto-generated method stub

	}

	@Override
	public int sample(){
		// TODO Auto-generated method stub
		int val = min+(step*sampledCount);
		if(val<=max){
			sampledCount++;
			return val;
		} else {
			//re-iterate the step distribution
			sampledCount = 1;
			return min;
		}
		
	}
	
	public int getStepCount(){
		return ((int)((max - min) / step) +1);
	}

	@Override
	public int[] sample(int sampleSize) {
		// TODO Auto-generated method stub
		return null;
	}

}
