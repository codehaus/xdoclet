package org.xdoclet;

import com.thoughtworks.qdox.model.DefaultDocletTag;
import com.thoughtworks.qdox.model.DefaultDocletTagFactory;
import com.thoughtworks.qdox.model.DocletTag;

/**
 * A paranoid tag factory that will remember
 * if it is asked to create a tag it doesn't know about. All the
 * standard javadoc tags (as of JDK 1.4.2) are preregistered.
 * It is possible to register a tag by class, so that tags with a
 * particular name will be of a particular class. It is also possible
 * to register tags simply by name, and they will be of the default type.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ConfigurableDocletTagFactory extends DefaultDocletTagFactory {
    public ConfigurableDocletTagFactory() {
        String[] defaultTags = new String[] {
            "param",
            "author",
            "see",
            "since",
            "exception",
            "throws",
            "version",
            "return",
            "inheritDoc",
            "deprecated"
        };
        for (int i = 0; i < defaultTags.length; i++) {
            registerTag(defaultTags[i], DefaultDocletTag.class);
        }
    }

    public DocletTag createDocletTag(String tag, String text) {
        throw new UnsupportedOperationException();
    }
}
