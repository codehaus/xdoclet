/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet.ant;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Describes a property file
 *
 * @author Diogo Quintela
 */
public class Properties {
    private String id;
    private String file;

    public Properties() {
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public java.util.Properties getProperties() {
        try {
            java.util.Properties p = new java.util.Properties();
            p.load(new FileInputStream(getFile()));
            return p;
        } catch (IOException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }
}
