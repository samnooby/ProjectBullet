import Vue from 'vue'
import Vuex from 'vuex'

//Tell vue to use the vuex plugin
Vue.use(Vuex)

var current_id = 0
const COLUMN_TYPES = ['Text', 'Blank', 'Checkbox', 'Dropdown']

class Collection {
    constructor(title, description, schema) {
        //Sets parameters
        this.id = current_id
        current_id++
        this.title = title
        this.description = description
        this.schema = []
        this.entries = [
            { 2: true, 3: "Test", 6: "three", 7: "Tester"}
        ]
        //Converts object into row class
        for (var i = 0; i < schema.length; i++) {
            this.schema.push(new Row(schema[i].free_space, schema[i].columns))
        }
    }
}

class Row {
    constructor(free_space, columns) {
        this.id = current_id
        current_id ++
        this.free_space = free_space
        this.columns = []
        //Converts the columns into their correct column class
        for (var i = 0; i < columns.length; i++) {
            this.columns.push(new Column(columns[i].title, columns[i].size, columns[i].type))
        }
    }
}

//This is an abstract class, Do not use
class Column {
    constructor(title, size, type, is_required=false) {
        this.id = current_id
        current_id ++
        this.title = title
        this.size = size
        this.type = type
        this.is_required = is_required
        this.items = ['one', 'two', 'three']
    }
}

//The store that holds the application data
export default new Vuex.Store({
    state: {
        collections: [
            new Collection("Test", "Description", [ 
            { free_space: 0, columns: [ { type: 'Checkbox', size: 2, title: 'test'}, { type: 'Text', size: 10, title: 'tester'}]},
            { free_space: 0, columns: [ { type: 'Blank', size: 2, title: ''}, { type: 'Dropdown', size: 2, title: 'NoteType'}, { type: 'Text', size: 8, title: 'Note'}]}
        ])
        ],
        user: {}
    },
    mutations: {
        CREATE_COLLECTION(state, collection) {
            //Converts the object to an actual collection object and pushes to list
            var new_collection = new Collection(collection.title, collection.description, collection.schema)
            state.collections.push(new_collection)
        },
        CREATE_ENTRY(state, entry_info) {
            //Finds the collection and adds the entry to the collection
            var collection = state.collections.find(collection => collection.id === entry_info.collection_id)
            let new_entry = Object.assign({}, entry_info.entry)
            collection.entries.push(new_entry)
        }
    },
    actions: {
        createCollection({ commit }, collection) {
            console.log("Creating collection")
            console.log(String(collection))
            //TODO: create an api
            commit('CREATE_COLLECTION', collection)
        },
        createEntry({ commit }, entry_info) {
            commit('CREATE_ENTRY', entry_info)
        }
    },
    getters: {
        //Gets the collection with the given id
        getCollectionById: (state) => (id) => {
            return state.collections.find(collection => collection.id === id)
        }
    }
})