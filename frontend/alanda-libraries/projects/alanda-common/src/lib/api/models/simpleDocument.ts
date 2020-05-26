export interface SimpleDocument {
  authorName?: string
  guid?: string
  name?: string
  path?: string
  mediaType?: string
  lastModified?: string
  size?: Number
  folder?: boolean
  versionString?: string
  label?: string[]
  checkedOut?: boolean
}
