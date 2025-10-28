package com.carrental.entity;

import java.time.LocalDate;

/**
 * 员工实体类
 * 对应数据库中的staff表
 */
public class Staff {
    private int staffId;                   // 主键
    private String name;                   // 姓名
    private String phone;                  // 联系电话
    private LocalDate entryDate;           // 入职日期
    private String position;               // 职位
    private int role;                      // 权限等级
    private String password;               // 登录密码

    // 无参构造函数
    public Staff() {}

    // 带参构造函数
    public Staff(int staffId, String name, String phone, LocalDate entryDate, 
                 String position, int role, String password) {
        this.staffId = staffId;
        this.name = name;
        this.phone = phone;
        this.entryDate = entryDate;
        this.position = position;
        this.role = role;
        this.password = password;
    }

    // Getter和Setter方法
    public int getStaffId() {
        return staffId;
    }
    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }
    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    public int getRole() {
        return role;
    }
    public void setRole(int role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "staffId=" + staffId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", entryDate=" + entryDate +
                ", position='" + position + '\'' +
                ", role=" + role +
                '}';
    }
}
