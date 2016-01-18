package randomization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;

import model.Distribution;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.GeometricDistribution;
import org.apache.commons.math3.distribution.LogisticDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import pcgui.Symbol;

public class TextParser {
	public static final int INTEGER = 1;
	public static final int REAL = 2;

	public Object parseText(String text, int type)
			throws IllegalArgumentException {
		// check for format
		if (text.contains("(") && text.contains(")")) {
			// extract the type of distribution
			String[] tokens = text.split("\\(");
			String distribution = tokens[0];
			String parameters = tokens[1];
			if ("uniform".equalsIgnoreCase(distribution)) {
				// TODO: how to check for integer

				// uniform(0,7)
				// uniform()
				// uniform(8)
				if (parameters.startsWith(")")) {
					// uniform()
					// no parameter
					// return uniform(0,1)
					if (type == INTEGER) {
						UniformIntegerDistribution uid = new UniformIntegerDistribution(
								0, 1);
						return uid;
					} else {
						UniformRealDistribution urd = new UniformRealDistribution(
								0.0, 1.0);
						return urd;
					}

				} else if (parameters.contains(",")) {
					// uniform(0,7)
					// two params
					String[] params = parameters.split("\\)")[0].split(",");
					if (type == INTEGER) {
						int a = Integer.parseInt(params[0]);
						int b = Integer.parseInt(params[1]);
						UniformIntegerDistribution uid = new UniformIntegerDistribution(
								a, b);
						return uid;
					} else {
						double a = Double.parseDouble(params[0]);
						double b = Double.parseDouble(params[1]);
						UniformRealDistribution urd = new UniformRealDistribution(
								a, b);
						return urd;
					}

				} else {
					// uniform(8) single param
					if (type == INTEGER) {
						String[] params = parameters.split("\\)");
						int a = Integer.parseInt(params[0]);
						UniformIntegerDistribution uid = new UniformIntegerDistribution(
								0, a);
						return uid;
					} else {
						String[] params = parameters.split("\\)");
						double a = Double.parseDouble(params[0]);
						UniformRealDistribution urd = new UniformRealDistribution(
								0, a);
						return urd;
					}
				}
			} else if ("normal".equalsIgnoreCase(distribution)) {
				// normal(deviation,mean)
				// normal(0,7)
				// normal()
				// normal(8)
				if (parameters.startsWith(")")) {
					// normal()
					// no parameter
					// return normal(1,0)
					// NormalDistribution(mean, deviation)
					NormalDistribution nd = new NormalDistribution(0, 1);
					return nd;

				} else if (parameters.contains(",")) {
					// normal(sigma,mean)
					// normal(0,7)
					// two params
					String[] params = parameters.split("\\)")[0].split(",");

					double sigma = Double.parseDouble(params[0]);
					double mean = Double.parseDouble(params[1]);
					NormalDistribution nd = new NormalDistribution(mean, sigma);
					return nd;

				} else {
					// normal(sigma)
					// normal(8) single param
					String[] params = parameters.split("\\)");
					double sigma = Double.parseDouble(params[0]);
					NormalDistribution nd = new NormalDistribution(0, sigma);
					return nd;

				}
			} else if ("exponential".equalsIgnoreCase(distribution)) {
				// exponential(lambda)
				// exponential()
				// exponential(8)
				if (parameters.startsWith(")")) {
					// exponential()
					// no parameter
					// return exponential(1,0)
					ExponentialDistribution ed = new ExponentialDistribution(1);
					return ed;

				} else if (parameters.contains(",")) {
					int commaCount = StringUtils.countMatches(parameters, ",");
					int argNumber= commaCount+1;
					
					if(argNumber==2){
						
						// exponential(lambda,min)
						// exponential(0,7)
						// two params
						String[] params = parameters.split("\\)")[0].split(",");

						double lambda = Double.parseDouble(params[0]);
						ExponentialDistribution ed = new ExponentialDistribution(lambda);
						return ed;
					} else {
						throw new IllegalArgumentException(
								"Exponential Distribution should contain minimum of 2 arguments alpha and beta and maximum of 3 arguments"
								+ " alpha, beta and min!");
					}


				} else {
					// exponential(lambda)
					// exponential(8) single param
					String[] params = parameters.split("\\)");
					double lambda = Double.parseDouble(params[0]);
					ExponentialDistribution ed = new ExponentialDistribution(lambda);
					return ed;

				} 
			} else if ("gamma".equalsIgnoreCase(distribution)) {
				// gamma(alpha,beta)
				// gamma(0,7)
				
				if (parameters.startsWith(")")) {
					// gamma()
					// no parameter
					// throw exception this value is not allowed
					throw new IllegalArgumentException(
							"Gamma Distribution should contain minimum of two arguments alpha and beta!");

				} else if (parameters.contains(",")) {
					// gamma(alpha,beta)
					//GammaDistribution(shape,scale)
					//count number of parameters
					int commaCount = StringUtils.countMatches(parameters, ",");
					int argNumber= commaCount+1;
					
					if(argNumber==2){
						
						String[] params = parameters.split("\\)")[0].split(",");

						double alpha = Double.parseDouble(params[0]);
						double beta = Double.parseDouble(params[1]);
						GammaDistribution gd = new GammaDistribution(alpha,beta);
						return gd;
					} else if(argNumber==3){
						
						String[] params = parameters.split("\\)")[0].split(",");

						double alpha = Double.parseDouble(params[0]);
						double beta = Double.parseDouble(params[1]);
						
						//TODO: min functionality not implemented in apache library
						double min = Double.parseDouble(params[2]);

						GammaDistribution gd = new GammaDistribution(alpha,beta);
						return gd;
					} else {
						throw new IllegalArgumentException(
								"Gamma Distribution should contain minimum of 2 arguments alpha and beta and maximum of 3 arguments"
								+ " alpha, beta and min!");
					}
		
					

				} else {
					throw new IllegalArgumentException(
							"Gamma Distribution should contain minimum of two arguments alpha and beta!");

				} 
			}  else if ("geometric".equalsIgnoreCase(distribution)) {
				// geometric(p)

				
				if (parameters.startsWith(")")) {
					// geometric()
					// no parameter
					// throw exception this value is not allowed
					throw new IllegalArgumentException(
							"geometric Distribution should contain exactly one parameter probability");

				} else if (parameters.contains(",")) {
					//geometric(,)
					// throw exception this value is not allowed
					throw new IllegalArgumentException(
							"geometric Distribution should contain exactly one parameter - probability");
		
					

				} else {
					//geometric(p)
					String[] params = parameters.split("\\)");
					double p = Double.parseDouble(params[0]);
					GeometricDistribution gd = new GeometricDistribution(p);
					return gd;
				}
			} else if ("logistic".equalsIgnoreCase(distribution)) {
				// logistic(beta,alpha)

				
				if (parameters.startsWith(")")) {
					// logistic()
					// no parameter
					// throw exception this value is not allowed
					throw new IllegalArgumentException(
							"logistic Distribution should contain exactly two parameters: beta and alpha");

				} else if (parameters.contains(",")) {
					//logistic(beta,alpha)
					// throw exception this value is not allowed
					//count number of parameters
					int commaCount = StringUtils.countMatches(parameters, ",");
					int argNumber= commaCount+1;
					
					if(argNumber==2){
						//logistic(beta,alpha)
						String[] params = parameters.split("\\)")[0].split(",");

						double beta = Double.parseDouble(params[0]);
						double alpha = Double.parseDouble(params[1]);
						LogisticDistribution ld = new LogisticDistribution(alpha,beta);
						return ld;
					} else {
						// throw exception this value is not allowed
						throw new IllegalArgumentException(
								"logistic Distribution should contain exactly two parameters: beta and alpha");

					}
					

				} else {
					//logistic(a)
					// throw exception this value is not allowed
					throw new IllegalArgumentException(
							"logistic Distribution should contain exactly two parameters: beta and alpha");

				}
			}  else if ("step".equalsIgnoreCase(distribution)) {
				// step(min,max,step)
				//step(min,max)// default step =1
				//step(max)//default min=1, step =1

				
				if (parameters.startsWith(")")) {
					// step()
					// no parameter
					// throw exception this value is not allowed
					throw new IllegalArgumentException(
							"step distribution should contain atleast 1 parameter");

				} else if (parameters.contains(",")) {
					// step(min,max,step)
					//step(min,max)// default step =1
					//count number of parameters
					int commaCount = StringUtils.countMatches(parameters, ",");
					int argNumber= commaCount+1;
					
					if(argNumber==2){
						//step(min,max)// default step =1
						String[] params = parameters.split("\\)")[0].split(",");

						int min = Integer.parseInt(params[0]);
						int max = Integer.parseInt(params[1]);
						StepDistribution sd = new StepDistribution(min, max);
						return sd;
					} else if(argNumber==3){
						//step(min,max,step)// default step =1
						String[] params = parameters.split("\\)")[0].split(",");

						int min = Integer.parseInt(params[0]);
						int max = Integer.parseInt(params[1]);
						int step = Integer.parseInt(params[2]);
						StepDistribution sd = new StepDistribution(min, max, step);
						return sd;
					} else {
						// throw exception this value is not allowed
						throw new IllegalArgumentException(
								"step distribution should contain at least 1 parameter and at maximum 3 parameters");
						
					}
					

				} else {
					//step(max)
					//similar to step(1,max,1)
					String[] params = parameters.split("\\)");

					int min = 1;
					int max = Integer.parseInt(params[0]);
					int step = 1;
					StepDistribution sd = new StepDistribution(min, max, step);
					return sd;
					

				}
			}
		}
		throw new IllegalArgumentException(
				"Distribution should contain opening and closing parenthesis '()'!");
	}
	
	
	
	private String[] allowedDistributionArray = {
			"uniform",
			"normal",
			"gamma",
			"exponential",
			"logistic",
			//"geometric",
		};
	
	private List<String> allowedDistributions =  Arrays.asList(allowedDistributionArray);

	public boolean validate(String text) {
		//distribution();
		//distribution(a)
		//distribution(a,b);
		//distribution(a,b,c);
		//distribution(a,b,c,d);
		if (text.contains("(") && text.contains(")")) {
			// extract the type of distribution
			//distribution
			//a,b)
			String[] tokens = text.split("\\(");
			String distribution = tokens[0];
			if(allowedDistributions.contains(distribution)){
				return true;
			} else {
				return false;
			}
		
		}
		return false;
	}
	public boolean validateStepDist(String text) {
		//distribution();
		//distribution(a)
		//distribution(a,b);
		//distribution(a,b,c);
		//distribution(a,b,c,d);
		if (text.contains("(") && text.contains(")")) {
			// extract the type of distribution
			//distribution
			//a,b)
			String[] tokens = text.split("\\(");
			String distribution = tokens[0];
			if("step".equalsIgnoreCase(distribution)){
				return true;
			} else {
				return false;
			}
		
		}
		return false;
	}
	public Distribution getDistribution(String text){
		Distribution dist;
		if(validate(text)){
			dist=new Distribution();
			String[] tokens = text.split("\\(");
			String distName = tokens[0];
			dist.setDistribution(distName.toLowerCase());
			String paramPart = tokens[1];
			tokens = paramPart.split("\\)");//splitting a,v,b,d)
			String params = tokens[0];
			if(params!=null && !params.isEmpty()){
				tokens = params.split(",");
				if(tokens!=null){
					dist.setParamList((List<String>) Arrays.asList(tokens));
				}
			}
			
			return dist;
		}
		return null;
		
	}
	
	public Distribution getStepDistribution(String text){
		Distribution dist;
		if(validateStepDist(text)){
			dist=new Distribution();
			String[] tokens = text.split("\\(");
			String distName = tokens[0];
			dist.setDistribution(distName.toLowerCase());
			String paramPart = tokens[1];
			tokens = paramPart.split("\\)");//splitting a,v,b,d)
			String params = tokens[0];
			if(params!=null && !params.isEmpty()){
				tokens = params.split(",");
				if(tokens!=null){
					dist.setParamList((List<String>) Arrays.asList(tokens));
				}
			}
			
			return dist;
		}
		return null;
		
	}
	
	public Object generateDistribution(Distribution dist, int type, HashMap<String,Symbol> symbolTable){
		switch(dist.getDistribution()){
			case "uniform": {
				if(type == INTEGER){
					if(dist.getParamList()==null){
						UniformIntegerDistribution uid = new UniformIntegerDistribution(
								0, 1);
						return uid;
					} else {
						if(dist.getParamList().size()==1){
							if(symbolTable.containsKey(dist.getParamList().get(0))){
								Symbol value = symbolTable.get(dist.getParamList().get(0));
								int max = Integer.parseInt((String) value.get());
							}
						}
						
					}
					
				}
			}
		
		}
		return null;
	}
	
	
	


}
