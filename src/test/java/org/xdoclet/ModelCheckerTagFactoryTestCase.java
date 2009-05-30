/*
 * Copyright (c) 2005
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.generama.ConfigurableDocletTagFactory;
import org.generama.MetadataProvider;
import org.generama.tests.SinkWriterMapper;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.JavaField;

/**
 * @author Diogo Quintela
 */
public class ModelCheckerTagFactoryTestCase extends TestCase {
    public static class TestJavaSourceProvider implements JavaSourceProvider {
        public Collection getURLs() {
            Collection retVal = new ArrayList();
            URL resource = Thread.currentThread().getContextClassLoader().getResource( "org/xdoclet/beans/TestBean.java");
            assertNotNull(resource);
            retVal.add(resource);
            return retVal;
        }

        public String getEncoding() {
            String retVal = System.getProperty("file.encoding");
            assertNotNull(retVal);
            return retVal;
        }
    }

    protected XDoclet createGeneramaBeans() {
        return new XDoclet(TestJavaSourceProvider.class, SinkWriterMapper.class);
    }

    public void testQDoxMetadataProviderDelegation() {
        XDoclet xDoclet = createGeneramaBeans();
        MutablePicoContainer pico = new DefaultPicoContainer();
        xDoclet.composeContainer(pico, null);
        pico.registerComponentInstance(Boolean.TRUE);

        QDoxMetadataProvider mp = (QDoxMetadataProvider) pico.getComponentInstanceOfType(MetadataProvider.class);
        assertNotNull(mp);
        ConfigurableDocletTagFactory docletTagFactory = mp.getDocletTagFactory();
        assertNotNull(docletTagFactory);
        docletTagFactory.registerTag("tag2", TagTwo.class);

        // Induce execution
        Collection metadata = mp.getMetadata();
        assertNotNull(metadata);

        TagTwo tagTwo = TagTwo.get();
        assertNotNull(tagTwo);
        tagTwo.verify();
    }


    public void testCheckDelegation() {
        ConfigurableDocletTagFactory docletTagFactory = new ConfigurableDocletTagFactory();
        docletTagFactory.registerTag("tag1", TagOne.class);
        ModelCheckerTagFactory modelChecker = new ModelCheckerTagFactory(docletTagFactory);
        modelChecker.createDocletTag("author", "author value", new JavaField(), 0);
        modelChecker.createDocletTag("tag1", "tag value", new JavaField(), 0);
        modelChecker.createDocletTag("throws", "Exception", new JavaField(), 0);
        modelChecker.validateModel();

        TagOne tagOne = TagOne.get();
        assertNotNull(tagOne);
        tagOne.verify();
    }

    public static class TestTag extends XDocletTag {
        private boolean called;

        public TestTag(String name, String value, AbstractBaseJavaEntity context, int lineNumber) {
            super(name, value, context, lineNumber);
            this.called = false;
        }

        protected void validateLocation() {
            // nothing
        }

        public void validateModel() {
            this.called = true;
        }

        public void verify() {
            if (!called) {
                throw new RuntimeException("validateModel wasn't called");
            }
        }
    }

    public static class TagOne extends TestTag {
        private static TagOne singleton;

        public TagOne(String name, String value, AbstractBaseJavaEntity context, int lineNumber) {
            super(name, value, context, lineNumber);
            singleton = this;
        }

        public static TagOne get() {
            return singleton;
        }
    }

    public static class TagTwo extends TestTag {
        private static TagTwo singleton;

        public TagTwo(String name, String value, AbstractBaseJavaEntity context, int lineNumber) {
            super(name, value, context, lineNumber);
            singleton = this;
        }

        public static TagTwo get() {
            return singleton;
        }
    }
}
