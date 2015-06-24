package br.com.wjaa.spider.robot;

/**
 * Created by wagner on 23/06/15.
 */
public class Paginator {


    public Paginator(String urlPage,Integer pagina){
        this.urlPage = urlPage;
        this.pagina = pagina;
    }


    private String urlPage;

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(Integer pagina) {
        this.pagina = pagina;
    }

    public String getUrlPage() {
        return urlPage;
    }

    public void setUrlPage(String urlPage) {
        this.urlPage = urlPage;
    }

    private Integer pagina;

}
