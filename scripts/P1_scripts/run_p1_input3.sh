i=140

## B

hadoop fs -mv /entrys/B.entry /entrys/entry 

startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
org.apache.giraph.GiraphRunner analysis.Analysis \
-vif analysis.DefaultVertexInputFormat \
-vip /path/to/your/input_type1/B/new_nodes \
-vof analysis.DefaultVertexOutputFormat \
-op /path/to/your/hdfs/output_type1/B_outw"$i" \
-eif analysis.DefaultEdgeInputFormat \
-eip /path/to/your/input_type1/B/final \
-mc analysis.MasterBroadcast \
-w "$i" \
-ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
> /path/to/your/hdfs/output_type1/B_c80W"$i"_result.txt 2>&1
then
        enddate=$(date "+%Y-%m-%d %H:%M:%S")
        echo "$enddate"
        echo "$startdate" >> /path/to/your/hdfs/output_type1/B_c80W"$i"_result.txt 2>&1
        echo "$enddate" >> /path/to/your/hdfs/output_type1/B_c80W"$i"_result.txt 2>&1
        echo "B------Success"
else
        enddate=$(date "+%Y-%m-%d %H:%M:%S")
        echo "$enddate"
        echo "$startdate" >> /path/to/your/hdfs/output_type1/B_c80W"$i"_result.txt 2>&1
        echo "$enddate" >> /path/to/your/hdfs/output_type1/B_c80W"$i"_result.txt 2>&1
        echo "B------Fail"
fi
hadoop fs -mv /entrys/entry /entrys/B.entry
echo -e "\n\n"
sleep 40

