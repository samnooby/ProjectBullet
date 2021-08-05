<template>
    <v-card>
        <v-card-title>Edit Column</v-card-title>
        <v-form>
            <v-text-field label="Collection Name" v-model="new_name"></v-text-field>
            <v-slider ticks thumb-label="always" min="1" :max="12 + size - total_size" v-model="new_size"></v-slider>
        </v-form>
        <v-card-actions>
            <v-btn>Confirm changes</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
export default {
    props: {
        name: {
            type: String,
            required: true
        },
        size: {
            type: Number,
            required: true
        },
        total_size: {
            type: Number,
            required: true
        },
        row_id: {
            type: Number,
            required: true
        }
    },
    data() {
        return {
            new_name: this.name,
            new_size: this.size
        }
    },
    watch: {
        name: function(val) {
            this.new_name = val
        },
        size: function(val) {
            this.new_size = val
        }
    }
}
</script>

<style>

</style>