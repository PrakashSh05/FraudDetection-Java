/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#E94F37',
          hover: '#d4432c',
          light: '#fdeee8',
        },
        bg: {
          DEFAULT: '#F6F7EB',
          surface: '#FFFFFF',
          sidebar: '#FFFFFF',
        },
        dark: {
          DEFAULT: '#393E41',
          muted: '#6C757D',
        },
        status: {
          success: '#10B981',
          warning: '#F59E0B',
          danger: '#EF4444',
          info: '#3B82F6',
        }
      },
    },
  },
  plugins: [],
}
