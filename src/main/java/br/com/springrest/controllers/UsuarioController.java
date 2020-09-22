package br.com.springrest.controllers;

import br.com.springrest.models.Usuario;
import br.com.springrest.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    private Usuario criarUsuario (@RequestBody Usuario usuario){
        Usuario usuario1 = new Usuario(usuario.getNome(), usuario.getLogin(), usuario.getSenha());
        return repository.save(usuario1);
    }

    @PutMapping("{id}")
    private ResponseEntity update(@PathVariable long id, @RequestBody Usuario usuario){
        return repository.findById(id)
                .map(record ->{
                    record.setNome(usuario.getNome());
                    record.setLogin(usuario.getLogin());
                    record.setSenha(usuario.getSenha());
                    Usuario updated = repository.save(record);
                    return ResponseEntity.ok().body(updated);
                }).orElse(ResponseEntity.notFound().build());
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
