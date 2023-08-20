# BigDataflow

BigDataflow is a distributed interprocedural dataflow analysis framework for analyzing the program of millions lines of code in minutes.

## Getting started

As the distributed analysis framework is implemented atop the general distributed graph processing platform (i.e., Apache Giraph), ensure that you have already installed Java and Hadoop before employing BigDataflow on your machines.

Versions for use with BigDataflow:

-  jdk version >= 1.8.0.
-  Apache Hadoop >= 2.7.2 , or EMR version >= 3.14.0

Apache Hadoop has three different installation modes: `Standalone`, `Pseudo-distributed`, and `Fully Distributed`.

If you plan to run BigDataflow on the cloud (i.e. in `Fully Distributed` mode), you can just skip the ***Installing Hadoop in Local Mode*** part and directly employ BigDataflow with no need for installing `hadoop`. Otherwise, you have to prepare a Hadoop environment on your local machine/cluster as follows.

### Installing Hadoop in Local Mode*

1, Download the Hadoop file.

```bash
$ wget https://archive.apache.org/dist/hadoop/common/hadoop-2.7.2/
$ tar -xzf hadoop-2.7.2.tar.gz
$ cd hadoop-2.7.2
```
2, vi `/etc/hosts`

```bash
127.0.0.1       localhost
```

3, vi `etc/hadoop/core-site.xml`

**Remember to change the Hadoop path according to your hadoop-2.7.2 installation directory**

```xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:8000</value>
  </property>
</configuration>
```

4, vi `etc/hadoop/hdfs-site.xml`

```xml
<configuration>
  <property>
    <name>dfs.namenode.name.dir</name>
    <value>/path/to/your/hadoop-2.7.2/tmp/dfs/namenode</value>
  </property>

  <property>
    <name>dfs.datanode.data.dir</name>
    <value>/path/to/your/hadoop-2.7.2/tmp/dfs/datanode</value>
  </property>

  <property>
    <name>dfs.replication</name>
    <value>3</value>
  </property>
</configuration>
```

5, vi `etc/hadoop/yarn-site.xml`

```xml
<configuration>
	<!-- Site specific YARN configuration properties -->
    <property>
            <name>yarn.acl.enable</name>
            <value>0</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>localhost</value>
    </property>
    <property>
        <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
    </property>
    <property>
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>28672</value>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>28672</value>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-vcores</name>
        <value>32</value>
    </property>
</configuration>
```

6, vi `etc/hadoop/mapred-site.xml`

```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapreduce.application.classpath</name>
        <value>/path/to/your/hadoop-2.7.2/share/hadoop/giraph/*:/path/to/your/hadoop-2.7.2/share/hadoop/mapreduce/*:/path/to/your/hadoop-2.7.2/share/hadoop/mapreduce/lib/*</value>
    </property>
    <property>
        <name>mapreduce.job.counters.limit</name>
        <value>20000</value>
    </property>
    <property>
        <name>mapred.tasktracker.map.tasks.maximum</name>
        <value>4</value>
    </property>
    <property>
        <name>mapred.map.tasks</name>
        <value>4</value>
    </property>
</configuration>
```

7, Format NameNode

```bash
$ cd hadoop-2.5.1/bin && hdfs namenode -format
```

8, Start HDFS and YARN

```bash
$ cd ../sbin/start-all.sh
```

### Implementing the APIs of BigDataflow according to the specific analysis

1, Download the entire BigDataFlow code.

```bash
$ git clone git@github.com:BigDataflow-system/BigDataflow.git
```

2, Interfaces under the `BigDataflow/analysis/src/main/java/data`

`Fact`: Fact of dataflow analysis. 

`Msg`:   Message of dataflow analysis, consisting of target vertex's id and the outgoing fact of the current vertex.

`Stmt`: Statement at each vertex of CFG.

`StmtList`: Statement list at each vertex of CFG.

`Tool` : Tool of dataflow analysis, consisting of merging, transferring, and propagating operations.

`VertexValue`: Vertex attribute of each vertex in CFG, consisting of its statement list and fact.

3, Interfaces under the `BigDataflow/analysis/src/main/java/analysis`

`Analysis` : The implementation of the optimized distributed worklist algorithm. Users can instantiate the `Msg`, `Fact`, `Tool` by overriding its functions according to their client analysis.

`MasterBroadcast`: Executed in the master node at the beginning of each superstep, used to broadcast the entries to CFG.

4, After implementing the interfaces, compile the code to produce the jar

```bash
# compile the client analysis
$ cd Analysis
$ mvn clean package
$ cd ..

# prepare the jar of giraph under your own jar_directoty
$ cp emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar jar_directoty/
$ cd jar_directoty/
$ mv emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar

# add your analysis class files into the jar
$ cd target/classes/
$ cp -r analysis/ data/ your_analysis/ your_data/ jar_directoty/
$ cd jar_directoty/
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./analysis/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./data/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./your_analysis/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./your_data/*.class
```

5, Put your analysis jar into your corresponding Hadoop directory

```bash
# jar directory at local
$ cp jar_directoty/giraph-examples-1.4.0-SNAPSHOT-shaded.jar /path/to/your/hadoop-2.7.2/share/hadoop/giraph

# jar directory on the cloud
$ cp jar_directoty/giraph-examples-1.4.0-SNAPSHOT-shaded.jar /opt/apps/extra-jars
```

### Usage Examples on the Cloud

**Running Alias Analysis**

1, vi `alias_analysis_conf` for each CFG of a program

If the BigDataflow is run locally, the content of `alias_analysis_conf.CFG` for each CFG is as follows:

```bash
hdfs://localhost:8000/alias_bench/CFG/entry
hdfs://localhost:8000/alias_bench/CFG/singleton
hdfs://localhost:8000/grammar
```

Else, set the content of `alias_analysis_conf.CFG` as follows if BigDataflow is run on the [EMR](https://www.alibabacloud.com/product/emapreduce) cluster:

```bash
# the `ClusterID` is automatically assigned by the EMR service
hdfs://emr-header-1.cluster-ClusterID:9000/alias_bench/CFG/entry
hdfs://emr-header-1.cluster-ClusterID:9000/alias_bench/CFG/singleton
hdfs://emr-header-1.cluster-ClusterID:9000/grammar
```

2, Put `alias_analysis_conf.CFG` and `grammar file` on HDFS dir

```bash
$ ./hadoop fs -put alias_analysis_conf.CFG /client
$ ./hadoop fs -put /path/to/AliasAnalysis/grammar /
```

3, Prepare the CFG Files for Alias Analysis

```bash
# prebuild HDFS dir for alias analysis
$ hadoop fs -mkdir -p /alias_bench/CFG

# put alias CFG Files on HDFS  
$ hadoop fs -put /alias_bench/CFG/var_singleton_info.txt /alias_graphs/CFG && \
  hadoop fs -put /alias_bench/CFG/new_entry.txt /alias_graphs/CFG  && \
  hadoop fs -put /alias_bench/CFG/final /alias_graphs/CFG  && \
  hadoop fs -put /alias_bench/CFG/id_stmt_info.txt /alias_graphs/CFG
  
$ hadoop fs -mv /alias_graphs/CFG/var_singleton_info.txt /alias_graphs/CFG/singleton && \
  hadoop fs -mv /alias_graphs/CFG/new_entry.txt /alias_graphs/CFG/entry   && \
  hadoop fs -mv /alias_graphs/CFG/id_stmt_info.txt /alias_graphs/CFG/id_stmt_info
```

4, Run Alias Analysis

And there are also some parameters used to fit your own analysis and running environment:

`-vif` : the vertex input format of specific dataflow analysis

`-vip` : the vertex input path on HDFS of specific dataflow analysis

`-vof` : the vertex output format of specific dataflow analysis

`-op`   : the results' output path of specific dataflow analysis

`-eif` : the edge input format of specific dataflow analysis

`-eip`:  the edge input path of specific dataflow analysis

`-wc`  : WorkerContext class, used by the workers to access the global data

`-mc`  : MasterBroadcast Class, used by the master to broadcast the entries of the  CFG

`-w `    : number of workers to use for computation

`-ca `  : custom arguments include maximum milliseconds to wait before giving up waiting for the workers and heap memory limits for the workers. For example, if each physical core of one node can not exceed 10GB, then the heap memory is set to 10×1024 = 10240MB.

Then, by executing the commands below, the alias analysis is launched.

```bash
$ hadoop fs -mv /client/alias_analysis_conf.CFG /client/analysis_conf

i=XX # i is set according to the predicted number of workers
startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
  org.apache.giraph.GiraphRunner alias_analysis.AliasAnalysis \
  -vif alias_analysis.AliasVertexInputFormat \
  -vip /alias_graphs/CFG/id_stmt_info \
  -vof alias_analysis.AliasVertexOutputFormat \
  -op /alias_res/CFG_W"$i" \
  -eif alias_analysis.AliasEdgeInputFormat \
  -eip /alias_graphs/CFG/final \
  -wc alias_analysis.MyWorkerContext \
  -mc analysis.MasterBroadcast \
  -w "$j" \
  -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
  > /alias_CFG_W"$i"_result.txt 2>&1
then
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /alias_CFG_W"$i"_result.txt 2>&1
  echo "$enddate" >> /alias_CFG_W"$i"_result.txt 2>&1
  echo "alias CFG------Success"
else
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /alias_CFG_W"$i"_result.txt 2>&1
  echo "$enddate" >> /alias_CFG_W"$i"_result.txt 2>&1
  echo "alias CFG------Fail"
fi

$ hadoop fs -mv /client/analysis_conf /client/alias_analysis_conf.CFG 
```

5, Check the results

You can see the results in the file `cache_CFG.res` after executing the following command.

```bash
# supposed i=100
$ hadoop fs -cat /alias_res/CFG_W100/p* > cache_CFG.res
```

**Running Cache Analysis**

1, vi `cache_analysis_conf`

Similar to alias analysis, the content of `cache_analysis_conf` is as follows.

```bash
# run BigDataflow at local
hdfs://localhost:8000/cache_entrys/entry

# run BigDataflow on the EMR
hdfs://emr-header-1.cluster-ClusterID:9000/cache_entrys/entry
```

2, Prepare the CFG Files for Cache Analysis

```bash
# prebuild HDFS dir for cache analysis
$ hadoop fs -mkdir -p /cache_bench/CFG

# put cache CFG Files on HDFS 
$ hadoop fs -put /cache_bench/CFG/entry.txt /cache_entrys
$ hadoop fs -mv /cache_entrys/entry.txt /cache_entrys/CFG.entry

$ hadoop fs -put /cache_bench/CFG/new-final.txt /cache_graphs/CFG && \
$ hadoop fs -put /cache_bench/CFG/new-nodes.txt /cache_graphs/CFG
$ hadoop fs -mv /cache_graphs/CFG/new-nodes.txt /cache_graphs/CFG/new_nodes && \
$ hadoop fs -mv /cache_graphs/CFG/new-final.txt /cache_graphs/CFG/final
```

3, Run Cache Analysis

```bash
$ hadoop fs -mv /client/cache_analysis_conf /client/analysis_conf
$ hadoop fs -mv /cache_entrys/CFG.entry /cache_entrys/entry

i=XX # i is set according to the predicted number of workers
startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

$ if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
  org.apache.giraph.GiraphRunner cache_analysis.CacheAnalysis \
  -vif cache_analysis.CacheVertexInputFormat \
  -vip /cache_graphs/CFG/new_nodes \
  -vof cache_analysis.CacheVertexOutputFormat \
  -op /cache_res/CFG_W"$i" \
  -eif cache_analysis.CacheEdgeInputFormat \
  -eip /cache_graphs/CFG/final \
  -mc analysis.MasterBroadcast \
  -w "$i" \
  -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
  > /cache_CFG_W"$i"_result.txt 2>&1
then
   enddate=$(date "+%Y-%m-%d %H:%M:%S")
   echo "$enddate"
   echo "$startdate" >> /cache_CFG_W"$i"_result.txt 2>&1
   echo "$enddate" >> /cache_CFG_W"$i"_result.txt 2>&1
   echo "CFG------Success"
else
   enddate=$(date "+%Y-%m-%d %H:%M:%S")
   echo "$enddate"
   echo "$startdate" >> /cache_CFG_W"$i"_result.txt 2>&1
   echo "$enddate" >> /cache_CFG_W"$i"_result.txt 2>&1
   echo "cache CFG------Fail"
fi

$ hadoop fs -mv /cache_entrys/entry /cache_entrys/CFG.entry 
$ hadoop fs -mv /client/analysis_conf /client/cache_analysis_conf
```
