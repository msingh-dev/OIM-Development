package ca.jndi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class GetUsersFromLDAP {

	public static void main(String args[]) throws IOException
	{
		Properties prop = readPropertiesFile("C:\\Users\\msingh\\Desktop\\simeio@training\\credentials.properties");		
		try {
				 String username= prop.getProperty("username");
				 String password = prop.getProperty("password");
				 String ldapURL = prop.getProperty("url");
				 String contextFactory = prop.getProperty("ctx");
				 String securityAuthentication = prop.getProperty("sAuth");	 
				 fetchUserListExternal(username,password,ldapURL,contextFactory,securityAuthentication);
			
		}catch(Exception e) {
			e.printStackTrace();
		}    
	}

	
	     @SuppressWarnings("null")
	     /**
	      * 
	      * @param username
	      * @param password
	      * @param ldapURL
	      * @param contextFactory
	      * @param securityAuthentication
	      * @throws IOException
	      */
		public static void fetchUserListExternal(String username,String password,String ldapURL,String contextFactory,String securityAuthentication) throws IOException {
	       Hashtable<String, String> env = new Hashtable<String, String>();
	       env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
	       // set security credentials
	       env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
	       env.put(Context.SECURITY_PRINCIPAL, username);
	       env.put(Context.SECURITY_CREDENTIALS, password);
	       // connect to my domain controller
	       env.put(Context.PROVIDER_URL, ldapURL);
	        NamingEnumeration<?> results = null;
	        LdapContext ctx = null;
	       try {
	         List<String> usersList = new ArrayList<String>();
	           List<String> usersList1 = new ArrayList<String>();
	         ctx = new InitialLdapContext(env, null);
	         SearchControls searchCtls = new SearchControls();
	           //Addign New SearchControl to print Specific result :   
	         Properties prop = readPropertiesFile("C:\\Users\\msingh\\Desktop\\credentials.properties");	
	        
	         String sourceFile = "C:\\Users\\msingh\\Desktop\\exe.csv" ;
	         List<String> attrList = getUsersFromFile(sourceFile);
	         List<String> userList = getUsersFromFile(sourceFile);
	         System.out.println("UserList" + userList);
	         PrintWriter writer = new PrintWriter(prop.getProperty("outputFileName")) ;
	 		StringBuilder sb = new StringBuilder();
	 		StringBuilder sb1 = new StringBuilder();
	         //String[] aList = new String[30];
				String sizeParameters = prop.getProperty("size");
				int size = Integer.parseInt(sizeParameters);
				String[] aList = new String[size];
	      for(int i = 0 ; i < userList.size() ; i++) {
	    	  aList[i] = userList.get(i);
	    	  sb1.append(aList[i]);
				if(i+1 != attrList.size()) {
				sb1.append(',');
				}
	      }
	  	sb1.append('\n');
		writer.write(sb1.toString());
	               
	         searchCtls.setReturningAttributes(aList);      
	         searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
	         // specify the LDAP search filter
	         String searchFilter = prop.getProperty("searchFilter");
	      // Specify the Base for the search
	         String searchBase =prop.getProperty("searchBase");
	         Attributes attrs = null;
	    results = ctx.search(searchBase, searchFilter, searchCtls);
	           
		while (results.hasMoreElements()) {

			SearchResult searchResult = (SearchResult) results.next();
			
			if (searchResult != null || !(searchResult.toString().isEmpty())) {
				attrs = searchResult.getAttributes();
			} else {
				break;
			}

			NamingEnumeration<? extends Attribute> attrs1 = attrs.getAll();

			String[] attributes = new String[size];
			for(int i = 0; i < attrList.size(); i++) {
				attributes[i] = attrList.get(i);
				System.out.println(attributes[i]);
				String param = attrs.get(attributes[i]).get().toString();
				sb.append(param);
				if(i+1 != attrList.size()) {
					sb.append(',');
				}
				sb.append('\n');
			}
//			while (attrs1.hasMore()) {
//				String param = attrs1.next().get().toString();
//				//System.out.println(param);
//				sb.append(param.replace(",", "::"));
//				sb.append(',');
//			}
		
		}
	           writer.write(sb.toString());

	       }catch (NameNotFoundException e) {
	      e.printStackTrace();
	    } catch (NamingException e) {
	      e.printStackTrace();
	    } finally {
	      if (results != null) {
	        try {
	          results.close();
	        } catch (Exception e) {
	        }
	      }

	      if (ctx != null) {
	        try {
	          ctx.close();
	        } catch (Exception e) {
	        }
	      }
	    }
	     }
	     
	     
	     /**
	      * 
	      * @param sourceFile
	      * @return
	      */
	     public static List<String> getUsersFromFile(String sourceFile){
	          System.out.println("Reading the data from CSV file :" + sourceFile);
	          String userLogin = "";
	          List<String> users = null;
	          try{
	            BufferedReader br = new BufferedReader(new FileReader(sourceFile));
	            users = new ArrayList<String>();
	             
	            br.readLine();
	            while(null != (userLogin = br.readLine())){
	              users.add(userLogin.trim());
	            }
	            br.close();
	          }catch (FileNotFoundException e){
	            System.out.println("CSV file not found " + e.getMessage());
	          }catch (IOException e){
	            System.out.println("IO exception occured" + e.getMessage());
	          }catch (Exception e){
	            System.out.println("Error occured while reading file" + e.getMessage());
	          }
	          return users;
	 }
	     
	     /**
	      * 
	      * @param fileName
	      * @return
	      * @throws IOException
	      */
	     public static Properties readPropertiesFile(String fileName) throws IOException {
	         FileInputStream fis = null;
	         Properties prop = null;
	         try {
	            fis = new FileInputStream(fileName);
	            prop = new Properties();
	            prop.load(fis);
	         } catch(FileNotFoundException fnfe) {
	            fnfe.printStackTrace();
	         } catch(IOException ioe) {
	            ioe.printStackTrace();
	         } finally {
	            fis.close();
	         }
	         return prop;
	      }


}

