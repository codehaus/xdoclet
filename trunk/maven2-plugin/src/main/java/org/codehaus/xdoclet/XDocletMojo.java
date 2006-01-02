package org.codehaus.xdoclet;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.generama.Generama;
import org.generama.defaults.FileWriterMapper;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.DefaultLifecycleContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.BeanPropertyComponentAdapter;
import org.picocontainer.defaults.BeanPropertyComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.xdoclet.XDoclet;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Espen Amble Kolstad
 * @author gjoseph
 *
 * @goal xdoclet
 * @phase generate-sources
 * @description xdoclet2 plugin
 */
public class XDocletMojo extends AbstractMojo {
    private final ObjectReference containerRef = new SimpleReference();

    /**
     * The project to create a build for.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * A list of config for XDoclet.
     *
     * @parameter
     * @required
     */
    private List configs = new LinkedList();

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public List getConfigs() {
        return configs;
    }

    public void setConfigs(List configs) {
        this.configs = configs;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("getPluginContext() = " + getPluginContext());
        getLog().debug("project.getBuild().getDirectory() = " + project.getBuild().getDirectory());

        final String outputDir = project.getBuild().getDirectory() + "/generated-resources/xdoclet";
        final Map defaultPluginProps = new HashMap();
        defaultPluginProps.put("destdir", outputDir);

        final Iterator it = configs.iterator();
        while (it.hasNext()) {
            final Config config = (Config) it.next();
            getLog().info(config.toString());
            if (getSourceRoot() != null) {
                final Generama generama = createGenerama(config);
                final ContainerComposer containerComposer = new PluginContainerComposer(generama, config, defaultPluginProps);
                final ContainerBuilder containerBuilder = new PluginLifecycleContainerBuilder(containerComposer);
                try {
                    containerBuilder.buildContainer(containerRef, null, null, true);
                    containerBuilder.killContainer(containerRef);
                } catch (UndeclaredThrowableException e) {
                    Throwable ex = e.getUndeclaredThrowable();
                    if (ex instanceof InvocationTargetException) {
                        ex = ((InvocationTargetException) ex).getTargetException();
                    }
                    throw new MojoExecutionException("Undeclared: ", ex);
                } catch (RuntimeException e) {
                    throw new MojoExecutionException("XDoclet plugin failed: " + e.getMessage(), e);
                }
                if (config.isAddToSources()) {
                    String addedSourceRoot = resolveOutputDir(config, outputDir);
                    if (project != null) {
                        getLog().info("Adding " + addedSourceRoot + " to compiler path");
                        project.addCompileSourceRoot(addedSourceRoot);
                    }
                }
            }
        }
        final Resource resource = new Resource();
        getLog().debug("Output outputDir: " + outputDir);
        resource.setDirectory(outputDir);
        //resource.addInclude("* * / *.xml");
        project.addResource(resource);
    }

    private String resolveOutputDir(Config config, String defaultOuputPath) {
        String out = defaultOuputPath;
        Map params = config.getParams();
        if (params != null) {
            String destDir = (String) params.get("destdir");
            if (destDir != null) {
                out = destDir;
            }
        }
        return out;
    }

    private File getSourceRoot() {
        final List sourceRoots = project.getCompileSourceRoots();
        if (sourceRoots != null && !sourceRoots.isEmpty()) {
            final File file = new File((String) sourceRoots.get(0));
            getLog().info("Source-root: " + file.getAbsolutePath());
            return file;
        }
        return null;
    }

    private Generama createGenerama(final Config config) {
        return new XDoclet(Maven2SourceProvider.class, FileWriterMapper.class) {
            public void composeContainer(MutablePicoContainer pico, Object scope) {
                super.composeContainer(pico, scope);
                pico.registerComponentInstance(new SourceSet(getSourceRoot(), config));
            }
        };
    }

    private static class PluginLifecycleContainerBuilder extends DefaultLifecycleContainerBuilder {
        private BeanPropertyComponentAdapterFactory propertyFactory = new BeanPropertyComponentAdapterFactory(new DefaultComponentAdapterFactory());

        private PluginLifecycleContainerBuilder(ContainerComposer composer) {
            super(composer);
        }

        protected PicoContainer createContainer(PicoContainer parentContainer, Object assemblyScope) {
            return new DefaultPicoContainer(propertyFactory);
        }
    }

    private static class PluginContainerComposer implements ContainerComposer {
        private final ContainerComposer extraContainerComposer;
        private final Config config;
        private final Map defaultPluginProps;

        private PluginContainerComposer(ContainerComposer extraContainerComposer, Config config, Map defaultPluginProps) {
            this.extraContainerComposer = extraContainerComposer;
            this.config = config;
            this.defaultPluginProps = defaultPluginProps;
        }

        public void composeContainer(MutablePicoContainer picoContainer, Object assemblyScope) {
            if (extraContainerComposer != null) {
                extraContainerComposer.composeContainer(picoContainer, assemblyScope);
            }
            final ClassLoader loader = getClass().getClassLoader();
            final NanoContainer container = new DefaultNanoContainer(loader, picoContainer);
            try {
                BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) container.registerComponentImplementation(config.getPlugin(), config.getPlugin());
                final Map mergedProps = new HashMap();
                mergedProps.putAll(defaultPluginProps);
                mergedProps.putAll(config.getParams());
                adapter.setProperties(mergedProps);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class Not Found: " + e.getMessage(), e);
            }
        }
    }

}