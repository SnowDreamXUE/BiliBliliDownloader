<template>
  <div id="app">
    <el-container>
      <el-aside>
        <el-menu
            :default-active="this.$route.path" router
            class="nav-side">
          <div class="avatar">
            <!-- 展示头像 -->
            <img :src="face" alt="" v-if="face" />
          </div>
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/downloader">下载</el-menu-item>
          <el-menu-item index="/login">登录</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import axios from "axios";
import {mapActions} from "vuex";

axios.defaults.baseURL = "http://localhost:8989"

export default {
  name: 'App',
  data() {
    return {
      uname: '', // 用户名
      face: '', // 头像
    }
  },
  mounted() {
    this.getUserInfo();
  },
  methods: {
    ...mapActions('userInfo', ['setData']),

    // 查询登录用户信息
    async getUserInfo() {
      if (this.$store.state.userInfo.uname) {
        this.uname = this.$store.state.userInfo.uname
        this.face = this.$store.state.userInfo.face
        return
      }
      axios.get("/user_info").then(res => {
        // console.log(res.data)
        if (res.data.data.isLogin) {
          this.uname = res.data.data.uname
          this.face = res.data.data.face

          // 代理加载头像
          this.face = `/proxy-image?url=${(this.face)}`;

          this.setData({
            uname: this.uname,
            face: this.face,
          })

          this.$notify.success({
            title: "已登录",
            message: "你好，" + this.uname + "!",
            duration: 3000
          })
        } else {
          this.$notify.error({
            title: "未登录",
            message: "请先登录，获取最高清晰度!",
            duration: 3000
          })
        }
      })
    },

  },
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.el-main {
  padding: 0;
  margin: 0;
}

.el-aside {
  width: 100px !important;
}

.nav-side {
  height: 100vh;
  width: 100px;
}

.el-menu-item {
  text-align: center;
}
.avatar {
  width: 100%;
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  border-bottom: #e6e6e6 1px solid;
}
.avatar img {
  height: 80%;
  object-fit: cover; /* 确保图片按比例缩放并覆盖容器 */
  border-radius: 50%; /* 可选：设置圆形头像 */
}
</style>