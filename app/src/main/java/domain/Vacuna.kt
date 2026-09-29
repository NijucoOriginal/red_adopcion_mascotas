package domain

import java.time.LocalDate

data class Vacuna(val id: Long,
                  var nombre: String,
                  var dosis: String,
                  var fechaDosis: MutableList<LocalDate>,
                  var fotosCarnetVacunacion: MutableList<String>,
                  var mascotaAsociada: Mascota)
