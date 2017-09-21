package kr.pe.sdh.core.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * DocUtil
 * - ASIS 소스
 * @author ASIS
 * @since 2017-01-05
 * @version 1.0
 * @see
 *
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 * 수정일      수정자  수정내용
 * ----------- ------- ---------------------------
 * 2017.01.05  성동훈  최초 생성
 *
 * </pre>
 */
public class DocUtil {
    private static Logger logger = LoggerFactory.getLogger(DocUtil.class);
    
    /**
     * 16자리의 키값을 입력하여 객체를 생성한다.
     * @param key 암/복호화를 위한 키값
     * @throws UnsupportedEncodingException 키값의 길이가 16이하일 경우 발생
     */
	private static String secretKey = "1234567890123456";
	private static String IV =  secretKey.substring(0,16);

	
    /**
     * AES256 으로 암호화 한다.
     * @param str 암호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
	public static String encrypt(String str) throws GeneralSecurityException, UnsupportedEncodingException
	{
		if (str==null)	return null;

	    byte[] keyData = secretKey.getBytes();
	    
	    SecretKey secureKey = new SecretKeySpec(keyData, "AES");
	     
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
		
		byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));

		return new String(Base64.encodeBase64(encrypted));
	}
	
    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     * @param str 복호화할 문자열
     * @return
     * @throws NoSuchAlgorithmException
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(String str) throws GeneralSecurityException, UnsupportedEncodingException {
    	
    	if (StringUtils.isEmpty(str)) 	return "";
    	
	    byte[] keyData = secretKey.getBytes();
	    SecretKey secureKey = new SecretKeySpec(keyData, "AES");
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes("UTF-8")));

        byte[] byteStr = Base64.decodeBase64(str.getBytes());
        String result = "";
        try{
        	result = new String(c.doFinal(byteStr), "UTF-8");
        }catch (Exception e){
        	e.printStackTrace();
        }
        return result;
    }
	
	public static String getSTRFilter(String str){
		if (str==null) return null;

	//		  String strlistchar   = ""; 
		String str_imsi;
		String []filter_word = {"","\\.","\\?","\\/","\\~","\\!","\\@","\\#","\\$","\\%","\\^","\\&","\\*","\\(",
			  				  "\\)","\\_","\\+","\\=","\\|","\\\\","\\}","\\]","\\{","\\[","\\\"","\\'","\\:","\\;","\\<","\\,","\\>","\\.","\\?","\\/"};

		for (String aFilter_word : filter_word) {
			//while(str.indexOf(filter_word[i]) >= 0){
			str_imsi = str.replaceAll(aFilter_word, "");
			str = str_imsi;
			//}
		} 

		return str; 
	} 
	
	/**
	 * 수출신고서 제출번호 체크섬구하기
	 * @param rptNo
	 * @return
	 */
	public static String getCheckDigitExp(String rptNo){
		//생성된 제출번호는 13자리
		int[] checkArr = {7,3,1,7,3,1,7,3,1,7,3,1,7};
		int chkNum;
		int chkSum = 0;
		for (int i=0; i<13; i++){
			chkNum = Integer.parseInt(rptNo.substring(i,1));
			chkNum = (chkNum * checkArr[i]) % 10;
			chkSum += chkNum;
		}
		chkSum = chkSum % 10;
		if (chkSum!=0) chkSum = 10 - chkSum;
		return rptNo+chkSum;
	}
	/**
	 * 주어진문자열에서 스페이스지우기
	 */
	public static String removeSpace(String szText){
		return szText.replaceAll("(!/|\r|\n| )", "");  // 공백제거
	}
	/**
	 * 주어진 텍스트를 몇개의 같은크기를 가진 문자열로나누어 그중 retSeq(1부터시작)번째를 리턴 
	 * strCutArray("12한글3", 3, 2) => 한
	 */
	public static String strCutArray(String szText, int cutSize, int arrCnt, int retSeq){
		String[] retArr = strCutArray( szText,  cutSize,  arrCnt);
		return retArr[retSeq-1];
	}
	/** 
	 * 주어진 텍스트를 몇개의 같은크기를 가진 문자열로 나눈다. 한글처리. 2byte 
	 * strCutArray("12한글3", 3, 3) => [12,한,글]
	 */
	public static String[] strCutArray(String szText, int cutSize, int arrCnt){
		if (StringUtils.isEmpty(szText)){
			szText = "";
		}
		String[] retArr = new String[arrCnt];
		String[] tmpArr;
		String remainText = szText;
		for (int i=0;i<arrCnt;i++){
			tmpArr = strCut(remainText, "", cutSize, 0);
			remainText = tmpArr[1];
			retArr[i] = tmpArr[0];
		}
		
		return retArr;
	}
	/**
	 * String[] resarr = strCut(aa, "", 35, 0); //원하는 만큼 돌려도 빈값으로 리턴됨.
	 * aa = resarr[1];
	 */
	public static String[] strCut(String szText, String szKey, int nLength, int nPrev){  // 문자열 자르기
	    
		String[] result =  new String[2];
	    String r_val = szText;
	    int oF = 0, oL = 0, rF = 0, rL = 0;
	    int nLengthPrev = 0;

	    //r_val = r_val.replaceAll("&", "&");
	    //r_val = r_val.replaceAll("(!/|\r|\n| )", "");  // 공백제거
	  
	    try {
	      byte[] bytes = r_val.getBytes("UTF-8");     // 바이트로 보관
	 
	      if(szKey != null && !szKey.equals("")) {
	        nLengthPrev = (!r_val.contains(szKey))? 0: r_val.indexOf(szKey);  // 일단 위치찾고
	        nLengthPrev = r_val.substring(0, nLengthPrev).getBytes("MS949").length;  // 위치까지길이를 byte로 다시 구한다
	        nLengthPrev = (nLengthPrev-nPrev >= 0)? nLengthPrev-nPrev:0;    // 좀 앞부분부터 가져오도록한다.
	      }
	    
	      // x부터 y길이만큼 잘라낸다. 한글안깨지게.
	      int j = 0;
	 
	      if(nLengthPrev > 0) while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          oF+=2; rF+=3; if(oF+2 > nLengthPrev) {break;} j+=3;
	        } else {if(oF+1 > nLengthPrev) {break;} ++oF; ++rF; ++j;}
	      }
	      
	      j = rF;
	 
	      while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          if(oL+2 > nLength) {break;} oL+=2; rL+=3; j+=3;
	        } else {if(oL+1 > nLength) {break;} ++oL; ++rL; ++j;}
	      }
	 
	      r_val = new String(bytes, rF, rL, "UTF-8");  // charset 옵션
	  
	      result[0] =  r_val;
	      result[1] =  new String(bytes, rL, bytes.length-rL, "UTF-8");
	    } catch(UnsupportedEncodingException e){ e.printStackTrace(); }  
	    
	    return result;  //return r_val
	}
	/**
	 * 문자열을 2byte기준으로 필요한 길이만큼 잘라서 리턴
	 */
	public static String getByteLenStr(String str, int len){
		return strCutArray(str, len, 1, 1);
	}
	/**
	 * 한글을 2byte로 계산하여 길이를 체크
	 * @throws UnsupportedEncodingException 
	 */
	public static int length2Byte(String str) throws UnsupportedEncodingException {
		int len = 0;
		len =  str.getBytes("MS949").length;
		return len;
	}
	
	
	/* 금액을 계산할때 사용 */
	/* float를 읽어서 정수부분만 리턴 */
	public static String parseFloatIntPart(float f){
		String s = Float.toString(f);
		int pos = s.indexOf(".");
		s = s.substring(0, pos);
		return s;
	}
	/* 소수점 pos에서 계산 round(2.567, 1)=2.6*/
	public static float round(float f, int pos){
		return generalRound(f, pos, "round");
	}
	/* 소수점 pos에서 계산 ceil(2.567, 1)=2.6*/
	public static float ceil(float f, int pos){
		return generalRound(f, pos, "ceil");
	}
	/* 소수점 pos에서 계산 floor(2.567, 1)=2.5*/
	public static float floor(float f, int pos){
		return generalRound(f, pos, "floor");
	}
	/* 소수점 pos에서 계산 round(2.567, 1)=2.6*/
	public static float generalRound(float in_f, int pos, String method){
		int i = pos;
		if (pos>0){
			while (i > 0){
				in_f = in_f*10;
				i--;
			}
		}else if (pos<0){
			while (i < 0){
				in_f = in_f/10f;
				i++;
			}
		}
		if ("round".equals(method)){
			in_f = Math.round(in_f);
		}else if ("ceil".equals(method)){
			in_f = (float)Math.ceil(in_f);
		}else if ("floor".equals(method)){
			in_f = (float)Math.floor(in_f);
		}
		
		BigDecimal in_f_bd = new BigDecimal(parseFloatIntPart(in_f));
		BigDecimal c_bd = BigDecimal.TEN;//new BigDecimal("10");
		
		i = pos;
		if (pos>0){
			while (i > 0){
				//in_f = in_f/10f;
				in_f_bd = in_f_bd.divide(c_bd);
				i--;
			}
		}else if (pos<0){
			while (i < 0){
				//in_f = in_f*10f;
				in_f_bd = in_f_bd.multiply(c_bd);
				i++;
			}
		}
		//return in_f;
		return in_f_bd.floatValue();
	}
	/**
	 * 환율을 곱하여 절사
	 * @param exchRate
	 * @param amt
	 * @return
	 */
	public static String calcExchWonFloor(String exchRate, String amt){
		if (StringUtils.isEmpty(exchRate)){
			exchRate = "0";
		}
		exchRate =  exchRate.replaceAll(",", "");
		if (StringUtils.isEmpty(amt)){
			amt = "0";
		}
		amt =  amt.replaceAll(",", "");

		float amt_f = Float.parseFloat(amt);
		float exchRate_f = Float.parseFloat(exchRate);
		float won_f = amt_f * exchRate_f;  
		won_f = floor(won_f, 0); //원단위절사 (수정전)
		//won_f = round(won_f, 0); //원단위 Round (수정후)
		return parseFloatIntPart(won_f);
	}
	/**
	 * 단가수량을 받아 가격을 리턴
	 * @param unitCostS
	 * @param qtyS
	 * @return
	 */
	public static String calcSumAmt(String unitCostS, String qtyS){
		if (StringUtils.isEmpty(unitCostS)||"null".equals(unitCostS)){
			unitCostS = "0";
		}
		unitCostS =  unitCostS.replaceAll(",", "");
		if (StringUtils.isEmpty(qtyS)||"null".equals(unitCostS)){
			qtyS = "0";
		}
		qtyS =  qtyS.replaceAll(",", "");
		
		float amt = Float.parseFloat(unitCostS);
		int qty = Integer.parseInt(qtyS);
		float sumamt = amt * qty;
		return Float.toString(sumamt);
	}
	/**
	 * 수출신고서 생성시 상품무게가 없다면 전체기준을 1로 잡고 품목수로 1을 나눈값을 개별 항목 무게로 잡는다.
	 * @param itemsCnt
	 * @return
	 */
	public static String calcBasicWeight(int itemsCnt){
		float wt = 1f;
		float basicWt = wt/itemsCnt;
		return Float.toString(round(basicWt, 1));	//소수점 두자리에서 반올림.
	}
	
	
	/**
	 * 기본값 설정 요청맵, 몰/개별맵, 전체맵에서 정보를 받아 값을 만듬.
	 * @param reqVal
	 * @param baseVal
	 * @return
	 */
	public static String chooseItemValue(String reqVal, String baseVal){
		String val;
		if ( StringUtils.isEmpty(reqVal)){
			if (StringUtils.isEmpty(baseVal)){
				val = "";
			}else{
				val = baseVal;
			}
		}else{
			val = reqVal;
		}
		return val;
	}
	
	/**
	 * 에러체크용 JSON 에러메세지 생성
	 * @param itemNm
	 * @param orgValue
	 * @param errJSON
	 * @return
	 */
	public static String jsonStr(String itemNm, String orgValue, String errJSON){
		if (StringUtils.isEmpty(errJSON)){
			errJSON += "\""+itemNm+"\":\""+orgValue+"\"";
		}else{
			errJSON += ",\""+itemNm+"\":\""+orgValue+"\"";
		}
		return errJSON;
	}
	
	/**
	 * 신고입력값 정제처리및 개별항목처리
	 * @param itemNm
	 * @param itemValue
	 * @return
	 */
	public static String checkItemValue(String itemNm, String itemValue){
		if ( itemValue==null){
			return "";
		}
		
		if (itemNm.equals("BRANDNAME_EN")){
			itemValue = removeSpace(itemValue);
		}
		
		return itemValue.toUpperCase();
	}
	/**
	 * 
	 * @param orgValue
	 * @param altValue
	 * @return
	 */
	public static String isEmptyReplace(String orgValue, String altValue){
		if (StringUtils.isEmpty(orgValue)) return altValue;
		else return orgValue;
	}
	public static String isEmpty(String orgValue){
		return isEmptyReplace(orgValue, "");
	}
	public static String getUUID(){
		return UUID.randomUUID().toString();
		
	}
	public static String getApiKey(){
		String uuid = getUUID();

		//현재시간과 랜덤값으로 UUID생성
		long millis = System.currentTimeMillis();
		java.util.Random r = new java.util.Random(millis);
		long rnd = r.nextLong();
		
		UUID uuid2 = new UUID(rnd, millis);
		String uuid2Str = uuid2.toString();
		
		return uuid + "-" + uuid2Str;
	}
	public static String getSaltStr(){  //이걸 써서 문자열 가지고 올 것 뒤에다 붙일 것
		return getUUID();
	}
	public static String encodeBase64(String str){
		String result;
		
		//sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		//result = encoder.encode(str.getBytes());
		//Window-->Preferences-->Java-->Compiler-->Error/Warnings Deprecated and Restricted API. Change it to warning
		//Access restriction: The type BASE64Encoder is not accessible due to restriction on required library C:\Program Files\Java\jre7\lib\rt.jar
		//의 에러가 발생함. 아래라이브러리로 대체하면 가능
		
		result = new String(Base64.encodeBase64(str.getBytes()));
		
		return result;
	}
	public static String decodeBase64(String str){
		String result;
//		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
//		try{
//			result = new String(decoder.decodeBuffer(str));
//		} catch (java.io.IOException ioe){
//			ioe.printStackTrace();
//		}
		Base64 base64Encoder = new Base64();
		result = new String(base64Encoder.decode(str.getBytes()));
		
		return result;
	}
	
	/**
	 * 신고인부호를 검증한다.  신고인부호는 다섯자리 숫자로 구성됨을 확인.
	 * @param applicantId
	 * @return
	 */
	public static boolean checkApplicantId(String applicantId){
		if ( applicantId==null ) return false;
		int len = 0;
		
		try {
			len = length2Byte(applicantId);
		} catch (UnsupportedEncodingException e) {
			logger.info("{}", e);
		}
		if (len != 5){
			return false;
		}
		String regex = "^[0-9]*$";
		return applicantId.matches(regex);
	}
	/**
	 * 우편번호를 확인한다. 다섯자리 숫자로 구성됨을 확인.
	 * @param applicantId
	 * @return
	 */
	public static boolean checkPostId(String applicantId){
		if ( applicantId==null ) return false;
		int len = 0;
		
		try {
			len = length2Byte(applicantId);
		} catch (UnsupportedEncodingException e) {
			logger.info("{}", e);
		}		
		if (len != 5){
			return false;
		}
		String regex = "^[0-9]*$";
		return applicantId.matches(regex);
	}
	/* 항목별 결제금액과 수량을 받아 단가를 계산한다 */
	public static String calcUnitPrice(String declPrice, String unitCnt){
		float declPriceF;
		float unitCntF;
		float unitPriceF;
		try {
			declPriceF = Float.parseFloat(declPrice);
			unitCntF = Float.parseFloat(unitCnt);
			
			if (unitCntF>0){
				unitPriceF = declPriceF/unitCntF;
				unitPriceF = floor(unitPriceF, 2);
			}else{
				return "";
			}
		} catch (Exception e){
			return "";
		}
		return Float.toString(unitPriceF);
	}
	
	/**
	 * 영문대문자, 숫자, 스페이스, 허용문자열()-_;:',./"
	 */
	public static boolean isEnglishOnlySymbols(String strVal){
		
		char chrInput;
		String[] acceptedStrArr = {" ", "(", ")", "-", "_", ";", ":", "'", ",", ".", "/", "\""};
		
		for (int i=0;i<strVal.length();i++){
			chrInput = strVal.charAt(i);
			if ( chrInput >= 0x61 && chrInput <= 0x7A ){
				//영문소문자
			} else if ( chrInput>= 0x41 && chrInput<=0x5A ) {
				//영문대문자
			}else if ( chrInput>=0x30 && chrInput<=0x39 ){
				//숫자
			}else{
				if (acceptedStrArr != null && acceptedStrArr.length > 0 ){
					boolean chkArr = false;
					for ( String acceptedStr : acceptedStrArr){
						char acceptedStrChr = acceptedStr.charAt(0);
						if ( acceptedStrChr==chrInput ){
							chkArr = true;
							break;
						}
					}
					if (chkArr){
						//허용된문자열
					}else{
						return false;
					}
				}else{
					return false;
				}
			}
			
		}
		
		return true;
	}
	
	public static String lpadbyte(String value, int length, String filler) {
		
		if ( filler == null ) {
			filler = " ";	//기본값 스페이스
		}
		
		int count = 0;
		
		for (int i = 0 ; i < value.length() ; i++) {
			char chr = value.charAt(i);
			if (chr >= 0x0001 && chr <= 0x007F) {
				count++;
			} /*else if (chr > 0x07FF) {	//한글	UTF에서 3바이트이므로 2바이트로 계산
				count += 2;
			} */else {
				count += 2;
			}
		}
		
		StringBuilder result = new StringBuilder();
		for ( int i = count ; i < length ; i ++ ) {
			result.append (filler);
		}
		result.append(value);
		return result.toString ();
	}
	
	/**
	 * XML Pretty Print  기능<br/>
	 * - Element 별 Enter, 하위 Node 들여쓰기
	 * 
	 * @param in XML 원문 스트링
	 * @return Pretty Print 처리 된 XML 스트링
	 * @throws Exception
	 */
	public static String xmlPrettyPrint(String in) {
		try{ 
	        Source xmlInput = new StreamSource(new StringReader(in.replaceAll(">\\s+<", "><")));
	        
	        StreamResult xmlOutput = new StreamResult(new StringWriter());
	
	        // Configure transformer
	        Transformer transformer = TransformerFactory.newInstance().newTransformer(); // An identity transformer
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(xmlInput, xmlOutput);
	
	        return xmlOutput.getWriter().toString().replaceAll("\\?><", "\\?>\n<");
		} catch(Exception e) {
			return in;
		}
	}
	
	public static boolean isEngNumSpcCharOnly(String strVal) {
		// 알파벳, 숫자, 공백, 특수문자만 허용하나 특수문자(!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~)에서 <>& 제외
		return Pattern.matches("^[a-zA-Z0-9\\p{Punct} \\t&&[^<>&]]*$", strVal);
	}
	
	public static boolean checkExportDeclIdPattern(String declId){
		String pattern = "^([가-힣]{2}[*]{4}|[가-힣]{3}[*]{2}|[가-힣]{4})([a-zA-Z0-9]{7})";
        return Pattern.compile(pattern).matcher(declId).matches();
    }
}
