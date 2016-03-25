package symjava.apache.hive.SymHive;

import lc.bytecode.ShuntingYardParser;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;

public class SymExpr extends UDF {

	@Description(name = "SymExpr", 
			value = "returns the evaluating result of an expression", 
			extended = "SELECT symexpr(\"diff(sqrt(x*x+y*y),x)\", x, y);")
	public String evaluate(String[] args) {
		double ret = 0;
		ShuntingYardParser p = new ShuntingYardParser();
		Expr expr = p.quickParse(args[0]);
		BytecodeFunc f = JIT.compile(expr);
		double[] inputs = new double[args.length-1];
		for(int i=1; i<args.length; i++)
			inputs[i-1] = Double.parseDouble(args[i]); 
		ret = f.apply(inputs);
		return String.valueOf(ret);
	}

	public static void main(String[] args) {
		SymExpr s = new SymExpr();
		String ret = s.evaluate(new String[]{"sqrt(x*x+y*y)","3.0","4.0"});
		System.out.println(ret);
	}
}
