package com.levi9.functionaltests.storage.domain.restfulbooker;

import com.levi9.functionaltests.rest.data.restfulbooker.RoomAmenities;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomDSO;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomType;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class RoomEntity {

	private Integer roomId;
	private String roomName;
	private Integer roomPrice;
	private String description;
	private RoomType type;
	private boolean accessible;
	private String image;
	private RoomAmenities amenities;

	public RoomEntity(final RoomDSO roomDSO) {
		this.roomId = roomDSO.getRoomid();
		this.roomName = roomDSO.getRoomName();
		this.roomPrice = Integer.parseInt(roomDSO.getRoomPrice());
		this.description = roomDSO.getDescription();
		this.type = RoomType.getEnum(roomDSO.getType());
		this.accessible = roomDSO.isAccessible();
		this.image = roomDSO.getImage();
		this.amenities = new RoomAmenities(StringUtils.join(roomDSO.getFeatures(), ","));
	}
}