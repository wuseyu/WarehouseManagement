import React from 'react';
import { Avatar, Card, Typography, Button } from 'antd';
import { UserOutlined, LogoutOutlined } from '@ant-design/icons';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { logout } from '../store/slices/authSlice';

const { Title, Text } = Typography;

const UserProfile: React.FC = () => {
  const dispatch = useAppDispatch();
  const { user } = useAppSelector((state) => state.auth);

  const handleLogout = () => {
    dispatch(logout());
  };

  if (!user) {
    return <Text>请先登录</Text>;
  }

  return (
    <Card style={{ width: 300, margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: 20 }}>
        <Avatar size={64} icon={<UserOutlined />} />
        <Title level={4} style={{ marginTop: 16 }}>{user.username}</Title>
        <Text type="secondary">用户ID: {user.id}</Text>
      </div>
      
      <div style={{ marginBottom: 16 }}>
        <Text strong>角色:</Text>
        <div>
          {user.roles.map((role: string, index: number) => (
            <Text key={index} style={{ display: 'block' }}>{role}</Text>
          ))}
        </div>
      </div>
      
      <Button 
        type="primary" 
        danger 
        icon={<LogoutOutlined />} 
        onClick={handleLogout}
        block
      >
        退出登录
      </Button>
    </Card>
  );
};

export default UserProfile; 