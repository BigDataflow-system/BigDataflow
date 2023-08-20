## Getting started

Before install hadoop and giraph on your machines, make sure you have already installed java and jdk version is >= jdk1.8.0.

### Prepare hadoop enviroment on cluster

1. Download hadoop file.

```bash
$ tar -xzf hadoop-2.7.2.tar.gz
$ cd hadoop-2.7.2
```
2. vi etc/hadoop/core-site.xml

   **Remember to change the hadoop path according to your hadoop-2.7.2 installation directory**

```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://master:9000</value>
    </property>
</configuration>
```


3. vi etc/hadoop/hdfs-site.xml

```xml
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/path/to/your/hadoop-2.7.2/tmp/dfs/namenode</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>/path/to/your/hadoop-2.7.2/tmp/dfs/datanode</value>
    </property>
</configuration>
```


4. vi etc/hadoop/yarn-site.xml

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
        <value>master</value>
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

5. vi etc/hadoop/mapred-site.xml

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

6. Format NameNode

```bash
$ cd hadoop-2.5.1/bin && hdfs namenode -format
```

7. Start HDFS and YARN

```bash
$ cd ../sbin/start-all.sh
```



### Install  Giraph enviroment

1. Download Giraph

```bash
$ git clone https://github.com/anonymous
```

2. Modify pom.xml

```xml
-        <munge.symbols>PURE_YARN,STATIC_SASL_SYMBOL</munge.symbols>
+        <munge.symbols>PURE_YARN</munge.symbols>
```

3. Compile Giraph

```bash
$ mvn   -Phadoop_yarn -Dhadoop.version=2.5.1  clean package  -DskipTests
```

4. Prebuild  Mapreduce classpath for adding Giraph jar to it

```bash
$ mkdir -p /path/to/your/hadoop-2.5.1/share/hadoop/giraph
```



### Compiling and Running A Analysis

**Also, remember to change the AAnalysis path according to your Giraph installation directory**

1. Download the entire code from`https://github.com/anonymous`


2. Compile the A Analysis jar by maven.

```bash
$ cd /path/to/your/hadoop-2.5.1/share/hadoop/giraph  &&  rm -r *
$ cd /path/to/your/Giraph/A_Analysis && mvn clean package
```

3. Add A Analysis jar classes into Giraph Jar

```bash
# copy AAnalysis jar classes to Mapreduce classpath
$ cd A_Analysis/target/classes 
$ cp -r analysis /path/to/your/hadoop-2.7.2/share/hadoop/giraph && cp -r stmt  /path/to/your/hadoop-2.7.2/share/hadoop/giraph

# copy Giraph jar to Mapreduce classpath
$ cp giraph-examples-1.4.0-SNAPSHOT-for-hadoop-2.7.2-jar-with-dependencies.jar /path/to/your/hadoop-2.7.2/share/hadoop/giraph

# integrate A_Analysis jar classes into Giraph jar
$ cd /path/to/your/hadoop-2.7.2/share/hadoop/giraph
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-for-hadoop-2.7.2-jar-with-dependencies.jar 
$ ./stmt/*.class
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-for-hadoop-2.7.2-jar-with-dependencies.jar ./analysis/*.class
```



#### Usage example for A Analysis

1. Prepare the start file and grammar file for A Analysis

```bash
$ ./hadoop fs -mkdir /entrys
```

2. Prepare the Files for A Analysis

```bash
# prebuild HDFS dir
$ cd /path/to/your/hadoop-2.7.2/bin
$ hadoop fs -mkdir -p /path/to/your/input_type1/A

# put Graph File on HDFS  
$ hadoop fs -put /input/PA/A/entry.txt /entrys
$ hadoop fs -mv /entrys/entry.txt /entrys/A.entry

$ hadoop fs -put /input/PA/A/new-final.txt /path/to/your/input_type1/A && \
$ hadoop fs -put /input/PA/A/new-nodes.txt /path/to/your/input_type1/A
$ hadoop fs -mv /path/to/your/input_type1/A/new-nodes.txt /path/to/your/input_type1/A/final/new_nodes && \
$ hadoop fs -mv /path/to/your/input_type1/A/new-final.txt /path/to/your/input_type1/A/final/final
```

3. Run A Analysis

```bash
$ ././scripts/P1_scripts/run_p1_input1.sh
```

4. After running, you can  see cat output  from HDFS dir `/path/to/your/hdfs/output_type1`, and its logs are in local dir `/path/to/your/log_type1/A`

