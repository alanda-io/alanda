export class Action<T> {
  private _type: string;
  private _payload: T;

  get type(): string {
    return this._type;
  }

  get payload(): T {
    return this._payload;
  }

  constructor(type: string, payload?: T) {
    this._type = type;
    this._payload = payload;
  }
}
