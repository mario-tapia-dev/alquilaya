package com.api.alquilaya.repositories;

import com.api.alquilaya.models.OficinaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OficinaRepository extends JpaRepository<OficinaModel, Long> {
}
