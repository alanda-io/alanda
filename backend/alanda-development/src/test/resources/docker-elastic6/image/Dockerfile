FROM docker.elastic.co/elasticsearch/elasticsearch:6.6.0

COPY --chown=elasticsearch:root 02_init-index-alanda.sh /usr/share/elasticsearch/02_init-index-alanda.sh
COPY --chown=elasticsearch:root index-schema.json /usr/share/elasticsearch/index-schema.json
COPY --chown=elasticsearch:root index-task.json /usr/share/elasticsearch/index-task.json
RUN chmod +x /usr/share/elasticsearch/02_init-index-alanda.sh

COPY elasticsearch.yml /usr/share/elasticsearch/config/elasticsearch.yml

RUN mkdir /data && chown -R elasticsearch:elasticsearch /data && echo 'path.data: /data' >> config/elasticsearch.yml

ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/e1f115e4ca285c3c24e847c4dd4be955e0ed51c2/wait-for-it.sh /utils/wait-for-it.sh

RUN /usr/local/bin/docker-entrypoint.sh elasticsearch -p /tmp/epid & /bin/bash /utils/wait-for-it.sh -t 0 localhost:9200 -- ./02_init-index-alanda.sh; kill $(cat /tmp/epid) && wait $(cat /tmp/epid); exit 0;


EXPOSE 9200 9300
