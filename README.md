# SymHive - Big data for math
SymHive allows you to use [SymJava](https://github.com/yuemingl/SymJava) symbolic-numeric computation engine in Hive. The following example shows how to use SymHive to fit the model 
```math
y = a/(b + x)*x,
```
using [Gauss Newton Method](https://en.wikipedia.org/wiki/Gauss%E2%80%93Newton_algorithm) .

``` sh
add jar /path/to/SymHive.jar
create temporary function sym_opt as 'symjava.apache.hive.SymHive.SymOptimize';
select sym_opt("eq( y,a/(b + x)*x, array(x), array(a,b) )",0.9,0.2,x,y) from gauss_newton group by group_id;
OK
0.36183442015991124 0.5562524645285598 
0.3634503676784828 0.5669911877080493 
Time taken: 2.262 seconds, Fetched: 2 row(s)
```

```sh
####Test Data####
create table gauss_newton (group_id String, x Double, y Double) row format delimited fields terminated by '\t' stored as textfile;
load data local inpath '/Users/yueming.liu/gauss_newton.txt' into table gauss_newton;
###Data File###
1	0.038	0.050
1	0.194	0.127
1	0.425	0.094
1	0.626	0.2122
1	1.253	0.2729
1	2.500	0.2665
1	3.740	0.3317
2	0.038	0.040
2	0.194	0.127
2	0.425	0.094
2	0.626	0.2122
2	1.253	0.2729
2	2.500	0.2665
2	3.740	0.3317
```



