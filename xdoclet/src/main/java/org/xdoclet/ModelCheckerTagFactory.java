/*
 * Copyright (c) 2005
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.generama.ConfigurableDocletTagFactory;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.DocletTagFactory;

/**
 * @author Diogo Quintela
 */
public class ModelCheckerTagFactory implements DocletTagFactory {
    private ConfigurableDocletTagFactory docletTagFactory;
    private Collection tagLst;

    public ModelCheckerTagFactory(ConfigurableDocletTagFactory docletTagFactory) {
        this.docletTagFactory = docletTagFactory;
        this.tagLst = new ArrayList();
    }

    public DocletTag createDocletTag(String tag, String text) {
        DocletTag retVal = docletTagFactory.createDocletTag(tag, text);
        if (retVal instanceof XDocletTag) {
            tagLst.add(retVal);
        }
        return retVal;
    }

    public DocletTag createDocletTag(String tag, String text, AbstractJavaEntity context, int lineNumber) {
        DocletTag retVal = docletTagFactory.createDocletTag(tag, text, context, lineNumber);
        if (retVal instanceof XDocletTag) {
            tagLst.add(retVal);
        }
        return retVal;
    }

    public void validateModel() {
        CollectionUtils.forAllDo(tagLst, new Closure() {
            public void execute(Object arg0) {
                XDocletTag tag = (XDocletTag)arg0;
                tag.validateModel();
            }
        });
    }
}
