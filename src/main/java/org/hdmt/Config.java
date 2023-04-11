package org.hdmt;

import java.util.Map;
import java.util.HashMap;
import java.io.FileInputStream;
import org.yaml.snakeyaml.Yaml;
import java.io.IOException;

public class Config {

    public static Map<String, String> OP_CODE_MAP;
    public static Map<String, String> OP_NAME_MAP;
    public static Map<String, String> CONDITIONALS_BOUNDARY_MUTATION;
    public static Map<String, String> MATH_MUTATION;
    public static Map<String, String> NEGATE_CONDITIONALS;

    static {

        try {
            FileInputStream op_code_yaml = new FileInputStream("/Users/mgartenhaus/CS527/HDMT/src/main/yamls/OP_CODE_MAP.yaml");

            OP_CODE_MAP = new Yaml().load(op_code_yaml);

            op_code_yaml.close();

            OP_NAME_MAP = new HashMap<>();
            for(Map.Entry<String, String> entry : OP_CODE_MAP.entrySet()){
                OP_NAME_MAP.put(entry.getValue(), entry.getKey());
            }
            

            CONDITIONALS_BOUNDARY_MUTATION = new HashMap<>();
            CONDITIONALS_BOUNDARY_MUTATION.put("if_icmplt", "if_icmple");
            CONDITIONALS_BOUNDARY_MUTATION.put("if_icmple", "if_icmplt");
            CONDITIONALS_BOUNDARY_MUTATION.put("iflt", "ifle");
            CONDITIONALS_BOUNDARY_MUTATION.put("ifle", "iflt");
            CONDITIONALS_BOUNDARY_MUTATION.put("if_icmpgt", "if_icmpge");
            CONDITIONALS_BOUNDARY_MUTATION.put("if_icmpge", "if_icmpgt");
            CONDITIONALS_BOUNDARY_MUTATION.put("ifgt", "ifge");
            CONDITIONALS_BOUNDARY_MUTATION.put("ifge", "ifgt");

            
            MATH_MUTATION = new HashMap<>();
            MATH_MUTATION.put("iadd", "isub");
            MATH_MUTATION.put("isub", "iadd");
            MATH_MUTATION.put("imul", "idiv");
            MATH_MUTATION.put("idiv", "imul");
            MATH_MUTATION.put("irem", "imul");
            MATH_MUTATION.put("iand", "ior");
            MATH_MUTATION.put("ior", "iand");
            MATH_MUTATION.put("ixor", "iand");
            MATH_MUTATION.put("ishl", "ishr");
            MATH_MUTATION.put("ishr", "ishl");
            MATH_MUTATION.put("iushr", "ishl");
            MATH_MUTATION.put("ladd", "lsub");
            MATH_MUTATION.put("lsub", "ladd");
            MATH_MUTATION.put("lmul", "ldiv");
            MATH_MUTATION.put("ldiv", "lmul");
            MATH_MUTATION.put("lrem", "lmul");
            MATH_MUTATION.put("land", "lor");
            MATH_MUTATION.put("lor", "land");
            MATH_MUTATION.put("lxor", "land");
            MATH_MUTATION.put("lshl", "lshr");
            MATH_MUTATION.put("lshr", "lshl");
            MATH_MUTATION.put("lushr", "lshl");
            MATH_MUTATION.put("dadd", "dsub");
            MATH_MUTATION.put("dsub", "dadd");
            MATH_MUTATION.put("dmul", "ddiv");
            MATH_MUTATION.put("ddiv", "dmul");
            MATH_MUTATION.put("drem", "dmul");
            MATH_MUTATION.put("dand", "dor");
            MATH_MUTATION.put("dor", "dand");
            MATH_MUTATION.put("dxor", "dand");
            MATH_MUTATION.put("fadd", "fsub");
            MATH_MUTATION.put("fsub", "fadd");
            MATH_MUTATION.put("fmul", "fdiv");
            MATH_MUTATION.put("fdiv", "fmul");
            MATH_MUTATION.put("frem", "fmul");
            MATH_MUTATION.put("fand", "for");
            MATH_MUTATION.put("for", "fand");
            MATH_MUTATION.put("fxor", "fand");


            NEGATE_CONDITIONALS = new HashMap();
            NEGATE_CONDITIONALS.put("if_acmpeq", "if_acmpne");
            NEGATE_CONDITIONALS.put("if_acmpne", "if_acmpeq");
            NEGATE_CONDITIONALS.put("if_icmpeq", "if_icmpne");
            NEGATE_CONDITIONALS.put("if_icmpne", "if_icmpeq");
            NEGATE_CONDITIONALS.put("if_icmple", "if_icmpgt");
            NEGATE_CONDITIONALS.put("if_icmpgt", "if_icmple");
            NEGATE_CONDITIONALS.put("if_icmplt", "if_icmpge");
            NEGATE_CONDITIONALS.put("if_icmpge", "if_icmplt");
            NEGATE_CONDITIONALS.put("ifeq", "ifne");
            NEGATE_CONDITIONALS.put("ifne", "ifeq");
            NEGATE_CONDITIONALS.put("ifle", "ifgt");
            NEGATE_CONDITIONALS.put("ifgt", "ifle");
            NEGATE_CONDITIONALS.put("iflt", "ifge");
            NEGATE_CONDITIONALS.put("ifge", "iflt");

        } catch (IOException e){}

    }

}