package org.xdoclet;

import org.generama.Generama;
import org.picocontainer.MutablePicoContainer;

/**
 * This class installs the core XDoclet components in a <a href="http://www.picocontainer.org/">PicoContainer</a>.
 * (It relies on the superclass to install the rest).
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDoclet extends Generama {
    private Class javaSourceProviderClass;

    public XDoclet(Class fileProviderClass, Class writerMapperClass) {
        super(QDoxMetadataProvider.class, writerMapperClass);
        this.javaSourceProviderClass = fileProviderClass;
    }

    public void install(MutablePicoContainer pico) {
        super.install(pico);
        pico.registerComponentImplementation(JavaSourceProvider.class, javaSourceProviderClass);
    }
}
