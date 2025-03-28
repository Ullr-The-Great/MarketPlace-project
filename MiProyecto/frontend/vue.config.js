module.exports = {
  publicPath: '/',
  devServer: {
    port: 8081, // Puerto del frontend
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // URL del backend
        changeOrigin: true,
      },
    },
    historyApiFallback: true
  },
};
