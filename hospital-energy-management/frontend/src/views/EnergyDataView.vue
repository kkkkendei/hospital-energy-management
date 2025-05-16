<template>
  <div class="energy-data-view-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>能源数据查询</span>
      </div>

      <!-- Filter Section -->
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="选择设备">
          <el-select v-model="filters.deviceId" placeholder="全部设备" clearable @clear="filters.deviceId = null">
            <el-option
              v-for="device in deviceList"
              :key="device.id"
              :label="device.name + ' (' + device.model + ')'"
              :value="device.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="filters.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期时间"
            end-placeholder="结束日期时间"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-ddTHH:mm:ss">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="fetchEnergyData">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- Data Table -->
      <el-table
        :data="energyDataList"
        v-loading="loading"
        style="width: 100%"
        border>
        <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
        <el-table-column prop="deviceId" label="设备ID" width="100" sortable></el-table-column>
        <el-table-column label="设备名称" width="180">
            <template slot-scope="scope">
                {{ getDeviceName(scope.row.deviceId) }}
            </template>
        </el-table-column>
        <el-table-column prop="recordTime" label="记录时间" width="200" sortable>
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.recordTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="energyValue" label="能耗值" width="120" sortable></el-table-column>
        <el-table-column prop="energyUnit" label="单位" width="100"></el-table-column>
        <el-table-column prop="notes" label="备注" min-width="200"></el-table-column>
      </el-table>

      <!-- Pagination (Optional, implement if needed) -->
      <!-- <el-pagination layout="prev, pager, next" :total="50"></el-pagination> --> 

    </el-card>
  </div>
</template>

<script>
export default {
  name: 'EnergyDataView',
  data() {
    return {
      filters: {
        deviceId: null,
        dateRange: [] // [startDate, endDate]
      },
      deviceList: [],
      energyDataList: [],
      loading: false
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
    fetchEnergyData() {
      this.loading = true;
      let params = {};
      if (this.filters.deviceId) {
        params.deviceId = this.filters.deviceId;
      }
      if (this.filters.dateRange && this.filters.dateRange.length === 2) {
        params.startTime = this.filters.dateRange[0];
        params.endTime = this.filters.dateRange[1];
      }

      this.$http.get('/api/energy-data', { params })
        .then(response => {
          this.energyDataList = response.data;
          this.loading = false;
        })
        .catch(error => {
          this.loading = false;
          this.$message.error('获取能源数据失败: ' + error.message);
        });
    },
    getDeviceName(deviceId) {
      const device = this.deviceList.find(d => d.id === deviceId);
      return device ? device.name : '未知设备';
    },
    formatDateTime(dateTimeStr) {
      if (!dateTimeStr) return '';
      try {
        // Assuming dateTimeStr is ISO 8601 like "2023-10-27T10:30:00"
        const date = new Date(dateTimeStr);
        if (isNaN(date.getTime())) {
            // Handle cases where date string might not be directly parsable by new Date()
            // For example, if it has a Z or timezone offset that JS new Date() handles differently across browsers
            // Or if it's just not a valid date string.
            // A more robust solution might involve a date library like Moment.js or date-fns if issues persist.
            return dateTimeStr; // Fallback to original string if parsing fails
        }
        // Simple formatting, customize as needed
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        const seconds = date.getSeconds().toString().padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      } catch (e) {
        console.warn('Error formatting date:', dateTimeStr, e);
        return dateTimeStr; // Fallback if any error during formatting
      }
    }
  },
  created() {
    this.fetchDevices();
    this.fetchEnergyData(); // Initial fetch
  }
};
</script>

<style scoped>
.energy-data-view-container {
  padding: 20px;
}
.filter-form {
  margin-bottom: 20px;
}
.el-select, .el-date-picker {
  width: 100%; /* Ensure selects and pickers take full width in their form item */
}
/* Adjust width for inline form items if needed */
.el-form--inline .el-form-item {
  margin-right: 10px;
}
</style>
