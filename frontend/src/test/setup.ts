import '@testing-library/jest-dom';
import { beforeAll, afterEach, afterAll } from 'vitest';

// Global test setup for Vitest & React Testing Library
beforeAll(() => {
  // Mock window.matchMedia if needed by components
  Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: (query: string) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: () => {},
      removeListener: () => {},
      addEventListener: () => {},
      removeEventListener: () => {},
      dispatchEvent: () => false,
    }),
  });
});

afterEach(() => {
  // Cleanup after each test
});

afterAll(() => {
  // Global teardown
});
