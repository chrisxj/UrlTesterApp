package com.example.chrisjohnson.myapplication;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

/**
 * Created by chrisjohnson on 12/12/14.
 */
public class UrlHelperTest extends AndroidTestCase {

    public static final String URL_CORRECT_FORMAT = "http://example/com";
    public static final String URL_MISSING_PROTOCOL = "example/com";

    @SmallTest
    public void testCheckNormaliseUrlAddsProtocolTest() {
        UrlHelper helper = new UrlHelper();
        Assert.assertEquals(URL_CORRECT_FORMAT,helper.NormaliseUrl(URL_MISSING_PROTOCOL));
    }

    @SmallTest
    public void testCheckNormaliseUrlIgnoresCorrectlyFormattedUrlTest() {
        UrlHelper helper = new UrlHelper();
        Assert.assertEquals(URL_CORRECT_FORMAT,helper.NormaliseUrl(URL_CORRECT_FORMAT));
    }

}
