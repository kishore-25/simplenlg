package simplenlg.test.lexicon;


import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import simplenlg.features.Feature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Inflection;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.NIHDBLexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 * Tests on the use of spelling and inflectional variants, using the NIHDBLexicon.
 * @author bertugatt
 *
 */
public class LexicalVariantsTests extends TestCase {

	// lexicon object -- an instance of Lexicon
	NIHDBLexicon lexicon = null;

	//factory for phrases
	NLGFactory factory;
	
	//realiser
	Realiser realiser;
	
	// DB location -- change this to point to the lex access data dir
	static String DB_FILENAME = "A:\\corpora\\LEX\\lexAccess2011\\data\\HSqlDb\\lexAccess2011";

	@Override
	@Before
	/*
	 * * Sets up the accessor and runs it -- takes ca. 26 sec
	 */
	public void setUp() {
		this.lexicon = new NIHDBLexicon(DB_FILENAME);
		this.factory = new NLGFactory(lexicon);
		this.realiser = new Realiser(this.lexicon);
	}

	/**
	 * Close the lexicon
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();

		if (lexicon != null)
			lexicon.close();
	}
	
//	/**
//	 * check that spelling variants are properly set
//	 */
//	@Test
//	public void testSpellingVariants() {
//		WordElement asd = lexicon.getWord("Adams-Stokes disease");
//		List<String> spellVars = asd
//				.getFeatureAsStringList(LexicalFeature.SPELL_VARS);
//		Assert.assertTrue(spellVars.contains("Adams Stokes disease"));
//		Assert.assertTrue(spellVars.contains("Adam-Stokes disease"));
//		Assert.assertEquals(2, spellVars.size());
//		Assert.assertEquals(asd.getBaseForm(), asd
//				.getFeatureAsString(LexicalFeature.DEFAULT_SPELL));
//
//		//default spell variant is baseform
//		Assert.assertEquals("Adams-Stokes disease", asd
//				.getDefaultSpellingVariant());		
//		
//		//default spell variant changes
//		asd.setDefaultSpellingVariant("Adams Stokes disease");
//		Assert.assertEquals("Adams Stokes disease", asd
//				.getDefaultSpellingVariant());
//	}
//	
//	/**
//	 * 
//	 */
//	public void testSpellingVariantWithInflection() {
//		realiser.setDebugMode(true);
//		WordElement word = lexicon.getWord("formalization");
//		List<String> spellVars = word.getSpellingVariants();
//		Assert.assertTrue(spellVars.contains("formalisation"));
//		Assert.assertEquals(Inflection.REGULAR, word.getDefaultInflectionalVariant());
//		//hydro.setDefaultSpellingVariant("hydroxy-benzonitrile");
//		NPPhraseSpec np = factory.createNounPhrase("the", "formalization");
//		//NPPhraseSpec np = factory.createNounPhrase(lexicon.getWord("the"), lexicon.getWord("formalization"));
//		np.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
//		System.out.println(realiser.realise(np));
//	}
	
	/**
	 * Test the inflectional variants for a verb.
	 */
	public void testVerbInflectionalVariants() {
		WordElement word = lexicon.getWord("lie", LexicalCategory.VERB);
		Assert.assertEquals(Inflection.REGULAR, word.getDefaultInflectionalVariant());
		
		//default past is "lied"
		InflectedWordElement infl = new InflectedWordElement(word);
		infl.setFeature(Feature.TENSE, Tense.PAST);
		String past = realiser.realise(infl).getRealisation();		
		Assert.assertEquals("lied", past);
		
		//switch to irregular
		word.setDefaultInflectionalVariant(Inflection.IRREGULAR);
		infl = new InflectedWordElement(word);		
		infl.setFeature(Feature.TENSE, Tense.PAST);		
		past = realiser.realise(infl).getRealisation();		
		Assert.assertEquals("lay", past);
		
		//switch back to regular
		word.setDefaultInflectionalVariant(Inflection.REGULAR);
		Assert.assertEquals(null, word.getFeature(LexicalFeature.PAST));
		infl = new InflectedWordElement(word);	
		infl.setFeature(Feature.TENSE, Tense.PAST);		
		past = realiser.realise(infl).getRealisation();		
		Assert.assertEquals("lied", past);
	}
	
	/**
	 * Test inflectional variants for nouns
	 */
	public void testNounInflectionalVariants() {
		WordElement word = lexicon.getWord("sanctum", LexicalCategory.NOUN);
		Assert.assertEquals(Inflection.REGULAR, word.getDefaultInflectionalVariant());
		
		//reg plural shouldn't be stored
		Assert.assertEquals(null, word.getFeature(LexicalFeature.PLURAL));
		InflectedWordElement infl = new InflectedWordElement(word);
		infl.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		String plur = realiser.realise(infl).getRealisation();		
		Assert.assertEquals("sanctums", plur);
		
		//switch to glreg
		word.setDefaultInflectionalVariant(Inflection.GRECO_LATIN_REGULAR);
		infl = new InflectedWordElement(word);
		infl.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		plur = realiser.realise(infl).getRealisation();		
		Assert.assertEquals("sancta", plur);
		
		//and back to reg
		word.setDefaultInflectionalVariant(Inflection.REGULAR);
		infl = new InflectedWordElement(word);
		infl.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
		plur = realiser.realise(infl).getRealisation();		
		Assert.assertEquals("sanctums", plur);
		
	}

}