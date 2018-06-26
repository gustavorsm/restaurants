package com.ucbcba.demo.services;

import com.ucbcba.demo.entities.NivelRestaurante;
import com.ucbcba.demo.repositories.NivelRestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NivelRestauranteServiceImpl implements NivelRestauranteService {

    private NivelRestauranteRepository nivelRestauranteRepository;

    @Autowired
    @Qualifier(value = "nivelRestauranteRepository")
    public void setNivelRestauranteRepository(NivelRestauranteRepository nivelRestauranteRepository) {
        this.nivelRestauranteRepository = nivelRestauranteRepository;
    }

    @Override
    public Iterable<NivelRestaurante> listAllNivelRestaurantes() {
        return nivelRestauranteRepository.findAll();
    }

    @Override
    public void saveNivelRestaurante(NivelRestaurante nivelRestaurante) {
        nivelRestauranteRepository.save(nivelRestaurante);
    }

    @Override
    public NivelRestaurante getNivelRestaurante(Integer id) {
        return nivelRestauranteRepository.findOne(id);
    }

    @Override
    public void deleteNivelRestaurante(Integer id) {
        nivelRestauranteRepository.delete(id);
    }

}
