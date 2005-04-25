package org.xdoclet;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DefaultDocletTag;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.DocletTag;

import java.net.URL;

import org.generama.*;

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
        throw new RuntimeException("@" + getName() + " " + getValue() + "\n in " + org.generama.ConfigurableDocletTagFactory.getLocation(this) + " (line " + getLineNumber() + "):\n" + message);
    }

    public boolean isOnConstructor() {
        return isOnConstructor;
    }

    public void setOnConstructor(boolean onConstructor) {
        isOnConstructor = onConstructor;
    }

    public boolean isOnMethod() {
        return isOnMethod;
    }

    public void setOnMethod(boolean onMethod) {
        isOnMethod = onMethod;
    }

    public boolean isOnField() {
        return isOnField;
    }

    public void setOnField(boolean onField) {
        isOnField = onField;
    }

    public boolean isOnClass() {
        return isOnClass;
    }

    public void setOnClass(boolean onClass) {
        isOnClass = onClass;
    }
}
