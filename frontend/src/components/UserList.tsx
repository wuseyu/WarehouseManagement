import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Popconfirm, Tag, Space } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';
import { useAppSelector } from '../store/hooks';

interface User {
  id: number;
  username: string;
  email: string;
  phone: string;
  roles: Array<{ id: number; name: string; type: string }>;
}

interface Role {
  id: number;
  name: string;
  type: string;
  responsibility?: string;
}

const UserList: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [availableRoles, setAvailableRoles] = useState<Role[]>([]);
  const [form] = Form.useForm();
  const { user } = useAppSelector((state) => state.auth);

  // 获取所有用户
  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await axios.get('/api/users');
      setUsers(response.data);
    } catch (error) {
      console.error('获取用户列表失败:', error);
      message.error('获取用户列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 获取所有角色
  const fetchRoles = async () => {
    try {
      const response = await axios.get('/api/roles');
      setAvailableRoles(response.data);
    } catch (error) {
      console.error('获取角色列表失败:', error);
      message.error('获取角色列表失败');
    }
  };

  useEffect(() => {
    fetchUsers();
    fetchRoles();
  }, []);

  // 添加/编辑用户
  const handleAddOrUpdateUser = () => {
    form.validateFields().then(async (values) => {
      try {
        // 转换角色ID为角色对象数组
        const rolesArray = values.roles.map((roleId: number) => {
          const role = availableRoles.find(r => r.id === roleId);
          if (!role) {
            throw new Error(`角色ID为${roleId}的角色不存在`);
          }
          return role;
        });
        
        const userData = {
          ...values,
          roles: rolesArray
        };
        
        if (editingUser) {
          // 更新用户
          await axios.put(`/api/users/${editingUser.id}`, {
            ...userData,
            id: editingUser.id
          });
          message.success('用户更新成功');
        } else {
          // 添加用户
          await axios.post('/api/users', userData);
          message.success('用户添加成功');
        }
        
        setModalVisible(false);
        form.resetFields();
        fetchUsers();
      } catch (error) {
        console.error('操作失败:', error);
        message.error('操作失败，请重试');
      }
    });
  };

  // 删除用户
  const handleDeleteUser = async (id: number) => {
    try {
      await axios.delete(`/api/users/${id}`);
      message.success('用户删除成功');
      fetchUsers();
    } catch (error) {
      console.error('删除用户失败:', error);
      message.error('删除用户失败');
    }
  };

  // 显示添加/编辑用户弹窗
  const showUserModal = (user?: User) => {
    setEditingUser(user || null);
    
    if (user) {
      form.setFieldsValue({
        username: user.username,
        email: user.email,
        phone: user.phone,
        roles: user.roles.map(role => role.id)
      });
    } else {
      form.resetFields();
    }
    
    setModalVisible(true);
  };

  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '电话',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '角色',
      key: 'roles',
      render: (_: any, record: User) => (
        <>
          {record.roles && record.roles.map(role => (
            <Tag color="blue" key={role.id}>
              {role.name.replace('ROLE_', '')}
            </Tag>
          ))}
        </>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: User) => (
        <Space size="middle">
          <Button 
            icon={<EditOutlined />} 
            onClick={() => showUserModal(record)}
            disabled={user?.id === record.id} // 不能编辑自己
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除此用户吗？"
            onConfirm={() => handleDeleteUser(record.id)}
            okText="是"
            cancelText="否"
            disabled={user?.id === record.id} // 不能删除自己
          >
            <Button 
              danger 
              icon={<DeleteOutlined />}
              disabled={user?.id === record.id} // 不能删除自己
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <div style={{ marginBottom: '16px', display: 'flex', justifyContent: 'space-between' }}>
        <h2>用户管理</h2>
        <Button 
          type="primary" 
          onClick={() => showUserModal()} 
          icon={<PlusOutlined />}
        >
          添加用户
        </Button>
      </div>
      
      <Table 
        columns={columns} 
        dataSource={users} 
        rowKey="id" 
        loading={loading}
        pagination={{ pageSize: 10 }}
      />
      
      <Modal
        title={editingUser ? '编辑用户' : '添加用户'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleAddOrUpdateUser}
        okText={editingUser ? '保存' : '添加'}
        cancelText="取消"
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input placeholder="请输入用户名" />
          </Form.Item>
          
          {!editingUser && (
            <Form.Item
              name="password"
              label="密码"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password placeholder="请输入密码" />
            </Form.Item>
          )}
          
          <Form.Item
            name="email"
            label="邮箱"
            rules={[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' }
            ]}
          >
            <Input placeholder="请输入邮箱" />
          </Form.Item>
          
          <Form.Item
            name="phone"
            label="电话"
          >
            <Input placeholder="请输入电话号码" />
          </Form.Item>
          
          <Form.Item
            name="roles"
            label="角色"
            rules={[{ required: true, message: '请选择至少一个角色' }]}
          >
            <Select
              mode="multiple"
              placeholder="请选择角色"
              options={availableRoles.map((role) => ({
                label: role.type ? `${role.type.replace('ROLE_', '')} (${role.name})` : role.name,
                value: role.id
              }))}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserList; 