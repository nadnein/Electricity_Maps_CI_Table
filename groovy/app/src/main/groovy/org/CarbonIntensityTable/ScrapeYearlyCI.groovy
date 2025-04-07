// NOTE: This code is currently not in use but has been retained for potential future requirements.
// It may serve as a reference or be reactivated if similar functionality is needed again.


package org.CarbonIntensityTable
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.time.Duration
import org.dflib.DataFrame
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.support.ui.Wait
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.interactions.Actions




class ScrapeYearlyCI {

    Map<String, Object> preferences = [:]
    FirefoxOptions driverOptions = new FirefoxOptions()
    WebDriver driver
    String userAgent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:135.0) Gecko/20100101 Firefox/135.0'

    ScrapeYearlyCI(String download_directory='.') {
        this.driverOptions.addPreference('browser.download.folderList', 2);
        this.driverOptions.addPreference('browser.download.dir', download_directory)
        this.driverOptions.addPreference('browser.download.useDownloadDir', true)
        this.driverOptions.addPreference('general.useragent.override', userAgent)
        this.driverOptions.addArguments('--width=1920')
        this.driverOptions.addArguments('--height=1200')
        //this.driverOptions.addArguments('-headless')

        this.driver = new FirefoxDriver(driverOptions)

        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2))
    }

    private void selectYear(String year) {
        try {
            // Locate the dropdown button
            WebElement dropdownButton = this.driver.findElement(By.id("year-select"))

            // Click the dropdown button to open the dropdown menu
            dropdownButton.click()
            println "Dropdown button clicked"

            // Wait for the dropdown options to appear
            Wait<WebDriver> wait = new WebDriverWait(this.driver, Duration.ofSeconds(2))
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='" + year + "']")))

            // Locate the option for the specified year in the dropdown
            WebElement yearOption = driver.findElement(By.xpath("//span[text()='" + year + "']"))

            // Click the option for the specified year
            yearOption.click()
            println "Year $year selected"
        } catch (Exception e) {
            println "Error selecting year $year"
            e.printStackTrace()
        }
    }

    private void scrape(String url) {
        // Navigate to the URL
        this.driver.get(url)

        // Wait for the <ul> element with data-sidebar="menu" to load
        List<WebElement> menus = this.driver.findElements(By.xpath("//ul[@data-sidebar='menu']"))
        
        if (menus.size() >= 3) {
            WebElement thirdMenu = menus.get(2) // Index 2 corresponds to the third item
            //println "Third Menu HTML: ${thirdMenu.getAttribute("outerHTML")}"

            // Perform operations on the third menu
            List<WebElement> collapsibleDivs = thirdMenu.findElements(By.xpath(".//div[contains(@class, 'group/collapsible')]"))
            if (collapsibleDivs.isEmpty()) {
                println "No collapsible divs found inside the third menu."
            } else {
                println "Found ${collapsibleDivs.size()} collapsible div(s) inside the third menu."
            }

            // Iterate through each <div> element
            collapsibleDivs.each { divElement ->
                try {
                    // Check if the div has data-state="open"
                    String dataState = divElement.getAttribute("data-state")
                    println "Data state: $dataState"
                    if (dataState != "open") {
                        // Locate the first button inside this div
                        WebElement button = divElement.findElement(By.xpath(".//a[starts-with(@class, 'peer/menu-button')]"))
                        if (button != null) {
                            println "Button located successfully."
                            Thread.sleep(3000) // Wait for 1 second
                            Actions actions = new Actions(driver)
                            actions.doubleClick(button).perform()
                            println "Menu button clicked"
                            
                
                            WebElement country = divElement.findElement(By.xpath(".//li[@data-sidebar='menu-item']//span"))
                            println "Processing country: ${country.getText()}"

                            WebElement dropdownButton = this.driver.findElement(By.id("year-select"))
                            if (dropdownButton != null && dropdownButton.isDisplayed() && dropdownButton.isEnabled()) {
                                println "Dropdown button located successfully and is ready for interaction."
                            } else {
                                println "Dropdown button is either not visible or not enabled."
                            }
                            Thread.sleep(3000) 
                            dropdownButton.click()
                            println "Dropdown button clicked"

                            // Collect the text from the <span> elements that were revealed
                            List<WebElement> sub_regions = divElement.findElements(By.xpath(".//ul[@data-sidebar='menu-sub']//span"))
                            sub_regions.each { region ->
                                println "Processing sub-region: ${region.getText()}"
                            }
                        }
                    }

    
                } catch (Exception e) {
                    println "Error processing div: $divElement"
                    e.printStackTrace()
                }
            }
        } else {
            println "There are less than 3 menus available."
        }
    }



    static void main(String[] args) {
        String url = "https://portal.electricitymaps.com/datasets"
        ScrapeYearlyCI scraperInstance = new ScrapeYearlyCI()
        scraperInstance.scrape(url)
    }
}