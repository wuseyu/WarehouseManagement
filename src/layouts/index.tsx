import React, { useState, useEffect } from 'react';
import {
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  ShoppingOutlined,
  InboxOutlined,
  ShopOutlined,
  CarOutlined,
  UserOutlined,
  OrderedListOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { Layout, Menu, Button, theme, Avatar, Dropdown } from 'antd';
import { Outlet, Link, useLocation, history } from 'umi';
import { logout } from '../services/auth';
import styles from './index.less';

const { Header, Sider, Content } = Layout;

type MenuItem = Required<MenuProps>['items'][number];

function getItem(
  label: React.ReactNode,
  key: React.Key,
  icon?: React.ReactNode,
  children?: MenuItem[],
): MenuItem {
  return {
    key,
    icon,
    children,
    label,
  } as MenuItem;
}

const items: MenuItem[] = [
  getItem('仪表盘', '/dashboard', <DashboardOutlined />),
  getItem('产品管理', '/product', <ShoppingOutlined />),
  getItem('库存管理', '/inventory', <InboxOutlined />),
  getItem('订单管理', '/order', <OrderedListOutlined />),
  getItem('仓库管理', '/warehouse', <ShopOutlined />),
  getItem('车辆管理', '/vehicle', <CarOutlined />),
  getItem('用户管理', '/user', <UserOutlined />),
];

const BaseLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const { token: { colorBgContainer, borderRadiusLG } } = theme.useToken();
  const location = useLocation();
  const [selectedKeys, setSelectedKeys] = useState<string[]>(['/dashboard']);

  useEffect(() => {
    // 获取当前路径作为选中的菜单项
    const pathKey = location.pathname;
    setSelectedKeys([pathKey]);
  }, [location.pathname]);

  // 判断是否已登录
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token && location.pathname !== '/login') {
      history.push('/login');
    }
  }, [location.pathname]);

  // 如果是登录页，不显示布局
  if (location.pathname === '/login') {
    return <Outlet />;
  }

  const dropdownItems = [
    {
      key: 'profile',
      label: '个人资料',
      icon: <UserOutlined />,
    },
    {
      key: 'logout',
      label: '退出登录',
      icon: <LogoutOutlined />,
      onClick: logout,
    },
  ];

  const username = localStorage.getItem('username') || '用户';

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider 
        trigger={null} 
        collapsible 
        collapsed={collapsed}
        theme="light"
        className={styles.sider}
      >
        <div className={styles.logo}>
          {!collapsed ? '仓储物流管理系统' : '仓储'}
        </div>
        <Menu
          theme="light"
          mode="inline"
          selectedKeys={selectedKeys}
          items={items}
          onClick={({key}) => {
            history.push(key as string);
          }}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }}>
          <div className={styles.headerContent}>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              className={styles.trigger}
            />
            <div className={styles.headerRight}>
              <Dropdown menu={{ items: dropdownItems }} placement="bottomRight">
                <div className={styles.userInfo}>
                  <Avatar icon={<UserOutlined />} />
                  <span className={styles.username}>{username}</span>
                </div>
              </Dropdown>
            </div>
          </div>
        </Header>
        <Content className={styles.content}>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default BaseLayout; 