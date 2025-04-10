import React, { useState, useEffect } from 'react';
import { Card, Form, Input, Button, Row, Col, Divider, message, Space, Typography, Tag } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, PhoneOutlined } from '@ant-design/icons';
import { authService } from '../services/auth.service';
import axios from 'axios';

const { Title, Text } = Typography;

interface UserData {
  id: number;
  username: string;
  email: string | null;
  phone: string | null;
  roles: string[];
}

const UserProfile: React.FC = () => {
  const [userData, setUserData] = useState<UserData | null>(null);
  const [loading, setLoading] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);

  // 创建表单实例并预设初始值
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();

  // 初始化数据 - 在组件挂载时执行一次
  useEffect(() => {
    // 1. 首先从localStorage获取基本用户信息
    const user = authService.getCurrentUser();
    
    // 2. 尝试获取扩展信息
    const userExtendedInfo = localStorage.getItem('userExtendedInfo');
    let email = '';
    let phone = '';
    
    if (userExtendedInfo) {
      try {
        const parsedInfo = JSON.parse(userExtendedInfo);
        email = parsedInfo.email || '';
        phone = parsedInfo.phone || '';
        console.log('从本地缓存加载了用户扩展信息:', parsedInfo);
      } catch (e) {
        console.error('解析本地用户信息失败:', e);
      }
    }
    
    // 3. 设置本地状态
    if (user) {
      const initialData = {
        id: user.id || 0,
        username: user.username,
        email: email,
        phone: phone,
        roles: user.roles || []
      };
      
      setUserData(initialData);
      
      // 4. 初始化表单值 - 重要：确保表单字段有初始值
      form.setFieldsValue({
        id: initialData.id,
        username: initialData.username,
        email: initialData.email,
        phone: initialData.phone
      });
      
      // 5. 从后端获取最新信息（如果可能）
      if (user.id) {
        fetchUserDetails(user.id);
      }
    }
  }, []); // 空依赖数组，确保只在组件挂载时执行一次

  // 从后端获取用户详细信息
  const fetchUserDetails = async (userId: number) => {
    try {
      setLoading(true);
      
      // 尝试从后端获取用户信息
      try {
        const response = await axios.get(`/api/users/id/${userId}`, {
          headers: {
            'Authorization': `Bearer ${authService.getCurrentUser()?.token}`
          }
        });
        
        const userDetails = response.data;
        console.log('从后端获取的用户详情:', userDetails);
        
        // 合并后端数据与现有数据
        const updatedData = {
          ...userData!,
          // 如果后端返回了email和phone则使用后端的，否则保留当前值
          email: userDetails.email || userData?.email || '',
          phone: userDetails.phone || userData?.phone || ''
        };
        
        setUserData(updatedData);
        
        // 重新设置表单字段，确保显示最新数据
        form.setFieldsValue({
          id: updatedData.id,
          username: updatedData.username,
          email: updatedData.email,
          phone: updatedData.phone
        });
        
        // 如果从后端获取到了数据，存储到本地
        saveUserInfoToLocal(updatedData, 'backend');
      } catch (error) {
        console.warn('获取用户信息失败，继续使用本地数据:', error);
      }
    } finally {
      setLoading(false);
    }
  };

  // 辅助函数：保存用户扩展信息到本地存储
  const saveUserInfoToLocal = (data: UserData, source = 'form') => {
    try {
      const extendedInfo = {
        email: data.email || '',
        phone: data.phone || '',
        lastUpdated: new Date().toISOString(),
        userId: data.id,
        username: data.username,
        source
      };
      localStorage.setItem('userExtendedInfo', JSON.stringify(extendedInfo));
      console.log(`已将用户信息保存到本地 (来源: ${source})`, extendedInfo);
    } catch (e) {
      console.error('保存用户信息到本地失败:', e);
    }
  };

  // 保存用户信息
  const handleSaveProfile = async (values: any) => {
    if (!userData) return;
    
    try {
      setLoading(true);
      
      // 准备要保存的数据
      const updatedData = {
        ...userData,
        username: values.username,
        email: values.email || '',
        phone: values.phone || ''
      };
      
      // 尝试与后端交互
      let backendUpdateSuccess = false;
      try {
        const response = await axios.put(`/api/users/${userData.id}`, {
          username: values.username,
          email: values.email,
          phone: values.phone
        }, {
          headers: {
            'Authorization': `Bearer ${authService.getCurrentUser()?.token}`
          }
        });
        
        if (response.status === 200) {
          backendUpdateSuccess = true;
        }
      } catch (error) {
        console.error('后端API调用失败，将只更新本地数据:', error);
      }
      
      // 更新本地状态
      setUserData(updatedData);
      
      // 更新表单状态（确保表单显示最新值）
      form.setFieldsValue({
        id: updatedData.id,
        username: updatedData.username,
        email: updatedData.email,
        phone: updatedData.phone
      });
      
      // 保存扩展信息到本地存储
      saveUserInfoToLocal(updatedData, backendUpdateSuccess ? 'backend+local' : 'local');
      
      // 更新本地存储中的用户基本信息
      const currentUser = authService.getCurrentUser();
      if (currentUser && currentUser.username !== values.username) {
        currentUser.username = values.username;
        localStorage.setItem('user', JSON.stringify(currentUser));
      }
      
      message.success(backendUpdateSuccess 
        ? '个人信息更新成功，已保存到服务器和本地' 
        : '个人信息已更新到本地，但未能保存到服务器');
      
    } catch (error) {
      console.error('更新个人信息失败:', error);
      message.error('更新个人信息失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 修改密码
  const handleChangePassword = async (values: any) => {
    if (!userData) return;
    
    if (values.newPassword !== values.confirmPassword) {
      message.error('两次输入的新密码不一致');
      return;
    }
    
    try {
      setPasswordLoading(true);
      const response = await axios.post(`/api/users/password/${userData.id}`, {
        oldPassword: values.oldPassword,
        newPassword: values.newPassword
      }, {
        headers: {
          'Authorization': `Bearer ${authService.getCurrentUser()?.token}`
        }
      });
      
      if (response.status === 200) {
        message.success('密码修改成功');
        passwordForm.resetFields();
      }
    } catch (error) {
      console.error('修改密码失败:', error);
      message.error('修改密码失败，请确认旧密码是否正确');
    } finally {
      setPasswordLoading(false);
    }
  };

  if (!userData) {
    return <div>加载用户信息中...</div>;
  }

  return (
    <div>
      <Row gutter={24}>
        <Col span={16}>
          <Card 
            title={
              <div>
                <span>个人资料</span>
                {userData?.email || userData?.phone ? (
                  <Tag color="green" style={{ marginLeft: 8 }}>数据已保存</Tag>
                ) : (
                  <Tag color="orange" style={{ marginLeft: 8 }}>未保存完整信息</Tag>
                )}
              </div>
            } 
            loading={loading}
          >
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSaveProfile}
              initialValues={{
                id: userData?.id,
                username: userData?.username,
                email: userData?.email || '',
                phone: userData?.phone || ''
              }}
            >
              <Form.Item label="用户ID">
                <Input 
                  value={userData?.id?.toString() || ''}
                  disabled
                  prefix={<UserOutlined />}
                  placeholder="用户ID"
                />
              </Form.Item>
              
              <Form.Item
                label="用户名"
                name="username"
                rules={[{ required: true, message: '请输入用户名' }]}
              >
                <Input 
                  prefix={<UserOutlined />}
                  placeholder="用户名"
                />
              </Form.Item>
              
              <Form.Item
                label="邮箱"
                name="email"
                rules={[
                  { type: 'email', message: '请输入有效的邮箱地址' }
                ]}
                help="修改后的邮箱将保存在本地，即使退出登录也会保留"
              >
                <Input 
                  prefix={<MailOutlined />}
                  placeholder="邮箱"
                />
              </Form.Item>
              
              <Form.Item
                label="电话"
                name="phone"
                rules={[
                  { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码' }
                ]}
                help="修改后的电话将保存在本地，即使退出登录也会保留"
              >
                <Input 
                  prefix={<PhoneOutlined />}
                  placeholder="电话"
                />
              </Form.Item>
              
              <Form.Item>
                <Button 
                  type="primary" 
                  htmlType="submit"
                  loading={loading}
                >
                  保存修改
                </Button>
                
                {/* 调试按钮 */}
                <Button 
                  style={{ marginLeft: 8 }}
                  onClick={() => {
                    // 检查本地存储内容
                    const userInfo = localStorage.getItem('user');
                    const extendedInfo = localStorage.getItem('userExtendedInfo');
                    
                    console.log('本地用户信息:', userInfo ? JSON.parse(userInfo) : null);
                    console.log('本地扩展信息:', extendedInfo ? JSON.parse(extendedInfo) : null);
                    console.log('当前表单值:', form.getFieldsValue());
                    console.log('当前组件状态:', userData);
                    
                    message.info('已在控制台输出本地存储信息，按F12查看');
                  }}
                >
                  检查存储
                </Button>
                
                <Button 
                  style={{ marginLeft: 8 }} 
                  danger
                  onClick={() => {
                    // 清空缓存并重新加载
                    localStorage.removeItem('userExtendedInfo');
                    message.info('已清除本地扩展信息缓存');
                    setTimeout(() => window.location.reload(), 1000);
                  }}
                >
                  清除缓存
                </Button>
              </Form.Item>
            </Form>
          </Card>
        </Col>
        
        <Col span={8}>
          <Card title="修改密码" loading={passwordLoading}>
            <Form
              form={passwordForm}
              layout="vertical"
              onFinish={handleChangePassword}
            >
              <Form.Item
                name="oldPassword"
                rules={[{ required: true, message: '请输入旧密码' }]}
              >
                <Input.Password 
                  prefix={<LockOutlined />}
                  placeholder="旧密码"
                />
              </Form.Item>
              
              <Form.Item
                name="newPassword"
                rules={[
                  { required: true, message: '请输入新密码' },
                  { min: 6, message: '密码长度至少6位' }
                ]}
              >
                <Input.Password 
                  prefix={<LockOutlined />}
                  placeholder="新密码"
                />
              </Form.Item>
              
              <Form.Item
                name="confirmPassword"
                rules={[
                  { required: true, message: '请确认新密码' },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue('newPassword') === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(new Error('两次输入的密码不一致'));
                    },
                  }),
                ]}
              >
                <Input.Password 
                  prefix={<LockOutlined />}
                  placeholder="确认新密码"
                />
              </Form.Item>
              
              <Form.Item>
                <Button 
                  type="primary" 
                  htmlType="submit"
                  loading={passwordLoading}
                >
                  修改密码
                </Button>
              </Form.Item>
            </Form>
          </Card>
          
          <Card title="角色信息" style={{ marginTop: 16 }}>
            <Space direction="vertical">
              <div>
                <Text strong>用户角色:</Text>
                <div style={{ marginTop: 8 }}>
                  {userData.roles.map(role => (
                    <div key={role}>
                      {role.replace('ROLE_', '')}
                    </div>
                  ))}
                </div>
              </div>
            </Space>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default UserProfile; 