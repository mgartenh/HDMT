package org.hdmt;

import java.util.List;
import java.util.ArrayList;
import org.hdmt.Mutator.MutationTypes;

public class InstructionData {
    public int opcode;
    public String opcodeString;
    public String instructionType;
    public String inputTypes;
    public String outputType;
    public String methodName;
    public List<MutationTypes> allowedMutations = new ArrayList<>();

    public int increment;
}