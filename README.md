[![Build Status](https://travis-ci.org/bzacar/mongeez.svg?branch=master)](https://travis-ci.org/bzacar/mongeez)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.bzacar/mongeez.svg)](https://mvnrepository.com/artifact/com.github.bzacar/mongeez)
[![Known Vulnerabilities](https://snyk.io/test/github/bzacar/mongeez/badge.svg)](https://snyk.io/test/github/bzacar/mongeez)

### Note: This is a fork of [mongeez](https://github.com/mongeez/mongeez)

It uses a different maven groupId.

```xml
<dependency>
    <groupId>com.github.bzacar</groupId>
    <artifactId>mongeez</artifactId>
</dependency>
```

This fork has five basic differences from the original repository:
1. All MongoDB java drivers are updated and all deprecated API uses are removed.
1. **useUtil option**: A new option added to change sets to use util scripts. Util scripts are defined like any
other change set and in mongeez.xml files they should be defined with util tag (instead of file tag used for 
normal change sets). Example change set definition to use util script:

   XML:
   ```xml
   <changeSet changeId="ChangeSet-Using-Util" author="bzacar" useUtil="true"/>
   ``` 
   JS:
   ```javascript
   //changeset bzacar:ChangeSet-Using-Util useUtil:true
   ```
   Example mongeez.xml which defines util scripts:
   ```xml
   <changeFiles>
     <file path="changeset_using_util.xml"/>
     <util path="util.xml"/>
   </changeFiles>
   ```
   Change sets which are flagged with useUtil option will be run together with util script. Currently you can only 
   define one util script change set.
1. **useMongoShell option:** This option can be used in order to force the library to use mongo shell to run the
change sets instead of using ```db.eval``` so that you don't need to create a special user in the database to execute
```db.eval``` commands. The drawback of this option is that it requires mongo shell to be install on the machine
that will run the change sets. The option can be activated as ```mongeez.setUseMongoShell(true)```.
1. **Dry Run option:** You can now execute a dry run with mongeez. To use dry run you need to call
```mongeez.dryRun()``` instead of ```mongeez.process()```. This new methods returns a nullable string and list of
strings pair. Nullable string part contains summary information about the last change set run on the database. If
there is no change set run on the database yet that string will be null. The list contains summary information about
the change sets that would have been run on the database if that was not a dry run. All summary information are in
the format: ```<author of the change set>:<id if the change set>:<if available resource path of the file otherwise file name>```
1. **Command line interface tool**: This is explain in following sections, [CLI](#Command-Line-Interface-Tool)

### What is mongeez?

mongeez allows you to manage changes of your mongo documents and propagate these changes in sync with your code changes when you perform deployments.

For further information and usage guidelines check out [the wiki](https://github.com/mongeez/mongeez/wiki/How-to-use-mongeez).

###  Join the user group
http://groups.google.com/group/mongeez-users

### Become a contributor
http://groups.google.com/group/mongeez-dev


### Add mongeez to your project
```xml
<dependency>
    <groupId>org.mongeez</groupId>
	<artifactId>mongeez</artifactId>
	<version>0.9.6</version>
</dependency>
```

Maven repo for releases - http://repo1.maven.org/maven2

Internal versions - https://oss.sonatype.org/content/groups/public


### Or download mongeez from
repo1.maven.org - http://repo1.maven.org/maven2/org/mongeez/mongeez

### Travis Continuous Integration Build Status

Hopefully this thing is routinely green. Travis-CI monitors new code to this project and tests it on a variety of JDKs.



### Command Line Interface Tool
This fork also includes a command line interface tool which uses mongeez to execute change sets on the database.
You can download the tarball from maven repository with this
[link](https://oss.sonatype.org/content/repositories/releases/com/github/bzacar/mongeez-cli/0.9.8-bzacar/mongeez-cli-0.9.8-bzacar-dist.tar.gz).

You can also add it as a dependency to your projects:
```xml
<dependency>
    <groupId>com.github.bzacar</groupId>
    <artifactId>mongeez-cli</artifactId>
    <type>tar.gz</type>
    <classifier>dist</classifier>
    <exclusions>
        <exclusion>
            <groupId>com.github.bzacar</groupId>
            <artifactId>mongeez</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

The tarball contains two files:
* Jar file of the tool
* Shell script to run the tool

You can run the tool with help option to see what parameters you can provide and what are the default values of
those parameters:
```bash
./mongeez-cli.sh --help
```

## License
Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
