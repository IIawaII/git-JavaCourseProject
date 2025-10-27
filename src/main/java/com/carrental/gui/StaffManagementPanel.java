package com.carrental.gui;

import javax.swing.*;
import java.awt.*;

import com.carrental.entity.Staff;
import com.carrental.service.UserService;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * 员工管理面板
 * 提供员工相关的管理功能
 */
public class StaffManagementPanel extends JPanel {
    private UserService userService;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> positionComboBox;

    public StaffManagementPanel() {
        this.userService = new UserService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadStaffData();
    }

    private void initializeComponents() {
        String[] columns = {"ID", "姓名", "电话", "入职日期", "职位", "权限等级"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        staffTable.setRowHeight(24);
        staffTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));

    // 隐藏ID列（模型保留ID以便内部使用）
    staffTable.getColumnModel().getColumn(0).setMinWidth(0);
    staffTable.getColumnModel().getColumn(0).setMaxWidth(0);
    staffTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        searchField = new JTextField(15);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        positionComboBox = new JComboBox<>(new String[]{"全部", "董事长", "经理", "员工"});
        positionComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        top.add(new JLabel("搜索:"));
        top.add(searchField);
        top.add(new JLabel("职位:"));
        top.add(positionComboBox);

        JButton searchBtn = new JButton("搜索");
        searchBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        searchBtn.addActionListener(e -> performSearch());
        top.add(searchBtn);

        JButton refreshBtn = new JButton("刷新");
        refreshBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        refreshBtn.addActionListener(e -> loadStaffData());
        top.add(refreshBtn);

        add(top, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(staffTable);
        scroll.setBorder(BorderFactory.createTitledBorder("员工列表"));
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton addBtn = new JButton("添加员工");
        addBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        addBtn.addActionListener(e -> showAddStaffDialog());
        bottom.add(addBtn);

        JButton editBtn = new JButton("修改员工");
        editBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        editBtn.addActionListener(e -> showEditStaffDialog());
        bottom.add(editBtn);

        JButton delBtn = new JButton("删除员工");
        delBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        delBtn.addActionListener(e -> deleteStaff());
        bottom.add(delBtn);

        JButton viewBtn = new JButton("查看详情");
        viewBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        viewBtn.addActionListener(e -> viewStaffDetails());
        bottom.add(viewBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        staffTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) viewStaffDetails();
            }
        });
    }

    private void loadStaffData() {
        tableModel.setRowCount(0);
        SwingWorker<java.util.List<Staff>, Void> worker = new SwingWorker<java.util.List<Staff>, Void>() {
            @Override
            protected java.util.List<Staff> doInBackground() throws Exception {
                return userService.getAllStaff();
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Staff> staffList = get();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    tableModel.setRowCount(0);
                    for (Staff s : staffList) {
                        Object[] row = {s.getStaffId(), s.getName(), s.getPhone(), s.getEntryDate() != null ? s.getEntryDate().format(fmt) : "", s.getPosition(), s.getRole()};
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StaffManagementPanel.this, "加载员工数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void performSearch() {
        String text = searchField.getText().trim();
        String pos = (String) positionComboBox.getSelectedItem();
        tableModel.setRowCount(0);
        SwingWorker<java.util.List<Staff>, Void> worker = new SwingWorker<java.util.List<Staff>, Void>() {
            @Override
            protected java.util.List<Staff> doInBackground() throws Exception {
                java.util.List<Staff> staffList = userService.getAllStaff();
                java.util.List<Staff> filtered = staffList.stream()
                        .filter(s -> text.isEmpty() || s.getName().contains(text) || s.getPhone().contains(text))
                        .filter(s -> pos.equals("全部") || (s.getPosition() != null && s.getPosition().equals(pos)))
                        .collect(java.util.stream.Collectors.toList());
                return filtered;
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Staff> staffList = get();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    tableModel.setRowCount(0);
                    for (Staff s : staffList) {
                        Object[] row = {s.getStaffId(), s.getName(), s.getPhone(), s.getEntryDate() != null ? s.getEntryDate().format(fmt) : "", s.getPosition(), s.getRole()};
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StaffManagementPanel.this, "搜索员工失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddStaffDialog() { new StaffDialog(this, null).setVisible(true); }

    private void showEditStaffDialog() {
        int sel = staffTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "请选择要修改的员工", "提示", JOptionPane.WARNING_MESSAGE); return; }
        int staffId = (Integer) tableModel.getValueAt(sel, 0);
        // fetch staff in background then open dialog on EDT in done()
        SwingWorker<Staff, Void> worker = new SwingWorker<Staff, Void>() {
            @Override
            protected Staff doInBackground() throws Exception {
                return userService.getStaffById(staffId);
            }

            @Override
            protected void done() {
                try {
                    Staff s = get();
                    if (s != null) new StaffDialog(StaffManagementPanel.this, s).setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StaffManagementPanel.this, "加载员工信息失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteStaff() {
        int sel = staffTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "请选择要删除的员工", "提示", JOptionPane.WARNING_MESSAGE); return; }
        int staffId = (Integer) tableModel.getValueAt(sel, 0);
        String name = (String) tableModel.getValueAt(sel, 1);
        int r = JOptionPane.showConfirmDialog(this, "确认删除员工: " + name + " ?", "确认", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return userService.deleteStaff(staffId);
                }

                @Override
                protected void done() {
                    try {
                        Boolean ok = get();
                        if (ok) { JOptionPane.showMessageDialog(StaffManagementPanel.this, "删除成功", "提示", JOptionPane.INFORMATION_MESSAGE); loadStaffData(); }
                        else JOptionPane.showMessageDialog(StaffManagementPanel.this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(StaffManagementPanel.this, "删除员工失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void viewStaffDetails() {
        int sel = staffTable.getSelectedRow();
        if (sel == -1) { JOptionPane.showMessageDialog(this, "请选择要查看的员工", "提示", JOptionPane.WARNING_MESSAGE); return; }
        int staffId = (Integer) tableModel.getValueAt(sel, 0);
        SwingWorker<Staff, Void> worker = new SwingWorker<Staff, Void>() {
            @Override
            protected Staff doInBackground() throws Exception {
                return userService.getStaffById(staffId);
            }

            @Override
            protected void done() {
                try {
                    Staff s = get();
                    if (s != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("ID: ").append(s.getStaffId()).append('\n');
                        sb.append("姓名: ").append(s.getName()).append('\n');
                        sb.append("电话: ").append(s.getPhone()).append('\n');
                        sb.append("入职日期: ").append(s.getEntryDate()).append('\n');
                        sb.append("职位: ").append(s.getPosition()).append('\n');
                        sb.append("权限等级: ").append(s.getRole()).append('\n');
                        JOptionPane.showMessageDialog(StaffManagementPanel.this, sb.toString(), "员工详情", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StaffManagementPanel.this, "加载员工详情失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public void refreshData() { loadStaffData(); }
}
