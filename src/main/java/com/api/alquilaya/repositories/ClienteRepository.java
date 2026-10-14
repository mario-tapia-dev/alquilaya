package com.api.alquilaya.repositories;

import com.api.alquilaya.models.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel, Long> {

    Optional<ClienteModel> findByEmail(String email);

    Optional<ClienteModel> findByLicenciaConducir(String licenciaConducir);
}
