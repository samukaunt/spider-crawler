package br.com.wjaa.spider.importa;

import br.com.wjaa.spider.robot.ConvenioVo;
import br.com.wjaa.spider.robot.EnderecoVo;
import br.com.wjaa.spider.robot.EspecialidadeVo;
import br.com.wjaa.spider.robot.MedicoVo;
import br.com.wjaa.spider.service.ProfissionalService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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


    public static void main(String [] args) throws IOException {
        System.out.print("Iniciando para = " + args[0]);
        ConvenioCategoriaVo[] categorias = gson.fromJson(new FileReader(new File("/home/wagner/dev/agendee/dados-medicos/categorias.json")),ConvenioCategoriaVo[].class);
        EspecialidadeEntity [] especialidades = gson.fromJson(new FileReader(new File("/home/wagner/dev/agendee/dados-medicos/especs.json")),EspecialidadeEntity[].class);
        BufferedReader f = new BufferedReader(new FileReader(new File("/home/wagner/dev/agendee/dados-medicos/medicos-"+args[0]+".json")));
        ProfissionalService profissionalService = new ProfissionalService(especialidades,categorias);
        String uf = args[0];
        String line = "";
        int count = 0;
        while ((line = f.readLine()) != null) {
            if (StringUtils.isNotBlank(line)){
                MedicoVo m = gson.fromJson(line, MedicoVo.class);
                count = profissionalService.createProfissional(uf, count, m);
            }

        }

    }



}

