package org.xdoclet;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DefaultDocletTag;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class XDocletTag extends DefaultDocletTag {
    protected boolean isOnConstructor;
    protected boolean isOnMethod;
    protected boolean isOnField;
    protected boolean isOnClass;

    protected XDocletTag(String name, String value, AbstractJavaEntity context, int lineNumber) {
        super(name, value, context, lineNumber);
        if (JavaMethod.class.isAssignableFrom(getContext().getClass())) {
            JavaMethod method = (JavaMethod) getContext();
            isOnConstructor = method.isConstructor();
            isOnMethod = !method.isConstructor();
        } else if (JavaField.class.isAssignableFrom(getContext().getClass())) {
            isOnField = true;
        } else {
            isOnClass = true;
        }
        validateLocation();
    }

    protected abstract void validateLocation();

    public final void bomb(String message) {
        throw new RuntimeException("@" + getName() + " " + getValue() + "\n in " + getLocation(this) + " (line " + getLineNumber() + "):\n" + message);
    }
}
