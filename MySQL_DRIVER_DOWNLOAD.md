# MySQL JDBC驱动下载说明

## 下载MySQL JDBC驱动

由于版权原因，MySQL JDBC驱动需要单独下载。请按照以下步骤操作：

### 方法1：从MySQL官网下载

1. 访问MySQL官网：https://dev.mysql.com/downloads/connector/j/
2. 选择 "Platform Independent" 版本
3. 下载 `mysql-connector-java-8.0.33.jar` 文件
4. 将下载的jar文件放在项目根目录下

### 方法2：使用Maven（如果使用Maven）

在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### 方法3：使用Gradle（如果使用Gradle）

在 `build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation 'mysql:mysql-connector-java:8.0.33'
}
```

## 验证驱动

下载完成后，确保项目根目录下有 `mysql-connector-java-8.0.33.jar` 文件，然后运行编译脚本即可。

## 注意事项

- 确保下载的驱动版本与代码中使用的版本一致
- 如果使用不同版本的驱动，请相应修改编译脚本中的jar文件名
- 驱动文件必须放在项目根目录下，与编译脚本在同一位置
