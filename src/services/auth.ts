import { get, post } from '../utils/request';
import { JwtResponse, LoginRequest, User } from '../types';

/**
 * 用户登录
 * @param loginData 登录信息
 * @returns JWT响应
 */
export const login = (loginData: LoginRequest): Promise<JwtResponse> => {
  return post<JwtResponse>('/api/auth/login', loginData);
};

/**
 * 用户注册
 * @param userData 用户信息
 * @returns 注册结果
 */
export const register = (userData: Partial<User>): Promise<string> => {
  return post<string>('/api/auth/register', userData);
};

/**
 * 获取当前用户信息
 * @returns 用户信息
 */
export const getCurrentUser = (): Promise<User> => {
  return get<User>('/users/current');
};

/**
 * 退出登录
 */
export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.location.href = '/login';
}; 