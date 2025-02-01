#  **Telegram category bot**

![Java](https://img.shields.io/badge/Java-17-blue?style=flat-square&logo=java)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green?style=flat-square&logo=spring)  
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue?style=flat-square&logo=postgresql)  

## 📦 **Установка и запуск**

### 1️⃣ Склонируйте репозиторий:

```bash
git clone https://github.com/kai713/CategoryManagementBot.git
cd CategoryManagementBot
```

### 2️⃣ Настройте базу данных:

- Убедитесь, что **PostgreSQL** запущен.
- Для этого зайдите по директории приложения, далее можете ввести команду "docker compose up -d", учтите чтобы был запущен docker engine в таком случае у вас загрузиться изоброжения и контейнер 

- Или же можете самому в ручную подключить вашу базу данных через application.properties указав url, username, password

### 3️⃣ Запуск приложение:

```bash
- Перед запуском создайте бота и вставьте свой токен и имя бота в application.properties, для создания телеграмм бота и токена: @BotFather 

- "mvn spring-boot:run" для запуска
```

### Функционал:
```bash
  Управление категориями и их отоброжения
  Построение иеррархии категории
  Импорт и экспорт ввиде эксел
```

### Команды:
```bash
🔹 /addElement - Добавить элемент: /addElement [родитель] <имя> 
 Добавить элемент: /addElement <имя>
🔹 /download - Скачать дерево в Excel (Файл не должен превышать 20мб)
🔹 /removeElement - Удалить элемент: /removeElement <имя>
🔹 /start - Команда для начало работы с телеграмм ботом
🔹 /upload - Загрузить дерево из Excel (Файл не должен превышать 20мб)
🔹 /viewTree - Показать дерево категорий
```
