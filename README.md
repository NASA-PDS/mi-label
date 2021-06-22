# Metadata Injector for PDS Labels (MILabel)
Software tool (formerly known as "Generate Tool") that provides a command-line interface for generating PDS4 Labels using a user provided PDS4 XML template and input (source) data products to parse the metadata from the source and inject into the output PDS4 labels. The XML-like Templates leveraged Apache Velocity variables and logic to enable the programmatic generation of the labels.

# Documentation
The documentation for the latest release of MILabel, including release notes, installation and operation of the software are online at https://nasa-pds-incubator.github.io/mi-label/.

If you would like to get the latest documentation, including any updates since the last release, you can execute the "mvn site:run" command and view the documentation locally at http://localhost:8080.

# Build
The software can be compiled and built with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn compile jar:jar" command. 

In order to create a complete distribution package, execute the 
following commands: 

```
% mvn site
% mvn package
```

# Operational Release

A release candidate should be created after the community has determined that a release should occur. These steps should be followed when generating a release candidate and when completing the release.

## Clone fresh repo
```
git clone git@github.com:NASA-PDS-Incubator/mi-label.git
```

## Release with ConMan

For internal JPL use, the ConMan software package can be used for releasing software, otherwise the following sections outline how to complete these steps manually.

## Manual Release

### Update Version Numbers

Update pom.xml for the release version or use the Maven Versions Plugin, e.g.:

```
# Skip this step if this is a RELEASE CANDIDATE, we will deploy as SNAPSHOT version for testing
VERSION=1.15.0
mvn versions:set -DnewVersion=$VERSION
git add pom.xml
```

### Update Changelog
Update Changelog using [Github Changelog Generator](https://github.com/github-changelog-generator/github-changelog-generator). Note: Make sure you set `$CHANGELOG_GITHUB_TOKEN` in your `.bash_profile` or use the `--token` flag.
```
# For RELEASE CANDIDATE, set VERSION to future release version.
github_changelog_generator --future-release v$VERSION

git add CHANGELOG.md
```

### Commit Changes
Commit changes using following template commit message:
```
# For operational release
git commit -m "[RELEASE] mi-label v$VERSION"

# For release candidate
CANDIDATE_NUM=1
git commit -m "[RELEASE] mi-label v${VERSION}-rc${CANDIDATE_NUM}"

# Push changes to main
git push -u origin main
```

### Build and Deploy Software to [Sonatype Maven Repo](https://repo.maven.apache.org/maven2/gov/nasa/pds/).

```
# For operational release
mvn clean site deploy -P release

# For release candidate
mvn clean site deploy
```

Note: If you have issues with GPG, be sure to make sure you've created your GPG key, sent to server, and have the following in your `~/.m2/settings.xml`:
```
<profiles>
  <profile>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <gpg.executable>gpg</gpg.executable>
      <gpg.keyname>KEY_NAME</gpg.keyname>
      <gpg.passphrase>KEY_PASSPHRASE</gpg.passphrase>
    </properties>
  </profile>
</profiles>

```

### Push Tagged Release
```
# For Release Candidate, you may need to delete old SNAPSHOT tag
git push origin :v$VERSION

# Now tag and push
git tag v${VERSION}
git push --tags

```

### Deploy Site to Github Pages

From cloned repo:
```
git checkout gh-pages

# Create specific version site
mkdir -p $VERSION

# Copy the over to version-specific and default sites
rsync -av target/site/ $VERSION
rsync -av $VERSION/* .

git add .

# For operational release
git commit -m "Deploy v$VERSION docs"

# For release candidate
git commit -m "Deploy ${VERSION}-rc${CANDIDATE_NUM} docs"

git push origin gh-pages
```

### Update Versions For Development

Update `pom.xml` with the next SNAPSHOT version either manually or using Github Versions Plugin.

For RELEASE CANDIDATE, ignore this step.

```
git checkout main

# For release candidates, skip to push changes to main
VERSION=1.16.0-SNAPSHOT
mvn versions:set -DnewVersion=$VERSION
git add pom.xml
git commit -m "Update version for $VERSION development"

# Push changes to main
git push -u origin main
```

### Complete Release in Github
Currently the process to create more formal release notes and attach Assets is done manually through the [Github UI](https://github.com/NASA-PDS-Incubator/mi-label/releases/new) but should eventually be automated via script.

*NOTE: Be sure to add the `tar.gz` and `zip` from the `target/` directory to the release assets, and use the CHANGELOG generated above to create the RELEASE NOTES.*

# Snapshot Release

Deploy software to Sonatype SNAPSHOTS Maven repo:

```
# Operational release
mvn clean site deploy
```

# Maven JAR Dependency Reference

## Operational Releases
https://search.maven.org/search?q=g:gov.nasa.pds%20AND%20a:mi-label&core=gav

## Snapshots
https://oss.sonatype.org/content/repositories/snapshots/gov/nasa/pds/mi-label/

If you want to access snapshots, add the following to your `~/.m2/settings.xml`:
```
<profiles>
  <profile>
     <id>allow-snapshots</id>
     <activation><activeByDefault>true</activeByDefault></activation>
     <repositories>
       <repository>
         <id>snapshots-repo</id>
         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
         <releases><enabled>false</enabled></releases>
         <snapshots><enabled>true</enabled></snapshots>
       </repository>
     </repositories>
   </profile>
</profiles>
```
