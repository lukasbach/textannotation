package edu.kit.textannotation.tests.selectionstrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.junit.Before;
import org.junit.Test;

import edu.kit.textannotation.annotationplugin.selectionstrategy.DefaultSelectionStrategy;
import edu.kit.textannotation.annotationplugin.selectionstrategy.SelectionStrategy;
import edu.kit.textannotation.annotationplugin.selectionstrategy.SentenceSelectionStrategy;
import edu.kit.textannotation.annotationplugin.selectionstrategy.WordSelectionStrategy;

public class SelectionStrategyTest {
	String content;
	IDocument document;
	
	@Before
	public void setup() {
		content = "Lorem ipsum. Doloret amet.";
		document = new Document(content);
	}
	
	@Test
	public void testDefaultStrategy() {
		Region result = (new DefaultSelectionStrategy()).evaluateSelection(new Region(7, 2), document);
		assertEquals(7, result.getOffset());
		assertEquals(2, result.getLength());
	}

	@Test
	public void testWordStrategy() {
		Region result = (new WordSelectionStrategy()).evaluateSelection(new Region(7, 2), document);
		assertEquals(6, result.getOffset());
		assertEquals(4, result.getLength());
	}

	@Test
	public void testSentenceStrategy() {
		Region result = (new SentenceSelectionStrategy()).evaluateSelection(new Region(7, 2), document);
		assertEquals(0, result.getOffset());
		assertEquals(11, result.getLength());
	}
	
	@Test
	public void testStrategyMetadataIsDefined() {
		List<SelectionStrategy> selectionStrategies = Arrays.asList(
				new DefaultSelectionStrategy(),
				new WordSelectionStrategy(),
				new SentenceSelectionStrategy()
		);
		
		for (SelectionStrategy strategy: selectionStrategies) {
			assertNotNull(strategy.getId());
			assertNotNull(strategy.getName());
			assertNotNull(strategy.getDescription());
			
			assertTrue(strategy.getId().length() > 0);
			assertTrue(strategy.getName().length() > 0);
			assertTrue(strategy.getDescription().length() > 0);
		}
	}
}
