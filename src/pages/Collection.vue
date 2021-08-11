<template>
  <v-container v-if="collection != null">
    <v-row>
      <v-col cols="12">
        <h1>{{ collection.title }}</h1>
        <h2>{{ collection.description }}</h2>
      </v-col>
      <v-col cols="12" id="add-entry-label">
          <h3>Add entry</h3>
      </v-col>
      <v-col cols="12">
        <CreateEntry v-bind:schema="collection.schema" />
      </v-col>
    </v-row>
    <v-row v-for="entry in collection.entries" :key="entry.id">
      <Entry v-bind:schema="collection.schema" v-bind:entry="entry" />
    </v-row>
  </v-container>
</template>

<script>
import CreateEntry from "../components/CreateEntry.vue";
import Entry from "../components/Entry.vue";
import { mapGetters } from "vuex";

export default {
  components: { CreateEntry, Entry },
  computed: {
    ...mapGetters(["getCollectionById"]),
  },
  data() {
    return {
      collection: null,
    };
  },
  mounted() {
    var current_collection = this.getCollectionById(this.$route.params.id);
    if (current_collection == null) {
      this.$router.push("/");
    }
    this.collection = current_collection;
  },
};
</script>

<style>
</style>