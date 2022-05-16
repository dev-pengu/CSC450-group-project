<template>
  <div class="notfound">
    <v-row justify="center" align="center">
      <v-col cols="12" sm="4">
        <div class="text-h1 text-center font-weight-bold">404</div>
        <div class="text-h4 text-center">Page not found</div>
        <div class="text-center mt-2">
          Sorry, the page you are looking for does not exist. <br />
          Checkout this meme or go back to the homepage:
        </div>
        <div class="text-center mt-3">
          <v-btn to="/dashboard" color="foa_button" class="foa_button_text--text">Back to Home</v-btn>
        </div>
      </v-col>
      <v-col cols="12" sm="4">
        <v-img contain :src="require(`../assets/meme-${memeNum}.jpeg`)" height="500"></v-img>
      </v-col>
    </v-row>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  data: () => ({
    memeNum: 0,
  }),
  created() {
    this.memeNum = Math.floor(Math.random() * 11);
  },
};
</script>
