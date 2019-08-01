import { Tag } from "./tag";

export class MultiTagRequest {
  documentIds: string[];
  tagsToAdd: Tag[];
  tagsToRemove: Tag[];
}

