package com.carrental.gui;

import com.carrental.entity.Car;
import com.carrental.entity.Staff;
import com.carrental.entity.User;
import com.carrental.service.CarService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 车辆管理面板
 * 提供车辆的增删改查功能
 */
public class CarManagementPanelForUser extends JPanel {
    private CarService carService;
    private JTable carTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> brandComboBox;
    private String userPermit;

    public CarManagementPanelForUser() {
        this.carService = new CarService();
        this.userPermit = userPermit;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadCarData();
        setButtonState();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 创建表格
        String[] columnNames = {"ID", "车牌号", "品牌", "型号", "颜色", "状态", "日租金", "押金", "购买日期"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        carTable = new JTable(tableModel);
        carTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        carTable.setRowHeight(25);
        carTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));

        // 设置列宽
        carTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        carTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        carTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        carTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        carTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        carTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        carTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        carTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        carTable.getColumnModel().getColumn(8).setPreferredWidth(100);

        // 创建搜索组件
        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        statusComboBox = new JComboBox<>(new String[]{"全部", "空闲", "已借出", "维修中"});
        statusComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        brandComboBox = new JComboBox<>(new String[]{"全部", "本田", "奔驰", "劳斯莱斯", "宝马", "奥迪", "丰田"});
        brandComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));
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
        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("车辆列表"));
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

        searchPanel.add(new JLabel("搜索:"));
        searchPanel.add(searchField);

        searchPanel.add(new JLabel("状态:"));
        searchPanel.add(statusComboBox);

        searchPanel.add(new JLabel("品牌:"));
        searchPanel.add(brandComboBox);

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
                loadCarData();
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

        JButton viewButton = new JButton("查看详情");
        viewButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCarDetails();
            }
        });
        buttonPanel.add(viewButton);

        return buttonPanel;
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // 双击表格行查看详情
        carTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewCarDetails();
                }
            }
        });
    }

    /**
     * 加载车辆数据
     */
    private void loadCarData() {
        tableModel.setRowCount(0);
        List<Car> cars = carService.getAllCars();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Car car : cars) {
            Object[] row = {
                    car.getCarId(),
                    car.getLicensePlateNumber(),
                    car.getBrand(),
                    car.getModel(),
                    car.getColor(),
                    car.getStatus(),
                    car.getRent(),
                    car.getDeposit(),
                    car.getPurchaseDate() != null ? car.getPurchaseDate().format(formatter) : ""
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        String searchText = searchField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();
        String brand = (String) brandComboBox.getSelectedItem();

        List<Car> cars = carService.getAllCars();

        // 过滤结果
        cars = cars.stream()
                .filter(car -> searchText.isEmpty() ||
                        car.getLicensePlateNumber().contains(searchText) ||
                        car.getBrand().contains(searchText) ||
                        car.getModel().contains(searchText))
                .filter(car -> status.equals("全部") || car.getStatus().equals(status))
                .filter(car -> brand.equals("全部") || car.getBrand().equals(brand))
                .collect(java.util.stream.Collectors.toList());

        // 更新表格
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Car car : cars) {
            Object[] row = {
                    car.getCarId(),
                    car.getLicensePlateNumber(),
                    car.getBrand(),
                    car.getModel(),
                    car.getColor(),
                    car.getStatus(),
                    car.getRent(),
                    car.getDeposit(),
                    car.getPurchaseDate() != null ? car.getPurchaseDate().format(formatter) : ""
            };
            tableModel.addRow(row);
        }
    }

    /**
     * 查看车辆详情
     */
    private void viewCarDetails() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要查看的车辆", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int carId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Car car = carService.getCarById(carId);
        if (car != null) {
            new CarDetailsDialog(this, car).setVisible(true);
        }
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        loadCarData();
    }
    private void setButtonState() {
        // 如果用户是普通用户（customer），禁用添加、修改、删除按钮
        if ("customer".equals(userPermit)) {
            for (Component comp : getComponentsInPanel()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    if ("添加车辆".equals(button.getText()) ||
                            "修改车辆".equals(button.getText()) ||
                            "删除车辆".equals(button.getText())) {
                        button.setEnabled(false);
                    }
                }
            }
        }
    }

    private Component[] getComponentsInPanel() {
        return ((JPanel) this.getComponent(2)).getComponents(); // 获取按钮面板的组件
    }
}
