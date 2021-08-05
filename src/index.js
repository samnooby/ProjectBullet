import Vue from 'vue'
import App from './App.vue'
import router from './plugins/router.js'
import store from './plugins/store.js'
import vuetify from './plugins/vuetify'

//Creates a new vue object and mounts the App object to the #app div
new Vue({
    router,
    store,
    vuetify,
    render: h => h(App)
}).$mount('#app')