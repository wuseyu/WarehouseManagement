import React from 'react';
import { Form, Input, Button, Alert } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined } from '@ant-design/icons';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { register } from '../store/slices/authSlice';
import { RegisterRequest } from '../types';

interface RegisterFormProps {
  onSuccess?: () => void;
}

const RegisterForm: React.FC<RegisterFormProps> = ({ onSuccess }) => {
  const dispatch = useAppDispatch();
  const { loading, error } = useAppSelector((state) => state.auth);
  const [form] = Form.useForm();

  const onFinish = async (values: RegisterRequest) => {
    try {
      await dispatch(register(values)).unwrap();
      if (onSuccess) {
        onSuccess();
      }
    } catch (err) {
      console.error('注册失败:', err);
    }
  };

  return (
    <Form
      form={form}
      name="register"
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
        rules={[
          { required: true, message: '请输入用户名!' },
          { min: 3, message: '用户名至少3个字符!' }
        ]}
      >
        <Input 
          prefix={<UserOutlined />} 
          placeholder="用户名" 
          size="large"
        />
      </Form.Item>

      <Form.Item
        name="email"
        rules={[
          { type: 'email', message: '请输入有效的邮箱地址!' }
        ]}
      >
        <Input 
          prefix={<MailOutlined />} 
          placeholder="邮箱 (可选)" 
          size="large"
        />
      </Form.Item>

      <Form.Item
        name="phone"
      >
        <Input 
          prefix={<PhoneOutlined />} 
          placeholder="电话 (可选)" 
          size="large"
        />
      </Form.Item>

      <Form.Item
        name="password"
        rules={[
          { required: true, message: '请输入密码!' },
          { min: 6, message: '密码至少6个字符!' }
        ]}
      >
        <Input.Password
          prefix={<LockOutlined />}
          placeholder="密码"
          size="large"
        />
      </Form.Item>

      <Form.Item
        name="confirmPassword"
        dependencies={['password']}
        rules={[
          { required: true, message: '请确认密码!' },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || getFieldValue('password') === value) {
                return Promise.resolve();
              }
              return Promise.reject(new Error('两次输入的密码不匹配!'));
            },
          }),
        ]}
      >
        <Input.Password
          prefix={<LockOutlined />}
          placeholder="确认密码"
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
          注册
        </Button>
      </Form.Item>
    </Form>
  );
};

export default RegisterForm; 