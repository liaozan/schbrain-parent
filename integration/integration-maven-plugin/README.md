### Summary

> this is a maven plugin that simplify container deployment. it will generate `kubernetes-deploy.yaml`、`Dockerfile` according to your configuration

### Requirement

- your project should be a Spring Boot project
- your project will running in container

### How to use

**First** add `integration-maven-plugin`  in the `pom.xml` , it must be added under the  `spring-boot-maven-plugin` . like this

```.xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>com.schbrain.maven.plugin</groupId>
                <artifactId>integration-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

**Second** specify build-script repository. 

```.xml
<configuration>
  <gitRepository>git@gitlab.xxx.com:tools/build-script.git</gitRepository>
  <branch>main</branch>
</configuration>
```

---

The completion config like this:

```.xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>com.schbrain.maven.plugin</groupId>
                <artifactId>integration-maven-plugin</artifactId>
                <configuration>
                    <gitRepository>git@gitlab.xxx.com:tools/build-script.git</gitRepository>
                    <branch>main</branch>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
