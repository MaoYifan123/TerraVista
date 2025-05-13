<!-- 文档9（views/AdminPoiManagement.vue）完整代码 -->
<template>
  <div class="admin-poi-management">
    <div class="toolbar">
      <el-button type="primary" @click="handleCreate">新增POI</el-button>
      <el-input
          v-model="searchKeyword"
          placeholder="搜索POI名称"
          style="width: 300px; margin-left: 20px"
          clearable
          @clear="loadPois"
          @keyup.enter="loadPois"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-table
        :data="poiList"
        border
        stripe
        v-loading="loading"
        style="width: 100%; margin-top: 20px"
    >
      <el-table-column prop="name" label="名称" width="200" />
      <el-table-column prop="province" label="省份" width="120" />
      <el-table-column prop="category" label="分类" width="150" />
      <el-table-column label="坐标" width="220">
        <template #default="{row}">
          <div>经度: {{ row.longitude }}</div>
          <div>纬度: {{ row.latitude }}</div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{row}">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm
              title="确认删除该POI吗？"
              @confirm="handleDelete(row.id)"
          >
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="loadPois"
      />
    </div>

    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        destroy-on-close
    >
      <el-form
          ref="formRef"
          :model="currentPoi"
          label-width="100px"
          :rules="formRules"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="currentPoi.name" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-select
              v-model="currentPoi.province"
              placeholder="选择省份"
              filterable
          >
            <el-option
                v-for="province in provinces"
                :key="province"
                :label="province"
                :value="province"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="currentPoi.category" />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input-number
              v-model="currentPoi.longitude"
              :precision="6"
              :step="0.000001"
          />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number
              v-model="currentPoi.latitude"
              :precision="6"
              :step="0.000001"
          />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="currentPoi.image" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const provinces = [
  '北京', '上海', '天津', '重庆', '河北', '山西', '辽宁', '吉林', '黑龙江',
  '江苏', '浙江', '安徽', '福建', '江西', '山东', '河南', '湖北', '湖南',
  '广东', '海南', '四川', '贵州', '云南', '陕西', '甘肃', '青海', '台湾',
  '内蒙古', '广西', '西藏', '宁夏', '新疆', '香港', '澳门'
]

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const poiList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('')
const currentPoi = ref({
  name: '',
  province: '',
  category: '',
  longitude: null,
  latitude: null,
  image: ''
})

const formRules = reactive({
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  province: [{ required: true, message: '请选择省份', trigger: 'change' }],
  category: [{ required: true, message: '请输入分类', trigger: 'blur' }],
  longitude: [{ required: true, message: '请输入经度', trigger: 'blur' }],
  latitude: [{ required: true, message: '请输入纬度', trigger: 'blur' }]
})

const loadPois = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.current - 1,
      size: pagination.size,
      name: searchKeyword.value
    }

    const response = await request.get('/poi/search', { params })
    poiList.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadPois)

const handleCreate = () => {
  dialogTitle.value = '新建POI'
  currentPoi.value = {
    name: '',
    province: '',
    category: '',
    longitude: null,
    latitude: null,
    image: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑POI'
  currentPoi.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (currentPoi.value.id) {
      await request.post('/admin/poi', currentPoi.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/admin/poi', currentPoi.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadPois()
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || '未知错误'))
  }
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/admin/poi/${id}`)
    ElMessage.success('删除成功')
    loadPois()
  } catch (error) {
    ElMessage.error('删除失败: ' + (error.response?.data?.message || '未知错误'))
  }
}
</script>

<style scoped>
.admin-poi-management {
  padding: 20px;
}

.toolbar {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>