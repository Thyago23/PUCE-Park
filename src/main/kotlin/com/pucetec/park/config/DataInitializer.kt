package com.pucetec.park.config

import com.pucetec.park.entities.EstadoPuesto
import com.pucetec.park.entities.PuestoParqueo
import com.pucetec.park.entities.ZonaParqueo
import com.pucetec.park.repositories.PuestoParqueoRepository
import com.pucetec.park.repositories.ZonaParqueoRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataInitializer(
    private val zonaRepository: ZonaParqueoRepository,
    private val puestoRepository: PuestoParqueoRepository,
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    private data class ZonaDef(val nombre: String, val descripcion: String, val ubicacion: String, val filas: List<FilaDef>)
    private data class FilaDef(val fila: String, val numeros: List<String>)

    private val zonas = listOf(
        ZonaDef("Zona A", "Zona norte - edificio principal", "Bloque A, planta baja", listOf(
            FilaDef("A", listOf("A-01","A-02","A-03","A-04","A-05")),
            FilaDef("B", listOf("A-06","A-07","A-08","A-09","A-10")),
        )),
        ZonaDef("Zona B", "Zona sur - biblioteca", "Bloque B, planta baja", listOf(
            FilaDef("A", listOf("B-01","B-02","B-03","B-04","B-05")),
            FilaDef("B", listOf("B-06","B-07","B-08","B-09","B-10")),
        )),
        ZonaDef("Zona C", "Zona este - laboratorios", "Bloque C, lateral derecho", listOf(
            FilaDef("A", listOf("C-01","C-02","C-03","C-04","C-05")),
            FilaDef("B", listOf("C-06","C-07","C-08","C-09","C-10")),
        )),
    )

    @Transactional
    override fun run(args: ApplicationArguments) {
        if (zonaRepository.count() > 0L) {
            logger.info("DataInitializer: data already present, skipping.")
            return
        }
        logger.info("DataInitializer: seeding zones and spaces...")
        for (def in zonas) {
            val zona = zonaRepository.save(ZonaParqueo(
                nombre = def.nombre,
                descripcion = def.descripcion,
                ubicacion = def.ubicacion,
                capacidadMaxima = def.filas.sumOf { it.numeros.size },
            ))
            var orden = 1
            for (filaDef in def.filas) {
                for (numero in filaDef.numeros) {
                    puestoRepository.save(PuestoParqueo(
                        zona = zona,
                        numeroPuesto = numero,
                        fila = filaDef.fila,
                        orden = orden++,
                        estado = EstadoPuesto.DISPONIBLE,
                    ))
                }
            }
            logger.info("DataInitializer: seeded zone '${def.nombre}' with ${def.filas.sumOf { it.numeros.size }} spaces.")
        }
        logger.info("DataInitializer: done.")
    }
}
