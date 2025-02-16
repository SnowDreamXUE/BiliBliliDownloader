import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import store from './store'

Vue.config.productionTip = false
Vue.use(ElementUI)

import {Notification} from 'element-ui';

Vue.prototype.$notify = Notification;

import VueCookies from 'vue-cookies';

Vue.use(VueCookies);


new Vue({
    store,
    router,
    render: h => h(App),
}).$mount('#app')
