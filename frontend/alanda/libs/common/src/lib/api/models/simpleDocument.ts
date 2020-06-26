export interface SimpleDocument {
  authorName?: string;
  guid?: string;
  name?: string;
  path?: string;
  mediaType?: string;
  lastModified?: string;
  size?: number;
  folder?: boolean;
  versionString?: string;
  label?: string[];
  checkedOut?: boolean;
}
