public class Epic extends Task {

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "Epic {" +
                "id=" + this.getId() + ", " +
                "name=" + this.getName() + ", " +
                "description=" + this.getDescription() + ", " +
                "status=" + this.getTaskStatus()  +
                "}";
    }
}
