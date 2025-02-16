<template>
  <div class="login">
    <el-card>
      <el-button type="primary" @click="getUserInfo">查询是否登录</el-button>
      <el-button type="primary" @click="login_url()">生成登录二维码</el-button>
      <el-button type="primary" @click="stopCheck()">终止轮询</el-button>
    </el-card>
    <canvas id="qrcode" v-if="showQrcode"></canvas>
  </div>
</template>

<script>
import QRCode from "qrcode";
import axios from "axios";

axios.defaults.baseURL = "http://localhost:8989"

export default {
  name: 'Login',
  data() {
    return {
      showQrcode: false, //控制二维码展示
      login_image_url: "",// 登录二维码
      qrcode_key: "",// 登录二维码key
      pollingIntervalId: null, // 轮询 ID
    }
  },
  methods: {

    // 生成登录二维码
    login_url() {
      axios.get("/login_url").then(res => {
        if (res.status === 200) {
          this.login_image_url = res.data.data.url;
          this.qrcode_key = res.data.data.qrcode_key;

          // 先设置 showQrcode 为 true，确保 <canvas> 渲染
          this.showQrcode = true;

          // 使用 $nextTick 确保 DOM 已更新后再生成二维码
          this.$nextTick(() => {
            QRCode.toCanvas(document.getElementById("qrcode"), this.login_image_url, { width: 200 }, (error) => {
              if (error) {
                console.error("生成二维码失败:", error);
              }
            });
          });

          this.pollLoginStatus();
        }
      }).catch((error) => {
        console.error("获取登录二维码失败:", error);
      });
    },

    // 轮询登录状态
    pollLoginStatus() {
      const POLL_INTERVAL = 6000; // 轮询间隔时间，单位为毫秒
      const MAX_POLL_TIME = 180000; // 最大轮询时间，单位为毫秒（180秒）
      const qrcode_key = this.qrcode_key;

      function login_check() {
        axios.get("/login_check", {
          params: {
            qrcode_key: qrcode_key
          }
        }).then(response => {
          const data = response.data.data;

          if (data.code === 0) {
            // 登录成功
            // console.log('登录成功:', data);
            this.$notify.success({
              title: '登录成功',
              message: '欢迎',
              duration: 3000
            });
            clearInterval(this.pollingIntervalId); // 停止轮询
          } else if (data.code === 86038) {
            // 二维码已失效
            console.error('二维码已失效');
            this.$notify.error({
              title: '二维码已失效',
              message: '请重新生成二维码',
              duration: 3000
            });
            clearInterval(this.pollingIntervalId); // 停止轮询
          } else if (data.code === 86090 || data.code === 86101) {
            // 继续轮询
            // console.log('等待二维码扫描...');
          }
        })
            .catch(error => {
              // console.error('获取登录状态失败:', error);
              this.$notify.error({
                title: '获取登录状态失败',
                message: error.message,
                duration: 3000
              });
              clearInterval(this.pollingIntervalId); // 遇到错误时停止轮询
            });
      }

      this.pollingIntervalId = setInterval(login_check, POLL_INTERVAL);

      // 设置最大轮询时间
      setTimeout(() => {
        clearInterval(this.pollingIntervalId);
        this.$notify.error({
          title: '轮询超时',
          message: '二维码可能已过期',
          duration: 3000
        })
      }, MAX_POLL_TIME);
    },

    //终止轮询
    stopCheck() {
      if (this.pollingIntervalId) {
        clearInterval(this.pollingIntervalId); // 清除轮询
        this.pollingIntervalId = null; // 清空轮询 ID
        this.showQrcode = false; // 隐藏二维码
      } else {

        this.$notify.warning({
          title: '没有正在运行的轮询',
          message: '请先生成登录二维码',
          duration: 3000
        });
      }
    },

    // 查询登录用户信息
    getUserInfo() {
      axios.get("/user_info").then(res => {
        // console.log(res.data)
        if(res.data.data.isLogin) {
          this.$notify.success({
            title: "已登录",
            message: "你好，" + res.data.data.uname + "!"
          })
        }else {
          this.$notify.error({
            title: "未登录",
            message: "请先登录，获取最高清晰度!"
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.login {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 200px;
}

.el-card {
  width: 600px;
  margin-bottom: 20px;

  ::v-deep(.el-card__body) {
    display: flex;
    justify-content: space-around;
    align-items: center;
  }
}

</style>