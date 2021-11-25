package com.levi9.functionaltests.ui.page.home;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class HomePage extends BasePage<HomePage> {

	private final By pageId = By.id("editorial_block_center");
	private final By signInButton = By.className("header_user_info");
	private final By signOutButton = By.className("header_user_info");
	private final By dressesMenuItem = By.xpath("(//a[@class='sf-with-ul'])[4]");
	private final By casualDressesSubMenuItem = By.xpath("(//a[@title='Casual Dresses'])[2]");
	private final By eveningDressesSubMenuItem = By.xpath("(//a[@title='Evening Dresses'])[2]");
	private final By summerDressesSubMenuItem = By.xpath("(//a[@title='Summer Dresses'])[2]");
	// Customer account
	private final By customerAccountLink = By.className("account");

	@Autowired
	private Storage storage;

	public HomePage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() {
		getWaitHelper().waitForAngularToFinish();
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		openPage(getAutomationPracticeUrl() + "index.php", pageId);
	}

	/**
	 * CLicks on Sign In Button
	 */
	public void clickSignInButton() {
		waitAndClick(signInButton);
		log.info("User clicked on Sign In button.");
	}

	/**
	 * Check that Customer is successfully logged in
	 *
	 * @return true if customer is logged in, otherwise false
	 */
	public boolean isCustomerLoggedIn() {
		log.info("Checking is customer logged in...");
		final String loggedInCustomer = waitAndGetText(customerAccountLink);

		return loggedInCustomer
			.contains(storage.getLastCustomer().getFirstName() + " " + storage.getLastCustomer().getLastName())
			&& isElementDisplayed(signOutButton, 5);
	}

	public void clickOnDressesSubMenuItem(final String typeOfDress) {
		getActionsHelper().moveToElement(dressesMenuItem);
		switch (typeOfDress.toLowerCase()) {
		case "casual":
			waitAndClick(casualDressesSubMenuItem);
			break;
		case "evening":
			waitAndClick(eveningDressesSubMenuItem);
			break;
		case "summer":
			waitAndClick(summerDressesSubMenuItem);
			break;
		default:
			throw new FunctionalTestsException("Dress category does not exist!");
		}
	}
}