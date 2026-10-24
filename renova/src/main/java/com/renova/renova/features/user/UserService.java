package com.renova.renova.features.user;

import com.renova.renova.config.ConexaoMySql;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    Connection conn = null;
    CallableStatement stmt = null;
    boolean success = false;

    public User login(User user) {
        String sql = "{CALL PROC_LOGIN_USER(?, ?)}";
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet result = null;

        try {
            ConexaoMySql dbConnection = new ConexaoMySql();
            conn = ConexaoMySql.getConn(ConexaoMySql.login, ConexaoMySql.senha);
            stmt = conn.prepareCall(sql);

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());

            result = stmt.executeQuery();

            if (result.next()) {
                try {
                    //user.setUserId(result.getString("user_id"));
                    user.setName(result.getString("name"));
                    user.setUserRole(UserRole.fromRole(result.getString("user_role")));

                    System.out.println("Login successful.");

                    return user;

                } catch (Exception e) {
                    throw new RuntimeException("Error assigning values to the user object after login: " + e);
                }

            } else {
                System.out.println("Login failed.");
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database during login: " + e.getMessage());
        } finally {
            try {
                if (result != null) result.close();
                if (stmt != null) stmt.close();
                if (conn != null) new ConexaoMySql().desconectar();
            } catch (SQLException e) {
                System.out.println("Warning: Failed to close connection - " + e.getMessage());
            }
        }

    }

    public void registerUser(User user) throws Exception {

        String sql = "{CALL PROC_REGISTER_USER(?, ?, ?, ?, ?)}";

        try {
            ConexaoMySql dbConnection = new ConexaoMySql();

            conn = ConexaoMySql.getConn(ConexaoMySql.login, ConexaoMySql.senha);

            stmt = conn.prepareCall(sql);

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            // CHANGED: the original passed a UserRole to setString(), which does not compile.
            // getRole() returns the database value ("funcionario" / "cliente").
            stmt.setString(5, user.getUserRole().getRole());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("New user registered successfully.");
            } else {
                System.out.println("Error registering user.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while registering user: " + e.getMessage());

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    new ConexaoMySql().desconectar();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close the database connection - " + e.getMessage());

            }
        }

    }

}