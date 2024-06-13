package likeai.fun;

public class Data {
    private String name;
    private String message;

    public String getName() {
        return name;
    }

    public Data setName(String name) {
        this.name = name;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Data setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "Data{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}