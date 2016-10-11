package com.phunghv.fbextractor.utils;

public class TextUtils {
	public static String normalizeAndToLowerCase(String message) {
		String temp = message.toLowerCase();
		temp = temp.replaceAll("[̣̀̃̉́]", "");
		temp = temp.replaceAll("̣₫", "đ");
		temp = temp.replaceAll("[iìỉĩíịj]", "i");
		temp = temp.replaceAll("[eèẻẽéẹêềểễếệ]", "e");
		temp = temp.replaceAll("[aàảãáạăằẳẵắặâầẩẫấậ]", "a");
		temp = temp.replaceAll("[uùủũúụưừửữứự]", "u");
		temp = temp.replaceAll("[oòỏõóọôồổỗốộơờởỡớợ]", "o");
		temp = temp.replaceAll("[yỳỷỹýỵ]", "y");
		return temp;
	}
}
