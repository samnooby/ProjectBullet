<template>
  <v-app>
      <h1 @click="$router.push({ name: 'Home' })">Welcome to project bullet</h1>
      <router-view></router-view>
  </v-app>
</template>

<script>
export default {

}
</script>

<style scoped lang="scss">

h1 {
  cursor: pointer;
}
</style>