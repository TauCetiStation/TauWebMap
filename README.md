## TauWebMap
Автоматическая карта станции для билда https://github.com/TauCetiStation/TauCetiClassic

#### Как собрать локально
Так как необходимое окружение для работы карты собирается докером, необходимо сделать ряд действий чтобы запустить всё локально.

**0.** Убедиться, что установлена JDK 8. Скачать можно [тут](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) или через любой пэкэджменеджер.

(Все команды выполнять из корня проекта)
1. `./gradlew build`
2. Из папки `render/build/libs/` скопировать `render.jar` в корень
3. Скачать и поместить в корень бинарник [pngquant](https://pngquant.org/)
4. Создать папку `tmp/repo` и вызвать `git clone https://github.com/TauCetiStation/TauCetiClassic.git tmp/repo`
5. `java -Xms32m -Xmx64m -jar server/build/libs/TauWebMap.jar`

После запуска сразу начнут генерироваться изображения карт. Они будут помещаться в `data/maps`. Полный процесс генерации занимает ~3 минуты.
После запуска карта будет доступна по адресу `localhost:3000`.

#### Как собрать докером
1. `docker build -t webmap .`
2. `docker run -p 3000:3000 webmap`

#### Как запустить докером ничего не собирая
`docker run -p 8080:3000 spair/tauwebmap`

#### Структура
* render - код непосредственно рендеринга и создания изображений для карт, который собирается в отдельный файл для дальнейшего запуска из под сервера
* server - сервер и основная точка запуска приложения
* ui - скрипты / стили / index.html файлы во время основной сборки переносящийся под `server/resources/webroot`

#### License
See the LICENSE file for license rights and limitations (AGPL-3.0).