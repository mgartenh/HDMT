package org.hdmt;

import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.InterruptedException;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.HashMap;
import java.time.Instant;
import java.io.FileWriter;


import org.hdmt.Mutator;

public class Run{

    public static List<String> listClasses(String directory) throws IOException {
        List<String> result= new ArrayList<>();

        if (directory.endsWith(".class")) {
            result.add(directory);
        } else {
            File directoryPath = new File(directory);
            File fileList[] = directoryPath.listFiles();

            for (File file: fileList) {
                result.addAll(listClasses(file.getAbsolutePath()));
            }
        }

        return result;
    }

    public static  Map<String , Object> loadConfig(String path) throws IOException {
        FileInputStream yamlFile = new FileInputStream(path);
    
        Map<String , Object> yamlMaps = (Map<String, Object>) new Yaml().load(yamlFile);

        yamlFile.close();

        return yamlMaps;
    }

    public static String stringMap(Map testing, int recursive) {

        // testing = new HashMap<>();

        String tabbing = "";
        for (int i = 0; i < recursive; i++) {
            tabbing += "\t";
        }

        String result = "{";

        for (Object key: testing.keySet()) {
            Object value = testing.get(key);

            String keyString = "'" + key.toString() + "'";
            String valueString;

            if (value instanceof Map) {
                valueString = stringMap((Map) value, recursive + 1) ;
            } else {
                valueString = "'" + value.toString() + "'";
            }
            result += "\n\t" + tabbing + keyString + " : "+ valueString + ",";
        }

        if (testing.keySet().size() > 0) {
            result += "\n" + tabbing + "}";
        } else {
            result += "}";
        }

        return result;

    }

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        // 0. get classes to mutate
        // "/Users/mgartenhaus/CS527/jsoup_eval/target/classes/org/jsoup/internal/"
        String classLoc = args[0];
        String outputLoc = args[1];

        List<String> projectClasses = listClasses(classLoc);
        
        System.out.println(projectClasses);

        // 1. load config
        Map<String , Object> projectConfig = loadConfig("src/main/yamls/project_config.yaml");
        Map<String , Object> classesConfig = loadConfig("src/main/yamls/class_method_config.yaml");

        // System.out.println(projectConfig);

        Map<String, Float> totalMutations = (Map<String, Float>) projectConfig.get("TOTAL_MUTATIONS");
        Map<String, Boolean> enableMutators = (Map<String, Boolean>) projectConfig.get("ENABLE_MUTATORS");

        Map<String, Map> projectResult = new HashMap<>();


        for (String projectClass: projectClasses) {
            System.out.printf("Running %s\n", projectClass);
            String[] info = projectClass.replace(".class", "").split("/");
            String className = info[info.length - 1];

            System.out.println(className);

            Map<String, Object> classConfig = classesConfig;
            for (String val: info) {
                if (classConfig.containsKey(val)) {
                    classConfig = (Map<String, Object>) classConfig.get(val);
                }
            }
            if (classConfig.equals(classesConfig)) {
                classConfig = null;
            }

            Mutator classMutator= new Mutator(projectClass, className);

            Instant start = Instant.now();

            Map<String, Map> classResult = classMutator.mutationTest();
            Map<String, Integer> aggregates = new HashMap<>();

            Instant end = Instant.now();

            aggregates.put("totalTime", (int)((end.getEpochSecond() - start.getEpochSecond()) * 1000 + (end.getNano() - start.getNano()) / 1000000));

            classResult.put("aggregates", aggregates);


            projectResult.put(className, classResult);


        }

        new File(outputLoc).getParentFile().mkdirs();
        FileWriter finalOutput = new FileWriter(outputLoc);

        finalOutput.write("config=" + stringMap(projectResult, 0));
        finalOutput.close();

        System.exit(0);

    }

}