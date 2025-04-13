// 用户相关类型
export interface User {
  id: number;
  username: string;
  email?: string;
  phone?: string;
  roles: Role[];
  createdAt: string;
  updatedAt: string;
}

export interface Role {
  id: number;
  name: string;
  type: RoleType;
  permissions: Permission[];
}

export enum RoleType {
  ADMIN = 'ADMIN',
  WAREHOUSE = 'WAREHOUSE',
  LOGISTICS = 'LOGISTICS',
  STORE = 'STORE'
}

export interface Permission {
  id: number;
  name: string;
  code: string;
  description?: string;
}

// 产品相关类型
export interface Product {
  id: number;
  sku: string;
  name: string;
  description?: string;
  category: string;
  categoryCode?: string;
  weight?: number;
  volume?: number;
  stackingLimit?: number;
  purchaseUnit?: string;
  salesUnit?: string;
  unitConversionRatio?: number;
  supplierName?: string;
  purchasePrice?: number;
  sellingPrice?: number;
  hasExpiration: boolean;
  shelfLifeDays?: number;
  createdAt: string;
}

// 库存相关类型
export interface Inventory {
  id: number;
  warehouse: Warehouse;
  product: Product;
  quantity: number;
  batchNo?: string;
  expirationDate?: string;
  status: InventoryStatus;
  lockedQuantity?: number;
  createdAt: string;
  updatedAt?: string;
  version: number;
}

export enum InventoryStatus {
  AVAILABLE = 'AVAILABLE',
  LOCKED = 'LOCKED',
  FROZEN = 'FROZEN',
  SCRAPPED = 'SCRAPPED'
}

// 仓库相关类型
export interface Warehouse {
  id: number;
  name: string;
  address: string;
  capacity: number;
  contactPerson?: string;
  contactPhone?: string;
}

// 订单相关类型
export interface Order {
  id: number;
  orderNo: string;
  user: User;
  deliveryAddress: string;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  updatedAt: string;
  orderItems: OrderItem[];
  task?: Task;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

export interface OrderItem {
  id: number;
  order?: Order;
  product: Product;
  quantity: number;
  unitPrice: number;
}

// 任务相关类型
export interface Task {
  id: number;
  title: string;
  description?: string;
  status: TaskStatus;
  assignee?: User;
  createdAt: string;
  updatedAt?: string;
  dueDate?: string;
}

export enum TaskStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

// 车辆相关类型
export interface Vehicle {
  id: number;
  vehicleNo: string;
  type: string;
  capacity: number;
  status: VehicleStatus;
}

export enum VehicleStatus {
  AVAILABLE = 'AVAILABLE',
  IN_USE = 'IN_USE',
  MAINTENANCE = 'MAINTENANCE'
}

// 运输相关类型
export interface Shipment {
  id: number;
  shipmentNo: string;
  order: Order;
  vehicle?: Vehicle;
  driver?: User;
  status: ShipmentStatus;
  departureTime?: string;
  arrivalTime?: string;
}

export enum ShipmentStatus {
  PENDING = 'PENDING',
  IN_TRANSIT = 'IN_TRANSIT',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

// 登录认证相关类型
export interface LoginRequest {
  username: string;
  password: string;
}

export interface JwtResponse {
  token: string;
  id: number;
  username: string;
  roles: string[];
} 