package org.xdoclet.predicate;

import org.apache.commons.collections.Predicate;
import com.thoughtworks.qdox.model.AbstractInheritableJavaEntity;
import com.thoughtworks.qdox.model.DocletTag;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class HasTag implements Predicate {
    private String tagName;
    private String attributeName;
    private String attributeValue;
    private boolean superclasses = false;

    public HasTag() {
    }

    public HasTag(String tagName, String attributeName, String attributeValue, boolean superclasses) {
        setTagName(tagName);
        setAttributeName(attributeName);
        setAttributeValue(attributeValue);
        setSuperclasses(superclasses);
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public void setSuperclasses(boolean superclasses) {
        this.superclasses = superclasses;
    }

    public boolean evaluate(Object o) {
        AbstractInheritableJavaEntity javaEntiry = (AbstractInheritableJavaEntity) o;
        boolean result = false;
        if (tagName == null) {
            throw new IllegalStateException("tagName must be specified");
        }

        DocletTag tag = javaEntiry.getTagByName(tagName, superclasses);
        if(tag == null) {
            result = false;
        } else {
            if ((attributeName == null) && (attributeValue == null)) {
                // just see if the tag is there
                result = tag != null;
            } else if ((attributeName != null) && (attributeValue != null)) {
                // see if the tag attribute is there with the right value
                return attributeValue.equals(tag.getNamedParameter(attributeName));
            } else {
                throw new IllegalStateException(
                    "Both or none of attributeName and attributeValue must be specified");
            }
        }
        return result;
    }
}
