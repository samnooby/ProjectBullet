<template>
  <v-container>
    <v-row>
      <v-col cols="12" id="title">
        <h1>Create a collection</h1>
      </v-col>
    </v-row>
    <v-row>
      <v-col sm="6" xs="12">
        <div class="textbox">
          <p>Collection Title</p>
          <v-text-field
            :rules="rules.title_length"
            counter
            placeholder="Title"
            v-model="title"
          ></v-text-field>
        </div>
      </v-col>
      <v-col sm="6" xs="12">
        <div class="textbox">
          <p>Description</p>
          <v-textarea
            :rules="rules.description_length"
            counter
            rows="1"
            auto-grow
            placeholder="Title"
            v-model="description"
          ></v-textarea>
        </div>
      </v-col>
    </v-row>
    <CreateSchema ref="schema_creator" />
    <v-row>
      <v-col cols="12" id="create-collection">
        <v-btn @click="createCollection" large color="success"
          >Create Collection</v-btn
        >
      </v-col>
    </v-row>
    <v-dialog v-model="dialog">
      <v-card>
        <v-card-title>Error!</v-card-title>
        <v-card-text>{{ error }}</v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import CreateSchema from "../components/CreateSchema.vue";

export default {
  components: {
    CreateSchema,
  },
  data() {
    return {
      dialog: false,
      error: "",
      title: "",
      description: "",
      rules: {
        title_length: [
          (v) => (v != null && v.length > 0) || "Title can not be blank",
          (v) => v == null || v.length <= 32 || "Max 32 characters",
        ],
        description_length: [
          (v) => v == null || v.length <= 128 || "Max 128 characters",
        ],
      },
    };
  },
  methods: {
    createCollection() {
      //Goes through the schema and everywhere there is blank space it creates a blank column
      var rows = this.$refs.schema_creator.rows;
      //Make sure there is a title and a schema
      if (this.title.length == 0) {
        this.error = "Title can not be blank";
        this.dialog = true;
      } else if (rows.length == 0 || this.$refs.schema_creator.num_columns == 0) {
        this.error = "Collection must have a schema";
        this.dialog = true;
      } else {
        //TODO: CREATE THE COLLECTION IN VUEX
        this.$store.dispatch('createCollection', { title: this.title, description: this.description, schema: rows})
        this.$router.push({ name: "Home" });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
#title {
  display: flex;
  justify-content: center;
}

#create-collection {
  display: flex;
  justify-content: center;
}
</style>