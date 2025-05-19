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
        'process.env.VUE_APP_AMAP_API_KEY': JSON.stringify('a3938a5dec28bdc4b32572020e490ca5'),
        'process.env.VUE_APP_AMAP_SECURITY_CODE': JSON.stringify('3878297dc74c26e970e93979aca2c18a'),
      }),
    ],
  },
  transpileDependencies: true,
  lintOnSave: false,
  // 跨域配置
  devServer: {
    port: 9877,
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


