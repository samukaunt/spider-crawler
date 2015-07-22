package br.com.wjaa.spider.importa;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wagner on 15/06/15.
 */
public class ClinicaEntity implements Serializable{

    private static final long serialVersionUID = 7207991135581266227L;

    private Long id;
    private String nome;
    private Short ddd;
    private Long telefone;
    private EnderecoEntity endereco;
    private List<ConvenioCategoriaEntity> convenios;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    public List<ConvenioCategoriaEntity> getConvenios() {
        return convenios;
    }


    public void setConvenios(List<ConvenioCategoriaEntity> convenios) {
        this.convenios = convenios;
    }


    public Short getDdd() {
        return ddd;
    }

    public void setDdd(Short ddd) {
        this.ddd = ddd;
    }

    public Long getTelefone() {
        return telefone;
    }

    public void setTelefone(Long telefone) {
        this.telefone = telefone;
    }


    public EnderecoEntity getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoEntity endereco) {
        this.endereco = endereco;
    }
}
