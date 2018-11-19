# Mongeez #

### Version 0.9.8-bzacar ###
* Upgraded vulnerable dependencies

### Version 0.9.7-bzacar ###
* All MongoDB java drivers are updated and all deprecated API uses are removed.
* **useUtil option**: A new option added to change sets to use util scripts. Util scripts are defined like any
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
* **useMongoShell option:** This option can be used in order to force the library to use mongo shell to run the
change sets instead of using ```db.eval``` so that you don't need to create a special user in the database to execute
```db.eval``` commands. The drawback of this option is that it requires mongo shell to be install on the machine
that will run the change sets. The option can be activated as ```mongeez.setUseMongoShell(true)```.
* **Dry Run option:** You can now execute a dry run with mongeez. To use dry run you need to call
```mongeez.dryRun()``` instead of ```mongeez.process()```. This new methods returns a nullable string and list of
strings pair. Nullable string part contains summary information about the last change set run on the database. If
there is no change set run on the database yet that string will be null. The list contains summary information about
the change sets that would have been run on the database if that was not a dry run. All summary information are in
the format: ```<author of the change set>:<id if the change set>:<if available resource path of the file otherwise file name>```
* **Command line interface tool**: This is explain in following sections, [CLI](./README.md#Command-Line-Interface-Tool)

### Version 0.9.6 ###
* added context parsing to formatted javascript changesets [#54](https://github.com/mongeez/mongeez/pull/54)

### Version 0.9.5 ###
* Use mongo 3.0
* Support authenticationDatabase
* Added ChangeSetValidator to detect duplicate ChangeSetIds [#43](https://github.com/mongeez/mongeez/pull/43)
* don't include log4j.properties in jar [#34](https://github.com/mongeez/mongeez/pull/34)
* Add context support [#33](https://github.com/mongeez/mongeez/pull/33)
* Fix NPE issues in xml file parsing [#32](https://github.com/mongeez/mongeez/pull/32)

### Version 0.9.4 ###
* pom: change scopes from provided to compile: commons-lang3, mongo-java-driver, spring-beans [#30](https://github.com/mongeez/mongeez/pull/30)
* add failOnError flag [#28](https://github.com/mongeez/mongeez/pull/28)
* upgrade mongo-java-driver to 2.11.1, slf4j to 1.7.5 and maven-compiler-plugin to 3.1

### Version 0.9.3 ###
* MongeezDao: add support for MongoDB 2.4 [#25](https://github.com/mongeez/mongeez/pull/25)
* migrate mongeez from log4j to slf4j
* pom: add davidmc24 as a developer [#20](https://github.com/mongeez/mongeez/pull/20)

### Version 0.9.2 ###
* make API change backwards compatible [#16](https://github.com/mongeez/mongeez/pull/16)
* Patch to add optional authentication to Mongeez runner [#14](https://github.com/mongeez/mongeez/pull/14)
* Javascript-formatted files, other new features [#12](https://github.com/mongeez/mongeez/pull/12)

### Version 0.9.1 ###
* mongeez library published to github
