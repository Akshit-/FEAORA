package model;

public class MapVisualization {
	private String mapType;
	private String sourceLocations;
	private String destination;
	private String nodeCondition;
	private String edgeCondition;
	private boolean showAllNodes;
	private String iterationCount;
	/**
	 * @return the mapType
	 */
	public String getMapType() {
		return mapType;
	}
	/**
	 * @param mapType the mapType to set
	 */
	public void setMapType(String mapType) {
		this.mapType = mapType;
	}
	/**
	 * @return the nodeCondition
	 */
	public String getNodeCondition() {
		return nodeCondition;
	}
	/**
	 * @param nodeCondition the nodeCondition to set
	 */
	public void setNodeCondition(String nodeCondition) {
		this.nodeCondition = nodeCondition;
	}
	/**
	 * @return the edgeCondition
	 */
	public String getEdgeCondition() {
		return edgeCondition;
	}
	/**
	 * @param edgeCondition the edgeCondition to set
	 */
	public void setEdgeCondition(String edgeCondition) {
		this.edgeCondition = edgeCondition;
	}
	/**
	 * @return the showAllNodes
	 */
	public boolean isShowAllNodes() {
		return showAllNodes;
	}
	/**
	 * @param showAllNodes the showAllNodes to set
	 */
	public void setShowAllNodes(boolean showAllNodes) {
		this.showAllNodes = showAllNodes;
	}
	public String getSourceLocations() {
		return sourceLocations;
	}
	public void setSourceLocations(String locations) {
		this.sourceLocations = locations;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getIterationCount() {
		return iterationCount;
	}
	public void setIterationCount(String iterationCount) {
		this.iterationCount = iterationCount;
	}
	

}
