<template>
  <div class="calendarManager">
    <v-row>
      <v-col cols="12" sm="5" md="4" lg="3">
        <ProfileNav />
      </v-col>
      <v-col cols="12" sm="7" md="8" lg="9">
        <v-sheet max-width="600" color="#00000000">
          <h4 class="text-h5 foa_text_header--text mb-5">
            Calendars
            <NewResourceModal
              v-if="availableFamilies.length > 0"
              :limit-to-adult="true"
              :limit-to-admin="false"
              form-title="Add a Calendar"
              :api-callback="createCalendar"
              :on-failure-callback="createFailure"
              :on-success-callback="createSuccess"
              :success-code="201"
            ></NewResourceModal>
          </h4>
          <div v-for="(calendar, i) in calendars" :key="calendar.id" class="my-7">
            <v-sheet elevation="12">
              <v-row align="center" class="pa-5">
                <v-col cols="6">
                  <span v-if="expandedCalendar.id !== calendar.id" class="text-subtitle-1">{{
                    calendar.description
                  }}</span>
                  <v-text-field
                    v-else
                    v-model="expandedCalendar.description"
                    label="Description"
                    color="foa_button"
                  ></v-text-field>
                </v-col>
                <v-col cols="3" class="text-caption"> Family: {{ getFamily(calendar.familyId).name }} </v-col>
                <v-col v-if="isAdmin(getFamily(calendar.familyId).memberData.role)" cols="3" class="d-flex justify-end">
                  <div v-if="expandedCalendar.id !== calendar.id">
                    <v-btn v-if="!calendar.default" icon color="error" class="mr-2" @click="deleteCal(calendar.id)">
                      <v-icon>mdi-delete</v-icon>
                    </v-btn>
                    <v-btn icon @click="toggleEdit(i)">
                      <v-icon>mdi-pencil</v-icon>
                    </v-btn>
                  </div>
                  <div v-else>
                    <v-btn icon :color="btnColor" class="mr-2" :disabled="loading" :loading="loading" @click="save">
                      <v-icon>mdi-content-save</v-icon>
                    </v-btn>
                    <v-btn icon color="error" :disabled="loading" :loading="loading" @click="expandedCalendar = {}">
                      <v-icon>mdi-close</v-icon>
                    </v-btn>
                  </div>
                </v-col>
                <v-col v-else cols="3">
                  <span class="text-caption">Cannot edit</span>
                </v-col>
              </v-row>
            </v-sheet>
          </div>
        </v-sheet>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import api from '../../api';
import { isAdmin, isAdult } from '../../util/RoleUtil';
import ProfileNav from '../../components/profile/ProfileNav.vue';
import NewResourceModal from '../../components/NewResourceModal.vue';

export default {
  name: 'CalendarManager',
  components: {
    ProfileNav,
    NewResourceModal,
  },
  data: () => ({
    calendars: [],
    expandedCalendar: {},
    createForm: false,
    loading: false,
  }),
  computed: {
    ...mapGetters({ families: 'getFamilies', getFamily: 'getFamily', user: 'getUser' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
    availableFamilies() {
      return this.families.filter((family) => isAdult(family.memberData.role));
    },
  },
  created() {
    this.getCalendars();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    toggleEdit(index) {
      this.expandedCalendar = { ...this.calendars[index] };
    },
    async save() {
      try {
        this.loading = true;
        const res = await api.updateCalendar(this.expandedCalendar);
        if (res.status === 200) {
          this.showSnackbar({
            type: 'success',
            message: 'Calendar updated successfully.',
            timeout: 3000,
          });
          this.expandedCalendar = {};
          this.getCalendars();
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was an error updating your calendar. Please try again in a few minutes.',
            timeout: 3000,
          });
          this.expandedCalendar = {};
          this.getCalendars();
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was an error updating your calendar. Please try again in a few minutes.',
        });
      } finally {
        this.loading = false;
      }
    },
    createCalendar: api.createCalendar,
    createFailure() {
      this.showSnackbar({
        type: 'error',
        message: 'There was an issue creating your calendar. Please try again in a few minutes.',
      });
    },
    createSuccess() {
      this.showSnackbar({
        type: 'success',
        message: 'Calendar created successfully.',
        timeout: 3000,
      });
      this.getCalendars();
    },
    async getCalendars() {
      try {
        this.loading = true;
        const res = await api.getCalendarsForSelect();
        if (res.status === 200) {
          this.calendars = res.data;
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was an error fetching your calendars. Please try again in a few minutes.',
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was an error fetching your calendars. Please try again in a few minutes.',
        });
      } finally {
        this.loading = false;
      }
    },
    async deleteCal(id) {
      try {
        this.loading = true;
        const res = await api.deleteCalendar(id);
        if (res.status === 200) {
          this.showSnackbar({
            type: 'success',
            message: 'Calendar deleted successfully.',
            timeout: 3000,
          });
          this.getCalendars();
        } else {
          this.showSnackbar({
            type: 'error',
            message: 'There was an error deleting your calendar. Please try again in a few minutes.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was an error deleting your calendar. Please try again in a few minutes.',
        });
      } finally {
        this.loading = false;
      }
    },
    isAdmin,
  },
};
</script>
