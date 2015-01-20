package ru.yandex.qatools.allure.spock;

import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.SpecInfo;
import ru.yandex.qatools.allure.config.AllureConfig;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 24.06.14
 */
public class AllureSpockExtension implements IGlobalExtension {

    private static boolean inited = false;

    @Override
    public void visitSpec(SpecInfo spec) {
        if (!inited) {
            doInit();
            inited = true;
        }
        spec.addListener(new SpockRunListener());
    }

    private void doInit() {
        TargetDirCleaner.deleteTestReports(AllureConfig.newInstance().getResultsDirectory());
    }
}
