package com.carrental.service;

import com.carrental.dao.StaffDAO;
import com.carrental.dao.UserDAO;
import com.carrental.entity.Staff;
import com.carrental.entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * 用户管理服务类
 * 提供用户和员工相关的业务逻辑
 */
public class UserService {
    private StaffDAO staffDAO;
    private UserDAO userDAO;

    public UserService() {
        this.staffDAO = new StaffDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * 员工登录
     * @param name 员工姓名
     * @param password 密码
     * @return 员工对象，登录失败返回null
     */
    public Staff login(String name, String password) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("用户名不能为空");
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            System.err.println("密码不能为空");
            return null;
        }

        // 直接执行登录验证，利用MySQL的锁机制
        return staffDAO.login(name.trim(), password.trim());
    }
    /**
     * 员工登出
     * @param name 员工姓名
     * @return 是否登出成功
     */
    public boolean logout(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("用户名不能为空");
            return false;
        }

        return staffDAO.logout(name.trim());
    }

    /**
     * 添加员工
     * @param staff 员工对象
     * @return 是否添加成功
     */
    public boolean addStaff(Staff staff) {
        if (!validateStaff(staff)) {
            return false;
        }

        return staffDAO.addStaff(staff);
    }

    /**
     * 删除员工
     * @param staffId 员工ID
     * @return 是否删除成功
     */
    public boolean deleteStaff(int staffId) {
        return staffDAO.deleteStaff(staffId);
    }

    /**
     * 更新员工信息
     * @param staff 员工对象
     * @return 是否更新成功
     */
    public boolean updateStaff(Staff staff) {
        if (!validateStaff(staff)) {
            return false;
        }

        return staffDAO.updateStaff(staff);
    }

    /**
     * 根据ID查询员工
     * @param staffId 员工ID
     * @return 员工对象
     */
    public Staff getStaffById(int staffId) {
        return staffDAO.getStaffById(staffId);
    }

    /**
     * 查询所有员工
     * @return 员工列表
     */
    public List<Staff> getAllStaff() {
        return staffDAO.getAllStaff();
    }

    /**
     * 根据职位查询员工
     * @param position 职位
     * @return 员工列表
     */
    public List<Staff> getStaffByPosition(String position) {
        return staffDAO.getStaffByPosition(position);
    }

    /**
     * 添加用户
     * @param user 用户对象
     * @return 是否添加成功
     */
    public boolean addUser(User user) {
        if (!validateUser(user)) {
            return false;
        }

        // 检查身份证号是否已存在
        User existingUser = userDAO.getUserByIdentityId(user.getIdentityId());
        if (existingUser != null) {
            System.err.println("身份证号已存在");
            return false;
        }

        return userDAO.addUser(user);
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    public boolean updateUser(User user) {
        if (!validateUser(user)) {
            return false;
        }

        return userDAO.updateUser(user);
    }

    /**
     * 根据ID查询用户
     * @param userId 用户ID
     * @return 用户对象
     */
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    /**
     * 根据身份证号查询用户
     * @param identityId 身份证号
     * @return 用户对象
     */
    public User getUserByIdentityId(String identityId) {
        return userDAO.getUserByIdentityId(identityId);
    }

    /**
     * 查询所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * 根据会员状态查询用户
     * @param member 会员状态
     * @return 用户列表
     */
    public List<User> getUsersByMember(String member) {
        return userDAO.getUsersByMember(member);
    }

    /**
     * 根据信誉度查询用户
     * @param judge 信誉度
     * @return 用户列表
     */
    public List<User> getUsersByJudge(String judge) {
        return userDAO.getUsersByJudge(judge);
    }

    /**
     * 验证员工信息
     * @param staff 员工对象
     * @return 是否有效
     */
    private boolean validateStaff(Staff staff) {
        if (staff.getName() == null || staff.getName().trim().isEmpty()) {
            System.err.println("员工姓名不能为空");
            return false;
        }

        if (staff.getPhone() == null || staff.getPhone().trim().isEmpty()) {
            System.err.println("联系电话不能为空");
            return false;
        }

        if (staff.getPosition() == null || staff.getPosition().trim().isEmpty()) {
            System.err.println("职位不能为空");
            return false;
        }

        if (staff.getPassword() == null || staff.getPassword().trim().isEmpty()) {
            System.err.println("密码不能为空");
            return false;
        }

        if (staff.getEntryDate() == null || staff.getEntryDate().isAfter(LocalDate.now())) {
            System.err.println("入职日期不能为空或晚于当前日期");
            return false;
        }

        if (staff.getRole() < 1 || staff.getRole() > 9) {
            System.err.println("权限等级必须在1-9之间");
            return false;
        }

        return true;
    }

    /**
     * 验证用户信息
     * @param user 用户对象
     * @return 是否有效
     */
    private boolean validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            System.err.println("用户姓名不能为空");
            return false;
        }

        if (user.getIdentityId() == null || user.getIdentityId().trim().isEmpty()) {
            System.err.println("身份证号不能为空");
            return false;
        }

        if (user.getIdentityId().length() != 18) {
            System.err.println("身份证号必须为18位");
            return false;
        }

        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            System.err.println("联系电话不能为空");
            return false;
        }

        if (user.getRegisterDate() == null || user.getRegisterDate().isAfter(LocalDate.now())) {
            System.err.println("注册日期不能为空或晚于当前日期");
            return false;
        }

        return true;
    }
    /**
     * 用户登录
     * @param phone 用户手机号（当用户名）
     * @param identityId 身份证号（当密码）
     * @return 用户对象，登录失败返回 null
     */
    public User userLogin(String phone, String identityId) {
        if (phone == null || phone.trim().isEmpty()) {
            System.err.println("手机号不能为空");
            return null;
        }
        if (identityId == null || identityId.trim().isEmpty()) {
            System.err.println("身份证号不能为空");
            return null;
        }

        // 调用 DAO 查询用户
        User user = userDAO.getUserByPhone(phone.trim());
        if (user == null) {
            System.err.println("用户不存在");
            return null;
        }

        if (!user.getIdentityId().equals(identityId.trim())) {
            System.err.println("身份证号不匹配");
            return null;
        }

        return user; // 登录成功
    }
    // 根据手机号查询用户
    public User getUserByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            System.err.println("手机号不能为空");
            return null;
        }
        return userDAO.getUserByPhone(phone.trim());
    }
}
