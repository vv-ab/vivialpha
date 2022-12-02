FROM azul/zulu-openjdk:17

COPY ./build/install/vivialpha .

COPY ./web ./web

RUN mkdir data

EXPOSE 1234

CMD ["java", "-jar", "vivialpha.jar", "1234"]
