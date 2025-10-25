package com.carrental.gui;

import com.carrental.entity.Car;
import com.carrental.entity.User;
import com.carrental.service.CarService;
import com.carrental.service.RentService;
import com.carrental.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 租车对话框
 * 用于处理租车业务
 */
public class RentCarDialog extends JDialog {
    private RentService rentService;
    private CarService carService;
    private UserService userService;
    private JComboBox<Car> carComboBox;
    private JComboBox<User> userComboBox;
    private JTextField rentDateField;
    private JTextField returnDateField;
    private JLabel rentAmountLabel;
    private JButton calculateButton;
    private JButton rentButton;
    private JButton cancelButton;
    private RentManagementPanel parentPanel;

    public RentCarDialog(RentManagementPanel parent) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "租车", true);
        this.parentPanel = parent;
        this.rentService = new RentService();
        this.carService = new CarService();
        this.userService = new UserService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        loadData();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        carComboBox = new JComboBox<>();
        userComboBox = new JComboBox<>();
        rentDateField = new JTextField(15);
        returnDateField = new JTextField(15);
        rentAmountLabel = new JLabel("¥0.00");
        calculateButton = new JButton("计算租金");
        rentButton = new JButton("确认租车");
        cancelButton = new JButton("取消");
        
        // 设置字体
        Font font = new Font("微软雅黑", Font.PLAIN, 12);
        carComboBox.setFont(font);
        userComboBox.setFont(font);
        rentDateField.setFont(font);
        returnDateField.setFont(font);
        rentAmountLabel.setFont(font);
        calculateButton.setFont(font);
        rentButton.setFont(font);
        cancelButton.setFont(font);
        
        // 设置提示文本
        rentDateField.setToolTipText("格式: yyyy-MM-dd (例如: 2024-01-01)");
        returnDateField.setToolTipText("格式: yyyy-MM-dd (例如: 2024-01-01)");
        
        // 设置默认日期
        rentDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        returnDateField.setText(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
        
        // 车辆选择
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("选择车辆:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(carComboBox, gbc);
        
        // 用户选择
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("选择用户:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(userComboBox, gbc);
        
        // 租借日期
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("租借日期:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(rentDateField, gbc);
        
        // 归还日期
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("归还日期:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(returnDateField, gbc);
        
        // 租金计算
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("预计租金:"), gbc);
        gbc.gridx = 1;
        JPanel rentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rentPanel.add(rentAmountLabel);
        rentPanel.add(calculateButton);
        mainPanel.add(rentPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(rentButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateRent();
            }
        });
        
        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRent();
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
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        // 加载可用车辆
        carComboBox.removeAllItems();
        carService.getAvailableCars().forEach(carComboBox::addItem);
        
        // 加载用户
        userComboBox.removeAllItems();
        userService.getAllUsers().forEach(userComboBox::addItem);
    }

    /**
     * 计算租金
     */
    private void calculateRent() {
        if (!validateInput()) {
            return;
        }
        
        try {
            Car selectedCar = (Car) carComboBox.getSelectedItem();
            LocalDate rentDate = LocalDate.parse(rentDateField.getText().trim());
            LocalDate returnDate = LocalDate.parse(returnDateField.getText().trim());
            
            BigDecimal rentAmount = carService.calculateRent(selectedCar.getCarId(), rentDate, returnDate);
            rentAmountLabel.setText("¥" + rentAmount.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "计算租金失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 执行租车
     */
    private void performRent() {
        if (!validateInput()) {
            return;
        }
        
        try {
            Car selectedCar = (Car) carComboBox.getSelectedItem();
            User selectedUser = (User) userComboBox.getSelectedItem();
            LocalDate rentDate = LocalDate.parse(rentDateField.getText().trim());
            LocalDate returnDate = LocalDate.parse(returnDateField.getText().trim());
            
            // 获取当前登录员工ID（这里假设为1，实际应该从登录状态获取）
            int staffId = 1;
            
            BigDecimal rentAmount = carService.calculateRent(selectedCar.getCarId(), rentDate, returnDate);
            
            int result = JOptionPane.showConfirmDialog(this, 
                "确认租车信息:\n" +
                "车辆: " + selectedCar.getLicensePlateNumber() + "\n" +
                "用户: " + selectedUser.getName() + "\n" +
                "租期: " + rentDate + " 至 " + returnDate + "\n" +
                "租金: ¥" + rentAmount + "\n\n确定要租车吗？", 
                "确认租车", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                if (rentService.rentCar(selectedCar.getCarId(), selectedUser.getUserId(), staffId, rentDate, returnDate)) {
                    JOptionPane.showMessageDialog(this, "租车成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                    parentPanel.refreshData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "租车失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "租车失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 验证输入
     * @return 是否有效
     */
    private boolean validateInput() {
        if (carComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "请选择车辆", "验证失败", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (userComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "请选择用户", "验证失败", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (rentDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入租借日期", "验证失败", JOptionPane.WARNING_MESSAGE);
            rentDateField.requestFocus();
            return false;
        }
        
        if (returnDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入归还日期", "验证失败", JOptionPane.WARNING_MESSAGE);
            returnDateField.requestFocus();
            return false;
        }
        
        try {
            LocalDate rentDate = LocalDate.parse(rentDateField.getText().trim());
            LocalDate returnDate = LocalDate.parse(returnDateField.getText().trim());
            
            if (rentDate.isAfter(returnDate)) {
                JOptionPane.showMessageDialog(this, "租借日期不能晚于归还日期", "验证失败", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式不正确，请使用 yyyy-MM-dd 格式", "验证失败", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
}
