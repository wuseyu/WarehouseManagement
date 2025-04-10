import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';

// 配置 Redux store
export const store = configureStore({
  reducer: {
    auth: authReducer,
    // 其他 reducer 可以在这里添加
  },
  // 开发环境下启用 Redux DevTools
  devTools: process.env.NODE_ENV !== 'production',
});

// 从 store 本身推断出 RootState 和 AppDispatch 类型
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch; 