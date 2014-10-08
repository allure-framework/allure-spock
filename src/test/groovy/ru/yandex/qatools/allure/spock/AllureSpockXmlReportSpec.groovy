package ru.yandex.qatools.allure.spock

import org.junit.runner.JUnitCore
import ru.yandex.qatools.allure.commons.AllureFileUtils
import ru.yandex.qatools.allure.spock.testdata.SimpleSpec
import ru.yandex.qatools.allure.utils.AllureResultsUtils
import spock.lang.Specification

import java.nio.file.Files

class AllureSpockXmlReportSpec extends Specification {

	def tmpFolder

	def setup() {
		tmpFolder = Files.createTempDirectory("allure").toFile()
//		tmpFolder = new File("c:\\temp\\")
		AllureResultsUtils.setResultsDirectory(tmpFolder);

	}

	def "generate simple test repo"() {
		when:
			JUnitCore jUnitCore = new JUnitCore()
			jUnitCore.run(SimpleSpec)

		then:
			AllureFileUtils.listTestSuiteFiles(tmpFolder).size() == 1

	}

}
