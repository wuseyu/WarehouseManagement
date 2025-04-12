import React from 'react';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Checkbox, Form, Input, message, Card, Typography } from 'antd';
import { history } from 'umi';
import { login } from '../../services/auth';
import styles from './index.less';

const { Title } = Typography;

const LoginPage: React.FC = () => {
  const [loading, setLoading] = React.useState(false);

  const handleSubmit = async (values: { username: string; password: string; remember: boolean }) => {
    try {
      setLoading(true);
      const response = await login({
        username: values.username,
        password: values.password,
      });

      // 保存token和用户信息
      localStorage.setItem('token', response.token);
      localStorage.setItem('userId', response.id.toString());
      localStorage.setItem('username', response.username);
      localStorage.setItem('roles', JSON.stringify(response.roles));

      message.success('登录成功');
      // 跳转到首页
      history.push('/dashboard');
    } catch (error) {
      message.error('登录失败，请检查用户名和密码');
      console.error('登录失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <Card bordered={false} className={styles.loginCard}>
          <div className={styles.header}>
            <Title level={2} className={styles.title}>
              仓储物流管理系统
            </Title>
            <div className={styles.desc}>先进高效的仓储物流解决方案</div>
          </div>

          <Form
            name="login"
            initialValues={{ remember: true }}
            onFinish={handleSubmit}
            className={styles.loginForm}
          >
            <Form.Item
              name="username"
              rules={[{ required: true, message: '请输入用户名!' }]}
            >
              <Input
                prefix={<UserOutlined />}
                placeholder="用户名"
                size="large"
                autoComplete="username"
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
                autoComplete="current-password"
              />
            </Form.Item>

            <Form.Item>
              <Form.Item name="remember" valuePropName="checked" noStyle>
                <Checkbox>记住我</Checkbox>
              </Form.Item>
              <a href="#" className={styles.forgotPassword}>
                忘记密码?
              </a>
            </Form.Item>

            <Form.Item>
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                className={styles.loginButton}
                size="large"
                block
              >
                登录
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </div>
    </div>
  );
};

export default LoginPage; 