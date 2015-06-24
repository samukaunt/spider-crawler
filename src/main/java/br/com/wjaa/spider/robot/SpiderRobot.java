package br.com.wjaa.spider.robot; /**
 * Created by wagner on 22/06/15.
 */

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author wagneraraujo-sao
 *
 */
public class SpiderRobot {



    private final String URL_SEARCH = "http://www.cremesp.org.br/?siteAcao=GuiaMedico&pesquisa=proc";
    private final CloseableHttpClient client = HttpClients.createDefault();
    CookieStore cookies = new BasicCookieStore();


    public List<Medico> letsGo() {
        List<Medico> medicos = new ArrayList<Medico>();
        try {

            HttpPost search = new HttpPost(URL_SEARCH);
            search.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            search.setHeader("Accept-Language","pt,en-US;q=0.8,en;q=0.6,pt-BR;q=0.4");
            search.setHeader("Cache-Control","max-age=0");
            search.setHeader("Connection","keep-alive");
            search.setHeader("Content-Type","application/x-www-form-urlencoded");
            search.setHeader("Host","www.cremesp.org.br");
            search.setHeader("Origin","http://www.cremesp.org.br");
            search.setHeader("Referer:",URL_SEARCH);
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("tipo", "nome"));
            postParams.add(new BasicNameValuePair("tipo_pesquisa", "avancada"));
            postParams.add(new BasicNameValuePair("palavra","a"));
            postParams.add(new BasicNameValuePair("cidade","SAO PAULO"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);
            search.setEntity(entity);

            HttpContext httpContext = new BasicHttpContext();

            httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookies);
            HttpResponse response = client.execute(search,httpContext);
            org.apache.http.Header[] headers1 = response.getAllHeaders();

            for(int i=0;i<headers1.length; i++)
            {
                if (headers1[i].getName().equals("Set-Cookie")){
                    System.out.println("Header Name 2 ::"+headers1[i].getName());
                    System.out.println("Header Val 2 ::"+headers1[i].getValue());
                }

            }

            InputStream page = response.getEntity().getContent();
            Document doc = Jsoup.parse(page, "ISO-8859-1", "?siteAcao=GuiaMedico&pesquisa=proc");
            //System.out.println(doc.body());
            Elements elements = doc.select("iframe");
            System.out.println(" src = " + elements.attr("src"));
            Scanner s = new Scanner(System.in);

            System.out.print("Entre com o codigo 1: ");
            String code1 = s.next();

            System.out.print("Entre com o codigo 2: ");
            String code2 = s.next();

            System.out.print("Entre com o hash: ");
            String hash = s.next();

            paginate(code1, code2, hash, medicos, "", 1, "");

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        medicos = applyFilter(medicos);

        return medicos;
    }



    private void paginate(String code1, String code2, String hash, List<Medico> medicos, String urlPage, Integer pagina, String urlAnterior ) {
        try{
            Document doc = null;
            if (pagina == 1){
                HttpPost search = new HttpPost(URL_SEARCH);
                search.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                search.setHeader("Accept-Language","pt,en-US;q=0.8,en;q=0.6,pt-BR;q=0.4");
                search.setHeader("Cache-Control","max-age=0");
                search.setHeader("Connection","keep-alive");
                search.setHeader("Content-Type","application/x-www-form-urlencoded");
                search.setHeader("Host","www.cremesp.org.br");
                search.setHeader("Origin","http://www.cremesp.org.br");
                search.setHeader("Referer:",URL_SEARCH);

                List<NameValuePair> postParams = new ArrayList<NameValuePair>();
                postParams.add(new BasicNameValuePair("tipo", "nome"));
                postParams.add(new BasicNameValuePair("tipo_pesquisa", "avancada"));
                postParams.add(new BasicNameValuePair("palavra","a"));
                postParams.add(new BasicNameValuePair("cidade","SAO PAULO"));
                postParams.add(new BasicNameValuePair("code",code1 + " " + code2));
                postParams.add(new BasicNameValuePair("coder","codigoOK"));
                postParams.add(new BasicNameValuePair("recaptcha_challenge_field",hash));
                postParams.add(new BasicNameValuePair("submit","Continuar"));

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);
                search.setEntity(entity);
                HttpContext httpContext = new BasicHttpContext();
                httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookies);
                HttpResponse response = client.execute(search, httpContext);
                InputStream page = response.getEntity().getContent();
                doc = Jsoup.parse(page, "ISO-8859-1", "?siteAcao=GuiaMedico&pesquisa=proc");

            }else{
                HttpGet get = new HttpGet(urlPage);
                get.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                get.setHeader("Accept-Language","pt,en-US;q=0.8,en;q=0.6,pt-BR;q=0.4");
                get.setHeader("Connection","keep-alive");
                get.setHeader("Host","www.cremesp.org.br");

                if (pagina == 2){
                    get.setHeader("Referer:", URL_SEARCH);
                }else{
                    get.setHeader("Referer:", urlAnterior);
                }
                HttpContext httpContext = new BasicHttpContext();
                httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookies);
                RequestConfig config =  RequestConfig.custom().setConnectTimeout(10000)
                        .setConnectionRequestTimeout(10000)
                        .setSocketTimeout(10000).build();
                httpContext.setAttribute(HttpClientContext.REQUEST_CONFIG, config);
                HttpResponse response = client.execute(get,httpContext);
                InputStream page = response.getEntity().getContent();
                doc = Jsoup.parse(page, "ISO-8859-1", "?siteAcao=GuiaMedico&pesquisa=proc");
            }

            medicos.addAll(makeMedico(doc.select(".fonte11 > tbody > tr > td > table")));

            Paginator paginator = this.getNextPage(doc, pagina);

            if (paginator != null){
                System.out.println("Pegando proxima pagina = " + paginator.getPagina());
                paginate(code1,code2,hash,medicos,paginator.getUrlPage(),paginator.getPagina(), urlPage);
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Paginator getNextPage(Document doc, Integer paginaAtual) {
        Elements as = doc.select("table.fonte11 > tbody > tr > td > a");

        if (as == null || as.size() == 0){
            // System.out.println("Doc = " + doc);
            System.out.println("Chegou no fim na pagina" + paginaAtual);
            return null;
        }
        for (int i = 0; i < as.size(); i++){
            String page = as.get(i).text().trim();
            if ( NumberUtils.isNumber(page) && Integer.valueOf(page) > paginaAtual){
                String url = "http://www.cremesp.org.br/" + as.attr("href");
                return new Paginator(url,Integer.valueOf(page));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Medico> applyFilter(List<Medico> medicos) {

		/*Filtrando os Medicos por regras para determinar se é um medico valido ou nao*/
        Collection<Medico> medicosFiltrados = CollectionUtils.select(medicos, new Predicate() {
            public boolean evaluate(Object o) {
                Medico p = (Medico)o;
                return p.validate();
            }
        });

        System.out.println("Resultado final, Medicos encontrados = " + medicos.size());
        System.out.println("Medicos filtrados = " + medicosFiltrados.size());

        return new ArrayList<Medico>(medicosFiltrados);
    }


    private CharSequence scape(String word) {

        try {
            return URLEncoder.encode(word.trim(), 	"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }



    public List<Medico> makeMedico(Elements medico){


        List<Medico> medicos = new ArrayList<Medico>();
        for (int i = 0; i < medico.size(); i++){
            Elements tds = medico.get(i).select("tbody > tr > td");
            Medico p = new Medico();

            p.setUrlFoto(tds.get(1).select("#foto").attr("style"));
            p.setNome(tds.get(3).text());
            p.setCrm(tds.get(2).text());
            p.setEmail(tds.get(4).text());
            p.setEspecialidades(tds.get(5).text());
            p.setEndereco(tds.get(6).text());
            p.setSituacao(tds.get(7).text());
            p.setInscritoEm(tds.get(2).text());
            //System.out.println(p);
            //System.out.println("--------------------------------");

            medicos.add(p);

        }
        return medicos;

    }


    private String getUrlImagem(Element e) {
        Elements es = e.getElementsByAttributeValue("class", "wss_resultadoBuscaConteudoVitrineImagemMedico");
        String src = es.attr("src");
        return src;
    }


    private Double getPrecoDe(Element e) {
        String value = e.getElementsByClass("wss_resultadoBuscaConteudoVitrineDetalheMedico")
                .get(0)
                .getElementsByClass("wss_resultadoBuscaConteudoVitrineDetalheMedicoPrecoRegular")
                .get(0)
                .getElementsByTag("strong")
                .text()
                .replaceAll("[^0-9,]", "").replace(",", ".");
        return NumberUtils.isNumber(value)? Double.valueOf(value): 0.0d ;
    }


    private Double getPrecoPor(Element e) {
        String value = e.getElementsByClass("wss_resultadoBuscaConteudoVitrineDetalheMedico")
                .get(0)
                .getElementsByClass("wss_resultadoBuscaConteudoVitrineDetalheMedicoPrecoVenda")
                .get(0)
                .getElementsByTag("strong")
                .text()
                .replaceAll("[^0-9,]", "").replace(",", ".");
        return NumberUtils.isNumber(value)? Double.valueOf(value): 0.0d ;
    }


    private String getDescricao(Element e) {
        String descricao = e.getElementsByClass("wss_resultadoBuscaConteudoVitrineTituloMarca").text() + "<br/>" +
                e.getElementsByClass("wss_resultadoBuscaConteudoVitrineTituloMedico").text();
        return descricao;
    }


    private Integer getUltimaPagina(InputStream page) throws IOException{
        Document docMoved = Jsoup.parse(page, "UTF-8", "web/Resultado.aspx?q=");

        Pattern p = Pattern.compile("pagina=[0-9]+");
        Matcher m = p.matcher(docMoved.html());
        String paginaStr = "";
        while(m.find()){
            paginaStr = m.group();
        }
        String pagina = paginaStr.replaceAll("[^0-9]", "");
        return NumberUtils.isNumber(pagina)? Integer.valueOf(pagina) : 0;

    }


    public static void main (String args[]){
        SpiderRobot sr = new SpiderRobot();
        sr.letsGo();
    }


}