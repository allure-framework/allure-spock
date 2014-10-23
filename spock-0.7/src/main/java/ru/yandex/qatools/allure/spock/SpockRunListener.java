package ru.yandex.qatools.allure.spock;

import org.junit.runner.Description;
import org.spockframework.runtime.AbstractRunListener;
import org.spockframework.runtime.model.ErrorInfo;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.IterationInfo;
import org.spockframework.runtime.model.SpecInfo;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.TestCaseFinishedEvent;
import ru.yandex.qatools.allure.events.TestCaseFailureEvent;
import ru.yandex.qatools.allure.events.TestCaseStartedEvent;
import ru.yandex.qatools.allure.events.TestSuiteFinishedEvent;
import ru.yandex.qatools.allure.events.TestSuiteStartedEvent;
import ru.yandex.qatools.allure.utils.AnnotationManager;
import spock.lang.Unroll;

import java.util.UUID;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 24.06.14
 */
public class SpockRunListener extends AbstractRunListener {

    private Allure lifecycle = Allure.LIFECYCLE;

    private String suiteGuid;

    public SpockRunListener() {
        suiteGuid = UUID.randomUUID().toString();
    }


    @Override
    public void beforeSpec(SpecInfo specInfo) {
        TestSuiteStartedEvent event = new TestSuiteStartedEvent(suiteGuid, specInfo.getName());
        new AnnotationManager(specInfo.getDescription().getAnnotations()).update(event);
        getLifecycle().fire(event);
    }

    @Override
    public void beforeFeature(FeatureInfo featureInfo) {
        if  (!isDataTest(featureInfo.getDescription())) {
            TestCaseStartedEvent event = new TestCaseStartedEvent(suiteGuid, featureInfo.getName());
            new AnnotationManager(featureInfo.getDescription().getAnnotations()).update(event);
            getLifecycle().fire(event);
        }
    }

    @Override
    public void beforeIteration(IterationInfo iterationInfo) {
        if (isDataTest(iterationInfo.getDescription())) {
            TestCaseStartedEvent event = new TestCaseStartedEvent(suiteGuid, iterationInfo.getName());
            new AnnotationManager(iterationInfo.getDescription().getAnnotations()).update(event);
            getLifecycle().fire(event);
        }
    }

    @Override
    public void afterIteration(IterationInfo iterationInfo) {
        if (isDataTest(iterationInfo.getDescription())) {
            getLifecycle().fire(new TestCaseFinishedEvent());
        }
    }

    @Override
    public void afterFeature(FeatureInfo featureInfo) {
        if  (!isDataTest(featureInfo.getDescription())) {
            getLifecycle().fire(new TestCaseFinishedEvent());
        }
    }

    @Override
    public void afterSpec(SpecInfo spec) {
        getLifecycle().fire(new TestSuiteFinishedEvent(suiteGuid));
    }

    @Override
    public void error(ErrorInfo error) {
        getLifecycle().fire(new TestCaseFailureEvent().withThrowable(error.getException()));
    }

    @Override
    public void specSkipped(SpecInfo spec) {
    }

    @Override
    public void featureSkipped(FeatureInfo feature) {
    }

    public Allure getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Allure lifecycle) {
        this.lifecycle = lifecycle;
    }

    private static boolean isDataTest(Description description) {
        return new AnnotationManager(description.getAnnotations()).isAnnotationPresent(Unroll.class);
    }

}
