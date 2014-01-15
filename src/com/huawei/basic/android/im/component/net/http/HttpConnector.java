/*
 * 文件名: HttpConnector.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: admin
 * 创建时间:Feb 11, 2012
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.huawei.basic.android.im.component.net.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.http.NameValuePair;

import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.Request.ContentType;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.component.net.http.Response.ResponseCode;
import com.huawei.basic.android.im.utils.StringUtil;

/**
 * HTTP连接类，提供连接的功能
 * 负责发起HTTP连接、获取网络数据
 * 
 */
public class HttpConnector
{
    
    /**
     * debug tag.
     */
    private static final String TAG = "HttpConnector";
    
    /**
     * 
     * 联网方法
     * 
     * @param request 请求对象
     * @return 响应对象
     */
    public static Response connect(Request request)
    {
        Response response = new Response();
        response.setCorrespondingRequest(request);
        HttpURLConnection httpConn = null;
        
        // url无效
        if (StringUtil.isNullOrEmpty(request.getUrl()))
        {
            response.setResponseCode(ResponseCode.ParamError);
            return response;
        }
        
        try
        {
            Logger.d(TAG, "request url : " + request.getUrl());
            URL url = new URL(request.getUrl());
            httpConn = (HttpURLConnection) url.openConnection();
            // httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            
            // set request method.
            setRequestMethod(request, httpConn);
            
            // set request property.
            setRequestProperty(request, httpConn);
            
            httpConn.setConnectTimeout(20000);
            httpConn.setReadTimeout(20000);
            
            if (request.getBody() != null)
            {
                // set request content.
                Logger.d(TAG, "request body : \n" + request.getBody());
                byte[] data = request.getBody().getBytes("UTF-8");
                
                // write data.
                OutputStream os = httpConn.getOutputStream();
                os.write(data);
                os.flush();
                os.close();
            }
            
            // get response code.
            int responseCode = initResponseCode(response, httpConn);
            
            // connect OK(200) or Created(201)
            
            switch (responseCode)
            {
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_CREATED:
                case HttpURLConnection.HTTP_BAD_REQUEST:
                case HttpURLConnection.HTTP_FORBIDDEN:
                case HttpURLConnection.HTTP_NOT_FOUND:
                case HttpURLConnection.HTTP_CONFLICT:
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    setResponseData(request, response, httpConn, false);
                    break;
                
                default:
                    response.setResponseCode(ResponseCode.Failed);
                    response.setData(Integer.toString(responseCode));
                    break;
            }
        }
        catch (SocketTimeoutException e)
        {
            e.printStackTrace();
            response.setResponseCode(ResponseCode.Timeout);
        }
        catch (ConnectException e)
        {
            e.printStackTrace();
            Logger.d(TAG, "HttpConnector exception!");
            response.setResponseCode(ResponseCode.NetworkError);
        }
        catch (SocketException se)
        {
            se.printStackTrace();
            response.setResponseCode(ResponseCode.NetworkError);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            response.setResponseCode(ResponseCode.ParamError);
        }
        catch (IOException e)
        {
            try
            {
                int responseCode = initResponseCode(response, httpConn);
                if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode
                        || HttpURLConnection.HTTP_FORBIDDEN == responseCode
                        || HttpURLConnection.HTTP_BAD_REQUEST == responseCode
                        || HttpURLConnection.HTTP_NOT_FOUND == responseCode
                        || HttpURLConnection.HTTP_CONFLICT == responseCode
                        || HttpURLConnection.HTTP_INTERNAL_ERROR == responseCode)
                {
                    setResponseData(request, response, httpConn, true);
                }
                Logger.d(TAG, "IOException getrespCode:" + responseCode);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                if (httpConn != null)
                {
                    httpConn.disconnect();
                }
            }
            Logger.d(TAG, "HttpConnector IOException ......!");
            
        }
        finally
        {
            if (httpConn != null)
            {
                httpConn.disconnect();
            }
        }
        Logger.d(TAG, "Response Code :  " + response.getResponseCode());
        return response;
    }
    
    /**
     * @param request
     * @param response
     * @param httpConn
     * @throws IOException
     */
    private static void setResponseData(Request request, Response response,
            HttpURLConnection httpConn, boolean isError) throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        InputStream is = null;
        if (isError)
        {
            is = httpConn.getErrorStream();
            BufferedReader br = null;
            if (is == null)
            {
                InputStream inputstream = httpConn.getInputStream();
                br = new BufferedReader(new InputStreamReader(inputstream));
            }
            else
            {
                br = new BufferedReader(new InputStreamReader(is));
            }
            
            String responsedata = "";
            String nachricht;
            while ((nachricht = br.readLine()) != null)
            {
                responsedata += nachricht;
            }
            bytes = responsedata.getBytes();
        }
        else
        {
            is = httpConn.getInputStream();
            int length = is.read(bytes);
            while (length != -1)
            {
                os.write(bytes, 0, length);
                length = is.read(bytes);
            }
            bytes = os.toByteArray();
        }
        
        if (request.isNeedByte())
        {
            response.setByteData(bytes);
        }
        
        response.setData(new String(bytes, "UTF-8"));
        is.close();
        
        if (!request.isNeedByte())
        {
            Logger.d(TAG, "response data : " + response.getData());
        }
        else
        {
            Logger.d(TAG, "response data : [ is byte data ]");
        }
    }
    
    /**
     * @param response
     * @param httpConn
     * @return
     * @throws IOException
     */
    private static int initResponseCode(Response response,
            HttpURLConnection httpConn) throws IOException
    {
        int responseCode = httpConn.getResponseCode();
        Logger.d(TAG, "Response Code[" + responseCode + "]");
        switch (responseCode)
        {
            case HttpURLConnection.HTTP_OK:
            case HttpURLConnection.HTTP_CREATED:
                response.setResponseCode(ResponseCode.Succeed);
                break;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                response.setResponseCode(ResponseCode.BadRequest);
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                response.setResponseCode(ResponseCode.UnAuthorized);
                break;
            case HttpURLConnection.HTTP_FORBIDDEN:
                response.setResponseCode(ResponseCode.Forbidden);
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                response.setResponseCode(ResponseCode.NotFound);
                break;
            case HttpURLConnection.HTTP_CONFLICT:
                response.setResponseCode(ResponseCode.Conflict);
                break;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                response.setResponseCode(ResponseCode.InternalError);
                break;
            default:
                response.setResponseCode(ResponseCode.Failed);
                break;
        }
        return responseCode;
    }
    
    /**
     * set request property.
     * @param httpConn HttpURLConnection
     */
    private static void setRequestProperty(Request request,
            HttpURLConnection httpConn)
    {
        
        // set content type
        if (request.getContentType() == ContentType.XML)
        {
            httpConn.setRequestProperty("Content-Type",
                    "text/xml;charset=UTF-8");
        }
        else if (request.getContentType() == ContentType.JSON)
        {
            httpConn.setRequestProperty("Content-Type",
                    "application/json;charset=UTF-8");
        }
        
        // set request property
        if (request.getRequestProperties() != null)
        {
            for (NameValuePair nameValuePair : request.getRequestProperties())
            {
                Logger.d(TAG,
                        nameValuePair.getName() + ":"
                                + nameValuePair.getValue());
                httpConn.setRequestProperty(nameValuePair.getName(),
                        nameValuePair.getValue());
            }
        }
    }
    
    /**
     * set request method
     * @param httpConn HttpURLConnection
     * @throws ProtocolException
     */
    private static void setRequestMethod(Request request,
            HttpURLConnection httpConn) throws ProtocolException
    {
        if (request.getRequestMethod() == RequestMethod.GET)
        {
            httpConn.setRequestMethod("GET");
        }
        else if (request.getRequestMethod() == RequestMethod.POST)
        {
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
        }
        else if (request.getRequestMethod() == RequestMethod.PUT)
        {
            httpConn.setRequestMethod("PUT");
            httpConn.setDoOutput(true);
        }
        else if (request.getRequestMethod() == RequestMethod.DELETE)
        {
            httpConn.setRequestMethod("DELETE");
        }
    }
}
