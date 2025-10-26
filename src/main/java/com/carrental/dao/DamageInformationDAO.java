package com.carrental.dao;

import com.carrental.entity.DamageInformation;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 损坏信息数据访问对象
 * 负责损坏信息相关的数据库操作
 */
public class DamageInformationDAO {
    private DatabaseConnection dbConnection;

    public DamageInformationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 添加损坏信息
     * @param damageInfo 损坏信息对象
     * @return 是否添加成功
     */
    public boolean addDamageInformation(DamageInformation damageInfo) {
        String sql = "INSERT INTO damage_information (car_id, photo, damage_data, damage_describe, damage_state) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, damageInfo.getCarId());
            pstmt.setBytes(2, damageInfo.getPhoto());
            pstmt.setDate(3, Date.valueOf(damageInfo.getDamageDate()));
            pstmt.setString(4, damageInfo.getDamageDescribe());
            pstmt.setString(5, damageInfo.getDamageState());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("添加损坏信息失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除损坏信息
     * @param damageId 损坏信息ID
     * @return 是否删除成功
     */
    public boolean deleteDamageInformation(int damageId) {
        String sql = "DELETE FROM damage_information WHERE damage_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, damageId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("删除损坏信息失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新损坏信息
     * @param damageInfo 损坏信息对象
     * @return 是否更新成功
     */
    public boolean updateDamageInformation(DamageInformation damageInfo) {
        String sql = "UPDATE damage_information SET car_id = ?, photo = ?, damage_data = ?, damage_describe = ?, damage_state = ? WHERE damage_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, damageInfo.getCarId());
            pstmt.setBytes(2, damageInfo.getPhoto());
            pstmt.setDate(3, Date.valueOf(damageInfo.getDamageDate()));
            pstmt.setString(4, damageInfo.getDamageDescribe());
            pstmt.setString(5, damageInfo.getDamageState());
            pstmt.setInt(6, damageInfo.getDamageId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("更新损坏信息失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID查询损坏信息
     * @param damageId 损坏信息ID
     * @return 损坏信息对象
     */
    public DamageInformation getDamageInformationById(int damageId) {
        String sql = "SELECT * FROM damage_information WHERE damage_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, damageId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDamageInformation(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("查询损坏信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 根据车辆ID查询损坏信息
     * @param carId 车辆ID
     * @return 损坏信息列表
     */
    public List<DamageInformation> getDamageInformationByCarId(int carId) {
        String sql = "SELECT * FROM damage_information WHERE car_id = ? ORDER BY damage_data DESC";
        List<DamageInformation> damageList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                damageList.add(mapResultSetToDamageInformation(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据车辆ID查询损坏信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return damageList;
    }

    /**
     * 查询所有损坏信息
     * @return 损坏信息列表
     */
    public List<DamageInformation> getAllDamageInformation() {
        String sql = "SELECT * FROM damage_information ORDER BY damage_data DESC";
        List<DamageInformation> damageList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                damageList.add(mapResultSetToDamageInformation(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("查询所有损坏信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return damageList;
    }

    /**
     * 根据维修状态查询损坏信息
     * @param damageState 维修状态
     * @return 损坏信息列表
     */
    public List<DamageInformation> getDamageInformationByState(String damageState) {
        String sql = "SELECT * FROM damage_information WHERE damage_state = ? ORDER BY damage_data DESC";
        List<DamageInformation> damageList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, damageState);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                damageList.add(mapResultSetToDamageInformation(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据维修状态查询损坏信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return damageList;
    }

    /**
     * 将ResultSet映射为DamageInformation对象
     * @param rs ResultSet对象
     * @return DamageInformation对象
     * @throws SQLException SQL异常
     */
    private DamageInformation mapResultSetToDamageInformation(ResultSet rs) throws SQLException {
        DamageInformation damageInfo = new DamageInformation();
        damageInfo.setDamageId(rs.getInt("damage_id"));
        damageInfo.setCarId(rs.getInt("car_id"));
        damageInfo.setPhoto(rs.getBytes("photo"));
        
        Date damageDate = rs.getDate("damage_data");
        if (damageDate != null) {
            damageInfo.setDamageDate(damageDate.toLocalDate());
        }
        
        damageInfo.setDamageDescribe(rs.getString("damage_describe"));
        damageInfo.setDamageState(rs.getString("damage_state"));
        
        return damageInfo;
    }
}
