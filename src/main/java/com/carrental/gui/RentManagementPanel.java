package com.carrental.gui;

import com.carrental.entity.RentInformation;
import com.carrental.service.RentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 租车管理面板
 * 提供租车相关的管理功能
 */
public class RentManagementPanel extends JPanel {
    private RentService rentService;
    private JTable rentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;

    public RentManagementPanel() {
        this.rentService = new RentService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadRentData();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 创建表格
        String[] columnNames = {"ID", "车辆ID", "用户ID", "员工ID", "租借日期", "归还日期", "支付金额", "退还金额"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        rentTable = new JTable(tableModel);
        rentTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        rentTable.setRowHeight(25);
        rentTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        
        // 设置列宽
        rentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        rentTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        rentTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        rentTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        rentTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        rentTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        rentTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        rentTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // 创建状态选择框
        statusComboBox = new JComboBox<>(new String[]{"全部", "租借中", "已归还"});
        statusComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 顶部搜索面板
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // 中间表格面板
        JScrollPane scrollPane = new JScrollPane(rentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("租车记录"));
        add(scrollPane, BorderLayout.CENTER);
        
        // 底部按钮面板
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建搜索面板
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        searchPanel.add(new JLabel("状态:"));
        searchPanel.add(statusComboBox);
        
        JButton searchButton = new JButton("搜索");
        searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        searchPanel.add(searchButton);
        
        JButton refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRentData();
            }
        });
        searchPanel.add(refreshButton);
        
        return searchPanel;
    }

    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton rentButton = new JButton("新增租车");
        rentButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRentCarDialog();
            }
        });
        buttonPanel.add(rentButton);
        
        JButton returnButton = new JButton("还车");
        returnButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReturnCarDialog();
            }
        });
        buttonPanel.add(returnButton);
        
        JButton viewButton = new JButton("查看详情");
        viewButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewRentDetails();
            }
        });
        buttonPanel.add(viewButton);
        
        JButton contractButton = new JButton("生成合同");
        contractButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        contractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateContract();
            }
        });
        buttonPanel.add(contractButton);
        
        return buttonPanel;
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // 双击表格行查看详情
        rentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewRentDetails();
                }
            }
        });
    }

    /**
     * 加载租车数据
     */
    private void loadRentData() {
        tableModel.setRowCount(0);
        List<RentInformation> rentInfos = rentService.getAllRentInformation();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (RentInformation rentInfo : rentInfos) {
            Object[] row = {
                rentInfo.getRentId(),
                rentInfo.getCarId(),
                rentInfo.getUserId(),
                rentInfo.getStaffId(),
                rentInfo.getRentDate() != null ? rentInfo.getRentDate().format(formatter) : "",
                rentInfo.getReturnDate() != null ? rentInfo.getReturnDate().format(formatter) : "",
                rentInfo.getPayTheAmount(),
                rentInfo.getReturnAmount()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        String status = (String) statusComboBox.getSelectedItem();
        
        List<RentInformation> rentInfos;
        if (status.equals("租借中")) {
            rentInfos = rentService.getCurrentRentals();
        } else if (status.equals("已归还")) {
            rentInfos = rentService.getAllRentInformation().stream()
                    .filter(rent -> rent.getReturnDate() != null)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            rentInfos = rentService.getAllRentInformation();
        }
        
        // 更新表格
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (RentInformation rentInfo : rentInfos) {
            Object[] row = {
                rentInfo.getRentId(),
                rentInfo.getCarId(),
                rentInfo.getUserId(),
                rentInfo.getStaffId(),
                rentInfo.getRentDate() != null ? rentInfo.getRentDate().format(formatter) : "",
                rentInfo.getReturnDate() != null ? rentInfo.getReturnDate().format(formatter) : "",
                rentInfo.getPayTheAmount(),
                rentInfo.getReturnAmount()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 显示租车对话框
     */
    private void showRentCarDialog() {
        new RentCarDialog(this).setVisible(true);
    }

    /**
     * 显示还车对话框
     */
    private void showReturnCarDialog() {
        int selectedRow = rentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要还车的记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int rentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        RentInformation rentInfo = rentService.getAllRentInformation().stream()
                .filter(rent -> rent.getRentId() == rentId)
                .findFirst()
                .orElse(null);
        
        if (rentInfo != null) {
            new ReturnCarDialog(this, rentInfo).setVisible(true);
        }
    }

    /**
     * 查看租车详情
     */
    private void viewRentDetails() {
        int selectedRow = rentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要查看的记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int rentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        RentInformation rentInfo = rentService.getAllRentInformation().stream()
                .filter(rent -> rent.getRentId() == rentId)
                .findFirst()
                .orElse(null);
        
        if (rentInfo != null) {
            new RentDetailsDialog(this, rentInfo).setVisible(true);
        }
    }

    /**
     * 生成合同
     */
    private void generateContract() {
        int selectedRow = rentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要生成合同的记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int rentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        RentInformation rentInfo = rentService.getAllRentInformation().stream()
                .filter(rent -> rent.getRentId() == rentId)
                .findFirst()
                .orElse(null);
        
        if (rentInfo != null) {
            new ContractDialog(this, rentInfo).setVisible(true);
        }
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        loadRentData();
    }
}
