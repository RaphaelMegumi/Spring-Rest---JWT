package br.com.springrest.security;

import br.com.springrest.ApplicationContextLoad;
import br.com.springrest.models.Usuario;
import br.com.springrest.repositories.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Service
@Component
public class JWTTokenAutenticacaoService {

    /*Tempo de validade do token em mili segundos, neste caso 172800000 são 2 dias*/
    private static final long EXPIRATION_TIME = 172800000;

    /*Uma senha única para compor a autenticação e ajudar na segurança*/
    private static final String SECRET = "SenhaExtremamenteSecreta";

    /*Prefixo padrão de Token (GERALMENTE ESSE É PADRÃO)*/
    private static final String TOKEN_PREFIX = "Bearer";

    /*GERALMENTE ESSE É PADRÃO TAMBÉM*/
    private static final String HEADER_STRING = "Authorization";

    /*Gerando Token de autenticação e adicionando ao cabeçalho e resposta Http*/
    // Gerando token de autenticação e adicionando ao cabeçalho e resposta Http
    public void addAuthentication(HttpServletResponse response, String username) throws IOException {

        // Montagem do Token
        String JWT = Jwts.builder() // gerador de token
                .setSubject(username) // adiciona usuário
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // tempo expiração
                .signWith(SignatureAlgorithm.HS512, SECRET) // algoritmo de geração de senha
                .compact(); // compactação String

        String token = TOKEN_PREFIX + " " + JWT; // Bearer 3498hih345jkh345ui53iu5hyi

        // adiciona token no cabeçalho http
        response.addHeader(HEADER_STRING, token); // Authorization: Bearer 3498hih345jkh345ui53iu5hyi

        liberacaoCors(response);

        // adiciona token como resposta no corpo do http
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }

    /*Retorna o usuário validado com token ou caso não seja validado retorna null*/
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response){

        /*Pega o token enviado no cabeçalho Http*/
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {

            String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

            /*faz a validação do token do usu[ario na requisição*/
            String user = Jwts.parser()
                    .setSigningKey(SECRET)  /*Aqui vem assim a resposta: Bearer i345h6kj43klndngiqpijfasdnfip235*/
                    .parseClaimsJws(tokenLimpo)   /*Ai aqui já vem assim: i345h6kj43klndngiqpijfasdnfip235*/
                    .getBody()
                    .getSubject();   /*E aqui ja sai assim a resposta: João Silva*/

            if (user != null) {
                Usuario usuario = ApplicationContextLoad
                        .getApplicationContext()
                        .getBean(UsuarioRepository.class)
                        .findUserByLogin(user);

                if (usuario != null) {

                    if(tokenLimpo.equalsIgnoreCase(usuario.getToken())) {

                        return new UsernamePasswordAuthenticationToken(
                                usuario.getLogin(),
                                usuario.getSenha(),
                                usuario.getAuthorities());
                    }
                }
            }
        }
        liberacaoCors(response);
        return null;
    }

    // CORS policy
    private void liberacaoCors(HttpServletResponse response) {
        if (response.getHeader("Access-Control-Allow-Origin") == null) {
            response.addHeader("Access-Control-Allow-Origin", "*");
        }

        if (response.getHeader("Access-Control-Allow-Headers") == null) {
            response.addHeader("Access-Control-Allow-Headers", "*");
        }

        if (response.getHeader("Access-Control-Request-Headers") == null) {
            response.addHeader("Access-Control-Request-Headers", "*");
        }

        if (response.getHeader("Access-Control-Allow-Methods") == null) {
            response.addHeader("Access-Control-Allow-Methods", "*");
        }
    }
}
