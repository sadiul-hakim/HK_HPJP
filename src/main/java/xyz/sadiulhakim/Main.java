package xyz.sadiulhakim;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/learn", "root", "hakim@123")) {
            System.out.println("Connected....");

//            List<Customer> customers = findAll(connection);
//            System.out.println(customers);

//            insertInBatch(connection);
//            insertInGroup(connection);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void insertInGroup(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        try (var statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO CITY(NAME, COUNTRYCODE, DISTRICT, POPULATION) VALUES ('Khulna','BD','Khulna',2000000)");
            statement.executeUpdate("INSERT INTO CITY(NAME, COUNTRYCODE, DISTRICT, POPULATION) VALUES ('Jessore','BD','Dhaka',1500000)");
            connection.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void insertInBatch(Connection connection) throws Exception {
        try (var statement = connection.createStatement()) {
            statement.addBatch("INSERT INTO CITY(NAME, COUNTRYCODE, DISTRICT, POPULATION) VALUES ('Kushtia','BD','Kushtia',200000)");
            statement.addBatch("INSERT INTO CITY(NAME, COUNTRYCODE, DISTRICT, POPULATION) VALUES ('Dhaka','BD','Dhaka',10000000)");
            int[] ints = statement.executeBatch();
            System.out.println(Arrays.toString(ints));
        }
    }

    private static List<Customer> findAll(Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select * from customers;");

            List<Customer> customers = new ArrayList<>();
            while (resultSet.next()) {
                Customer customer = new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("currency"),
                        resultSet.getString("country"),
                        resultSet.getInt("balance")
                );
                customers.add(customer);
            }

            return customers;
        }
    }
}


record Customer(
        int id,
        String first_name,
        String last_name,
        String email,
        String currency,
        String country,
        int balance
) {

}