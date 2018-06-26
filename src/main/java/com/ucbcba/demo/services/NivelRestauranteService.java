package com.ucbcba.demo.services;

import com.ucbcba.demo.entities.NivelRestaurante;

public interface NivelRestauranteService {

    Iterable<NivelRestaurante> listAllNivelRestaurantes();

    void saveNivelRestaurante(NivelRestaurante comment);

    NivelRestaurante getNivelRestaurante(Integer id);

    void deleteNivelRestaurante(Integer id);

}
