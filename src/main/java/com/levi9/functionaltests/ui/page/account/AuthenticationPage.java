package com.levi9.functionaltests.ui.page.account;

import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.automationpractice.CustomerEntity;
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
public class AuthenticationPage extends BasePage<AuthenticationPage> {

	private final By pageId = By.id("login_form");
	private final By emailAddressInput = By.id("email_create");
	private final By submitButton = By.id("SubmitCreate");

	@Autowired
	private Storage storage;

	public AuthenticationPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() {
		getWaitHelper().waitForAngularToFinish();
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		openPage(getAutomationPracticeUrl() + "index.php?controller=authentication&back=my-account", pageId);
	}

	/**
	 * Start creation of new account
	 */
	public void startCreationOfNewAccount(final CustomerEntity customer) {
		log.info("User starts creating new account.");
		waitAndSendKeys(emailAddressInput, customer.getEmail());
		waitAndClick(submitButton);
	}
}
