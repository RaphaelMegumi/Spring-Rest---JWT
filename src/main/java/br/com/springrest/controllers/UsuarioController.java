package br.com.springrest.controllers;

import br.com.springrest.models.Usuario;
import br.com.springrest.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping
    private List<Usuario> findAll(){
        return repository.findAll();
    }

    @GetMapping("{id}")
    private ResponseEntity<Usuario> findById(@PathVariable long id){
        return repository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/")
    private ResponseEntity <Usuario> create (@RequestBody Usuario usuario){
        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        Usuario usuarioSalvo = repository.save(usuario);
        return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
    }

    @PutMapping(value = "/")
    private ResponseEntity update(@RequestBody Usuario usuario){
        Usuario user = repository.findById(usuario.getId()).get();
        if (!user.getSenha().equals(usuario.getSenha())){
            String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
            usuario.setSenha(senhaCriptografada);
        }
        Usuario usuarioSalvo = repository.save(usuario);
        return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    private ResponseEntity<?> delete(@PathVariable long id){
        return repository.findById(id)
                .map(record -> {
                    repository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
