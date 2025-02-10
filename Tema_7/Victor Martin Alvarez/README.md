# Tema 7 - Victor Martin Alvarez

## Set up

```bash
podman run -d --rm -p3306:3306 -e MYSQL_ROOT_PASSWORD=strong_password --name mysql8 mysql
```

```bash
podman run -d --rm --name redis -p 6379:6379 redis
```

## Install `employees` database

Get the zip from https://github.com/datacharmer/test_db

```bash
unzip employees.zip
```

```bash
cd test_db-master
```

```bash
mysqlsh -h localhost -u root -p < employees.sql
```
