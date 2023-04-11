# Hyperparameter Directed Mutation Testing

# Run Guide
1. Update Run.java to .class file to mutate
2. Update Mutator.java (line 245) to the mutator to use
3. Run `mvn package`
4. Run `java -cp target/hdmt-1.12.2-SNAPSHOT-jar-with-dependencies.jar org.hdmt.Run`
## Repo Structure

### Java Files (src/main/java/org/hdmt)
1. Config.java: Loads different configuration mappings. Helps determine which opcodes are eligible for which mutations and which opcode descriptions are associated with which opcodes.
2. InstructionData.java: Java struct which collects data on each ASM instruction. This data helps determine which mutations we can do.
3. Mutator.java: Main functionality of HDMT. Processes java.class file and performs mutations using ASM code on single java class.
4. Run.java: Wrapper function to run Mutator class. Can use to apply across multiple classes and collect processing info.

### yaml Files (src/main/yamls)
1. class_method_config.yaml: Configurations to override project_config.yaml defaults at the class and/or method level.
2. OP_CODE_MAP.yaml: Mapping of opcode hexidecimal to opcode name.
3. project_config.yaml: Default configurations for project level mutation testing hyperparameters.