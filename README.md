Инструкция по развертыванию приложения:
1. В терминале запустить команду mvn clean install
2. В корне проекта найти файл compose.yml, открыть его и запустить создание контейнеров, нажав на кнопку, либо через консоль(docker compose up -d)
3. По ссылке http://localhost:8081/swagger-ui/index.html доступен интерфейс swagger для тестирования запросов основного сервиса и http://localhost:8082/swagger-ui/index.html на дополнительном,
   http://localhost:8082/swagger-ui/index.html для регистрации
4. Для доступа к запросам в основном сервисе необходимо зарегистрировать пользователя и использовать его аккаунт
