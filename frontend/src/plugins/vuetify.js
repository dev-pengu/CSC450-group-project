import Vue from 'vue';
import Vuetify from 'vuetify/lib/framework';
import colors from 'vuetify/lib/util/colors';

Vue.use(Vuetify);

export default new Vuetify({
  theme: {
    dark: false,
    themes: {
      light: {
        foa_content_bg: '#CCEFDA',
        foa_text: '#616161',
        foa_text_header: '#004E49',
        foa_highlight: '#D9F1F1',
        foa_link: '#009076',
        foa_nav_bg: '#9CD8D4',
        foa_nav_link: '#004E49',
        foa_button: '#00B5AA',
        foa_button_dark: '#004E49',
        foa_button_text: '#ffffff',
        due_warning: colors.amber.darken3,
        due_overdue: colors.red.darken2,
      },
      dark: {
        foa_content_bg: '#CCEFDA',
        foa_text: '#ffffff',
        foa_text_header: '#ffffff',
        foa_highlight: '#D9F1F1',
        foa_link: '#00B5AA',
        foa_nav_bg: '#9CD8D4',
        foa_nav_link: '#004E49',
        foa_button: '#00B5AA',
        foa_button_dark: '#004E49',
        foa_button_text: '#ffffff',
        due_warning: colors.amber.lighten1,
        due_overdue: colors.red.lighten1,
      },
    },
  },
});
