package xyz.sadiulhakim.example.bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.math.BigDecimal;

/**
 * A sample program demonstrating a bank transaction using JDBC with
 * manual transaction management (commit and rollback).
 *
 * This program uses a BankService class to handle fund transfers between two accounts.
 * It shows two scenarios: a successful transaction that is committed, and a
 * failed transaction that is rolled back, ensuring data integrity.
 *
 * Prerequisites:
 * 1. MySQL database named `bankdb`
 * 2. A `accounts` table with columns: `account_number`, `owner_name`, `balance`
 * 3. MySQL Connector/J JAR file in the classpath.
 */
public class Main {

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password"; // Replace with your MySQL password

    // SQL script to set up the database and table
    private static final String SETUP_SQL = """
        -- Create the database if it doesn't exist
        CREATE DATABASE IF NOT EXISTS bankdb;
        
        -- Use the newly created database
        USE bankdb;
        
        -- Drop the table if it exists to start fresh
        DROP TABLE IF EXISTS accounts;
        
        -- Create the accounts table
        CREATE TABLE accounts (
            account_number VARCHAR(50) PRIMARY KEY,
            owner_name VARCHAR(100) NOT NULL,
            balance DECIMAL(10, 2) NOT NULL
        );
        
        -- Insert initial data
        INSERT INTO accounts (account_number, owner_name, balance) VALUES
        ('1001', 'Alice', 1000.00),
        ('1002', 'Bob', 500.00);
    """;

    public static void main(String[] args) {
        System.out.println("--- Starting Bank Transaction Demo ---");

        // Setup the database with initial accounts
        setupDatabase();

        // Instantiate the bank service
        BankService bankService = new BankService();

        // Scenario 1: A successful transaction
        System.out.println("\nScenario 1: Successful Fund Transfer (Commit)");
        bankService.transferFunds("1001", "1002", new BigDecimal("200.00"));
        printAccountBalances();

        // Scenario 2: A failed transaction due to insufficient funds
        System.out.println("\nScenario 2: Failed Fund Transfer (Rollback)");
        bankService.transferFunds("1001", "1002", new BigDecimal("1000.00"));
        printAccountBalances();
    }

    /**
     * Sets up the database and the accounts table with initial data.
     */
    private static void setupDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            // Execute the setup script. We split the script by semicolons.
            for (String sql : SETUP_SQL.split(";")) {
                if (!sql.trim().isEmpty()) {
                    statement.execute(sql);
                }
            }
            System.out.println("Database setup complete.");
        } catch (SQLException e) {
            System.err.println("Database setup failed: " + e.getMessage());
        }
    }

    /**
     * Prints the current balances of all accounts.
     */
    private static void printAccountBalances() {
        System.out.println("Current Account Balances:");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT account_number, owner_name, balance FROM accounts ORDER BY account_number")) {

            while (resultSet.next()) {
                String accountNumber = resultSet.getString("account_number");
                String ownerName = resultSet.getString("owner_name");
                BigDecimal balance = resultSet.getBigDecimal("balance");
                System.out.printf("Account: %s (%s) | Balance: $%.2f%n", accountNumber, ownerName, balance);
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve account balances: " + e.getMessage());
        }
    }
}