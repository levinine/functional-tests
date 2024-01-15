package com.levi9.functionaltests.rest.data.restfulbooker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoomAmenities {

	@Default
	private boolean wifi = false;
	@Default
	private boolean tv = false;
	@Default
	private boolean radio = false;
	@Default
	private boolean refreshments = false;
	@Default
	private boolean safe = false;
	@Default
	private boolean views = false;

	public RoomAmenities(final String amenities) {
		this.wifi = StringUtils.containsIgnoreCase(amenities, "wifi");
		this.tv = StringUtils.containsIgnoreCase(amenities, "tv");
		this.radio = StringUtils.containsIgnoreCase(amenities, "radio");
		this.refreshments = StringUtils.containsIgnoreCase(amenities, "refreshments");
		this.safe = StringUtils.containsIgnoreCase(amenities, "safe");
		this.views = StringUtils.containsIgnoreCase(amenities, "views");
	}

	public List<String> getAmenitiesAsList() {
		final List<String> amenities = new ArrayList<>();
		if (isWifi()) {
			amenities.add("WiFi");
		}
		if (isTv()) {
			amenities.add("TV");
		}
		if (isRadio()) {
			amenities.add("Radio");
		}
		if (isRefreshments()) {
			amenities.add("Refreshments");
		}
		if (isSafe()) {
			amenities.add("Safe");
		}
		if (isViews()) {
			amenities.add("Views");
		}
		return amenities;
	}

	public String getRoomDetailsFromAmenities() {
		final List<String> amenities = getAmenitiesAsList();
		if (!amenities.isEmpty()) {
			return String.join(", ", amenities);
		} else {
			return "No features added to the room";
		}
	}
}
