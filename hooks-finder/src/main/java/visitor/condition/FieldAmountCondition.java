package visitor.condition;

import reader.ObfuscatedClass;

public class FieldAmountCondition implements Condition {

    private final int min;

    private final int max;

    private final String type;

    public FieldAmountCondition(int min, int max, String type) {
        if (min <= 0) {
            throw new IllegalArgumentException("Min has to be higher than 0");
        }
        if (max <= 0) {
            throw new IllegalArgumentException("Max has to be higher than 0");
        }
        if (max < min) {
            throw new IllegalArgumentException("Max has to be higher than min");
        }
        this.min = min;
        this.max = max;
        this.type = type;
    }

    @Override
    public boolean check(ObfuscatedClass obfuscatedClass) {
        var fields = obfuscatedClass.getFields();

        var found = 0;
        for (var field : fields) {
            if (field.desc.equals(type)) {
                found++;
            }
        }
        return found >= min && found <= max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FieldAmountCondition{" +
                "min=" + min +
                ", max=" + max +
                ", type='" + type + '\'' +
                '}';
    }
}
