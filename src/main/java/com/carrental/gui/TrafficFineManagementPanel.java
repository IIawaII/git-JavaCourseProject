package com.carrental.gui;

import com.carrental.dao.CarDAO;
import com.carrental.dao.TrafficFineDAO;
import com.carrental.dao.UserDAO;
import com.carrental.entity.Car;
import com.carrental.entity.TrafficFine;
import com.carrental.entity.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 违章罚款管理面板
 * 负责违章罚款信息的管理
 */
public class TrafficFineManagementPanel extends JPanel {
    private TrafficFineDAO trafficFineDAO;
    private CarDAO carDAO;
    private UserDAO userDAO;
    private JTable fineTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> stateFilterCombo;
    private JComboBox<String> carFilterCombo;
    private JComboBox<String> userFilterCombo;

    public TrafficFineManagementPanel() {
        this.trafficFineDAO = new TrafficFineDAO();
        this.carDAO = new CarDAO();
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 创建表格
        String[] columnNames = {"罚款ID", "车辆ID", "车牌号", "用户ID", "用户姓名", "违规日期", "违规地点", "罚款金额", "罚款状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        fineTable = new JTable(tableModel);
        fineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fineTable.getTableHeader().setReorderingAllowed(false);

        // 创建按钮
        addButton = new JButton("添加罚款记录");
        editButton = new JButton("修改罚款记录");
        deleteButton = new JButton("删除罚款记录");
        refreshButton = new JButton("刷新");

        // 创建筛选组件
        stateFilterCombo = new JComboBox<>(new String[]{"全部", "已交", "未交"});
        carFilterCombo = new JComboBox<>();
        carFilterCombo.addItem("全部车辆");
        userFilterCombo = new JComboBox<>();
        userFilterCombo.addItem("全部用户");
        
        // 加载车辆和用户列表
        loadCarList();
        loadUserList();
    }

    /**
     * 加载车辆列表到筛选框
     */
    private void loadCarList() {
        List<Car> cars = carDAO.getAllCars();
        for (Car car : cars) {
            carFilterCombo.addItem(car.getLicensePlateNumber() + " (ID:" + car.getCarId() + ")");
        }
    }

    /**
     * 加载用户列表到筛选框
     */
    private void loadUserList() {
        List<User> users = userDAO.getAllUsers();
        for (User user : users) {
            userFilterCombo.addItem(user.getName() + " (ID:" + user.getUserId() + ")");
        }
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // 顶部工具栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("罚款状态:"));
        topPanel.add(stateFilterCombo);
        topPanel.add(new JLabel("车辆:"));
        topPanel.add(carFilterCombo);
        topPanel.add(new JLabel("用户:"));
        topPanel.add(userFilterCombo);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // 中间表格
        JScrollPane scrollPane = new JScrollPane(fineTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        addButton.addActionListener(e -> showAddFineDialog());
        editButton.addActionListener(e -> showEditFineDialog());
        deleteButton.addActionListener(e -> deleteFineRecord());
        refreshButton.addActionListener(e -> loadData());
        stateFilterCombo.addActionListener(e -> loadData());
        carFilterCombo.addActionListener(e -> loadData());
        userFilterCombo.addActionListener(e -> loadData());
    }

    /**
     * 加载数据
     */
    private void loadData() {
        tableModel.setRowCount(0);
        
        List<TrafficFine> fineList;
        String selectedState = (String) stateFilterCombo.getSelectedItem();
        String selectedCar = (String) carFilterCombo.getSelectedItem();
        String selectedUser = (String) userFilterCombo.getSelectedItem();
        
        if ("全部".equals(selectedState) && "全部车辆".equals(selectedCar) && "全部用户".equals(selectedUser)) {
            fineList = trafficFineDAO.getAllTrafficFine();
        } else {
            // 需要实现组合筛选
            fineList = trafficFineDAO.getAllTrafficFine();
            fineList = fineList.stream()
                    .filter(f -> "全部".equals(selectedState) || selectedState.equals(f.getFineState()))
                    .filter(f -> "全部车辆".equals(selectedCar) || extractCarIdFromCombo(selectedCar) == f.getCarId())
                    .filter(f -> "全部用户".equals(selectedUser) || extractUserIdFromCombo(selectedUser) == f.getUserId())
                    .collect(java.util.stream.Collectors.toList());
        }
        
        for (TrafficFine fine : fineList) {
            Car car = carDAO.getCarById(fine.getCarId());
            User user = userDAO.getUserById(fine.getUserId());
            String licensePlate = car != null ? car.getLicensePlateNumber() : "未知";
            String userName = user != null ? user.getName() : "未知";
            
            Object[] row = {
                fine.getFineId(),
                fine.getCarId(),
                licensePlate,
                fine.getUserId(),
                userName,
                fine.getViolationDate(),
                fine.getOffendingLocation(),
                fine.getFine(),
                fine.getFineState()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 从下拉框文本中提取车辆ID
     */
    private int extractCarIdFromCombo(String comboText) {
        if (comboText.contains("(ID:")) {
            String idPart = comboText.substring(comboText.indexOf("(ID:") + 4);
            idPart = idPart.substring(0, idPart.indexOf(")"));
            return Integer.parseInt(idPart);
        }
        return -1;
    }

    /**
     * 从下拉框文本中提取用户ID
     */
    private int extractUserIdFromCombo(String comboText) {
        if (comboText.contains("(ID:")) {
            String idPart = comboText.substring(comboText.indexOf("(ID:") + 4);
            idPart = idPart.substring(0, idPart.indexOf(")"));
            return Integer.parseInt(idPart);
        }
        return -1;
    }

    /**
     * 显示添加罚款记录对话框
     */
    private void showAddFineDialog() {
        TrafficFineDialog dialog = new TrafficFineDialog(null, carDAO.getAllCars(), userDAO.getAllUsers());
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadData();
        }
    }

    /**
     * 显示修改罚款记录对话框
     */
    private void showEditFineDialog() {
        int selectedRow = fineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要修改的罚款记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int fineId = (Integer) tableModel.getValueAt(selectedRow, 0);
        TrafficFine fine = trafficFineDAO.getTrafficFineById(fineId);
        
        if (fine != null) {
            TrafficFineDialog dialog = new TrafficFineDialog(fine, carDAO.getAllCars(), userDAO.getAllUsers());
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadData();
            }
        }
    }

    /**
     * 删除罚款记录
     */
    private void deleteFineRecord() {
        int selectedRow = fineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的罚款记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int fineId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String offendingLocation = (String) tableModel.getValueAt(selectedRow, 6);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要删除罚款记录吗？\n违规地点: " + offendingLocation, 
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (trafficFineDAO.deleteTrafficFine(fineId)) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
