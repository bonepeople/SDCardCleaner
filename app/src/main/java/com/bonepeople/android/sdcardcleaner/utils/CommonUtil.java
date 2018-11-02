package com.bonepeople.android.sdcardcleaner.utils;

import java.math.BigInteger;

/**
 * 通用工具
 * Created by bonepeople on 2017/12/10.
 */

public class CommonUtil {

    /**
     * 对比两个路径（用于排序）
     */
    public static int comparePath(String path1, String path2) {
        int mark1 = 0, mark2 = 0;
        char char1, char2;

        while (mark1 < path1.length() && mark2 < path2.length()) {
            char1 = Character.toLowerCase(path1.charAt(mark1));
            char2 = Character.toLowerCase(path2.charAt(mark2));
            if (Character.isDigit(char1) && Character.isDigit(char2)) {
                StringBuilder str_number_1 = new StringBuilder();
                StringBuilder str_number_2 = new StringBuilder();
                //此处可以两个字符串同步获取数字，当某一个数字位数大于另一个的时候直接返回大小，可以加快判断速度减小内存占用
                //在获取数字的时候需要对开头为0的数字进行处理，避免由补0导致的位数差异而影响数字大小的判断
                //获取第一个字符串中的数字
                while (mark1 < path1.length() && Character.isDigit(path1.charAt(mark1))) {
                    str_number_1.append(path1.charAt(mark1));
                    mark1++;
                }
                //获取第二个字符串中的数字
                while (mark2 < path2.length() && Character.isDigit(path2.charAt(mark2))) {
                    str_number_2.append(path2.charAt(mark2));
                    mark2++;
                }
                if (str_number_1.length() == str_number_2.length()) {
                    //将字符串转换为数字进行比较
                    BigInteger number_1 = new BigInteger(str_number_1.toString());
                    BigInteger number_2 = new BigInteger(str_number_2.toString());
                    int result = number_1.compareTo(number_2);
                    if (result != 0)
                        return result;
                } else
                    return str_number_1.length() - str_number_2.length();
            } else if (char1 == char2) {
                mark1++;
                mark2++;
            } else {
                return char1 - char2;
            }
        }
        if (mark1 == path1.length())
            if (mark2 == path2.length())
                return 0;
            else
                return -1;
        else
            return 1;
    }
}
