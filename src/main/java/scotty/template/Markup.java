package scotty.template;

public class Markup {
    final char operator;
    final String body;

    public Markup(final String script) {
        operator = script.charAt(0);
        body = script.substring(1).trim();
    }
}