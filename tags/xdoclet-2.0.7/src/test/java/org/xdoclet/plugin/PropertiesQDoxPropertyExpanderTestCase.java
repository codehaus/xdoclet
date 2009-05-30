/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet.plugin;

import java.io.File;

import java.net.URL;

import java.util.Properties;

import org.generama.JellyTemplateEngine;
import org.generama.MetadataProvider;
import org.generama.Plugin;
import org.generama.QDoxCapableMetadataProvider;
import org.generama.WriterMapper;

import org.generama.tests.AbstractXMLGeneratingPluginTestCase;

import org.xdoclet.QDoxMetadataProvider;

import org.xdoclet.tools.PropertiesQDoxPropertyExpander;

/**
 * @author Diogo Quintela
 */
public class PropertiesQDoxPropertyExpanderTestCase extends AbstractXMLGeneratingPluginTestCase {
    protected MetadataProvider createMetadataProvider() throws Exception {
        PropertiesQDoxPropertyExpander expander = new PropertiesQDoxPropertyExpander();
        Properties props = new Properties();
        props.setProperty("value", "props-test-value");
        expander.addProperties("props", props);
        URL reader = getResourceRelativeToThisPackage("classes");
        return new QDoxMetadataProvider(new File(reader.getPath()), expander);
    }

    protected URL getExpected() throws Exception {
        return this.getResourceRelativeToThisPackage("dummy.xml");
    }

    protected Plugin createPlugin(MetadataProvider metadataProvider, WriterMapper writerMapper) {
        return new QDoxPropertyExpanderDummyPlugin(new JellyTemplateEngine(),
            (QDoxCapableMetadataProvider) metadataProvider, writerMapper);
    }
}
