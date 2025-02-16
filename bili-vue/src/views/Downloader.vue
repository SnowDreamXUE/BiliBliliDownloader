<template>
  <div class="download-page">
    <div v-if="downloadUrls.length === 0" class="no-data">暂无数据</div>
    <div v-if="downloadUrls.length > 1" class="once">
      <el-button type="primary" round @click="downloadAll">全部下载</el-button>
    </div>
    <div v-for="item in downloadUrls" :key="item.cid" class="download-item">
      <el-col :span="12">
        <div class="video-title">{{ item.title }}</div>
      </el-col>

      <el-col :span="7">
        <div class="video-quality">
          <el-select
              class="select-quality"
              v-model="item.qn"
              placeholder="选择清晰度"
              @change="getDownloadUrl(item.cid,item.qn)"
          >
            <el-option
                v-for="(option, index) in item.Qualities"
                :key="index"
                :label="option.label"
                :value="option.value"
            ></el-option>
          </el-select>
        </div>
      </el-col>
      <el-col :span="5">
       <div class="download-action">
         <el-button type="primary" @click="downVideo(item)">下载</el-button>
       </div>
      </el-col>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import {mapActions} from "vuex";
axios.defaults.baseURL = "http://localhost:8989"

export default {
  name: 'Downloader',

  data() {
    return {
      avid: "",
      p: "",
      pages: [],          // 所有分集信息
      selectedPages: [],  // 选中的分集索引
      downloadUrls: [],   // 分集下载链接
    }
  },

  mounted() {
    this.init();
    this.fetchDownloadUrls();
  },

  methods: {
    ...mapActions(['removeSelectedPage']),

    init() {
      const videoData = this.$store.state;

      // 将 Vuex 中的数据赋值给组件的 data
      this.avid = videoData.avid || "";
      this.p = videoData.p || "";
      this.pages = videoData.pages || [];
      this.selectedPages = videoData.selectedPages || [];
    },

    // 拆分出的获取下载链接方法
    async fetchDownloadUrls() {
      this.downloadUrls = [];

      try {
        // 获取所有选中分集的下载链接
        const promises = this.selectedPages.map(cid => {
          return this.getDownloadUrl(cid, 80);
        });

        await Promise.all(promises);
        this.$nextTick(() => {
          this.activeNames = '下载列表'; // 自动展开下载列表
        });
      } catch (error) {
        this.$notify.error({
          title: '获取下载链接失败',
          message: error.message,
          duration: 3000
        });
      }
    },

    // 修改后的获取下载链接方法
    async getDownloadUrl(cid, qn) {
      try {
        const res = await axios.get(`/download/${this.avid}/${cid}?qn=${qn}`);
        // console.log(res.data);

        // 提取清晰度选项
        let accept_description = res.data.data.accept_description;
        let accept_quality = res.data.data.accept_quality;
        const Qualities = accept_description.map((_, index) => ({
          value: accept_quality[index],
          label: accept_description[index],
        }));

        // 处理DASH格式的视频和音频
        const videoData = res.data.data.dash.video || [];
        const audioData = res.data.data.dash.audio || [];

        // 示例：选择最高质量的视频和音频流（根据实际需求调整）
        const bestVideo = videoData.find(video => video.id === qn);
        const bestAudio = audioData[0]; // 假设音频只有一种选择

        if (bestVideo && bestAudio) {
          const title = this.pages.find(p => p.cid === cid).part;

          // 更新下载链接（这里我们假设只需要视频链接，实际情况可能需要合成音视频）
          const existing = this.downloadUrls.find(item => item.cid === cid);
          if (existing) {
            existing.videoUrl = bestVideo.baseUrl; // 使用视频的基本URL作为下载链接
            existing.audioUrl = bestAudio.baseUrl; // 如果也需要音频链接的话
          } else {
            this.downloadUrls.push({
              cid,
              videoUrl: bestVideo.baseUrl,
              audioUrl: bestAudio.baseUrl, // 如果也需要音频链接的话
              title,
              Qualities,
              qn
            });
          }
        } else {
          this.$notify.error({
            title: '获取链接失败',
            message: `CID:${cid} 清晰度${qn}不可用或无音频流`,
            duration: 3000
          });
        }
      } catch (error) {
        this.$notify.error({
          title: '获取链接失败',
          message: `CID:${cid} 请求过程中发生错误`,
          duration: 3000
        });
      }
    },


    downVideo(item) {
      let videoData = {
        title: item.title,
        videoUrl: item.videoUrl,
        audioUrl: item.audioUrl,
        qn: item.qn
      }
      axios.post(`/downloadVideo`,videoData).then(res => {
        this.$notify.info({
          title: '下载成功',
          message: res.data,
          duration: 3000
        })

        // 新增：下载成功后移除当前项
        const index = this.downloadUrls.findIndex(d => d.cid === item.cid);
        if (index !== -1) {
          this.downloadUrls.splice(index, 1);
        }
        this.removeSelectedPage(item.cid);
      }).catch(error => {
        this.$notify.error({
          title: '下载失败',
          message: error.message,
          duration: 3000
        });
      })
    },

    // 全部下载功能
    downloadAll() {
      if (this.downloadUrls.length === 0) {
        this.$notify.warning({
          title: '提示',
          message: '当前没有可下载的视频',
          duration: 3000
        });
        return;
      }

      this.$confirm('确认要下载所有视频吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.downloadUrls.forEach(item => {
          this.downVideo(item); // 调用单个下载方法
        });
        this.$notify.success({
          title: '操作成功',
          message: '已开始批量下载',
          duration: 3000
        });
      }).catch(() => {
        this.$notify.info({
          title: '已取消',
          message: '批量下载已取消',
          duration: 3000
        });
      });
    }

  }
}
</script>

<style scoped>
.download-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: calc(100vh - 40px);
  overflow: auto;
  margin: 20px 0;
}

.no-data {
  font-size: 20px;
  color: #999;
  margin-top: 20px;
}

.once {
  width: 70%;
}

.download-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 15px 0;
  padding: 12px;
  border-radius: 6px;
  background: #f8f8f8;
  width: 70%;
  height: 100px;

  .video-title {
    font-weight: 500;
    color: #333;
    margin: 10px;
  }

  .video-quality {
    display: flex;
    justify-content: center;

    .select-quality {
      width: 80%;
    }
  }

  .download-action {
    display: flex;
    justify-content: center;

  }
}
</style>