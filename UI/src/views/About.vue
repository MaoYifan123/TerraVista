<template>
  <div class="about">
    <div class="about-header">
      <h1>关于 TerraVista</h1>
      <div class="divider"></div>
    </div>

    <div class="about-content">
      <div class="about-section">
        <h2>我们的使命</h2>
        <p>TerraVista 致力于通过先进的技术和用户友好的界面，帮助用户发现和探索中国丰富多彩的旅游资源。我们的平台整合了地理信息系统和人工智能，为旅行者提供个性化的旅游建议和便捷的信息获取渠道。</p>
      </div>

      <div class="about-section">
        <h2>我们的团队</h2>
        <p>我们是来自武汉大学的学生团队，对计算机科学、地理信息系统和旅游管理等知识有浓厚的兴趣。团队成员对旅游和技术充满热情，致力于创造更好的旅行体验。</p>

        <div class="team-members">
          <div class="team-member">
            <div class="member-avatar">👨‍💻</div>
            <h3>团队负责人</h3>
            <p>郭瀚遥</p>
          </div>
        </div>
      </div>

      <div class="about-section">
        <h2>联系我们</h2>
        <p>如果您有任何问题、建议或合作意向，请随时通过以下方式联系我们：</p>
        <div class="contact-info">
          <div class="contact-item">
            <strong>📧 邮箱：</strong>
            <a href="mailto:guohanyao@whu.edu.cn">guohanyao@whu.edu.cn</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.about {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.about-header {
  text-align: center;
  margin-bottom: 50px;
}

.about-header h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 20px;
}

.divider {
  height: 3px;
  width: 80px;
  background-color: #42b983;
  margin: 0 auto;
}

.about-content {
  background-color: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.about-section {
  margin-bottom: 40px;
}

.about-section:last-child {
  margin-bottom: 0;
}

.about-section h2 {
  color: #2c3e50;
  font-size: 1.8rem;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 10px;
}

.about-section h2:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 50px;
  height: 3px;
  background-color: #42b983;
}

.about-section p {
  color: #4a4a4a;
  line-height: 1.7;
  font-size: 1.1rem;
}

.team-members {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.team-member {
  text-align: center;
  padding: 20px;
  border-radius: 10px;
  background-color: #f8f9fa;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.team-member:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.member-avatar {
  font-size: 3.5rem;
  margin-bottom: 15px;
}

.team-member h3 {
  font-size: 1.3rem;
  color: #2c3e50;
  margin-bottom: 5px;
}

.team-member p {
  font-size: 1rem;
  color: #6c757d;
}

.contact-info {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-top: 15px;
}

.contact-item {
  margin-bottom: 10px;
}

.contact-item:last-child {
  margin-bottom: 0;
}

.contact-item a {
  color: #42b983;
  text-decoration: none;
  transition: color 0.3s ease;
}

.contact-item a:hover {
  color: #3aa876;
  text-decoration: underline;
}

@media (max-width: 768px) {
  .about-content {
    padding: 25px;
  }

  .about-header h1 {
    font-size: 2rem;
  }

  .about-section h2 {
    font-size: 1.5rem;
  }

  .about-section p {
    font-size: 1rem;
  }
}
</style>
<script setup lang="ts">
</script>