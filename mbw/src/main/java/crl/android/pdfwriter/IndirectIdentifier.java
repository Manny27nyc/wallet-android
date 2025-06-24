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

public class IndirectIdentifier extends Base {

	private int mNumber;
	private int mGeneration;

	public IndirectIdentifier() {
		clear();
	}
	
	public void setNumber(int Number) {
		this.mNumber = Number;
	}

	public int getNumber() {
		return mNumber;
	}

	public void setGeneration(int Generation) {
		this.mGeneration = Generation;
	}

	public int getGeneration() {
		return mGeneration;
	}
	
	@Override
	public void clear() {
		mNumber = 0;
		mGeneration = 0;
	}

	@Override
	public String toPDFString() {
		return Integer.toString(mNumber) + " " + Integer.toString(mGeneration);
	}
}
