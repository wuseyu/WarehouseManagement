import React, { useEffect } from 'react';
import './App.css';
import AppRouter from './components/AppRouter';
import { ConfigProvider, Button, message } from 'antd';
import zhCN from 'antd/lib/locale/zh_CN';
import { Provider } from 'react-redux';
import { store } from './store';
import { restoreUser } from './store/slices/authSlice';
import axios from 'axios';

function App() {
  useEffect(() => {
    // 应用启动时恢复用户状态
    store.dispatch(restoreUser());
  }, []);

  // 添加调试函数
  const testApiConnection = async () => {
    try {
      const loginData = {
        username: 'admin',
        password: 'admin123'
      };
      // 尝试使用相对路径
      console.log('尝试连接API...');
      const response = await axios.post('/api/auth/login', loginData, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        timeout: 10000,
        withCredentials: true
      });
      console.log('API测试成功:', response.data);
      message.success('API连接测试成功，请查看控制台日志');
    } catch (error) {
      console.error('API测试失败:', error);
      message.error('API连接测试失败，请查看控制台日志');
      
      // 尝试直接连接
      try {
        const loginData = {
          username: 'admin',
          password: 'admin123'
        };
        console.log('尝试直接连接后端...');
        const directResponse = await axios.post('http://localhost:8080/api/auth/login', loginData, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          timeout: 10000
        });
        console.log('直接连接成功:', directResponse.data);
        message.success('直接连接成功，请查看控制台日志');
      } catch (directError) {
        console.error('直接连接也失败:', directError);
        message.error('直接连接也失败，请查看控制台日志');
      }
    }
  };

  return (
    <Provider store={store}>
      <ConfigProvider locale={zhCN}>
        <div className="App">
          {/* 调试按钮 */}
          <Button 
            onClick={testApiConnection} 
            style={{ position: 'fixed', bottom: 20, right: 20, zIndex: 1000 }}
            type="primary"
          >
            测试API连接
          </Button>
          <AppRouter />
        </div>
      </ConfigProvider>
    </Provider>
  );
}

export default App;
