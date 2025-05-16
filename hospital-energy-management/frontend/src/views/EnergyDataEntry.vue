<template>
  <div class="energy-data-entry-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>能源数据录入</span>
      </div>
      <el-form :model="formData" :rules="rules" ref="dataFormRef" label-width="120px">
        <el-form-item label="选择设备" prop="deviceId">
          <el-select v-model="formData.deviceId" placeholder="请选择设备" style="width: 100%;">
            <el-option
              v-for="device in deviceList"
              :key="device.id"
              :label="device.name + ' (' + device.model + ')'"
              :value="device.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="记录时间" prop="recordTime">
          <el-date-picker
            v-model="formData.recordTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%;"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-ddTHH:mm:ss">
          </el-date-picker>
        </el-form-item>

        <el-form-item label="能耗值" prop="energyValue">
          <el-input-number v-model="formData.energyValue" :precision="2" :step="0.1" placeholder="请输入能耗值" style="width: 100%;"></el-input-number>
        </el-form-item>

        <el-form-item label="单位" prop="energyUnit">
          <el-input v-model="formData.energyUnit" placeholder="例如: kWh, m³"></el-input>
        </el-form-item>

        <el-form-item label="备注" prop="notes">
          <el-input type="textarea" v-model="formData.notes" placeholder="请输入备注信息"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EnergyDataEntry',
  data() {
    return {
      formData: {
        deviceId: null,
        recordTime: '',
        energyValue: 0,
        energyUnit: 'kWh',
        notes: ''
      },
      deviceList: [],
      rules: {
        deviceId: [
          { required: true, message: '请选择设备', trigger: 'change' }
        ],
        recordTime: [
          { required: true, message: '请选择记录时间', trigger: 'change' }
        ],
        energyValue: [
          { required: true, message: '请输入能耗值', trigger: 'blur' },
          { type: 'number', message: '能耗值必须为数字'}
        ],
        energyUnit: [
          { required: true, message: '请输入单位', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    fetchDevices() {
      this.$http.get('/api/devices')
        .then(response => {
          this.deviceList = response.data;
        })
        .catch(error => {
          this.$message.error('获取设备列表失败: ' + error.message);
        });
    },
    submitForm() {
      this.$refs.dataFormRef.validate((valid) => {
        if (valid) {
          // 确保 recordTime 是 ISO 8601 格式的字符串，如果后端需要 Date 对象，则需要调整
          // Element UI 的 value-format="yyyy-MM-ddTHH:mm:ss" 应该能保证格式正确
          const dataToSubmit = {
            ...this.formData,
            // 如果后端期望 recordTime 是不含时区的纯净 ISO 字符串，确保 formData.recordTime 符合
            // 如果后端需要特定时区处理，这里可能需要 moment.js 或其他库来转换
          };

          this.$http.post('/api/energy-data', dataToSubmit)
            .then(() => {
              this.$message.success('能源数据提交成功!');
              this.resetForm();
            })
            .catch(error => {
              let errorMessage = '能源数据提交失败。';
              if (error.response && error.response.data && error.response.data.message) {
                errorMessage += '原因: ' + error.response.data.message;
              } else if (error.message) {
                errorMessage += '原因: ' + error.message;
              }
              this.$message.error(errorMessage);
            });
        } else {
          this.$message.error('请检查表单输入项!');
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.dataFormRef.resetFields();
      // Manually reset non-prop fields if necessary, e.g., if initial values are complex
      this.formData.energyValue = 0; // el-input-number might not reset to 0 with resetFields if initial is not set
      this.formData.energyUnit = 'kWh';
      this.formData.notes = '';
      // formData.deviceId and recordTime should be reset by resetFields
    }
  },
  created() {
    this.fetchDevices();
  }
};
</script>

<style scoped>
.energy-data-entry-container {
  padding: 20px;
}
.box-card {
  width: 600px;
  margin: 0 auto;
}
.el-input-number {
  width: 100%;
}
</style>
