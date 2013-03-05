package test;

import java.util.ArrayList;

import org.junit.*;
import static org.junit.Assert.*;

import org.societies.context.similarity.impl.*;

public class TestCSE {

	/**
	 * @param args
	 */


	@Test
	public void testOccupation(){
		System.out.println("test Occupation");
		ContextSimilarityEvaluator cse = new ContextSimilarityEvaluator();
		String[] ids = {"jtest1"};//,"jtest2"};
		ArrayList<String> attrib = new ArrayList<String>();
		attrib.add("occupation");
		evaluationResult ie = cse.evaluateSimilarity(ids, attrib);

		
	}
}
