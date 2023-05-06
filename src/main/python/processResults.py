import pathlib
import importlib
import pandas as pd
import collections
import functools
import operator

def mutantAggregate(series):
    return dict(functools.reduce(operator.add, map(collections.Counter, list(series))))

def process_results(output_directory):

    output_directory_path = pathlib.Path(output_directory)

    outputfiles = [str(path) for path in output_directory_path.rglob("*.py")]

    config = dict()

    for file in outputfiles:
        spec = importlib.util.spec_from_file_location(file, file)
        mod = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(mod)

        config = {**config,**mod.config}

    classInfo = dict()

    for clazz in config.keys():
        if clazz != "aggregates":
            classInfo[clazz] = {    
                "totalTime": 0,
                "totalMutations": 0,
                "totalFailures": 0,
                "mutationTypeInfo": dict()
            }
            
            for key in config[clazz].keys():
                if key != "aggregates":
                    aggregates = config[clazz][key]["aggregates"]
                    classInfo[clazz]["totalFailures"] += int(aggregates["totalFailures"])
                    classInfo[clazz]["totalMutations"] += int(aggregates["totalMutations"])
                    classInfo[clazz]["totalTime"] += int(aggregates["totalTime"])
                    
                    mutationTypeCount = config[clazz][key]["mutationTypeCount"]
                    mutationTypeFailureCount = config[clazz][key]["mutationTypeFailureCount"]
                    
                    for muteType in mutationTypeCount.keys():
                        if (muteType not in classInfo[clazz]["mutationTypeInfo"].keys()):
                            classInfo[clazz]["mutationTypeInfo"][muteType] = {"mutants" : 0, "caught_mutants" : 0}
                        
                        classInfo[clazz]["mutationTypeInfo"][muteType]["mutants"] += int(mutationTypeCount[muteType])
                        classInfo[clazz]["mutationTypeInfo"][muteType]["caught_mutants"] += int(mutationTypeFailureCount[muteType])
        
            
    df = pd.DataFrame.from_dict(classInfo, orient="index")  
    df = pd.concat([df.drop(['mutationTypeInfo'], axis=1), df['mutationTypeInfo'].apply(pd.Series)], axis="columns")
    for col in df.columns:
        df[col] = df[col].apply(
            lambda x: x if x == x else {"mutants":0, "caught_mutants":0}
        )

    mutations_per_class = df[["totalTime", "totalMutations", "totalFailures"]].copy()
    mutations_per_class["mutationScore"] = mutations_per_class["totalFailures"]/mutations_per_class["totalMutations"]

    mutations_per_class.to_csv(f"{output_directory}/mutations_per_class.csv")

    mutations_per_type = dict()
    for col in df.columns[4:]:
        mutations_per_type[col] = mutantAggregate(df[col])

    mutations_per_type = pd.DataFrame.from_dict(mutations_per_type, orient="index")
    mutations_per_type["mutation_score"] = mutations_per_type["caught_mutants"] / mutations_per_type["mutants"]

    mutations_per_type.to_csv(f"{output_directory}/mutations_per_type.csv")