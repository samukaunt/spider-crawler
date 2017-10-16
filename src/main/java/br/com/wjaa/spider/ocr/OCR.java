package br.com.wjaa.spider.ocr;

import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OCR {
    private static final String INPUT = "/home/wagner/Downloads/javaocr/testes/image.jpg";
    private static final String OUTPUT = "/home/wagner/Downloads/javaocr/testes/image-ocr.png";
    private static final String TESSERACT_BIN = "tesseract";
    private static final String TESSERACT_OUTPUT = "/home/wagner/Downloads/javaocr/testes/out.txt";
    private static final int WHITE = 0x00FFFFFF, BLACK = 0x00000000;

    private static List<Transportador> transportadors = new ArrayList<>();

    public static void main(String... args) throws Exception {
        System.out.println("Iniciando tentativa de quebrar algum captcha....");
        letsGoSelenium();
        System.out.println("Selenium iniciado....");
        start();

    }

    static int count = 1;

    private static void start() throws IOException, InterruptedException {

        List<String> cnpjs = getCnpjs();

        for (String cnpj : cnpjs) {

            if (cnpj.length() > 11 && cnpj.length() < 14){
                cnpj = String.format("%014d", new Long(cnpj));
                if (!isValidCNPJ(cnpj)){
                    appendErrorFile(cnpj);
                    return;
                }
            }

            if (cnpj.length() < 11){
                cnpj = String.format("%011d", new Long(cnpj));
                if (!isValidCPF(cnpj)){
                    appendErrorFile(cnpj);
                    return;
                }
            }

            boolean retry = true;
            while (retry) {

                System.out.println("Iniciando tentativa: " + count + " para o cnpj = " + cnpj);
                saveImage();
                System.out.println("Imagem do captcha salva");
                String captcha = getCaptcha();
                System.out.println("resultado na tentativa de quebrar o captcha");
                System.out.println("CAPTCHA: " + captcha);
                if (captcha == null) {
                    count++;
                    continue;
                }
                System.out.println("Um captcha foi quebrando tentando submiter o formulario.");

                if (!submitForm(captcha, cnpj)) {
                    count++;
                    continue;
                }
                retry = false;
            }

            System.out.println("Captcha quebrado na tentativa: " + count + " cnpjs para o cnpj = " + cnpj);
            System.out.println("################ FIM #######################");
            System.out.println(new GsonBuilder().create().toJson(transportadors));

        }
        //driver.close();
        //service.stop();

    }

    private static final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] pesoCNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    public static boolean isValidCPF(String cpf) {
        if ((cpf==null) || (cpf.length()<10)) return false;


        Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
        Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
        return cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString());
    }


    public static boolean isValidCNPJ(String cnpj) {
        if ((cnpj==null)||(cnpj.length()<13)) return false;

        Integer digito1 = calcularDigito(cnpj.substring(0,12), pesoCNPJ);
        Integer digito2 = calcularDigito(cnpj.substring(0,12) + digito1, pesoCNPJ);
        return cnpj.equals(cnpj.substring(0,12) + digito1.toString() + digito2.toString());
    }

    private static int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }
        soma = 11 - soma % 11;
        return soma > 9 ? 0 : soma;
    }



    private static List<String> getCnpjs() throws IOException {
        return IOUtils.readLines(new FileInputStream(new File("/home/wagner/Downloads/javaocr/testes/cnpjs.csv")));

    }

    private static void appendFile(String json) {
        try {
            FileWriter fw = new FileWriter("/home/wagner/Downloads/javaocr/testes/transportador.json", true); //the true will append the new data
            fw.write(json + ",");//appends the string to the file
            fw.write("\n");//appends the string to the file
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    private static void appendErrorFile(String cnpj) {
        try {
            FileWriter fw = new FileWriter("/home/wagner/Downloads/javaocr/testes/cnpj_invalidos.json", true); //the true will append the new data
            fw.write(cnpj + ",");//appends the string to the file
            fw.write("\n");//appends the string to the file
            fw.close();
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    private static boolean submitForm(String capcha, String cnpj) throws InterruptedException {
        driver.findElement(By.id("recaptcha_response_field")).sendKeys(capcha);
        WebElement inputCnpj = driver.findElement(By.cssSelector("input.mask-cpf-cnpj"));
        inputCnpj.clear();
        inputCnpj.sendKeys(cnpj);
        driver.findElement(By.id("btn-consultar")).click();

        Thread.sleep(6000);

        String msg = null;

        try {
            msg = driver.findElement(By.cssSelector("span.field-validation-error")).getText();
            if (msg != null) {
                System.out.println("NAO DEU CERTO MSG: '" + msg + "'");
                return false;
            }
        } catch (Exception ex) {

        }
        System.out.println("DEU CERTO INICIANDO A COLETA DOS DADOS" + msg);

        try{

            WebElement we = driver.findElement(By.cssSelector("#div-resultado .box-body"));
            Transportador t = getTransportador(we, cnpj);
            if (t != null){
                appendFile(new GsonBuilder().create().toJson(t));
                transportadors.add(t);
            }
        }catch(Exception ex){

            Thread.sleep(5000);
            WebElement we = driver.findElement(By.cssSelector("#div-resultado .box-body"));
            Transportador t = getTransportador(we, cnpj);
            if (t != null){
                appendFile(new GsonBuilder().create().toJson(t));
                transportadors.add(t);
            }


        }


        return true;

    }

    private static Transportador getTransportador(WebElement we, String cnpj) {
        try{

            Transportador t = new Transportador();
            List<WebElement> listDados = we.findElements(By.className("row"));
            WebElement dados1 = listDados.get(0);
            try {
                t.setNome(dados1.findElement(By.cssSelector(".form-group .col-md-10")).getText());
            } catch (Exception ex) {

            }

            WebElement dados2 = listDados.get(1);

            try {
                t.setCpfCnpj(dados2.findElements(By.cssSelector(".form-group .col-md-3")).get(0).getText());
                t.setDesde(dados2.findElement(By.cssSelector(".form-group .col-md-4")).getText());
            } catch (Exception ex) {

            }

            WebElement dados3 = listDados.get(2);

            try {
                t.setRntrc(dados3.findElements(By.cssSelector(".form-group .col-md-3")).get(0).getText());
                t.setValidade(dados3.findElement(By.cssSelector(".form-group .col-md-4")).getText());
            } catch (Exception ex) {

            }

            WebElement dados4 = listDados.get(3);

            try {
                t.setSituacaoRntrc(dados4.findElements(By.cssSelector(".form-group .col-md-3")).get(0).getText());
                t.setMunicipio(dados4.findElement(By.cssSelector(".form-group .col-md-4")).getText());
            } catch (Exception ex) {

            }

            return t;
        }catch(Exception ex){
            System.out.println("ERRO AO PEGAR OS DADOS DO TRANSPORTADOR PULANDO O CNPJ:" + cnpj);
            appendErrorFile(cnpj);
        }
        return null;

    }

    private static void saveImage() throws IOException, InterruptedException {
        driver.findElement(By.id("recaptcha_reload")).click();
        Thread.sleep(2000);

        try{

            WebElement inputCaptcha = driver.findElement(By.id("recaptcha_challenge_image"));
            String image = inputCaptcha.getAttribute("src");
            URL url = new URL(image);
            BufferedImage buffImage = ImageIO.read(url);
            ImageIO.write(buffImage, "jpg", new File("/home/wagner/Downloads/javaocr/testes/image.jpg"));
        }catch(Exception ex){
            Thread.sleep(5000);
            WebElement inputCaptcha = driver.findElement(By.id("recaptcha_challenge_image"));
            String image = inputCaptcha.getAttribute("src");
            URL url = new URL(image);
            BufferedImage buffImage = ImageIO.read(url);
            ImageIO.write(buffImage, "jpg", new File("/home/wagner/Downloads/javaocr/testes/image.jpg"));
        }

    }

    private static String getCaptcha() throws IOException, InterruptedException {
        BufferedImage image = ImageIO.read(new FileInputStream(INPUT));
        int average = 0;

        for (int row = 0; ++row < image.getHeight(); )
            for (int column = 0; ++column < image.getWidth(); )
                average += image.getRGB(column, row) & 0x000000FF;
        average /= image.getWidth() * image.getHeight();

        for (int row = 0; ++row < image.getHeight(); )
            for (int column = 0; ++column < image.getWidth(); )
                if ((image.getRGB(column, row) & 0x000000FF) <= average * .80)
                    image.setRGB(column, row, BLACK);
                else
                    image.setRGB(column, row, WHITE);

        for (int row = 0; ++row < image.getHeight(); )
            for (int column = 0; ++column < image.getWidth(); )
                if ((image.getRGB(column, row) & WHITE) == WHITE) {
                    int height = countVerticalWhite(image, column, row);
                    int width = countHorizontalWhite(image, column, row);
                    if ((width * height <= 6) || (width == 1) || (height == 1))
                        image.setRGB(column, row, BLACK);
                }

        for (int row = 0; ++row < image.getHeight(); )
            for (int column = 0; ++column < image.getWidth(); )
                if ((image.getRGB(column, row) & WHITE) != WHITE)
                    if (countBlackNeighbors(image, column, row) <= 3)
                        image.setRGB(column, row, WHITE);

        ImageIO.write(image, "png", new File(OUTPUT));
        Process tesseractProc = Runtime.getRuntime().exec(TESSERACT_BIN + " " + OUTPUT + " " + TESSERACT_OUTPUT + " nobatch letters");
        tesseractProc.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(TESSERACT_OUTPUT + ".txt")));

        String captcha = reader.readLine();
        reader.close();
        return captcha;
    }


    private static ChromeDriverService service;
    private static WebDriver driver;

    private static void letsGoSelenium() {
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("/opt/chromedriver/chromedriver"))
                .usingAnyFreePort()
                .build();

        try {
            service.start();
            driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
            driver.get("http://consultapublicarntrc.antt.gov.br/consultapublica");
            Thread.sleep(2000);
            /*MODO PODRE ONDE O USUÁRIO DIGITARÁ O CAPTCHA*/
            /*Scanner s = new Scanner(System.in);
            System.out.print("Entre com o codigo 1: ");
            String code = s.next();
            inputCaptcha.sendKeys(code);*/

            //searchBox.quit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private static int countVerticalWhite(BufferedImage image, int x, int y) {
        return (countAboveWhite(image, x, y) + countBelowWhite(image, x, y)) + 1;
    }

    private static int countHorizontalWhite(BufferedImage image, int x, int y) {
        return (countLeftWhite(image, x, y) + countRightWhite(image, x, y)) + 1;
    }

    private static int countLeftWhite(BufferedImage image, int x, int y) {
        int leftWhite = 0;
        x--;
        while (x-- > 0)
            if ((image.getRGB(x, y) & WHITE) == WHITE)
                leftWhite++;
            else
                break;
        return leftWhite;
    }

    private static int countBelowWhite(BufferedImage image, int x, int y) {
        int rightWhite = 0;
        y++;
        while (y < image.getHeight())
            if ((image.getRGB(x, y++) & WHITE) == WHITE)
                rightWhite++;
            else
                break;
        return rightWhite;
    }

    private static int countAboveWhite(BufferedImage image, int x, int y) {
        int leftWhite = 0;
        y--;
        while (y-- > 0)
            if ((image.getRGB(x, y) & WHITE) == WHITE)
                leftWhite++;
            else
                break;
        return leftWhite;
    }

    private static int countRightWhite(BufferedImage image, int x, int y) {
        int rightWhite = 0;
        x++;
        while (x < image.getWidth())
            if ((image.getRGB(x++, y) & WHITE) == WHITE)
                rightWhite++;
            else
                break;
        return rightWhite;
    }

    private static int countBlackNeighbors(BufferedImage image, int x, int y) {
        int numBlacks = 0;
        if (pixelColor(image, x - 1, y) != WHITE)
            numBlacks++;
        if (pixelColor(image, x - 1, y + 1) != WHITE)
            numBlacks++;
        if (pixelColor(image, x - 1, y - 1) != WHITE)
            numBlacks++;
        if (pixelColor(image, x, y + 1) != WHITE)
            numBlacks++;
        if (pixelColor(image, x, y - 1) != WHITE)
            numBlacks++;
        if (pixelColor(image, x + 1, y) != WHITE)
            numBlacks++;
        if (pixelColor(image, x + 1, y + 1) != WHITE)
            numBlacks++;
        if (pixelColor(image, x + 1, y - 1) != WHITE)
            numBlacks++;
        return numBlacks;
    }

    private static int pixelColor(BufferedImage image, int x, int y) {
        if (x >= image.getWidth() || x < 0 || y < 0 || y >= image.getHeight())
            return WHITE;
        return image.getRGB(x, y) & WHITE;
    }

}
