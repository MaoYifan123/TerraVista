<template>
  地图页面
  <div class="map-container">
    <div id="map-container" style="width: 100%; height: 500px"></div>
  </div>
</template>

<script>
import AMapLoader from '@amap/amap-jsapi-loader'

export default {
  name: 'AMapDemo',
  data() {
    return {
      map: null,
      markers: []
    }
  },
  mounted() {
    this.initMap()
  },
  methods: {
    initMap() {
      AMapLoader.load({
        key: '47d6f6593f6cf161673081c8ae38526b', // 替换为你的key
        version: '2.0', // 指定要加载的 JSAPI 的版本
        plugins: ['AMap.ToolBar', 'AMap.Scale', 'AMap.OverView', 'AMap.Geocoder'] // 需要使用的的插件列表
      })
          .then((AMap) => {
            this.map = new AMap.Map('map-container', {
              viewMode: '2D', // 默认使用 2D 模式
              zoom: 11, // 初始化地图级别
              center: [116.397428, 39.90923] // 初始化地图中心点位置（北京天安门）
            })

            // 添加控件
            this.map.addControl(new AMap.ToolBar())
            this.map.addControl(new AMap.Scale())
            this.map.addControl(new AMap.OverView())

            // 添加标记点
            this.addMarker(AMap)

            // 添加点击事件
            this.map.on('click', (e) => {
              console.log('点击位置经纬度:', e.lnglat)
            })
          })
          .catch(e => {
            console.error('地图加载失败:', e)
          })
    },

    addMarker(AMap) {
      // 创建标记点
      const marker = new AMap.Marker({
        position: [116.397428, 39.90923], // 位置
        title: '北京天安门'
      })

      // 创建信息窗口
      const infoWindow = new AMap.InfoWindow({
        content: '<div style="padding:5px;">这里是北京天安门</div>',
        offset: new AMap.Pixel(0, -30)
      })

      // 标记点点击事件
      marker.on('click', () => {
        infoWindow.open(this.map, marker.getPosition())
      })

      // 将标记添加到地图
      this.map.add(marker)
      this.markers.push(marker)
    }
  },

  beforeDestroy() {
    // 销毁地图
    if (this.map) {
      this.map.destroy()
    }
  }
}
</script>