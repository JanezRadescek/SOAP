import javax.xml.soap.*;
import org.w3c.dom.NodeList;
import java.util.Scanner;


public class SOAPClientSAAJ {

    // SAAJ - SOAP Client Testing
    public static void main(String args[]) {
        
    	
        String soapEndpointUrl = "http://ec.europa.eu/taxation_customs/vies/services/checkVatService";
        String soapAction = "urn:ec.europa.eu:taxud:vies:services:checkVat/checkVatPortType/checkVatRequest";

        callSoapWebService(soapEndpointUrl, soapAction);
    }

   
    private static void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter a county: ");
        String country = reader.next();
        System.out.println("Enter a VAT: ");
        String VAT = reader.next();
        reader.close();

        String myNamespace = "urn";
        String myNamespaceURI = "urn:ec.europa.eu:taxud:vies:services:checkVat:types";

        
        
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

            

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        
        SOAPElement soapBodyElem1 = soapBody.addChildElement("checkVat", myNamespace);
        SOAPElement soapBodyElem21 = soapBodyElem1.addChildElement("countryCode", myNamespace);
        soapBodyElem21.addTextNode(country);
        SOAPElement soapBodyElem22 = soapBodyElem1.addChildElement("vatNumber", myNamespace);
        soapBodyElem22.addTextNode(VAT);
        
        
    }

    
    private static void callSoapWebService(String soapEndpointUrl, String soapAction) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println("\n");
            parseXML(soapResponse);
           
            
            soapConnection.close();
        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
    }




	private static void parseXML(SOAPMessage soapResponse) throws Exception {
		// Parse XML
		SOAPBody body = soapResponse.getSOAPBody();
		NodeList name = body.getElementsByTagName("name");
		NodeList address = body.getElementsByTagName("address");
		NodeList innername = name.item(0).getChildNodes();
		NodeList inneraddress = address.item(0).getChildNodes();
		String nameS = innername.item(0).getTextContent();
		String addressS = inneraddress.item(0).getTextContent();
		
		// Print results
		System.out.println("Parsed XML:");
		System.out.print("Naziv:");
		System.out.println(nameS);
		System.out.print("Naslov:");
		System.out.println(addressS);
		
	}
	


	private static SOAPMessage createSOAPRequest(String soapAction) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();


        createSoapEnvelope(soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");
        

        return soapMessage;
    }

}