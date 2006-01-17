package org.codehaus.xdoclet;

import org.codehaus.plexus.util.DirectoryScanner;
import org.xdoclet.JavaSourceProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Espen Amble Kolstad
 * @author gjoseph
 *
 * @version $Revision$
 */
public class Maven2SourceProvider implements JavaSourceProvider {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final String encoding;
    private final List compileSourceRoots;
    private final String[] includes;
    private final String[] excludes;

    public Maven2SourceProvider(Config config, List compileSourceRoots) {
        this.encoding = config.getEncoding();
        this.compileSourceRoots = compileSourceRoots;
        this.includes = toStringArray(config.getIncludes());
        this.excludes = toStringArray(config.getExcludes());
    }

    public Collection getURLs() throws IOException {
        final List urls = new ArrayList();
        final Iterator it = compileSourceRoots.iterator();
        while (it.hasNext()) {
            final String baseDir = (String) it.next();
            final DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(baseDir);
            scanner.setFollowSymlinks(true);
            scanner.setExcludes(excludes);
            scanner.setIncludes(includes);
            scanner.addDefaultExcludes();
            scanner.scan();
            final String[] files = scanner.getIncludedFiles();
            for (int i = 0; i < files.length; i++) {
                final File file = new File(baseDir, files[i]);
                urls.add(file.toURL());
            }
        }
        return urls;
    }

    private String[] toStringArray(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        return commaSeparated.split(",", 0);
    }

    public String getEncoding() {
        return encoding;
    }
}