package kr.pe.sdh.core.util;

import kr.pe.sdh.core.exception.BizException;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date 객체 관련 Util
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
public class DateUtil {

	@Value("#{config['convert.util.DefaultDateformat']}")
	private static String defaultDateFormat;

	/**
	 * 금일 날짜를 문자열로 반환
	 * @return
	 */
	public static String getToday() {
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		//String hour = String.valueOf(cal.get(Calendar.HOUR));
		//String minute = String.valueOf(cal.get(Calendar.MINUTE));
		//ex)월은 두자리표현 - 9월 -> 09월, 10월 -> 10월

		month = (month.length() == 1) ? "0" + month : month;
		day = (day.length() == 1) ? "0" + day : day;

		return year + month + day;
	}

	/**
	 * 금일 날짜를 파라미터의 형식으로 문자열 반환
	 * @param format
	 * @return
	 */
	public static String getToday(String format)     
	{
		Date date = getTimeZone();
		return(getFormatedDate(date, format));
	}

	/**
	 * 서버의 타입존을 반환
	 * @return
	 */
	public  static Date getTimeZone()
	{
		Calendar utcCal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		return utcCal.getTime();
	}

	/**
	 * date 파라미터 Date 객체를 파라미터 format의 형식으로 문자열로 변환하여 반환
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getFormatedDate(Date date, String format)
	{
		if (date == null) {
			return "";
		} else {
			SimpleDateFormat formatter = new SimpleDateFormat();
			formatter.applyPattern(format);
			return formatter.format(date);
		}
	}
	
	/**
	 * 원하는 날짜형식으로 오늘 날짜를 리턴한다.
	 * 
	 * @see SimpleDateFormat
	 * @param pattern
	 * @return string 패턴화된 날짜 문자열
	 */
	public static String today(String pattern) {
		return format(new Date(), pattern);
	}
	
	/**
	 * 현재 날짜를 반환한다.
	 * 
	 * @return timestamp
	 */
	public static Timestamp stamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 파라미터의 날짜를 반환한다.
	 *
	 * @return timestamp
	 */
	public static Timestamp stamp(String datestr) throws Exception {
		return stamp(datestr, defaultDateFormat);
	}
	
	/**
	 * datestr을 pattern 형식으로 날짜를 반환
	 * @param datestr
	 * @return
	 */
	public static Timestamp stamp(String datestr, String pattern) throws Exception {
		return new Timestamp(parse(datestr, pattern).getTime());
	}
	
	/**
	 * 주어진 Date를 pattern화 된 문자열로 반환한다.
	 * 
	 * @param date 패턴화할 날짜
	 * @param pattern string 패턴
	 * @return string 패턴화된 날짜 문자열
	 */
	public static String format(Date date, String pattern) {
		String str;
		if(date.toString().substring(0,19).equals("0001/01/01 00:00:00")) {
			str = "";
		} else {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			str = simpleDateFormat.format(date);
		}
		return str;
	}
	
	/**
	 * fromPattern형식의 날짜 문자열을 toPattern형식의 날짜 문자열로 반환한다.
	 * 
	 * @param datestr
	 * @param fromPattern
	 * @param toPattern
	 * @return string
	 */
	public static String format(String datestr, String fromPattern, String toPattern) throws Exception {
		return format(parse(datestr, fromPattern), toPattern);
	}
	
	/**
	 * 현재 날짜를 기준으로 원하는 시점의 날짜를 구함
	 * 
	 * @param amount 원하는 날짜 시점 (10일 후를 원하면 10, 10일 전을 원하면 -10)
	 * @param pattern 날짜 format
	 * @return string
	 */
	public static String format(int amount, String pattern) {
        return format(amount, pattern, Calendar.DAY_OF_MONTH);
    }
	
	public static String format(int amount, String pattern, int gubun) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(gubun, amount);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(calendar.getTime());
    }
	
	/**
	 * 특정일자 를 기준으로 원하는 시점의 날짜를 구함
	 * formatstr(일자포멧)은 fromdatestr(특정일자)의 포멧과 동일해야 한다.
	 * 사용예 addDate(10, "yyyy-MM-dd", "2007-01-09");
	 * 
	 * @param amount 원하는 날짜 시점 (10일 후를 원하면 10, 10일 전을 원하면 -10)
	 * @param pattern 특정일자의 포멧
	 * @param datestr 특정일자(기준일자)
	 * @return string
	 */
	public static String format(int amount, String pattern, String datestr) throws Exception {
		return format(amount, pattern, datestr, Calendar.DAY_OF_MONTH);
    }
	
	public static String format(int amount, String pattern, String datestr, int gubun) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parse(datestr, pattern));
        calendar.add(gubun, amount);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(calendar.getTime());
    }
	
	/**
	 * pattern형식으로 포맷된 날짜 문자열을 java.util.Date 형태로 반환한다.
	 * 
	 * @param datestr
	 * 			date string you want to check.
	 * @param pattern
	 * 			string representation of the date format. For example, "yyyy-MM-dd".
	 * @return date
	 */
	public static Date parse(String datestr, String pattern) throws Exception {
		if (datestr == null) {
			throw new BizException("date string to check is null");
		}
		
		if (pattern == null) {
			throw new BizException("format string to check date is null");
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.KOREA);
		Date date;
		
		try {
			date = formatter.parse(datestr);
		} catch (ParseException e) {
			throw new BizException(" wrong date:\"" + datestr + "\" with format \"" + pattern + "\"");
		}
		
		if (!format(date, pattern).equals(datestr)) {
			throw new BizException("Out of bound date:\"" + datestr + "\" with format \"" + pattern + "\"");
		}
		return date;
	}
	
	public static String getDateTime(){
		return getDateTime("yyyyMMddHHmmss");
	}
	public static String getDateTime(String format){
		long time = System.currentTimeMillis();
		SimpleDateFormat date = new SimpleDateFormat(format);
		
		return date.format(new java.sql.Date(time));
	}
	/**
	 * 현재일자를 기준으로 가까운 이전일자의 일요일을 구함.
	 * @return
	 */
	public static String firstDate(){
		long time = System.currentTimeMillis();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String strDate = dateFormat.format(new java.sql.Date(time));
		return firstDate(strDate);
	}
	/**
	 * 주어진날자에서 가까운 일요일을 구함.
	 * @param strDate
	 * @return
	 */
	public static String firstDate(String strDate){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date nDate;	// java.text.ParseException
		try {
			nDate = dateFormat.parse(strDate);	// java.text.ParseException
		}catch(ParseException e){
			return strDate;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(nDate);
		
		int dayNum = cal.get(Calendar.DAY_OF_WEEK)-1;
		
		int adjust = 0;	//일요일이 기준일자
		
		cal.add(Calendar.DATE, adjust-dayNum);
		return dateFormat.format(cal.getTime());
	}
	
	/**
	 * 주어진 포맷의 날자에서 주어진 분만큼을 더하거나 뺌
	 * @param strDate
	 * @param format
	 * @param min
	 * @return
	 */
	public static String addMinute(String strDate, String format, int min){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date nDate=null;	// java.text.ParseException
		try {
			nDate = dateFormat.parse(strDate);	// java.text.ParseException
		}catch(ParseException e){
			return strDate;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(nDate);
		
		cal.add(Calendar.MINUTE, min);
		return dateFormat.format(cal.getTime());
	}
}
