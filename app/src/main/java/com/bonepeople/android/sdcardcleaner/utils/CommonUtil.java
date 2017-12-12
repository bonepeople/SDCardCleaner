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
    public static int comparePath(String _path1, String _path2) {
        int _mark1 = 0, _mark2 = 0;
        char _char1, _char2;

        while (_mark1 < _path1.length() && _mark2 < _path2.length()) {
            _char1 = _path1.charAt(_mark1);
            _char2 = _path2.charAt(_mark2);
            if (Character.isDigit(_char1) && Character.isDigit(_char2)) {
                StringBuilder _str_number_1 = new StringBuilder();
                StringBuilder _str_number_2 = new StringBuilder();
                //获取第一个字符串中的数字
                while (_mark1 < _path1.length() && Character.isDigit(_path1.charAt(_mark1))) {
                    _str_number_1.append(_path1.charAt(_mark1));
                    _mark1++;
                }
                //获取第二个字符串中的数字
                while (_mark2 < _path2.length() && Character.isDigit(_path2.charAt(_mark2))) {
                    _str_number_2.append(_path2.charAt(_mark2));
                    _mark2++;
                }
                if (_str_number_1.length() == _str_number_2.length()) {
                    //将字符串转换为数字进行比较
                    BigInteger _number_1 = new BigInteger(_str_number_1.toString());
                    BigInteger _number_2 = new BigInteger(_str_number_2.toString());
                    int _result = _number_1.compareTo(_number_2);
                    if (_result != 0)
                        return _result;
                } else
                    return _str_number_1.length() - _str_number_2.length();
            } else if (Character.toUpperCase(_char1) == Character.toUpperCase(_char2)) {
                _mark1++;
                _mark2++;
            } else {
                return String.valueOf(_char1).compareToIgnoreCase(String.valueOf(_char2));
            }
            if (_mark1 == _path1.length() && _mark2 == _path2.length())
                return 0;
            if (_mark1 == _path1.length())
                return -1;
            if (_mark2 == _path2.length())
                return 1;
        }
        return 0;
    }
}
