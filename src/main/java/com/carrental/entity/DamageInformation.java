package com.carrental.entity;

import java.time.LocalDate;

/**
 * 损坏信息实体类
 * 对应数据库中的damage_information表
 */
public class DamageInformation {
    private int damageId;              // 主键
    private int carId;                 // 外键，车辆ID
    private byte[] photo;              // 照片
    private LocalDate damageDate;      // 损毁日期
    private String damageDescribe;     // 损毁描述
    private String damageState;        // 维修状态（已维修、未维修）

    // 无参构造函数
    public DamageInformation() {}

    // 带参构造函数
    public DamageInformation(int damageId, int carId, byte[] photo, LocalDate damageDate, 
                           String damageDescribe, String damageState) {
        this.damageId = damageId;
        this.carId = carId;
        this.photo = photo;
        this.damageDate = damageDate;
        this.damageDescribe = damageDescribe;
        this.damageState = damageState;
    }

    // Getter和Setter方法
    public int getDamageId() {
        return damageId;
    }
    public void setDamageId(int damageId) {
        this.damageId = damageId;
    }

    public int getCarId() {
        return carId;
    }
    public void setCarId(int carId) {
        this.carId = carId;
    }

    public byte[] getPhoto() {
        return photo;
    }
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public LocalDate getDamageDate() {
        return damageDate;
    }
    public void setDamageDate(LocalDate damageDate) {
        this.damageDate = damageDate;
    }

    public String getDamageDescribe() {
        return damageDescribe;
    }
    public void setDamageDescribe(String damageDescribe) {
        this.damageDescribe = damageDescribe;
    }

    public String getDamageState() {
        return damageState;
    }
    public void setDamageState(String damageState) {
        this.damageState = damageState;
    }

    @Override
    public String toString() {
        return "DamageInformation{" +
                "damageId=" + damageId +
                ", carId=" + carId +
                ", damageDate=" + damageDate +
                ", damageDescribe='" + damageDescribe + '\'' +
                ", damageState='" + damageState + '\'' +
                '}';
    }
}
