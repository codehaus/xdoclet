/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet.plugin;

import org.generama.JellyTemplateEngine;
import org.generama.QDoxCapableMetadataProvider;
import org.generama.WriterMapper;

import org.generama.defaults.QDoxPlugin;

/**
 * @author Diogo Quintela
 */
public class QDoxPropertyExpanderDummyPlugin extends QDoxPlugin {
    public QDoxPropertyExpanderDummyPlugin(JellyTemplateEngine jellyTemplateEngine,
        QDoxCapableMetadataProvider metadataProvider, WriterMapper writerMapper) {
        super(jellyTemplateEngine, metadataProvider, writerMapper);
        setMultioutput(false);
        metadataProvider.getDocletTagFactory().registerTag(DummyClassTagImpl.NAME, DummyClassTagImpl.class);
    }
}
