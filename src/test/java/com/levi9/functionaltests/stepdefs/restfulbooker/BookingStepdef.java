package com.levi9.functionaltests.stepdefs.restfulbooker;

import static org.assertj.core.api.Assertions.*;

import com.levi9.functionaltests.ui.pages.restfulbooker.BannerPage;
import com.levi9.functionaltests.ui.pages.restfulbooker.FrontPage;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class BookingStepdef {

	@Autowired
	private FrontPage frontPage;

	@Autowired
	private BannerPage bannerPage;

	@Given("Visitor is on the Front Page")
	public void visitorIsOnFrontPage() {
		frontPage.load();
		bannerPage.closeBanner();
		assertThat(frontPage.isLoaded()).as("Visitor is not on the Front Page!").isTrue();
		log.info("Front Page is loaded");
	}

	@When("Visitor {string} {string} with an (invalid )email {string} and phone number {string} tries to book a room {string}")
	public void visitorBooksRoom(final String firstName, final String lastName, final String email, final String phoneNumber, final String roomName) {
		frontPage.bookRoom(roomName, firstName, lastName, email, phoneNumber);
	}

	@When("Visitor {string} with an email {string} and phone number {string} tries to book a room {string} without filling up first name field")
	public void visitorBooksRoomWithoutFirstName(final String lastName, final String email, final String phoneNumber, final String roomName) {
		visitorBooksRoom("", lastName, email, phoneNumber, roomName);
	}

	@When("Visitor {string} with an email {string} and phone number {string} tries to book a room {string} by filling up first name with value length of {int} characters")
	public void visitorBooksRoomWithInvalidFirstName(final String lastName, final String email, final String phoneNumber, final String roomName,
		final int firstNameLength) {
		final String firstName = RandomStringUtils.randomAlphabetic(firstNameLength);
		visitorBooksRoom(firstName, lastName, email, phoneNumber, roomName);
	}

	@When("Visitor {string} with an email {string} and phone number {string} tries to book a room {string} without filling up last name field")
	public void visitorBooksRoomWithoutLastName(final String firstName, final String email, final String phoneNumber, final String roomName) {
		visitorBooksRoom(firstName, "", email, phoneNumber, roomName);
	}

	@When("Visitor {string} with an email {string} and phone number {string} tries to book a room {string} by filling up last name with value length of {int} characters")
	public void visitorBooksRoomWithInvalidLastName(final String firstName, final String email, final String phoneNumber, final String roomName,
		final int lastNameLength) {
		final String lastName = RandomStringUtils.randomAlphabetic(lastNameLength);
		visitorBooksRoom(firstName, lastName, email, phoneNumber, roomName);
	}

	@When("Visitor {string} {string} with phone number {string} tries to book a room {string} without filling up email field")
	public void visitorBooksRoomWithoutEmail(final String firstName, final String lastName, final String phoneNumber, final String roomName) {
		visitorBooksRoom(firstName, lastName, "", phoneNumber, roomName);
	}

	@When("Visitor {string} {string} with an email {string} tries to book a room {string} without filling up phone field")
	public void visitorBooksRoomWithoutPhoneNumber(final String firstName, final String lastName, final String email, final String roomName) {
		visitorBooksRoom(firstName, lastName, email, "", roomName);
	}

	@When("Visitor {string} {string} with an email {string} tries to book a room {string} by filling up phone with value length of {int} characters")
	public void visitorBooksRoomWithInvalidPhoneNumber(final String firstName, final String lastName, final String email, final String roomName,
		final int phoneNumberLength) {
		final String phoneNumber = RandomStringUtils.randomNumeric(phoneNumberLength);
		visitorBooksRoom(firstName, lastName, email, phoneNumber, roomName);
	}

	@When("Visitor {string} {string} with an email {string} and phone number {string} tries to book a room {string} without setting booking dates")
	public void visitorBooksRoomWithoutSettingBookingDates(final String firstName, final String lastName, final String email, final String phoneNumber,
		final String roomName) {
		frontPage.bookRoomWithoutDates(roomName, firstName, lastName, email, phoneNumber);
	}

	@Then("Visitor will get Booking Successful! Message")
	public void assertBookingSuccessful() {
		assertThat(frontPage.isBookingSuccessfulConfirmationDisplayed()).as("Booking Successful Confirmation Modal is not displayed!").isTrue();
		log.info("Booking Successful! Message is displayed");
	}

	@Then("Visitor will get Booking Validation/Mandatory Error Message: {string}")
	public void assertBookingErrorMessages(final String message) {
		assertThat(frontPage.areBookingErrorMessagesDisplayed()).as("Booking Error Messages are not displayed!").isTrue();
		assertThat(frontPage.getBookingErrorMessages()).as("Wrong Booking Error Message is displayed!").contains(message);
		log.info("Booking Validation / Mandatory Error Message '{}' is displayed", message);
	}

}
