FROM openjdk:8-jdk-alpine as build
WORKDIR /tools
COPY . .
RUN ./gradlew cleanProject build

FROM openjdk:8-jre-alpine as render
WORKDIR /render

COPY --from=build /tools/render/build/libs/render.jar .
COPY .revisions .

RUN apk update && \
apk add git pngquant && \
mkdir -p tmp/repo && \
git clone --progress https://github.com/TauCetiStation/TauCetiClassic.git tmp/repo && \
java -Xms1g -Xmx1g -jar render.jar

FROM openjdk:8-jre-alpine
WORKDIR /usr/tauwebmap

ENV WEB_MAP TauWebMap.jar

COPY --from=build /tools/server/build/libs/$WEB_MAP .
COPY --from=render /render/data data/
COPY .revisions .

EXPOSE 3000

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -Xms16m -Xmx32m -jar $WEB_MAP"]
