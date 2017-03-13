# storm简单用例
实现本地和storm集群的流计算

# 具备功能
简单计算单词出现频率

# 相关总结
* Storm在多个工作节点上运行的问题
    * 问题：storm启动之后只在一个worker上运行
    * 原因：storm程序中未指定worker数量，默认为一个worker
    * 解决：
        * 程序中调用backtype.storm.Config的setNumWorkers方法指定worker数量
        * 使用storm rebalance的命令可以动态调整topology的运行worker节点数、线程数
        * storm rebalance topologyName [-w waitTimeSecs] [-n numWorkers] [-e component=parallelism]
* Storm打包问题
    * 问题：使用Eclipse的Export打包，发布到Storm集群，报错
    * 原因：由于storm官方要求运行的jar包需要去除storm-core的依赖，而Eclipse的Export打包无法排除storm-core相关的包
    * 解决：使用maven打jar包，在maven配置文件的storm-core的dependency中添加<scope>provided</scope>或者<scope>test</scope>，可以在打包时解除storm-core相关包的依赖
        * Topology发布时出现AlreadyAliveException(msg: topologyName is alreadyactive)异常
    * 问题：发现已经发布的一个topology，在所有worker节点都未启动，所以在master节点重新发布，报错
    * 原因：已经发布的topology，由于一些特殊原因未在任何一个worker节点启动，由于master节点会认为该topology在发布中，所以会报topology已有效的异常。
    * 解决：
        * 使用storm killtopologyName命令先结束掉topology，再重新发布。
* Storm与Spring集成的问题
    * 问题：spring的Service类及ClassPathXmlApplicationContext初始化时报java.io.NotSerializableException的异常
    * 原因：由于service在定义时做了初始化，而storm的发布需要先序列化sport、bolt对象，将sport、bolt对象发布到worker节点上，然后在worker节点上反序列化，再运行，所以在sport、bolt初始化时需要创建的对象都需要实现java.io.Serializable接口。
    * 解决：
        * 如果是自己写的类，可以使其实现java.io.Serializable接口
        * 如果是第三方包的类，那么需要将对象的初始化放到sport、bolt的prepare方法中，该方法是在worker节点反序列化之后被调用，专门用来初始化对象的方法
  
# 注意
版本问题，高版本有可能会导致无法启动本地模式运行strom
      
# 参考资料
http://storm.apache.org/releases/current/index.html  
http://storm.apache.org/releases/current/storm-redis.html  
https://github.com/apache/storm/tree/master/external/storm-kafka  

# 问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* 邮件: (zhou5827297@163.com)
