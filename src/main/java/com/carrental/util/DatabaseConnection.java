package com.carrental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 * 使用单例模式管理数据库连接
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://10.245.203.137:3306/car_rental_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    private static DatabaseConnection instance;
    private Connection connection;

    /**
     * 私有构造函数，防止外部实例化
     */
    private DatabaseConnection() {
        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接实例（单例模式）
     * @return DatabaseConnection实例
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * 获取数据库连接
     * @return Connection对象
     */
    public Connection getConnection() {
        try {
            // 检查连接是否有效
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("重新获取数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("关闭数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试数据库连接
     * @return 连接是否成功
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("测试数据库连接失败: " + e.getMessage());
            return false;
        }
    }
}
