package paa.reservas.util.impl;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * A waypoint that also has a color and a label
 * 
 * @author Martin Steiger
 */
public class HotelWaypoint extends DefaultWaypoint {
	private final String name;
	private final int stars;
	private final int availableSingleRooms;
	private final int totalSingleRooms;
	private final int availableDoubleRooms;
	private final int totalDoubleRooms;
	private final Long hotelCode;

	public Long getHotelCode() {
		return hotelCode;
	}

	public HotelWaypoint(String name, int stars, int availableSingleRooms, int totalSingleRooms, int availableDoubleRooms, int totalDoubleRooms, Long hotelCode, double longitude, double latitude) { //GeoPosition coord) {
		super(new GeoPosition(latitude, longitude));
		this.name = name;
		this.stars = stars;
		this.availableSingleRooms = availableSingleRooms;
		this.totalSingleRooms = totalSingleRooms;
		this.availableDoubleRooms = availableDoubleRooms;
		this.totalDoubleRooms = totalDoubleRooms;
		this.hotelCode = hotelCode;
	}

	public String getName() {
		return this.name;
	}

	public int getStars() {
		return this.stars;
	}

	public int getAvailableSingleRooms() {
		return availableSingleRooms;
	}

	public int getTotalSingleRooms() {
		return totalSingleRooms;
	}

	public int getAvailableDoubleRooms() {
		return availableDoubleRooms;
	}

	public int getTotalDoubleRooms() {
		return totalDoubleRooms;
	}
}
