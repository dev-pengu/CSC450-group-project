<template>
  <Bar
    :chart-options="chartOptions"
    :chart-data="chartData"
    :chart-id="chartId"
    :dataset-id-key="datasetIdKey"
    :width="width"
    :height="height"
  />
</template>

<script>
import { Bar } from 'vue-chartjs/legacy';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);
export default {
  name: 'ResultChart',
  components: {
    Bar,
  },
  props: {
    labels: {
      default: () => [],
      type: Array,
    },
    votes: {
      default: () => [],
      type: Array,
    },
    pollDescription: {
      default: '',
      type: String,
    },
    chartId: {
      default: 'bar-chart',
      type: String,
    },
    datasetIdKey: {
      type: String,
      default: 'label',
    },
    width: {
      type: Number,
      default: 400,
    },
    height: {
      type: Number,
      default: 400,
    },
  },
  data: (instance) => ({
    chartData: {
      labels: instance.labels,
      datasets: [
        {
          label: 'Poll responses',
          data: instance.votes,
          backgroundColor: [
            'rgba(255, 99, 132, 0.2)',
            'rgba(255, 159, 64, 0.2)',
            'rgba(255, 205, 86, 0.2)',
            'rgba(75, 192, 192, 0.2)',
          ],
          borderColor: ['rgb(255, 99, 132)', 'rgb(255, 159, 64)', 'rgb(255, 205, 86)', 'rgb(75, 192, 192)'],
          borderWidth: 1,
        },
      ],
    },
    chartOptions: {
      indexAxis: 'y',
      responsive: true,
      plugins: {
        title: {
          display: true,
          text: instance.pollDescription,
        },
      },
      scales: {
        x: {
          beginAtZero: true,
        },
      },
    },
  }),
};
</script>
