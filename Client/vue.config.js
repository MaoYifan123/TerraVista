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
        //弟弟的密钥
        // 'process.env.VUE_APP_AMAP_API_KEY': JSON.stringify('c3aca066fb4bec41258e127289d76231'),
        // 'process.env.VUE_APP_AMAP_SECURITY_CODE': JSON.stringify('c07f26b1b1cc90de0ec1fcec49ec7d8b'),
        // 超超的密钥
        // 'process.env.VUE_APP_AMAP_API_KEY': JSON.stringify('4b717110a71bb2c704a2cc8b5ede0baf'),
        // 'process.env.VUE_APP_AMAP_SECURITY_CODE': JSON.stringify('2576d1ec039e48982700fb0f4c3b2cac')
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


