package org.codehaus.xdoclet;

import org.apache.maven.model.Resource;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.generama.JellyTemplateEngine;
import org.generama.MergeableVelocityTemplateEngine;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author Espen Amble Kolstad
 * @author gjoseph
 * @author Konstantin Pribluda
 * @version $Revision$
 * @goal xdoclet
 * @phase generate-sources
 * @description xdoclet2 plugin
 */
public class XDocletMojo extends AbstractMojo {
	private static class PluginContainerComposer implements ContainerComposer {
		private final List compileSourceRoots;
		private final List dependenciesSourcesURLs;

		private final Config config;

		private final Map defaultPluginProps;

		public PluginContainerComposer(Config config, Map defaultPluginProps,
				List compileSourceRoots, List dependenciesSourcesURLs) {
			this.config = config;
			this.defaultPluginProps = defaultPluginProps;
			this.compileSourceRoots = compileSourceRoots;
			this.dependenciesSourcesURLs = dependenciesSourcesURLs;
		}

		public void composeContainer(MutablePicoContainer pico,
				Object assemblyScope) {
			// duplicate of what is in Generama, XDoclet, XDocletTask to avoid
			// clumsy code
			pico.registerComponentImplementation(ClasspathFileResourceVelocityComponent.class);
			pico.registerComponentImplementation(QDoxMetadataProvider.class);
			pico.registerComponentImplementation(FileWriterMapper.class);
			pico.registerComponentImplementation(JellyTemplateEngine.class);
			pico.registerComponentImplementation(MergeableVelocityTemplateEngine.class);

			Maven2SourceProvider sourceProvider = new Maven2SourceProvider(
					config, compileSourceRoots, dependenciesSourcesURLs);
			pico.registerComponentInstance(sourceProvider);

			// register the plugin itself
			final ClassLoader cl = Thread.currentThread()
					.getContextClassLoader();
			// System.err.println("fuck, I got a classloader: " + cl);
			final NanoContainer nano = new DefaultNanoContainer(cl, pico);
			for (Iterator iter = config.getComponents().iterator(); iter
					.hasNext();) {
				Component comp = (Component) iter.next();
				try {
					BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) nano
							.registerComponentImplementation(
									comp.toString(), comp.getClassname());
					final Map mergedProps = new HashMap();
					mergedProps.putAll(defaultPluginProps);
					mergedProps.putAll(config.getParams());
					mergedProps.putAll(comp.getParams());
					adapter.setProperties(mergedProps);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Class Not Found: "
							+ e.getMessage(), e);
				}
			}
		}
	}

	private static class PluginLifecycleContainerBuilder extends
			DefaultLifecycleContainerBuilder {
		private BeanPropertyComponentAdapterFactory propertyFactory = new BeanPropertyComponentAdapterFactory(
				new DefaultComponentAdapterFactory());

		private PluginLifecycleContainerBuilder(ContainerComposer composer) {
			super(composer);
		}

		protected PicoContainer createContainer(PicoContainer parentContainer,
				Object assemblyScope) {
			return new DefaultPicoContainer(propertyFactory);
		}
	}

	/**
	 * A list of config for XDoclet.
	 *
	 * @parameter
	 * @required
	 */
	private List configs = new LinkedList();

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
	 * Local maven repository.
	 *
	 * @parameter expression="${localRepository}"
	 * @required
	 * @readonly
	 */
	private ArtifactRepository localRepository;

	/**
	 * Artifact factory, needed to download source jars for inclusion in classpath.
	 *
	 * @component role="org.apache.maven.artifact.factory.ArtifactFactory"
	 * @required
	 * @readonly
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * Artifact resolver, needed to download source jars for inclusion in classpath.
	 *
	 * @component role="org.apache.maven.artifact.resolver.ArtifactResolver"
	 * @required
	 * @readonly
	 */
	private ArtifactResolver artifactResolver;

	/**
	 * A comma separated list of patterns for which source jars artifacts should be passed to QDox for parsing.
	 * i.e "org.apache:*,com:*,net.sourceforge.myproject:myartifact" or even "foo.bar:baz*" are supported.
	 * The version can't be specified here and will be deducted from the project's dependencies.
	 * If you don't want to <strong>generate</strong> code or descriptors for these artifacts, you'll need
	 * to add <code>&lt;restrictedpath&gt;file://${settings.localRepository}&lt;/restrictedpath&gt;</code> to the
	 * appropriate components(xdoclet plugins).
	 * @parameter
	 */
	private String sourceArtifacts;

    /**
     * The output directory into which to copy the sources.
     * 
     * @parameter expression="${project.build.outputDirectory}/generated-resources/xdoclet"
     */
    private String sourcesOuputDirectory;

    /**
     * The output directory into which to copy the resources.
     * 
     * @parameter expression="${project.build.outputDirectory}/generated-resources/xdoclet"
     */
    private String resourcesOutputDirectory;

	public void execute() throws MojoExecutionException, MojoFailureException {
		final List dependenciesSourcesURLs = new ArrayList();

		final List sourceDependencyIncludes = Util.toTrimmedList(sourceArtifacts);
		final PatternIncludesArtifactFilter filter = new PatternIncludesArtifactFilter(sourceDependencyIncludes, true);

		if (!sourceDependencyIncludes.isEmpty()) {
			final List remoteArtifactRepositories = project.getRemoteArtifactRepositories();
			final List deps = project.getDependencies();

			final Iterator itDeps = deps.iterator();
			while (itDeps.hasNext()) {
				final Dependency dep = (Dependency) itDeps.next();
				final String artifactId = dep.getArtifactId();
				final String groupId = dep.getGroupId();
				final String version = dep.getVersion();
				final Artifact a = artifactFactory.createArtifactWithClassifier(groupId, artifactId, version, "java-source", "sources");
				try {
					if (filter.include(a)) {
						artifactResolver.resolve(a, remoteArtifactRepositories, localRepository);
						if (a.getFile() == null) {
							throw new ArtifactNotFoundException("Can't resolve", a);
						}
						dependenciesSourcesURLs.add(a.getFile());
					}
				} catch (ArtifactNotFoundException e) {
					throw new MojoExecutionException("Source artifact for " + groupId + ":" + artifactId + ":" + version + " was not found : " + e.getMessage());
				} catch (ArtifactResolutionException e) {
					getLog().warn("Could not download source artifact for " + groupId + ":" + artifactId + ":" + version + " : " + e.getMessage());
				}
			}
		}

		final Iterator it = configs.iterator();
		while (it.hasNext()) {
			final Config config = (Config) it.next();
			getLog().debug(config.toString());

			String defaultOuputPath = project.getBuild().getDirectory()
					+ "/generated-resources/xdoclet";
			final String outputPath = resolveOutputDir(config, defaultOuputPath);
			final Map defaultPluginProps = Collections.singletonMap("destdir",
					outputPath);
			final ContainerComposer containerComposer = new PluginContainerComposer(
					config, defaultPluginProps, project.getCompileSourceRoots(), dependenciesSourcesURLs);
			final ContainerBuilder containerBuilder = new PluginLifecycleContainerBuilder(
					containerComposer);
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
				throw new MojoExecutionException("XDoclet plugin failed: "
						+ e.getMessage(), e);
			}

            final File outpathFile = new File(outputPath);
            if (outpathFile.exists()) {
                // TODO if java-generating plugin
                if (config.isAddToSources()) {
                    if (!outputPath.equals(sourcesOuputDirectory)) {
                        copyOutput(outpathFile, new File(sourcesOuputDirectory), config.getExcludes(), config.getIncludes());
                    }

                    getLog().debug("Adding " + sourcesOuputDirectory + " to compiler path");
                    project.addCompileSourceRoot(sourcesOuputDirectory);
                    // TODO else
                }
                if (config.isAddToResources()) {
                    if (!outputPath.equals(resourcesOutputDirectory)) {
                        copyOutput(outpathFile, new File(resourcesOutputDirectory), config.getResourcesExcludes(), config.getResourcesIncludes());
                    }

                    final Resource resource = new Resource();
                    getLog().debug("Adding " + resourcesOutputDirectory + " to resources");
                    resource.setDirectory(resourcesOutputDirectory);
                    resource.addInclude(config.getResourcesIncludes());
                    resource.addExclude(config.getResourcesExcludes());
                    project.addResource(resource);
                }
            }
		}
	}

	public List getConfigs() {
		return configs;
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

    void copyOutput(File inputDir, File outputDir, String excludes, String includes) throws MojoExecutionException {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setExcludes(Util.toTrimmedStringArray(excludes));
        scanner.setIncludes(Util.toTrimmedStringArray(includes));
        scanner.setBasedir(inputDir);
        scanner.addDefaultExcludes();
        scanner.scan();
        List includedFiles = Arrays.asList(scanner.getIncludedFiles());
        for (Iterator j = includedFiles.iterator(); j.hasNext();) {
            String name = (String) j.next();

            File source = new File(inputDir, name);

            File destinationFile = new File(outputDir, name);

            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }

            try {
                if (destinationFile.lastModified() < source.lastModified()) {
                    FileUtils.copyFile(source, destinationFile);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Error copying resources", e);
            }
        }
    }

    public void setConfigs(List configs) {
		this.configs = configs;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

}
