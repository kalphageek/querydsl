### 1. Querydsl 설정
#### 1.1 pom.xml
```xml
<project>
	<properties>
		<querydsl.version>5.0.0</querydsl.version>
	</properties>

    <dependencies>
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-apt</artifactId>
            <version>${querydsl.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-jpa</artifactId>
            <version>${querydsl.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>com.mysema.maven</groupId>
                <artifactId>apt-maven-plugin</artifactId>
                <version>1.1.3</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>process</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>target/generated-sources</outputDirectory>
                            <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>add-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                                <source>target/generated-sources</source>
                            </sources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-clean-plugin</artifactId>
                <version>${maven-clean-plugin.version}</version>
                <configuration>
                    <filesets>
                        <fileset>
                            <directory>target/generated-sources/annotations</directory>
                            <includes>
                                <include>**</include>
                            </includes>
                        </fileset>
                    </filesets>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
#### 1.2 Source Generate
> compile하면 target/generated-sources 아래에 package/Q[Entity] 코드가 생성된다.
#### * JPA의 Prepared SQL 파라미터 Bindiong 값 보기
> pom.xml에 p6spy Dependency 추가. 운영시에는 가급적 제거
```xml
		<dependency>
			<groupId>com.github.gavlyukovskiy</groupId>
			<artifactId>p6spy-spring-boot-starter</artifactId>
			<version>1.8.1</version>
		</dependency>
```
### 2. H2 테스트DB 설정
#### 2.1 pom.xml에 H2 Dependency 추가
```xml
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
```
#### 2.2 application-test.yml 설정
> 메모리 모드<br>위치 : test/resources/application-test.yml
```yaml
spring:
  datasource:
    username: sa
    password:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver

logging:
  level:
    org:
      hibernate:
        SQL: DEBUG
        type.descriptor.sql.BasicBinder: TRACE
```
#### 2.3 test코드에 application-test.yml 사용 설정
```java
@ActiveProfiles("test")
// Test의 @Transactional은 모두 Rollback하게 한다. @Commit을 이용할 수 있다.
@Transactional
class HelloTest {
}
```

### 3. H2 운영DB 설정
> 파일 모드
#### 1.1 H2 Download
> https://h2database.com/html/main.html
#### 1.2 파일생성 및 H2 실행
```bash
$ cd ~/workspace/h2/
$ mkdir data
$ touch data/querydsl.mv.db
$ chmod 755 bin/h2.sh
$ bin/h2.sh # Web Browser 자동실행
```
#### 2.3 file/TCP모드 접속
> 처음 개발할 때는 Test 결과확인을 위해 file/TCP 모드를 사용하고, 운영중 Test는 Memory 모드를 사용한다
* 저장한 설정 : Generic H2 (Embedded)
* JDBC URL : jdbc:h2:tcp://localhost/~/workspace/h2/data/querydsl
#### 2.4 스키마 / 데이터 자동생성
> resources 아래에 schema.sql, data.sql 를 생성해 넣는다.
