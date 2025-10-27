package com.carrental.dao;

import com.carrental.entity.MaintainInformation;
import com.carrental.util.DatabaseConnection;
import com.carrental.util.AppLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 维修信息数据访问对象
 * 负责维修信息相关的数据库操作
 */
public class MaintainInformationDAO {
    private DatabaseConnection dbConnection;

    public MaintainInformationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 添加维修信息
     * @param maintainInfo 维修信息对象
     * @return 是否添加成功
     */
    public boolean addMaintainInformation(MaintainInformation maintainInfo) {
        String sql = "INSERT INTO maintain_information (maintain_id, car_id, maintain_data, maintain_describe, maintain_begin_date, maintain_finish_date, maimtain_cost) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maintainInfo.getMaintainId());
            pstmt.setInt(2, maintainInfo.getCarId());
            pstmt.setDate(3, Date.valueOf(maintainInfo.getMaintainDate()));
            pstmt.setString(4, maintainInfo.getMaintainDescribe());
            pstmt.setDate(5, Date.valueOf(maintainInfo.getMaintainBeginDate()));
            pstmt.setDate(6, Date.valueOf(maintainInfo.getMaintainFinishDate()));
            pstmt.setBigDecimal(7, maintainInfo.getMaintainCost());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            AppLogger.logException("添加维修信息失败: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除维修信息
     * @param maintainId 维修信息ID
     * @return 是否删除成功
     */
    public boolean deleteMaintainInformation(int maintainId) {
        String sql = "DELETE FROM maintain_information WHERE maintain_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maintainId);
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            AppLogger.logException("删除维修信息失败: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 更新维修信息
     * @param maintainInfo 维修信息对象
     * @return 是否更新成功
     */
    public boolean updateMaintainInformation(MaintainInformation maintainInfo) {
        String sql = "UPDATE maintain_information SET car_id = ?, maintain_data = ?, maintain_describe = ?, maintain_begin_date = ?, maintain_finish_date = ?, maimtain_cost = ? WHERE maintain_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maintainInfo.getCarId());
            pstmt.setDate(2, Date.valueOf(maintainInfo.getMaintainDate()));
            pstmt.setString(3, maintainInfo.getMaintainDescribe());
            pstmt.setDate(4, Date.valueOf(maintainInfo.getMaintainBeginDate()));
            pstmt.setDate(5, Date.valueOf(maintainInfo.getMaintainFinishDate()));
            pstmt.setBigDecimal(6, maintainInfo.getMaintainCost());
            pstmt.setInt(7, maintainInfo.getMaintainId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            AppLogger.logException("更新维修信息失败: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * 根据ID查询维修信息
     * @param maintainId 维修信息ID
     * @return 维修信息对象
     */
    public MaintainInformation getMaintainInformationById(int maintainId) {
        String sql = "SELECT * FROM maintain_information WHERE maintain_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maintainId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMaintainInformation(rs);
                }
            }
            
        } catch (SQLException e) {
            AppLogger.logException("查询维修信息失败: " + e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * 根据车辆ID查询维修信息
     * @param carId 车辆ID
     * @return 维修信息列表
     */
    public List<MaintainInformation> getMaintainInformationByCarId(int carId) {
        String sql = "SELECT * FROM maintain_information WHERE car_id = ? ORDER BY maintain_data DESC";
        List<MaintainInformation> maintainList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    maintainList.add(mapResultSetToMaintainInformation(rs));
                }
            }
            
        } catch (SQLException e) {
            AppLogger.logException("根据车辆ID查询维修信息失败: " + e.getMessage(), e);
        }
        
        return maintainList;
    }

    /**
     * 查询所有维修信息
     * @return 维修信息列表
     */
    public List<MaintainInformation> getAllMaintainInformation() {
        String sql = "SELECT * FROM maintain_information ORDER BY maintain_data DESC";
        List<MaintainInformation> maintainList = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                maintainList.add(mapResultSetToMaintainInformation(rs));
            }
            
        } catch (SQLException e) {
            AppLogger.logException("查询所有维修信息失败: " + e.getMessage(), e);
        }
        
        return maintainList;
    }

    /**
     * 获取下一个可用的维修ID
     * @return 下一个维修ID
     */
    public int getNextMaintainId() {
        String sql = "SELECT MAX(maintain_id) FROM maintain_information";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
            
        } catch (SQLException e) {
            AppLogger.logException("获取下一个维修ID失败: " + e.getMessage(), e);
        }
        
        return 1;
    }

    /**
     * 将ResultSet映射为MaintainInformation对象
     * @param rs ResultSet对象
     * @return MaintainInformation对象
     * @throws SQLException SQL异常
     */
    private MaintainInformation mapResultSetToMaintainInformation(ResultSet rs) throws SQLException {
        MaintainInformation maintainInfo = new MaintainInformation();
        maintainInfo.setMaintainId(rs.getInt("maintain_id"));
        maintainInfo.setCarId(rs.getInt("car_id"));
        
        Date maintainDate = rs.getDate("maintain_data");
        if (maintainDate != null) {
            maintainInfo.setMaintainDate(maintainDate.toLocalDate());
        }
        
        maintainInfo.setMaintainDescribe(rs.getString("maintain_describe"));
        
        Date beginDate = rs.getDate("maintain_begin_date");
        if (beginDate != null) {
            maintainInfo.setMaintainBeginDate(beginDate.toLocalDate());
        }
        
        Date finishDate = rs.getDate("maintain_finish_date");
        if (finishDate != null) {
            maintainInfo.setMaintainFinishDate(finishDate.toLocalDate());
        }
        
        maintainInfo.setMaintainCost(rs.getBigDecimal("maimtain_cost"));
        
        return maintainInfo;
    }
}
