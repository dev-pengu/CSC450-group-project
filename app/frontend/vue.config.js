module.exports = {
  outputDir: 'target/dist',
  assetsDir: 'static',
  devServer: {
    proxy: {
      '/api/services': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true,
      },
    },
  },
};
