package com.lacus.domain.dig.form.validate;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidateBuilder {

    public static ValidateBuilder builder() {
        return new ValidateBuilder();
    }

    public NonEmptyValidateBuilder nonEmptyValidateBuilder() {
        return new NonEmptyValidateBuilder();
    }

    public UnionNonEmptyValidateBuilder unionNonEmptyValidateBuilder() {
        return new UnionNonEmptyValidateBuilder();
    }

    public MutuallyExclusiveValidateBuilder mutuallyExclusiveValidateBuilder() {
        return new MutuallyExclusiveValidateBuilder();
    }

    public static class NonEmptyValidateBuilder {
        public NonEmptyValidate nonEmptyValidate() {
            return new NonEmptyValidate();
        }
    }

    public static class UnionNonEmptyValidateBuilder {
        private final List<String> fields = new ArrayList<>();

        public UnionNonEmptyValidateBuilder fields(@NonNull String... fields) {
            Collections.addAll(this.fields, fields);
            return this;
        }

        public UnionNonEmptyValidate unionNonEmptyValidate() {
            return new UnionNonEmptyValidate(fields);
        }
    }

    public static class MutuallyExclusiveValidateBuilder {
        private final List<String> fields = new ArrayList<>();

        public MutuallyExclusiveValidateBuilder fields(@NonNull String... fields) {
            Collections.addAll(this.fields, fields);
            return this;
        }

        public MutuallyExclusiveValidate mutuallyExclusiveValidate() {
            return new MutuallyExclusiveValidate(fields);
        }
    }
}
