// 登录请求类型
export interface LoginRequest {
  username: string;
  password: string;
}

// 注册请求类型
export interface RegisterRequest {
  username: string;
  password: string;
  email?: string;
  phone?: string;
}

// JWT响应类型
export interface JwtResponse {
  token: string;
  id: number;
  username: string;
  roles: string[];
  type: string;
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