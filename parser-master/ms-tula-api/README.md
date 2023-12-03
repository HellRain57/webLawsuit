# schema.provision.pom.xml

Файл для регистрации AVRO схем в реестре схем.

Использует плагин `kafka-schema-registry-maven-plugin` от компании Confluent. Полный список команд, реализуемых плагином, приведён в [документации](https://docs.confluent.io/platform/current/schema-registry/develop/maven-plugin.html#sr-maven-plugin).


Все команды в примерах ниже запускаются из корневого каталога проекта `ms-profile-core`.


* Регистрация в локальном реестре по адресу `http://localhost:8081`:

```shell
mvn -f ms-tula-api/schema.provision.pom.xml schema-registry:register
```

* Регистрация в локальном реестре по произвольному адресу, указанному в командной строке:

```shell
mvn -f ms-tula-api/schema.provision.pom.xml \
  -Dschema.registry.url=http://localhost:8081 \
  schema-registry:register
```