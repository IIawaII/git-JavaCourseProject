package com.carrental.gui;

import com.carrental.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private JPanel chartPanel;


    public FinancialReportPanel() throws IOException {
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

        // 初始化图表面板
        chartPanel = new JPanel(new BorderLayout());  // 给 chartPanel 分配实际的容器
        JLabel chartLabel = new JLabel("图表将在这里显示");
        chartPanel.add(chartLabel, BorderLayout.CENTER);
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

        // 将图表区域添加到面板的下方
        add(chartPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> {
            try {
                loadData();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * 加载数据
     */
    private void loadData() throws IOException {
        loadProfitData();
        loadUnpaidFineData();
        loadStaffCarCountData();
        loadRepairedCarData();
        loadChart(); // 加载图表
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
    private void loadChart() throws IOException {
        System.out.println("加载图表");
        Process process = null;
        try {
            // 运行第一个 Python 脚本（chartplot.py）生成仪表盘图像
            String pythonInterpreterPath = "/opt/anaconda3/bin/python";  // 确保这个路径指向你的 Anaconda 环境
            String chartPlotScriptPath = "python/python_code/chartplot.py";  // chartplot.py 脚本的相对路径
            ProcessBuilder pbChartPlot = new ProcessBuilder(pythonInterpreterPath, chartPlotScriptPath);
            pbChartPlot.redirectErrorStream(true);
            process = pbChartPlot.start();

            // 等待 chartplot.py 执行完成
            process.waitFor();

            // 运行第二个 Python 脚本（staff_manage_car_plot.py）生成饼图
            String staffManageCarPlotScriptPath = "python/python_code/staff_manage_car_plot.py";  // staff_manage_car_plot.py 脚本的相对路径
            ProcessBuilder pbStaffManageCarPlot = new ProcessBuilder(pythonInterpreterPath, staffManageCarPlotScriptPath);
            pbStaffManageCarPlot.redirectErrorStream(true);
            process = pbStaffManageCarPlot.start();

            // 等待 staff_manage_car_plot.py 执行完成
            process.waitFor();

            // 加载仪表盘图图像
            ImageIcon chartIcon = new ImageIcon("python/plot/total_profit_progress.png");  // 相对路径
            Image img = chartIcon.getImage();
            int width = 400;
            int height = 300;
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImg);
            JLabel chartLabel = new JLabel(scaledIcon);

            // 加载饼图图像
            ImageIcon pieChartIcon = new ImageIcon("python/plot/staff_manage_car_plot.png");  // 相对路径
            Image pieImg = pieChartIcon.getImage();
            Image scaledPieImg = pieImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledPieIcon = new ImageIcon(scaledPieImg);
            JLabel pieChartLabel = new JLabel(scaledPieIcon);

            // 创建一个 JPanel 来容纳两个图表
            JPanel imagePanel = new JPanel();
            imagePanel.setLayout(new FlowLayout());  // 使用 FlowLayout 来并排显示
            imagePanel.add(chartLabel);
            imagePanel.add(pieChartLabel);

            // 清除旧的内容并添加新的图表容器
            chartPanel.removeAll();
            chartPanel.add(imagePanel, BorderLayout.CENTER);

            // 更新 UI
            chartPanel.revalidate();
            chartPanel.repaint();

        } catch (IOException | InterruptedException e) {
            System.err.println("执行 Python 脚本失败: " + e.getMessage());
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
