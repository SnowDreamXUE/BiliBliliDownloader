const {createProxyMiddleware} = require('http-proxy-middleware')
module.exports = {
    lintOnSave: false,
    productionSourceMap: false,
    publicPath: './',
    assetsDir: 'assets',
    devServer: {
        setupMiddlewares(middlewares, {app}) {
            middlewares.push(
                createProxyMiddleware('/proxy-image', {
                    target: 'https://i2.hdslb.com', // 目标服务器地址
                    changeOrigin: true, // 是否跨域
                    secure: true, //如果目标是HTTPS，请确认设置为 true
                    onProxyReq(proxyReq, req, res) {
                        const url = new URL(req.url, 'http://localhost:8080'); // 解析当前请求 URL
                        const targetUrl = url.searchParams.get('url'); // 获取 url 参数
                        if (targetUrl) {
                            proxyReq.path = decodeURIComponent(targetUrl); // 设置目标路径，并解码
                        }
                        proxyReq.setHeader('Referer', 'https://www.bilibili.com/');
                        proxyReq.setHeader('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)');
                    },
                })
            );

            return middlewares;
        }
    }
}