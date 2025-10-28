package com.carrental.dao;

import com.carrental.entity.Car;
import com.carrental.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 车辆数据访问对象
 * 负责车辆相关的数据库操作
 */
public class CarDAO {
    private final DatabaseConnection dbConnection;

    public CarDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 添加车辆
     * @param car 车辆对象
     * @return 是否添加成功
     */
    public boolean addCar(Car car) {
        String sql = "INSERT INTO car (car_id, license_plate_number, model, color, status, brand, purchase_date, photo, rent, deposit) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, car.getCarId());
            pstmt.setString(2, car.getLicensePlateNumber());
            pstmt.setString(3, car.getModel());
            pstmt.setString(4, car.getColor());
            pstmt.setString(5, car.getStatus());
            pstmt.setString(6, car.getBrand());
            pstmt.setDate(7, Date.valueOf(car.getPurchaseDate()));
            pstmt.setBytes(8, car.getPhoto());
            pstmt.setBigDecimal(9, car.getRent());
            pstmt.setString(10, car.getDeposit());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("添加车辆失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID删除车辆
     * @param carId 车辆ID
     * @return 是否删除成功
     */
    public boolean deleteCar(int carId) {
        String sql = "DELETE FROM car WHERE car_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("删除车辆失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新车辆信息
     * @param car 车辆对象
     * @return 是否更新成功
     */
    public boolean updateCar(Car car) {
        String sql = "UPDATE car SET license_plate_number = ?, model = ?, color = ?, status = ?, brand = ?, purchase_date = ?, photo = ?, rent = ?, deposit = ? WHERE car_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, car.getLicensePlateNumber());
            pstmt.setString(2, car.getModel());
            pstmt.setString(3, car.getColor());
            pstmt.setString(4, car.getStatus());
            pstmt.setString(5, car.getBrand());
            pstmt.setDate(6, Date.valueOf(car.getPurchaseDate()));
            pstmt.setBytes(7, car.getPhoto());
            pstmt.setBigDecimal(8, car.getRent());
            pstmt.setString(9, car.getDeposit());
            pstmt.setInt(10, car.getCarId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("更新车辆失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID查询车辆
     * @param carId 车辆ID
     * @return 车辆对象
     */
    public Car getCarById(int carId) {
        String sql = "SELECT * FROM car WHERE car_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCar(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("查询车辆失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * 查询所有车辆
     * @return 车辆列表
     */
    public List<Car> getAllCars() {
        String sql = "SELECT * FROM car ORDER BY car_id";
        List<Car> cars = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("查询所有车辆失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }

    /**
     * 根据状态查询车辆
     * @param status 车辆状态
     * @return 车辆列表
     */
    public List<Car> getCarsByStatus(String status) {
        String sql = "SELECT * FROM car WHERE status = ? ORDER BY car_id";
        List<Car> cars = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据状态查询车辆失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }

    /**
     * 根据品牌查询车辆
     * @param brand 品牌
     * @return 车辆列表
     */
    public List<Car> getCarsByBrand(String brand) {
        String sql = "SELECT * FROM car WHERE brand = ? ORDER BY car_id";
        List<Car> cars = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, brand);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("根据品牌查询车辆失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return cars;
    }

    /**
     * 更新车辆状态
     * @param carId 车辆ID
     * @param status 新状态
     * @return 是否更新成功
     */
    public boolean updateCarStatus(int carId, String status) {
        String sql = "UPDATE car SET status = ? WHERE car_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, carId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.err.println("更新车辆状态失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将ResultSet映射为Car对象
     * @param rs ResultSet对象
     * @return Car对象
     * @throws SQLException SQL异常
     */
    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setCarId(rs.getInt("car_id"));
        car.setLicensePlateNumber(rs.getString("license_plate_number"));
        car.setModel(rs.getString("model"));
        car.setColor(rs.getString("color"));
        car.setStatus(rs.getString("status"));
        car.setBrand(rs.getString("brand"));
        
        Date purchaseDate = rs.getDate("purchase_date");
        if (purchaseDate != null) {
            car.setPurchaseDate(purchaseDate.toLocalDate());
        }
        
        car.setPhoto(rs.getBytes("photo"));
        
        BigDecimal rent = rs.getBigDecimal("rent");
        if (rent != null) {
            car.setRent(rent);
        }
        
        car.setDeposit(rs.getString("deposit"));
        
        return car;
    }
}
