package org.xdoclet.ant;

import org.apache.tools.ant.types.FileSet;
import org.generama.Generama;
import org.generama.ant.AbstractGeneramaTask;
import org.generama.defaults.FileWriterMapper;
import org.picocontainer.MutablePicoContainer;
import org.xdoclet.XDoclet;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDocletTask extends AbstractGeneramaTask {
    private Collection filesets = new ArrayList();
    private String encoding = System.getProperty("file.encoding");
    private Boolean verbose = Boolean.TRUE;

    protected Generama createGenerama() {
        return new XDoclet(AntSourceProvider.class, FileWriterMapper.class) {
            public void composeContainer(MutablePicoContainer pico, Object scope) {
                super.composeContainer(pico, scope);
                pico.registerComponentInstance(filesets);
                pico.registerComponentInstance(getProject());
                pico.registerComponentInstance(encoding);
                pico.registerComponentInstance(verbose);
            }
        };
    }

    public void addFileset(FileSet fileSet) {
        filesets.add(fileSet);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }
}
