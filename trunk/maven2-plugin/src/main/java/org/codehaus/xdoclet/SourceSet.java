package org.codehaus.xdoclet;

import java.io.File;
import java.util.Set;

/**
 * @author Espen Amble Kolstad
 *
 * @version $Revision$
 */
public class SourceSet {
    private String encoding;
    private File baseDir;
    private Set includes;
    private Set excludes;

    public SourceSet(File baseDir, Config config) {
        this.encoding = config.getEncoding();
        this.baseDir = baseDir;
        this.includes = config.getIncludes();
        this.excludes = config.getExcludes();
        if (this.includes.size() == 0) {
            includes.add("**/*.*");
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public Set getIncludes() {
        return includes;
    }

    public Set getExcludes() {
        return excludes;
    }
}