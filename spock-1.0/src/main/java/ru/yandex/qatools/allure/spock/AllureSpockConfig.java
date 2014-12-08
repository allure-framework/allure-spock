package ru.yandex.qatools.allure.spock;

import ru.yandex.qatools.properties.PropertyLoader;
import ru.yandex.qatools.properties.annotations.Property;
import ru.yandex.qatools.properties.annotations.Resource;

@Resource.Classpath("allure.properties")
public class AllureSpockConfig {

    @Property("allure.spock.results.delete")
    private Boolean deleteAllureTestReportResult = true;

    public Boolean isDeleteAllureTestReportResult() {
        return deleteAllureTestReportResult;
    }

    public AllureSpockConfig() {
        PropertyLoader.populate(this);
    }

    public static AllureSpockConfig newInstance() {
        return new AllureSpockConfig();
    }
}
