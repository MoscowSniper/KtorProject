# Ktor Shop Service

Backend-сервис интернет-магазина на **Ktor + PostgreSQL + Exposed + JWT + Redis + RabbitMQ**.

## Возможности
- Регистрация и авторизация пользователей
- Просмотр товаров
- Создание и отмена заказов
- История покупок
- Админ API для CRUD товаров
- Закрытая статистика заказов
- Audit logs
- Swagger документация: `/swagger`

## API
- `POST /auth/register`
- `POST /auth/login`
- `GET /products`
- `GET /products/{id}`
- `POST /orders`
- `GET /orders`
- `DELETE /orders/{id}`
- `POST /products` (admin)
- `PUT /products/{id}` (admin)
- `DELETE /products/{id}` (admin)
- `GET /stats/orders` (admin)

## Быстрый старт
```bash
docker-compose up --build
```

## Запуск локально
```bash
./gradlew run
```


## Ошибка подключения к PostgreSQL (`role "shop" does not exist`)
Если вы запускаете сервис не через `docker-compose`, в локальном PostgreSQL часто нет пользователя `shop`.

Используйте переменные окружения перед запуском:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/postgres
export DB_USER=postgres
export DB_PASSWORD=postgres
./gradlew run
```

Либо создайте пользователя/БД `shop`, как в `docker-compose.yml`.

## Тесты
```bash
./gradlew test
```

В проекте добавлены:
- Unit тесты (`src/test/kotlin/com/example/unit`)
- Integration тесты с Testcontainers (`src/test/kotlin/com/example/integration`)
- E2E API тесты (`src/test/kotlin/com/example/e2e`)

## CI/CD
GitHub Actions workflow: `.github/workflows/ci.yml`

## Деплой
### Render / Railway
1. Подключить репозиторий.
2. Build command: `./gradlew buildFatJar`
3. Start command: `java -jar build/libs/app.jar`
4. Задать переменные окружения: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `REDIS_HOST`, `RABBIT_HOST`.

### Ссылка на сервис
- _Добавьте ссылку после деплоя (например: `https://your-app.onrender.com`)_.
