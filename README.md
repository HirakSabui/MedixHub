MedicalStoreManagement
======================

Simple Java Swing + JDBC desktop application for managing a small medical store inventory and billing.

Contents
--------
- src/ - Java source
  - db/DBConnection.java         - JDBC connection helper (reads env vars or runtime settings)
  - ui/                         - Swing UI windows
    - LoginFrame.java
    - Dashboard.java
    - AddMedicineFrame.java
    - AddStockFrame.java
    - BillingFrame.java
    - ShowStockFrame.java
    - DBSettingsDialog.java      - dialog to test/save JDBC URL, user and password
    - UIUtils.java               - L&F and framing helpers
  - Main.java                    - application entry point
- lib/                           - place MySQL connector JAR here (not included)

Features
--------
- Login screen authenticates against `users` table.
- Dashboard with quick links to:
  - Add new medicine
  - Add stock to an existing medicine
  - Billing (sell medicine, deduct stock, create bills)
  - Show stock (JTable with optional search)
- DB Settings dialog to enter/test/save JDBC URL, user and password at runtime.

Database
--------
Database name: `medical_store`

SQL (run in your MySQL client to create schema):

```sql
CREATE DATABASE IF NOT EXISTS medical_store;
USE medical_store;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255) NOT NULL
);
-- insert a test admin user (plaintext password for demo only)
INSERT IGNORE INTO users(username, password) VALUES ('admin', 'admin123');

CREATE TABLE IF NOT EXISTS medicines (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  company VARCHAR(100),
  price DOUBLE NOT NULL,
  stock INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bills (
  bill_id INT AUTO_INCREMENT PRIMARY KEY,
  medicine_name VARCHAR(100),
  quantity INT,
  total DOUBLE,
  bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Important: passwords in this example are stored as plain text to keep the demo simple. For any real use, store hashed passwords (bcrypt/argon2).

Dependencies
------------
- Java JDK 8+ installed and on PATH
- MySQL server (local or reachable host)
- MySQL Connector/J JAR (put the JAR in `lib/` and name it `mysql-connector-j.jar` or update classpath)

How to build & run (Windows PowerShell)
--------------------------------------
1) Put MySQL connector JAR into the `lib/` folder (example filename: `mysql-connector-j.jar`).

2) (Optional) Set environment variables for DB connection (recommended instead of hardcoding):

```powershell
# set for current PowerShell session
$env:DB_URL  = 'jdbc:mysql://127.0.0.1:3306/medical_store?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC'
$env:DB_USER = 'root'
$env:DB_PASS = 'your_mysql_password'
```

3) Compile:

```powershell
javac -cp ".;lib/mysql-connector-j.jar" src\Main.java src\db\DBConnection.java src\ui\*.java
```

4) Run:

```powershell
java -cp ".;lib/mysql-connector-j.jar;src" Main
```

Notes and troubleshooting
------------------------
- "JDBC driver not found" or similar: ensure `mysql-connector-j.jar` exists in `lib/` and the classpath used for `javac` and `java` includes it.
- "Communications link failure": MySQL server is not reachable. Check:
  - Is MySQL running? (Windows Services or `Get-Service -Name *mysql*`)
  - Is the server listening on the port in your JDBC URL? (`Test-NetConnection -ComputerName 127.0.0.1 -Port 3306`)
  - If MySQL runs in Docker/WSL, ensure the port is forwarded to the host or use the appropriate host IP.
- "Public key retrieval is not allowed": the JDBC URL in this project includes `allowPublicKeyRetrieval=true` in defaults to work around this for local dev. For production, configure proper SSL.
- If you change DB host/port/user/password at runtime, open the Login window, click "DB Settings", enter values, click "Test Connection" and then "Save and Close".

Security
--------
- This project is a demo. Do NOT store plaintext passwords or hardcode credentials in source for production.
- Use environment variables, or an encrypted config store in real deployments.
- Use hashed passwords for users (bcrypt / argon2) instead of plain text.

Structure & Notes for Developers
--------------------------------
- UI classes are in `src/ui/` and use Swing with GridBagLayout for forms.
- DB access is in `src/db/DBConnection.java` — use `DBConnection.getConnection()` to obtain a Connection.
- All SQL uses PreparedStatement to avoid SQL injection.
- You can add unit tests (JUnit) and a small `DbTester` utility if you want quick CLI checks.

Next improvements (suggested)
---------------------------
- Add password hashing for users.
- Add input validation and better error handling for forms.
- Add export/import of inventory and bills (CSV/PDF receipts).
- Add user roles and permissions.

License
-------
MIT-style (use as you like for local development). Remove or change license to suit your needs.

Questions / Issues
-----------------
If you want me to: revert hardcoded defaults, add a `DbTester` CLI, or switch to hashed passwords — reply with which item and I'll implement it.
