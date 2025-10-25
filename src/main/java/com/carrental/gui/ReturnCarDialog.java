package com.carrental.gui;

import com.carrental.entity.RentInformation;
import com.carrental.service.RentService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 还车对话框
 * 用于处理还车业务
 */
public class ReturnCarDialog extends JDialog {
    private RentInformation rentInfo;
    private RentService rentService;
    private JTextField returnDateField;
    private JTextField damageCostField;
    private JLabel actualRentLabel;
    private JLabel returnAmountLabel;
    private JButton calculateButton;
    private JButton returnButton;
    private JButton cancelButton;
    private RentManagementPanel parentPanel;

    public ReturnCarDialog(RentManagementPanel parent, RentInformation rentInfo) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "还车", true);
        this.parentPanel = parent;
        this.rentInfo = rentInfo;
        this.rentService = new RentService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        returnDateField = new JTextField(15);
        damageCostField = new JTextField(15);
        actualRentLabel = new JLabel("¥0.00");
        returnAmountLabel = new JLabel("¥0.00");
        calculateButton = new JButton("计算费用");
        returnButton = new JButton("确认还车");
        cancelButton = new JButton("取消");
        
        // 设置字体
        Font font = new Font("微软雅黑", Font.PLAIN, 12);
        returnDateField.setFont(font);
        damageCostField.setFont(font);
        actualRentLabel.setFont(font);
        returnAmountLabel.setFont(font);
        calculateButton.setFont(font);
        returnButton.setFont(font);
        cancelButton.setFont(font);
        
        // 设置默认值
        returnDateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        damageCostField.setText("0");
        
        // 设置提示文本
        returnDateField.setToolTipText("格式: yyyy-MM-dd (例如: 2024-01-01)");
        damageCostField.setToolTipText("车辆损坏费用，无损坏请输入0");
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
        
        // 租车信息显示
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("租车信息: 车辆ID-" + rentInfo.getCarId() + 
            ", 用户ID-" + rentInfo.getUserId() + 
            ", 租借日期-" + rentInfo.getRentDate());
        infoLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        mainPanel.add(infoLabel, gbc);
        
        // 实际归还日期
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("实际归还日期:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(returnDateField, gbc);
        
        // 损坏费用
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("损坏费用:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(damageCostField, gbc);
        
        // 实际租金
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("实际租金:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(actualRentLabel, gbc);
        
        // 退还金额
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("退还金额:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(returnAmountLabel, gbc);
        
        // 计算按钮
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(calculateButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(returnButton);
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
                calculateReturn();
            }
        });
        
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performReturn();
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
     * 计算还车费用
     */
    private void calculateReturn() {
        try {
            LocalDate actualReturnDate = LocalDate.parse(returnDateField.getText().trim());
            BigDecimal damageCost = new BigDecimal(damageCostField.getText().trim());
            
            // 计算实际租金
            BigDecimal actualRent = rentService.calculateRent(rentInfo.getCarId(), rentInfo.getRentDate(), actualReturnDate);
            actualRentLabel.setText("¥" + actualRent.toString());
            
            // 计算退还金额
            BigDecimal returnAmount = rentInfo.getPayTheAmount().subtract(actualRent).subtract(damageCost);
            returnAmountLabel.setText("¥" + returnAmount.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "计算失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 执行还车
     */
    private void performReturn() {
        try {
            LocalDate actualReturnDate = LocalDate.parse(returnDateField.getText().trim());
            BigDecimal damageCost = new BigDecimal(damageCostField.getText().trim());
            
            int result = JOptionPane.showConfirmDialog(this, 
                "确认还车信息:\n" +
                "实际归还日期: " + actualReturnDate + "\n" +
                "损坏费用: ¥" + damageCost + "\n" +
                "退还金额: " + returnAmountLabel.getText() + "\n\n确定要还车吗？", 
                "确认还车", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                if (rentService.returnCar(rentInfo.getRentId(), actualReturnDate, damageCost)) {
                    JOptionPane.showMessageDialog(this, "还车成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                    parentPanel.refreshData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "还车失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "还车失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
