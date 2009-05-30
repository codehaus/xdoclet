package org.xdoclet.predicate;

import org.apache.commons.collections.Predicate;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class IsA implements Predicate {
    private String className;

    public IsA() {
    }

    public IsA(String className) {
        setClassName(className);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public final boolean evaluate(Object o) {
        JavaClass clazz = (JavaClass) o;
        return clazz.isA(className);
    }
}
