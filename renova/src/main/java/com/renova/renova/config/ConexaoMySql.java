package com.renova.renova.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySql {

    private static final String sDriver = "com.mysql.cj.jdbc.Driver";
    private static final String connStr = "jdbc:mysql://localhost:3306/renova";

    public static final String login = "root";
    public static final String senha = "11905972";

    static Connection conn;

    public ConexaoMySql() {
    }

    public static Connection getConn(String login, String senha) throws SQLException {
        try {
            // Agora o sDriver sempre terá o valor correto
            Class.forName(sDriver);
            conn = DriverManager.getConnection(connStr, login, senha);
            System.out.println("Conectado!!");
            return conn;

        } catch (ClassNotFoundException e) {
            String error = "Driver nao encontrado";
            throw new SQLException(error, e);

        } catch (SQLException e) {
            String error = "Erro ao obter conexao :" + e;
            throw new SQLException(error, e);
        }
    }

    public void desconectar() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Banco desconectado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}