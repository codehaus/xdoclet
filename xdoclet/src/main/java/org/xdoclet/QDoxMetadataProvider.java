package org.xdoclet;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import org.generama.ConfigurableDocletTagFactory;
import org.generama.QDoxCapableMetadataProvider;
import org.picocontainer.Startable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This MetadataProvider provides QDox {@link JavaClass} objects.
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class QDoxMetadataProvider implements QDoxCapableMetadataProvider, Startable {
    private final ConfigurableDocletTagFactory docletTagFactory = new ConfigurableDocletTagFactory();

    private JavaSourceProvider urlProvider;
    private File singleSourceOrDirectory;
    private Boolean verbose;

    /**
     * Main constructor. Gives fine control over what sources to parse.
     */
    public QDoxMetadataProvider(JavaSourceProvider fileProvider, Boolean verbose) {
        this.urlProvider = fileProvider;
        this.verbose = verbose;
    }
    /**
    * be verbose by default
    */
    public QDoxMetadataProvider(JavaSourceProvider fileProvider) {
        this(fileProvider,Boolean.TRUE);
    }
    
    /**
     * Convenience constructor for testing.
     */
    public QDoxMetadataProvider(final URL singleSource,Boolean verbose) {
        this(new JavaSourceProvider() {
            public Collection getURLs() {
                return Collections.singleton(singleSource);
            }

            public String getEncoding() {
                return "ISO-8859-1";
            }
        }, verbose);
    }
    /**
    * be werbose by default. 
    */
    public QDoxMetadataProvider(final URL singleSource) {
        this(singleSource,Boolean.TRUE);
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
     * @return a sorted Collection of {@link JavaClass}.
     */
    public Collection getMetadata() {
        try {
            ModelCheckerTagFactory modelChecker = new ModelCheckerTagFactory(docletTagFactory);
            JavaDocBuilder builder = new JavaDocBuilder(modelChecker);
            if (urlProvider != null) {
                builder.setEncoding(urlProvider.getEncoding());
                addSourcesFromJavaSourceProvider(builder);
            } else {
                if (singleSourceOrDirectory.isDirectory()) {
                    builder.addSourceTree(singleSourceOrDirectory);
                } else {
                    builder.addSource(singleSourceOrDirectory);
                }
            }
            modelChecker.validateModel();
            List result = Arrays.asList(builder.getClasses());
            Collections.sort(result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't parse Java Sources", e);
        }
    }

    private void addSourcesFromJavaSourceProvider(JavaDocBuilder builder) throws IOException {
        Collection urls = urlProvider.getURLs();
        for (Iterator iterator = urls.iterator(); iterator.hasNext();) {
            URL next = (URL) iterator.next();
            builder.addSource(next);
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
        }
		return "";
    }

    public void start() {
    }

	public void stop() {
        if (verbose.booleanValue()) {
		    docletTagFactory.printUnknownTags();
        }
	}
}
