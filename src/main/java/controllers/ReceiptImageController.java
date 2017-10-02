package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.NotEmpty;

import static java.lang.System.out;

import java.util.HashSet;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);

    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     *
     * public class ReceiptSuggestionResponse {
     *     String merchantName;
     *     String amount;
     * }
     */
    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        HashSet<Character> mySet = new HashSet<Character>();
        Character allNums[] ={'0', '1','2','3','4','5','6','7','8','9','.',',', '$'};
        for (int i =0; i<13; i++){
            mySet.add(allNums[i]);
        }

        System.out.println("made the hash set");
        System.out.println(mySet);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);
           // AnnotateImageResponse res2 = responses.getResponses(1);


            String merchantName = null;
            // Your Algo Here!!
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount
            int first = 0;
            String firstWord = "didn't work";
            BigDecimal amount = new BigDecimal(2.3);
            String lastNumber = "again sucks";
            //System.out.println("trying");
           // System.out.println(res.getTextAnnotationsList().get(0));
            //System.out.println("test is above");
            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                String firstText =annotation.getDescription();
                //System.out.println(firstText);
                String[] textSplit = firstText.split("\\r?\\n"); //split by newline
                if (first ==0){
                    first++;
                    String firstLine = textSplit[0];
                    firstWord = firstLine.split(" ")[0];
                    

                    //firstWord = textSplit[0]; 
                    
                    // for (int i =0; i<textSplit.length; i++){
                    //     //System.out.println(textSplit[i]);
                    // }
                    //System.out.println(firstWord);
                }

                //out.printf("Position : %s\n", annotation.getBoundingPoly());
                //out.printf("Text: %s\n", annotation.getDescription());


                for (int i =0; i<textSplit.length; i++){
                    String thisWord = textSplit[i];
                    // if (thisWord.substring(0,1).equals('$')){
                    //     System.out.println(thisWord);
                    //     thisWord = thisWord.substring(1);
                    //     System.out.println(thisWord);
                    // }
                    int flag = 0;
                    System.out.println(thisWord);
                    
                    for (int j =0; j<thisWord.length(); j++){
                        System.out.println(thisWord.charAt(j));
                        if (!(mySet.contains(thisWord.charAt(j)) || thisWord.charAt(j)=='.' || thisWord.charAt(j)==',')){
                            System.out.println("wtf");
                            flag = 1;
                        }
                        if (thisWord.charAt(j)=='.'){
                            System.out.println("BAAAAAAAAAAAAAAAAAAANNNNNNNNGGGG");
                            System.out.println(flag);
                        }

                    }
                    if (flag == 0 && thisWord.length() < 10){
                        System.out.println("made the cut:");
                        System.out.println(thisWord);
                        lastNumber = thisWord;
                    }

                    // if (thisWord.chars().allMatch( Character::isDigit)){
                    //     System.out.println(thisWord);
                    //     if (thisWord.length() < 10){
                    //         int num = Integer.parseInt(thisWord);
                    //         amount = new BigDecimal(num);
                    //     }
                    // }
                }
                // if (annotation.getDescription().chars().allMatch( Character::isDigit )){//true if string is numeric
                //     System.out.println("got in the if statement working");
                //     int num = Integer.parseInt(annotation.getDescription());
                //     amount = new BigDecimal(num); 
                // }
            }

            //replace comma with period
            for (int i =0; i<lastNumber.length(); i++){
                if (lastNumber.charAt(i)==','){
                    lastNumber=lastNumber.substring(0,i)+"."+lastNumber.substring(i+1);
                }
            }

            //get the $ out
            if (lastNumber.charAt(0) == '$'){
                lastNumber = lastNumber.substring(1);
            }

            merchantName = firstWord;
            System.out.println(merchantName);
            System.out.println(amount);
            System.out.println(lastNumber);
            Double amountDouble = Double.parseDouble(lastNumber);
            String amountString = amountDouble.toString();
            if (amountString.length()>2){
                if (amountString.charAt(amountString.length()-2)=='.'){
                    amountString = amountString + "0";
                    System.out.println("fixing the issue");
                }
                // if (amountString.charAt(amountString.length()-2)=='.'){
                //     System.out.println("fixing the issue");
                //     amountString = amountString + "0";
                //     amount = new BigDecimal(amountString);
                // }
                // else{
                //     System.out.println("there is no issue");
                //     amount = new BigDecimal(amountDouble);
                // }
            }
            System.out.println(amountString);
            amount = new BigDecimal(amountString);
            System.out.println(amount);
            

            //TextAnnotation fullTextAnnotation = res.getFullTextAnnotation();
            return new ReceiptSuggestionResponse(merchantName, amount);
        }
        catch (Exception e){

            String merchantName = "EXCEPT BLOCK";
            BigDecimal amount = new BigDecimal(2.3);
            return new ReceiptSuggestionResponse(merchantName, amount);

        }
    }
}