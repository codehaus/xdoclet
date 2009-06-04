package org.codehaus.xdoclet;

import junit.framework.TestCase;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author Espen Amble Kolstad
 *
 * @version $Revision$
 */
public class TestXDocletMojo extends TestCase {
    private XDocletMojo mojo;

    public void testExecute() throws Exception {
        mojo.execute();
    }

    protected void setUp() throws Exception {
        super.setUp();

        mojo = new XDocletMojo();
        mojo.setProject(new MavenProject(new Model()) {
            public List getCompileSourceRoots() {
                return Arrays.asList(new File[]{new File("/home/espen/tmp")});
            }
        });
    }
}