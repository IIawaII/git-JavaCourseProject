package com.carrental.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 财务报表面板
 * 提供财务报表相关功能
 */
public class FinancialReportPanel extends JPanel {
    
    public FinancialReportPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // 初始化组件
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel label = new JLabel("财务报表功能开发中...", SwingConstants.CENTER);
        label.setFont(new java.awt.Font("微软雅黑", java.awt.Font.BOLD, 16));
        add(label, BorderLayout.CENTER);
    }
}
