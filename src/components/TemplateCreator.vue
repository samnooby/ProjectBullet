<template>
  <v-container>
    <v-row v-for="row in rows" :key="row.id">
      <v-col
        v-for="column in row.columns"
        :key="column.name"
        :cols="column.size"
        class="columns"
        @click="editDialog(row.id, column.name)"
      >
        {{ column.name }}
      </v-col>
      <v-col
        v-if="row.total_size < 12"
        :cols="12 - row.total_size"
        id="createColumn"
      >
        <v-btn class="button" @click="createColumn(row.id)" icon>
          <v-icon> mdi-plus </v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" id="createRow">
        <v-btn icon class="button" @click="createRow">
          <v-icon> mdi-plus </v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <v-dialog v-model="show_dialog">
      <ColumnEditor v-bind="editing_column" />
    </v-dialog>
  </v-container>
</template>

<script>
import ColumnEditor from "./ColumnEditor.vue";

export default {
  components: { ColumnEditor },
  data() {
    return {
      show_dialog: false,
      rows: [
        {
          id: 0,
          total_size: 9,
          columns: [
            { name: "bullet", size: 3 },
            { name: "text", size: 6 },
          ],
        },
      ],
      editing_column: {
          name: "",
          size: 0,
          total_size: 0,
          row_id: 0
      }
    }
  },
  methods: {
    //Creates a new column in the row with the given id
    createColumn(row_id) {
      //Finds the row
      var row = this.rows.find((item) => item.id == row_id);
      //Creates a new column with the remaining size and changes the total size
      row.columns.push({ name: "New Column", size: 12 - row.total_size });
      row.total_size = 12;
    },
    //Creates a new row below the current row
    createRow() {
      //Creates a new row where the id is the position of the row in the list
      this.rows.push({
        id: this.rows.length + 1,
        total_size: 12,
        columns: [{ name: "New Column", size: 12 }],
      });
    },
    //Opens the dialog and sets the correct row and column in the data
    editDialog(row_id, column_name) {
        //Find the column
        var row = this.rows.find(item => item.id == row_id)
        this.editing_column = row.columns.find(item => item.name == column_name)
        //Add row info
        this.editing_column.total_size = row.total_size
        this.editing_column.row_id = row.id
        //Show the dialog
        this.show_dialog = true
    }
  },
};
</script>

<style lang="scss">
.columns {
  border: 1px black solid;
}

#createColumn {
  display: flex;
  justify-content: center;
  align-content: center;
  .button {
    background-color: lightgrey;
  }
}

#createRow {
  display: flex;
  justify-content: center;
  .button {
    background-color: lightgrey;
  }
}
</style>