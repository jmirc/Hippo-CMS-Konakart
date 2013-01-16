# News

2013-01-16: Just release the first enterprise version for the Hippo enterprise offer. This project will not be updated anymore. Please, fell free to contect me for more information.

# Hippo CMS - Konakart integration

The aim of this project is to create a bridge between Hippo CMS and Konakart.

## Before starting a project
Don't set to true subjectbasedsession, users are not saved in hippo.  


### Konakart installation 
Please install the [community](http://www.konakart.com/downloads/community_edition) version or the enterprise version.

Only community features are available.

### Define Environment Variables
In order to process with the next step, you need to define the KONAKART_HOME variable.

```
i.e.: KONAKART_HOME='C:\app\konakart\KonaKart-6.3.0.0\'
```

### Import Konakart libraries within your local .m2 repo
Konakart uses ant to build the entire project. The librairies are not available on any Maven repository. 
The project [Konakart Dependency](https://github.com/jmirc/Hippo-CMS-Konakart/tree/master/konakart-dependency) has been created to import into your local m2 repo the librairies.

The following steps need to be executed:

1. Clone the project
1. cd konakart-dependency
1. run mvn install

## How to start a project

*  Create a new project using the latest version of the artifact. Currently tested with the version 1.05.06

### Global POM.XML
* Add the following conf
```xml
    <properties>
      <mysql.version>5.1.18</mysql.version>
      <hippo-konakart.version>1.00.04</hippo-konakart.version>
    </properties>
    <dependencyManagement>
     ...
     <dependency>
     	<groupId>org.onehippo.forge.konakart</groupId>
        <artifactId>hippo-addon-konakart-cms</artifactId>
        <version>${hippo-konakart.version}</version>
     </dependency>
     <dependency>
        <groupId>org.onehippo.forge.konakart</groupId>
        <artifactId>hippo-addon-konakart-repository</artifactId>
        <version>${hippo-konakart.version}</version>
     </dependency>
     <dependency>
        <groupId>org.onehippo.forge.konakart</groupId>
        <artifactId>hippo-addon-konakart-hstclient</artifactId>
     	<version>${hippo-konakart.version}</version>
     </dependency> 
     <dependency>
         <groupId>mysql</groupId>
         <artifactId>mysql-connector-java</artifactId>
         <version>${mysql.version}</version>
     </dependency>
      ...
    </dependencyManagement>

    <profile>
       <id>cargo.run</id>
       <dependencies>
          <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <version>${mysql.version}</version>
          </dependency>
       </dependencies>
       <build>
         <plugins>
            <plugin>
            ...
            <configuration>
               <container>
                 <dependencies>
                      <dependency>
                         <groupId>mysql</groupId>
                         <artifactId>mysql-connector-java</artifactId>
                      </dependency>
                 </dependencies>
               ...
               </container>
    </profile>
```

### CMS Configuration
* Add a copy of the konakart.properties and the konakart_app.properties files under src/main/resources
* Add the following lines into the pom.xml file

```xml
       <dependency>
            <groupId>org.onehippo.forge.konakart</groupId>
            <artifactId>hippo-addon-konakart-cms</artifactId>
        </dependency>

        <dependency>
            <groupId>org.onehippo.forge.konakart</groupId>
            <artifactId>hippo-addon-konakart-repository</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
```

### SITE Configuration
* Add a copy of the konakart.properties, the konakart_app.properties and the konakart_velocity.properties files under src/main/resources
* Add the following lines into the pom.xml file

```xml

        <dependency>
            <groupId>org.onehippo.forge.konakart</groupId>
            <artifactId>hippo-addon-konakart-hstclient</artifactId>
        </dependency>
       <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
```
* Create a new file named //konakart-hst-configuration.xml// under resources/META-INF/hst-assembly/overrides to add the konakart Valve

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <import resource="classpath:/org/onehippo/forge/konakart/hst/konakart-hst-configuration.xml"/>
</beans>
```

* Add in the web.xml of your site the following value to context parameter hst-beans-annotated-classes (note that the values are comma separated):

```xml
  <context-param>
    <param-name>hst-beans-annotated-classes</param-name>
    <param-value>
        classpath*:org/onehippo/forge/konakart/hst/beans/**/*.class
    </param-value>
  </context-param>
```

### Database Configuration
* Add the following database configuration to the context.xml file. You will add the connection to the Konakart DB previously created.

**You need to change the username, password as well as the database name.**

```xml

    <!-- Hippo Konakart configuration-->
    <Resource
            name="jdbc/konakart" auth="Container" type="javax.sql.DataSource"
            maxActive="20" maxIdle="10" minIdle="2" initialSize="2" maxWait="10000"
            testOnBorrow="true" validationQuery="select 1 from dual"
            poolPreparedStatements="true"
            username="TBD" password="TBD"
            driverClassName="com.mysql.jdbc.Driver"
            url="jdbc:mysql://localhost:3306/TBD?zeroDateTimeBehavior=convertToNull&amp;autoReconnect=true&amp;characterEncoding=utf8" />

```
### Security 
The authentication process is based on spring security.

* You need to create a new file under _src/main/webapp/WEB-INF/applicationContext-security.xml_. Take the [example](https://raw.github.com/jmirc/Hippo-CMS-Konakart/master/hippokart/site/src/main/webapp/WEB-INF/applicationContext-security.xml)
* You need to modify the _web.xml_. Take the [example](https://raw.github.com/jmirc/Hippo-CMS-Konakart/master/hippokart/site/src/main/webapp/WEB-INF/web.xml)

## Now compile and run your project


## Post configuration

### Hippo-Konakart Locales Configuration
* In order to synchronize the product between Konakart and Hippo, you need to update the available locale within Konakart. By default, Konakart installs the following locales:
    * Deutch - de_DE
    * English - en_GB
    * Español - es_ES
    * Português - pt_BR

The English locale must be updated from *en_GB* to *en_US*.


### How to start the synchronization?
The synchronization is started after log-in to the CMS. A service has been created to handle this task.



### Hippo-Konakart Store1 Configuration
* You need to update the following node with your needs. "/konakart:konakart/konakart:stores/store1"
    * Update contentroot (i.e. /content/documents/gettingstarted)
    * Update galleryroot (i.e. /content/gallery/gettingstarted)
    * Add the locale property to map with the konakart locale
	

	
