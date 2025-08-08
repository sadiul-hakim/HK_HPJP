# High Performance Java Persistence

The persistence.xml file must be located in the src/main/resources/META-INF directory. This is a standard convention
that all JPA providers, including Hibernate, follow to find the configuration. Make sure the file path is correct in
your project structure.

A **`persistence.xml`** file is an XML configuration file used by JPA to define one or more **persistence units**. A
persistence unit is a logical grouping of entity classes and their associated configurations, such as database
connection details, that should be managed together.

Here is a typical structure of a `persistence.xml` file for a Java SE application using Hibernate and MySQL.

-----

### Basic `persistence.xml` Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="3.0"
             xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd">

    <persistence-unit name="my-persistence-unit" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <class>xyz.sadiulhakim.entity.Employee</class>
        <class>xyz.sadiulhakim.entity.Department</class>

        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/jpa_demo"/>
            <property name="jakarta.persistence.jdbc.user" value="root"/>
            <property name="jakarta.persistence.jdbc.password" value="password"/>

            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.MySQL8Dialect"/>
        </properties>
    </persistence-unit>
</persistence>
```

-----

### Breakdown of the File

* **`<persistence>`**: This is the root element of the file. The `version`, `xmlns`, and `xsi:schemaLocation` attributes
  define the XML schema for JPA 3.0.
* **`<persistence-unit>`**: This element defines a single persistence unit.
    * `name`: A unique name for the persistence unit. This is the name you use in your code to create the
      `EntityManagerFactory`, for example, `Persistence.createEntityManagerFactory("my-persistence-unit")`.
    * `transaction-type`: Specifies the type of transaction management.
        * `RESOURCE_LOCAL` (used here) is for Java SE applications where you manually manage transactions.
        * `JTA` is for Java EE environments where the application server manages transactions.
* **`<provider>`**: This tag specifies the JPA implementation. Here, it points to Hibernate's persistence provider
  class.
* **`<class>`**: You must list all your entity classes that belong to this persistence unit.
* **`<properties>`**: This is where you configure the database connection and other Hibernate-specific settings.
    * **Jakarta Persistence Properties**: The `jakarta.persistence.jdbc.*` properties are standard JPA properties for
      configuring the database connection.
    * **Hibernate Properties**: The `hibernate.*` properties are specific to Hibernate and control its behavior.
        * `hibernate.hbm2ddl.auto`: A critical property that controls schema generation.
            * `create`: Creates the schema every time the application starts.
            * `update`: Updates the schema incrementally.
            * `validate`: Validates the schema against your entities.
            * `none`: Disables schema generation.
        * `hibernate.show_sql`: If set to `true`, Hibernate will print all executed SQL statements to the console.
        * `hibernate.dialect`: Specifies the SQL dialect for your database (e.g., MySQL, PostgreSQL, Oracle) so
          Hibernate can generate correct and optimized SQL.