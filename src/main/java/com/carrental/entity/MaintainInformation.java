package com.carrental.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 维修信息实体类
 * 对应数据库中的maintain_information表
 */
public class MaintainInformation {
    private int maintainId;            // 主键
    private int carId;                 // 外键，车辆ID
    private LocalDate maintainDate;    // 报修日期
    private String maintainDescribe;   // 报修描述
    private LocalDate maintainBeginDate;   // 维修开始日期
    private LocalDate maintainFinishDate;  // 维修结束日期
    private BigDecimal maintainCost;   // 维修费用

    // 无参构造函数
    public MaintainInformation() {}

    // 带参构造函数
    public MaintainInformation(int maintainId, int carId, LocalDate maintainDate, 
                             String maintainDescribe, LocalDate maintainBeginDate, 
                             LocalDate maintainFinishDate, BigDecimal maintainCost) {
        this.maintainId = maintainId;
        this.carId = carId;
        this.maintainDate = maintainDate;
        this.maintainDescribe = maintainDescribe;
        this.maintainBeginDate = maintainBeginDate;
        this.maintainFinishDate = maintainFinishDate;
        this.maintainCost = maintainCost;
    }

    // Getter和Setter方法
    public int getMaintainId() {
        return maintainId;
    }

    public void setMaintainId(int maintainId) {
        this.maintainId = maintainId;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public LocalDate getMaintainDate() {
        return maintainDate;
    }

    public void setMaintainDate(LocalDate maintainDate) {
        this.maintainDate = maintainDate;
    }

    public String getMaintainDescribe() {
        return maintainDescribe;
    }

    public void setMaintainDescribe(String maintainDescribe) {
        this.maintainDescribe = maintainDescribe;
    }

    public LocalDate getMaintainBeginDate() {
        return maintainBeginDate;
    }

    public void setMaintainBeginDate(LocalDate maintainBeginDate) {
        this.maintainBeginDate = maintainBeginDate;
    }

    public LocalDate getMaintainFinishDate() {
        return maintainFinishDate;
    }

    public void setMaintainFinishDate(LocalDate maintainFinishDate) {
        this.maintainFinishDate = maintainFinishDate;
    }

    public BigDecimal getMaintainCost() {
        return maintainCost;
    }

    public void setMaintainCost(BigDecimal maintainCost) {
        this.maintainCost = maintainCost;
    }

    @Override
    public String toString() {
        return "MaintainInformation{" +
                "maintainId=" + maintainId +
                ", carId=" + carId +
                ", maintainDate=" + maintainDate +
                ", maintainDescribe='" + maintainDescribe + '\'' +
                ", maintainBeginDate=" + maintainBeginDate +
                ", maintainFinishDate=" + maintainFinishDate +
                ", maintainCost=" + maintainCost +
                '}';
    }
}
