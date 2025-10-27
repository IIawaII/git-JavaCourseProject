package com.carrental.dao;

import com.carrental.entity.Staff;
import com.carrental.util.DatabaseConnection;
import com.carrental.util.AppLogger;
import com.carrental.util.SessionLockManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 员工数据访问对象
 * 负责员工相关的数据库操作
 */
public class StaffDAO {
    private DatabaseConnection dbConnection;
    // 标记上一次登录尝试是否因为已被其它会话占用锁而被阻塞
    private boolean lastLoginBlockedByLock = false;

    public StaffDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 员工登录验证
     * 使用MySQL锁机制防止并发登录
     * @param name 员工姓名
     * @param password 密码
     * @return 员工对象，登录失败返回null
     */
    public Staff login(String name, String password) {
        // reset last-login blocked flag
        this.lastLoginBlockedByLock = false;

        // 使用原子性 UPDATE 来翻转 role（仅当 role >= 0 时），以防止并发登录的竞态条件
        String updateSql = "UPDATE staff SET role = -ABS(role) WHERE name = ? AND password = ? AND role >= 0";
        String checkLoggedSql = "SELECT COUNT(*) FROM staff WHERE name = ? AND role < 0";
        String selectSql = "SELECT * FROM staff WHERE name = ? AND password = ?";

        try (Connection conn = dbConnection.getConnection()) {
            try (PreparedStatement up = conn.prepareStatement(updateSql)) {
                up.setString(1, name);
                up.setString(2, password);
                int updated = up.executeUpdate();
                if (updated == 0) {
                    // 没有更新，可能是用户名/密码错误或已被标记为已登录
                    try (PreparedStatement chk = conn.prepareStatement(checkLoggedSql)) {
                        chk.setString(1, name);
                        try (ResultSet rs = chk.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                this.lastLoginBlockedByLock = true;
                                return null;
                            }
                        }
                    }
                    return null;
                }

                // 更新成功，查询并返回员工信息
                try (PreparedStatement sel = conn.prepareStatement(selectSql)) {
                    sel.setString(1, name);
                    sel.setString(2, password);
                    try (ResultSet rs = sel.executeQuery()) {
                        if (rs.next()) {
                            return mapResultSetToStaff(rs);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.logException("员工登录验证失败: " + e.getMessage(), e);
        }

        return null;
    }
    
    /**
     * 查询上一次登录尝试是否因为锁被占用而被阻塞
     * @return true 表示上次尝试因锁被占用被阻塞
     */
    public boolean wasLastLoginBlockedByLock() {
        return this.lastLoginBlockedByLock;
    }
    
    /**
     * 标记员工为已登录状态
     * @param conn 数据库连接
     * @param name 员工姓名
     * @throws SQLException SQL异常
     */
    private void markStaffAsLoggedIn(Connection conn, String name) throws SQLException {
        // 将role字段设为负数来标记登录状态
        String sql = "UPDATE staff SET role = -ABS(role) WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * 标记员工为已登出状态
     * @param conn 数据库连接
     * @param name 员工姓名
     * @throws SQLException SQL异常
     */
    private void markStaffAsLoggedOut(Connection conn, String name) throws SQLException {
        // 将role字段恢复为正数
        String sql = "UPDATE staff SET role = ABS(role) WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }
    }

    /**
     * 添加员工
     * @param staff 员工对象
     * @return 是否添加成功
     */
    public boolean addStaff(Staff staff) {
        String sql = "INSERT INTO staff (name, phone, entry_date, position, role, password) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getPhone());
            pstmt.setDate(3, Date.valueOf(staff.getEntryDate()));
            pstmt.setString(4, staff.getPosition());
            pstmt.setInt(5, staff.getRole());
            pstmt.setString(6, staff.getPassword());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            AppLogger.logException("添加员工失败: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据ID删除员工
     * @param staffId 员工ID
     * @return 是否删除成功
     */
    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            AppLogger.logException("删除员工失败: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 更新员工信息
     * @param staff 员工对象
     * @return 是否更新成功
     */
    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE staff SET name = ?, phone = ?, entry_date = ?, position = ?, role = ?, password = ? WHERE staff_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getPhone());
            pstmt.setDate(3, Date.valueOf(staff.getEntryDate()));
            pstmt.setString(4, staff.getPosition());
            pstmt.setInt(5, staff.getRole());
            pstmt.setString(6, staff.getPassword());
            pstmt.setInt(7, staff.getStaffId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            AppLogger.logException("更新员工失败: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据ID查询员工
     * @param staffId 员工ID
     * @return 员工对象
     */
    public Staff getStaffById(int staffId) {
        String sql = "SELECT * FROM staff WHERE staff_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
            
        } catch (SQLException e) {
            AppLogger.logException("查询员工失败: " + e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * 查询所有员工
     * @return 员工列表
     */
    public List<Staff> getAllStaff() {
        String sql = "SELECT * FROM staff ORDER BY staff_id";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
            
        } catch (SQLException e) {
            AppLogger.logException("查询所有员工失败: " + e.getMessage(), e);
        }
        
        return staffList;
    }

    /**
     * 清理陈旧的登录标记：扫描 staff 表中 role < 0 的记录，检查对应的会话锁是否依然存在；
     * 若锁不存在则认为之前的会话已断开，恢复该用户为登出状态。
     * 该方法可在应用启动时调用以纠正异常断开的会话遗留状态。
     */
    public void cleanupStaleLogins() {
        String sql = "SELECT name FROM staff WHERE role < 0";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                String lockName = "staff_session_lock_" + name;
                try (PreparedStatement check = conn.prepareStatement("SELECT IS_USED_LOCK(?)")) {
                    check.setString(1, lockName);
                    try (ResultSet crs = check.executeQuery()) {
                        if (crs.next()) {
                            // 若返回 NULL，表示锁不存在，需重置登录标记
                            if (crs.getObject(1) == null) {
                                try (Connection uconn = dbConnection.getConnection()) {
                                    uconn.setAutoCommit(false);
                                    markStaffAsLoggedOut(uconn, name);
                                    uconn.commit();
                                    AppLogger.info("已清理陈旧登录标记: " + name);
                                } catch (SQLException ex) {
                                    AppLogger.logException("清理陈旧登录标记失败: " + ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            AppLogger.logException("扫描陈旧登录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据职位查询员工
     * @param position 职位
     * @return 员工列表
     */
    public List<Staff> getStaffByPosition(String position) {
        String sql = "SELECT * FROM staff WHERE position = ? ORDER BY staff_id";
        List<Staff> staffList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, position);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    staffList.add(mapResultSetToStaff(rs));
                }
            }
            
        } catch (SQLException e) {
            AppLogger.logException("根据职位查询员工失败: " + e.getMessage(), e);
        }
        
        return staffList;
    }

    /**
     * 员工登出
     * 清除登录状态标记
     * @param name 员工姓名
     * @return 是否登出成功
     */
    public boolean logout(String name) {
        boolean dbOk = false;
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 标记为已登出（恢复 role 正数）
                markStaffAsLoggedOut(conn, name);
                conn.commit();
                dbOk = true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            AppLogger.logException("员工登出失败: " + e.getMessage(), e);
            dbOk = false;
        }

        // 释放会话锁（如果有的话）
        try {
            SessionLockManager.getInstance().releaseSessionLock(name);
        } catch (Exception e) {
            AppLogger.logException("释放会话锁时出错: " + e.getMessage(), e);
        }

        return dbOk;
    }
    
    /**
     * 将ResultSet映射为Staff对象
     * @param rs ResultSet对象
     * @return Staff对象
     * @throws SQLException SQL异常
     */
    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setName(rs.getString("name"));
        staff.setPhone(rs.getString("phone"));
        
        Date entryDate = rs.getDate("entry_date");
        if (entryDate != null) {
            staff.setEntryDate(entryDate.toLocalDate());
        }
        
        staff.setPosition(rs.getString("position"));
        // 确保role始终为正数（恢复原始权限等级）
        int role = rs.getInt("role");
        staff.setRole(Math.abs(role));
        staff.setPassword(rs.getString("password"));
        
        return staff;
    }
}
