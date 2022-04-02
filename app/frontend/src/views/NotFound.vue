<template>
  <div class="notfound">
    <v-row justify="center">
      <v-col cols="8" sm="6">
        <div class="text-h2 text-center font-weight-bold">404</div>
        <div class="text-h5 text-center">Page not found</div>
        <div class="text-center">
          Sorry, the page you are looking for does not exist. <br />
          Checkout this dank meme or go back to the homepage:
        </div>
      </v-col>
    </v-row>
    <v-row justify="center">
      <v-btn to="/" color="foa_button" class="foa_button_fg--text">Back to Home</v-btn>
    </v-row>
    <v-row>
      <v-img class="mt-4" contain :src="memeSrc" height="400"></v-img>
    </v-row>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'NotFound',
  data: () => ({
    memeSrc: '',
    memeLinks: [],
  }),
  created() {
    axios.get('https://meme-api.herokuapp.com/gimme').then((response) => {
      this.memeLinks = response.data.preview;
      this.memeSrc = this.memeLinks[this.memeLinks.length - 1];
    });
  },
};
</script>
