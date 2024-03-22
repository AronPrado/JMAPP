import androidx.room.Entity
import com.javierprado.jmapp.data.entities.Curso
import com.javierprado.jmapp.data.entities.Estudiante
import java.io.Serializable
@Entity
class Calificacion : Serializable {

    var calificacionId: Int? = null

    var calificacion1: Int? = null

    var calificacion2: Int? = null

    var calificacion3: Int? = null

    var calificacion4: Int? = null

    var calificacionFinal: Double = 0.0

    lateinit var curso: Curso
    lateinit var estudiante: Estudiante
}
