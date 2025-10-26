package com.carrental.gui;

import com.carrental.dao.CarDAO;
import com.carrental.dao.TrafficFineDAO;
import com.carrental.dao.UserDAO;
import com.carrental.entity.Car;
import com.carrental.entity.TrafficFine;
import com.carrental.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 违章罚款对话框
 * 用于添加和编辑违章罚款信息
 */
public class TrafficFineDialog extends JDialog {
    private TrafficFine trafficFine;
    private List<Car> carList;
    private List<User> userList;
    private TrafficFineDAO trafficFineDAO;
    
    private JComboBox<String> carCombo;
    private JComboBox<String> userCombo;
    private JTextField violationDateField;
    private JTextField offendingLocationField;
    private JTextField fineField;
    private JComboBox<String> fineStateCombo;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private boolean confirmed = false;

    public TrafficFineDialog(TrafficFine trafficFine, List<Car> carList, List<User> userList) {
        this.trafficFine = trafficFine;
        this.carList = carList;
        this.userList = userList;
        this.trafficFineDAO = new TrafficFineDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        
        if (trafficFine != null) {
            loadData();
        }
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 车辆选择
        carCombo = new JComboBox<>();
        for (Car car : carList) {
            carCombo.addItem(car.getLicensePlateNumber() + " (ID:" + car.getCarId() + ")");
        }
        
        // 用户选择
        userCombo = new JComboBox<>();
        for (User user : userList) {
            userCombo.addItem(user.getName() + " (ID:" + user.getUserId() + ")");
        }
        
        // 违规日期
        violationDateField = new JTextField(15);
        violationDateField.setText(LocalDate.now().toString());
        
        // 违规地点
        offendingLocationField = new JTextField(20);
        
        // 罚款金额
        fineField = new JTextField(15);
        
        // 罚款状态
        fineStateCombo = new JComboBox<>(new String[]{"未交", "已交"});
        
        // 按钮
        confirmButton = new JButton("确认");
        cancelButton = new JButton("取消");
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 车辆选择
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("车辆:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(carCombo, gbc);
        
        // 用户选择
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("用户:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(userCombo, gbc);
        
        // 违规日期
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("违规日期:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(violationDateField, gbc);
        
        // 违规地点
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("违规地点:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(offendingLocationField, gbc);
        
        // 罚款金额
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("罚款金额:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(fineField, gbc);
        
        // 罚款状态
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("罚款状态:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(fineStateCombo, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        confirmButton.addActionListener(e -> handleConfirm());
        cancelButton.addActionListener(e -> handleCancel());
    }

    /**
     * 设置对话框属性
     */
    private void setupDialog() {
        setTitle(trafficFine == null ? "添加罚款记录" : "修改罚款记录");
        setModal(true);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (trafficFine != null) {
            // 设置车辆选择
            for (int i = 0; i < carCombo.getItemCount(); i++) {
                String item = carCombo.getItemAt(i);
                if (item.contains("(ID:" + trafficFine.getCarId() + ")")) {
                    carCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            // 设置用户选择
            for (int i = 0; i < userCombo.getItemCount(); i++) {
                String item = userCombo.getItemAt(i);
                if (item.contains("(ID:" + trafficFine.getUserId() + ")")) {
                    userCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            violationDateField.setText(trafficFine.getViolationDate().toString());
            offendingLocationField.setText(trafficFine.getOffendingLocation());
            fineField.setText(trafficFine.getFine().toString());
            fineStateCombo.setSelectedItem(trafficFine.getFineState());
        }
    }

    /**
     * 处理确认
     */
    private void handleConfirm() {
        if (!validateInput()) {
            return;
        }
        
        try {
            TrafficFine newTrafficFine = new TrafficFine();
            
            if (trafficFine != null) {
                newTrafficFine.setFineId(trafficFine.getFineId());
            }
            
            // 设置车辆ID
            String selectedCar = (String) carCombo.getSelectedItem();
            int carId = extractCarIdFromCombo(selectedCar);
            newTrafficFine.setCarId(carId);
            
            // 设置用户ID
            String selectedUser = (String) userCombo.getSelectedItem();
            int userId = extractUserIdFromCombo(selectedUser);
            newTrafficFine.setUserId(userId);
            
            // 设置其他字段
            newTrafficFine.setViolationDate(LocalDate.parse(violationDateField.getText().trim()));
            newTrafficFine.setOffendingLocation(offendingLocationField.getText().trim());
            newTrafficFine.setFine(new BigDecimal(fineField.getText().trim()));
            newTrafficFine.setFineState((String) fineStateCombo.getSelectedItem());
            
            boolean success;
            if (trafficFine == null) {
                success = trafficFineDAO.addTrafficFine(newTrafficFine);
            } else {
                success = trafficFineDAO.updateTrafficFine(newTrafficFine);
            }
            
            if (success) {
                confirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "保存失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 处理取消
     */
    private void handleCancel() {
        dispose();
    }

    /**
     * 验证输入
     */
    private boolean validateInput() {
        if (violationDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入违规日期", "提示", JOptionPane.WARNING_MESSAGE);
            violationDateField.requestFocus();
            return false;
        }
        
        if (offendingLocationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入违规地点", "提示", JOptionPane.WARNING_MESSAGE);
            offendingLocationField.requestFocus();
            return false;
        }
        
        if (fineField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入罚款金额", "提示", JOptionPane.WARNING_MESSAGE);
            fineField.requestFocus();
            return false;
        }
        
        try {
            LocalDate.parse(violationDateField.getText().trim());
            new BigDecimal(fineField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期或金额格式不正确", "提示", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
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
     * 是否确认
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}
