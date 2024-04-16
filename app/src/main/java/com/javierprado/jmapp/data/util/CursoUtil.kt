package com.javierprado.jmapp.data.util

import android.graphics.Color
import com.javierprado.jmapp.R

class CursoUtil {
    companion object {

            //COLORES
        //CURSOS
        private val m1 = Color.parseColor("#2a34c1") ; private val m2 = Color.parseColor("#FDFFFC")
        private val e1 = Color.parseColor("#655f68") ; private val e2 = Color.parseColor("#D6EAE8")
        private val a1 = Color.parseColor("#44A1A0") ; private val a2 = Color.parseColor("#FFFFFF")
        private val i1 = Color.parseColor("#E7BB41") ; private val i2 = Color.parseColor("#FF2FBFF")
        private val l1 = Color.parseColor("#D33F49") ; private val l2 = Color.parseColor("#EFF0D1")
        private val c1 = Color.parseColor("#00a855") ; private val c2 = Color.parseColor("#4F3130")
        private val h1 = Color.parseColor("#65655E") ; private val h2 = Color.parseColor("#ECFFB0")
        //ASISTENCIA
        private val asA = Color.parseColor("#D9F9A5")
        private val asF = Color.parseColor("#B80C09")
        private val asT = Color.parseColor("#F4AC45")

        private var backAs = hashMapOf("F" to asF, "A" to asA, "T" to asT)
        private var backCs = hashMapOf("m" to m1, "e" to e1, "a" to a1, "i" to i1, "l" to l1, "c" to c1, "h" to h1)
        private var textCs = hashMapOf("m" to m2, "e" to e2, "a" to a2, "i" to i2, "l" to l2, "c" to c2, "h" to h2)
        //IMAGES
        private val imgM = R.drawable.cursom ; private val imgE = R.drawable.cursoe
        private val imgA = R.drawable.cursoa ; private val imgI = R.drawable.cursoi
        private val imgL = R.drawable.cursol ; private val imgC = R.drawable.cursoc
        private val imgH = R.drawable.cursoh
        private var images = hashMapOf("m" to imgM, "e" to imgE, "a" to imgA, "i" to imgI, "l" to imgL, "c" to imgC, "h" to imgH)

        fun getBackgroundColor(item: String, isCurso: Boolean = true ): Int {
            return if(isCurso){
                backCs[item[0].toString().lowercase()] ?: Color.parseColor("#0089e2")
            }else{
                backAs[item[0].toString()] ?: Color.parseColor("#0089e2")
            }
        }
        fun getTextColor(curso: String): Int{ return textCs[curso[0].toString().lowercase()]!! }
        fun getImg(curso: String): Int{ return images[curso[0].toString().lowercase()]!! }
    }
}