package api;


import io.dropwizard.jersey.validation.Validators;
import org.junit.Test;

import javax.validation.Validator;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class TestMoreCases {

    private final Validator validator = Validators.newValidator();

    @Test
    public void testValidHasSizeZero() {
         CreateReceiptRequest receipt = new CreateReceiptRequest();
         receipt.merchant = "OK";

         receipt.amount = new BigDecimal(33.44);
         assertThat(validator.validate(receipt), hasSize(0));
    }
}