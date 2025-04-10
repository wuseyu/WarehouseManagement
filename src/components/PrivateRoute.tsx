import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAppSelector } from '../store/hooks';

interface PrivateRouteProps {
  children: React.ReactNode;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { isAuthenticated } = useAppSelector((state) => state.auth);
  const location = useLocation();

  if (!isAuthenticated) {
    // 如果用户未认证，重定向到登录页面，并记录用户原本想访问的页面路径
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 如果用户已认证，则渲染子组件
  return <>{children}</>;
};

export default PrivateRoute;
