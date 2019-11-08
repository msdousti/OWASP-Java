# OWASP Java - Vulnerability Showcase

A **seriously flawed** Java project for teaching [OWASP Top 10 - 2017](https://www.owasp.org/images/7/72/OWASP_Top_10-2017_%28en%29.pdf.pdf) concepts.

```
DO NOT USE ANY PART OF THIS CODE IN PRODUCTION.
```

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You'll need:

* [JDK 11](https://www.oracle.com/javadownload). I used  version "11.0.5" 2019-10-15 LTS.
* [Payara Server](https://www.payara.fish/software/downloads/). I used version Payara Server 5.193.1 Full.
* [MySQL Community Server 8](https://dev.mysql.com/downloads/mysql/). I used MySQL Community Server 8.0.18.
* [MySQL Connector/J 8](https://dev.mysql.com/downloads/connector/j/). I used ... "Platform Independent" version.
* [IntelliJ IDEA](https://www.jetbrains.com/idea/). Not a prerequisite per se, but it would be hard to work with the project without it. I used IntelliJ IDEA 2019.2.3.

#### JDK 11
Install JDK, and configure environment variables `JAVA_HOME` and `PATH` properly. Some guidelines are provided in [this Stackoverflow post](https://stackoverflow.com/q/1672281/459391).

Open a command prompt, and make sure everything works smoothly:

![Setting up JDK-11](extra/images/jdk-11.png)

#### MySQL Community Server 8
Unzip MySQL, and go to the `bin` directory. Execute the following command (*not recommended* for production environments):
```
mysqld --initialize-insecure
```

This command initializes the data directory, and creates a `'root'@'localhost'` superuser account with empty password.

To install the service on Windows machines, run:
```
mysqld --install
```

Finally, to start the service, run:
```
sc start mysql
```

To check whether the installation was successful, issue the command `mysql -uroot`: 
![MySQL console](extra/images/mysql.png)

Tyep `exit`, and press `Enter` to get out of MySQL console.

We need to create our sample database, and populate it with sample data. The SQL file is called [testdb.sql](extra/testdb.sql). Download it to MySQL `bin` directory, and from there execute the following command:
```
mysql -uroot < testdb.sql
```

If everything goes smoothly, you can issue the commands and see the results as shown below: 

![MySQL - check database creation and data population](extra/images/mysql-2.png)

Finally, change the `root` password to `123456`, as empty passwords will not be accepted by Payara Server (next step). Notice that the password itself, as well as the following method for changing the password, are **totally insecure**:
```
mysqladmin --user=root password "123456"
``` 

#### Payara Server
Download and extract Payara Server, as well as MySQL Connector/J. I extracted the latter to `C:\tmp\mysql-connector-java-8.0.18.jar`.

Make sure you followed the instructions for installing and configuring JDK-11. Start Payara Server using the following command:
```
asadmin start-domain
```

Next, install MySQL Connector/J:
```
asadmin add-library C:\tmp\mysql-connector-java-8.0.18.jar
```

Here's a snapshot of how things should look like:

![Payara - starting server and installing MySQL Connector/J](extra/images/payara.png)

Open [Payara Web Console](http://localhost:4848/), and navigate to `Resources → JDBC → JDBC Connection Pools`. Click the `New` buttton.

![Payara - creating new connection pool](extra/images/payara-new-cp.png)

For Step 1, fill in the information exactly as follows:

![Payara - connection pool - step 1 of 2](extra/images/payara-new-cp-step1.png)

For Step 2, scroll down until you see the `Additional Properties`:

![Payara - connection pool - step 2 of 2](extra/images/payara-new-cp-step2.png)

You have to set the following properties (sorted alphabetically):

* `allowPublicKeyRetrieval` – Whether the client is allowed to automatically request the public key from the server. Set to `true`.
* `DatabaseName` – The name of the database you want to connect to. In this case, `testdb`.
* `Password`: The password for the specified user. Here, it's `123456`.
* `ServerName`: The location of the MySQL server. In this case `localhost`.
* `User`: The username for connecting to the database. Here, it's `root`.

Hit the `Finish` button. Click the newly created connection pool. In the `General` tab, click the `Ping` button. If everything goes right, you should see the following:

![Payara - connection pool - Ping](extra/images/payara-new-cp-step2.png)

Similarly, create another connection pool called `MySQL_readonly_Pool`. The Step 1 is the same as the previous connection pool. For Step 2, set the `Additional Properties` as follows:

* `allowPublicKeyRetrieval: true`.
* `Password:     MyVeryLongPassphrase`.
* `ServerName:   localhost`.
* `ServerName:   localhost`.
* `User:         readonly`.

Finally, we need to set up the JDBC resources. Go to `Resources → JDBC → JDBC Resources`, and click `New`. Fill in the form as follows:

* `JNDI Name: jdbc/MySQL_root_DataSource`
* `Connection Pool: MySQL_root_Pool`

![Payara - JDBC Resources](extra/images/payara-new-jdbc-res.png)

Similarly, do this for the second connection pool:
* `JNDI Name: jdbc/MySQL_readonly_DataSource`
* `Connection Pool: MySQL_readonly_Pool`

#### IntelliJ IDEA
