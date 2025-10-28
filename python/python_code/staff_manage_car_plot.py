'''
MacOS专用字体设置
MacOS 系统中使用的中文字体路径
适用于MacOS15版本
可直接复制到代码中使用
'''
import matplotlib.pyplot as plt
from matplotlib import font_manager
# 设置字体路径
font_path = '/System/Library/Fonts/STHeiti Medium.ttc'

# 加载字体
my_font = font_manager.FontProperties(fname=font_path)

# 设置为默认字体
plt.rcParams['font.family'] = my_font.get_name()
plt.rcParams['axes.unicode_minus'] = False  # 正确显示负号

import mysql.connector
import matplotlib.pyplot as plt

# 建立数据库连接
db_connection = mysql.connector.connect(
    host="localhost",       # 数据库主机地址
    user="root",            # 用户名
    password="12345678",    # 密码
    database="car_rental_system"   # 数据库名
)

cursor = db_connection.cursor()

# 查询员工和他们管理的车辆数量
query = """
    SELECT s.name, COUNT(r.car_id) AS car_count
    FROM rent_information r
    JOIN staff s ON r.staff_id = s.staff_id
    GROUP BY s.staff_id
"""
cursor.execute(query)

# 获取查询结果
results = cursor.fetchall()

# 提取员工名称和车辆数量
staff_names = [row[0] for row in results]
car_counts = [row[1] for row in results]

# 绘制饼图
plt.figure(figsize=(7, 7))
plt.pie(
    car_counts,
    labels=staff_names,
    autopct='%1.1f%%',
    startangle=90,
    colors=['#61c0bf','#bbded6','#fae3d9','#ffb6b9','#c2c2f0','#ff6666','#ffccff','#99ffcc','#ccffcc','#ffb3e6']
    )
plt.title('员工管理车辆数量分布')
plt.axis('equal')  # 使饼图为圆形
plt.savefig('/Volumes/HIKSEMI/GitHub_project/java_car_rent_system/git-JavaCourseProject/python/plot/staff_manage_car_plot.png')

# 关闭数据库连接
cursor.close()
db_connection.close()
