package com.uneatlantico.encuestas.DB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class NutriQuestExecuter{

    private lateinit var rDB: SQLiteDatabase
    private lateinit var wDB: SQLiteDatabase
    private val ct:Context

    constructor(ct: Context){
        this.ct = ct
        rDB = NutriQuestDB(ct).readableDatabase
        wDB = NutriQuestDB(ct).writableDatabase
    }

    fun openRDB() {
        try {
            if(!rDB.isOpen)
                rDB = NutriQuestDB(ct).readableDatabase
        }catch (e:Exception){}

    }

    fun openWDB(){
        try {
            if(!rDB.isOpen)
                wDB = NutriQuestDB(ct).writableDatabase
        }catch (e:Exception){}
    }

    fun closeDB() {
        if (wDB.isOpen) wDB.close()
        if (rDB.isOpen) rDB.close()
    }
    //companion object {

    /**
     * TODO como completar la query
     * necesito todos las posibles respuestas de la tabla RespuestasPosibles con el idPregunta del parametro
     */
    /*fun getQuestion(ct: Context, idPregunta: Int):Pregunta{
        var preguntaE = Pregunta()
        try {
            val db = NutriQuestDB(ct).readableDatabase
            val sql = "SELECT pregunta FROM Preguntas WHERE _id = $idPregunta UNION SELECT respuesta FROM RespuestasPosibles where idPregunta = $idPregunta order by pregunta desc"
            val sql2 = "select p.pregunta pregunta,r.respuesta respuesta from Preguntas p, RespuestasPosibles r where p._id = $idPregunta and r.idPregunta = $idPregunta ORDER BY p._id desc"
            val sql3 = "SELECT p.pregunta, c.idCategoria, c.visibilidad FROM Preguntas p, CategoriaElementoVisibilidad c WHERE p._id = 8 and c.idElemento = 8 and c.tipoElemento = 0 UNION SELECT r.respuesta, c.idCategoria, c.visibilidad FROM RespuestasPosibles r, CategoriaElementoVisibilidad c where r.idPregunta = 8 and r._id = c.idElemento and c.tipoElemento = 1 order by pregunta desc"
            val sql4 = "SELECT * from (select _id, pregunta FROM Preguntas WHERE _id = 8 UNION SELECT _id, respuesta FROM RespuestasPosibles where idPregunta = 8 order by pregunta) t1 left JOIN CategoriaElementoVisibilidad c ON c.idElemento = t1._id"
            val sql5 = "SELECT pregunta, idCategoria, visibilidad from (SELECT * from (select _id, pregunta FROM Preguntas WHERE _id = 8 UNION SELECT _id, respuesta FROM RespuestasPosibles where idPregunta = 8 ) t1  left JOIN CategoriaElementoVisibilidad c ON c.idElemento = t1._id)"
            val sql6=""
            var pregunta:String
            var respuestasPosibles: ArrayList<String> = ArrayList()
            val cursor = db.rawQuery(sql2, null)
            var i = 0
            if(cursor.moveToFirst()){
                pregunta = cursor.getString(cursor.getColumnIndex("pregunta"))
                while (!cursor.isAfterLast) {
                    respuestasPosibles.add(i, cursor.getString(cursor.getColumnIndex("respuesta")))
                    i++
                    cursor.moveToNext()
                }
                preguntaE = Pregunta(idPregunta, pregunta,posiblesRespuestas = respuestasPosibles )
            }
            cursor.close()
            db.close()
        }
        catch (e:Exception){
            Log.d("GetQuestionException", e.message)
        }
        return preguntaE
    }*/

    /**
     * devuelve una pregunta con sus limitaciones
     */
    fun getPregunta(ct: Context, idPregunta: Int): SoloPregunta {
        var pregunta = SoloPregunta()
        try {
            //val db = NutriQuestDB(ct).readableDatabase
            val sql = "select pregunta, idCategoria, visibilidad, minRespuestas, maxRespuestas from (select p._id, pregunta, minRespuestas, maxRespuestas from Preguntas p where _id = $idPregunta) t left join Visibilidad c on idElemento = t._id and tipoElemento = 0"
            val cursor = rDB.rawQuery(sql, null)
            var respuesta:String
            var idCategoria:Int
            var visibilidad:Int
            var minRespuestas:Int
            var maxRespuestas:Int
            //Log.d("sqlyes", sql)
            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast) {
                    respuesta = cursor.getString(cursor.getColumnIndex("pregunta"))
                    idCategoria = cursor.getInt(cursor.getColumnIndex("idCategoria"))
                    visibilidad = cursor.getInt(cursor.getColumnIndex("visibilidad"))
                    minRespuestas =  cursor.getInt(cursor.getColumnIndex("minRespuestas"))
                    maxRespuestas =  cursor.getInt(cursor.getColumnIndex("maxRespuestas"))
                    pregunta = SoloPregunta(_id = idPregunta, pregunta = respuesta, idCategoria = idCategoria, visibilidad = visibilidad, minRespuestas = minRespuestas, maxRespuestas = maxRespuestas)
                    cursor.moveToNext()
                }
            }
            cursor.close()
        }catch (e:Exception){Log.d("getPreguntaException", "r"+e.toString())}
        return pregunta
    }

    /**
     * devuelve todas las respuestas y sus limitaciones para una pregunta dada
     */
    fun getRespuestas( idPregunta: Int):ArrayList<Respuesta>{

        var respuestasPosibles: ArrayList<Respuesta> = ArrayList()
        try {
            //val db = NutriQuestDB(ct).readableDatabase
            //var pregunta:String
            val sql1 = "select distinct t._id _id, t.respuesta respuesta, t.idCategoria determinaCategoria,c.idCategoria categoriaVisibilidad, visibilidad, idPreguntaSiguiente,contestado from (select r._id, respuesta, idCategoria, idPreguntaSiguiente from RespuestasPosibles r where idPregunta = $idPregunta) t left join Visibilidad c on idElemento = t._id and tipoElemento = 1 left join (SELECT idRespuesta ,contestado from Respuestas) re on re.idRespuesta = t._id"
            //"select t._id, t.respuesta, t.idCategoria determinaCategoria,c.idCategoria categoriaVisibilidad, visibilidad, idPreguntaSiguiente,contestado from (select r._id, respuesta, idCategoria, idPreguntaSiguiente from RespuestasPosibles r where idPregunta = $idPregunta) t left join CategoriaElementoVisibilidad c on idElemento = t._id and tipoElemento = 1 left join (SELECT idRespuesta ,contestado from Respuestas) re on re.idRespuesta = t._id"
            //val sql = "select respuesta, t.idCategoria determinaCategoria,c.idCategoria categoriaVisibilidad, visibilidad, idPreguntaSiguiente, contestado from (select r._id, respuesta, idCategoria, idPreguntaSiguiente from RespuestasPosibles r where idPregunta = $idPregunta) t left join CategoriaElementoVisibilidad c on idElemento = t._id and tipoElemento = 1"
            val cursor = rDB.rawQuery(sql1, null)
            var _id:Int
            var respuesta:String
            var determinaCategoria:Int
            var categoriaVisibilidad:Int
            var visibilidad:Int
            var idPreguntaSiguiente:Int
            var contestado:Int
            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast) {
                    _id= cursor.getInt(cursor.getColumnIndex("_id"))
                    respuesta = cursor.getString(cursor.getColumnIndex("respuesta"))
                    determinaCategoria = cursor.getInt(cursor.getColumnIndex("determinaCategoria"))
                    categoriaVisibilidad = cursor.getInt(cursor.getColumnIndex("categoriaVisibilidad"))
                    visibilidad = cursor.getInt(cursor.getColumnIndex("visibilidad"))
                    idPreguntaSiguiente = cursor.getInt(cursor.getColumnIndex("idPreguntaSiguiente"))
                    contestado = cursor.getInt(cursor.getColumnIndex("contestado"))
                    //Log.d("micategoria", determinaCategoria.toString())
                    respuestasPosibles.add(Respuesta(_id = _id, respuesta = respuesta, categoriaVisibilidad = categoriaVisibilidad, determinaCategoria = determinaCategoria, visibilidad = visibilidad, idPreguntaSiguiente = idPreguntaSiguiente, contestadoAnterior = contestado))
                    cursor.moveToNext()
                }
            }
            cursor.close()
            //db.close()
        }catch (e:Exception){Log.d("getRespuestaException", e.toString())}
        return respuestasPosibles
    }

    /**
     * devuelve todas las categorias asignadas al usuario por sus respuestas
     */
    fun getCategoriasUsuario(ct: Context):List<Int>{
        var categoriasUsuario:ArrayList<Int> = ArrayList()
        try{
            //val db = NutriQuestDB(ct).readableDatabase
            val sql = "SELECT idCategoria FROM Respuestas where contestado = 1 and idCategoria != 0"
            val cursor = rDB.rawQuery(sql, null)
            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast) {
                    categoriasUsuario.add(cursor.getInt(cursor.getColumnIndex("idCategoria")))
                    cursor.moveToNext()
                }
            }
            cursor.close()
            // db.close()
        }catch (e:Exception){Log.d("getCatgUsrExcp", " "+e.toString())}
        //Log.d("categorias", categoriasUsuario.toString())
        return categoriasUsuario
    }

    fun getEncuestas():List<Encuesta>{
        val encuestas = ArrayList<Encuesta>()
        val sql = "SELECT * FROM encuestas"
        val cursor = rDB.rawQuery(sql, null)
        var idEncuesta = 0
        var nombre = ""
        var terminado:Int =0
        var tipo:Int = 0
        if(cursor.moveToFirst()) {
            while(!cursor.isAfterLast) {
                idEncuesta = cursor.getInt(cursor.getColumnIndex("idEncuesta"))
                nombre = cursor.getString(cursor.getColumnIndex("nombre"))
                terminado = cursor.getInt(cursor.getColumnIndex("terminado"))
                //preguntaFinal = cursor.getInt(cursor.getColumnIndex("preguntaFinal"))
                tipo = cursor.getInt(cursor.getColumnIndex("tipo"))

            }
            val encuesta = Encuesta(idEncuesta, nombre, tipo, terminado)
            encuestas.add(encuesta)
        }
        cursor.close()
        return encuestas//Encuesta(idEncuesta, nombre, tipo, terminado)
    }

    fun getEncuesta(idEncuesta:Int):Encuesta{
        val sql = "SELECT * FROM encuestas WHERE idEncuesta = $idEncuesta"
        val cursor = rDB.rawQuery(sql, null)
        var idEncuesta = 0
        var nombre = ""
        var terminado:Int =0
        var tipo:Int = 0
        if(cursor.moveToFirst()) {

            idEncuesta = cursor.getInt(cursor.getColumnIndex("idEncuesta"))
            nombre = cursor.getString(cursor.getColumnIndex("nombre"))
            terminado = cursor.getInt(cursor.getColumnIndex("terminado"))
            //preguntaFinal = cursor.getInt(cursor.getColumnIndex("preguntaFinal"))
            tipo = cursor.getInt(cursor.getColumnIndex("tipo"))

        }

        return Encuesta(idEncuesta, nombre, tipo, terminado)
    }

    fun setPregunta(pregunta:PreguntaRaw){
        val sql = "INSERT INTO Preguntas (_id, pregunta, idPreguntaSiguiente, minRespuestas, maxRespuestas) VALUES (${pregunta._id}, '${pregunta.pregunta}', ${pregunta.idPreguntaSiguiente}, ${pregunta.minRespuestas}, ${pregunta.maxRespuestas});"

        wDB.execSQL(sql)
    }

    fun setRespuesta(respuestas:List<RespuestaPosibleRaw>){
        var sql = "INSERT INTO RespuestasPosibles (_id, respuesta, idPregunta, idCategoria, idPreguntaSiguiente) VALUES "
        var posicion = 0
        respuestas.forEach{

            val valuestemp = "(${it._id}, '${it.respuesta}', '${it.idPregunta}', ${it.idCategoria}, ${it.idPreguntaSiguiente})"
            sql += valuestemp
            sql += if(posicion+1 < respuestas.size) "," else ";"
            posicion ++
        }
        wDB.execSQL(sql)
    }

    fun setCategorias(categorias: List<CategoriaRaw>){
        var sql = "INSERT INTO Categorias (_id, categoria) VALUES "
        var posicion = 0
        categorias.forEach{

            val valuestemp = "(${it._id}, '${it.categoria}')"
            sql += valuestemp
            sql += if(posicion+1 < categorias.size) "," else ";"
            posicion ++
        }
        wDB.execSQL(sql)
    }

    fun setVisibilidad(visibilidad:List<VisibilidadRaw>){
        var sql = "INSERT INTO Visibilidad (_id, idElemento, tipoElemento, idCategoria, visibilidad) VALUES "
        var posicion = 0
        visibilidad.forEach{

            val valuestemp = "(${it._id},${it.idElemento}, '${it.tipoElemento}', ${it.idCategoria}, ${it.visibilidad})"
            sql += valuestemp
            sql += if(posicion+1 < visibilidad.size) "," else ";"
            posicion ++
        }
        wDB.execSQL(sql)
    }

    fun setEncuesta(encuesta:EncuestaRaw){
        try {
            var sql = "INSERT INTO encuestas (idEncuesta, idPrimeraPregunta, numeroPreguntas, preguntaFinal, clave) VALUES (${encuesta.idEncuesta}, ${encuesta.idPrimeraPregunta}, ${encuesta.numeroPreguntas}, ${encuesta.preguntaFinal}, '${encuesta.clave}')"
            Log.d("guardarEncuesta", sql)
            wDB.execSQL(sql)
        } catch (efg:Exception){Log.d("setEncuestaExcp", efg.message)}
    }

    fun setEncuesta(encuesta: Encuesta){
        val sql = "INSERT INTO encuestas (idEncuesta, nombre, terminado, tipo) VALUES (${encuesta.idEncuesta}, ${encuesta.nombre},${encuesta.terminado}, ${encuesta.tipo});"
        wDB.execSQL(sql)
    }

    fun updateEncuesta(idEncuesta: Int, terminado:Int){
        val sql = "UPDATE encuestas SET terminado = $terminado  WHERE idEncuesta = $idEncuesta;"
        wDB.execSQL(sql)

    }

    fun guardarRespuesta(ct: Context, idPregunta: Int, respuestas:List<String>){
        try {
            val db = NutriQuestDB(ct).writableDatabase
            var sql = "INSERT INTO Respuestas (idRespuesta, idPregunta) VALUES "
            for(i in 0 until respuestas.size){
                val valuestemp = "('${respuestas[i]}', $idPregunta)"
                sql += valuestemp
                if(i+1 < respuestas.size)
                    sql += ","
            }
            Log.d("insertstatement", sql)
            db.execSQL(sql)
            db.close()
        }
        catch (e:Exception){
            Log.d("excepcionInsertespuesta", e.message)
        }
    }

    fun updateRespuestaEncuesta(idPregunta: Int, idEncuesta: Int){
        try{
            val sql = "UPDATE encuestas SET preguntaFinal = $idPregunta where _id = $idEncuesta"
            Log.d("updateProgreso", sql)
            wDB.execSQL(sql)
        }catch (e:Exception){Log.d("updateProgresoExcp", e.message)}
    }

    fun getProgreso(idEncuesta: Int):Int{
        var idPregunta = 0
        val sql = "SELECT preguntaFinal p FROM encuestas WHERE _id = $idEncuesta"

        val cursor = rDB.rawQuery(sql, null)
        if(cursor.moveToFirst()){
            idPregunta = cursor.getInt(cursor.getColumnIndex("p"))
        }

        return idPregunta
    }

    fun insertRespuestas(respuestas: ArrayList<RespuestaRaw>){
        try {
            //val db = NutriQuestDB(ct).writableDatabase
            var sql = "INSERT INTO Respuestas (idRespuesta, idPregunta, idCategoria, idPreguntaPrevia, idPreguntaPosterior, contestado) VALUES "
            for(i in 0 until respuestas.size){

                val valuestemp = "(${respuestas[i].idRespuesta}, ${respuestas[i].idPregunta}, ${respuestas[i].idCategoria}, ${respuestas[i].idPreguntaPrevia}, ${respuestas[i].idPreguntaPosterior}, ${respuestas[i].contestado})"
                sql += valuestemp
                if (i + 1 < respuestas.size)
                    sql += ","

            }
            Log.d("insertstatement", sql)
            wDB.execSQL(sql)
            //db.close()
        }
        catch (e:Exception){
            Log.d("excepcionInsertRespuest", e.message)
        }
    }

    fun numeroPreguntas():Int{
        var numeroPreguntas:Int = 0
        try{
            //val db = NutriQuestDB(ct).readableDatabase
            val sql = "SELECT COUNT(_id) c FROM Preguntas"
            val cursor = rDB.rawQuery(sql, null)
            if(cursor.moveToFirst()){
                numeroPreguntas = cursor.getInt(cursor.getColumnIndex("c"))
            }
            cursor.close()
            //db.close()
        }catch (e:Exception){Log.d("numeroPreguntasExcp", e.message)}
        return numeroPreguntas
    }

    fun numeroPreguntaSiguiente(ct: Context, idPregunta:Int): IntArray{
        var idPreguntasPosteriores = IntArray(2)
        try{
            //val db = NutriQuestDB(ct).readableDatabase
            val sql = "SELECT idPreguntaSiguiente ip, idPreguntaPosterior ir FROM (SELECT _id,idPreguntaSiguiente from Preguntas where _id = $idPregunta) p left join (SELECT idPreguntaPosterior, idPregunta from Respuestas where idPregunta = $idPregunta and contestado = 1) r on r.idPregunta = p._id"//"SELECT idPreguntaPosterior, idPreguntaSiguiente FROM Respuestas r, Preguntas p where idPregunta = (SELECT idPregunta from respuestas ORDER BY _id desc limit 1) and contestado = 1 and p._id = idPregunta order by r._id desc limit 1"
            val cursor = rDB.rawQuery(sql, null)

            if(cursor.moveToFirst()){
                idPreguntasPosteriores[0] = cursor.getInt(cursor.getColumnIndex("ip"))
                idPreguntasPosteriores[1] = cursor.getInt(cursor.getColumnIndex("ir"))
                Log.d("nextQ", idPregunta.toString())
            }
            cursor.close()
            //db.close()
        }catch (e:Exception){Log.d("numeroPregSigExcp", e.message)}
        return idPreguntasPosteriores
    }



    fun getAllRespuestas(ct: Context):ArrayList<RespuestasUsuario>{
        var respuestasUsuario: ArrayList<RespuestasUsuario> = ArrayList()
        try {
           // val db = NutriQuestDB(ct).readableDatabase
            var pregunta:String
            //val sql1 = "select respuesta, t.idCategoria determinaCategoria,c.idCategoria categoriaVisibilidad, visibilidad from (select r._id, respuesta from RespuestasPosibles r where idPregunta = $idPregunta) t left join CategoriaElementoVisibilidad c on idElemento = t._id and tipoElemento = 1"
            val sql = "select * from Respuestas ORDER BY idPreguntaPrevia"
            val cursor = rDB.rawQuery(sql, null)
            var respuesta:String
            var idPregunta:Int
            var idCategoria: Int
            var idPreguntaPrevia:Int
            var idPreguntaPosterior: Int
            var contestado: Int
            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast) {
                    respuesta = cursor.getString(cursor.getColumnIndex("respuesta"))
                    idPregunta = cursor.getInt(cursor.getColumnIndex("idPregunta"))
                    idCategoria = cursor.getInt(cursor.getColumnIndex("idCategoria"))
                    idPreguntaPrevia = cursor.getInt(cursor.getColumnIndex("idPreguntaPrevia"))
                    idPreguntaPosterior = cursor.getInt(cursor.getColumnIndex("idPreguntaPosterior"))
                    contestado = cursor.getInt(cursor.getColumnIndex("contestado"))
                    //Log.d("micategoria", respuesta.toString())
                    respuestasUsuario.add(RespuestasUsuario(respuesta, idPregunta, idCategoria, idPreguntaPrevia, idPreguntaPosterior, contestado))
                    cursor.moveToNext()
                }
            }
            cursor.close()
            //db.close()
        }catch (e:Exception){Log.d("getRespuestaException", e.message)}
        return respuestasUsuario
    }

    fun existePregunta(id:Int):Boolean{
        var existe = false
        val sql = "Select _id from preguntas where _id = $id;"
        val cursor = rDB.rawQuery(sql, null)
        if(cursor.moveToFirst()){
            if(id == cursor.getInt(cursor.getColumnIndex("_id")))
                existe = true
        }
        return existe
    }


    fun getClave(idEncuesta: Int):String{
        var clave = ""
        try{
            val sql = "SELECT clave FROM encuestas WHERE idEncuesta = $idEncuesta;"
            val cursor = rDB.rawQuery(sql, null)
            if(cursor.moveToFirst()){
                clave = cursor.getString(cursor.getColumnIndex("clave"))
            }
            cursor.close()

        }catch (e:Exception){Log.d("getClaveExp", e.message)}
        return clave
    }

    fun setClave(idEncuesta: Int){
    }

    fun getUsuario():Usuario{
        var usuario = Usuario()
        try {
            // val db = NutriQuestDB(ct).readableDatabase
            //val sql1 = "select respuesta, t.idCategoria determinaCategoria,c.idCategoria categoriaVisibilidad, visibilidad from (select r._id, respuesta from RespuestasPosibles r where idPregunta = $idPregunta) t left join CategoriaElementoVisibilidad c on idElemento = t._id and tipoElemento = 1"
            val sql = "select * from usuario"
            val cursor = rDB.rawQuery(sql, null)
            var idPersona: String
            var nombre: String
            var email: String
            var photoUrl: String
            var idAndroid: String

            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast) {
                    idPersona = cursor.getString(cursor.getColumnIndex("idPersona"))
                    nombre = cursor.getString(cursor.getColumnIndex("nombre"))
                    email = cursor.getString(cursor.getColumnIndex("email"))
                    photoUrl = cursor.getString(cursor.getColumnIndex("photoUrl"))
                    idAndroid = cursor.getString(cursor.getColumnIndex("idAndroid"))

                    usuario = Usuario(idPersona, nombre, email, photoUrl, idAndroid)
                    cursor.moveToNext()
                }
            }
            cursor.close()
            //db.close()
        }catch (e:Exception){Log.d("getRespuestaException", e.message)}
        return usuario
    }

    fun idUsuario():String{
        var idUsuario = ""
        try{
            val sql = "SELECT idPersona FROM usuario"
            val cursor = rDB.rawQuery(sql, null)
            if(cursor.moveToFirst()){
                idUsuario = cursor.getString(cursor.getColumnIndex("idPersona"))
            }
            cursor.close()

        }catch (e:Exception){Log.d("excepcionLeerUsuario", e.message)}
        return idUsuario
    }

    companion object {
        fun insertarUsuario(ct: Context, Usuario:List<String>){

            try {
                val db = NutriQuestDB(ct).writableDatabase
                var sql = "INSERT INTO usuario (idPersona, nombre, email, photoUrl, idAndroid) VALUES ('${Usuario[0]}', '${Usuario[1]}', '${Usuario[2]}', '${Usuario[3]}', '${Usuario[4]}');"
                Log.d("insertarUsuario", sql)
                db.execSQL(sql)
                db.close()
            }
            catch (e:Exception){
                Log.d("excepcionInsertRespuest", e.message)
            }
        }

        fun actualizarUsuario(ct: Context, usuario:List<String>){

            try {
                val db = NutriQuestDB(ct).writableDatabase
                var sql = "UPDATE usuario SET idPersona = '${usuario[0]}', nombre = '${usuario[1]}', email = '${usuario[2]}', photoUrl = '${usuario[3]}', idAndroid = '${usuario[4]}' WHERE _id = 1;"
                db.execSQL(sql)
                db.close()
            }
            catch (e:Exception){
                Log.d("excepcionInsertRespuest", e.message)
            }
        }

        fun idUsuario(ct: Context):String{
            var idUsuario = ""
            try{
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT idPersona FROM usuario"
                val cursor = db.rawQuery(sql, null)
                if(cursor.moveToFirst()){
                    idUsuario = cursor.getString(cursor.getColumnIndex("idPersona"))
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("excepcionLeerUsuario", e.message)}
            return idUsuario
        }

        fun ultimaPregunta(ct:Context):Int{
            var idPreguntaUltima:Int = 0
            try{
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT _id FROM Preguntas WHERE idPreguntaSiguiente = (SELECT idPregunta FROM respuestas order by _id LIMIT 1)"
                val cursor = db.rawQuery(sql, null)
                if(cursor.moveToFirst()){
                    idPreguntaUltima = cursor.getInt(cursor.getColumnIndex("idSiguientePregunta"))
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("ultimaPregExcp", e.message)}
            return idPreguntaUltima
        }

        fun deleteAll(ct: Context){
            val db = NutriQuestDB(ct).writableDatabase
            db.execSQL("DELETE FROM Respuestas")
            db.close()
        }
    }
}