package de.bstreit.java.oscr.business.bill;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import de.bstreit.java.oscr.business.AbstractSpringTestWithContext;
import de.bstreit.java.oscr.text.formatting.BillFormatterTest;

@Ignore
@ContextConfiguration(classes = { BillFormatterTest.class })
@Configuration
public class ResourceTest extends AbstractSpringTestWithContext {

	@Value("classpath:sampleBill_inhouse_onlyOneVATClass_withVariation")
	Resource sampleBill;

	@Test
	public void testBill() throws Exception {
		System.out.println(sampleBill.getFile());
	}
}
