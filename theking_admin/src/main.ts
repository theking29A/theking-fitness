import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'

import 'ant-design-vue/dist/reset.css'
import 'ant-design-vue/dist/antd.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
