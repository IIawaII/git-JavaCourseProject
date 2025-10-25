package com.carrental.test;

import com.carrental.entity.Car;
import com.carrental.entity.User;
import com.carrental.entity.Staff;
import com.carrental.entity.RentInformation;
import com.carrental.service.CarService;
import com.carrental.service.UserService;
import com.carrental.service.RentService;
import com.carrental.util.DatabaseConnection;

import java.math.BigDecimal;

/**
 * 系统测试类
 * 用于测试系统的基本功能
 */
public class SystemTest {
    
    public static void main(String[] args) {
        System.out.println("汽车出租管理系统测试开始...");
        System.out.println("================================");
        
        // 测试数据库连接
        testDatabaseConnection();
        
        // 测试车辆服务
        testCarService();
        
        // 测试用户服务
        testUserService();
        
        // 测试租车服务
        testRentService();
        
        System.out.println("================================");
        System.out.println("系统测试完成！");
    }
    
    /**
     * 测试数据库连接
     */
    private static void testDatabaseConnection() {
        System.out.println("测试数据库连接...");
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (dbConn.testConnection()) {
            System.out.println("✓ 数据库连接成功");
        } else {
            System.out.println("✗ 数据库连接失败");
        }
    }
    
    /**
     * 测试车辆服务
     */
    private static void testCarService() {
        System.out.println("测试车辆服务...");
        try {
            CarService carService = new CarService();
            
            // 查询所有车辆
            java.util.List<Car> cars = carService.getAllCars();
            System.out.println("✓ 查询到 " + cars.size() + " 辆车辆");
            
            // 查询可用车辆
            java.util.List<Car> availableCars = carService.getAvailableCars();
            System.out.println("✓ 查询到 " + availableCars.size() + " 辆可用车辆");
            
        } catch (Exception e) {
            System.out.println("✗ 车辆服务测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试用户服务
     */
    private static void testUserService() {
        System.out.println("测试用户服务...");
        try {
            UserService userService = new UserService();
            
            // 测试员工登录
            Staff staff = userService.login("super213", "213");
            if (staff != null) {
                System.out.println("✓ 员工登录测试成功: " + staff.getName());
            } else {
                System.out.println("✗ 员工登录测试失败");
            }
            
            // 查询所有用户
            java.util.List<User> users = userService.getAllUsers();
            System.out.println("✓ 查询到 " + users.size() + " 个用户");
            
        } catch (Exception e) {
            System.out.println("✗ 用户服务测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试租车服务
     */
    private static void testRentService() {
        System.out.println("测试租车服务...");
        try {
            RentService rentService = new RentService();
            
            // 查询所有租车信息
            java.util.List<RentInformation> rentInfos = rentService.getAllRentInformation();
            System.out.println("✓ 查询到 " + rentInfos.size() + " 条租车记录");
            
            // 计算总收益
            BigDecimal totalRevenue = rentService.calculateTotalRevenue();
            System.out.println("✓ 总收益: ¥" + totalRevenue);
            
        } catch (Exception e) {
            System.out.println("✗ 租车服务测试失败: " + e.getMessage());
        }
    }
}
