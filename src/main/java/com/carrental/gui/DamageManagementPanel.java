package com.carrental.gui;

import com.carrental.dao.CarDAO;
import com.carrental.dao.DamageInformationDAO;
import com.carrental.entity.Car;
import com.carrental.entity.DamageInformation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * 损坏管理面板
 * 负责车辆损坏信息的管理
 */
public class DamageManagementPanel extends JPanel {
    private DamageInformationDAO damageDAO;
    private CarDAO carDAO;
    private JTable damageTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> stateFilterCombo;
    private JComboBox<Object> carFilterCombo;

    public DamageManagementPanel() {
        this.damageDAO = new DamageInformationDAO();
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
        String[] columnNames = {"损坏ID", "车辆ID", "车牌号", "损坏日期", "损坏描述", "维修状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        damageTable = new JTable(tableModel);
        damageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        damageTable.getTableHeader().setReorderingAllowed(false);
    // 隐藏损坏ID和车辆ID列（保留在模型中以便编辑/删除时使用）
    damageTable.getColumnModel().getColumn(0).setMinWidth(0);
    damageTable.getColumnModel().getColumn(0).setMaxWidth(0);
    damageTable.getColumnModel().getColumn(0).setPreferredWidth(0);
    damageTable.getColumnModel().getColumn(1).setMinWidth(0);
    damageTable.getColumnModel().getColumn(1).setMaxWidth(0);
    damageTable.getColumnModel().getColumn(1).setPreferredWidth(0);

        // 创建按钮
        addButton = new JButton("添加损坏记录");
        editButton = new JButton("修改损坏记录");
        deleteButton = new JButton("删除损坏记录");
        refreshButton = new JButton("刷新");

        // 创建筛选组件
        stateFilterCombo = new JComboBox<>(new String[]{"全部", "已维修", "未维修"});
        carFilterCombo = new JComboBox<>();
        carFilterCombo.addItem("全部车辆");
        carFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof com.carrental.entity.Car) {
                    setText(((com.carrental.entity.Car) value).getLicensePlateNumber());
                }
                return this;
            }
        });

        // 加载车辆列表
        loadCarList();
    }

    /**
     * 加载车辆列表到筛选框
     */
    private void loadCarList() {
        List<Car> cars = carDAO.getAllCars();
        // 保持第一个是 "全部车辆"
        for (Car car : cars) {
            carFilterCombo.addItem(car);
        }
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // 顶部工具栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("维修状态:"));
        topPanel.add(stateFilterCombo);
        topPanel.add(new JLabel("车辆:"));
        topPanel.add(carFilterCombo);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // 中间表格
        JScrollPane scrollPane = new JScrollPane(damageTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
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
        addButton.addActionListener(e -> showAddDamageDialog());
        editButton.addActionListener(e -> showEditDamageDialog());
        deleteButton.addActionListener(e -> deleteDamageRecord());
        refreshButton.addActionListener(e -> loadData());
        stateFilterCombo.addActionListener(e -> loadData());
        carFilterCombo.addActionListener(e -> loadData());
    }

    /**
     * 加载数据
     */
    private void loadData() {
        tableModel.setRowCount(0);
        
        List<DamageInformation> damageList;
    String selectedState = (String) stateFilterCombo.getSelectedItem();
    Object selectedCar = carFilterCombo.getSelectedItem();
        
        if ("全部".equals(selectedState) && "全部车辆".equals(selectedCar)) {
            damageList = damageDAO.getAllDamageInformation();
        } else if (!"全部".equals(selectedState) && "全部车辆".equals(selectedCar)) {
            damageList = damageDAO.getDamageInformationByState(selectedState);
        } else if ("全部".equals(selectedState) && !(selectedCar instanceof String)) {
            int carId = extractCarIdFromCombo(selectedCar);
            damageList = damageDAO.getDamageInformationByCarId(carId);
        } else {
            // 需要实现组合筛选
            damageList = damageDAO.getAllDamageInformation();
            damageList = damageList.stream()
                    .filter(d -> "全部".equals(selectedState) || selectedState.equals(d.getDamageState()))
                    .filter(d -> (selectedCar instanceof String && "全部车辆".equals(selectedCar)) || (selectedCar instanceof Car && extractCarIdFromCombo(selectedCar) == d.getCarId()))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        for (DamageInformation damage : damageList) {
            Car car = carDAO.getCarById(damage.getCarId());
            String licensePlate = car != null ? car.getLicensePlateNumber() : "未知";
            
            Object[] row = {
                damage.getDamageId(),
                damage.getCarId(),
                licensePlate,
                damage.getDamageDate(),
                damage.getDamageDescribe(),
                damage.getDamageState()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 从下拉框文本中提取车辆ID
     */
    private int extractCarIdFromCombo(Object comboItem) {
        if (comboItem instanceof Car) {
            return ((Car) comboItem).getCarId();
        }
        return -1;
    }

    /**
     * 显示添加损坏记录对话框
     */
    private void showAddDamageDialog() {
        DamageDialog dialog = new DamageDialog(null, carDAO.getAllCars());
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadData();
        }
    }

    /**
     * 显示修改损坏记录对话框
     */
    private void showEditDamageDialog() {
        int selectedRow = damageTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要修改的损坏记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int damageId = (Integer) tableModel.getValueAt(selectedRow, 0);
        DamageInformation damage = damageDAO.getDamageInformationById(damageId);
        
        if (damage != null) {
            DamageDialog dialog = new DamageDialog(damage, carDAO.getAllCars());
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                loadData();
            }
        }
    }

    /**
     * 删除损坏记录
     */
    private void deleteDamageRecord() {
        int selectedRow = damageTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的损坏记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int damageId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String damageDescribe = (String) tableModel.getValueAt(selectedRow, 4);
        
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要删除损坏记录吗？\n损坏描述: " + damageDescribe, 
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (damageDAO.deleteDamageInformation(damageId)) {
                JOptionPane.showMessageDialog(this, "删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
