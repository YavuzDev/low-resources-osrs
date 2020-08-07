package visitor.condition;

import reader.ObfuscatedClass;

public class FieldAmountCondition implements Condition {

    private final int amount;

    private final String type;

    public FieldAmountCondition(int amount, String type) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount has to be higher than 0");
        }
        this.amount = amount;
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
        return found == amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "FieldAmountCondition{" +
                "amount=" + amount +
                ", type='" + type + '\'' +
                '}';
    }
}
