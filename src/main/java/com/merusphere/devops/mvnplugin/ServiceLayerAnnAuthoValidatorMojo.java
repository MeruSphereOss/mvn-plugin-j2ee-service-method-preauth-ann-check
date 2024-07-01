package com.merusphere.devops.mvnplugin;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Checks the methods not having Security Annotations. Scan the classes
 * from given Package name from the configuration parameter named 'pkg'. Ignore the
 * list of classes from the scanning configuration parameter named 'ignoreClassList'. Ignore
 * the list of classes/methods having the annotation from the configuration
 * parameter named ignoreAnnotation
 *
 */
@Mojo(name = "j2ee-srv-method-ann-check", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresDependencyResolution = ResolutionScope.TEST)
public class ServiceLayerAnnAuthoValidatorMojo extends AbstractMojo {
	/**
	 * Ignore the list of classes from the configuration parameter named
	 * ignoreClassList
	 */
	@Parameter(property = "ignoreClassList")
	String[] ignoreClassList;

	/**
	 * Ignore the list of classes/methods having the annotation from the
	 * configuration parameter named ignoreAnnotation
	 */
	@Parameter(property = "ignoreAnnotation")
	String ignoreAnnotation;

	/**
	 * Scan the classes from the Package name from the configuration parameter named
	 * pkg
	 */
	@Parameter(property = "pkg")
	String pkg;

	/**
	 * Gives access to the Maven project information.
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;

	/**
	 * Checks the number of methods not having Security Annotations
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if (pkg == null || pkg.trim().length() <= 0) {
				new MojoExecutionException("Package name can't be null for the project : " + project.getName());
			}

			if (getLog().isInfoEnabled()) {
				getLog().info(new StringBuffer("Using Package " + pkg));
			}

			// Load the Class File information
			Set<URL> urls = new HashSet<>();
			List<String> testElements = project.getTestClasspathElements();
			for (String element : testElements) {
				urls.add(new File(element).toURI().toURL());
			}
			List<String> runtimeElements = project.getRuntimeClasspathElements();
			for (String element : runtimeElements) {
				urls.add(new File(element).toURI().toURL());
			}
			List<String> compiletimeElements = project.getRuntimeClasspathElements();
			for (String element : compiletimeElements) {
				urls.add(new File(element).toURI().toURL());
			}
			List<String> systemtimeElements = project.getRuntimeClasspathElements();
			for (String element : systemtimeElements) {
				urls.add(new File(element).toURI().toURL());
			}
			Set<Artifact> artifactSet = project.getArtifacts();
			for (Artifact artft : artifactSet) {
				urls.add(artft.getFile().toURI().toURL());
			}

			if (getLog().isInfoEnabled()) {
				getLog().info("Classpath Found " + urls.size() + " Classses");
			}
			for (URL url : urls) {
				getLog().info("URL file " + url.getFile());
			}

			// Load the Classpath
			ClassLoader contextClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[0]),
					Thread.currentThread().getContextClassLoader());

			Thread.currentThread().setContextClassLoader(contextClassLoader);

			// Load the Classes from the given Package name
			Reflections reflections = new Reflections(pkg);
			Set<Class<?>> allSet = reflections.getTypesAnnotatedWith(org.springframework.stereotype.Service.class,
					true);
			if (allSet.isEmpty()) {
				if (getLog().isInfoEnabled()) {
					getLog().info("No Service Impl classes Found");
				}
				return;
			} else {
				if (getLog().isInfoEnabled()) {
					getLog().info("Relection Found " + allSet.size() + " Classses");
					for (Class<?> clazz : allSet) {
						getLog().info("Relection found a class " + clazz);
					}
				}
			}

			// Load the ignoreClass List
			Map<String, String> ignoreClazzMap = new HashMap<>();
			if (ignoreClassList != null && ignoreClassList.length > 0) {
				for (int idx = 0; idx < ignoreClassList.length; idx++) {
					ignoreClazzMap.put(ignoreClassList[idx], "");
				}
			}

			// Identify the Classes not having PreAuthorize annotation
			List<String> errorList = new ArrayList<>();
			for (Class<?> clazz : allSet) {
				if (ignoreClazzMap.get(clazz.getCanonicalName()) != null) {
					if (getLog().isInfoEnabled()) {
						getLog().info(new StringBuffer("Ignoring a clazz " + clazz.getCanonicalName()));
					}
					continue;
				}
				if (getLog().isInfoEnabled()) {
					getLog().info(new StringBuffer("Scanning a clazz " + clazz.getCanonicalName()));
				}
				Method[] methodArr = clazz.getDeclaredMethods();
				if (methodArr.length == 0) {
					errorList.add("No Methods Found for clazz " + clazz.getCanonicalName());
				}
				for (Method method : methodArr) {
					Annotation ann1 = method.getAnnotation(PreAuthorize.class);
					Annotation ann2 = null;
					if (ignoreAnnotation == null) {
						Annotation[] annArr = method.getAnnotations();
						for (Annotation ann : annArr) {
							if (ann.getClass().getCanonicalName().equals(ignoreAnnotation)) {
								ann2 = ann;
								break;
							}
						}
					}
					if (ann1 == null && ann2 == null) {
						errorList.add("Annotation missing for " + clazz.getCanonicalName() + "." + method.getName());
					}
				}
			}
			if (getLog().isInfoEnabled()) {
				getLog().info(new StringBuffer("Found " + errorList.size() + " classes without Security Annotation"));
			}
			
			// Print Error List
			for (String error : errorList) {
				getLog().error(error);
			}
			if (!errorList.isEmpty()) {
				new MojoExecutionException("Found " + errorList.size() + " Errors in the Service Impl");
			}
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			new MojoExecutionException(e.getMessage(), e);
		}
	}

}
