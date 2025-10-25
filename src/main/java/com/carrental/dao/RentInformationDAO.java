package com.carrental.dao;

import com.carrental.entity.RentInformation;
import com.carrental.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 租车信息数据访问对象
 * 负责租车信息相关的数据库操作
 */
public class RentInformationDAO {
    private DatabaseConnection dbConnection;

    public RentInformationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 添加租车信息
     * @param rentInfo 租车信息对象
     * @return 是否添加成功
     */
    public boolean addRentInformation(RentInformation rentInfo) {
        String sql = "INSERT INTO rent_information (car_id, staff_id, user_id, rent_date, return_date, pay_the_amount, return_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentInfo.getCarId());
            pstmt.setInt(2, rentInfo.getStaffId());
            pstmt.setInt(3, rentInfo.getUserId());
            pstmt.setDate(4, Date.valueOf(rentInfo.getRentDate()));
            pstmt.setDate(5, Date.valueOf(rentInfo.getReturnDate()));
            pstmt.setBigDecimal(6, rentInfo.getPayTheAmount());
            pstmt.setBigDecimal(7, rentInfo.getReturnAmount());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("添加租车信息失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID删除租车信息
     * @param rentId 租车信息ID
     * @return 是否删除成功
     */
    public boolean deleteRentInformation(int rentId) {
        String sql = "DELETE FROM rent_information WHERE rent_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("删除租车信息失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新租车信息
     * @param rentInfo 租车信息对象
     * @return 是否更新成功
     */
    public boolean updateRentInformation(RentInformation rentInfo) {
        String sql = "UPDATE rent_information SET car_id = ?, staff_id = ?, user_id = ?, rent_date = ?, return_date = ?, pay_the_amount = ?, return_amount = ? WHERE rent_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentInfo.getCarId());
            pstmt.setInt(2, rentInfo.getStaffId());
            pstmt.setInt(3, rentInfo.getUserId());
            pstmt.setDate(4, Date.valueOf(rentInfo.getRentDate()));
            pstmt.setDate(5, Date.valueOf(rentInfo.getReturnDate()));
            pstmt.setBigDecimal(6, rentInfo.getPayTheAmount());
            pstmt.setBigDecimal(7, rentInfo.getReturnAmount());
            pstmt.setInt(8, rentInfo.getRentId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("更新租车信息失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID查询租车信息
     * @param rentId 租车信息ID
     * @return 租车信息对象
     */
    public RentInformation getRentInformationById(int rentId) {
        String sql = "SELECT * FROM rent_information WHERE rent_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRentInformation(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("查询租车信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 查询所有租车信息
     * @return 租车信息列表
     */
    public List<RentInformation> getAllRentInformation() {
        String sql = "SELECT * FROM rent_information ORDER BY rent_id";
        List<RentInformation> rentInfoList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                rentInfoList.add(mapResultSetToRentInformation(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("查询所有租车信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentInfoList;
    }

    /**
     * 根据车辆ID查询租车信息
     * @param carId 车辆ID
     * @return 租车信息列表
     */
    public List<RentInformation> getRentInformationByCarId(int carId) {
        String sql = "SELECT * FROM rent_information WHERE car_id = ? ORDER BY rent_id";
        List<RentInformation> rentInfoList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rentInfoList.add(mapResultSetToRentInformation(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据车辆ID查询租车信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentInfoList;
    }

    /**
     * 根据用户ID查询租车信息
     * @param userId 用户ID
     * @return 租车信息列表
     */
    public List<RentInformation> getRentInformationByUserId(int userId) {
        String sql = "SELECT * FROM rent_information WHERE user_id = ? ORDER BY rent_id";
        List<RentInformation> rentInfoList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rentInfoList.add(mapResultSetToRentInformation(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据用户ID查询租车信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentInfoList;
    }

    /**
     * 根据员工ID查询租车信息
     * @param staffId 员工ID
     * @return 租车信息列表
     */
    public List<RentInformation> getRentInformationByStaffId(int staffId) {
        String sql = "SELECT * FROM rent_information WHERE staff_id = ? ORDER BY rent_id";
        List<RentInformation> rentInfoList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rentInfoList.add(mapResultSetToRentInformation(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据员工ID查询租车信息失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return rentInfoList;
    }

    /**
     * 计算租金
     * @param carId 车辆ID
     * @param rentDate 租借日期
     * @param returnDate 归还日期
     * @return 租金金额
     */
    public BigDecimal calculateRent(int carId, LocalDate rentDate, LocalDate returnDate) {
        String sql = "SELECT rent FROM car WHERE car_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal dailyRent = rs.getBigDecimal("rent");
                long days = java.time.temporal.ChronoUnit.DAYS.between(rentDate, returnDate);
                return dailyRent.multiply(BigDecimal.valueOf(days));
            }
            
        } catch (SQLException e) {
            System.err.println("计算租金失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * 将ResultSet映射为RentInformation对象
     * @param rs ResultSet对象
     * @return RentInformation对象
     * @throws SQLException SQL异常
     */
    private RentInformation mapResultSetToRentInformation(ResultSet rs) throws SQLException {
        RentInformation rentInfo = new RentInformation();
        rentInfo.setRentId(rs.getInt("rent_id"));
        rentInfo.setCarId(rs.getInt("car_id"));
        rentInfo.setStaffId(rs.getInt("staff_id"));
        rentInfo.setUserId(rs.getInt("user_id"));
        
        Date rentDate = rs.getDate("rent_date");
        if (rentDate != null) {
            rentInfo.setRentDate(rentDate.toLocalDate());
        }
        
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            rentInfo.setReturnDate(returnDate.toLocalDate());
        }
        
        BigDecimal payAmount = rs.getBigDecimal("pay_the_amount");
        if (payAmount != null) {
            rentInfo.setPayTheAmount(payAmount);
        }
        
        BigDecimal returnAmount = rs.getBigDecimal("return_amount");
        if (returnAmount != null) {
            rentInfo.setReturnAmount(returnAmount);
        }
        
        return rentInfo;
    }
}
