import java.util.concurrent.Callable;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class DownloadPage implements Callable<String> {
    private String html;
    private final String url;
    private final CloseableHttpClient client;
    private final HttpContext context;
    private final HttpGet httpget;

    public DownloadPage(CloseableHttpClient httpclient,String url) {
        this.client = httpclient;
        this.context = new BasicHttpContext();
        this.httpget = new HttpGet(url);
        this.url = url;
    }

    private void getHTML() {
        try {
            CloseableHttpResponse response = client.execute(httpget,context);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    html = EntityUtils.toString(entity);
                }
                else {
                    System.out.println(url +" fetch failed");
                }
            }
            finally {
                response.close();
            }
        }
        catch (Exception e) {
            System.out.println(url + " error:" + e);
        }
    }
    
    public String call() {
        getHTML();
        return html;
    }
}
