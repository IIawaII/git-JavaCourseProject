# 汽车出租管理系统 - 快速开始指南

## 🚀 快速启动

### 1. 准备工作

**必需环境：**
- JDK 8 或更高版本
- MySQL 8.0 数据库
- MySQL JDBC驱动（mysql-connector-java-8.0.33.jar）

### 2. 下载MySQL驱动

从以下地址下载MySQL JDBC驱动：
- 官网：https://dev.mysql.com/downloads/connector/j/
- 或者使用Maven/Gradle（参见 MySQL_DRIVER_DOWNLOAD.md）

将 `mysql-connector-java-8.0.33.jar` 文件放在项目根目录下。

### 3. 配置数据库

数据库连接信息（已在代码中配置）：
- 地址：10.245.203.137:3306
- 数据库名：car_rental_system
- 用户名：root
- 密码：12345678

### 4. 编译和运行

**Windows系统：**
```bash
双击运行 compile_and_run.bat
```

**Linux/Mac系统：**
```bash
chmod +x compile_and_run.sh
./compile_and_run.sh
```

**手动编译：**
```bash
# Windows
javac -cp ".;mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/*.java src/main/java/com/carrental/**/*.java
java -cp ".;mysql-connector-java-8.0.33.jar" com.carrental.CarRentalSystem

# Linux/Mac
javac -cp ".:mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/*.java src/main/java/com/carrental/**/*.java
java -cp ".:mysql-connector-java-8.0.33.jar" com.carrental.CarRentalSystem
```

### 5. 登录系统

系统提供了以下测试账号：

| 用户名 | 密码 | 权限级别 | 可用功能 |
|--------|------|----------|----------|
| super213 | 213 | 董事长(9) | 所有功能 |
| 俞锦豪 | 111 | 经理(6) | 车辆、租车、用户、员工管理 |
| user1 | user123 | 员工(2) | 车辆、租车管理 |

## 📋 主要功能

### 车辆管理
- ✅ 添加新车辆
- ✅ 修改车辆信息
- ✅ 删除车辆
- ✅ 查看车辆详情
- ✅ 搜索和筛选车辆

### 租车管理
- ✅ 新增租车记录
- ✅ 处理还车业务
- ✅ 自动计算租金
- ✅ 查看租车详情
- ✅ 生成租车合同

### 用户管理（经理及以上）
- 用户信息管理
- 会员状态管理
- 信誉度评价

### 员工管理（经理及以上）
- 员工信息管理
- 权限等级管理
- 职位管理

### 财务报表（董事长）
- 收益统计
- 数据分析

## 🎯 使用流程示例

### 租车流程
1. 登录系统
2. 进入"租车管理"标签页
3. 点击"新增租车"按钮
4. 选择车辆和用户
5. 设置租借和归还日期
6. 点击"计算租金"查看费用
7. 确认信息后点击"确认租车"
8. 可选：点击"生成合同"生成租车合同

### 还车流程
1. 进入"租车管理"标签页
2. 选择要还车的记录
3. 点击"还车"按钮
4. 输入实际归还日期和损坏费用
5. 点击"计算费用"查看退还金额
6. 确认后点击"确认还车"

## ⚠️ 注意事项

1. **首次运行**：确保数据库连接正常
2. **权限管理**：不同级别员工看到的功能不同
3. **数据验证**：系统会自动验证输入数据的合法性
4. **车辆状态**：只能租借状态为"空闲"的车辆
5. **日期格式**：输入日期时使用 yyyy-MM-dd 格式（如：2024-01-01）

## 🐛 常见问题

**Q: 编译时提示找不到MySQL驱动？**
A: 确保 mysql-connector-java-8.0.33.jar 文件在项目根目录下。

**Q: 无法连接数据库？**
A: 检查数据库服务是否启动，网络连接是否正常。

**Q: 登录失败？**
A: 确认用户名和密码是否正确，检查数据库中的staff表。

**Q: 界面显示乱码？**
A: 确保系统支持中文字体，可能需要安装微软雅黑字体。

## 📞 技术支持

如遇到问题，请检查：
1. Java版本是否为JDK 8或更高
2. MySQL驱动是否正确放置
3. 数据库连接信息是否正确
4. 控制台错误信息

## 📝 开发说明

- 项目使用Java 8开发
- GUI框架：Swing
- 数据库：MySQL 8.0
- 架构：MVC三层架构
- 设计模式：单例模式、工厂模式

祝使用愉快！🎉

