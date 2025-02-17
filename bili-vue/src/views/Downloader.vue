<template>
  <div class="download-page">
    <div v-if="downloadUrls.length === 0" class="no-data">暂无数据</div>
    <div v-if="downloadUrls.length > 1" class="once">
      <el-button type="primary" round @click="downloadAll">全部下载</el-button>
    </div>
    <div v-for="item in downloadUrls" :key="item.cid" class="download-item">
      <div class="video-pic">
        <img :src="pic" alt="封面"/>
      </div>

      <div class="video-info">
        <div class="download-item-top">
          <div class="video-title">{{ item.title }}</div>
          <div class="download-action">
            <el-button
                type="primary"
                round size="mini"
                @click="downVideo(item)"
                :loading="item.status === 'downloading'"
                :disabled="item.status === 'downloading'"
            >
              {{ getButtonText(item) }}
            </el-button>
          </div>
        </div>
        <div class="video-select">
          <div class="audio-quality">
            <el-select
                class="select-quality"
                v-model="item.audioId"
                size="small"
                placeholder="选择音频码率"
                @change="selectDownloadFormat(item)"
                :disabled="item.status === 'downloading'"
            >
              <el-option
                  v-for="(value, key) in audioIds"
                  :key="key"
                  :label="value"
                  :value="key"
              ></el-option>
            </el-select>
          </div>

          <div class="video-quality">
            <el-select
                class="select-quality"
                v-model="item.videoKey"
                size="small"
                placeholder="选择清晰度"
                @change="selectDownloadFormat(item)"
                :disabled="item.status === 'downloading'"
            >
              <el-option
                  v-for="video in filteredVideos(item)"
                  :key="`${video.id}_${video.codecid}`"
                  :label="getQualityLabel(video)"
                  :value="`${video.id}_${video.codecid}`"
              ></el-option>
            </el-select>
          </div>
        </div>
        <div v-if="item.status === 'downloading'" class="progress-container">
          <div class="progress-info">
            {{ getProgressText(item) }}
          </div>
          <el-progress
              :percentage="progresses[item.cid]?.progress || 0"
              :status="getProgressType(item)"
          ></el-progress>
        </div>
      </div>
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
      pic: "",
      pages: [],          // 所有分集信息
      selectedPages: [],  // 选中的分集索引
      downloadUrls: [],   // 分集下载链接
      progresses: {}, // 存储结构：{ cid: 进度百分比 }
      audioIds: { // 音频码率
        '30280': "192K",
        '30216': "64K",
        '30232': "132K",
      },
      qualities: { // 视频清晰度
        '116': "1080P60",
        '112': "1080P+",
        '80': "1080P",
        '74': "720P60",
        '64': "720P",
        '32': "480P",
        '16': "360P",
      },
      codecNames: { // 视频编码格式
        7: 'AVC',
        12: 'HEVC',
      },
    }
  },

  mounted() {
    this.init();
    this.fetchDownloadUrls();
  },

  methods: {
    ...mapActions('video', ['removeSelectedPage']),

    init() {
      const videoData = this.$store.state.video;

      // 将 Vuex 中的数据赋值给组件的 data
      this.avid = videoData.avid || "";
      this.p = videoData.p || "";
      this.pic = videoData.bgUrl || "";
      this.pages = videoData.pages || [];
      this.selectedPages = videoData.selectedPages || [];
    },

    // 拆分出的获取下载链接方法
    async fetchDownloadUrls() {
      this.downloadUrls = [];

      try {
        // 获取所有选中分集的下载链接
        const promises = this.selectedPages.map(cid => {
          return this.getDownloadUrl(cid);
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
    async getDownloadUrl(cid) {
      try {
        const res = await axios.get(`/download/${this.avid}/${cid}`);
        // console.log(res.data);

        // 获取支持的清晰度列表并转换为字符串
        const supportedQualities = res.data.data.accept_quality.map(String);

        // 初始化视频流选择
        const firstVideo = res.data.data.dash.video[0];
        const item = {
          cid,
          videoData: res.data.data.dash.video,
          audioData: res.data.data.dash.audio,
          title: this.pages.find(p => p.cid === cid).part,
          supportedQualities,
          videoKey: `${firstVideo.id}_${firstVideo.codecid}`,
          audioId: String(res.data.data.dash.audio[0]?.id || ''),
          videoUrl: '',
          audioUrl: '',
        };
        this.downloadUrls.push(item);
        this.selectDownloadFormat(item);
      } catch (error) {
        this.$notify.error({
          title: '获取链接失败',
          message: `CID:${cid} 请求过程中发生错误`,
          duration: 3000
        });
      }
    },

    // 获取视频质量标签
    getQualityLabel(video) {
      const qualityName = this.qualities[video.id] || video.id;
      const codecName = this.codecNames[video.codecid] || `Codec ${video.codecid}`;
      return `${qualityName} ${codecName}`;
    },

    // 过滤支持的视频流
    filteredVideos(item) {
      return item.videoData.filter(video =>
          item.supportedQualities.includes(String(video.id))
      );
    },

    // 选择下载的格式
    selectDownloadFormat(item) {
      if (!item.videoKey) return;

      const [id, codecid] = item.videoKey.split('_');
      const videoObj = item.videoData.find(v =>
          String(v.id) === id && String(v.codecid) === codecid
      );

      if (videoObj) {
        item.videoUrl = videoObj.baseUrl;
      } else {
        this.$notify.error({
          title: '错误',
          message: '无法找到对应的视频流',
        });
      }

      // 处理音频部分（保持原逻辑）
      const audioObj = item.audioData.find(a => String(a.id) === item.audioId);
      item.audioUrl = audioObj?.baseUrl;
      if (!item.audioUrl) {
        this.$notify.error({
          title: '获取音频失败',
          message: `CID:${item.cid} 音频码率不可用`,
        });
      }
    },

    getButtonText(item) {
      if (item.status === 'downloading') {
        return '下载中';
      }
      return '下载';
    },

    getProgressText(item) {
      const progress = this.progresses[item.cid];
      if (!progress) return '';

      switch (progress.stage) {
        case 'VIDEO':
          return '下载视频流...';
        case 'AUDIO':
          return '下载音频流...';
        case 'MERGE':
          return '合并音视频...';
        case 'COMPLETE':
          return '下载完成';
        default:
          return '准备下载...';
      }
    },

    // 修改这个方法来返回正确的 status 值
    getProgressType(item) {
      const progress = this.progresses[item.cid];
      if (!progress) {
        return null; // undefined 也是有效的状态值
      }

      if (progress.stage === 'COMPLETE') {
        return 'success';
      }
      return null; // 返回 undefined 而不是空字符串
    },

    async downVideo(item) {
      // 设置下载状态
      this.$set(item, 'status', 'downloading');

      const videoData = {
        title: item.title,
        videoUrl: item.videoUrl,
        audioUrl: item.audioUrl,
        qn: item.qn
      };

      try {
        const response = await axios.post('/downloadVideo', videoData);
        if (response.status === 409) {// 文件已存在
          this.$notify.warning({
            title: '文件已存在',
            message: `%{item.title} 已经下载过，无需重复下载`,
          });
          this.removeDownloadItem(item.cid);
          return;
        }

        const taskId = response.data
        this.$set(this.progresses, item.cid, { progress: 0, stage: 'INIT' });

        const checkProgress = async () => {
          try {
            const { data: progressData } = await axios.get(`/task/${taskId}/progress`);

            this.$set(this.progresses, item.cid, {
              progress: progressData.progress,
              stage: progressData.stage
            });

            if (progressData.stage !== 'COMPLETE') {
              setTimeout(checkProgress, 1000);
            } else {
              this.$notify.success({
                title: '下载完成',
                message: `${item.title} 下载成功`
              });
              setTimeout(() => this.removeDownloadItem(item.cid), 2000);
            }
          } catch (error) {
            this.$notify.error({
              title: '下载失败',
              message: error.message
            });
            this.removeDownloadItem(item.cid);
          }
        };

        checkProgress();
      } catch (error) {
        if (error.response && error.response.status === 409) { // 特殊处理文件已存在的错误
          this.$notify.warning({
            title: '文件已存在',
            message: `${item.title} 已经下载过，无需重复下载`
          });
        } else {
          this.$notify.error({
            title: '下载失败',
            message: error.message
          });
        }
        this.removeDownloadItem(item.cid);
      }
    },

    removeDownloadItem(cid) {
      delete this.progresses[cid];
      const index = this.downloadUrls.findIndex(d => d.cid === cid);
      if (index !== -1) this.downloadUrls.splice(index, 1);
      this.removeSelectedPage(cid);
    },

    // 测试使用downVideo方法
    // downVideo(item) {
    //   console.log(item);
    // },

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

  },
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
  align-items: center;
  margin: 20px 0;
  padding: 12px;
  border-radius: 6px;
  border: #d27ed8 2px solid;
  width: 80%;
  height: 150px;

}

.video-info {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

.download-item-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 40px;
  margin-bottom: 10px;
}

.download-action {
  display: flex;
  justify-content: center;
  align-items: center;


}

.video-pic {
  display: flex;
  height: 100%;
  justify-items: center;
  margin-right: 20px;

  img {
    height: 100%;
    object-fit: cover;
    border-radius: 6px;
  }
}

.video-select {
  display: flex;
  justify-content: left;
  justify-items: center;
}

.select-quality {
  width: 120px;
  margin-right: 20px;
}

.progress-container {
  margin-top: 10px;
}
.progress-info {
  margin-bottom: 5px;
  font-size: 12px;
  color: #606266;
}
</style>