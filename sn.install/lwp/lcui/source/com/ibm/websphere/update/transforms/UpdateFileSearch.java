/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2011, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/* @copyright module */
package com.ibm.websphere.update.transforms;

import java.io.*;
import java.util.*;

/* 
 * ClassName: UpdateFileSearch
 * Abstract: This service provides utilities to filter search directory nodes and files based on
 * a specified query.  Wildcards are supported on both the directory and file level. 
 * 
 * History 1.2, 12/6/02
 *
 * 30-Aug-2002 Initial Version
 */
/**
 *  
 */
public class UpdateFileSearch {


	//********************************************************
	//  Debugging Utilities
	//********************************************************
    public static final String debugPropertyName = "com.ibm.websphere.update.transforms.debug" ;
	//********************************************************
	//  Debugging Utilities
	//********************************************************
    public static final String debugTrueValue = "true" ;
	//********************************************************
	//  Debugging Utilities
	//********************************************************
    public static final String debugFalseValue = "false" ;

    // Debugging support ...
    protected static boolean debug;

    static {
        String debugValue = System.getProperty(debugPropertyName);

        debug = ( (debugValue != null) && debugValue.equals(debugTrueValue) );
    }

    /**
	 * @return  the debug
	 * @uml.property  name="debug"
	 */
    public static boolean isDebug()
    {
        return debug;
    }

    public static void debug(String arg)
    {
        if ( !debug )
            return;

        System.out.println(arg);
    }

    public static void debug(String arg1, String arg2) 
    {
        if ( !debug )
            return;

        System.out.print(arg1);
        System.out.println(arg2);
    }
    
	//********************************************************
	//  Instance State 	
	//********************************************************
	private String updateRoot;
	private static String[] searchPattern;

	public static int FILES_ONLY = 0;
	public static int DIRECTORIES_ONLY = 1;
	public static int FILES_AND_DIRECTORIES = 2;

	private List wildCardResults = null;
	private List finalResults = null;
	private int searchFilter = 0;

	public UpdateFileSearch(String updateRoot) {
		this.updateRoot = updateRoot;
		finalResults = new ArrayList();
		
	}


	//********************************************************
	//  Method Definitions
	//********************************************************

	/**
	 * @param searchFilter  the searchFilter to set
	 * @uml.property  name="searchFilter"
	 */
	public void setSearchFilter(int searchFilter) {
		this.searchFilter = searchFilter;
	}

	/**
	 * @return  the searchFilter
	 * @uml.property  name="searchFilter"
	 */
	public int getSearchFilter() {
		return searchFilter;
	}

	public void normalizePattern(String pattern, String separator) {
		StringTokenizer st = new StringTokenizer(pattern, separator);
		searchPattern = new String[st.countTokens()];

		int count = 0;
		while (st.hasMoreTokens()) {
			searchPattern[count] = st.nextToken();
			count++;
		}
	}

	/**
	 * Returns the update root from which to base all searches
	 * @uml.property  name="updateRoot"
	 */
	public String getUpdateRoot() {
		return updateRoot;
	}

	/**
	 * Returns the update root from which to base all searches
	 * @uml.property  name="updateRoot"
	 */
	public void setUpdateRoot(String updateRoot) {
		this.updateRoot = updateRoot;
	}

	public List getAbsoluteFilesIds(List files) {
		List fileIds = new ArrayList();

		int filesSize = files.size();
		for (int i = 0; i < filesSize; i++) {
			fileIds.add((String) ((File) files.get(i)).getAbsolutePath());
		}

		return fileIds;
	}

	/**
	 * Returns an arraylist of Files found based on 
	 * the given search parameter
	 * 
	 * NOTE: search parameter must be relative to update root
	 * (example: {ROOT} config/cells/cell1/*.xml
	 * 
	 */
    public List search(String searchPattern){
    	normalizePattern(searchPattern, File.separator);
    	return searchFileTree(new File(updateRoot), 0, 0);
    	
    }

	/**
	 * Returns a list of Files found based on 
	 * the given search parameter and a new root path.
	 * 
	 * NOTE: search parameter must be relative to update root
	 * (example: {ROOT} config/cells/cell1/*.xml
	 * 
	 */
	public List search(String newUpdateRoot, String searchPattern) {
		setUpdateRoot(newUpdateRoot);
		normalizePattern(searchPattern, File.separator);
		return searchFileTree(new File(newUpdateRoot), 0, 0);
	}


	/**
	 * Returns a list of Files found based on 
	 * the given search parameter
	 * 
	 * NOTE: search parameter must be relative to update root
	 * (example: {ROOT} config/cells/cell1/*.xml
	 * 
	 */
	public List searchFileTree(File root, int depth, int matchLevel) {
		//determine the filter type
		int filterType = getSearchFilter();

		debug(
			"[DEBUG - searchFileTree] Searching root:"
				+ root.getAbsolutePath());
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			debug(
				"[DEBUG - searchFileTree] Located "
					+ files.length
					+ "under "
					+ root.getAbsolutePath());
			if (depth < searchPattern.length) {

				String match = searchPattern[matchLevel];

				debug(
					"[DEBUG - searchFileTree] Using match criteria: " + match);

				if (!match.equals("*")) {
					if (matchLevel < searchPattern.length - 1) {
						for (int j = 0; j < files.length; j++) {
							if (!hasWildCard(match)) {
								debug("[DEBUG - searchFileTree] Match criteria doesn't contain wildcard");

								if (files[j].getName().equals(match)) {
									searchFileTree(
										files[j],
										depth + 1,
										matchLevel + 1);
								}
							} else {
								debug("[DEBUG - searchFileTree] Match criteria contains wildcard");
								File result;
								if ((result = scanWildCard(files[j], match))
									!= null) {
									if (files[j]
										.getName()
										.equals(result.getName())) {
										searchFileTree(
											files[j],
											depth + 1,
											matchLevel + 1);
									}
								}
							}
						}

					} else {
						if (!hasWildCard(match)) {
							debug("[DEBUG - searchFileTree] Match criteria doesn't contain wildcard");

							for (int j = 0; j < files.length; j++) {
								if (files[j].getName().equals(match)) {
									if (files[j].isDirectory()) {
										debug(
											"[DEBUG - searchFileTree] "
												+ files[j]
												+ " is a directory");

										File[] f = files[j].listFiles();

										for (int k = 0; k < f.length; k++) {
											if (filterType == FILES_ONLY) {
												if (f[k].isFile()) {
													debug(
														"[DEBUG - searchFileTree] MATCH FOUND: "
															+ f[k]
																.getAbsolutePath());
													finalResults.add(f[k]);
												}
											} else if (
												filterType
													== DIRECTORIES_ONLY) {
												if (f[k].isDirectory()) {
													debug(
														"[DEBUG - searchFileTree] MATCH FOUND: "
															+ f[k]
																.getAbsolutePath());
													finalResults.add(f[k]);
												}
											} else if (
												filterType
													== FILES_AND_DIRECTORIES) {

												debug(
													"[DEBUG - searchFileTree] MATCH FOUND: "
														+ f[k].getAbsolutePath());
												finalResults.add(f[k]);

											}
										}

									} else {
										//is not a file...so chech each one directly (no need to drill down)
										if (filterType == FILES_ONLY) {

											if (files[j].isFile()) {

												if (files[j]
													.getName()
													.equals(match)) {
													debug(
														"[DEBUG - searchFileTree] MATCH FOUND: "
															+ files[j]
																.getAbsolutePath());
													finalResults.add(files[j]);
												}

											}
										} else if (
											filterType == DIRECTORIES_ONLY) {
											if (files[j].isDirectory()) {
												if (files[j]
													.getName()
													.equals(match)) {
													debug(
														"[DEBUG - searchFileTree] MATCH FOUND: "
															+ files[j]
																.getAbsolutePath());
													finalResults.add(files[j]);
												}
											}
										} else if (
											filterType
												== FILES_AND_DIRECTORIES) {

											if (files[j]
												.getName()
												.equals(match)) {
												debug(
													"[DEBUG - searchFileTree] MATCH FOUND: "
														+ files[j]
															.getAbsolutePath());
												finalResults.add(files[j]);
											}

										}
									}
								}

							}
						} else {
							for (int j = 0; j < files.length; j++) {
								debug("[DEBUG - searchFileTree] Processing wildcards");
								if (filterType == FILES_ONLY) {
									if (files[j].isFile()) {
										File result =
											scanWildCard(files[j], match);

										if (result != null) {
											debug(
												"[DEBUG - searchFileTree] MATCH FOUND: "
													+ files[j].getAbsolutePath());
											finalResults.add(files[j]);
										} else {
											debug("[DEBUG - searchFileTree] NO MATCH FOUND");
										}

									}
								} else if (filterType == DIRECTORIES_ONLY) {
									if (files[j].isDirectory()) {

										File result =
											scanWildCard(files[j], match);

										if (result != null) {

											if (result.isDirectory()) {
												debug(
													"[DEBUG - searchFileTree] "
														+ result
														+ " is a directory");

												File[] f = result.listFiles();

												for (int k = 0;
													k < f.length;
													k++) {
													if (filterType
														== FILES_ONLY) {
														if (f[k].isFile()) {
															debug(
																"[DEBUG - searchFileTree] MATCH FOUND: "
																	+ f[k]
																		.getAbsolutePath());
															finalResults.add(f[k]);
														}
													} else if (
														filterType
															== DIRECTORIES_ONLY) {
														if (f[k]
															.isDirectory()) {
															debug(
																"[DEBUG - searchFileTree] MATCH FOUND: "
																	+ f[k]
																		.getAbsolutePath());
															finalResults.add(f[k]);
														}
													} else if (
														filterType
															== FILES_AND_DIRECTORIES) {

														debug(
															"[DEBUG - searchFileTree] MATCH FOUND: "
																+ f[k]
																	.getAbsolutePath());
														finalResults.add(f[k]);

													}
												}

											}else{

												debug(
													"[DEBUG - searchFileTree] MATCH FOUND: "
														+ files[j].getAbsolutePath());
	
												finalResults.add(files[j]);
											}

										} else {
											debug("[DEBUG - searchFileTree] NO MATCH FOUND");
										}
									}

								} else if (
									filterType == FILES_AND_DIRECTORIES) {
									File result = scanWildCard(files[j], match);

									if (result != null) {
										if (result.isDirectory()) {
											debug(
												"[DEBUG - searchFileTree] "
													+ result
													+ " is a directory");

											File[] f = result.listFiles();

											for (int k = 0;
												k < f.length;
												k++) {
												if (filterType == FILES_ONLY) {
													if (f[k].isFile()) {
														debug(
															"[DEBUG - searchFileTree] MATCH FOUND: "
																+ f[k]
																	.getAbsolutePath());
														finalResults.add(f[k]);
													}
												} else if (
													filterType
														== DIRECTORIES_ONLY) {
													if (f[k].isDirectory()) {
														debug(
															"[DEBUG - searchFileTree] MATCH FOUND: "
																+ f[k]
																	.getAbsolutePath());
														finalResults.add(f[k]);
													}
												} else if (
													filterType
														== FILES_AND_DIRECTORIES) {

													debug(
														"[DEBUG - searchFileTree] MATCH FOUND: "
															+ f[k]
																.getAbsolutePath());
													finalResults.add(f[k]);

												}
											}

										} else {

											debug(
												"[DEBUG - searchFileTree] MATCH FOUND: "
													+ files[j].getAbsolutePath());

											finalResults.add(files[j]);

										}

									}

								}
							}

						}

					}

				} else {
					if (matchLevel == searchPattern.length - 1) {
						for (int j = 0; j < files.length; j++) {
							if (filterType == FILES_ONLY) {
								if (files[j].isFile())
									debug(
										"[DEBUG - searchFileTree] MATCH FOUND: "
											+ files[j].getAbsolutePath());
								finalResults.add(files[j]);

							} else if (filterType == DIRECTORIES_ONLY) {
								if (files[j].isDirectory())
									debug(
										"[DEBUG - searchFileTree] MATCH FOUND: "
											+ files[j].getAbsolutePath());
								finalResults.add(files[j]);
							} else if (filterType == FILES_AND_DIRECTORIES) {
								debug(
									"[DEBUG - searchFileTree] MATCH FOUND: "
										+ files[j].getAbsolutePath());
								finalResults.add(files[j]);
							}

						}
					}
					for (int j = 0; j < files.length; j++) {
						searchFileTree(files[j], depth + 1, matchLevel + 1);
					}
				}

			} else {

				//end search

			}
		}

		return finalResults;

	}

	private File scanWildCard(File file, String match) {
		File result = null;
		boolean hasSearchableChars = false;

		String searchableString = null;
		char[] matchAsCharArray = match.toCharArray();
		List wildCardIndices = new ArrayList();
		List searchableChars = new ArrayList();

		for (int i = 0; i < matchAsCharArray.length; i++) {
			if (matchAsCharArray[i] == '*') {
				debug("[DEBUG - scanWildCard]: located * at position: " + i);
				wildCardIndices.add(new Integer(i));
				if (searchableString != null) {
					searchableChars.add(searchableString);
					debug(
						"[DEBUG - scanWildCard] set searchable string: "
							+ searchableString);
					searchableString = null;
				}
			} else {
				hasSearchableChars = true;

				if (searchableString == null) {
					searchableString = new String(matchAsCharArray, i, 1);
				} else {
					if (matchAsCharArray[i - 1] != '*')
						searchableString += new String(matchAsCharArray, i, 1);
				}
			}

			if (i == matchAsCharArray.length - 1) {
				if (searchableString != null) {
					searchableChars.add(searchableString);
				}
			}
		}

		//if not all characters are *
		if (hasSearchableChars) {
			result =
				resolveWildCardMatches(
					file.getParent(),
					file.getName(),
					matchAsCharArray,
					searchableChars,
					wildCardIndices);

			debug(
				"[DEBUG - scanWildCard] result is not null" + (result != null));
		} else {
			result = file;
		}

		return result;

	}

	private File resolveWildCardMatches(
		String filePathQualifier,
		String file,
		char[] matchAsCharArray,
		List chars,
		List indices) {

		File result = null;
		int match = 0;

		//if the match query ends with a *
		if (matchAsCharArray[matchAsCharArray.length - 1] == '*') {

			for (int i = 0; i < chars.size(); i++) {
				String charToSearch = (String) chars.get(i);

				if ((i - 0 == 0) && matchAsCharArray[i] != '*') {
					if (file.startsWith(charToSearch))
						match++;
				} else {
					if (file.indexOf(charToSearch) >= 0)
						match++;
				}
			}

		} else {
			for (int i = 0; i < chars.size(); i++) {
				String charToSearch = (String) chars.get(i);

				if (i == chars.size() - 1) {
					if (file.endsWith(charToSearch))
						match++;
				} else {
					if ((i - 0 == 0) && matchAsCharArray[i] != '*') {
						if (file.startsWith(charToSearch))
							match++;
					} else {
						if (file.indexOf(charToSearch) >= 0)
							match++;
					}
				}
			}

		}

		debug(
			"[DEBUG - resolveWildCardMatches] matchesFound:"
				+ match
				+ " | matchesToFind:"
				+ chars.size());

		//file has been matched on all non-wildcard characters
		if (match == chars.size())
			result = new File(filePathQualifier + File.separator + file);

		return result;

	}

	private boolean hasWildCard(String pattern) {
		return ((pattern.indexOf("*") >= 0) ? true : false);
	}


}
