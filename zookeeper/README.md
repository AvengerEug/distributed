# zookeeper相关知识点

## 一、配置文件参数解释

```properties
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/tmp/zookeeper #zookeeper有两个东西需要存储， 一个是快照(zookeeper会将数据存储在内存，但是会在某些事件点将存储的内存打成一个快照存在磁盘上)，另一个是事务日志。 此目录一般是存储快照的
clientPort=2181
```

## 二、操作

|           操作           |     作用     |           示例            |          含义          | 注意事项                                                     |
| :----------------------: | :----------: | :-----------------------: | :--------------------: | ------------------------------------------------------------ |
|        ls + 节点         | 查看指定节点 |           ls /            | 查看根节点下有哪些节点 |                                                              |
| create + 节点 + 节点内容 |   创建节点   | create /eug "eug节点内容" | 在根节点下创建eug节点  | 节点内容必填，否则创建会创建失败(但不会报错，只不过是空的)，假设运行此命令: create /eug/test </br>那么执行ls /eug命令时，是看不到test节点的 |
|        get + 节点        | 取节点的信息 |         get /eug          |   获取eug节点的信息    |                                                              |
|      delete + 节点       | 删除指定节点 |        delete /eug        | 删除根节点下的eug节点  |                                                              |

## 三、搭建zookeeper集群

* zookeeper的集群会有三个角色, Leader、Follower、Observer。

* zookeeper集群配置文件

  ```properties
  tickTime=2000
  initLimit=10
  syncLimit=5
  dataDir=/tmp/zookeeper 
  clientPort=2181
  peerType=observer  #若启动的节点不是observer的话则不需要此配置
  
  server.1=localhost:2887:3887  # 2887是用来同步的(新集群新增了一个实例，需要同步信息). 3887是用来选举的(当leader挂掉后需要其他的实例进行选举，选出新的leader)
  server.2=localhost:2888:3888
  server.3=localhost:2889:3889
  server.4=localhost:2890:3890:observer  # 后面的observer表示此节点是一个observer角色
  ```

* 目标: 启动一个leader，两个follower，一个observer

* 背景: 将zookeeper解压缩至`/root/distributed`文件夹下，并将解压后的文件夹重命名为`zookeeper`, 且为zookeeper bin目录下的脚本配置到了环境变量

### 3.1 启动非observer的zoo1实例

1. 构建如下内容的配置文件(在zookeeper的conf目录下创建zoo1.cfg文件)

   ```properties
   tickTime=2000
   initLimit=10
   syncLimit=5
   dataDir=/root/distributed/zoo1
   clientPort=2181
   
   server.1=localhost:2887:3887
   server.2=localhost:2888:3888
   server.3=localhost:2889:3889
   server.4=localhost:2890:3890:observer
   ```

2. 在`/root/distributed/zoo1`文件夹中执行如下命令

   ```shell
   echo "1" > myid
   ```

3. 执行如下命令以`zoo1.cfg`配置文件启动zookeeper

   ```shell
   zkServer.sh start /root/distributed/zookeeper/conf/zoo1.cfg
   ```

4. 使用如下命令查看启动状态

   ```shell
   zkServer.sh status /root/distributed/zookeeper/conf/zoo1.cfg
   ```

   会有如下输出：

   ```shell
   ZooKeeper JMX enabled by default
   Using config: /root/distributed/zookeeper/conf/zoo1.cfg
   Error contacting service. It is probably not running.
   ```

   可以看出, 日志中提示了，不能连接服务，可能没有运行(因为在配置文件中配置了三个非observer角色的节点，在zookeeper中有一个叫`过半协议`来决定集群是否可用，因为目前只有一个实例, 而 **1 < 3/1**, 所以集群现在是不可用的)

5. 使用如下命令连接实例

   ```shell
   zkCli.sh -server localhost:2181
   ```

   可能会出现连接上zookeeper实例的界面，但是一会儿界面就变了，页面一直在重复输出如下信息

   ```verilog
   2020-03-22 15:24:29,034 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@879] - Socket connection established to localhost/0:0:0:0:0:0:0:1:2181, initiating session
   2020-03-22 15:24:29,039 [myid:] - INFO  [main-SendThread(localhost:2181):ClientCnxn$SendThread@1158] - Unable to read additional data from server sessionid 0x0, likely server has closed socket, closing socket connection and attempting reconnect
   ```

### 3.2 启动非observer的zoo2实例

1. 构建如下内容的配置文件(在zookeeper的conf目录下创建zoo1.cfg文件)

   ```properties
   tickTime=2000
   initLimit=10
   syncLimit=5
   dataDir=/root/distributed/zoo2
   clientPort=2182
   
   server.1=localhost:2887:3887
   server.2=localhost:2888:3888
   server.3=localhost:2889:3889
   server.4=localhost:2890:3890:observer
   ```

2. 在`/root/distributed/zoo2`文件夹中执行如下命令

   ```shell
   echo "2" > myid
   ```

3. 执行如下命令以`zoo2.cfg`配置文件启动zookeeper

   ```shell
   zkServer.sh start /root/distributed/zookeeper/conf/zoo2.cfg
   ```

4. 使用如下命令查看启动状态

   ```shell
   zkServer.sh status /root/distributed/zookeeper/conf/zoo2.cfg
   ```

   会有如下日志输出:

   ```verilog
   ZooKeeper JMX enabled by default
   Using config: /root/distributed/zookeeper/conf/zoo2.cfg
   Mode: leader
   ```

   可以发现，它的 Mode为leader

5. 使用如下命令连接实例

   ```shell
   zkCli.sh -server localhost:2182
   ```

   使用如上命令发现已经连接上2182的实例了，

   我们再执行如下命令连接下2181实例:

   ```shell
   zkCli.sh -server localhost:2181
   ```

   然后我们也发现2181实例也连接上了，同样的，我们查看2181实例的状态，发现它的Mode为leader

   通过启动2182实例，我们发现zookeeper集群要启动起来，必须要所有非observer实例进行过半选举，只有过半选举通过后，整个集群才能正常使用，比如此，配置的非observer的节点为3，而已经启动的非observer为2，已经大于1.5了。于是他们要选出一方作为leader，有了leader和follower后，集群就能正常启动

### 3.3 启动非observer的zoo3实例

* 与如上操作相同

### 3.4 启动observer的zoo

* 与如上操作类似，但需要注意，需要在配置文件中新增`peerType=observer`的配置

### 3.5  注意事项

* zookeeper集群是否能够启动，要看配置文件配置的非observer节点个数以及启动的非observer实例的个数，若过半了，则可以开始进行选举leader节点，选出leader节点后，集群即可使用。我们可以做实验，当配置4个非observer节点时，只有启动了3个实例，zookeeper集群才能被使用
* zookeeper的follower节点增多会影响写操作，因为所有的follower需要进行对写操作进行投票，若节点很多则在投票时会消费大量时间。若想提高读操作的性能，则可以扩容observer的节点。

## 四、zookeeper集群案例

### 4.1 集群案例1之请求client请求到leader角色的节点

* 在这种案例中，假设有4个集群，分别为一个leader、两个follower、一个observer。当client的`在根目录下添加/eug节点的`请求到达leader时，此时leader会`提议`，将这个操作告诉两个follower，然后由follower做出决定，若follower认可 则发送`ack`(表示支持), 否则不发送，而leader默认支持，最后leader会进行统计，若follower发送的`ack + 1`(+1的原因是leader默认支持)超过follower的数量时，则表示通过(假设一个follower发送了ack另外一个未发送, 那么 `ack + 1 = 2, 2 > 3/1则表示此操作通过`)，通过后leader会发送`commit`来告知follower和observer可以执行添加`/eug`节点的操作了。于是整个集群的实例就会添加`/eug`节点

### 4.2 集群案例2之请求client请求到follower/或observer角色的节点

* 因为follower是leader的跟随者, observer是一个监听者，无法做出决策，所以它会将请求转发给leader，让leader来处理，这样的话又回到`3.1`的情况下了

## 五、zookeeper的CAP协议

* zookeeper的CAP协议要根据架构以及出现问题节点的类型而定，假设有如下情况: 一个leader节点，两个follower节点，一个observer节点。

* 下面我们先分析下根据上述假设我们能得到如下信息：

  ```txt
  1. 非observer节点有3个，也就是说只要有2个非observer节点在运行，那么集群就不会挂
  ```

* 下面我们将按照不同实例的运行情况来分析CAP协议：

  ```txt
  情况一: 假设我们将leader节点停掉，会发生什么，此时的协议是哪种？
   => 由上述我们得知的消息可知, 只要有两个非observer节点在运行，集群就不会挂，所以此时存在AP协议，因为集群还是可用的。但是其中会多做一个操作，就是要在剩下的两个非observer节点中重选选出leader
  
  情况二: 假设我们将observer节点停掉，会发生什么？此时的协议是哪种?
   => 由上述我们得知的消息可知, 只要有两个非observer节点在运行，集群就不会挂. 即使停掉了observer操作，集群的可用性不会受任何影响。此时的协议是AP
   
  情况三: 假设我们将observer节点停掉后重启，会发生什么，此时的协议是哪种?
   => observer节点重新启动后，会去同步其他节点的数据，此时的协议是CP，保证数据的一致性
  
  情况四: 假设我们将observer节点和一台非observer节点停掉，会发生什么？此时的协议是什么？
   => 即使停了两台，但是非observer的运行数量2 是大于1.5的，所以依然是可以运行的。此时的协议是AP
  
  情况五：假设我们将两台非observer节点停掉，会发生什么？此时的协议是什么？
   => 停了两台非observer节点，那么实际上运行的非observer节点数量为1，小于1.5。 此时整个集群将会挂掉，包括observer都会挂掉。
  ```

  

