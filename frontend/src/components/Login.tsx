import React from 'react';
import { Card, message, Typography, Divider, Table } from 'antd';
import { useNavigate } from 'react-router-dom';
import LoginForm from './LoginForm';
import { useAppSelector } from '../store/hooks';

const { Title, Text } = Typography;

// 默认账号数据
const defaultAccounts = [
  { username: 'admin', password: 'admin123', role: '超级管理员', description: '系统最高权限' },
  { username: 'operator', password: 'operator123', role: '城市运营商', description: '管理区域仓库' },
  { username: 'agent', password: 'agent123', role: '代理商', description: '负责商品调拨' },
  { username: 'supplier', password: 'supplier123', role: '供应商', description: '商品供应' },
  { username: 'store', password: 'store123', role: '门店', description: '终端销售' },
];

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  // 如果已经登录，重定向到首页
  React.useEffect(() => {
    if (isAuthenticated) {
      navigate('/');
    }
  }, [isAuthenticated, navigate]);

  const handleLoginSuccess = () => {
    message.success('登录成功');
    navigate('/');
  };

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      height: '100vh',
      background: '#f0f2f5'
    }}>
      <Card style={{ width: 800, boxShadow: '0 4px 8px rgba(0,0,0,0.1)' }}>
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <Title level={2}>仓库管理系统</Title>
          <Title level={4} style={{ marginTop: 0 }}>用户登录</Title>
        </div>
        
        <div style={{ display: 'flex' }}>
          <div style={{ width: '50%', padding: '0 10px' }}>
            <LoginForm onSuccess={handleLoginSuccess} />
          </div>
          
          <Divider type="vertical" style={{ height: 'auto' }} />
          
          <div style={{ width: '50%', padding: '0 10px' }}>
            <Text strong>测试账号信息</Text>
            <Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
              （注意：这些是测试账号，仅用于演示不同权限级别）
            </Text>
            
            <Table 
              columns={[
                {
                  title: '用户名',
                  dataIndex: 'username',
                  key: 'username',
                },
                {
                  title: '密码',
                  dataIndex: 'password',
                  key: 'password',
                },
                {
                  title: '角色',
                  dataIndex: 'role',
                  key: 'role',
                },
                {
                  title: '描述',
                  dataIndex: 'description',
                  key: 'description',
                }
              ]}
              dataSource={defaultAccounts} 
              size="small" 
              pagination={false}
              rowKey="username"
            />
          </div>
        </div>
      </Card>
    </div>
  );
};

export default Login; 