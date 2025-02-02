# Entrega Elasticsearch - Parte I

## Crear un alias para el índice `employees`

### Comando cURL

```bash
curl --location 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/_aliases' \
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

### Comando cURL
 ```bash
 curl --location 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_doc' \
--header 'Content-Type: application/json' \
--data '{
   "FirstName":"Manu",
   "LastName":"Quijada",
   "Designation":"Software Architect",
   "Salary":"5000000",
   "DateOfJoining":"2014-01-13",
   "Address":"8445 Green Street Morristown, NJ 07960",
   "Gender":"Male",
   "Age":24,
   "MaritalStatus":"Married",
   "Interests":"R/C Boats,Dolls,Cloud Watching,Animals/pets/dogs,Crocheting,Casino Gambling"
}'
```

### Parte III Obtención simple de elementos

### Comando cURL
 ```bash
curl --location 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_doc/1tJVVZQBIrnWRzWMXPFG'
```

### Parte IV Eliminación de elementos

### Comando cURL
 ```bash
curl --location --request DELETE 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_doc/1tJVVZQBIrnWRzWMXPFG'
```

### Parte V Consultas
### Obtener empleados cuyo puesto sea Software Engineer
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
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
### Obtener empleados cuyo puesto NO sea Software Engineer
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
"query": {
        "bool": {
            "must_not": {
                "match": {
                    "position": "Software Engineer"
                }
            }
        }
    }
}'
```

### Obtener la primera página de empleados cuya designation sea Software Engineer asumiendo un tamaño de página de 35 elementos.
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "Designation": "Software Engineer"
        }
    },
    "size": 35,
    "from": 0
}'

```
### Obtener la tercera página de empleados cuya designation sea Software Engineer asumiendo un tamaño de página de 35 elementos.
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "Designation": "Software Engineer"
        }
    },
    "size": 35,
    "from": 70
}'
```

### Obtener los primeros 13 empleados cuyo salario sea mayor a 67.000 dólares.
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "range": {
            "Salary": {
                "gt" : "67000"
            }
        }
    }
}'
```

### Obtener el número total que hayan entrado en la empresa en el mes de Mayo del año 2003. No se pide una consulta específica, solo saber el número total de hits.
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_count' \
--header 'Content-Type: application/json' \
--data '{"query": {
        "range": {
            "DateOfJoining": {
                "gte": "2003-05-01",
                "lte": "2003-05-31",
                "format": "yyyy-MM-dd"
            }
        }
    }
}'
```

### Obtener empleados cuyo nombre sea NATALIE
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
     "query": {
        "match": {
            "FirstName": "NATALIE"
        }
    }
}'
```

### Obtener empleados cuya dirección sea o contenga Street
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
      "query": {
        "match": {
            "Address": "Street"
        }
    }
}'
```

### Obtener empleados cuya dirección sea o contenga wood
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
      "query": {
        "match": {
            "Address": "Wood"
        }
    }
}'
```

### Obtener empleados interesados en Wrestling.
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "Interests": "Wrestling"
        }
    }
}'
```

### Obtener el número de hombres y mujeres interesad@s en Wrestling
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "Interests": "Wrestling"
        }
    },
    "aggs": {
        "gender_distribution": {
           "terms": {
                "field": "Gender"
            }
        }
    }
}'
```

### En base a la consulta anterior, obtener la edad media de cada grupo (grupo hombres y grupo mujeres).
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "query": {
        "match": {
            "Interests": "Wrestling"
        }
    },
    "aggs": {
        "gender_distribution": {
            "terms": {
                "field": "Gender"
            },
            "aggs": {
                "average_age": {
                    "avg": {
                        "field": "age"
                    }
                }
            }
        }
    }
}
'
```

### Obtener el número de empleados en función de los siguientes tramos de salario: menor de 60.000 dólares (tramo 1), entre 60.000 dólares y 67.000 dólares (tramo 2) y superior a 67.000 dólares (tramo 3).
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
    "aggs": {
        "salary_ranges": {
            "range": {
                "field": "salary",
                "ranges": [
                    { "to": 60000 },
                    { "from": 60000, "to": 67000 },
                    { "from": 67000 }
                ]
            }
        }
    }
}
'
```

### En base a la consulta anterior, para cada tramo, hallar el número de empleados que están casados y no casados.
 ```bash
curl --location --request GET 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-alias/_search' \
--header 'Content-Type: application/json' \
--data '{
  "query": {
    "match": {
      "Interests": "Wrestling"
    }
  },
  "aggs": {
    "TramosSalario": {
      "range": {
        "field": "Salary",
        "ranges": [
          { "to": 60000 },              
          { "from": 60000, "to": 67000 }, 
          { "from": 67000 }              
        ]
      },
      "aggs": {
        "EdadPromedio": {
          "avg": {
            "field": "Age"
          }
        },
        "EstadoCivil": {
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

### Parte VI Crear otro índice y modificar el alias
 ```bash
curl --location --request PUT 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees-v2' \
--header 'Content-Type: application/json' \
--data '{
   "mappings":{
      "properties":{
         "Address":{
            "type":"search_as_you_type"
         },
         "Age":{
            "type":"integer"
         },
         "DateOfJoining":{
            "type":"date",
            "format":"yyyy-MM-dd"
         },
         "Designation":{
            "type":"keyword"
         },
         "FirstName":{
            "type":"text"
         },
         "Gender":{
            "type":"keyword"
         },
         "Interests":{
            "type":"text"
         },
         "LastName":{
            "type":"text"
         },
         "MaritalStatus":{
            "type":"keyword"
         },
         "Salary":{
            "type":"double"
         }
      }
   }
}'

curl --location 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/_aliases' \
--header 'Content-Type: application/json' \
--data '{
    "actions": [
        {
            "add": {
                "index": "employees-v2",
                "alias": "employees-alias"
            }
        }
    ]
}'

La consulta devolverá datos de ambos índices (employees y employees-v2).

curl --location --request DELETE 'https://5riaff3kw6:4ojipnapfh@unir-search-7346039580.us-east-1.bonsaisearch.net:443/employees'
```






