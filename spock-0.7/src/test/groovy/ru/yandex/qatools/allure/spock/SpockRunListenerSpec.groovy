package ru.yandex.qatools.allure.spock

import org.junit.runner.Description
import org.spockframework.runtime.JUnitDescriptionGenerator
import org.spockframework.runtime.SpecInfoBuilder
import org.spockframework.runtime.model.ErrorInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.SpecInfo
import ru.yandex.qatools.allure.Allure
import ru.yandex.qatools.allure.events.TestCaseFailureEvent
import ru.yandex.qatools.allure.events.TestCaseFinishedEvent
import ru.yandex.qatools.allure.events.TestCaseStartedEvent
import ru.yandex.qatools.allure.events.TestSuiteStartedEvent
import ru.yandex.qatools.allure.model.LabelName
import ru.yandex.qatools.allure.model.Status
import ru.yandex.qatools.allure.spock.testdata.SimpleAnnotatedSpecification
import ru.yandex.qatools.allure.spock.testdata.SimpleSpecification
import spock.lang.Specification

class SpockRunListenerSpec extends Specification {
	Allure allure
	SpockRunListener listener

	def setup() {
		allure = Mock(Allure)
		listener = new SpockRunListener()
		listener.setLifecycle(allure)
	}

	def "TestSuiteStartedEvent is fired before Spec started"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)

		when:
			listener.beforeSpec(spec)

		then:
			1 * allure.fire({ TestSuiteStartedEvent event ->
				assert event.name == "SimpleSpecification"
				assert event.title == null
				assert event.description == null
				true
			})
	}

	def "TestSuiteStartedEvent is enriched with annotations"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleAnnotatedSpecification)

		when:
			listener.beforeSpec(spec)

		then:
			1 * allure.fire({ TestSuiteStartedEvent event ->
				assert event.name == "SimpleAnnotatedSpecification"
				assert event.title == "Simple Specification"
				assert event.description.value == "Simple Specification used for allure sock extension"
				true
			})
	}

	def "TestCaseStartedEvent is fired before Feature started"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)

		when:
			listener.beforeFeature(spec.features.find({ it.name == spec.toFeatureName("successful test") }))

		then:
			1 * allure.fire({ TestCaseStartedEvent event ->
				assert event.name == "successful test"
				true
			})
	}

	def "TestCaseStartedEvent is enriched with annotation"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleAnnotatedSpecification)

		when:
			listener.beforeFeature(spec.features.find({ it.name == spec.toFeatureName("successful test") }))

		then:
			1 * allure.fire({ TestCaseStartedEvent event ->
				assert event.name == "successful test"
				assert event.labels.find({ it.value == "My Feature" && it.name == LabelName.FEATURE.value() })
				assert event.labels.find({ it.value == "Story1" && it.name == LabelName.STORY.value() })
				assert event.labels.find({ it.value == "Story2" && it.name == LabelName.STORY.value() })
				true
			})
	}

	def "TestCaseStartedEvent is not fired for data-driven feature when beforeFeature"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)

		when:
			listener.beforeFeature(spec.features.find({ it.name == spec.toFeatureName("parametrised test") }))

		then:
			0 * allure.fire(_)
	}

	def "TestCaseStartedEvent is fired for data-driven test beforeIteration"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)
			FeatureInfo dataDrivenFeature = getFeatureInfo("successful test", spec)
			IterationInfo iteration = createIterationInfo("successful test", spec, dataDrivenFeature)

		when:
			listener.beforeIteration(iteration)
		then:
			0 * allure.fire(_)
	}


	def "TestCaseStartedEvent is not fired when regular feature when beforeIteration"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)
			FeatureInfo dataDrivenFeature = getFeatureInfo("parametrised test", spec)
			IterationInfo iteration = createIterationInfo("parametrised test[0]", spec, dataDrivenFeature)

		when:
			listener.beforeIteration(iteration)
		then:
			1 * allure.fire({ TestCaseStartedEvent event ->
				assert event.name == "parametrised test[0]"
				true
			})
	}

	def "TestCaseFinishedEvent is fired for regular feature afterFeature"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)

		when:
			listener.afterFeature(getFeatureInfo("successful test", spec))

		then:
			1 * allure.fire(_ as TestCaseFinishedEvent )
	}

	def "TestCaseFinishedEvent is not fired for data-driven feature when afterFeature"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)
			FeatureInfo dataDrivenFeature = getFeatureInfo("parametrised test", spec)

		when:
			listener.afterFeature(dataDrivenFeature)
		then:
			0 * allure.fire(_)
	}

	def "TestCaseFinishedEvent is not fired for regular feature afterIteration"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)
			FeatureInfo dataDrivenFeature = getFeatureInfo("successful test", spec)
			IterationInfo iteration = createIterationInfo("successful test", spec, dataDrivenFeature)

		when:
			listener.afterIteration(iteration)

		then:
			0 * allure.fire(_)
	}

	def "TestCaseFinishedEvent is fired for data-driven feature afterIteration"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)
			FeatureInfo dataDrivenFeature = getFeatureInfo("parametrised test", spec)
			IterationInfo iteration = createIterationInfo("parametrised test[0]", spec, dataDrivenFeature)

		when:
			listener.afterIteration(iteration)
		then:
			1 * allure.fire(_ as TestCaseFinishedEvent)
	}

	def "TestCaseFailureEvent if fired when assertion error"() {
		given:
			def spec = createSpecificationInfoFrom(SimpleSpecification)
			FeatureInfo failed = getFeatureInfo("failed test", spec)
			def error = new AssertionError()
			ErrorInfo errorInfo = new ErrorInfo(failed.getFeatureMethod(), error)

		when:
			listener.error(errorInfo)

		then:
			1 * allure.fire( { TestCaseFailureEvent event ->
				assert event.getThrowable() == error
				assert event.getStatus() == Status.FAILED
				true
			})

	}

	private SpecInfo createSpecificationInfoFrom(Class<?> specClass) {
		def spec = new SpecInfoBuilder(specClass).build()
		new JUnitDescriptionGenerator(spec).attach()
		return spec
	}

	private IterationInfo createIterationInfo(String iterationName, SpecInfo spec, FeatureInfo dataDrivenFeature) {
		def iteration = new IterationInfo(dataDrivenFeature, new Object[0], 1)

		Description description = Description.createTestDescription(spec.getReflection(),
				iterationName, dataDrivenFeature.getFeatureMethod().getReflection().getAnnotations());
		iteration.setDescription(description);
		iteration.setName(iterationName)
		iteration
	}

	private FeatureInfo getFeatureInfo(String featureName, SpecInfo spec) {
		FeatureInfo dataDrivenFeature = spec.features.find({ it.name == spec.toFeatureName(featureName) })
		dataDrivenFeature
	}



}
