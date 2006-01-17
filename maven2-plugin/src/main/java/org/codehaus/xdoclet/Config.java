package org.codehaus.xdoclet;

import java.util.Collections;
import java.util.Map;

/**
 * @author Espen Amble Kolstad
 * @author gjoseph
 * 
 * @version $Revision$
 */
public class Config {
    private String plugin;
    private String includes = "**/*.java";
    private String excludes = "";
    private String encoding = System.getProperty("file.encoding");
    private Map params = Collections.EMPTY_MAP;
    private boolean addToSources = true;

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public String getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
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