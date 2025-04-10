import React, { useEffect, useState } from 'react';
import { Typography, Row, Col, Card, Statistic, Divider, Tag, List } from 'antd';
import { 
  ShoppingOutlined, 
  InboxOutlined, 
  UserOutlined, 
  CarOutlined,
  BankOutlined,
  ShoppingCartOutlined
} from '@ant-design/icons';
import { useAppSelector } from '../store/hooks';
import UserProfile from './UserProfile';
import { authService } from '../services/auth.service';

const { Title, Text, Paragraph } = Typography;

// 定义不同角色的权限和职责
interface RoleInfo {
  description: string;
  color: string;
  permissions: string[];
  features: string[];
}

interface RolePermissions {
  [key: string]: RoleInfo;
}

const rolePermissions: RolePermissions = {
  'ROLE_SUPER_ADMIN': {
    description: '超级管理员',
    color: 'red',
    permissions: [
      '所有模块的查看和管理权限',
      '用户管理',
      '系统设置',
      '报表分析',
      '所有业务数据的管理权限'
    ],
    features: ['产品管理', '库存管理', '仓库管理', '订单管理', '车辆管理', '物流管理', '任务管理', '用户管理', '报表分析', '系统设置']
  },
  'ROLE_CITY_OPERATOR': {
    description: '城市运营商',
    color: 'blue',
    permissions: [
      '区域内仓库和车辆的管理',
      '库存管理',
      '物流管理',
      '任务安排'
    ],
    features: ['库存管理', '仓库管理', '车辆管理', '物流管理', '任务管理', '报表分析']
  },
  'ROLE_AGENT': {
    description: '代理商',
    color: 'green',
    permissions: [
      '商品调拨',
      '库存查看',
      '订单处理',
      '任务查看'
    ],
    features: ['产品管理', '库存管理', '订单管理', '任务管理']
  },
  'ROLE_SUPPLIER': {
    description: '供应商',
    color: 'orange',
    permissions: [
      '商品管理',
      '订单查看'
    ],
    features: ['产品管理']
  },
  'ROLE_STORE': {
    description: '门店',
    color: 'purple',
    permissions: [
      '订单管理',
      '库存查看',
      '物流查看'
    ],
    features: ['库存管理', '订单管理', '物流管理']
  }
};

const Dashboard: React.FC = () => {
  const { user } = useAppSelector((state) => state.auth);
  const [userRole, setUserRole] = useState<string>('');
  const [username, setUsername] = useState<string>('');

  useEffect(() => {
    const user = authService.getCurrentUser();
    if (user) {
      setUsername(user.username);
      // 如果有多个角色，使用第一个角色
      setUserRole(user.roles && user.roles.length > 0 ? user.roles[0] : '');
    }
  }, []);

  // 获取当前角色的权限信息
  const roleInfo = userRole && rolePermissions[userRole] ? rolePermissions[userRole] : {
    description: '未知角色',
    color: 'default',
    permissions: [],
    features: []
  };

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>仓库管理系统 - 仪表盘</Title>
      <Paragraph>欢迎回来，{username || '用户'}！您当前的角色是：
        <Tag color={roleInfo.color}>
          {roleInfo.description || userRole.replace('ROLE_', '') || '未知角色'}
        </Tag>
      </Paragraph>

      <Divider orientation="left">角色权限</Divider>
      
      <Row gutter={16}>
        <Col span={12}>
          <Card title="您的权限" bordered={false}>
            {roleInfo.permissions ? (
              <List
                size="small"
                dataSource={roleInfo.permissions}
                renderItem={item => <List.Item>• {item}</List.Item>}
              />
            ) : (
              <Text>未找到角色权限信息</Text>
            )}
          </Card>
        </Col>
        
        <Col span={12}>
          <Card title="可访问功能" bordered={false}>
            {roleInfo.features ? (
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                {roleInfo.features.map((feature: string) => (
                  <Tag key={feature} color="blue">{feature}</Tag>
                ))}
              </div>
            ) : (
              <Text>未找到可访问功能信息</Text>
            )}
          </Card>
        </Col>
      </Row>

      <Divider orientation="left">系统概况</Divider>
      
      <Row gutter={16} style={{ marginBottom: '24px' }}>
        <Col span={24}>
          <Card title="用户信息">
            <UserProfile />
          </Card>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col span={6}>
          <Card>
            <Statistic
              title="产品总数"
              value={128}
              prefix={<ShoppingOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="库存总量"
              value={2639}
              prefix={<InboxOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="订单总数"
              value={358}
              prefix={<ShoppingCartOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="仓库数量"
              value={12}
              prefix={<BankOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <div style={{ marginTop: '24px' }}>
        <Card title="系统公告">
          <p>欢迎使用仓库管理系统! 本系统提供全面的仓库管理功能，包括产品管理、库存管理、订单处理和配送管理。</p>
          <p>当前登录用户: {user?.username}，角色: {user?.roles.join(', ')}</p>
          <p>如有任何问题，请联系系统管理员。</p>
        </Card>
      </div>
    </div>
  );
};

export default Dashboard; 