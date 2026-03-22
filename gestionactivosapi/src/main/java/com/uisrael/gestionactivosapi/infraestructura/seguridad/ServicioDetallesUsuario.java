package com.uisrael.gestionactivosapi.infraestructura.seguridad;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

@Service
public class ServicioDetallesUsuario implements UserDetailsService {

    @Autowired
    private IUsuariosJpaRepositorio repositorioUsuarios;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        UsuariosJpa usuario = repositorioUsuarios.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        if (!usuario.isEstado()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + correo);
        }

        Collection<GrantedAuthority> autoridades = new ArrayList<>();
        if (usuario.getFkRol() != null && usuario.getFkRol().getNombre() != null) {
            autoridades.add(new SimpleGrantedAuthority("ROLE_" + usuario.getFkRol().getNombre()));
        }

        return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                usuario.isEstado(),
                true,
                true,
                true,
                autoridades
        );
    }
}
