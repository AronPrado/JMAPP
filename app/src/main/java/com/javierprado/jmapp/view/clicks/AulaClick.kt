package com.javierprado.jmapp.view.clicks

import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Horario

interface AulaClick {
    fun onAulaClicker(aula: Aula?)
}