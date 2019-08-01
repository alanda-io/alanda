FROM camunda/camunda-bpm-platform:wildfly-7.10.0

COPY --chown=camunda:camunda ./src_modules/ /camunda/modules/

RUN mkdir /camunda/standalone/templates
RUN mkdir -p /camunda/standalone/attachments/

#RUN chown -R camunda:camunda /camunda/modules
RUN chown -R camunda:camunda /camunda/standalone/attachments/


EXPOSE 8080 9990 8787

WORKDIR /camunda

