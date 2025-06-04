<template>
  <div>
    <el-card>
      <div slot="header">
        <span>欢迎回来, {{ currentUser ? currentUser.username : '未知用户' }}!</span>
      </div>
      <p>这里是医院能源管理系统仪表盘。</p>
      <p>当前用户：{{ currentUser ? currentUser.username : '未知用户' }} ({{ currentUser && currentUser.enabled ? '管理员' : '普通用户' }})</p>
      <div v-if="isAdmin">
        <div v-if="alarmDevices.length > 0" style="margin-bottom: 20px;">
          <el-alert
            v-for="device in alarmDevices"
            :key="device.deviceId"
            :title="`警报：设备【${device.deviceName}】平均能耗已达 ${device.averageEnergyConsumedPerRecord} kWh，超出阈值！`"
            type="error"
            show-icon
            :closable="false"
            style="margin-bottom: 8px;"
          />
        </div>
        <el-table :data="deviceStats" style="width: 100%; margin-top: 20px;" :row-class-name="tableRowClassName">
          <el-table-column prop="deviceName" label="设备名称" width="180" />
          <el-table-column prop="totalEnergyConsumed" label="总能耗 (kWh)" width="180" />
          <el-table-column prop="unit" label="单位" width="100" />
          <el-table-column prop="averageEnergyConsumedPerRecord" label="平均能耗 (kWh)" width="180" />
          <el-table-column prop="numberOfRecords" label="数据条数" width="120" />
          <el-table-column label="状态" width="120">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.totalEnergyConsumed > threshold" type="danger">能耗超标</el-tag>
              <el-tag v-else type="success">正常</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import axios from 'axios';

export default {
  name: 'UserDashboard',
  data() {
    return {
      deviceStats: [],
      threshold: 100, // 每台设备报警阈值 100kWh
      timer: null
    };
  },
  computed: {
    ...mapState('auth', ['currentUser']),
    isAdmin() {
      return this.currentUser && this.currentUser.enabled === true;
    },
    alarmDevices() {
      return this.deviceStats.filter(device => device.averageEnergyConsumedPerRecord > this.threshold);
    }
  },
  methods: {
    fetchDeviceStats() {
      // 获取近30天的用电设备能耗数据
      const now = new Date();
      const start = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      start.setDate(start.getDate() - 29); // 包含今天共30天
      axios.get('/api/energy-stats', {
        params: {
          startTime: start.toISOString().slice(0, 19),
          endTime: now.toISOString().slice(0, 19),
          timeGranularityForTrend: 'daily',
          energyType: 'kWh' // 只查用电
        }
      }).then(res => {
        if (res.data && res.data.deviceBreakdown) {
          this.deviceStats = res.data.deviceBreakdown;
        }
      }).catch(() => {
        this.$message.error('获取设备能耗数据失败');
      });
    },
    tableRowClassName({ row }) {
      return row.averageEnergyConsumedPerRecord > this.threshold ? 'alarm-row' : '';
    }
  },
  mounted() {
    if (this.isAdmin) {
      this.fetchDeviceStats();
      this.timer = setInterval(this.fetchDeviceStats, 30000); // 每30秒刷新一次
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }
};
</script>

<style scoped>
.el-card {
  margin: 20px;
}

p {
  margin: 10px 0;
  font-size: 16px;
  line-height: 1.5;
}

.alarm-row {
  background: #ffeaea !important;
}
</style> 