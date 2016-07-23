package com.dcp.extractor.data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Paging;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import facebook4j.auth.OAuthAuthorization;
import facebook4j.auth.OAuthSupport;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;
import vn.hus.nlp.tokenizer.Tokenizer;
import vn.hus.nlp.tokenizer.VietTokenizer;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

public class App {
	public static int MAX_PAGE = 10;

	public static Configuration createConfiguration() {
		ConfigurationBuilder confBuilder = new ConfigurationBuilder();

		confBuilder.setDebugEnabled(true);

		// String url = "https://graph.facebook.com/v2.2/";
		// System.out.println(url);
		// confBuilder.setRestBaseURL(url);
		confBuilder.setOAuthAppId("1543380259298935");
		confBuilder.setOAuthPermissions(
				"user_groups,user_events,user_managed_groups,rsvp_event,publish_pages,user_photos,user_posts,user_friends");
		confBuilder.setOAuthAppSecret("8fea96f557e4ea87afc067a9276069e9");
		confBuilder.setUseSSL(true);
		confBuilder.setJSONStoreEnabled(true);
		Configuration configuration = confBuilder.build();
		return configuration;
	}

	public static void main(String[] args) throws FacebookException, IOException {
		Configuration configuration = createConfiguration();
		FacebookFactory facebookFactory = new FacebookFactory(configuration);
		Facebook facebookClient = facebookFactory.getInstance();
		AccessToken accessToken = null;
		try {
			OAuthSupport oAuthSupport = new OAuthAuthorization(configuration);
			accessToken = oAuthSupport.getOAuthAppAccessToken();
		} catch (FacebookException e) {
			System.err.println("Error while creating access token " + e.getLocalizedMessage());
		}
		facebookClient.setOAuthAccessToken(accessToken);
		System.out.println("Access Token" + accessToken);
		/* Get feeds from facebook group */
		ResponseList<Post> feeds = facebookClient.getFeed("443537385830239");
		VietTokenizer tokenizer = new VietTokenizer("tokenizer.properties");
		FileWriter writter = new FileWriter("out.txt");
		/* Processing post by post */
		int count = 0;
		for (int countPage = 0; countPage < MAX_PAGE; countPage++) {
			for (int i = 0; i < feeds.size(); i++) {
				// Get post.
				Post post = feeds.get(i);
				count++;
				// Get (string) message.
				String message = post.getMessage();
				message = (message == null) ? "" : message;
				processing(tokenizer, message, writter);
				writter.write("\r\n\r\n\r\n__________________________________________\r\n");
				System.out.println("===================================================================");
			}
			Paging<Post> paging = feeds.getPaging();
			feeds = facebookClient.fetchNext(paging);
		}
		writter.close();
	}

	private static void processing(VietTokenizer tokenizer, String message, FileWriter writter) throws IOException {
		String[] listString = tokenizer.tokenize(message);
		Tokenizer a = tokenizer.getTokenizer();
		List<TaggedWord> result = a.getResult();
		for (TaggedWord d : result) {
			System.out.println(String.format("%20s%30s", d.getRule().getName(), d.getText()));
			writter.write(String.format("%20s%30s\r\n", d.getRule().getName(), d.getText()));
		}
	}
}