FROM openjdk:8-jdk-alpine as build
WORKDIR /usr/workspace
COPY . .
RUN ./gradlew cleanProject build

FROM openjdk:8-jre-alpine
WORKDIR /usr/tauwebmap

ENV BUILD_SPACE /usr/workspace
ENV WEB_MAP TauWebMap.jar

COPY --from=build $BUILD_SPACE/server/build/libs/$WEB_MAP .
COPY --from=build $BUILD_SPACE/render/build/libs/render.jar .

RUN apk update && \
apk add git pngquant && \
mkdir -p tmp/repo && \
git clone --progress https://github.com/TauCetiStation/TauCetiClassic.git tmp/repo

EXPOSE 3000

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -Xms32m -Xmx64m -jar $WEB_MAP"]
