import axios from 'axios';
import { Product } from '../types';

const API_URL = process.env.REACT_APP_API_URL || '/api';

export const productService = {
  getProducts: async (searchText: string = '') => {
    const response = await axios.get(`${API_URL}/products?search=${searchText}`);
    return response.data;
  },
  
  getProductById: async (id: number) => {
    const response = await axios.get(`${API_URL}/products/${id}`);
    return response.data;
  },
  
  createProduct: async (product: Omit<Product, 'id'>) => {
    const response = await axios.post(`${API_URL}/products`, product);
    return response.data;
  },
  
  updateProduct: async (id: number, product: Partial<Product>) => {
    const response = await axios.put(`${API_URL}/products/${id}`, product);
    return response.data;
  },
  
  deleteProduct: async (id: number) => {
    const response = await axios.delete(`${API_URL}/products/${id}`);
    return response.data;
  }
}; 