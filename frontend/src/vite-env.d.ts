/// <reference types="vite/client" />

/**
 * Type declarations for Vite environment variables.
 * All custom VITE_ prefixed variables must be declared here.
 */
interface ImportMetaEnv {
  /** Optional override for the API base URL (e.g. for local dev or custom deployments). */
  readonly VITE_API_URL?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
