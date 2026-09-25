package com.curtavento.curta_vento.usuario;

import com.curtavento.curta_vento.municipio.Municipio;
import com.curtavento.curta_vento.usina.Usina;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senha;

    @Column(nullable = false)
    private String perfil;

    // Relacionamentos restaurados
    @ManyToOne
    @JoinColumn(name = "usina_id")
    private Usina usina;

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    public Usuario() {
    }

    // --- GETTERS E SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public Usina getUsina() { return usina; }
    public void setUsina(Usina usina) { this.usina = usina; }
    public Municipio getMunicipio() { return municipio; }
    public void setMunicipio(Municipio municipio) { this.municipio = municipio; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // O prefixo "ROLE_" é obrigatório aqui para o Spring Security abrir a porta
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.perfil));
    }

    @Override
    public String getPassword() { return this.senha; }

    @Override
    public String getUsername() { return this.email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}