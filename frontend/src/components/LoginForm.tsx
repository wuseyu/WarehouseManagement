import React, { useState } from 'react';
import { Form, Input, Button, Alert, message, Divider, Typography } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { login } from '../store/slices/authSlice';
import { LoginRequest } from '../types';

interface LoginFormProps {
  onSuccess?: () => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onSuccess }) => {
  const dispatch = useAppDispatch();
  const { loading, error } = useAppSelector((state) => state.auth);
  const [form] = Form.useForm();

  const onFinish = async (values: LoginRequest) => {
    try {
      console.log('尝试登录，提交的数据:', values);
      // 显示正在登录的loading提示
      const loadingMessage = message.loading('正在登录...', 0);
      
      try {
        const result = await dispatch(login(values)).unwrap();
        console.log('登录成功，返回结果:', result);
        
        // 关闭loading消息
        loadingMessage();
        
        message.success('登录成功，欢迎回来！');
        
        if (onSuccess) {
          onSuccess();
        }
      } catch (err: any) {
        // 关闭loading消息
        loadingMessage();
        
        console.error('登录失败，详细错误:', err);
        
        // 显示不同的错误消息
        if (err.message?.includes('用户名不存在')) {
          message.error('用户不存在，请检查用户名或注册新账号');
        } else if (err.message?.includes('密码不正确')) {
          message.error('密码不正确，请重新输入');
        } else if (err.message?.includes('403')) {
          message.error('没有权限登录，请联系管理员');
        } else {
          message.error('登录失败: ' + (err.message || '未知错误，请检查网络连接或服务器状态'));
        }
      }
    } catch (err: any) {
      console.error('登录过程中发生错误:', err);
      message.error('登录过程中发生错误');
    }
  };

  return (
    <Form
      form={form}
      name="login"
      initialValues={{ remember: true }}
      onFinish={onFinish}
      style={{ maxWidth: 300, margin: '0 auto' }}
    >
      {error && (
        <Form.Item>
          <Alert message={error} type="error" showIcon />
        </Form.Item>
      )}

      <Form.Item
        name="username"
        rules={[{ required: true, message: '请输入用户名!' }]}
      >
        <Input 
          prefix={<UserOutlined />} 
          placeholder="用户名" 
          size="large"
        />
      </Form.Item>

      <Form.Item
        name="password"
        rules={[{ required: true, message: '请输入密码!' }]}
      >
        <Input.Password
          prefix={<LockOutlined />}
          placeholder="密码"
          size="large"
        />
      </Form.Item>

      <Form.Item>
        <Button 
          type="primary" 
          htmlType="submit" 
          loading={loading}
          style={{ width: '100%' }}
          size="large"
        >
          登录
        </Button>
      </Form.Item>
      
      <Divider plain>
        <Typography.Text type="secondary" style={{ fontSize: '12px' }}>
          提示：如果登录失败，请尝试点击"初始化用户"按钮
        </Typography.Text>
      </Divider>
    </Form>
  );
};

export default LoginForm; 