package ru.yandex.qatools.allure.spock;

import org.spockframework.runtime.AbstractRunListener;
import org.spockframework.runtime.model.ErrorInfo;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.IterationInfo;
import org.spockframework.runtime.model.SpecInfo;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;
import ru.yandex.qatools.allure.events.TestCaseFailureEvent;
import ru.yandex.qatools.allure.events.TestCaseFinishedEvent;
import ru.yandex.qatools.allure.events.TestCaseStartedEvent;
import ru.yandex.qatools.allure.events.TestSuiteFinishedEvent;
import ru.yandex.qatools.allure.events.TestSuiteStartedEvent;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 24.06.14
 */
public class SpockRunListener extends AbstractRunListener {

    @Override
    public void beforeSpec(SpecInfo spec) {
        Allure.LIFECYCLE.fire(new TestSuiteStartedEvent(spec.getPackage(), spec.getPackage()));
    }

    @Override
    public void beforeFeature(FeatureInfo feature) {
        Allure.LIFECYCLE.fire(new TestCaseStartedEvent(
                feature.getParent().getPackage(),
                feature.getFeatureMethod().getName()
        ));
    }

    @Override
    public void beforeIteration(IterationInfo iteration) {
        Allure.LIFECYCLE.fire(new StepStartedEvent(iteration.getName()));
    }

    @Override
    public void afterIteration(IterationInfo iteration) {
        Allure.LIFECYCLE.fire(new StepFinishedEvent());
    }

    @Override
    public void afterFeature(FeatureInfo feature) {
        Allure.LIFECYCLE.fire(new TestCaseFinishedEvent());
    }

    @Override
    public void afterSpec(SpecInfo spec) {
        Allure.LIFECYCLE.fire(new TestSuiteFinishedEvent(spec.getPackage()));
    }

    @Override
    public void error(ErrorInfo error) {
        Allure.LIFECYCLE.fire(new TestCaseFailureEvent().withThrowable(error.getException()));
    }

    @Override
    public void specSkipped(SpecInfo spec) {
    }

    @Override
    public void featureSkipped(FeatureInfo feature) {
    }
}
