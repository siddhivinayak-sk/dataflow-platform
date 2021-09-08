package myprocessor.processors.csvtoxml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;
import org.apache.nifi.util.StringUtils;

@Tags({"csvtoxml"})
@CapabilityDescription("This Processor Converts FlowFile data to XML from CSV")
@SeeAlso({})
//@ReadsAttributes({@ReadsAttribute(attribute="", description="")})
//@WritesAttributes({@WritesAttribute(attribute="", description="")})
public class MyProcessor extends AbstractProcessor {

    public static final PropertyDescriptor MY_PROPERTY = new PropertyDescriptor
            .Builder()
            .name("MyProperty")
            .displayName("My property")
            .description("Example Property")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final Relationship SUCCESS = new Relationship.Builder()
            .name("SUCCESS")
            .description("Successfully processed and transformed source and sent file to queue")
            .build();
    
    public static final Relationship FALIURE = new Relationship.Builder()
            .name("FALIURE")
            .description("Failed to process the file")
            .build();
    
    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        descriptors = new ArrayList<>();
        descriptors.add(MY_PROPERTY);
        descriptors = Collections.unmodifiableList(descriptors);

        relationships = new HashSet<>();
        relationships.add(SUCCESS);
        relationships.add(FALIURE);
        relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {

    }

    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) {
        FlowFile flowFile = session.get();
        if ( flowFile == null ) {
            return;
        }
    	try {
	        final AtomicReference<StringBuilder> value = new AtomicReference<>();
	        session.read(flowFile, new InputStreamCallback() {
				
				@Override
				public void process(InputStream in) throws IOException {
					StringBuilder output = new StringBuilder();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		        	String record = null;
		        	while((record = reader.readLine()) != null) {
		        		if(StringUtils.isNotEmpty(record) && record.contains(",")) {
		        			String[] fields = record.split(",");
		        			output.append("\n\t<account>");
		        			output.append("\n\t\t<accountNo>" + fields[0] + "</accountNo>");
		        			output.append("\n\t\t<customerId>" + fields[1] + "</customerId>");
		        			output.append("\n\t\t<bankCode>" + fields[2] + "</bankCode>");
		        			output.append("\n\t\t<branchCode>" + fields[3] + "</branchCode>");
		        			output.append("\n\t\t<balance>0.0</balance>");
		        			output.append("\n\t\t<countryCode>IN</countryCode>");
		        			output.append("\n\t</account>");
		        		}
		        	}
		        	value.set(output);
				}
			});
	        flowFile = session.write(flowFile, new OutputStreamCallback() {
				@Override
				public void process(OutputStream out) throws IOException {
			    	if(value.get().length() > 0) {
			    		StringBuilder output = value.get();
			    		output.insert(0, "\n<accounts>");
			    		output.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			    		output.append("\n</accounts>");
		    			out.write(output.toString().getBytes());
		    			out.flush();
			    	}
				}
			});
	        session.putAttribute(flowFile, "filename", flowFile.getAttribute("filename").replace(".csv", ".xml"));
	        session.transfer(flowFile, SUCCESS);
	    }
	    catch(Throwable th) {
	    	session.transfer(flowFile, FALIURE);
	    }
    }
}
