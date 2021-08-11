<template>
  <v-container>
      <v-row v-for="row in schema" :key="row.id">
          <v-col cols="1"></v-col>
          <v-col v-for="column in row.columns" :key="column.id" :cols="column.size">
              <v-text-field v-if="column.type=='Text'" :label="column.title" v-model="prototype[column.id]"></v-text-field>
              <v-checkbox v-else-if="column.type=='Checkbox'" v-model="prototype[column.id]"></v-checkbox>
          </v-col>
      </v-row>
      <v-row>
          <v-btn>Create Entry</v-btn>
      </v-row>
  </v-container>
</template>

<script>


export default {
    props: {
        schema: Array
    },
    data() {
        return {
            prototype: {}
        }
    },
}
</script>

<style>

</style>