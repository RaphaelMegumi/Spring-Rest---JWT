package br.com.springrest.repositories;

import br.com.springrest.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u WHERE u.login = ?1")
    Usuario findUserByLogin(String login);
}
