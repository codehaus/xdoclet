package org.xdoclet;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DefaultDocletTag;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;

import java.net.URL;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class XDocletTag extends DefaultDocletTag {
    protected boolean isOnConstructor;
    protected boolean isOnMethod;
    protected boolean isOnField;
    protected boolean isOnClass;

    public XDocletTag(String name, String value, int lineNumber) {
        super(name, value, lineNumber);
    }

    public void setContext(AbstractJavaEntity owner) {
        super.setContext(owner);
        isOnConstructor = false;
        isOnMethod = false;
        isOnField = false;
        isOnClass = false;
        if (getContext().getClass().equals(JavaMethod.class)) {
            JavaMethod method = (JavaMethod) getContext();
            isOnConstructor = method.isConstructor();
            isOnMethod = !method.isConstructor();
        } else if (getContext().getClass().equals(JavaField.class)) {
            isOnField = true;
        } else {
            isOnClass = true;
        }
        validateLocation();
    }

    protected abstract void validateLocation();

    public final void bomb(String message) {
        throw new RuntimeException("@" + getName() + " in " + getLocation(this) + " (line " + getLineNumber() + "): " + message);
    }

    static String getLocation(DocletTag tag) {
        String location = null;
        URL sourceURL = tag.getContext().getSource().getURL();
        if (sourceURL != null) {
            location = sourceURL.toExternalForm();
        } else {
            // dunno what file it is (might be from a reader).
            JavaClass clazz;
            if (tag.getContext() instanceof JavaClass) {
                // it's on a class (outer class)
                clazz = (JavaClass) tag.getContext();
            } else {
                clazz = (JavaClass) tag.getContext().getParent();
            }
            location = clazz.getFullyQualifiedName();
        }
        return location;
    }
}
