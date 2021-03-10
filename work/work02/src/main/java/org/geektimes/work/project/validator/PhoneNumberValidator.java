package org.geektimes.work.project.validator;

import org.geektimes.work.project.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号校验器
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, User> {

	String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(16[0,5-9])|(17[0,5-9])|(18[0,5-9]))\\d{8}$";

	@Override
	public void initialize(PhoneNumber constraintAnnotation) {

	}

	@Override
	public boolean isValid(User value, ConstraintValidatorContext context) {
		Pattern pattern = Pattern.compile(regex);//编译正则表达式
		Matcher matcher = pattern.matcher(value.getPhoneNumber());  //创建给定输入模式的匹配器
		return matcher.matches();
	}
}
