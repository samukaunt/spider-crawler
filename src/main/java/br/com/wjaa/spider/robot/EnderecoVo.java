package br.com.wjaa.spider.robot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wagner on 15/06/15.
 */
public class EnderecoVo implements Serializable{

    private static final long serialVersionUID = 7207991135581266227L;

    private Long id;
    private String logradouro;
    private Integer numero;
    private String bairro;
    private String localidade;
    private String uf;
    private String complemento;
    private String cep;
    private Double latitude;
    private Double longitude;
    private Long idLogin;
    private String telefone;
    private List<ConvenioVo> convenios;
    private String nomeClinica;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(Long idLogin) {
        this.idLogin = idLogin;
    }


    @Override
    public String toString() {
        return this.logradouro + ", "  + this.numero + " - " + this.localidade + " " + this.uf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void addConvenio(ConvenioVo convenioVo) {
        if (convenios == null){
            this.convenios = new ArrayList<ConvenioVo>();
        }
        this.convenios.add(convenioVo);
    }

    public List<ConvenioVo> getConvenios() {
        return convenios;
    }

    public void setConvenios(List<ConvenioVo> convenios) {
        this.convenios = convenios;
    }

    public String getNomeClinica() {
        return nomeClinica;
    }

    public void setNomeClinica(String nomeClinica) {
        this.nomeClinica = nomeClinica;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnderecoVo that = (EnderecoVo) o;

        return cep != null ? cep.equals(that.cep) : that.cep == null;

    }

    @Override
    public int hashCode() {
        return cep != null ? cep.hashCode() : 0;
    }
}
