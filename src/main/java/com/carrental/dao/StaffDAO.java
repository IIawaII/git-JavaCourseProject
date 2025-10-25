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
     * @param name 员工姓名
     * @param password 密码
     * @return 员工对象，登录失败返回null
     */
    public Staff login(String name, String password) {
        String sql = "SELECT * FROM staff WHERE name = ? AND password = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStaff(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("员工登录验证失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
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
        staff.setRole(rs.getInt("role"));
        staff.setPassword(rs.getString("password"));
        
        return staff;
    }
}
