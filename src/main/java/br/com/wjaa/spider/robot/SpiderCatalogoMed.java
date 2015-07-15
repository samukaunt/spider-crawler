package br.com.wjaa.spider.robot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Created by wagner on 15/07/15.
 */
public class SpiderCatalogoMed {

    private static final String URL_SEARCH = "http://www.catalogo.med.br/?act=search&q=&fSpeciality=0&fCity=S%E3o+Paulo&plan=&lang=0&free=0&photo=0";
    private static final Log log = LogFactory.getLog(SpiderCatalogoMed.class);
    private static ChromeDriverService service;
    private WebDriver driver;
    public static void main(String [] args){
        SpiderCatalogoMed spider = new SpiderCatalogoMed();
        spider.letsGo();
    }


    private void letsGo() {
        Document doc = null;
        List<MedicoVo> medicos = new ArrayList<MedicoVo>();
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("/opt/chromedriver/chromedriver"))
                .usingAnyFreePort()
                .build();

        try {
            log.info("iniciando robo");
            service.start();
            driver = new RemoteWebDriver(service.getUrl(),
                    DesiredCapabilities.chrome());
            driver.get(URL_SEARCH);
            log.info("abrindo pagina principal");
            Thread.sleep(2000);
            WebElement souPaciente = driver.findElement(By.linkText("Sou paciente"));

            if (souPaciente != null){
                souPaciente.click();
            }
            Thread.sleep(2000);

            WebElement proximaPagina = driver.findElement(By.linkText(">"));
            log.info("verificando existencia de proxima pagina, existe? = " + (proximaPagina != null));
            while (proximaPagina != null){
                String linkProximaPagina = proximaPagina.getAttribute("href");
                List<String> links = this.getLinksMedicos(driver.findElements(By.cssSelector(".nameprop")));
                log.info("Busca encontrou na pagina " + links.size() + " medico(s)");
                for (String linkMedico : links){
                    log.info("abrindo perfil...");
                    driver.get(linkMedico);
                    doc = Jsoup.parse(driver.getPageSource());
                    log.info("construindo medico...");
                    medicos.add(makeMedico(doc));

                    Thread.sleep(1000);
                    //TODO TALVEZ AQUI PRECISE VOLTAR..
                    //driver.navigate().back();
                }
                log.info("abrindo proxima pagina...");
                driver.get(linkProximaPagina);
                proximaPagina = driver.findElement(By.linkText(">"));
                Thread.sleep(2000);
            }



            //searchBox.quit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private List<String> getLinksMedicos(List<WebElement> elements) {
        List<String> links = new ArrayList<String>(elements.size());
        for (WebElement e : elements){
            if ( !e.getAttribute("class").contains("doc_class")){
                links.add(e.getAttribute("href"));
            }
        }
        return links;
    }

    private MedicoVo makeMedico(Document doc) {
        MedicoVo medicoVo = new MedicoVo();

        this.setDadosBasicos(medicoVo,doc);

        Elements dados = doc.select(".stab");
        for (int i = 0; i < dados.size(); i ++){
            Element dado = dados.get(i);
            this.setEndereco(medicoVo,dado);

        }
        return null;
    }

    private void setEndereco(MedicoVo medicoVo, Element dado) {
        EnderecoVo endVo = new EnderecoVo();
        Elements rua = dado.select(".address");
        if (rua != null){
            endVo.setLocalidade(rua.get(0).text());
        }
        Elements cidadeBairro = dado.select(".city");
        if (cidadeBairro != null){
            String value = cidadeBairro.get(0).text();
            String bairro = value.substring(0,value.indexOf("-"));
            String cidade = value.substring(value.indexOf("-"),value.indexOf("/"));
            String uf = value.substring(value.indexOf("/"),value.length());
            endVo.setBairro(bairro);
            endVo.setLocalidade(cidade);
            endVo.setUf(uf);
        }
        Elements cep = dado.select(".postalCode");
        if (cep != null){
            endVo.setCep(cep.get(0).text().replace("CEP:",""));
        }

        Elements phone = dado.select(".phoneContainer");
        if (phone != null){
            endVo.setTelefone(phone.get(0).text());
        }


        this.setPlanosEndereco(endVo, dado);
        medicoVo.addEndereco(endVo);

    }

    private void setPlanosEndereco(EnderecoVo endVo, Element dado) {
        Elements planos = dado.select(".pseudolist span");

        for (int j = 0; j < planos.size(); j++ ){
            Element plano = planos.get(j);
            ConvenioVo convenioVo = new ConvenioVo();
            convenioVo.setNome(plano.text());
            endVo.addConvenio(convenioVo);
        }
    }

    private void setDadosBasicos(MedicoVo medicoVo, Document doc) {

        Elements enome = doc.select(".name");
        if (enome != null){
            medicoVo.setNome(enome.get(0).text());
        }
        Elements eespec = doc.select(".doctor_specs li");
        if (eespec != null){
            String espec = eespec.get(0).text();
            String [] especs = espec.split(",");
            if (especs != null && especs.length > 0){

                for (int i = 0; i < especs.length; i++){
                    EspecialidadeVo especVo = new EspecialidadeVo();
                    especVo.setNome(especs[i]);
                    medicoVo.addEspecialidade(especVo);
                }
            }

        }
        //CRM: 102071 - SP. RQE: 42192
        Elements crm = doc.select(".docid");
        if (crm != null){
            String value = crm.get(0).text();
            medicoVo.setCrm(Integer.valueOf(value.substring(value.indexOf("CRM:"),value.indexOf("-")).trim()));
        }


    }


}
