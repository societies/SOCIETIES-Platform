package org.societies.comm.event.test;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.comm.event.EventMgmtImpl;
import org.societies.comm.event.mock.MockEventListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EventMgmtImplTest {

	private EventMgmtImpl eventMgmt;
	private MockEventListener mockListener;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {		
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/bundle_context_test.xml");
		eventMgmt=(EventMgmtImpl) ctx.getBean("eventMgmt");
		mockListener=new MockEventListener();
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testSubscribeInternalEvent() {
		eventMgmt.subscribeInternalEvent(mockListener, new String[] {EventTypes.CONTEXT_EVENT}, "someFilter");		
	}

	//@Test
	public void testUnSubscribeInternalEvent() {
		eventMgmt.subscribeInternalEvent(mockListener, new String[] {EventTypes.CONTEXT_EVENT}, "someFilter");		
					
	}

	//@Test
	public void testPublishInternalEvent() {
		InternalEvent event = new InternalEvent(EventTypes.CONTEXT_EVENT, "css1_event", "css1", new String("content"));		
		try {
			eventMgmt.publishInternalEvent(event);
		} catch (EMSException e) {
			fail(e.getMessage());
		}
	}

}
