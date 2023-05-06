import pathlib
import os
from processResults import process_results

class_build_directory = "/Users/mgartenhaus/CS527/jsoup_eval/"
class_file_directory = "/Users/mgartenhaus/CS527/jsoup_eval/target/classes/org/jsoup/nodes/"
hdmt_directory = "/Users/mgartenhaus/CS527/HDMT/"
output_directory = "/Users/mgartenhaus/CS527/HDMT/results/03_per_instruction/"

if class_file_directory.endswith(".class"):
    classfiles = [class_file_directory]
else:
    class_file_directory_path = pathlib.Path(class_file_directory)
    classfiles = [str(path) for path in class_file_directory_path.rglob("*.class")]

i = 0
print(classfiles)
for file in classfiles[i:]:
    print(f"{i}-Running {file}")
    i+=1
    if "$" in file:
        continue
    output_file = output_directory + file.replace(class_file_directory, "").replace(".class", ".py")

    os.system(f"cd {class_build_directory}; mvn compile")
    os.system(f"cd {hdmt_directory}; java -cp target/hdmt-1.12.2-SNAPSHOT-jar-with-dependencies.jar org.hdmt.Run {file} {output_file}")
     

process_results(output_directory)


