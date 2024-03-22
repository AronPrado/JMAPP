import androidx.room.Entity
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Estudiante
import java.io.Serializable

class Calificacion : Serializable {
    var calificacionId: Int = 0
    var calificacion1: Int = 0
    var calificacion2: Int = 0
    var calificacion3: Int = 0
    var calificacion4: Int = 0

    var calificacionFinal: Double = 0.0

    lateinit var curso: Curso
    lateinit var estudiante: Estudiante
}
