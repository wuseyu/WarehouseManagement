import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Typography, Table, Tag } from 'antd';
import {
  ShoppingCartOutlined,
  ShopOutlined,
  InboxOutlined,
  CarOutlined,
} from '@ant-design/icons';
import ReactECharts from 'echarts-for-react';
import { get } from '../../utils/request';
import { Order, OrderStatus, Product } from '../../types';

const { Title } = Typography;

interface DashboardData {
  orderCount: number;
  productCount: number;
  warehouseCount: number;
  vehicleCount: number;
  recentOrders: Order[];
  topProducts: Product[];
}

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [dashboardData, setDashboardData] = useState<DashboardData>({
    orderCount: 0,
    productCount: 0,
    warehouseCount: 0,
    vehicleCount: 0,
    recentOrders: [],
    topProducts: [],
  });

  // 模拟获取仪表盘数据
  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        // TODO: 替换为实际的API调用
        // const response = await get<DashboardData>('/dashboard');
        // setDashboardData(response);

        // 模拟数据
        setTimeout(() => {
          setDashboardData({
            orderCount: 256,
            productCount: 120,
            warehouseCount: 5,
            vehicleCount: 18,
            recentOrders: [
              {
                id: 1,
                orderNo: 'ORD-2023-04-01-0001',
                user: {
                  id: 1,
                  username: '张三',
                  roles: [],
                  createdAt: '',
                  updatedAt: ''
                },
                deliveryAddress: '上海市浦东新区张江高科技园区',
                status: OrderStatus.PROCESSING,
                totalAmount: 1200,
                createdAt: '2023-04-01 10:20:30',
                updatedAt: '2023-04-01 10:30:45',
                orderItems: []
              },
              {
                id: 2,
                orderNo: 'ORD-2023-04-01-0002',
                user: {
                  id: 2,
                  username: '李四',
                  roles: [],
                  createdAt: '',
                  updatedAt: ''
                },
                deliveryAddress: '北京市海淀区中关村',
                status: OrderStatus.SHIPPED,
                totalAmount: 850,
                createdAt: '2023-04-01 11:15:22',
                updatedAt: '2023-04-01 11:30:00',
                orderItems: []
              },
              {
                id: 3,
                orderNo: 'ORD-2023-04-01-0003',
                user: {
                  id: 3,
                  username: '王五',
                  roles: [],
                  createdAt: '',
                  updatedAt: ''
                },
                deliveryAddress: '广州市天河区珠江新城',
                status: OrderStatus.PENDING,
                totalAmount: 2200,
                createdAt: '2023-04-01 12:05:10',
                updatedAt: '2023-04-01 12:05:10',
                orderItems: []
              },
            ],
            topProducts: [],
          });
          setLoading(false);
        }, 1000);
      } catch (error) {
        console.error('获取仪表盘数据失败:', error);
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  // 订单状态分布图表选项
  const orderStatusOptions = {
    title: {
      text: '订单状态分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
    },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '待处理' },
          { value: 30, name: '处理中' },
          { value: 25, name: '已发货' },
          { value: 10, name: '已送达' },
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };

  // 近7天订单趋势图表选项
  const orderTrendOptions = {
    title: {
      text: '近7天订单趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['4-1', '4-2', '4-3', '4-4', '4-5', '4-6', '4-7']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: [20, 15, 25, 30, 22, 18, 32],
        type: 'line',
        smooth: true
      }
    ]
  };

  // 订单表格列定义
  const orderColumns = [
    {
      title: '订单号',
      dataIndex: 'orderNo',
      key: 'orderNo',
    },
    {
      title: '客户',
      dataIndex: ['user', 'username'],
      key: 'username',
    },
    {
      title: '金额',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (amount: number) => `¥${amount.toFixed(2)}`,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: OrderStatus) => {
        let color = 'default';
        let text = '未知状态';
        
        switch (status) {
          case OrderStatus.PENDING:
            color = 'gold';
            text = '待处理';
            break;
          case OrderStatus.PROCESSING:
            color = 'blue';
            text = '处理中';
            break;
          case OrderStatus.SHIPPED:
            color = 'geekblue';
            text = '已发货';
            break;
          case OrderStatus.DELIVERED:
            color = 'green';
            text = '已送达';
            break;
          case OrderStatus.CANCELLED:
            color = 'red';
            text = '已取消';
            break;
        }
        
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
  ];

  return (
    <div>
      <Title level={3}>仪表盘</Title>
      <Row gutter={16}>
        <Col xs={24} sm={12} md={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="订单总数"
              value={dashboardData.orderCount}
              prefix={<ShoppingCartOutlined />}
              valueStyle={{ color: '#1677ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="产品总数"
              value={dashboardData.productCount}
              prefix={<InboxOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="仓库数量"
              value={dashboardData.warehouseCount}
              prefix={<ShopOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={12} lg={6}>
          <Card loading={loading}>
            <Statistic
              title="车辆数量"
              value={dashboardData.vehicleCount}
              prefix={<CarOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginTop: '24px' }}>
        <Col xs={24} md={12}>
          <Card title="订单状态分布" loading={loading}>
            <ReactECharts option={orderStatusOptions} style={{ height: 300 }} />
          </Card>
        </Col>
        <Col xs={24} md={12}>
          <Card title="近7天订单趋势" loading={loading}>
            <ReactECharts option={orderTrendOptions} style={{ height: 300 }} />
          </Card>
        </Col>
      </Row>

      <Row style={{ marginTop: '24px' }}>
        <Col span={24}>
          <Card title="最近订单" loading={loading}>
            <Table
              columns={orderColumns}
              dataSource={dashboardData.recentOrders}
              rowKey="id"
              pagination={{ pageSize: 5 }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard; 