package com.levi9.functionaltests.stepdefs.restfulbooker;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.levi9.functionaltests.rest.data.restfulbooker.RoomAmenities;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomDSO;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomType;
import com.levi9.functionaltests.rest.service.restfulbooker.RoomService;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.restfulbooker.RoomEntity;
import com.levi9.functionaltests.ui.pages.restfulbooker.BannerPage;
import com.levi9.functionaltests.ui.pages.restfulbooker.RoomsPage;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class RoomManagementStepdef {

	@Autowired
	private Storage storage;

	@Autowired
	private RoomsPage roomsPage;

	@Autowired
	private BannerPage bannerPage;

	@Autowired
	private RoomService roomService;

	@Given("User is on the Rooms Management Page")
	public void userIsOnRoomsManagementPage() {
		roomsPage.load();
		bannerPage.closeBanner();
		assertThat(roomsPage.isLoaded()).as("User is not on the Booking Management Login Page!").isTrue();
	}

	@Given("User has created {roomType} type {accessible} room {string} priced at {int} GBP with {string}")
	public void userCreatedRoom(final RoomType roomType, final boolean accessible, final String roomName, final int roomPrice, final String features) {
		final RoomAmenities roomAmenities = new RoomAmenities(features);
		roomService.createRoom(roomName, roomType, accessible, Integer.toString(roomPrice), roomAmenities);
	}

	@When("User creates new {roomType} type {accessible} room {string} priced at {int} GBP with {string}")
	public void userCreatesNewRoom(final RoomType roomType, final boolean accessible, final String roomName, final int roomPrice, final String features) {
		final RoomAmenities roomAmenities = new RoomAmenities(features);
		roomsPage.createRoom(roomName, roomType, accessible, Integer.toString(roomPrice), roomAmenities);
	}

	@Then("New {roomType} type {accessible} room {string} priced at {int} GBP with {string} will be created")
	public void assertRoomCreated(final RoomType roomType, final boolean accessible, final String roomName, final int roomPrice, final String features) {
		final RoomAmenities roomAmenities = new RoomAmenities(features);
		assertSoftly(softly -> {
			softly.assertThat(roomsPage.getActualRoomName(roomName)).as("Room Name is wrong!").isEqualTo(roomName);
			softly.assertThat(roomsPage.getActualRoomType(roomName)).as("Room Type is wrong!").isEqualTo(roomType.getValue());
			softly.assertThat(roomsPage.getActualRoomAccessibility(roomName)).as("Room Accessibility is wrong!").isEqualTo(String.valueOf(accessible));
			softly.assertThat(roomsPage.getActualRoomPrice(roomName)).as("Room Price is wrong!").isEqualTo(Integer.toString(roomPrice));
			softly.assertThat(roomsPage.getActualRoomDetails(roomName)).as("Room Details are wrong!").isEqualTo(roomAmenities.getRoomDetailsFromAmenities());
		});

		final RoomEntity roomEntity = RoomEntity.builder()
			.roomName(roomName)
			.roomPrice(roomPrice)
			.type(roomType)
			.description("Please enter a description for this room")
			.accessible(accessible)
			.amenities(roomAmenities)
			.image("https://www.mwtestconsultancy.co.uk/img/room1.jpg")
			.build();

		final RoomDSO actualRoom = roomService.getRoom(roomName);
		roomEntity.setRoomId(actualRoom.getRoomid());

		storage.getRooms().add(roomEntity);
	}

	@When("User creates new {roomType} type {accessible} room priced at {int} GBP with {string} without filling up room name field")
	public void userCreatesNewRoomWithoutRoomName(final RoomType roomType, final boolean accessible, final int roomPrice, final String features) {
		final RoomAmenities roomAmenities = new RoomAmenities(features);
		roomsPage.createRoom(null, roomType, accessible, Integer.toString(roomPrice), roomAmenities);
	}

	@When("User creates new {roomType} type {accessible} room {string} with {string} without filling up room price field")
	public void userCreatesNewRoomWithoutRoomPrice(final RoomType roomType, final boolean accessible, final String roomName, final String features) {
		final RoomAmenities roomAmenities = new RoomAmenities(features);
		roomsPage.createRoom(roomName, roomType, accessible, null, roomAmenities);
	}

	@Then("User will get validation/mandatory error message: {string}")
	public void assertValidationOrMandatoryErrorMessage(final String message) {
		assertThat(roomsPage.getValidationOrMandatoryErrorMessages()).as("Message '" + message + "' is not displayed!").contains(message);
	}
}
