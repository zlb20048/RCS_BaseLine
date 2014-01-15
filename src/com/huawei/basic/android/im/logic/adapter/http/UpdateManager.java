package com.huawei.basic.android.im.logic.adapter.http;

import java.io.ByteArrayInputStream;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import com.huawei.basic.android.im.common.FusionConfig;
import com.huawei.basic.android.im.component.log.Logger;
import com.huawei.basic.android.im.component.net.http.HttpManager;
import com.huawei.basic.android.im.component.net.http.IHttpListener;
import com.huawei.basic.android.im.component.net.http.Response;
import com.huawei.basic.android.im.component.net.http.Request.ContentType;
import com.huawei.basic.android.im.component.net.http.Request.RequestMethod;
import com.huawei.basic.android.im.logic.model.CheckUpdateInfoModel;
import com.huawei.basic.android.im.utils.FileUtil;


/**
 *  版本更新管理类 [功能详细描述]
 * @author meiyue
 * @version [RCS Client V100R001C03, Feb 11, 2012]
 */
public class UpdateManager extends HttpManager
{
    /**
     * Tag
     */
    public static final String TAG = "UpdateManager";
    
    
    /**
     * 检查更新
     */
    public static final int CHECK_UPDATE = 0x00000005;
    
    /**
     * 获取包名称
     */
    public static final int GET_PACKAGE_NAME = 0x00000003;
    
    /**
     * 下载filelist.xml的 url
     */
    private String fileListUrl;

    
    /**
     *检查更新<BR>
     * @param version version
     * @param width width
     * @param height height
     * @param iListener iListener
     */
    public void requestCheckUpdate(String version, String width, String height,
            IHttpListener iListener)
    {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("version", version);
        data.put("width", width);
        data.put("height", height);
        super.send(UpdateManager.CHECK_UPDATE, data, iListener);
    }
    
    /**
     * 获取http内容类型 [功能详细描述]
     * @param action action
     * @return http内容类型
     * @see com.chinaunicom.woyou.framework.net.http.HttpManager#getContentType()
     */
    @Override
    protected ContentType getContentType(int action)
    {
        return ContentType.XML;
    }
    
    /**
     * 获取request请求方式<BR>
     * @param action action标识
     * @return RequestMethod request请求方式
     * @see com.huawei.basic.android.im.component.net.http.HttpManager#getRequestMethod(int)
     */
    @Override
    protected RequestMethod getRequestMethod(int action)
    {
        switch (action)
        {
            case CHECK_UPDATE:
                return RequestMethod.POST;
            case GET_PACKAGE_NAME:
                return RequestMethod.POST;
        }
        return super.getRequestMethod(action);
    }
    
    /**
     * 获取url地址
     * @param action action标识
     * @param sendData sendData
     * @return url地址
     * @see com.chinaunicom.woyou.framework.net.http.RequestAdapter#getUrl()
     */
    @Override
    public String getUrl(int action, Map<String, Object> sendData)
    {
        String url = "";
        switch (action)
        {
            
            case CHECK_UPDATE:
                url = FusionConfig.getInstance()
                        .getAasResult()
                        .getLiveupdateurl()
                        + "/OUS/webService/checkVersion.action";
                break;
            case GET_PACKAGE_NAME:
                url = fileListUrl ;
                break;
            default:
                break;
        }
        return url;
    }
    
    /**
     * 获取解析后服务器返回的数据
     * @param action action标识
     * @param response response
     * @param sendData sendData
     * @return 解析服务器返回的数据
     * @see com.chinaunicom.woyou.framework.net.http.RequestAdapter#getUrl()
     */
    @Override
    protected Object handleData(int action, Map<String, Object> sendData,
            Response response)
    {
        String responseData = response.getData();
        switch (action)
        {
            
            case CHECK_UPDATE:
                return downloadAndParse(stringToStream(responseData),
                        CHECK_UPDATE);
            case GET_PACKAGE_NAME:
                return downloadAndParse(stringToStream(responseData),
                        GET_PACKAGE_NAME);
            default:
                return null;
        }
    }
    
    /**
     * 获取url地址
     * @param action action标识
     * @param sendData sendData
     * @return 解析服务器返回的数据
     * @see com.chinaunicom.woyou.framework.net.http.RequestAdapter#getUrl()
     */
    @Override
    protected String getBody(int action, Map<String, Object> sendData)
    {
        switch (action)
        {
            case CHECK_UPDATE:
                String version = (String) sendData.get("version");
                return getUpdateParamerStr(version, sendData);
                
            case GET_PACKAGE_NAME:
                return null;
                
            default:
                break;
        }
        return null;
    }
    
    /**
    * http请求参数
    * @param action action标识
    * @return http请求参数
    * @see com.chinaunicom.woyou.framework.net.http.HttpManager#getRequestProperties()
    */
    @Override
    public List<NameValuePair> getRequestProperties(int action)
    {
        List<NameValuePair> temp = super.getRequestProperties(action);
        
        temp.add(new BasicNameValuePair("Authorization",
                FusionConfig.getInstance().getOseReqAuthorization()));
        
        return temp;
    }
    
    /**
     * 
     * 更新参数 [功能详细描述]
     * @return String
     */
    private String getUpdateParamerStr(String str, Map<String, Object> sendData)
    {
        StringBuffer xml = new StringBuffer();
        String width = (String) sendData.get("width");
        String height = (String) sendData.get("height");
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<root>");
        xml.append("<rule name=\"DeviceName\">")
                .append("Android")
                .append("</rule>");
        xml.append("<rule name=\"DeviceType\">").append("").append("</rule>");
        xml.append("<rule name=\"OSVersion\">")
                //.append(Build.VERSION.SDK)
                .append("hitalk")
                .append("</rule>");
        xml.append("<rule name=\"Version\">").append(str)
        // .append("1.0")
                .append("</rule>");
        xml.append("<rule name=\"Screen\">")
                .append(height)
                .append("_")
                .append(width)
                .append("</rule>");
        xml.append("<rule name=\"ResourceVersion\">")
                .append("")
                .append("</rule>")
                .append("</root>");
        return xml.toString();
    }
    
    /**
     * 
     * 将String转化为InputStream [功能详细描述]
     * 
     * @param str String
     * @return InputStream
     */
    public InputStream stringToStream(String str)
    {
        InputStream inputStream = null;
        inputStream = new ByteArrayInputStream(str.getBytes());
        return inputStream;
    }
    
    /**
     * 
     * 解析方法 [功能详细描述]
     * @param inputStream 要解析的InputStream
     * @param handlerStyle 解析标识符
     * @return boolean
     */
    public CheckUpdateInfoModel downloadAndParse(InputStream inputStream,
            int handlerStyle)
    {
        CheckUpdateInfoModel checkUpdateModel = null;
        try
        {
            
            InputStreamReader inParse = new InputStreamReader(inputStream,
                    "utf-8");
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            UpdateHandler handler = new UpdateHandler(handlerStyle);
            reader.setContentHandler(handler);
            reader.parse(new InputSource(inParse));
            
            // 解析完后获取对象
            checkUpdateModel = handler.getCheckUpdateModel();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            // 关闭流
            FileUtil.closeStream(inputStream);
        }
        
        return checkUpdateModel;
    }
    


    public  void downloadAndParseFilelist(String url, IHttpListener iListener)
    {
        fileListUrl = url + CheckUpdateInfoModel.MID_URL + CheckUpdateInfoModel.FILELIST_NAME;
        Logger.d("LYDIA", "*************************filelistUrl" + fileListUrl);
        super.send(UpdateManager.GET_PACKAGE_NAME, null, iListener);
    }

    
}
