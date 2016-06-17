package br.com.wjaa.spider.importa;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wagner on 15/06/15.
 */
public class ClinicaForm implements Serializable{

    private static final long serialVersionUID = 7207991135581266227L;

    private Long id;
    private Long idClinica;
    private String nome;
    private Short ddd;
    private Long telefone;
    private Boolean aceitaParticular;
    private EnderecoForm endereco;


    private String horaFuncionamentoIni;
    private String horaFuncionamentoFim;
    private Integer tempoConsultaEmMin;
    private int [] idsCategorias;

    private Double valorConsulta;

    public String getAberturaAgenda() {
        return aberturaAgenda;
    }

    public void setAberturaAgenda(String aberturaAgenda) {
        this.aberturaAgenda = aberturaAgenda;
    }

    public Double getValorConsulta() {
        return valorConsulta;
    }

    public void setValorConsulta(Double valorConsulta) {
        this.valorConsulta = valorConsulta;
    }

    public Integer getTempoConsultaEmMin() {
        return tempoConsultaEmMin;
    }

    public void setTempoConsultaEmMin(Integer tempoConsultaEmMin) {
        this.tempoConsultaEmMin = tempoConsultaEmMin;
    }

    public String getHoraFuncionamentoFim() {
        return horaFuncionamentoFim;
    }

    public void setHoraFuncionamentoFim(String horaFuncionamentoFim) {
        this.horaFuncionamentoFim = horaFuncionamentoFim;
    }

    public String getHoraFuncionamentoIni() {
        return horaFuncionamentoIni;
    }

    public void setHoraFuncionamentoIni(String horaFuncionamentoIni) {
        this.horaFuncionamentoIni = horaFuncionamentoIni;
    }

    private String aberturaAgenda;

    public Long getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Long idClinica) {
        this.idClinica = idClinica;
    }

    public Boolean getAceitaParticular() {
        return aceitaParticular;
    }



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


    public EnderecoForm getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoForm endereco) {
        this.endereco = endereco;
    }

    public void setAceitaParticular(Boolean aceitaParticular) {
        this.aceitaParticular = aceitaParticular;
    }

    public int[] getIdsCategorias() {
        return idsCategorias;
    }

    public void setIdsCategorias(int[] idsCategorias) {
        this.idsCategorias = idsCategorias;
    }
}
