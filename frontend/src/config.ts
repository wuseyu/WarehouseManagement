// API配置
export const API_CONFIG = {
  // 基础URL
  BASE_URL: 'http://localhost:8080',
  
  // API前缀
  API_PREFIX: '/api',
  
  // 认证端点
  AUTH: {
    LOGIN: '/api/auth/signin',
    REGISTER: '/api/auth/signup',
    CHECK_USER: '/api/auth/check-user',
    INIT_USERS: '/api/auth/init-default-users',
    TEST: '/api/auth/test'
  },
  
  // 构建完整URL的辅助函数
  getFullUrl: (endpoint: string) => {
    // 如果端点已经包含完整URL，则直接返回
    if (endpoint.startsWith('http')) {
      return endpoint;
    }
    
    // 如果端点已经包含API前缀，则只添加基础URL
    if (endpoint.startsWith('/api')) {
      return `${API_CONFIG.BASE_URL}${endpoint}`;
    }
    
    // 否则，添加基础URL和API前缀
    return `${API_CONFIG.BASE_URL}${API_CONFIG.API_PREFIX}${endpoint}`;
  }
};

// 导出默认配置
export default API_CONFIG; 