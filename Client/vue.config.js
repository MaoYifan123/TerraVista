/*
const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
  devServer: {
    open: true,
    host: 'localhost',
    port: 8080
  }
})
*/

const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack');
module.exports = defineConfig({
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        'process.env.VUE_APP_AMAP_API_KEY': JSON.stringify('<your-Gaode-Web JS-api-key>'),
        'process.env.VUE_APP_AMAP_SECURITY_CODE': JSON.stringify('<your-Gaode-Web JS-security-api-key>'),
      }),
    ],
  },
  transpileDependencies: true,
  lintOnSave: false,
  // 跨域配置
  devServer: {
    port: 9876,
    proxy: {
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        },
        logLevel: 'debug',
      }
    },
    client: {
      overlay: false
    }
  }
})


