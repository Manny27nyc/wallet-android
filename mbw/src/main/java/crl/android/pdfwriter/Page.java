/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
//
//  Android PDF Writer
//  http://coderesearchlabs.com/androidpdfwriter
//
//  by Javier Santo Domingo (j-a-s-d@coderesearchlabs.com)
//

package crl.android.pdfwriter;

import java.util.ArrayList;

public class Page {

	private PDFDocument mDocument;
	private IndirectObject mIndirectObject;
	private ArrayList<IndirectObject> mPageFonts;
	private ArrayList<XObjectImage> mXObjects;
	private IndirectObject mPageContents;
	
	public Page(PDFDocument document) {
		mDocument = document;
		mIndirectObject = mDocument.newIndirectObject();
		mPageFonts = new ArrayList<IndirectObject>();
		mXObjects = new ArrayList<XObjectImage>();
		setFont(StandardFonts.SUBTYPE, StandardFonts.TIMES_ROMAN, StandardFonts.WIN_ANSI_ENCODING);
		mPageContents = mDocument.newIndirectObject();
		mDocument.includeIndirectObject(mPageContents);
	}
	
	public IndirectObject getIndirectObject() {
		return mIndirectObject;
	}
	
	private String getFontReferences() {
		StringBuilder builder = new StringBuilder();
		if (!mPageFonts.isEmpty()) {
			builder.append("    /Font <<\n");
			int x = 0;
			for (IndirectObject lFont : mPageFonts) {
				builder.append("      /F" + Integer.toString(++x) + " " + lFont.getIndirectReference() + "\n");
			}
			builder.append("    >>\n");
		}
		return builder.toString();
	}

	private String getXObjectReferences() {
		StringBuilder builder = new StringBuilder("");
		if (!mXObjects.isEmpty()) {
			builder.append("    /XObject <<\n");
			for (XObjectImage xObj : mXObjects) {
				builder.append("      ").append(xObj.asXObjectReference()).append("\n");
			}
			builder.append("    >>\n");
		}
		return builder.toString();
	}
	
	public void render(String pagesIndirectReference) {
		mIndirectObject.setDictionaryContent(
			"  /Type /Page\n  /Parent " + pagesIndirectReference + "\n" +
			"  /Resources <<\n" + getFontReferences() + getXObjectReferences() + "  >>\n" +
			"  /Contents " + mPageContents.getIndirectReference() + "\n"
		);
	}
	
	public void setFont(String subType, String baseFont) {
		IndirectObject lFont = mDocument.newIndirectObject();
		mDocument.includeIndirectObject(lFont);
		lFont.setDictionaryContent("  /Type /Font\n  /Subtype /" + subType + "\n  /BaseFont /" + baseFont + "\n");
		mPageFonts.add(lFont);
	}

	public void setFont(String subType, String baseFont, String encoding) {
		IndirectObject lFont = mDocument.newIndirectObject();
		mDocument.includeIndirectObject(lFont);
		lFont.setDictionaryContent("  /Type /Font\n  /Subtype /" + subType + "\n  /BaseFont /" + baseFont + "\n  /Encoding /" + encoding + "\n");
		mPageFonts.add(lFont);
	}
	
	private void addContent(String content) {
      // Optimized by Jan. removed unnecessary copying 
      // This saves time and memory resources significantly!
		mPageContents.addStreamContent(content);
		//String streamContent = mPageContents.getStreamContent();
		mPageContents.setDictionaryContent("  /Length " + Integer.toString(mPageContents.getStreamContentSize()) + "\n");
		//mPageContents.setStreamContent(streamContent);
	}
	
	public void addRawContent(String rawContent) {
		addContent(rawContent);
	}

	public void addText(int leftPosition, int topPositionFromBottom, int fontSize, String text) {
		addText(leftPosition, topPositionFromBottom, fontSize, text, Transformation.DEGREES_0_ROTATION);
	}
	
	public void addText(int leftPosition, int topPositionFromBottom, int fontSize, String text, String transformation) {
		addContent(
			"BT\n" +
			transformation + " " + Integer.toString(leftPosition) + " " + Integer.toString(topPositionFromBottom) + " Tm\n" +
			"/F" + Integer.toString(mPageFonts.size()) + " " + Integer.toString(fontSize) + " Tf\n" +
			"(" + text + ") Tj\n" +
			"ET\n"
		);
	}

	public void addTextAsHex(int leftPosition, int topPositionFromBottom, int fontSize, String hex) {
		addTextAsHex(leftPosition, topPositionFromBottom, fontSize, hex, Transformation.DEGREES_0_ROTATION);
	}
	
	public void addTextAsHex(int leftPosition, int topPositionFromBottom, int fontSize, String hex, String transformation) {
		addContent(
			"BT\n" +
			transformation + " " + Integer.toString(leftPosition) + " " + Integer.toString(topPositionFromBottom) + " Tm\n" +
			"/F" + Integer.toString(mPageFonts.size()) + " " + Integer.toString(fontSize) + " Tf\n" +
			"<" + hex + "> Tj\n" +
			"ET\n"
		);
	}
	
	public void addLine(int fromLeft, int fromBottom, int toLeft, int toBottom) {
		addContent(
			Integer.toString(fromLeft) + " " + Integer.toString(fromBottom) + " m\n" +
			Integer.toString(toLeft) + " " + Integer.toString(toBottom) + " l\nS\n"
		);
	}
	
	public void addRectangle(int fromLeft, int fromBottom, int toLeft, int toBottom) {
		addContent(
			Integer.toString(fromLeft) + " " + Integer.toString(fromBottom) + " " +
			Integer.toString(toLeft) + " " + Integer.toString(toBottom) + " re\nS\n"
		);
	}

   public void addFilledRectangle(double fromLeft, double fromBottom, double toLeft, double toBottom) {
      addContent(
         Double.toString(fromLeft) + " " + Double.toString(fromBottom) + " " +
         Double.toString(toLeft) + " " + Double.toString(toBottom) + " re\nf\n"
      );
   }
	
	private String ensureXObjectImage(XObjectImage xObject) {
		for (XObjectImage x : mXObjects) {
			if (x.getId().equals(xObject.getId())) {
				return x.getName();
			}
		}
		mXObjects.add(xObject);
		xObject.appendToDocument();
		return xObject.getName();
	}
	
	public void addImage(int fromLeft, int fromBottom, int width, int height, XObjectImage xImage, String transformation){
		final String name = ensureXObjectImage(xImage);
		final String translate = "1 0 0 1 " + fromLeft + " " + fromBottom;
		final String scale = "" + width + " 0 0 " + height + " 0 0";
		final String rotate = transformation + " 0 0";
		addContent(
			"q\n" +
			translate + " cm\n" +
			rotate + " cm\n" +
			scale + " cm\n" +
			name + " Do\n" +
			"Q\n"
		);
	}
}
