package com.levi9.functionaltests.ui.page.account;

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
public class MyAccountPage extends BasePage<MyAccountPage> {

	private final By pageId = By.xpath("//h1[.='My account']'");

	@Autowired
	private Storage storage;

	public MyAccountPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() {
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		if (storage.getCustomers().isEmpty()) {
			throw new FunctionalTestsException("You are not allowed to go to this page without first creating an account and login to application!");
		} else {
			openPage(getAutomationPracticeUrl() + "index.php?controller=my-account", pageId);
		}
	}
}
