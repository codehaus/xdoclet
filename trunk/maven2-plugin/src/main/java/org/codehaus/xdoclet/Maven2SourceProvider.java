package org.codehaus.xdoclet;

import org.codehaus.plexus.util.DirectoryScanner;
import org.xdoclet.JavaSourceProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Espen Amble Kolstad
 *
 * @version $Revision$
 */
public class Maven2SourceProvider implements JavaSourceProvider {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final String encoding;
    private final List compileSourceRoots;
    private final Set includes;
    private final Set excludes;

    public Maven2SourceProvider(Config config, List compileSourceRoots) {
        this.encoding = config.getEncoding();
        this.compileSourceRoots = compileSourceRoots;
        this.includes = config.getIncludes();
        this.excludes = config.getExcludes();
    }

    public Collection getURLs() throws IOException {
        final List urls = new ArrayList();
        final Iterator it = compileSourceRoots.iterator();
        while (it.hasNext()) {
            final String baseDir = (String) it.next();
            final DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(baseDir);
            scanner.setFollowSymlinks(true);
            scanner.setExcludes(toStringArray(excludes));
            scanner.setIncludes(toStringArray(includes));
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

    private String[] toStringArray(Set strings) {
        if (strings == null || strings.isEmpty()) {
            return EMPTY_STRING_ARRAY;
        }
        return (String[]) strings.toArray(new String[strings.size()]);
    }

    public String getEncoding() {
        return encoding;
    }
}