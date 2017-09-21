package kr.pe.sdh.core.util;

import java.security.MessageDigest;

/**
 * @author 성동훈
 * @version 1.0
 * @see <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017-04-28  성동훈  최초 생성
 * </pre>
 * @since 2017-04-28
 */
public class Sha256 {
    public static boolean compareEncrypt(String compare1, String compare2) {
        return encrypt(compare1).equals(encrypt(compare2));
    }

    public static String encrypt(String password) {
        try{
            if(password == null) return "";

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            StringBuffer sb = new StringBuffer();
            for(int i=0; i<byteData.length; i++){
                sb.append(Integer.toString((byteData[i] & 0xff & byteData[i])));
            }

            StringBuffer hexString = new StringBuffer();
            for(int i=0; i<byteData.length; i++){
                String hex = Integer.toHexString(0xff & byteData[i]);
                if(hex.length() == 1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        }catch (Exception e){
            throw new RuntimeException();
        }

    }
}
