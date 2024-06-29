# Maven settings
## Create the following file if not exists and put the following content
File name : ~/.m2/settings.xml
Content

```
<settings>
	<profiles>
		<profile>
			<id>ossrh</id>
			<activation>
				<activeByDefault>true</activeByDefault>
			</activation>
			<properties>
				<gpg.keyname>AA11076961ACC367FB8F1BDB1BC2F43DE7F66EEC</gpg.keyname>
				<gpg.passphrase>gpg@Rsa1</gpg.passphrase>
			</properties>
		</profile>
	</profiles>

	<servers>
		<server>
			<id>${server}</id>
			<username>1UOiBHbd</username>
			<password>7vVsB/9h+Y2VVhLwfkhkiphAsHGJFeZdwxImHmI+tv5h</password>
		</server>
	</servers>
</settings>
```