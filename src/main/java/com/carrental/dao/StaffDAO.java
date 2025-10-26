package com.carrental.dao;

import com.carrental.entity.Staff;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 员工数据访问对象
 * 负责员工相关的数据库操作
 */
public class StaffDAO {
    private DatabaseConnection dbConnection;

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
        String lockName = "staff_login_lock_" + name;
        String sql = "SELECT * FROM staff WHERE name = ? AND password = ?";
        
        try (Connection conn = dbConnection.getConnection()) {
            // 设置连接为手动提交模式，确保锁的原子性
            conn.setAutoCommit(false);
            
            try {
                // 尝试获取MySQL锁，超时时间为5秒
                if (acquireLock(conn, lockName, 5)) {
                    return performLoginWithLock(conn, lockName, name, password, sql);
                } else {
                    // 无法获取锁时，尝试自动恢复异常状态
                    try {
                        autoResetAbnormalState(conn, name);
                        // 重新尝试获取锁
                        if (acquireLock(conn, lockName, 2)) {
                            // 重新执行登录流程
                            return performLoginWithLock(conn, lockName, name, password, sql);
                        }
                    } catch (SQLException e) {
                        // 忽略自动恢复过程中的异常
                    }
                    return null;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("员工登录验证失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * 获取MySQL锁
     * @param conn 数据库连接
     * @param lockName 锁名称
     * @param timeout 超时时间（秒）
     * @return 是否成功获取锁
     * @throws SQLException SQL异常
     */
    private boolean acquireLock(Connection conn, String lockName, int timeout) throws SQLException {
        String sql = "SELECT GET_LOCK(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lockName);
            pstmt.setInt(2, timeout);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
        }
        return false;
    }
    
    /**
     * 释放MySQL锁
     * @param conn 数据库连接
     * @param lockName 锁名称
     * @throws SQLException SQL异常
     */
    private void releaseLock(Connection conn, String lockName) throws SQLException {
        String sql = "SELECT RELEASE_LOCK(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lockName);
            pstmt.executeQuery();
        }
    }
    
    /**
     * 检查员工是否已经登录
     * 使用staff表的role字段作为登录状态标记（临时方案）
     * @param conn 数据库连接
     * @param name 员工姓名
     * @return 是否已登录
     * @throws SQLException SQL异常
     */
    private boolean isStaffAlreadyLoggedIn(Connection conn, String name) throws SQLException {
        // 由于不能修改表结构，我们使用一个临时方案：
        // 在staff表中添加一个is_logged_in字段来标记登录状态
        // 但根据要求不能修改表结构，所以我们使用role字段的符号位来标记
        // 这里我们使用一个更简单的方法：检查是否存在活跃的登录会话
        String sql = "SELECT COUNT(*) FROM staff WHERE name = ? AND role < 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    /**
     * 自动重置异常状态
     * 在检测到异常登录状态时自动重置，确保系统健壮性
     * @param conn 数据库连接
     * @param name 员工姓名
     * @throws SQLException SQL异常
     */
    private void autoResetAbnormalState(Connection conn, String name) throws SQLException {
        // 检查是否存在MySQL锁，如果不存在说明之前的会话已断开
        String lockName = "staff_login_lock_" + name;
        String checkLockSql = "SELECT IS_USED_LOCK(?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(checkLockSql)) {
            pstmt.setString(1, lockName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // 如果锁不存在（返回NULL），说明之前的会话已断开，可以安全重置
                if (rs.getObject(1) == null) {
                    markStaffAsLoggedOut(conn, name);
                }
            }
        }
    }
    
    /**
     * 在已获取锁的情况下执行登录流程
     * @param conn 数据库连接
     * @param lockName 锁名称
     * @param name 员工姓名
     * @param password 密码
     * @param sql 查询SQL
     * @return 员工对象，登录失败返回null
     * @throws SQLException SQL异常
     */
    private Staff performLoginWithLock(Connection conn, String lockName, String name, String password, String sql) throws SQLException {
        try {
            // 检查是否已有其他用户登录
            if (isStaffAlreadyLoggedIn(conn, name)) {
                // 自动重置异常状态，确保系统健壮性
                autoResetAbnormalState(conn, name);
            }
            
            // 执行登录验证
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Staff staff = mapResultSetToStaff(rs);
                    // 标记用户为已登录状态
                    markStaffAsLoggedIn(conn, name);
                    conn.commit();
                    return staff;
                }
            }
            
            conn.commit();
            return null;
            
        } finally {
            // 释放锁
            releaseLock(conn, lockName);
        }
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
            System.err.println("添加员工失败: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("删除员工失败: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("更新员工失败: " + e.getMessage());
            e.printStackTrace();
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
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("查询员工失败: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("查询所有员工失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return staffList;
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
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                staffList.add(mapResultSetToStaff(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据职位查询员工失败: " + e.getMessage());
            e.printStackTrace();
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
        String lockName = "staff_logout_lock_" + name;
        
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // 获取锁
                if (acquireLock(conn, lockName, 5)) {
                    try {
                        // 标记为已登出
                        markStaffAsLoggedOut(conn, name);
                        conn.commit();
                        return true;
                    } finally {
                        releaseLock(conn, lockName);
                    }
                } else {
                    System.err.println("无法获取登出锁");
                    return false;
                }
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            System.err.println("员工登出失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
