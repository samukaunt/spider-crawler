package br.com.wjaa.spider.importa;

import java.io.Serializable;

/**
 * Created by wagner on 15/06/15.
 */
public class EspecialidadeEntity implements Serializable {

    private static final long serialVersionUID = -315464438387192274L;
    private Integer id;
    private String nome;
    private Boolean popular;

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
}
