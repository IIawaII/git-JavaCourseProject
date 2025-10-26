package com.carrental.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 * 使用单例模式管理数据库连接
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://10.245.203.137:3306/car_rental_system?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    private static DatabaseConnection instance;
    private Connection connection;
    private boolean connectionFailed = false; // 标记连接是否失败
    private boolean initialized = false; // 标记是否已尝试初始化

    /**
     * 私有构造函数，防止外部实例化
     */
    private DatabaseConnection() {
        initializeConnection();
    }

    /**
     * 初始化数据库连接
     */
    private void initializeConnection() {
        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            this.connectionFailed = false; // 连接成功，重置失败标记
            this.initialized = true;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            this.connectionFailed = true; // 标记连接失败
            this.initialized = true; // 标记已尝试初始化
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
    public Connection getConnection() throws SQLException {
        if (!initialized) {
            // 如果尚未初始化，先初始化连接
            initializeConnection();
        }
        
        try {
            // 如果连接失败过，尝试重新连接
            if (connectionFailed || connection == null || connection.isClosed()) {
                try {
                    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                    connectionFailed = false; // 重新连接成功，重置失败标记
                } catch (SQLException e) {
                    System.err.println("重新获取数据库连接失败: " + e.getMessage());
                    e.printStackTrace();
                    connectionFailed = true; // 标记连接失败
                    throw e; // 抛出异常让调用者处理
                }
            }
        } catch (SQLException e) {
            System.err.println("检查数据库连接时出错: " + e.getMessage());
            e.printStackTrace();
            connectionFailed = true;
            throw e;
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

    /**
     * 检查连接是否失败
     * @return 是否连接失败
     */
    public boolean isConnectionFailed() {
        if (!initialized) {
            // 如果尚未初始化，先初始化
            initializeConnection();
        }
        return connectionFailed;
    }
}