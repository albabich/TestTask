package com.space.service.specs;


public class Filter {
    private String key;
    private QueryOperator operator;
    private Object value;

    public Filter(String field, QueryOperator operator, Object value) {
        this.key = field;
        this.operator = operator;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String field) {
        this.key = field;
    }

    public QueryOperator getOperator() {
        return operator;
    }


    public void setOperator(QueryOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "field='" + key + '\'' +
                ", operator=" + operator +
                ", value='" + value + '\'' +
                '}';
    }
//  private List<String> values;//Used in case of IN operator
}


