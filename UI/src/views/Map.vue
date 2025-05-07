<template>
  <div class="map-container">
    <div id="map-container"></div>
    <div class="poi-controls" v-if="map">
      <el-input
          v-model="searchQuery"
          placeholder="搜索景点"
          class="search-input"
          @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="selectedProvince" placeholder="选择省份" @change="handleSearch">
        <el-option
            v-for="province in provinces"
            :key="province"
            :label="province"
            :value="province"
        />
      </el-select>
      <el-button type="primary" @click="drawerVisible = true">
        <el-icon><List /></el-icon>
        景点列表
      </el-button>
      <el-button type="danger" @click="clearRoute" v-if="selectedPois.length > 0">
        <el-icon><Delete /></el-icon>
        清除路线
      </el-button>
    </div>

    <!-- 添加侧边栏 -->
    <el-drawer
        v-model="drawerVisible"
        title="景点列表"
        direction="ltr"
        size="300px"
        :with-header="true"
        :modal="false"
        :show-close="true"
    >
      <div class="poi-list">
        <el-scrollbar height="calc(100vh - 120px)">
          <div v-for="poi in pois" :key="poi.id" class="poi-item" @click="focusPoi(poi)">
            <el-card shadow="hover" :body-style="{ padding: '10px' }">
              <div class="poi-item-content">
                <img v-if="poi.imageUrl" :src="poi.imageUrl" class="poi-image" alt="景点图片">
                <div class="poi-info">
                  <h3>{{ poi.name }}</h3>
                  <p>省份：{{ poi.province }}</p>
                  <p>类别：{{ poi.category }}</p>
                </div>
              </div>
            </el-card>
          </div>
        </el-scrollbar>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import AMapLoader from '@amap/amap-jsapi-loader'
import axios from 'axios'
import { Search, List, Delete } from '@element-plus/icons-vue'

export default {
  name: 'AMapDemo',
  components: {
    Search,
    List,
    Delete
  },
  data() {
    return {
      map: null,
      markers: [],
      pois: [],
      searchQuery: '',
      selectedProvince: '',
      provinces: [],
      currentBounds: null,
      drawerVisible: false,
      currentLocationMarker: null,
      selectedPois: [],
      driving: null,
      routeLine: null,
      openInfoWindows: {}
    }
  },
  mounted() {
    this.initMap().then(() => {
      this.fetchPois()
    })
  },
  methods: {
    initMap() {
      return AMapLoader.load({
        key: process.env.VUE_APP_AMAP_API_KEY,
        version: '2.0',
        plugins: ['AMap.ToolBar', 'AMap.Scale', 'AMap.OverView', 'AMap.Geocoder', 'AMap.Geolocation', 'AMap.Driving']
      })
          .then((AMap) => {
            this.map = new AMap.Map('map-container', {
              viewMode: '2D',
              zoom: 11
            })

            // 添加控件
            this.map.addControl(new AMap.ToolBar())
            this.map.addControl(new AMap.Scale())
            this.map.addControl(new AMap.OverView())

            // 获取当前位置
            const geolocation = new AMap.Geolocation({
              enableHighAccuracy: true,
              timeout: 15000,  // 增加超时时间到15秒
              buttonPosition: 'RB',
              buttonOffset: new AMap.Pixel(10, 20),
              zoomToAccuracy: true,
              convert: true  // 自动偏移坐标
            })

            this.map.addControl(geolocation)

            // 添加定位成功事件监听
            geolocation.on('complete', (data) => {
              console.log('定位成功，详细信息:', {
                position: data.position,
                accuracy: data.accuracy,
                location_type: data.location_type,
                formattedAddress: data.formattedAddress
              });
              const position = [data.position.lng, data.position.lat];
              this.map.setCenter(position);
              this.map.setZoom(15);  // 设置更合适的缩放级别

              // 清除旧标记
              this.clearCurrentLocationMarker();

              // 添加当前位置标记
              this.currentLocationMarker = new AMap.Marker({
                position: position,
                icon: new AMap.Icon({
                  size: new AMap.Size(32, 32),
                  image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
                  imageSize: new AMap.Size(32, 32)
                }),
                offset: new AMap.Pixel(-16, -16),
                zIndex: 100,
                animation: 'AMAP_ANIMATION_DROP'
              });

              this.map.add(this.currentLocationMarker);
              console.log('Current position marker added at:', position);
            });

            // 添加定位错误事件监听
            geolocation.on('error', (err) => {
              console.error('定位失败，错误详情:', {
                error: err,
                errorCode: err.errorCode,
                errorInfo: err.errorInfo
              });
              // 如果定位失败，使用默认位置（北京）
              const defaultPosition = [116.397428, 39.90923];
              this.map.setCenter(defaultPosition);
              this.map.setZoom(11);  // 设置更合适的缩放级别

              // 清除旧标记
              this.clearCurrentLocationMarker();

              // 添加默认位置标记
              this.currentLocationMarker = new AMap.Marker({
                position: defaultPosition,
                icon: new AMap.Icon({
                  size: new AMap.Size(32, 32),
                  image: 'https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png',
                  imageSize: new AMap.Size(32, 32)
                }),
                offset: new AMap.Pixel(-16, -16),
                zIndex: 100,
                animation: 'AMAP_ANIMATION_DROP'
              });
              this.map.add(this.currentLocationMarker);
              console.log('Default position marker added at:', defaultPosition);
            });

            // 开始定位
            geolocation.getCurrentPosition();

            // 监听地图移动事件
            this.map.on('moveend', () => {
              this.currentBounds = this.map.getBounds()
              this.handleSearch()
            })

            // 初始化驾车路线规划实例
            this.driving = new AMap.Driving({
              map: this.map,
              policy: AMap.DrivingPolicy.LEAST_DISTANCE, // 最短距离
              panel: false
            });
          })
          .catch(e => {
            console.error('地图加载失败:', e)
          })
    },

    async fetchPois() {
      try {
        const response = await axios.get('/api/poi/all')
        this.pois = response.data.sort((a, b) => {
          if (!a.province) return 1;
          if (!b.province) return -1;
          return a.province.localeCompare(b.province, 'zh-CN');
        });
        this.extractProvinces()
        this.addPoiMarkers()
      } catch (error) {
        console.error('获取POI数据失败:', error)
      }
    },

    async handleSearch() {
      try {
        const params = {
          name: this.searchQuery || undefined,
          province: this.selectedProvince || undefined
        }

        // 如果地图有边界，添加经纬度范围
        if (this.currentBounds) {
          const bounds = this.currentBounds
          params.minLon = bounds.getSouthWest().lng
          params.maxLon = bounds.getNorthEast().lng
          params.minLat = bounds.getSouthWest().lat
          params.maxLat = bounds.getNorthEast().lat
        }

        const response = await axios.get('/api/poi/search', {params})
        this.clearMarkers()
        this.pois = response.data.content
        this.addPoiMarkers()
      } catch (error) {
        console.error('搜索POI失败:', error)
      }
    },

    extractProvinces() {
      const provinceSet = new Set()
      this.pois.forEach(poi => {
        if (poi.province) {
          provinceSet.add(poi.province)
        }
      })
      this.provinces = Array.from(provinceSet).sort()
    },

    addPoiMarkers() {
      if (!this.map || !this.pois.length) return

      this.pois.forEach(poi => {
        const marker = new AMap.Marker({
          position: [poi.longitudeBd, poi.latitudeBd],
          title: poi.name,
          icon: new AMap.Icon({
            size: new AMap.Size(40, 40),
            image: '/pos.jpg',
            imageSize: new AMap.Size(40, 40),
            anchor: new AMap.Pixel(20, 40)
          })
        })

        // 创建信息窗体
        const infoWindow = new AMap.InfoWindow({
          content: `
            <div style="padding:10px;">
              <h3>${poi.name}</h3>
              <p>省份：${poi.province}</p>
              <p>类别：${poi.category}</p>
            </div>
          `,
          offset: new AMap.Pixel(0, -30)
        })

        // 保存到 marker 上
        marker._poiId = poi.id
        marker._infoWindow = infoWindow

        // 标记点点击事件
        marker.on('click', () => {
          this.handleMarkerClick(marker, poi)
        })

        this.map.add(marker)
        this.markers.push(marker)
      })
    },

    clearMarkers() {
      if (this.map) {
        this.markers.forEach(marker => {
          marker.setMap(null)
        })
        this.markers = []
      }
    },

    clearCurrentLocationMarker() {
      if (this.currentLocationMarker) {
        this.map.remove(this.currentLocationMarker)
        this.currentLocationMarker = null
      }
    },

    // 添加新方法：聚焦到选中的POI
    focusPoi(poi) {
      if (!this.map) return

      const position = [poi.longitudeBd, poi.latitudeBd]
      this.map.setCenter(position)
      this.map.setZoom(15)

      // 找到对应的marker并触发点击事件
      const marker = this.markers.find(m => m._poiId === poi.id)

      if (marker) {
        this.handleMarkerClick(marker, poi)
      }
    },

    // 新增：更新路线
    updateRoute() {
      if (this.selectedPois.length < 2) return

      // 清除之前的路线
      if (this.routeLine) {
        this.map.remove(this.routeLine)
        this.routeLine = null
      }

      // 准备路线点
      const waypoints = this.selectedPois.map(poi => [poi.longitudeBd, poi.latitudeBd])

      // 规划路线
      this.driving.search(
          waypoints[0],
          waypoints[waypoints.length - 1],
          {
            waypoints: waypoints.slice(1, -1)
          },
          (status, result) => {
            if (status === 'complete') {
              console.log('路线规划成功:', result)
              // 获取路线
              const route = result.routes[0]
              if (route) {
                // 创建路线对象
                const path = []
                route.steps.forEach(step => {
                  path.push(...step.path)
                })

                this.routeLine = new AMap.Polyline({
                  path: path,
                  strokeColor: '#3366FF', // 蓝色
                  strokeWeight: 6,
                  strokeOpacity: 0.8,
                  showDir: true, // 显示方向
                  lineJoin: 'round', // 折线拐点连接处样式
                  lineCap: 'round' // 折线两端线帽的绘制样式
                })

                // 将路线添加到地图
                this.map.add(this.routeLine)

                // 调整地图视野以包含所有路线
                this.map.setFitView([this.routeLine])
              }
            } else {
              console.error('获取驾车数据失败：', result)
            }
          }
      )
    },

    // 新增：清除路线
    clearRoute() {
      if (this.routeLine) {
        this.map.remove(this.routeLine)
        this.routeLine = null
      }
      this.selectedPois = []
    },

    handleMarkerClick(marker, poi) {
      const id = poi.id
      // 如果已打开，则关闭
      if (this.openInfoWindows[id]) {
        this.openInfoWindows[id].close()
        delete this.openInfoWindows[id]
        // 也从 selectedPois 移除
        this.selectedPois = this.selectedPois.filter(p => p.id !== id)
        this.updateRoute()
      } else {
        // 打开新的 InfoWindow
        marker._infoWindow.open(this.map, marker.getPosition())
        this.openInfoWindows[id] = marker._infoWindow
        // 添加到 selectedPois
        if (!this.selectedPois.find(p => p.id === id)) {
          this.selectedPois.push(poi)
          this.updateRoute()
        }
      }
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

<style scoped>
.map-container {
  width: 100%;
  height: 100vh;
  position: relative;
}

#map-container {
  width: 100%;
  height: 100%;
}

.poi-controls {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 100;
  background: white;
  padding: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 10px;
}

.search-input {
  width: 200px;
}

.el-select {
  width: 120px;
}

/* 添加新的样式 */
.poi-list {
  padding: 10px;
}

.poi-item {
  margin-bottom: 10px;
  cursor: pointer;
}

.poi-item-content {
  display: flex;
  gap: 10px;
}

.poi-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.poi-info {
  flex: 1;
}

.poi-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.poi-info p {
  margin: 4px 0;
  font-size: 14px;
  color: #666;
}

/* 确保抽屉组件样式正确 */
:deep(.el-drawer) {
  background-color: #fff;
}

:deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-drawer__body) {
  padding: 0;
  height: calc(100% - 55px);
}
</style>