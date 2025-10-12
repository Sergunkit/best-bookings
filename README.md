# Hexling

## Запуск

> требования: Docker, docker-compose, Node.JS, npm, git, Make

1. Склонируйте репозиторий с GitHub и перейдите в директорию проекта:

```bash
git clone https://github.com/LucyMurrr/hotel-booking.git
cd hotel-booking

```

2. Сгенерируйте код:

```bash
make gen-all
```

3. Перейдите в папку client и установите зависимости:

```bash
cd client && make docker-dependencies-fix
```

4. Вернитесь в корень проект и запустите:

```bash
cd .. && make dev
```

5. (опционально) Наполнение данными БД. Выполните после полного запуска приложения, не останавливая контейнеры

```bash
make seed
```
