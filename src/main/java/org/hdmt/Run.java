package org.hdmt;

import java.io.IOException;

import org.hdmt.Mutator;

public class Run{
    public static void main(String args[]) throws IOException {
        new Mutator("/Users/mgartenhaus/CS527/jsoup_eval/target/classes/org/jsoup/nodes/Node.class"); //helper/DataUtil.class");
        System.out.println("hello world");
    }
}