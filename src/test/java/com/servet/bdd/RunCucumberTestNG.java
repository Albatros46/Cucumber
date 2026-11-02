package com.servet.bdd;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestNG runner (mvn test -Ptestng). Every scenario — every Examples row, too — is one row of the
 * DataProvider; parallel = true runs the rows on several threads (surefire property
 * dataproviderthreadcount, pom: ${threads}). TestNG does not read junit-platform.properties,
 * so the options are repeated here.
 */
@CucumberOptions(features = "classpath:features", glue = "com.servet.bdd", tags = "not @api",
        plugin = {"summary", "html:target/cucumber-report.html", "json:target/cucumber.json"})
@SuppressWarnings("removal") // cucumber-testng is deprecated for removal since Cucumber 8.0
public class RunCucumberTestNG extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios(ITestContext context) {
        return super.scenarios(context);
    }
}
