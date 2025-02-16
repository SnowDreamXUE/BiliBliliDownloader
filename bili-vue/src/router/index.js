import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "@/views/Home.vue";
import Login from "@/views/Login.vue";
import Downloader from "@/views/Downloader.vue";
Vue.use(VueRouter)

const routes = [
    {
        path: "/",
        name: "Home",
        component: Home
    },
    {
        path: "/login",
        name: "Login",
        component: Login
    },
    {
        path: "/downloader",
        name: "Download",
        component: Downloader
    }
]

const router = new VueRouter({
    routes
})

export default router