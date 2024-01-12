package com.levi9.functionaltests.stepdefs.restfulbooker;

import static org.assertj.core.api.Assertions.*;

import com.levi9.functionaltests.rest.service.restfulbooker.AuthService;
import com.levi9.functionaltests.ui.pages.restfulbooker.AdminPage;
import com.levi9.functionaltests.ui.pages.restfulbooker.BannerPage;
import com.levi9.functionaltests.ui.pages.restfulbooker.HeaderPage;
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
public class LoginStepdef {

	@Autowired
	private AdminPage adminPage;

	@Autowired
	private BannerPage bannerPage;

	@Autowired
	private RoomsPage roomsPage;

	@Autowired
	private HeaderPage headerPage;

	@Autowired
	private AuthService authService;

	@Given("User is on the Booking Management Login Page")
	public void userIsOnBookingManagementLoginPage() {
		adminPage.load();
		bannerPage.closeBanner();
		assertThat(adminPage.isLoaded()).as("User is not on the Booking Management Login Page!").isTrue();
	}

	@Given("User is logged via Booking Management Login Page as Administrator")
	public void userIsLoggedInAsAdministratorViaUiAndApi() {
		authService.login("admin", "password");
		userIsOnBookingManagementLoginPage();
		loginWithValidUsernameAndPassword();
	}

	@Given("User is logged in as Administrator")
	public void userIsLoggedInAsAdministratorViaApi() {
		authService.login("admin", "password");
	}

	@When("Tries to login with valid username and password")
	public void loginWithValidUsernameAndPassword() {
		adminPage.login("admin", "password");
	}

	@When("Tries to login with only valid password")
	public void loginWithPasswordOnly() {
		adminPage.login("", "password");
	}

	@When("Tries to login with only valid username")
	public void loginWithUsernameOnly() {
		adminPage.login("admin", "");
	}

	@When("Tries to login with wrong password")
	public void loginWithWrongPassword() {
		adminPage.login("admin", "wrong_password");
	}

	@Then("User is Logged In")
	public void assertUserIsLoggedInSuccessfully() {
		assertThat(headerPage.isLogoutLinkVisible()).as("User is not logged in!").isTrue();
	}

	@Then("User is redirected to Rooms Management Page")
	public void assertUserIsRedirectedToRoomsManagementPage() {
		assertThat(roomsPage.isLoaded()).as("User is not redirected to Rooms Management Page!").isTrue();
	}

	@Then("User is still on Booking Management Login Page")
	public void assertUserIsStillOnBookingManagementLoginPage() {
		assertThat(adminPage.isLoaded()).as("User is not anymore on Booking Management Login Page!").isTrue();
	}

	@Then("Username field will have red border")
	public void assertUsernameFieldHasRedBorder() {
		assertThat(adminPage.usernameFieldHasRedBorder()).as("Username field does not have red border!").isTrue();
	}

	@Then("Password field will have red border")
	public void assertPasswordFieldHasRedBorder() {
		assertThat(adminPage.passwordFieldHasRedBorder()).as("Password field does not have red border!").isTrue();
	}

	@Then("Both Username and Passwords fields will have red border")
	public void assertUsernameAndPasswordFieldsHaveRedBorder() {
		assertUsernameFieldHasRedBorder();
		assertPasswordFieldHasRedBorder();
	}
}
