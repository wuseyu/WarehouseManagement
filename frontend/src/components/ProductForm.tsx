import React from 'react';
import { Modal, Form, Input, InputNumber } from 'antd';
import { Product } from '../types';

interface ProductFormProps {
  visible: boolean;
  onCancel: () => void;
  onSubmit: (values: any) => void;
  initialValues: Product | null;
}

const ProductForm: React.FC<ProductFormProps> = ({
  visible,
  onCancel,
  onSubmit,
  initialValues
}) => {
  const [form] = Form.useForm();

  React.useEffect(() => {
    if (visible) {
      form.resetFields();
      if (initialValues) {
        form.setFieldsValue(initialValues);
      }
    }
  }, [visible, initialValues, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onSubmit(values);
    } catch (error) {
      console.error('验证失败:', error);
    }
  };

  return (
    <Modal
      title={initialValues ? '编辑产品' : '添加产品'}
      open={visible}
      onOk={handleSubmit}
      onCancel={onCancel}
      okText={initialValues ? '更新' : '创建'}
      cancelText="取消"
    >
      <Form
        form={form}
        layout="vertical"
      >
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
        <Form.Item
          name="category"
          label="类别"
          rules={[{ required: true, message: '请输入类别' }]}
        >
          <Input />
        </Form.Item>
        <Form.Item
          name="description"
          label="描述"
        >
          <Input.TextArea rows={3} />
        </Form.Item>
        <Form.Item
          name="price"
          label="价格"
          rules={[{ required: true, message: '请输入价格' }]}
        >
          <InputNumber min={0} step={0.01} prefix="¥" style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item
          name="stockQuantity"
          label="库存数量"
          rules={[{ required: true, message: '请输入库存数量' }]}
        >
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default ProductForm; 