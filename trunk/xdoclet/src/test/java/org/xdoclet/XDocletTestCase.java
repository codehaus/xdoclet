package org.xdoclet;

import org.generama.Generama;
import org.generama.tests.SinkWriterMapper;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDocletTestCase extends TestCase {

    public static class TestJavaSourceProvider implements JavaSourceProvider {
        public Collection getFiles() {
            Reader one = new StringReader("" +
                    "public class One{}"
            );
            Reader two = new StringReader("" +
                    "public class Two{}"
            );
            Reader three = new StringReader("" +
                    "public class Three{}"
            );
            List result = new ArrayList();
            result.add(one);
            result.add(two);
            result.add(three);
            return result;
        }

        public String getEncoding() {
            return null;
        }
    }

    protected XDoclet createGeneramaWithThreeMetadataObjects() throws IOException {
        return new XDoclet(TestJavaSourceProvider.class, SinkWriterMapper.class);
    }

    public void testTagRegistration() throws IOException {
        XDoclet xDoclet = createGeneramaWithThreeMetadataObjects();
        MutablePicoContainer pico = new DefaultPicoContainer();
        xDoclet.install(pico);

        QDoxMetadataProvider mp = (QDoxMetadataProvider) pico.getComponentInstance(QDoxMetadataProvider.class);
        assertNotNull(mp);
    }

}
