import axios, { AxiosResponse, InternalAxiosRequestConfig } from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api', // 直接指向后端API
  timeout: 10000,  // 10秒超时
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 添加token
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 使用token或jwt字段
      const tokenValue = user.token || user.jwt;
      if (tokenValue && config.headers) {
        config.headers['Authorization'] = `Bearer ${tokenValue}`;
        console.log('请求添加Authorization头:', `Bearer ${tokenValue.substring(0, 20)}...`);
        console.log('用户角色:', user.roles);
      }
    }
    return config;
  },
  (error) => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  (response: AxiosResponse): AxiosResponse => {
    // 可以在这里添加调试信息
    if (response.config.url?.includes('inventories')) {
      console.log('库存API响应:', {
        url: response.config.url,
        status: response.status,
        data: response.data
      });
    }
    return response;
  },
  (error) => {
    if (error.response) {
      console.error('请求错误详情:', {
        url: error.config?.url,
        method: error.config?.method,
        status: error.response.status,
        data: error.response.data
      });
      
      // 身份验证错误，清除token并重定向到登录页
      if (error.response.status === 401) {
        console.error('认证失败，需要重新登录');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
      // 权限错误
      if (error.response.status === 403) {
        console.error('权限不足，无法访问该资源');
        alert('您没有权限执行此操作，请联系管理员');
      }
      // 冲突错误，通常是乐观锁冲突
      if (error.response.status === 409) {
        console.error('数据已被其他用户修改，请刷新后重试');
      }
    } else if (error.request) {
      // 请求已发送但未收到响应
      console.error('无法连接到服务器，请检查网络连接:', error.request);
      alert('无法连接到服务器，请检查网络连接');
    } else {
      // 设置请求时发生错误
      console.error('请求错误:', error.message);
    }
    return Promise.reject(error);
  }
);

export default api; 