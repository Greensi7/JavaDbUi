package org.example.parser;

import cz.fi.muni.pb162.sqlike.query.builder.field.Condition;
import cz.fi.muni.pb162.sqlike.query.builder.field.Field;
import cz.fi.muni.pb162.sqlike.query.builder.field.condition.AndCondition;
import cz.fi.muni.pb162.sqlike.query.builder.field.condition.BiggerSmallerCondition;
import cz.fi.muni.pb162.sqlike.query.builder.field.condition.EqualCondition;
import cz.fi.muni.pb162.sqlike.query.builder.field.condition.NotCondition;
import cz.fi.muni.pb162.sqlike.query.builder.field.condition.OrCondition;
import cz.fi.muni.pb162.sqlike.query.builder.field.condition.SmallerBiggerCondition;
import cz.fi.muni.pb162.sqlike.query.builder.field.impl.StandardField;
import cz.fi.muni.pb162.sqlike.query.builder.field.impl.StandardFieldClass;

public class ConditionBuilder {

    public static Condition parse(String input) {
        if (input == null || input.isBlank()) return null;

        input = input.trim();

        if (input.contains(" AND ")) {
            String[] parts = input.split(" AND ", 2);
            return new AndCondition(parse(parts[0]), parse(parts[1]));
        } else if (input.contains(" OR ")) {
            String[] parts = input.split(" OR ", 2);
            return new OrCondition(parse(parts[0]), parse(parts[1]));
        } else if (input.startsWith("NOT ")) {
            return new NotCondition(parse(input.substring(4)));
        }

        String[] operators = { "=", "<", ">" };
        for (String op : operators) {
            int index = input.indexOf(op);
            if (index > 0) {
                String left = input.substring(0, index).trim();
                String right = input.substring(index + op.length()).trim();

                Field field = new StandardField(left, StandardFieldClass.COLUMN);
                Object value = parseLiteral(right);

                return switch (op) {
                    case "=" -> new EqualCondition(field, value);
                    case "<" -> new SmallerBiggerCondition(field, value);
                    case ">" -> new BiggerSmallerCondition(field, value);
                    default -> null;
                };
            }
        }

        return null;
    }

    private static Object parseLiteral(String literal) {
        try {
            if (literal.matches("-?\\d+")) return Integer.parseInt(literal);
            if (literal.matches("-?\\d+\\.\\d+")) return Double.parseDouble(literal);
            if (literal.equalsIgnoreCase("true") || literal.equalsIgnoreCase("false")) return Boolean.parseBoolean(literal);
        } catch (Exception ignored) {}
        return literal.replaceAll("^['\"]|['\"]$", "");
    }
}
