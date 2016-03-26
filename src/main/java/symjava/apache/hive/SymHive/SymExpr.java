package symjava.apache.hive.SymHive;

import lc.bytecode.ShuntingYardParser;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;

import symjava.bytecode.BytecodeFunc;
import symjava.symbolic.Expr;
import symjava.symbolic.utils.JIT;

class SymExpr extends GenericUDF {
	boolean compiled;
	BytecodeFunc f;

	@Override
	public String getDisplayString(String[] arg0) {
		return "sym_expr()";
	}

	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments)
			throws UDFArgumentException {
		this.compiled = false;
		// the return type of our function is a String, so we provide the
		// correct object inspector
		return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
	}

	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		StringObjectInspector soi = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
		if (!compiled) {
			ShuntingYardParser p = new ShuntingYardParser();
			String expr = soi.getPrimitiveJavaObject(arguments[0].get());
			Expr pe = p.quickParse(expr);
			f = JIT.compile(pe);
			compiled = true;
		}
		double[] inputs = new double[arguments.length - 1];
		for (int i = 1; i < arguments.length; i++)
			inputs[i - 1] = Double.parseDouble(soi
					.getPrimitiveJavaObject(arguments[i].get()));
		double ret = f.apply(inputs);
		return String.valueOf(ret);
	}

}