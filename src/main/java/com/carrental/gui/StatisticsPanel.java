package com.carrental.gui;

import com.carrental.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

/**
 * 数据统计和分析面板
 * 提供各种数据统计和分析功能
 */
public class StatisticsPanel extends JPanel {
    private DatabaseConnection dbConnection;
    private JTextArea statisticsArea;
    private JButton refreshButton;
    private JComboBox<String> reportTypeCombo;

    public StatisticsPanel() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadStatistics();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 统计类型选择
        String[] reportTypes = {
            "全部统计",
            "车辆统计",
            "用户统计", 
            "员工统计",
            "租车统计",
            "财务统计"
        };
        reportTypeCombo = new JComboBox<>(reportTypes);
        
        // 统计结果显示区域
        statisticsArea = new JTextArea(20, 60);
        statisticsArea.setEditable(false);
        statisticsArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        // 刷新按钮
        refreshButton = new JButton("刷新统计");
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部工具栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("统计类型:"));
        topPanel.add(reportTypeCombo);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);
        
        // 中间统计结果
        JScrollPane scrollPane = new JScrollPane(statisticsArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadStatistics());
        reportTypeCombo.addActionListener(e -> loadStatistics());
    }

    /**
     * 加载统计数据
     */
    private void loadStatistics() {
        String selectedType = (String) reportTypeCombo.getSelectedItem();
        StringBuilder statistics = new StringBuilder();
        
        statistics.append("=== 汽车租赁管理系统 - 数据统计报告 ===\n");
        statistics.append("生成时间: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))).append("\n\n");
        
        switch (selectedType) {
            case "全部统计":
                loadAllStatistics(statistics);
                break;
            case "车辆统计":
                loadCarStatistics(statistics);
                break;
            case "用户统计":
                loadUserStatistics(statistics);
                break;
            case "员工统计":
                loadStaffStatistics(statistics);
                break;
            case "租车统计":
                loadRentStatistics(statistics);
                break;
            case "财务统计":
                loadFinancialStatistics(statistics);
                break;
        }
        
        statisticsArea.setText(statistics.toString());
    }

    /**
     * 加载全部统计
     */
    private void loadAllStatistics(StringBuilder statistics) {
        loadCarStatistics(statistics);
        statistics.append("\n");
        loadUserStatistics(statistics);
        statistics.append("\n");
        loadStaffStatistics(statistics);
        statistics.append("\n");
        loadRentStatistics(statistics);
        statistics.append("\n");
        loadFinancialStatistics(statistics);
    }

    /**
     * 加载车辆统计
     */
    private void loadCarStatistics(StringBuilder statistics) {
        statistics.append("【车辆统计】\n");
        statistics.append("----------------------------------------\n");
        
        try (Connection conn = dbConnection.getConnection()) {
            // 总车辆数
            String sql = "SELECT COUNT(*) FROM car";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.append("总车辆数: ").append(rs.getInt(1)).append(" 辆\n");
                }
            }
            
            // 按状态统计
            sql = "SELECT status, COUNT(*) FROM car GROUP BY status";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                statistics.append("按状态统计:\n");
                while (rs.next()) {
                    statistics.append("  ").append(rs.getString(1)).append(": ").append(rs.getInt(2)).append(" 辆\n");
                }
            }
            
            // 按品牌统计
            sql = "SELECT brand, COUNT(*) FROM car GROUP BY brand ORDER BY COUNT(*) DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                statistics.append("按品牌统计:\n");
                while (rs.next()) {
                    statistics.append("  ").append(rs.getString(1)).append(": ").append(rs.getInt(2)).append(" 辆\n");
                }
            }
            
        } catch (SQLException e) {
            statistics.append("车辆统计加载失败: ").append(e.getMessage()).append("\n");
        }
    }

    /**
     * 加载用户统计
     */
    private void loadUserStatistics(StringBuilder statistics) {
        statistics.append("【用户统计】\n");
        statistics.append("----------------------------------------\n");
        
        try (Connection conn = dbConnection.getConnection()) {
            // 总用户数
            String sql = "SELECT COUNT(*) FROM user";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.append("总用户数: ").append(rs.getInt(1)).append(" 人\n");
                }
            }
            
            // 按会员状态统计
            sql = "SELECT member, COUNT(*) FROM user GROUP BY member";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                statistics.append("按会员状态统计:\n");
                while (rs.next()) {
                    statistics.append("  ").append(rs.getString(1)).append(": ").append(rs.getInt(2)).append(" 人\n");
                }
            }
            
            // 按信誉度统计
            sql = "SELECT judge, COUNT(*) FROM user GROUP BY judge ORDER BY COUNT(*) DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                statistics.append("按信誉度统计:\n");
                while (rs.next()) {
                    statistics.append("  ").append(rs.getString(1)).append(": ").append(rs.getInt(2)).append(" 人\n");
                }
            }
            
        } catch (SQLException e) {
            statistics.append("用户统计加载失败: ").append(e.getMessage()).append("\n");
        }
    }

    /**
     * 加载员工统计
     */
    private void loadStaffStatistics(StringBuilder statistics) {
        statistics.append("【员工统计】\n");
        statistics.append("----------------------------------------\n");
        
        try (Connection conn = dbConnection.getConnection()) {
            // 总员工数
            String sql = "SELECT COUNT(*) FROM staff";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.append("总员工数: ").append(rs.getInt(1)).append(" 人\n");
                }
            }
            
            // 按职位统计
            sql = "SELECT position, COUNT(*) FROM staff GROUP BY position ORDER BY COUNT(*) DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                statistics.append("按职位统计:\n");
                while (rs.next()) {
                    statistics.append("  ").append(rs.getString(1)).append(": ").append(rs.getInt(2)).append(" 人\n");
                }
            }
            
            // 按权限等级统计
            sql = "SELECT role, COUNT(*) FROM staff GROUP BY role ORDER BY role DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                statistics.append("按权限等级统计:\n");
                while (rs.next()) {
                    statistics.append("  等级").append(rs.getInt(1)).append(": ").append(rs.getInt(2)).append(" 人\n");
                }
            }
            
        } catch (SQLException e) {
            statistics.append("员工统计加载失败: ").append(e.getMessage()).append("\n");
        }
    }

    /**
     * 加载租车统计
     */
    private void loadRentStatistics(StringBuilder statistics) {
        statistics.append("【租车统计】\n");
        statistics.append("----------------------------------------\n");
        
        try (Connection conn = dbConnection.getConnection()) {
            // 总租车记录数
            String sql = "SELECT COUNT(*) FROM rent_information";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.append("总租车记录数: ").append(rs.getInt(1)).append(" 条\n");
                }
            }
            
            // 总租车收入
            sql = "SELECT SUM(pay_the_amount) FROM rent_information";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalIncome = rs.getBigDecimal(1);
                    statistics.append("总租车收入: ").append(totalIncome != null ? totalIncome : "0").append(" 元\n");
                }
            }
            
            // 平均租车金额
            sql = "SELECT AVG(pay_the_amount) FROM rent_information";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal avgAmount = rs.getBigDecimal(1);
                    statistics.append("平均租车金额: ").append(avgAmount != null ? avgAmount.setScale(2, BigDecimal.ROUND_HALF_UP) : "0").append(" 元\n");
                }
            }
            
        } catch (SQLException e) {
            statistics.append("租车统计加载失败: ").append(e.getMessage()).append("\n");
        }
    }

    /**
     * 加载财务统计
     */
    private void loadFinancialStatistics(StringBuilder statistics) {
        statistics.append("【财务统计】\n");
        statistics.append("----------------------------------------\n");
        
        try (Connection conn = dbConnection.getConnection()) {
            // 损坏记录统计
            String sql = "SELECT COUNT(*) FROM damage_information";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.append("损坏记录数: ").append(rs.getInt(1)).append(" 条\n");
                }
            }
            
            // 维修记录统计
            sql = "SELECT COUNT(*) FROM maintain_information";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.append("维修记录数: ").append(rs.getInt(1)).append(" 条\n");
                }
            }
            
            // 总维修费用
            sql = "SELECT SUM(maimtain_cost) FROM maintain_information";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalCost = rs.getBigDecimal(1);
                    statistics.append("总维修费用: ").append(totalCost != null ? totalCost : "0").append(" 元\n");
                }
            }
            
            // 违章罚款统计
            sql = "SELECT COUNT(*) FROM traffic_fine";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    statistics.append("违章罚款记录数: ").append(rs.getInt(1)).append(" 条\n");
                }
            }
            
            // 未交罚款总额
            sql = "SELECT SUM(fine) FROM traffic_fine WHERE fine_state = '未交'";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal unpaidFine = rs.getBigDecimal(1);
                    statistics.append("未交罚款总额: ").append(unpaidFine != null ? unpaidFine : "0").append(" 元\n");
                }
            }
            
        } catch (SQLException e) {
            statistics.append("财务统计加载失败: ").append(e.getMessage()).append("\n");
        }
    }
}
