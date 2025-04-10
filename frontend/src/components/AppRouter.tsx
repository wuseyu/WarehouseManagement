import React, { useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Login from './Login';
import Register from './Register';
import Dashboard from './Dashboard';
import ProductList from './ProductList';
import MainLayout from './Layout';
import PrivateRoute from './PrivateRoute';
import ApiTest from './ApiTest';
import { useAppSelector } from '../store/hooks';
import ProfilePage from '../pages/ProfilePage';
import UserList from './UserList';

// 导入其他页面组件
// 注意：如果这些组件尚未创建，我们需要创建占位符组件
const PlaceholderComponent = ({ title }: { title: string }) => (
  <div style={{ textAlign: 'center', padding: '50px 0' }}>
    <h2>{title}页面</h2>
    <p>该功能正在开发中，敬请期待...</p>
  </div>
);

// 占位符组件定义
const InventoryManagement = () => <PlaceholderComponent title="库存管理" />;
const WarehouseManagement = () => <PlaceholderComponent title="仓库管理" />;
const OrderManagement = () => <PlaceholderComponent title="订单管理" />;
const VehicleManagement = () => <PlaceholderComponent title="车辆管理" />;
const ShipmentManagement = () => <PlaceholderComponent title="物流管理" />;
const TaskManagement = () => <PlaceholderComponent title="任务管理" />;
const ReportAnalytics = () => <PlaceholderComponent title="报表分析" />;
const SystemSettings = () => <PlaceholderComponent title="系统设置" />;
const UnauthorizedPage = () => (
  <div style={{ textAlign: 'center', padding: '100px 0' }}>
    <h1>未授权访问</h1>
    <p>您没有权限访问此页面</p>
  </div>
);

const AppRouter: React.FC = () => {
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  return (
    <Router>
      <Routes>
        {/* 公开路由 */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/api-test" element={<ApiTest />} />
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
        
        {/* 受保护路由 - 基本页面 */}
        <Route path="/" element={
          <PrivateRoute>
            <MainLayout>
              <Dashboard />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 个人信息页面 - 所有已登录用户可访问 */}
        <Route path="/profile" element={
          <PrivateRoute>
            <MainLayout>
              <ProfilePage />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 产品管理 */}
        <Route path="/products" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN', 'ROLE_SUPPLIER', 'ROLE_AGENT']}>
            <MainLayout>
              <ProductList />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 库存管理 */}
        <Route path="/inventory" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR', 'ROLE_AGENT', 'ROLE_STORE']}>
            <MainLayout>
              <InventoryManagement />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 仓库管理 */}
        <Route path="/warehouses" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR']}>
            <MainLayout>
              <WarehouseManagement />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 订单管理 */}
        <Route path="/orders" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN', 'ROLE_STORE', 'ROLE_AGENT']}>
            <MainLayout>
              <OrderManagement />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 车辆管理 */}
        <Route path="/vehicles" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR']}>
            <MainLayout>
              <VehicleManagement />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 物流管理 */}
        <Route path="/shipments" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR', 'ROLE_STORE']}>
            <MainLayout>
              <ShipmentManagement />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 任务管理 */}
        <Route path="/tasks" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR', 'ROLE_AGENT']}>
            <MainLayout>
              <TaskManagement />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 用户管理 */}
        <Route path="/users" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN']}>
            <MainLayout>
              <UserList />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 报表分析 */}
        <Route path="/reports" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN', 'ROLE_CITY_OPERATOR']}>
            <MainLayout>
              <ReportAnalytics />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 受保护路由 - 系统设置 */}
        <Route path="/settings" element={
          <PrivateRoute roles={['ROLE_SUPER_ADMIN']}>
            <MainLayout>
              <SystemSettings />
            </MainLayout>
          </PrivateRoute>
        } />
        
        {/* 默认路由 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
};

export default AppRouter; 