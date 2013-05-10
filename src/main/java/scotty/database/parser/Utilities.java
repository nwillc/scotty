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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public final class Utilities {
    private Utilities() {
    }

    /**
     * Checks if A is a superset of B. A must have all the attributes, with equal values of B.
     */
    public static boolean superset(Context a, Context b) {
        if (b == null) {
            return true;
        }

        for (String key : b.keySet()) {
            String value = a.get(key);
            if (!b.get(key).equals(value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * The similarity score is the count of all matching attributes between two contexts. If any attribute name is shared where
     * the values differ the similarity is -1;
     */
    public static int similarity(Context a, Context b) {
        int score = 0;
        if (a == null || b == null) {
            return score;
        }

        for (String key : b.keySet()) {
            if (!a.containsKey(key)) {
                continue;
            }

            if (!a.get(key).equals(b.get(key))) {
                return -1;
            }
            score++;
        }
        return score;
    }

    public static String find(Context context, String name) {
        String[] parts = name.split("\\.");


        for (String part : parts) {
            if (!context.isParent()) {
                return context.get(part);
            }

            context = context.getChildren().get(part);
            if (context == null) {
                return null;
            }
        }
        return null;
    }

    public static List<Context> query(Context context, Context criteria) {
        List<Context> contexts = new LinkedList<>();
        List<Ranked<Context>> results = rankedQuery(context, criteria);
        for (Ranked<Context> ranked : results) {
            contexts.add(ranked.getData());
        }
        return contexts;
    }

    public static List<Ranked<Context>> rankedQuery(Context context, Context criteria) {
        List<Ranked<Context>> results = new LinkedList<>();

        if (context.isParent()) {
            for (Context child : context.getChildren().values()) {
                results.addAll(rankedQuery(child, criteria));
            }
        } else {
            int rank = similarity(context, criteria);
            if (rank > 0) {
                results.add(new Ranked<>(rank, context));
            }
        }

        Collections.sort(results);
        return results;
    }
}
