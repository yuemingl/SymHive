package symjava.apache.hive.SymHive;

import java.util.ArrayList;
import java.util.List;

import lc.bytecode.ShuntingYardParser;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;

import symjava.examples.GaussNewton;
import symjava.symbolic.Expr;

/**
 */
@Description(name = "sym_optimize", value = "_FUNC_(col) - Optimization a given model")
public class SymOptimize extends UDAF {

	public static class OptEvaluator implements UDAFEvaluator {
		ArrayList<ArrayList<String>> data;

		public OptEvaluator() {
			super();
			data = new ArrayList<ArrayList<String>>();
		}

		public void init() {
			data.clear();
		}

		/**
		 * Iterate through one row of original data.
		 * This UDF accepts arbitrary number of String arguments, so we use String[].
		 * This function should always return true.
		 */
		public boolean iterate(String[] o) {
			if (o != null) {
				ArrayList<String> tuple = new ArrayList<String>();
				for (int i=0; i<o.length; i++) {
					tuple.add(o[i]);
				}
				data.add(tuple);
			}
			return true;
		}

		/**
		 * Terminate a partial aggregation and return the state.
		 */
		public ArrayList<ArrayList<String>> terminatePartial() {
			return data;
		}

		/**
		 * Merge with a partial aggregation.
		 * 
		 * This function should always have a single argument which has the same
		 * type as the return value of terminatePartial().
		 * 
		 * This function should always return true.
		 */
		public boolean merge(ArrayList<ArrayList<String>> o) {
			if (o != null) {
				data.addAll(o);
			}
			return true;
		}

		/**
		 * Terminates the aggregation and return the final result.
		 * 
		 *  sym_optimize("eq(...)", init1, init2,..., initN, x1, x2,..., xN)
		 *  
		 */
		public String terminate() {
			if(data.size() == 0) return null;
			
			String equationExpr = data.get(0).get(0);
			Integer maxIter = 100;
			Double eps = 1e-4;
			int len = data.get(0).size();
			double[] initGuess = toArray(data.get(0), 1, 1+len/2);
			
			ShuntingYardParser p = new ShuntingYardParser();
			System.out.println(">>Equation: "+equationExpr);
			List<Object> retExpr = p.fullParse(equationExpr);
			double[] ret = GaussNewton.solve((Expr)retExpr.get(retExpr.size()-1), 
					initGuess, getData(), 
					maxIter, eps);
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<ret.length; i++) 
				sb.append(ret[i]).append(" ");
			return sb.toString();
		}
		
		public double[][] getData() {
			double[][] ret = new double[this.data.size()][];
			for(int i=0; i<this.data.size(); i++) {
				int len = this.data.get(i).size();
				ret[i] = toArray(this.data.get(i), 1+len/2, len);
			}
			return ret;
		}
		
		public static double[] toArray(ArrayList<String> list, int start, int end) {
			double[] ret = new double[end-start];
			for(int i=start; i<end; i++) {
				ret[i-start] = Double.parseDouble(list.get(i));
			}
			return ret;
		}
	}
	
	public static void main(String[] args) {
		OptEvaluator t = new OptEvaluator();
		t.init();
		t.iterate(new String[] { "eq( y,a/(b + x)*x, array(x), array(a,b) )", "0.9", "0.2", "0.038", "0.050" });
		t.iterate(new String[] { "eq( y,a/(b + x)*x, array(x), array(a,b) )", "0.9", "0.2", "0.194", "0.127" });
		t.iterate(new String[] { "eq( y,a/(b + x)*x, array(x), array(a,b) )", "0.9", "0.2", "0.425", "0.094" });
		t.iterate(new String[] { "eq( y,a/(b + x)*x, array(x), array(a,b) )", "0.9", "0.2", "0.626", "0.2122" });
		t.iterate(new String[] { "eq( y,a/(b + x)*x, array(x), array(a,b) )", "0.9", "0.2", "1.253", "0.2729" });
		t.iterate(new String[] { "eq( y,a/(b + x)*x, array(x), array(a,b) )", "0.9", "0.2", "2.500", "0.2665" });
		t.iterate(new String[] { "eq( y,a/(b + x)*x, array(x), array(a,b) )", "0.9", "0.2", "3.740", "0.3317" });
		t.terminate();
		
	}

}