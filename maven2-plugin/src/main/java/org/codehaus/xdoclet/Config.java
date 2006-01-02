package org.codehaus.xdoclet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Espen Amble Kolstad
 *
 * @version $Revision$
 */
public class Config {
    private String plugin;
    private Set includes = new HashSet();
    private Set excludes = new HashSet();
    private String encoding = System.getProperty("file.encoding");
    private Map params = new HashMap();
    private boolean addToSources = true;

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public Set getIncludes() {
        return includes;
    }

    public void setIncludes(Set includes) {
        this.includes = includes;
    }

    public Set getExcludes() {
        return excludes;
    }

    public void setExcludes(Set excludes) {
        this.excludes = excludes;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public boolean isAddToSources() {
        return addToSources;
    }

    public void setAddToSources(boolean addToSources) {
        this.addToSources = addToSources;
    }

    public String toString() {
        return "Config{" +
                "plugin='" + plugin + '\'' +
                ", includes=" + includes +
                ", excludes=" + excludes +
                ", encoding='" + encoding + '\'' +
                ", addToSources='" + addToSources + '\'' +
                ", params=" + params +
                '}';
    }
}