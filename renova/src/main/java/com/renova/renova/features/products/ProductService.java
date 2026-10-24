package com.renova.renova.features.products;

import com.renova.renova.config.ConexaoMySql;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class ProductService {

    Connection conn = null;
    CallableStatement stmt = null;
    ResultSet resultSet;

    public void registerProduct(Product product) {

        String sql = "{CALL PROC_REGISTER_PRODUCT(?, ?, ?, ?)}";

        try {
            ConexaoMySql dbConnection = new ConexaoMySql();
            conn = ConexaoMySql.getConn(ConexaoMySql.login, ConexaoMySql.senha);
            stmt = conn.prepareCall(sql);
            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setString(4, product.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Product '" + product.getProductName() + "' was added successfully.");
            } else {
                System.out.println("Error registering product.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while registering product: " + e.getMessage());

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

    public ArrayList<Product> listProducts() throws SQLException {
        String sql = "{CALL PROC_LIST_PRODUCTS()}";

        try {
            ConexaoMySql dbConnection = new ConexaoMySql();

            conn = ConexaoMySql.getConn(ConexaoMySql.login, ConexaoMySql.senha);
            stmt = conn.prepareCall(sql);

            ArrayList<Product> products = new ArrayList<>();

            resultSet = stmt.executeQuery();

            while (resultSet.next()) {

                Product product = new Product();

                product.setProductId(resultSet.getString("product_id"));
                product.setProductName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStatus(resultSet.getString("status"));

                products.add(product);
            }

            if (!products.isEmpty()) {
                System.out.println("Products listed successfully.");
            } else {
                System.out.println("No products listed.");
            }

            return products;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while listing products: " + e.getMessage());

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    new ConexaoMySql().desconectar();
                }
                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e) {
                System.out.println("Failed to close the database connection - " + e.getMessage());
            }
        }
    }

    public Product findProductById(String searchId) {

        String sql = "{CALL PROC_FIND_PRODUCT_BY_ID(?)}";

        try {
            ConexaoMySql dbConnection = new ConexaoMySql();
            conn = ConexaoMySql.getConn(ConexaoMySql.login, ConexaoMySql.senha);
            stmt = conn.prepareCall(sql);

            stmt.setString(1, searchId);
            resultSet = stmt.executeQuery();

            if (resultSet.next()) {

                Product foundProduct = new Product();

                foundProduct.setProductId(resultSet.getString("product_id"));
                foundProduct.setProductName(resultSet.getString("product_name"));
                foundProduct.setDescription(resultSet.getString("description"));
                foundProduct.setPrice(resultSet.getDouble("price"));
                foundProduct.setStatus(resultSet.getString("status"));

                System.out.println("Product ID " + searchId + " found successfully.");

                return foundProduct;

            } else {
                System.out.println("No product found with ID: " + searchId);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while finding product by ID: " + e.getMessage());

        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
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

    public ArrayList<Product> findProductsByName(String searchTerm) throws SQLException {
        String sql = "{CALL PROC_FIND_PRODUCTS_BY_NAME(?)}";

        try {
            ConexaoMySql dbConnection = new ConexaoMySql();

            conn = ConexaoMySql.getConn(ConexaoMySql.login, ConexaoMySql.senha);
            stmt = conn.prepareCall(sql);

            ArrayList<Product> products = new ArrayList<>();

            stmt.setString(1, searchTerm);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {

                Product product = new Product();

                product.setProductId(resultSet.getString("product_id"));
                product.setProductName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("description"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStatus(resultSet.getString("status"));

                products.add(product);
            }

            if (!products.isEmpty()) {
                System.out.println("Products searched by name listed successfully.");
            }

            return products;

        } catch (SQLException e) {
            throw new RuntimeException("Database error while listing products by name: " + e.getMessage());

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    new ConexaoMySql().desconectar();
                }
                if (resultSet != null) {
                    resultSet.close();
                }

            } catch (SQLException e) {
                System.out.println("Failed to close the database connection - " + e.getMessage());
            }
        }
    }
}