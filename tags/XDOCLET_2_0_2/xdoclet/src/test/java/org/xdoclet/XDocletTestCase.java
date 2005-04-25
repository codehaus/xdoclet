package org.xdoclet;

import junit.framework.TestCase;
import org.generama.tests.SinkWriterMapper;
import org.generama.MetadataProvider;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDocletTestCase extends TestCase {

    public static class TestJavaSourceProvider implements JavaSourceProvider {
        public Collection getURLs() {
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
        xDoclet.composeContainer(pico, null);
        pico.registerComponentInstance(Boolean.TRUE);

        QDoxMetadataProvider mp = (QDoxMetadataProvider) pico.getComponentInstanceOfType(MetadataProvider.class);
        assertNotNull(mp);
    }

}
