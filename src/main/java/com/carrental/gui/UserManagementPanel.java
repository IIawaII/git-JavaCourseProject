package com.carrental.gui;

import javax.swing.*;
import java.awt.*;
 
import com.carrental.entity.User;
import com.carrental.service.UserService;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 用户管理面板
 * 提供用户相关的管理功能
 */
public class UserManagementPanel extends JPanel {
    private UserService userService;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> memberComboBox;
    private JComboBox<String> judgeComboBox;

    public UserManagementPanel() {
        this.userService = new UserService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadUserData();
    }

    private void initializeComponents() {
        String[] columnNames = {"ID", "姓名", "身份证号", "电话", "注册日期", "会员", "信誉度"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        userTable = new JTable(tableModel);
        userTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userTable.setRowHeight(24);
        userTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));

        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        memberComboBox = new JComboBox<>(new String[]{"全部", "是", "非"});
        memberComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        judgeComboBox = new JComboBox<>(new String[]{"全部", "非常低", "低", "中", "高", "非常高"});
        judgeComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        searchPanel.add(new JLabel("搜索:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("会员:"));
        searchPanel.add(memberComboBox);
        searchPanel.add(new JLabel("信誉:"));
        searchPanel.add(judgeComboBox);

        JButton searchButton = new JButton("搜索");
        searchButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { performSearch(); }
        });
        searchPanel.add(searchButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> loadUserData());
        searchPanel.add(refreshButton);

        add(searchPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("用户列表"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton addButton = new JButton("添加用户");
        addButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        addButton.addActionListener(e -> showAddUserDialog());
        buttonPanel.add(addButton);

        JButton editButton = new JButton("修改用户");
        editButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        editButton.addActionListener(e -> showEditUserDialog());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("删除用户");
        deleteButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        deleteButton.addActionListener(e -> deleteUser());
        buttonPanel.add(deleteButton);

        JButton viewButton = new JButton("查看详情");
        viewButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        viewButton.addActionListener(e -> viewUserDetails());
        buttonPanel.add(viewButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) viewUserDetails();
            }
        });
    }

    private void loadUserData() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (User u : users) {
            Object[] row = {
                u.getUserId(),
                u.getName(),
                u.getIdentityId(),
                u.getPhone(),
                u.getRegisterDate() != null ? u.getRegisterDate().format(fmt) : "",
                u.getMember(),
                u.getJudge()
            };
            tableModel.addRow(row);
        }
    }

    private void performSearch() {
        String text = searchField.getText().trim();
        String member = (String) memberComboBox.getSelectedItem();
        String judge = (String) judgeComboBox.getSelectedItem();

        List<User> users = userService.getAllUsers();
        users = users.stream()
                .filter(u -> text.isEmpty() || u.getName().contains(text) || u.getIdentityId().contains(text) || u.getPhone().contains(text))
                .filter(u -> member.equals("全部") || (u.getMember() != null && u.getMember().equals(member)))
                .filter(u -> judge.equals("全部") || (u.getJudge() != null && u.getJudge().equals(judge)))
                .collect(java.util.stream.Collectors.toList());

        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (User u : users) {
            Object[] row = {
                u.getUserId(), u.getName(), u.getIdentityId(), u.getPhone(),
                u.getRegisterDate() != null ? u.getRegisterDate().format(fmt) : "",
                u.getMember(), u.getJudge()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        new UserDialog(this, null).setVisible(true);
    }

    private void showEditUserDialog() {
        int sel = userTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "请选择要修改的用户", "提示", JOptionPane.WARNING_MESSAGE); return; }
        int userId = (Integer) tableModel.getValueAt(sel, 0);
        User user = userService.getUserById(userId);
        if (user != null) new UserDialog(this, user).setVisible(true);
    }

    private void deleteUser() {
        int sel = userTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "请选择要删除的用户", "提示", JOptionPane.WARNING_MESSAGE); return; }
        int userId = (Integer) tableModel.getValueAt(sel, 0);
        String name = (String) tableModel.getValueAt(sel, 1);
        int r = JOptionPane.showConfirmDialog(this, "确认删除用户: " + name + " ?", "确认", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            if (userService.deleteUser(userId)) { JOptionPane.showMessageDialog(this, "删除成功", "提示", JOptionPane.INFORMATION_MESSAGE); loadUserData(); }
            else JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewUserDetails() {
        int sel = userTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "请选择要查看的用户", "提示", JOptionPane.WARNING_MESSAGE); return; }
        int userId = (Integer) tableModel.getValueAt(sel, 0);
        User u = userService.getUserById(userId);
        if (u != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("ID: ").append(u.getUserId()).append('\n');
            sb.append("姓名: ").append(u.getName()).append('\n');
            sb.append("身份证号: ").append(u.getIdentityId()).append('\n');
            sb.append("电话: ").append(u.getPhone()).append('\n');
            sb.append("注册日期: ").append(u.getRegisterDate()).append('\n');
            sb.append("会员: ").append(u.getMember()).append('\n');
            sb.append("信誉: ").append(u.getJudge()).append('\n');
            JOptionPane.showMessageDialog(this, sb.toString(), "用户详情", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshData() { loadUserData(); }
}
