package org.xdoclet;

import org.generama.Generama;
import org.generama.tests.SinkWriterMapper;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XDocletTestCase { // extends AbstractGeneramaTestCase {

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
    }

    protected Generama createGeneramaWithThreeMetadataObjects() {
        Generama generama = new XDoclet(TestJavaSourceProvider.class, SinkWriterMapper.class);
        return generama;
    }
}
