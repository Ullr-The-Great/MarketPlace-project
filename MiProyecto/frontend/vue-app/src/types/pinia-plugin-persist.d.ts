import 'pinia'

declare module 'pinia' {
  export interface DefineStoreOptionsBase<S, Store> {
    persist?: boolean | PersistedStateOptions | PersistedStateOptions[]
  }
}

interface PersistedStateOptions {
  key?: string
  storage?: Storage
  paths?: string[]
  beforeRestore?: (context: { store: any }) => void
  afterRestore?: (context: { store: any }) => void
  serializer?: {
    serialize: (value: any) => string
    deserialize: (value: string) => any
  }
  debug?: boolean
}