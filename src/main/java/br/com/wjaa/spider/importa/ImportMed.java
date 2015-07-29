package br.com.wjaa.spider.importa;

import br.com.wjaa.spider.robot.EnderecoVo;
import br.com.wjaa.spider.robot.EspecialidadeVo;
import br.com.wjaa.spider.robot.MedicoVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wagner on 21/07/15.
 */
public class ImportMed {

    private static Gson gson = new GsonBuilder().create();
    private static EspecialidadeEntity [] especialidades;
    private static List<ConvenioCategoriaEntity> categorias;
    private static CloseableHttpClient httpclient = HttpClients.createDefault();

    public static void main(String [] args) throws IOException {

        especialidades = gson.fromJson(new FileReader(new File("/home/wagner/Downloads/especs.json")),EspecialidadeEntity[].class);
        BufferedReader f = new BufferedReader(new FileReader(new File("/home/wagner/Downloads/medicos.json")));
        String line = "";
        int count = 0;
        while ((line = f.readLine()) != null) {
            if (StringUtils.isNotBlank(line)){
                MedicoVo m = gson.fromJson(line, MedicoVo.class);
                MedicoEntity me = new MedicoEntity();
                me.setNome(m.getNome());
                me.setCrm(m.getCrm());
                me.setClinicas(getClinicas(m.getEnderecos(),me));
                me.setEmail("unknow@unknow.com.br");
                me.setAceitaParticular(true);
                me.setSenha("unknow");
                me.setEspecialidades(getEspecialidade(m.getEspecialidades()));

                if (me.getEspecialidades() == null && m.getEspecialidades() != null){
                    for (EspecialidadeVo e : m.getEspecialidades() ){
                        System.out.println(e.getNome());
                    }
                } if (m.getEspecialidades() == null){
                    System.out.println(m.getNome());
                }



               System.out.println("Salvando o médico numero [ " + ++count + "]");

               CloseableHttpResponse response = null;

                HttpPost post = new HttpPost("http://localhost:9191/ranchucrutes-ws/medico/save");
                post.setHeader("dataType","json");
                post.setHeader("Content-Type","application/json");
                post.setHeader("mimeType","application/json");


                StringEntity entity = new StringEntity(gson.toJson(me), HTTP.UTF_8);
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


            }

        }

    }

    private static List<EspecialidadeEntity> getEspecialidade(List<EspecialidadeVo> especialidades) {
        if (especialidades != null){
            List<EspecialidadeEntity> especialidadeEntities = new ArrayList<EspecialidadeEntity>(especialidades.size());
            for(EspecialidadeVo e : especialidades){
                especialidadeEntities.add(getEspecialidadeByVo(e));
            }
            return especialidadeEntities;
        }
        return null;
    }

    private static EspecialidadeEntity getEspecialidadeByVo(EspecialidadeVo e) {
        for(EspecialidadeEntity ee : especialidades){

            //verificando igualdade completa
            if (ee.getNome().equalsIgnoreCase(e.getNome())){
                return ee;
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
                    return ee;
                }

            }
        }
        return null;
    }

    private static String getParcial(String s) {
        return s.substring(0,s.length()-3);
    }

    private static List<MedicoClinicaEntity> getClinicas(List<EnderecoVo> enderecos, MedicoEntity m) {
        List<MedicoClinicaEntity> medicoClinicaEntities = new ArrayList<MedicoClinicaEntity>();
        for(EnderecoVo e : enderecos){
            MedicoClinicaEntity mc = new MedicoClinicaEntity();

            mc.setClinica(getClinica(e, m));
            medicoClinicaEntities.add(mc);
        }
        return medicoClinicaEntities;
    }

    private static ClinicaEntity getClinica(EnderecoVo e, MedicoEntity m) {
        ClinicaEntity c = new ClinicaEntity();
        EnderecoEntity ee = new EnderecoEntity();
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
                    }else{
                        ee.setNumero(Integer.valueOf(log[1].trim()));
                    }
                }catch(Exception ex){
                    //System.out.println("Erro no parse do numero = " + log[1] + " jogando no complemento");
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
                            c.setTelefone(Long.valueOf(telddd[1].trim()));
                        }else{
                            if (telddd[0].startsWith("(")){
                                String ntel = telddd[0].replace("(", "").replace(")", " ");
                                telddd = ntel.split(" ");
                                c.setDdd(Short.valueOf(telddd[0].trim()));
                                c.setTelefone(Long.valueOf(telddd[1].trim()));
                            }
                        }
                    }else{
                        if (tels[0].startsWith("(")){
                            String ntel = tels[0].replace("(", "").replace(")", " ");
                            tels = ntel.split(" ");
                            c.setDdd(Short.valueOf(tels[0].trim()));
                            c.setTelefone(Long.valueOf(tels[1].trim()));
                        }

                    }
                }


            }
        }catch(Exception ex){
            System.out.println("erro no telefone =" + ex.getMessage());
        }

        return c;
    }

}

