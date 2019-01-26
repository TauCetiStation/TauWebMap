FROM openjdk:8-jdk-alpine as build
WORKDIR /build
COPY . .
RUN ./gradlew cleanProject build

FROM openjdk:8-jre-alpine as render
WORKDIR /render

COPY --from=build /build/render/build/libs/render.jar .
COPY .revisions .

RUN apk update && \
apk add git pngquant && \
mkdir -p tmp/repo && \
git clone --progress https://github.com/TauCetiStation/TauCetiClassic.git tmp/repo && \
java -Xms1g -Xmx1g -jar render.jar

FROM oracle/graalvm-ce:1.0.0-rc11 as native
WORKDIR /native
COPY --from=build /build/server/build/libs/server.jar .
RUN native-image \
--no-server \
--static \
--delay-class-initialization-to-runtime=io.netty.handler.codec.http.HttpObjectEncoder \
-H:-UseServiceLoaderFeature \
-H:IncludeResources="(.*.conf)|(webroot/.*)|(META-INF/mime.types)" \
-jar server.jar

FROM alpine:3.7
WORKDIR /usr/tauwebmap

COPY --from=render /render/data ./data
COPY --from=native /native/server .

ENTRYPOINT ["sh", "-c"]
CMD ["./server"]
