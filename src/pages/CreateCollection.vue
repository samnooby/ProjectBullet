<template>
  <v-form>
    <v-container>
      <v-row>
        <v-col cols="12" sm="6" md="3">
          <v-text-field label="Collection Name"></v-text-field>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12">
          <h3>Template</h3>
          <TemplateCreator />
        </v-col>
      </v-row>
    </v-container>
  </v-form>
</template>

<script>
import TemplateCreator from '../components/TemplateCreator.vue'

export default {
    components: { TemplateCreator },
  data() {
    return {
      
    };
  },
  
};
</script>

<style lang="scss">
h3 {
    text-decoration: underline;
}
</style>