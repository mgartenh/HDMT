package org.hdmt;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.Type;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.objectweb.asm.Opcodes;
import java.lang.Integer;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;
import java.io.File;
import java.lang.InterruptedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import org.hdmt.InstructionData;
import org.hdmt.Config;

public class Mutator {

    private static Random rand = new Random();

    public enum MutationTypes {
        CONDITIONALS_BOUNDARY,
        INCREMENTS,
        INVERT_NEGS,
        MATH,
        NEGATE_CONDITIONALS,
        RETURN_VALS,
        VOID_METHOD_CALLS,
        FALSE_RETURNS,
        TRUE_RETURNS,
        NULL_RETURNS,
        PRIMITIVE_RETURNS,
        CONSTRUCTOR_CALLS,
        INLINE_CONSTS,
        NON_VOID_METHOD_CALLS,
        REMOVE_CONDITIONALS,
        REMOVE_INCREMENTS,
        AOR,
        AOD,
        OBBN,
        ROR,
        UOI
    }

    private String path;
    private String name;
    private String tempDir;
    private String tempPath;
    private MutationTypes mutation;

    public Mutator(String classPath, String className) throws IOException, InterruptedException {

        path = classPath;
        name = className;

        prepareTestEnv();
    }

    public Map<String, Map> mutationTest() throws IOException, FileNotFoundException, InterruptedException {

        List<MethodNode> methods = loadClass().methods;

        int totalMethods = methods.size();

        Map<String, Map> classResults = new HashMap<>();

        for (int i = 0; i < totalMethods; i++) {
            String methodName = methods.get(i).name + "_" + methods.get(i).desc;

            int totalMutations = 0; 
            int totalFailures = 0;
            Instant start = Instant.now();
            Map<MutationTypes, Integer> mutationTypeCount = new HashMap<>();
            Map<MutationTypes, Integer> mutationTypeFailureCount = new HashMap<>();

            int totalInstructions = methods.get(i).instructions.size();
            for (int k = 0; k < totalInstructions * 0.1 + 1; k++) {
                ClassNode mutantClass = loadClass();
                final MethodNode method = mutantClass.methods.get(i);
                boolean mutationResult;
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<Boolean> future = executor.submit(new Callable() {
                    public Boolean call() throws Exception {
                        return performMutation(method, 0);
                    }
                });
                try {
                    mutationResult = future.get(10, TimeUnit.SECONDS);
                    future.cancel(true);
                } catch (Exception e) {
                    System.out.println(mutation.toString() + " : "+ e.toString());
                    future.cancel(true);
                    mutationResult = false;
                }
                if (mutationResult) {
                    totalMutations++;
                    if (mutationTypeCount.containsKey(mutation)) {
                        mutationTypeCount.put(mutation, mutationTypeCount.get(mutation) + 1);
                    } else {
                        mutationTypeCount.put(mutation, 1);
                        mutationTypeFailureCount.put(mutation, 0);
                    }
                    
                    ClassWriter cw = new ClassWriter(0);
                    mutantClass.accept(cw);
                    new File(path).delete();
                    FileOutputStream out = new FileOutputStream(path);
                    out.write(cw.toByteArray());
                    executor = Executors.newSingleThreadExecutor();
                    future = executor.submit(new Callable() {
                        public Boolean call() throws Exception {
                            return runTests(name + "Test");
                        }
                    });
                    boolean testResult, failure;
                    try {
                        testResult = future.get(10, TimeUnit.SECONDS);
                        future.cancel(true);
                        failure = false;
                    } catch (Exception e) {
                        System.out.println(mutation.toString() + " : "+ e.toString());
                        future.cancel(true);
                        failure = true;
                        testResult = false;
                    }
                    if (testResult) {
                        System.out.println(name + " " + methodName + " mutation of type " + mutation.toString() + " caused tests to pass");
                    } else if (!failure) {
                        System.out.println(name + " " + methodName + " mutation of type " + mutation.toString() + " caused tests to fail");
                        totalFailures++;
                        mutationTypeFailureCount.put(mutation, mutationTypeFailureCount.get(mutation) + 1);
                    }
                }

            }


            Instant end = Instant.now();

            Map<String, Map> methodResults = new HashMap<>();

            Map<String, Integer> aggregates = new HashMap<>();

            aggregates.put("totalMutations", totalMutations);
            aggregates.put("totalFailures", totalFailures);
            aggregates.put("totalTime", (int)((end.getEpochSecond() - start.getEpochSecond()) * 1000 + (end.getNano() - start.getNano()) / 1000000));


            methodResults.put("mutationTypeCount", mutationTypeCount);
            methodResults.put("mutationTypeFailureCount", mutationTypeFailureCount);
            methodResults.put("aggregates", aggregates);
            classResults.put(methodName, methodResults);

        }

        new File(path).delete();
        Files.move(Paths.get(tempPath), Paths.get(path));
        // System.out.println(runTests("ALL"));

        return classResults;

    }

    private void prepareTestEnv() throws IOException {
        tempDir = System.getProperty("user.dir") + "/temp/";
        tempPath = tempDir + name + ".class";
        new File(tempDir).mkdirs();
        new File(tempPath).delete();
        Files.move(Paths.get(path), Paths.get(tempPath));
    }

    private ClassNode loadClass() throws IOException, FileNotFoundException {
        FileInputStream in = new FileInputStream(tempPath);
        ClassReader cr = new ClassReader(in);
        ClassNode v = new ClassNode(Opcodes.ASM9);
        cr.accept(v, ClassReader.SKIP_DEBUG);

        return v;
    }


    private static boolean runTest(String testClassName) throws IOException, InterruptedException {
        String additionalParam;
        if (testClassName.equals("ALL")) {
            additionalParam = "";
        } else {
            additionalParam = " -Dtest=" + testClassName + " -DfailIfNoTests=false";
        }
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", "mvn surefire:test" + additionalParam);
        builder.directory(new File("/Users/mgartenhaus/CS527/jsoup_eval/"));
        Process pr = builder.start();
        InputStream stdout = pr.getInputStream();
        BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
        pr.waitFor(100, TimeUnit.MILLISECONDS);

        boolean result = true;

        String line = reader.readLine();
        while (line != null && ! line.trim().equals("--EOF--")) {
            // System.out.println ("Stdout: " + line);
            if (line.contains("BUILD FAILURE")) {
                result = false;
                break;
            } else if (line.contains("BUILD SUCCESS")) {
                result = true;
                break;
            }
            line = reader.readLine();
        }

        pr.destroyForcibly();

        return result;
    }

    public static boolean runTests(String testClassName) throws IOException, InterruptedException {
        boolean result = true;
        if (!testClassName.equals("ALL")) {
            result = runTest(testClassName);
        }
        if (result) {
            result = runTest("ALL");
        }

        return result;

    }
    

    private static List<String> getOpcodes(InsnList instructions) {

        List<String> opcodes = new ArrayList<>();

        for (AbstractInsnNode inst : instructions) {
            opcodes.add(Config.OP_CODE_MAP.get(Integer.toHexString(inst.getOpcode())));
        }

        return opcodes;

    }

    private static String getOpcodeDescString(int code) {
        return Config.OP_CODE_MAP.get(Integer.toHexString(code));
    }

    private static int getOpcodeInt(String desc) {
        return Integer.parseInt(Config.OP_NAME_MAP.get(desc), 16);
    }

    private static List<InstructionData> processInstructions(InsnList instructions, MethodNode method) {
        List<InstructionData> info = new ArrayList<>();
        for (AbstractInsnNode inst : instructions) {
            info.add(processInstruction(inst, method));
        }
        return info;
    }

    private static InstructionData processInstruction(AbstractInsnNode instruction, MethodNode method) {

        InstructionData data = new InstructionData();
        data.opcode = instruction.getOpcode();
        data.opcodeString = getOpcodeDescString(data.opcode);

        List<String> skip = new ArrayList<>();
        skip.add("goto");

        if (skip.contains(data.opcodeString)) {
            return data;
        } else if (instruction instanceof MethodInsnNode) {
            data.instructionType = "method";
            //VOID_METHOD_CALLS, NON_VOID_METHOD_CALLS
            MethodInsnNode methodInstruction = (MethodInsnNode) instruction;
            data.inputTypes = methodInstruction.desc.split("\\)")[0] + ")";
            data.outputType = methodInstruction.desc.split("\\)")[1];
            data.methodName = methodInstruction.name;
            // System.out.println(methodInstruction.desc);
            if (data.outputType.equals("V")) {
                data.allowedMutations.add(MutationTypes.VOID_METHOD_CALLS);
            } else {
                data.allowedMutations.add(MutationTypes.NON_VOID_METHOD_CALLS);
            }
        } else if (instruction instanceof FieldInsnNode) {
            //  GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
            FieldInsnNode fieldInstruction = (FieldInsnNode) instruction;
        } else if (instruction instanceof IincInsnNode) {
            data.instructionType = "increment";
            IincInsnNode incrementInstruction = (IincInsnNode) instruction;
            data.increment = incrementInstruction.incr;
            data.allowedMutations.add(MutationTypes.INCREMENTS);
            data.allowedMutations.add(MutationTypes.REMOVE_INCREMENTS);
        } else if (instruction instanceof IntInsnNode) {
            IntInsnNode intInstruction = (IntInsnNode) instruction;
        } else if (instruction instanceof JumpInsnNode) {
            // this instruction can be for CONDITIONALS_BOUNDARY, REMOVE_CONDITIONALS, ROR
            JumpInsnNode jumpInstruction = (JumpInsnNode) instruction;

            if (Config.CONDITIONALS_BOUNDARY_MUTATION.keySet().contains(data.opcodeString)) {
                data.allowedMutations.add(MutationTypes.CONDITIONALS_BOUNDARY);
                data.allowedMutations.add(MutationTypes.ROR);
            }
            if (Config.NEGATE_CONDITIONALS.keySet().contains(data.opcodeString)) {
                data.allowedMutations.add(MutationTypes.NEGATE_CONDITIONALS);
            }
            if (!data.opcodeString.equals("goto")) {
                data.allowedMutations.add(MutationTypes.REMOVE_CONDITIONALS);
            }
        } else if (instruction instanceof TypeInsnNode) {
            TypeInsnNode typeInstruction = (TypeInsnNode) instruction;
            if (data.opcodeString.equals("new")) {
                data.allowedMutations.add(MutationTypes.CONSTRUCTOR_CALLS);
            }
        } else if (instruction instanceof VarInsnNode) {
            if (data.opcodeString.startsWith("iload") || data.opcodeString.equals("iaload")) {
                data.allowedMutations.add(MutationTypes.UOI);
            }
        } else if (instruction instanceof InsnNode) {

            InsnNode instructionInstruction = (InsnNode) instruction;
            // System.out.println(data.opcodeString);
            if (Config.MATH_MUTATION.keySet().contains((data.opcodeString))) {
                data.allowedMutations.add(MutationTypes.MATH);
                data.allowedMutations.add(MutationTypes.AOD);
            }
            data.allowedMutations.add(MutationTypes.RETURN_VALS);
            // data.allowedMutations.add(MutationTypes.EMPTY_RETURNS);
            if (data.opcodeString.equals("ireturn") && Type.getReturnType(method.desc).equals(Type.BOOLEAN_TYPE)) {
                data.allowedMutations.add(MutationTypes.FALSE_RETURNS);
                data.allowedMutations.add(MutationTypes.TRUE_RETURNS);
            }

            if (data.opcodeString.equals("ireturn") || data.opcodeString.equals("dreturn") || data.opcodeString.equals("freturn") || data.opcodeString.equals("lreturn")){ 
                data.allowedMutations.add(MutationTypes.PRIMITIVE_RETURNS);
            }

            data.allowedMutations.add(MutationTypes.NULL_RETURNS);
             
            if (data.opcodeString.contains("const_") && !data.opcodeString.equals("aconst_null")) {
                data.allowedMutations.add(MutationTypes.INLINE_CONSTS);
            }
            // data.allowedMutations.add(MutationTypes.CRCR);
            if (data.opcodeString.equals("iand") || data.opcodeString.equals("ior") || data.opcodeString.equals("land") || data.opcodeString.equals("lor")) {
                data.allowedMutations.add(MutationTypes.OBBN);
            } 
            
            if (data.opcodeString.endsWith("add") || data.opcodeString.endsWith("sub") || data.opcodeString.endsWith("mul") || data.opcodeString.endsWith("div") || data.opcodeString.endsWith("rem")) {
                data.allowedMutations.add(MutationTypes.AOR);
            }

            if (data.opcodeString.endsWith("neg")) {
                data.allowedMutations.add(MutationTypes.INVERT_NEGS);
            }

        } else {
        }

        return data;


        // FieldInsnNode - has opcode - works!
                // IincInsnNode - ++i or --i, has attribute incr, we can negate the incr
                // InsnNode - zero operand instruction -- zero operand instruction, just create new object with different opcode
                // IntInsn - instruction with single int operand -- we can change the instruction -works!
                // JumpInsnNode -> if opcodes -- we can change instruction - works!
                // MethodInsnNode -> method call
                //typeinsnnode -> new, cast, instanceof
                //VarInsnNode -> local variable instruction - we can change, works!
    }

    private boolean performMutation(MethodNode method, int recursionCount) {
        if (recursionCount > 10) {
            System.out.println("unable to find mutation");
            return false;
        }
        InsnList instructions = method.instructions;

        List<InstructionData> instructionData = processInstructions(instructions, method);

        List<MutationTypes> candidateMutations = new ArrayList<>();
        for (MutationTypes val: MutationTypes.values()) {
            candidateMutations.add(val);
        }

        int candidateIndex = rand.nextInt(candidateMutations.size());

        mutation = candidateMutations.get(candidateIndex);
        List<Integer> validIndexes = new ArrayList<>();
        switch (mutation) {
            case CONDITIONALS_BOUNDARY:

                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.CONDITIONALS_BOUNDARY)) {
                        validIndexes.add(i);
                    }
                }

                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));
                    InstructionData mutateData = instructionData.get(instructionLoc);

                    String prev = mutateData.opcodeString;
                    String mut = Config.CONDITIONALS_BOUNDARY_MUTATION.get(prev);
                    // System.out.printf("%s-%s\n", prev, mut);
                    int opcode = getOpcodeInt(mut);

                    ((JumpInsnNode) instructions.get(instructionLoc)).setOpcode(opcode);

                    return true;

                } else {
                    return performMutation(method, ++recursionCount);
                }

            case INCREMENTS: //IincInsnNode: node.incr = node.incr * -1

                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.INCREMENTS)) {
                        validIndexes.add(i);
                    }
                }

                if(validIndexes.size() > 0) {;
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    ((IincInsnNode) instructions.get(instructionLoc)).incr *=-1;
                    
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
                
            case MATH:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.MATH)) {
                        validIndexes.add(i);
                    }
                }

                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));
                    InstructionData mutateData = instructionData.get(instructionLoc);

                    String prev = mutateData.opcodeString;
                    String mut = Config.MATH_MUTATION.get(prev);
                    int opcode = getOpcodeInt(mut);

                    instructions.set(instructions.get(instructionLoc), new InsnNode(opcode));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
                
            case NEGATE_CONDITIONALS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.NEGATE_CONDITIONALS)) {
                        validIndexes.add(i);
                    }
                }

                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));
                    InstructionData mutateData = instructionData.get(instructionLoc);

                    String prev = mutateData.opcodeString;
                    String mut = Config.NEGATE_CONDITIONALS.get(prev);
                    // System.out.printf("%s-%s\n", prev, mut);
                    int opcode = getOpcodeInt(mut);

                    ((JumpInsnNode) instructions.get(instructionLoc)).setOpcode(opcode);

                    // System.out.printf("%d-%d\n", mutateData.opcode, instructions.get(instructionLoc).getOpcode());
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case VOID_METHOD_CALLS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.VOID_METHOD_CALLS)) {
                        validIndexes.add(i);
                    }
                }

                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(0); //rand.nextInt(validIndexes.size()));

                    instructions.remove(instructions.get(instructionLoc));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case REMOVE_INCREMENTS:    
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.INCREMENTS)) {
                        validIndexes.add(i);
                    }
                }

                if(validIndexes.size() > 0) {;
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    ((IincInsnNode) instructions.get(instructionLoc)).incr = 0;
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
        
            case FALSE_RETURNS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.FALSE_RETURNS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    instructions.insertBefore(instructions.get(instructionLoc), new InsnNode(getOpcodeInt("iconst_0")));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case TRUE_RETURNS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.TRUE_RETURNS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    instructions.insertBefore(instructions.get(instructionLoc), new InsnNode(getOpcodeInt("iconst_1")));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case REMOVE_CONDITIONALS:
                
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.REMOVE_CONDITIONALS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));
                    InstructionData mutateData = instructionData.get(instructionLoc);

                    String prev = mutateData.opcodeString;
                    String mut = "goto";
                    int opcode = getOpcodeInt(mut);
                    ((JumpInsnNode) instructions.get(instructionLoc)).setOpcode(opcode);

                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case NULL_RETURNS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.NULL_RETURNS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    instructions.insertBefore(instructions.get(instructionLoc), new InsnNode(getOpcodeInt("aconst_null")));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case CONSTRUCTOR_CALLS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.CONSTRUCTOR_CALLS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    instructions.set(instructions.get(instructionLoc), new InsnNode(getOpcodeInt("aconst_null")));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case OBBN:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.OBBN)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    int path = rand.nextInt(3);
                    if (path == 0) {
                        String opDesc = instructionData.get(instructionLoc).opcodeString;
                        if (opDesc.contains("and")) {
                            instructions.set(instructions.get(instructionLoc), new InsnNode(getOpcodeInt(opDesc.replace("and", "or"))));
                        } else {
                            instructions.set(instructions.get(instructionLoc), new InsnNode(getOpcodeInt(opDesc.replace("or", "and"))));
                        }
                    }
                    else if (path ==1) {
                        instructions.remove(instructions.get(instructionLoc));
                        instructions.remove(instructions.get(instructionLoc - 2));
                    } else {
                        instructions.remove(instructions.get(instructionLoc));
                        instructions.remove(instructions.get(instructionLoc -1));
                    }
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case AOD:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.AOD)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    int path = rand.nextInt(2);
                    if (path == 0) {
                        instructions.remove(instructions.get(instructionLoc));
                        instructions.remove(instructions.get(instructionLoc -2));
                    } else {
                        instructions.remove(instructions.get(instructionLoc));
                        instructions.remove(instructions.get(instructionLoc -1));
                    }
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case ROR:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.ROR)) {
                        validIndexes.add(i);
                    }
                }

                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    int random = rand.nextInt(5);
                    String opcodeString = instructionData.get(instructionLoc).opcodeString;

                    if (opcodeString.endsWith("lt")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -2) + 
                            Arrays.asList(new String[]{"le", "gt", "ge", "eq", "ne"}).get(random)
                        );

                    } else if (opcodeString.endsWith("le")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -2) + 
                            Arrays.asList(new String[]{"lt", "gt", "ge", "eq", "ne"}).get(random)
                        );
                    } else if (opcodeString.endsWith("gt")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -2) + 
                            Arrays.asList(new String[]{"le", "lt", "ge", "eq", "ne"}).get(random)
                        );
                    } else if (opcodeString.endsWith("ge")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -2) + 
                            Arrays.asList(new String[]{"le", "lt", "gt", "eq", "ne"}).get(random)
                        );
                    } else if (opcodeString.endsWith("eq")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -2) + 
                            Arrays.asList(new String[]{"le", "lt", "gt", "ge", "ne"}).get(random)
                        );
                    } else {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -2) + 
                            Arrays.asList(new String[]{"le", "lt", "gt", "ge", "eq"}).get(random)
                        );
                    }
                    InstructionData mutateData = instructionData.get(instructionLoc);

                    ((JumpInsnNode) instructions.get(instructionLoc)).setOpcode(getOpcodeInt((opcodeString)));

                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }

            case AOR:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.AOR)) {
                        validIndexes.add(i);
                    }
                }

                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    int random = rand.nextInt(4);
                    String opcodeString = instructionData.get(instructionLoc).opcodeString;

                    if (opcodeString.endsWith("add")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -3) + 
                            Arrays.asList(new String[]{"sub", "mul", "div", "rem"}).get(random)
                        );
                    } else if (opcodeString.endsWith("sub")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -3) + 
                            Arrays.asList(new String[]{"add", "mul", "div", "rem"}).get(random)
                        );
                    } else if (opcodeString.endsWith("mul")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -3) + 
                            Arrays.asList(new String[]{"add", "sub", "div", "rem"}).get(random)
                        );
                    } else if (opcodeString.endsWith("div")) {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -3) + 
                            Arrays.asList(new String[]{"add", "sub", "mul", "rem"}).get(random)
                        );
                    }  else {
                        opcodeString = (
                            opcodeString.substring(0, opcodeString.length() -3) + 
                            Arrays.asList(new String[]{"add", "sub", "mul", "div"}).get(random)
                        );
                    }
                    InstructionData mutateData = instructionData.get(instructionLoc);

                    instructions.set(instructions.get(instructionLoc), new InsnNode(getOpcodeInt(opcodeString)));

                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }

            case PRIMITIVE_RETURNS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.PRIMITIVE_RETURNS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    String opcodeString = instructionData.get(instructionLoc).opcodeString;
                    String primVal;
                    if (opcodeString.startsWith("i")) {
                        primVal = "iconst_0";
                    } else if (opcodeString.startsWith("d")) {
                        primVal = "dconst_0";
                    } else if (opcodeString.startsWith("l")) {
                        primVal = "lconst_0";
                    } else {
                        primVal = "fconst_0";
                    }

                    instructions.insertBefore(instructions.get(instructionLoc), new InsnNode(getOpcodeInt(primVal)));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }

            case NON_VOID_METHOD_CALLS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.NON_VOID_METHOD_CALLS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    String returnType = instructionData.get(instructionLoc).outputType;

                    String returnValue;
                    if (returnType.equals("J")) {
                        returnValue = "lconst_0";
                    } else if (returnType.equals("F")) {
                        returnValue = "fconst_0";
                    } else if (returnType.equals("I") || returnType.equals("B") || returnType.equals("S") || returnType.equals("Z") || returnType.equals("C")) {
                        returnValue = "iconst_0";
                    } else if (returnType.equals("D")) {
                        returnValue = "dconst_0";
                    } else {
                        returnValue = "aconst_null";
                    }

                    instructions.insertBefore(instructions.get(instructionLoc), new InsnNode(getOpcodeInt(returnValue)));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case INVERT_NEGS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.INVERT_NEGS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));
                    instructions.remove(instructions.get(instructionLoc));

                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case INLINE_CONSTS:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.INLINE_CONSTS)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));

                    String opcodeString = instructionData.get(instructionLoc).opcodeString;
                    String muteString ="";
                    if (opcodeString.equals("iconst_0")) {
                        muteString = "iconst_1";
                    } else if (opcodeString.equals("iconst_1")) {
                        muteString = "iconst_0";
                    } else if (opcodeString.equals("iconst_m1")) {
                        muteString = "iconst_1";
                    } else if (opcodeString.equals("iconst_5")) {
                        muteString = "iconst_m1";
                    } else if (opcodeString.startsWith("iconst_")) {
                        muteString = "iconst_" + (Integer.parseInt(opcodeString.substring(opcodeString.length() - 1, opcodeString.length())) +1);
                    } else if (opcodeString.equals("lconst_0")) {
                        muteString = "lconst_1";
                    } else if (opcodeString.equals("lconst_1")) {
                        muteString = "lconst_0";
                    } else if (opcodeString.equals("fconst_1") || opcodeString.equals("fconst_2")) {
                        muteString = "fconst_0";
                    } else if (opcodeString.equals("fconst_0")) {
                        muteString = "fconst_1";
                    } else if (opcodeString.equals("dconst_0")) {
                        muteString ="dconst_1";
                    } else if (opcodeString.equals("dconst_1")) {
                        muteString = "dconst_0";
                    } else {
                        // System.out.println("THERE IS NO MUTE STRING FOR: " + muteString);
                    }
                    

                    instructions.set(instructions.get(instructionLoc), new InsnNode(getOpcodeInt(muteString)));
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }
            case UOI:
                for (int i = 0; i < instructionData.size(); i++) {
                    InstructionData instruction = instructionData.get(i);
                    if (instruction.allowedMutations.contains(MutationTypes.UOI)) {
                        validIndexes.add(i);
                    }
                }
                if(validIndexes.size() > 0) {
                    int instructionLoc = validIndexes.get(rand.nextInt(validIndexes.size()));
                    
                    VarInsnNode variableInstruction = (VarInsnNode) instructions.get(instructionLoc);
                    int random = rand.nextInt(4);
                    int variable = variableInstruction.var;

                    if (random ==0) {
                        // i++
                        instructions.insert(instructions.get(instructionLoc), new IincInsnNode(1, variable));
                    } else if (random ==1) {
                        // i--
                        instructions.insert(instructions.get(instructionLoc), new IincInsnNode(-1, variable));
                    } else if (random ==2) {
                        //++i
                        instructions.insertBefore(instructions.get(instructionLoc), new IincInsnNode(1, variable));
                    } else {
                        // i--
                        instructions.insertBefore(instructions.get(instructionLoc), new IincInsnNode(-1, variable));
                    }
                    return true;
                } else {
                    return performMutation(method, ++recursionCount);
                }

                // EMPTY_RETURNS, -- change method return to appropriate null value
                // ABS, -- negate value
                // CRCR, -- other inline const mutator
            
        }

        return false;

    }  
}