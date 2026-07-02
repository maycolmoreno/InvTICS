package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ISetupInicialUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUsuariosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.SetupInicialUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.UsuariosUseCaseImpl;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.DepartamentoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.RolRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IUsuariosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.UsuariosRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

@Configuration
public class UsuariosConfig {

	@Bean
	IUsuariosUseCase usuariosUseCase(UsuarioRepositorioPuerto repositorio, RolRepositorioPuerto rolesRepositorio,
			DepartamentoRepositorioPuerto departamentosRepositorio, PasswordEncoder passwordEncoder) {
		return new UsuariosUseCaseImpl(repositorio, rolesRepositorio, departamentosRepositorio, passwordEncoder);
	}

	@Bean
	ISetupInicialUseCase setupInicialUseCase(UsuarioRepositorioPuerto usuariosRepositorio,
			RolRepositorioPuerto rolesRepositorio, DepartamentoRepositorioPuerto departamentosRepositorio,
			PasswordEncoder passwordEncoder) {
		return new SetupInicialUseCaseImpl(usuariosRepositorio, rolesRepositorio, departamentosRepositorio,
				passwordEncoder);
	}

	@Bean
	UsuariosRepositorioImpl usuariosRepositorio(IUsuariosJpaRepositorio jpaRepositorio, IUsuariosJpaMapper mapper) {
		return new UsuariosRepositorioImpl(jpaRepositorio, mapper);
	}

}
