// 基本的分页类型
export interface Pagination {
  current: number;
  pageSize: number;
  total: number;
}

// 基本的响应类型
export interface ApiResponse<T> {
  data: T;
  message: string;
  success: boolean;
}

// 用户类型
export interface User {
  id: number;
  username: string;
  email: string;
  phone?: string;
  createdAt: string;
  updatedAt: string;
  roles?: Role[];
  role: string;
}

// 角色类型
export interface Role {
  id: number;
  name: string;
  type: string;
  responsibility?: string;
}

// 产品类型
export interface Product {
  id?: number;
  name: string;
  sku: string;
  category: string;
  description?: string;
  price: number;
  stockQuantity: number;
  createdAt?: string;
  updatedAt?: string;
}

// 库存类型
export interface Inventory {
  id: number;
  productId: number;
  warehouseId: number;
  quantity: number;
  lastUpdated: string;
  product?: Product;
  status: 'AVAILABLE' | 'RESERVED' | 'DAMAGED' | 'EXPIRED';
  version: number;
  warehouse?: Warehouse;
}

// 仓库类型
export interface Warehouse {
  id: number;
  name: string;
  location: string;
  capacity: number;
  inventories?: Inventory[];
}

// 订单类型
export interface Order {
  id: number;
  orderNo: string;
  user: User;
  status: 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  deliveryAddress: string;
  totalAmount?: number;
  items?: OrderItem[];
  createdAt: string;
  updatedAt: string;
  task?: Task;
}

// 订单项类型
export interface OrderItem {
  id: number;
  order: Order;
  product: Product;
  quantity: number;
  unitPrice: number;
  batchNo?: string;
  warehouseId?: number;
}

// 任务类型
export interface Task {
  id: number;
  title: string;
  description: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
  assignedVehicleId?: number;
  createdAt: string;
}

// 运输工具类型
export interface Vehicle {
  id: number;
  plateNumber: string;
  driverName: string;
  capacity: number;
  status: 'AVAILABLE' | 'IN_USE' | 'MAINTENANCE';
}

// 发货类型
export interface Shipment {
  id: number;
  task: Task;
  shipmentStatus: string;
  shippedTime?: string;
  deliveryTime?: string;
  notes?: string;
}

// 登录请求类型
export interface LoginRequest {
  username: string;
  password: string;
}

// 注册请求类型
export interface RegisterRequest {
  username: string;
  password: string;
  confirmPassword?: string;
  email?: string;
  phone?: string;
}

// JWT响应类型
export interface JwtResponse {
  token: string;
  jwt?: string;
  id?: number;
  userId?: number;
  username: string;
  roles: string[];
  type?: string;
  email?: string;
  phone?: string;
}

// 认证状态类型
export interface AuthState {
  isAuthenticated: boolean;
  user: JwtResponse | null;
  loading: boolean;
  error: string | null;
} 