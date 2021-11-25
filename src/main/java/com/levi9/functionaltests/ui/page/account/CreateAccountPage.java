package com.levi9.functionaltests.ui.page.account;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import com.levi9.functionaltests.exceptions.FunctionalTestsException;
import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.automationpractice.CustomerEntity;
import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
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
public class CreateAccountPage extends BasePage<CreateAccountPage> {

	// Page identification
	private final By pageId = By.xpath("//h1[.='Create an account']");
	// New Account Personal Information section
	private final By mrRadioButton = By.id("uniform-id_gender1");
	private final By firstNameInfoInput = By.id("customer_firstname");
	private final By lastNameInfoInput = By.id("customer_lastname");
	private final By emailInput = By.id("email");
	private final By passwordInput = By.id("passwd");
	private final By dateOfBirthDaySelect = By.id("uniform-days");
	private final By dateOfBirthMonthSelect = By.id("uniform-months");
	private final By dateOfBirthYearSelect = By.id("uniform-years");
	// Customer address section
	private final By countryId = By.id("id_country");
	private final By firstNameInput = By.id("firstname");
	private final By lastNameInput = By.id("lastname");
	private final By companyInput = By.id("company");
	private final By addressLineOneInput = By.id("address1");
	private final By cityInput = By.id("city");
	private final By stateSelect = By.id("uniform-id_state");
	private final By postalCodeInput = By.id("postcode");
	private final By countrySelect = By.id("uniform-id_country");
	private final By additionalInfoTextArea = By.id("other");
	private final By homePhoneInput = By.id("phone");
	private final By mobilePhoneInput = By.id("phone_mobile");
	private final By addressAliasInput = By.id("alias");
	// Submit form button
	private final By registerButton = By.id("submitAccount");

	@Autowired
	private Storage storage;

	public CreateAccountPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() {
		getWaitHelper().waitForAngularToFinish();
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		throw new FunctionalTestsException("You are not allowed to go to this page without first defining new account email address!");
	}

	/**
	 * Fill in the create an account form with valid data
	 */
	public void fillInCreateAnAccountFormWithValidData(final CustomerEntity customer) {
		log.info("User fills the create and account form with valid data.");
		inputValidPersonalInformation(customer);
		inputValidAddress(customer);
		registerNewAccount();
	}

	private void inputValidPersonalInformation(final CustomerEntity customer) {
		customer.setFirstName(randomAlphabetic(5));
		customer.setLastName(randomAlphabetic(5));
		customer.setPassword(randomAlphanumeric(10));
		getWaitHelper().waitForAngularToFinish();
		waitAndClick(mrRadioButton);
		waitAndSendKeys(firstNameInfoInput, customer.getFirstName());
		waitAndSendKeys(lastNameInfoInput, customer.getLastName());
		waitAndSendKeys(emailInput, customer.getEmail());
		waitAndSendKeys(passwordInput, customer.getPassword());
		waitAndClick(dateOfBirthDaySelect);
		final int randomNumber = current().nextInt(10);
		new Select(getDriver().findElement(By.id("days"))).selectByIndex(randomNumber);
		waitAndClick(dateOfBirthMonthSelect);
		new Select(getDriver().findElement(By.id("months"))).selectByIndex(randomNumber);
		waitAndClick(dateOfBirthYearSelect);
		new Select(getDriver().findElement(By.id("years"))).selectByIndex(randomNumber);
	}

	private void inputValidAddress(final CustomerEntity customer) {
		final String randomText = randomAlphabetic(5);
		final int randomNumber = current().nextInt(10);
		customer.setAddress(randomText + randomNumber);
		waitAndSendKeys(firstNameInput, customer.getFirstName());
		waitAndSendKeys(lastNameInput, customer.getLastName());
		waitAndSendKeys(companyInput, randomText);
		waitAndSendKeys(addressLineOneInput, customer.getAddress());
		waitAndSendKeys(cityInput, randomText);
		waitAndClick(stateSelect);
		new Select(getDriver().findElement(By.id("id_state"))).selectByIndex(randomNumber + 1);
		waitAndSendKeys(postalCodeInput, "00000");
		waitAndClick(countrySelect);
		new Select(getDriver().findElement(countryId)).selectByIndex(1);
		waitAndSendKeys(additionalInfoTextArea, randomText);
		waitAndSendKeys(homePhoneInput, "123456789");
		waitAndSendKeys(mobilePhoneInput, "987654321");
		waitAndSendKeys(addressAliasInput, randomText);
	}

	private void registerNewAccount() {
		waitAndClick(registerButton);
	}
}
