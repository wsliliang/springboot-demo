ChatGPT写页面，根据traceId查询调用链
```
使用原生html和js写一个页面，最上面一个输入框A，输入框placeholder为:"输入span index名称"，输入框下面有另外一个输入框B，placeholder为:"输入traceId", 再下面有个按钮"查询",点击按钮使用fetch发送http post请求，请求url为http://localhost:9200/{indexName}/_search,Content-Type为application/json,requestbody为{
  "_source": {
    "includes": ["traceID", "spanID","operationName","references","tags","process.serviceName"]
  },
  "query": {
    "match": {
      "traceID": "{traceId}"
    }
  }
},
url中的{indexName}为输入框A的值，body中的{traceId}为输入框B的值，接口响应为json,假设响应体为resp，resp.hits.hits的值为json数组，数组中的每个元素都有_source属性，_source的值也是json对象，取出每个元素的_source值，组成一个新的数组，并将数组的值输出到查询按钮的下方。
```

直接把ES查询结果给GPT,让它分析调用链
```
以下是jaeger存储在ES中的span数据，请分析调用链：
```
