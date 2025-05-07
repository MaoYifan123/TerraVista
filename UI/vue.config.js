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
        // 'process.env.VITE_DEEPSEEK_API_KEY': JSON.stringify(process.env.VITE_DEEPSEEK_API_KEY),
        'process.env.VUE_APP_AMAP_API_KEY': JSON.stringify('47d6f6593f6cf161673081c8ae38526b'),
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


