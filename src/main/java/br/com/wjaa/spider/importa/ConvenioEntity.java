package br.com.wjaa.spider.importa;

import java.util.List;

/**
 * Created by wagner on 15/06/15.
 */
public class ConvenioEntity {

    private Integer id;
    private String nome;
    private Boolean popular;
    private List<ConvenioCategoriaEntity> categorias;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public Boolean getPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

    public List<ConvenioCategoriaEntity> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<ConvenioCategoriaEntity> categorias) {
        this.categorias = categorias;
    }

}
