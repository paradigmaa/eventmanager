# Event Manager Platform

Проект состоит из двух сервисов, которые общаются через Kafka. Первый управляет мероприятиями, второй сохраняет историю изменений.

## Структура

- **event-manager** — основной сервис. Создание событий, регистрация участников, поиск.
- **event-notificator** — слушает Kafka и складывает всё в свою базу.
- **event-common** — общие классы для сообщений.

## Технологии

Java 21, Spring Boot 3, Kafka, PostgreSQL, Docker, JUnit, Mockito.

## Как запустить

1. Собрать все модули:
   mvn clean package

text

2. Запустить инфраструктуру (PostgreSQL, Kafka):
   docker-compose up -d

text

3. Запустить сервисы:
   java -jar event-manager/target/event-manager.jar
   java -jar event-notificator/target/event-notificator.jar

text

Или через Docker Compose, если настроен.

## Что делает event-manager

- Создание и редактирование мероприятий
- Удаление (только для владельца или админа)
- Регистрация участников
- Отмена регистрации
- Поиск с фильтрацией: название, цена, количество мест, локация, статус
- Пагинация
- При изменении мероприятия отправляет сообщение в Kafka

## Что делает event-notificator

- Подписывается на топик Kafka
- Сохраняет каждое изменение в отдельную таблицу
- В базе хранится: какое мероприятие изменилось, кто изменил, что именно поменялось, когда

## Тесты

Написаны тесты для репозиториев, сервисов и контроллеров. Используются JUnit 5 и Mockito. Отчёт по покрытию собирается через JaCoCo.
mvn test
mvn jacoco:report

text

## API

После запуска event-manager документация доступна по адресу:
http://localhost:8080/swagger-ui.html

## Что можно добавить

- CI/CD через GitHub Actions
- Миграции БД (Flyway)
- Аудит в самом event-manager
- Интеграционные тесты с Testcontainers

## Контакты

Автор: Архипов Александр
GitHub: https://github.com/paradigmaa