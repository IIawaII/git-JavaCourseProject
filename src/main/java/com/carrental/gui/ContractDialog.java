package com.carrental.gui;

import com.carrental.entity.RentInformation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * 合同生成对话框
 * 生成租车合同
 */
public class ContractDialog extends JDialog {
    private RentInformation rentInfo;

    public ContractDialog(Component parent, RentInformation rentInfo) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "生成合同", true);
        this.rentInfo = rentInfo;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 组件将在setupLayout中创建
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 合同内容面板
        JPanel contractPanel = new JPanel(new BorderLayout());
        contractPanel.setBorder(BorderFactory.createTitledBorder("租车合同"));
        
        JTextArea contractTextArea = new JTextArea(20, 50);
        contractTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        contractTextArea.setEditable(false);
        contractTextArea.setWrapStyleWord(true);
        contractTextArea.setLineWrap(true);
        
        // 生成合同内容
        String contractContent = generateContractContent();
        contractTextArea.setText(contractContent);
        
        JScrollPane scrollPane = new JScrollPane(contractTextArea);
        contractPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contractPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton saveButton = new JButton("保存合同");
        saveButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveContract(contractContent);
            }
        });
        buttonPanel.add(saveButton);
        
        JButton printButton = new JButton("打印合同");
        printButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printContract();
            }
        });
        buttonPanel.add(printButton);
        
        JButton closeButton = new JButton("关闭");
        closeButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // 事件处理器已在setupLayout中设置
    }

    /**
     * 设置对话框属性
     */
    private void setupDialog() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setResizable(true);
    }

    /**
     * 生成合同内容
     * @return 合同内容
     */
    private String generateContractContent() {
        StringBuilder contract = new StringBuilder();
        
        contract.append("汽车出租合同\n");
        contract.append("==================================================\n\n");
        
        contract.append("合同编号: CR").append(String.format("%06d", rentInfo.getRentId())).append("\n");
        contract.append("签订日期: ").append(rentInfo.getRentDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))).append("\n\n");
        
        contract.append("甲方（出租方）: 汽车出租公司\n");
        contract.append("乙方（承租方）: 用户ID-").append(rentInfo.getUserId()).append("\n\n");
        
        contract.append("根据《中华人民共和国合同法》及相关法律法规，甲乙双方在平等、自愿的基础上，就汽车租赁事宜达成如下协议：\n\n");
        
        contract.append("第一条 租赁车辆\n");
        contract.append("车辆ID: ").append(rentInfo.getCarId()).append("\n");
        contract.append("租借日期: ").append(rentInfo.getRentDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))).append("\n");
        contract.append("归还日期: ").append(rentInfo.getReturnDate() != null ? 
            rentInfo.getReturnDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) : "待定").append("\n\n");
        
        contract.append("第二条 租金及押金\n");
        contract.append("支付金额: ¥").append(rentInfo.getPayTheAmount().toString()).append("\n");
        contract.append("退还金额: ¥").append(rentInfo.getReturnAmount().toString()).append("\n\n");
        
        contract.append("第三条 双方权利义务\n");
        contract.append("甲方权利义务:\n");
        contract.append("1. 提供符合安全标准的车辆\n");
        contract.append("2. 负责车辆的日常维护和保养\n");
        contract.append("3. 协助乙方处理车辆故障\n\n");
        
        contract.append("乙方权利义务:\n");
        contract.append("1. 按时支付租金\n");
        contract.append("2. 合理使用车辆，不得超载或超速\n");
        contract.append("3. 按时归还车辆\n");
        contract.append("4. 承担车辆使用期间的违章罚款\n\n");
        
        contract.append("第四条 违约责任\n");
        contract.append("1. 乙方逾期归还车辆的，按日收取滞纳金\n");
        contract.append("2. 乙方损坏车辆的，应承担维修费用\n");
        contract.append("3. 乙方违反交通法规的，由乙方承担相应责任\n\n");
        
        contract.append("第五条 争议解决\n");
        contract.append("本合同履行过程中发生争议的，双方应协商解决；协商不成的，可向有管辖权的人民法院起诉。\n\n");
        
        contract.append("第六条 其他条款\n");
        contract.append("1. 本合同一式两份，甲乙双方各执一份\n");
        contract.append("2. 本合同自双方签字之日起生效\n");
        contract.append("3. 本合同未尽事宜，按国家相关法律法规执行\n\n");
        
        contract.append("甲方（盖章）: 汽车出租公司\n");
        contract.append("乙方（签字）: ________________\n");
        contract.append("经办人: 员工ID-").append(rentInfo.getStaffId()).append("\n\n");
        
        contract.append("签订日期: ").append(rentInfo.getRentDate().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))).append("\n");
        
        return contract.toString();
    }

    /**
     * 保存合同
     * @param content 合同内容
     */
    private void saveContract(String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("租车合同_" + rentInfo.getRentId() + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "合同保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 打印合同
     */
    private void printContract() {
        try {
            JTextArea textArea = new JTextArea(generateContractContent());
            textArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            textArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "打印失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
