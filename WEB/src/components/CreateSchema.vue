<template>
  <v-container>
    <v-row>
      <v-col cols="12" id="title">
        <h2>Schema</h2>
      </v-col>
    </v-row>
    <v-row class="row mb-1" v-for="row in rows" :key="row.id">
      <!-- <v-col cols="1"></v-col> -->
      <v-col
        class="column"
        v-for="column in row.columns"
        :key="column.id"
        :cols="column.size"
        @click="editColumn(row, column)"
      >
        {{ column.title }}
      </v-col>
      <v-col class="add" v-if="row.free_space > 0" :cols="row.free_space">
        <v-btn id="create-column" icon @click="createColumn(row.id)">
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col class="add" cols="12">
        <v-btn id="create-row" icon @click="createRow">
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <v-dialog v-model="dialog">
      <v-card>
        <v-card-title>Edit Column</v-card-title>
        <v-card-text>
          <v-row v-if="current_column && current_row">
            <v-col sm="6" cols="12">
              <v-text-field v-model="current_column.title"></v-text-field>
            </v-col>
            <v-col sm="6" cols="12">
              <v-select
                label="Type"
                :items="column_types"
                v-model="current_column.type"
              >
              </v-select>
            </v-col>
            <v-col cols="12">
              <v-slider
                min="1"
                v-model="current_column.size"
                :max="old_size + current_row.free_space"
                ticks="always"
                thumb-label="always"
              ></v-slider>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
            <v-btn @click="dialog = false">Done</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
const DEFAULT_ROW_SIZE = 12;

export default {
  data() {
    return {
      column_types: ["Text", "Checkbox", "Dropdown", "Blank"],
      dialog: false,
      current_id: 0,
      num_columns: 0,
      //The rows that will be used to define the schema
      rows: [],
      //Used to edit the columns in the schema
      current_column: null,
      current_row: null,
      old_size: 0,
    };
  },
  methods: {
    //createRow creates a new row with default values and adds it to the list of rows
    createRow() {
      var new_row = {
        id: this.current_id,
        free_space: DEFAULT_ROW_SIZE,
        columns: [],
      };
      this.current_id++;
      this.rows.push(new_row);
    },
    //createColumn creates a new column in the row with the given id
    createColumn(row_id) {
      //Finds row and creates column
      var row = this.rows.find((v) => v.id == row_id);
      var new_column = {
        id: this.current_id,
        type: "Text",
        size: row.free_space,
        title: "column",
      };
      this.current_id++;
      this.num_columns ++
      //Updates row and adds column
      row.free_space = 0;
      row.columns.push(new_column);
    },
    editColumn(row, column) {
      //Set the current row and column to be edited
      this.current_row = row;
      this.current_column = column;
      //Set the old size and open the dialog
      this.old_size = column.size;
      this.dialog = true;
    },
    createCollection() {
        console.log("Creating collection: ")
    }
  },
  watch: {
    dialog: function (newValue) {
      if (newValue == false) {
        //Find the new size by re adding the old size and subtracting the new size
        this.current_row.free_space =
          this.current_row.free_space +
          this.old_size -
          this.current_column.size;
        //Gets rid of the title if it is a blank type
        if (this.current_column.type == "Blank") {
          this.current_column.title = "";
        }
        //Reset the current row and column
        this.current_column = null;
        this.current_row = null;
        this.old_size = 0;
      }
    },
  },
};
</script>

<style scoped lang="scss">
#title {
  display: flex;
  justify-content: center;
}

.column {
  border: 1px solid black;
  padding: 0;
  height: 30px;
}

.add {
  display: flex;
  justify-content: center;
  align-content: center;
}

#create-row {
  background-color: lightgray;
}

#create-column {
  background-color: lightsteelblue;
}

#create-actions {
    display: flex;
    justify-content: center;
}
</style>