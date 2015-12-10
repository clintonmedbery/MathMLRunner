/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathmlrunner;

/**
 *
 * @author clintonmedbery
 */
public class MathMLRunner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        
        String baseURL = "https://dl.dropboxusercontent.com/u/2174517/mathmlSampleExpression.xml";
	String query = "";
        String prefix = "apply";
	String resultQuery = "//" + prefix + ":apply[count(" + prefix + ":cn)=2]"; 
	String departureStateName = RestfulServiceSupport.queryWithXMLReturn( baseURL, query, resultQuery );
        System.out.println(departureStateName);
    }
    
}
