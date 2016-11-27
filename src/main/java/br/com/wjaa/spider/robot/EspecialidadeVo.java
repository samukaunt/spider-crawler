package br.com.wjaa.spider.robot;

import java.io.Serializable;

/**
 * Created by wagner on 15/06/15.
 */
public class EspecialidadeVo implements Serializable {

    private static final long serialVersionUID = -315464438387192274L;
    private Integer id;
    private String nome;
    private Boolean popular;

    public EspecialidadeVo() {
    }

    public EspecialidadeVo(String nome) {
        this.nome = nome;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EspecialidadeVo that = (EspecialidadeVo) o;

        return nome.equals(that.nome);

    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }

    @Override
    public String toString() {
        return "EspecialidadeVo{" +
                "nome='" + nome + '\'' +
                '}';
    }
}
