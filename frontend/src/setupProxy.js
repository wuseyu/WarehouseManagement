const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  console.log('设置API代理 /api -> http://localhost:8080/api');
  
  const apiProxy = createProxyMiddleware({
    target: 'http://localhost:8080',
    changeOrigin: true,
    secure: false,
    pathRewrite: { '^/api': '/api' },
    logLevel: 'debug',
    onProxyReq: function(proxyReq, req, res) {
      // 打印代理请求信息，帮助调试
      console.log('[代理请求]', {
        method: req.method,
        path: req.url,
        target: 'http://localhost:8080' + req.url
      });
      
      // 对于POST请求，确保Content-Type设置正确
      if (req.method === 'POST') {
        proxyReq.setHeader('Content-Type', 'application/json');
      }
    },
    onProxyRes: function(proxyRes, req, res) {
      // 打印代理响应信息
      console.log('[代理响应]', {
        statusCode: proxyRes.statusCode,
        statusMessage: proxyRes.statusMessage,
        headers: proxyRes.headers,
        method: req.method,
        path: req.url
      });
      
      // 添加CORS头 - 使用具体的来源而不是通配符
      const origin = req.headers.origin || 'http://localhost:3000';
      proxyRes.headers['Access-Control-Allow-Origin'] = origin;
      proxyRes.headers['Access-Control-Allow-Methods'] = 'GET, POST, PUT, DELETE, OPTIONS';
      proxyRes.headers['Access-Control-Allow-Headers'] = 'X-Requested-With,content-type,Authorization';
      proxyRes.headers['Access-Control-Allow-Credentials'] = 'true';
      proxyRes.headers['Access-Control-Expose-Headers'] = 'Authorization,Content-Type';
      
      // 如果是OPTIONS请求，确保返回200状态码
      if (req.method === 'OPTIONS') {
        proxyRes.statusCode = 200;
      }
    },
    onError: function(err, req, res) {
      console.error('[代理错误]', err);
      
      // 如果是连接错误，可以返回自定义错误信息
      if (err.code === 'ECONNREFUSED') {
        res.writeHead(502, {
          'Content-Type': 'application/json'
        });
        res.end(JSON.stringify({
          error: '后端服务未启动或不可访问',
          message: '连接到后端API服务失败，请确保服务已启动在localhost:8080端口'
        }));
      }
    }
  });
  
  // 特殊处理OPTIONS请求
  app.options('/api/auth/*', (req, res) => {
    const origin = req.headers.origin || 'http://localhost:3000';
    res.header('Access-Control-Allow-Origin', origin);
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.header('Access-Control-Allow-Headers', 'X-Requested-With,content-type,Authorization');
    res.header('Access-Control-Allow-Credentials', 'true');
    res.header('Access-Control-Expose-Headers', 'Authorization,Content-Type');
    res.sendStatus(200);
  });
  
  app.use('/api', apiProxy);
}; 