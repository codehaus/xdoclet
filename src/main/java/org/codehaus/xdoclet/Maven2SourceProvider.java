package org.codehaus.xdoclet;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.SelectorUtils;
import org.xdoclet.JavaSourceProvider;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Espen Amble Kolstad
 * @author gjoseph
 * @version $Revision$
 */
public class Maven2SourceProvider implements JavaSourceProvider {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final String encoding;
    /**
     * A list of String paths relative to the basedir.
     */
    private final List compileSourceRoots;
    /**
     * A List of URLs also containing sources to be parsed.
     */
    private final List sourceJars;

    private final String[] includes;
    private final String[] excludes;

    public Maven2SourceProvider(Config config, List compileSourceRoots, List sourceJars) {
        this.encoding = config.getEncoding();
        this.compileSourceRoots = compileSourceRoots;
        this.sourceJars = sourceJars;
        this.includes = Util.toTrimmedStringArray(config.getIncludes());
        this.excludes = Util.toTrimmedStringArray(config.getExcludes());
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
        // open all jars and add each and every matching entry
        final Iterator itSrcJars = sourceJars.iterator();
        while (itSrcJars.hasNext()) {
            final File file = (File) itSrcJars.next();
            final URL fileUrl = file.toURI().toURL();
            final String urlPrefix = "jar:" + fileUrl.toExternalForm() + "!/";
            final JarFile jarFile = new JarFile(file);
            final Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                final String entryName = entry.getName();
                if (isIncluded(entryName) && !isExcluded(entryName)) {
                    final URL url = new URL(urlPrefix + entryName);
                    urls.add(url);
                }
            }
        }
        return urls;
    }

    public String getEncoding() {
        return encoding;
    }


    private final static boolean caseSensitive = true;

    // copied from plexus-util DirectoryScanner:
    protected boolean isIncluded(String name) {
        for (int i = 0; i < includes.length; i++) {
            if (matchPath(includes[i], name, caseSensitive)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isExcluded(String name) {
        for (int i = 0; i < excludes.length; i++) {
            if (matchPath(excludes[i], name, caseSensitive)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
        return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
    }
}