<template>
  <div class="device-management">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>设备列表</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="openAddDeviceDialog">添加设备</el-button>
      </div>
      
      <el-table
        :data="devices"
        v-loading="loading"
        style="width: 100%"
        border
        stripe>
        <el-table-column prop="serialNumber" label="ID" width="80" sortable></el-table-column>
        <el-table-column prop="name" label="设备名称" sortable show-overflow-tooltip></el-table-column>
        <el-table-column prop="model" label="型号" width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="类型" width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="location" label="位置" width="180" show-overflow-tooltip></el-table-column>
        <el-table-column prop="installationDate" label="安装日期" width="150" sortable>
          <template slot-scope="scope">
            {{ formatDate(scope.row.installationDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
                <el-tag :type="statusTagType(scope.row.status)" size="medium">{{ scope.row.status }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="ratedPower" label="额定功率(kW)" width="150" sortable></el-table-column>
        <el-table-column prop="energyType" label="能源类型" width="130" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- TODO: Add Pagination if needed -->

    </el-card>

    <!-- Add/Edit Device Dialog -->
    <el-dialog
      :title="isEditMode ? '编辑设备' : '添加设备'"
      :visible.sync="dialogVisible"
      width="60%"
      :close-on-click-modal="false"
      @close="resetForm('deviceFormRef')">
      <el-form :model="deviceForm" ref="deviceFormRef" :rules="deviceFormRules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="deviceForm.name" placeholder="请输入设备名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="deviceForm.model" placeholder="请输入设备型号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-input v-model="deviceForm.type" placeholder="请输入设备类型"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="deviceForm.location" placeholder="请输入设备位置"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="安装日期" prop="installationDate">
              <el-date-picker
                v-model="deviceForm.installationDate"
                type="date"
                placeholder="选择安装日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="deviceForm.status" placeholder="请选择设备状态" style="width: 100%;">
                <el-option label="运行中" value="运行中"></el-option>
                <el-option label="故障" value="故障"></el-option>
                <el-option label="维护中" value="维护中"></el-option>
                <el-option label="停止" value="停止"></el-option>
                <el-option label="正常" value="正常"></el-option> 
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="制造商" prop="manufacturer">
              <el-input v-model="deviceForm.manufacturer" placeholder="请输入制造商"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="序列号" prop="serialNumber">
              <el-input v-model="deviceForm.serialNumber" placeholder="请输入序列号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="额定功率(kW)" prop="ratedPower">
              <el-input-number v-model="deviceForm.ratedPower" :precision="2" :step="0.1" :min="0" placeholder="请输入额定功率" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="能源类型" prop="energyType">
              <el-input v-model="deviceForm.energyType" placeholder="请输入能源类型"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="notes">
          <el-input type="textarea" v-model="deviceForm.notes" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitDeviceForm('deviceFormRef')">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
// import axios from 'axios'; // Removed unused import

const defaultDeviceForm = {
  id: null,
  name: '',
  model: '',
  type: '',
  location: '',
  installationDate: null,
  status: '正常',
  manufacturer: '',
  serialNumber: '',
  ratedPower: 0,
  energyType: '',
  notes: ''
};

export default {
  name: 'DeviceManagement',
  data() {
    return {
      devices: [],
      loading: false,
      dialogVisible: false,
      isEditMode: false,
      deviceForm: { ...defaultDeviceForm },
      deviceFormRules: {
        name: [
          { required: true, message: '请输入设备名称', trigger: 'blur' }
        ],
        model: [
          { required: true, message: '请输入设备型号', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请输入设备类型', trigger: 'blur' }
        ],
        location: [
          { required: true, message: '请输入设备位置', trigger: 'blur' }
        ],
        installationDate: [
          { required: true, message: '请选择安装日期', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择设备状态', trigger: 'change' }
        ],
        // ratedPower is not strictly required, but if provided, should be a number.
        // Other fields like manufacturer, serialNumber, energyType, notes are optional.
      }
    };
  },
  created() {
    this.fetchDevices();
  },
  methods: {
    async fetchDevices() {
      this.loading = true;
      try {
        const response = await this.$http.get('/api/devices');
        this.devices = response.data || [];
      } catch (error) {
        console.error("获取设备列表失败:", error);
        this.$message.error('获取设备列表失败，请稍后重试。');
        this.devices = [];
      } finally {
        this.loading = false;
      }
    },
    formatDate(dateArray) {
      if (!dateArray) return '';
      if (Array.isArray(dateArray)) {
        const [year, month, day] = dateArray;
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      } else if (typeof dateArray === 'string') {
        return dateArray.split('T')[0];
      }
      return String(dateArray); 
    },
    statusTagType(status) {
        if (status === '运行中' || status === '正常') return 'success';
        if (status === '故障') return 'danger';
        if (status === '维护中') return 'warning';
        if (status === '停止') return 'info';
        return '';
    },
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
      }
      this.deviceForm = { ...defaultDeviceForm };
    },
    openAddDeviceDialog() {
      this.isEditMode = false;
      this.resetForm('deviceFormRef'); // Reset form before opening
      this.dialogVisible = true;
      this.$nextTick(() => { // Ensure form is rendered before trying to clear validation
        if (this.$refs.deviceFormRef) {
          this.$refs.deviceFormRef.clearValidate();
        }
      });
    },
    handleEdit(row) {
      this.isEditMode = true;
      // Create a deep copy to avoid modifying the original table data directly
      this.deviceForm = JSON.parse(JSON.stringify(row));
      // Ensure installationDate is in 'yyyy-MM-dd' format for the date picker
      if (this.deviceForm.installationDate && Array.isArray(this.deviceForm.installationDate)) {
         this.deviceForm.installationDate = this.formatDate(this.deviceForm.installationDate);
      }
      this.dialogVisible = true;
       this.$nextTick(() => {
        if (this.$refs.deviceFormRef) {
          this.$refs.deviceFormRef.clearValidate();
        }
      });
    },
    handleDelete(row) {
      this.$confirm(`确定要删除设备 "${row.name}" 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.$http.delete(`/api/devices/${row.id}`);
          this.$message.success('删除成功!');
          this.fetchDevices(); // Refresh list
        } catch (error) {
          console.error("删除设备失败:", error);
          const errorMsg = error.response && error.response.data && error.response.data.message ? error.response.data.message : '删除失败，请稍后重试。';
          this.$message.error(errorMsg);
        }
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    async submitDeviceForm(formName) {
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          try {
            if (this.isEditMode) {
              // Edit mode
              await this.$http.put(`/api/devices/${this.deviceForm.id}`, this.deviceForm);
              this.$message.success('设备更新成功!');
            } else {
              // Add mode
              await this.$http.post('/api/devices', this.deviceForm);
              this.$message.success('设备添加成功!');
            }
            this.dialogVisible = false;
            this.fetchDevices(); // Refresh list
          } catch (error) {
            console.error("提交设备表单失败:", error);
            const errorMsg = error.response && error.response.data && error.response.data.message ? error.response.data.message : '操作失败，请稍后重试。';
            this.$message.error(errorMsg);
          }
        } else {
          console.log('表单校验失败!');
          return false;
        }
      });
    }
  }
};
</script>

<style scoped>
.device-management {
  padding: 20px;
}
.box-card {
  min-height: calc(100vh - 130px); /* Adjust based on your layout's header/footer height */
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
</style> 