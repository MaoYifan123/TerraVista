<template>
  <div class="ai-recommend-container">
    <div class="header-actions">
      <el-button type="primary" @click="dialogVisible = true">
        智能景点推荐
      </el-button>
      <el-button type="danger" @click="clearHistory" v-if="conversationHistory.length > 0">
        清除历史记录
      </el-button>
    </div>

    <el-dialog
      v-model="dialogVisible"
      title="智能景点推荐"
      width="50%"
      :before-close="handleClose"
    >
      <el-form :model="filterForm" label-width="100px">
        <el-form-item label="选择省份">
          <el-select
            v-model="filterForm.province"
            multiple
            collapse-tags
            placeholder="请选择省份"
          >
            <el-option
              v-for="province in tags.provinces"
              :key="province"
              :label="province"
              :value="province"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="景观偏好">
          <el-select
            v-model="filterForm.landscape"
            multiple
            collapse-tags
            placeholder="请选择景观偏好"
          >
            <el-option
              v-for="tag in tags.landscape"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="文化偏好">
          <el-select
            v-model="filterForm.culture"
            multiple
            collapse-tags
            placeholder="请选择文化偏好"
          >
            <el-option
              v-for="tag in tags.culture"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="补充说明">
          <el-input
            v-model="filterForm.additionalInfo"
            type="textarea"
            :rows="3"
            placeholder="请输入您的其他需求或偏好，例如：想要安静的地方、适合拍照的景点等"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="getRecommendations" :loading="loading">
            获取推荐
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 对话历史记录对话框 -->
    <div class="conversation-container">
      <div v-for="(message, index) in conversationHistory" :key="index" class="message-item">
        <div class="message-header">
          <span class="timestamp">{{ message.timestamp }}</span>
        </div>
        <div class="message-content">
          <p class="typewriter">{{ message.content }}</p>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="rawContentVisible"
      title="推荐内容"
      width="70%"
      :before-close="handleRawContentClose"
    >
      <div class="raw-content">
        <p class="typewriter">{{ rawContent }}</p>
      </div>
    </el-dialog>

    <div v-if="recommendations.length > 0" class="recommendations-container">
      <el-card v-for="(rec, index) in recommendations" :key="index" class="recommendation-card">
        <template #header>
          <div class="recommendation-header">
            <h3>{{ rec.name }}</h3>
          </div>
        </template>
        <div class="recommendation-content">
          <p><strong>推荐理由：</strong><span class="typewriter">{{ typewriterTexts[index]?.reason || '' }}</span></p>
          <p><strong>最佳游玩时间：</strong><span class="typewriter">{{ typewriterTexts[index]?.bestTime || '' }}</span></p>
          <p><strong>交通建议：</strong><span class="typewriter">{{ typewriterTexts[index]?.transportation || '' }}</span></p>
        </div>
      </el-card>
    </div>

    <el-empty v-else-if="!loading" description="请点击上方按钮获取推荐" />
  </div>
</template>

<script>
import axios from 'axios'
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'AIRecommend',
  setup() {
    const router = useRouter()
    const dialogVisible = ref(false)
    const rawContentVisible = ref(false)
    const rawContent = ref('')
    const conversationHistory = ref([])
    const recommendations = ref([])
    const loading = ref(false)
    const typewriterTexts = reactive({})
    
    const filterForm = reactive({
      province: [],
      landscape: [],
      culture: [],
      additionalInfo: ''
    })

    const tags = reactive({
      provinces: [],
      landscape: [],
      culture: []
    })

    // 从localStorage加载对话历史
    const loadConversationHistory = () => {
      const savedHistory = localStorage.getItem('conversationHistory')
      if (savedHistory) {
        try {
          conversationHistory.value = JSON.parse(savedHistory)
        } catch (e) {
          console.error('加载对话历史失败:', e)
          conversationHistory.value = []
        }
      }
    }

    // 保存对话历史到localStorage
    const saveConversationHistory = () => {
      try {
        localStorage.setItem('conversationHistory', JSON.stringify(conversationHistory.value))
      } catch (e) {
        console.error('保存对话历史失败:', e)
      }
    }

    // 监听对话历史变化
    watch(conversationHistory, () => {
      saveConversationHistory()
    }, { deep: true })

    // 初始化函数
    const initialize = async () => {
      // 检查登录状态
      const token = localStorage.getItem('token')
      if (!token) {
        router.push('/login')
        return
      }
      // 加载对话历史
      loadConversationHistory()
      // 获取标签
      await fetchTags()
    }

    // 在组件挂载时初始化
    onMounted(() => {
      initialize()
    })

    // 添加清除历史记录的方法
    const clearHistory = () => {
      if (confirm('确定要清除所有对话历史吗？')) {
        conversationHistory.value = []
        localStorage.removeItem('conversationHistory')
      }
    }

    const startTypewriter = (index) => {
      const rec = recommendations.value[index]
      const fields = ['reason', 'bestTime', 'transportation']
      
      fields.forEach(field => {
        const text = rec[field]
        let currentIndex = 0
        
        const interval = setInterval(() => {
          if (currentIndex < text.length) {
            typewriterTexts[index][field] = text.substring(0, currentIndex + 1)
            currentIndex++
          } else {
            clearInterval(interval)
          }
        }, 50)
      })
    }

    const startContentTypewriter = (text, historyIndex) => {
      let currentIndex = 0
      const interval = setInterval(() => {
        if (currentIndex < text.length) {
          conversationHistory.value[historyIndex].content = text.substring(0, currentIndex + 1)
          currentIndex++
        } else {
          clearInterval(interval)
        }
      }, 20)
    }

    const handleClose = (done) => {
      if (confirm('确认关闭？')) {
        done()
      }
    }

    const handleRawContentClose = (done) => {
      if (confirm('确认关闭？')) {
        done()
      }
    }

    const fetchTags = async () => {
      try {
        const response = await axios.get('/api/ai/tags')
        Object.assign(tags, response.data)
      } catch (error) {
        console.error('获取标签失败:', error)
        alert('获取标签失败')
      }
    }

    const getRecommendations = async () => {
      if (filterForm.province.length === 0) {
        alert('请至少选择一个省份')
        return
      }

      loading.value = true
      dialogVisible.value = false
      
      try {
        const response = await axios.post('/api/ai/recommend', {
          province: filterForm.province,
          landscape: filterForm.landscape || [],
          culture: filterForm.culture || [],
          additionalInfo: filterForm.additionalInfo || ''
        })
        
        console.log('API Response:', response.data)
        
        let content = ''
        if (response.data.choices && response.data.choices[0]?.message?.content) {
          content = response.data.choices[0].message.content
        } else if (typeof response.data === 'string') {
          content = response.data
        } else if (response.data.content) {
          content = response.data.content
        }

        console.log('Extracted content:', content)

        // 处理可能包含的markdown代码块标记
        content = content.replace(/```json\n?|\n?```/g, '').trim()

        conversationHistory.value.push({
          timestamp: new Date().toLocaleString(),
          content: ''
        })
        
        startContentTypewriter(content, conversationHistory.value.length - 1)
        
        let parsedRecommendations
        try {
          if (Array.isArray(content)) {
            parsedRecommendations = content
          } else {
            const parsedData = JSON.parse(content)
            if (parsedData.recommendations && Array.isArray(parsedData.recommendations)) {
              parsedRecommendations = parsedData.recommendations
              if (parsedData.additional_tips) {
                const tipsContent = JSON.stringify(parsedData.additional_tips, null, 2)
                conversationHistory.value.push({
                  timestamp: new Date().toLocaleString(),
                  content: '额外提示：\n' + tipsContent
                })
              }
            } else {
              parsedRecommendations = parsedData
            }
          }
        } catch (e) {
          console.error('解析推荐数据失败:', e)
          console.error('原始数据:', content)
          alert('解析推荐数据失败，请检查返回数据格式')
          return
        }

        if (!Array.isArray(parsedRecommendations)) {
          console.error('推荐数据格式不正确:', parsedRecommendations)
          alert('推荐数据格式不正确，应为数组格式')
          return
        }

        recommendations.value = parsedRecommendations
        
        // 初始化打字机效果
        Object.keys(typewriterTexts).forEach(key => delete typewriterTexts[key])
        recommendations.value.forEach((rec, index) => {
          typewriterTexts[index] = {
            reason: '',
            bestTime: '',
            transportation: ''
          }
          startTypewriter(index)
        })
      } catch (error) {
        console.error('获取推荐失败:', error)
        alert(error.response?.data?.error || '获取推荐失败')
      } finally {
        loading.value = false
      }
    }

    return {
      dialogVisible,
      rawContentVisible,
      rawContent,
      conversationHistory,
      filterForm,
      tags,
      recommendations,
      loading,
      typewriterTexts,
      handleClose,
      handleRawContentClose,
      fetchTags,
      getRecommendations,
      clearHistory
    }
  }
}
</script>

<style scoped>
.ai-recommend-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.recommendations-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.recommendation-card {
  height: 100%;
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recommendation-header h3 {
  margin: 0;
  color: #303133;
}

.recommendation-content {
  color: #606266;
}

.recommendation-content p {
  margin: 8px 0;
  line-height: 1.6;
}

.el-select {
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.typewriter {
  display: inline-block;
  min-height: 1.2em;
}

.raw-content {
  font-family: monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  max-height: 60vh;
  overflow-y: auto;
}

.raw-content .typewriter {
  margin: 0;
  line-height: 1.6;
}

.conversation-container {
  margin-top: 20px;
  max-height: 500px;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.message-item {
  margin-bottom: 20px;
  padding: 15px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-header {
  margin-bottom: 10px;
  color: #909399;
  font-size: 0.9em;
}

.message-content {
  font-family: monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
  line-height: 1.6;
}

.message-content .typewriter {
  margin: 0;
}

.el-textarea {
  width: 100%;
}

.header-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
</style>
