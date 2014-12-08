package ru.yandex.qatools.allure.spock

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class TargetDirCleanerSpec extends Specification {

	@Rule
	TemporaryFolder temporaryFolder = new TemporaryFolder()

	def "allure report directory is cleaned up"() {
		when:
			File targetDir = temporaryFolder.newFolder("allure-report")
			ArrayList files = [
					new File(targetDir, "01410c3e-0bc9-42ed-878b-e695395c4b05-testsuite.xml"),
					new File(targetDir, "16c54e03-9e68-4e3b-8860-fdca670ab1d1-testsuite.xml")
			]
			files*.createNewFile()

		then:
			files*.exists()

		when:
			TargetDirCleaner.deleteTestReports(targetDir)

		then:
			files*.exists() == [false, false]
	}

	def "no error when report folder does not exist"() {

		when:
			TargetDirCleaner.deleteTestReports(new File("not/exists"))

		then:
			true
	}
}
