package com.api.alquilaya.repositories;

import com.api.alquilaya.models.VehiculoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<VehiculoModel, Long> {

    Optional<VehiculoModel> findByMatricula(String matricula);

    boolean existsByOficina_IdOficina(Long idOficina);

    long countByEstado(String estado);
}
