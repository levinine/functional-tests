package com.levi9.functionaltests.ui.pages.restfulbooker;

import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.apache.commons.lang3.StringUtils;
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
public class AdminPage extends BasePage<AdminPage> {

	private final By page = By.xpath("//*[@data-testid='login-header']");
	private final By usernameField = By.id("username");
	private final By passwordField = By.id("password");
	private final By loginButton = By.id("doLogin");

	protected AdminPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	/**
	 * Checks if page is loaded.
	 *
	 * @return true if yes, otherwise false
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
	 * Login on B&B Booking Management Admin page by filling up username and password fields.
	 *
	 * @param username username
	 * @param password password
	 */
	public void login(final String username, final String password) {
		waitAndSendKeys(usernameField, username);
		waitAndSendKeys(passwordField, password);
		waitAndClick(loginButton);
	}

	/**
	 * Checks if element has red border.
	 *
	 * @param locator element locator, {@link By}
	 *
	 * @return true if element has red border, otherwise false
	 */
	private boolean hasRedBorder(final By locator) {
		final String styleAttributes = waitAndGetAttribute(locator, "style");
		return StringUtils.containsIgnoreCase(styleAttributes, "border: 1px solid red");
	}

	/**
	 * Checks if username field has red border.
	 *
	 * @return true if username field has red border, otherwise false
	 */
	public boolean usernameFieldHasRedBorder() {
		return hasRedBorder(usernameField);
	}

	/**
	 * Checks if password field has red border.
	 *
	 * @return true if password field has red border, otherwise false
	 */
	public boolean passwordFieldHasRedBorder() {
		return hasRedBorder(passwordField);
	}

}
