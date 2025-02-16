import Vue from 'vue'
import Vuex from 'vuex'
import video from "@/store/modules/video";
import userInfo from "@/store/modules/userInfo";
Vue.use(Vuex)

const store = new Vuex.Store({
    modules: {
        video,
        userInfo,
    }
})

export default store