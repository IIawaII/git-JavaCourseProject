package com.carrental.util;

import com.carrental.util.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 会话级别的命名锁管理器
 * <p>
 * 为了防止同一账号在多台设备同时登录，我们在登录成功后保留一个长连接并持有 MySQL 命名锁
 * 直到用户显式登出或连接断开（连接断开会自动释放锁）。
 */
public class SessionLockManager {
    private static final SessionLockManager INSTANCE = new SessionLockManager();

    // 存储持有锁的连接，key 为用户名
    private final ConcurrentMap<String, Connection> lockConnections = new ConcurrentHashMap<>();

    private SessionLockManager() {}

    // 在构造时注册 JVM 关闭钩子，确保进程退出时释放本进程持有的所有会话锁
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (String user : lockConnections.keySet()) {
                try {
                    releaseSessionLock(user);
                } catch (Exception e) {
                    // 记录但忽略异常
                    AppLogger.logException("Shutdown releaseSessionLock 异常: " + e.getMessage(), e);
                }
            }
        }));
    }

    public static SessionLockManager getInstance() {
        return INSTANCE;
    }

    /**
     * 尝试为指定用户获取会话锁（会保留连接以便长期持有锁）
     * @param username 用户名
     * @param timeoutSec 获取锁的超时时间（秒）
     * @return 获取成功返回 true，失败或异常返回 false
     */
    public boolean acquireSessionLock(String username, int timeoutSec) {
        String lockName = "staff_session_lock_" + username;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(true);

            try (PreparedStatement pstmt = conn.prepareStatement("SELECT GET_LOCK(?, ?)")) {
                pstmt.setString(1, lockName);
                pstmt.setInt(2, timeoutSec);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 1) {
                        // 成功获取锁，保存连接以保持锁
                        Connection previous = lockConnections.putIfAbsent(username, conn);
                        if (previous != null) {
                            // 已经有人在本进程持有该锁（不大可能），关闭本连接并返回 false
                            try { conn.close(); } catch (SQLException ignore) {}
                            AppLogger.warn("本地已有会话锁: " + username);
                            return false;
                        }
                        AppLogger.info("获取会话锁成功: " + lockName);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.logException("acquireSessionLock 异常: " + e.getMessage(), e);
            if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
        }

        // 未获取到锁，确保连接被关闭
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignore) {}
        }
        AppLogger.info("获取会话锁失败: " + lockName);
        return false;
    }

    /**
     * 释放指定用户的会话锁并关闭连接
     * @param username 用户名
     * @return 如果之前确实持有锁并成功释放则返回 true，否则返回 false
     */
    public boolean releaseSessionLock(String username) {
        Connection conn = lockConnections.remove(username);
        if (conn == null) {
            return false;
        }

        String lockName = "staff_session_lock_" + username;
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT RELEASE_LOCK(?)")) {
            pstmt.setString(1, lockName);
            try (ResultSet rs = pstmt.executeQuery()) {
                // 忽略结果
            }
        } catch (SQLException e) {
            AppLogger.logException("releaseSessionLock 异常: " + e.getMessage(), e);
        } finally {
            try { conn.close(); } catch (SQLException ignore) {}
        }

        return true;
    }

    /**
     * 检查当前进程是否持有指定用户的会话锁
     * @param username 用户名
     * @return true 表示本进程持有锁
     */
    public boolean isLockHeldLocally(String username) {
        return lockConnections.containsKey(username);
    }
}
