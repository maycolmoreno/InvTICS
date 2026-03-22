package com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;

public interface IVincularCustodioConUsuarioUseCase {

    Custodios ejecutar(int idCustodio, int idUsuario);
}
