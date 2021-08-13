<template>
  <v-container class="ma-0 pa-0">
    <v-row class="ma-0" v-for="row in schema" :key="row.id">
      <!-- <v-col cols="1"></v-col> -->
      <v-col
        class="ma-0 pa-0"
        v-for="column in row.columns"
        :key="column.id"
        :cols="column.size"
      >
        <div class="column-div">
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <p class="ma-0 pa-0" v-bind="attrs" v-on="on">{{ column.title }}</p>
            </template>
            <span>{{ column.title }}</span>
          </v-tooltip>

          <v-text-field
            v-if="column.type == 'Text'"
            hide-details
            class="text-column ma-0 pa-0"
            v-model="prototype[column.id]"
          ></v-text-field>
          <v-checkbox
            v-else-if="column.type == 'Checkbox'"
            class="ma-0 pa-0"
            v-model="prototype[column.id]"
          ></v-checkbox>
          <v-overflow-btn
            v-else-if="column.type == 'Dropdown'"
            dense
            hide-details
            class="ma-0 pa-0 overflow-btn"
            v-model="prototype[column.id]"
            :items="column.items"
          ></v-overflow-btn>
        </div>
      </v-col>
    </v-row>
    <v-row>
      <v-btn @click="createEntry">Create Entry</v-btn>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: {
    schema: Array,
  },
  data() {
    return {
      prototype: {},
    };
  },
  methods: {
    createEntry() {
      this.$emit('createCollectionEntry', this.prototype)
      this.prototype = {}
    }
  },
};
</script>

<style lang="scss">
.text-column {
  input {
    margin: 0;
    padding: 0;
  }
}

.column-div {
  padding-left: 10px;
  p {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>