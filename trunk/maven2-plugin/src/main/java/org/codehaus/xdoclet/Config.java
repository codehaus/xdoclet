package org.codehaus.xdoclet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Espen Amble Kolstad
 * @author gjoseph
 * @author Konstantin Pribluda
 * 
 * @version $Revision$
 */
public class Config {
    private boolean addToSources = true;
    private List components = Collections.EMPTY_LIST;
    private String encoding = System.getProperty("file.encoding");
    private String excludes = "";
    private String includes = "**/*.java";
    private Map params = Collections.EMPTY_MAP;

    public List getComponents() {
		return components;
	}

	public String getEncoding() {
        return encoding;
    }

	public String getExcludes() {
        return excludes;
    }

    public String getIncludes() {
        return includes;
    }

    public Map getParams() {
        return params;
    }


    public boolean isAddToSources() {
        return addToSources;
    }

    public void setAddToSources(boolean addToSources) {
        this.addToSources = addToSources;
    }

    public void setComponents(List components) {
		this.components = components;
	}

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public void setParams(Map params) {
        this.params = params;
    }


    public String toString() {
        return "Config{" +
                ", includes=" + includes +
                ", excludes=" + excludes +
                ", encoding='" + encoding + '\'' +
                ", addToSources='" + addToSources + '\'' +
                ", params=" + params +
                '}';
    }
}