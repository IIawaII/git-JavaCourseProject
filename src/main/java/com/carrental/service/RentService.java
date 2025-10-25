package com.carrental.service;

import com.carrental.dao.RentInformationDAO;
import com.carrental.dao.CarDAO;
import com.carrental.dao.UserDAO;
import com.carrental.dao.StaffDAO;
import com.carrental.entity.RentInformation;
import com.carrental.entity.Car;
import com.carrental.entity.User;
import com.carrental.entity.Staff;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 租车管理服务类
 * 提供租车相关的业务逻辑
 */
public class RentService {
    private RentInformationDAO rentInfoDAO;
    private CarDAO carDAO;
    private UserDAO userDAO;
    private StaffDAO staffDAO;

    public RentService() {
        this.rentInfoDAO = new RentInformationDAO();
        this.carDAO = new CarDAO();
        this.userDAO = new UserDAO();
        this.staffDAO = new StaffDAO();
    }

    /**
     * 租车
     * @param carId 车辆ID
     * @param userId 用户ID
     * @param staffId 员工ID
     * @param rentDate 租借日期
     * @param returnDate 归还日期
     * @return 是否租车成功
     */
    public boolean rentCar(int carId, int userId, int staffId, LocalDate rentDate, LocalDate returnDate) {
        // 验证租车信息
        if (!validateRentInfo(carId, userId, staffId, rentDate, returnDate)) {
            return false;
        }
        
        // 检查车辆是否可用
        Car car = carDAO.getCarById(carId);
        if (car == null || !"空闲".equals(car.getStatus())) {
            System.err.println("车辆不可用");
            return false;
        }
        
        // 计算租金
        BigDecimal rentAmount = rentInfoDAO.calculateRent(carId, rentDate, returnDate);
        if (rentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("租金计算错误");
            return false;
        }
        
        // 创建租车信息
        RentInformation rentInfo = new RentInformation();
        rentInfo.setCarId(carId);
        rentInfo.setUserId(userId);
        rentInfo.setStaffId(staffId);
        rentInfo.setRentDate(rentDate);
        rentInfo.setReturnDate(returnDate);
        rentInfo.setPayTheAmount(rentAmount);
        rentInfo.setReturnAmount(BigDecimal.ZERO); // 初始退还金额为0
        
        // 添加租车信息
        boolean success = rentInfoDAO.addRentInformation(rentInfo);
        
        if (success) {
            // 更新车辆状态为已借出
            carDAO.updateCarStatus(carId, "已借出");
        }
        
        return success;
    }

    /**
     * 还车
     * @param rentId 租车信息ID
     * @param actualReturnDate 实际归还日期
     * @param damageCost 损坏费用
     * @return 是否还车成功
     */
    public boolean returnCar(int rentId, LocalDate actualReturnDate, BigDecimal damageCost) {
        RentInformation rentInfo = rentInfoDAO.getRentInformationById(rentId);
        if (rentInfo == null) {
            System.err.println("租车信息不存在");
            return false;
        }
        
        // 计算实际租金
        BigDecimal actualRent = rentInfoDAO.calculateRent(rentInfo.getCarId(), rentInfo.getRentDate(), actualReturnDate);
        
        // 计算退还金额（支付金额 - 实际租金 - 损坏费用）
        BigDecimal returnAmount = rentInfo.getPayTheAmount().subtract(actualRent).subtract(damageCost);
        
        // 更新租车信息
        rentInfo.setReturnDate(actualReturnDate);
        rentInfo.setReturnAmount(returnAmount);
        
        boolean success = rentInfoDAO.updateRentInformation(rentInfo);
        
        if (success) {
            // 更新车辆状态为空闲
            carDAO.updateCarStatus(rentInfo.getCarId(), "空闲");
        }
        
        return success;
    }

    /**
     * 查询所有租车信息
     * @return 租车信息列表
     */
    public List<RentInformation> getAllRentInformation() {
        return rentInfoDAO.getAllRentInformation();
    }

    /**
     * 根据用户ID查询租车信息
     * @param userId 用户ID
     * @return 租车信息列表
     */
    public List<RentInformation> getRentInformationByUserId(int userId) {
        return rentInfoDAO.getRentInformationByUserId(userId);
    }

    /**
     * 根据车辆ID查询租车信息
     * @param carId 车辆ID
     * @return 租车信息列表
     */
    public List<RentInformation> getRentInformationByCarId(int carId) {
        return rentInfoDAO.getRentInformationByCarId(carId);
    }

    /**
     * 根据员工ID查询租车信息
     * @param staffId 员工ID
     * @return 租车信息列表
     */
    public List<RentInformation> getRentInformationByStaffId(int staffId) {
        return rentInfoDAO.getRentInformationByStaffId(staffId);
    }

    /**
     * 查询当前租借中的车辆
     * @return 租车信息列表
     */
    public List<RentInformation> getCurrentRentals() {
        List<RentInformation> allRentals = rentInfoDAO.getAllRentInformation();
        return allRentals.stream()
                .filter(rent -> rent.getReturnDate() == null || rent.getReturnDate().isAfter(LocalDate.now()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 计算租金
     * @param carId 车辆ID
     * @param rentDate 租借日期
     * @param returnDate 归还日期
     * @return 租金金额
     */
    public BigDecimal calculateRent(int carId, LocalDate rentDate, LocalDate returnDate) {
        if (rentDate.isAfter(returnDate)) {
            System.err.println("租借日期不能晚于归还日期");
            return BigDecimal.ZERO;
        }
        
        return rentInfoDAO.calculateRent(carId, rentDate, returnDate);
    }

    /**
     * 计算总收益
     * @return 总收益
     */
    public BigDecimal calculateTotalRevenue() {
        List<RentInformation> allRentals = rentInfoDAO.getAllRentInformation();
        return allRentals.stream()
                .filter(rent -> rent.getReturnDate() != null)
                .map(rent -> rent.getPayTheAmount().subtract(rent.getReturnAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 验证租车信息
     * @param carId 车辆ID
     * @param userId 用户ID
     * @param staffId 员工ID
     * @param rentDate 租借日期
     * @param returnDate 归还日期
     * @return 是否有效
     */
    private boolean validateRentInfo(int carId, int userId, int staffId, LocalDate rentDate, LocalDate returnDate) {
        // 检查车辆是否存在
        Car car = carDAO.getCarById(carId);
        if (car == null) {
            System.err.println("车辆不存在");
            return false;
        }
        
        // 检查用户是否存在
        User user = userDAO.getUserById(userId);
        if (user == null) {
            System.err.println("用户不存在");
            return false;
        }
        
        // 检查员工是否存在
        Staff staff = staffDAO.getStaffById(staffId);
        if (staff == null) {
            System.err.println("员工不存在");
            return false;
        }
        
        // 检查日期
        if (rentDate.isBefore(LocalDate.now())) {
            System.err.println("租借日期不能早于当前日期");
            return false;
        }
        
        if (rentDate.isAfter(returnDate)) {
            System.err.println("租借日期不能晚于归还日期");
            return false;
        }
        
        return true;
    }
}
