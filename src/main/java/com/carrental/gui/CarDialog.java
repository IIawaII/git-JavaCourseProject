package com.carrental.gui;

import com.carrental.entity.Car;
import com.carrental.service.CarService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 车辆信息对话框
 * 用于添加和修改车辆信息
 */
public class CarDialog extends JDialog {
    private Car car;
    private CarService carService;
    private JTextField licensePlateField;
    private JTextField brandField;
    private JTextField modelField;
    private JTextField colorField;
    private JComboBox<String> statusComboBox;
    private JTextField rentField;
    private JTextField depositField;
    private JTextField purchaseDateField;
    private JButton saveButton;
    private JButton cancelButton;
    private CarManagementPanel parentPanel;

    public CarDialog(CarManagementPanel parent, Car car) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), car == null ? "添加车辆" : "修改车辆", true);
        this.parentPanel = parent;
        this.car = car;
        this.carService = new CarService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        
        if (car != null) {
            loadCarData();
        }
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        licensePlateField = new JTextField(20);
        brandField = new JTextField(20);
        modelField = new JTextField(20);
        colorField = new JTextField(20);
        statusComboBox = new JComboBox<>(new String[]{"空闲", "已借出", "维修中"});
        rentField = new JTextField(20);
        depositField = new JTextField(20);
        purchaseDateField = new JTextField(20);
        
        saveButton = new JButton("保存");
        cancelButton = new JButton("取消");
        
        // 设置字体
        Font font = new Font("微软雅黑", Font.PLAIN, 12);
        licensePlateField.setFont(font);
        brandField.setFont(font);
        modelField.setFont(font);
        colorField.setFont(font);
        statusComboBox.setFont(font);
        rentField.setFont(font);
        depositField.setFont(font);
        purchaseDateField.setFont(font);
        saveButton.setFont(font);
        cancelButton.setFont(font);
        
        // 设置提示文本
        purchaseDateField.setToolTipText("格式: yyyy-MM-dd (例如: 2024-01-01)");
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 车牌号
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("车牌号:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(licensePlateField, gbc);
        
        // 品牌
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("品牌:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(brandField, gbc);
        
        // 型号
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("型号:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(modelField, gbc);
        
        // 颜色
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("颜色:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(colorField, gbc);
        
        // 状态
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("状态:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(statusComboBox, gbc);
        
        // 日租金
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("日租金:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(rentField, gbc);
        
        // 押金
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("押金:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(depositField, gbc);
        
        // 购买日期
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("购买日期:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(purchaseDateField, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCar();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * 设置对话框属性
     */
    private void setupDialog() {
        setSize(400, 350);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    /**
     * 加载车辆数据
     */
    private void loadCarData() {
        if (car != null) {
            licensePlateField.setText(car.getLicensePlateNumber());
            brandField.setText(car.getBrand());
            modelField.setText(car.getModel());
            colorField.setText(car.getColor());
            statusComboBox.setSelectedItem(car.getStatus());
            rentField.setText(car.getRent().toString());
            depositField.setText(car.getDeposit());
            
            if (car.getPurchaseDate() != null) {
                purchaseDateField.setText(car.getPurchaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        }
    }

    /**
     * 保存车辆信息
     */
    private void saveCar() {
        // 验证输入
        if (!validateInput()) {
            return;
        }
        
        try {
            Car carToSave = car == null ? new Car() : car;
            
            carToSave.setLicensePlateNumber(licensePlateField.getText().trim());
            carToSave.setBrand(brandField.getText().trim());
            carToSave.setModel(modelField.getText().trim());
            carToSave.setColor(colorField.getText().trim());
            carToSave.setStatus((String) statusComboBox.getSelectedItem());
            carToSave.setRent(new BigDecimal(rentField.getText().trim()));
            carToSave.setDeposit(depositField.getText().trim());
            carToSave.setPurchaseDate(LocalDate.parse(purchaseDateField.getText().trim()));
            
            boolean success;
            if (car == null) {
                success = carService.addCar(carToSave);
            } else {
                success = carService.updateCar(carToSave);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                parentPanel.refreshData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "保存失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 验证输入
     * @return 是否有效
     */
    private boolean validateInput() {
        if (licensePlateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入车牌号", "验证失败", JOptionPane.WARNING_MESSAGE);
            licensePlateField.requestFocus();
            return false;
        }
        
        if (brandField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入品牌", "验证失败", JOptionPane.WARNING_MESSAGE);
            brandField.requestFocus();
            return false;
        }
        
        if (modelField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入型号", "验证失败", JOptionPane.WARNING_MESSAGE);
            modelField.requestFocus();
            return false;
        }
        
        if (colorField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入颜色", "验证失败", JOptionPane.WARNING_MESSAGE);
            colorField.requestFocus();
            return false;
        }
        
        if (rentField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入日租金", "验证失败", JOptionPane.WARNING_MESSAGE);
            rentField.requestFocus();
            return false;
        }
        
        try {
            BigDecimal rent = new BigDecimal(rentField.getText().trim());
            if (rent.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "日租金必须大于0", "验证失败", JOptionPane.WARNING_MESSAGE);
                rentField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "日租金格式不正确", "验证失败", JOptionPane.WARNING_MESSAGE);
            rentField.requestFocus();
            return false;
        }
        
        if (depositField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入押金", "验证失败", JOptionPane.WARNING_MESSAGE);
            depositField.requestFocus();
            return false;
        }
        
        if (purchaseDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入购买日期", "验证失败", JOptionPane.WARNING_MESSAGE);
            purchaseDateField.requestFocus();
            return false;
        }
        
        try {
            LocalDate.parse(purchaseDateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "购买日期格式不正确，请使用 yyyy-MM-dd 格式", "验证失败", JOptionPane.WARNING_MESSAGE);
            purchaseDateField.requestFocus();
            return false;
        }
        
        return true;
    }
}
