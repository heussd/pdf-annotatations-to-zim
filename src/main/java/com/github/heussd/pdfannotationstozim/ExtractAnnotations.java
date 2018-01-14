package com.github.heussd.pdfannotationstozim;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.util.PDFTextStripperByArea;

public class ExtractAnnotations {

	public List<String> retrieveAnnotations(URL url) {
		List<String> annotations = new ArrayList<String>();

		try {
			PDDocument document = PDDocument.load(url);
			List<PDPage> allPages = document.getDocumentCatalog().getAllPages();

			for (Iterator<PDPage> iterator = allPages.iterator(); iterator.hasNext();) {
				PDPage pdPage = iterator.next();
				annotations.addAll(retrieveAnnotationText(pdPage));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return annotations;
	}

	public static List<String> retrieveAnnotationText(PDPage pdPage) throws IOException {
		List<String> annotations = new ArrayList<String>();
		
		
		
		for (PDAnnotation annotation : pdPage.getAnnotations()) {
			String rawStringAnnotation = null;

			if (annotation.getSubtype().equals("Highlight")) {
				rawStringAnnotation = retrieveHighlight(annotation, pdPage);
				rawStringAnnotation = clean(rawStringAnnotation);

				System.out.println(rawStringAnnotation);

				annotations.add(rawStringAnnotation);
			} else {
				// System.out.println("Dont know how to handle " + annotation.getSubtype());
			}
		}
		return annotations;
	}

	public static String clean(final String annotationText) {
		String cleanedString = annotationText;
		cleanedString = cleanedString.replaceAll(" -", "-");
		cleanedString = cleanedString.replaceAll("-\n", "");
		cleanedString = cleanedString.replaceAll("\n", " ");
		cleanedString = cleanedString.trim();
		return cleanedString;
	}

	/**
	 * Taken from https://stackoverflow.com/questions/32608083/not-able-to-read-the-exact-text-highlighted-across-the-lines
	 */
	public static String retrieveHighlight(PDAnnotation annotation, PDPage page) throws IOException {
		assert annotation.getSubtype().equals("Highlight");

		String str = null;

		PDFTextStripperByArea stripperByArea = new PDFTextStripperByArea();

		COSArray quadsArray = (COSArray) annotation.getDictionary().getDictionaryObject(COSName.getPDFName("QuadPoints"));

		for (int j = 1, k = 0; j <= (quadsArray.size() / 8); j++) {

			COSFloat ULX = (COSFloat) quadsArray.get(0 + k);
			COSFloat ULY = (COSFloat) quadsArray.get(1 + k);
			COSFloat URX = (COSFloat) quadsArray.get(2 + k);
			COSFloat URY = (COSFloat) quadsArray.get(3 + k);
			COSFloat LLX = (COSFloat) quadsArray.get(4 + k);
			COSFloat LLY = (COSFloat) quadsArray.get(5 + k);

			k += 8;

			float ulx = ULX.floatValue() - 1; // upper left x.
			float uly = ULY.floatValue(); // upper left y.
			float width = URX.floatValue() - LLX.floatValue(); // calculated by upperRightX - lowerLeftX.
			float height = URY.floatValue() - LLY.floatValue(); // calculated by upperRightY - lowerLeftY.

			PDRectangle pageSize = page.getMediaBox();
			uly = pageSize.getHeight() - uly;

			Rectangle2D.Float rectangle_2 = new Rectangle2D.Float(ulx, uly, width, height);
			stripperByArea.addRegion("highlightedRegion", rectangle_2);
			stripperByArea.extractRegions(page);
			String highlightedText = stripperByArea.getTextForRegion("highlightedRegion");

			if (j > 1) {
				str = str.concat(highlightedText);
			} else {
				str = highlightedText;
			}
		}
		return str;
	}
}
