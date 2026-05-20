package in.tech_camp.protospace.factory;

import in.tech_camp.protospace.form.CommentForm;
import net.datafaker.Faker;

public class CommentFormFactory {
  private static final Faker faker = new Faker();

  public static CommentForm createComment() {
    CommentForm commentForm = new CommentForm();
    commentForm.setText(faker.lorem().sentence(10));
    return commentForm;
  }
}
