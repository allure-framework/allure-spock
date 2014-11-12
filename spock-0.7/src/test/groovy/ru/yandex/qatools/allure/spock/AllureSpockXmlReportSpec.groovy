package ru.yandex.qatools.allure.spock

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.runner.JUnitCore
import ru.yandex.qatools.allure.commons.AllureFileUtils
import ru.yandex.qatools.allure.spock.testdata.SimpleSpecification
import ru.yandex.qatools.allure.utils.AllureResultsUtils
import spock.lang.Specification

class AllureSpockXmlReportSpec extends Specification {

	@Rule
	public TemporaryFolder tmpFolderRule = new TemporaryFolder()
	private File tmpFolder

	def setup() {
		tmpFolder = tmpFolderRule.newFolder("allure-spock-adapter-test")
		AllureResultsUtils.setResultsDirectory(tmpFolder);

	}

	def "generate simple test report"() {
		when:
			JUnitCore jUnitCore = new JUnitCore()
			jUnitCore.run(SimpleSpecification)

		then:
			AllureFileUtils.listTestSuiteFiles(tmpFolder).size() == 1
	}
}
