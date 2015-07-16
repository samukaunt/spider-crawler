package br.com.wjaa.spider.robot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wagner on 15/07/15.
 */
public class SpiderCatalogoMed {

    private static final String URL_SEARCH = "http://www.catalogo.med.br/?act=search&q=&fSpeciality=0&fCity=S%E3o+Paulo&plan=&lang=0&free=0&photo=0";
    private static final Log log = LogFactory.getLog(SpiderCatalogoMed.class);
    private static ChromeDriverService service;
    private WebDriver driver;
    private Gson gson = new GsonBuilder().create();
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
            Thread.sleep(5000);
            WebElement souPaciente = driver.findElement(By.linkText("Sou paciente"));

            if (souPaciente != null){
                try{
                    souPaciente.click();
                }catch(Exception e){
                    log.error("Erro ao clicar no link sou paciente");
                }

            }
            Thread.sleep(2000);

            WebElement proximaPagina = driver.findElement(By.linkText(">"));
            log.info("verificando existencia de proxima pagina, existe? = " + (proximaPagina != null));
            int count = 1;
            while (proximaPagina != null){
                log.info("#######PAGINA = " + count++);
                String linkProximaPagina = proximaPagina.getAttribute("href");
                List<String> links = this.getLinksMedicos(driver.findElements(By.cssSelector(".nameprop")));
                log.info("Busca encontrou na pagina, " + links.size() + " medico(s)");

                for (String linkMedico : links){
                    log.info("abrindo perfil...");
                    driver.get(linkMedico);
                    Thread.sleep(4000);
                    log.info("clicando nos links de telefones");
                    List<WebElement> linkTelefones = driver.findElements(By.className("seephone"));
                    for(WebElement e : linkTelefones ){
                        try{
                            e.click();
                            Thread.sleep(500);
                        }catch(Exception ex){
                            log.error("Erro ao clicar no link sou paciente");
                        }

                    }
                    Thread.sleep(1000);
                    log.info("construindo medico...");
                    doc = Jsoup.parse(driver.getPageSource());
                    MedicoVo medico = makeMedico(doc);
                    String json = gson.toJson(medico);

                    this.appendFile(json);


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

    private void appendFile(String json) {
        try{
            FileWriter fw = new FileWriter("/home/wagner/workspace-wjaa/dados-medicos/medicos.json",true); //the true will append the new data
            fw.write(json);//appends the string to the file
            fw.write("\n\n");//appends the string to the file
            fw.close();
        }
        catch(IOException ioe){
            System.err.println("IOException: " + ioe.getMessage());
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
        return medicoVo;
    }

    private void setEndereco(MedicoVo medicoVo, Element dado) {
        EnderecoVo endVo = new EnderecoVo();
        Elements rua = dado.select(".address");
        if (rua != null){
            endVo.setLogradouro(rua.get(0).text());
        }
        Elements cidadeBairro = dado.select(".city");
        if (cidadeBairro != null){
            String value = cidadeBairro.get(0).text();
            if (value.contains("-")){
                String bairro = value.substring(0,value.indexOf("-"));
                endVo.setBairro(bairro);
            }
            if (value.contains("-") && value.contains("/")){
                String cidade = value.substring(value.indexOf("-")+1,value.indexOf("/"));
                endVo.setLocalidade(cidade);
                String uf = value.substring(value.indexOf("/")+1,value.length());
                endVo.setUf(uf);
            }
        }
        Elements cep = dado.select(".postalCode");
        if (cep != null){
            endVo.setCep(cep.get(0).text().replace("CEP:",""));
        }

        Elements phone = dado.select(".phoneContainer");
        if (phone != null){
            endVo.setTelefone(phone.get(0).text().replaceAll("(Mencione o catalogo.med.br quando ligar.)",""));
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
            for (int i = 0; i < eespec.size(); i++){
                String espec = eespec.get(i).text();
                EspecialidadeVo especVo = new EspecialidadeVo();
                especVo.setNome(espec);
                medicoVo.addEspecialidade(especVo);
            }
        }
        //CRM: 102071 - SP. RQE: 42192
        Elements crm = doc.select(".docid");
        if (crm != null){
            String value = crm.get(0).text();
            value = value.replaceAll("CRM:", "");
            if (value.contains("-")){
                medicoVo.setCrm(Integer.valueOf(value
                        .substring(0, value.indexOf("-"))
                        .trim()));
            }
        }
        Elements foto = doc.select(".showphoto img");
        if (foto != null){
            String urlFoto = foto.get(0).attr("src");
            try {
                URL uri = new URL(urlFoto);
                // gravando imagem
                RenderedImage img = ImageIO.read(uri);
                if (medicoVo.getCrm() != null){

                    ImageIO.write((RenderedImage) img, "jpg", new File("/home/wagner/workspace-wjaa/dados-medicos/fotos/" + medicoVo.getCrm() + ".jpg"));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }


}
