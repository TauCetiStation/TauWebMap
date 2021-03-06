[![Build Status](https://travis-ci.org/TauCetiStation/TauWebMap.svg?branch=master)](https://travis-ci.org/TauCetiStation/TauWebMap)

## TauWebMap
Автоматическая карта станции для билда https://github.com/TauCetiStation/TauCetiClassic

### Файл ревизий
Файл в корне проекта `.revisions`. Формат: `01.01.2000 012345678`, где сначала идёт дата (она будет видна польователю) и через пробел значение коммита.
Первая строка будет картой которую увидит пользователь по умолчанию.

### Как собрать локально
Так как необходимое окружение для работы карты собирается докером, необходимо сделать ряд действий чтобы запустить всё локально.

**0.** Убедиться, что установлена JDK 8. Скачать можно [тут](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) или через любой пэкэджменеджер.

(Все команды выполнять из корня проекта)
1. `./gradlew build`
2. Создать папку `tmp/repo` и вызвать `git clone https://github.com/TauCetiStation/TauCetiClassic.git tmp/repo`
3. `java -jar render/build/libs/render.jar -d`
4. `java -jar server/build/libs/server.jar`

После запуска карта будет доступна по адресу `localhost:3000`.

### Как собрать докером
1. `docker build -t tcstation/tauwebmap .`
2. `docker run -d -m 50m --rm -p 3000:3000 tcstation/tauwebmap`

Чтобы запушить: `docker push tcstation/tauwebmap`

### Как запустить докером ничего не собирая
`docker run -d -m 50m --rm -p 3000:3000 tcstation/tauwebmap`

### Структура
* render - код непосредственно рендеринга и создания изображений для карт
* server - сервер и основная точка запуска приложения
* ui - скрипты / стили / index.html файлы во время основной сборки переносящийся под `server/resources/webroot`

### License
See the LICENSE file for license rights and limitations (AGPL-3.0).