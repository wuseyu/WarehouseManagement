import React, { useState, useMemo, useEffect } from 'react';
import { Layout, Menu, Button, Dropdown, Avatar, Space, Badge } from 'antd';
import {
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  UserOutlined,
  LogoutOutlined,
  HomeOutlined,
  ShoppingOutlined,
  InboxOutlined,
  ShoppingCartOutlined,
  CarOutlined,
  TeamOutlined,
  BankOutlined,
  SettingOutlined,
  BarChartOutlined,
  ClockCircleOutlined,
  ApiOutlined,
  FileSearchOutlined,
  RocketOutlined
} from '@ant-design/icons';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { authService } from '../services/auth.service';
import { useAppDispatch } from '../store/hooks';
import { logout } from '../store/slices/authSlice';

const { Header, Sider, Content } = Layout;

interface MainLayoutProps {
  children: React.ReactNode;
}

// 权限控制：根据用户角色控制可见菜单
const hasPermission = (userRoles: string[], requiredRoles: string[]) => {
  if (!requiredRoles || requiredRoles.length === 0) return true;
  return userRoles.some(role => requiredRoles.includes(role));
};

const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  
  const user = useMemo(() => authService.getCurrentUser(), []);
  const userRoles = useMemo(() => user?.roles || [], [user]);

  // 在组件挂载时输出用户角色信息，用于调试
  useEffect(() => {
    if (user) {
      console.log('当前用户信息:', {
        username: user.username,
        roles: user.roles,
        token: user.token ? `${user.token.substring(0, 15)}...` : '未设置'
      });
    } else {
      console.warn('用户未登录，将重定向到登录页');
      navigate('/login');
    }
  }, [user, navigate]);

  // 菜单配置，包含权限控制
  const menuItems = useMemo(() => {
    const allMenuItems = [
      {
        key: '/',
        icon: <HomeOutlined />,
        label: <Link to="/">仪表盘</Link>,
        roles: [] // 所有角色都可见
      },
      {
        key: '/products',
        icon: <ShoppingOutlined />,
        label: <Link to="/products">产品管理</Link>,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_SUPPLIER', 'ROLE_AGENT'] // 管理员、供应商和代理商可见
      },
      {
        key: '/inventory',
        icon: <InboxOutlined />,
        label: <Link to="/inventory">库存管理</Link>,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR', 'ROLE_AGENT', 'ROLE_STORE'] // 管理员、运营商、代理商和门店可见
      },
      {
        key: '/warehouses',
        icon: <BankOutlined />,
        label: <Link to="/warehouses">仓库管理</Link>,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR'] // 管理员和运营商可见
      },
      {
        key: '/orders',
        icon: <ShoppingCartOutlined />,
        label: <Link to="/orders">订单管理</Link>,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_STORE', 'ROLE_AGENT'] // 管理员、门店和代理商可见
      },
      {
        key: '/vehicles',
        icon: <CarOutlined />,
        label: <Link to="/vehicles">车辆管理</Link>,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR'] // 管理员和运营商可见
      },
      {
        key: '/shipments',
        icon: <RocketOutlined />,
        label: <Link to="/shipments">物流管理</Link>,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR', 'ROLE_STORE'] // 管理员、运营商和门店可见
      },
      {
        key: '/tasks',
        icon: <ClockCircleOutlined />,
        label: <Link to="/tasks">任务管理</Link>,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR', 'ROLE_AGENT'] // 管理员、运营商和代理商可见
      },
      {
        key: '/users',
        icon: <TeamOutlined />,
        label: <Link to="/users">用户管理</Link>,
        roles: ['ROLE_SUPER_ADMIN'] // 仅管理员可见
      },
      {
        key: '/reports',
        icon: <BarChartOutlined />,
        label: <Link to="/reports">报表分析</Link>,
        roles: ['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR'] // 管理员和运营商可见
      },
      {
        key: '/api-test',
        icon: <ApiOutlined />,
        label: <Link to="/api-test">API测试</Link>,
        roles: ['ROLE_SUPER_ADMIN'] // 仅管理员可见，开发工具
      },
      {
        key: '/settings',
        icon: <SettingOutlined />,
        label: <Link to="/settings">系统设置</Link>,
        roles: ['ROLE_SUPER_ADMIN'] // 仅管理员可见
      }
    ];

    // 根据用户角色过滤菜单
    return allMenuItems.filter(item => hasPermission(userRoles, item.roles));
  }, [userRoles]);

  const handleLogout = () => {
    // 使用Redux的登出方法
    dispatch(logout())
      .then(() => {
        // 登出成功后跳转到登录页面
        console.log('登出成功，跳转到登录页面');
        navigate('/login');
      })
      .catch((error: Error) => {
        console.error('登出失败:', error);
        // 即使登出失败，也尝试跳转到登录页面
        navigate('/login');
      });
  };

  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人信息',
      onClick: () => navigate('/profile'),
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      danger: true,
      onClick: handleLogout,
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed}>
        <div className="logo" style={{ 
          height: '64px', 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          color: 'white',
          fontSize: collapsed ? '14px' : '18px',
          padding: '16px'
        }}>
          {collapsed ? 'WMS' : '仓库管理系统'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
        />
      </Sider>
      <Layout className="site-layout">
        <Header className="site-layout-background" style={{ 
          padding: '0 16px', 
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          background: '#fff'
        }}>
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{ fontSize: '16px', width: 64, height: 64 }}
          />
          <Space>
            <Badge 
              count={5} 
              size="small" 
              style={{ marginRight: 24 }}
              title="未读消息"
            >
              <a href="/messages" style={{ color: 'rgba(0,0,0,0.65)' }}>
                <i className="anticon notification-icon"></i>
              </a>
            </Badge>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Space style={{ cursor: 'pointer' }}>
                <span>{user?.username || '用户'}</span>
                <Avatar icon={<UserOutlined />} />
                <div style={{ fontSize: '12px', color: '#1890ff' }}>
                  {userRoles[0]?.replace('ROLE_', '') || '用户'}
                </div>
              </Space>
            </Dropdown>
          </Space>
        </Header>
        <Content
          className="site-layout-background"
          style={{
            margin: '24px 16px',
            padding: 24,
            minHeight: 280,
            overflow: 'auto'
          }}
        >
          {children}
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout; 