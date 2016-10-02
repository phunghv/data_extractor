package org.dataextractor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.json.simple.parser.ParseException;

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

	public static int MAX_PAGE = 1;
	public static boolean CRAWL = true;
	public static String[] WORD_ECLECTRICTY = { "điện:", "đ:", "điện", "đ", "Điện", "Đ", "Điện" };
	public static String[] WORD_WARTER = { "nước:", "nc:", "n:", "nước", "nc", "n" };
	public static String[] WORD_ROM_PRICE = { "giá", "₫", "giá:", "₫:", "phòng" };
	public static String[] WORD_LEASE = { "cho thuê nhà", "cho thuê" };
	public static String[] WORD_TENANT = { "cần thuê nhà", "cần thuê", "cần tìm", "cần thuê phong" };
	public static String[] WORD_DUSTY = { "thanh lý", "sim", "bán" };
	public static String[] PREFIX_PLACE = { "o", "khu vuc", "tai", "dia chi", "ngo", "đc", "khu", "gan", "duong" };
	public static int LEASE = 1;
	public static int TENANT = 2;
	public static int DUSTY = 3;
	public int countMessage = 0;

	public App() {
	}

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

	public static void main(String[] args) throws Exception {
		App app = new App();
		app.extractPost();
	}

	public void anlanyticPost(String json, String key) throws ParseException {

	}

	public void anlanyticMessage(List<TaggedWord> taggedWord) {
		countMessage++;
		System.out.println("Message " + countMessage);
		for (TaggedWord w : taggedWord) {
			System.out.println("========================");
			System.out.println("Line : " + w.getLine());
			System.out.println("Column: " + w.getColumn());
			System.out.println("Text: " + w.getText());
			System.out.println("Hash: " + w.hashCode());
			System.out.println("String: " + w.toString());
			System.out.println("========================");
		}
	}

	public void typeOfPost(String message) {

	}

	public void extractPost() throws FacebookException, IOException {
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
		ResponseList<Post> feeds = facebookClient.getFeed("451604054903475");
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream file = classLoader.getResourceAsStream("crawl_tokenizer.properties");
		Properties properties = new Properties();
		properties.load(file);
		VietTokenizer tokenizer = new VietTokenizer(properties);
		tokenizer.turnOnSentenceDetection();
		FileWriter writter = new FileWriter("out.txt");
		int count = 0;
		for (int countPage = 0; countPage < MAX_PAGE; countPage++) {
			for (int i = 0; i < feeds.size(); i++) {
				Post post = feeds.get(i);
				count++;
				String message = post.getMessage();
				message = (message == null) ? "" : message;
				System.out.println(message);
				processing(tokenizer, message, writter);
			}
			Paging<Post> paging = feeds.getPaging();
			feeds = facebookClient.fetchNext(paging);
		}
		writter.close();
	}

	public static boolean inArray(String[] arrayString, String value) {
		for (int i = 0; i < arrayString.length; i++) {
			if (value.equals(arrayString[i])) {
				return true;
			}
		}
		return false;
	}

	private static boolean checkType(String message, String[] check) {
		for (String x : check) {
			if (message.contains(x)) {
				return true;
			}
		}
		return false;
	}

	private void processing(VietTokenizer tokenizer, String message, FileWriter writter) throws IOException {
		String[] listString = tokenizer.tokenize(message);
		System.out.println(listString);
//		Tokenizer a = tokenizer.getTokenizer();
///		List<TaggedWord> result = a.getResult();
//		anlanyticMessage(result);
	}

	// Phung's function
	private static void process(Scanner sc, String message, FileWriter writter1, FileWriter writter2,
			FileWriter writter3, FileWriter writter4, FileWriter writter5) throws IOException {
		System.out.println(message);
		System.out.println("_____________________");
		// System.out.println("Select ? 1: thue 2: cho thue 3 : o ghep 4: tim o
		// ghep #: spam");
		String[] thuePhong = { "can phong", "muon thue phong", "tim phong", "con trong" };
		String[] choThue = { "co nha", "cho thue", "nhuong lai", "co phong", "chinh chu", "dien tich" };
		String[] oGhep = { "ghep" };
		// String[] timOGhep = {""};
		String temp = message.toLowerCase();
		temp = temp.replaceAll("[̣̀̃̉́]", "");
		temp = temp.replaceAll("̣₫", "đ");
		temp = temp.replaceAll("[iìỉĩíịj]", "i");
		temp = temp.replaceAll("[eèẻẽéẹêềểễếệ]", "e");
		temp = temp.replaceAll("[aàảãáạăằẳẵắặâầẩẫấậ]", "a");
		temp = temp.replaceAll("[uùủũúụưừửữứự]", "u");
		temp = temp.replaceAll("[oòỏõóọôồổỗốộơờởỡớợ]", "o");
		temp = temp.replaceAll("[yỳỷỹýỵ]", "y");
		// System.out.println(temp);
		// System.exit(0);
		int n = 0;
		if (checkType(temp, thuePhong)) {
			n = 1;
		} else if (checkType(temp, choThue)) {
			n = 2;
		} else if (checkType(temp, oGhep)) {
			n = 3;
		} else {
			// n = 0;
		}

		switch (n) {
		case 1:
			writter1.write(temp + "\r\n ===================================== \r\n");

			break;
		case 2:
			writter2.write(temp + "\r\n ===================================== \r\n");
			break;
		case 3:
			writter3.write(temp + "\r\n ===================================== \r\n");
			break;
		case 4:
			writter4.write(temp + "\r\n ===================================== \r\n");
			break;
		default:
			writter5.write(temp + "\r\n ===================================== \r\n");
		}
	}

	public String filterVNString(String string) {
		string = string.replaceAll("[̣̀̃̉́]", "");
		string = string.replaceAll("̣₫", "đ");
		string = string.replaceAll("[iìỉĩíịj]", "i");
		string = string.replaceAll("[eèẻẽéẹêềểễếệ]", "e");
		string = string.replaceAll("[aàảãáạăằẳẵắặâầẩẫấậ]", "a");
		string = string.replaceAll("[uùủũúụưừửữứự]", "u");
		string = string.replaceAll("[oòỏõóọôồổỗốộơờởỡớợ]", "o");
		string = string.replaceAll("[yỳỷỹýỵ]", "y");
		return string;
	}
}