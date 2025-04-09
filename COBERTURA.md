# Informe de cobertura

Este informe presenta los resultados de la cobertura de código obtenida tras la ejecución de las pruebas utilizando JaCoCo.

La cobertura total obtenida es del 46% en cuanto concierne a las instrucciones (912 / 1712 instrucciones), y de un 36% en las ramas (46 / 72 ramas).

### ¿Qué clases / métodos crees que faltan por cubrir con pruebas?

Habría que cubrir los métodos de otros servicios, como `ReviewService` o `FavoriteFilmService`, ya que ambos son susceptibles de generar errores si se modifican los modelos, debido a que tienen relaciones basadas en los repositorios y utilizan DTOs.

Para el método `getUser()` de `UserService`, se podría crear un test que valide el comportamiento cuando el usuario no es encontrado.

### ¿Qué clases / métodos crees que no hace falta cubrir con pruebas? 

Los getters y setters de las entidades, así como los métodos que **no** contienen lógica de negocio, no requieren pruebas exhaustivas. Esto aplica, por ejemplo, a todos los métodos de los modelos, ya que pertenecen a estas categorías.

Por otro lado, servicios como `UserComponent` o las partes restantes de `UserService` no presentan actualmente una complejidad que justifique la creación de pruebas específicas.

En cuanto a `DatabaseInitializer`, no es necesario incluir pruebas, dado que su única responsabilidad es inicializar la base de datos utilizando funciones que ya están cubiertas por otras pruebas.

Finalmente, los métodos de los controladores que aún no cuentan con pruebas únicamente realizan redirecciones o invocan métodos de servicios que ya han sido probados, por lo que tampoco se considera necesario desarrollar pruebas adicionales para ellos.