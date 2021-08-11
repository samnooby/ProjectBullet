import Vue from 'vue'
import App from './App.vue'
import router from './plugins/router.js'
import store from './plugins/store.js'
import vuetify from './plugins/vuetify'

//A directive that sets the focus to itself when it is created
Vue.directive('focus', {
    // When the bound element is inserted into the DOM...
    inserted: function (el) {
      // Focus the element
      el.focus()
    }
  })

//Creates a new vue object and mounts the App object to the #app div
new Vue({
    router,
    store,
    vuetify,
    render: h => h(App)
}).$mount('#app')