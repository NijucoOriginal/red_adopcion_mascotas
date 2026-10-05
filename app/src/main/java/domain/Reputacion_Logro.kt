package domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["id_logro","id_reputacion"],
    foreignKeys = [
        ForeignKey(
            entity = Reputacion::class,
            parentColumns = ["id"],
            childColumns = ["id_reputacion"],
            onDelete=ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Logro::class,
            parentColumns = ["id"],
            childColumns = ["id_logro"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["id_reputacion"]),
        Index(value = ["id_logro"])
    ]
)
data class Reputacion_Logro(val id_logro: Long,
                            val id_reputacion: Long) {
}