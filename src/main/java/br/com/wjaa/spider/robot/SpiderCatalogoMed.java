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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wagner on 15/07/15.
 */
public class SpiderCatalogoMed {

    private static String URL_SEARCH = "http://www.catalogo.med.br/?act=search&q=&fSpeciality=0&fCity=SP&plan=&lang=0&free=0&photo=0";
    //private static String URL_SEARCH = "http://www.catalogo.med.br/index.pl?C=A&V=66436974793D5350266C616E673D302670686F746F3D3126667265653D3026713D26737465705365617263685F696E6465783D3130363026706C616E3D26665370656369616C6974793D30266163743D736561726368";

    //int countPaginas = 497;
    //int countMedicos = 3164;
    //int countProxy = 1;
    int countPaginas = 1;
    int countMedicos = 1;
    int countProxy = 1;
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



        /**-----*/
        String PROXY = "http://127.0.0.1:8118";
        org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
        proxy.setHttpProxy(PROXY)
                .setFtpProxy(PROXY)
                .setSslProxy(PROXY);
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(CapabilityType.PROXY, proxy);

        /********/

        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("/opt/chromedriver/chromedriver"))
                .usingAnyFreePort()

                .build();

        try {
            log.info("iniciando robo");
            service.start();
            driver = new RemoteWebDriver(service.getUrl(),cap);
            //driver = new RemoteWebDriver(service.getUrl(),
                   // DesiredCapabilities.chrome());

            //driver = new ChromeDriver(cap);
            driver.get(URL_SEARCH);
            log.info("abrindo pagina principal");
            Thread.sleep(7000);
            WebElement souPaciente = driver.findElement(By.linkText("Sou paciente"));

            if (souPaciente != null){
                try{
                    souPaciente.click();
                }catch(Exception e){
                    log.error("Erro ao clicar no link sou paciente");
                }

            }
            Thread.sleep(2000);

            WebElement proximaPagina = null;
            try{
                proximaPagina = driver.findElement(By.linkText(">"));
            }catch(Exception ex){
                log.error("nao existe proxima pagina");
            }

            log.info("verificando existencia de proxima pagina, existe? = " + (proximaPagina != null));
            String linkProximaPagina = "";
            while (proximaPagina != null){
                try{

                    if (linkProximaPagina != null){
                        URL_SEARCH = linkProximaPagina;
                    }
                    linkProximaPagina = proximaPagina.getAttribute("href");
                    List<String> links = this.getLinksMedicos(driver.findElements(By.cssSelector(".nameprop")));
                    log.info("Busca encontrou na pagina, " + links.size() + " medico(s)");

                    for (String linkMedico : links){
                        log.info("abrindo perfil...");
                        driver.get(linkMedico);
                        Thread.sleep(9000);
                        log.info("clicando nos links de telefones");
                        List<WebElement> linkTelefones = driver.findElements(By.className("seephone"));
                        for(WebElement e : linkTelefones ){
                            try{
                                e.click();
                                Thread.sleep(4000);
                            }catch(Exception ex){
                                log.error("Erro ao clicar no link sou paciente");
                            }

                        }
                        Thread.sleep(1000);
                        log.info("construindo medico...");
                        doc = Jsoup.parse(driver.getPageSource());
                        MedicoVo medico = makeMedico(doc);
                        log.info("TOTAL DE MEDICOS = " + countMedicos++);
                        String json = gson.toJson(medico);
                        this.appendFile(json);
                        log.info("MEDICO ADICIONADO COM SUCESSO, INDO PARAO PRÓXIMO.");
                        Thread.sleep(1000);
                    }
                    log.info("abrindo proxima pagina...");
                    driver.get(linkProximaPagina);


                    proximaPagina = driver.findElement(By.linkText(">"));
                    log.info("#######PAGINA = " + countPaginas++);

                    Thread.sleep(2000);
                }catch (Exception ex){
                    log.error("Erro:" ,ex);
                    boolean conseguiu = false;
                    int quantidade = 1;
                    while (!conseguiu  && quantidade < 8){

                        try{

                            log.info("ROBO MORREU NA PAGINA = " + countPaginas + " COM TOTAL DE MEDICOS = " + countMedicos + "NA PAGINA = " + linkProximaPagina);
                            log.info("REINICIANDO PROXY, QUANTIDADE = " + countProxy++);
                            executeCommand("/home/wagner/./reiniciaProxy.sh");
                            Thread.sleep(10000);
                            service.stop();
                            service.start();
                            Thread.sleep(5000);
                            driver = new RemoteWebDriver(service.getUrl(),cap);

                            //driver = new RemoteWebDriver(service.getUrl(),
                                    //DesiredCapabilities.chrome());

                            log.info("ABRINDO A PAGINA Q DEU PAU....");
                            driver.get(URL_SEARCH);
                            Thread.sleep(5000);

                            try{
                                souPaciente = driver.findElement(By.linkText("Sou paciente"));
                                if (souPaciente != null) {
                                    souPaciente.click();
                                }
                            }catch(Exception e){
                                log.error("Erro ao clicar no link sou paciente");
                            }

                            //log.info("abrindo proxima pagina...");
                            //driver.get(linkProximaPagina);
                            proximaPagina = driver.findElement(By.linkText(">"));
                            Thread.sleep(2000);
                            conseguiu = true;

                        }catch(Exception exx){
                            log.error("Ainda nao conseguiu...tentando novamente............");
                            quantidade ++;
                            continue;
                        }
                    }
                }
                log.info("############### TERMINOU LOOP COM UM TOTAL DE " + countMedicos + " MEDICOS, EM " + countPaginas +
                        " PAGINAS, COM  " + countProxy + " REINICIADAS DE PROXY E PAGINA = " + linkProximaPagina);


            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public void executeCommand(final String command){
        final ArrayList<String> commands = new ArrayList<String>();
        commands.add(command);
        BufferedReader br = null;
        try {
            final ProcessBuilder p = new ProcessBuilder(commands);
            final Process process = p.start();
            final InputStream is = process.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr); String line;
            while((line = br.readLine()) != null) {
                System.out.println("Retorno do comando = [" + line + "]");
            }
        } catch (Exception ioe) {
            log.error("Erro ao executar comando shell" + ioe.getMessage());

        } finally {
            secureClose(br);
        }
    }
    private void secureClose(final Closeable resource) {
        try {
            if (resource != null) {
                resource.close();
            }
        } catch (IOException ex) {
            log.error("Erro = " + ex.getMessage());
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
        if (rua != null && rua.size() >0){
            endVo.setLogradouro(rua.get(0).text());
        }
        Elements cidadeBairro = dado.select(".city");
        if (cidadeBairro != null && cidadeBairro.size() >0){
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
        if (cep != null && cep.size() > 0){
            endVo.setCep(cep.get(0).text().replace("CEP:",""));
        }

        Elements phone = dado.select(".phoneContainer");
        if (phone != null && phone.size() > 0){
            endVo.setTelefone(phone.get(0).text().replace("(Mencione o catalogo.med.br quando ligar.)", ""));
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
        if (enome != null && enome.size() > 0){
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
        if (crm != null && crm.size() > 0){
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
            }catch (Exception e){
                e.printStackTrace();
            }


        }


    }


}
