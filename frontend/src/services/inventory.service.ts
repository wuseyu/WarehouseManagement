import api from './api';
import { Inventory, ApiResponse, Pagination } from '../types';

interface InventoryListResponse {
  content: Inventory[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
}

interface InventoryAdjustRequest {
  delta: number;
  version: number;
}

interface BulkStatusUpdateRequest {
  status: 'AVAILABLE' | 'RESERVED' | 'DAMAGED' | 'EXPIRED';
  ids: number[];
}

export const inventoryService = {
  // 获取所有库存（分页）
  getInventories: async (status?: string, page = 0, size = 10) => {
    try {
      console.log('获取库存列表，参数:', { status, page, size });
      const response = await api.get<InventoryListResponse>('/inventories', {
        params: { status, page, size }
      });
      console.log('获取库存列表成功:', response.data);
      return {
        data: response.data.content,
        pagination: {
          current: response.data.pageable.pageNumber + 1,
          pageSize: response.data.pageable.pageSize,
          total: response.data.totalElements
        }
      };
    } catch (error) {
      console.error('获取库存列表失败:', error);
      throw error;
    }
  },

  // 根据ID获取库存
  getInventoryById: async (id: number) => {
    try {
      console.log(`获取库存详情，ID: ${id}`);
      const response = await api.get<Inventory>(`/inventories/${id}`);
      console.log('获取库存详情成功:', response.data);
      return response.data;
    } catch (error) {
      console.error(`获取库存详情失败，ID: ${id}，错误:`, error);
      throw error;
    }
  },

  // 创建库存
  createInventory: async (inventory: Partial<Inventory>) => {
    try {
      console.log('创建库存，数据:', inventory);
      const response = await api.post<Inventory>('/inventories', inventory);
      console.log('创建库存成功:', response.data);
      return response.data;
    } catch (error) {
      console.error('创建库存失败，错误:', error);
      throw error;
    }
  },

  // 调整库存数量
  adjustInventoryQuantity: async (id: number, adjustData: InventoryAdjustRequest) => {
    try {
      console.log(`调整库存数量，ID: ${id}，数据:`, adjustData);
      const response = await api.put<void>(`/inventories/${id}/adjust`, adjustData);
      console.log('调整库存数量成功');
      return response.data;
    } catch (error) {
      console.error(`调整库存数量失败，ID: ${id}，错误:`, error);
      throw error;
    }
  },

  // 批量更新库存状态
  bulkUpdateStatus: async (data: BulkStatusUpdateRequest) => {
    try {
      console.log('批量更新库存状态，数据:', data);
      const response = await api.put<void>('/inventories/bulk-status', data);
      console.log('批量更新库存状态成功');
      return response.data;
    } catch (error) {
      console.error('批量更新库存状态失败，错误:', error);
      throw error;
    }
  },

  // 根据仓库和产品查询库存
  getByWarehouseAndProduct: async (warehouseId: number, productId: number) => {
    try {
      console.log('根据仓库和产品查询库存，参数:', { warehouseId, productId });
      const response = await api.get<Inventory[]>('/inventories', {
        params: { warehouseId, productId }
      });
      console.log('根据仓库和产品查询库存成功:', response.data);
      return response.data;
    } catch (error) {
      console.error('根据仓库和产品查询库存失败，错误:', error);
      throw error;
    }
  }
}; 