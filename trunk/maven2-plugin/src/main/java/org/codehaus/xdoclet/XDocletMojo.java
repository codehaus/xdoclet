package org.codehaus.xdoclet;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.generama.JellyTemplateEngine;
import org.generama.VelocityTemplateEngine;
import org.generama.defaults.FileWriterMapper;
import org.generama.velocity.ClasspathFileResourceVelocityComponent;
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
import org.xdoclet.QDoxMetadataProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
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
     * @readonly
     */
    private MavenProject project;

    /**
     * A list of config for XDoclet.
     *
     * @parameter
     * @required
     */
    private List configs = new LinkedList();

    public void execute() throws MojoExecutionException, MojoFailureException {
        final Iterator it = configs.iterator();
        while (it.hasNext()) {
            final Config config = (Config) it.next();
            getLog().debug(config.toString());

            String defaultOuputPath = project.getBuild().getDirectory() + "/generated-resources/xdoclet";
            final String outputPath = resolveOutputDir(config, defaultOuputPath);
            final Map defaultPluginProps = Collections.singletonMap("destdir", outputPath);
            final ContainerComposer containerComposer = new PluginContainerComposer(config, defaultPluginProps, project.getCompileSourceRoots());
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
                // TODO if java-generating plugin
                getLog().debug("Adding " + outputPath + " to compiler path");
                project.addCompileSourceRoot(outputPath);
                // TODO else
                final Resource resource = new Resource();
                getLog().debug("Adding " + outputPath + " to resources");
                resource.setDirectory(outputPath);
                resource.addInclude("**/*");
                project.addResource(resource);
            }
        }
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

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public void setConfigs(List configs) {
        this.configs = configs;
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
        private final Config config;
        private final Map defaultPluginProps;
        private final List compileSourceRoots;

        public PluginContainerComposer(Config config, Map defaultPluginProps, List compileSourceRoots) {
            this.config = config;
            this.defaultPluginProps = defaultPluginProps;
            this.compileSourceRoots = compileSourceRoots;
        }

        public void composeContainer(MutablePicoContainer pico, Object assemblyScope) {
            // duplicate of what is in Generama, XDoclet, XDocletTask to avoid clumsy code
            pico.registerComponentImplementation(ClasspathFileResourceVelocityComponent.class);
            pico.registerComponentImplementation(QDoxMetadataProvider.class);
            pico.registerComponentImplementation(FileWriterMapper.class);
            pico.registerComponentImplementation(JellyTemplateEngine.class);
            pico.registerComponentImplementation(VelocityTemplateEngine.class);

            Maven2SourceProvider sourceProvider = new Maven2SourceProvider(config, compileSourceRoots);
            pico.registerComponentInstance(sourceProvider);

            // register the plugin itself
            final ClassLoader cl = getClass().getClassLoader();
            final NanoContainer nano = new DefaultNanoContainer(cl, pico);
            try {
                BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) nano.registerComponentImplementation(config.getPlugin(), config.getPlugin());
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
