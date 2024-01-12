package com.levi9.functionaltests.ui.pages.restfulbooker;

import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.openqa.selenium.By;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class HeaderPage extends BasePage<HeaderPage> {

	private final By page = By.cssSelector(".nav-item");
	private final By roomsLink = By.linkText("Rooms");
	private final By reportLink = By.linkText("Report");
	private final By brandingLink = By.linkText("Branding");
	private final By messagesLink = By.cssSelector("[href*='#/admin/messages']");
	private final By unreadMessagesNumber = By.cssSelector("a[href*='#/admin/messages'] .notification");
	private final By frontPageLink = By.linkText("Front Page");
	private final By logoutLink = By.linkText("Logout");

	protected HeaderPage(final BaseDriver baseDriver) {
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
	 * Check is Logout Link Visible in HeaderPage.
	 *
	 * @return true if they are, otherwise false
	 */
	public boolean isLogoutLinkVisible() {
		return isElementVisible(logoutLink, 5);
	}

	/**
	 * Click on Rooms Link in HeaderPage.
	 */
	public void clickOnRooms() {
		waitAndClick(roomsLink);
	}

	/**
	 * Click on Report Link in HeaderPage.
	 */
	public void clickOnReport() {
		waitAndClick(reportLink);
	}

	/**
	 * Click on Branding Link in HeaderPage.
	 */
	public void clickOnBranding() {
		waitAndClick(brandingLink);
	}

	/**
	 * Click on Messages Link in HeaderPage.
	 */
	public void clickOnMessages() {
		waitAndClick(messagesLink);
	}

	/**
	 * Get Number of Unread Messages.
	 *
	 * @return count of unread messages
	 */
	public String getUnreadMessagesCount() {
		return unreadMessagesNumber.findElement(getDriver()).getText();
	}

	/**
	 * Click on Front Page Link in HeaderPage.
	 */
	public void clickOnFrontPage() {
		waitAndClick(frontPageLink);
	}

	/**
	 * Click on Logout Link in HeaderPage.
	 */
	public void clickOnLogout() {
		waitAndClick(logoutLink);
	}

}
