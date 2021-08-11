import Vue from 'vue'
import Vuex from 'vuex'

//Tell vue to use the vuex plugin
Vue.use(Vuex)

var current_id = 0

class Collection {
    constructor(title, description, schema) {
        //Sets parameters
        this.title = title
        this.description = description
        this.schema = []
        //Converts object into row class
        for (var i = 0; i < schema.length; i ++) {
            this.schema.push(new Row(schema[i].free_space, schema[i].columns))
        }
    }
}

class Row {
    constructor(free_space, columns) {
        this.free_space = free_space
        this.columns = []
        //Converts the columns into their correct column class
        for (var i = 0; i < columns.length; i ++) {
            this.columns.push(new Column(columns[i].title, columns[i].size, columns[i].type))
        }
    }
}

//This is an abstract class, Do not use
class Column {
    constructor(title, size, type) {
        this.title = title
        this.size = size
        this.type = type
    }
}

//The store that holds the application data
export default new Vuex.Store({
    state: {
        collections: []
    },
    mutations: {
        CREATE_COLLECTION (state, collection) {
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
    getters: {}
})