package com.servet.bdd;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/** Entry point for mvn test: runs every .feature in the package "features" (src/test/resources/features). */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
class RunCucumberTest {
}
