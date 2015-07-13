package spider;

import java.util.ArrayList;

import java.util.concurrent.TimeUnit;
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
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);

        CloseableHttpClient httpclient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();

        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(200);

        Pattern p = Pattern.compile("/article/Friends/\\d+");

        ArrayList<Future<String>> origincon = new ArrayList<Future<String>>(TPN);
        ArrayList<Future<String>> con = new ArrayList<Future<String>>();

        try {
            //DownloadPage[] origindown = new DownloadPage[TPN];

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


                System.out.println("HrefExtractor " + i + " started");
            }

            System.out.println(queue.size());
            // get the real content
            while (true) {
                String str = queue.poll(10, TimeUnit.SECONDS);
                if (null == str) {
                    System.out.println(" no urls to download anymore");
                    break;
                }
                else {
                    str = "http://m.byr.cn" + str;
                    System.out.println("start downloading from "+ str);

                    DownloadPage origindown = new DownloadPage(httpclient, str);
                    Future<String> result = pool.submit(origindown);
                    con.add(result);

                    System.out.println("test");
                }
            }
        }
        finally {
        }
        /*
        finally {
            httpclient.close();
        }
        */
        
        String tmp = null;
        for (int i = 0; i < con.size(); i++) {
            tmp = con.get(i).get();
        }

        pool.shutdown();

        System.out.println(tmp);

        System.out.println(con.size());

        /*/ test
        while (queue.size() > 0) {
            System.out.println(queue.take());
        }
        */

    }
}
