package org.xdoclet;

import com.thoughtworks.qdox.model.DefaultDocletTag;
import com.thoughtworks.qdox.model.DocletTag;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ConfigurableDocletTagFactoryTestCase extends TestCase {
    public void testShouldFailOnUnknownTags() {
        ConfigurableDocletTagFactory tagFactory = new ConfigurableDocletTagFactory();
        DocletTag unknownTag = tagFactory.createDocletTag("unknown", "whatever", 0);
        List unknownTags = tagFactory.getUnknownTags();
        assertSame(unknownTag, unknownTags.get(0));
    }

    public void testShouldAcceptDefaultTags() {
        ConfigurableDocletTagFactory tagFactory = new ConfigurableDocletTagFactory();
        DocletTag knownTag1 = tagFactory.createDocletTag("author", "whoever", 0);
        DocletTag knownTag2 = tagFactory.createDocletTag("param", "whatever", 0);
        List unknownTags = tagFactory.getUnknownTags();
        assertEquals(0, unknownTags.size());
        assertTrue(knownTag1 instanceof DefaultDocletTag);
        assertTrue(knownTag2 instanceof DefaultDocletTag);
    }

    public static class SpecialTag extends DefaultDocletTag {
        public SpecialTag(String name, String value, int lineNumber) {
            super(name, value, lineNumber);
        }
    }

    public void testShouldCreateSpecialTagsIfRegisteredWithSpecialTag() {
        ConfigurableDocletTagFactory tagFactory = new ConfigurableDocletTagFactory();
        tagFactory.registerTag("special-tag", SpecialTag.class);
        DocletTag specialTag = tagFactory.createDocletTag("special-tag", "whoever", 0);
        assertTrue(specialTag instanceof SpecialTag);
    }

    public void testShouldCreateDefaultTagsIfRegisteredWithoutTagClass() {
        ConfigurableDocletTagFactory tagFactory = new ConfigurableDocletTagFactory();
        tagFactory.registerTag("smile-tag", null);
        DocletTag specialTag = tagFactory.createDocletTag("smile-tag", "whoever", 0);
        assertTrue(specialTag instanceof DefaultDocletTag);
    }
}
