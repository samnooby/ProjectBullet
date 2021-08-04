import VueRouter from 'vue-router'
import Vue from 'vue'

//Tell the vue frameword to use the router
Vue.use(VueRouter)

//The router object storing paths and settings
export default new VueRouter({
    routes: [
        {path: '/', name: 'Home', component: () => import(/* webpackChunkName: "home" */ './pages/Home.vue')},
        {path: '/create', name: 'CreateCollection', component: () => import(/* webpackChunkName: "createcollection" */ './pages/CreateCollection.vue')},
        {path: '/404', name: 'NotFound', component: () => import(/* webpackChunkName: "notfound" */ './pages/NotFound.vue')},
        {path: '/*', redirect: { name: 'NotFound' }}
    ]
})