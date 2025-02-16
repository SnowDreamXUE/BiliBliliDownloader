<template>
  <div id="home">
    <div class="logo">
      <img src="@/assets/BILIBILI_LOGO.svg" draggable="false" alt="logo"/>
    </div>

    <div class="input_search">
      <input type="text" placeholder="请输入视频链接..." v-model.trim="videoUrl"/>
      <button @click="download">获取</button>
    </div>

    <div class="use-info">
      <el-collapse :accordion="true" v-model="activeNames">
        <!-- 第一步：分集选择 -->
        <el-collapse-item v-show="pages.length > 0" title="选择分集" name="分集">
          <div class="step-content">
            <el-checkbox-group v-model="selectedPages">
              <el-checkbox
                  v-for="(page, index) in pages"
                  :key="index"
                  :label="page.cid"
                  class="page-checkbox"
              >
                {{ page.part }} (P{{ index + 1 }})
              </el-checkbox>
            </el-checkbox-group>

            <div class="step-actions">
              <el-button
                  class="next-step"
                  @click="handleNextStep"
                  :disabled="selectedPages.length === 0"
              >
                下一步
              </el-button>
            </div>
          </div>
        </el-collapse-item>

      </el-collapse>
    </div>
  </div>
</template>

<script>
import axios from "axios"

axios.defaults.baseURL = "http://localhost:8989"
import {mapActions} from "vuex"
import router from "@/router";

export default {
  mounted() {
    this.videoUrl = this.$store.state.videoUrl || "";
  },

  data() {
    return {
      activeNames: "",
      videoUrl: "",
      bgUrl: "",
      videoName: "",
      bv: "",
      avid: "",
      p: "",
      pages: [],          // 所有分集信息
      selectedPages: [],  // 选中的分集索引
    };
  },

  methods: {
    ...mapActions(['saveData', 'setVideoUrl']),

    // 新增步骤控制方法
    handleNextStep() {
      if (this.selectedPages.length === 0) {
        this.$message.warning("请至少选择一个分集");
        return;
      }

      this.saveData({
        bgUrl: this.imgUrl(),
        avid: this.avid,
        p: this.p,
        pages: this.pages,
        selectedPages: this.selectedPages
      })
      router.push({name: "Download"})
    },


    // 输入变化时重置
    change() {
      this.setVideoUrl(this.videoUrl)
      this.pages = []
      this.selectedPages = []
      this.bgUrl = ""
    },

    // 主下载流程
    // 修改后的下载流程
    async download() {

      this.change();

      try {
        await this.parseUrl();
        if (this.bv) {
          await this.getAvidCidByBv();
          // 自动展开分集选择
          this.$nextTick(() => {
            this.activeNames = '分集';
          });
        }
      } catch (error) {
        this.$notify.error({
          title: '解析链接失败',
          message: error.message,
          duration: 3000
        });
      }
    },

    // 解析URL
    parseUrl() {
      return new Promise((resolve) => {
        if (this.videoUrl.includes("?p=")) {
          this.p = this.videoUrl.split("?p=")[1].split("&")[0]
          this.bv = this.videoUrl.split("/video/")[1].split("?p=")[0]
          resolve()
        } else if (this.videoUrl.includes("https://b23.tv")) {
          axios.get("/move?url=" + this.videoUrl).then(resp => {
            this.videoUrl = resp.data.split(`<meta data-vue-meta="true" itemprop="url" content="`)[1].split(`/">`)[0]
            this.bv = this.videoUrl.split("/video/")[1].split("/?")[0]
            resolve()
          })
        } else {
          this.bv = this.videoUrl.split("/video/")[1].split("/?")[0]
          resolve()
        }
      })
    },

    // 获取视频基本信息
    getAvidCidByBv() {
      return new Promise((resolve) => {
        axios.get(`/av/${this.bv}`).then(resp => {
          // console.log(resp.data)
          this.videoName = resp.data.data.title
          this.avid = resp.data.data.aid
          this.bgUrl = resp.data.data.pic
          this.pages = resp.data.data.pages
          this.selectedPages = this.pages.map(page => page.cid);
          resolve()
        })
      })
    },
    // 获取视频封面
    imgUrl() {
      return this.bgUrl ? "https" + this.bgUrl.substring(4) : ""
    }
  },
};
</script>

<style lang="less" scoped>

.step-content {
  padding: 16px;
}

.step-actions {
  margin-top: 24px;
  text-align: right;
}

.page-checkbox {
  display: block;
  margin: 8px 0;

  .el-checkbox__label {
    font-size: 14px;
    color: #666;
  }
}

#home {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-direction: column;
  height: calc(100vh - 20px);

  .logo {
    width: 35%;
    height: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 50px;
    img {
      width: 30%;
      user-select: none;
    }
  }

  .input_search {
    width: 45%;
    margin-top: 50px;
    display: flex;
    align-items: center;
    justify-content: center;

    input {
      width: 85%;
      height: 45px;
      padding-right: 20px;
      border: 1px solid #888;
      border-right: none;
      border-radius: 7px 0 0 7px;
      font-size: 16px;
      color: #363636;
      outline: none;
      text-indent: 0.7em;

      &::placeholder {
        color: #979797;
        font-size: 14px;
      }
    }

    button {
      width: 15%;
      height: 45px;
      border: none;
      background-color: #1296db;
      border-radius: 0 7px 7px 0;
      font-size: 14px;
      cursor: pointer;
      position: relative;
      letter-spacing: 0.1em;
      color: #fff;
      padding: 2px 4px;
      outline: none;
      user-select: none;
    }
  }

  .use-info {
    width: 44.5%;
    user-select: none;
  }

  .van-cell::after {
    border: none;
  }
}
</style>
