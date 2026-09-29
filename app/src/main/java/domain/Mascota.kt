package domain

data class Mascota(
    val id: Long,
    var nombre: String,
    var tipoAnimal: TipoAnimal,
    var altura: Double,
    var peso: Double,
    var longitud: Double,
    val vacunas: MutableList<Vacuna>,
)