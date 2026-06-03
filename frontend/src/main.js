import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import './styles/global.css'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)

// 默认 CSS 变量
const style = document.createElement('style')
style.textContent = `:root{--brand-start:#1a2639;--brand-mid:#3d5a80;--brand-end:#7fa1c4;}`
document.head.appendChild(style)

app.use(router)
app.mount('#app')
