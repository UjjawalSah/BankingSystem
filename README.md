Banking System Project
Table of Contents
Introduction
Features
Technologies Used
Architecture
Setup and Installation
Usage
Testing
Contributing
License
Introduction
This project aims to enhance the overall banking experience by facilitating convenient account access, transaction tracking, and account management. The system is designed to efficiently manage customer accounts and transactions, providing a user-friendly platform for both administrators and customers.

Features
User Management: Registration, login, and management of customer accounts.
Transaction Management: Deposits, withdrawals, and transaction history.
Admin Control: Admins can add, delete, and view customer details.
Security: Robust measures to safeguard sensitive information and ensure secure transactions.
Responsive UI: A user-friendly interface built with modern web technologies.
Technologies Used
Frontend: HTML, CSS, JavaScript, SweetAlert JS
Backend: Java Servlets, JDBC
Database: MySQL
Web Server: Apache Tomcat
IDE: Eclipse
Code Quality: SonarQube
Architecture
The banking system follows a client-server architecture, with a well-defined database schema to ensure data integrity and security.

Architecture Diagram

Class Diagram

Sequence Diagram

Setup and Installation
Prerequisites
Java Development Kit (JDK)
Apache Tomcat Server
MySQL Database
Eclipse IDE
SonarQube
Installation Steps
Clone the repository:
git clone  https://github.com/UjjawalSah/BankingSystem.git

Setup the database:
Install MySQL and create a database.
Import the provided SQL script to set up the required tables.

Configure the backend:
Open the project in Eclipse.
Configure the database connection in the JDBC configuration file.

Deploy on Tomcat:
Build the project and deploy the WAR file to the Apache Tomcat server.
Run SonarQube:

Install and configure SonarQube for code quality analysis.
Run the analysis to ensure code standards are met.

Usage

Admin Panel:
Admins can register new users, manage accounts, and oversee transactions.

User Dashboard:
Users can log in, view account details, perform transactions, and view transaction history.

Testing

Test Cases
Mobile Number Validation: Ensures the input is a valid 10-digit number.
Aadhar Number Validation: Checks for a valid 12-digit Aadhar number.
Initial Balance Validation: Ensures the initial balance is at least 1000.
Email Validation: Verifies that the email contains an "@" symbol and a valid domain.
Account Deletion Validation: Ensures accounts with non-zero balances cannot be deleted.
Insufficient Balance Validation: Ensures transactions fail if the balance is insufficient.

 
