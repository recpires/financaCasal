import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// Porta fixa: o backend so libera CORS para http://localhost:5173
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
  },
})
