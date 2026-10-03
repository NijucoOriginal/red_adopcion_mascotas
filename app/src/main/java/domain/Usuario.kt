package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/*@Entity(foreignKeys = [
    ForeignKey(
        entity = Reputacion:: class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("reputacion"),
        onDelete = ForeignKey.CASCADE
    )])


 */

@Entity
data class Usuario(
                   @PrimaryKey(autoGenerate = true) val id: Long,
                   var nombre_usuario: String,
                   val correo_electronico: String,
                   var contrasenia: String,
                   val rol: Rol,
                   var codigo_multiproposito: String?,
                   var primer_nombre: String?,
                   var segundo_nombre: String?,
                   var primer_apellido: String?,
                   var segundo_apellido: String?,
                   var direccion: String?,
                   var telefono: String?,
                   var estado_usuario: EstadoUsuario,
                   //val reputacion: Long
) {
}