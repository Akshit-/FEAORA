package pcgui.output.map;

import java.math.BigDecimal;

public class GeoCode {
	private BigDecimal longitude;
	private BigDecimal latitude;
	private String label;
	public GeoCode(BigDecimal longitude, BigDecimal latitude, String location) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.label = location;
	}
	/**
	 * @return the longitude
	 */
	public BigDecimal getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the latitude
	 */
	public BigDecimal getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	/* 
	 * Will be used as shortcut to append to URL
	 */
	@Override
	public String toString() {
		return  longitude + "," + latitude + "," + label;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}
