# Project name :: spring-service-ann-autho-check
A Maven Plugin for J2EE Spring Service Layer Pre-Authorize Annotation check

# Usage in your project
Add the following Plugin to the POM.xml of your project

```
	<plugin>
		<groupId>com.merusphere.devops</groupId>
		<artifactId>mvnplugin.j2ee-srv-method-ann-check</artifactId>
		<version>0.9.2</version>
		<executions>
			<execution>
				<phase>compile</phase>
				<goals>
					<goal>j2ee-srv-method-ann-check</goal>
				</goals>
			</execution>
		</executions>
		<configuration>
			<pkg>Your project base package</pkg>
		</configuration>
	</plugin>
```

## Configuration Parameters
ignoreClassList - Not Mandatory - Ignore the list of classes from the configuration parameter named ignoreClassList
ignoreAnnotation - Not Mandatory - Ignore the list of classes/methods having the annotation from the configuration parameter named ignoreAnnotation
pkg - Mandatory - Scan the classes from the Package name from the configuration parameter named pkg

## System Parameters
skip-j2ee-srv-method-ann-check - If we pass on this System parameter with value 'true' then this Plugin's goal(s) will be skipped

## How to run this plugin in your projects

Note : This PlugIn runs as part of the Maven Phase: compile
If you want to fire it, use the Goal : j2ee-srv-method-ann-check

```
mvn clean compile
or
mvn clean install
```

## Output of this Plugin
1. It will print the list of Classes & Methods not having Required Annotation

