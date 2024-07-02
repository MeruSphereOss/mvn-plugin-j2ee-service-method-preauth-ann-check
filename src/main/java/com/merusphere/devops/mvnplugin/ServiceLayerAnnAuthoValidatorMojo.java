package com.merusphere.devops.mvnplugin;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
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
import org.springframework.util.FileSystemUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Checks the methods not having Security Annotations. Scan the classes from
 * given Package name from the configuration parameter named 'pkg'. Ignore the
 * list of classes from the scanning configuration parameter named
 * 'ignoreClassList'. Ignore the list of classes/methods having the annotation
 * from the configuration parameter named ignoreAnnotation
 *
 */
@Mojo(name = "j2ee-srv-method-ann-check", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
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
	 * Gives access to the Maven project session.
	 */
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	private MavenSession session;

	/**
	 * HTML Report Format
	 */
	private String htmlTmpl = "<!DOCTYPE html>\n"
			+ "<html lang='en'>\n"
			+ "  <head>\n"
			+ "    <link\n"
			+ "      href='https://unpkg.com/gridjs/dist/theme/mermaid.min.css'\n"
			+ "      rel='stylesheet'\n"
			+ "    />\n"
			+ "  </head>\n"
			+ "  <body>\n"
			+ "    <h1>Report j2ee-srv-method-ann-check</h1>\n"
			+ "    <h3>Missing Annotation Report</h3>\n"
			+ "    <h3>Date : @DATE@</h3>\n"
			+ "    <div id='wrapper'></div>\n"
			+ "\n"
			+ "    <script src='https://unpkg.com/gridjs/dist/gridjs.umd.js'></script>\n"
			+ "    <script>\n"
			+ "      new gridjs.Grid({\n"
			+ "        columns: ['Class', 'Method'],\n"
			+ "        sort: true,\n"
			+ "        search: true,\n"
			+ "        data: @REPORT@\n"
			+ "      }).render(document.getElementById('wrapper'));\n"
			+ "    </script>\n"
			+ "  </body>\n"
			+ "</html>";

	/**
	 * Checks the number of methods not having Security Annotations
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if ("true".equals(System.getProperty("skip-j2ee-srv-method-ann-check"))) {
				getLog().info("Skipping PlugIn goal j2ee-srv-method-ann-check");
				return;
			}

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
			List<String> compiletimeElements = project.getCompileClasspathElements();
			for (String element : compiletimeElements) {
				urls.add(new File(element).toURI().toURL());
			}
			List<String> systemtimeElements = project.getSystemClasspathElements();
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
			ClassLoader contextClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]),
					Thread.currentThread().getContextClassLoader());

			Thread.currentThread().setContextClassLoader(contextClassLoader);

			// Load the Classes from the given Package name
			Reflections reflections = new Reflections(pkg);
			Set<Class<?>> srvClazzSet = reflections.getTypesAnnotatedWith(org.springframework.stereotype.Service.class,
					true);
			if (getLog().isInfoEnabled()) {
				getLog().info("Number of Classes found with @Service : " + srvClazzSet.size());
			}

			Set<Class<?>> txnClazzSet = reflections
					.getTypesAnnotatedWith(org.springframework.transaction.annotation.Transactional.class, true);
			if (getLog().isInfoEnabled()) {
				getLog().info("Number of Classes found with @Transactional : " + txnClazzSet.size());
			}

			// Union of both the sets
			srvClazzSet.retainAll(txnClazzSet);
			if (getLog().isInfoEnabled()) {
				getLog().info("Number of Classes found after Filter : " + srvClazzSet.size());
			}

			if (srvClazzSet.isEmpty()) {
				if (getLog().isInfoEnabled()) {
					getLog().info("No ServiceImpl classes Found in this project");
				}
				return;
			} else {
				if (getLog().isInfoEnabled()) {
					getLog().info("Relection Found " + srvClazzSet.size() + " Classses");
					for (Class<?> clazz : srvClazzSet) {
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
			List<ClazzMethod> clazzMethodList = new ArrayList<>();
			for (Class<?> clazz : srvClazzSet) {
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
						clazzMethodList.add(new ClazzMethod(clazz.getCanonicalName(), method.getName()));
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

			// Generate Report
			String classifiedReportDir = session.getExecutionRootDirectory() + "/target/j2ee-srv-method-ann-check";
			getLog().info(new StringBuffer("Generating Report at " + classifiedReportDir));

			if (classifiedReportDir != null && classifiedReportDir.length() > 0) {
				File reportDirFile = new File(classifiedReportDir);
				if (reportDirFile.exists()) {
					FileSystemUtils.deleteRecursively(new File(classifiedReportDir));
				}

				Path reportPath = Files
						.createDirectories(Paths.get(project.getBuild().getDirectory()).resolve(classifiedReportDir));

				ObjectMapper jsonApi = new ObjectMapper();
				byte[] clazzMethodListBa = jsonApi.writeValueAsBytes(clazzMethodList);

				// JSON Report
				File jsonFile = new File(classifiedReportDir + "/MissingAnotationEntityList.json");
				FileOutputStream fout = new FileOutputStream(jsonFile);
				fout.write(clazzMethodListBa);
				fout.close();
				getLog().info(new StringBuffer("Generated  Report at " + jsonFile.getAbsolutePath()));

				// HTML Report
				// Ref https://gridjs.io/docs/config
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for (ClazzMethod cm : clazzMethodList) {
					sb.append("[").append("'").append(cm.getClazz()).append("'").append(" , ").append("'")
							.append(cm.getMethod()).append("'").append("]").append(" , ");
				}
				sb.append("]");

				htmlTmpl = htmlTmpl.replace("@REPORT@", sb.toString());
				htmlTmpl = htmlTmpl.replace("@DATE@", (new Date()).toString());
				File htmlFile = new File(classifiedReportDir + "/MissingAnotationEntityList.html");
				FileOutputStream fout2 = new FileOutputStream(htmlFile);
				fout2.write(htmlTmpl.getBytes());
				fout2.close();
				getLog().info(new StringBuffer("Generated  Report at " + htmlFile.getAbsolutePath()));

				System.exit(1);
			}
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			new MojoExecutionException(e.getMessage(), e);
		}
	}
}
