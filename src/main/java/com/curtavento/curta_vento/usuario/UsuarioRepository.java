package com.curtavento.curta_vento.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    java.util.Optional<Usuario> findByEmail(String email);

}