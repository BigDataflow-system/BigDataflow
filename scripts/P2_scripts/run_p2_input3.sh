  
i=20

## B/apps
hadoop fs -mv /analysis/start.B.txt /analysis/start 

startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
org.apache.giraph.GiraphRunner analysis.Analysis \
-vif analysis.VertexInputFormat \
-vip /path/to/your/input_type2/B/id_stmt_info \
-vof analysis.VertexOutputFormat \
-op /path/to/your/hdfs/output_type2/B/apps_outw"$i" \
-eif analysis.FinalEdgeInputFormat \
-eip /path/to/your/input_type2/B/final \
-wc analysis.MyWorkerContext   \
-mc analysis.MasterBroadcast \
-w "$i" -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
> /path/to/your/log_type2/B_w"$i"_result.txt 2>&1
then
        enddate=$(date "+%Y-%m-%d %H:%M:%S")
        echo "$enddate"
        echo "$startdate" >> /path/to/your/log_type2/B_w"$i"_result.txt
        echo "$enddate" >> /path/to/your/log_type2/B_w"$i"_result.txt
        echo "B/apps------Success"
else
        enddate=$(date "+%Y-%m-%d %H:%M:%S")
        echo "$enddate"
        echo "$startdate" >> /path/to/your/log_type2/B_w"$i"_result.txt
        echo "$enddate" >> /path/to/your/log_type2/B_w"$i"_result.txt
        echo "B/apps------Fail"
fi
hadoop fs -mv /analysis/start /analysis/start.B.txt
echo -e "\n\n"
sleep 40






