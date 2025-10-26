package com.carrental.gui;

import com.carrental.entity.Car;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 车辆详情对话框
 * 显示车辆的详细信息
 */
public class CarDetailsDialog extends JDialog {
    private static final Font LABEL_FONT = new Font("微软雅黑", Font.BOLD, 12);
    private static final Font VALUE_FONT = new Font("微软雅黑", Font.PLAIN, 12);
    private static final Color RENT_COLOR = Color.RED;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    
    private static final Map<String, Color> STATUS_COLOR_MAP = new HashMap<>();
    static {
        STATUS_COLOR_MAP.put("空闲", Color.GREEN);
        STATUS_COLOR_MAP.put("已借出", Color.RED);
        STATUS_COLOR_MAP.put("维修中", Color.ORANGE);
    }

    private final Car car;

    public CarDetailsDialog(Component parent, Car car) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "车辆详情", true);
        this.car = car;
        
        initializeDialog();
    }

    /**
     * 初始化对话框
     */
    private void initializeDialog() {
        setupLayout();
        setupDialog();
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建主面板
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = createGridBagConstraints();
        
        // 使用数组定义字段配置，便于维护
        Object[][] fieldConfigs = {
            {"车辆ID:", String.valueOf(car.getCarId()), null},
            {"车牌号:", car.getLicensePlateNumber(), null},
            {"品牌:", car.getBrand(), null},
            {"型号:", car.getModel(), null},
            {"颜色:", car.getColor(), null},
            {"状态:", car.getStatus(), getStatusColor(car.getStatus())},
            {"日租金:", formatCurrency(car.getRent()), RENT_COLOR},
            {"押金:", formatCurrency(car.getDeposit()), RENT_COLOR},
            {"购买日期:", formatDate(car.getPurchaseDate()), null}
        };
        
        int row = 0;
        for (Object[] config : fieldConfigs) {
            addFieldRow(mainPanel, gbc, row, 
                (String) config[0], 
                (String) config[1], 
                (Color) config[2]);
            row++;
        }
        
        return mainPanel;
    }

    /**
     * 创建GridBagConstraints
     */
    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    /**
     * 添加字段行
     */
    private void addFieldRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value, Color color) {
        // 标签
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);
        panel.add(labelComponent, gbc);
        
        // 值
        gbc.gridx = 1;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(VALUE_FONT);
        
        // 设置颜色（如果指定了颜色）
        if (color != null) {
            valueComponent.setForeground(color);
        }
        
        panel.add(valueComponent, gbc);
    }

    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("关闭");
        closeButton.setFont(VALUE_FONT);
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        return buttonPanel;
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
     * 格式化货币 - 处理数值类型
     */
    private String formatCurrency(Number amount) {
        if (amount == null) {
            return "¥0";
        }
        return "¥" + amount.toString();
    }
    
    /**
     * 格式化货币 - 处理字符串类型
     */
    private String formatCurrency(String amountStr) {
        if (amountStr == null || amountStr.isEmpty()) {
            return "¥0";
        }
        // 如果字符串已经包含货币符号，直接返回
        if (amountStr.startsWith("¥")) {
            return amountStr;
        }
        return "¥" + amountStr;
    }

    /**
     * 格式化日期
     */
    private String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return "未知";
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * 根据状态获取颜色
     * @param status 状态
     * @return 颜色
     */
    private Color getStatusColor(String status) {
        return STATUS_COLOR_MAP.getOrDefault(status, Color.BLACK);
    }
}