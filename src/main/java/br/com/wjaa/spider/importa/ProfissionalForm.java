package br.com.wjaa.spider.importa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by wagner on 12/06/15.
 */
public class ProfissionalForm implements Serializable{

    private static final long serialVersionUID = 8235523726178515224L;
    private Long idLogin;
    private String nome;
    private String numeroRegistro;
    private int [] idEspecialidade;
    private Short ddd;
    private Long celular;
    private String email;
    private String senha;
    private String cpf;
    private String foto;

    public Long getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(Long idLogin) {
        this.idLogin = idLogin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Short getDdd() {
        return ddd;
    }

    public void setDdd(Short ddd) {
        this.ddd = ddd;
    }

    public Long getCelular() {
        return celular;
    }

    public void setCelular(Long telefone) {
        this.celular = celular;
    }

    public int[] getIdEspecialidade() {
        return idEspecialidade;
    }

    public void setIdEspecialidade(int[] idEspecialidade) {
        this.idEspecialidade = idEspecialidade;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
