package ru.yandex.qatools.allure.spock.testdata

import ru.yandex.qatools.allure.annotations.Description
import ru.yandex.qatools.allure.annotations.Features
import ru.yandex.qatools.allure.annotations.Stories
import ru.yandex.qatools.allure.annotations.Title
import spock.lang.Specification

@Title("Simple Specification")
@Description("Simple Specification used for allure sock extension")
class SimpleAnnotatedSpecification extends Specification {

	@Features("My Feature")
	@Stories(["Story1", "Story2"])
	def "successful test"() {
		given: "input data"
			println("input data")
		when: "do action"
			println("do action")
		then:
			println("success")
			true
	}

}
