package org.xdoclet;

import org.generama.MetadataProvider;

import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.io.*;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * This MetadataProvider provides QDox {@link JavaClass} objects.
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class QDoxMetadataProvider implements MetadataProvider {
    private final ConfigurableDocletTagFactory docletTagFactory = new ConfigurableDocletTagFactory();

    private JavaSourceProvider fileProvider;
    private File singleSourceOrDirectory;

    /**
     * Main constructor. Gives fine control over what sources to parse.
     */
    public QDoxMetadataProvider(JavaSourceProvider fileProvider) {
        this.fileProvider = fileProvider;
    }

    /**
     * Convenience constructor for testing.
     */
    public QDoxMetadataProvider(final Reader singleSource) {
        this(new JavaSourceProvider() {
            public Collection getFiles() throws IOException {
                return Collections.singleton(singleSource);
            }

            public String getEncoding() {
                return "ISO-8859-1";
            }
        });
    }

    /**
     * Convenience constructor for testing. If singleSourceOrDirectory
     * is a directory, all sources in it will be parsed. 
     */
    public QDoxMetadataProvider(final File singleSourceOrDirectory) {
        if(!singleSourceOrDirectory.exists()) {
            throw new IllegalArgumentException(singleSourceOrDirectory.getAbsolutePath() + " doesn't exist! No Java sources to parse.");
        }
        this.singleSourceOrDirectory = singleSourceOrDirectory;
    }

    public ConfigurableDocletTagFactory getDocletTagFactory() {
        return docletTagFactory;
    }

    /**
     * @return a Collection of {@link JavaClass}.
     */
    public Collection getMetadata() throws IOException {
        JavaDocBuilder builder = new JavaDocBuilder(docletTagFactory);
        if (fileProvider != null) {
            builder.setEncoding(fileProvider.getEncoding());
            addSourcesFromJavaSourceProvider(builder);
        } else {
            if (singleSourceOrDirectory.isDirectory()) {
                builder.addSourceTree(singleSourceOrDirectory);
            } else {
                builder.addSource(singleSourceOrDirectory);
            }
        }
        return Arrays.asList(builder.getClasses());
    }

    private void addSourcesFromJavaSourceProvider(JavaDocBuilder builder) throws IOException {
        Collection files = fileProvider.getFiles();
        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
            Object next = iterator.next();
            if (next instanceof File) {
                File file = (File) next;
                builder.addSource(file);
            } else if (next instanceof Reader) {
                Reader reader = (Reader) next;
                builder.addSource(reader);
            }
        }
    }

    public String getOriginalFileName(Object metadata) {
        String result;
        if (metadata instanceof JavaClass) {
            JavaClass javaClass = (JavaClass) metadata;
            result = javaClass.getName() + ".java";
        } else {
            result = "";
        }
        return result;
    }

    public String getOriginalPackageName(Object metadata) {
        if (metadata instanceof JavaClass) {
            JavaClass javaClass = (JavaClass) metadata;
            return javaClass.getPackage();
        } else {
            return "";
        }
    }
}
