package com.carrental.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 违章罚款实体类
 * 对应数据库中的traffic_fine表
 */
public class TrafficFine {
    private int fineId;                // 主键
    private int carId;                 // 外键，车辆ID
    private int userId;                // 外键，用户ID
    private LocalDate violationDate;   // 违规日期
    private String offendingLocation;  // 违规地点
    private BigDecimal fine;           // 罚款金额
    private String fineState;          // 罚款状态（已交、未交）

    // 无参构造函数
    public TrafficFine() {}

    // 带参构造函数
    public TrafficFine(int fineId, int carId, int userId, LocalDate violationDate, 
                      String offendingLocation, BigDecimal fine, String fineState) {
        this.fineId = fineId;
        this.carId = carId;
        this.userId = userId;
        this.violationDate = violationDate;
        this.offendingLocation = offendingLocation;
        this.fine = fine;
        this.fineState = fineState;
    }

    // Getter和Setter方法
    public int getFineId() { return fineId; }
    public void setFineId(int fineId) { this.fineId = fineId; }

    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getViolationDate() { return violationDate; }
    public void setViolationDate(LocalDate violationDate) { this.violationDate = violationDate; }

    public String getOffendingLocation() { return offendingLocation; }
    public void setOffendingLocation(String offendingLocation) { this.offendingLocation = offendingLocation; }

    public BigDecimal getFine() { return fine; }
    public void setFine(BigDecimal fine) { this.fine = fine; }

    public String getFineState() { return fineState; }
    public void setFineState(String fineState) { this.fineState = fineState; }

    @Override
    public String toString() {
        return "TrafficFine{" +
                "fineId=" + fineId +
                ", carId=" + carId +
                ", userId=" + userId +
                ", violationDate=" + violationDate +
                ", offendingLocation='" + offendingLocation + '\'' +
                ", fine=" + fine +
                ", fineState='" + fineState + '\'' +
                '}';
    }
}
