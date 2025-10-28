import mysql.connector
import plotly.graph_objects as go

# 建立数据库连接
db_connection = mysql.connector.connect(
    host="localhost",       # 数据库主机地址
    user="root",            # 用户名
    password="12345678",    # 密码
    database="car_rental_system"   # 数据库名
)

# 创建一个游标对象
cursor = db_connection.cursor()

# 执行查询操作
car_id_query = "SELECT car_id FROM rent_information;"
user_pay_query = "SELECT pay_the_amount FROM rent_information;"
return_pay_query = "SELECT return_amount FROM rent_information;"
maintain_cost_query = "SELECT maimtain_cost FROM maintain_information;"

# 执行查询并获取结果
cursor.execute(car_id_query)
car_ids = cursor.fetchall()  # 获取所有 car_id
cursor.execute(user_pay_query)
user_pays = cursor.fetchall()  # 获取所有 pay_the_amount
cursor.execute(return_pay_query)
return_pays = cursor.fetchall()  # 获取所有 return_amount
cursor.execute(maintain_cost_query)
maintain_costs = cursor.fetchall()  # 获取所有 maintain_cost

# 关闭游标和数据库连接
cursor.close()
db_connection.close()

# 打印查询结果
print("Combined Results:")
total_profit = 0
for car_id, user_pay, return_pay, maintain_cost in zip(car_ids, user_pays, return_pays, maintain_costs):
    # 如果维护成本是空值（NULL），则设置为0
    maintain_cost_value = maintain_cost[0] if maintain_cost[0] is not None else 0
    # 计算利润 = 用户支付金额 - 归还金额 - 维护成本
    profit = user_pay[0] - return_pay[0] - maintain_cost_value
    total_profit += profit  # 累加总利润
    # 打印结果，包括利润
    print(f"Car ID: {car_id[0]}, User Payment: {user_pay[0]}, Return Payment: {return_pay[0]}, Maintain Cost: {maintain_cost_value}, Profit: {profit}")

# 目标利润
target_profit = 1000

# 计算整体进度
progress = min(total_profit / target_profit, 1.0) * 100  # 计算进度百分比

# 创建仪表盘图
fig = go.Figure(go.Indicator(
    mode="gauge+number+delta",
    value=progress,
    delta={'reference': 100, 'increasing': {'color': "RebeccaPurple"}},
    gauge={
        'axis': {'range': [0, 100], 'tickwidth': 1, 'tickcolor': "darkblue"},
        'bar': {'color': "#71c9ce"},
        'bgcolor': "white",
        'borderwidth': 2,
        'bordercolor': "gray",
        'steps': [
            {'range': [0, 50], 'color': '#e3fdfd'},
            {'range': [50, 80], 'color': '#a6e3e9'},
            {'range': [80, 100], 'color': '#71c9ce'}
        ],
        'threshold': {
            'line': {'color': "red", 'width': 4},
            'thickness': 0.75,
            'value': progress
        }
    },
    title={'text': "Total Profit Progress"}
))

fig.update_layout(
    paper_bgcolor="white",
    font={'color': "darkblue", 'family': "Arial"},
    height=400,
    width=500
)


fig.write_image("/Volumes/HIKSEMI/GitHub_project/java_car_rent_system/git-JavaCourseProject/python/plot/total_profit_progress.png")
print("Gauge chart saved as 'total_profit_progress.png'")