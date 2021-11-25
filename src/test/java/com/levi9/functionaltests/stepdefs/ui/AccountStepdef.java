package com.levi9.functionaltests.stepdefs.ui;

import static org.assertj.core.api.Assertions.*;

import com.levi9.functionaltests.storage.Storage;
import com.levi9.functionaltests.storage.domain.automationpractice.CustomerEntity;
import com.levi9.functionaltests.ui.page.account.AuthenticationPage;
import com.levi9.functionaltests.ui.page.account.CreateAccountPage;
import com.levi9.functionaltests.ui.page.account.MyAccountPage;
import com.levi9.functionaltests.ui.page.home.HomePage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
public class AccountStepdef {

	@Autowired
	private Storage storage;

	@Autowired
	private AuthenticationPage authenticationPage;

	@Autowired
	private CreateAccountPage createAccountPage;

	@Autowired
	private MyAccountPage myAccountPage;

	@Autowired
	private HomePage homePage;

	@Given("^Customer navigate[s|d] to authentication page$")
	public void customerNavigatesToAuthenticationPage() {
		// Open home screen and navigate to Authentication page
		homePage.open();
		homePage.clickSignInButton();
	}

	@When("^Creates? new account$")
	public void createNewAccount() {
		// Define new customer
		final String customerEmail = RandomStringUtils.randomAlphanumeric(10) + "@test.com";
		final CustomerEntity customer = CustomerEntity.builder().email(customerEmail).build();
		// Start creation of new account
		authenticationPage.startCreationOfNewAccount(customer);
		// Input valid information, address and register successfully
		createAccountPage.fillInCreateAnAccountFormWithValidData(customer);
		storage.getCustomers().add(customer);
	}

	@Then("^(?:Customer )?(?:[Ww]ill|[Ii]s)?\\s?(not)?\\s?(?:be)?\\s?logged in on successful account creation$")
	public void accountCreated(final String notLoggedIn) {
		final boolean isLoggedIn = StringUtils.isEmpty(notLoggedIn);
		// Check that customer is logged in on successful account creation
		if (isLoggedIn) {
			assertThat(homePage.isCustomerLoggedIn()).as("Customer is not logged in!").isTrue();
		} else {
			assertThat(homePage.isCustomerLoggedIn()).as("Customer is not logged in!").isFalse();
		}
		log.info("Customer is successfully logged in.");
	}
}
