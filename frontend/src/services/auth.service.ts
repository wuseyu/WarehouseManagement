import axios from 'axios';
import { LoginRequest, RegisterRequest, JwtResponse } from '../types';
import API_CONFIG from '../config';

// 根URL为空，因为setupProxy.js已经配置了/api的代理
const API_URL = '';

export const authService = {
  // 登录方法
  login: async (loginRequest: LoginRequest): Promise<JwtResponse> => {
    console.log('尝试登录请求，数据:', loginRequest);
    
    try {
      // 确保请求使用正确的JSON格式
      const directEndpoint = API_CONFIG.AUTH.LOGIN;
      console.log(`尝试直接调用后端API: ${directEndpoint}`);
      
      const requestBody = JSON.stringify({
        username: loginRequest.username,
        password: loginRequest.password
      });

      console.log('请求体:', requestBody);
      
      const response = await fetch(API_CONFIG.getFullUrl(directEndpoint), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: requestBody
      });
      
      // 检查状态码
      if (!response.ok) {
        const errorData = await response.json().catch(() => null) || await response.text();
        console.error('登录失败，状态码:', response.status, '错误信息:', errorData);
        throw new Error(typeof errorData === 'object' ? errorData.message || `状态码: ${response.status}` : `状态码: ${response.status}`);
      }
      
      console.log('登录成功!');
      const userData = await response.json();
      console.log('用户数据:', userData);
      
      // 统一使用token字段
      if (userData.jwt && !userData.token) {
        userData.token = userData.jwt;
      }
      
      if (userData.userId && !userData.id) {
        userData.id = userData.userId;
      }
      
      if (userData) {
        // 保存用户基本信息
        localStorage.setItem('user', JSON.stringify(userData));
        
        // 设置认证头
        localStorage.setItem('token', userData.token || userData.jwt);
        
        // 设置axios默认头
        axios.defaults.headers.common['Authorization'] = `Bearer ${userData.token || userData.jwt}`;
      }
      return userData;
    } catch (error: any) {
      console.error('登录失败:', error);
      
      // 如果直接请求失败，尝试通过代理请求
      try {
        console.log('尝试通过代理请求...');
        const response = await fetch(API_CONFIG.AUTH.LOGIN, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: JSON.stringify(loginRequest)
        });
        
        if (!response.ok) {
          const errorText = await response.text();
          console.error('代理请求失败，状态码:', response.status, '错误信息:', errorText);
          throw new Error(errorText || `状态码: ${response.status}`);
        }
        
        console.log('代理请求成功');
        const userData = await response.json();
        if (userData) {
          localStorage.setItem('user', JSON.stringify(userData));
          localStorage.setItem('token', userData.token || userData.jwt);
          axios.defaults.headers.common['Authorization'] = `Bearer ${userData.token || userData.jwt}`;
          return userData;
        }
      } catch (proxyError: any) {
        console.error('代理请求也失败', proxyError);
        throw proxyError;
      }
      
      throw error;
    }
  },
  
  // 注册方法
  register: async (registerRequest: RegisterRequest): Promise<any> => {
    console.log('尝试注册，请求数据:', registerRequest);
    
    try {
      const directEndpoint = API_CONFIG.AUTH.REGISTER;
      console.log(`尝试直接调用后端API: ${directEndpoint}`);
      
      const requestBody = JSON.stringify({
        username: registerRequest.username,
        password: registerRequest.password,
        email: registerRequest.email,
        phone: registerRequest.phone
      });
      
      console.log('注册请求体:', requestBody);
      
      const response = await fetch(API_CONFIG.getFullUrl(directEndpoint), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: requestBody
      });
      
      // 检查状态码
      if (!response.ok) {
        const errorData = await response.json().catch(() => null) || await response.text();
        console.error('注册失败，状态码:', response.status, '错误信息:', errorData);
        throw new Error(typeof errorData === 'object' ? errorData.message || `状态码: ${response.status}` : `状态码: ${response.status}`);
      }
      
      console.log('注册成功!');
      const userData = await response.json();
      console.log('注册响应:', userData);
      return userData;
    } catch (error: any) {
      console.error('注册失败:', error);
      
      // 如果直接请求失败，尝试通过代理请求
      try {
        console.log('尝试通过代理请求...');
        const response = await fetch(API_CONFIG.AUTH.REGISTER, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          body: JSON.stringify(registerRequest)
        });
        
        if (!response.ok) {
          const errorText = await response.text();
          console.error('代理请求失败，状态码:', response.status, '错误信息:', errorText);
          throw new Error(errorText || `状态码: ${response.status}`);
        }
        
        console.log('代理注册请求成功');
        const userData = await response.json();
        console.log('注册响应:', userData);
        return userData;
      } catch (proxyError: any) {
        console.error('代理注册请求也失败', proxyError);
        throw proxyError;
      }
      
      throw error;
    }
  },
  
  // 登出方法
  logout: (): void => {
    localStorage.removeItem('user');
    // 同时清除扩展用户信息
    localStorage.removeItem('userExtendedInfo');
    // 删除请求头中的授权信息
    delete axios.defaults.headers.common['Authorization'];
    // 导航到登录页面 - 这里我们只移除数据，具体导航在调用的组件中处理
  },
  
  // 获取当前用户
  getCurrentUser: (): JwtResponse | null => {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  },
  
  // 添加认证头部
  setupAuthHeader: (): void => {
    const user = authService.getCurrentUser();
    if (user) {
      // 使用token或jwt字段
      const tokenValue = user.token || user.jwt;
      if (tokenValue) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${tokenValue}`;
      } else {
        delete axios.defaults.headers.common['Authorization'];
      }
    } else {
      delete axios.defaults.headers.common['Authorization'];
    }
  },
  
  // 添加一个新方法用于同步用户扩展信息
  syncUserExtendedInfo: (userData: any): void => {
    if (!userData) return;
    
    try {
      // 获取当前存储的扩展信息
      const extendedInfoStr = localStorage.getItem('userExtendedInfo');
      let extendedInfo = {};
      
      if (extendedInfoStr) {
        try {
          extendedInfo = JSON.parse(extendedInfoStr);
        } catch (e) {
          console.error('解析扩展信息失败，将创建新的:', e);
        }
      }
      
      // 用新用户信息更新扩展信息
      const updatedInfo = {
        ...extendedInfo,
        userId: userData.id || userData.userId,
        username: userData.username,
        // 如果后端返回了email和phone，就使用后端的
        ...(userData.email && { email: userData.email }),
        ...(userData.phone && { phone: userData.phone }),
        lastUpdated: new Date().toISOString()
      };
      
      // 保存到本地存储
      localStorage.setItem('userExtendedInfo', JSON.stringify(updatedInfo));
      console.log('已同步用户扩展信息:', updatedInfo);
    } catch (e) {
      console.error('同步用户扩展信息失败:', e);
    }
  },
  
  // 获取用户完整信息，包括扩展信息
  getUserFullInfo: (): any => {
    const basicUser = authService.getCurrentUser();
    if (!basicUser) return null;
    
    // 尝试获取扩展信息
    try {
      const extendedInfoStr = localStorage.getItem('userExtendedInfo');
      if (extendedInfoStr) {
        const extendedInfo = JSON.parse(extendedInfoStr);
        
        // 确保是当前用户的扩展信息
        if (extendedInfo.userId == basicUser.id || 
            extendedInfo.username === basicUser.username) {
          
          // 合并基本信息和扩展信息
          return {
            ...basicUser,
            email: extendedInfo.email || basicUser.email || '',
            phone: extendedInfo.phone || basicUser.phone || ''
          };
        }
      }
    } catch (e) {
      console.error('获取扩展信息失败:', e);
    }
    
    // 如果没有扩展信息或出错，返回基本信息
    return basicUser;
  }
}; 