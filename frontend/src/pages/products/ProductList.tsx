import React, { useEffect, useState } from 'react';
import { Table, Button, Space, Typography, Input, Tag, Modal, Form, Select, message } from 'antd';
import { PlusOutlined, SearchOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { Product } from '../../types';
import { productService } from '../../services/product.service';

const { Title } = Typography;
const { Option } = Select;

const ProductList: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [searchText, setSearchText] = useState<string>('');
  const [isModalVisible, setIsModalVisible] = useState<boolean>(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const data = await productService.getAllProducts();
      setProducts(data);
    } catch (err) {
      message.error('获取产品列表失败');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (value: string) => {
    setSearchText(value);
  };

  const filteredProducts = products.filter(
    (product) =>
      product.name.toLowerCase().includes(searchText.toLowerCase()) ||
      product.sku.toLowerCase().includes(searchText.toLowerCase()) ||
      (product.category && product.category.toLowerCase().includes(searchText.toLowerCase()))
  );

  const showAddModal = () => {
    setEditingProduct(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const showEditModal = (product: Product) => {
    setEditingProduct(product);
    form.setFieldsValue({
      name: product.name,
      sku: product.sku,
      description: product.description,
      category: product.category,
      price: product.price,
    });
    setIsModalVisible(true);
  };

  const handleDelete = async (id: number | undefined) => {
    if (!id) {
      message.error('产品ID不存在');
      return;
    }
    
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除这个产品吗？此操作不可撤销。',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await productService.deleteProduct(id);
          message.success('产品删除成功');
          fetchProducts();
        } catch (err) {
          message.error('产品删除失败');
          console.error(err);
        }
      },
    });
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      if (editingProduct) {
        if (!editingProduct.id) {
          message.error('产品ID不存在');
          return;
        }
        await productService.updateProduct(editingProduct.id, {
          ...editingProduct,
          ...values,
        });
        message.success('产品更新成功');
      } else {
        await productService.createProduct(values);
        message.success('产品创建成功');
      }
      setIsModalVisible(false);
      fetchProducts();
    } catch (err) {
      console.error('表单验证失败:', err);
    }
  };

  const columns = [
    {
      title: '产品名称',
      dataIndex: 'name',
      key: 'name',
      sorter: (a: Product, b: Product) => a.name.localeCompare(b.name),
    },
    {
      title: 'SKU',
      dataIndex: 'sku',
      key: 'sku',
    },
    {
      title: '类别',
      dataIndex: 'category',
      key: 'category',
      render: (category: string) => (
        <Tag color="blue">{category || '未分类'}</Tag>
      ),
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      render: (price: number) => `¥${price.toFixed(2)}`,
      sorter: (a: Product, b: Product) => a.price - b.price,
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Product) => (
        <Space size="middle">
          <Button
            type="primary"
            icon={<EditOutlined />}
            size="small"
            onClick={() => showEditModal(record)}
          >
            编辑
          </Button>
          <Button
            danger
            icon={<DeleteOutlined />}
            size="small"
            onClick={() => record.id && handleDelete(record.id)}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <Title level={2}>产品管理</Title>
        <Space>
          <Input
            placeholder="搜索产品"
            prefix={<SearchOutlined />}
            onChange={(e) => handleSearch(e.target.value)}
            style={{ width: 250 }}
          />
          <Button type="primary" icon={<PlusOutlined />} onClick={showAddModal}>
            添加产品
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={filteredProducts}
        rowKey="id"
        loading={loading}
        pagination={{ pageSize: 10 }}
      />

      <Modal
        title={editingProduct ? '编辑产品' : '添加产品'}
        visible={isModalVisible}
        onOk={handleModalOk}
        onCancel={() => setIsModalVisible(false)}
        width={600}
        okText={editingProduct ? '更新' : '创建'}
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="产品名称"
            rules={[{ required: true, message: '请输入产品名称' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            name="sku"
            label="SKU"
            rules={[{ required: true, message: '请输入SKU' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item name="description" label="描述">
            <Input.TextArea rows={4} />
          </Form.Item>

          <Form.Item name="category" label="类别">
            <Select placeholder="选择类别">
              <Option value="电子产品">电子产品</Option>
              <Option value="家居用品">家居用品</Option>
              <Option value="食品">食品</Option>
              <Option value="服装">服装</Option>
              <Option value="办公用品">办公用品</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="price"
            label="价格"
            rules={[{ required: true, message: '请输入价格' }]}
          >
            <Input type="number" min={0} step={0.01} prefix="¥" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ProductList; 