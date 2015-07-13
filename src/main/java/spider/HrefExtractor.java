package spider;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HrefExtractor implements Runnable {

    private String str;
    private Pattern p;
    private ArrayBlockingQueue<String> queue;
    
    public HrefExtractor(String str, Pattern p, ArrayBlockingQueue<String> queue) {
        
        this.str = str;
        this.p = p;
        this.queue = queue;
    }

    private void extractor () throws InterruptedException {
        
        Matcher m = p.matcher(str);

        while(m.find()) {
            queue.put(m.group());
        }
    }

    public void run() {

        try {
            extractor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("one thread for getting url is interrupted");
        }
    }
}
