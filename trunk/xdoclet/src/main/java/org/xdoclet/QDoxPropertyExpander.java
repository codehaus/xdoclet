/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet;

/**
 * Interface that permits the expantion of QDox tags that matches "${anything}" into a runtime value.
 * Example from XDocletTask: ${props.value} expands "value" property from property file named "props".
 * If the property cannot be expanded, it should return the initial value
 *
 * @author Diogo Quintela
 */
public interface QDoxPropertyExpander {
    /**
     * Expands property value
     * @param value The property to expand
     * @return The expanded property, or the initial value
     * @see org.xdoclet.QDoxPropertyExpander
     */
    String expand(String value);
}
