package br.com.wjaa.spider.ocr;

import java.util.Arrays;
import java.util.List;

public class Transportador {

    private String nome;
    private String cpfCnpj;
    private String rntrc;
    private String situacaoRntrc;
    private String desde;
    private String validade;
    private String municipio;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getRntrc() {
        return rntrc;
    }

    public void setRntrc(String rntrc) {
        this.rntrc = rntrc;
    }

    public String getSituacaoRntrc() {
        return situacaoRntrc;
    }

    public void setSituacaoRntrc(String situacaoRntrc) {
        this.situacaoRntrc = situacaoRntrc;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }


    public List<String> getValues() {
        return Arrays.asList(this.nome,this.cpfCnpj,this.rntrc,this.situacaoRntrc,this.desde,this.validade,this.municipio);
    }
}
