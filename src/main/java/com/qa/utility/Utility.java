package com.qa.utility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.qa.base.TestBase;

public class Utility extends TestBase {
		
	public static boolean  verifyimageAccessible(String url) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			// verifying response code he HttpStatus should be 200 if not,
			// increment as invalid images count
			if (response.getStatusLine().getStatusCode() != 200)
				return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
