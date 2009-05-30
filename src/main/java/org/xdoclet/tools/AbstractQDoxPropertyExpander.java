/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xdoclet.QDoxPropertyExpander;

/**
 * Basic implementation of QDoxPropertyExpander interface
 *
 * @author Diogo Quintela
 *
 * @see org.xdoclet.QDoxPropertyExpander
 */
public abstract class AbstractQDoxPropertyExpander implements QDoxPropertyExpander {
    /** Regexp pattern */
    private Pattern pattern;

    /**
     * Constructs a new AbstractQDoxPropertyExpander
     */
    public AbstractQDoxPropertyExpander() {
        pattern = Pattern.compile("\\$\\{(" + getPattern() + ")\\}");
    }

    /**
     * Gets matching pattern.
     * If we want to match ${[a-z]+} we should return [a-z]+
     * @return The inner pattern to match
     */
    protected String getPattern() {
        return "[-0-9a-zA-Z_.]+";
    }

    /**
     * Expands the given property, or null if property could not be expanded
     * @param property The property to be expanded
     * @return Expanded value of null
     */
    protected abstract String expandProperty(String property, String[] groups);

    /**
     * Expands the value
     * @see org.xdoclet.QDoxPropertyExpander#expand(java.lang.String)
     */
    public String expand(String value) {
        Matcher matcher = pattern.matcher(value);
        StringBuffer buf = new StringBuffer();
        String prop;
        int start = 0;
        boolean result = matcher.find();

        // Loop through and create a new String
        // with the replacements
        while (result) {
            String[] groups = new String[matcher.groupCount()];

            for (int j = 1; j <= groups.length; j++) {
                groups[j - 1] = matcher.group(j);
            }

            buf.append(value.substring(start, matcher.start(0)));
            start = matcher.end(0);
            prop = expandProperty(matcher.group(1), groups);

            if (prop != null) {
                buf.append(prop);
            } else {
                buf.append(matcher.group(0));
            }

            result = matcher.find();
        }

        // Add the last segment of input to
        // the new String
        buf.append(value.substring(start, value.length()));
        return buf.toString();
    }
}
