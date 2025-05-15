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
          :size="leftDrawerWidth"
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
            <div class="drawer-controls">
              <el-button-group>
                <el-button
                    size="small"
                    @click="adjustLeftDrawerWidth(-50)"
                    :disabled="leftDrawerWidth <= 200"
                >
                  <el-icon><Remove /></el-icon>
                </el-button>
                <el-button
                    size="small"
                    @click="adjustLeftDrawerWidth(50)"
                    :disabled="leftDrawerWidth >= 600"
                >
                  <el-icon><Plus /></el-icon>
                </el-button>
              </el-button-group>
            </div>
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
                <div class="tags-header-controls">
                  <el-button
                      v-if="selectedTags.length > 0"
                      type="text"
                      @click="clearTags"
                  >
                    清除筛选
                  </el-button>
                  <el-button
                      type="text"
                      @click="toggleTagFilter"
                  >
                    <el-icon>
                      <component :is="isTagFilterCollapsed ? 'ArrowDown' : 'ArrowUp'" />
                    </el-icon>
                  </el-button>
                </div>
              </div>
              <el-collapse-transition>
                <div v-show="!isTagFilterCollapsed">
                  <el-scrollbar height="200px" class="tags-scrollbar">
                    <div class="tags-categories">
                      <!-- 景点类型 -->
                      <div class="tag-category">
                        <h5>景点类型</h5>
                        <el-select
                            v-model="selectedTags.scenicType"
                            multiple
                            collapse-tags
                            collapse-tags-tooltip
                            placeholder="选择景点类型"
                            class="tags-select-input"
                            @change="handleSearch"
                        >
                          <el-option
                              v-for="tag in Array.from(tagCategories.scenicType.tags)"
                              :key="tag"
                              :label="tag"
                              :value="tag"
                          >
                            <el-tag :type="tagCategories.scenicType.type" effect="light">{{ tag }}</el-tag>
                          </el-option>
                        </el-select>
                      </div>

                      <!-- 景点等级 -->
                      <div class="tag-category">
                        <h5>景点等级</h5>
                        <el-select
                            v-model="selectedTags.category"
                            multiple
                            collapse-tags
                            collapse-tags-tooltip
                            placeholder="选择景点等级"
                            class="tags-select-input"
                            @change="handleSearch"
                        >
                          <el-option
                              v-for="tag in ['5A', '4A']"
                              :key="tag"
                              :label="tag"
                              :value="tag"
                          >
                            <el-tag type="success" effect="light">{{ tag }}</el-tag>
                          </el-option>
                        </el-select>
                      </div>

                      <!-- 景点照片 -->
                      <div class="tag-category">
                        <h5>景点照片</h5>
                        <el-select
                            v-model="selectedTags.photo"
                            multiple
                            collapse-tags
                            collapse-tags-tooltip
                            placeholder="选择照片状态"
                            class="tags-select-input"
                            @change="handleSearch"
                        >
                          <el-option
                              v-for="tag in ['有照片', '无照片']"
                              :key="tag"
                              :label="tag"
                              :value="tag"
                          >
                            <el-tag type="info" effect="light">{{ tag }}</el-tag>
                          </el-option>
                        </el-select>
                      </div>

                      <!-- 官网信息 -->
                      <div class="tag-category">
                        <h5>相关信息网站</h5>
                        <el-select
                            v-model="selectedTags.website"
                            multiple
                            collapse-tags
                            collapse-tags-tooltip
                            placeholder="选择网站状态"
                            class="tags-select-input"
                            @change="handleSearch"
                        >
                          <el-option
                              v-for="tag in ['有网站', '无网站']"
                              :key="tag"
                              :label="tag"
                              :value="tag"
                          >
                            <el-tag type="warning" effect="light">{{ tag }}</el-tag>
                          </el-option>
                        </el-select>
                      </div>
                    </div>
                  </el-scrollbar>
                </div>
              </el-collapse-transition>
            </div>
          </div>
        </template>
        <div class="poi-list">
          <el-scrollbar :height="getPoiListHeight()">
            <div class="poi-grid">
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
                        <!-- 添加官网信息显示 -->
                        <div v-if="poi.amapDetails.website" class="poi-website">
                          <el-link
                              :href="poi.amapDetails.website"
                              target="_blank"
                              type="primary"
                              :underline="false"
                              class="website-link"
                          >
                            <el-icon><Link /></el-icon>
                            <span>访问网站</span>
                          </el-link>
                        </div>
                        <div v-else class="poi-no-website">
                          <el-tag type="info" effect="plain" size="small">
                            <el-icon><Link /></el-icon>
                            <span>暂无网站</span>
                          </el-tag>
                        </div>
                        <div v-if="poi.amapDetails.photos && poi.amapDetails.photos.length > 0 && !poi.amapDetails.photos.every(photo => photo.error)" class="poi-photos">
                          <el-carousel
                              :interval="4000"
                              type="card"
                              height="200px"
                              indicator-position="outside"
                              arrow="always"
                              :autoplay="true"
                          >
                            <el-carousel-item v-for="(photo, photoIndex) in poi.amapDetails.photos.filter(photo => !photo.error)" :key="photoIndex">
                              <div class="poi-photo-container">
                                <img
                                    :src="photo.url"
                                    :alt="photo.title"
                                    class="poi-photo"
                                    @load="handleImageLoad($event, poi.id, photoIndex)"
                                    @error="handleImageError($event, poi.id, photoIndex)"
                                    :class="{ 'loading': !photo.loaded, 'loaded': photo.loaded }"
                                >
                                <div v-if="!photo.loaded" class="poi-photo loading-placeholder">
                                  <el-icon class="loading-icon"><Loading /></el-icon>
                                  <span>暂无图片...</span>
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
                      <div v-if="poi.amapDetails.photos && poi.amapDetails.photos.length > 0 && !poi.amapDetails.photos.every(photo => photo.error)" class="poi-photos">
                        <el-carousel
                            :interval="4000"
                            type="card"
                            height="200px"
                            indicator-position="outside"
                            arrow="always"
                            :autoplay="true"
                        >
                          <el-carousel-item v-for="(photo, photoIndex) in poi.amapDetails.photos.filter(photo => !photo.error)" :key="photoIndex">
                            <div class="poi-photo-container">
                              <img
                                  :src="photo.url"
                                  :alt="photo.title"
                                  class="poi-photo"
                                  @load="handleImageLoad($event, poi.id, photoIndex)"
                                  @error="handleImageError($event, poi.id, photoIndex)"
                                  :class="{ 'loading': !photo.loaded, 'loaded': photo.loaded }"
                              >
                              <div v-if="!photo.loaded" class="poi-photo loading-placeholder">
                                <el-icon class="loading-icon"><Loading /></el-icon>
                                <span>暂无图片...</span>
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
import { Search, List, Delete, Close, Rank, Plus, Remove, Sort, Picture, Loading, Link } from '@element-plus/icons-vue'
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
    Link,
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
      selectedTags: {
        scenicType: [], // 景点类型
        category: [],   // 景点等级
        photo: [],      // 景点照片
        website: []     // 官网信息
      },
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
      leftDrawerWidth: 300,
      isTagFilterCollapsed: false,
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
        // securityJsCode: process.env.VUE_APP_AMAP_SECURITY_CODE,
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
        console.log('开始获取所有POI数据...');
        const response = await axios.get('/api/poi/all')
        // 保存完整的POI数据
        this.allPois = response.data.sort((a, b) => {
          if (!a.province) return 1;
          if (!b.province) return -1;
          return a.province.localeCompare(b.province, 'zh-CN');
        });

        console.log(`成功获取到 ${this.allPois.length} 个POI数据`);

        this.pois = [...this.allPois];
        this.extractProvinces();

        // 初始化POI搜索服务
        this.amapPOIService = new AMap.PlaceSearch({
          pageSize: 5,
          pageIndex: 1,
          extensions: 'all',
          type: '风景名胜;公园广场;旅游景点',
          city: '全国',
          citylimit: false,
          autoFitView: true
        });

        // 为每个POI设置基本详情并保存到数据库
        console.log('开始处理每个POI的详细信息...');
        for (const poi of this.allPois) {
          try {
            console.log(`处理POI: ${poi.name} (${poi.id})`);
            const details = await this.fetchPoiDetails(poi);
            console.log(`POI ${poi.name} 的详细信息已保存到数据库`);
          } catch (error) {
            console.error(`处理POI ${poi.name} 时出错:`, error);
          }
        }
        console.log('所有POI信息处理完成');

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
          markerContent.style.transition = 'all 0.3s ease'

          const marker = new AMap.Marker({
            position: [poi.longitudeGcj, poi.latitudeGcj],
            title: poi.name,
            content: markerContent,
            offset: new AMap.Pixel(-size/2, -size/2),
            zIndex: this.activeMarkers.has(poi.id) ? 100 : 1
          })

          // 保存到 marker 上
          marker._poiId = poi.id
          marker._size = size
          marker._content = markerContent

          // 如果是已激活的标记，设置激活样式
          if (this.activeMarkers.has(poi.id)) {
            markerContent.style.backgroundImage = `url('/spoj.png')`
            markerContent.style.transform = 'scale(1.2)'
          }

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
          // 构建搜索关键词
          const searchKeyword = poi.name;
          console.log('开始搜索POI:', poi.name);

          const result = await new Promise((resolve, reject) => {
            // 使用周边搜索
            this.amapPOIService.searchNearBy(
                searchKeyword,
                position,
                5000,  // 搜索半径5公里
                (status, result) => {
                  console.log('搜索结果:', status, result);
                  if (status === 'complete' && result.poiList && result.poiList.pois.length > 0) {
                    // 找到最匹配的结果
                    const bestMatch = result.poiList.pois.find(p =>
                        p.name.includes(poi.name) || poi.name.includes(p.name)
                    ) || result.poiList.pois[0];
                    resolve(bestMatch);
                  } else {
                    // 如果周边搜索失败，尝试普通搜索
                    this.amapPOIService.search(searchKeyword, (status, result) => {
                      console.log('普通搜索结果:', status, result);
                      if (status === 'complete' && result.poiList && result.poiList.pois.length > 0) {
                        const bestMatch = result.poiList.pois.find(p =>
                            p.name.includes(poi.name) || poi.name.includes(p.name)
                        ) || result.poiList.pois[0];
                        resolve(bestMatch);
                      } else {
                        reject(new Error('未找到POI详情'));
                      }
                    });
                  }
                }
            );
          });

          console.log('获取到的POI详情:', result);

          // 更新POI信息
          poi.amapDetails = {
            description: result.description || result.type || result.address || '暂无介绍',
            photos: result.photos ? result.photos.map(photo => ({
              url: photo.url,
              title: photo.title || poi.name,
              loaded: false,
              error: false
            })) : [],
            tags: this.extractTags(result)
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
            }],
            tags: []
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

    // 添加计算名称相似度的方法
    calculateNameSimilarity(str1, str2) {
      // 移除常见后缀
      const removeSuffixes = (str) => {
        return str.replace(/景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区/g, '');
      };

      const s1 = removeSuffixes(str1);
      const s2 = removeSuffixes(str2);

      // 如果完全相等
      if (s1 === s2) return 1;

      // 如果一个是另一个的子串
      if (s1.includes(s2) || s2.includes(s1)) return 0.8;

      // 计算最长公共子序列长度
      const lcs = (s1, s2) => {
        const m = s1.length;
        const n = s2.length;
        const dp = Array(m + 1).fill().map(() => Array(n + 1).fill(0));

        for (let i = 1; i <= m; i++) {
          for (let j = 1; j <= n; j++) {
            if (s1[i - 1] === s2[j - 1]) {
              dp[i][j] = dp[i - 1][j - 1] + 1;
            } else {
              dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
          }
        }

        return dp[m][n];
      };

      const commonLength = lcs(s1, s2);
      const maxLength = Math.max(s1.length, s2.length);

      return commonLength / maxLength;
    },

    // 修改 handleMarkerClick 方法，使用相同的搜索逻辑
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

          // Try multiple search strategies with fuzzy search
          const searchStrategies = [
            // Strategy 1: Full name with province
            `${poi.province} ${poi.name}`,
            // Strategy 2: Just the name
            poi.name,
            // Strategy 3: Remove common suffixes
            poi.name.replace(/景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区/g, ''),
            // Strategy 4: Remove province and common suffixes
            poi.name.replace(/景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区/g, '').replace(new RegExp(poi.province, 'g'), ''),
            // Strategy 5: Split name into parts and search each part
            ...poi.name.split(/[景区|旅游区|公园|风景区|旅游度假区|生态旅游区|国家公园|自然保护区]/).filter(part => part.length > 1)
          ];

          let result = null;
          for (const keyword of searchStrategies) {
            console.log('Trying search keyword:', keyword);

            result = await new Promise((resolve, reject) => {
              const placeSearch = new AMap.PlaceSearch({
                pageSize: 5,
                pageIndex: 1,
                extensions: 'all',
                type: '风景名胜;公园广场;旅游景点;文化场馆;休闲娱乐;自然风光;历史古迹;宗教场所',
                city: poi.province,
                citylimit: true,
                radius: 30000,
                sortrule: 'distance',
                autoFitView: true
              });

              placeSearch.search(keyword, async (status, searchResult) => {
                console.log('Search result for keyword:', keyword, {
                  status,
                  resultCount: searchResult?.poiList?.pois?.length || 0,
                  firstResult: searchResult?.poiList?.pois?.[0]
                });

                if (status === 'complete' && searchResult?.poiList?.pois?.length > 0) {
                  // Find the best match among results
                  const bestMatch = searchResult.poiList.pois.find(p => {
                    const nameMatch = p.name.includes(keyword) || keyword.includes(p.name);
                    const typeMatch = p.type && p.type.includes('风景名胜') || p.type.includes('公园') || p.type.includes('旅游');
                    const distanceMatch = p.distance < 30000; // Within 30km
                    return nameMatch && (typeMatch || distanceMatch);
                  });

                  if (bestMatch) {
                    // 获取详细信息
                    try {
                      const detailResult = await new Promise((resolveDetail, rejectDetail) => {
                        placeSearch.getDetails(bestMatch.id, (status, detail) => {
                          if (status === 'complete' && detail) {
                            resolveDetail(detail);
                          } else {
                            rejectDetail(new Error('Failed to get POI details'));
                          }
                        });
                      });

                      // 合并基本信息和详细信息
                      const enrichedResult = {
                        ...bestMatch,
                        ...detailResult,
                        facility: detailResult.facility || bestMatch.facility || '',
                        open_time: detailResult.open_time || bestMatch.open_time || '',
                        best_time: detailResult.best_time || bestMatch.best_time || '',
                        rating: detailResult.rating || bestMatch.rating || '',
                        price: detailResult.price || bestMatch.price || '',
                        description: detailResult.description || bestMatch.description || bestMatch.type || bestMatch.address || '暂无介绍'
                      };

                      resolve(enrichedResult);
                    } catch (error) {
                      console.error('Failed to get POI details:', error);
                      resolve(bestMatch);
                    }
                  } else {
                    resolve(searchResult.poiList.pois[0]);
                  }
                } else {
                  reject(new Error('No matching POI found'));
                }
              });
            }).catch(error => {
              console.log('Search failed for keyword:', keyword, error);
              return null;
            });

            if (result) break;
          }

          if (!result) {
            throw new Error('All search strategies failed');
          }

          console.log('Found POI details:', result);

          // 更新POI信息，确保包含所有类型的标签
          poi.amapDetails = {
            description: result.description || result.type || result.address || '暂无介绍',
            photos: result.photos ? result.photos.map(photo => ({
              url: photo.url,
              title: photo.title || poi.name,
              loaded: false,
              error: false
            })) : [],
            tags: this.extractTags({
              description: result.description || '',
              facility: result.facility || '',
              open_time: result.open_time || '',
              best_time: result.best_time || '',
              rating: result.rating || '',
              price: result.price || '',
              type: result.type || '',
              address: result.address || '',
              business_hours: result.business_hours || '',
              environment: result.environment || '',
              atmosphere: result.atmosphere || '',
              service: result.service || '',
              traffic: result.traffic || '',
              parking: result.parking || '',
              special: result.special || '',
              activity: result.activity || '',
              event: result.event || '',
              promotion: result.promotion || '',
              discount: result.discount || '',
              season: result.season || '',
              weather: result.weather || '',
              recommendation: result.recommendation || '',
              review: result.review || '',
              comment: result.comment || ''
            })
          };

          // 如果没有照片，尝试获取更多照片
          if (!poi.amapDetails.photos || poi.amapDetails.photos.length === 0) {
            try {
              // 构建搜索关键词
              const searchKeywords = [
                `${poi.province} ${poi.name}`,
                poi.name,
                `${poi.name} 景区`,
                `${poi.name} 旅游`,
                `${poi.name} 风景`
              ];

              // 尝试不同的搜索关键词
              for (const keyword of searchKeywords) {
                const searchResult = await new Promise((resolve, reject) => {
                  const placeSearch = new AMap.PlaceSearch({
                    pageSize: 5,
                    pageIndex: 1,
                    extensions: 'all',
                    type: '风景名胜;公园广场;旅游景点',
                    city: poi.province,
                    citylimit: true,
                    radius: 30000,
                    sortrule: 'distance'
                  });

                  placeSearch.search(keyword, (status, result) => {
                    if (status === 'complete' && result.poiList && result.poiList.pois.length > 0) {
                      resolve(result.poiList.pois[0]);
                    } else {
                      reject(new Error('No results found'));
                    }
                  });
                });

                if (searchResult && searchResult.photos && searchResult.photos.length > 0) {
                  // 找到照片，更新POI信息
                  poi.amapDetails.photos = searchResult.photos.map(photo => ({
                    url: photo.url,
                    title: photo.title || poi.name,
                    loaded: false,
                    error: false
                  }));
                  break;
                }
              }
            } catch (error) {
              console.error('Failed to fetch additional photos:', error);
            }
          }

          // 如果仍然没有照片，生成静态地图图片
          if (!poi.amapDetails.photos || poi.amapDetails.photos.length === 0) {
            poi.amapDetails.photos = [{
              url: this.generateDefaultStaticMapUrl(poi),
              title: poi.name,
              loaded: false,
              error: false
            }];
          }

          console.log('Updated POI details:', poi.amapDetails);
        } catch (error) {
          console.error('Failed to fetch POI details:', error);
          poi.amapDetails = {
            description: '暂无介绍',
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
        if (this.isRouteVisible) {
          this.updateRoute()
        }
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
      if (Object.values(this.selectedTags).some(tags => tags.length > 0)) {
        filteredPois = filteredPois.filter(poi => {
          // 检查景点类型标签
          const hasScenicType = this.selectedTags.scenicType.length === 0 ||
              this.selectedTags.scenicType.some(tag => poi.amapDetails?.tags?.includes(tag));

          // 检查景点等级标签
          const hasCategory = this.selectedTags.category.length === 0 ||
              this.selectedTags.category.includes(poi.category);

          // 检查照片标签
          const hasPhoto = this.selectedTags.photo.length === 0 ||
              this.selectedTags.photo.some(tag => {
                if (tag === '有照片') {
                  return poi.amapDetails?.photos?.length > 0;
                } else if (tag === '无照片') {
                  return !poi.amapDetails?.photos?.length;
                }
                return false;
              });

          // 检查官网标签
          const hasWebsite = this.selectedTags.website.length === 0 ||
              this.selectedTags.website.some(tag => {
                if (tag === '有网站') {
                  return poi.amapDetails?.website;
                } else if (tag === '无网站') {
                  return !poi.amapDetails?.website;
                }
                return false;
              });

          // 返回是否满足所有选中的标签条件
          return hasScenicType && hasCategory && hasPhoto && hasWebsite;
        });
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

    // 修改处理标签选择的方法
    handleTagSelect(tag) {
      // 获取所有选中的标签
      const allSelectedTags = [
        ...this.selectedTags.scenicType,
        ...this.selectedTags.category,
        ...this.selectedTags.photo,
        ...this.selectedTags.website
      ];

      // 获取带有选中标签的POI
      const taggedPois = this.pois.filter(poi =>
          poi.amapDetails &&
          poi.amapDetails.tags &&
          allSelectedTags.some(selectedTag => poi.amapDetails.tags.includes(selectedTag))
      );

      // 如果找到了带有标签的POI，自动设置为路线点
      if (taggedPois.length > 0) {
        // 清空当前选中的POI
        this.selectedPois = [];

        // 将找到的POI添加到路线中
        taggedPois.forEach((poi, index) => {
          // 第一个POI设为起点
          if (index === 0) {
            this.selectedPois.push({ ...poi, role: 'start' });
          }
          // 最后一个POI设为终点
          else if (index === taggedPois.length - 1) {
            this.selectedPois.push({ ...poi, role: 'end' });
          }
          // 中间的POI设为途经点
          else {
            this.selectedPois.push({ ...poi, role: 'waypoint' });
          }
        });

        // 自动显示路线
        this.isRouteVisible = true;
        this.rightDrawerVisible = true;
        this.updateRoute();

        // 调整地图视图以显示所有选中的POI
        if (this.selectedPois.length > 0) {
          const bounds = new AMap.Bounds();
          this.selectedPois.forEach(poi => {
            bounds.extend([poi.longitudeGcj, poi.latitudeGcj]);
          });
          this.map.setBounds(bounds, {
            padding: [50, 50, 50, 50]
          });
        }
      }
    },

    // 修改清除标签的方法
    clearTags() {
      this.selectedTags = {
        scenicType: [],
        category: [],
        photo: [],
        website: []
      };
      // 清除路线
      this.selectedPois = [];
      this.isRouteVisible = false;
      if (this.routeLine) {
        this.map.remove(this.routeLine);
        this.routeLine = null;
      }
      // 清除路线标记
      this.routeMarkers.forEach(marker => {
        this.map.remove(marker);
      });
      this.routeMarkers = [];
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

      // 1. 景点类型标签
      const scenicTypeTags = {
        natural: ['自然风光', '山水景观', '湖泊', '河流', '瀑布', '峡谷', '森林', '草原', '湿地', '海洋', '沙滩', '岛屿'],
        cultural: ['人文景观', '历史古迹', '文化遗址', '宗教场所', '古建筑', '古寺庙', '古塔', '古桥', '古井', '古树'],
        leisure: ['休闲娱乐', '主题公园', '游乐园', '度假村', '温泉', '滑雪场', '高尔夫', '农家乐'],
        education: ['科普教育', '博物馆', '纪念馆', '艺术馆', '科技馆', '动物园', '植物园', '水族馆'],
        protection: ['生态保护', '地质公园', '森林公园', '湿地公园', '海洋公园', '自然保护区'],
        ancient: ['古镇', '古街', '古村落', '古墓', '古战场', '古关隘', '古驿站', '古码头', '古渡口', '古运河', '古长城', '古堡', '古寨'],
        modern: ['现代建筑', '城市公园', '广场', '商业街', '购物中心', '体育场馆', '会展中心', '艺术中心'],
        scenic: ['风景名胜', '旅游景点', '景区', '风景区', '旅游区', '旅游度假区', '生态旅游区', '国家公园', '自然保护区']
      };

      // 2. 设施服务标签
      const facilityTags = {
        basic: ['有停车场', '有厕所', '有休息区', '有饮水处', '有吸烟区'],
        service: ['有餐厅', '有购物', '有住宿', '有导游', '有讲解', '有寄存处'],
        technology: ['有WiFi', '有充电站', '有电子导览', '有智能讲解'],
        medical: ['有医疗点', '有急救站', '有医务室'],
        accessibility: ['有无障碍设施', '有儿童设施', '有老人设施', '有宠物设施'],
        security: ['有安保', '有监控', '有消防设施', '有紧急出口'],
        other: ['有观景台', '有摄影点', '有纪念品店', '有游客中心']
      };

      // 3. 评价标签
      const ratingTags = {
        popularity: ['热门景点', '人气景点', '网红景点', '必游景点'],
        quality: ['好评景点', '推荐景点', '特色景点', '经典景点'],
        trend: ['新兴景点', '小众景点', '潜力景点', '新晋景点'],
        award: ['金牌景点', '星级景点', '示范景点', '标杆景点']
      };

      // 4. 价格标签
      const priceTags = {
        level: ['高端景点', '中端景点', '经济景点', '免费景点'],
        discount: ['优惠景点', '特惠景点', '折扣景点', '团购景点'],
        special: ['会员景点', '年卡景点', '联票景点', '套票景点'],
        season: ['旺季景点', '淡季景点', '节假日景点', '平日景点']
      };

      // 5. 时间标签
      const timeTags = {
        openTime: ['全天开放', '夜间开放', '季节性开放', '预约开放', '限时开放'],
        period: ['节假日开放', '周末开放', '工作日开放', '早场开放', '晚场开放'],
        season: ['春季景点', '夏季景点', '秋季景点', '冬季景点'],
        special: ['节日景点', '活动景点', '展览景点', '演出景点']
      };

      // 6. 评分标签
      const scoreTags = {
        rating: ['5.0分', '4.5分', '4.0分', '3.5分', '3.0分'],
        level: ['特级景点', '一级景点', '二级景点', '三级景点'],
        award: ['金牌景点', '银牌景点', '铜牌景点', '优秀景点'],
        special: ['必游景点', '推荐景点', '特色景点', '经典景点']
      };

      // 7. 最佳观赏时间标签
      const bestTimeTags = {
        season: ['春季最佳', '夏季最佳', '秋季最佳', '冬季最佳'],
        time: ['早晨最佳', '中午最佳', '下午最佳', '傍晚最佳', '夜间最佳'],
        weather: ['晴天最佳', '雨天最佳', '雪天最佳', '阴天最佳'],
        special: ['日出最佳', '日落最佳', '花季最佳', '红叶最佳']
      };

      // 从描述中提取景点类型标签
      if (result.description) {
        Object.values(scenicTypeTags).flat().forEach(tag => {
          if (result.description.includes(tag)) {
            tags.add(tag);
            this.tagCategories.scenicType.tags.add(tag);
          }
        });
      }

      // 从类型中提取景点类型标签
      if (result.type) {
        Object.values(scenicTypeTags).flat().forEach(tag => {
          if (result.type.includes(tag)) {
            tags.add(tag);
            this.tagCategories.scenicType.tags.add(tag);
          }
        });
      }

      // 从设施信息中提取设施服务标签
      if (result.facility) {
        Object.values(facilityTags).flat().forEach(tag => {
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
          Object.values(ratingTags.popularity).forEach(tag => {
            tags.add(tag);
            this.tagCategories.rating.tags.add(tag);
          });
        } else if (rating >= 4.0) {
          Object.values(ratingTags.quality).forEach(tag => {
            tags.add(tag);
            this.tagCategories.rating.tags.add(tag);
          });
        }
        // 添加评分标签
        const scoreTag = rating.toFixed(1) + '分';
        tags.add(scoreTag);
        this.tagCategories.score.tags.add(scoreTag);
      }

      // 根据价格添加价格标签
      if (result.price) {
        const price = parseFloat(result.price);
        if (price > 100) {
          Object.values(priceTags.level).slice(0, 1).forEach(tag => {
            tags.add(tag);
            this.tagCategories.price.tags.add(tag);
          });
        } else if (price > 50) {
          Object.values(priceTags.level).slice(1, 2).forEach(tag => {
            tags.add(tag);
            this.tagCategories.price.tags.add(tag);
          });
        } else if (price > 0) {
          Object.values(priceTags.level).slice(2, 3).forEach(tag => {
            tags.add(tag);
            this.tagCategories.price.tags.add(tag);
          });
        } else {
          Object.values(priceTags.level).slice(3, 4).forEach(tag => {
            tags.add(tag);
            this.tagCategories.price.tags.add(tag);
          });
        }
      }

      // 根据开放时间添加时间标签
      if (result.open_time) {
        if (result.open_time.includes('24小时') || result.open_time.includes('全天')) {
          Object.values(timeTags.openTime).slice(0, 1).forEach(tag => {
            tags.add(tag);
            this.tagCategories.time.tags.add(tag);
          });
        }
        if (result.open_time.includes('夜间') || result.open_time.includes('晚上')) {
          Object.values(timeTags.openTime).slice(1, 2).forEach(tag => {
            tags.add(tag);
            this.tagCategories.time.tags.add(tag);
          });
        }
        // 提取最佳观赏时间
        const timeMatch = result.open_time.match(/(\d{1,2}[:：]\d{2})\s*[-~]\s*(\d{1,2}[:：]\d{2})/);
        if (timeMatch) {
          const bestTimeTag = `${timeMatch[1]} - ${timeMatch[2]}`;
          tags.add(bestTimeTag);
          this.tagCategories.bestTime.tags.add(bestTimeTag);
        }
      }

      // 根据季节和天气添加最佳观赏时间标签
      if (result.best_time) {
        Object.values(bestTimeTags.season).forEach(tag => {
          if (result.best_time.includes(tag)) {
            tags.add(tag);
            this.tagCategories.bestTime.tags.add(tag);
          }
        });
        Object.values(bestTimeTags.weather).forEach(tag => {
          if (result.best_time.includes(tag)) {
            tags.add(tag);
            this.tagCategories.bestTime.tags.add(tag);
          }
        });
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
        // 首先尝试从数据库获取POI信息
        console.log('尝试从数据库获取POI信息:', poi.name);
        const response = await axios.get(`/api/poiinfo/info/${poi.id}`);
        if (response.data) {
          console.log('从数据库获取到POI信息:', poi.name);
          poi.amapDetails = {
            description: response.data.description || `${poi.name}位于${poi.province}，是一个${poi.category}级景区。`,
            photos: response.data.photos ? response.data.photos.map(photo => ({
              url: photo,
              title: poi.name,
              loaded: false,
              error: false
            })) : [],
            tags: response.data.tags || [],
            website: response.data.website || null,
            rating: response.data.rating,
            price: response.data.price,
            openTime: response.data.openTime,
            facility: response.data.facility,
            address: response.data.address,
            type: response.data.type
          };
          return poi.amapDetails;
        }
      } catch (error) {
        console.log('数据库中没有POI信息，将从高德地图API获取:', poi.name);
      }

      try {
        // 构建搜索关键词
        const searchKeywords = [
          `${poi.province} ${poi.name}`,
          poi.name,
          `${poi.name} 景区`,
          `${poi.name} 旅游`,
          `${poi.name} 风景`,
          `${poi.name} 纪念馆`,
          `${poi.name} 遗址`,
          `${poi.name} 旧址`
        ];

        let result = null;
        for (const keyword of searchKeywords) {
          try {
            result = await new Promise((resolve, reject) => {
              this.amapPOIService.search(keyword, (status, searchResult) => {
                if (status === 'complete' && searchResult.poiList && searchResult.poiList.pois.length > 0) {
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
            console.log(`搜索关键词 ${keyword} 失败:`, error);
            continue;
          }
        }

        if (!result) {
          throw new Error('所有搜索策略都失败了');
        }

        console.log('获取到的POI详情:', result);

        // 更新POI信息
        poi.amapDetails = {
          description: result.description || result.type || result.address || `${poi.name}位于${poi.province}，是一个${poi.category}级景区。`,
          photos: result.photos ? result.photos.map(photo => ({
            url: photo.url,
            title: photo.title || poi.name,
            loaded: false,
            error: false
          })) : [],
          tags: this.extractTags(result),
          website: result.website || null,
          rating: result.rating ? parseFloat(result.rating) : null,
          price: result.price ? parseFloat(result.price) : null,
          openTime: result.open_time || result.business_hours || null,
          facility: result.facility || null,
          address: result.address || null,
          type: result.type || null
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

        // 保存到数据库
        try {
          console.log('正在保存POI信息到数据库:', poi.name);
          const saveResponse = await axios.post('/api/poiinfo/put', {
            poiId: poi.id,
            name: poi.name,
            description: poi.amapDetails.description,
            photos: poi.amapDetails.photos.map(photo => photo.url),
            tags: poi.amapDetails.tags,
            website: poi.amapDetails.website,
            rating: poi.amapDetails.rating,
            price: poi.amapDetails.price,
            openTime: poi.amapDetails.openTime,
            facility: poi.amapDetails.facility,
            address: poi.amapDetails.address,
            type: poi.amapDetails.type
          });
          console.log('POI信息保存成功:', poi.name);
        } catch (error) {
          console.error('保存POI信息到数据库失败:', error);
        }

        console.log('更新后的POI详情:', poi.amapDetails);
        return poi.amapDetails;
      } catch (error) {
        console.error('获取POI详情失败:', error);
        poi.amapDetails = {
          description: `${poi.name}位于${poi.province}，是一个${poi.category}级景区。`,
          photos: [{
            url: this.generateDefaultStaticMapUrl(poi),
            title: poi.name,
            loaded: false,
            error: false
          }],
          tags: [],
          website: null,
          rating: null,
          price: null,
          openTime: null,
          facility: null,
          address: null,
          type: null
        };
        return poi.amapDetails;
      }
    },

    // 修改更新标记图标的方法
    updateMarkerIcon(marker, isActive) {
      console.log('Updating marker icon:', { isActive, markerId: marker._poiId })
      const size = marker._size || 40
      const imagePath = isActive ? '/spoj.png' : '/pos.png'
      console.log('Using image path:', imagePath)

      // 更新标记内容
      if (marker._content) {
        marker._content.style.backgroundImage = `url('${imagePath}')`
        marker._content.style.transition = 'all 0.3s ease'
        marker.setzIndex(isActive ? 100 : 1)

        // 添加动画效果
        if (isActive) {
          marker._content.style.transform = 'scale(1.2)'
        } else {
          marker._content.style.transform = 'scale(1)'
        }
      }
    },

    // 修改 updateRoute 方法使用 GCJ-02 坐标
    updateRoute() {
      // 修改条件：只要有至少一个选中的POI就可以规划路线
      if (this.selectedPois.length < 1) return

      // 清除之前的路线
      if (this.routeLine) {
        this.map.remove(this.routeLine)
        this.routeLine = null
      }

      // 清除所有路线相关的标记
      this.clearRouteMarkers()

      // 准备路线点
      const waypoints = []

      // 添加起点（优先使用手动选择的起点，如果没有则使用当前位置）
      const startPoi = this.selectedPois.find(poi => poi.role === 'start')
      if (startPoi) {
        waypoints.push([startPoi.longitudeGcj, startPoi.latitudeGcj])
      } else if (this.currentLocation) {
        // 如果没有手动选择的起点，使用当前位置作为起点
        waypoints.push(this.currentLocation.position)
      }

      // 添加途经点
      this.selectedPois
          .filter(poi => poi.role === 'waypoint')
          .forEach(poi => {
            waypoints.push([poi.longitudeGcj, poi.latitudeGcj])
          })

      // 添加终点（优先使用手动选择的终点，如果没有则使用当前位置）
      const endPoi = this.selectedPois.find(poi => poi.role === 'end')
      if (endPoi) {
        waypoints.push([endPoi.longitudeGcj, endPoi.latitudeGcj])
      } else if (this.currentLocationRole === 'end' && this.currentLocation) {
        waypoints.push(this.currentLocation.position)
      }

      if (waypoints.length < 2) return

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

                // 计算总距离和时间
                const totalDistance = route.distance
                const totalDuration = route.time
                const distanceText = (totalDistance / 1000).toFixed(1) + '公里'
                const durationText = Math.ceil(totalDuration / 60) + '分钟'

                // 创建路线对象
                this.routeLine = new AMap.Polyline({
                  path: path,
                  strokeColor: '#3366FF',
                  strokeWeight: 6,
                  strokeOpacity: 0.8,
                  showDir: true,
                  lineJoin: 'round',
                  lineCap: 'round'
                })

                // 将路线添加到地图
                this.map.add(this.routeLine)

                // 添加起点标记
                const startMarker = new AMap.Marker({
                  position: waypoints[0],
                  icon: new AMap.Icon({
                    size: new AMap.Size(32, 32),
                    image: 'https://webapi.amap.com/theme/v1.3/markers/n/start.png',
                    imageSize: new AMap.Size(32, 32)
                  }),
                  offset: new AMap.Pixel(-16, -16),
                  zIndex: 100
                })
                startMarker._isStartMarker = true
                this.map.add(startMarker)
                this.routeMarkers.push(startMarker)

                // 添加终点标记
                const endMarker = new AMap.Marker({
                  position: waypoints[waypoints.length - 1],
                  icon: new AMap.Icon({
                    size: new AMap.Size(32, 32),
                    image: 'https://webapi.amap.com/theme/v1.3/markers/n/end.png',
                    imageSize: new AMap.Size(32, 32)
                  }),
                  offset: new AMap.Pixel(-16, -16),
                  zIndex: 100
                })
                endMarker._isEndMarker = true
                this.map.add(endMarker)
                this.routeMarkers.push(endMarker)

                // 显示路线信息
                const infoWindow = new AMap.InfoWindow({
                  content: `
                  <div style="padding:10px;">
                    <h3>路线信息</h3>
                    <p>总距离：${distanceText}</p>
                    <p>预计时间：${durationText}</p>
                  </div>
                `,
                  position: waypoints[0],
                  offset: new AMap.Pixel(0, -30),
                  autoMove: false
                })

                infoWindow.open(this.map)
              }
            } else {
              console.error('获取驾车数据失败：', result)
              console.error('错误详情：', {
                status: status,
                errorCode: result?.errorCode,
                errorInfo: result?.errorInfo,
                errorDetail: result?.errorDetail
              });
            }
          }
      )
    },

    // 新增：清除路线相关标记的方法
    clearRouteMarkers() {
      this.routeMarkers.forEach(marker => {
        if (marker && marker.getMap()) {
          this.map.remove(marker)
        }
      })
      this.routeMarkers = []
    },

    // 修改清除路线的方法
    clearRoute() {
      // 清除路线
      if (this.routeLine) {
        this.map.remove(this.routeLine)
        this.routeLine = null
      }

      // 清除所有路线相关的标记
      this.clearRouteMarkers()

      // 清除所有选择状态
      this.selectedPois = []

      // 清除所有信息窗口
      this.clearAllInfoWindows()

      // 重置所有标记为未激活状态
      this.markers.forEach(marker => {
        this.updateMarkerIcon(marker, false)
      })

      // 重置当前位置角色
      this.currentLocationRole = 'start'

      // 重置路线显示状态
      this.isRouteVisible = false
    },

    adjustLeftDrawerWidth(delta) {
      const newWidth = this.leftDrawerWidth + delta;
      if (newWidth >= 200 && newWidth <= 600) {
        this.leftDrawerWidth = newWidth;
      }
    },

    toggleTagFilter() {
      this.isTagFilterCollapsed = !this.isTagFilterCollapsed;
    },

    getPoiListHeight() {
      // 计算POI列表的高度
      const baseHeight = 'calc(100vh - 500px)';
      if (this.isTagFilterCollapsed) {
        return 'calc(100vh - 300px)'; // 当标签筛选折叠时，增加列表高度
      }
      return baseHeight;
    },

    // 修改保存POI信息到数据库的部分
    async savePoiToDatabase(poi, details) {
      try {
        console.log(`开始保存POI ${poi.name} 的信息到数据库...`);
        const response = await axios.post('/api/poiinfo/info', {
          poiId: poi.id,
          name: poi.name,
          description: details.description,
          tags: details.tags,
          photos: details.photos.map(photo => photo.url),
          website: details.website,
          rating: details.rating,
          price: details.price,
          openTime: details.openTime,
          facility: details.facility,
          address: details.address,
          type: details.type
        });

        if (response.data) {
          console.log(`POI ${poi.name} 的信息已成功保存到数据库`);
          return true;
        } else {
          console.error(`POI ${poi.name} 的信息保存失败: 服务器返回空数据`);
          return false;
        }
      } catch (error) {
        console.error(`POI ${poi.name} 的信息保存失败:`, error);
        return false;
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
  width: v-bind(leftDrawerWidth + 'px') !important;
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

/* 修改左侧抽屉遮罩层样式 */
:deep(.left-drawer-wrapper .el-drawer__mask) {
  left: 0;
  width: v-bind(leftDrawerWidth + 'px') !important;
}

/* 优化标签筛选区域样式 */
.tags-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.tags-header h4 {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.tags-header-controls {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 添加POI网格布局样式 */
.poi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 15px;
  padding: 10px;
}

.poi-item {
  width: 100%;
  margin: 0;
}

/* 响应式布局调整 */
@media screen and (min-width: 768px) {
  .poi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (min-width: 1024px) {
  .poi-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 优化抽屉头部样式 */
.drawer-header {
  padding: 15px;
  border-bottom: 1px solid #e4e7ed;
  background-color: #fff;
}

.drawer-header h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #303133;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.drawer-controls {
  margin-bottom: 10px;
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

/* 优化标签筛选区域样式 */
.tags-filter {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  transition: all 0.3s ease;
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

/* 优化POI卡片样式 */
.poi-item .el-card {
  height: 100%;
  transition: all 0.3s ease;
}

.poi-item .el-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.poi-item-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.poi-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 4px;
}

.poi-info {
  flex: 1;
}

.poi-info h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  line-height: 1.4;
}

.poi-info p {
  margin: 4px 0;
  font-size: 14px;
  color: #666;
}

/* 优化滚动条样式 */
:deep(.el-scrollbar__bar) {
  z-index: 2000;
}

:deep(.el-scrollbar__bar.is-horizontal) {
  display: none;
}

:deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

/* 添加官网信息相关样式 */
.poi-website {
  margin-top: 10px;
  margin-bottom: 10px;
}

.website-link {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #409EFF;
  text-decoration: none;
  transition: all 0.3s ease;
}

.website-link:hover {
  color: #66b1ff;
}

.website-link .el-icon {
  font-size: 16px;
}

.poi-no-website {
  margin-top: 10px;
  margin-bottom: 10px;
}

.poi-no-website .el-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.poi-no-website .el-icon {
  font-size: 14px;
}
</style>