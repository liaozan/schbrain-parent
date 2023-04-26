### How to develop

* add the following content to your maven settings.xml
* active jenkins profile on your ide

```xml
    <profile>
      <id>jenkins</id>
      <repositories>
        <repository>
          <id>repo.jenkins-ci.org</id>
          <url>https://repo.jenkins-ci.org/public/</url>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>repo.jenkins-ci.org</id>
          <url>https://repo.jenkins-ci.org/public/</url>
        </pluginRepository>
      </pluginRepositories>
    </profile>
 ```

### How to build

run `mvn clean package -Dmaven.test.skip` in terminal, plugin will be stored in the target directory named `integration-jenkins-plugin.hpi`, upload it to your jenkins server 
