# BigDataflow-classic

BigDataflow-naive is a distributed dataflow analysis framework that supports the general interprocedural dataflow analysis but follows the classic worklist algorithm.

## Getting started

The version of dependent tools and the environment requirement is the same as the BigDataflow.

### Usage Examples on the Cloud

**Running Alias Analysis**

1, Compile the code to produce the alias jar

```bash
# compile the alias analysis
$ cd AliasAnalysis
$ mvn clean package
$ cd ..

# prepare the jar of giraph under your jar_directoty
$ cp emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar alias_jar_directoty/
$ cd alias_jar_directoty/
$ mv emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar

# add your analysis class files into the jar
$ cd target/classes/
$ cp -r analysis/ stmt/ alias_jar_directoty/
$ cd alias_jar_directoty/
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./analysis/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./stmt/*.class 
```

2, Put your analysis jar into your corresponding Hadoop directory

```bash
# jar directory at local
$ cp alias_jar_directoty/giraph-examples-1.4.0-SNAPSHOT-shaded.jar /path/to/your/hadoop-2.7.2/share/hadoop/giraph

# jar directory on the cloud
$ cp alias_jar_directoty/giraph-examples-1.4.0-SNAPSHOT-shaded.jar /opt/apps/extra-jars
```

3, Run Alias Analysis

As the global file `grammar` and specific CFG files have already been added into the HDFS directory,  alias analysis can be launched by executing the commands below.

```bash
$ hadoop fs -mv /client/alias_analysis_conf.CFG /client/analysis_conf

i=XX
startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

$ if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
  org.apache.giraph.GiraphRunner analysis.Alias_Analysis \
  -vif analysis.AliasVertexInputFormat \
  -vip /alias_graphs/CFG/id_stmt_info \
  -vof analysis.AliasVertexOutputFormat \
  -op /classic_alias_res/CFG_W"$i" \
  -eif analysis.FinalEdgeInputFormat \
  -eip /alias_graphs/CFG/final \
  -wc analysis.MyWorkerContext   \
  -mc analysis.MasterBroadcast \
  -w "$i" \
  -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
  > /classic_alias_CFG_W"$i"_result.txt 2>&1
then
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /classic_alias_CFG_W"$i"_result.txt
  echo "$enddate" >> /classic_alias_CFG_W"$i"_result.txt
  echo "classic alias CFG------Success"
else
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /classic_alias_CFG_W"$i"_result.txt
  echo "$enddate" >> /classic_alias_CFG_W"$i"_result.txt
  echo "classic alias CFG------Fail"
fi

$ hadoop fs -mv /client/analysis_conf /client/alias_analysis_conf.CFG 
```

**Running Cache Analysis**

1, Compile the code to produce the cache jar

```bash
# compile the cache analysis
$ cd CacheAnalysis
$ mvn clean package
$ cd ..

# prepare the jar of giraph under your own jar_directoty
$ cp emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar cache_jar_directoty/
$ cd cache_jar_directoty/
$ mv emr-giraph-examples-1.4.0-SNAPSHOT-shaded.jar giraph-examples-1.4.0-SNAPSHOT-shaded.jar

# add your analysis class files into the jar
$ cd target/classes/
$ cp -r analysis/ toImplement/ cache_jar_directoty/
$ cd cache_jar_directoty/
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./analysis/*.class ; \
$ jar uvf giraph-examples-1.4.0-SNAPSHOT-shaded.jar  ./toImplement/*.class 
```

2, Put your analysis jar into your corresponding Hadoop directory

```bash
# jar directory at local
$ cp cache_jar_directoty/giraph-examples-1.4.0-SNAPSHOT-shaded.jar /path/to/your/hadoop-2.7.2/share/hadoop/giraph

# jar directory on the cloud
$ cp cache_jar_directoty/giraph-examples-1.4.0-SNAPSHOT-shaded.jar /opt/apps/extra-jars
```

3, Run Cache Analysis

```bash
$ hadoop fs -mv /client/cache_analysis_conf /client/analysis_conf
$ hadoop fs -mv /cache_entrys/CFG.entry /cache_entrys/entry

i=XX
startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

$ if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
  org.apache.giraph.GiraphRunner analysis.Analysis \
  -vif analysis.DefaultVertexInputFormat \
  -vip /cache_graphs/CFG/new_nodes \
  -vof analysis.DefaultVertexOutputFormat \
  -op /classic_cache_res/CFG_W"$i" \
  -eif analysis.DefaultEdgeInputFormat \
  -eip /cache_graphs/CFG/final \
  -mc analysis.MasterBroadcast \
  -w "$i" \
  -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
  > /classic_cache_CFG_W"$i"_result.txt 2>&1
then
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /classic_cache_CFG_W"$i"_result.txt 2>&1
  echo "$enddate" >> /classic_cache_CFG_W"$i"_result.txt 2>&1
  echo "classic cache CFG------Success"
else
  enddate=$(date "+%Y-%m-%d %H:%M:%S")
  echo "$enddate"
  echo "$startdate" >> /classic_cache_CFG_W"$i"_result.txt 2>&1
  echo "$enddate" >> /classic_cache_CFG_W"$i"_result.txt 2>&1
  echo "classic cache CFG------Fail"
fi

$ hadoop fs -mv /cache_entrys/entry /cache_entrys/CFG.entry 
$ hadoop fs -mv /client/analysis_conf /client/cache_analysis_conf
```

