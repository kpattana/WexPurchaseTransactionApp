**Wex Purchase transaction service.**  
*Given*  
A purchase transaction payload  
*When*  
Service post api called  
*Then*  
Save purchase transaction and return saved database row.  

*Given*  
Already saved transaction id and valid country currency   
*When*  
Service get api called  
*Then*  
return saved transaction details along with applied exchange rate.  

*Compiled with java 17 as specified in pom.xml*
  
**Local Run:**  
* Port configured - 8091
* Context configured - purchase-transaction-service
* swagger link - http://localhost:8091/purchase-transaction-service/swagger-ui/index.html
* Code coverage(available after tests run) - /WexPurchaseTransactionApp/target/site/jacoco/index.html

**Note**
* Country-Currency required by external api(fiscalData) is case-sensitive (e.g: 'United Kingdom-Pound')
* In memory db configured
* Basic authentication security configured 
* DB/Web username & password in application.yml
* No profiles configured        
