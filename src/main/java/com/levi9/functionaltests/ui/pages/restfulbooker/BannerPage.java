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
public class BannerPage extends BasePage<AdminPage> {

	private final By letMeHackButton = By.xpath("//div[@data-target='#collapseBanner']/button");

	protected BannerPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	private final By page = By.id("collapseBanner");

	/**
	 * Checks if page is loaded.
	 *
	 * @return true if yes, otherwise no
	 */
	public boolean isLoaded() {
		return isElementVisible(page, 5);
	}

	public void closeBanner() {
		if (isLoaded()) {
			waitAndClick(letMeHackButton);
		}
	}
}
