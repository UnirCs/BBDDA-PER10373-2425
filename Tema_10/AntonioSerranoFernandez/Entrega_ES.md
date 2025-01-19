# Ejercicio Tema 10: Elasticsearch

1. Genera un alias para el indice employees, que se llamará employees-alias. A partir de ahora realizaremos las consultas siempre sobre este alias y no sobre el índice original.

```sh
curl --location '{{elasticsearch-host}}/_aliases' \
--header 'Content-Type: application/json' \
--data '{
  "actions": [
    {
      "add": {
        "index": "employees",
        "alias": "employees-alias"
      }
    }
  ]
}'
```

2. Inserta un nuevo elemento en el índice utilizando tus datos (puedes inventartelos si lo consideras). Guarda el ID de documento que has obtenido tras la creacion del elemento.

```sh
curl --location '{{elasticsearch-host}}/employees-alias/_doc/' \
--header 'Content-Type: application/json' \
--data '{
    "FirstName": "MARIA",
    "LastName": "GOMEZ",
    "Designation": "Data Scientist",
    "Salary": "850000",
    "DateOfJoining": "2016-05-20",
    "Address": "1234 Maple Avenue, Boston, MA 02108",
    "Gender": "Female",
    "Age": 29,
    "MaritalStatus": "Single",
    "Interests": "Machine Learning,AI Research,Hiking,Reading Fiction,Traveling,Yoga"
}
'
```

3. Utilizando el ID del paso anterior, obtén los datos de tu registro. Deberías obtener lo mismo que anteriormente escribiste.

```sh
curl --location '{{elasticsearch-host}}/employees-alias/_doc/8U_Qe5QBJNYMKW3QJ6oY'
```

4. Elimina el elemento que has creado anteriormente.

```sh
curl --location --request DELETE '{{elasticsearch-host}}/employees-alias/_doc/8U_Qe5QBJNYMKW3QJ6oY'
```

5.1. Obtener empleados cuyo puesto sea Software Engineer.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "term": {
            "Designation": {
                "value": "Software Engineer"
            }
        }
    }
}
'
```

5.2. Obtener empleados cuyo puesto NO sea Software Engineer.

```sh
curl --location '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "bool" : {
            "must_not" : {
                "term" : {
                    "Designation": {
                        "value": "Software Engineer"
                    }
                }
            }
        }
    }
}'
```

5.3. Obtener la primera página de empleados cuya designation sea Software Engineer asumiendo un tamaño de página de 35 elementos.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "from": 0,
    "size": 35,
    "query": {
        "term": {
            "Designation": {
                "value": "Software Engineer"
            }
        }
    }
}
'
```

5.4. Obtener la tercera página de empleados cuya designation sea Software Engineer asumiendo un tamaño de página de 35 elementos.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "from": 0,
    "size": 35,
    "query": {
        "term": {
            "Designation": {
                "value": "Software Engineer"
            }
        }
    }
}
'
```

5.5. Obtener los primeros 13 empleados cuyo salario sea mayor a 67.000 dólares.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 13,
    "query": {
        "range": {
            "Salary": {
                "gt": 67000
            }
        }
    }
}
'
```

5.6. Obtener el número total que hayan entrado en la empresa en el mes de Mayo del año 2003. No se pide una consulta específica, solo saber el número total de hits.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "query": {
        "range": {
            "DateOfJoining": {
                "gte": "2003-05-01",
                "lte": "2003-05-31",
                "format": "yyyy-MM-dd"
            }
        }
    }
}
'
```

5.7. Obtener empleados cuyo nombre sea NATALIE.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "FirstName": "NATALIE"
        }
    }
}
'
```

5.8. Obtener empleados cuya dirección sea o contenga Street.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "multi_match": {
            "query": "Street",
            "type": "bool_prefix",
            "fields": [
                "Address",
                "Address._2gram",
                "Address._3gram"
            ]
        }
    }
}
'
```

5.9. Obtener empleados cuya dirección sea o contenga wood.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "multi_match": {
            "query": "wood",
            "type": "bool_prefix",
            "fields": [
                "Address",
                "Address._2gram",
                "Address._3gram"
            ]
        }
    }
}
'
```

5.10. Obtener empleados interesados en Wrestling.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "Interests": "Wrestling"
        }
    }
}
'
```

5.11. Obtener el número de hombres y mujeres interesad@s en Wrestling.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "query": {
        "match": {
            "Interests": "Wrestling"
        }
    },
    "aggs": {
        "Distribución por género": {
            "terms": {
                "field": "Gender"
            }
        }
    }
}
'
```

5.12. En base a la consulta anterior, obtener la edad media de cada grupo (grupo hombres y grupo mujeres).

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "query": {
        "match": {
            "Interests": "Wrestling"
        }
    },
    "aggs": {
        "Distribución por género": {
            "terms": {
                "field": "Gender"
            },
            "aggs": {
                "Edad media": {
                    "avg": {
                        "field": "Age"
                    }
                }
            }
        }
    }
}
'
```

5.13. Obtener el número de empleados en función de los siguientes tramos de salario: menor de 60.000 dólares (tramo 1), entre 60.000 dólares y 67.000 dólares (tramo 2) y superior a 67.000 dólares (tramo 3).

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "aggs": {
        "Rangos de salario": {
            "range": {
                "field": "Salary",
                "ranges": [
                    { "key": "Menos de 60000 dólares", "to": 60000 },
                    { "key": "Entre 60.000 dólares y 67.000 dólares", "from": 60000, "to": 67000 },
                    { "key": "Superior a 67.000 dólares", "from": 67000 }
                ]
            }
        }
    }
}
'
```

5.14. En base a la consulta anterior, para cada tramo, hallar el número de empleados que están casados y no casados.

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "aggs": {
        "Rangos de salario": {
            "range": {
                "field": "Salary",
                "ranges": [
                    { "key": "Menos de 60000 dólares", "to": 60000 },
                    { "key": "Entre 60.000 dólares y 67.000 dólares", "from": 60000, "to": 67000 },
                    { "key": "Superior a 67.000 dólares", "from": 67000 }
                ]
            },
            "aggs": {
                "Estado civil": {
                    "terms": {
                        "field": "MaritalStatus"
                    }
                }
            }
        }
    }
}
'
```

6.1. Crea un nuevo índice de la misma forma que hiciste al principio, pero ahora llámalo employees-v2 y mete en él todos los datos del fichero de prueba. Modifica el alias employees-alias que creaste antes para que apunte tanto al índice employees original como al nuevo employees-v2. Puedes comprobar que lo has hecho correctamente ejecutando la operación "Obtener todos los alias" de la colección de Postman.

```sh
curl --location '{{elasticsearch-host}}/_aliases' \
--header 'Content-Type: application/json' \
--data '{
    "actions": [
        {
        "add": {
            "index": "employees",
            "alias": "employees-alias"
        }
        },
        {
        "add": {
            "index": "employees-v2",
            "alias": "employees-alias"
        }
        }
    ]
}
'
```

6.2. Realiza alguna de las consultas anteriores. ¿Qué observas?

```sh
curl --location --request GET '{{elasticsearch-host}}/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "FirstName": "NATALIE"
        }
    }
}
'
```

Al ejecutar la consulta sobre empleados con el nombre "NATALIE", se observa que la respuesta contiene dos resultados idénticos, pero provenientes de índices diferentes:
- Uno de los documentos proviene del índice employees.
- El otro documento proviene del índice employees-v2.

Esto se debe a que en el paso 6.1 se creó el índice employees-v2 con los mismos datos que el índice original employees, y luego se configuró el alias employees-alias para que apuntara a ambos índices. Como consecuencia, cualquier consulta ejecutada sobre el alias employees-alias ahora recupera documentos de ambos índices. Esto es, los resultados se han duplicado porque ambos índices contienen la misma información y la consulta se está ejecutando sobre un alias que apunta a ambos. Esto es lo esperado en este escenario, ya que el alias employees-alias ahora consulta en dos fuentes de datos (employees y employees-v2). Si en el futuro employees-v2 tuviera datos actualizados o diferentes, la consulta sobre el alias reflejaría las diferencias entre los índices.

6.3. Elimina employees del conjunto de índices a los que hace referencia el alias.

```sh
curl --location '{{elasticsearch-host}}/_aliases' \
--header 'Content-Type: application/json' \
--data '{
 "actions" : [
    { "remove" : {
        "index" : "employees", 
        "alias" : "employees-alias"
        } 
    }
 ]
}'
```
