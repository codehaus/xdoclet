/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An QDoxPropertyExpander that lookups entries in a set of properties objects
 * @author Diogo Quintela
 */
public class PropertiesQDoxPropertyExpander extends AbstractQDoxPropertyExpander {
    /** Map with key that represent Id's for properties. Values are instances of java.util.Properties */
    private Map propertiesMap = new HashMap();

    /**
     * Constructs a new PropertiesQDoxPropertyExpander
     */
    public PropertiesQDoxPropertyExpander() {
        super();
    }

    /**
     * Add's a java.util.Properties
     * @param id The name for it
     * @param props The properties
     */
    public void addProperties(String id, Properties props) {
        if (id == null || props == null) {
            throw new NullPointerException("id cannot be null");
        }

        propertiesMap.put(id, props);
    }

    /**
     * Gets matching pattern.
     * If we want to match ${[a-z]+} we should return [a-z]+
     * @return The inner pattern to match
     */
    protected String getPattern() {
        return "([-0-9a-zA-Z_]+)\\.([-0-9a-zA-Z_]+)";
    }

    /**
     * Expands the given property, or null if property could not be expanded
     * @param property The property to be expanded
     * @return Expanded value of null
     */
    protected String expandProperty(String property, String[] groups) {
        String retVal = null;
        Properties props = (Properties) propertiesMap.get(groups[1]);

        if (props != null) {
            retVal = props.getProperty(groups[2]);
        }

        return retVal;
    }
}
