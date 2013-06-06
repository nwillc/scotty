/*
 * (c) Copyright Selerity, Inc. 2009-2013. All rights reserved. This source code is confidential
 * and proprietary information of Selerity Inc. and may be used only by a recipient designated
 * by and for the purposes permitted by Selerity Inc. in writing.  Reproduction of, dissemination
 * of, modifications to or creation of derivative works from this source code, whether in source
 * or binary forms, by any means and in any form or manner, is expressly prohibited, except with
 * the prior written permission of Selerity Inc..  THIS CODE AND INFORMATION ARE PROVIDED "AS IS"
 * WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. This notice may not be
 * removed from the software by any user thereof.
 */

package scotty.database.parser;

import scotty.database.Context;
import scotty.database.Database;
import scotty.database.Instance;
import scotty.database.Type;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility methods used by the database portions of Scotty.
 */
public final class Utilities {
    private static final Logger LOGGER = Logger.getLogger(Utilities.class.getName());

    private Utilities() {
	}

    /**
     * Query a context for all the contexts within it that match a criteria.
     * @param context The context to query.
     * @param criteria The criteria to match.
     * @return the list of matches in ranked order
     */
    public static List<Context> query(Context context, Context criteria) {
		List<Context> contexts = new LinkedList<>();
		List<Ranked<Context>> results = rankedQuery(context, criteria);
		for (Ranked<Context> ranked : results) {
			contexts.add(ranked.getData());
		}
		return contexts;
	}

    /**
     * Query a context for all the contexts within it that match a criteria.
     * @param context The context to query
     * @param criteria the criteria to match
     * @return the results, with their associated ranks in ranked order
     */
	public static List<Ranked<Context>> rankedQuery(Context context, Context criteria) {
		List<Ranked<Context>> results = new LinkedList<>();

		if (context.isContainer()) {
			for (Context child : context.getContained().values()) {
				results.addAll(rankedQuery(child, criteria));
			}
		} else {
			float rank = context.similarity(criteria);
			if (rank > Similarity.NOT_SIMILAR) {
				results.add(new Ranked<>(rank, context.getAge(), context));
			}
		}

		Collections.sort(results);
		return results;
	}

    /**
     * Print out the contents of a database, flattening all the inheritance to the instance level.
     * @param database the database
     * @param printStream the output stream
     */
	public static void print(Database database, PrintStream printStream) {
		List<Context> types = new LinkedList<>(database.getContained().values());
		Collections.sort(types);
		for (Context type : types) {
			printStream.format("Type: %s\n", ((Type) type).getName());
			List<Context> instances = new LinkedList<>(type.getContained().values());
			Collections.sort(instances);
			for (Context instance : instances) {
				printStream.format("\tInstance: %s\n", ((Instance) instance).getName());
				List<String> attrNames = new LinkedList<>(instance.keySet());
				Collections.sort(attrNames);
				for (String key : attrNames) {
					printStream.format("\t\t%20s: %s\n", key, instance.get(key));
				}
			}
		}
	}

    /**
     * Look up a resource as either a resource or a file.
     * @param resource item name to look for
     * @return return input stream or null if not found
     */
    public static InputStream getResourceAsStream(String resource) {
           InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
           if (stream != null) {
               return stream;
           }

           // Couldn't be pulled by the class loader, maybe it's just a file
           try {
               stream = new FileInputStream(resource);
           } catch (FileNotFoundException e) {
               LOGGER.warning("Failed to find " + resource + " as resource or file.");
           }

           return stream;
       }
}
