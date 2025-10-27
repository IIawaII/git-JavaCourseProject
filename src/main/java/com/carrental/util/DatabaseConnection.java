package com.carrental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 数据库连接工具类
 * 使用单例模式管理数据库连接
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://10.245.203.137:3306/car_rental_system?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "12345678";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // 配置文件路径（相对于项目根目录）
    private static final String CONFIG_PATH = "config.properties";

    // 可被覆盖的配置（从 config.properties 读取后赋值）
    private String url = URL;
    private String username = USERNAME;
    private String password = PASSWORD;
    private String driver = DRIVER;
    private static DatabaseConnection instance;
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
        // 尝试从配置文件加载（若存在）
        loadConfigFromFile();
        try {
            Class.forName(driver);
            // 尝试短暂建立一次连接以验证配置是否可用，然后立即关闭。
            try (Connection testConn = DriverManager.getConnection(url, username, password)) {
                this.connectionFailed = false;
            } catch (SQLException se) {
                System.err.println("初始化时无法建立测试连接: " + se.getMessage());
                this.connectionFailed = true;
            }
            this.initialized = true;
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 驱动类未找到: " + e.getMessage());
            e.printStackTrace();
            this.connectionFailed = true;
            this.initialized = true;
        }
    }

    /**
     * 从项目根目录的 config.properties 加载数据库配置（若文件存在且含有相关项）
     */
    private void loadConfigFromFile() {
        File f = new File(CONFIG_PATH);
        if (!f.exists()) return;

        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream(f)) {
            p.load(fis);
            String v;
            v = p.getProperty("database.url"); if (v != null && !v.trim().isEmpty()) url = v.trim();
            v = p.getProperty("database.username"); if (v != null && !v.trim().isEmpty()) username = v.trim();
            v = p.getProperty("database.password"); if (v != null) password = v; // allow empty
            v = p.getProperty("database.driver"); if (v != null && !v.trim().isEmpty()) driver = v.trim();
        } catch (IOException e) {
            System.err.println("加载 config.properties 失败: " + e.getMessage());
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
        // 每次都返回一个新的 Connection，调用者应负责关闭它（DAO 中已使用 try-with-resources）
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            connectionFailed = false;
            return conn;
        } catch (SQLException e) {
            connectionFailed = true;
            System.err.println("获取数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 关闭数据库连接
     */
    public void closeConnection() {
        // 不维护长期连接，关闭操作不需要处理具体连接。
        // 保留方法以兼容现有调用（无操作）。
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