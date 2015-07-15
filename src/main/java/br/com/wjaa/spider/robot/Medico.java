package br.com.wjaa.spider.robot;

/**
 * Created by wagner on 22/06/15.
 */
public class Medico {
    private String nome;
    private String especialidades;
    private String endereco;
    private String crm;
    private String inscritoEm;
    private String situacao;
    private String urlFoto;
    private String email;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    public String getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(String especialidades) {
        this.especialidades = especialidades;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getInscritoEm() {
        return inscritoEm;
    }

    public void setInscritoEm(String inscritoEm) {
        this.inscritoEm = inscritoEm;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean validate() {
        //TODO VERIFICAR AQUI SE ESTÁ ATIVO E OUTRAS REGRAS PARA NAO CADASTRAR MEDICOS SEM MUITA INFORMACAO
        // E MEDICOS Q NAO ESTAO ATIVOS.
        return true;
    }


    @Override
    public String toString() {
        return "Medico{" +
                "nome='" + nome + '\'' +
                ", especialidades='" + especialidades + '\'' +
                ", endereco='" + endereco + '\'' +
                ", crm='" + crm + '\'' +
                ", inscritoEm='" + inscritoEm + '\'' +
                ", situacao='" + situacao + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Medico medico = (Medico) o;

        if (nome != null ? !nome.equals(medico.nome) : medico.nome != null) return false;
        if (especialidades != null ? !especialidades.equals(medico.especialidades) : medico.especialidades != null)
            return false;
        if (endereco != null ? !endereco.equals(medico.endereco) : medico.endereco != null) return false;
        if (crm != null ? !crm.equals(medico.crm) : medico.crm != null) return false;
        if (inscritoEm != null ? !inscritoEm.equals(medico.inscritoEm) : medico.inscritoEm != null) return false;
        if (situacao != null ? !situacao.equals(medico.situacao) : medico.situacao != null) return false;
        if (urlFoto != null ? !urlFoto.equals(medico.urlFoto) : medico.urlFoto != null) return false;
        return !(email != null ? !email.equals(medico.email) : medico.email != null);

    }

    @Override
    public int hashCode() {
        int result = nome != null ? nome.hashCode() : 0;
        result = 31 * result + (especialidades != null ? especialidades.hashCode() : 0);
        result = 31 * result + (endereco != null ? endereco.hashCode() : 0);
        result = 31 * result + (crm != null ? crm.hashCode() : 0);
        result = 31 * result + (inscritoEm != null ? inscritoEm.hashCode() : 0);
        result = 31 * result + (situacao != null ? situacao.hashCode() : 0);
        result = 31 * result + (urlFoto != null ? urlFoto.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
