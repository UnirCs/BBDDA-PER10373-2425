# Ejercicio Spring Data JPA

En el presente archivo se detalla cómo se han llevado a cabo los apartados 2 y 3 del ejercicio.

## Parte 2: Ampliación de repositorios

### Descripción

Se han añadido tres métodos nuevos en el repositorio `EmployeeRepository`, utilizando la **derivación de nombres de Spring Data JPA**. Estos métodos permiten realizar consultas adicionales y facilitan la búsqueda dinámica de empleados.

### Métodos nuevos

#### **Métodos en `EmployeeRepository`**

1. `List<Employee> findByFirstNameAndGender(String firstName, Gender gender);`  
   Devuelve los empleados que coincidan con un nombre y género específicos.

2. `List<Employee> findByGenderAndHireDateBefore(Gender gender, Date hireDate);`  
   Encuentra mujeres contratadas antes de una fecha específica.

3. `List<Employee> findByLastNameAndHireDateBetween(String lastName, Date startDate, Date endDate);`  
   Busca empleados con un apellido dado contratados en un rango de fechas.

#### **Métodos en `DepartmentRepository`**

1. `List<Department> findByDeptNameStartingWith(String prefix);`  
   Devuelve los departamentos cuyo nombre comienza con un prefijo específico.

2. `List<Department> findByDeptNoContaining(String deptNoFragment);`  
   Encuentra los departamentos cuyo número contiene un fragmento específico.

### Consultas dinámicas implementadas

Además de los métodos anteriores, se han utilizado **consultas dinámicas** mediante el uso de `Specification` de Spring Data JPA. Esto permite construir consultas flexibles en tiempo de ejecución basadas en los parámetros proporcionados. En concreto, se han creado las siguientes especificaciones dinámicas:

#### Especificaciones en `EmployeeSpecifications`

1. **Filtrar por nombre**: Busca empleados cuyo nombre coincide parcialmente o completamente con el valor proporcionado.
   ```java
   Specification<Employee> withFirstName(String firstName);
   ```

2. **Filtrar por apellido**: Busca empleados cuyo apellido coincide parcialmente o completamente con el valor proporcionado.
   ```java
   Specification<Employee> withLastName(String lastName);
   ```

3. **Filtrar por género**: Busca empleados con un género específico.
   ```java
   Specification<Employee> withGender(Gender gender);
   ```

4. **Filtrar por fecha de contratación exacta o rango**: Permite buscar empleados contratados en una fecha específica o dentro de un rango de fechas.
   ```java
   Specification<Employee> withHireDate(Date hireDate);
   Specification<Employee> withHireDateBetween(Date startDate, Date endDate);
   ```

5. **Filtrar por fecha de nacimiento**: Busca empleados con una fecha de nacimiento específica.
   ```java
   Specification<Employee> withBirthDate(Date birthDate);
   ```

#### Especificaciones en `DepartmentSpecifications`

1. **Filtrar por nombre parcial**: Busca departamentos cuyo nombre coincide parcial o completamente con el valor proporcionado.
   ```java
   Specification<Department> withDeptName(String deptName);
   ```

2. **Filtrar por número exacto**: Busca departamentos cuyo número coincide exactamente con el valor proporcionado.
   ```java
   Specification<Department> withDeptName(String deptName);
   ```

Estas especificaciones permiten construir consultas combinando múltiples filtros en tiempo de ejecución mediante `Specification.where()` y `.and()`. Véase el archivo `EmployeeController.java`.

## Parte 3: Nuevos endpoints en controladores

### Descripción

Se han creado nuevos endpoints en el controlador `EmployeeController` para exponer la funcionalidad adicional definida en los repositorios. Estos endpoints permiten realizar búsquedas avanzadas de empleados con filtros específicos.

### Cambios en `EmployeeController`

#### **1. Empleados con un nombre y género determinados**
- **URL:** `GET /api/employees/filter/by-name-and-gender`
- **Descripción:** Permite buscar empleados que coincidan con un nombre y género específicos.
- **Parámetros de consulta:**
  - `firstName`: Nombre del empleado.
  - `gender`: Género del empleado.
- **Ejemplo de uso en Postman:**
    - Con método `findByFirstNameAndGender`:
      ```http
      GET http://localhost:8088/api/employees/filter/by-name-and-gender?firstName=Taylor&gender=F
      ```
    - Mediante consulta dinámica:
      ```http
      GET http://localhost:8088/api/employees?firstName=Taylor&gender=F
      ```  
- **Respuesta:** Lista de empleados que coinciden con el nombre y género indicados.

#### **2. Mujeres contratadas antes de una fecha específica**
- **URL:** `GET /api/employees/filter/women-hired-before`
- **Descripción:** Devuelve todas las mujeres contratadas antes de una fecha dada.
- **Parámetros de consulta:**
  - `hireDate`: Fecha límite para la contratación (formato `YYYY-MM-DD`).
- **Ejemplo de uso en Postman:**
    - Con método `findByGenderAndHireDateBefore`:
      ```http
      GET http://localhost:8088/api/employees/filter/women-hired-before?hireDate=1990-01-01
      ```
    - Mediante consulta dinámica:
      ```http
      GET http://localhost:8088/api/employees?gender=F&hiredBefore=1990-01-01
      ```  
- **Respuesta:** Lista de mujeres contratadas antes de la fecha indicada.

#### **3. Empleados con un apellido específico contratados en un rango de fechas**
- **URL:** `GET /api/employees/filter/by-lastname-and-hiredate`
- **Descripción:** Busca empleados con un apellido específico que fueron contratados dentro de un rango de fechas.
- **Parámetros de consulta:**
  - `lastName`: Apellido del empleado.
  - `startDate`: Fecha de inicio del rango (formato `YYYY-MM-DD`).
  - `endDate`: Fecha de fin del rango (formato `YYYY-MM-DD`).
- **Ejemplo de uso en Postman:**
    - Con método `findByLastNameAndHireDateBetween`:
      ```http
      GET http://localhost:8088/api/employees/filter/by-lastname-and-hiredate?lastName=Smith&startDate=2005-01-01&endDate=2020-01-01
      ```
    - Mediante consulta dinámica:
      ```http
      GET http://localhost:8088/api/employees?lastName=Smith&hiredAfter=2005-01-01&hiredBefore=2020-01-01
      ```  
- **Respuesta:** Lista de empleados que coinciden con los criterios especificados.

### Cambios en `DepartmentController`

#### **1. Departamentos cuyo nombre comienza con un prefijo**
- **URL:** `GET /api/departments/filter/by-prefix`
- **Descripción:** Permite buscar departamentos cuyo nombre comienza con un prefijo específico.
- **Ejemplo de uso en Postman:**
    - Con método `findByDeptNameStartingWith`:
      ```http
      GET http://localhost:8088/api/departments/filter/by-prefix?prefix=Data
      ```
    - Mediante consulta dinámica:
      ```http
      GET http://localhost:8088/api/departments?deptName=Data
      ```
- **Respuesta:** Lista de departamentos cuyo nombre comienza con el prefijo indicado.

#### **2. Departamentos cuyo número contiene un fragmento**
- **URL:** `GET /api/departments/filter/by-number-fragment`
- **Descripción:** Permite buscar departamentos cuyo número contiene un fragmento específico.
- **Ejemplo de uso en Postman:**
    - Con método `findByDeptNoContaining`:
      ```http
      GET http://localhost:8088/api/departments/filter/by-number-fragment?deptNoFragment=12
      ```
    - Mediante consulta dinámica:
      ```http
      GET http://localhost:8088/api/departments?deptNoFragment=12
      ```
- **Respuesta:** Lista de departamentos cuyo número contiene el fragmento proporcionado.

#### **3. Departamentos con empleados asignados**
- **URL:** `GET /api/departments/with-employees`
- **Descripción:** Devuelve todos los departamentos que tienen al menos un empleado asignado.
- **Ejemplo de uso en Postman:**
    - Con método `hasEmployees`:
      ```http
      GET http://localhost:8088/api/departments/with-employees
      ```
    - Mediante consulta dinámica:
      ```http
      GET http://localhost:8088/api/departments?hasEmployees=true
      ```
- **Respuesta:** Lista de departamentos con empleados asignados.

#### **4. Departamentos con managers asignados**
- **URL:** `GET /api/departments/with-managers`
- **Descripción:** Devuelve todos los departamentos que tienen al menos un manager asignado.
- **Ejemplo de uso en Postman:**
    - Con método `hasManagers`:
      ```http
      GET http://localhost:8088/api/departments/with-managers
      ```
    - Mediante consulta dinámica:
      ```http
      GET http://localhost:8088/api/departments?hasManagers=true
      ```
- **Respuesta:** Lista de departamentos con managers asignados.
