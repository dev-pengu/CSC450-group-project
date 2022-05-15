<template>
  <div class="calendaring">
    <!-- Filter Bar -->
    <v-navigation-drawer v-model="filterBarOpen" fixed temporary right width="375px">
      <v-sheet class="pa-5">
        <v-row>
          <v-col cols="12">
            <div class="d-flex justify-space-between align-center">
              <h3 class="text-uppercase foa_text_header--text">Active Filters</h3>
              <v-btn :loading="loading" class="ma-1" color="red" plain @click="resetFilters">Clear</v-btn>
              <v-btn color="red" icon small @click="filterBarOpen = !filterBarOpen"><v-icon>mdi-close</v-icon></v-btn>
            </div>
            <v-divider class="mb-2" />
            <v-row v-for="filterSet in Object.keys(activeFilters)" :key="filterSet" align="center" justify="start">
              <v-col v-for="(filter, i) in activeFilters[filterSet]" :key="filter.id" class="shrink">
                <v-chip :disabled="loading" close @click:close="removeFilter(filterSet, i)">
                  <v-icon v-if="filterSet == 'FAMILY'" left>mdi-human-male-female-child</v-icon>
                  <v-icon v-else-if="filterSet == 'CALENDAR'" left>mdi-calendar</v-icon>
                  <v-icon v-else left>mdi-account</v-icon>
                  {{ filter.display }}
                </v-chip>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
        <v-row>
          <v-col cols="12">
            <h3 class="text-uppercase foa_text_header--text">Filters</h3>
            <v-divider class="mb-2" />
            <h4 class="foa_text_header--text">Families</h4>
            <div>
              <v-list>
                <template v-for="filter in filters.FAMILY">
                  <v-list-item
                    v-if="!activeFilters.FAMILY.map((f) => f.id).includes(filter.id)"
                    :key="filter.id"
                    :disabled="loading"
                    @click="addFilter('FAMILY', filter)"
                  >
                    <v-list-item-avatar>
                      <v-icon :disabled="loading">mdi-human-male-female-child</v-icon>
                    </v-list-item-avatar>
                    <v-list-item-title v-text="filter.display"></v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </div>
            <v-divider />
            <h4 class="foa_text_header--text">Calendars</h4>
            <div>
              <v-list>
                <template v-for="filter in filters.CALENDAR">
                  <v-list-item
                    v-if="!activeFilters.CALENDAR.map((f) => f.id).includes(filter.id)"
                    :key="filter.id"
                    :disabled="loading"
                    @click="addFilter('CALENDAR', filter)"
                  >
                    <v-list-item-avatar>
                      <v-icon :disabled="loading">mdi-calendar</v-icon>
                    </v-list-item-avatar>
                    <v-list-item-title v-text="filter.display"></v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </div>
            <v-divider />
            <h4 class="foa_text_header--text">Members</h4>
            <div>
              <v-list>
                <template v-for="filter in filters.USER">
                  <v-list-item
                    v-if="!activeFilters.USER.map((f) => f.id).includes(filter.id)"
                    :key="filter.id"
                    :disabled="loading"
                    @click="addFilter('USER', filter)"
                  >
                    <v-list-item-avatar>
                      <v-icon :disabled="loading">mdi-account</v-icon>
                    </v-list-item-avatar>
                    <v-list-item-title v-text="filter.display"></v-list-item-title>
                  </v-list-item>
                </template>
              </v-list>
            </div>
            <v-switch v-model="autoSearch" label="Auto search on change?" inset color="foa_button"></v-switch>
            <v-btn v-if="!autoSearch" @click="getCalendarData">Search</v-btn>
          </v-col>
        </v-row>
      </v-sheet>
    </v-navigation-drawer>
    <!-- Page Content -->
    <v-sheet tile :height="$vuetify.breakpoint.smAndDown ? 124 : 64">
      <span v-if="$vuetify.breakpoint.smAndDown && cal" class="text-h5 pa-4 d-flex">{{ cal.title }}</span>
      <v-toolbar flat>
        <v-btn small class="foa_button_text--text d-inline-block" :color="btnColor" elevation="2" @click="goToToday"
          >Today</v-btn
        >
        <v-btn fab text x-small @click="$refs.calendar.prev()"><v-icon>mdi-chevron-left</v-icon></v-btn>
        <v-btn fab text x-small @click="$refs.calendar.next()"><v-icon>mdi-chevron-right</v-icon></v-btn>
        <v-toolbar-title v-if="$vuetify.breakpoint.mdAndUp && cal">{{ cal.title }}</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-menu bottom right>
          <template #activator="{ on, attrs }">
            <v-btn outlined color="grey darken-2" v-bind="attrs" v-on="on">
              <span>{{ typeToLabel[type] }}</span>
              <v-icon right> mdi-menu-down </v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item @click="type = 'day'">
              <v-list-item-title>Day</v-list-item-title>
            </v-list-item>
            <v-list-item @click="type = '4day'">
              <v-list-item-title>4 days</v-list-item-title>
            </v-list-item>
            <v-list-item @click="type = 'week'">
              <v-list-item-title>Week</v-list-item-title>
            </v-list-item>
            <v-list-item @click="type = 'month'">
              <v-list-item-title>Month</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
        <v-btn class="ml-2" icon :disabled="loading" :loading="loading" @click="getCalendarData"
          ><v-icon>mdi-cached</v-icon>
        </v-btn>
        <v-btn
          icon
          :color="filterBtnColor"
          :disabled="loading"
          :loading="loading"
          @click.stop="filterBarOpen = !filterBarOpen"
        >
          <v-icon>mdi-filter</v-icon>
        </v-btn>
        <div>
          <CalendarEventModal ref="eventModal" :on-events-change="getCalendarData" />
        </div>
      </v-toolbar>
    </v-sheet>
    <v-sheet height="600">
      <v-calendar
        ref="calendar"
        v-model="value"
        color="foa_button"
        :type="type"
        :start="start"
        :end="end"
        :events="events"
        @change="getCalendarData"
        @click:date="goToDate"
        @click:event="handleEventClick"
        @click:more="goToDate"
      >
        <template #day-body="{ date, week }">
          <div class="v-current-time" :class="{ first: date === week[0].date }" :style="{ top: nowY }"></div>
        </template>
      </v-calendar>
    </v-sheet>
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex';
import api from '../../api';
import CalendarEventModal from '../../components/calendar/CalendarEventModal.vue';

export default {
  name: 'Calendar',
  components: {
    CalendarEventModal,
  },
  data: () => ({
    events: [],
    start: '',
    end: '',
    filters: {
      CALENDAR: [],
      USER: [],
      FAMILY: [],
    },
    activeFilters: {
      CALENDAR: [],
      USER: [],
      FAMILY: [],
    },
    activePicker: null,
    menu: false,
    autoSearch: false,
    ready: false,
    type: 'day',
    loading: false,
    filterBarOpen: false,
    value: '',
    filtersActive: false,
    months: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    typeToLabel: {
      month: 'Month',
      week: 'Week',
      day: 'Day',
      '4day': '4 Days',
    },
    selectedEvent: undefined,
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
      return this.filtersActive ? this.btnColor : '';
    },
    btnColor() {
      return this.$vuetify.theme.dark ? 'foa_button' : 'foa_button_dark';
    },
  },
  watch: {
    type(val) {
      this.focusTime(val);
    },
    menu(val) {
      if (val) {
        this.activePicker = 'YEAR';
      }
    },
  },
  created() {
    const currentDate = new Date();
    const endDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0);
    if (this.type === 'month') {
      this.start = `${currentDate.getFullYear()}-${`0${currentDate.getMonth() + 1}`.slice(-2)}-01`;
      this.end = `${endDate.getFullYear()}-${`0${endDate.getMonth() + 1}`.slice(-2)}-${endDate.getDate()}`;
    } else if (this.type === 'day') {
      this.start = `${currentDate.getFullYear()}-${`0${currentDate.getMonth() + 1}`.slice(
        -2
      )}-${currentDate.getDate()}`;
      this.end = `${currentDate.getFullYear()}-${`0${currentDate.getMonth() + 1}`.slice(-2)}-${currentDate.getDate()}`;
    }
  },
  mounted() {
    this.ready = true;
    this.scrollToTime();
    this.updateTime();
    if (this.$route.query.familyId !== undefined) {
      if (this.activeFilters.FAMILY !== undefined) {
        this.activeFilters.FAMILY.push({
          id: +this.$route.query.familyId,
          display: this.getFamily(+this.$route.query.familyId).name,
        });
        this.filtersActive = true;
      }
    }
    this.getCalendarData();
  },
  methods: {
    ...mapActions(['showSnackbar']),
    async getCalendarData() {
      try {
        this.loading = true;
        if (this.cal === null) {
          return;
        }
        const req = {
          start: `${this.cal.lastStart.date} 00:00`,
          end: `${this.cal.lastEnd.date} 23:59`,
          filters: this.activeFilters,
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
          this.filters = calendarRes.data.searchFilters;
          this.activeFilters = calendarRes.data.activeSearchFilters;
        } else {
          this.events = [];
          this.showSnackbar({
            type: 'error',
            message: 'There was a problem getting your events, if the issue persists please contact support.',
            timeout: 3000,
          });
        }
      } catch (err) {
        this.showSnackbar({
          type: 'error',
          message: 'There was a problem getting your events, if the issue persists please contact support.',
          timeout: 3000,
        });
      } finally {
        this.loading = false;
      }
    },
    getCurrentTime() {
      return this.cal ? this.cal.times.now.hour * 60 + this.cal.times.now.minute : 0;
    },
    scrollToTime() {
      const time = this.getCurrentTime();
      const first = Math.max(0, time - (time % 30) - 30);
      setTimeout(() => this.cal.scrollToTime(first), 200);
    },
    updateTime() {
      setInterval(() => this.cal.updateTimes(), 60000);
    },
    focusTime(newVal) {
      if (newVal !== 'month') this.scrollToTime();
    },
    goToDate(day) {
      this.value = day.date;
      this.type = 'day';
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
          owningEventId: event.repetitionSchedule ? event.repetitionSchedule.owningEventId : null,
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
    goToToday() {
      this.value = '';
      this.type = 'day';
      this.scrollToTime();
    },
    removeFilter(filterSet, i) {
      this.activeFilters[filterSet].splice(i, 1);
      if (
        this.activeFilters.CALENDAR.length === 0 &&
        this.activeFilters.USER.length === 0 &&
        this.activeFilters.FAMILY.length === 0
      ) {
        this.filtersActive = false;
      }
      if (this.autoSearch) {
        this.getCalendarData();
      }
    },
    addFilter(filterSet, filter) {
      this.activeFilters[filterSet].push(filter);
      if (!this.filtersActive) {
        this.filtersActive = true;
      }
      if (this.autoSearch) {
        this.getCalendarData();
      }
    },
    resetFilters() {
      this.activeFilters.FAMILY = [];
      this.activeFilters.CALENDAR = [];
      this.activeFilters.USER = [];
      this.filtersActive = false;
      this.getCalendarData();
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
