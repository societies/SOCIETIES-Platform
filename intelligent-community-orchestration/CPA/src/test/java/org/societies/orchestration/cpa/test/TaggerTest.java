package org.societies.orchestration.cpa.test;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 05/10/13
 * Time: 00:32
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class TaggerTest {
    public static void main(String[] args) throws Exception {
        // Initialize the tagger
        MaxentTagger tagger = new MaxentTagger("english-left3words-distsim.tagger");

        // The sample string
        String sample = "I am a good boy";
        String[] tokens = sample.split(" ");

        for(int i=0;i<tokens.length;i++){
            String tagged = tagger.tagString(tokens[i]);
            System.out.println(tagged);
        }

/*        if (args.length != 2) {
            System.err.println("usage: java TaggerDemo modelFile fileToTag");
            return;
        }
        MaxentTagger tagger = new MaxentTagger(args[0]);
        TokenizerFactory<CoreLabel> ptbTokenizerFactory =
                PTBTokenizer.factory(new CoreLabelTokenFactory(), "untokenizable=noneKeep");
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "utf-8"));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out, "utf-8"));
        DocumentPreprocessor documentPreprocessor = new DocumentPreprocessor(r);
        documentPreprocessor.setTokenizerFactory(ptbTokenizerFactory);
        for (List<HasWord> sentence : documentPreprocessor) {
            List<TaggedWord> tSentence = tagger.tagSentence(sentence);
            pw.println(Sentence.listToString(tSentence, false));
        }*/


    }
}
