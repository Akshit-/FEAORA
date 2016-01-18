package model;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import randomization.TextParser;

public class DistributionTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TextParser parser = new TextParser();


		IntegerDistribution id = (IntegerDistribution) parser.parseText("uniform()", TextParser.INTEGER);
		for(int i=1;i<101;i++){
			System.out.print(", "+id.sample());
		}
		System.out.print("\n");
		RealDistribution rd = (RealDistribution) parser.parseText("exponential(4,0)", TextParser.REAL);
		for(int i=1;i<101;i++){
			System.out.print(", "+rd.sample());
		}
	}

}
