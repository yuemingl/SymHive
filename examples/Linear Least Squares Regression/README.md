This example is taken from here:
http://www.cyclismo.org/tutorial/R/linearLeastSquares.html
Given the data:
```sh
> year <- c(2000 ,   2001  ,  2002  ,  2003 ,   2004)
> rate <- c(9.34 ,   8.50  ,  7.62  ,  6.93  ,  6.60)
fit the model:
rate = (slope)year + intercept
```
SymHive solution:

```sh
add jar /Users/yueming.liu/SymHive.jar;

create temporary function sym_opt as 'symjava.apache.hive.SymHive.SymOptimize';

hive> create table year_rate (year Int, rate Double) row format delimited fields terminated by ',' stored as textfile;
OK
Time taken: 0.347 seconds

hive> load data local inpath '/Users/yueming.liu/workspace/eclipse_kepler/SymHive/examples/Linear Least Squares Regression/InterestRate.txt' into table year_rate;
Loading data to table default.year_rate
Table default.year_rate stats: [numFiles=1, totalSize=49]
OK

hive> select sym_opt("eq(y,a*x+b, [x],[a,b])", 0, 0, year, rate) from year_rate group by 1 --Note: 0,0 are the initial guess of a,b; We use group by 1 to group the data for the model
Warning: Using constant number  1 in group by. If you try to use position alias when hive.groupby.orderby.position.alias is false, the position alias will be ignored.
Query ID = yueming.liu_20160328152822_8c855119-3144-49fb-a565-c0da6549675c
Total jobs = 1
Launching Job 1 out of 1
Number of reduce tasks not specified. Estimated from input data size: 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Job running in-process (local Hadoop)
>>Equation: eq(y,a*x+b, array(x),[a,b])
eq(y, a*x + b, array(x), array(a,b))
Arguments: a, b, x, y
Jacobian = 
-x,-1
Residuals = 
-(a*x + b) + y
JIT Batch: -x
JIT Batch: -1
JIT Batch: -(a*x + b) + y
Iterativly sovle ... 
a=-0.70500 b=1419.20800 
2016-03-28 15:28:25,205 Stage-1 map = 100%,  reduce = 100%
Ended Job = job_local1935796294_0007
MapReduce Jobs Launched: 
Stage-Stage-1:  HDFS Read: 686 HDFS Write: 98 SUCCESS
Total MapReduce CPU Time Spent: 0 msec
OK
-0.7049999999998506 1419.207999999701 
Time taken: 2.309 seconds, Fetched: 1 row(s)
```
