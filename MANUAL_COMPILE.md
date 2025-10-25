# 手动编译指南

如果自动编译脚本有问题，可以按照以下步骤手动编译：

## Windows系统

### 1. 打开命令提示符
按 `Win + R`，输入 `cmd`，回车

### 2. 切换到项目目录
```cmd
cd C:\Users\woyych\Desktop\Java课设
```

### 3. 检查MySQL驱动
确保项目根目录下有 `mysql-connector-java-8.0.33.jar` 文件

### 4. 手动编译（按顺序执行）

```cmd
REM 编译实体类
javac -cp ".;mysql-connector-java-8.0.33.jar" -d . src\main\java\com\carrental\entity\*.java

REM 编译工具类
javac -cp ".;mysql-connector-java-8.0.33.jar" -d . src\main\java\com\carrental\util\*.java

REM 编译DAO类
javac -cp ".;mysql-connector-java-8.0.33.jar" -d . src\main\java\com\carrental\dao\*.java

REM 编译Service类
javac -cp ".;mysql-connector-java-8.0.33.jar" -d . src\main\java\com\carrental\service\*.java

REM 编译GUI类
javac -cp ".;mysql-connector-java-8.0.33.jar" -d . src\main\java\com\carrental\gui\*.java

REM 编译测试类
javac -cp ".;mysql-connector-java-8.0.33.jar" -d . src\main\java\com\carrental\test\*.java

REM 编译主类
javac -cp ".;mysql-connector-java-8.0.33.jar" -d . src\main\java\com\carrental\CarRentalSystem.java
```

### 5. 运行程序
```cmd
java -cp ".;mysql-connector-java-8.0.33.jar" com.carrental.CarRentalSystem
```

## Linux/Mac系统

### 1. 打开终端
### 2. 切换到项目目录
```bash
cd /path/to/Java课设
```

### 3. 手动编译
```bash
# 编译实体类
javac -cp ".:mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/entity/*.java

# 编译工具类
javac -cp ".:mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/util/*.java

# 编译DAO类
javac -cp ".:mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/dao/*.java

# 编译Service类
javac -cp ".:mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/service/*.java

# 编译GUI类
javac -cp ".:mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/gui/*.java

# 编译测试类
javac -cp ".:mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/test/*.java

# 编译主类
javac -cp ".:mysql-connector-java-8.0.33.jar" -d . src/main/java/com/carrental/CarRentalSystem.java
```

### 4. 运行程序
```bash
java -cp ".:mysql-connector-java-8.0.33.jar" com.carrental.CarRentalSystem
```

## 常见问题

### Q: 提示找不到mysql-connector-java-8.0.33.jar
A: 请确保该文件在项目根目录下，文件名完全正确

### Q: 提示找不到Java命令
A: 请确保已安装JDK并配置了环境变量

### Q: 编译时出现编码错误
A: 请确保源文件使用UTF-8编码保存

### Q: 运行时出现数据库连接错误
A: 请检查数据库服务是否启动，网络连接是否正常

## 推荐使用IDE

如果手动编译仍有问题，建议使用IDE（如IntelliJ IDEA、Eclipse等）：
1. 导入项目
2. 添加MySQL驱动到classpath
3. 直接运行CarRentalSystem.java
