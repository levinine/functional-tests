package com.levi9.functionaltests.ui.page.dresses;

import com.levi9.functionaltests.ui.base.BaseDriver;
import com.levi9.functionaltests.ui.base.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Slf4j
@Component
@Scope("cucumber-glue")
public class DressesPage extends BasePage<DressesPage> {

	// Page identification
	private final By pageId = By.cssSelector("div#columns>div>a:nth-of-type(3)");

	// Summer dress
	private final By summerDress = By.xpath("(//div[@class='left-block']//div)[1]");
	private final By summerDressMoveButton = By.xpath("(//span[text()='More'])[1]");

	public DressesPage(final BaseDriver baseDriver) {
		super(baseDriver);
	}

	@Override
	protected void isLoaded() {
		getWaitHelper().waitForAngularToFinish();
		getWaitHelper().waitForElementToBeVisibleByDefaultTimeout(pageId);
	}

	@Override
	protected void load() {
		openPage(getAutomationPracticeUrl() + "index.php?id_category=8&controller=category", pageId);
	}

	public void clickOnFirstDress() {
		final JavascriptExecutor js = driver;
		js.executeScript("javascript:window.scrollBy(250,550)");
		getActionsHelper().moveToElement(summerDress);
		getActionsHelper().moveToElement(summerDressMoveButton);
		waitAndClick(summerDressMoveButton);
	}
}
