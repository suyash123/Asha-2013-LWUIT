/*
 *  Copyright © 2008, 2010, Oracle and/or its affiliates. All rights reserved
 */
package com.bsb.hike.io;

import com.bsb.hike.util.Log;
import com.sun.lwuit.html.DocumentInfo;
import com.sun.lwuit.html.DocumentRequestHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 * An implementation of DocumentRequestHandler that handles fetching HTML
 * documents both from HTTP and from the JAR. This request handler takes care of
 * cookies, redirects and handles both GET and POST requests
 *
 * @author Ofir Leitner
 */
public class HttpRequestHandler implements DocumentRequestHandler {

    private static final String ioErrorMessage = "There seems to be a network issue. Please try again later.";
    private static final String securityErrorMessage = "Please check your Network Settings.";
    private static final String TAG = "HttpRequestHandler";

    /**
     * {@inheritDoc}
     */
    public InputStream resourceRequested(DocumentInfo docInfo) {
        HttpConnection hc = null;
        try {
            Log.v(TAG, "docInfo: " + docInfo);
            String url = docInfo.getUrl();
            Log.v(TAG, "url: " + url);
            String params = docInfo.getParams();
            if ((!docInfo.isPostRequest()) && (params != null) && (!params.equals(""))) {
                url = url + "?" + params;
            }

            hc = (HttpConnection) Connector.open(url);
            String encoding = null;
            if (docInfo.isPostRequest()) {
                encoding = "application/x-www-form-urlencoded";
            }
            if (!docInfo.getEncoding().equals(DocumentInfo.ENCODING_ISO)) {
                encoding = docInfo.getEncoding();
            }
            //hc.setRequestProperty("Accept_Language","en-US");

            if (encoding != null) {
                hc.setRequestProperty("Content-Type", encoding);
            }

            if (docInfo.isPostRequest()) {
                hc.setRequestMethod(HttpConnection.POST);
                if (params == null) {
                    params = "";
                }
                byte[] paramBuf = params.getBytes();
                hc.setRequestProperty("Content-Length", "" + paramBuf.length);
                OutputStream os = hc.openOutputStream();
                os.write(paramBuf);
                os.close();

                //os.flush(); // flush is said to be problematic in some devices, uncomment if it is necessary for your device
            }
            Log.v(TAG, "docInfo: " + docInfo);

            String contentTypeStr = hc.getHeaderField("content-type");
            if (contentTypeStr != null) {
                contentTypeStr = contentTypeStr.toLowerCase();
                if (docInfo.getExpectedContentType() == DocumentInfo.TYPE_HTML) { //We perform these checks only for text (i.e. main page), for images/css we just send what the server sends and "hope for the best"
                    if (contentTypeStr != null) {
                        if ((contentTypeStr.startsWith("text/")) || (contentTypeStr.startsWith("application/xhtml")) || (contentTypeStr.startsWith("application/vnd.wap"))) {
                            docInfo.setExpectedContentType(DocumentInfo.TYPE_HTML);
                        } else if (contentTypeStr.startsWith("image/")) {
                            docInfo.setExpectedContentType(DocumentInfo.TYPE_IMAGE);
                            hc.close();
                            return getStream("<img src=\"" + url + "\">", null);
                        } else {
                            hc.close();
                            return getStream("Content type " + contentTypeStr + " is not supported.", "Error");
                        }
                    }
                }

                if ((docInfo.getExpectedContentType() == DocumentInfo.TYPE_HTML) || (docInfo.getExpectedContentType() == DocumentInfo.TYPE_CSS)) { // Charset is relevant for HTML and CSS only
                    int charsetIndex = contentTypeStr.indexOf("charset=");
                    if (charsetIndex != -1) {
                        String charset = contentTypeStr.substring(charsetIndex + 8);
                        docInfo.setEncoding(charset.trim());
                        //                    if ((charset.startsWith("utf-8")) || (charset.startsWith("utf8"))) { //startwith to allow trailing white spaces
                        //                        docInfo.setEncoding(DocumentInfo.ENCODING_UTF8);
                        //                    }
                    }

                }
            }

            int response = hc.getResponseCode();
            String responsemsg = hc.getResponseMessage();
            Log.v(TAG, "response code: " + response);
            if (response / 100 == 3) { // 30x code is redirect
                String newURL = hc.getHeaderField("Location");
                if (newURL != null) {
                    hc.close();
                    docInfo.setUrl(newURL);
                    if ((response == 302) || (response == 303)) { // The "302 Found" and "303 See Other" change the request method to GET
                        docInfo.setPostRequest(false);
                        docInfo.setParams(null); //reset params
                    }
                    return resourceRequested(docInfo);
                }
            } else if (response == 200) {
                InputStream is = hc.openInputStream();
                int x;
                String datastr = "";
                while ((x = is.read()) != -1){
                    datastr += (char)x;
                }
                Log.v(TAG, "inputstream read" + datastr);
                return new ByteArrayInputStream(datastr.getBytes());
            } else {
                return getStream(responsemsg, "" + response);
            }
        } catch (Exception e) {
            String msg = "Error occured", title = "Error";
            if (e instanceof SecurityException) {
                msg = securityErrorMessage;
                title = "Security error";
            } else if (e instanceof IOException) {
                msg = ioErrorMessage;
            } else if (e instanceof IllegalArgumentException) {
                title = "Malformed URL";
            }
            Log.v(TAG, "Exception: " + e.getMessage());
            return getStream(msg, title);
        } finally {
            try {
                if (hc != null) {
                    hc.close(); // close HttpConnection so it won't be left open after HTMLComponent has handled InputStream
                }
            } catch (Exception e) {
            }
        }
        return getStream("Error occured.", "Error");
    }

    /**
     * Returns an Inputstream of the specified HTML text
     *
     * @param htmlText The text to get the stream from
     * @param title The page's title
     * @return an Inputstream of the specified HTML text
     */
    private InputStream getStream(String htmlText, String title) {
        Log.v(TAG, "creating local stream" + htmlText);
        String titleStr = "";
        if (title != null) {
            titleStr = "<head><title>" + title + "</title></head>";
        }
        htmlText = "<html>" + titleStr + "<body>" + htmlText + "</body></html>";
        ByteArrayInputStream bais = new ByteArrayInputStream(htmlText.getBytes());
        Log.v(TAG, "returning local stream" + htmlText);
        return bais;
    }
}