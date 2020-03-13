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
