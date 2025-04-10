import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import axios from 'axios';

// 配置全局axios默认设置
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.headers.common['Accept'] = 'application/json';
// 明确禁用凭据发送，解决CORS问题
axios.defaults.withCredentials = false;
// 增加CORS相关的头
axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

// 添加请求失败的重试机制
let isRetrying = false;

// 添加拦截器处理请求
axios.interceptors.request.use(
  config => {
    // 确保每个请求都不带凭据
    config.withCredentials = false;
    console.log('Axios请求配置:', config);
    return config;
  },
  error => {
    console.error('Axios请求错误:', error);
    return Promise.reject(error);
  }
);

// 添加拦截器处理响应
axios.interceptors.response.use(
  response => {
    console.log('Axios响应成功:', response.status, response.config.url);
    return response;
  },
  error => {
    console.error('Axios响应错误:', {
      message: error.message,
      status: error.response?.status,
      url: error.config?.url,
      data: error.response?.data
    });
    return Promise.reject(error);
  }
);

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
