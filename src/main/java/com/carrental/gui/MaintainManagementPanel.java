package com.carrental.gui;

import com.carrental.dao.CarDAO;
import com.carrental.dao.MaintainInformationDAO;
import com.carrental.entity.Car;
import com.carrental.entity.MaintainInformation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 维修管理面板
 * 负责车辆维修信息的管理
 */
public class MaintainManagementPanel extends JPanel {
    private MaintainInformationDAO maintainDAO;
    private CarDAO carDAO;
    private JTable maintainTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> carFilterCombo;

    public MaintainManagementPanel() {
        this.maintainDAO = new MaintainInformationDAO();
        this.carDAO = new CarDAO();
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
        String[] columnNames = {"维修ID", "车辆ID", "车牌号", "报修日期", "维修描述", "开始日期", "结束日期", "维修费用"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        maintainTable = new JTable(tableModel);
        maintainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        maintainTable.getTableHeader().setReorderingAllowed(false);

        // 创建按钮
        addButton = new JButton("添加维修记录");
        editButton = new JButton("修改维修记录");
        deleteButton = new JButton("删除维修记录");
        refreshButton = new JButton("刷新");

        // 创建筛选组件
        carFilterCombo = new JComboBox<>();
        carFilterCombo.addItem("全部车辆");
        
        // 加载车辆列表
        loadCarList();
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
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // 顶部工具栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("车辆:"));
        topPanel.add(carFilterCombo);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // 中间表格
        JScrollPane scrollPane = new JScrollPane(maintainTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
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
        addButton.addActionListener(e -> showAddMaintainDialog());
        editButton.addActionListener(e -> showEditMaintainDialog());
        deleteButton.addActionListener(e -> deleteMaintainRecord());
        refreshButton.addActionListener(e -> loadData());
        carFilterCombo.addActionListener(e -> loadData());
    }

    /**
     * 加载数据
     */
    private void loadData() {
        tableModel.setRowCount(0);
        
        List<MaintainInformation> maintainList;
        String selectedCar = (String) carFilterCombo.getSelectedItem();
        
        if ("全部车辆".equals(selectedCar)) {
            maintainList = maintainDAO.getAllMaintainInformation();
        } else {
            int carId = extractCarIdFromCombo(selectedCar);
            maintainList = maintainDAO.getMaintainInformationByCarId(carId);
        }
        
        for (MaintainInformation maintain : maintainList) {
            Car car = carDAO.getCarById(maintain.getCarId());
            String licensePlate = car != null ? car.getLicensePlateNumber() : "未知";
            
            Object[] row = {
                maintain.getMaintainId(),
                maintain.getCarId(),
                licensePlate,
                maintain.getMaintainDate(),
                maintain.getMaintainDescribe(),
                maintain.getMaintainBeginDate(),
                maintain.getMaintainFinishDate(),
                maintain.getMaintainCost()
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
     * 显示添加维修记录对话框
     */
    private void showAddMaintainDialog() {
        MaintainDialog dialog = new MaintainDialog(null, carDAO.getAllCars(), maintainDAO.getNextMaintainId());
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadData();
        }
    }

    /**
     * 显示修改维修记录对话框
     */
    private void showEditMaintainDialog() {
        int selectedRow = maintainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要修改的维修记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int maintainId = (Integer) tableModel.getValueAt(selectedRow, 0);
        MaintainInformation maintain = maintainDAO.getMaintainInformationById(maintainId);
        
        if (maintain != null) {
            MaintainDialog dialog = new MaintainDialog(maintain, carDAO.getAllCars(), maintainId);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadData();
            }
        }
    }

    /**
     * 删除维修记录
     */
    private void deleteMaintainRecord() {
        int selectedRow = maintainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的维修记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int maintainId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String maintainDescribe = (String) tableModel.getValueAt(selectedRow, 4);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要删除维修记录吗？\n维修描述: " + maintainDescribe, 
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (maintainDAO.deleteMaintainInformation(maintainId)) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
