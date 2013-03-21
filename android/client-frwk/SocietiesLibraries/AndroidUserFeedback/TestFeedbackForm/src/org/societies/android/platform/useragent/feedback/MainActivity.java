package org.societies.android.platform.useragent.feedback;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.simple.basic.URIConverter;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	private static final String LOG_TAG = MainActivity.class.getName();
	private static final String EXTRA_PRIVACY_POLICY = "org.societies.userfeedback.eventInfo";
	
	private static String beanXML = "<userFeedbackPrivacyNegotiationEvent xmlns=\"http://societies.org/api/internal/schema/useragent/feedback\">            <responsePolicy>               <responseItems class=\"java.util.ArrayList\">                  <responseItem>                     <decision>PERMIT</decision>                     <requestItem>                        <actions class=\"java.util.ArrayList\">" + 
                       "    <action>                              <actionConstant>READ</actionConstant>                              <optional>false</optional>                           </action>                        </actions>                        <conditions class=\"java.util.ArrayList\">                           <condition>                              <conditionConstant>SHARE_WITH_CIS_MEMBERS_ONLY</conditionConstant>" + 
                       "       <optional>true</optional>                              <value>YES</value>                           </condition>                           <condition>                              <conditionConstant>SHARE_WITH_3RD_PARTIES</conditionConstant>                              <optional>true</optional>                              <value>YES</value>                           </condition>                           <condition>" +
                       "       <conditionConstant>RIGHT_TO_OPTOUT</conditionConstant>                              <optional>false</optional>                              <value>YES</value>                           </condition>                           <condition>                              <conditionConstant>DATA_RETENTION_IN_HOURS</conditionConstant>                              <optional>true</optional>                              <value>24</value>" + 
                       "    </condition>                        </conditions>                        <optional>false</optional>                        <resource>                           <dataType>locationSymbolic</dataType>                           <scheme>CONTEXT</scheme>                        </resource>                     </requestItem>                  </responseItem>                  <responseItem>                     <decision>PERMIT</decision>" + 
                       " <requestItem>                        <actions class=\"java.util.ArrayList\">                           <action>                              <actionConstant>READ</actionConstant>                              <optional>false</optional>                           </action>                        </actions>                        <conditions class=\"java.util.ArrayList\">                           <condition>                              <conditionConstant>SHARE_WITH_CIS_MEMBERS_ONLY</conditionConstant>" + 
                       "       <optional>true</optional>                              <value>YES</value>                           </condition>                           <condition>                              <conditionConstant>SHARE_WITH_3RD_PARTIES</conditionConstant>                              <optional>true</optional>                              <value>YES</value>                           </condition>                           <condition>                              <conditionConstant>RIGHT_TO_OPTOUT</conditionConstant>" +
                       "       <optional>false</optional>                              <value>YES</value>                           </condition>                           <condition>                              <conditionConstant>DATA_RETENTION_IN_HOURS</conditionConstant>                              <optional>true</optional>                              <value>24</value>                           </condition>                        </conditions>                        <optional>false</optional>                        <resource>                           <dataType>name</dataType>" + 
                       "    <scheme>CONTEXT</scheme>                        </resource>                     </requestItem>                  </responseItem>                  <responseItem>                     <decision>PERMIT</decision>                     <requestItem>                        <actions class=\"java.util.ArrayList\">                           <action>                              <actionConstant>READ</actionConstant>                              <optional>false</optional>                           </action>                           <action>" + 
                       "       <actionConstant>WRITE</actionConstant>                              <optional>false</optional>                           </action>                           <action>                              <actionConstant>CREATE</actionConstant>                              <optional>false</optional>                           </action>                           <action>                              <actionConstant>DELETE</actionConstant>                              <optional>false</optional>                           </action>                        </actions>                        <conditions class=\"java.util.ArrayList\">" + 
                       "    <condition>                              <conditionConstant>SHARE_WITH_CIS_MEMBERS_ONLY</conditionConstant>                             <optional>true</optional>                              <value>YES</value>                           </condition>                           <condition>                              <conditionConstant>SHARE_WITH_3RD_PARTIES</conditionConstant>                              <optional>true</optional>                              <value>YES</value>                           </condition>                           <condition>                              <conditionConstant>RIGHT_TO_OPTOUT</conditionConstant>                              <optional>false</optional>                              <value>YES</value>" + 
                       "    </condition>                       </conditions>                        <optional>false</optional>                        <resource>                           <dataType>SomeDataType</dataType>                           <scheme>CONTEXT</scheme>                        </resource>                     </requestItem>                  </responseItem>                  <responseItem>                     <decision>PERMIT</decision>                     <requestItem>                        <actions class=\"java.util.ArrayList\">" + 
                       "    <action>                              <actionConstant>READ</actionConstant>                              <optional>false</optional>                           </action>                           <action>                              <actionConstant>WRITE</actionConstant>                              <optional>false</optional>                           </action>                           <action>                              <actionConstant>CREATE</actionConstant>                              <optional>false</optional>                           </action>" + 
                       "    <action>                              <actionConstant>DELETE</actionConstant>                              <optional>false</optional>                           </action>                        </actions>                        <conditions class=\"java.util.ArrayList\">                           <condition>                              <conditionConstant>SHARE_WITH_CIS_MEMBERS_ONLY</conditionConstant>                              <optional>true</optional>                              <value>YES</value>                           </condition>                           <condition>" + 
                       "       <conditionConstant>SHARE_WITH_3RD_PARTIES</conditionConstant>                              <optional>true</optional>                              <value>YES</value>                           </condition>                           <condition>                              <conditionConstant>RIGHT_TO_OPTOUT</conditionConstant>                              <optional>false</optional>                              <value>YES</value>                           </condition>                        </conditions>                        <optional>false</optional>                        <resource>                           <dataType>AnotherDataType</dataType>                           <scheme>CONTEXT</scheme>                        </resource>" +
                       "</requestItem>                  </responseItem>               </responseItems>               <negotiationStatus>ONGOING</negotiationStatus>               <requestor class=\"org.societies.api.schema.identity.RequestorServiceBean\">                  <requestorId>jack.societies.local.macs.hw.ac.uk</requestorId>                  <requestorServiceId>                     <serviceInstanceIdentifier>css://eliza@societies.org/HelloEarth</serviceInstanceIdentifier>                     <identifier class=\"java.net.URI\">css://eliza@societies.org/HelloEarth</identifier>                  </requestorServiceId>               </requestor>            </responsePolicy>" +
                       "<negotiationDetails>               <negotiationID>1223</negotiationID>               <requestor class=\"org.societies.api.schema.identity.RequestorCisBean\">                  <requestorId>jack.societies.local.macs.hw.ac.uk</requestorId>		  <cisRequestorId>jackCis.societies.local.macs.hw.ac.uk</cisRequestorId>               </requestor>            </negotiationDetails>         </userFeedbackPrivacyNegotiationEvent>";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //GET SIMPLE SERIALISER
  		Registry registry = new Registry();
  		Strategy strategy = new RegistryStrategy(registry);
  		Serializer ser = new Persister(strategy);
  		try {
  			registry.bind(java.net.URI.class, URIConverter.class);
  		} catch (Exception e) {
  			System.out.println(e.getMessage());
  		}
  		
  		Class<?> cl = org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent.class;
  		Object output = null;
  		try {
  			output = ser.read(cl, beanXML);
  		} catch (Exception e) {
  			System.out.println(e.getMessage());
  		}
  		
  		UserFeedbackPrivacyNegotiationEvent bean = (UserFeedbackPrivacyNegotiationEvent)output;
  		Intent intent = new Intent(this.getApplicationContext(), NegotiationActivity.class);
		intent.putExtra(EXTRA_PRIVACY_POLICY, (Parcelable) bean);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
