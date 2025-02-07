Recuerda que debes crear una carpeta con tu nombre completo y apellidos (no con tu usuario de GitHub) e incluir dentro los ejercicios de este tema.

Ejercicio 1

Parte I
1.- Genera un alias para el indice employees, que se llamará employees-alias. A partir de ahora realizaremos las consultas siempre sobre este alias y no sobre el índice original.

POST {{elasticsearch-host}}/_aliases

{
    "actions": [
        {
            "add": {
                "index": "employees",
                "alias": "employes-alias"
            }    
        }
    ]
}

Parte II
1.- Inserta un nuevo elemento en el índice utilizando tus datos (puedes inventartelos si lo consideras). Guarda el ID de documento que has obtenido tras la creacion del elemento.

POST {{elasticsearch-host}}/employees-alias/_doc

{               
    "FirstName": "JUAN",
    "LastName": "CORTES",
    "Designation": "Data analist",
    "Salary": "69000",
    "DateOfJoining": "2025-01-01",
    "Address": "180 Gran via, BCN 08022",
    "Gender": "Male",
    "Age": 45,
    "MaritalStatus": "Unmarried",
    "Interests": "Running,TV watching, Soccer, Cooking"
}  

Parte III
1.- Utilizando el ID del paso anterior, obtén los datos de tu registro. Deberías obtener lo mismo que anteriormente escribiste.

GET {{elasticsearch-host}}/employees-alias/_doc/1isOyJQB07GcLSbPPnAL

{
    "_index": "employees",
    "_type": "_doc",
    "_id": "1isOyJQB07GcLSbPPnAL",
    "_version": 1,
    "_seq_no": 10001,
    "_primary_term": 1,
    "found": true,
    "_source": {
    "FirstName": "JUAN",
    "LastName": "CORTES",
    "Designation": "Data analist",
    "Salary": "69000",
    "DateOfJoining": "2025-01-01",
    "Address": "180 Gran via, BCN 08022",
    "Gender": "Male",
    "Age": 45,
    "MaritalStatus": "Unmarried",
    "Interests": "Running,TV watching, Soccer, Cooking"
}
}

Parte IV
1.- Elimina el elemento que has creado anteriormente.

DELETE {{elasticsearch-host}}/employees-alias/_doc/1isOyJQB07GcLSbPPnAL

{
    "_index": "employees",
    "_type": "_doc",
    "_id": "1isOyJQB07GcLSbPPnAL",
    "_version": 2,
    "result": "deleted",
    "_shards": {
    "total": 2,
    "successful": 2,
    "failed": 0
    },
    "_seq_no": 10002,
    "_primary_term": 1
}

Parte V

1.- Obtener empleados cuyo puesto sea Software Engineer

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "term": {
            "Designation": {
            "value": "Software Engineer"
            }
        }
    }
}

2.- Obtener empleados cuyo puesto NO sea Software Engineer

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "bool": {
            "must_not": {
                "term": {
                    "Designation": {
                        "value": "Software Engineer"
                    }
                }
            }
        }
    }
}

3.- Obtener la primera página de empleados cuya designation sea Software Engineer asumiendo un tamaño de página de 35 elementos.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "term": {
            "Designation": {
                "value": "Software Engineer"
            }
        }
    },
    "size": 35,
    "from": 0
}

4.- Obtener la tercera página de empleados cuya designation sea Software Engineer asumiendo un tamaño de página de 35 elementos.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "term": {
            "Designation": {
                "value": "Software Engineer"
        }
    }
},
    "size": 35,
    "from": 3
}

5.- Obtener los primeros 13 empleados cuyo salario sea mayor a 67.000 dólares.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "range": {
            "Salary": {
                "gt": 67000
            }
        }
    },
    "size": 13,
    "from": 0
}

6.- Obtener el número total que hayan entrado en la empresa en el mes de Mayo del año 2003. No se pide una consulta específica, solo saber el número total de hits.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "bool": {
            "filter": [
                {
                    "range": {
                        "DateOfJoining": {
                            "gte": "2003-05-01",
                            "lte": "2003-05-31"
                        }
                    }
                }
            ]
        }
    }
}

La respuest verificando los hits es 8:

"hits": {
"total": {
"value": 8,
"relation": "eq"
},

7.- Obtener empleados cuyo nombre sea NATALIE.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "match": {
            "FirstName": "NATALIE"
        }
    }
}

8.- Obtener empleados cuya dirección sea o contenga Street.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "match": {
            "Address": "Street"
        }
    }
}

9.- Obtener empleados cuya dirección sea o contenga wood.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "match": {
            "Address": "wood"
        }
    }      
}

10.- Obtener empleados interesados en Wrestling.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "query": {
        "match": {
            "Interests": "wrestling"
        }
    }
}

11.- Obtener el número de hombres y mujeres interesad@s en Wrestling.

GET {{elasticsearch-host}}/employees-alias/_search

{
    "size": 0,
    "query": {
        "match": {
        "Interests": "Wrestling"
        }
    },
    "aggs": {
        "gender_count": {
            "terms": {
                "field": "Gender"
            },
    "aggs": {
        "edad_media": {
            "avg": {
                "field": "Age"
                    }
                }
            }
        }
    }
}

12.-En base a la consulta anterior, obtener la edad media de cada grupo (grupo hombres y grupo mujeres).

GET {{elasticsearch-host}}/employees-alias/_search

{
  "size": 0,
  "query": {
    "exists": {
      "field": "Salary"
    }
  },
  "aggs": {
    "salario_ranges": {
      "range": {
        "field": "Salary",
        "ranges": [
          {
            "to": 60000,
            "key": "Tramo 1: Menor de 60.000 dólares"
          },
          {
            "from": 60000,
            "to": 67000,
            "key": "Tramo 2: Entre 60.000 y 67.000 dólares"
          },
          {
            "from": 67000,
            "key": "Tramo 3: Superior a 67.000 dólares"
          }
        ]
      }
    }
  }
}

13.- Obtener el número de empleados en función de los siguientes tramos de salario: menor de 60.000 dólares (tramo 1), entre 60.000 dólares y 67.000 dólares (tramo 2) y superior a 67.000 dólares (tramo 3). 

GET {{elasticsearch-host}}/employees-alias/_search

{
  "size": 0,
  "query": {
    "exists": {
      "field": "Salary"
    }
  },
  "aggs": {
    "salario_ranges": {
      "range": {
        "field": "Salary",
        "ranges": [
          {
            "to": 60000,
            "key": "Tramo 1: Menor de 60.000 dólares"
          },
          {
            "from": 60000,
            "to": 67000,
            "key": "Tramo 2: Entre 60.000 y 67.000 dólares"
          },
          {
            "from": 67000,
            "key": "Tramo 3: Superior a 67.000 dólares"
          }
        ]
      }
    }
  }
}

14.- En base a la consulta anterior, para cada tramo, hallar el número de empleados que están casados y no casados.

GET {{elasticsearch-host}}/employees-alias/_search

{
  "size": 0,
  "query": {
    "exists": {
      "field": "Salary"
    }
  },
  "aggs": {
    "salario_ranges": {
      "range": {
        "field": "Salary",
        "ranges": [
          {
            "to": 60000,
            "key": "Tramo 1: Menor de 60.000 dólares"
          },
          {
            "from": 60000,
            "to": 67000,
            "key": "Tramo 2: Entre 60.000 y 67.000 dólares"
          },
          {
            "from": 67000,
            "key": "Tramo 3: Superior a 67.000 dólares"
          }
        ]
      },
      "aggs": {
        "estado_civil": {
          "terms": {
            "field": "MaritalStatus"
          }
        }
      }
    }
  }
}

Parte VI

1:_Crea un nuevo índice de la misma forma que hiciste al principio, pero ahora llámalo employees-v2 y mete en él todos los datos del fichero de prueba. Modifica el alias employees-alias que creaste antes para que apunte tanto al índice employees original como al nuevo employees-v2. Puedes comprobar que lo has hecho correctamente ejecutando la operación "Obtener todos los alias" de la colección de Postman.

Una vez creados los indices y alias, si ejecutamos:

GET {{elasticsearch-host}}/_aliases

Nos muestra los alias actuales:

{
    "employees-v2": {
        "aliases": {
            "employees-alias": {}
        }
    },
    "employees": {
        "aliases": {
            "employees-alias": {}
        }
    }
}

2.- Realiza alguna de las consultas anteriores. ¿Qué observas?

Los resulatdos de las consultas estan duplicados ya que el alias apunta a dos indices diferentes.

Por ejemplo, el resultado de la consulta sobre el nombre Natalie:

{
    "took": 1,
    "timed_out": false,
    "_shards": {
        "total": 2,
        "successful": 2,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 2,
            "relation": "eq"
        },
        "max_score": 8.804874,
        "hits": [
            {
                "_index": "employees",
                "_type": "_doc",
                "_id": "bivYx5QB07GcLSbP7k5F",
                "_score": 8.804874,
                "_source": {
                    "FirstName": "NATALIE",
                    "LastName": "SERVIS",
                    "Designation": "Senior Software Engineer",
                    "Salary": "61000",
                    "DateOfJoining": "2003-09-19",
                    "Address": "34 Kingston St. El Dorado, AR 71730",
                    "Gender": "Female",
                    "Age": 35,
                    "MaritalStatus": "Unmarried",
                    "Interests": "Guitar,Learning A Foreign Language,Blacksmithing,Embroidery,Collecting,Becoming A Child Advocate,Taxidermy"
                }
            },
            {
                "_index": "employees-v2",
                "_type": "_doc",
                "_id": "ZaTex5QB_aW-aRnWkg4f",
                "_score": 8.804874,
                "_source": {
                    "FirstName": "NATALIE",
                    "LastName": "SERVIS",
                    "Designation": "Senior Software Engineer",
                    "Salary": "61000",
                    "DateOfJoining": "2003-09-19",
                    "Address": "34 Kingston St. El Dorado, AR 71730",
                    "Gender": "Female",
                    "Age": 35,
                    "MaritalStatus": "Unmarried",
                    "Interests": "Guitar,Learning A Foreign Language,Blacksmithing,Embroidery,Collecting,Becoming A Child Advocate,Taxidermy"
                }
            }
        ]
    }
}

3.- Elimina employees del conjunto de índices a los que hace referencia el alias.

POST {{elasticsearch-host}}/_aliases

{
    "actions": [
        {
            "remove": {
                "index": "employees",
                "alias": "employees-alias"
            }
        }
    ]
}

Y comprobamos:

GET {{elasticsearch-host}}/_aliases

{
    "employees": {
        "aliases": {}
    },
    "employees-v2": {
        "aliases": {
            "employees-alias": {}
        }
    }
}
