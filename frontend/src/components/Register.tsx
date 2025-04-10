import React, { useEffect } from 'react';
import { Card, message, Typography, Button } from 'antd';
import { useNavigate } from 'react-router-dom';
import RegisterForm from './RegisterForm';
import { useAppSelector } from '../store/hooks';

const { Title } = Typography;

const Register: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  // 如果已经登录，重定向到首页
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/');
    }
  }, [isAuthenticated, navigate]);

  const handleRegisterSuccess = () => {
    message.success('注册成功，请登录');
    navigate('/login');
  };

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      height: '100vh',
      background: '#f0f2f5'
    }}>
      <Card style={{ width: 400, boxShadow: '0 4px 8px rgba(0,0,0,0.1)' }}>
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <Title level={2}>仓库管理系统</Title>
          <Title level={4} style={{ marginTop: 0 }}>用户注册</Title>
        </div>
        
        <RegisterForm onSuccess={handleRegisterSuccess} />
        
        <div style={{ textAlign: 'center', marginTop: 16 }}>
          已有账号？<Button type="link" onClick={() => navigate('/login')}>立即登录</Button>
        </div>
      </Card>
    </div>
  );
};

export default Register; 