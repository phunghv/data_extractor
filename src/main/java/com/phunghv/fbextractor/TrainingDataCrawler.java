package com.phunghv.fbextractor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.phunghv.fbextractor.utils.TextUtils;

/**
 * @author FungJi
 * @version 23:25 10102016 Using class to prepare training data from crawled
 *          data.
 */
public class TrainingDataCrawler {
	private static final int CHOTHUE = 0;
	private static final int TIMPHONG = 1;
	private static final int OGHEP = 2;
	private static final int SPAM = 3;
	private static int[] total = { 0, 0, 0, 0 };
	private static final String[][] sign = { { "can phong", "muon thue phong", "tim phong", "con trong" }, // index
																											// =
																											// 0
																											// tim
																											// phong
			{ "co nha", "cho thue", "nhuong lai", "co phong", "chinh chu", "dien tich" }, // index
																							// =1
																							// :
																							// cho
																							// thue
			{ "ghep" } // index = 2: o ghep
	};

	private static boolean checkType(String message, String[] check) {
		for (String x : check) {
			if (message.contains(x)) {
				return true;
			}
		}
		return false;
	}
	// private FileWriter chothueFileWriter = null;
	// private FileWriter timphongFileWriter = null;
	// private FileWriter oghepFileWriter = null;
	// private FileWriter spamWriter = null;

	public void initialize(String path) {
		// create directory to save all data from string path input
		Path pathx = Paths.get(path);
		try {
			Files.createDirectories(pathx);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void createTrainingData(String path, String message) {
		int type = this.classify(message);
		total[type]++;
		String fileName = String.format("%06d", total[type]) + ".txt";
		this.writeToFile(message, path, fileName, type);
	}

	private int classify(String text) {
		String message = TextUtils.normalizeAndToLowerCase(text);
		for (int i = 0; i < sign.length; i++) {
			if (this.checkType(message, sign[i])) {
				return i;
			}
		}
		return SPAM;
	}

	private void writeToFile(String content, String path, String fileName, int type) {
		String dir = "spam";
		if (type == 0) {
			dir = "timphong";
		} else if (type == 1) {
			dir = "chothue";
		} else if (type == 2) {
			dir = "oghep";
		} else {
			// defaul
			dir = "spam";
		}
		dir = path + File.separator + dir;
		Path pathx = Paths.get(dir);
		try {
			if (!Files.exists(pathx)) {
				Files.createDirectories(pathx);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {

			File file = new File(dir + File.separator + fileName);
			// if file doesnt exists, then create it
			System.out.println(file.getAbsolutePath());
			if (!file.exists()) {
				file.createNewFile();
			}
			System.out.println("Write to file : " + file.getAbsolutePath());
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < sign.length; i++) {
			for (int j = 0; j < sign[i].length; j++) {
				System.out.println(sign[i][j]);
			}
		}
		System.out.println(String.format("%05d", 10));
	}
}
