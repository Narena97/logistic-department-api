# API отдела логистики
В проекте разработан веб-сервис для отдела логистики. Документацияк к API доступна в [Swagger UI](http://localhost:8080/swagger-ui.html#/). Для запуска проекта необходимо создать базу данных с названием **db_logistic_department** (username: postgres, password: admin) и схему в базе данных с названием **logistic**.
- Хранится информация о водителях и автомобилях. За одним водителем не может быть закреплено более трех автомобилей.
- Автомобили могут быть различных типов: легковой, грузовой, автобус.
- У водителей имеются права на вождение автомобилей категорий B, C, D; у водительских удостоверений есть срок действия.
- В проекте поддержаны CRUD операции для водителей и автомобилей.
- Разработаны метод закрепления автомобиля за водителем и открепления от него, метод вывода списка водителей и закрепленных за ними автомобилей.
## Стек технологий
- Spring
- Spring Boot
- REST API
- Hibernate
- PostgreSQL
- JUnit 5
- Liquibase
- Swagger