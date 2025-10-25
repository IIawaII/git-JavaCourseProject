package com.carrental.gui;

import com.carrental.entity.Car;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * 车辆详情对话框
 * 显示车辆的详细信息
 */
public class CarDetailsDialog extends JDialog {
    private Car car;

    public CarDetailsDialog(Component parent, Car car) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "车辆详情", true);
        this.car = car;
        
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
        
        // 车辆ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel idLabel = new JLabel("车辆ID:");
        idLabel.setFont(labelFont);
        mainPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        JLabel idValue = new JLabel(String.valueOf(car.getCarId()));
        idValue.setFont(valueFont);
        mainPanel.add(idValue, gbc);
        
        // 车牌号
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel licenseLabel = new JLabel("车牌号:");
        licenseLabel.setFont(labelFont);
        mainPanel.add(licenseLabel, gbc);
        gbc.gridx = 1;
        JLabel licenseValue = new JLabel(car.getLicensePlateNumber());
        licenseValue.setFont(valueFont);
        mainPanel.add(licenseValue, gbc);
        
        // 品牌
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel brandLabel = new JLabel("品牌:");
        brandLabel.setFont(labelFont);
        mainPanel.add(brandLabel, gbc);
        gbc.gridx = 1;
        JLabel brandValue = new JLabel(car.getBrand());
        brandValue.setFont(valueFont);
        mainPanel.add(brandValue, gbc);
        
        // 型号
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel modelLabel = new JLabel("型号:");
        modelLabel.setFont(labelFont);
        mainPanel.add(modelLabel, gbc);
        gbc.gridx = 1;
        JLabel modelValue = new JLabel(car.getModel());
        modelValue.setFont(valueFont);
        mainPanel.add(modelValue, gbc);
        
        // 颜色
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel colorLabel = new JLabel("颜色:");
        colorLabel.setFont(labelFont);
        mainPanel.add(colorLabel, gbc);
        gbc.gridx = 1;
        JLabel colorValue = new JLabel(car.getColor());
        colorValue.setFont(valueFont);
        mainPanel.add(colorValue, gbc);
        
        // 状态
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel statusLabel = new JLabel("状态:");
        statusLabel.setFont(labelFont);
        mainPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        JLabel statusValue = new JLabel(car.getStatus());
        statusValue.setFont(valueFont);
        statusValue.setForeground(getStatusColor(car.getStatus()));
        mainPanel.add(statusValue, gbc);
        
        // 日租金
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel rentLabel = new JLabel("日租金:");
        rentLabel.setFont(labelFont);
        mainPanel.add(rentLabel, gbc);
        gbc.gridx = 1;
        JLabel rentValue = new JLabel("¥" + car.getRent().toString());
        rentValue.setFont(valueFont);
        rentValue.setForeground(Color.RED);
        mainPanel.add(rentValue, gbc);
        
        // 押金
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel depositLabel = new JLabel("押金:");
        depositLabel.setFont(labelFont);
        mainPanel.add(depositLabel, gbc);
        gbc.gridx = 1;
        JLabel depositValue = new JLabel("¥" + car.getDeposit());
        depositValue.setFont(valueFont);
        depositValue.setForeground(Color.RED);
        mainPanel.add(depositValue, gbc);
        
        // 购买日期
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel purchaseLabel = new JLabel("购买日期:");
        purchaseLabel.setFont(labelFont);
        mainPanel.add(purchaseLabel, gbc);
        gbc.gridx = 1;
        String purchaseDateStr = car.getPurchaseDate() != null ? 
            car.getPurchaseDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) : "未知";
        JLabel purchaseValue = new JLabel(purchaseDateStr);
        purchaseValue.setFont(valueFont);
        mainPanel.add(purchaseValue, gbc);
        
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

    /**
     * 根据状态获取颜色
     * @param status 状态
     * @return 颜色
     */
    private Color getStatusColor(String status) {
        switch (status) {
            case "空闲":
                return Color.GREEN;
            case "已借出":
                return Color.RED;
            case "维修中":
                return Color.ORANGE;
            default:
                return Color.BLACK;
        }
    }
}
