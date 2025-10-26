package com.carrental.gui;

import com.carrental.dao.CarDAO;
import com.carrental.dao.MaintainInformationDAO;
import com.carrental.entity.Car;
import com.carrental.entity.MaintainInformation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 维修信息对话框
 * 用于添加和编辑维修信息
 */
public class MaintainDialog extends JDialog {
    private MaintainInformation maintainInfo;
    private List<Car> carList;
    private MaintainInformationDAO maintainDAO;
    private int maintainId;
    
    private JComboBox<String> carCombo;
    private JTextField maintainDateField;
    private JTextArea maintainDescribeArea;
    private JTextField maintainBeginDateField;
    private JTextField maintainFinishDateField;
    private JTextField maintainCostField;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private boolean confirmed = false;

    public MaintainDialog(MaintainInformation maintainInfo, List<Car> carList, int maintainId) {
        this.maintainInfo = maintainInfo;
        this.carList = carList;
        this.maintainDAO = new MaintainInformationDAO();
        this.maintainId = maintainId;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        
        if (maintainInfo != null) {
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
        
        // 报修日期
        maintainDateField = new JTextField(15);
        maintainDateField.setText(LocalDate.now().toString());
        
        // 维修描述
        maintainDescribeArea = new JTextArea(3, 20);
        maintainDescribeArea.setLineWrap(true);
        maintainDescribeArea.setWrapStyleWord(true);
        
        // 维修开始日期
        maintainBeginDateField = new JTextField(15);
        maintainBeginDateField.setText(LocalDate.now().toString());
        
        // 维修结束日期
        maintainFinishDateField = new JTextField(15);
        
        // 维修费用
        maintainCostField = new JTextField(15);
        
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
        
        // 报修日期
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("报修日期:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(maintainDateField, gbc);
        
        // 维修描述
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHEAST;
        mainPanel.add(new JLabel("维修描述:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JScrollPane(maintainDescribeArea), gbc);
        
        // 维修开始日期
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("开始日期:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(maintainBeginDateField, gbc);
        
        // 维修结束日期
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("结束日期:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(maintainFinishDateField, gbc);
        
        // 维修费用
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("维修费用:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(maintainCostField, gbc);
        
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
        setTitle(maintainInfo == null ? "添加维修记录" : "修改维修记录");
        setModal(true);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (maintainInfo != null) {
            // 设置车辆选择
            for (int i = 0; i < carCombo.getItemCount(); i++) {
                String item = carCombo.getItemAt(i);
                if (item.contains("(ID:" + maintainInfo.getCarId() + ")")) {
                    carCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            maintainDateField.setText(maintainInfo.getMaintainDate().toString());
            maintainDescribeArea.setText(maintainInfo.getMaintainDescribe());
            maintainBeginDateField.setText(maintainInfo.getMaintainBeginDate().toString());
            if (maintainInfo.getMaintainFinishDate() != null) {
                maintainFinishDateField.setText(maintainInfo.getMaintainFinishDate().toString());
            }
            maintainCostField.setText(maintainInfo.getMaintainCost().toString());
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
            MaintainInformation newMaintainInfo = new MaintainInformation();
            newMaintainInfo.setMaintainId(maintainId);
            
            // 设置车辆ID
            String selectedCar = (String) carCombo.getSelectedItem();
            int carId = extractCarIdFromCombo(selectedCar);
            newMaintainInfo.setCarId(carId);
            
            // 设置其他字段
            newMaintainInfo.setMaintainDate(LocalDate.parse(maintainDateField.getText().trim()));
            newMaintainInfo.setMaintainDescribe(maintainDescribeArea.getText().trim());
            newMaintainInfo.setMaintainBeginDate(LocalDate.parse(maintainBeginDateField.getText().trim()));
            
            if (!maintainFinishDateField.getText().trim().isEmpty()) {
                newMaintainInfo.setMaintainFinishDate(LocalDate.parse(maintainFinishDateField.getText().trim()));
            }
            
            if (!maintainCostField.getText().trim().isEmpty()) {
                newMaintainInfo.setMaintainCost(new BigDecimal(maintainCostField.getText().trim()));
            }
            
            boolean success;
            if (maintainInfo == null) {
                success = maintainDAO.addMaintainInformation(newMaintainInfo);
            } else {
                success = maintainDAO.updateMaintainInformation(newMaintainInfo);
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
        if (maintainDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入报修日期", "提示", JOptionPane.WARNING_MESSAGE);
            maintainDateField.requestFocus();
            return false;
        }
        
        if (maintainDescribeArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入维修描述", "提示", JOptionPane.WARNING_MESSAGE);
            maintainDescribeArea.requestFocus();
            return false;
        }
        
        if (maintainBeginDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入维修开始日期", "提示", JOptionPane.WARNING_MESSAGE);
            maintainBeginDateField.requestFocus();
            return false;
        }
        
        try {
            LocalDate.parse(maintainDateField.getText().trim());
            LocalDate.parse(maintainBeginDateField.getText().trim());
            
            if (!maintainFinishDateField.getText().trim().isEmpty()) {
                LocalDate.parse(maintainFinishDateField.getText().trim());
            }
            
            if (!maintainCostField.getText().trim().isEmpty()) {
                new BigDecimal(maintainCostField.getText().trim());
            }
            
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
     * 是否确认
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}
