package kr.pe.sdh.core.util;

import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 각종 키를 생성
 * @author 성동훈
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
public class KeyGen {

	static int keyCount =0;
	static String thisTime ="";
	protected static Random randomNumberGenerator = new Random();

	/**
	 * 시간으로 키생성
	 * @return
	 */
	public static String getTimeKey(){
		return getTimeKey(null);
	}

	/**
	 * 시간을 파라미터의 길이로 키생성
	 * @param nsize
	 * @return
	 */
	public static String getTimeKey(int nsize){
		return getTimeKey(null,nsize);
	}

	/**
	 * 시간에 파라미터를 접두어로 붙여 키생성
	 * @param prefix
	 * @return
	 */
	public static String getTimeKey(String prefix) {
		return getTimeKey(prefix,0);
	}

	/**
	 * 시간에 파라미터를 접두어로 붙여 파라미터의 길이로 키생성
	 * @param prefix
	 * @param nsize
	 * @return
	 */
	public static synchronized String getTimeKey(String prefix,int nsize) {
		String jobId;
		if (prefix==null) prefix ="";
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		Date currentTime = new Date();
		jobId = formatter.format(currentTime)+prefix;
		
		String seq =Integer.toString(incCount(jobId));
		
		StringBuilder sb =new StringBuilder();
		sb.append(jobId);
		for(int i=0;i<nsize-seq.length();i++){
			sb.append("0");
		}
		sb.append(seq);
		
		thisTime =jobId;
		
		return sb.toString();
	}
	protected static  int incCount(String ctime)
	{
		if (ctime.equals(thisTime)){
			keyCount++;
			}else{
				keyCount =1;
				thisTime =ctime;
		}
		return keyCount;
		
		
	}
	public static synchronized String getRandomKey(String strDomainName)
    {
        String dname =strDomainName==null?"":"@"+strDomainName;
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		Date currentTime = new Date();
		String jobId = formatter.format(currentTime);

		return (new StringBuffer(jobId)).append(Integer.toString(Math.abs(Integer.MAX_VALUE))).append(dname).toString();
    }

	public static synchronized String getRandomTimeKey()
    {
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
		Date currentTime = new Date();
		String jobId = formatter.format(currentTime);
		int iran = (new Random()).nextInt(1000);
		return (new StringBuffer(jobId)).append(Integer.toString(iran)).toString();
    }
	private static int seq;
	/**
	 * random 한 변수를 얻어온다
	 */
	public static synchronized String getKey() 
	{
		if( seq == 999 ) seq = 0;

		String msg = String.valueOf ( System.currentTimeMillis() + new DecimalFormat( "000" ).format( seq++ ));

		char[] seed = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
						'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
						'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
						'u', 'v', 'w', 'x', 'y', 'z',
						'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
						'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
						'U', 'V', 'W', 'X', 'Y', 'Z' };

		BigInteger zero = BigInteger.ZERO;//new BigInteger(0);
		BigInteger length = new BigInteger("" + seed.length);
		BigInteger val = new BigInteger( msg );

		char[] result = new char[ 30 ];
		int i = 0;
		while(val.compareTo(zero) > 0)
		{
			BigInteger mok = val.divide(length);
			BigInteger nam = val.mod(length);
			val = mok;
			result[i++] = seed[nam.intValue()];
		}
		char[] retval = new char[i];
		System.arraycopy(result, 0, retval, 0, i);
		return String.valueOf( retval );
	}

	private static final int startYear = 5;
	private static final int alpabetCnt = 26;
	// 65(A) to 90(Z)
	public static String getAutoGenPassNo(String heatNo, String inLotNo) {
		String passNo = heatNo;
		 if(passNo.length() > 5) passNo = passNo.substring(0, 6);
		 
		 // 단조년
		 if(inLotNo.length() > 8 && StringUtils.isNumeric(inLotNo.substring(7, 9))) {
			 passNo += "-";
			 passNo += (char)((int)'A' + ((Integer.parseInt(inLotNo.substring(7, 9)) - startYear) % alpabetCnt));
		 }
		 
		 // 단조월
		 if(inLotNo.length() > 10 && StringUtils.isNumeric(inLotNo.substring(9, 11))) {
			 int month = Integer.parseInt(inLotNo.substring(9, 11));
			 if(month > 12) {
			 } else {
				 passNo += (char)((int)'A' + month);
			 }
		 }
		 
		 // 단조일
		 if(inLotNo.length() > 12 && StringUtils.isNumeric(inLotNo.substring(11, 13))) {
			 int day = Integer.parseInt(inLotNo.substring(11, 13));
			 if(day > 31) {
			 } else {			 
				 if(day >= alpabetCnt) {
					 passNo += Integer.toString(++day - alpabetCnt);
				 } else {
					 passNo += (char)((int)'A' +day); 
				 }
			 }
		 }		
		 
		 if(inLotNo.length() > 13) {
			 passNo += inLotNo.substring(13, 14);
		 }
		 
		 if(inLotNo.length() > 23) {	// 열처리 입력받음
			 // 열처리년 
			 if(StringUtils.isNumeric(inLotNo.substring(15, 17))) {
				 passNo += "-";				 
				 passNo += (char)((int)'A' + ((Integer.parseInt(inLotNo.substring(15, 17)) - startYear) % alpabetCnt));
			 }
			 
			 // 열처리월
			 if(StringUtils.isNumeric(inLotNo.substring(17, 19))) {
				 int month = Integer.parseInt(inLotNo.substring(17, 19));				 
				 if(month > 12) {
				 } else {
					 passNo += (char)((int)'A' + month);
				 }				 
			 }
			 
			 // 열처리일
			 if(StringUtils.isNumeric(inLotNo.substring(19, 21))) {
				 int day = Integer.parseInt(inLotNo.substring(19, 21));
				 if(day > 31) {
				 } else {			 
					 if(day >= alpabetCnt) {
						 passNo += Integer.toString(++day - alpabetCnt);
					 } else {
						 passNo += (char)((int)'A' +day); 
					 }
				 }
			 }				 
		 }

		 if(inLotNo.length() > 22) {
			 passNo += inLotNo.substring(21, 22);
		 }		 
	
		return passNo;
	}	
}
