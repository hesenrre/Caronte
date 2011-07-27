package com.jwm.caronte.cron;

public class CronParser {
	
	public static String days[] = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	public static String posfix[] = {"first","second","third","forth","fith"};
	public static String months[] = {"January","February","March","April","May","June","July","August","September","October","November","December"};
	public String parseExpression(String cronExpr){
		String parsed="";
		
		String [] digits  = cronExpr.split("\\s") ;
		for(int j=digits.length-1;j >= 3;j--){
			parsed = parseSub(digits[j],j) + parsed;
		}
		System.out.println("date = " + parsed);
		String time = "";
		for(int j=2;j >=0 ;j--){
			String timePart =parseSub(digits[j],j) ; //
			if(timePart.matches("^\\d+$")){
				time = (time.equals("") ? (timePart):( time.matches("[\\d]{1,2}(:[\\d]{1,2})?"))? (time + ":" + timePart) :  ( timePart  + getDigitString(j)) );
			}else{
				time = timePart.equals("")?time : (time+ " " + timePart);
			}
		}
		
		System.out.println("time = " + time);
		return "Fire at " + time + " " + parsed;
	}
	
	public String parseSub(String subExpr, int index){
		String parsed="";
		
		if(subExpr.equals("*") || subExpr.equals("?"))
			return "";		
		if(subExpr.matches("^\\d+$") && !subExpr.equals("0") ){
			if(index== 4){
				return months[new Integer(subExpr).intValue()-1];
			}
			return subExpr + (index >= 3?getDigitString(index) :""   ); 
		}
		if(subExpr.matches(".*\\-.*")){
			String[] num= subExpr.split("-");
			parsed= " from " + getDigitString(index) +  num[0] + " to " + num[1]; 
		}
		if(subExpr.matches(".*\\,.*")){			
			
			parsed= getDigitString(index) + subExpr; 
		}
		
		if(subExpr.matches(".*/.*")){
			String pos = index <=2 ? "at" : "on"; 
			String[] num= subExpr.split("/");
			parsed= " every " + num[1] + "  " + getDigitString(index) + (num[0].equals("0")?"" : " starting " + pos +" " + getDigitString(index) + num[0]) ; 
		}
		if(subExpr.matches(".*L$") ){
			if(index == 3){
				parsed = " the last day of month";
			}
			else{
				if(subExpr.length() == 1){
					parsed = "Saturday";
				}else{
					parsed = "every last " + days[new Integer(String.valueOf(subExpr.charAt(0))).intValue()-1] + " of the month";
				}
			}
			
		}
		if(subExpr.matches(".*W$") ){	
			if(String.valueOf(subExpr.charAt(0)).equals("W")){
				parsed = "the last week day of the month";
			}else{
				parsed = " the nearest weekday to the" + (subExpr.substring(0,subExpr.length()-2));
			}
		
		}
		if(subExpr.matches(".*#.*") ){
			String[] num = subExpr.split("#");
			parsed = "the " + posfix[new Integer(num[1]).intValue() -1] +" " + days[new Integer(String.valueOf(subExpr.charAt(0))).intValue()-1] +" of the month";
		}
		
		return  parsed;
	}

	
	public String getDigitString(int index){
		String digit = "";
		switch(index){
			case 0:{
				digit= "second(s)";
				break;
			}
			case 1: {
				digit = "minute(s)";
				break;
			}
			case 2:{
				digit = "hour(s) ";
				break;
			}	
			case 3:{
				digit = "day(s) of month ";
				break;
			}			
			case 4:{
				digit = "month";
				break;
			}
			case 5:{
				digit = " day of week ";
				break;
			}
			case 6:{
				digit = "year ";
				break;
			}
		}
		return digit;
	}

}
