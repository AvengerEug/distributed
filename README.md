# 分布式系统相关知识点

## 一、背景

* 为什么需要分布式系统？

  ```markdown
  在传统的单应用(架构)电商项目中，我们通常会将所有的业务放在一个系统。但这样会出现一个问题: 若系统的登录api出现了问题(假设因为某些原因出现了死循环的情况)，此时若有人故意对登录api进行压测，导致所有的线程都在死循环中无法做出响应。若api中有对应用内存的占用情况，那么随着时间的推移，应用的使用内存会超出启动项目时设置的jvm运行内存最终导致程序未响应(cpu可能会将对应的进程杀掉)。此时，项目则直接挂了，后续若用户想浏览商品，发现页面无法响应出来。这就是传统单应用项目的缺点。
  
  为了解决这样的问题，我们可以根据故障原因着手。上面说了，整个系统是因为登录api出了问题而影响到了用户浏览商品的功能。那么我们为什么不能将用户浏览商品的功能和登录api分别运行呢？这样的话，就算登录api挂了，我的浏览商品功能依然能工作，所以我们可以将用户的模块作为一个程序运行，商品的模块作为一个程序运行。于是"垂直架构"的微服务架构就这样出来了。但随着模块的集群数量足以保证项目的正常运行，但压力却在一个数据库中，所以到最后会衍生成模块使用自己的数据库的情况
  
  可是有了微服务，我们还会遇到很多问题: 
  Q1: 若有些模块需要依赖呢？ 假设下单流程中，我们需要依赖商品模块(查看是否有库存，下单成功后还要减库存)、用户模块(获取用户的具体信息)、其他模块(比如: 发短信、邮件)等等，此时我们要怎么解决呢？`通信`
  A1: 可以使用RPC(remote proceduce call)思想进行设计
  
  Q2: 在Q1基础上，下单的整个流程依赖于商品模块，且订单模块的新增订单和商品模块的减少库存是具有事务性的(但它们两个模块使用的是不同的库)。两者要么同时成立、要么同时不成立。 所以此时我们出现了`分布式事务问题`
  A2: 可以使用zookeeper解决
  
  Q3: 在Q1的基础上，若在获取用户具体信息的过程中，用户模块迟迟未响应。在某个活动中，下单的用户突然猛增，然后下单的逻辑卡在了用户模块下，最终导致下单请求迟迟未响应或者直接订单模块也挂掉了，导致整个微服务架构使用不起来，`雪崩`。
  A3: 可以使用分布式架构的熔断机制，做降级、请求超时等处理
  
  Q4: 在Q1的基础上，我们要如何在不同的微服务中确认当前的用户是谁？即`分布式系统中session的问题`
  A4: 可以使用jwt(json web token), 或者使用redis缓存, session复制等等
  
  Q5: 在Q1的基础上，每个微服务可能会有自己的配置，当服务集群部署时，若我们需要修改一个配置，我们不可能会修改配置，打成jar/war包并重新部署(万一集群非常多呢).
  A5: apollo等等
  
  Q6: 在Q1基础上，若有多个用户同时下一个库存只有一个的商品的订单，且订单模块和商品模块使用的不同的库，我们要如何保证第一个用户下了订单其他的就下不了了呢？ `分布式锁`
  A6: 当然可以采取锁商品表，可以锁表的话那代价就太大了。可以使用zookeeper的分布式锁解决，在第一个用户拿到锁时在去和商品模块进行交互
  
  Q7: 在Q1的基础上，若我们需要将整个下单流程的日志给记录下来，我们要如何处理？`分布式系统的日志`
  A7: 可以使用ELK, 或者zipkin + elasticsearch
  
  Q8: 在Q1的基础上，假设用户下完单后没有及时付款，那么在规定的时间内未付款的话，此订单将被取消。这种case肯定是要用job来执行的，可是如果把job写在订单模块时，当订单模块有多个集群，那么此job就会跑多次，很浪费.`分布式系统job`
  A8: 可以添加一个job调度模块，为job调取模块至部署一个实例，然后通过RPC调用对应的服务
  
  Q9: 对于客户端来说，需要记录后台各模块的ip地址和端口，对于前端而言，工作复杂且繁重。`分布式系统网关`
  A9: 可以构建一个服务作为网关，由网关来做内部转发和过滤以及负载均衡工作
  
  Q10: 假设用户在发布商品的case中，需要上传图片，我们需要如何保证集群环境下都能使用到此图片
  A10: 可以使用aliyun的oss或者自己搭建NFS文件共享服务器。大型项目推荐aliyun的oss，因为自己搭建NFS文件共享服务器，需要手动的在每台服务器上挂在共享目录
  
  ```

## 二、概念

```txt
从进程的角度看，两个程序分别运行在两台主机的进程上，它们一起共同完成一个功能。这两个程序组成的系统就叫做分布式系统。
从实现功能角度看:
  1. 若两个程序实现的功能是一模一样的，此时它们交作集群
  2. 若两个程序实现的功能不一致，但是为了完成某一个业务，此时它们叫微服务
```

## 三、微服务类别

* 上面说了，微服务就是两个程序实现不同的功能，但是为了某一个业务而存在的。但是这样的微服务粒度比较大。通常我们可以以如下几种情况来切分微服务:
  1. 业务: 比如下订单逻辑，我们需要新增订单，但是新增订单的同时我们需要查询可使用的库存等等。所以我们通常会将订单和库存抽出为一个微服务。
  2. 数据库: 其实与1所述的差不多，使用的数据库不同肯定就是业务不同
  3. 接口: 此粒度最小，我们可以以接口为度架构微服务，比如用户模块中, 登录api和修改密码api可以分别作为一个微服务。当然使用这种架构的话，项目的架构就会变成非常复杂。

## 四、CAP理论

* CAP原则又称CAP定理，指的是在一个分布式系统中。一致性(Consistency)、可用性(Availability)、分布容错性
  (Partition tolerance). 这三个要素最多只能同时实现两个，不可能三者同时实现

* C(Consistency): 若分布式部署时，有个节点有集群操作。我在集群节点A中对一个内存中的数据做了操作。我想在
  节点B中获取到修改后的数据。此时我必须将在节点A修改数据的操作同步到节点B中。而在同步过程中为了保证不出错，
  我必须暂停对节点B中针对此数据的请求，待同步完成后再开放请求。

* A(Availability): 只要用户发送请求就必须响应数据，

* P(Partition tolerance): 分布式部署时，多台服务器每个模块可能部署在不同机器上，而这些机器可能分布在不
  同的区域(比如机器A在中国北京，机器B在美国洛杉矶)，而他们要通行，难免避免不了网络问题而导致的通信失败。

* C(一致性)和A(可用性)的矛盾:  如要保证一致性，那么必然会存在所有服务暂停使用的情况。这与可用性矛盾了。所以一般的分布式基本上都是实现`CA` 或`CP`。 具体使用哪种架构，试业务而定。假设与钱有关的，那肯定是要保证一致性, 所以会采取`CP`架构。假设在秒杀活动是，为了能保证系统可用，则采取`CP`架构

## 五、BASE理论

* BASE是指基本可用(Basically Availability)、软状态(Soft State)、最终一致性(Eventual Consistency).
* `软状态`可以理解为中间状态，即集群的两个redis, A中对数据进行了修改，但是B中的数据还没修改
* `最终一致性`表示集群的两个redis, A中对数据进行了修改，但是B中的数据还没来得及修改就被用户读取了，但是这重要，用户多读几次就能读到更新后的数据了

## 六、分布式系统主要解决的问题

* 分布式各系统中间都需要进行网络通信，所以本来在单一架构中能保证的一致性升级为分布式之后就难以保证，而zookeeper就是为了解决分布式系统主要解决`一致性问题`，再加上zookeeper的其他特性还可以解决分布式锁、分布式事务等问题

## 七、zookeeper

* 链接: [https://github.com/AvengerEug/distributed/tree/develop/zookeeper](https://github.com/AvengerEug/distributed/tree/develop/zookeeper)

## 八、Dubbo
* 链接: [https://github.com/AvengerEug/distributed/tree/develop/dubbo](https://github.com/AvengerEug/distributed/tree/develop/dubbo)
