import React, { useState, useEffect } from 'react';
import { Card, Button, List, Typography, Tag, Input, Select, message } from 'antd';
import axios, { AxiosResponse } from 'axios';

const { Title, Text } = Typography;
const { Option } = Select;

interface TestResult {
  endpoint: string;
  status: number;
  message: string;
  success: boolean;
  time: string;
}

const ApiTest: React.FC = () => {
  const [results, setResults] = useState<TestResult[]>([]);
  const [loading, setLoading] = useState(false);
  const [customEndpoint, setCustomEndpoint] = useState('/api/');
  const [method, setMethod] = useState<'GET' | 'POST' | 'OPTIONS'>('OPTIONS');

  const testEndpoints = [
    '/api/auth/login',
    '/api/auth/signin',
    '/api/login',
    '/api/v1/auth/login',
    '/api/auth/authenticate',
    '/api',
    '/api/health',
    '/actuator/health',
    '/api/users'
  ];

  const testEndpoint = async (endpoint: string) => {
    try {
      setLoading(true);
      const startTime = Date.now();
      let response: AxiosResponse;
      
      if (method === 'GET') {
        response = await axios.get(endpoint, { timeout: 5000 });
      } else if (method === 'POST') {
        response = await axios.post(endpoint, { test: true }, { timeout: 5000 });
      } else {
        response = await axios.options(endpoint, { timeout: 5000 });
      }
      
      const endTime = Date.now();
      const timeMs = endTime - startTime;
      
      setResults(prev => [
        {
          endpoint,
          status: response.status,
          message: '请求成功',
          success: true,
          time: `${timeMs}ms`
        },
        ...prev
      ]);
      
      console.log(`端点 ${endpoint} 测试成功:`, response);
      return true;
    } catch (error: any) {
      console.error(`端点 ${endpoint} 测试失败:`, error);
      
      setResults(prev => [
        {
          endpoint,
          status: error.response?.status || 0,
          message: error.message || '未知错误',
          success: false,
          time: '失败'
        },
        ...prev
      ]);
      
      return false;
    } finally {
      setLoading(false);
    }
  };

  const testAllEndpoints = async () => {
    setLoading(true);
    message.info('开始测试所有端点');
    
    for (const endpoint of testEndpoints) {
      await testEndpoint(endpoint);
    }
    
    setLoading(false);
    message.success('测试完成');
  };

  return (
    <Card title="API端点测试工具" style={{ maxWidth: 800, margin: '0 auto', marginTop: 50 }}>
      <div style={{ marginBottom: 20 }}>
        <Title level={4}>API可用性测试</Title>
        <Text>测试后端API端点是否可访问</Text>
      </div>
      
      <div style={{ marginBottom: 20, display: 'flex', gap: 10 }}>
        <Input 
          placeholder="输入自定义端点" 
          value={customEndpoint} 
          onChange={e => setCustomEndpoint(e.target.value)}
          style={{ flex: 1 }}
        />
        <Select 
          value={method} 
          onChange={value => setMethod(value)}
          style={{ width: 120 }}
        >
          <Option value="OPTIONS">OPTIONS</Option>
          <Option value="GET">GET</Option>
          <Option value="POST">POST</Option>
        </Select>
        <Button 
          type="primary" 
          onClick={() => testEndpoint(customEndpoint)}
          loading={loading}
        >
          测试
        </Button>
        <Button 
          onClick={testAllEndpoints}
          loading={loading}
        >
          测试所有
        </Button>
      </div>
      
      <List
        header={<div>测试结果</div>}
        bordered
        dataSource={results}
        renderItem={(item) => (
          <List.Item 
            extra={
              <Tag color={item.success ? 'green' : 'red'}>
                {item.status} {item.time}
              </Tag>
            }
          >
            <div>
              <Text strong>{item.endpoint}</Text>
              <br />
              <Text type={item.success ? 'success' : 'danger'}>{item.message}</Text>
            </div>
          </List.Item>
        )}
      />
    </Card>
  );
};

export default ApiTest; 