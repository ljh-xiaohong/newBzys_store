package com.bapm.bzys.newBzys_store.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) 2014 All rights reserved
 * 名称：CommonUtil.java
 * 描述：工具类
 * Author: 小洪
 * Date： 2015/1/19 13:19
 * Version：1.0
 */
public class CommonUtil {

    /*判断字符串是否为空*/
    public static boolean isNull(String text) {
        if (text == null || "".equals(text.trim()) || "null".equals(text))
            return true;
        return false;
    }

    /*检查手机号码用户名*/
    public static Boolean checkUserName(String userName) {
        if (userName.length() != 11 || !checkPhoneNum(userName)) {
            return false;
        }
        return true;
    }

    /*检查手机号码用户名*/
    public static Boolean checTelPhone(String userName) {
        if (userName.length() >= 7&&userName.length() <= 11 ) {
            return true;
        }
        return false;
    }

    /*检查用户密码*/
    public static Boolean checkPwd(String pwd) {
        if (pwd.length() != 6) {
            return false;
        }
        return true;
    }
    /*检查用户密码*/
    public static Boolean checkPwds(String pwd) {
        if (pwd.length() < 6||pwd.length()>10) {
            return false;
        }
        return true;
    }

    /*验证手机号码*/
    public static boolean checkPhoneNum(String userPhone) {
        String regExp = "^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(19[0-9])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(userPhone);
        return m.matches();
    }


    /*验证网址*/
    public static boolean checkUrl(String url) {
        String regExp = "^((https?|ftp|news):\\/\\/)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))(\\/[a-z0-9_\\-\\.~]+)*(\\/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(url);
        return m.matches();
    }

    /*验证邮箱*/
    public static boolean checkEmail(String email) {
        String regExp ="^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /*隐藏手机号中间位数，用*号显示*/
    public static String compilePhone(String mobile) {
        return mobile.replaceAll("(?<=\\d{3})\\d(?=\\d{4})", "*");
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 验证身份证号是否符合规则
     * @param text 身份证号
     * @return
     */
    public static boolean personIdValidation(String text) {
        if (CommonUtil.isNull(text)){
            return false;
        }
        String regx = "[0-9]{17}x";
        String regX = "[0-9]{17}X";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        return text.matches(regx) ||text.matches(regX) || text.matches(reg1) || text.matches(regex);
    }

}
