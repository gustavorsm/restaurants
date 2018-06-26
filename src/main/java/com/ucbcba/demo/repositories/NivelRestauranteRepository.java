package com.ucbcba.demo.repositories;

import com.ucbcba.demo.entities.NivelRestaurante;
import org.springframework.data.repository.CrudRepository;
import javax.transaction.Transactional;

@Transactional
public interface NivelRestauranteRepository extends CrudRepository<NivelRestaurante, Integer> {
}
