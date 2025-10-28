package com.carrental.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 汽车实体类
 * 对应数据库中的car表
 */
public class Car {
    private int carId;                    // 主键
    private String licensePlateNumber;     // 车牌号
    private String model;                  // 型号
    private String color;                  // 颜色
    private String status;                 // 状态
    private String brand;                  // 品牌
    private LocalDate purchaseDate;        // 购买日期
    private byte[] photo;                  // 照片
    private BigDecimal rent;               // 日租金
    private String deposit;                // 押金

    // 无参构造函数
    public Car() {}

    // 带参构造函数
    public Car(int carId, String licensePlateNumber, String model, String color, 
               String status, String brand, LocalDate purchaseDate, byte[] photo, 
               BigDecimal rent, String deposit) {
        this.carId = carId;
        this.licensePlateNumber = licensePlateNumber;
        this.model = model;
        this.color = color;
        this.status = status;
        this.brand = brand;
        this.purchaseDate = purchaseDate;
        this.photo = photo;
        this.rent = rent;
        this.deposit = deposit;
    }

    // Getter和Setter方法
    public int getCarId() {
        return carId;
    }
    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }
    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public byte[] getPhoto() {
        return photo;
    }
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public BigDecimal getRent() {
        return rent;
    }
    public void setRent(BigDecimal rent) {
        this.rent = rent;
    }

    public String getDeposit() {
        return deposit;
    }
    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    @Override
    public String toString() {
        return licensePlateNumber + " - " + brand + " - " + model;
    }
}
