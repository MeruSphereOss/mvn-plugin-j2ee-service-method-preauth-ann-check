
# Project name :: spring-service-ann-autho-check
To identify the list of J2EE Spring Service Layer methods not having Pre-Authorise Annotation. This can be used in the CI Pipeline for better quality and security.

On a high level there are 5 key steps involved in getting your open source code published. Assuming you already have your JAVA and Maven based code setup on Github.

## Prerequisite Steps
* Step 0: Maven settings [help](https://www.baeldung.com/maven-settings-xml)
* Step 1: Make sure if you have installed JDK, Maven, Github, etc.
* Step 2: Create a Github account if you haven't already.
* Step 3: Create a new Github repository.
* Step 4: Add a new SSH key to your Github account

```
1. Open Terminal.
2. Paste the text below, substituting in your GitHub email address.
# ssh-keygen -t ed25519 -C "your_email@example.com"
```
* Step 5: Push the code to Github. 
* Step 6: Sign up for a Sonatype Jira account.
* Step 7: Create a [new Jira issue](https://issues.sonatype.org/secure/CreateIssue.jspa?issuetype=21&pid=10134) for new project hosting. [Click here](https://issues.sonatype.org/browse/OSSRH-24465) for a sample request. 
* Step 8:Install [GNU PG](https://www.gnupg.org/download/). Install in your OS and verify as follows:

```
C:\Users\Nadeem>gpg --version
gpg (GnuPG) 2.1.15
libgcrypt 1.7.3
Copyright (C) 2016 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <https://gnu.org/licenses/gpl.html>
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

Home: C:/Users/Nadeem/AppData/Roaming/gnupg
Supported algorithms:
Pubkey: RSA, ELG, DSA, ECDH, ECDSA, EDDSA
Cipher: IDEA, 3DES, CAST5, BLOWFISH, AES, AES192, AES256, TWOFISH,
        CAMELLIA128, CAMELLIA192, CAMELLIA256
Hash: SHA1, RIPEMD160, SHA256, SHA384, SHA512, SHA224
Compression: Uncompressed, ZIP, ZLIB, BZIP2

C:\Users\Nadeem>
```
* Step 9: Generate the key pair.

## Step to deploy the artifact on the Maven Central
1. Request for hosting your code on Sonatype.
1. Generating and publishing PGP keys.
1. Setting up Maven Configuration.
1. Setting up POM xml for the project.
1. Deploying the artifact.

## Request for hosting code on Sonatype
You will need to raise a ticket on Sonatype JIRA for new Project hosting. You will first need to sign up [here](https://issues.sonatype.org/secure/Signup!default.jspa)

Please create a JIRA ID with the following Details

```
Subject : Need a Maven Plugin to find out the number of Methods in the J2EE Spring Service Layer not having Security Annotations in the source code
Description : Need a Maven Plugin to find out the number of Methods in the J2EE Spring Service Layer not having Security Annotations
Need a Maven Plugin to find out the number of Methods in the J2EE Spring Service Layer not having Security Annotations.
If any method in the Spring Service Layer not having Security Annotation then it will be a vulnerable one and lead to security loop wholes.
Is it better find out such methods and inform the developer as part of the CI 
This small plugin will enable to find out the number of Methods in the J2EE Spring Service Layer not having Security Annotations.
Group Id : com.shadkona.oss.ci
SCM URL : https://github.com/Shadkona-OSS/spring-service-ann-autho-check.git
Project URL : https://github.com/Shadkona-OSS/spring-service-ann-autho-check.git
```

Created JIRA ID https://issues.sonatype.org/browse/OSSRH-76835
[LINK](https://issues.sonatype.org/browse/OSSRH-76835)

```
To register this Group Id you must prove ownership of the domain shadkona.com adding a TXT record to your DNS with the text: OSSRH-<JIRAID>
in case create a TXT record with value OSSRH-76835
```

## Generating and Publishing PGP keys

PGP(Pretty Good Privacy) keys are used to verify authenticity of a digital artifact(in this case your packaged code) using digitally encrypted signatures. We will use this to digitally sign our code as we upload to Sonatype repositories.

Make sure you have the gpg CLI available on your machine. Once you have it installed. You can use the command gpg --full-gen-key command to generate the key. As a part of the options presented in the terminal.

1. Use RSA for the kind of key
1. Use the default key size
1. Use the option where the key never expires
1. Then confirm your choices by typing Y and hitting ENTER.
1. Enter Phassphrase in this case it is 'Shadkona-OSS'

```
pcv@HYDMAC025 spring-service-ann-autho-check % gpg --full-gen-ke
gpg (GnuPG/MacGPG2) 2.2.32; Copyright (C) 2021 Free Software Foundation, Inc.
This is free software: you are free to change and redistribute it.
There is NO WARRANTY, to the extent permitted by law.

Please select what kind of key you want:
   (1) RSA and RSA (default)
   (2) DSA and Elgamal
   (3) DSA (sign only)
   (4) RSA (sign only)
  (14) Existing key from card
Your selection? 1
RSA keys may be between 1024 and 4096 bits long.
What keysize do you want? (3072) 
Requested keysize is 3072 bits
Please specify how long the key should be valid.
         0 = key does not expire
      <n>  = key expires in n days
      <n>w = key expires in n weeks
      <n>m = key expires in n months
      <n>y = key expires in n years
Key is valid for? (0) 
Key does not expire at all
Is this correct? (y/N) y

GnuPG needs to construct a user ID to identify your key.

Real name: Shadkona-OSS
Email address: corporate@shadkona.com
Comment: Shadkona-OSS
You selected this USER-ID:
    "Shadkona-OSS (Shadkona-OSS) <corporate@shadkona.com>"

Change (N)ame, (C)omment, (E)mail or (O)kay/(Q)uit? O
We need to generate a lot of random bytes. It is a good idea to perform
some other action (type on the keyboard, move the mouse, utilize the
disks) during the prime generation; this gives the random number
generator a better chance to gain enough entropy.
We need to generate a lot of random bytes. It is a good idea to perform
some other action (type on the keyboard, move the mouse, utilize the
disks) during the prime generation; this gives the random number
generator a better chance to gain enough entropy.
gpg: key 559FE948461241F8 marked as ultimately trusted
gpg: revocation certificate stored as '/Users/pcv/.gnupg/openpgp-revocs.d/839C4A82B068E031570C5452559FE948461241F8.rev'
public and secret key created and signed.

pub   rsa3072 2022-01-02 [SC]
      839C4A82B068E031570C5452559FE948461241F8
uid                      Shadkona-OSS (Shadkona-OSS) <corporate@shadkona.com>
sub   rsa3072 2022-01-02 [E]

pcv@HYDMAC025 spring-service-ann-autho-check % 
```

You will then need to create a file gpg.conf to add an entry for the default key server. This will make sure that the generated key is uploaded to one of the public key servers.

Below is the entry you will need to make for the gpg.conf at the gpg installation path(~/.gnupg/gpg.conf)

```
keyserver keys.openpgp.org
```

I used keys.openpgp.org as the key server, you can use any other public key server as you wish. Below are some options.

+ pgp.mit.edu
+ subkeys.pgp.net
+ pgp.surfnet.nl
+ pgp.uni-mainz.de
+ pgp.rediris.es
+ keyserver.linux.it

Now you need to publish your key to the key server. Use the command below with the key ID that was generated for the key earlier.

```
pcv@HYDMAC025 spring-service-ann-autho-check % gpg --send-key 839C4A82B068E031570C5452559FE948461241F8
gpg: sending key 559FE948461241F8 to hkp://keys.openpgp.org
pcv@HYDMAC025 spring-service-ann-autho-check % 
```

Now, that you have uploaded the key to the key server. Ideally, you should receive an email of the upload confirmation from the public key server.

An email with the following subject received

```
Verify corporate@shadkona.com for your key on keys.openpgp.org
```

This email contain a link to Verify the email, please follow the verification procedure. The Final verification messge should be like the following

```
keys.openpgp.org
You uploaded the key 839C4A82B068E031570C5452559FE948461241F8.

This key is now published with only non-identity information. (What does this mean?)

To make the key available for search by email address, you can verify it belongs to you:

Verification Pending
corporate@shadkona.com

Note: Some providers delay emails for up to 15 minutes to prevent spam. Please be patient.
```

The gpg tool has more useful commands. Adding a link for the command reference [here](https://gock.net/blog/2020/gpg-cheat-sheet)

## Setting up Maven Configuration
### Step 1: Add a distributed management section to your POM.

Now, let's focus on setting up Maven config for us to use Maven plugins to build, sign and release artifacts to Sonatype repository.

Please add the below profile and server specs in the settings.xml in the Maven PATH - ~/.m2/settings.xml 

```
<settings xmlns="https://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://maven.apache.org/SETTINGS/1.0.0
                      https://maven.apache.org/xsd/settings-1.0.0.xsd">
<profiles>
<profile>
<id>ossrh</id>
<activation>
<activeByDefault>true</activeByDefault>
</activation>
<properties>
<gpg.keyname>839C4A82B068E031570C5452559FE948461241F8</gpg.keyname>
<gpg.passphrase>Shadkona-OSS</gpg.passphrase>
</properties>
</profile>
</profiles>

<servers>
  <server>
    <id>ossrh</id>
    <username>devops-shadkona</username>
    <password>1_Qossrepo4plugin</password>
   </server>
  </servers>
</settings>

```

### Setting up POM XML

We are now going to configure the POM XML file on your Maven project. These steps are also outlined on Sonatype documentation, here is the [link](https://central.sonatype.org/publish/publish-maven/#nexus-staging-maven-plugin-for-deployment-and-release)

#### First add the distribution management and nexus repo plugin for your project.

```
<distributionManagement>
  <snapshotRepository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
  </snapshotRepository>
  <repository>
    <id>ossrh</id>
    <url>https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/</url>
  </repository>
</distributionManagement>
<build>
  <plugins>
    <plugin>
      <groupId>org.sonatype.plugins</groupId>
      <artifactId>nexus-staging-maven-plugin</artifactId>
      <version>1.6.7</version>
      <extensions>true</extensions>
      <configuration>
        <serverId>ossrh</serverId>
        <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
        <autoReleaseAfterClose>true</autoReleaseAfterClose>
      </configuration>
    </plugin>
    ...
  </plugins>
</build>
```

Add source, javadoc and gpg artifact signing plugins.

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-source-plugin</artifactId>
    <version>2.2.1</version>
    <executions>
        <execution>
            <id>attach-sources</id>
            <goals>
                <goal>jar-no-fork</goal>
            </goals>
        </execution>
    </executions>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>2.9.1</version>
    <executions>
        <execution>
            <id>attach-javadocs</id>
            <goals>
                <goal>jar</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-gpg-plugin</artifactId>
    <version>3.0.1</version>
    <executions>
        <execution>
            <id>sign-artifacts</id>
            <phase>verify</phase>
            <goals>
                <goal>sign</goal>
            </goals>
            <configuration>
                <keyname>${gpg.keyname}</keyname>
                <passphraseServerId>${gpg.passphrase}</passphraseServerId>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Then add the SCM details

```
<scm>
	<connection>scm:git:https://github.com/Shadkona-OSS/spring-service-ann-autho-check.git</connection>
	<url>https://github.com/Shadkona-OSS/spring-service-ann-autho-check</url>
	<developerConnection>scm:git:https://github.com/Shadkona-OSS/spring-service-ann-autho-check</developerConnection>
	<tag>HEAD</tag>
</scm>
```

At last, do not forget to add additional metadata for the project.

```
<description>
    Need a Maven Plugin to find out the number of Methods in the J2EE Spring Service Layer not having Security Annotations in the source code
	</description>

	<licenses>
		<license>
			<name>MIT License</name>
			<url>https://github.com/kgsnipes/unum/blob/main/LICENSE</url>
		</license>
	</licenses>

	<developers>
		<developer>
			<name>P C Varma</name>
			<email>pcvarma@gmail.com</email>
			<roles>
				<role>owner</role>
			</roles>
		</developer>
	</developers>
```
You are now done with the POM XML. Let's now move to the final step.


### Deploying the artifact

This step is the easiest one.
There are basically two versions of your artifacts - a staged version and a release version.
To deploy to the staged version use the below Maven goal.


To release the artifact to Maven Central Repo

```
Makesure the functionality and commit all the code changes with a Release version
mvn clean deploy
Create a Tag in the Git with a Release version
mvn release:clean
mvn versions:set -DnewVersion=<New Development Version>
Then commit the git for New Development Version
```
Then check the Artifacts after 4 to 12 hrs at [Maven Cental](https://s01.oss.sonatype.org/content/repositories/snapshots/com/shadkona/oss/ci/spring-service-ann-autho-check/)


That's It! This ends the process.

If everything goes well, your artifact should be listed in a matter of an hour in the Sonatype repository - https://search.maven.org/. It might take up-to 4-8 hours to reflect in other maven repositories.

I know! It's a long process. For now, there is no easier way around. I wish there was an easier way to do this. If anybody has found a better way, please feel free to share your inputs in the comments section.


## How to run this plugin used in other projects

```
mvn com.shadkona.oss.ci:spring-service-ann-autho-check:1.4.7-SNAPSHOT:svc-layer-validate
```

