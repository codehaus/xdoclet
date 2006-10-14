/*
 * Copyright (c) 2003
 * XDoclet Team
 * All rights reserved.
 */
package org.xdoclet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generama.ConfigurableDocletTagFactory;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.DefaultDocletTag;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * A copy of ConfigurableDocletTagFactory that permits construction with an
 * QDoxPropertyExpander.
 * At present, extends ConfigurableDocletTagFactory to match QDoxMetadataProvider
 *
 * TODO: Refactor this, and maybe ConfigurableDocletTagFactory could be transformed
 * in an interface. A copy of the class is not definitly a good solution :/
 *
 * @author Diogo Quintela
 *
 * @see org.generama.ConfigurableDocletTagFactory
 */
public class ExpanderConfigurableDocletTagFactory extends ConfigurableDocletTagFactory {
    /** Property expander */
    private final QDoxPropertyExpander expander;
    private List unknownTags = new ArrayList();
    private Map registeredTags = null;

    /**
     * Default construtor
     */
    public ExpanderConfigurableDocletTagFactory(QDoxPropertyExpander expander) {
        // Super constructor register tags also, repeating here hence
        // We are suposing duplicating the class. :(
        String[] defaultTags = new String[] {"param", "author", "see", "since", "exception", "throws", "version", "return", "inheritDoc", "deprecated"};

        for (int i = 0; i < defaultTags.length; i++) {
            registerTag(defaultTags[i], DefaultDocletTag.class);
        }

        this.expander = expander;
    }

    public DocletTag createDocletTag(String tag, String text) {
        throw new UnsupportedOperationException();
    }

    public DocletTag createDocletTag(String tag, String text, AbstractJavaEntity context, int lineNumber) {
        if (registeredTags == null) {
            registeredTags = new HashMap();
        }
        Class tagClass = (Class) registeredTags.get(tag);
        boolean isKnown = true;

        if (tagClass == null) {
            tagClass = DefaultDocletTag.class;
            isKnown = false;
        }

        Class[][] constructors = {
                {String.class, String.class, AbstractJavaEntity.class, Integer.TYPE, QDoxPropertyExpander.class},
                {String.class, String.class, AbstractJavaEntity.class, Integer.TYPE}
            };
        Object[][] constructorArgs = {
                {tag, text, context, new Integer(lineNumber), expander},
                {tag, text, context, new Integer(lineNumber)}
            };

        // Try all defined constructors
        // This eventually could be built using a PicoContainer, but maybe not
        // without using a dummy class to encapsulate the two argument strings
        for (int i = 0; i < constructors.length; i++) {
            try {
                Constructor newTag = tagClass.getConstructor(constructors[i]);
                DocletTag result = (DocletTag) newTag.newInstance(constructorArgs[i]);

                if (!isKnown) {
                    unknownTags.add(result);
                }

                return result;
            } catch (NoSuchMethodException e) {
                // Try all constructors
                // Swallow the exception, a runtime exception will be thrown later
                // If no constructor could be used
            } catch (ClassCastException e) {
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getTargetException());
            }
        }

        // Could not find a suitable constructor
        throw new RuntimeException("No (String, String, AbstractJavaEntity, int) constructor in " + tagClass.getName());
        /*
           try {
               Constructor newTag = tagClass.getConstructor(new Class[] {String.class, String.class, AbstractJavaEntity.class, Integer.TYPE});
               DocletTag result = (DocletTag) newTag.newInstance(new Object[] {tag, text, context, new Integer(lineNumber)});
               if (!isKnown) {
                   unknownTags.add(result);
               }
               return result;
           } catch (ClassCastException e) {
               throw new RuntimeException(e);
           } catch (NoSuchMethodException e) {
               throw new RuntimeException("No (String, String, AbstractJavaEntity, int) constructor in " +
                   tagClass.getName());
           } catch (SecurityException e) {
               throw new RuntimeException(e);
           } catch (InstantiationException e) {
               throw new RuntimeException(e);
           } catch (IllegalAccessException e) {
               throw new RuntimeException(e);
           } catch (IllegalArgumentException e) {
               throw new RuntimeException(e);
           } catch (InvocationTargetException e) {
               throw new RuntimeException(e.getTargetException());
           }
         */
    }

    public void registerTag(String tagName, Class tagClass) {
        // Avoid null pointer because of our hack
        if (registeredTags == null) {
            registeredTags = new HashMap();
        }
        registeredTags.put(tagName, tagClass != null ? tagClass : DefaultDocletTag.class);
    }

    public void registerTags(Map tags) {
        Set tagNames = tags.keySet();

        for (Iterator iterator = tagNames.iterator(); iterator.hasNext();) {
            String tagName = (String) iterator.next();
            registerTag(tagName, (Class) tags.get(tagName));
        }
    }

    public List getUnknownTags() {
        return unknownTags;
    }

    public void printUnknownTags() {
        for (Iterator iterator = unknownTags.iterator(); iterator.hasNext();) {
            DocletTag docletTag = (DocletTag) iterator.next();
            System.out.println("Unknown tag: @" + docletTag.getName() + " in " + getLocation(docletTag) + " (line " +
                docletTag.getLineNumber() + ")");
        }
    }

    public static String getLocation(DocletTag tag) {
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
