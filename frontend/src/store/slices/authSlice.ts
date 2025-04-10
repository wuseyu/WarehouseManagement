import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { authService } from '../../services/auth.service';
import { AuthState, LoginRequest, RegisterRequest, JwtResponse } from '../../types';

// 初始状态
const initialState: AuthState = {
  isAuthenticated: false,
  user: null,
  loading: false,
  error: null,
};

// 异步 thunk action 创建器 - 登录
export const login = createAsyncThunk(
  'auth/login',
  async (loginRequest: LoginRequest) => {
    const response = await authService.login(loginRequest);
    return response;
  }
);

// 异步 thunk action 创建器 - 注册
export const register = createAsyncThunk(
  'auth/register',
  async (registerRequest: RegisterRequest) => {
    const response = await authService.register(registerRequest);
    return response;
  }
);

// 异步 thunk action 创建器 - 登出
export const logout = createAsyncThunk(
  'auth/logout',
  async () => {
    await authService.logout();
  }
);

// 检查认证状态
export const checkAuth = createAsyncThunk(
  'auth/checkAuth',
  async () => {
    const user = authService.getCurrentUser();
    if (!user) {
      throw new Error('No user found');
    }
    return user;
  }
);

// 创建 auth slice
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // 清除错误
    clearError: (state) => {
      state.error = null;
    },
    // 从本地存储恢复用户状态
    restoreUser: (state) => {
      const user = authService.getCurrentUser();
      if (user) {
        state.isAuthenticated = true;
        state.user = user;
        // 设置认证头
        authService.setupAuthHeader();
      }
    },
  },
  extraReducers: (builder) => {
    // 登录状态处理
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.isAuthenticated = true;
        state.user = action.payload;
        state.error = null;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '登录失败';
      });

    // 注册状态处理
    builder
      .addCase(register.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(register.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(register.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || '注册失败';
      });
      
    // 登出状态处理  
    builder
      .addCase(logout.fulfilled, (state) => {
        state.isAuthenticated = false;
        state.user = null;
        state.error = null;
      });

    // 检查认证状态处理
    builder
      .addCase(checkAuth.pending, (state) => {
        state.loading = true;
      })
      .addCase(checkAuth.fulfilled, (state, action) => {
        state.loading = false;
        state.isAuthenticated = true;
        state.user = action.payload;
      })
      .addCase(checkAuth.rejected, (state) => {
        state.loading = false;
        state.isAuthenticated = false;
        state.user = null;
      });
  },
});

// 导出 actions
export const { clearError, restoreUser } = authSlice.actions;

// 导出 reducer
export default authSlice.reducer; 