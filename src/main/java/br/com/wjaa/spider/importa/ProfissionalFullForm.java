package br.com.wjaa.spider.importa;

import java.util.List;

/**
 * Created by wagner on 6/15/16.
 */
public class ProfissionalFullForm {

    private ProfissionalForm profissional;
    private List<ClinicaForm> clinicas;

    public ProfissionalForm getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalForm profissional) {
        this.profissional = profissional;
    }

    public List<ClinicaForm> getClinicas() {
        return clinicas;
    }

    public void setClinicas(List<ClinicaForm> clinicas) {
        this.clinicas = clinicas;
    }

}
