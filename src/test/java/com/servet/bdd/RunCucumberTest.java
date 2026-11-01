package com.servet.bdd;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/** Entry point for mvn test: runs every .feature under src/test/resources/features with Cucumber. */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
class RunCucumberTest {
}
