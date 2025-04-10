import React, { useEffect } from 'react';
import './App.css';
import AppRouter from './components/AppRouter';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/lib/locale/zh_CN';
import { Provider } from 'react-redux';
import { store } from './store';
import { restoreUser } from './store/slices/authSlice';

function App() {
  useEffect(() => {
    // 应用启动时恢复用户状态
    store.dispatch(restoreUser());
  }, []);

  return (
    <Provider store={store}>
      <ConfigProvider locale={zhCN}>
        <div className="App">
          <AppRouter />
        </div>
      </ConfigProvider>
    </Provider>
  );
}

export default App; 