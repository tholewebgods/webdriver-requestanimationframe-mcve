package mcve.webdriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RequestAnimationFrame {
	private static final String MCVE_FIXTURE_LOCATION = "file:///tmp/mcve-requestanimationframe.html";

	private static URL DRIVER_URL = null;

	static {
		try {
			DRIVER_URL = new URL("http://localhost:4444");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println("Info: start 3 test threads");

			new Thread(() -> {
				test();
			}).start();

			new Thread(() -> {
				test();
			}).start();

			new Thread(() -> {
				test();
			}).start();
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	private static void test() {
		WebDriver driver = null;

		try {
			System.out.println("Info: get driver");
			driver = getDriver();

			driver.get(MCVE_FIXTURE_LOCATION);
			// Wait for page to load
			Thread.sleep(1000);

			System.out.println("Info: click on button will enqueue requestAnimationFrame() after 1s ...");
			// 1s after the click a requestAnimationFrame() is enqueued that sets .done ...
			driver.findElement(By.cssSelector("button")).click();

			System.out.println("Info: wait for rendering-done selector");
			// ... with the next frame the .done class shall be present on this element after the button was clicked
			waitForElement(driver, By.cssSelector("#target.done"), Duration.ofSeconds(5));

			System.out.println("Info: selector found");

			// Artificial wait to simulate long running test.
			// Triggers other tests to time out due to exceeding the wait timeout.
			Thread.sleep(8000);

			System.out.println("Info: test successful");
		} catch (Throwable t) {
			System.err.println("Err :" + t.getMessage());
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}

	private static void waitForElement(WebDriver driver, By selector, Duration timeout) {
		FluentWait<WebDriver> wait = new WebDriverWait(driver, timeout, Duration.ofMillis(50))
			.ignoring(StaleElementReferenceException.class)
			.ignoring(NoSuchElementException.class);

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
		} catch (TimeoutException e) {
			throw new RuntimeException(String.format("Timeout waiting for the selector %s to be visible", selector));
		}
	}

	private static WebDriver getDriver() {
		DesiredCapabilities dc = new DesiredCapabilities();

		ChromeOptions options = new ChromeOptions();
		dc.setBrowserName(options.getBrowserName());
		dc.setPlatform(Platform.fromString("LINUX"));

		System.out.println("Info: connect to driver " + DRIVER_URL);
		return new RemoteWebDriver(DRIVER_URL, dc);
	}
}
