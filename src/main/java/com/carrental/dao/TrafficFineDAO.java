package com.carrental.dao;

import com.carrental.entity.TrafficFine;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 违章罚款数据访问对象
 * 负责违章罚款相关的数据库操作
 */
public class TrafficFineDAO {
    private DatabaseConnection dbConnection;

    public TrafficFineDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 添加违章罚款
     * @param trafficFine 违章罚款对象
     * @return 是否添加成功
     */
    public boolean addTrafficFine(TrafficFine trafficFine) {
        String sql = "INSERT INTO traffic_fine (car_id, user_id, violation_date, offending_location, fine, fine_state) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, trafficFine.getCarId());
            pstmt.setInt(2, trafficFine.getUserId());
            pstmt.setDate(3, Date.valueOf(trafficFine.getViolationDate()));
            pstmt.setString(4, trafficFine.getOffendingLocation());
            pstmt.setBigDecimal(5, trafficFine.getFine());
            pstmt.setString(6, trafficFine.getFineState());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("添加违章罚款失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除违章罚款
     * @param fineId 罚款ID
     * @return 是否删除成功
     */
    public boolean deleteTrafficFine(int fineId) {
        String sql = "DELETE FROM traffic_fine WHERE fine_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fineId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("删除违章罚款失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新违章罚款
     * @param trafficFine 违章罚款对象
     * @return 是否更新成功
     */
    public boolean updateTrafficFine(TrafficFine trafficFine) {
        String sql = "UPDATE traffic_fine SET car_id = ?, user_id = ?, violation_date = ?, offending_location = ?, fine = ?, fine_state = ? WHERE fine_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, trafficFine.getCarId());
            pstmt.setInt(2, trafficFine.getUserId());
            pstmt.setDate(3, Date.valueOf(trafficFine.getViolationDate()));
            pstmt.setString(4, trafficFine.getOffendingLocation());
            pstmt.setBigDecimal(5, trafficFine.getFine());
            pstmt.setString(6, trafficFine.getFineState());
            pstmt.setInt(7, trafficFine.getFineId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("更新违章罚款失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID查询违章罚款
     * @param fineId 罚款ID
     * @return 违章罚款对象
     */
    public TrafficFine getTrafficFineById(int fineId) {
        String sql = "SELECT * FROM traffic_fine WHERE fine_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fineId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTrafficFine(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("查询违章罚款失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 根据车辆ID查询违章罚款
     * @param carId 车辆ID
     * @return 违章罚款列表
     */
    public List<TrafficFine> getTrafficFineByCarId(int carId) {
        String sql = "SELECT * FROM traffic_fine WHERE car_id = ? ORDER BY violation_date DESC";
        List<TrafficFine> fineList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                fineList.add(mapResultSetToTrafficFine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据车辆ID查询违章罚款失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fineList;
    }

    /**
     * 根据用户ID查询违章罚款
     * @param userId 用户ID
     * @return 违章罚款列表
     */
    public List<TrafficFine> getTrafficFineByUserId(int userId) {
        String sql = "SELECT * FROM traffic_fine WHERE user_id = ? ORDER BY violation_date DESC";
        List<TrafficFine> fineList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                fineList.add(mapResultSetToTrafficFine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据用户ID查询违章罚款失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fineList;
    }

    /**
     * 查询所有违章罚款
     * @return 违章罚款列表
     */
    public List<TrafficFine> getAllTrafficFine() {
        String sql = "SELECT * FROM traffic_fine ORDER BY violation_date DESC";
        List<TrafficFine> fineList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                fineList.add(mapResultSetToTrafficFine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("查询所有违章罚款失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fineList;
    }

    /**
     * 根据罚款状态查询违章罚款
     * @param fineState 罚款状态
     * @return 违章罚款列表
     */
    public List<TrafficFine> getTrafficFineByState(String fineState) {
        String sql = "SELECT * FROM traffic_fine WHERE fine_state = ? ORDER BY violation_date DESC";
        List<TrafficFine> fineList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fineState);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                fineList.add(mapResultSetToTrafficFine(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据罚款状态查询违章罚款失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fineList;
    }

    /**
     * 将ResultSet映射为TrafficFine对象
     * @param rs ResultSet对象
     * @return TrafficFine对象
     * @throws SQLException SQL异常
     */
    private TrafficFine mapResultSetToTrafficFine(ResultSet rs) throws SQLException {
        TrafficFine trafficFine = new TrafficFine();
        trafficFine.setFineId(rs.getInt("fine_id"));
        trafficFine.setCarId(rs.getInt("car_id"));
        trafficFine.setUserId(rs.getInt("user_id"));
        
        Date violationDate = rs.getDate("violation_date");
        if (violationDate != null) {
            trafficFine.setViolationDate(violationDate.toLocalDate());
        }
        
        trafficFine.setOffendingLocation(rs.getString("offending_location"));
        trafficFine.setFine(rs.getBigDecimal("fine"));
        trafficFine.setFineState(rs.getString("fine_state"));
        
        return trafficFine;
    }
}
