package myNetty.domain;

/**
 * @author Yurii Solopko
 * класс оболочка для таблицы количества переадресаций по URL
 */
public class RedirectRequest {

    private String url;

    private Long count;

    public RedirectRequest() {

    }

    public RedirectRequest(String url, Long count) {
        this.url = url;
        this.count = count;
    }

    @Override
    public String toString() {
        return "URL = " + url + ", No. of redirects = " + count;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
