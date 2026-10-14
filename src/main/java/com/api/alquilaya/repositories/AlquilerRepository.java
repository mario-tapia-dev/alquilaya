package com.api.alquilaya.repositories;

import java.time.LocalDate;

import com.api.alquilaya.models.AlquilerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlquilerRepository extends JpaRepository<AlquilerModel, Long> {

    boolean existsByCliente_IdCliente(Long idCliente);

    boolean existsByVehiculo_IdVehiculo(Long idVehiculo);

    // Alquileres del mismo vehiculo que se cruzan con el rango [inicio, fin).
    // Devolver el vehiculo el mismo dia que otro cliente lo recoge NO cuenta como cruce.
    @Query("select count(a) from AlquilerModel a "
            + "where a.vehiculo.idVehiculo = :idVehiculo "
            + "and a.fechaAlquiler < :fin and a.fechaDevolucion > :inicio "
            + "and a.idAlquiler <> :excluirId")
    long contarSolapamientos(@Param("idVehiculo") Long idVehiculo,
                             @Param("inicio") LocalDate inicio,
                             @Param("fin") LocalDate fin,
                             @Param("excluirId") Long excluirId);

    // Alquileres en curso hoy para un vehiculo
    @Query("select count(a) from AlquilerModel a "
            + "where a.vehiculo.idVehiculo = :idVehiculo "
            + "and a.fechaAlquiler <= :hoy and a.fechaDevolucion > :hoy")
    long contarActivos(@Param("idVehiculo") Long idVehiculo, @Param("hoy") LocalDate hoy);

    // Alquileres en curso hoy (todos los vehiculos)
    @Query("select count(a) from AlquilerModel a "
            + "where a.fechaAlquiler <= :hoy and a.fechaDevolucion > :hoy")
    long contarActivosGlobal(@Param("hoy") LocalDate hoy);
}
