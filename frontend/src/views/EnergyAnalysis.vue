<template>
  <div class="energy-analysis-container">
    <el-card class="box-card filter-card">
      <div slot="header" class="clearfix">
        <span>能耗分析筛选</span>
      </div>

      <!-- Filter Section -->
      <el-form :inline="true" :model="filters" class="filter-form" ref="filterFormRef">
        <el-form-item label="选择设备" prop="deviceIds">
          <el-select v-model="filters.deviceIds" multiple placeholder="可多选，或不选代表所有设备" style="width: 280px;" clearable>
            <el-option
              v-for="device in deviceList"
              :key="device.id"
              :label="device.name + (device.model ? ' (' + device.model + ')' : '')"
              :value="device.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="能源类型" prop="energyType">
           <el-input v-model="filters.energyType" placeholder="例如: 电能, 水 (可选)" style="width: 180px;" clearable></el-input>
        </el-form-item>

        <el-form-item label="分析周期" prop="timeRange" required>
          <el-date-picker
            v-model="filters.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期时间"
            end-placeholder="结束日期时间"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-ddTHH:mm:ss" 
            :default-time="['00:00:00', '23:59:59']"
            style="width: 370px;">
          </el-date-picker>
        </el-form-item>
        
        <el-form-item label="趋势粒度" prop="timeGranularityForTrend">
            <el-select v-model="filters.timeGranularityForTrend" placeholder="选择趋势图时间粒度" style="width: 150px;">
                <el-option label="每日" value="daily"></el-option>
                <el-option label="每小时" value="hourly"></el-option>
            </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="performAnalysis" :loading="loading">开始分析</el-button>
          <el-button icon="el-icon-refresh" @click="resetFilters">重置筛选</el-button>
          <el-button 
            type="success" 
            icon="el-icon-document" 
            @click="exportDataAsCSV" 
            :disabled="!analysisResults.overallStats || loading"
            v-if="analysisResults.overallStats">
            导出CSV报表
          </el-button>
          <el-button 
            type="primary" 
            icon="el-icon-notebook-2" 
            @click="exportDataAsExcel" 
            :disabled="!analysisResults.overallStats || loading"
            v-if="analysisResults.overallStats">
            导出Excel报表
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Results Display Area -->
    <div v-if="loading" class="loading-overlay">
        <el-spinner text="正在加载分析结果..."></el-spinner>
    </div>

    <div v-if="!loading && analysisResults.overallStats">
        <!-- Overall Stats Summary -->
        <el-card class="box-card stats-summary-card">
            <div slot="header" class="clearfix">
                <span>总体能耗摘要</span>
            </div>
            <el-descriptions :column="2" border>
                <el-descriptions-item label="总能耗">
                    {{ analysisResults.overallStats.totalEnergyConsumed != null ? analysisResults.overallStats.totalEnergyConsumed.toFixed(2) : 'N/A' }} 
                    {{ analysisResults.overallStats.unit }}
                </el-descriptions-item>
                <el-descriptions-item label="平均能耗 (每条记录)">
                    {{ analysisResults.overallStats.averageEnergyConsumedPerRecord != null ? analysisResults.overallStats.averageEnergyConsumedPerRecord.toFixed(2) : 'N/A' }} 
                    {{ analysisResults.overallStats.unit }}
                </el-descriptions-item>
                <el-descriptions-item label="数据记录数">
                    {{ analysisResults.overallStats.numberOfRecords != null ? analysisResults.overallStats.numberOfRecords : 'N/A' }}
                </el-descriptions-item>
                 <el-descriptions-item label="查询参数">
                    设备: {{ analysisResults.queryParameters.deviceIds && analysisResults.queryParameters.deviceIds.length > 0 ? analysisResults.queryParameters.deviceIds.join(', ') : '全部' }} <br/>
                    类型: {{ analysisResults.queryParameters.energyType || '全部' }} <br/>
                    时间: {{ new Date(analysisResults.queryParameters.startTime).toLocaleString() }} - {{ new Date(analysisResults.queryParameters.endTime).toLocaleString() }}
                </el-descriptions-item>
            </el-descriptions>
        </el-card>

        <!-- Chart Display Area -->
        <el-row :gutter="20">
            <el-col :span="24"> <!-- Changed to full width for single trend chart -->
              <el-card shadow="hover" class="chart-card">
                <!-- Trend chart will be rendered here by ECharts -->
                <div ref="trendChart" style="height: 450px; width: 100%;"></div>
              </el-card>
            </el-col>
            <!-- Pie chart column can be added back if needed
            <el-col :span="12">
              <el-card shadow="hover" class="chart-card">
                <div slot="header">能耗占比图 (示例)</div>
                <div ref="pieChart" style="height: 400px;"></div>
              </el-card>
            </el-col>
            -->
        </el-row>
        
        <!-- Device Breakdown Table -->
        <el-card class="box-card device-breakdown-card" v-if="analysisResults.deviceBreakdown && analysisResults.deviceBreakdown.length > 0">
            <div slot="header" class="clearfix">
                <span>各设备能耗细分</span>
            </div>
            <el-table :data="analysisResults.deviceBreakdown" stripe style="width: 100%" border>
                <el-table-column prop="deviceName" label="设备名称" sortable></el-table-column>
                <el-table-column prop="deviceId" label="设备ID" sortable width="120"></el-table-column>
                <el-table-column label="总能耗" sortable>
                    <template slot-scope="scope">
                        {{ scope.row.totalEnergyConsumed != null ? scope.row.totalEnergyConsumed.toFixed(2) : 'N/A' }} {{ scope.row.unit }}
                    </template>
                </el-table-column>
                <el-table-column label="平均能耗 (每条记录)" sortable>
                     <template slot-scope="scope">
                        {{ scope.row.averageEnergyConsumedPerRecord != null ? scope.row.averageEnergyConsumedPerRecord.toFixed(2) : 'N/A' }} {{ scope.row.unit }}
                    </template>
                </el-table-column>
                <el-table-column prop="numberOfRecords" label="数据记录数" sortable width="150"></el-table-column>
            </el-table>
        </el-card>
         <el-alert
            v-else-if="!loading && analysisResults.overallStats && (!analysisResults.deviceBreakdown || analysisResults.deviceBreakdown.length === 0)"
            title="没有查询到符合条件的设备细分数据"
            type="info"
            show-icon
            :closable="false"
            style="margin-top: 20px;">
        </el-alert>
    </div>
    <el-empty v-if="!loading && !analysisResults.overallStats" description="暂无分析数据，请选择筛选条件后点击'开始分析'"></el-empty>

  </div>
</template>

<script>
import * as echarts from 'echarts'; // 1. Import ECharts
import * as XLSX from 'xlsx'; // Import XLSX

export default {
  name: 'EnergyAnalysis',
  data() {
    return {
      filters: {
        deviceIds: [],
        energyType: '',
        timeRange: [], // [startTime, endTime] as ISO strings from date picker
        timeGranularityForTrend: 'daily' // Added for API, can be configurable
      },
      deviceList: [],
      loading: false,
      trendChartInstance: null,
      pieChartInstance: null, // Keep for now, implement later if needed
      analysisResults: { // To store all parts of the response
        queryParameters: null,
        overallStats: null,
        deviceBreakdown: [],
        energyTrend: null
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
    validateFilters() {
        if (!this.filters.timeRange || this.filters.timeRange.length !== 2) {
            this.$message.error('请选择分析周期！');
            return false;
        }
        // Convert to Date objects to check validity
        const startTime = new Date(this.filters.timeRange[0]);
        const endTime = new Date(this.filters.timeRange[1]);

        if (isNaN(startTime.getTime()) || isNaN(endTime.getTime())) {
            this.$message.error('分析周期的日期时间格式无效！');
            return false;
        }
        
        if (startTime >= endTime) {
            this.$message.error('开始时间必须早于结束时间！');
            return false;
        }
        return true;
    },
    performAnalysis() {
      if (!this.validateFilters()) {
          return;
      }
      // Note: Element UI form validation (`this.$refs.filterFormRef.validate`)
      // is useful if you have specific rules on <el-form-item prop="xxx">.
      // Here, validateFilters() handles the primary logic.
      // If filterFormRef has rules, they will also be checked.
      this.$refs.filterFormRef.validate((valid) => {
        if (valid) { // `valid` will be true if Element UI rules (if any) pass
          this.loading = true;
          const params = {
            // deviceIds should be an array of Longs, Axios handles serialization
            deviceIds: this.filters.deviceIds.length > 0 ? this.filters.deviceIds.join(',') : null, // Send as comma-separated string
            energyType: this.filters.energyType || null, // Send null if empty
            startTime: this.filters.timeRange[0], // ISO String
            endTime: this.filters.timeRange[1],   // ISO String
            timeGranularityForTrend: this.filters.timeGranularityForTrend
          };

          console.log("Submitting analysis params:", params);

          this.$http.get('/api/energy-stats', { params })
            .then(response => {
              if (response.data) {
                this.analysisResults = response.data;
                this.$message.success('能耗分析数据已获取!');
                this.$nextTick(() => { // Ensure DOM is updated before rendering charts
                    this.renderCharts();
                });
              } else {
                this.$message.error('获取能耗分析数据失败: 响应为空');
                this.clearResultsAndCharts();
              }
              this.loading = false;
            })
            .catch(error => {
              let errMsg = '获取能耗分析数据失败';
              if (error.response && error.response.data) {
                // Try to get message from Spring Boot's default error response
                if (error.response.data.message) errMsg += ': ${error.response.data.message}';
                else if (error.response.data.error) errMsg += ': ${error.response.data.error}';
              } else if (error.message) {
                errMsg += ': ${error.message}';
              }
              this.$message.error(errMsg);
              this.loading = false;
              this.clearResultsAndCharts();
            });
        } else {
          // This part is for Element UI's own validation messages if form rules are set
          // this.$message.error('请检查筛选条件中的错误!'); // Typically Element UI shows errors directly on fields
          return false;
        }
      });
    },
    clearResultsAndCharts() {
        this.analysisResults = {
            queryParameters: null,
            overallStats: null,
            deviceBreakdown: [],
            energyTrend: null
        };
        if (this.trendChartInstance) {
            this.trendChartInstance.clear();
            // Optional: Dispose and nullify if you want to completely re-init next time
            // this.trendChartInstance.dispose();
            // this.trendChartInstance = null;
        }
        // if (this.pieChartInstance) { this.pieChartInstance.clear(); }
    },
    resetFilters() {
      // Check if filterFormRef is available before calling resetFields
      if (this.$refs.filterFormRef) {
        this.$refs.filterFormRef.resetFields();
      }
      this.filters.deviceIds = [];
      this.filters.energyType = '';
      this.filters.timeRange = [];
      this.filters.timeGranularityForTrend = 'daily';
      this.clearResultsAndCharts();
    },
    exportDataAsCSV() {
      if (!this.analysisResults || !this.analysisResults.overallStats) {
        this.$message.info('没有可导出的分析数据。');
        return;
      }

      const { queryParameters, overallStats, deviceBreakdown } = this.analysisResults;
      let csvContent = "";

      // Helper to format arrays for CSV (handles comma within strings if any by quoting)
      const escapeCSV = (field) => {
        if (field === null || typeof field === 'undefined') return '""';
        let str = String(field);
        // If the field contains a comma, a quote, or a newline, enclose it in double quotes
        // and escape any existing double quotes by doubling them.
        if (str.search(/("|,|\n)/g) >= 0) {
            str = '"' + str.replace(/"/g, '""') + '"';
        }
        return str;
      };
      
      const formatDateForCSV = (isoDateString) => {
          if (!isoDateString) return 'N/A';
          try {
              return new Date(isoDateString).toLocaleString();
          } catch (e) {
              return isoDateString; // fallback to original if parsing fails
          }
      };

      // Section 1: Query Parameters
      csvContent += "查询参数\n";
      csvContent += "参数名称,参数值\n";
      csvContent += `设备IDs,${escapeCSV(queryParameters.deviceIds && queryParameters.deviceIds.length > 0 ? queryParameters.deviceIds.join(';') : '全部')}\n`; // Use semicolon for list
      csvContent += `能源类型,${escapeCSV(queryParameters.energyType || '全部')}\n`;
      csvContent += `开始时间,${escapeCSV(formatDateForCSV(queryParameters.startTime))}\n`;
      csvContent += `结束时间,${escapeCSV(formatDateForCSV(queryParameters.endTime))}\n`;
      csvContent += `趋势图粒度,${escapeCSV(queryParameters.timeGranularityForTrend || 'N/A')}\n\n`;

      // Section 2: Overall Statistics
      csvContent += "总体能耗摘要\n";
      csvContent += "统计项,值,单位\n";
      csvContent += `${escapeCSV('总能耗')},${escapeCSV(overallStats.totalEnergyConsumed != null ? overallStats.totalEnergyConsumed.toFixed(2) : 'N/A')},${escapeCSV(overallStats.unit || '')}\n`;
      csvContent += `${escapeCSV('平均能耗 (每条记录)')},${escapeCSV(overallStats.averageEnergyConsumedPerRecord != null ? overallStats.averageEnergyConsumedPerRecord.toFixed(2) : 'N/A')},${escapeCSV(overallStats.unit || '')}\n`;
      csvContent += `${escapeCSV('数据记录数')},${escapeCSV(overallStats.numberOfRecords != null ? overallStats.numberOfRecords : 'N/A')},\n\n`;

      // Section 3: Device Breakdown
      if (deviceBreakdown && deviceBreakdown.length > 0) {
        csvContent += "各设备能耗细分\n";
        // Header row for device breakdown
        csvContent += "设备ID,设备名称,总能耗,平均能耗 (每条记录),数据记录数,单位\n";
        // Data rows
        deviceBreakdown.forEach(device => {
          csvContent += [
            escapeCSV(device.deviceId),
            escapeCSV(device.deviceName),
            escapeCSV(device.totalEnergyConsumed != null ? device.totalEnergyConsumed.toFixed(2) : 'N/A'),
            escapeCSV(device.averageEnergyConsumedPerRecord != null ? device.averageEnergyConsumedPerRecord.toFixed(2) : 'N/A'),
            escapeCSV(device.numberOfRecords),
            escapeCSV(device.unit || '')
          ].join(',') + "\n";
        });
      } else {
        csvContent += "各设备能耗细分\n";
        csvContent += "没有查询到符合条件的设备细分数据\n";
      }
      csvContent += "\n";

      // Create Blob and trigger download
      // UTF-8 BOM to ensure Excel opens cyrillic/special characters correctly
      const BOM = "\uFEFF"; 
      const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' });
      const link = document.createElement("a");
      if (link.download !== undefined) { // Feature detection
        const url = URL.createObjectURL(blob);
        const reportDate = new Date().toISOString().split('T')[0]; // YYYY-MM-DD
        link.setAttribute("href", url);
        link.setAttribute("download", `能耗分析报表_${reportDate}.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      } else {
        this.$message.error('您的浏览器不支持直接下载文件，请尝试手动复制内容。');
      }
    },
    formatDateForDisplay(isoString) {
      if (!isoString) return 'N/A';
      return new Date(isoString).toLocaleString('zh-CN', {
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false
      });
    },
    exportDataAsExcel() {
      if (!this.analysisResults || !this.analysisResults.overallStats) {
        this.$message.info('没有可导出的分析数据。');
        return;
      }

      try {
        const wb = XLSX.utils.book_new();

        // --- Sheet 1: 查询与摘要 ---
        const queryParams = this.analysisResults.queryParameters || {};
        const overallStats = this.analysisResults.overallStats || {};
        
        const ws1Data = [
          ["报表生成时间:", new Date().toLocaleString()],
          [], // Empty row for spacing
          ["查询参数"],
          ["选择设备:", queryParams.deviceIds && queryParams.deviceIds.length > 0 ? queryParams.deviceIds.join(', ') : "全部"],
          ["能源类型:", queryParams.energyType || "全部"],
          ["分析周期 (开始):", this.formatDateForDisplay(queryParams.startTime)],
          ["分析周期 (结束):", this.formatDateForDisplay(queryParams.endTime)],
          ["趋势粒度:", queryParams.timeGranularityForTrend === 'daily' ? '每日' : (queryParams.timeGranularityForTrend === 'hourly' ? '每小时' : queryParams.timeGranularityForTrend)],
          [], // Empty row for spacing
          ["总体能耗摘要"],
          ["总能耗:", `${overallStats.totalEnergyConsumed != null ? overallStats.totalEnergyConsumed.toFixed(2) : 'N/A'} ${overallStats.unit || ''}`],
          ["平均能耗 (每条记录):", `${overallStats.averageEnergyConsumedPerRecord != null ? overallStats.averageEnergyConsumedPerRecord.toFixed(2) : 'N/A'} ${overallStats.unit || ''}`],
          ["数据记录数:", overallStats.numberOfRecords != null ? overallStats.numberOfRecords : 'N/A'],
        ];
        const ws1 = XLSX.utils.aoa_to_sheet(ws1Data);
        // Adjust column widths for Sheet 1
        ws1['!cols'] = [ {wch: 25}, {wch: 30} ];
        XLSX.utils.book_append_sheet(wb, ws1, "查询与摘要");

        // --- Sheet 2: 设备能耗明细 ---
        const deviceBreakdown = this.analysisResults.deviceBreakdown || [];
        if (deviceBreakdown.length > 0) {
          const ws2Data = [
            ["设备ID", "设备名称", "总能耗", "单位", "平均能耗 (每条记录)", "数据记录数"]
          ];
          deviceBreakdown.forEach(device => {
            ws2Data.push([
              device.deviceId,
              device.deviceName,
              device.totalEnergyConsumed != null ? device.totalEnergyConsumed.toFixed(2) : 'N/A',
              device.unit || '',
              device.averageEnergyConsumedPerRecord != null ? device.averageEnergyConsumedPerRecord.toFixed(2) : 'N/A',
              device.numberOfRecords
            ]);
          });
          const ws2 = XLSX.utils.aoa_to_sheet(ws2Data);
          // Adjust column widths for Sheet 2
          ws2['!cols'] = [ {wch: 15}, {wch: 25}, {wch: 15}, {wch: 10}, {wch: 20}, {wch: 15} ];
          XLSX.utils.book_append_sheet(wb, ws2, "设备能耗明细");
        } else {
            const ws2EmptyData = [["没有查询到符合条件的设备细分数据"]];
            const ws2 = XLSX.utils.aoa_to_sheet(ws2EmptyData);
            XLSX.utils.book_append_sheet(wb, ws2, "设备能耗明细");
        }

        // Generate Excel file and trigger download
        const reportTime = new Date().toISOString().slice(0, 19).replace(/:/g, "-").replace("T", "_");
        const fileName = `能耗分析报表_${reportTime}.xlsx`;
        XLSX.writeFile(wb, fileName);
        this.$message.success(`成功导出Excel报表: ${fileName}`);

      } catch (error) {
        console.error("导出Excel失败:", error);
        this.$message.error('导出Excel报表失败，详情请查看控制台。');
      }
    },
    renderCharts() {
      if (!this.$refs.trendChart) { // Ensure the ref is available
          console.warn("Trend chart ref not found, cannot render.");
          return;
      }
      if (!this.analysisResults || !this.analysisResults.energyTrend || !this.analysisResults.energyTrend.timeLabels) {
        if (this.trendChartInstance) this.trendChartInstance.clear();
        console.log("No trend data to render or chart instance not ready.");
        return;
      }

      const trendData = this.analysisResults.energyTrend;
      
      if (!this.trendChartInstance) {
        try {
            this.trendChartInstance = echarts.init(this.$refs.trendChart);
        } catch (e) {
            console.error("Failed to initialize trend chart:", e);
            this.$message.error("图表初始化失败，请刷新页面或检查控制台。");
            return;
        }
      }
      
      const yAxisUnit = trendData.datasets && trendData.datasets.length > 0 && trendData.datasets[0].unit 
                        ? trendData.datasets[0].unit 
                        : '';

      const trendOption = {
        title: {
          text: '能耗趋势图',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          formatter: (params) => { // Custom tooltip formatter
            let tooltipHtml = params[0].name + '<br/>'; // Time label
            params.forEach(item => {
              tooltipHtml += `${item.marker} ${item.seriesName}: ${item.value != null ? item.value.toFixed(2) : 'N/A'} ${yAxisUnit}<br/>`;
            });
            return tooltipHtml;
          }
        },
        legend: {
          data: trendData.datasets ? trendData.datasets.map(ds => ds.label) : [],
          top: 'bottom'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%', // Adjusted for legend at bottom
          containLabel: true
        },
        toolbox: {
          feature: {
            saveAsImage: { title: '保存为图片' }
          }
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: trendData.timeLabels || []
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: `{value} ${yAxisUnit}`
          }
        },
        dataZoom: [ // Add data zoom for better navigation on large datasets
            {
                type: 'slider',
                start: 0,
                end: 100,
                xAxisIndex: 0,
                filterMode: 'empty'
            },
            {
                type: 'inside',
                start: 0,
                end: 100,
                xAxisIndex: 0,
                filterMode: 'empty'
            }
        ],
        series: trendData.datasets ? trendData.datasets.map(ds => ({
          name: ds.label,
          type: 'line',
          data: ds.data || [],
          smooth: true,
          itemStyle: { /*Можно добавить стили для линий/точек*/ },
          lineStyle: { /*Можно добавить стили для линий*/ }
        })) : []
      };
      this.trendChartInstance.setOption(trendOption, true); // true to not merge with previous options
    },
    handleResize() { 
        this.$nextTick(() => { // Ensure resize happens after any DOM updates
            if (this.trendChartInstance) {
                this.trendChartInstance.resize();
            }
            // if (this.pieChartInstance) { this.pieChartInstance.resize(); }
        });
    }
  },
  mounted() {
    window.addEventListener('resize', this.handleResize);
    // Consider fetching devices only if deviceList is empty or on demand
    if(this.deviceList.length === 0) {
        this.fetchDevices();
    }
  },
  created() {
    // fetchDevices moved to mounted to ensure $http is available if it relies on global setup.
    // However, typically it's fine in created(). If issues, move back to created() or ensure $http setup.
  },
  beforeDestroy() {
    if (this.trendChartInstance) {
      this.trendChartInstance.dispose();
      this.trendChartInstance = null;
    }
    // if (this.pieChartInstance) { this.pieChartInstance.dispose(); this.pieChartInstance = null; }
    window.removeEventListener('resize', this.handleResize);
  }
};
</script>

<style scoped>
.energy-analysis-container {
  padding: 20px;
}
.filter-card, .stats-summary-card, .chart-card, .device-breakdown-card {
  margin-bottom: 20px;
}

.filter-form .el-form-item {
  margin-bottom: 18px; /* Increased bottom margin */
}

/* Make sure chart resizes correctly within its container */
.chart-card div[ref="trendChart"] {
    width: 100%; /* Ensure ECharts takes full width of its parent */
}

.loading-overlay {
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.7);
    z-index: 2000; /* High z-index to overlay other content */
}
.el-descriptions {
    margin-top: 15px;
}

.stats-summary-card .el-descriptions-item__label {
  font-weight: bold;
}

.stats-summary-card .el-descriptions-item__content {
  word-break: break-all;
}
</style> 