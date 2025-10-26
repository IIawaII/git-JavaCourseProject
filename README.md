# 汽车出租管理系统

## 项目简介

这是一个基于Java 8开发的汽车出租管理系统，使用Swing GUI框架实现用户界面，MySQL数据库进行数据持久化存储。系统提供了完整的汽车租赁管理功能，包括车辆管理、租车管理、用户管理、员工管理等核心功能。

## 技术栈

- **开发语言**: Java 8
- **GUI框架**: Swing
- **数据库**: MySQL 8.0
- **数据库连接**: JDBC
- **架构模式**: MVC (Model-View-Controller)

## 系统功能

### 1. 用户登录
- 员工身份验证
- 权限分级管理
- 安全登录机制

### 2. 车辆管理
- 车辆信息的增删改查
- 车辆状态管理（空闲、已借出、维修中）
- 车辆搜索和筛选
- 车辆详情查看

### 3. 租车管理
- 租车业务处理
- 还车业务处理
- 租金计算
- 租车记录查询
- 合同生成

### 4. 用户管理
- 用户信息管理
- 用户信誉度评价
- 会员状态管理

### 5. 员工管理
- 员工信息管理
- 权限等级管理
- 职位管理

### 6. 财务报表
- 利润分析（基于数据库视图）
- 未交罚款统计
- 员工管理车辆数量统计
- 已维修车辆统计

### 7. 损坏管理
- 车辆损坏记录管理
- 损坏状态跟踪
- 损坏信息查询和筛选

### 8. 维修管理
- 车辆维修记录管理
- 维修费用统计
- 维修进度跟踪

### 9. 违章罚款管理
- 违章罚款记录管理
- 罚款状态管理
- 罚款统计分析

### 10. 数据统计和分析
- 车辆统计（按状态、品牌）
- 用户统计（按会员状态、信誉度）
- 员工统计（按职位、权限等级）
- 租车统计（收入、平均金额）
- 财务统计（维修费用、罚款统计）

## 数据库设计

系统使用MySQL数据库，包含以下主要表：

- `car`: 车辆信息表
- `user`: 用户信息表
- `staff`: 员工信息表
- `rent_information`: 租车信息表
- `damage_information`: 损坏信息表
- `maintain_information`: 维修信息表
- `traffic_fine`: 违章罚款表
- `financial_statement`: 财务报表表

## 项目结构

```
src/main/java/com/carrental/
├── CarRentalSystem.java          # 主类
├── entity/                       # 实体类包
│   ├── Car.java                  # 车辆实体
│   ├── User.java                 # 用户实体
│   ├── Staff.java                # 员工实体
│   ├── RentInformation.java      # 租车信息实体
│   ├── DamageInformation.java    # 损坏信息实体
│   ├── MaintainInformation.java  # 维修信息实体
│   └── TrafficFine.java          # 违章罚款实体
├── dao/                          # 数据访问层
│   ├── CarDAO.java               # 车辆数据访问
│   ├── UserDAO.java              # 用户数据访问
│   ├── StaffDAO.java             # 员工数据访问
│   ├── RentInformationDAO.java   # 租车信息数据访问
│   ├── DamageInformationDAO.java # 损坏信息数据访问
│   ├── MaintainInformationDAO.java # 维修信息数据访问
│   └── TrafficFineDAO.java       # 违章罚款数据访问
├── service/                      # 业务逻辑层
│   ├── CarService.java           # 车辆业务逻辑
│   ├── UserService.java          # 用户业务逻辑
│   └── RentService.java          # 租车业务逻辑
├── gui/                          # 图形界面包
│   ├── LoginFrame.java           # 登录界面
│   ├── MainFrame.java            # 主界面
│   ├── CarManagementPanel.java   # 车辆管理面板
│   ├── RentManagementPanel.java  # 租车管理面板
│   ├── DamageManagementPanel.java # 损坏管理面板
│   ├── MaintainManagementPanel.java # 维修管理面板
│   ├── TrafficFineManagementPanel.java # 违章罚款管理面板
│   ├── FinancialReportPanel.java # 财务报表面板
│   ├── StatisticsPanel.java      # 数据统计面板
│   ├── CarDialog.java            # 车辆信息对话框
│   ├── RentCarDialog.java        # 租车对话框
│   ├── ReturnCarDialog.java      # 还车对话框
│   ├── ContractDialog.java       # 合同生成对话框
│   ├── DamageDialog.java          # 损坏信息对话框
│   ├── MaintainDialog.java       # 维修信息对话框
│   ├── TrafficFineDialog.java    # 违章罚款对话框
│   └── ...                       # 其他界面组件
└── util/                         # 工具类包
    └── DatabaseConnection.java    # 数据库连接工具
```

## 安装和运行

### 环境要求

- JDK 8 或更高版本
- MySQL 8.0 或更高版本
- MySQL JDBC驱动

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE car_rental_system;
```

2. 导入数据库结构（使用提供的SQL脚本）

3. 修改数据库连接配置：
   - 文件：`src/main/java/com/carrental/util/DatabaseConnection.java`
   - 修改URL、用户名、密码等连接参数

### 编译和运行

1. 编译项目：
```bash
javac -cp ".:mysql-connector-java-8.0.33.jar" src/main/java/com/carrental/*.java src/main/java/com/carrental/**/*.java
```

2. 运行程序：
```bash
java -cp ".:mysql-connector-java-8.0.33.jar:src/main/java" com.carrental.CarRentalSystem
```

## 默认登录账号

系统预设了以下测试账号：

- **董事长**: 用户名 `super213`, 密码 `213`
- **经理**: 用户名 `俞锦豪`, 密码 `111`
- **员工**: 用户名 `user1`, 密码 `user123`

## 系统特色

1. **面向对象设计**: 合理运用Java面向对象特性，使用抽象类、接口等结构
2. **分层架构**: 采用DAO-Service-GUI三层架构，代码结构清晰
3. **异常处理**: 完善的异常处理机制，提高系统稳定性
4. **数据验证**: 全面的输入验证，确保数据完整性
5. **用户友好**: 直观的图形界面，操作简单便捷
6. **权限管理**: 基于角色的权限控制，不同级别员工看到不同功能

## 开发规范

- 代码注释完整，符合JavaDoc规范
- 变量命名规范，使用驼峰命名法
- 方法职责单一，符合单一职责原则
- 异常处理完善，避免程序崩溃
- 数据库操作使用预编译语句，防止SQL注入

## 未来扩展

- 添加网络功能，支持多用户同时使用
- 集成支付系统，支持在线支付
- 添加车辆定位功能
- 实现数据备份和恢复
- 添加系统日志功能
- 支持多语言界面

## 许可证

本项目仅供学习和研究使用。
