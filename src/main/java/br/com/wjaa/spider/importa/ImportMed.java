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
                me.setClinicas(getClinicas(m.getEnderecos()));
                me.setEmail("unknow@unknow.com.br");
                me.setAceitaParticular(true);
                me.setSenha("unknow");
                me.setEspecialidades(getEspecialidade(m.getEspecialidades()));
                System.out.println("Salvando o médico");

                CloseableHttpResponse response = null;

                HttpPost post = new HttpPost("http://localrest.marcmed.com.br/medico/save");
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
            if (ee.getNome().equalsIgnoreCase(e.getNome())){
                return ee;
            }
        }
        return null;
    }

    private static List<MedicoClinicaEntity> getClinicas(List<EnderecoVo> enderecos) {
        List<MedicoClinicaEntity> medicoClinicaEntities = new ArrayList<MedicoClinicaEntity>();
        for(EnderecoVo e : enderecos){
            MedicoClinicaEntity mc = new MedicoClinicaEntity();

            mc.setClinica(getClinica(e));
            medicoClinicaEntities.add(mc);
        }
        return medicoClinicaEntities;
    }

    private static ClinicaEntity getClinica(EnderecoVo e) {
        ClinicaEntity c = new ClinicaEntity();
        EnderecoEntity ee = new EnderecoEntity();
        ee.setUf(e.getUf());
        ee.setBairro(e.getBairro());
        ee.setCep(e.getCep() != null ? e.getCep().replace("-","") : "");
        ee.setComplemento(e.getComplemento());
        ee.setLogradouro(e.getLogradouro());
        ee.setLocalidade(e.getLocalidade());
        ee.setNumero(0);
        c.setEndereco(ee);
        return c;
    }

}
