<template>
  <v-container>
      <v-row class="ma-0 pa-0" v-for="row in schema" :key="row.id">
          <v-col class="ma-0 pa-0 column" v-for="column in row.columns" :key="column.id" :cols="column.size">
              <v-text-field
            v-if="column.type == 'Text'"
            hide-details
            class="text-column ma-0 pa-0"
            v-model="entry[column.id]"
          ></v-text-field>
          <v-checkbox
            v-else-if="column.type == 'Checkbox'"
            class="ma-0 pa-0"
            v-model="entry[column.id]"
            hide-details
          ></v-checkbox>
          <v-overflow-btn
            v-else-if="column.type == 'Dropdown'"
            dense
            hide-details
            class="ma-0 pa-0 overflow-btn"
            v-model="entry[column.id]"
            :items="column.items"
          ></v-overflow-btn>
          </v-col>
      </v-row>
  </v-container>
</template>

<script>
export default {
    props: {
        schema: Array,
        entry: Object
    },
}
</script>

<style lang="scss" scoped>
</style>