package org.xdoclet.ant;

import org.generama.Generama;
import org.generama.ant.AbstractGeneramaTask;
import org.generama.defaults.FileWriterMapper;
import org.xdoclet.XDoclet;
import org.apache.tools.ant.types.FileSet;
import org.picocontainer.MutablePicoContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDocletTask extends AbstractGeneramaTask {
    private Collection filesets = new ArrayList();
    private String encoding = System.getProperty("file.encoding");

    protected Generama createGenerama() throws IOException {
        return new XDoclet(AntFileProvider.class, FileWriterMapper.class) {
            public void install(MutablePicoContainer pico) {
                super.install(pico);
                pico.registerComponentInstance(filesets);
                pico.registerComponentInstance(getProject());
                pico.registerComponentInstance(encoding);
            }
        };
    }

    public void addFileset(FileSet fileSet) {
        filesets.add(fileSet);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
