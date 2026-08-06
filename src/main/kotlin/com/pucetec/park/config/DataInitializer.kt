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

    // Genera 3 filas (A, B, C) de 10 puestos = 30 por zona.
    private fun filas30(prefijo: String): List<FilaDef> {
        val nums = (1..30).map { "$prefijo-${it.toString().padStart(2, '0')}" }
        return listOf(
            FilaDef("A", nums.subList(0, 10)),
            FilaDef("B", nums.subList(10, 20)),
            FilaDef("C", nums.subList(20, 30)),
        )
    }

    private val zonas = listOf(
        ZonaDef("Zona A", "Zona norte - edificio principal", "Bloque A, planta baja", filas30("A")),
        ZonaDef("Zona B", "Zona sur - biblioteca", "Bloque B, planta baja", filas30("B")),
        ZonaDef("Zona C", "Zona este - laboratorios", "Bloque C, lateral derecho", filas30("C")),
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
