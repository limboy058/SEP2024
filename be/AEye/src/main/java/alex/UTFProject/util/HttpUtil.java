package alex.UTFProject.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
public class HttpUtil {
    public static String get(String url, Map<String, String> param) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            //补充参数
            url += "?";
            url += String.join("&", param.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList()));
            log.info("request: " + url);
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，鉴权
//            httpGet.setHeader("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public static String post(String url, Map<String, Object> paramMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            // 通过map集成entrySet方法获取entity
            Set<Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
            }
            // 为httpPost设置封装好的请求参数
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String postJson(String url, Map<String, Object> paramMap, Map<String, Object> jsonMap) {
        return postJson(url,paramMap,jsonMap,null);
    }

    public static String postJson(String url, Map<String, Object> paramMap, Map<String, Object> jsonMap, HttpClient httpClient) {
        if(paramMap != null) {
            url += "?";
            url += String.join("&", paramMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList()));
        }
        //创建客户端
        if(httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
        //创建一个post请求
        HttpPost request = new HttpPost(url);
        //设置响应头
        request.setHeader("Content-Type", "application/json;charset=UTF-8");
        //通过fastJson设置json数据
        JSONObject postData = new JSONObject();
        //设置要检测的内容
        postData.putAll(jsonMap);
        String jsonString = postData.toString();
        request.setEntity(new StringEntity(jsonString, "utf-8"));

        try {
            HttpResponse response = httpClient.execute(request);
            // 从响应模型中获取响应实体
            HttpEntity entity = response.getEntity();
            //得到响应结果
            return EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postFormData(String url, Map<String, Object> paramMap, Map<String, File> fileMap) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            url += "?";
            url += String.join("&", paramMap.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList()));
            HttpPost httppost = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            fileMap.forEach((key, value) -> builder.addPart(key, new FileBody(value)));

            HttpEntity httpEntity = builder.build();

            httppost.setEntity(httpEntity);

            String result = null;
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                EntityUtils.consume(resEntity);
                return result;

            } finally {
                response.close();
            }
        } catch (Exception e) {
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        map.put("b", "c");
        System.out.println(HttpUtil.get("https://zhuanlan.zhihu.com/p/69285935", map));
    }
} 