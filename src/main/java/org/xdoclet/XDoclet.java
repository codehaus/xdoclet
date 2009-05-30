package org.xdoclet;

import org.generama.Generama;
import org.picocontainer.MutablePicoContainer;

/**
 * This class installs the core XDoclet components in a <a href="http://www.picocontainer.org/">PicoContainer</a>.
 * (It relies on the superclass to compose the rest of the components).
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

    public void composeContainer(MutablePicoContainer pico, Object scope) {
        super.composeContainer(pico, scope);
        pico.registerComponentImplementation(javaSourceProviderClass);
    }
}
