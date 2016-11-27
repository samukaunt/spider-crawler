package br.com.wjaa.spider.service;

import br.com.wjaa.spider.importa.*;
import br.com.wjaa.spider.robot.ConvenioVo;
import br.com.wjaa.spider.robot.EnderecoVo;
import br.com.wjaa.spider.robot.EspecialidadeVo;
import br.com.wjaa.spider.robot.MedicoVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wagner on 26/11/16.
 */
public class ProfissionalService {


    private Gson gson = new GsonBuilder().create();
    private CloseableHttpClient httpclient = HttpClients.createDefault();
    private EspecialidadeEntity [] especialidades;
    private ConvenioCategoriaVo[] categorias;

    public ProfissionalService(EspecialidadeEntity [] especialidades, ConvenioCategoriaVo[] categorias){
        this.especialidades = especialidades;
        this.categorias = categorias;
    }


    public int createProfissional(String uf, int count, MedicoVo m) throws IOException {
        ProfissionalFullForm profissionalFullForm = new ProfissionalFullForm();
        ProfissionalForm profissional = new ProfissionalForm();
        profissionalFullForm.setClinicas(getClinicas(profissional,m));
        profissional.setNome(m.getNome());
        //profissional.setNumeroRegistro(m.getCrm().toString());
        if (m.getCrm() != null){

            profissional.setNumeroRegistro(m.getCrm()  + "-" + uf);
            if (m.getCrm() != null){
                profissional.setFoto(profissional.getNumeroRegistro() + ".jpg");
            }
        }
        profissional.setEmail("empty@empty.com.br");
        profissional.setSenha("empty");
        profissional.setIdEspecialidade(getEspecialidade(m.getEspecialidades()));

        if (profissional.getIdEspecialidade() == null && m.getEspecialidades() != null){
            for (EspecialidadeVo e : m.getEspecialidades() ){
                System.out.println(e.getNome());
            }
        }

        if (m.getEspecialidades() == null){
            System.out.println(m.getNome());
        }

        System.out.println("Verificando se o médico nao existe na base, buscando pelo nome = " + m.getNome());
        ProfissionalBasicoVo profissionalBasicoVo = getProfissionalByName(m.getNome());
        if (profissionalBasicoVo != null && (!"1".equals(profissionalBasicoVo.getNumeroRegistro()) || uf.equals("SP"))){
            System.out.println("Profissional já é cadastrado, adicionando o id....");
            profissional.setIdLogin(profissionalBasicoVo.getId());

        }else{
            System.out.println("Profissional novo, criando....");
            profissional = createProfissional(profissional);

            if (profissional == null){
                System.out.println("ERRO NA CRIACAO DO PROFISSIONAL PARTINDO PARA O PROXIMO");
                return count;
            }

            System.out.println("PROFISSIONAL CRIADO COM SUCESSO!");

        }
        profissionalFullForm.setProfissional(profissional);
        System.out.println("Salvando o médico numero [ " + ++count + "]");

        CloseableHttpResponse response = null;

        HttpPost post = new HttpPost("http://rest.agendee.com.br/profissional/update");
        post.setHeader("dataType","json");
        post.setHeader("Content-Type","application/json");
        post.setHeader("mimeType","application/json");


        StringEntity entity = new StringEntity(gson.toJson(profissionalFullForm), HTTP.UTF_8);
        post.setEntity(entity);

        httpclient = HttpClients.createDefault();
        response = httpclient.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();

        if ( statusCode >= 400 ){
            System.out.println("####### ERRO: " + EntityUtils.toString(response.getEntity()));
        }else{
            System.out.println("SUCESSO");
        }
        httpclient.close();
        return count;
    }

    private  ProfissionalForm createProfissional(ProfissionalForm profissional) throws IOException {
        try{

            CloseableHttpResponse response = null;

            HttpPost post = new HttpPost("http://rest.agendee.com.br/profissional/save");
            post.setHeader("dataType","json");
            post.setHeader("Content-Type","application/json");
            post.setHeader("mimeType","application/json");


            StringEntity entity = new StringEntity(gson.toJson(profissional), HTTP.UTF_8);
            post.setEntity(entity);

            httpclient = HttpClients.createDefault();
            response = httpclient.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();

            if ( statusCode >= 400 ){
                System.out.println("####### ERRO: " + EntityUtils.toString(response.getEntity(),HTTP.UTF_8));
                return null;
            }else{
                System.out.println("NOVO PROFISSIONAL CRIADO COM SUCESSO");
            }
            return gson.fromJson(EntityUtils.toString(response.getEntity(), HTTP.UTF_8), ProfissionalForm.class);
        }finally {
            httpclient.close();
        }

    }

    private  ProfissionalBasicoVo getProfissionalByName(String nome) throws IOException {

        try{
            CloseableHttpResponse response = null;

            HttpPost post = new HttpPost("http://rest.agendee.com.br/profissional/name/search");
            post.setHeader("Content-Type","application/x-www-form-urlencoded");

            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("startName", nome));
            post.setEntity(new UrlEncodedFormEntity(postParameters,HTTP.UTF_8));

            httpclient = HttpClients.createDefault();
            response = httpclient.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();

            if ( statusCode >= 400 ){
                System.out.println("####### ERRO: " + EntityUtils.toString(response.getEntity(), HTTP.UTF_8));
            }else{
                System.out.println("BUSCA DE PROFISSIONAL EXECUTADA COM SUCESSO.");
            }

            String json = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

            ProfissionalBasicoVo [] profissionais = gson.fromJson(json, ProfissionalBasicoVo[].class);

            return profissionais.length > 0 ? profissionais[0] : null;

        }finally {
            httpclient.close();
        }
    }

    private  int[] getEspecialidade(List<EspecialidadeVo> especs) {
        if (especs != null){
            int [] ids = new int[]{};
            for(EspecialidadeVo e : especs){
                Integer id = getEspecialidadeByVo(e);
                if (id != null){
                    ids = ArrayUtils.add(ids,id);
                }

            }
            return ids;
        }
        return null;
    }

    private  Integer getEspecialidadeByVo(EspecialidadeVo e) {
        for(EspecialidadeEntity ee : especialidades){

            //verificando igualdade completa
            if (ee.getNome().equalsIgnoreCase(e.getNome())){
                return ee.getId();
            }

            //verificando igualdade parcial
            String nomeVo [] = e.getNome().split(" ");
            String nomeEntity [] = ee.getNome().split(" ");
            if (nomeVo.length == nomeEntity.length){
                boolean equal = true;
                for (int i = 0; i < nomeVo.length; i++){
                    //maior que 4 para ignorar os 'de' 'a' 'da'
                    if (nomeVo[i].length() > 4){
                        // System.out.println("comparando esse =" + nomeEntity[i] + " com o começo desse " + getParcial(nomeVo[i]));
                        equal &= nomeEntity[i].startsWith(getParcial(nomeVo[i]));
                    }
                }

                if (equal){
                    return ee.getId();
                }

            }
        }



        return null;
    }

    private  String getParcial(String s) {
        if (StringUtils.isBlank(s)){
            return s;
        }
        if (s.length() < 6){
            return s;
        }
        return s.substring(0,s.length()-3);
    }

    private  List<ClinicaForm> getClinicas(ProfissionalForm m, MedicoVo medicoVo) {
        List<ClinicaForm> clinicas = new ArrayList<ClinicaForm>();
        for(EnderecoVo e : medicoVo.getEnderecos()){
            clinicas.add(getClinica(e, m));
        }
        return clinicas;
    }

    private  ClinicaForm getClinica(EnderecoVo e, ProfissionalForm m) {
        ClinicaForm c = new ClinicaForm();
        boolean aceitaParticular = getAceitaParticular(e.getConvenios());
        c.setAceitaParticular(true);
        c.setIdsCategorias(getIdsCategorias(e.getConvenios()));
        c.setHoraFuncionamentoFim("18:00");
        c.setHoraFuncionamentoIni("8:00");
        c.setTempoConsultaEmMin(15);
        c.setAberturaAgenda("SEMANAL");
        c.setNome(e.getNomeClinica());
        EnderecoForm ee = new EnderecoForm();

        ee.setUf(e.getUf());
        ee.setBairro(e.getBairro());
        ee.setCep(e.getCep() != null ? e.getCep().replace("-","") : "");
        ee.setComplemento(e.getComplemento());

        if ( StringUtils.isNotBlank(e.getLogradouro()) ){
            String log[] = e.getLogradouro().split(",");
            if (log.length == 1){
                ee.setLogradouro(e.getLogradouro());
            }else if (log.length == 2){
                ee.setLogradouro(log[0]);
                try{
                    if (log[1].contains("-")){
                        String comp[] = log[1].split("-");
                        ee.setNumero(Integer.valueOf(comp[0].trim()));
                        ee.setComplemento(comp[1]);
                    }else if (log[1].split(" ").length > 1){
                        String comp[] = log[1].split(" ");
                        if (NumberUtils.isNumber(comp[0])){
                            ee.setNumero(Integer.valueOf(comp[0].trim()));
                            if (comp.length > 1){
                                ee.setComplemento(comp[1]);
                                ee.setComplemento(log[1].replace(comp[0],"").trim());
                            }
                        }else if (NumberUtils.isNumber(comp[1])){
                            ee.setNumero(Integer.valueOf(comp[1].trim()));
                            if (comp.length > 2){
                                ee.setComplemento(log[1].replace(comp[1],"").trim());
                            }
                        }else{
                            ee.setComplemento("Numero " + log[1]);
                        }
                    }else{
                        ee.setNumero(Integer.valueOf(log[1].trim()));
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                    ee.setComplemento("Numero " + log[1]);
                }
            }else if (log.length > 2){
                ee.setLogradouro(log[0]);
            }else{
                ee.setLogradouro(e.getLogradouro());
            }
        }
        ee.setLocalidade(e.getLocalidade());
        c.setEndereco(ee);

        try{

            if ( StringUtils.isNotBlank(e.getTelefone())) {
                String tels [] = e.getTelefone().split(";");

                if (tels.length > 1){
                    for(String tel : tels){
                        String telddd[] = tel.split(" ");
                        if (telddd.length == 2){
                            if (telddd[1].startsWith("9") || telddd[1].startsWith("8") || telddd[1].startsWith("7")){
                                m.setDdd(Short.valueOf(telddd[0].trim()));
                                m.setCelular(Long.valueOf(telddd[1].trim()));
                            }else{
                                c.setDdd(Short.valueOf(telddd[0].trim()));
                                c.setTelefone(Long.valueOf(telddd[1].trim()));
                            }
                        }
                    }
                }else{
                    if (tels[0].contains(" ")){

                        String telddd [] = tels[0].split(" ");
                        if (telddd.length == 2){
                            c.setDdd(Short.valueOf(telddd[0].trim()));
                            c.setTelefone(Long.valueOf(telddd[1].replace("-","").trim()));
                        }else{
                            if (telddd[0].startsWith("(")){
                                String ntel = telddd[0].replace("(", "").replace(")", " ");
                                telddd = ntel.split(" ");
                                c.setDdd(Short.valueOf(telddd[0].trim()));
                                c.setTelefone(Long.valueOf(telddd[1].replace("-","").trim()));
                            }
                        }
                    }else{
                        if (tels[0].startsWith("(")){
                            String ntel = tels[0].replace("(", "").replace(")", " ");
                            tels = ntel.split(" ");
                            c.setDdd(Short.valueOf(tels[0].trim()));
                            c.setTelefone(Long.valueOf(tels[1].replace("-","").trim()));
                        }

                    }
                }


            }
        }catch(Exception ex){
            System.out.println("erro no telefone =" + ex.getMessage());
        }

        return c;
    }

    private  int[] getIdsCategorias(List<ConvenioVo> convenios) {
        if (convenios != null){
            int [] ids = new int[convenios.size()];
            for(ConvenioVo c : convenios){
                Integer id = getCategoriasByVo(c);
                if (id != null){
                    ids = ArrayUtils.add(ids,id);
                }
            }
            return ids;
        }
        return null;
    }

    private  Integer getCategoriasByVo(ConvenioVo c) {
        for(ConvenioCategoriaVo cc : categorias){

            //verificando igualdade completa
            if (cc.getConvenioVo().getNome().equalsIgnoreCase(c.getNome())){
                return cc.getId();
            }

            //verificando igualdade parcial
            String nomeVo = c.getNome();
            String nomeEntity = cc.getConvenioVo().getNome();

            //checando até 80% do valor do nome do convenio.
            String nomeVoParcial = getParcial(nomeVo.trim());
            String nomeEntityParcial = getParcial(nomeEntity.trim());

            if (nomeVoParcial.equalsIgnoreCase(nomeEntityParcial)){
                return cc.getId();
            }

            if (nomeVoParcial.contains(nomeEntityParcial)){
                return cc.getId();
            }

            if (nomeEntityParcial.contains(nomeVoParcial)){
                return cc.getId();
            }

        }
        return null;
    }

    private  boolean getAceitaParticular(List<ConvenioVo> convenios) {

        if (convenios != null){
            for (ConvenioVo c : convenios) {
                if (c.getNome() != null){
                    if ("PARTICULAR".equals(c.getNome()) || "PARTICULARES".equals(c.getNome()) || c.getNome().contains("PARTICU") ){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    
}
