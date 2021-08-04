import Vue from 'vue'
import App from './App.vue'

//Creates a new vue object and mounts the App object to the #app div
new Vue({
    render: h => h(App)
}).$mount('#app')