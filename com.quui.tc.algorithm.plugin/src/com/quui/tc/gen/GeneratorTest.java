package com.quui.tc.gen;

import java.io.File;

import junit.framework.TestCase;

public class GeneratorTest extends TestCase {
    public void testGen(){
        String problem = Util.getText(new File("testdata/problem1.txt"));
        CodeGen gen = new CodeGen(problem);
        System.out.println(gen.createSolutionText());
        System.out.println(gen.createTestText());
    }
}
