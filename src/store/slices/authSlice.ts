import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
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
  async (loginRequest: LoginRequest, { rejectWithValue }) => {
    try {
      const response = await authService.login(loginRequest);
      // 设置认证头
      authService.setupAuthHeader();
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || '登录失败');
    }
  }
);

// 异步 thunk action 创建器 - 注册
export const register = createAsyncThunk(
  'auth/register',
  async (registerRequest: RegisterRequest, { rejectWithValue }) => {
    try {
      const response = await authService.register(registerRequest);
      return response;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || '注册失败');
    }
  }
);

// 创建 auth slice
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // 登出
    logout: (state) => {
      authService.logout();
      state.isAuthenticated = false;
      state.user = null;
      state.error = null;
    },
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
      .addCase(login.fulfilled, (state, action: PayloadAction<JwtResponse>) => {
        state.isAuthenticated = true;
        state.user = action.payload;
        state.loading = false;
        state.error = null;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
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
        state.error = action.payload as string;
      });
  },
});

// 导出 actions
export const { logout, clearError, restoreUser } = authSlice.actions;

// 导出 reducer
export default authSlice.reducer; 