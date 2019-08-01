import { Observable } from 'rxjs';

/**
 * source: https://github.com/bpmn-io/bpmn-js-example-angular
 * An operator that imports the first XML piped via the piped diagram XML
 * into the passed BpmnJS instance.
 */
export const importDiagram = (bpmnJS) => <Object>(source: Observable<string>) =>
  new Observable<string>(observer => {

    const subscription = source.subscribe({
      next(res: any) {
        // canceling the subscription as we are interested
        // in the first diagram to display only
        subscription.unsubscribe();

        bpmnJS.importXML(res.bpmn20Xml, function(err, warnings) {

          if (err) {
            observer.error(err);
          } else {
            observer.next(warnings);
          }

          observer.complete();
        });
      },
      error(e) {
        console.log('ERROR');
        observer.error(e);
      },
      complete() {
        observer.complete();
      }
    });
});