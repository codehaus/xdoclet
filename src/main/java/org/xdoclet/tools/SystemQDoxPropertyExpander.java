/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet.tools;

/**
 * An QDoxPropertyExpander that lookups entries in system properties
 * @author Diogo Quintela
 */
public class SystemQDoxPropertyExpander extends AbstractQDoxPropertyExpander {
    /**
     * Constructs a new SystemQDoxPropertyExpander
     */
    public SystemQDoxPropertyExpander() {
        super();
    }

    /**
     * Expands the given property, or null if property could not be expanded
     * @param property The property to be expanded
     * @return Expanded value of null
     */
    protected String expandProperty(String property, String[] groups) {
        String retVal = null;

        try {
            retVal = System.getProperty(property);
        } catch (IllegalArgumentException e) {
            // Ignore IllegalArgumentException thrown in System.getProperty
        }

        return retVal;
    }
}
