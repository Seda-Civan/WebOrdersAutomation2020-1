package com.weborders.utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Driver {

    //same for everyone
    private static ThreadLocal<WebDriver> driverPool = new ThreadLocal<>();
    ///our driver was singleton, so one driver can not be in 3 browser at the same time
    //if we are running 3 browsers; we need 3 driver
    //ThreadLocal<WebDriver> driverPool = new ThreadLocal<>(); =>  java will create clone of driver per thread
    //get() object of current thread
    //webdriver object wrapped by threadLocal
    //now we can run 3 test parallel, at the same time !
    //it runs one thread per CPU core
    //thread : there is some program, some actions happens, some task is getting executed
    //if you have 1 thread : you can execute only ==> tests one by one not same time
    //in java to execution there is process every action will be performed in separate thread
    //when you have couple of them tests can distributed among threads,
    // since we are distributing executing test at same time performance will be high
    //execute them parallel => it will take less time
    //creating thread => having another hand to do action
    //Execution of multiple actions at there same time
    //you can crate threads, but to control them you need to make test synchronized
    //synchronized makes method thread safe,it ensures that only 1 thread can use it at the time
    //thread safety reduces performance but it makes everything safe/
    //what make executable parallel =>> on data provider (parallel = true) makes it runnable on smoketest class
    //provisioning test executable at same time -
    //parallel doing 2 things at the same time --  synchronized once i hold other hand not
    //synchronized keyword to method name :
    //synchronized block : reduce wait time
    //if method synchronized: controls thread when its needed
    //how to run test in parallel ? INTERVIEW
    //make your driver thread local
    //make getDriver() method synchronized



    //so no one can create object of Driver class
    //everyone should call static getter method instead
    private Driver() {

    }

    //synchronized makes method thread safe,it ensures that only 1 thread can use it at the time
    //thread safety reduces performance but it makes everything safe/
    public synchronized static WebDriver getDriver() {
        //if webdriver object doesn't exist
        //create it
        if (driverPool.get() == null) {
            //specify browser type in configuration.properties file
            String browser = ConfigurationReader.getProperty("browser").toLowerCase();
            switch (browser) {
                case "chrome":
                    WebDriverManager.chromedriver().version("79").setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--start-maximized");
                    driverPool.set(new ChromeDriver(chromeOptions));
                    break;
                case "chromeheadless":
                    //to run chrome without interface (headless mode)
                    WebDriverManager.chromedriver().version("79").setup();
                    ChromeOptions options = new ChromeOptions();
                    options.setHeadless(true);
                    driverPool.set(new ChromeDriver(options));
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driverPool.set(new FirefoxDriver());
                    break;
                default:
                    throw new RuntimeException("Wrong browser name!");
            }
        }
        return driverPool.get();
    }

    public static void closeDriver() {
        if (driverPool != null) {
            driverPool.get().quit();
            driverPool.remove();
        }
    }
}