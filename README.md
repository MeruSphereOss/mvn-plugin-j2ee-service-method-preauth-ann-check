# Project name :: spring-service-ann-autho-check
A Maven Plugin for J2EE Spring Service Layer Pre-Authorize Annotation check

# Usage in your project
Add the following Plugin to the POM.xml of your project

```
			<plugin>
				<groupId>com.merusphere.devops</groupId>
				<artifactId>mvnplugin.j2ee-srv-method-ann-check</artifactId>
				<version>0.9.1</version>
				<executions>
					<execution>
						<id>compile</id>
						<goals>
							<goal>compile</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<ignoreClassList>
						<arg>classname1</arg>
						<arg>classname2</arg>
					</ignoreClassList>
					<ignoreAnnotation>annotation class name</ignoreAnnotation>
					<pkg>com.your.project</pkg>
				</configuration>
			</plugin>
```

## Configuration Parameters
ignoreClassList - Not Mandatory - Ignore the list of classes from the configuration parameter named ignoreClassList
ignoreAnnotation - Not Mandatory - Ignore the list of classes/methods having the annotation from the configuration parameter named ignoreAnnotation
pkg - Mandatory - Scan the classes from the Package name from the configuration parameter named pkg


## How to run this plugin in your projects

```
mvn com.merusphere.devops:mvnplugin.j2ee-srv-method-ann-check:0.9.1:j2ee-srv-method-ann-check
```

## Output of this Plugin
1. It will print the list of Classes & Methods not having Required Annotation

