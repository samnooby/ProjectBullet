import Vue from 'vue'
import Vuex from 'vuex'

//Tell vue to use the vuex plugin
Vue.use(Vuex)

var current_id = 0

class Collection {
    constructor(title, description, schema) {
        //Sets parameters
        this.id = current_id
        current_id++
        this.title = title
        this.description = description
        this.schema = []
        this.entries = []
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
    constructor(title, size, type) {
        this.id = current_id
        current_id ++
        this.title = title
        this.size = size
        this.type = type
    }
}

//The store that holds the application data
export default new Vuex.Store({
    state: {
        collections: [
            new Collection("Test", "Description", [ {id: 0, free_space: 0, columns: [ { id: 1, type: 'Text', size: 10, title: 'tester'}]} ])
        ]
    },
    mutations: {
        CREATE_COLLECTION(state, collection) {
            //Converts the object to an actual collection object and pushes to list
            var new_collection = new Collection(collection.title, collection.description, collection.schema)
            state.collections.push(new_collection)
        }
    },
    actions: {
        createCollection({ commit }, collection) {
            console.log("Creating collection")
            console.log(String(collection))
            //TODO: create an api
            commit('CREATE_COLLECTION', collection)
        }
    },
    getters: {
        //Gets the collection with the given id
        getCollectionById: (state) => (id) => {
            return state.collections.find(collection => collection.id === id)
        }
    }
})