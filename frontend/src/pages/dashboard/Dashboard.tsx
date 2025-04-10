import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Statistic, Table, Typography, Button } from 'antd';
import { ShoppingOutlined, InboxOutlined, CarOutlined, OrderedListOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import { Product, Inventory, Vehicle, Order } from '../../types';
import { productService } from '../../services/product.service';

const { Title } = Typography;

const Dashboard: React.FC = () => {
  const [productCount, setProductCount] = useState<number>(0);
  const [lowStockProducts, setLowStockProducts] = useState<Partial<Product>[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const products = await productService.getAllProducts();
        setProductCount(products.length);
        
        // 这里只是模拟低库存产品的数据
        // 实际上应该从后端获取低库存警告
        setLowStockProducts(products.slice(0, 5).map(p => ({
          id: p.id,
          name: p.name,
          sku: p.sku,
          category: p.category || '未分类'
        })));
      } catch (error) {
        console.error('获取数据失败:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const columns = [
    {
      title: '产品名称',
      dataIndex: 'name',
      key: 'name',
      render: (text: string, record: any) => <Link to={`/products/${record.id}`}>{text}</Link>,
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
      title: '操作',
      key: 'action',
      render: (_: any, record: any) => (
        <Button type="link" size="small">
          查看库存
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Title level={2}>仪表盘</Title>
      
      <Row gutter={[16, 16]}>
        <Col span={6}>
          <Card>
            <Statistic
              title="产品总数"
              value={productCount}
              prefix={<ShoppingOutlined />}
              loading={loading}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="库存数量"
              value={254}
              prefix={<InboxOutlined />}
              loading={loading}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="待处理订单"
              value={18}
              prefix={<OrderedListOutlined />}
              loading={loading}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="可用车辆"
              value={12}
              prefix={<CarOutlined />}
              loading={loading}
            />
          </Card>
        </Col>
      </Row>

      <div style={{ marginTop: 24 }}>
        <Title level={4}>低库存产品预警</Title>
        <Table
          loading={loading}
          columns={columns}
          dataSource={lowStockProducts}
          rowKey="id"
          pagination={false}
        />
      </div>
    </div>
  );
};

export default Dashboard; 