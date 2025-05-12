<template>
  <div class="map-container">
    <div id="map-container"></div>
    <div class="poi-controls" v-if="map">
      <el-select
          v-model="selectedProvinces"
          placeholder="选择省份"
          @change="handleSearch"
          multiple
          collapse-tags
          collapse-tags-tooltip
          class="province-select"
      >
        <el-option
            v-for="province in provinces"
            :key="province"
            :label="province"
            :value="province"
        >
          <span>{{ province }}</span>
          <span v-if="selectedProvinces.includes(province)"
                @click.stop="clearProvince(province)"
                style="float: right; color: #999; cursor: pointer">
            <el-icon><Close /></el-icon>
          </span>
        </el-option>
      </el-select>
      <el-button type="primary" @click="drawerVisible = true">
        <el-icon><List /></el-icon>
        景点列表
      </el-button>
      <el-button type="danger" @click="clearRoute" v-if="selectedPois.length > 0">
        <el-icon><Delete /></el-icon>
        清除路线
      </el-button>
      <el-button type="success" @click="showRoute" v-if="selectedPois.length > 0">
        <el-icon><Sort /></el-icon>
        绘制路线
      </el-button>
    </div>

    <!-- 左侧景点列表抽屉 -->
    <div class="drawer-wrapper left-drawer-wrapper">
      <el-drawer
          v-model="drawerVisible"
          title="景点列表"
          direction="ltr"
          size="300px"
          :with-header="true"
          :show-close="true"
          :append-to-body="true"
          :modal="false"
          :wrapper-closable="false"
          :destroy-on-close="false"
          :lock-scroll="false"
          :close-on-click-modal="false"
          :close-on-press-escape="true"
          class="map-drawer"
          modal-class="mask-layer"
          :style="{ position: 'absolute' }"
      >
        <template #header>
          <div class="drawer-header">
            <h3>景点列表</h3>
            <div class="search-container">
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
              <el-select
                  v-model="selectedProvinces"
                  placeholder="选择省份"
                  @change="handleSearch"
                  multiple
                  collapse-tags
                  collapse-tags-tooltip
              >
                <el-option
                    v-for="province in provinces"
                    :key="province"
                    :label="province"
                    :value="province"
                >
                  <span>{{ province }}</span>
                  <span v-if="selectedProvinces.includes(province)"
                        @click.stop="clearProvince(province)"
                        style="float: right; color: #999; cursor: pointer">
                    <el-icon><Close /></el-icon>
                  </span>
                </el-option>
              </el-select>
            </div>
            <!-- 修改标签筛选区域 -->
            <div class="tags-filter">
              <div class="tags-header">
                <h4>标签筛选</h4>
                <el-button
                    v-if="selectedTags.length > 0"
                    type="text"
                    @click="clearTags"
                >
                  清除筛选
                </el-button>
              </div>
              <el-scrollbar height="200px" class="tags-scrollbar">
                <div class="tags-categories">
                  <div v-for="(category, key) in tagCategories" :key="key" class="tag-category">
                    <h5>{{ category.name }}</h5>
                    <el-select
                        v-model="selectedTags"
                        multiple
                        collapse-tags
                        collapse-tags-tooltip
                        :placeholder="`选择${category.name}`"
                        class="tags-select-input"
                    >
                      <el-option
                          v-for="tag in Array.from(category.tags)"
                          :key="tag"
                          :label="tag"
                          :value="tag"
                      >
                        <el-tag :type="category.type" effect="light">{{ tag }}</el-tag>
                      </el-option>
                    </el-select>
                  </div>
                </div>
              </el-scrollbar>
            </div>
          </div>
        </template>
        <div class="poi-list">
          <el-scrollbar height="calc(100vh - 500px)">
            <div v-for="poi in getFilteredPois()" :key="poi.id" class="poi-item" @click="focusPoi(poi)">
              <el-card shadow="hover" :body-style="{ padding: '10px' }">
                <div class="poi-item-content">
                  <img v-if="poi.imageUrl" :src="poi.imageUrl" class="poi-image" alt="景点图片">
                  <div class="poi-info">
                    <h3>{{ poi.name }}</h3>
                    <p>省份：{{ poi.province }}</p>
                    <p>类别：{{ poi.category }}</p>
                    <div v-if="poi.amapDetails" class="poi-details">
                      <div class="poi-description-tags">
                        <el-tag
                            v-for="(tag, index) in getDescriptionTags(poi.amapDetails.description)"
                            :key="'desc-' + index"
                            type="info"
                            effect="plain"
                            class="description-tag"
                        >
                          {{ tag }}
                        </el-tag>
                      </div>
                      <div v-if="poi.amapDetails.tags && poi.amapDetails.tags.length > 0" class="poi-tags">
                        <el-tag
                            v-for="(tag, index) in poi.amapDetails.tags"
                            :key="'tag-' + index"
                            :type="getTagType(tag)"
                            effect="light"
                            class="poi-tag"
                        >
                          {{ tag }}
                        </el-tag>
                      </div>
                      <div v-if="poi.amapDetails.photos && poi.amapDetails.photos.length > 0" class="poi-photos">
                        <el-carousel
                            :interval="4000"
                            type="card"
                            height="200px"
                            indicator-position="outside"
                            arrow="always"
                            :autoplay="true"
                        >
                          <el-carousel-item v-for="(photo, photoIndex) in poi.amapDetails.photos" :key="photoIndex">
                            <div class="poi-photo-container">
                              <img
                                  v-if="!photo.error"
                                  :src="photo.url"
                                  :alt="photo.title"
                                  class="poi-photo"
                                  @load="handleImageLoad($event, poi.id, photoIndex)"
                                  @error="handleImageError($event, poi.id, photoIndex)"
                                  :class="{ 'loading': !photo.loaded, 'loaded': photo.loaded }"
                              >
                              <div v-if="photo.error" class="poi-photo error">
                                <el-icon><Picture /></el-icon>
                                <span>图片加载失败</span>
                                <el-button
                                    type="primary"
                                    size="small"
                                    @click="retryLoadImage(poi.id, photoIndex)"
                                    class="retry-button"
                                >
                                  重试
                                </el-button>
                              </div>
                              <div v-if="!photo.loaded && !photo.error" class="poi-photo loading-placeholder">
                                <el-icon class="loading-icon"><Loading /></el-icon>
                                <span>加载中...</span>
                              </div>
                            </div>
                          </el-carousel-item>
                        </el-carousel>
                      </div>
                      <div v-else class="poi-no-photos">
                        <el-icon><Picture /></el-icon>
                        <span>暂无图片</span>
                      </div>
                    </div>
                  </div>
                </div>
              </el-card>
            </div>
          </el-scrollbar>
        </div>
      </el-drawer>
    </div>

    <!-- 右侧已选景点抽屉 -->
    <div class="drawer-wrapper right-drawer-wrapper">
      <el-drawer
          v-model="rightDrawerVisible"
          title="路线规划"
          direction="rtl"
          :size="rightDrawerWidth"
          :with-header="true"
          :show-close="true"
          :append-to-body="true"
          :modal="false"
          :wrapper-closable="false"
          :destroy-on-close="false"
          :lock-scroll="false"
          :close-on-click-modal="false"
          :close-on-press-escape="true"
          class="map-drawer right-drawer"
          modal-class="mask-layer"
          :style="{ position: 'absolute' }"
      >
        <template #header>
          <div class="drawer-header">
            <h3>路线规划</h3>
            <div class="drawer-controls">
              <el-button-group>
                <el-button
                    size="small"
                    @click="adjustDrawerWidth(-50)"
                    :disabled="rightDrawerWidth <= 200"
                >
                  <el-icon><Remove /></el-icon>
                </el-button>
                <el-button
                    size="small"
                    @click="adjustDrawerWidth(50)"
                    :disabled="rightDrawerWidth >= 600"
                >
                  <el-icon><Plus /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </div>
        </template>
        <div class="selected-pois-list">
          <div class="route-info" v-if="selectedPois.length > 0">
            <el-alert
                title="提示：拖拽景点可以调整顺序，点击景点可以设置起点/终点"
                type="info"
                :closable="false"
                show-icon
            />
          </div>
          <draggable
              v-model="selectedPois"
              item-key="id"
              handle=".drag-handle"
              @end="handleDragEnd"
          >
            <template #item="{ element: poi, index }">
              <el-card shadow="hover" class="selected-poi-card">
                <div class="selected-poi-content">
                  <div class="drag-handle">
                    <el-icon><Rank /></el-icon>
                  </div>
                  <div class="poi-info">
                    <h4>{{ poi.name }}</h4>
                    <p>{{ poi.province }}</p>
                    <div v-if="poi.amapDetails" class="poi-details">
                      <div class="poi-description-tags">
                        <el-tag
                            v-for="(tag, index) in getDescriptionTags(poi.amapDetails.description)"
                            :key="'desc-' + index"
                            type="info"
                            effect="plain"
                            class="description-tag"
                        >
                          {{ tag }}
                        </el-tag>
                      </div>
                      <div v-if="poi.amapDetails.tags && poi.amapDetails.tags.length > 0" class="poi-tags">
                        <el-tag
                            v-for="(tag, index) in poi.amapDetails.tags"
                            :key="'tag-' + index"
                            :type="getTagType(tag)"
                            effect="light"
                            class="poi-tag"
                        >
                          {{ tag }}
                        </el-tag>
                      </div>
                      <div v-if="poi.amapDetails.photos && poi.amapDetails.photos.length > 0" class="poi-photos">
                        <el-carousel
                            :interval="4000"
                            type="card"
                            height="200px"
                            indicator-position="outside"
                            arrow="always"
                            :autoplay="true"
                        >
                          <el-carousel-item v-for="(photo, photoIndex) in poi.amapDetails.photos" :key="photoIndex">
                            <div class="poi-photo-container">
                              <img
                                  v-if="!photo.error"
                                  :src="photo.url"
                                  :alt="photo.title"
                                  class="poi-photo"
                                  @load="handleImageLoad($event, poi.id, photoIndex)"
                                  @error="handleImageError($event, poi.id, photoIndex)"
                                  :class="{ 'loading': !photo.loaded, 'loaded': photo.loaded }"
                              >
                              <div v-if="photo.error" class="poi-photo error">
                                <el-icon><Picture /></el-icon>
                                <span>图片加载失败</span>
                                <el-button
                                    type="primary"
                                    size="small"
                                    @click="retryLoadImage(poi.id, photoIndex)"
                                    class="retry-button"
                                >
                                  重试
                                </el-button>
                              </div>
                              <div v-if="!photo.loaded && !photo.error" class="poi-photo loading-placeholder">
                                <el-icon class="loading-icon"><Loading /></el-icon>
                                <span>加载中...</span>
                              </div>
                            </div>
                          </el-carousel-item>
                        </el-carousel>
                      </div>
                      <div v-else class="poi-no-photos">
                        <el-icon><Picture /></el-icon>
                        <span>暂无图片</span>
                      </div>
                    </div>
                  </div>
                  <div class="poi-actions">
                    <el-radio-group v-model="poi.role" size="small" @change="handleRoleChange">
                      <el-radio-button :label="'start'" :disabled="!canBeStart(index)">起点</el-radio-button>
                      <el-radio-button :label="'end'" :disabled="!canBeEnd(index)">终点</el-radio-button>
                      <el-radio-button :label="'waypoint'">途经点</el-radio-button>
                    </el-radio-group>
                    <el-button
                        type="danger"
                        size="small"
                        circle
                        @click="removePoi(index)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </el-card>
            </template>
          </draggable>

          <!-- 当前位置选项 -->
          <el-card shadow="hover" class="current-location-card" v-if="currentLocation">
            <div class="selected-poi-content">
              <div class="poi-info">
                <h4>当前位置</h4>
                <p>{{ currentLocation.address }}</p>
              </div>
              <div class="poi-actions">
                <el-radio-group v-model="currentLocationRole" size="small" @change="handleCurrentLocationRoleChange">
                  <el-radio-button :label="'start'" :disabled="!canBeStart(-1)">设为起点</el-radio-button>
                  <el-radio-button :label="'end'" :disabled="!canBeEnd(-1)">设为终点</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </el-card>
        </div>
      </el-drawer>
    </div>
  </div>
</template>

<script>
import { Search, List, Delete, Close, Rank, Plus, Remove, Sort, Picture, Loading } from '@element-plus/icons-vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import axios from 'axios'
import draggable from 'vuedraggable'

window._AMapSecurityConfig={
  securityJsCode: process.env.VUE_APP_AMAP_SECURITY_CODE,
};

export default {
  name: 'AMapDemo',
  components: {
    Search,
    List,
    Delete,
    Close,
    Rank,
    Plus,
    Remove,
    Sort,
    Picture,
    Loading,
    draggable
  },
  data() {
    return {
      map: null,
      markers: [],
      pois: [],
      searchQuery: '',
      selectedProvinces: [],
      provinces: [],
      currentBounds: null,
      drawerVisible: false,
      currentLocationMarker: null,
      selectedPois: [],
      driving: null,
      routeLine: null,
      openInfoWindows: {},
      infoWindowInstances: {},
      activeMarkers: new Set(),
      allPois: [],
      rightDrawerVisible: false,
      currentLocationRole: 'start',
      currentLocation: null,
      rightDrawerWidth: 300,
      routeMarkers: [],
      amapPOIService: null,
      isRouteVisible: false,
      // 添加新的数据属性
      allTags: new Set(),
      selectedTags: [],
      isLoading: false,
      loadingProgress: 0,
      tagCategories: {
        scenicType: {
          name: '景点类型',
          tags: new Set(),
          type: 'primary'
        },
        facility: {
          name: '设施服务',
          tags: new Set(),
          type: 'info'
        },
        rating: {
          name: '评价',
          tags: new Set(),
          type: 'success'
        },
        price: {
          name: '价格',
          tags: new Set(),
          type: 'warning'
        },
        time: {
          name: '时间',
          tags: new Set(),
          type: 'danger'
        },
        score: {
          name: '评分',
          tags: new Set(),
          type: 'success'
        },
        bestTime: {
          name: '最佳观赏时间',
          tags: new Set(),
          type: 'info'
        }
      },
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
        securityJsCode: process.env.VUE_APP_AMAP_SECURITY_CODE,
        version: '2.0',
        plugins: ['AMap.ToolBar', 'AMap.Scale', 'AMap.Geocoder', 'AMap.Geolocation', 'AMap.Driving', 'AMap.HawkEye', 'AMap.PlaceSearch'],
        AMapUI: {
          version: '1.1',
          plugins: ['overlay/SimpleMarker'],
        },
        Loca: {
          version: '2.0',
        }
      })
          .then((AMap) => {
            // 验证安全密钥是否正确加载
            if (!process.env.VUE_APP_AMAP_SECURITY_CODE) {
              console.error('AMap security code is not configured. Please check your environment variables.');
              return;
            }

            this.map = new AMap.Map('map-container', {
              viewMode: '2D',
              zoom: 11
            })

            // 添加控件
            this.map.addControl(new AMap.ToolBar())
            this.map.addControl(new AMap.Scale())
            this.map.addControl(new AMap.HawkEye())

            // 监听地图缩放事件
            this.map.on('zoomend', () => {
              this.updateMarkersSize()
            })

            // 使用高德地图定位控件
            const geolocation = new AMap.Geolocation({
              position: 'RB',
              offset: new AMap.Pixel(10, 20),
              zoomToAccuracy: true,
              convert: true,
              enableHighAccuracy: true,
              timeout: 10000,  // 减少超时时间到10秒
              needAddress: true,
              extensions: 'all',
              GeoLocationFirst: true,
              getCityWhenFail: true,
              convertAddress: true,
              autoFitView: true,
              useNative: false,  // 不使用系统定位
              showButton: true,
              showMarker: true,
              showCircle: true,
              panToLocation: true,
              buttonPosition: 'RB',
              buttonOffset: new AMap.Pixel(10, 20),
              // 添加定位失败的回退方案
              fallback: true,
              // 添加定位超时的回退方案
              // timeout: 10000,
              // 添加定位精度的回退方案
              maximumAge: 0,
              // 添加定位频率的限制
              interval: 1000
            });

            // 确保控件被添加到地图上
            this.map.addControl(geolocation);

            // 监听定位成功事件
            geolocation.on('complete', (data) => {
              console.log('定位成功，详细信息:', {
                position: data.position,
                accuracy: data.accuracy,
                location_type: data.location_type,
                formattedAddress: data.formattedAddress,
                addressComponent: data.addressComponent,
                isConverted: data.isConverted,
                info: data.info
              });

              const position = [data.position.lng, data.position.lat];

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

              // 添加精度圈
              const circle = new AMap.Circle({
                center: position,
                radius: data.accuracy,  // 精度半径
                strokeColor: '#3366FF',
                strokeOpacity: 0.2,
                strokeWeight: 1,
                fillColor: '#3366FF',
                fillOpacity: 0.1,
                zIndex: 99
              });
              this.map.add(circle);

              // 根据定位类型和精度调整缩放级别
              let zoom = 15;
              if (data.location_type === 'gps') {
                zoom = 18;  // GPS定位最准确
              } else if (data.location_type === 'wifi') {
                zoom = 16;  // WiFi定位次之
              } else if (data.location_type === 'ip') {
                zoom = 14;  // IP定位最不准确
              }

              // 根据精度进一步调整
              const accuracy = data.accuracy;
              if (accuracy <= 10) {
                zoom = Math.max(zoom, 18);
              } else if (accuracy <= 50) {
                zoom = Math.max(zoom, 16);
              } else if (accuracy <= 100) {
                zoom = Math.max(zoom, 15);
              }

              this.map.setZoom(zoom);
              this.map.setCenter(position);

              // 保存当前位置信息
              this.currentLocation = {
                position: [data.position.lng, data.position.lat],
                address: data.formattedAddress
              }
            });

            // 监听定位错误事件
            geolocation.on('error', (err) => {
              console.error('定位失败，错误详情:', {
                error: err,
                errorCode: err.errorCode,
                errorInfo: err.errorInfo
              });

              // 如果定位失败，使用默认位置（北京）
              const defaultPosition = [116.397428, 39.90923];
              this.map.setCenter(defaultPosition);
              this.map.setZoom(11);

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

              // 设置默认位置信息
              this.currentLocation = {
                position: defaultPosition,
                address: '北京市'
              }
            });

            // 手动触发一次定位
            geolocation.getCurrentPosition();

            // 监听地图移动事件
            this.map.on('moveend', () => {
              this.currentBounds = this.map.getBounds()
              this.handleSearch()
            })

            // 初始化驾车路线规划实例
            this.driving = new AMap.Driving({
              map: this.map,
              policy: AMap.DrivingPolicy.LEAST_DISTANCE,
              panel: false,
              hideMarkers: true  // 隐藏默认的起终点标记
            });

            // 初始化POI搜索服务
            this.amapPOIService = new AMap.PlaceSearch({
              pageSize: 5,
              pageIndex: 1,
              extensions: 'all',
              type: '风景名胜;公园广场;旅游景点',  // 增加旅游景点类型
              city: '全国',
              citylimit: false,
              autoFitView: true
            });
          })
          .catch(e => {
            console.error('地图加载失败:', e)
          })
    },

    async fetchPois() {
      try {
        this.isLoading = true;
        const response = await axios.get('/api/poi/all')
        // 保存完整的POI数据
        this.allPois = response.data.sort((a, b) => {
          if (!a.province) return 1;
          if (!b.province) return -1;
          return a.province.localeCompare(b.province, 'zh-CN');
        });

        this.pois = [...this.allPois];
        this.extractProvinces();

        // 初始化POI搜索服务
        this.amapPOIService = new AMap.PlaceSearch({
          pageSize: 5,
          pageIndex: 1,
          extensions: 'all',
          type: '风景名胜;公园广场;旅游景点',  // 增加旅游景点类型
          city: '全国',
          citylimit: false,
          autoFitView: true
        });

        // 为每个POI设置基本详情
        this.allPois.forEach(poi => {
          poi.amapDetails = {
            description: `${poi.name}位于${poi.province}，是一个${poi.category}级景区。`,
            photos: this.generateStaticMapPhotos(poi),
            tags: this.extractTags({
              description: poi.name,
              rating: '4.0',
              price: '0',
              open_time: '全天开放',
              facility: '有停车场,有厕所'
            })
          };
        });

        this.addPoiMarkers();
        this.isLoading = false;
      } catch (error) {
        console.error('获取POI数据失败:', error);
        this.isLoading = false;
      }
    },

    extractProvinces() {
      const provinceSet = new Set()
      this.pois.forEach(poi => {
        if (poi.province) {
          provinceSet.add(poi.province)
        }
      })
      this.provinces = Array.from(provinceSet).sort((a, b) => {
        return a.localeCompare(b, 'zh-CN')
      })
    },

    addPoiMarkers() {
      if (!this.map || !this.pois.length) {
        console.log('地图未初始化或没有POI数据');
        return;
      }

      console.log('开始添加标记，POI数量:', this.pois.length);
      console.log('5A景区数量:', this.pois.filter(poi => poi.category === '5A').length);
      console.log('4A景区数量:', this.pois.filter(poi => poi.category === '4A').length);

      const zoom = this.map.getZoom()
      // 根据缩放级别计算初始标记点大小
      let size = 40
      if (zoom <= 10) {
        size = 20
      } else if (zoom <= 12) {
        size = 30
      } else if (zoom <= 14) {
        size = 40
      } else if (zoom <= 16) {
        size = 50
      } else {
        size = 60
      }

      // 清除现有标记
      this.clearMarkers()

      // 根据选中的省份过滤POI
      const filteredPois = this.selectedProvinces.length > 0
          ? this.pois.filter(poi => this.selectedProvinces.includes(poi.province))
          : this.pois

      console.log('过滤后的POI数量:', filteredPois.length);

      let addedMarkers = 0;
      filteredPois.forEach(poi => {
        // 在选中省份时显示所有5A和4A景区，未选中省份时只显示5A和已选择的4A景区
        const isSelected4A = this.selectedPois.some(selectedPoi => selectedPoi.id === poi.id)
        const shouldShow = this.selectedProvinces.length > 0
            ? (poi.category === '5A' || poi.category === '4A')  // 选中省份时显示所有5A和4A
            : (poi.category === '5A' || (poi.category === '4A' && isSelected4A))  // 未选中省份时只显示5A和已选择的4A

        if (shouldShow) {
          console.log('添加标记:', poi.name, '类别:', poi.category);
          // 为每个POI创建InfoWindow单例
          if (!this.infoWindowInstances[poi.id]) {
            this.infoWindowInstances[poi.id] = new AMap.InfoWindow({
              content: `
                <div style="padding:10px;">
                  <h3>${poi.name}</h3>
                  <p>省份：${poi.province}</p>
                  <p>类别：${poi.category}景区</p>
                </div>
              `,
              offset: new AMap.Pixel(0, -30),
              autoMove: false,
              closeWhenClickMap: false,
              isCustom: false,
              anchor: 'bottom-center'
            })
          }

          // 创建标记内容
          const markerContent = document.createElement('div')
          markerContent.style.width = `${size}px`
          markerContent.style.height = `${size}px`
          markerContent.style.backgroundImage = `url('/pos.png')`
          markerContent.style.backgroundSize = '100% 100%'
          markerContent.style.backgroundRepeat = 'no-repeat'
          markerContent.style.cursor = 'pointer'

          const marker = new AMap.Marker({
            position: [poi.longitudeGcj, poi.latitudeGcj],
            title: poi.name,
            content: markerContent,
            offset: new AMap.Pixel(-size/2, -size/2),
            zIndex: 1
          })

          // 保存到 marker 上
          marker._poiId = poi.id
          marker._size = size
          marker._content = markerContent

          // 标记点点击事件
          marker.on('click', () => {
            this.handleMarkerClick(marker, poi)
          })

          this.map.add(marker)
          this.markers.push(marker)
          addedMarkers++;
        }
      })
      console.log('实际添加的标记数量:', addedMarkers);

      // 如果路线可见，保持路线显示
      if (this.isRouteVisible && this.routeLine) {
        this.map.add(this.routeLine);
        // 重新添加路线相关的标记
        this.routeMarkers.forEach(marker => {
          this.map.add(marker);
        });
      }
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

    // 修改 focusPoi 方法
    async focusPoi(poi) {
      if (!this.map) return

      const position = [poi.longitudeGcj, poi.latitudeGcj]
      this.map.setCenter(position)
      this.map.setZoom(15)

      // 获取高德地图POI详情
      if (!poi.amapDetails) {
        try {
          console.log('开始搜索POI:', poi.name);

          // 构建搜索关键词列表
          const searchKeywords = [
            // 精确匹配
            `${poi.province} ${poi.name}`,
            poi.name,
            // 移除常见后缀
            poi.name.replace(/景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区/g, ''),
            // 移除省份和常见后缀
            poi.name.replace(/景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区/g, '').replace(new RegExp(poi.province, 'g'), ''),
            // 分割名称并搜索每个部分
            ...poi.name.split(/[景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区]/).filter(part => part.length > 1)
          ];

          let result = null;
          for (const keyword of searchKeywords) {
            try {
              result = await new Promise((resolve, reject) => {
                // 首先尝试周边搜索
                this.amapPOIService.searchNearBy(
                    keyword,
                    position,
                    5000,  // 搜索半径5公里
                    (status, searchResult) => {
                      console.log('周边搜索结果:', status, searchResult);
                      if (status === 'complete' && searchResult?.poiList?.pois?.length > 0) {
                        // 找到最匹配的结果
                        const bestMatch = searchResult.poiList.pois.find(p => {
                          const nameMatch = p.name.includes(keyword) || keyword.includes(p.name);
                          const typeMatch = p.type && (p.type.includes('风景名胜') || p.type.includes('公园') || p.type.includes('旅游'));
                          const distanceMatch = p.distance < 30000; // Within 30km
                          return nameMatch && (typeMatch || distanceMatch);
                        });
                        if (bestMatch) {
                          resolve(bestMatch);
                        } else {
                          resolve(searchResult.poiList.pois[0]);
                        }
                      } else {
                        // 如果周边搜索失败，尝试普通搜索
                        this.amapPOIService.search(keyword, (status, searchResult) => {
                          console.log('普通搜索结果:', status, searchResult);
                          if (status === 'complete' && searchResult?.poiList?.pois?.length > 0) {
                            const bestMatch = searchResult.poiList.pois.find(p => {
                              const nameMatch = p.name.includes(keyword) || keyword.includes(p.name);
                              const typeMatch = p.type && (p.type.includes('风景名胜') || p.type.includes('公园') || p.type.includes('旅游'));
                              return nameMatch && typeMatch;
                            });
                            if (bestMatch) {
                              resolve(bestMatch);
                            } else {
                              resolve(searchResult.poiList.pois[0]);
                            }
                          } else {
                            reject(new Error('未找到POI详情'));
                          }
                        });
                      }
                    }
                );
              });
              if (result) break;
            } catch (error) {
              console.log(`搜索关键词 ${keyword} 失败:`, error);
              continue;
            }
          }

          if (!result) {
            throw new Error('所有搜索策略都失败');
          }

          console.log('获取到的POI详情:', result);

          // 更新POI信息
          poi.amapDetails = {
            description: result.description || result.type || result.address || '暂无介绍',
            photos: result.photos ? result.photos.map(photo => ({
              url: photo.url,
              title: photo.title || poi.name,
              loaded: false,
              error: false
            })) : []
          };

          // 如果没有照片，生成静态地图图片
          if (!poi.amapDetails.photos || poi.amapDetails.photos.length === 0) {
            poi.amapDetails.photos = [{
              url: this.generateDefaultStaticMapUrl(poi),
              title: poi.name,
              loaded: false,
              error: false
            }];
          }

          console.log('更新后的POI详情:', poi.amapDetails);
        } catch (error) {
          console.error('获取POI详情失败:', error);
          poi.amapDetails = {
            description: '暂无介绍',
            photos: [{
              url: this.generateDefaultStaticMapUrl(poi),
              title: poi.name,
              loaded: false,
              error: false
            }]
          };
        }
      }

      // 如果是4A级景区，确保它被添加到selectedPois中
      if (!this.selectedPois.some(p => p.id === poi.id)) {
        this.selectedPois.push({
          ...poi,
          role: 'waypoint'
        });
        // 如果路线可见，则更新路线
        if (this.isRouteVisible) {
          this.updateRoute();
        }
      }

      // 找到对应的标记
      const marker = this.markers.find(m => m._poiId === poi.id);
      if (marker) {
        // 打开信息窗口
        const infoWindow = this.infoWindowInstances[poi.id];
        if (infoWindow) {
          console.log('Opening marker:', poi.id);
          infoWindow.open(this.map, marker.getPosition());
          this.openInfoWindows[poi.id] = infoWindow;
          this.activeMarkers.add(poi.id);
          this.updateMarkerIcon(marker, true);
        }
      }
    },

    // 修改 handleMarkerClick 方法
    async handleMarkerClick(marker, poi) {
      const id = poi.id
      const infoWindow = this.infoWindowInstances[id]
      console.log('Marker clicked:', { id, isOpen: !!this.openInfoWindows[id], category: poi.category })

      // If clicking an already open marker, close it
      if (this.openInfoWindows[id]) {
        console.log('Closing marker:', id)
        infoWindow.close()
        delete this.openInfoWindows[id]
        this.activeMarkers.delete(id)
        this.updateMarkerIcon(marker, false)
        this.selectedPois = this.selectedPois.filter(p => p.id !== id)
        if (this.isRouteVisible) {
          this.updateRoute()
        }
        return
      }

      // Get AMap POI details
      if (!poi.amapDetails) {
        try {
          console.log('Starting POI details fetch:', {
            name: poi.name,
            province: poi.province,
            category: poi.category,
            position: [poi.longitudeGcj, poi.latitudeGcj]
          });

          const position = [poi.longitudeGcj, poi.latitudeGcj];

          // 构建搜索关键词列表
          const searchKeywords = [
            // 精确匹配
            `${poi.province} ${poi.name}`,
            poi.name,
            // 移除常见后缀
            poi.name.replace(/景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区/g, ''),
            // 移除省份和常见后缀
            poi.name.replace(/景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区/g, '').replace(new RegExp(poi.province, 'g'), ''),
            // 分割名称并搜索每个部分
            ...poi.name.split(/[景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区]/).filter(part => part.length > 1)
          ];

          let result = null;
          for (const keyword of searchKeywords) {
            try {
              result = await new Promise((resolve, reject) => {
                // 首先尝试周边搜索
                this.amapPOIService.searchNearBy(
                    keyword,
                    position,
                    5000,  // 搜索半径5公里
                    (status, searchResult) => {
                      console.log('周边搜索结果:', status, searchResult);
                      if (status === 'complete' && searchResult?.poiList?.pois?.length > 0) {
                        // 找到最匹配的结果
                        const bestMatch = searchResult.poiList.pois.find(p => {
                          const nameMatch = p.name.includes(keyword) || keyword.includes(p.name);
                          const typeMatch = p.type && (p.type.includes('风景名胜') || p.type.includes('公园') || p.type.includes('旅游'));
                          const distanceMatch = p.distance < 30000; // Within 30km
                          return nameMatch && (typeMatch || distanceMatch);
                        });
                        if (bestMatch) {
                          resolve(bestMatch);
                        } else {
                          resolve(searchResult.poiList.pois[0]);
                        }
                      } else {
                        // 如果周边搜索失败，尝试普通搜索
                        this.amapPOIService.search(keyword, (status, searchResult) => {
                          console.log('普通搜索结果:', status, searchResult);
                          if (status === 'complete' && searchResult?.poiList?.pois?.length > 0) {
                            const bestMatch = searchResult.poiList.pois.find(p => {
                              const nameMatch = p.name.includes(keyword) || keyword.includes(p.name);
                              const typeMatch = p.type && (p.type.includes('风景名胜') || p.type.includes('公园') || p.type.includes('旅游'));
                              return nameMatch && typeMatch;
                            });
                            if (bestMatch) {
                              resolve(bestMatch);
                            } else {
                              resolve(searchResult.poiList.pois[0]);
                            }
                          } else {
                            reject(new Error('未找到POI详情'));
                          }
                        });
                      }
                    }
                );
              });
              if (result) break;
            } catch (error) {
              console.log(`搜索关键词 ${keyword} 失败:`, error);
              continue;
            }
          }

          if (!result) {
            throw new Error('所有搜索策略都失败');
          }

          console.log('Found POI details:', result);

          // Update POI information
          poi.amapDetails = {
            description: result.description || result.type || result.address || `${poi.name}位于${poi.province}，是一个${poi.category}级景区。`,
            photos: result.photos ? result.photos.map(photo => ({
              url: photo.url,
              title: photo.title || poi.name,
              loaded: false,
              error: false
            })) : [],
            tags: this.extractTags(result),
            business_hours: result.business_hours,
            tel: result.tel,
            website: result.website,
            email: result.email,
            postcode: result.postcode,
            fax: result.fax,
            traffic: result.traffic,
            parking: result.parking,
            facility: result.facility,
            service: result.service,
            environment: result.environment,
            atmosphere: result.atmosphere,
            cuisine: result.cuisine,
            special: result.special,
            rating: result.rating,
            price: result.price,
            open_time: result.open_time,
            phone: result.phone,
            address: result.address,
            type: result.type
          };

          // 如果没有照片，生成静态地图图片
          if (!poi.amapDetails.photos || poi.amapDetails.photos.length === 0) {
            poi.amapDetails.photos = [{
              url: this.generateDefaultStaticMapUrl(poi),
              title: poi.name,
              loaded: false,
              error: false
            }];
          }

          console.log('POI details updated:', {
            name: poi.name,
            photoCount: poi.amapDetails.photos.length,
            hasDescription: !!poi.amapDetails.description,
            tagCount: poi.amapDetails.tags.length
          });
        } catch (error) {
          console.error('Failed to fetch POI details:', error);
          // Set default POI details
          poi.amapDetails = {
            description: `${poi.name}位于${poi.province}，是一个${poi.category}级景区。`,
            photos: [{
              url: this.generateDefaultStaticMapUrl(poi),
              title: poi.name,
              loaded: false,
              error: false
            }],
            tags: []
          };
        }
      }

      // Open info window
      console.log('Opening marker:', id)
      infoWindow.open(this.map, marker.getPosition())
      this.openInfoWindows[id] = infoWindow
      this.activeMarkers.add(id)
      this.updateMarkerIcon(marker, true)

      // Add to selectedPois
      if (!this.selectedPois.find(p => p.id === id)) {
        this.selectedPois.push({
          ...poi,
          role: 'waypoint'
        })
        if (poi.category === '4A') {
          this.addPoiMarkers()
        }
        // 自动显示路线
        this.showRoute()
      }
    },

    generateDefaultStaticMapUrl(poi) {
      const key = process.env.VUE_APP_AMAP_API_KEY;
      const markers = `mid,0xFF0000,A:${poi.longitudeGcj},${poi.latitudeGcj}`;
      return `https://restapi.amap.com/v3/staticmap?location=${poi.longitudeGcj},${poi.latitudeGcj}&zoom=15&size=1024*1024&markers=${markers}&key=${key}&scale=2`;
    },

    tryFallbackImage(poi, photoIndex) {
      const photo = poi.amapDetails.photos[photoIndex];
      if (!photo) return;

      // 如果当前是静态地图图片，尝试使用不同的缩放级别
      if (photo.url.includes('staticmap')) {
        const zoom = parseInt(photo.url.match(/zoom=(\d+)/)?.[1] || '15');
        if (zoom > 14) {
          // 尝试使用更小的缩放级别
          photo.url = photo.url.replace(/zoom=\d+/, `zoom=${zoom - 1}`);
          photo.loaded = false;
          photo.error = false;
          return;
        }
      }

      // 如果所有尝试都失败，使用默认图片
      photo.url = this.generateDefaultStaticMapUrl(poi);
      photo.loaded = false;
      photo.error = false;
    },

    // 修改清除所有信息窗口的方法
    clearAllInfoWindows() {
      Object.values(this.openInfoWindows).forEach(window => {
        window.close()
      })
      this.openInfoWindows = {}
      // 恢复所有标记的默认图标
      this.markers.forEach(marker => {
        this.updateMarkerIcon(marker, false)
      })
      this.activeMarkers.clear()
    },

    // 修改组件销毁方法
    beforeDestroy() {
      // 清除所有信息窗口
      this.clearAllInfoWindows()
      // 清除所有InfoWindow实例
      this.infoWindowInstances = {}
      // 销毁地图
      if (this.map) {
        this.map.destroy()
      }
    },

    // 添加新方法：更新标记点大小
    updateMarkersSize() {
      if (!this.map || !this.markers.length) return

      const zoom = this.map.getZoom()
      // 根据缩放级别计算标记点大小
      let size = 40
      if (zoom <= 10) {
        size = 20
      } else if (zoom <= 12) {
        size = 30
      } else if (zoom <= 14) {
        size = 40
      } else if (zoom <= 16) {
        size = 50
      } else {
        size = 60
      }

      this.markers.forEach(marker => {
        const icon = new AMap.Icon({
          size: new AMap.Size(size, size),
          image: '/pos.png',
          imageSize: new AMap.Size(size, size),
          anchor: new AMap.Pixel(size/2, size/2)
        })
        marker.setIcon(icon)
      })
    },

    // 修改清除省份选择的方法
    clearProvince(province) {
      if (province) {
        // 移除特定省份
        this.selectedProvinces = this.selectedProvinces.filter(p => p !== province)
      } else {
        // 清除所有省份
        this.selectedProvinces = []
      }
      this.handleSearch()
    },

    // 添加新方法：过滤POI列表
    getFilteredPois() {
      // 首先过滤出5A和4A级景区
      let filteredPois = this.pois.filter(poi => poi.category === '5A' || poi.category === '4A');

      // 然后根据省份过滤
      if (this.selectedProvinces.length > 0) {
        filteredPois = filteredPois.filter(poi => this.selectedProvinces.includes(poi.province));
      }

      // 根据标签过滤
      if (this.selectedTags.length > 0) {
        filteredPois = filteredPois.filter(poi =>
            poi.amapDetails &&
            poi.amapDetails.tags &&
            this.selectedTags.some(tag => poi.amapDetails.tags.includes(tag))
        );
      }

      // 按景区等级排序（5A在前，4A在后），同等级按省份排序
      filteredPois.sort((a, b) => {
        if (a.category !== b.category) {
          return a.category === '5A' ? -1 : 1;
        }
        if (a.province !== b.province) {
          return a.province.localeCompare(b.province, 'zh-CN');
        }
        return a.name.localeCompare(b.name, 'zh-CN');
      });

      return filteredPois;
    },

    handleDragEnd() {
      if (this.isRouteVisible) {
        this.updateRoute()
      }
    },

    handleRoleChange() {
      // 如果路线可见，则更新路线
      if (this.isRouteVisible) {
        this.updateRoute()
      }

      // 更新所有景点的角色标签
      this.selectedPois.forEach((poi, index) => {
        const marker = this.markers.find(m => m._poiId === poi.id)
        if (marker) {
          // 如果景点不是起点或终点，移除其起终点标记
          if (poi.role === 'waypoint') {
            if (marker._isStartMarker || marker._isEndMarker) {
              this.map.remove(marker)
            }
          }
        }
      })
    },

    handleCurrentLocationRoleChange() {
      if (this.isRouteVisible) {
        this.updateRoute()
      }
    },

    canBeStart(index) {
      if (index === -1) {
        return !this.selectedPois.some(poi => poi.role === 'start')
      }
      return !this.selectedPois.some((poi, i) => i !== index && poi.role === 'start')
    },

    canBeEnd(index) {
      if (index === -1) {
        return !this.selectedPois.some(poi => poi.role === 'end')
      }
      return !this.selectedPois.some((poi, i) => i !== index && poi.role === 'end')
    },

    removePoi(index) {
      const poi = this.selectedPois[index]
      const marker = this.markers.find(m => m._poiId === poi.id)
      if (marker) {
        this.updateMarkerIcon(marker, false)
        this.activeMarkers.delete(poi.id)
      }
      this.selectedPois.splice(index, 1)
      if (this.isRouteVisible) {
        this.updateRoute()
      }
    },

    adjustDrawerWidth(delta) {
      const newWidth = this.rightDrawerWidth + delta;
      if (newWidth >= 200 && newWidth <= 600) {
        this.rightDrawerWidth = newWidth;
      }
    },

    // 添加新方法：显示路线
    showRoute() {
      this.isRouteVisible = true
      this.rightDrawerVisible = true  // 显示右侧边栏
      this.updateRoute()
    },

    handleImageLoad(event, poiId, photoIndex) {
      console.log('Image loaded successfully:', event.target.src);
      const img = event.target;
      const photoUrl = img.src;

      // 更新POI列表中的照片状态
      this.pois.forEach(poi => {
        if (poi.id === poiId && poi.amapDetails && poi.amapDetails.photos) {
          const photo = poi.amapDetails.photos[photoIndex];
          if (photo && photo.url === photoUrl) {
            photo.loaded = true;
            photo.error = false;
          }
        }
      });

      // 更新selectedPois中的照片状态
      this.selectedPois.forEach(poi => {
        if (poi.id === poiId && poi.amapDetails && poi.amapDetails.photos) {
          const photo = poi.amapDetails.photos[photoIndex];
          if (photo && photo.url === photoUrl) {
            photo.loaded = true;
            photo.error = false;
          }
        }
      });
    },

    handleImageError(event, poiId, photoIndex) {
      console.error('Image load error:', {
        poiId,
        photoIndex,
        src: event.target.src,
        error: event
      });

      // 更新POI列表中的照片状态
      this.pois.forEach(poi => {
        if (poi.id === poiId && poi.amapDetails && poi.amapDetails.photos) {
          const photo = poi.amapDetails.photos[photoIndex];
          if (photo && photo.url === event.target.src) {
            photo.loaded = false;
            photo.error = true;
            // 尝试使用备用图片
            this.tryFallbackImage(poi, photoIndex);
          }
        }
      });

      // 更新selectedPois中的照片状态
      this.selectedPois.forEach(poi => {
        if (poi.id === poiId && poi.amapDetails && poi.amapDetails.photos) {
          const photo = poi.amapDetails.photos[photoIndex];
          if (photo && photo.url === event.target.src) {
            photo.loaded = false;
            photo.error = true;
            // 尝试使用备用图片
            this.tryFallbackImage(poi, photoIndex);
          }
        }
      });
    },

    retryLoadImage(poiId, photoIndex) {
      // 重置照片状态
      this.pois.forEach(poi => {
        if (poi.id === poiId && poi.amapDetails && poi.amapDetails.photos) {
          const photo = poi.amapDetails.photos[photoIndex];
          if (photo) {
            photo.loaded = false;
            photo.error = false;
            // 添加时间戳来强制重新加载
            const timestamp = new Date().getTime();
            const separator = photo.url.includes('?') ? '&' : '?';
            photo.url = `${photo.url}${separator}t=${timestamp}`;
          }
        }
      });

      // 同样更新selectedPois中的照片状态
      this.selectedPois.forEach(poi => {
        if (poi.id === poiId && poi.amapDetails && poi.amapDetails.photos) {
          const photo = poi.amapDetails.photos[photoIndex];
          if (photo) {
            photo.loaded = false;
            photo.error = false;
            // 添加时间戳来强制重新加载
            const timestamp = new Date().getTime();
            const separator = photo.url.includes('?') ? '&' : '?';
            photo.url = `${photo.url}${separator}t=${timestamp}`;
          }
        }
      });
    },

    // 添加新方法：处理标签选择
    handleTagSelect(tag) {
      const index = this.selectedTags.indexOf(tag);
      if (index === -1) {
        this.selectedTags.push(tag);
      } else {
        this.selectedTags.splice(index, 1);
      }
    },

    // 添加新方法：清除所有标签选择
    clearTags() {
      this.selectedTags = [];
    },

    // 添加新方法：处理描述文本为标签
    getDescriptionTags(description) {
      if (!description) return [];

      // 分割描述文本
      const tags = description.split(/[,，;；]/)
          .map(tag => tag.trim())
          .filter(tag => tag.length > 1)
          // 标准化标签（去除重复词）
          .map(tag => {
            // 处理常见的重复词
            const commonSuffixes = ['景区', '景点', '旅游区', '公园', '风景区', '旅游度假区', '生态旅游区', '国家公园', '自然保护区'];
            let normalizedTag = tag;
            commonSuffixes.forEach(suffix => {
              if (normalizedTag.endsWith(suffix)) {
                normalizedTag = normalizedTag.replace(suffix, '');
              }
            });
            return normalizedTag.trim();
          })
          // 过滤掉空标签
          .filter(tag => tag.length > 1);

      // 去重并排序
      return [...new Set(tags)].sort((a, b) => a.localeCompare(b, 'zh-CN'));
    },

    // 在 methods 中添加新方法
    getTagType(tag) {
      // 景点类型标签
      if (['自然风光', '人文景观', '历史古迹', '文化遗址', '宗教场所'].includes(tag)) {
        return 'primary';
      }
      // 设施服务标签
      if (tag.startsWith('有')) {
        return 'info';
      }
      // 评价标签
      if (['热门景点', '好评景点', '推荐景点'].includes(tag)) {
        return 'success';
      }
      // 价格标签
      if (['高端景点', '免费景点'].includes(tag)) {
        return 'warning';
      }
      // 时间标签
      if (['全天开放', '夜间开放'].includes(tag)) {
        return 'danger';
      }
      // 评分标签
      if (tag.match(/^\d+\.\d+$/)) {
        return 'success';
      }
      // 最佳观赏时间标签
      if (tag.match(/\d{1,2}[:：]\d{2}\s*[-~]\s*\d{1,2}[:：]\d{2}/)) {
        return 'info';
      }
      return 'default';
    },

    // 添加生成静态地图URL的方法
    generateStaticImageUrl(zoom, size, position) {
      const key = process.env.VUE_APP_AMAP_API_KEY;
      const markers = `mid,0xFF0000,A:${position[0]},${position[1]}`;
      return `https://restapi.amap.com/v3/staticmap?location=${position[0]},${position[1]}&zoom=${zoom}&size=${size}&markers=${markers}&key=${key}&scale=2`;
    },

    extractTags(result) {
      const tags = new Set();

      // 景点类型标签
      const scenicTypeTags = [
        '自然风光', '人文景观', '历史古迹', '文化遗址', '宗教场所',
        '休闲娱乐', '科普教育', '生态保护', '地质公园', '森林公园',
        '湿地公园', '海洋公园', '主题公园', '博物馆', '纪念馆',
        '艺术馆', '科技馆', '动物园', '植物园', '水族馆',
        '温泉', '滑雪场', '高尔夫', '游乐园', '度假村',
        '农家乐', '古镇', '古街', '古村落', '古建筑',
        '古寺庙', '古塔', '古桥', '古井', '古树',
        '古墓', '古战场', '古关隘', '古驿站', '古码头',
        '古渡口', '古运河', '古长城', '古堡', '古寨'
      ];

      // 设施服务标签
      const facilityTags = [
        '有停车场', '有餐厅', '有厕所', '有WiFi', '有休息区',
        '有观景台', '有导游', '有讲解', '有购物', '有住宿',
        '有医疗点', '有寄存处', '有充电站', '有饮水处', '有吸烟区',
        '有无障碍设施', '有儿童设施', '有老人设施', '有宠物设施'
      ];

      // 评价标签
      const ratingTags = [
        '热门景点', '好评景点', '推荐景点', '网红景点', '必游景点',
        '人气景点', '特色景点', '经典景点', '新兴景点', '小众景点'
      ];

      // 价格标签
      const priceTags = [
        '高端景点', '中端景点', '经济景点', '免费景点', '优惠景点',
        '特惠景点', '折扣景点', '团购景点', '会员景点', '年卡景点'
      ];

      // 时间标签
      const timeTags = [
        '全天开放', '夜间开放', '季节性开放', '预约开放', '限时开放',
        '节假日开放', '周末开放', '工作日开放', '早场开放', '晚场开放'
      ];

      // 从描述中提取景点类型标签
      if (result.description) {
        scenicTypeTags.forEach(tag => {
          if (result.description.includes(tag)) {
            tags.add(tag);
            this.tagCategories.scenicType.tags.add(tag);
          }
        });
      }

      // 从设施信息中提取设施服务标签
      if (result.facility) {
        facilityTags.forEach(tag => {
          if (result.facility.includes(tag.replace('有', ''))) {
            tags.add(tag);
            this.tagCategories.facility.tags.add(tag);
          }
        });
      }

      // 根据评分添加评价标签
      if (result.rating) {
        const rating = parseFloat(result.rating);
        if (rating >= 4.5) {
          tags.add('热门景点');
          tags.add('好评景点');
          this.tagCategories.rating.tags.add('热门景点');
          this.tagCategories.rating.tags.add('好评景点');
        } else if (rating >= 4.0) {
          tags.add('推荐景点');
          this.tagCategories.rating.tags.add('推荐景点');
        }
        // 添加评分标签
        const scoreTag = rating.toFixed(1);
        tags.add(scoreTag);
        this.tagCategories.score.tags.add(scoreTag);
      }

      // 根据价格添加价格标签
      if (result.price) {
        const price = parseFloat(result.price);
        if (price > 100) {
          tags.add('高端景点');
          this.tagCategories.price.tags.add('高端景点');
        } else if (price > 50) {
          tags.add('中端景点');
          this.tagCategories.price.tags.add('中端景点');
        } else if (price > 0) {
          tags.add('经济景点');
          this.tagCategories.price.tags.add('经济景点');
        } else {
          tags.add('免费景点');
          this.tagCategories.price.tags.add('免费景点');
        }
      }

      // 根据开放时间添加时间标签
      if (result.open_time) {
        if (result.open_time.includes('24小时') || result.open_time.includes('全天')) {
          tags.add('全天开放');
          this.tagCategories.time.tags.add('全天开放');
        }
        if (result.open_time.includes('夜间') || result.open_time.includes('晚上')) {
          tags.add('夜间开放');
          this.tagCategories.time.tags.add('夜间开放');
        }
        // 提取最佳观赏时间
        const timeMatch = result.open_time.match(/(\d{1,2}[:：]\d{2})\s*[-~]\s*(\d{1,2}[:：]\d{2})/);
        if (timeMatch) {
          const bestTimeTag = `${timeMatch[1]} - ${timeMatch[2]}`;
          tags.add(bestTimeTag);
          this.tagCategories.bestTime.tags.add(bestTimeTag);
        }
      }

      return Array.from(tags);
    },

    handleSearch() {
      // Filter POIs based on selected provinces and search query
      let filteredPois = [...this.allPois];

      // Filter by selected provinces
      if (this.selectedProvinces.length > 0) {
        filteredPois = filteredPois.filter(poi =>
            this.selectedProvinces.includes(poi.province)
        );
      }

      // Filter by search query
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase();
        filteredPois = filteredPois.filter(poi =>
            poi.name.toLowerCase().includes(query) ||
            poi.province.toLowerCase().includes(query) ||
            (poi.category && poi.category.toLowerCase().includes(query))
        );
      }

      // Update the pois array with filtered results
      this.pois = filteredPois;

      // Update markers on the map
      this.addPoiMarkers();
    },

    async fetchPoiDetails(poi) {
      try {
        // 首先设置默认详情，确保即使搜索失败也有基本数据
        const defaultDetails = {
          description: `${poi.name}位于${poi.province}，是一个${poi.category}级景区。`,
          photos: this.generateStaticMapPhotos(poi),
          tags: this.extractTags({
            description: poi.name,
            rating: '4.0',
            price: '0',
            open_time: '全天开放',
            facility: '有停车场,有厕所'
          })
        };

        const searchKeywords = [
          `${poi.province} ${poi.name}`,
          poi.name,
          `${poi.name} 景区`,
          `${poi.name} 旅游`,
          `${poi.name} 风景`,
          `${poi.name} 纪念馆`,  // 添加纪念馆相关关键词
          `${poi.name} 遗址`,    // 添加遗址相关关键词
          `${poi.name} 旧址`     // 添加旧址相关关键词
        ];

        let result = null;
        for (const keyword of searchKeywords) {
          try {
            result = await new Promise((resolve, reject) => {
              this.amapPOIService.search(keyword, (status, searchResult) => {
                if (status === 'complete' && searchResult.poiList && searchResult.poiList.pois.length > 0) {
                  // 找到最匹配的结果
                  const bestMatch = searchResult.poiList.pois.find(p =>
                      p.name.includes(poi.name) || poi.name.includes(p.name)
                  ) || searchResult.poiList.pois[0];
                  resolve(bestMatch);
                } else {
                  reject(new Error('No results found'));
                }
              });
            });
            if (result) break;
          } catch (error) {
            console.log(`Search failed for keyword ${keyword}:`, error);
            continue;
          }
        }

        if (!result) {
          console.log(`No results found for POI ${poi.name}, using default details`);
          poi.amapDetails = defaultDetails;
          return defaultDetails;
        }

        // 更新POI信息
        poi.amapDetails = {
          description: result.description || result.type || result.address || defaultDetails.description,
          photos: result.photos ? result.photos.map(photo => ({
            url: photo.url,
            title: photo.title || poi.name,
            loaded: false,
            error: false
          })) : defaultDetails.photos,
          tags: this.extractTags(result)
        };

        // 如果没有照片，使用默认的静态地图照片
        if (!poi.amapDetails.photos || poi.amapDetails.photos.length === 0) {
          poi.amapDetails.photos = defaultDetails.photos;
        }

        return poi.amapDetails;
      } catch (error) {
        console.error(`Failed to fetch details for POI ${poi.name}:`, error);
        // 使用默认详情
        poi.amapDetails = {
          description: `${poi.name}位于${poi.province}，是一个${poi.category}级景区。`,
          photos: this.generateStaticMapPhotos(poi),
          tags: this.extractTags({
            description: poi.name,
            rating: '4.0',
            price: '0',
            open_time: '全天开放',
            facility: '有停车场,有厕所'
          })
        };
        return poi.amapDetails;
      }
    }
  }
}
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 100vh;
  position: relative;
  pointer-events: auto;
}

#map-container {
  width: 100%;
  height: 100%;
  position: relative;
  z-index: 1;
  pointer-events: auto;
}

.poi-controls {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 2001;
  background: white;
  padding: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  display: flex;
  gap: 10px;
  align-items: center;
  pointer-events: auto;
}

.province-select {
  width: 200px;
}

/* 确保地图控件中的选择器样式正确 */
:deep(.poi-controls .el-select) {
  width: 200px;
}

:deep(.poi-controls .el-select .el-input__wrapper) {
  background-color: white;
}

:deep(.poi-controls .el-select .el-input__inner) {
  height: 32px;
}

:deep(.poi-controls .el-select .el-tag) {
  margin: 2px;
}

.el-button {
  margin-left: 10px;
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
  z-index: 1001 !important;
  pointer-events: auto;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
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

/* 修改抽屉样式，确保不影响地图交互 */
:deep(.map-drawer) {
  position: absolute !important;
}

:deep(.el-drawer__wrapper) {
  position: absolute !important;
  pointer-events: none !important;
}

:deep(.el-drawer__container) {
  position: absolute !important;
  pointer-events: none !important;
}

:deep(.el-drawer__header),
:deep(.el-drawer__body) {
  pointer-events: auto !important;
}

:deep(.el-drawer__mask) {
  position: absolute !important;
  width: 300px !important;
  height: 100% !important;
  background-color: transparent !important;
  opacity: 1;
  pointer-events: none !important;
}

/* 右侧抽屉遮罩层样式 */
:deep(.right-drawer .el-drawer__mask) {
  right: 0;
  width: v-bind(rightDrawerWidth + 'px') !important;
}

/* 左侧抽屉遮罩层样式 */
:deep(.left-drawer-wrapper .el-drawer__mask) {
  left: 0;
  width: 300px !important;
}

.poi-details {
  margin-top: 10px;
  font-size: 12px;
}

.poi-description {
  color: #606266;
  margin-bottom: 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.poi-tags {
  margin-top: 10px;
}

.poi-photos {
  margin-top: 10px;
  position: relative;
}

.poi-photos .el-carousel {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.poi-photos .el-carousel__item {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.poi-photos .el-carousel__item img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.poi-no-photos {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  background-color: #f5f7fa;
  border-radius: 8px;
  color: #909399;
  font-size: 14px;
}

.poi-no-photos .el-icon {
  font-size: 24px;
  margin-right: 8px;
}

/* 优化轮播图指示器样式 */
:deep(.el-carousel__indicators) {
  bottom: 10px;
}

:deep(.el-carousel__button) {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background-color: #fff;
  transform: scale(1.2);
}

/* 优化轮播图箭头样式 */
:deep(.el-carousel__arrow) {
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  width: 36px;
  height: 36px;
  transition: all 0.3s ease;
}

:deep(.el-carousel__arrow:hover) {
  background-color: rgba(0, 0, 0, 0.5);
  transform: scale(1.1);
}

:deep(.el-carousel__arrow--left) {
  left: 10px;
}

:deep(.el-carousel__arrow--right) {
  right: 10px;
}

/* 优化轮播图卡片样式 */
:deep(.el-carousel--card) {
  padding: 0 20px;
}

:deep(.el-carousel--card .el-carousel__item) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
}

:deep(.el-carousel--card .el-carousel__item.is-active) {
  transform: scale(1.05);
}

:deep(.el-carousel--card .el-carousel__item img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

:deep(.el-carousel--card .el-carousel__item:hover img) {
  transform: scale(1.05);
}

/* 添加图片加载动画 */
.poi-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.poi-photo.loading {
  opacity: 0;
  filter: blur(10px);
}

.poi-photo.loaded {
  opacity: 1;
  filter: blur(0);
}

.poi-photo.error {
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
  min-height: 200px;
}

.poi-photo.error .el-icon {
  font-size: 24px;
  margin-right: 8px;
}

/* 优化轮播图样式 */
:deep(.el-carousel__item) {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-carousel__item img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

:deep(.el-carousel--card) {
  padding: 0 20px;
}

:deep(.el-carousel--card .el-carousel__item) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
}

:deep(.el-carousel--card .el-carousel__item.is-active) {
  transform: scale(1.05);
}

:deep(.el-carousel__indicators) {
  bottom: 10px;
}

:deep(.el-carousel__button) {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background-color: #fff;
  transform: scale(1.2);
}

:deep(.el-carousel__arrow) {
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  width: 36px;
  height: 36px;
  transition: all 0.3s ease;
}

:deep(.el-carousel__arrow:hover) {
  background-color: rgba(0, 0, 0, 0.5);
  transform: scale(1.1);
}

:deep(.el-carousel__arrow--left) {
  left: 10px;
}

.poi-photo-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.poi-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.poi-photo.loading {
  opacity: 0;
}

.poi-photo.loaded {
  opacity: 1;
}

.poi-photo.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
  padding: 20px;
  text-align: center;
}

.poi-photo.error .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.poi-photo.error .retry-button {
  margin-top: 8px;
}

.loading-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 14px;
}

.loading-icon {
  font-size: 24px;
  margin-bottom: 8px;
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 添加标签筛选相关样式 */
.tags-filter {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  max-height: 300px;
  overflow: hidden;
}

.tags-scrollbar {
  margin-top: 10px;
}

.tags-categories {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding-right: 10px;
}

.tag-category {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.tag-category h5 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
}

.tags-select-input {
  width: 100%;
}

:deep(.el-select-dropdown__item) {
  padding: 0 12px;
}

:deep(.el-select-dropdown__item .el-tag) {
  margin: 4px 0;
  width: 100%;
  text-align: center;
}

/* 添加加载进度条样式 */
.loading-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #409EFF;
  transition: width 0.3s ease;
  z-index: 2000;
}

/* 优化标签显示样式 */
.poi-tags {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.poi-tags .el-tag {
  margin: 0;
}

/* 优化搜索容器样式 */
.search-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 15px;
}

.search-input {
  width: 100%;
}

/* 优化抽屉头部样式 */
.drawer-header {
  padding: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.drawer-header h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #303133;
}

/* 添加描述标签样式 */
.poi-description-tags {
  margin-bottom: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.description-tag {
  margin: 0;
  font-size: 12px;
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
  border-radius: 4px;
  background-color: #f0f2f5;
  color: #606266;
  border: 1px solid #dcdfe6;
}

.description-tag:hover {
  background-color: #e6e8eb;
}

/* 优化标签容器样式 */
.poi-tags {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.poi-tags .el-tag {
  margin: 0;
  font-size: 12px;
  padding: 0 8px;
  height: 24px;
  line-height: 24px;
}

/* 移除原有的描述文本样式 */
.poi-description {
  display: none;
}
</style>