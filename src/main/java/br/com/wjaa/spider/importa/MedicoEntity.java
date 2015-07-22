package br.com.wjaa.spider.importa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by wagner on 12/06/15.
 */
public class MedicoEntity implements Serializable{

    private static final long serialVersionUID = 8235523726178515224L;
    private Integer crm;
    private String nome;
    private Short ddd;
    private Long celular;
    private List<MedicoClinicaEntity> clinicas;
    private List<EspecialidadeEntity> especialidades;
    private Boolean aceitaParticular;
    private Long idLogin;
    private String email;
    private String senha;
    private Date dataCriacao;
    private Date dataUltimoAcesso;

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

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataUltimoAcesso() {
        return dataUltimoAcesso;
    }

    public void setDataUltimoAcesso(Date dataUltimoAcesso) {
        this.dataUltimoAcesso = dataUltimoAcesso;
    }
    //private Notification notificacoes de aplicativos

    public Integer getCrm() {
        return crm;
    }

    public void setCrm(Integer crm) {
        this.crm = crm;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<MedicoClinicaEntity> getClinicas() {
        return clinicas;
    }

    public void setClinicas(List<MedicoClinicaEntity> clinicas) {
        this.clinicas = clinicas;
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

    public List<EspecialidadeEntity> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<EspecialidadeEntity> especialidades) {
        this.especialidades = especialidades;
    }

    public Boolean getAceitaParticular() {
        return aceitaParticular;
    }

    public void setAceitaParticular(Boolean aceitaParticular) {
        this.aceitaParticular = aceitaParticular;
    }

}
