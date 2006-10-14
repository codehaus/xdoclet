/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet.plugin;


public class DummyClassTagImpl extends org.xdoclet.XDocletTag {
    public static final String NAME = "dummy.class";
    private static final java.util.List ALLOWED_PARAMETERS = java.util.Arrays.asList(new String[] {"name", "value", "extra", ""});
    private static final java.util.List ALLOWED_VALUES = java.util.Arrays.asList(new String[] {""});

    public DummyClassTagImpl(String name, String value, com.thoughtworks.qdox.model.AbstractJavaEntity entity,
            int lineNumber, org.xdoclet.QDoxPropertyExpander expander) {
            super(name, value, entity, lineNumber, expander);
        }

    public DummyClassTagImpl(String name, String value, com.thoughtworks.qdox.model.AbstractJavaEntity entity,
        int lineNumber) {
        super(name, value, entity, lineNumber);
    }

    public java.lang.String getName_() {
        return getNamedParameter("name");
    }

    public java.lang.String getValue() {
        return getNamedParameter("value");
    }

    public java.lang.String getExtra() {
        return getNamedParameter("extra");
    }

    protected void validateLocation() {
        if (isOnField) {
            bomb("is not allowed on fields");
        }

        if (isOnConstructor) {
            bomb("is not allowed on constructors");
        }

        if (isOnMethod) {
            bomb("is not allowed on methods");
        }

        // check uniqueness
        if (getContext().getTagsByName(NAME).length > 1) {
            bomb("is allowed only once");
        }

        // warn deprecation
        // check for allowed values for whole tag
        if (ALLOWED_VALUES.size() > 1 && !ALLOWED_VALUES.contains(getValue())) {
            bomb("\"" + getValue() + "\" is not a valid value. Allowed values are ");
        }

        // Verify that all parameters are known.
        final java.util.Collection parameterNames = getNamedParameterMap().keySet();

        for (java.util.Iterator iterator = parameterNames.iterator(); iterator.hasNext();) {
            String parameterName = (String) iterator.next();

            if (!ALLOWED_PARAMETERS.contains(parameterName)) {
                bomb(parameterName + " is an invalid parameter name.");
            }
        }

        // Get all the parameters to validate their contents
        getName_();
        getValue();
        getExtra();
    }

	public void validateModel() {
	}
}
