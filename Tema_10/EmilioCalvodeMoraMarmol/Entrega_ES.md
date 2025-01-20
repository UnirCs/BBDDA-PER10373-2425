### Parte I) Generar un alias
- 1) Genera un alias para el indice employees, que se llamará employees-alias. A partir de ahora realizaremos las consultas siempre sobre este alias y no sobre el índice original.

```
curl --location 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/_aliases' \
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


### Parte II) Inserción de elementos
- 1) Inserta un nuevo elemento en el índice utilizando tus datos (puedes inventartelos si lo consideras). Guarda el ID de documento que has obtenido tras la creacion del elemento.
```
curl --location 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_doc' \
--header 'Content-Type: application/json' \
--data '{
   "FirstName":"Emilio",
   "LastName":"Calvo de Mora Marmol",
   "Designation":"Sorcerer Supreme",
   "Salary":"20",
   "DateOfJoining":"2025-01-20",
   "Address":"177A Bleecker Street, New York City",
   "Gender":"Male",
   "Age":27,
   "MaritalStatus":"Situationship",
   "Interests":"Mystic Arts, Surgeries"
}'
```



### Parte III) Obtención simple de elementos
- 1) Utilizando el ID del paso anterior, obtén los datos de tu registro. Deberías obtener lo mismo que anteriormente escribiste.
```
curl --location 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_doc/miaVhJQBan2_MVtrNUyH'
```



### Parte IV) Eliminación de elementos
- 1) Elimina el elemento que has creado anteriormente.
``` 
curl --location --request DELETE 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_doc/miaVhJQBan2_MVtrNUyH'
```



### Parte V) Consultas
- 1) Obtener empleados cuyo puesto sea ``Software Engineer``.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "term": {
            "Designation": {
                "value": "Software Engineer"
            }
        }
    }
}'
```

- 2) Obtener empleados cuyo puesto NO sea ``Software Engineer``.
```
curl --location 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "bool" : {
            "must_not" : {
                "term": {
                    "Designation": {
                        "value": "Software Engineer"
                    }
                }
            }
        }
    }
}'
```

- 3) Obtener la primera página de empleados cuya ``designation`` sea ``Software Engineer`` asumiendo un tamaño de página de 35 elementos.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 35,
    "query": {
        "term": {
            "Designation": {
                "value": "Software Engineer"
            }
        }
    }
}'
```

- 4) Obtener la tercera página de empleados cuya ``designation`` sea ``Software Engineer`` asumiendo un tamaño de página de 35 elementos.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "from": 105,
    "size": 35,
    "query": {
        "term": {
            "Designation": {
                "value": "Software Engineer"
            }
        }
    }
}'
```

- 5) Obtener los primeros 13 empleados cuyo salario sea mayor a 67.000 dólares.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 13,
    "query": {
        "range": {
            "Salary": {
                "gt": "67000"
            }
        }
    }
}'
```

- 6) Obtener el número total que hayan entrado en la empresa en el mes de Mayo del año 2003. No se pide una consulta específica, solo saber el número total de hits.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "query": {
        "range": {
            "DateOfJoining": {
                "format": "yyyy-MM-dd",
                "gte": "2003-05-01",
                "lt": "2003-06-01"
            }
        }
    }
}'
```

- 7) Obtener empleados cuyo nombre sea ``NATALIE``.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "FirstName": "NATALIE"
        }
    }
}'
```

- 8) Obtener empleados cuya dirección sea o contenga ``Street``.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "multi_match": {
            "query": "Street",
            "type": "bool_prefix",
            "fields": [
                "Address"
            ]
        }
    }
}'
```

- 9) Obtener empleados cuya dirección sea o contenga ``wood``.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "multi_match": {
            "query": "wood",
            "type": "bool_prefix",
            "fields": [
                "Address"
            ]
        }
    }
}'
```

- 10) Obtener empleados interesados en ``Wrestling``.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "multi_match": {
            "query": "wrestling",
            "type": "bool_prefix",
            "fields": [
                "Interests"
            ]
        }
    }
}'
```

- 11) Obtener el número de hombres y mujeres interesad@s en ``Wrestling``.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "query": {
        "multi_match": {
            "query": "wrestling",
            "type": "bool_prefix",
            "fields": [
                "Interests"
            ]
        }
    },
    "aggs": {
        "Generos": {
            "terms": {
                "field": "Gender"
            }
        }
    }
}'
```

- 12) En base a la consulta anterior, obtener la edad media de cada grupo (grupo hombres y grupo mujeres).
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
   "size":0,
   "query":{
      "multi_match":{
         "query":"wrestling",
         "type":"bool_prefix",
         "fields":[
            "Interests"
         ]
      }
   },
   "aggs":{
      "Generos":{
         "terms":{
            "field":"Gender"
         },
         "aggs":{
            "Edad media":{
               "avg":{
                  "field":"Age"
               }
            }
         }
      }
   }
}'
```

- 13) Obtener el número de empleados en función de los siguientes tramos de salario: menor de 60.000 dólares (``tramo 1``), entre 60.000 dólares y 67.000 dólares (``tramo 2``) y superior a 67.000 dólares (``tramo 3``).
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "aggs": {
        "Banda salarial": {
            "range": {
                "field": "Salary",
                "ranges": [
                    {"key": "tramo 1", "from": 0, "to": 60000},
                    {"key": "tramo 2", "from": 60000, "to": 67000},
                    {"key": "tramo 3", "from": 67000}
                ]
            }
        }
    }
}'
```

- 14) En base a la consulta anterior, para cada tramo, hallar el número de empleados que están casados y no casados.
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "size": 0,
    "aggs": {
        "Banda salarial": {
            "range": {
                "field": "Salary",
                "ranges": [
                    {"key": "tramo 1", "to": 60000},
                    {"key": "tramo 2", "from": 60000, "to": 67000},
                    {"key": "tramo 3", "from": 67000}
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
}'
```



### Parte VI) Crear otro índice y modificar el alias
- 1) Crea un nuevo índice de la misma forma que hiciste al principio, pero ahora llámalo ``employees-v2`` y mete en él todos los datos del fichero de prueba.  

    ```
    curl --location 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/_aliases' \
    --header 'Content-Type: application/json' \
    --data '{
        "actions": [
            {
                "add": {
                    "index": "employees",
                    "alias": "employees-v2"
                }
            }
        ]
    }'
  
    ```
       curl -XPUT 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/_bulk' --data-binary @Employees_raw.json -H 'Content-Type: application/json'  


2) Modifica el alias ``employees-alias`` que creaste antes para que apunte tanto al índice ``employees`` original como al nuevo ``employees-v2``.Puedes comprobar que lo has hecho correctamente ejecutando la operación "Obtener todos los alias" de la colección de Postman.

```
curl --location 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/_aliases' \
--header 'Content-Type: application/json' \
--data '{
  "actions": [
      {
          "add": {
              "index": "employees",
              "alias": "employees-v2"
          }
      },
    {
      "add": {
        "index": "employees",
        "alias": "employees-alias"
      }
    }
  ]
}'
```

```
curl --location 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/_aliases'
```

- 2) Realiza alguna de las consultas anteriores. ¿Qué observas?
```
curl --location --request GET 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
   "size":0,
   "query":{
      "multi_match":{
         "query":"wrestling",
         "type":"bool_prefix",
         "fields":[
            "Interests"
         ]
      }
   },
   "aggs":{
      "Generos":{
         "terms":{
            "field":"Gender"
         },
         "aggs":{
            "Edad media":{
               "avg":{
                  "field":"Age"
               }
            }
         }
      }
   }
}'
```
 <strong>Se obtiene un número de hits duplicado, 308 en vez de los 154 anteriores, dado que el alias se estaría aplicando a ambos índices.</strong>
- 3) Elimina ``employees`` del conjunto de índices a los que hace referencia el alias.
```
curl --location 'https://4ofpsq8k40:owhpxqmhns@a-search-4607360773.us-east-1.bonsaisearch.net:443/_aliases' \
--header 'Content-Type: application/json' \
--data '{
    "actions": [
        {
            "remove": {
                "index": "employees",
                "alias": "employees-alias"
            }
        }
    ]
}'
```