import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Space, message, Popconfirm } from 'antd';
import { SearchOutlined, EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { Product } from '../types';
import { productService } from '../services/productService';
import ProductForm from './ProductForm';

const ProductList: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [search, setSearch] = useState<string>('');
  const [visible, setVisible] = useState<boolean>(false);
  const [currentProduct, setCurrentProduct] = useState<Product | null>(null);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async (searchText = '') => {
    setLoading(true);
    try {
      const data = await productService.getProducts(searchText);
      setProducts(data);
    } catch (error) {
      message.error('获取产品列表失败');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    fetchProducts(search);
  };

  const handleDelete = async (id: number | undefined) => {
    if (!id) {
      message.error('产品ID不存在');
      return;
    }
    try {
      await productService.deleteProduct(id);
      message.success('删除成功');
      fetchProducts(search);
    } catch (error) {
      message.error('删除失败');
      console.error(error);
    }
  };

  const handleEdit = (record: Product) => {
    setCurrentProduct(record);
    setVisible(true);
  };

  const handleAdd = () => {
    setCurrentProduct(null);
    setVisible(true);
  };

  const handleFormSubmit = async (values: any) => {
    try {
      if (currentProduct) {
        if (!currentProduct.id) {
          message.error('产品ID不存在');
          return;
        }
        await productService.updateProduct(currentProduct.id, values);
        message.success('更新成功');
      } else {
        await productService.createProduct(values);
        message.success('创建成功');
      }
      setVisible(false);
      fetchProducts(search);
    } catch (error) {
      message.error('操作失败');
      console.error(error);
    }
  };

  const columns = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
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
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      render: (price: number) => `¥${price.toFixed(2)}`,
    },
    {
      title: '库存',
      dataIndex: 'stockQuantity',
      key: 'stockQuantity',
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Product) => (
        <Space size="middle">
          <Button 
            type="text" 
            icon={<EditOutlined />} 
            onClick={() => handleEdit(record)}
          />
          <Popconfirm
            title="确定删除该产品吗?"
            onConfirm={() => record.id && handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="text" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Space>
          <Input
            placeholder="搜索产品名称"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onPressEnter={handleSearch}
            prefix={<SearchOutlined />}
            style={{ width: 250 }}
          />
          <Button type="primary" onClick={handleSearch}>
            搜索
          </Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增产品
          </Button>
        </Space>
      </div>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={products}
        loading={loading}
        pagination={{ showSizeChanger: true }}
      />
      {visible && (
        <ProductForm
          visible={visible}
          onCancel={() => setVisible(false)}
          onSubmit={handleFormSubmit}
          initialValues={currentProduct}
        />
      )}
    </div>
  );
};

export default ProductList; 