package xyz.sadiulhakim.example.bank;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankService {

    // Database connection details (re-declared for clarity in this class)
    private static final String URL = "jdbc:mysql://localhost:3307/learn";
    private static final String USER = "root";
    private static final String PASSWORD = "hakim@123"; // Replace with your MySQL password

    /**
     * Transfers a specified amount of money from one account to another.
     * This operation is managed as a single, atomic transaction.
     * * @param fromAccount The account number to transfer from.
     *
     * @param toAccount The account number to transfer to.
     * @param amount    The amount to transfer.
     */
    public void transferFunds(String fromAccount, String toAccount, BigDecimal amount) {
        // We use try-with-resources to ensure the connection is always closed.
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // --- TRANSACTION START ---
            // 1. Disable auto-commit mode to manage the transaction manually.
            connection.setAutoCommit(false);

            // 2. Debit the funds from the sender's account.
            String debitSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            try (var debitStatement = connection.prepareStatement(debitSql)) {
                debitStatement.setBigDecimal(1, amount);
                debitStatement.setString(2, fromAccount);
                int rowsAffected = debitStatement.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.printf("Error: Account %s not found. Rolling back transaction.%n", fromAccount);
                    connection.rollback();
                    return;
                }
            }

            // 3. Check if the sender has sufficient funds after the debit.
            // This is a crucial step to demonstrate rollback logic.
            String checkBalanceSql = "SELECT balance FROM accounts WHERE account_number = ?";
            try (var checkStatement = connection.prepareStatement(checkBalanceSql)) {
                checkStatement.setString(1, fromAccount);
                try (ResultSet rs = checkStatement.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal currentBalance = rs.getBigDecimal("balance");
                        if (currentBalance.compareTo(BigDecimal.ZERO) < 0) {
                            // Insufficient funds, so we roll back the entire transaction.
                            System.out.printf("Error: Insufficient funds in account %s. Rolling back transaction.%n", fromAccount);
                            connection.rollback();
                            return;
                        }
                    }
                }
            }

            // 4. Credit the funds to the receiver's account.
            String creditSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            try (var creditStatement = connection.prepareStatement(creditSql)) {
                creditStatement.setBigDecimal(1, amount);
                creditStatement.setString(2, toAccount);
                creditStatement.executeUpdate();
            }

            // 5. If all statements executed successfully, commit the transaction.
            /**
             * what does that mean? when we call debitStatement.executeUpdate(); is it reflected in database?
             * That's an excellent question, and it gets to the very heart of why transaction management is so important.
             *
             * The short answer is: **No, when `connection.setAutoCommit(false)` is active, the changes from `debitStatement.executeUpdate()` are not immediately reflected in the database in a permanent way.**
             *
             * Here's a detailed breakdown of what that means:
             *
             * 1.  **`setAutoCommit(true)` (The Default Behavior):**
             *     * When auto-commit is enabled (the default), every single SQL statement is its own individual transaction.
             *     * When you call `executeUpdate()`, the change is immediately and permanently written to the database. If you had another `executeUpdate()` statement after that, it would start a brand-new, separate transaction.
             *     * This is fine for simple, non-related operations, but it's dangerous for things like a bank transfer, because if the second operation fails, the first one has already been committed and cannot be undone.
             *
             * 2.  **`setAutoCommit(false)` (Manual Transaction Control):**
             *     * When you set auto-commit to `false`, you are essentially telling the database: "I'm about to perform a series of operations. Don't make any of these changes permanent yet. Hold them in a temporary state."
             *     * When you call `debitStatement.executeUpdate()`, the change is indeed executed, but it is only applied to the database session's temporary "transaction buffer." It is not visible to any other user or application connected to the database. The change is in a pending state.
             *     * The same goes for the `creditStatement.executeUpdate()`. It's also in a pending state.
             *
             * 3.  **The Role of `commit()` and `rollback()`:**
             *     * **`connection.commit()`**: This is the command that says, "Okay, all the pending changes in this transaction are complete and successful. Make them permanent and visible to everyone else." At this point, and only at this point, are the changes written to the database's log and finalized.
             *     * **`connection.rollback()`**: This is the command that says, "Something went wrong. Discard all the pending changes in this transaction and revert everything back to the state it was in before I started."
             *
             * **Analogy: Writing a Blog Post**
             *
             * * Think of the database as the final, published blog post.
             * * **`executeUpdate()` with auto-commit `true`:** Every sentence you type is immediately published. If you make a mistake in the next sentence, you can't just delete the previous one—it's already public.
             * * **`executeUpdate()` with auto-commit `false`:** You are writing the blog post in a text editor. Every sentence you type is just a change in your local, unsaved document.
             * * **`commit()`:** This is like clicking the "Publish" button. All your changes are made at once, and the post is now live for everyone to read.
             * * **`rollback()`:** This is like closing the text editor without saving. All your unsaved changes are lost forever.
             *
             * In the bank transfer example, this is crucial. The `debitStatement` and `creditStatement` are a single unit of work. We don't want to debit one account unless we can successfully credit the other. `setAutoCommit(false)` allows us to bundle these two operations together and either save both (commit) or throw both away (rollback).
             */
            connection.commit();
            System.out.printf("Successfully transferred $%.2f from %s to %s.%n", amount, fromAccount, toAccount);

        } catch (SQLException e) {
            // If any SQL error occurs, the transaction is implicitly rolled back
            // by the try-with-resources block or an explicit rollback can be added.
            System.err.println("Transaction failed with SQL error: " + e.getMessage());
        }
    }
}

