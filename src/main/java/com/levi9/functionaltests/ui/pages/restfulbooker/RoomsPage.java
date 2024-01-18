package com.levi9.functionaltests.ui.pages.restfulbooker;

import com.levi9.functionaltests.rest.data.restfulbooker.RoomAmenities;
import com.levi9.functionaltests.rest.data.restfulbooker.RoomType;
import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class RoomsPage extends BasePage<RoomsPage> {

	private final By page = By.xpath("//*[text()='Room details']");
	private final By roomNameField = By.id("roomName");
	private final By roomTypeSelect = By.id("type");
	private final By roomAccessibleSelect = By.id("accessible");
	private final By roomPriceField = By.id("roomPrice");
	private final By wifiCheckbox = By.id("wifiCheckbox");
	private final By tvCheckbox = By.id("tvCheckbox");
	private final By radioCheckbox = By.id("radioCheckbox");
	private final By refreshmentsCheckbox = By.id("refreshCheckbox");
	private final By safeCheckbox = By.id("safeCheckbox");
	private final By viewsCheckbox = By.id("viewsCheckbox");
	private final By createRoomButton = By.id("createRoom");
	private final By alert = By.cssSelector("div.alert");

	@Autowired
	protected RoomsPage(final BaseDriver baseDriver) {
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
		openPage(getRestfulBookerPlatformUrl() + "/#/admin", page);
	}

	/**
	 * Enter Room Name.
	 *
	 * @param roomName room name
	 */
	public void enterRoomName(@Nullable final String roomName) {
		if (!StringUtils.isEmpty(roomName)) {
			waitAndSendKeys(roomNameField, roomName);
		}
	}

	/**
	 * Select Room Type.
	 *
	 * @param roomType room type, {@link RoomType}
	 */
	public void selectRoomType(@Nullable final RoomType roomType) {
		if (null != roomType) {
			waitAndSelectByValue(roomTypeSelect, roomType.getValue());
		}
	}

	/**
	 * Enter Room Price.
	 *
	 * @param roomPrice room price
	 */
	public void enterPrice(@Nullable final String roomPrice) {
		if (!StringUtils.isEmpty(roomPrice)) {
			waitAndSendKeys(roomPriceField, roomPrice);
		}
	}

	/**
	 * Select Room Amenities.
	 *
	 * @param roomAmenities room amenities, {@link RoomAmenities}
	 */
	public void selectAmenities(final RoomAmenities roomAmenities) {
		if (roomAmenities.isWifi()) {
			waitAndClick(wifiCheckbox);
		}
		if (roomAmenities.isTv()) {
			waitAndClick(tvCheckbox);
		}
		if (roomAmenities.isRadio()) {
			waitAndClick(radioCheckbox);
		}
		if (roomAmenities.isRefreshments()) {
			waitAndClick(refreshmentsCheckbox);
		}
		if (roomAmenities.isSafe()) {
			waitAndClick(safeCheckbox);
		}
		if (roomAmenities.isViews()) {
			waitAndClick(viewsCheckbox);
		}
	}

	/**
	 * Create Room by filling Room Name, selecting Room Type, selecting is Room Accessible or not, filling up Room Price and selecting Room Amenities.
	 *
	 * @param roomName      room name
	 * @param roomType      room type
	 * @param accessible    accessible
	 * @param roomPrice     room price
	 * @param roomAmenities room amenities
	 */
	public void createRoom(@Nullable final String roomName, @Nullable final RoomType roomType, final boolean accessible, @Nullable final String roomPrice,
		final RoomAmenities roomAmenities) {
		if (null != roomName) {
			enterRoomName(roomName);
		}
		if (null != roomType) {
			selectRoomType(roomType);
		}
		waitAndSelectByValue(roomAccessibleSelect, accessible ? "true" : "false");
		if (null != roomPrice) {
			enterPrice(roomPrice);
		}
		selectAmenities(roomAmenities);
		waitAndClick(createRoomButton);
		log.info("Create Room with: Name={}; Price={}; Type={}; Accessible={}; Features={}", roomName, roomPrice, roomType, accessible,
			roomAmenities.getRoomDetailsFromAmenities());
	}

	/**
	 * Get Room from Room List by Room Name.
	 *
	 * @param roomName room name
	 *
	 * @return row in table {@link WebElement} representing Room Row
	 */
	private WebElement getRoomFromTable(final String roomName) {
		final By roomLocator = By.xpath("//div[./div[@data-testid='roomlisting'][.//p[contains(@id,'" + roomName + "')]]][last()]");
		return waitAndGetWebElement(roomLocator);
	}

	/**
	 * Get Actual Room name from Room List.
	 *
	 * @param roomName room name
	 *
	 * @return actual room name as {@link String}
	 */
	public String getActualRoomName(final String roomName) {
		return getRoomFromTable(roomName).findElement(By.cssSelector("p[id*=roomName]")).getText();
	}

	/**
	 * Get Actual Room Type from Room List.
	 *
	 * @param roomName room name
	 *
	 * @return room type as {@link String}
	 */
	public String getActualRoomType(final String roomName) {
		return getRoomFromTable(roomName).findElement(By.cssSelector("p[id*=type]")).getText();
	}

	/**
	 * Get Actual Room Accessibility from Room List.
	 *
	 * @param roomName room name
	 *
	 * @return room accessibility as {@link String}
	 */
	public String getActualRoomAccessibility(final String roomName) {
		return getRoomFromTable(roomName).findElement(By.cssSelector("p[id*=accessible]")).getText();
	}

	/**
	 * Get Actual Room Price from Room List.
	 *
	 * @param roomName room name
	 *
	 * @return room price as {@link String}
	 */
	public String getActualRoomPrice(final String roomName) {
		return getRoomFromTable(roomName).findElement(By.cssSelector("p[id*=roomPrice]")).getText();
	}

	/**
	 * Get Actual Room Details from Room List.
	 *
	 * @param roomName room name
	 *
	 * @return room details as {@link String}
	 */
	public String getActualRoomDetails(final String roomName) {
		return getRoomFromTable(roomName).findElement(By.cssSelector("p[id*=details]")).getText();
	}

	/**
	 * Get List of Validation or Mandatory Errors.
	 *
	 * @return list of validation and mandatory errors
	 */
	public List<String> getValidationOrMandatoryErrorMessages() {
		return waitAndGetWebElement(alert).findElements(By.cssSelector("p")).stream().map(WebElement::getText).toList();
	}
}
