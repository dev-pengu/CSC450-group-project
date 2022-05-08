<template>
  <div>
    <div class="text-h5 foa_text_header--text mb-3">
      My Calendar
      <CalendarEventModal ref="eventModal" open-icon="mdi-plus-box" :on-events-change="getCalendarData" />
      <v-btn class="ml-2" icon :disabled="loading" :loading="loading" @click="getCalendarData"
        ><v-icon>mdi-cached</v-icon>
      </v-btn>
    </div>
    <v-sheet :height="calendarHeight">
      <v-calendar
        ref="calendar"
        v-model="value"
        color="foa_button"
        :type="calendarType"
        :start="start"
        :end="end"
        :events="events"
        @click:event="handleEventClick"
      >
        <template #day-body="{ date, week }">
          <div class="v-current-time" :class="{ first: date === week[0].date }" :style="{ top: nowY }"></div>
        </template>
      </v-calendar>
    </v-sheet>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex';
import api from '../../api';
import CalendarEventModal from './CalendarEventModal.vue';

export default {
  name: 'CalendarPreview',
  components: {
    CalendarEventModal,
  },
  props: {
    start: {
      default: '2000-01-01',
      type: String,
    },
    end: {
      default: '2000-01-02',
      type: String,
    },
    type: {
      default: 'custom-daily',
      type: String,
    },
    calendarHeight: {
      default: 400,
      type: Number,
    },
  },
  data: (instance) => ({
    ready: false,
    calendarType: instance.type,
    events: [],
    types: ['month', 'week', 'day'],
    loading: false,
    filterBarOpen: false,
    value: '',
    filtersActive: false,
  }),
  computed: {
    ...mapGetters({ getFamily: 'getFamily', user: 'getUser' }),
    cal() {
      return this.ready ? this.$refs.calendar : null;
    },
    nowY() {
      return this.cal ? `${this.cal.timeToY(this.cal.times.now)}px` : '-10px';
    },
    filterBtnColor() {
      return this.filtersActive ? 'foa_button' : '';
    },
  },
  mounted() {
    this.ready = true;
    this.scrollToTime();
    this.updateTime();
    this.getCalendarData();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    getCurrentTime() {
      return this.cal ? this.cal.times.now.hour * 60 + this.cal.times.now.minute : 0;
    },
    scrollToTime() {
      const time = this.getCurrentTime();
      const first = Math.max(0, time - (time % 30) - 30);
      this.cal.scrollToTime(first);
    },
    updateTime() {
      setInterval(() => this.cal.updateTimes(), 60000);
    },
    handleEventClick(calendarEvent) {
      const event = this.events[calendarEvent.eventParsed.index];
      const eventData = {
        id: event.recurringEvent ? event.recurringId : event.id,
        calendarId: event.calendarId,
        description: event.description,
        allDay: event.allDay,
        startDate: event.startDate.split(' ')[0],
        startTime: event.startDate.split(' ')[1],
        endDate: event.endDate.split(' ')[0],
        endTime: event.endDate.split(' ')[1],
        assignees: event.familyEvent ? [] : event.assignees.map((assignee) => ({ id: assignee.userId })),
        familyEvent: event.familyEvent,
        notes: event.notes,
        isRepeating: event.repetitionSchedule !== null,
        repetitionSchedule: {
          id: event.repetitionSchedule ? event.repetitionSchedule.id : 0,
          interval: event.repetitionSchedule ? event.repetitionSchedule.interval : 1,
          frequency: event.repetitionSchedule ? event.repetitionSchedule.frequency : 'DAILY',
          count: event.repetitionSchedule ? event.repetitionSchedule.count : 1,
          startDate: event.repetitionSchedule ? event.repetitionSchedule.startDate : null,
          owningEventId: event.repetitionSchedule ? event.repetitionSchedule.owningEventId: null,
        },
        isRecurring: event.recurringEvent,
        creator: `${event.createdBy.firstName} ${event.createdBy.lastName}`,
        canEdit: event.userCanEdit,
      };
      this.$refs.eventModal.loading = true;
      this.$refs.eventModal.eventData = eventData;
      this.$refs.eventModal.dialogOpen = true;
      this.$refs.eventModal.loading = false;
    },
    async getCalendarData() {
      try {
        const req = {
          start: `${this.start} 00:00`,
          end: `${this.end} 23:59`,
          filters: {
            USER: [
              {
                id: this.user.id,
              },
            ],
          },
        };
        const calendarRes = await api.searchCalendars(req);
        if (calendarRes.status === 200) {
          const e = [];
          calendarRes.data.calendars.forEach(
            (calendar) =>
              calendar.events &&
              calendar.events.forEach((event) => {
                if (event.familyEvent) {
                  e.push({
                    ...event,
                    name: `${this.getFamily(calendar.familyId).name.slice(0, 3)} - ${event.description}`,
                    start: event.allDay ? event.startDate.slice(0, 10) : event.startDate,
                    end: event.allDay ? event.endDate.slice(0, 10) : event.endDate,
                    color: `#${this.getFamily(calendar.familyId).eventColor}`,
                    timed: !event.allDay,
                  });
                } else {
                  event.assignees.forEach((assignee) => {
                    e.push({
                      ...event,
                      name: `${this.getFamily(calendar.familyId).name.slice(0, 3)} - ${event.description}`,
                      start: event.allDay ? event.startDate.slice(0, 10) : event.startDate,
                      end: event.allDay ? event.endDate.slice(0, 10) : event.endDate,
                      color: `#${assignee.color}`,
                      timed: !event.allDay,
                    });
                  });
                }
              })
          );
          this.events = e;
        } else {
          this.events = [];
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem getting your events, if the issue persists please contact support.',
          });
        }
      } catch (err) {
        this.events = [];
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem getting your events, if the issue persists please contact support.',
        });
      }
    },
  },
};
</script>

<style lang="scss">
.v-current-time {
  height: 2px;
  background-color: #ea4335;
  position: absolute;
  left: -1px;
  right: 0;
  pointer-events: none;

  &.first::before {
    content: '';
    position: absolute;
    background-color: #ea4335;
    width: 12px;
    height: 12px;
    border-radius: 50%;
    margin-top: -5px;
    margin-left: -6.5px;
  }
}
</style>
