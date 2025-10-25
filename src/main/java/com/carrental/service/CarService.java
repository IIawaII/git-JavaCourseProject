package com.carrental.service;

import com.carrental.dao.CarDAO;
import com.carrental.dao.RentInformationDAO;
import com.carrental.entity.Car;
import com.carrental.entity.RentInformation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 车辆管理服务类
 * 提供车辆相关的业务逻辑
 */
public class CarService {
    private CarDAO carDAO;
    private RentInformationDAO rentInfoDAO;

    public CarService() {
        this.carDAO = new CarDAO();
        this.rentInfoDAO = new RentInformationDAO();
    }

    /**
     * 添加车辆
     * @param car 车辆对象
     * @return 是否添加成功
     */
    public boolean addCar(Car car) {
        // 验证车辆信息
        if (!validateCar(car)) {
            return false;
        }
        
        // 检查车牌号是否已存在
        List<Car> existingCars = carDAO.getAllCars();
        for (Car existingCar : existingCars) {
            if (existingCar.getLicensePlateNumber().equals(car.getLicensePlateNumber())) {
                System.err.println("车牌号已存在: " + car.getLicensePlateNumber());
                return false;
            }
        }
        
        return carDAO.addCar(car);
    }

    /**
     * 删除车辆
     * @param carId 车辆ID
     * @return 是否删除成功
     */
    public boolean deleteCar(int carId) {
        // 检查车辆是否正在租借中
        List<RentInformation> rentInfos = rentInfoDAO.getRentInformationByCarId(carId);
        for (RentInformation rentInfo : rentInfos) {
            if (rentInfo.getReturnDate() == null || rentInfo.getReturnDate().isAfter(LocalDate.now())) {
                System.err.println("车辆正在租借中，无法删除");
                return false;
            }
        }
        
        return carDAO.deleteCar(carId);
    }

    /**
     * 更新车辆信息
     * @param car 车辆对象
     * @return 是否更新成功
     */
    public boolean updateCar(Car car) {
        if (!validateCar(car)) {
            return false;
        }
        
        return carDAO.updateCar(car);
    }

    /**
     * 根据ID查询车辆
     * @param carId 车辆ID
     * @return 车辆对象
     */
    public Car getCarById(int carId) {
        return carDAO.getCarById(carId);
    }

    /**
     * 查询所有车辆
     * @return 车辆列表
     */
    public List<Car> getAllCars() {
        return carDAO.getAllCars();
    }

    /**
     * 查询可用车辆
     * @return 可用车辆列表
     */
    public List<Car> getAvailableCars() {
        return carDAO.getCarsByStatus("空闲");
    }

    /**
     * 根据品牌查询车辆
     * @param brand 品牌
     * @return 车辆列表
     */
    public List<Car> getCarsByBrand(String brand) {
        return carDAO.getCarsByBrand(brand);
    }

    /**
     * 根据型号查询车辆
     * @param model 型号
     * @return 车辆列表
     */
    public List<Car> getCarsByModel(String model) {
        List<Car> allCars = carDAO.getAllCars();
        return allCars.stream()
                .filter(car -> car.getModel().equals(model))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 更新车辆状态
     * @param carId 车辆ID
     * @param status 新状态
     * @return 是否更新成功
     */
    public boolean updateCarStatus(int carId, String status) {
        Car car = carDAO.getCarById(carId);
        if (car != null) {
            car.setStatus(status);
            return carDAO.updateCar(car);
        }
        return false;
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
     * 验证车辆信息
     * @param car 车辆对象
     * @return 是否有效
     */
    private boolean validateCar(Car car) {
        if (car.getLicensePlateNumber() == null || car.getLicensePlateNumber().trim().isEmpty()) {
            System.err.println("车牌号不能为空");
            return false;
        }
        
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            System.err.println("型号不能为空");
            return false;
        }
        
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            System.err.println("品牌不能为空");
            return false;
        }
        
        if (car.getRent() == null || car.getRent().compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("日租金必须大于0");
            return false;
        }
        
        if (car.getPurchaseDate() == null || car.getPurchaseDate().isAfter(LocalDate.now())) {
            System.err.println("购买日期不能为空或晚于当前日期");
            return false;
        }
        
        return true;
    }
}
