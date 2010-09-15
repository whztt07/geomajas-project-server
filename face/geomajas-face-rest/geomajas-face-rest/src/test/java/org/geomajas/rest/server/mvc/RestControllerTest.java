package org.geomajas.rest.server.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.AssertTrue;

import org.geomajas.layer.feature.InternalFeature;
import org.geomajas.security.SecurityManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.vividsolutions.jts.geom.Envelope;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/geomajas/spring/geomajasContext.xml",
		"/org/geomajas/testdata/beanContext.xml", "/org/geomajas/testdata/layerBeans.xml" })
public class RestControllerTest {

	private final Logger log = LoggerFactory.getLogger(RestControllerTest.class);

	@Autowired
	RestController restController;

	@Autowired
	private SecurityManager securityManager;

	@Autowired
	private View view;

	private HandlerAdapter adapter;

	@Before
	public void login() {
		// assure security context is set
		securityManager.createSecurityContext(null);
		adapter = new AnnotationMethodHandlerAdapter();
	}

	@Test
	public void readOneFeature() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/rest/beans/1.json");
		request.setMethod("GET");

		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mav = adapter.handle(request, response, restController);

		Object o = mav.getModel().get(RestController.FEATURE_COLLECTION);
		Assert.assertTrue(o instanceof InternalFeature);
		InternalFeature feature = (InternalFeature) o;
		Assert.assertTrue(feature instanceof InternalFeature);

		Assert.assertEquals("bean1", feature.getAttributes().get("stringAttr").getValue());
		Assert.assertEquals(true, feature.getAttributes().get("booleanAttr").getValue());
		Assert.assertEquals("100,23", feature.getAttributes().get("currencyAttr").getValue());
		Calendar c = Calendar.getInstance();
		c.set(2010, 1, 23, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);
		Assert.assertEquals(c.getTime(), feature.getAttributes().get("dateAttr").getValue());
		Assert.assertEquals(123.456, feature.getAttributes().get("doubleAttr").getValue());
		Assert.assertEquals(456.789F, feature.getAttributes().get("floatAttr").getValue());
		Assert.assertEquals("http://www.geomajas.org/image1", feature.getAttributes().get("imageUrlAttr").getValue());
		Assert.assertEquals(789, feature.getAttributes().get("integerAttr").getValue());
		Assert.assertEquals(123456789L, feature.getAttributes().get("longAttr").getValue());
		Assert.assertEquals((short) 123, feature.getAttributes().get("shortAttr").getValue());
		Assert.assertEquals("http://www.geomajas.org/url1", feature.getAttributes().get("urlAttr").getValue());

		view.render(mav.getModel(), request, response);
		response.flushBuffer();
		// TODO: fix for date GEOT-3266
		// Object json = new JSONParser().parse(response.getContentAsString());
		// Assert.assertTrue(json instanceof JSONObject);

	}

	@Test
	public void bboxFilter() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/rest/beans");
		request.addParameter("bbox", "4,6,0,3");
		request.setMethod("GET");

		MockHttpServletResponse response = new MockHttpServletResponse();
		ModelAndView mav = adapter.handle(request, response, restController);
		Object o = mav.getModel().get(RestController.FEATURE_COLLECTION);
		Assert.assertTrue(o instanceof List<?>);
		for (Object f : (List<?>) o) {
			Assert.assertTrue(f instanceof InternalFeature);
			Assert.assertTrue(new Envelope(4, 6, 0, 3).intersects(((InternalFeature) f).getGeometry()
					.getEnvelopeInternal()));
		}

	}
	
	@Test
	public void featurePaging() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/rest/beans");
		request.setMethod("GET");
		MockHttpServletResponse response = new MockHttpServletResponse();
		// check all
		ModelAndView mav = adapter.handle(request, response, restController);
		Assert.assertEquals(Arrays.asList("1","2","3"), getIdsFromModel(mav.getModel()));
		// check first 1
		request.setParameter("maxFeatures", "1");
		mav = adapter.handle(request, response, restController);
		Assert.assertEquals(Arrays.asList("1"), getIdsFromModel(mav.getModel()));
		// check first 2
		request.setParameter("maxFeatures", "2");
		mav = adapter.handle(request, response, restController);
		Assert.assertEquals(Arrays.asList("1","2"), getIdsFromModel(mav.getModel()));
		// check 1 -3
		request.setParameter("maxFeatures", "2");
		request.setParameter("offset", "1");
		mav = adapter.handle(request, response, restController);
		Assert.assertEquals(Arrays.asList("2","3"), getIdsFromModel(mav.getModel()));
	}
	
	@Test
	public void ordering() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/rest/beans");
		request.setMethod("GET");
		MockHttpServletResponse response = new MockHttpServletResponse();
		// check descending on string attribute
		request.setParameter("order_by", "stringAttr");
		request.setParameter("dir", "DESC");
		ModelAndView mav = adapter.handle(request, response, restController);
		Assert.assertEquals(Arrays.asList("3","2","1"), getIdsFromModel(mav.getModel()));
	}

	private List<String> getIdsFromModel(Map<String, Object> model) {
		Object o = model.get(RestController.FEATURE_COLLECTION);
		Assert.assertTrue(o instanceof List<?>);
		List<?> ff = (List<?>) o;
		ArrayList<String> ids = new ArrayList<String>();
		for (Object f : ff) {
			Assert.assertTrue(f instanceof InternalFeature);
			ids.add(((InternalFeature)f).getId());
		}
		return ids;
	}

}
