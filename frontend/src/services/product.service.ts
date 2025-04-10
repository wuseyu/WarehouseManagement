import api from './api';
import { Product } from '../types';

export const productService = {
  // 获取所有产品
  getAllProducts: async () => {
    const response = await api.get<Product[]>('/products');
    return response.data;
  },

  // 根据ID获取产品
  getProductById: async (id: number) => {
    const response = await api.get<Product>(`/products/${id}`);
    return response.data;
  },

  // 创建产品
  createProduct: async (product: Partial<Product>) => {
    const response = await api.post<Product>('/products', product);
    return response.data;
  },

  // 更新产品
  updateProduct: async (id: number, product: Partial<Product>) => {
    const response = await api.put<Product>(`/products/${id}`, product);
    return response.data;
  },

  // 删除产品
  deleteProduct: async (id: number) => {
    const response = await api.delete(`/products/${id}`);
    return response.data;
  },

  // 搜索产品
  searchProducts: async (name?: string, category?: string) => {
    const response = await api.get<Product[]>('/products/search', {
      params: { name, category }
    });
    return response.data;
  }
}; 