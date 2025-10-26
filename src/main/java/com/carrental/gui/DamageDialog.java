package com.carrental.gui;

import com.carrental.dao.CarDAO;
import com.carrental.dao.DamageInformationDAO;
import com.carrental.entity.Car;
import com.carrental.entity.DamageInformation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * 损坏信息对话框
 * 用于添加和编辑损坏信息
 */
public class DamageDialog extends JDialog {
    private DamageInformation damageInfo;
    private List<Car> carList;
    private DamageInformationDAO damageDAO;
    
    private JComboBox<String> carCombo;
    private JTextField damageDateField;
    private JTextArea damageDescribeArea;
    private JComboBox<String> damageStateCombo;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private boolean confirmed = false;

    public DamageDialog(DamageInformation damageInfo, List<Car> carList) {
        this.damageInfo = damageInfo;
        this.carList = carList;
        this.damageDAO = new DamageInformationDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        
        if (damageInfo != null) {
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
        
        // 损坏日期
        damageDateField = new JTextField(15);
        damageDateField.setText(LocalDate.now().toString());
        
        // 损坏描述
        damageDescribeArea = new JTextArea(4, 20);
        damageDescribeArea.setLineWrap(true);
        damageDescribeArea.setWrapStyleWord(true);
        
        // 维修状态
        damageStateCombo = new JComboBox<>(new String[]{"未维修", "已维修"});
        
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
        
        // 损坏日期
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("损坏日期:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(damageDateField, gbc);
        
        // 损坏描述
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHEAST;
        mainPanel.add(new JLabel("损坏描述:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JScrollPane(damageDescribeArea), gbc);
        
        // 维修状态
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("维修状态:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(damageStateCombo, gbc);
        
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
        setTitle(damageInfo == null ? "添加损坏记录" : "修改损坏记录");
        setModal(true);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (damageInfo != null) {
            // 设置车辆选择
            for (int i = 0; i < carCombo.getItemCount(); i++) {
                String item = carCombo.getItemAt(i);
                if (item.contains("(ID:" + damageInfo.getCarId() + ")")) {
                    carCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            damageDateField.setText(damageInfo.getDamageDate().toString());
            damageDescribeArea.setText(damageInfo.getDamageDescribe());
            damageStateCombo.setSelectedItem(damageInfo.getDamageState());
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
            DamageInformation newDamageInfo = new DamageInformation();
            
            if (damageInfo != null) {
                newDamageInfo.setDamageId(damageInfo.getDamageId());
            }
            
            // 设置车辆ID
            String selectedCar = (String) carCombo.getSelectedItem();
            int carId = extractCarIdFromCombo(selectedCar);
            newDamageInfo.setCarId(carId);
            
            // 设置其他字段
            newDamageInfo.setDamageDate(LocalDate.parse(damageDateField.getText().trim()));
            newDamageInfo.setDamageDescribe(damageDescribeArea.getText().trim());
            newDamageInfo.setDamageState((String) damageStateCombo.getSelectedItem());
            
            boolean success;
            if (damageInfo == null) {
                success = damageDAO.addDamageInformation(newDamageInfo);
            } else {
                success = damageDAO.updateDamageInformation(newDamageInfo);
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
        if (damageDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入损坏日期", "提示", JOptionPane.WARNING_MESSAGE);
            damageDateField.requestFocus();
            return false;
        }
        
        if (damageDescribeArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入损坏描述", "提示", JOptionPane.WARNING_MESSAGE);
            damageDescribeArea.requestFocus();
            return false;
        }
        
        try {
            LocalDate.parse(damageDateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式不正确，请使用YYYY-MM-DD格式", "提示", JOptionPane.WARNING_MESSAGE);
            damageDateField.requestFocus();
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
     * 是否确认
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}
