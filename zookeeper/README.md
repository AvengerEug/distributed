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

|                 操作                 |         作用         |              示例              |                             含义                             | 注意事项                                                     |
| :----------------------------------: | :------------------: | :----------------------------: | :----------------------------------------------------------: | ------------------------------------------------------------ |
|              ls + 节点               |     查看指定节点     |              ls /              |                    查看根节点下有哪些节点                    |                                                              |
|       create + 节点 + 节点内容       |       创建节点       |   create /eug "eug节点内容"    |                    在根节点下创建eug节点                     | 节点内容必填，否则创建会创建失败(但不会报错，只不过是空的)，假设运行此命令: create /eug/test </br>那么执行ls /eug命令时，是看不到test节点的 |
|              get + 节点              |     取节点的信息     |            get /eug            |                      获取eug节点的信息                       |                                                              |
|            delete + 节点             |     删除指定节点     |          delete /eug           |                    删除根节点下的eug节点                     | 当前节点有子节点时，无法删除                                 |
|              rmr + 节点              | 删除指定节点及子节点 |            rmr /eug            |                                                              | 可以删除节点及其子节点                                       |
| setAcl + 节点 + schema:id:permission |  为指定节点添加权限  | setAcl /eug world:anyone:cdrwa | 为/eug节点的任何人添加创建、删除、读取、写、以及再次更改权限的权限 |                                                              |
|            getAcl + 节点             |  获取某个节点的权限  |          getAcl /eug           |                        获取/eug的权限                        |                                                              |

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

  ## 六、节点类型
  
  * 持久节点: 即是用create命令创建的节点, 即使服务器重启了，也还存在。命令如下：
  
    ```shell
    create /eug content
    ```
  
  * 临时节点: 临时节点的生命周期和客户端会话绑定。如果客户端的会话失效，那么这个节点就会自动被清除。(会话失效 != 连接断开)。 在临时节点下不能创建子节点, 命令如下: 
  
    ```
    create -e /eug content
    ```
  
    当使用 `quit`命令退出会话时，再重新连接，可以发现刚刚创建的临时节点不存在了
  
    但是如果按`ctrl + c`退出的话，再重新连接，使用`ls / `命令查看节点，可以发现临时节点还是存在的。然后我们再按`ctrl + c`退出，再重新连接，再次使用`ls / `命令查看节点，发现临时节点还是存在，但是过了一段时间后，再使用`ls / `命令查看节点，发现它已经不在了。出现这个问题的原因是因为`zookeeper`的`session`有关。过程是这样的: 在客户端连服务端时，在服务端会生成一个session，然后服务端会做一个倒计时操作，当倒计时结束后，服务端就认为这个session会过期。其中，在服务端做倒计时操作时，如果客户端再次发送了心跳给服务端，那么服务端就会重新倒计时。所以对于客户端而言，如果使用了`quit`退出了连接，客户端在关闭连接的时候，会告诉服务端要关闭session，待服务端关闭session后，服务端再删除那个临时节点。如果使用了`ctrl + c`退出了连接，但是客户端不会发给服务端任何操作，心跳也不会发送了，若客户端在服务端处理上个session倒计时期间重连了服务端，这个时候还是能看到临时节点，若超过了session的倒计时，那么临时节点将会被删掉。那么问题来了，服务端的倒计时默认到底有多久呢？(这个以后看源码得知)。  当session会话没有过期时，不管我们有多少个客户端连上服务端，都能看到临时节点
  
  *  持久顺序节点:  可以使用如下命令创建顺序节点:
  
    ```shell
    # 假设已经存在eug节点, 执行完如下命令后，会产生一个叫`/eug/seq_000000000`的节点，再执行同样的代码，会产生一个叫`/eug/seq_000000001`的节点，所以可知，我们在命令行给节点起的名字只是做前缀使用，并且后面的顺序节点会以递增的方式命名，但要注意，顺序节点的递增是根据父节点而言的，若父节点不同了，那么又会从0开始递增
    create -s /eug/seq_ 1
    ```
  
  * zookeeper对每个节点存储的内容最大上限为1m

## 七、Stat

* 在linux中, `stat + 文件名`可以列出文件的信息(大小、权限、创建时间、更改时间)。而在zookeeper中，stat可以查看某个节点的信息,eg:

  ```
  stat /eug
  
  --->
  
  [zk: localhost:2181(CONNECTED) 21] stat /eug
  cZxid = 0x4    -->   创建节点的事务ID   create zxid
  ctime = Sun Mar 22 11:15:22 CST 2020   --> 创建节点的时间 create time 
  mZxid = 0x4   --> 最后一次修改节点的事务ID
  mtime = Sun Mar 22 11:15:22 CST 2020  --> 最后一次修改的时间 
  pZxid = 0x4  --> 最后删除或修改子节点的事务ID，即操作子节点的事务id
  cversion = 0  --> 修改子节点的次数 change version
  dataVersion = 0  --> 数据的版本, 即节点中的内容, 使用set命令修改, eg: set /eug 2  将内容修改成2, 再查看节点信息，这里就会变成2
  aclVersion = 0  --> 权限的版本
  ephemeralOwner = 0x0  --> 如果当前节点是临时节点，这里就会有值，可以创建一个临时节点，然后用stat命令查看它
  dataLength = 15 --> 数据的长度
  numChildren = 0  --> 子节点的个数
  ```

## 八、Watch

* 有三个客户端: zookeeper原生客户端、ZkClient、curator

* `zookeeper的原生客户端`: 可以对zookeeper的一些操作添加监听器。zookeeper会在`客户端连接服务器、新增节点、删除节点、修改节点、修改子节点`时发布一个事件，具体事件类型如下: 

  ```java
  None (-1),
  NodeCreated (1),
  NodeDeleted (2),
  NodeDataChanged (3),
  NodeChildrenChanged (4);
  ```

  并通知所有的监听器。但监听器是`一致性的，执行完一遍后不会再被触发`。并且当客户端在给服务器发送心跳保证session不过期时，网络突然断了，而且等服务器的session过期后网络才好，此时客户端即使再重发心跳给服务端也没用了，因为服务端的session过期了，进而导致服务端对当前session的临时节点以及对临时节点的watcher都会无效

* `zkclient客户端`: 此客户端需要自己指定序列化对象，即要通过它来写数据、读数据，若读取其他序列化方式写入的数据，此客户端无法读取。但它解决了zookeeper原生客户端的监听器只能执行一次的问题.(需要添加zkclient-0.10.jar依赖包)

  ```java
  //TestZKClientReadData.java
  public class TestZKClientReadData {
  
      public static void main(String[] args) throws IOException {
          ZkClient zkClient = new ZkClient("192.168.111.146:2181", 10000, 10000, new SerializableSerializer());
  
          zkClient.subscribeDataChanges("/eug-zkclient", new IZkDataListener() {
              @Override
              public void handleDataChange(String s, Object o) throws Exception {
                  System.out.println("节点修改数据的监听器");
                  System.out.println("o : " + o);
              }
  
              @Override
              public void handleDataDeleted(String s) throws Exception {
                  System.out.println("节点被删除的监听器");
                  System.out.println("s: " + s);
              }
          });
  
          System.in.read();
      }
  }
  
  //TestZKClientWriteData.java
  public class TestZKClientWriteData {
  
      public static void main(String[] args) {
          ZkClient zkClient = new ZkClient("192.168.111.146:2181", 10000, 10000, new SerializableSerializer());
  
          //zkClient.createPersistent("/eug-zkclient", "eug-zkclient");
          zkClient.writeData("/eug-zkclient", "eug-zkclient node content changed");
      }
  }
  
  // 先启动监听器, 在重写节点的程序
  
  ```

* `curator客户端`: curator客户端是Netflix开源的一个客户端，里面的插件比较多，它的事件监听特性和原生zookeeper一致，不会被重复触发. 需要引入: `curator-client-2.12.0.jar, curator-framework-2.12.0.jar, curator-recipes-2.12.0.jar`三个依赖包

  ```java
  //TestCuratorClient.java
  public class TestCuratorClient {
  
      public static void main(String[] args) throws Exception {
          CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.168.111.146:2181", new RetryNTimes(2, 10000));
          curatorFramework.start();
  
          byte[] bytes = curatorFramework.getData().forPath("/eug");
          System.out.println(new String(bytes));
  
          curatorFramework.getData().usingWatcher(new CuratorWatcher() {
              @Override
              public void process(WatchedEvent watchedEvent) throws Exception {
                  System.out.println("对/eug节点的监听器");
              }
          }).forPath("/eug");
  
  
          System.in.read();
  
      }
  }
  ```

  

## 九、ACL

* zookeeper会在每个节点上添加一些权限信息

* 权限赋值

  1. 格式: schema:id:permission

     ```
     schema表示权限的策略、
     ID表示授权对象
     permission表示具体的权限
     
     schema可取: 
       1. world: 只有一个用户， 与id为anyone是配套的，如果写了world那么后面就是anyone， 比如: setAcl /eug world:anyone:c 表示/eug这个节点对于任何人而言是可以创建子节点的
       2. anyone: 所有人
       3. ip: 指定ip
       4. auth: 已添加认证的用户认证
       5. digest: 使用"用户名:密码"方式认证
     =============
     id可取:
       1. world: 只有一个id时，表示anyone
       2. ip: 通常是一个ip地址或者地址段，比如192.168.0.110或者192.168.0.1/24
       3. auth: 用户名
       4. digest: 自定义通常是"username:BASE64(SHA-1(username:password))"
     ============
     权限可取:
       1. CREATE, 简写c，表示创建子节点
       2. DELETE, 简写d，表示删除子节点
       3. READ, 简写r，可以读取节点信息及显示子节点列表
       4. WRITE, 简写w, 看设置节点数据
       5. ADMIN, 简写a, 可以设置节点控制列表
     
     ```

  2. 案例

     1. 为/eug节点添加任何人都可以写的操作

        ```
        # 后面加了一个a是方便后续重新为/eug节点赋值权限
        setAcl /eug world:anyone:wa 
        ```

     2. 为/eug节点添加指定ip才能操作

        ```
        setAcl /eug ip:192.168.0.113:wa
        ```

     3. 为/eug节点添加只有指定用户才能进行写操作

        ```
        # 先新增一个用户
        addauth digest zhangsan:123
        # 为节点添加指定权限
        setAcl /eug auth:zhangsan:123:ra
        
        # 当其他客户端要读取/eug节点的话要怎么办呢？
        # 首先先做一个类似认证的操作, 如下
        addauth digest zhangsan:123
        # 再查看节点信息
        get /eug
        ```

        

* 获取权限信息

  1. 案例

     ```
     [zk: localhost:2181(CONNECTED) 22] getAcl /eug
     'world,'anyone
     : cdrwa
     
     
     -> cdrw 表示create、delete、read、write、admin
     ```

* 特点

  ```
  1. zookeeper的权限控制是基于节点来的，若对父节点有cdrw权限，但这不意味着对子节点也有同样的权限。所以还需要对子节点重新赋值权限才行
  2. 子节点不会继承父节点的权限，他们都是独立的。所以客户端有可能无法访问父节点，但他有可能能访问子节点
  3. 每个节点有多个权限控制方案和多个权限
  ```


## 十、Zookeeper有何应用场景

* 要知道它有什么应用场景，还是要跟它的初衷说起。之前说到, zookeeper的出现是为了分布式系统CAP定理的一致性问题，而且它自带广播机制，于是我们产生了一些应用场景。如下

  1. 分布式锁

     ```
     在秒杀系统中, 多个用户抢占数量为1的商品，我们采取锁表的机制是不可取的。因为系统会变得非常慢。这个时候我们可以借助zookeeper，当用户下单时，因为集群的原因，可能会有不同的用户下单的请求到达不同的服务器上。这个时候，我们可以将在下单操作执行前先跟zookeeper交互一下(zookeeper肯定也是集群的), 于是每个服务器跟zookeeper的交互可能会在不同的zookeeper实例中。那这个时候，哪个服务器在zookeeper中成功的创建了一个节点，那就说明这个服务器有下单的权限，进而进行下单操作。在这个创建节点的过程中，就是分布式锁的精髓
     ```
     ![秒杀库存为1的商品.png](https://github.com/AvengerEug/distributed/blob/develop/zookeeper/秒杀库存为1的商品.png)
     
  2. 分布式系统配置中心
  
     ```
     在分布式系统中，不同的应用程序或者相同的应用程序会部署在不同的机器上。若配置发生改变，我们需要一个个的去更新代码，重启服务器。就算集成了CI，把同步代码部署的步骤给自动化了，但是却还需要重启服务器。我们如何做到在不重启服务器的情况下，完成同步配置呢？最好的方式就是我们修改配置后，将修改的配置通知给各个服务器，让程序更新已加载到内存中的配置。 因为zookeeper有watch机制, 所以我们可以将分布式系统的所有机器都连接上zookeeper, 并对自己感兴趣的节点添加监听器，当我们把更新后的配置存储到它们监听的节点上时，zookeeper会发布一个修改节点的事件(包括修改后的数据)，这样，我们就可以在获取到更新后的配置，并加载到内存中了
     ```
     ![分布式配置中心.png](https://github.com/AvengerEug/distributed/blob/develop/zookeeper/分布式配置中心.png)
  
  3. 分布式队列
  
     ```
     因为zookeeper中存在临时顺序节点的特性，所以我们可以将某一个父节点(比如/distributed_que)下面的临时顺序节点当做队列中的元素。当消费者发现/distributed_que节点下面无临时顺序节点时则阻塞，有则拿最小的节点消费(保证了FIFO)，最后删除(或者回话过期后自动删除).生产者生产了新的东西时，往/distributed_que添加一个节点。
     ```
     ![分布式队列.png](https://github.com/AvengerEug/distributed/blob/develop/zookeeper/分布式队列.png)
  
  4. 分布式命名服务
  
     ```
     同上也是利用了zookeeper的顺序节点特性以及强一致性。这里就不阐述了
     ```
  
     


​     

​     