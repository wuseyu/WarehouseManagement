import React from 'react';
import { Typography, Card, Breadcrumb } from 'antd';
import { HomeOutlined, UserOutlined } from '@ant-design/icons';
import UserProfile from '../components/UserProfile';
import { Link } from 'react-router-dom';

const { Title } = Typography;

const ProfilePage: React.FC = () => {
  return (
    <div>
      <Breadcrumb style={{ marginBottom: 16 }}>
        <Breadcrumb.Item>
          <Link to="/"><HomeOutlined /> 首页</Link>
        </Breadcrumb.Item>
        <Breadcrumb.Item>
          <UserOutlined /> 个人信息
        </Breadcrumb.Item>
      </Breadcrumb>
      
      <Title level={2}>个人信息</Title>
      <UserProfile />
    </div>
  );
};

export default ProfilePage; 