package org.xdoclet.ant;

import org.xdoclet.JavaSourceProvider;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AntFileProvider implements JavaSourceProvider {
    private final Collection filesets;
    private final Project project;

    public AntFileProvider(Collection filesets, Project project) {
        this.filesets = filesets;
        this.project = project;
    }

    public Collection getFiles() {
        Collection files = new ArrayList();
        for (Iterator iterator = filesets.iterator(); iterator.hasNext();) {
            FileSet fileSet = (FileSet) iterator.next();
            DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(project);
            String[] srcFiles = directoryScanner.getIncludedFiles();
            for (int i = 0; i < srcFiles.length; i++) {
                String srcFile = srcFiles[i];
                File file = new File(directoryScanner.getBasedir(), srcFile);
                files.add(file);
            }
        }
        return files;
    }
}
