<template>
  <div class="calendarEventModal d-inline-block">
    <v-dialog v-model="confirmBreakRecurring">
      <v-card>
        <v-card-title>Confirm Update</v-card-title>
        <v-card-text
          >This event is part of a series of recurring events. Are you sure you want to proceed with updating? By doing
          so, the event will be removed from the series.
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn small text color="error" @click="confirmBreakRecurring = false"> Cancel </v-btn>
          <v-btn small text color="success" @click="updateEvent"> OK </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogOpen" persistent transition="dialog-bottom-transition" max-width="700">
      <template #activator="{ on, attrs }">
        <v-btn icon :color="btnColor" v-bind="attrs" v-on="on"
          ><v-icon>{{ openIcon }}</v-icon></v-btn
        >
      </template>
      <v-card>
        <div class="d-flex justify-space-between align-center">
          <v-card-title v-text="getDialogTitle()"></v-card-title>
          <v-btn class="mr-5" color="error" icon @click="closeDialog"><v-icon>mdi-close</v-icon></v-btn>
        </div>
        <v-card-text>
          <v-form :readonly="!eventData.canEdit">
            <div v-if="eventData.id !== 0" class="text-caption mb-2">Created by: {{ eventData.creator }}</div>
            <v-select
              v-if="eventData.id === 0"
              v-model="eventData.calendarId"
              outlined
              label="Calendar"
              :items="calendars"
              item-value="id"
              item-text="display"
            ></v-select>
            <v-text-field
              v-model="eventData.description"
              label="Title"
              outlined
              @change="
                () => {
                  if (!loading) eventCoreUpdated = true;
                }
              "
            ></v-text-field>
            <div class="d-flex justify-space-around align-center">
              <v-switch
                v-model="eventData.allDay"
                inset
                color="foa_button"
                label="All Day"
                @change="
                  () => {
                    if (!loading) eventCoreUpdated = true;
                  }
                "
              ></v-switch>
              <v-switch
                v-model="eventData.isRepeating"
                inset
                color="foa_button"
                label="Repeating"
                @change="
                  () => {
                    if (!loading) eventCoreUpdated = true;
                  }
                "
              ></v-switch>
            </div>
            <v-sheet
              v-if="eventData.isRepeating"
              :color="$vuetify.theme.dark ? '#3f3f3f' : '#eaeaea'"
              elevation="6"
              rounded
              class="pa-2"
            >
              <v-row align="center" justify="center">
                <v-col cols="3" sm="2">Every</v-col>
                <v-col cols="3" sm="2">
                  <v-text-field
                    v-model="eventData.repetitionSchedule.interval"
                    dense
                    type="number"
                    hint="interval"
                    persistent-hint
                    @blur="scheduleUpdated = true"
                  ></v-text-field>
                </v-col>
                <v-col cols="6" sm="3">
                  <v-select
                    v-model="eventData.repetitionSchedule.frequency"
                    dense
                    :items="frequencies"
                    item-value="value"
                    item-text="display"
                    hint="frequency"
                    persistent-hint
                    @blur="scheduleUpdated = true"
                  ></v-select>
                </v-col>
                <v-col cols="2" sm="1"> for </v-col>
                <v-col cols="3" sm="2">
                  <v-text-field
                    v-model="eventData.repetitionSchedule.count"
                    dense
                    type="number"
                    hint="times"
                    persistent-hint
                    @blur="scheduleUpdated = true"
                  ></v-text-field>
                </v-col>
                <v-col cols="5" sm="2"> occurrences</v-col>
              </v-row>
              <div v-if="eventData.repetitionSchedule.startDate" class="text-caption mb-2">
                Starting {{ `${new Date(eventData.repetitionSchedule.startDate).toDateString()}` }}
              </div>
              <v-alert v-if="showScheduleMessage"
                >You are changing the Series. Doing so will update any future events and any events no longer in the
                series will be recreated.</v-alert
              >
            </v-sheet>
            <v-row align="center" justify="center">
              <v-col cols="6">
                <v-menu
                  ref="startMenu"
                  v-model="startMenu"
                  :disabled="!eventData.canEdit"
                  :close-on-content-click="false"
                  :return-value.sync="eventData.startDate"
                  transition="scale-transition"
                  offset-y
                  min-width="auto"
                >
                  <template #activator="{ on, attrs }">
                    <v-text-field
                      v-model="eventData.startDate"
                      label="Start"
                      prepend-icon="mdi-calendar"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                      @change="
                        () => {
                          if (!loading) eventCoreUpdated = true;
                        }
                      "
                    ></v-text-field>
                  </template>
                  <v-date-picker v-model="eventData.startDate" no-title scrollable>
                    <v-spacer></v-spacer>
                    <v-btn text color="primary" @click="startMenu = false"> Cancel </v-btn>
                    <v-btn text color="primary" @click="$refs.startMenu.save(eventData.startDate)"> OK </v-btn>
                  </v-date-picker>
                </v-menu>
              </v-col>
              <v-col v-if="!eventData.allDay" cols="6">
                <v-menu
                  ref="startTimeMenu"
                  v-model="startTimeMenu"
                  :disabled="!eventData.canEdit"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  :return-value.sync="eventData.startTime"
                  transition="scale-transition"
                  offset-y
                  max-width="290px"
                  min-width="290px"
                >
                  <template #activator="{ on, attrs }">
                    <v-text-field
                      v-model="eventData.startTime"
                      prepend-icon="mdi-clock-time-four-outline"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                      @change="
                        () => {
                          if (!loading) eventCoreUpdated = true;
                        }
                      "
                    ></v-text-field>
                  </template>
                  <v-time-picker
                    v-if="startTimeMenu"
                    v-model="eventData.startTime"
                    full-width
                    @click:minute="$refs.startTimeMenu.save(eventData.startTime)"
                  ></v-time-picker>
                </v-menu>
              </v-col>
            </v-row>
            <v-row align="center" justify="center">
              <v-col cols="6">
                <v-menu
                  ref="endMenu"
                  v-model="endMenu"
                  :disabled="!eventData.canEdit"
                  :close-on-content-click="false"
                  :return-value.sync="eventData.endDate"
                  transition="scale-transition"
                  offset-y
                  min-width="auto"
                >
                  <template #activator="{ on, attrs }">
                    <v-text-field
                      v-model="eventData.endDate"
                      label="End"
                      prepend-icon="mdi-calendar"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                      @change="
                        () => {
                          if (!loading) eventCoreUpdated = true;
                        }
                      "
                    ></v-text-field>
                  </template>
                  <v-date-picker v-model="eventData.endDate" no-title scrollable>
                    <v-spacer></v-spacer>
                    <v-btn text color="primary" @click="endMenu = false"> Cancel </v-btn>
                    <v-btn text color="primary" @click="$refs.endMenu.save(eventData.endDate)"> OK </v-btn>
                  </v-date-picker>
                </v-menu>
              </v-col>
              <v-col v-if="!eventData.allDay" cols="6">
                <v-menu
                  ref="endTimeMenu"
                  v-model="endTimeMenu"
                  :disabled="!eventData.canEdit"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  :return-value.sync="eventData.endTime"
                  transition="scale-transition"
                  offset-y
                  max-width="290px"
                  min-width="290px"
                >
                  <template #activator="{ on, attrs }">
                    <v-text-field
                      v-model="eventData.endTime"
                      prepend-icon="mdi-clock-time-four-outline"
                      readonly
                      v-bind="attrs"
                      v-on="on"
                      @change="
                        () => {
                          if (!loading) eventCoreUpdated = true;
                        }
                      "
                    ></v-text-field>
                  </template>
                  <v-time-picker
                    v-if="endTimeMenu"
                    v-model="eventData.endTime"
                    full-width
                    @click:minute="$refs.endTimeMenu.save(eventData.endTime)"
                  ></v-time-picker>
                </v-menu>
              </v-col>
            </v-row>
            <v-switch
              v-model="eventData.familyEvent"
              inset
              color="foa_button"
              label="Family Event"
              hint="If yes, all members will be assigned"
              persistent-hint
              @change="
                () => {
                  if (!loading) eventCoreUpdated = true;
                }
              "
            ></v-switch>
            <v-select
              v-if="!eventData.familyEvent"
              v-model="eventData.assignees"
              :items="members"
              label="Assignees"
              multiple
              chips
              item-text="display"
              item-value="id"
              @change="
                () => {
                  if (!loading) eventCoreUpdated = true;
                }
              "
            ></v-select>
            <v-textarea v-model="eventData.notes" class="mt-2" outlined label="Additional Notes"></v-textarea>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            v-if="eventData.id !== 0 && eventData.canEdit"
            class="d-inline-block"
            elevation="2"
            color="error"
            :disabled="loading"
            @click="deleteEvent"
            >Delete</v-btn
          >
          <v-btn class="d-inline-block" elevation="2" :disabled="loading" @click="closeDialog">Cancel</v-btn>
          <v-btn
            v-if="eventData.canEdit"
            class="foa_button_text--text d-inline-block"
            :color="btnColor"
            elevation="2"
            :disabled="loading"
            @click="submit"
            >{{ eventData.id !== 0 ? 'Update' : 'Add' }}</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';

export default {
  name: 'CalendarEventModal',
  props: {
    event: {
      default: () => ({
        id: 0,
        calendarId: 0,
        description: '',
        allDay: false,
        startDate: '',
        startTime: '',
        endDate: '',
        endTime: '',
        assignees: [],
        familyEvent: true,
        notes: '',
        isRepeating: false,
        repetitionSchedule: {
          id: 0,
          interval: 1,
          frequency: 'DAILY',
          count: 1,
          startDate: null,
        },
        isRecurring: false,
        creator: '',
        canEdit: true,
      }),
      type: Object,
    },
    onEventsChange: {
      default: () => {},
      type: Function,
    },
    openIcon: {
      default: 'mdi-calendar-clock',
      type: String,
    },
  },
  data: (instance) => ({
    loading: false,
    eventData: instance.event,
    defaultEventData: {
      id: 0,
      calendarId: 0,
      description: '',
      allDay: false,
      startDate: '',
      startTime: '',
      endDate: '',
      endTime: '',
      assignees: [],
      familyEvent: true,
      notes: '',
      isRepeating: false,
      repetitionSchedule: {
        id: 0,
        interval: 1,
        frequency: 'DAILY',
        count: 1,
        startDate: null,
      },
      isRecurring: false,
      creator: '',
      canEdit: true,
    },
    dialogOpen: false,
    valid: false,
    calendars: [],
    startMenu: false,
    endMenu: false,
    startTimeMenu: false,
    endTimeMenu: false,
    members: [],
    frequencies: [
      { value: 'DAILY', display: 'Day(s)' },
      { value: 'WEEKLY', display: 'Week(s)' },
      { value: 'WEEK_DAY', display: 'Week Day(s)' },
      { value: 'MONTHLY', display: 'Month(s)' },
      { value: 'YEARLY', display: 'Year(s)' },
    ],
    confirmBreakRecurring: false,
    scheduleUpdated: false,
    showScheduleMessage: false,
    eventCoreUpdated: false,
  }),
  computed: {
    ...mapGetters({ getFamily: 'getFamily' }),
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  watch: {
    // eslint-disable-next-line func-names
    'eventData.calendarId': function () {
      this.getMembers();
    },
  },
  mounted() {
    this.getCalendarList();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    closeDialog() {
      this.dialogOpen = false;
      this.eventData = { ...this.defaultEventData };
    },
    async submit() {
      if (this.eventData.id === 0) {
        const req = {
          allDay: this.eventData.allDay,
          familyEvent: this.eventData.familyEvent,
          startDate: this.eventData.allDay
            ? `${this.eventData.startDate} 00:00`
            : `${this.eventData.startDate} ${this.eventData.startTime}`,
          endDate: this.eventData.allDay
            ? `${this.eventData.endDate} 00:00`
            : `${this.eventData.endDate} ${this.eventData.endTime}`,
          description: this.eventData.description,
          notes: this.eventData.notes,
          calendarId: this.eventData.calendarId,
          assignees: this.eventData.familyEvent
            ? []
            : this.eventData.assignees.map((assignee) => ({ userId: assignee })),
        };
        if (this.eventData.isRepeating) {
          req.repetitionSchedule = { ...this.eventData.repetitionSchedule };
        }
        try {
          this.loading = true;
          const res = await api.addCalendarEvent(req);
          if (res.status === 201) {
            this.closeDialog();
            this.showSnackbar({
              type: 'success',
              message: 'Event created successfully.',
              timeout: 3000,
            });
            this.onEventsChange();
          } else {
            // TODO: handle api exceptions
          }
        } catch (err) {
          this.showSnackbar({
            type: 'error',
            message: 'There was an issue creating your event. Please try again in a few minutes.',
          });
        } finally {
          this.loading = false;
        }
      } else if (this.scheduleUpdated && !this.showScheduleMessage) {
        this.showScheduleMessage = true;
      } else if (this.scheduleUpdated && this.showScheduleMessage) {
        this.showScheduleMessage = false;
        if (this.eventData.isRecurring && this.eventCoreUpdated) {
          this.confirmBreakRecurring = true;
        } else {
          this.updateEvent();
        }
      } else {
        this.updateEvent();
      }
    },
    async updateEvent() {
      const req = {
        id: this.eventData.isRecurring ? null : this.eventData.id,
        allDay: this.eventData.allDay,
        familyEvent: this.eventData.familyEvent,
        startDate: this.eventData.allDay
          ? `${this.eventData.startDate} 00:00`
          : `${this.eventData.startDate} ${this.eventData.startTime}`,
        endDate: this.eventData.allDay
          ? `${this.eventData.endDate} 00:00`
          : `${this.eventData.endDate} ${this.eventData.endTime}`,
        description: this.eventData.description,
        notes: this.eventData.notes,
        calendarId: this.eventData.calendarId,
        recurringEvent: this.eventData.isRecurring,
        recurringId: this.eventData.isRecurring ? this.eventData.id : null,
        assignees: this.eventData.familyEvent
          ? []
          : this.eventData.assignees.map((assignee) => ({ userId: assignee.id })),
      };
      if (this.eventData.isRepeating) {
        req.repetitionSchedule = {
          ...this.eventData.repetitionSchedule,
          id: this.eventData.repetitionSchedule.id === 0 ? null : this.eventData.repetitionSchedule.id,
        };
      }
      try {
        this.loading = true;
        const res = await api.updateCalendarEvent(req);
        if (res.status === 200) {
          this.closeDialog();
          this.showSnackbar({
            type: 'success',
            message: 'Event updated successfully.',
            timeout: 3000,
          });
          this.onEventsChange();
        } else {
          // TODO handle api exceptions
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was an issue updating your event. Please try again in a few minutes.',
        });
      } finally {
        this.loading = false;
      }
    },
    getDialogTitle() {
      if (this.eventData.id === 0) {
        return 'Add Event';
      }
      if (!this.eventData.canEdit) {
        return 'View Event';
      }
      return 'Edit Event';
    },
    async deleteEvent() {
      try {
        this.loading = true;
        const res = await api.deleteEvent(this.eventData.id, this.eventData.isRecurring);
        if (res.status === 200) {
          this.closeDialog();
          this.showSnackbar({
            type: 'success',
            message: 'Event deleted successfully.',
            timeout: 3000,
          });
          this.onEventsChange();
        } else {
          // TODO: handle api exception codes
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was an issue deleting the event. Please try again in a few minutes.',
        });
      } finally {
        this.loading = false;
      }
    },
    async getCalendarList() {
      try {
        this.loading = true;
        const res = await api.getCalendarsForSelect();
        if (res.status === 200) {
          this.calendars = res.data.map((calendar) => ({
            id: calendar.id,
            display: `${this.getFamily(calendar.familyId).name} - ${calendar.description}`,
          }));
        } else {
          this.calendars = [];
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was an issue fetching data from the server, if the issue persists please contact support.',
        });
      } finally {
        this.loading = false;
      }
    },
    async getMembers() {
      if (this.eventData.calendarId === 0) {
        this.members = [];
        return;
      }
      try {
        this.loading = true;
        const res = await api.getPotentialAssignees(this.eventData.calendarId);
        if (res.status === 200) {
          this.members = res.data;
        } else {
          this.members = [];
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was an issue fetching data from the server, if the issue persists please contact support.',
        });
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>
