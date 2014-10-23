package ru.yandex.qatools.allure.spock;

import org.spockframework.runtime.extension.IGlobalExtension;
import org.spockframework.runtime.model.SpecInfo;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 24.06.14
 */
public class AllureSpockExtension implements IGlobalExtension {

    @Override
    public void visitSpec(SpecInfo spec) {
        spec.addListener(new SpockRunListener());
    }
}
