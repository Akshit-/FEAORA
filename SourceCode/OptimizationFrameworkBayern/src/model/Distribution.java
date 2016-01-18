package model;

import java.util.ArrayList;
import java.util.List;

public class Distribution {

	//normal
	//gaussian
	//random
	//step increment
	
	private String distribution;
	private List<String> paramList;
	public String getDistribution() {
		return distribution;
	}
	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	public List<String> getParamList() {
		return paramList;
	}
	public void setParamList(List<String> list) {
		this.paramList = list;
	}
	
	
	
}
