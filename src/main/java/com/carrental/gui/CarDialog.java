package com.carrental.gui;

import com.carrental.entity.Car;
import com.carrental.service.CarService;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * 车辆信息对话框（添加/修改）
 */
public class CarDialog extends JDialog {
    private Car car;
    private CarService carService;
    private JTextField carIdField, licensePlateField, brandField, modelField, colorField;
    private JComboBox<String> statusComboBox;
    private JFormattedTextField rentField, depositField;
    private DatePicker purchaseDatePicker;
    private JButton saveButton, cancelButton;
    private CarManagementPanel parentPanel;

    public CarDialog(CarManagementPanel parent, Car car) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), car == null ? "添加车辆" : "修改车辆", true);
        this.parentPanel = parent;
        this.car = car;
        this.carService = new CarService();

        initComponents();
        setupLayout();
        setupEvents();
        setupDialog();

        if (car != null) loadCarData();
    }

    private void initComponents() {
        Font font = new Font(Font.DIALOG, Font.PLAIN, 12);

        carIdField = new JTextField();
        licensePlateField = new JTextField();
        brandField = new JTextField();
        modelField = new JTextField();
        colorField = new JTextField();

        statusComboBox = new JComboBox<>(new String[]{"空闲", "已借出", "维修中"});
        statusComboBox.setFont(font);

        // 数字输入框
        NumberFormatter moneyFormatter = new NumberFormatter(NumberFormat.getNumberInstance());
        moneyFormatter.setValueClass(BigDecimal.class);
        moneyFormatter.setMinimum(BigDecimal.ZERO);
        moneyFormatter.setAllowsInvalid(false);
        rentField = new JFormattedTextField(moneyFormatter);
        depositField = new JFormattedTextField(moneyFormatter);

        // 日期选择器
        purchaseDatePicker = new DatePicker();

        // 字体
        JTextField[] fields = {licensePlateField, brandField, modelField, colorField, rentField, depositField};
        for (JTextField f : fields) f.setFont(font);

        saveButton = new JButton("保存");
        cancelButton = new JButton("取消");
        saveButton.setFont(font);
        cancelButton.setFont(font);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"车辆ID:", "车牌号:", "品牌:", "型号:", "颜色:", "状态:", "日租金:", "押金:", "购买日期:"};
        Component[] components = {carIdField, licensePlateField, brandField, modelField, colorField, statusComboBox, rentField, depositField, purchaseDatePicker};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            mainPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            mainPanel.add(components[i], gbc);
        }

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        saveButton.addActionListener(e -> saveCar());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupDialog() {
        setSize(400, 380);
        setResizable(false);
        setLocationRelativeTo(getParent());
    }

    private void loadCarData() {
        licensePlateField.setText(car.getLicensePlateNumber());
        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        colorField.setText(car.getColor());
        statusComboBox.setSelectedItem(car.getStatus());
        rentField.setValue(car.getRent());
        depositField.setValue(new BigDecimal(car.getDeposit()));
        if (car.getPurchaseDate() != null)
            purchaseDatePicker.setSelectedDate(car.getPurchaseDate());
    }

    private void saveCar() {
        // 输入验证
        if (carIdField.getText().trim().isEmpty()) {showError("请输入车辆ID"); return;}
        if (licensePlateField.getText().trim().isEmpty()) { showError("请输入车牌号"); return; }
        if (brandField.getText().trim().isEmpty()) { showError("请输入品牌"); return; }
        if (modelField.getText().trim().isEmpty()) { showError("请输入型号"); return; }
        if (colorField.getText().trim().isEmpty()) { showError("请输入颜色"); return; }
        if (rentField.getValue() == null) { showError("请输入日租金"); return; }
        if (depositField.getValue() == null) { showError("请输入押金"); return; }
        if (!purchaseDatePicker.isValidDate()) { showError("请选择有效的购买日期"); return; }

        try {
            Car carToSave = car == null ? new Car() : car;
            carToSave.setCarId(Integer.parseInt(carIdField.getText().trim()));
            carToSave.setLicensePlateNumber(licensePlateField.getText().trim());
            carToSave.setBrand(brandField.getText().trim());
            carToSave.setModel(modelField.getText().trim());
            carToSave.setColor(colorField.getText().trim());
            carToSave.setStatus((String) statusComboBox.getSelectedItem());
            carToSave.setRent(new BigDecimal(rentField.getText().trim()));
            carToSave.setDeposit(depositField.getText().trim());
            carToSave.setPurchaseDate(purchaseDatePicker.getSelectedDate());

            boolean success = (car == null) ? carService.addCar(carToSave) : carService.updateCar(carToSave);
            if (success) {
                JOptionPane.showMessageDialog(this, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                parentPanel.refreshData();
                dispose();
            } else {
                showError("保存失败");
            }
        } catch (Exception ex) {
            showError("保存失败: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }

}