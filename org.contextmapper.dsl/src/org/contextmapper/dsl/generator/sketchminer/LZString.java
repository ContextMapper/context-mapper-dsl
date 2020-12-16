/*
 * Copyright (c) 2016 rufushuang
 * 
 * Notice: This is a copy from https://github.com/rufushuang/lz-string4java and the class
 * has been reduced to the methods we need.
 * 
 * LZString4Java By Rufus Huang 
 * https://github.com/rufushuang/lz-string4java
 * MIT License
 * 
 * Port from original JavaScript version by pieroxy 
 * https://github.com/pieroxy/lz-string
 */
package org.contextmapper.dsl.generator.sketchminer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is copied from https://github.com/rufushuang/lz-string4java.
 *
 * Copyright (c) 2016 rufushuang
 */
public class LZString {

	private static char[] keyStrUriSafe = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-$".toCharArray();

	public static String compressToEncodedURIComponent(String input) {
		if (input == null)
			return "";
		return LZString._compress(input, 6, new CompressFunctionWrapper() {
			@Override
			public char doFunc(int a) {
				return keyStrUriSafe[a];
			}
		});
	}

	private static abstract class CompressFunctionWrapper {
		public abstract char doFunc(int i);
	}

	private static String _compress(String uncompressedStr, int bitsPerChar, CompressFunctionWrapper getCharFromInt) {
		if (uncompressedStr == null)
			return "";
		int i, value;
		Map<String, Integer> context_dictionary = new HashMap<String, Integer>();
		Set<String> context_dictionaryToCreate = new HashSet<String>();
		String context_c = "";
		String context_wc = "";
		String context_w = "";
		int context_enlargeIn = 2; // Compensate for the first entry which should not count
		int context_dictSize = 3;
		int context_numBits = 2;
		StringBuilder context_data = new StringBuilder(uncompressedStr.length() / 3);
		int context_data_val = 0;
		int context_data_position = 0;
		int ii;

		for (ii = 0; ii < uncompressedStr.length(); ii += 1) {
			context_c = String.valueOf(uncompressedStr.charAt(ii));
			if (!context_dictionary.containsKey(context_c)) {
				context_dictionary.put(context_c, context_dictSize++);
				context_dictionaryToCreate.add(context_c);
			}

			context_wc = context_w + context_c;
			if (context_dictionary.containsKey(context_wc)) {
				context_w = context_wc;
			} else {
				if (context_dictionaryToCreate.contains(context_w)) {
					if (context_w.charAt(0) < 256) {
						for (i = 0; i < context_numBits; i++) {
							context_data_val = (context_data_val << 1);
							if (context_data_position == bitsPerChar - 1) {
								context_data_position = 0;
								context_data.append(getCharFromInt.doFunc(context_data_val));
								context_data_val = 0;
							} else {
								context_data_position++;
							}
						}
						value = context_w.charAt(0);
						for (i = 0; i < 8; i++) {
							context_data_val = (context_data_val << 1) | (value & 1);
							if (context_data_position == bitsPerChar - 1) {
								context_data_position = 0;
								context_data.append(getCharFromInt.doFunc(context_data_val));
								context_data_val = 0;
							} else {
								context_data_position++;
							}
							value = value >> 1;
						}
					} else {
						value = 1;
						for (i = 0; i < context_numBits; i++) {
							context_data_val = (context_data_val << 1) | value;
							if (context_data_position == bitsPerChar - 1) {
								context_data_position = 0;
								context_data.append(getCharFromInt.doFunc(context_data_val));
								context_data_val = 0;
							} else {
								context_data_position++;
							}
							value = 0;
						}
						value = context_w.charAt(0);
						for (i = 0; i < 16; i++) {
							context_data_val = (context_data_val << 1) | (value & 1);
							if (context_data_position == bitsPerChar - 1) {
								context_data_position = 0;
								context_data.append(getCharFromInt.doFunc(context_data_val));
								context_data_val = 0;
							} else {
								context_data_position++;
							}
							value = value >> 1;
						}
					}
					context_enlargeIn--;
					if (context_enlargeIn == 0) {
						context_enlargeIn = powerOf2(context_numBits);
						context_numBits++;
					}
					context_dictionaryToCreate.remove(context_w);
				} else {
					value = context_dictionary.get(context_w);
					for (i = 0; i < context_numBits; i++) {
						context_data_val = (context_data_val << 1) | (value & 1);
						if (context_data_position == bitsPerChar - 1) {
							context_data_position = 0;
							context_data.append(getCharFromInt.doFunc(context_data_val));
							context_data_val = 0;
						} else {
							context_data_position++;
						}
						value = value >> 1;
					}

				}
				context_enlargeIn--;
				if (context_enlargeIn == 0) {
					context_enlargeIn = powerOf2(context_numBits);
					context_numBits++;
				}
				// Add wc to the dictionary.
				context_dictionary.put(context_wc, context_dictSize++);
				context_w = context_c;
			}
		}

		// Output the code for w.
		if (!context_w.isEmpty()) {
			if (context_dictionaryToCreate.contains(context_w)) {
				if (context_w.charAt(0) < 256) {
					for (i = 0; i < context_numBits; i++) {
						context_data_val = (context_data_val << 1);
						if (context_data_position == bitsPerChar - 1) {
							context_data_position = 0;
							context_data.append(getCharFromInt.doFunc(context_data_val));
							context_data_val = 0;
						} else {
							context_data_position++;
						}
					}
					value = context_w.charAt(0);
					for (i = 0; i < 8; i++) {
						context_data_val = (context_data_val << 1) | (value & 1);
						if (context_data_position == bitsPerChar - 1) {
							context_data_position = 0;
							context_data.append(getCharFromInt.doFunc(context_data_val));
							context_data_val = 0;
						} else {
							context_data_position++;
						}
						value = value >> 1;
					}
				} else {
					value = 1;
					for (i = 0; i < context_numBits; i++) {
						context_data_val = (context_data_val << 1) | value;
						if (context_data_position == bitsPerChar - 1) {
							context_data_position = 0;
							context_data.append(getCharFromInt.doFunc(context_data_val));
							context_data_val = 0;
						} else {
							context_data_position++;
						}
						value = 0;
					}
					value = context_w.charAt(0);
					for (i = 0; i < 16; i++) {
						context_data_val = (context_data_val << 1) | (value & 1);
						if (context_data_position == bitsPerChar - 1) {
							context_data_position = 0;
							context_data.append(getCharFromInt.doFunc(context_data_val));
							context_data_val = 0;
						} else {
							context_data_position++;
						}
						value = value >> 1;
					}
				}
				context_enlargeIn--;
				if (context_enlargeIn == 0) {
					context_enlargeIn = powerOf2(context_numBits);
					context_numBits++;
				}
				context_dictionaryToCreate.remove(context_w);
			} else {
				value = context_dictionary.get(context_w);
				for (i = 0; i < context_numBits; i++) {
					context_data_val = (context_data_val << 1) | (value & 1);
					if (context_data_position == bitsPerChar - 1) {
						context_data_position = 0;
						context_data.append(getCharFromInt.doFunc(context_data_val));
						context_data_val = 0;
					} else {
						context_data_position++;
					}
					value = value >> 1;
				}

			}
			context_enlargeIn--;
			if (context_enlargeIn == 0) {
				context_enlargeIn = powerOf2(context_numBits);
				context_numBits++;
			}
		}

		// Mark the end of the stream
		value = 2;
		for (i = 0; i < context_numBits; i++) {
			context_data_val = (context_data_val << 1) | (value & 1);
			if (context_data_position == bitsPerChar - 1) {
				context_data_position = 0;
				context_data.append(getCharFromInt.doFunc(context_data_val));
				context_data_val = 0;
			} else {
				context_data_position++;
			}
			value = value >> 1;
		}

		// Flush the last char
		while (true) {
			context_data_val = (context_data_val << 1);
			if (context_data_position == bitsPerChar - 1) {
				context_data.append(getCharFromInt.doFunc(context_data_val));
				break;
			} else
				context_data_position++;
		}
		return context_data.toString();
	}

	private static int powerOf2(int power) {
		return 1 << power;
	}

}
