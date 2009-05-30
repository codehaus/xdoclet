/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet;

import junit.framework.TestCase;

import java.util.Enumeration;
import java.util.Properties;

import org.xdoclet.tools.PropertiesQDoxPropertyExpander;
import org.xdoclet.tools.SystemQDoxPropertyExpander;

/**
 * Small test case for QDoxPropertyExpander
 * @author Diogo Quintela
 */
public class QDoxPropertyExpanderTestCase extends TestCase {
    public void testPropertiesQDoxPropertyExpander() {
        Properties props = new Properties();
        props.setProperty("test-key", "test-value");
        PropertiesQDoxPropertyExpander expander = new PropertiesQDoxPropertyExpander();
        expander.addProperties("props", props);
        String[][] tests = {
            {"abc", "abc"},
            {"${value}", "${value}"},
            {"${props.value}", "${props.value}"},
            {"${props.test-key}", "test-value"},
            {"lkjdasjkladslk ${props.test-key} adsjk", "lkjdasjkladslk test-value adsjk"},
            {"asd ${value} adsjh ${props.test-key} sdakdsa", "asd ${value} adsjh test-value sdakdsa"}
        };

        for (int i = 0; i < tests.length; i++) {
            assertEquals(expander.expand(tests[i][0]), tests[i][1]);
        }
    }

    public void testSystemQDoxPropertyExpander() {
        String[] tests = {"abc", "TEST", "lkjdasjkladslk TEST adsjk", "asd TEST adsjh TEST sdakdsa"};
        SystemQDoxPropertyExpander expander = new SystemQDoxPropertyExpander();

        for (int i = 0; i < tests.length; i++) {
            Enumeration keys = System.getProperties().keys();

            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String keyVal = System.getProperty(key);

                // Ignore empty keyVal's
                String firstVal = replace(tests[i], "TEST", keyVal);
                String secondVal = expander.expand(replace(tests[i], "TEST", "${" + key + "}"));
                assertEquals(tests[i] + "/" + key, firstVal, secondVal);
            }
        }
    }

    public void testReplace() {
        String[][] tests = {
            {"abc", "abc"},
            {"TEST", "${value}"},
            {"lkjdasjkladslk TEST adsjk", "lkjdasjkladslk ${value} adsjk"},
            {"asd TEST adsjh TEST sdakdsa", "asd ${value} adsjh ${value} sdakdsa"}
        };

        for (int i = 0; i < tests.length; i++) {
            assertEquals(replace(tests[i][0], "TEST", "${value}"), tests[i][1]);
        }
    }

    private String replace(String value, String token, String newValue) {
        int idx;

        while ((idx = value.indexOf(token)) >= 0) {
            value = value.substring(0, idx) + newValue + value.substring(idx + token.length());
        }

        return value;
    }
}
