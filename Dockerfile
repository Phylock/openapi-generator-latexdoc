FROM maven:3.8.2-jdk-11 as Build

COPY generators/ .

RUN --mount=type=cache,target=/root/.m2 mvn -f oatpp clean package


FROM openapitools/openapi-generator-cli:v5.3.1

COPY docker-entrypoint.sh /usr/local/bin/

COPY --from=Build /oatpp/target/*.jar /opt/openapi-generator/extension/generators/

ENTRYPOINT ["docker-entrypoint.sh"]

CMD ["help"]