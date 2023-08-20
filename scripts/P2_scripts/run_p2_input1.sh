
j=10

## A
hadoop fs -mv /analysis/start.A.txt /analysis/start 

startdate=$(date "+%Y-%m-%d %H:%M:%S")
echo "$startdate"

if hadoop jar /opt/apps/extra-jars/giraph-examples-1.4.0-SNAPSHOT-shaded.jar \
org.apache.giraph.GiraphRunner analysis.Analysis \
-vif analysis.VertexInputFormat \
-vip /path/to/your/input_type2/A/id_stmt_info \
-vof analysis.VertexOutputFormat \
-op /path/to/your/hdfs/output_type2/A_outw"$j" \
-eif analysis.FinalEdgeInputFormat \
-eip /path/to/your/input_type2/A/final \
-wc analysis.MyWorkerContext   \
-mc analysis.MasterBroadcast \
-w "$j" -ca giraph.maxCounterWaitMsecs=-1,giraph.yarn.task.heap.mb=14336 \
> /path/to/your/log_type2/A_w"$j"_result.txt 2>&1
then
        enddate=$(date "+%Y-%m-%d %H:%M:%S")
        echo "$enddate"
        echo "$startdate" >> /path/to/your/log_type2/A_w"$j"_result.txt
        echo "$enddate" >> /path/to/your/log_type2/A_w"$j"_result.txt
        echo "A------Success"
else
        enddate=$(date "+%Y-%m-%d %H:%M:%S")
        echo "$enddate"
        echo "$startdate" >> /path/to/your/log_type2/A_w"$j"_result.txt
        echo "$enddate" >> /path/to/your/log_type2/A_w"$j"_result.txt
        echo "A------Fail"
fi
hadoop fs -mv /analysis/start /analysis/start.A.txt
echo -e "\n\n"
sleep 15

