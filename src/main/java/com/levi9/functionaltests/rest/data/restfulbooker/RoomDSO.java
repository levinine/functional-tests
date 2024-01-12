package com.levi9.functionaltests.rest.data.restfulbooker;

import java.util.List;

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
public class RoomDSO {

	private int roomid;
	private String roomName;
	private String roomPrice;
	private String description;
	private String type;
	private boolean accessible;
	private String image;
	private List<String> features;
}
