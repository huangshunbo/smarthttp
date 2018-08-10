#### Summary
---


#### Getting Started
---
导入库
```Java
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}


dependencies {
  implementation 'com.github.huangshunbo:smarthttp:lastest.release'
}
```

初始化
```Java
SmartHttp.init(Application application);
SmartHttp.initBuilder(OkHttpClient.Builder builder);
SmartHttp.setHeaders(HashMap<String, String> headers);
SmartHttp.addInterceptor(Interceptor interceptor);
SmartHttp.addNetworkInterceptor(Interceptor interceptor);
```
init必须要进行调用;其他可选,builder不设置的话会有一套默认设置
SmartHttp同时暴露获取PostRequestCall和GetRequestCall方法

推荐在业务层在做一层封装,加入URL处理、加解密、Response解析、请求管理等,下面是一个推荐案例

Smart.java 封装
```Java
public class Smart {

    static {
        SmartHttp.init(BaseApplication.application);
    }

    public static RequestCall orderPost(String method, HashMap<String,String> params){
        return SmartHttp.post(URLConstants.generateOrderUrl(method))
                .setRequestParams(params)
                .buildRequestWithCache();
    }

    public static RequestCall wheatherGet(HashMap<String,String> params){
        return SmartHttp.get(URLConstants.generateWheatherUrl())//在【URLConstants】中做URL处理
                .setRequestParams(params)//可以在这里加入加密的环节,将参数进行加密
                .buildRequestWithCache(100);//支持设置是否缓存及缓存时间
    }
}
```
URLConstants.java URL地址封装,可配置不同环境下的请求路径,Debug或线上环境
```Java
class URLConstants {

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    private static final String RELEASE_URL_WHEATHER = "www.sojson.com/open/api/weather/json.shtml";
    private static final String DEBUG_URL_WHEATHER = "www.sojson.com/open/api/weather/json.shtml";

    public static String generateOrderUrl(String method) {
        return generateDomain() + method;
    }

    public static String generateWheatherUrl(String method){
        return generateDomain() + method;
    }

    private static String generateDomain(){
        if(DEBUG){
            return HTTP + DEBUG_URL_WHEATHER;
        }else {
            return HTTPS + RELEASE_URL_WHEATHER;
        }
    }
}
```
Response解析处理,默认提供两种简单的FileCallback、StringCallback,继承自AbstractCallback
一般我们的返回结果都是json且有多层级,这里提供一个案例
最外层的CommonBean
```Java
public class CommonBean<T> {
    private int status;
    private String message;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
```
那么我们对应这个外层CommonBean自己写一个Callback进行解析
```Java
public abstract class CommonCallback<T> extends AbstractCallback<T> {
    Gson gson = new Gson();
    @Override
    public T parseResponse(Response response) throws IOException {
        //此处还可以加入解密需求
        SmartBean bean = gson.fromJson(response.body().string(),CommonBean.class);
        Class clz = (Class<T>)(((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        return (T) gson.fromJson(gson.toJson(bean.getData()),clz);
    }
}
```
此外有一些错误返回值我们可以需要共同的拦截处理,有两个渠道一个是在Callback这里覆写onFailure进行拦截处理,另一个渠道自然是通过Interceptor进行拦截处理

那么我们的请求就是
```Java
HashMap<String,String> params = new HashMap<>();
params.put("city","深圳");
Smart.wheatherGet(params).execute(new SmartCallback<DataBean>() {

    @Override
    public void onSuccess(DataBean bean) {
        Log.d("hsb","onSuccess \n" + bean.toString());
    }

    @Override
    public void onFailure(ApiException e) {
        Log.d("hsb","onFailure \n" + e.getDetailMessage());
    }
});
```
最后还有请求的管理,可以在网络请求的地方加入,如果你使用MVC那么可以在BaseActivity中做处理;如果你是用MVP可以在P层中做处理。
这里以MVC为例,
```Java
public class BaseActivity extends AppCompatActivity implements IRequestManager<RequestCall>{

    private BlockingQueue<RequestCall> mCallBlockingQueue = new LinkedBlockingDeque<>();

    @Override
    protected void onPause() {
        super.onPause();
        removeAllCalls();
    }

    @Override
    public void addCalls(RequestCall call) {
        //call.getCall() == null 同样加入，重新加载会重新生成call
        if (call != null && mCallBlockingQueue != null && !mCallBlockingQueue.contains(call)) {
            mCallBlockingQueue.add(call);
        }
    }

    @Override
    public void removeCall(RequestCall call) {
        if (mCallBlockingQueue != null) {
            synchronized (mCallBlockingQueue) {
                if (mCallBlockingQueue != null) {
                    mCallBlockingQueue.remove(call);
                }
            }
        }
    }

    @Override
    public void removeAllCalls() {
        if (mCallBlockingQueue != null) {
            synchronized (mCallBlockingQueue) {
                if (mCallBlockingQueue != null) {
                    for (RequestCall requestCall : mCallBlockingQueue) {
                        if (requestCall != null && requestCall.getCall() != null) {
                            requestCall.getCall().cancel();
                        }
                    }
                    mCallBlockingQueue.clear();
                    mCallBlockingQueue = null;
                }
            }
        }
    }
}
```

#### Feature
---

#### Sample
---
#### Known Issues
---
暂时没有收到任何反馈，有任何疑问或需求，可提issue。
#### Support
---
黄顺波





