package org.xdoclet.ant;

import org.xdoclet.JavaSourceProvider;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;
import java.net.MalformedURLException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AntSourceProvider implements JavaSourceProvider {
    private final Collection filesets;
    private final Project project;
    private final String encoding;

    public AntSourceProvider(Collection filesets, Project project, String encoding) {
        this.filesets = filesets;
        this.project = project;
        this.encoding = encoding;
    }

    public Collection getURLs() {
        Collection urls = new ArrayList();
        for (Iterator iterator = filesets.iterator(); iterator.hasNext();) {
            FileSet fileSet = (FileSet) iterator.next();
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(project);
            String[] srcFiles = directoryScanner.getIncludedFiles();
            for (int i = 0; i < srcFiles.length; i++) {
                String srcFile = srcFiles[i];
                File file = new File(directoryScanner.getBasedir(), srcFile);
                try {
                    urls.add(file.toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return urls;
    }

    public String getEncoding() {
        return encoding;
    }
}
