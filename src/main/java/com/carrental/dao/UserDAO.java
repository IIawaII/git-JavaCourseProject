package com.carrental.dao;

import com.carrental.entity.User;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象
 * 负责用户相关的数据库操作
 */
public class UserDAO {
    private DatabaseConnection dbConnection;

    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 添加用户
     * @param user 用户对象
     * @return 是否添加成功
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO user (name, identity_id, phone) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getIdentityId());
            pstmt.setString(3, user.getPhone());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("添加用户失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("删除用户失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE user SET name = ?, identity_id = ?, phone = ?, register_date = ?, member = ?, judge = ? WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getIdentityId());
            pstmt.setString(3, user.getPhone());
            pstmt.setDate(4, Date.valueOf(user.getRegisterDate()));
            pstmt.setString(5, user.getMember());
            pstmt.setString(6, user.getJudge());
            pstmt.setInt(7, user.getUserId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("更新用户失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID查询用户
     * @param userId 用户ID
     * @return 用户对象
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM user WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据身份证号查询用户
     * @param identityId 身份证号
     * @return 用户对象
     */
    public User getUserByIdentityId(String identityId) {
        String sql = "SELECT * FROM user WHERE identity_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, identityId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("根据身份证号查询用户失败: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM user ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("查询所有用户失败: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * 根据会员状态查询用户
     * @param member 会员状态
     * @return 用户列表
     */
    public List<User> getUsersByMember(String member) {
        String sql = "SELECT * FROM user WHERE member = ? ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("根据会员状态查询用户失败: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * 根据信誉度查询用户
     * @param judge 信誉度
     * @return 用户列表
     */
    public List<User> getUsersByJudge(String judge) {
        String sql = "SELECT * FROM user WHERE judge = ? ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, judge);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("根据信誉度查询用户失败: " + e.getMessage());
            e.printStackTrace();
        }

        return users;
    }

    /**
     * 将ResultSet映射为User对象
     * @param rs ResultSet对象
     * @return User对象
     * @throws SQLException SQL异常
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setIdentityId(rs.getString("identity_id"));
        user.setPhone(rs.getString("phone"));

        Date registerDate = rs.getDate("register_date");
        if (registerDate != null) {
            user.setRegisterDate(registerDate.toLocalDate());
        }

        user.setMember(rs.getString("member"));
        user.setJudge(rs.getString("judge"));

        return user;
    }

    public User getUserByPhone(String phone) {
                /*
        这个方法用于用户登陆时判断用户手机号和身份证是否匹配
        要name是因为登陆后在右上角会显示用户名字
         */
        String sql = "SELECT name, identity_id, phone FROM user WHERE phone = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setName(rs.getString("name"));
                user.setIdentityId(rs.getString("identity_id"));
                user.setPhone(rs.getString("phone"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
