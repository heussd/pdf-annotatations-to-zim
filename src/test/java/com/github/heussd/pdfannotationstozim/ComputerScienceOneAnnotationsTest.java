package com.github.heussd.pdfannotationstozim;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ComputerScienceOneAnnotationsTest {

	private PDDocument document;
	private List<PDPage> allPages;

	@Before
	public void setUp() throws Exception {
		URL demoPdf = ComputerScienceOneAnnotationsTest.class.getClassLoader().getResource("ComputerScienceOne.pdf");
		System.out.println("Loading " + demoPdf);
		document = PDDocument.load(demoPdf);
		allPages = document.getDocumentCatalog().getAllPages();
	}

	@Test
	public void testNewlineSelection() throws Exception {
		final String expectation = "It involves studying and understanding computational processes and the development of algorithms and techniques and how they apply to problems. Problem solving skills are not something that can be distilled down into a single stepby-step process.";
		System.out.println(expectation);

		List<String> annotations = ExtractAnnotations.retrieveAnnotationText(allPages.get(35));
		assertTrue("Received null", annotations != null);
		assertTrue("No annotations retrieved", annotations.size() > 0);
		assertTrue("But found " + annotations, annotations.contains(expectation));
	}

	@Test
	public void testInlineSelection() throws Exception {
		final String expectation = "Without a main function, the code may still be useful:";
		System.out.println(expectation);

		List<String> annotations = ExtractAnnotations.retrieveAnnotationText(allPages.get(40));
		assertTrue("Received null", annotations != null);
		assertTrue("No annotations retrieved", annotations.size() > 0);
		assertTrue("But found " + annotations, annotations.contains(expectation));
	}

	@Test
	public void testPlainText() throws Exception {
		final String expectation = "Exercise 2.1. Write a program that calculates mileage deduction for income tax using the standard rate of $0.575 per mile. Your program will read in a beginning and ending odometer reading and calculate the difference and total deduction. Take care that your output is in whole cents. An example run of the program may look like the following.";
		System.out.println(expectation);

		List<String> annotations = ExtractAnnotations.retrieveAnnotationText(allPages.get(84));

		assertTrue("Received null", annotations != null);
		assertTrue("No annotations retrieved", annotations.size() > 0);
		assertTrue("But found " + annotations, annotations.contains(expectation));
	}

	@Test
	public void testHyphenRemoval() throws Exception {
		final String expectation = "printf()-style Methods in Several Languages. Languages support formatting directly to the Standard Output as well as to strings that can be further used or manipulated. Most languages also support printf()-style formatting to other output mechanisms (streams, files, etc.).";
		System.out.println(expectation);

		List<String> annotations = ExtractAnnotations.retrieveAnnotationText(allPages.get(76));

		assertTrue("Received null", annotations != null);
		assertTrue("No annotations retrieved", annotations.size() > 0);
		assertTrue("But found " + annotations, annotations.contains(expectation));
	}

	@After
	public void closePdf() {
		try {
			document.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
