# High Performance Java Persistence

## persistence.xml

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

## hibernate.properties

In plain Hibernate (without JPA), you can use **`hibernate.properties`** instead of `persistence.xml`.

Here’s how it works depending on whether you use **JPA** or **native Hibernate**:

---

### 1️⃣ If you use **JPA** (`EntityManagerFactory`)

* `persistence.xml` is required because the JPA spec mandates it.
* The JPA bootstrapping process looks specifically for a `META-INF/persistence.xml` file, and `hibernate.properties`
  will be ignored.
* You can replace `persistence.xml` **only** if you skip JPA entirely and use Hibernate’s **native bootstrapping** API
  instead.

---

### 2️⃣ If you use **Native Hibernate** (`SessionFactory`)

* You don’t need `persistence.xml`.
* Hibernate will automatically look for a `hibernate.properties` file in your **classpath root** (e.g.,
  `src/main/resources`).
* You can also supply a `hibernate.cfg.xml`, but `hibernate.properties` is simpler.

**Example `hibernate.properties`**

```properties
hibernate.connection.driver_class=org.postgresql.Driver
hibernate.connection.url=jdbc:postgresql://localhost:5432/mydb
hibernate.connection.username=myuser
hibernate.connection.password=mypass
hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
hibernate.format_sql=true
```

---

✅ **Summary:**

* **JPA** → needs `persistence.xml` (unless you ditch JPA).
* **Native Hibernate** → `hibernate.properties` works fine and replaces `persistence.xml`.

---

### **how to bootstrap Hibernate using `hibernate.properties` without JPA**, so no `persistence.xml` is needed..

Here’s how you can run Hibernate with **`hibernate.properties`** and **no `persistence.xml`** by using
Hibernate’s native API.

---

## **1️⃣ Project Structure**

```
src/main/java/com/example/Main.java
src/main/java/com/example/entity/User.java
src/main/resources/hibernate.properties
```

---

## **2️⃣ `hibernate.properties`**

Place this in `src/main/resources` (classpath root):

```properties
hibernate.connection.driver_class=org.postgresql.Driver
hibernate.connection.url=jdbc:postgresql://localhost:5432/mydb
hibernate.connection.username=myuser
hibernate.connection.password=mypass
hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
hibernate.hbm2ddl.auto=update
hibernate.show_sql=true
hibernate.format_sql=true
```

---

## **3️⃣ Entity Class**

```java
package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

---

## **4️⃣ Hibernate Bootstrapping (No JPA)**

```java
package com.example;

import com.example.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main {
    public static void main(String[] args) {
        // Load settings from hibernate.properties automatically
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);

        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                User user = new User("Alice");
                session.persist(user);

                session.getTransaction().commit();
            }
        }
    }
}
```

**How this works:**

* `Configuration()` automatically reads `hibernate.properties` from the classpath.
* No `hibernate.cfg.xml` or `persistence.xml` is needed.
* You just register your annotated entity classes manually (`addAnnotatedClass`).

---

## **5️⃣ Maven Dependencies**

```xml

<dependencies>
    <!-- Hibernate core -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>6.5.2.Final</version>
    </dependency>

    <!-- PostgreSQL driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version>
    </dependency>

    <!-- Jakarta Persistence API -->
    <dependency>
        <groupId>jakarta.persistence</groupId>
        <artifactId>jakarta.persistence-api</artifactId>
        <version>3.1.0</version>
    </dependency>
</dependencies>
```

---

With this setup:

* **We’re not using JPA’s `EntityManagerFactory`**, only Hibernate’s `SessionFactory`.
* **`hibernate.properties` replaces `persistence.xml`** entirely.
* The trade-off: you lose portability to other JPA providers (EclipseLink, etc.), but gain simplicity.

---
