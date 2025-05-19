<template>
  <div class="home">
    <div class="hero-section">
      <div class="hero-content">
        <h1>欢迎使用 TerraVista</h1>
        <p class="subtitle">探索世界，发现美好</p>
        <div class="cta-buttons">
          <router-link to="/map" class="cta-button primary">探索地图</router-link>
          <router-link to="/AIRecommend" class="cta-button secondary">AI推荐</router-link>
        </div>
      </div>
    </div>
    
    <div class="features-section">
      <h2 class="section-title">我们的特色</h2>
      <div class="feature-cards">
        <div class="feature-card">
          <div class="feature-icon">🗺️</div>
          <h3>交互式地图</h3>
          <p>探索中国各地景点，获取详细信息</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🤖</div>
          <h3>AI景点推荐</h3>
          <p>根据您的偏好智能推荐最适合的旅游目的地</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">📱</div>
          <h3>便捷体验</h3>
          <p>随时随地规划您的旅行，轻松获取景点信息</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src

export default {
  name: 'HomeView',
  components: {
  }
}
</script>

<style scoped>
.home {
  width: 100%;
}

.hero-section {
  background-image: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url('https://source.unsplash.com/1600x900/?china,landscape');
  background-size: cover;
  background-position: center;
  color: white;
  text-align: center;
  padding: 80px 20px;
  border-radius: 12px;
  margin-bottom: 40px;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-content h1 {
  font-size: 3rem;
  margin-bottom: 20px;
  font-weight: 700;
}

.subtitle {
  font-size: 1.5rem;
  margin-bottom: 40px;
  font-weight: 300;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.cta-button {
  display: inline-block;
  padding: 15px 30px;
  border-radius: 30px;
  font-size: 1.1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.cta-button.primary {
  background-color: #42b983;
  color: white;
}

.cta-button.primary:hover {
  background-color: #3aa876;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(66, 185, 131, 0.3);
}

.cta-button.secondary {
  background-color: transparent;
  color: white;
  border: 2px solid white;
}

.cta-button.secondary:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(255, 255, 255, 0.2);
}

.features-section {
  padding: 40px 20px;
  text-align: center;
}

.section-title {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 40px;
  position: relative;
  display: inline-block;
}

.section-title:after {
  content: '';
  position: absolute;
  width: 60px;
  height: 3px;
  background: #42b983;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
}

.feature-cards {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}

.feature-card {
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  width: 300px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 1.3rem;
  color: #2c3e50;
  margin-bottom: 15px;
}

.feature-card p {
  color: #6c757d;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 2.2rem;
  }
  
  .subtitle {
    font-size: 1.2rem;
  }
  
  .feature-cards {
    flex-direction: column;
    align-items: center;
  }
  
  .feature-card {
    width: 100%;
    max-width: 350px;
  }
}
</style>
