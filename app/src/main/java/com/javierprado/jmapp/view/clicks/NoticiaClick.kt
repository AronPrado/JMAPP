package com.javierprado.jmapp.view.clicks

import com.javierprado.jmapp.data.entities.Aula
import com.javierprado.jmapp.data.entities.Horario
import com.javierprado.jmapp.data.entities.Noticia

interface NoticiaClick {
    fun onNoticiaClicker(noticia: Noticia?)
}