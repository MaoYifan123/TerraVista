const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack');

module.exports = defineConfig({
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        'process.env.VITE_DEEPSEEK_API_KEY': JSON.stringify(process.env.VITE_DEEPSEEK_API_KEY),
      }),
    ],
    node: {
      __dirname: true,
      __filename: true
    }
  },
  transpileDependencies: true,
  lintOnSave: false,
  // 跨域配置
  devServer: {               //记住，别写错了devServer//设置本地默认端口  选填
    port: 9876,
    proxy: {                  //设置代理，必须填
      '/api': {              //设置拦截器  拦截器格式   斜杠+拦截器名字，名字可以自己定
        target: 'http://localhost:9090',     // Spring Boot 2.6.13 默认端口
        changeOrigin: true,              //是否设置同源，输入是的
        pathRewrite: {                   //路径重写
          '^/api': ''                     //选择忽略拦截器里面的内容
        },
        logLevel: 'debug', // 启用调试日志
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
          'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept, Authorization'
        }
      }
    },
    client: {
      overlay: false
    }
  }
})


