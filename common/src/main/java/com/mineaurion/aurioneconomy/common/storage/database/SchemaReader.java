package com.mineaurion.aurioneconomy.common.storage.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public final class SchemaReader {

    private SchemaReader() {}

    public static List<String> getStatements(InputStream is) throws IOException {
        List<String> queries = new LinkedList<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("--") || line.startsWith("#")){
                    continue;
                }

                sb.append(line);

                if(line.endsWith(";")) {
                    sb.deleteCharAt(sb.length() - 1);

                    String result = sb.toString().trim();
                    if(!result.isEmpty()){
                        queries.add(result);
                    }

                    sb = new StringBuilder();
                }
            }
        }
        return queries;
    }
}
