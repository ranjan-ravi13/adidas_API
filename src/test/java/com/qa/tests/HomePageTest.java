package com.qa.tests;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.qa.base.TestBase;
import com.qa.client.RestClient;
import com.qa.utility.Utility;

public class HomePageTest extends TestBase {

	TestBase testBase;
	String baseUrl;
	String pathUrl;
	String requestUrl;
	RestClient restClient;
	CloseableHttpResponse closeableHttpResponse;

	@BeforeMethod
	public void setUp() throws ClientProtocolException, IOException {
		testBase = new TestBase();
		baseUrl = prop.getProperty("authorityUrl");
		pathUrl = prop.getProperty("pathUrl");
		requestUrl = baseUrl + pathUrl;
		restClient = new RestClient();
	}

	@Test
	public void verifyResponseTime() throws ClientProtocolException, IOException {
		long startTime = System.currentTimeMillis();
		CloseableHttpResponse httpResponse = restClient.get(requestUrl);
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("*** elapsedTime : " + elapsedTime);
		int StatusCode = httpResponse.getStatusLine().getStatusCode();

		Assert.assertEquals(StatusCode, testBase.RESPONSE_STATUS_CODE_200, "Get method call not successfull");
		Assert.assertTrue((elapsedTime < 1000), " Response Time is more than 1 second");// elapsed time is in milliseconds
	}

	@Test
	public void landingPathApiTest() throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException {

		CloseableHttpResponse httpResponse = restClient.get(requestUrl);
		int StatusCode = httpResponse.getStatusLine().getStatusCode();
		System.out.println("*** StatusCode received is : " + StatusCode);

		Assert.assertEquals(StatusCode, testBase.RESPONSE_STATUS_CODE_200, "Get method call not successfull");

		String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		JSONObject responseJsonObj = new JSONObject(responseString);

		System.out.println("The response from Landing API is --->>" + responseJsonObj);

		// Parsing the above response json to get the values of required keys

		JSONArray component_presentationsArray = responseJsonObj.getJSONArray("component_presentations");
		System.out.println("<<<<  -------------   >>");
		System.out.println("The value 0f component_presentations is --->>" + component_presentationsArray);

		int lengthBaseArray = component_presentationsArray.length();

		// for (int i = 0; i < lengthBaseArray; i++) {
			JSONObject jsonObject = component_presentationsArray.getJSONObject(0);

			try {
				JSONObject jsonComponentObject = jsonObject.getJSONObject("component");
				JSONObject jsoncontent_fieldsObject = jsonComponentObject.getJSONObject("content_fields");

				JSONArray itemsArray = jsoncontent_fieldsObject.getJSONArray("items");

				int lengthInnerArray = itemsArray.length();

				for (int j = 0; j < lengthInnerArray; j++) {

					JSONObject jsonInnerObject = itemsArray.getJSONObject(j);

					JSONObject supporting_fieldsObject = jsonInnerObject.getJSONObject("supporting_fields");
					JSONObject innerSupporting_fieldsObject = supporting_fieldsObject
							.getJSONObject("supporting_fields");
					JSONObject standard_metadataObject = innerSupporting_fieldsObject
							.getJSONObject("standard_metadata");
					String value = standard_metadataObject.getString("analytics_name");
					System.out.println("Analytics_Name Value is : " + value);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.assertTrue(false, "The Test failed since Analytics_Name is not present for every component");
			}
	}

	@Test
	public void imageAccessibilityTest() throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException {
		boolean flag = true;
		CloseableHttpResponse httpResponse = restClient.get(requestUrl);
		int StatusCode = httpResponse.getStatusLine().getStatusCode();
		System.out.println("*** StatusCode received is : " + StatusCode);

		Assert.assertEquals(StatusCode, testBase.RESPONSE_STATUS_CODE_200, "Get method call not successfull");

		String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

		JSONObject responseJsonObj = new JSONObject(responseString);

		System.out.println("The response from Landing API is --->>" + responseJsonObj);

		// Parsing the above response json to get the values of required keys

		JSONArray component_presentationsArray = responseJsonObj.getJSONArray("component_presentations");
		System.out.println("<<<<  -------------   >>");
		System.out.println();
		System.out.println("The value 0f component_presentations is --->>" + component_presentationsArray);

		int lengthBaseArray = component_presentationsArray.length();

		// for (int i = 0; i < lengthBaseArray; i++) {
		JSONObject jsonObject = component_presentationsArray.getJSONObject(0);

		/*
		 * System.out.println("<<<<  -------------   >>"); System.out.println();
		 * System.out.println( "The value 0f component_presentations is --->>"
		 * +jsonObject);
		 */
		try {
			JSONObject jsonComponentObject = jsonObject.getJSONObject("component");
			JSONObject jsoncontent_fieldsObject = jsonComponentObject.getJSONObject("content_fields");

			JSONArray itemsArray = jsoncontent_fieldsObject.getJSONArray("items");

			int lengthInnerArray = itemsArray.length();

			for (int j = 0; j < lengthInnerArray; j++) {

				JSONObject jsonInnerObject = itemsArray.getJSONObject(j);

				JSONObject background_mediaObject = jsonInnerObject.getJSONObject("background_media");

				JSONObject desktop_imageObject = background_mediaObject.getJSONObject("desktop_image");
				JSONObject tablet_imageObject = background_mediaObject.getJSONObject("tablet_image");
				JSONObject mobile_imageObject = background_mediaObject.getJSONObject("mobile_image");

				String url1 = desktop_imageObject.getString("url");
				String url2 = tablet_imageObject.getString("url");
				String url3 = mobile_imageObject.getString("url");

				List<String> urlList = new ArrayList<>();
				urlList.add(url1);
				urlList.add(url2);
				urlList.add(url3);

				for (String URL : urlList) {
					flag = Utility.verifyimageAccessible(URL);
				}
				Assert.assertTrue(flag, "Image link is not accessible");

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" Image url is not present");
			Assert.assertTrue(false, "Image url is not present");
		}

	}

}
