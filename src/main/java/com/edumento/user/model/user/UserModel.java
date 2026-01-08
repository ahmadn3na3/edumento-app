package com.edumento.user.model.user;

import com.edumento.space.domain.Space;
import com.edumento.user.domain.User;
import java.util.function.Predicate;

/** Created by ahmad on 2/17/16. */
public class UserModel extends UserInfoModel {
  private Long spacesCount = 0L;

  private String school;

  public UserModel(User user) {
    super(user);
    spacesCount =
        user.getSpaces().stream()
            .filter(
                new Predicate<Space>() {
                  @Override
                  public boolean test(Space space) {
                    return !space.isDeleted();
                  }
                })
            .count();
    school = user.getSchool();
  }

  /**
   * @return the school
   */
  public String getSchool() {
    return school;
  }

  /**
   * @param school the school to set
   */
  public void setSchool(String school) {
    this.school = school;
  }

  public Long getSpacesCount() {
    return spacesCount;
  }

  public void setSpacesCount(Long spacesCount) {
    this.spacesCount = spacesCount;
  }
}
