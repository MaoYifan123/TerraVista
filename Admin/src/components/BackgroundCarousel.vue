<template>
  <div class="background-carousel" :class="{ 'hidden': isMapPage }">
    <div class="carousel-overlay"></div>
    <transition-group name="fade" tag="div" class="carousel-container">
      <div 
        v-for="(image, index) in images" 
        :key="index" 
        v-show="currentIndex === index"
        class="carousel-slide"
        :style="{ backgroundImage: `url(${image})` }"
      ></div>
    </transition-group>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'BackgroundCarousel',
  setup() {
    const images = [
      '/凤凰古城.jpg',
      '/华山.jpg',
      '/织金洞.jpg',
      '/黄果树瀑布.jpg'
    ]
    
    const currentIndex = ref(0)
    const intervalId = ref(null)
    const route = useRoute()
    
    const isMapPage = computed(() => {
      return route.path === '/map'
    })
    
    const startCarousel = () => {
      intervalId.value = setInterval(() => {
        currentIndex.value = (currentIndex.value + 1) % images.length
      }, 8000) // Change image every 8 seconds
    }
    
    watch(() => isMapPage.value, (newValue) => {
      if (newValue && intervalId.value) {
        clearInterval(intervalId.value)
        intervalId.value = null
      } else if (!newValue && !intervalId.value) {
        startCarousel()
      }
    })
    
    onMounted(() => {
      if (!isMapPage.value) {
        startCarousel()
      }
    })
    
    onUnmounted(() => {
      if (intervalId.value) {
        clearInterval(intervalId.value)
      }
    })
    
    return {
      images,
      currentIndex,
      isMapPage
    }
  }
}
</script>

<style scoped>
.background-carousel {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  overflow: hidden;
}

.background-carousel.hidden {
  display: none;
  visibility: hidden;
  opacity: 0;
}

.carousel-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.75);
  z-index: 1;
}

.carousel-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.carousel-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 