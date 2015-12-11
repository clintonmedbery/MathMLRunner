package mathmlrunner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


@SuppressWarnings("javadoc")
public class RestfulServiceSupport {

	public static String queryWithXMLReturn( String baseURL, String dbQuery, String resultQuery ) throws Exception
	{
		String rtnval = null;
                String xmlString = "";
		
		String serviceAddress = baseURL + dbQuery;
                System.out.println(serviceAddress);
		
		// change spaces to %20
		serviceAddress = serviceAddress.replace(" ", "%20");

		URL restURL = new URL(serviceAddress);
		HttpURLConnection connection = (HttpURLConnection)restURL.openConnection();
		
		connection.setRequestMethod( "GET" );
		
		connection.connect();
			
		if( connection.getResponseCode() == 200 )
		{
			// grab the input stream associated with the content
			InputStream in = connection.getInputStream();
			
			StringBuffer sb = new StringBuffer();
			
			// establish an inputStreamReader and tell it the data is in UTF-8
		    Reader reader = new InputStreamReader(in, "UTF-8");
		    int c;
		    
		    //read a character at a time, appending into the StringBuffer
		    while ((c = reader.read()) != -1) sb.append((char) c);
		    
			connection.disconnect();
			
			
                        xmlString = xmlString + sb.toString();
                        //System.out.println(sb.toString());
		}
                else {
                    connection.disconnect();

                }
                JSONObject xmlJSONObj = XML.toJSONObject(xmlString);
                xmlJSONObj = xmlJSONObj.getJSONObject("m:math");
                xmlJSONObj = xmlJSONObj.getJSONObject("m:apply");

                Set<String> keys = xmlJSONObj.keySet();
               
                //System.out.println(xmlJSONObj);

                //System.out.println(mathArray.toString());
                //System.out.println(xmlJSONObj);
                getNumber(xmlJSONObj, 0.0);
		return rtnval;
	}
        
    private static void getNumber(JSONObject mathJSON, double number) {
        String majorOperator = "";
        ArrayList<Double> finalNumbers = new ArrayList<Double>();
        ArrayList<Double> currentNums = new ArrayList<Double>();
        //System.out.println(mathJSON);
        if(hasApply(mathJSON)){
                Set<String> keys = mathJSON.keySet();
                for (String key: keys) {
                    //System.out.println(key);

                    if(!key.equals("m:apply") && !key.equals("mml:apply")){
                        majorOperator = key;
                        System.out.println("MAJOR OPERATOR: " + majorOperator);

                    }

                }
                
                JSONArray array = mathJSON.getJSONArray("m:apply");
                System.out.println("ARRAY LENGTH: " + array.length());

                for(int i = 0; i < array.length(); i++){
                    if(hasCN(array.getJSONObject(i))){
                        currentNums = getCNArray(array.getJSONObject(i));
                       
                        //Need to get the operator
                        String operator = getNonCNOperator(array.getJSONObject(i));
                        
                        finalNumbers.add(doOperation(operator, currentNums));
                        currentNums.clear();
                    }
                }
                
                
        } else {
            
        }
        
        System.out.println(doOperation(majorOperator, finalNumbers));
    
    }
    
//    private static double getNumberFromArray(JSONArray array){
//        
//    }
    
    
    private static void getOperator(){
        
    }

//    private static void assessJSON(JSONObject mathJSON) {
////        System.out.println("ASSESSING OBJECT");
//            //System.out.println(mathJSON);
//            double number = 0.0;
////            System.out.println(hasApply(mathJSON));
//            
//            Set<String> keys = mathJSON.keySet();
//                for (String key: keys) {
//                    System.out.println(key);
//                    if(key.equals("m:apply") || key.equals("mml:apply")){
//                        JSONArray array = mathJSON.getJSONArray("m:apply");
//                        System.out.println("ARRAY");
//                        System.out.println(array);
//                        for(int i = 0; i < array.length() - 1; i++){
//                            System.out.println("Outer:" + array.getJSONObject(i));
//                            if(hasApply(array.getJSONObject(i)) == false){
//                                
//
//                                Set<String> innerKeys = array.getJSONObject(i).keySet();
//                                if(hasCN(array.getJSONObject(i))){
//                                    for (String innerKey: innerKeys){
//                                        System.out.println(innerKey);
//                                        
//                                
//                                    }
//                                }
//                                
//                                
//                                
//                            }
//                            
//
//                        }
//                        
//                    }
//                }
//    }
    
    private static boolean hasApply(JSONObject leftoverJSON){
        Set<String> keys = leftoverJSON.keySet();
        boolean hasApply = false;
        for (String key: keys ){
            if(key.equals("m:apply") || key.equals("mml:apply")){
                
                hasApply = true;
            }
        }
        return hasApply;
    }
    
    private static int applyDepth(JSONObject leftoverJSON){
        int count = 0;
        while(hasApply(leftoverJSON)){
            count = count + 1;
            JSONArray array = leftoverJSON.getJSONArray("m:apply");
        }
        return count;
    }
    private static boolean hasCN(JSONObject leftoverJSON){
        Set<String> keys = leftoverJSON.keySet();
        boolean hasCN = false;
        for (String key: keys ){
            if(key.equals("m:cn") || key.equals("mml:cn")){
                hasCN = true;
            }
        }
        return hasCN;
    }
    
    private static ArrayList<Double> getCNArray(JSONObject jsonObject){
        ArrayList<Double> nums = new ArrayList<Double>();

        JSONArray array = jsonObject.getJSONArray("m:cn");
        for(int i = 0; i < array.length(); i++){
            nums.add(array.getDouble(i));
        }
        System.out.println("GET CN ARRAY: " + nums.toString());

        return nums;
    }
    
    private static String getNonCNOperator(JSONObject jsonObject){
        String operator = "";
        Set<String> keys = jsonObject.keySet();
        
        for(String key:keys) {
            if(!key.equals("m:apply") && !key.equals("mml:apply") && !key.equals("m:cn") && !key.equals("mml:cn") ) {
                operator = key;
            }
        }
        return operator;
    }
    
    private static Double doOperation(String operation, ArrayList<Double> arrayList){
        Double number = arrayList.get(0);
        System.out.println("");

        System.out.println("OPERATION TIME");

        System.out.println("ARRAYLIST: " + arrayList);

        if(operation.equals("m:plus")){
            System.out.println("OPERATION: PLUS");

            for(int i = 1; i < arrayList.size(); i++){
                System.out.println(number + " = " + number + " + " + arrayList.get(i));
                number = number + arrayList.get(i);
            }
        } else if(operation.equals("m:minus")){
            System.out.println("OPERATION: MINUS");

            for(int i = 1; i < arrayList.size(); i++){
                System.out.println(number + " = " + number + " - " + arrayList.get(i));

                number = number - arrayList.get(i);
            }
        } else if(operation.equals("m:divide")){
            System.out.println("OPERATION: DIVIDE");

            for(int i = 1; i < arrayList.size(); i++){
                System.out.println(number + " = " + number + " / " + arrayList.get(i));

                number = number / arrayList.get(i);
            }
        } else if(operation.equals("m:times")){
            System.out.println("OPERATION: TIMES");

            for(int i = 1; i < arrayList.size(); i++){
                System.out.println(number + " = " + number + " * " + arrayList.get(i));

                number = number * arrayList.get(i);
            }
        } 
        System.out.println("ANSWER: "+ number);
        System.out.println("");

        return number;
    }
}	
