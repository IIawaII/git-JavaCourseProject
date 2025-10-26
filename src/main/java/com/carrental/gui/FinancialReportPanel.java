package com.carrental.gui;

import com.carrental.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * 财务报表面板
 * 提供财务报表相关功能
 */
public class FinancialReportPanel extends JPanel {
    private DatabaseConnection dbConnection;
    private JTable profitTable;
    private JTable unpaidFineTable;
    private JTable staffCarCountTable;
    private JTable repairedCarTable;
    private JButton refreshButton;
    private JTabbedPane reportTabbedPane;

    public FinancialReportPanel() {
        this.dbConnection = DatabaseConnection.getInstance();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 创建标签页
        reportTabbedPane = new JTabbedPane();
        
        // 利润分析表
        String[] profitColumns = {"车辆编号", "用户支付金额", "归还用户金额", "用户造成的损坏", "利润"};
        DefaultTableModel profitModel = new DefaultTableModel(profitColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        profitTable = new JTable(profitModel);
        
        // 未交罚款表
        String[] unpaidFineColumns = {"名字", "罚款金额"};
        DefaultTableModel unpaidFineModel = new DefaultTableModel(unpaidFineColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        unpaidFineTable = new JTable(unpaidFineModel);
        
        // 员工管理车辆数量表
        String[] staffCarCountColumns = {"管理员工", "管理车辆数量"};
        DefaultTableModel staffCarCountModel = new DefaultTableModel(staffCarCountColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffCarCountTable = new JTable(staffCarCountModel);
        
        // 已维修车辆表
        String[] repairedCarColumns = {"车牌号", "型号", "颜色", "状态", "日租金", "押金"};
        DefaultTableModel repairedCarModel = new DefaultTableModel(repairedCarColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        repairedCarTable = new JTable(repairedCarModel);
        
        // 刷新按钮
        refreshButton = new JButton("刷新数据");
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部工具栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);
        
        // 中间标签页
        reportTabbedPane.addTab("利润分析", new JScrollPane(profitTable));
        reportTabbedPane.addTab("未交罚款", new JScrollPane(unpaidFineTable));
        reportTabbedPane.addTab("员工管理统计", new JScrollPane(staffCarCountTable));
        reportTabbedPane.addTab("已维修车辆", new JScrollPane(repairedCarTable));
        
        add(reportTabbedPane, BorderLayout.CENTER);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadData());
    }

    /**
     * 加载数据
     */
    private void loadData() {
        loadProfitData();
        loadUnpaidFineData();
        loadStaffCarCountData();
        loadRepairedCarData();
    }

    /**
     * 加载利润分析数据
     */
    private void loadProfitData() {
        DefaultTableModel model = (DefaultTableModel) profitTable.getModel();
        model.setRowCount(0);
        
        String sql = "SELECT * FROM profit_view";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("车辆编号"),
                    rs.getBigDecimal("用户支付金额"),
                    rs.getBigDecimal("归还用户金额"),
                    rs.getBigDecimal("用户造成的损坏"),
                    rs.getBigDecimal("利润")
                };
                model.addRow(row);
            }
            
        } catch (SQLException e) {
            System.err.println("加载利润分析数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 加载未交罚款数据
     */
    private void loadUnpaidFineData() {
        DefaultTableModel model = (DefaultTableModel) unpaidFineTable.getModel();
        model.setRowCount(0);
        
        String sql = "SELECT * FROM fine_not_paied";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("名字"),
                    rs.getBigDecimal("罚款金额")
                };
                model.addRow(row);
            }
            
        } catch (SQLException e) {
            System.err.println("加载未交罚款数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 加载员工管理车辆数量数据
     */
    private void loadStaffCarCountData() {
        DefaultTableModel model = (DefaultTableModel) staffCarCountTable.getModel();
        model.setRowCount(0);
        
        String sql = "SELECT * FROM staff_car_count";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("管理员工"),
                    rs.getInt("管理车辆数量")
                };
                model.addRow(row);
            }
            
        } catch (SQLException e) {
            System.err.println("加载员工管理车辆数量数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 加载已维修车辆数据
     */
    private void loadRepairedCarData() {
        DefaultTableModel model = (DefaultTableModel) repairedCarTable.getModel();
        model.setRowCount(0);
        
        String sql = "SELECT * FROM repaired_car";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("车牌号"),
                    rs.getString("型号"),
                    rs.getString("颜色"),
                    rs.getString("状态"),
                    rs.getBigDecimal("日租金"),
                    rs.getString("押金")
                };
                model.addRow(row);
            }
            
        } catch (SQLException e) {
            System.err.println("加载已维修车辆数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
