package br.com.wjaa.spider.importa;

/**
 * Created by wagner on 21/07/15.
 */
public class MedicoClinicaEntity {

    private Long id;
    private Long idMedico;
    private ClinicaForm clinica;
    private Short ddd;
    private Long telefone;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
    }

    public ClinicaForm getClinica() {
        return clinica;
    }

    public void setClinica(ClinicaForm clinica) {
        this.clinica = clinica;
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
}
