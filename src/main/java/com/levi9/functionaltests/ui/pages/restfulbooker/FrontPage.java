package com.levi9.functionaltests.ui.pages.restfulbooker;

import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import java.util.List;

import javax.annotation.Nullable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class FrontPage extends BasePage<FrontPage> {

	private final By page = By.cssSelector(".hotel-description");

	private final By bookThisRoomButton = By.cssSelector("button.openBooking");
	private final By bookingFirstNameField = By.cssSelector("input.room-firstname");
	private final By bookingLastNameField = By.cssSelector("input.room-lastname");
	private final By bookingEmailField = By.cssSelector("input.room-email");
	private final By bookingPhoneNumberField = By.cssSelector("input.room-phone");
	private final By bookingBookButton = By.xpath("//button[.='Book']");
	private final By bookingCalendarNextButton = By.xpath("//button[.='Next']");
	private final By bookingSuccessfulConfirmation = By.xpath("//div[contains(@class, 'confirmation-modal')]//h3[.='Booking Successful!']");
	private final By bookingErrorMessages = By.cssSelector("div.hotel-room-info .alert.alert-danger");
	private final By contactNameField = By.cssSelector("input[data-testid='ContactName']");
	private final By contactEmailField = By.cssSelector("input[data-testid='ContactEmail']");
	private final By contactPhoneField = By.cssSelector("input[data-testid='ContactPhone']");
	private final By contactSubjectField = By.cssSelector("input[data-testid='ContactSubject']");
	private final By contactDescriptionField = By.cssSelector("textarea[data-testid='ContactDescription']");
	private final By contactSubmitButton = By.cssSelector("button#submitContact");
	private final By contactSuccessMessageHeader = By.cssSelector("div.contact h2");
	private final By contactSuccessMessageSubject = By.cssSelector("div.contact p[style='font-weight: bold;']");
	private final By contactErrorMessages = By.cssSelector("div.contact .alert.alert-danger");

	protected FrontPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	/**
	 * Checks if page is loaded.
	 *
	 * @return true if yes, otherwise no
	 */
	public boolean isLoaded() {
		return isElementVisible(page, 5);
	}

	/**
	 * Load Page.
	 */
	public void load() {
		openPage(getRestfulBookerPlatformUrl(), page);
	}

	/**
	 * Send Message to Hotel using Contact Form on bottom of Front Page.
	 *
	 * @param name        visitor's name
	 * @param email       visitor's email
	 * @param phone       visitor's phone
	 * @param subject     message subject
	 * @param description message description
	 */
	public void sendMessage(@Nullable final String name, @Nullable final String email, @Nullable final String phone, @Nullable final String subject,
		@Nullable final String description) {
		if (null != name) {
			waitAndSendKeys(contactNameField, name);
		}
		if (null != email) {
			waitAndSendKeys(contactEmailField, email);
		}
		if (null != phone) {
			waitAndSendKeys(contactPhoneField, phone);
		}
		if (null != subject) {
			waitAndSendKeys(contactSubjectField, subject);
		}
		if (null != description) {
			waitAndSendKeys(contactDescriptionField, description);
		}
		waitAndClick(contactSubmitButton);
	}

	/**
	 * Get Room WebElement on Front Page based on Room Name.
	 *
	 * @param roomName room name
	 *
	 * @return {@link WebElement}
	 */
	private WebElement getBookingRoomElement(final String roomName) {
		final By bookingRoom = By.xpath(
			"//div[contains(@class, 'room-header')]/following-sibling::div[.//img[contains(@alt," + roomName + ")]][last()]");
		return waitAndGetWebElement(bookingRoom);
	}

	/**
	 * Click on Book This Room Button inside Room WebElement to open Booking form.
	 *
	 * @param roomName room name
	 */
	public void clickBookThsRoomButton(final String roomName) {
		waitAndClick(getBookingRoomElement(roomName).findElement(bookThisRoomButton));
	}

	/**
	 * Fill in all Booking Fields inside Room WebElement.
	 *
	 * @param roomName    room name
	 * @param firstName   booking first name
	 * @param lastName    booking last name
	 * @param email       booking email
	 * @param phoneNumber booking phone number
	 */
	public void fillBookingFields(final String roomName, final String firstName, final String lastName, final String email, final String phoneNumber) {
		waitAndSendKeys(getBookingRoomElement(roomName).findElement(bookingFirstNameField), firstName);
		waitAndSendKeys(getBookingRoomElement(roomName).findElement(bookingLastNameField), lastName);
		waitAndSendKeys(getBookingRoomElement(roomName).findElement(bookingEmailField), email);
		waitAndSendKeys(getBookingRoomElement(roomName).findElement(bookingPhoneNumberField), phoneNumber);
	}

	/**
	 * Select Booking Dates.
	 * Currently, this only goes to next month and select whole month.
	 *
	 * @param roomName room name
	 */
	public void selectBookingDates(final String roomName) {
		waitAndClick(getBookingRoomElement(roomName).findElement(bookingCalendarNextButton));
		final List<WebElement> bookingCalendarDaysElements = getBookingRoomElement(roomName).findElements(By.cssSelector(".rbc-date-cell:not(.rbc-off-range)"));
		final WebElement fromDayElement = bookingCalendarDaysElements.getFirst();
		final WebElement toDayElement = bookingCalendarDaysElements.getLast();
		getActionsHelper().dragAndDrop(fromDayElement, toDayElement);
	}

	/**
	 * Click on Book Room Button inside Room WebElement.
	 *
	 * @param roomName room name
	 */
	public void clickOnBookButton(final String roomName) {
		waitAndClick(getBookingRoomElement(roomName).findElement(bookingBookButton));
	}

	/**
	 * Click on Book This Room Button to open Booking form, fill in all Booking Fields, Select Booking Dates and Click on Book Room Button.
	 *
	 * @param roomName    room name
	 * @param firstName   first name
	 * @param lastName    last name
	 * @param email       email
	 * @param phoneNumber phone number
	 */
	public void bookRoom(final String roomName, final String firstName, final String lastName, final String email, final String phoneNumber) {
		clickBookThsRoomButton(roomName);
		fillBookingFields(roomName, firstName, lastName, email, phoneNumber);
		selectBookingDates(roomName);
		clickOnBookButton(roomName);
	}

	/**
	 * Click on Book This Room Button to open Booking form, fill in all Booking Fields but DO NOT Select Booking Dates and Click on Book Room Button.
	 *
	 * @param roomName    room name
	 * @param firstName   first name
	 * @param lastName    last name
	 * @param email       email
	 * @param phoneNumber phone number
	 */
	public void bookRoomWithoutDates(final String roomName, final String firstName, final String lastName, final String email, final String phoneNumber) {
		clickBookThsRoomButton(roomName);
		fillBookingFields(roomName, firstName, lastName, email, phoneNumber);
		clickOnBookButton(roomName);
	}

	/**
	 * Checks if Booking Successful Confirmation Modal Displayed.
	 *
	 * @return true if it is, otherwise false
	 */
	public boolean isBookingSuccessfulConfirmationDisplayed() {
		return isElementVisible(bookingSuccessfulConfirmation, 10);
	}

	/**
	 * Checks if Booking Error Messages are Displayed.
	 *
	 * @return true if they are, otherwise false
	 */
	public boolean areBookingErrorMessagesDisplayed() {
		return isElementVisible(bookingErrorMessages, 10);
	}

	/**
	 * Get List of Booking Error Messages.
	 *
	 * @return list of booking error messages
	 */
	public List<String> getBookingErrorMessages() {
		return waitAndGetWebElement(bookingErrorMessages).findElements(By.cssSelector("p")).stream().map(WebElement::getText).toList();
	}

	/**
	 * Checks if Contact Successful Confirmation Displayed.
	 *
	 * @return true if it is, otherwise false
	 */
	public boolean isContactSuccessfulConfirmationDisplayed() {
		return isElementVisible(contactSuccessMessageHeader, 10);
	}

	/**
	 * Get Contact Successful Confirmation HeaderPage Text.
	 *
	 * @return header text
	 */
	public String getContactSuccessfulHeaderText() {
		return waitAndGetText(contactSuccessMessageHeader);
	}

	/**
	 * Get Contact Successful Confirmation Subject Text.
	 *
	 * @return subject text
	 */
	public String getContactSuccessfulSubjectText() {
		return waitAndGetText(contactSuccessMessageSubject);
	}

	/**
	 * Checks if Contact Error Messages are Displayed.
	 *
	 * @return true if they are, otherwise false
	 */
	public boolean areContactErrorMessagesDisplayed() {
		return isElementVisible(contactErrorMessages, 10);
	}

	/**
	 * Get List of Contact Error Messages.
	 *
	 * @return list of contact error messages
	 */
	public List<String> getContactErrorMessages() {
		return waitAndGetWebElement(contactErrorMessages).findElements(By.cssSelector("p")).stream().map(WebElement::getText).toList();
	}
}
