import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';

Vue.use(Vuetify);

export default new Vuetify({
  theme: {
    themes: {
      light: {
        foa_content_bg: '#CCEFDA',
        foa_text: '#616161',
        foa_text_header: '#004E49',
        foa_highlight: '#D9F1F1',
        foa_link: '#009076',
        foa_nav_bg: '#9CD8D4',
        foa_nav_link_active: '#004E49',
        foa_nav_link: '#6F6F6F',
        foa_button: '#00B5AA',
        foa_button_dark: '#004E49',
        foa_button_fg: '#ffffff',
      },
    },
  },
});
