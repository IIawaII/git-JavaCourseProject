package com.carrental.entity;

import java.time.LocalDate;

/**
 * 用户实体类
 * 对应数据库中的user表
 */
public class User {
    private int userId;                    // 主键
    private String name;                   // 姓名
    private String identityId;             // 身份证号
    private String phone;                  // 联系电话
    private LocalDate registerDate;        // 注册日期
    private String member;                 // 会员状态
    private String judge;                  // 信誉度评价

    // 无参构造函数
    public User() {}

    // 带参构造函数
    public User(int userId, String name, String identityId, String phone, 
                LocalDate registerDate, String member, String judge) {
        this.userId = userId;
        this.name = name;
        this.identityId = identityId;
        this.phone = phone;
        this.registerDate = registerDate;
        this.member = member;
        this.judge = judge;
    }

    // Getter和Setter方法
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIdentityId() {
        return identityId;
    }
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }
    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public String getMember() {
        return member;
    }
    public void setMember(String member) {
        this.member = member;
    }

    public String getJudge() {
        return judge;
    }
    public void setJudge(String judge) {
        this.judge = judge;
    }

    @Override
    public String toString() {
        return userId + " - " + name;
    }
}
