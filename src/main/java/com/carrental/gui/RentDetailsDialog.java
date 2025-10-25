package com.carrental.gui;

import com.carrental.entity.RentInformation;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * 租车详情对话框
 * 显示租车的详细信息
 */
public class RentDetailsDialog extends JDialog {
    private RentInformation rentInfo;

    public RentDetailsDialog(Component parent, RentInformation rentInfo) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "租车详情", true);
        this.rentInfo = rentInfo;
        
        setupLayout();
        setupDialog();
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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font("微软雅黑", Font.BOLD, 12);
        Font valueFont = new Font("微软雅黑", Font.PLAIN, 12);
        
        // 租车ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel idLabel = new JLabel("租车ID:");
        idLabel.setFont(labelFont);
        mainPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        JLabel idValue = new JLabel(String.valueOf(rentInfo.getRentId()));
        idValue.setFont(valueFont);
        mainPanel.add(idValue, gbc);
        
        // 车辆ID
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel carIdLabel = new JLabel("车辆ID:");
        carIdLabel.setFont(labelFont);
        mainPanel.add(carIdLabel, gbc);
        gbc.gridx = 1;
        JLabel carIdValue = new JLabel(String.valueOf(rentInfo.getCarId()));
        carIdValue.setFont(valueFont);
        mainPanel.add(carIdValue, gbc);
        
        // 用户ID
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel userIdLabel = new JLabel("用户ID:");
        userIdLabel.setFont(labelFont);
        mainPanel.add(userIdLabel, gbc);
        gbc.gridx = 1;
        JLabel userIdValue = new JLabel(String.valueOf(rentInfo.getUserId()));
        userIdValue.setFont(valueFont);
        mainPanel.add(userIdValue, gbc);
        
        // 员工ID
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel staffIdLabel = new JLabel("员工ID:");
        staffIdLabel.setFont(labelFont);
        mainPanel.add(staffIdLabel, gbc);
        gbc.gridx = 1;
        JLabel staffIdValue = new JLabel(String.valueOf(rentInfo.getStaffId()));
        staffIdValue.setFont(valueFont);
        mainPanel.add(staffIdValue, gbc);
        
        // 租借日期
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel rentDateLabel = new JLabel("租借日期:");
        rentDateLabel.setFont(labelFont);
        mainPanel.add(rentDateLabel, gbc);
        gbc.gridx = 1;
        String rentDateStr = rentInfo.getRentDate() != null ? 
            rentInfo.getRentDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) : "未知";
        JLabel rentDateValue = new JLabel(rentDateStr);
        rentDateValue.setFont(valueFont);
        mainPanel.add(rentDateValue, gbc);
        
        // 归还日期
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel returnDateLabel = new JLabel("归还日期:");
        returnDateLabel.setFont(labelFont);
        mainPanel.add(returnDateLabel, gbc);
        gbc.gridx = 1;
        String returnDateStr = rentInfo.getReturnDate() != null ? 
            rentInfo.getReturnDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) : "未归还";
        JLabel returnDateValue = new JLabel(returnDateStr);
        returnDateValue.setFont(valueFont);
        if (rentInfo.getReturnDate() == null) {
            returnDateValue.setForeground(Color.RED);
        }
        mainPanel.add(returnDateValue, gbc);
        
        // 支付金额
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel payLabel = new JLabel("支付金额:");
        payLabel.setFont(labelFont);
        mainPanel.add(payLabel, gbc);
        gbc.gridx = 1;
        JLabel payValue = new JLabel("¥" + rentInfo.getPayTheAmount().toString());
        payValue.setFont(valueFont);
        payValue.setForeground(Color.RED);
        mainPanel.add(payValue, gbc);
        
        // 退还金额
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel returnLabel = new JLabel("退还金额:");
        returnLabel.setFont(labelFont);
        mainPanel.add(returnLabel, gbc);
        gbc.gridx = 1;
        JLabel returnValue = new JLabel("¥" + rentInfo.getReturnAmount().toString());
        returnValue.setFont(valueFont);
        returnValue.setForeground(Color.GREEN);
        mainPanel.add(returnValue, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("关闭");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置对话框属性
     */
    private void setupDialog() {
        setSize(400, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
}
