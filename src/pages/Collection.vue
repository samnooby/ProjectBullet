<template>
  <v-container v-if="collection != null">
      <h1>{{ collection.title }} </h1>
      <h2>{{ collection.description }} </h2>
  </v-container>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
    computed: {
        ...mapGetters(['getCollectionById'])
    },
    data() {
        return {
            collection: null
        }
    },
    mounted() {
        var current_collection = this.getCollectionById(this.$route.params.id)
        if (current_collection == null) {
            this.$router.push('/')
        }
        this.collection = current_collection
    },
}
</script>

<style>

</style>