import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class Manager {
    private static int N = 10;

    public static void main(String[] args) throws Exception {

        // construct an arraylist containing the origin url
        // 164 is total page number
        int TPN = 164;
        String[] originurl = new String[TPN];
        String friend = "http://m.byr.cn/board/Friends?p=";
        for (int i = 1; i <= TPN; i++) { 
            originurl[i-1] = friend + i;
        }


        ExecutorService pool = Executors.newFixedThreadPool(N);        

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpclient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();

        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(200);

        Pattern p = Pattern.compile("/article/Friends/\\d+");

        try {
            //DownloadPage[] origindown = new DownloadPage[TPN];
            ArrayList<Future<String>> origincon = new ArrayList<Future<String>>(TPN);

            for (int i = 0; i <TPN; i++) {
                
                //origindown[i] = new DownloadPage(httpclient, originurl[i]);
                DownloadPage origindown = new DownloadPage(httpclient, originurl[i]);

                Future<String> result = pool.submit(origindown);
                origincon.add(result);
            }
           
            // get the urls 
            System.out.println("Start getting urls");

            for (int i = 0; i < TPN; i++) {

                HrefExtractor he = new HrefExtractor(origincon.get(i).get(), p, queue);
                Thread thread = new Thread(he);
                pool.execute(thread);
            }

        }
        finally {
            httpclient.close();
        }
        


        pool.shutdown();


        // test
        while (queue.size() > 0) {
            System.out.println(queue.take());
        }


    }
}
