package com.accountclaim.SailpointIDNOW.controller;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;


@Controller
public class LoginIDNOW {
	
	public String accessToken;
	public String accessTen;
	public String pass;
	public String pkey;
	public String encryptedString;
	public JSONArray json1;
	public JSONObject json;
	public JSONObject json2;
	public JSONObject json3;
	public JSONObject json4;
	public Object name;
	public Object email;
	public Object cas;
	public Object cp;
	public Object id;
	public Object uid;
	public int usid;
	public Object extid;
	public Object identityId;
	public Object sourceId;
	public Object publicKey;
	public Object publicKeyId;
	public Object state;
	public Object requestId;
	public Object acId;
	public Object acc;
	public Object alias;
	public JSONObject att;
	public Timestamp otpstamp;
	public String pas;
	private static final String DB_URL = "jdbc:mysql://3.224.232.216:3306/idnow?useUnicode=true&characterEncoding=UTF-8";
	private static final String DB_USERNAME = "varun";
	private static final String DB_PASSWORD = "Varun@12345";
	ResultSet userpresent;

	
	
	@GetMapping("/IDNOWaccess")
	
	public String token() throws IOException
	{
		URL url = new URL("https://partner055.api.identitynow.com/oauth/token");
        Map<String,Object> param = new LinkedHashMap<>();
        param.put("grant_type", "client_credentials");
        param.put("client_id", "8b57dc1f-95fa-47e0-858f-2c97a31092b1");
        param.put("client_secret", "fcc1710f4d7006347b7a2c2eecfa9dfe2c2920f2f45a63dc955b20a3e6d83753");
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> para : param.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(para.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(para.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8"); 
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty( "charset", "utf-8");
        connection.setUseCaches( true );
        connection.getOutputStream().write(postDataBytes);
        
        int responseCode = connection.getResponseCode();
        System.out.println("Response code for Access Token Generation : " + responseCode);
        
        
        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        String responseBody = scanner.useDelimiter("\\A").next();
        System.out.println("Response body: " + responseBody);
        //to read only the access token from the body
        JSONObject jsonObject = new JSONObject(responseBody);
        accessToken = jsonObject.getString("access_token");
        System.out.println("Access Token: " + accessToken);
                           
        // Close the connection
        scanner.close();
        connection.disconnect();
        return "IDNOWaccess";
	}
	
	@PostMapping("/IDNOWgenerate")
	public String generateotp(ModelMap model, @RequestParam int userid, @RequestParam int cp1) throws IOException, SQLException, ClassNotFoundException
	{
		usid=userid;
		URL url1 = new URL("https://partner055.api.identitynow.com/v2/identities/"+userid+"");
		HttpsURLConnection connection1 = (HttpsURLConnection) url1.openConnection();
        connection1.setRequestMethod("GET");
        connection1.setDoOutput(true);
        connection1.setRequestProperty("Authorization","Bearer " + accessToken);
        connection1.setRequestProperty("Content-Type", "application/json");
        int responseCode1 = connection1.getResponseCode();
        System.out.println("Response code for displaying identities  : " + responseCode1);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        System.out.println("Result for identity details: "+sb.toString());
        connection1.disconnect();
        
        
        json = new JSONObject(sb.toString());
        //json1 = json.getJSONArray("attributes");
        id = json.get("id");
        uid = json.get("uid");
        email = json.get("email");
        extid = json.get("externalId");
        alias = json.get("alias");
        att = (JSONObject) json.get("attributes");
        
        System.out.println("UserId: "+id);
        System.out.println("UserName: "+uid);
        System.out.println("Email: "+email);
        System.out.println("externalId: " +extid);
        System.out.println("alias: "+alias);
        System.out.println("Attributes: "+ att);
        
        
        for (int i=0;i<att.length();i++)
        {
        	String result=att.toString(i);
        	JSONObject jsonObject1 = new JSONObject(result);
            cas = jsonObject1.get("cloudAuthoritativeSource");
            cp= jsonObject1.get("cp1");
            
        }
        System.out.println("Cloud Authoritative Source: "+cas);
        System.out.println("Personal Identification Number: "+cp);
        
        int j = Integer.valueOf((String) id); 
        int PII = Integer.valueOf((String) cp);
        
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection3 = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
//        PreparedStatement statement = connection3.prepareStatement("INSERT INTO userlist (UserId, OTPCount) VALUES (?, ?)");
//        {
//        	statement.setInt(1,userid);
//            statement.setInt(2, 0); // Initial count can be set to 0 or any other value
//
//            int rowsAffected = statement.executeUpdate();
//            if (rowsAffected > 0) {
//                System.out.println("User added to the blacklist successfully.");
//            } else {
//                System.out.println("Failed to add the user to the blacklist.");
//            }
//        }
        if (userid==j && cp1==PII) {
        PreparedStatement statement1 = connection3.prepareStatement("SELECT * FROM userdata WHERE UserId = ?");
        {
        	statement1.setInt(1,userid);
        	userpresent= statement1.executeQuery();
        	if (userpresent.next()) {
        		
        		System.out.println("Failed to add the user to the blacklist. As user is already present");
        		model.put("errorMsg", "Account Claimed");
        		return "IDNOWaccess";

        }
        	else 
        	{
        		PreparedStatement statement = connection3.prepareStatement("INSERT INTO userdata (UserId, OTPCount) VALUES (?, ?)");
                {
                	statement.setInt(1,userid);
                    statement.setInt(2, 0); // Initial count can be set to 0 or any other value

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("User added to the blacklist successfully.");
                    }
        	}
        	}
        	
    }
        connection3.close();      
        if(email!=null) {
        final String CHARACTER = "0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 5; i++)
        {
            int index = random.nextInt(CHARACTER.length());
            password.append(CHARACTER.charAt(index));
            pas=password.toString();
        }
        otpstamp = new Timestamp(System.currentTimeMillis());
        }
        
	
 	URL url = new URL("https://securdi-partner.saviyntcloud.com/ECM/api/login");
    HttpsURLConnection connection0 = (HttpsURLConnection) url.openConnection();
    connection0.setRequestMethod("POST");
    connection0.setDoOutput(true);
    connection0.setRequestProperty("Content-Type", "application/json");
    String body = "{\"username\":\"vambrale\",\"password\":\"V@run95?\"}";  
    DataOutputStream outputStream = new DataOutputStream(connection0.getOutputStream());
    outputStream.writeBytes(body);
    outputStream.flush();
    outputStream.close();
    //get status code like 200/404/401  etc  
    int responseCode = connection0.getResponseCode();
    System.out.println("Response code for Access Token Generation : " + responseCode);
    // to read the access token complete body
    InputStream inputStream = connection0.getInputStream();
    Scanner scanner = new Scanner(inputStream);
    String responseBody = scanner.useDelimiter("\\A").next();
    System.out.println("Response body: " + responseBody);
    //to read only the access token from the body
    JSONObject jsonObject = new JSONObject(responseBody);
    accessTen = jsonObject.getString("access_token");
    System.out.println("Access Token: " + accessTen);
                       
    // Close the connection
    scanner.close();
    connection0.disconnect();
	
        
    URL url2 = new URL("https://securdi-partner.saviyntcloud.com/ECM/api/sendEmail");
    Map<String,Object> param = new LinkedHashMap<>();
    param.put("to", email);
    param.put("from", "vambrale@securdi.com");
    param.put("body","Hi "+ uid + ","+"\n\n"+"Your OTP is: "+pas+", It will be valid for 3 minutes. Go back to the portal and enter the OTP before the time runs out" );
    param.put("subject", "OTP generation email");
    StringBuilder postData = new StringBuilder();
    for (Map.Entry<String,Object> para : param.entrySet()) {
        if (postData.length() != 0) postData.append('&');
        postData.append(URLEncoder.encode(para.getKey(), "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(String.valueOf(para.getValue()), "UTF-8"));
    }
    byte[] postDataBytes = postData.toString().getBytes("UTF-8");
    HttpsURLConnection connection2 = (HttpsURLConnection) url2.openConnection();
    connection2.setRequestMethod("POST");
    connection2.setDoOutput(true);
    connection2.setRequestProperty("Authorization","Bearer " + accessTen);
    connection2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    connection2.setRequestProperty( "charset", "utf-8");
    connection2.setUseCaches( true );
    connection2.getOutputStream().write(postDataBytes);
    
    int responseCode2 = connection2.getResponseCode();
    System.out.println("Response code for email sending : " + responseCode2); 
    
    
    BufferedReader in = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
    String inputLine;
    StringBuilder response = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
    	              	
       response.append(inputLine);
    }
    System.out.println("Email Sending Response " +response );
    in.close();
    connection2.disconnect();
	}     
        else
        {
        	model.put("errorMsg", "Invalid Credentials");
        	return "IDNOWaccess";
        }
   return "IDNOWgenerate";

	}

  @PostMapping("/IDNOWvalidate")
	
	public String validateotp(ModelMap model, @RequestParam String otp) throws Exception
	{
		 
			
		//validate otp and generate password 	
		if (otp!=pas) {
			URL url = new URL("https://partner055.api.identitynow.com/v3/query-password-info");
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
     		connection.setRequestMethod("POST");
     		connection.setDoOutput(true);
     		connection.setRequestProperty("Authorization","Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");
            String body5="{\n\"userName\": \""+uid+"\",\n    \"sourceName\" : \"IdentityNow\"\n}";
            DataOutputStream outputStream5 = new DataOutputStream(connection.getOutputStream());
            outputStream5.writeBytes(body5);
            outputStream5.flush();
            outputStream5.close();
	        
	        int responseCode = connection.getResponseCode();
	        System.out.println("Response code for Access Token Generation : " + responseCode);
	        
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = bufferedReader.readLine()) != null) {
	            sb.append(line);
	        }
	        System.out.println("Result for identity details: "+sb.toString());
	        connection.disconnect();
	        
	        json2 = new JSONObject(sb.toString());
	        identityId = json2.get("identityId");
	        sourceId = json2.get("sourceId");
	        publicKey = json2.get("publicKey");
	        publicKeyId = json2.get("publicKeyId");
	        
	        System.out.println("IdentityID: "+identityId);
	        System.out.println("sourceID: "+sourceId);
	        System.out.println("PublicKey: "+publicKey);
	        System.out.println("AccountID or Alias: "+alias);
	        System.out.println("PublicKeyID: "+publicKeyId);
	        
		    }
     		final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            StringBuilder password = new StringBuilder();
            for (int j = 0; j < 12; j++)
            {
                int index = random.nextInt(CHARACTERS.length());
                password.append(CHARACTERS.charAt(index));
                pass=password.toString();  
            }
            System.out.println("PASSWORD: "+pass);
            pkey=publicKey.toString();
            
            byte[] publicKeyBytes = Base64.getDecoder().decode(pkey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // Encrypt the string using the public key
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(pass.getBytes(StandardCharsets.UTF_8));

            // Encode the encrypted bytes as a Base64 string
            encryptedString = Base64.getEncoder().encodeToString(encryptedBytes);           
            System.out.println("Encrypted string: " + encryptedString);
            
            URL url3 = new URL("https://partner055.api.identitynow.com/v3/set-password");
			HttpsURLConnection connection4 = (HttpsURLConnection) url3.openConnection();
     		connection4.setRequestMethod("POST");
     		connection4.setDoOutput(true);
     		connection4.setRequestProperty("Authorization","Bearer " + accessToken);
            connection4.setRequestProperty("Content-Type", "application/json");
            String body6="{\n \"identityId\": \""+identityId+"\",\n \"encryptedPassword\": \""+encryptedString+"\",\n \"publicKeyId\": \""+publicKeyId+"\",\n \"accountId\": \""+alias+"\",\n \"sourceId\": \""+sourceId+"\"\n}";
            DataOutputStream outputStream6 = new DataOutputStream(connection4.getOutputStream());
            outputStream6.writeBytes(body6);
            outputStream6.flush();
            outputStream6.close();
	        
	        int responseCode1 = connection4.getResponseCode();
	        System.out.println("Response code for Access Token Generation : " + responseCode1);
	        
	        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection4.getInputStream()));
	        StringBuilder sb2 = new StringBuilder();
	        String line;
	        while ((line = bufferedReader.readLine()) != null) {
	            sb2.append(line);
	        }
	        System.out.println("Result for identity details: "+sb2.toString());
	        connection4.disconnect();
            
	        json3 = new JSONObject(sb2.toString());
	        state = json3.get("state");
	        requestId = json3.get("requestId");
	        
	        Thread.sleep(4000);
	        
	        URL url4 = new URL("https://partner055.api.identitynow.com/v3/password-change-status/"+requestId+"");
			HttpsURLConnection connection5 = (HttpsURLConnection) url4.openConnection();
	        connection5.setRequestMethod("GET");
	        connection5.setDoOutput(true);
	        connection5.setRequestProperty("Authorization","Bearer " + accessToken);
	        connection5.setRequestProperty("Content-Type", "application/json");
	        int responseCode4 = connection5.getResponseCode();
	        System.out.println("Response code for displaying identities  : " + responseCode4);
	        BufferedReader bufferedReader4 = new BufferedReader(new InputStreamReader(connection5.getInputStream()));
	        StringBuilder sb3 = new StringBuilder();
	        String line1;
	        while ((line1 = bufferedReader4.readLine()) != null) {
	            sb3.append(line1);
	        }
	        System.out.println("Result for identity details: "+sb3.toString());
	        connection5.disconnect();
	        
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        Connection connection6 = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

	        if(state.equals("FINISHED"))
	        {
                      PreparedStatement statement1 = connection6.prepareStatement("UPDATE userdata SET PSGCount = ? WHERE UserId="+usid+"");
	                    {
	                    	statement1.setInt(1,1); // Initial count can be set to 0 or any other value

	                        int rowsAffected1 = statement1.executeUpdate();
	                        if (rowsAffected1 > 0) {
	                            System.out.println("Password generated successfully.");
	                        }
	            	}
	       	

	        }
	        connection6.close();
	        
	        model.put("password", ""+pass+"");
	        model.put("uname", ""+alias+"");
//	        json4 = new JSONObject(sb3.toString());
//	        state = json4.get("state");
//	        requestId = json4.get("requestId");
	        
	        
	        
			
	
	        return "IDNOWvalidate";
	}
  
  
  
}
  
 
    