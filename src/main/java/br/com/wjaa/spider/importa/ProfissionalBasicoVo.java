package br.com.wjaa.spider.importa;

/**
 * Created by wagner on 6/16/16.
 */
public class ProfissionalBasicoVo {

    private Long id;
    private String nome;
    private String numeroRegistro;
    private String espec;
    private Double latitude;
    private Double longitude;
    private String endereco;
    private String telefone;
    private Boolean temAgenda;
    private Integer idProfissao;
    private String nomeProfissao;
    private Integer idParceiro;
    private Boolean aceitaParticular;
    private Boolean aceitaPlano;
    private Long idClinicaAtual;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getEspec() {
        return espec;
    }

    public void setEspec(String espec) {
        this.espec = espec;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getEndereco() {
        return endereco;
    }


    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getTemAgenda() {
        return temAgenda;
    }

    public void setTemAgenda(Boolean temAgenda) {
        this.temAgenda = temAgenda;
    }

    public Integer getIdProfissao() {
        return idProfissao;
    }

    public void setIdProfissao(Integer idProfissao) {
        this.idProfissao = idProfissao;
    }

    public String getNomeProfissao() {
        return nomeProfissao;
    }

    public void setNomeProfissao(String nomeProfissao) {
        this.nomeProfissao = nomeProfissao;
    }

    public Integer getIdParceiro() {
        return idParceiro;
    }

    public void setIdParceiro(Integer idParceiro) {
        this.idParceiro = idParceiro;
    }

    public Boolean getAceitaParticular() {
        return aceitaParticular;
    }

    public void setAceitaParticular(Boolean aceitaParticular) {
        this.aceitaParticular = aceitaParticular;
    }

    public Boolean getAceitaPlano() {
        return aceitaPlano;
    }

    public void setAceitaPlano(Boolean aceitaPlano) {
        this.aceitaPlano = aceitaPlano;
    }

    public Long getIdClinicaAtual() {
        return idClinicaAtual;
    }

    public void setIdClinicaAtual(Long idClinicaAtual) {
        this.idClinicaAtual = idClinicaAtual;

    }
}

