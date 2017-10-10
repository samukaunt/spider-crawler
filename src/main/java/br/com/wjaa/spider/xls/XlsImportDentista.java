package br.com.wjaa.spider.xls;


import br.com.wjaa.spider.importa.ConvenioCategoriaVo;
import br.com.wjaa.spider.importa.EspecialidadeEntity;
import br.com.wjaa.spider.robot.EnderecoVo;
import br.com.wjaa.spider.robot.EspecialidadeVo;
import br.com.wjaa.spider.robot.Medico;
import br.com.wjaa.spider.robot.MedicoVo;
import br.com.wjaa.spider.service.ProfissionalService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * Created by wagner on 21/11/16.
 */
public class XlsImportDentista {

    static Integer count = 0;
    private static Gson gson = new GsonBuilder().create();

    public static void main(String args[]) throws IOException {
        EspecialidadeEntity [] especialidades = gson.fromJson(new InputStreamReader(XlsImportDentista.class.getClassLoader().getResourceAsStream("especs_dentista.json")),EspecialidadeEntity[].class);
        ConvenioCategoriaVo[] categorias = gson.fromJson(new InputStreamReader(XlsImportDentista.class.getClassLoader().getResourceAsStream("categorias.json")),ConvenioCategoriaVo[].class);
        InputStream fin = XlsImportDentista.class.getClassLoader().getResourceAsStream("dentistas.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(fin);
        XSSFSheet ws = wb.getSheetAt(0);

        int rowNum = ws.getLastRowNum() + 1;
        int colNum = ws.getRow(0).getLastCellNum();
        List<MedicoVo> medicoVos = new ArrayList<>();
        for(int i = 0; i <rowNum; i++){
            XSSFRow row = ws.getRow(i);


            String nome = row.getCell(0).toString().trim();
            MedicoVo m = getMedicoByName(nome, medicoVos);
            if (m == null){
                m = new MedicoVo();
                //PEGAR AQUI O NUMERO DO CRM NO CAMPO DE PESSOA FISICA
                m.setCrm(getCrm(row.getCell(8).toString()));
                m.setEnderecos(new ArrayList<>());
                m.setEspecialidades(new ArrayList<>());
                medicoVos.add(m);
            }
            //nome
            m.setNome(nome);

            //especialidade
            String xlsEspec = row.getCell(1).toString();
            if (xlsEspec.contains(",")){
                String [] especArray = xlsEspec.split(",");
                for (String e: especArray) {
                    EspecialidadeVo espec = new EspecialidadeVo(e.trim());
                    if (!m.getEspecialidades().contains(espec)){
                        m.getEspecialidades().add(espec);
                    }
                }
            }else{
                EspecialidadeVo espec = new EspecialidadeVo(xlsEspec.trim());
                if (!m.getEspecialidades().contains(espec)){
                    m.getEspecialidades().add(espec);
                }

            }

            //endereco
            EnderecoVo end = new EnderecoVo();
            end.setLogradouro(row.getCell(2).toString().trim());
            end.setBairro(row.getCell(3).toString().trim());
            end.setLocalidade(row.getCell(4).toString().trim());
            end.setTelefone(row.getCell(5).toString().trim());
            end.setUf(row.getCell(6).toString().trim());
            end.setCep(row.getCell(7).toString().trim());
            if (!m.getEnderecos().contains(end)){
                m.getEnderecos().add(end);
            }


            //Clinica

        }


        final ProfissionalService profissionalService = new ProfissionalService(especialidades,categorias);
        medicoVos.stream().forEach(m -> {
            try {
                count = profissionalService.createProfissional("SP", count, m);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });




    }

    private static Integer getCrm(String s) {
        try{
            if (s.contains("Pessoa Fisica ")){
                return Integer.valueOf(s.replace("Pessoa Fisica ",""));
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    private static MedicoVo getMedicoByName(String nome, List<MedicoVo> medicoVos) {
        for(MedicoVo m : medicoVos){
            if (m.getNome().equalsIgnoreCase(nome)){
                return m;
            }
        }
        return null;
    }


}
