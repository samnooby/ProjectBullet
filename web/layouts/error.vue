<template>
    <div>
        <h1 v-if="error.statusCode == 404">Page not found</h1>
        <h1 v-else>Something went wrong</h1>
        <NuxtLink to="/">Back Home</NuxtLink>
    </div>
</template>

<script>
export default {
    props: ['error'],
    layout: 'error'
}
</script>

<style>

</style>