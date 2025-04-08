# Informe de cobertura

Este informe presenta los resultados de la cobertura de código obtenida tras la ejecución de las pruebas utilizando JaCoCo.
La cobertura total obtenida es del 46% en cuanto concierne a las (ignorando 912 de 1712 instrucciones), y de un 36% en las ramas (ignorando 46 de 72 ramas lógicas).

### ¿Qué clases/métodos crees que faltan por cubrir con pruebas? 
Habría que cubrir los métodos de otros servicios como ReviewService o FavoriteFlmService, ya que ambos osn susceptibles a crear errores si se tocan los modelos debido a que tiene relaciones basadas en los repositorios y utilizan DTOs.

Para el método getUser() de UserService, se podría crear un test que verifique el comportamiento cuando no se encuentra el usuario.

### ¿Qué clases/métodos crees que no hace falta cubrir con pruebas? 
Los getters y setters de las entidades, así como los métodos que no contienen lógica de negocio, no requieren pruebas exhaustivas. 
Por ejemplo todos los métodos de los modelos recaen en estas categorías.

Servicios como UserComponent o el resto de UserService no tienen una complejidad actualmente que requiera hacer pruebas para ellos.

DatabaseInitializer no necesita pruebas, ya que su función es inicializar la base de datos con funciones que ya estarían probadas en otros lugares.

Los métodos de los controladores que no tienen pruebas, solo hacen redirecciones o utilizan métodos de servicios que ya están probados, por lo que tampoco es necesario desarrollar pruebas para ellos