package ch.unil.wsa;

import java.io.PrintWriter;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class App extends Configured implements Tool {
	private static final String FS_PARAM_NAME = "fs.defaultFS";
	private static final String consumer = Config.CONSUMER;
	private static final String consumerSecret = Config.CONSUMER_SECRET;
	private static final String token = Config.TOKEN;
	private static final String tokenSecret = Config.TOKEN_SECRET;
	
	public int run(String[] args) throws Exception {
		// public static void run(String consumerKey, String consumerSecret,
		// String token, String secret) throws InterruptedException {
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

		endpoint.languages(Lists.newArrayList("fr"));
		endpoint.trackTerms(Terms.all());
		
		Authentication auth = new OAuth1(consumer, consumerSecret, token, tokenSecret);
		// Authentication auth = new BasicAuth(username, password);

		// Create a new BasicClient. By default gzip is enabled.
		Client client = new ClientBuilder().hosts(Constants.STREAM_HOST).endpoint(endpoint).authentication(auth)
				.processor(new StringDelimitedProcessor(queue)).build();

		// Establish a connection
		client.connect();
		
		try {
			System.setProperty("HADOOP_USER_NAME", "hduser");
			DateTimeFormatter date = DateTimeFormatter.ofPattern("YYYY-MM-dd_hh-mm-ss");
			final Path path = new Path("WSA17-ADV-presidentielleFR-" + date.format(java.time.LocalDateTime.now()) + ".txt");

			try (final DistributedFileSystem dFS = new DistributedFileSystem() {
				{
					initialize(new URI("hdfs://localhost:54310"), new Configuration());
					System.out.println("dFS is initialized");
				}
			};
			final FSDataOutputStream streamWriter = dFS.create(path);
			final PrintWriter writer = new PrintWriter(streamWriter, true );) {
				System.out.println("Writer is instanciated.");
				int j = 0;
				while (true) {
					for(int i = 0; i<100 ;++i){
					//System.out.println("The size of the queue is : " + queue.size());
					String msg = queue.take();
					//System.out.println(msg);
					//writer.println(msg);
					writer.write(msg);
					writer.flush();
					}
					streamWriter.hflush();
				j++;
				
				System.out.println(j + "00 messages received.");
				}

			}
		} catch (Exception e) {
			client.stop();
			this.run(args);
		}
		return 0;
	}
    public static void main( String[] args ) throws Exception {
    	try{
	    	App app = new App();
	    	app.run(args);
    	}catch(Exception e){
    		e.getStackTrace();
    	}
    }
}
