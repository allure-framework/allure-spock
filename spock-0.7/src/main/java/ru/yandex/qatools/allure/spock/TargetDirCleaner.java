package ru.yandex.qatools.allure.spock;

import ru.yandex.qatools.allure.config.AllureConfig;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class TargetDirCleaner {

    public static void deleteTestReports(File dir) {
        if (AllureSpockConfig.newInstance().isDeleteAllureTestReportResult()) {

            if (!dir.exists()) return;

            String testSuiteFileRegex = AllureConfig.newInstance().getTestSuiteFileRegex();
            final Pattern pattern = Pattern.compile(testSuiteFileRegex);

            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return pattern.matcher(name).matches();
                }
            });

            if (files == null) return;

            for (File file : files) {
                file.delete();
            }
        }
    }

}
