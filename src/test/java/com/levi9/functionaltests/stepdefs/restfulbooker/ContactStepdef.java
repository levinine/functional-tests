package com.levi9.functionaltests.stepdefs.restfulbooker;

import static com.levi9.functionaltests.util.FakeUtil.getRandomEmail;
import static com.levi9.functionaltests.util.FakeUtil.getRandomPhoneNumber;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.levi9.functionaltests.ui.pages.restfulbooker.FrontPage;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Slf4j
public class ContactStepdef {

	@Autowired
	private FrontPage frontPage;

	@When("Visitor {string} tries to contact property regarding {string} by filling up all mandatory fields with valid data")
	public void visitorContactsProperty(final String fullName, final String subject) {
		frontPage.sendMessage(fullName, getRandomEmail(), getRandomPhoneNumber(), subject, RandomStringUtils.randomAlphanumeric(200));
	}

	@When("Visitor tries to contact property regarding {string} without filling up name field")
	public void visitorContactPropertyWithoutName(final String subject) {
		frontPage.sendMessage(null, getRandomEmail(), getRandomPhoneNumber(), subject, RandomStringUtils.randomAlphanumeric(200));
	}

	@When("Visitor {string} tries to contact property regarding {string} without filling up email field")
	public void visitorContactsPropertyWithoutEmail(final String fullName, final String subject) {
		frontPage.sendMessage(fullName, null, getRandomPhoneNumber(), subject, RandomStringUtils.randomAlphanumeric(200));
	}

	@When("Visitor {string} tries to contact property regarding {string} without filling up phone field")
	public void visitorContactsPropertyWithoutPhoneNumber(final String fullName, final String subject) {
		frontPage.sendMessage(fullName, getRandomEmail(), null, subject, RandomStringUtils.randomAlphanumeric(200));
	}

	@When("Visitor {string} tries to contact property without filling up subject field")
	public void visitorContactsPropertyWithoutSubject(final String fullName) {
		frontPage.sendMessage(fullName, getRandomEmail(), getRandomPhoneNumber(), null, RandomStringUtils.randomAlphanumeric(200));
	}

	@When("Visitor {string} tries to contact property regarding {string} without filling up message field")
	public void visitorContactsPropertyWithoutMessage(final String fullName, final String subject) {
		frontPage.sendMessage(fullName, getRandomEmail(), getRandomPhoneNumber(), subject, null);
	}

	@When("Visitor {string} tries to contact property regarding {string} by filling up email with invalid value: {string}")
	public void visitorContactsPropertyWithInvalidEmail(final String fullName, final String subject, final String email) {
		frontPage.sendMessage(fullName, email, getRandomPhoneNumber(), subject, RandomStringUtils.randomAlphanumeric(200));
	}

	@When("Visitor {string} tries to contact property regarding {string} by filling up phone with valid/invalid value: {string}")
	public void visitorContactsPropertyValidOrInvalidPhoneNumber(final String fullName, final String subject, final String phoneNumber) {
		frontPage.sendMessage(fullName, getRandomEmail(), phoneNumber, subject, RandomStringUtils.randomAlphanumeric(200));
	}

	@When("Visitor {string} tries to contact property by filling up subject with value length of {int} characters")
	public void visitorContactsPropertyWithInvalidSubject(final String fullName, final int subjectLength) {
		frontPage.sendMessage(fullName, getRandomEmail(), getRandomPhoneNumber(), RandomStringUtils.randomAlphanumeric(subjectLength),
			RandomStringUtils.randomAlphanumeric(200));
	}

	@When("Visitor {string} tries to contact property regarding {string} by filling up message with value length of {int} characters")
	public void visitorContactsPropertyWithInvalidMessage(final String fullName, final String subject, final int messageLength) {
		frontPage.sendMessage(fullName, getRandomEmail(), getRandomPhoneNumber(), subject, RandomStringUtils.randomAlphanumeric(messageLength));
	}

	@Then("Visitor {string} will get Thanks for getting in touch message")
	public void assertThanksForGettingInTouchMessage(final String fullName) {
		assertThat(frontPage.isContactSuccessfulConfirmationDisplayed()).as("Contact Successful Confirmation Message is not displayed!").isTrue();
	}

	@Then("Visitor {string} will get Thanks for getting in touch message regarding subject {string}")
	public void assertMessageSubject(final String fullName, final String subject) {
		assertSoftly(softly -> {
			softly.assertThat(frontPage.getContactSuccessfulHeaderText()).as("Contact Successful HeaderPage Text is wrong!")
				.isEqualTo("Thanks for getting in touch " + fullName + "!");
			softly.assertThat(frontPage.getContactSuccessfulSubjectText()).as("Contact Successful Subject Text is wrong!").isEqualTo(subject);
		});
	}

	@Then("Visitor will get Contact Validation/Mandatory Error Message: {string}")
	public void assertContactErrorMessages(final String message) {
		assertThat(frontPage.areContactErrorMessagesDisplayed()).as("Contact Error Messages are not displayed!").isTrue();
		assertThat(frontPage.getContactErrorMessages()).as("Wrong Contact Error Message is displayed!").contains(message);
	}
}
