package com.levi9.functionaltests.storage.domain.restfulbooker;

import com.levi9.functionaltests.rest.data.restfulbooker.RoomAmenities;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomType;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import restfulbooker.model.room.Room;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoomEntity {

	private Integer roomId;
	private String roomName;
	private Integer roomPrice;
	private String description;
	private RoomType type;
	private Boolean accessible;
	private String image;
	private RoomAmenities amenities;

	public RoomEntity(final Room room) {
		this.roomId = room.getRoomid();
		this.roomName = room.getRoomName();
		this.roomPrice = room.getRoomPrice();
		this.description = room.getDescription();
		this.type = RoomType.getEnum(room.getType());
		this.accessible = room.getAccessible();
		this.image = room.getImage();
		this.amenities = new RoomAmenities(StringUtils.join(room.getFeatures(), ","));
	}
}