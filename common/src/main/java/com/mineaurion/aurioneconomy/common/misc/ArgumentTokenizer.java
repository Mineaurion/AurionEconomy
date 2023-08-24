package com.mineaurion.aurioneconomy.common.misc;

import java.util.List;

public enum ArgumentTokenizer {

    EXECUTE {
        @Override
        public List<String> tokenizeInput(String args) {
            return new QuotedStringTokenizer(args).tokenize(true);
        }
    },
    TAB_COMPLETE {
        @Override
        public List<String> tokenizeInput(String args) {
            return new QuotedStringTokenizer(args).tokenize(false);
        }
    };

    public List<String> tokenizeInput(String[] args) {
        return tokenizeInput(String.join(" ", args));
    }

    public abstract List<String> tokenizeInput(String args);

}