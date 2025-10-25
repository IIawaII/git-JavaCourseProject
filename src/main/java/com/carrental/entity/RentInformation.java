package com.carrental.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 租车信息实体类
 * 对应数据库中的rent_information表
 */
public class RentInformation {
    private int rentId;                    // 主键
    private int carId;                     // 外键 - 车辆ID
    private int staffId;                   // 外键 - 员工ID
    private int userId;                    // 外键 - 用户ID
    private LocalDate rentDate;            // 租借日期
    private LocalDate returnDate;          // 归还日期
    private BigDecimal payTheAmount;       // 支付金额
    private BigDecimal returnAmount;       // 退还金额

    // 无参构造函数
    public RentInformation() {}

    // 带参构造函数
    public RentInformation(int rentId, int carId, int staffId, int userId, 
                          LocalDate rentDate, LocalDate returnDate, 
                          BigDecimal payTheAmount, BigDecimal returnAmount) {
        this.rentId = rentId;
        this.carId = carId;
        this.staffId = staffId;
        this.userId = userId;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.payTheAmount = payTheAmount;
        this.returnAmount = returnAmount;
    }

    // Getter和Setter方法
    public int getRentId() {
        return rentId;
    }

    public void setRentId(int rentId) {
        this.rentId = rentId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getPayTheAmount() {
        return payTheAmount;
    }

    public void setPayTheAmount(BigDecimal payTheAmount) {
        this.payTheAmount = payTheAmount;
    }

    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
    }

    @Override
    public String toString() {
        return "RentInformation{" +
                "rentId=" + rentId +
                ", carId=" + carId +
                ", staffId=" + staffId +
                ", userId=" + userId +
                ", rentDate=" + rentDate +
                ", returnDate=" + returnDate +
                ", payTheAmount=" + payTheAmount +
                ", returnAmount=" + returnAmount +
                '}';
    }
}
