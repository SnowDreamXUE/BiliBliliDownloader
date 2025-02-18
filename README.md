## BiliBiliDownloader
Vue2 + SpringBoot搭建的B站视频下载器
## 功能
- 扫码登录B站（cookies保存）
- 音频下载（可选码率）
- 视频下载（可选清晰度）
- 封面下载
- 支持多P下载
## 使用
- resource目录下有`gyan.dev`编译的ffmpeg-7.1-essentials_build中的ffmpeg.exe
- 登录后cookies保存在项目根目录下的cookies文件夹中
- 下载的视频保存在项目根目录下的download文件夹中
- 下载、cookies、ffmpeg路径可在application.properties配置
## 注意
- 本项目仅供学习交流使用，请勿用于商业用途
- 不承担任何法律责任