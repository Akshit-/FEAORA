package model;

public class Visualization {
	private String chartType;
	private String xAxis;
	private String yAxis;
	private boolean usingIterationNumAsX;
	/**
	 * @return the chartType
	 */
	public String getChartType() {
		return chartType;
	}
	/**
	 * @param chartType the chartType to set
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	/**
	 * @return the xAxis
	 */
	public String getXAxis() {
		return xAxis;
	}
	/**
	 * @param xAxis the xAxis to set
	 */
	public void setXAxis(String xAxis) {
		this.xAxis = xAxis;
	}
	/**
	 * @return the yAxis
	 */
	public String getYAxis() {
		return yAxis;
	}
	/**
	 * @param yAxis the yAxis to set
	 */
	public void setYAxis(String yAxis) {
		this.yAxis = yAxis;
	}
	/**
	 * @return the usingIterationNumAsX
	 */
	public boolean isUsingIterationNumAsX() {
		return usingIterationNumAsX;
	}
	/**
	 * @param usingIterationNumAsX the usingIterationNumAsX to set
	 */
	public void setUsingIterationNumAsX(boolean usingIterationNumAsX) {
		this.usingIterationNumAsX = usingIterationNumAsX;
	}
	

}
