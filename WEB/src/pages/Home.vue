<template>
    <div>
        <h1>This is the home page</h1>
        <router-link to="/create">Create page</router-link>
        <div v-for="collection in collections" :key=collection.id>
            <v-btn @click="goToCollection(collection.id)">{{ collection.title }}</v-btn>
        </div>
    </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
    computed: {
        ...mapState([ 'collections' ])
    },
    methods: {
        //goToCollection pushes the router to the collection page with the given collection id
        goToCollection(id) {
            this.$router.push({ name: 'Collection', params: { id: id}})
        }
    },
}
</script>

<style>

</style>