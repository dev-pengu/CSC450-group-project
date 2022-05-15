<template>
  <div class="userProfle">
    <v-row>
      <v-col cols="12" sm="5" md="4" lg="3">
        <ProfileNav />
      </v-col>
      <v-col cols="12" sm="7" md="8" lg="9">
        <v-sheet max-width="600" color="#00000000">
          <h4 class="text-h5 foa_text_header--text mb-5">My Profile</h4>
          <v-form ref="form" v-model="valid" class="ml-5">
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="settings.firstName"
                  label="First Name"
                  color="foa_button"
                  :rules="[required, max]"
                  counter="50"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="settings.lastName"
                  label="Last Name"
                  color="foa_button"
                  :rules="[required, max]"
                  counter="50"
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12" sm="6">
                <v-switch v-model="settings.useDarkMode" inset label="Use Dark Mode" color="foa_button"></v-switch>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="settings.timezone"
                  :items="timezones"
                  color="foa_button"
                  item-color="foa_button"
                  label="Timezone"
                  required
                  hide-details
                ></v-select>
              </v-col>
            </v-row>
          </v-form>

          <v-data-iterator
            :items="settings.colorsByFamily"
            item-key="familyId"
            :loading="loading"
            no-data-text="You haven't joined any families yet!"
            hide-default-footer
            class="ms-5"
          >
            <template #header>
              <h6 class="text-h6 mt-5 mb-3">Family Event Colors</h6>
            </template>
            <template #default="props">
              <v-row>
                <v-col v-for="family in props.items" :key="family.familyId" cols="12" md="6">
                  <v-card>
                    <v-card-title class="subheading">
                      <v-icon>mdi-human-male-female-child</v-icon> {{ family.family }}
                    </v-card-title>
                    <v-card-text>
                      <v-color-picker v-model="family.color" hide-inputs hide-mode-switch mode="hexa"></v-color-picker>
                    </v-card-text>
                  </v-card>
                </v-col>
              </v-row>
            </template>
          </v-data-iterator>
          <v-row class="mt-5">
            <v-col class="d-flex justify-end">
              <v-spacer></v-spacer>
              <v-btn
                class="foa_button_text--text"
                color="foa_button"
                elevation="2"
                :loading="loading"
                :disabled="loading || !valid"
                @click="save"
                >Save Changes</v-btn
              >
            </v-col>
          </v-row>
        </v-sheet>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapActions } from 'vuex';
import api from '../../api';
import store from '../../store';
import ProfileNav from '../../components/profile/ProfileNav.vue';

export default {
  name: 'UserProfile',
  components: {
    ProfileNav,
  },
  data: () => ({
    loading: false,
    valid: true,
    timezones: [],
    settings: {},
    required: (v) => !!v || 'First name and last name are required',
    max: (v) => (v && v.length <= 50) || 'Max 50 characters',
  }),
  async created() {
    const res = await api.getTimezones();
    this.timezones = res.data;
  },

  async mounted() {
    this.fetchSettings();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    async save() {
      try {
        this.loading = true;
        const req = this.settings;
        req.colorsByFamily = req.colorsByFamily.map((colorObj) => ({
          ...colorObj,
          color: colorObj.color.replace('#', ''),
        }));
        const res = await api.updateUserSettings(req);
        if (res.status === 200) {
          this.showSnackbar({ type: 'success', message: 'Your preferences were saved successfully!', timeout: 3000 });
          this.fetchSettings();
          store.commit('UPDATE_USER', res.data);
          this.$vuetify.theme.dark = this.settings.useDarkMode;
          localStorage.setItem('darkMode', this.$vuetify.theme.dark.toString());
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'We ran into an error processing your changes. Please try again in a few minutes!',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an error processing your changes. Please try again in a few minutes!',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    async fetchSettings() {
      try {
        this.loading = true;
        const res = await api.getUserSettings();
        if (res.status === 200) {
          const settings = res.data;
          settings.colorsByFamily = settings.colorsByFamily.map((colorObj) => ({
            ...colorObj,
            color: `#${colorObj.color}`,
          }));
          this.settings = settings;
        } else {
          this.showSnackbar({
            type: 'error',
            message:
              'We ran into an error getting your current settings. If the error persists, please contact support!',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'We ran into an error getting your current settings. If the error persists, please contact support!',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
